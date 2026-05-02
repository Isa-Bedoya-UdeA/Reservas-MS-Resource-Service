package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

/**
 * Exception thrown when trying to create a duplicate active employee-service association.
 */
public class EmployeeServiceAlreadyExistsException extends RuntimeException {

    public EmployeeServiceAlreadyExistsException(UUID employeeId, UUID serviceId) {
        super("Ya existe una asociación activa entre el empleado " + employeeId + " y el servicio " + serviceId);
    }
}
