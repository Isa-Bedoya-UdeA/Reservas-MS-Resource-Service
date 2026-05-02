package com.codefactory.reservasmsscheduleservice.service;

import java.util.UUID;

import com.codefactory.reservasmsscheduleservice.dto.request.CreateEmployeeServiceRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeServiceResponseDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeWithServicesResponseDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.ServiceWithEmployeesResponseDTO;

/**
 * Service interface for managing employee-service associations.
 */
public interface EmployeeServiceOfferingService {

    /**
     * Creates a new association between an employee and a service.
     *
     * @param request the request containing employee and service IDs
     * @param providerId the authenticated provider ID
     * @return the created association DTO
     */
    EmployeeServiceResponseDTO createAssociation(CreateEmployeeServiceRequestDTO request, UUID providerId);

    /**
     * Deactivates an existing employee-service association.
     *
     * @param associationId the association ID
     * @param providerId the authenticated provider ID
     */
    void deactivateAssociation(UUID associationId, UUID providerId);

    /**
     * Activates an existing employee-service association.
     *
     * @param associationId the association ID
     * @param providerId the authenticated provider ID
     */
    void activateAssociation(UUID associationId, UUID providerId);

    /**
     * Permanently deletes an employee-service association.
     *
     * @param associationId the association ID
     * @param providerId the authenticated provider ID
     */
    void deleteAssociation(UUID associationId, UUID providerId);

    /**
     * Retrieves all employees associated with a service.
     *
     * @param serviceId the service ID
     * @param providerId the authenticated provider ID
     * @return list of employees for the service
     */
    ServiceWithEmployeesResponseDTO getEmployeesByService(UUID serviceId, UUID providerId);

    /**
     * Retrieves all services associated with an employee.
     *
     * @param employeeId the employee ID
     * @param providerId the authenticated provider ID
     * @return employee with their services
     */
    EmployeeWithServicesResponseDTO getServicesByEmployee(UUID employeeId, UUID providerId);

    /**
     * Retrieves active employees associated with a service (public endpoint).
     *
     * @param serviceId the service ID
     * @return list of active employees for the service
     */
    ServiceWithEmployeesResponseDTO getActiveEmployeesByService(UUID serviceId);
}
