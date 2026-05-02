package com.codefactory.reservasmsresourceservice.service.impl;

import com.codefactory.reservasmsresourceservice.dto.request.CreateScheduleBlockRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.ScheduleBlockResponseDTO;
import com.codefactory.reservasmsresourceservice.entity.Employee;
import com.codefactory.reservasmsresourceservice.entity.ScheduleBlock;
import com.codefactory.reservasmsresourceservice.entity.WorkSchedule;
import com.codefactory.reservasmsresourceservice.exception.EmployeeNotFoundException;
import com.codefactory.reservasmsresourceservice.exception.InvalidScheduleBlockException;
import com.codefactory.reservasmsresourceservice.exception.ScheduleBlockConflictException;
import com.codefactory.reservasmsresourceservice.exception.ScheduleBlockNotFoundException;
import com.codefactory.reservasmsresourceservice.mapper.ScheduleBlockMapper;
import com.codefactory.reservasmsresourceservice.repository.EmployeeRepository;
import com.codefactory.reservasmsresourceservice.repository.ScheduleBlockRepository;
import com.codefactory.reservasmsresourceservice.repository.WorkScheduleRepository;
import com.codefactory.reservasmsresourceservice.service.ScheduleBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleBlockServiceImpl implements ScheduleBlockService {

    private final ScheduleBlockRepository scheduleBlockRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final EmployeeRepository employeeRepository;
    private final ScheduleBlockMapper scheduleBlockMapper;

    private static final Set<String> VALID_BLOCK_TYPES = Set.of(
            "RESERVA", "VACACIONES", "PERMISO", "ADMINISTRATIVO"
    );

    private static final Set<String> VALID_DAYS = Set.of(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
    );

    @Override
    @Transactional
    public ScheduleBlockResponseDTO createScheduleBlock(CreateScheduleBlockRequestDTO request, UUID providerIdFromJWT) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        if (!employee.getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para crear bloqueos de horario para este empleado");
        }

        // Validate block type
        if (request.getBlockType() != null && !VALID_BLOCK_TYPES.contains(request.getBlockType())) {
            throw new InvalidScheduleBlockException("El tipo de bloqueo debe ser uno de: RESERVA, VACACIONES, PERMISO, ADMINISTRATIVO");
        }

        // Validate date is not in the past
        if (request.getDate().isBefore(LocalDate.now())) {
            throw new InvalidScheduleBlockException("La fecha del bloqueo no puede ser en el pasado");
        }

        // Validate time range
        if (request.getEndTime().isBefore(request.getStartTime()) || request.getEndTime().equals(request.getStartTime())) {
            throw new InvalidScheduleBlockException("La hora de fin debe ser mayor a la hora de inicio");
        }

        // Check if employee works during this time (work schedule validation)
        String dayOfWeek = getDayOfWeekFromDate(request.getDate());
        List<WorkSchedule> workSchedules = workScheduleRepository.findByEmployeeIdAndDayOfWeekAndActiveTrue(
                request.getEmployeeId(), dayOfWeek);

        boolean worksDuringTime = workSchedules.stream()
                .anyMatch(ws -> isTimeWithinWorkSchedule(request.getStartTime(), request.getEndTime(), ws));

        if (!worksDuringTime) {
            throw new InvalidScheduleBlockException("El empleado no trabaja durante el horario especificado para el día " + dayOfWeek);
        }

        // Check for overlapping blocks
        List<ScheduleBlock> existingBlocks = scheduleBlockRepository.findOverlappingBlocks(
                request.getEmployeeId(), request.getDate(), request.getStartTime(), request.getEndTime());

        if (!existingBlocks.isEmpty()) {
            throw new ScheduleBlockConflictException("Ya existe un bloqueo que se superpone con el horario especificado para la fecha " + request.getDate());
        }

        ScheduleBlock scheduleBlock = scheduleBlockMapper.toEntity(request);
        scheduleBlock.setEmployee(employee);
        scheduleBlock.setActive(true);
        ScheduleBlock savedScheduleBlock = scheduleBlockRepository.save(scheduleBlock);
        return scheduleBlockMapper.toDto(savedScheduleBlock);
    }

    @Override
    @Transactional
    public void deleteScheduleBlock(UUID id, UUID providerIdFromJWT) {
        ScheduleBlock scheduleBlock = scheduleBlockRepository.findById(id)
                .orElseThrow(() -> new ScheduleBlockNotFoundException(id));

        // Validate ownership through employee
        if (!scheduleBlock.getEmployee().getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para eliminar este bloqueo de horario");
        }

        // Soft delete by setting active to false
        scheduleBlock.setActive(false);
        scheduleBlockRepository.save(scheduleBlock);
    }

    @Override
    public ScheduleBlockResponseDTO getScheduleBlockById(UUID id, UUID providerIdFromJWT) {
        ScheduleBlock scheduleBlock = scheduleBlockRepository.findById(id)
                .orElseThrow(() -> new ScheduleBlockNotFoundException(id));

        // Validate ownership through employee
        if (!scheduleBlock.getEmployee().getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para ver este bloqueo de horario");
        }

        return scheduleBlockMapper.toDto(scheduleBlock);
    }

    @Override
    public List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployee(UUID employeeId, UUID providerIdFromJWT) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (!employee.getProviderId().equals(providerIdFromJWT)) {
            throw new AccessDeniedException("No tienes permisos para ver bloqueos de horario de este empleado");
        }

        return scheduleBlockRepository.findByEmployeeIdAndActiveTrue(employeeId).stream()
                .map(scheduleBlockMapper::toDto)
                .toList();
    }

    @Override
    public List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeePublic(UUID employeeId) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return scheduleBlockRepository.findByEmployeeIdAndActiveTrue(employeeId).stream()
                .map(scheduleBlockMapper::toDto)
                .toList();
    }

    @Override
    public List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeeAndDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return scheduleBlockRepository.findByEmployeeIdAndDateBetweenAndActiveTrue(employeeId, startDate, endDate).stream()
                .map(scheduleBlockMapper::toDto)
                .toList();
    }

    @Override
    public List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeeAndDate(UUID employeeId, LocalDate date) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        return scheduleBlockRepository.findByEmployeeIdAndDateAndActiveTrue(employeeId, date).stream()
                .map(scheduleBlockMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void createReservationBlock(UUID employeeId, UUID reservationId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Validate employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        // Check if employee works during this time
        String dayOfWeek = getDayOfWeekFromDate(date);
        List<WorkSchedule> workSchedules = workScheduleRepository.findByEmployeeIdAndDayOfWeekAndActiveTrue(
                employeeId, dayOfWeek);

        boolean worksDuringTime = workSchedules.stream()
                .anyMatch(ws -> isTimeWithinWorkSchedule(startTime, endTime, ws));

        if (!worksDuringTime) {
            throw new InvalidScheduleBlockException("El empleado no trabaja durante el horario especificado para el día " + dayOfWeek);
        }

        // Check for overlapping blocks
        List<ScheduleBlock> existingBlocks = scheduleBlockRepository.findOverlappingBlocks(
                employeeId, date, startTime, endTime);

        if (!existingBlocks.isEmpty()) {
            throw new ScheduleBlockConflictException("Ya existe un bloqueo que se superpone con el horario especificado para la fecha " + date);
        }

        // Create reservation block
        ScheduleBlock scheduleBlock = ScheduleBlock.builder()
                .employee(employee)
                .reservationId(reservationId)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .blockType("RESERVA")
                .active(true)
                .build();

        scheduleBlockRepository.save(scheduleBlock);
    }

    @Override
    @Transactional
    public void cancelReservationBlock(UUID reservationId) {
        Optional<ScheduleBlock> scheduleBlock = scheduleBlockRepository.findActiveByReservationId(reservationId);
        
        if (scheduleBlock.isPresent()) {
            ScheduleBlock block = scheduleBlock.get();
            block.setActive(false);
            scheduleBlockRepository.save(block);
        }
    }

    @Override
    public boolean isEmployeeAvailable(UUID employeeId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // Check if employee works during this time
        String dayOfWeek = getDayOfWeekFromDate(date);
        List<WorkSchedule> workSchedules = workScheduleRepository.findByEmployeeIdAndDayOfWeekAndActiveTrue(
                employeeId, dayOfWeek);

        boolean worksDuringTime = workSchedules.stream()
                .anyMatch(ws -> isTimeWithinWorkSchedule(startTime, endTime, ws));

        if (!worksDuringTime) {
            return false;
        }

        // Check for blocking conflicts
        List<ScheduleBlock> blockingBlocks = scheduleBlockRepository.findOverlappingBlocks(
                employeeId, date, startTime, endTime);

        return blockingBlocks.isEmpty();
    }

    private String getDayOfWeekFromDate(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return switch (dayOfWeek) {
            case MONDAY -> "LUNES";
            case TUESDAY -> "MARTES";
            case WEDNESDAY -> "MIERCOLES";
            case THURSDAY -> "JUEVES";
            case FRIDAY -> "VIERNES";
            case SATURDAY -> "SABADO";
            case SUNDAY -> "DOMINGO";
        };
    }

    private boolean isTimeWithinWorkSchedule(LocalTime blockStart, LocalTime blockEnd, WorkSchedule workSchedule) {
        return blockStart.isBefore(workSchedule.getEndTime()) && 
               blockEnd.isAfter(workSchedule.getStartTime());
    }

    private boolean isTimeOverlapping(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
