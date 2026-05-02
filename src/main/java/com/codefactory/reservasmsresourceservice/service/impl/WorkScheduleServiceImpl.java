package com.codefactory.reservasmsresourceservice.service.impl;

import com.codefactory.reservasmsresourceservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.WorkScheduleResponseDTO;
import com.codefactory.reservasmsresourceservice.entity.Employee;
import com.codefactory.reservasmsresourceservice.entity.WorkSchedule;
import com.codefactory.reservasmsresourceservice.exception.EmployeeNotFoundException;
import com.codefactory.reservasmsresourceservice.exception.InvalidWorkScheduleException;
import com.codefactory.reservasmsresourceservice.exception.WorkScheduleConflictException;
import com.codefactory.reservasmsresourceservice.exception.WorkScheduleNotFoundException;
import com.codefactory.reservasmsresourceservice.mapper.WorkScheduleMapper;
import com.codefactory.reservasmsresourceservice.repository.EmployeeRepository;
import com.codefactory.reservasmsresourceservice.repository.WorkScheduleRepository;
import com.codefactory.reservasmsresourceservice.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkScheduleServiceImpl implements WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkScheduleMapper workScheduleMapper;

    private static final Set<String> VALID_DAYS = Set.of(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
    );

    @Override
    @Transactional
    public WorkScheduleResponseDTO createWorkSchedule(CreateWorkScheduleRequestDTO request, UUID providerIdFromJWT) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        if (!employee.getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para crear horarios laborales para este empleado");
        }

        // Validate employee is active
        if (!employee.getActive()) {
            throw new InvalidWorkScheduleException("No se puede crear horarios laborales para un empleado inactivo");
        }

        // Validate day of week
        if (!VALID_DAYS.contains(request.getDayOfWeek())) {
            throw new InvalidWorkScheduleException("El día de la semana debe ser uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO");
        }

        // Validate time range
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
            throw new InvalidWorkScheduleException("La hora de fin debe ser mayor a la hora de inicio");
        }

        // Check for overlapping schedules on the same day
        List<WorkSchedule> existingSchedules = workScheduleRepository
                .findByEmployeeIdAndDayOfWeekAndActiveTrue(request.getEmployeeId(), request.getDayOfWeek());
        
        for (WorkSchedule existing : existingSchedules) {
            if (isTimeOverlapping(request.getStartTime(), request.getEndTime(), existing.getStartTime(), existing.getEndTime())) {
                throw new WorkScheduleConflictException("Ya existe un horario laboral que se superpone con el rango de tiempo especificado para el día " + request.getDayOfWeek());
            }
        }

        WorkSchedule workSchedule = workScheduleMapper.toEntity(request);
        workSchedule.setEmployee(employee);
        workSchedule.setActive(true);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toDto(savedWorkSchedule);
    }

    @Override
    @Transactional
    public WorkScheduleResponseDTO updateWorkSchedule(UUID id, UpdateWorkScheduleRequestDTO request, UUID providerIdFromJWT) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new WorkScheduleNotFoundException(id));

        // Validate ownership through employee
        if (!workSchedule.getEmployee().getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para modificar este horario laboral");
        }

        // Validate day of week if provided
        if (request.getDayOfWeek() != null && !VALID_DAYS.contains(request.getDayOfWeek())) {
            throw new InvalidWorkScheduleException("El día de la semana debe ser uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO");
        }

        // Validate time range if both times are provided
        if (request.getStartTime() != null && request.getEndTime() != null) {
            if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
                throw new InvalidWorkScheduleException("La hora de fin debe ser mayor a la hora de inicio");
            }
        }

        workScheduleMapper.updateEntityFromDto(request, workSchedule);
        WorkSchedule savedWorkSchedule = workScheduleRepository.save(workSchedule);
        return workScheduleMapper.toDto(savedWorkSchedule);
    }

    @Override
    @Transactional
    public void deleteWorkSchedule(UUID id, UUID providerIdFromJWT) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new WorkScheduleNotFoundException(id));

        // Validate ownership through employee
        if (!workSchedule.getEmployee().getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este horario laboral");
        }

        workScheduleRepository.delete(workSchedule);
    }

    @Override
    public WorkScheduleResponseDTO getWorkScheduleById(UUID id, UUID providerIdFromJWT) {
        WorkSchedule workSchedule = workScheduleRepository.findById(id)
                .orElseThrow(() -> new WorkScheduleNotFoundException(id));

        // Validate ownership through employee
        if (!workSchedule.getEmployee().getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para ver este horario laboral");
        }

        return workScheduleMapper.toDto(workSchedule);
    }

    @Override
    public List<WorkScheduleResponseDTO> getWorkSchedulesByEmployee(UUID employeeId, UUID providerIdFromJWT) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (!employee.getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para ver horarios laborales de este empleado");
        }

        return workScheduleRepository.findByEmployeeId(employeeId).stream()
                .map(workScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<WorkScheduleResponseDTO> getActiveWorkSchedulesByEmployee(UUID employeeId) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return workScheduleRepository.findActiveSchedulesByEmployeeOrdered(employeeId).stream()
                .map(workScheduleMapper::toDto)
                .toList();
    }

    @Override
    public List<WorkScheduleResponseDTO> getWorkSchedulesByEmployeePublic(UUID employeeId) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return workScheduleRepository.findByEmployeeIdAndActiveTrue(employeeId).stream()
                .map(workScheduleMapper::toDto)
                .toList();
    }

    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
