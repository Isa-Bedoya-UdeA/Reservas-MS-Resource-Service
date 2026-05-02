package com.codefactory.reservasmsscheduleservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codefactory.reservasmsscheduleservice.client.CatalogClientWrapper;
import com.codefactory.reservasmsscheduleservice.dto.external.ExternalServiceOfferingDTO;
import com.codefactory.reservasmsscheduleservice.dto.request.CreateEmployeeServiceRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeResponseDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeServiceResponseDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeWithServicesResponseDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.ServiceWithEmployeesResponseDTO;
import com.codefactory.reservasmsscheduleservice.entity.Employee;
import com.codefactory.reservasmsscheduleservice.entity.EmployeeServiceOffering;
import com.codefactory.reservasmsscheduleservice.exception.*;
import com.codefactory.reservasmsscheduleservice.mapper.EmployeeMapper;
import com.codefactory.reservasmsscheduleservice.repository.EmployeeRepository;
import com.codefactory.reservasmsscheduleservice.repository.EmployeeServiceOfferingRepository;
import com.codefactory.reservasmsscheduleservice.service.EmployeeServiceOfferingService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the Employee Service Offering Service.
 * Manages associations between employees and services with proper ownership validation.
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceOfferingServiceImpl implements EmployeeServiceOfferingService {

    private final EmployeeServiceOfferingRepository employeeServiceOfferingRepository;
    private final EmployeeRepository employeeRepository;
    private final CatalogClientWrapper catalogClientWrapper;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeServiceResponseDTO createAssociation(CreateEmployeeServiceRequestDTO request, UUID providerId) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new EmployeeNotFoundException(request.getEmployeeId()));

        if (!employee.getProviderId().equals(providerId)) {
            throw new EmployeeServiceOwnershipException("El empleado no pertenece a este proveedor");
        }

        // Validate service exists and belongs to provider (via Catalog Service)
        ExternalServiceOfferingDTO service = catalogClientWrapper.validateServiceOwnership(
                request.getServiceId(), providerId);

        // Check if association already exists and is active
        Optional<EmployeeServiceOffering> existingAssociation =
                employeeServiceOfferingRepository.findByEmployeeIdAndServiceId(
                        request.getEmployeeId(), request.getServiceId());

        if (existingAssociation.isPresent()) {
            EmployeeServiceOffering assoc = existingAssociation.get();
            if (assoc.getActive()) {
                throw new EmployeeServiceAlreadyExistsException(request.getEmployeeId(), request.getServiceId());
            }
            // If exists but inactive, reactivate it
            assoc.setActive(true);
            EmployeeServiceOffering saved = employeeServiceOfferingRepository.save(assoc);
            return mapToDto(saved);
        }

        // Create new association
        EmployeeServiceOffering association = EmployeeServiceOffering.builder()
                .employee(employee)
                .serviceId(request.getServiceId())
                .active(true)
                .build();

        EmployeeServiceOffering saved = employeeServiceOfferingRepository.save(association);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void deactivateAssociation(UUID associationId, UUID providerId) {
        EmployeeServiceOffering association = employeeServiceOfferingRepository.findById(associationId)
                .orElseThrow(() -> new EmployeeServiceNotFoundException(associationId));

        // Validate ownership through employee
        if (!association.getEmployee().getProviderId().equals(providerId)) {
            throw new EmployeeServiceOwnershipException();
        }

        // Check if already inactive
        if (!association.getActive()) {
            throw new EmployeeServiceAlreadyInactiveException(associationId);
        }

        association.setActive(false);
        employeeServiceOfferingRepository.save(association);
    }

    @Override
    @Transactional
    public void activateAssociation(UUID associationId, UUID providerId) {
        EmployeeServiceOffering association = employeeServiceOfferingRepository.findById(associationId)
                .orElseThrow(() -> new EmployeeServiceNotFoundException(associationId));

        // Validate ownership through employee
        if (!association.getEmployee().getProviderId().equals(providerId)) {
            throw new EmployeeServiceOwnershipException();
        }

        // Check if already active
        if (association.getActive()) {
            throw new EmployeeServiceAlreadyActiveException(associationId);
        }

        association.setActive(true);
        employeeServiceOfferingRepository.save(association);
    }

    @Override
    @Transactional
    public void deleteAssociation(UUID associationId, UUID providerId) {
        EmployeeServiceOffering association = employeeServiceOfferingRepository.findById(associationId)
                .orElseThrow(() -> new EmployeeServiceNotFoundException(associationId));

        // Validate ownership through employee
        if (!association.getEmployee().getProviderId().equals(providerId)) {
            throw new EmployeeServiceOwnershipException();
        }

        employeeServiceOfferingRepository.delete(association);
    }

    @Override
    public ServiceWithEmployeesResponseDTO getEmployeesByService(UUID serviceId, UUID providerId) {
        // Validate service exists and belongs to provider
        ExternalServiceOfferingDTO service = catalogClientWrapper.validateServiceOwnership(serviceId, providerId);

        // Get all associations for this service
        List<EmployeeServiceOffering> associations =
                employeeServiceOfferingRepository.findByServiceId(serviceId);

        // Filter by provider ownership and map to DTOs
        List<EmployeeResponseDTO> employees = associations.stream()
                .filter(assoc -> assoc.getEmployee().getProviderId().equals(providerId))
                .map(assoc -> employeeMapper.toDto(assoc.getEmployee()))
                .collect(Collectors.toList());

        return ServiceWithEmployeesResponseDTO.builder()
                .idServicio(service.getIdServicio())
                .idProveedor(service.getIdProveedor())
                .nombreServicio(service.getNombreServicio())
                .duracionMinutos(service.getDuracionMinutos())
                .precio(service.getPrecio())
                .descripcion(service.getDescripcion())
                .activo(service.getActivo())
                .capacidadMaxima(service.getCapacidadMaxima())
                .employees(employees)
                .createdAt(service.getCreatedAt())
                .build();
    }

    @Override
    public EmployeeWithServicesResponseDTO getServicesByEmployee(UUID employeeId, UUID providerId) {
        // Validate employee exists and belongs to provider
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeId));

        if (!employee.getProviderId().equals(providerId)) {
            throw new EmployeeServiceOwnershipException("El empleado no pertenece a este proveedor");
        }

        // Get all active associations for this employee
        List<EmployeeServiceOffering> associations =
                employeeServiceOfferingRepository.findByEmployeeIdAndActiveTrue(employeeId);

        // Fetch service details from Catalog Service
        List<ExternalServiceOfferingDTO> services = associations.stream()
                .map(assoc -> catalogClientWrapper.getServiceOrThrow(assoc.getServiceId()))
                .collect(Collectors.toList());

        return EmployeeWithServicesResponseDTO.builder()
                .id(employee.getId())
                .providerId(employee.getProviderId())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .active(employee.getActive())
                .hireDate(employee.getHireDate())
                .notes(employee.getNotes())
                .services(services)
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }

    @Override
    public ServiceWithEmployeesResponseDTO getActiveEmployeesByService(UUID serviceId) {
        // Get service details from Catalog Service (public endpoint, no ownership validation)
        ExternalServiceOfferingDTO service = catalogClientWrapper.getServiceOrThrow(serviceId);

        // Get only active associations for this service
        List<EmployeeServiceOffering> associations =
                employeeServiceOfferingRepository.findByServiceIdAndActiveTrue(serviceId);

        // Filter active employees and map to DTOs
        List<EmployeeResponseDTO> employees = associations.stream()
                .filter(assoc -> assoc.getEmployee().getActive())
                .map(assoc -> employeeMapper.toDto(assoc.getEmployee()))
                .collect(Collectors.toList());

        return ServiceWithEmployeesResponseDTO.builder()
                .idServicio(service.getIdServicio())
                .idProveedor(service.getIdProveedor())
                .nombreServicio(service.getNombreServicio())
                .duracionMinutos(service.getDuracionMinutos())
                .precio(service.getPrecio())
                .descripcion(service.getDescripcion())
                .activo(service.getActivo())
                .capacidadMaxima(service.getCapacidadMaxima())
                .employees(employees)
                .createdAt(service.getCreatedAt())
                .build();
    }

    /**
     * Maps an EmployeeServiceOffering entity to a DTO.
     *
     * @param entity the entity to map
     * @return the mapped DTO
     */
    private EmployeeServiceResponseDTO mapToDto(EmployeeServiceOffering entity) {
        return EmployeeServiceResponseDTO.builder()
                .id(entity.getId())
                .employeeId(entity.getEmployee().getId())
                .serviceId(entity.getServiceId())
                .active(entity.getActive())
                .assignmentDate(entity.getAssignmentDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
