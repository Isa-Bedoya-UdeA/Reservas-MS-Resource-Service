package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

/**
 * Exception thrown when an employee-service association is not found.
 */
public class EmployeeServiceNotFoundException extends RuntimeException {

    public EmployeeServiceNotFoundException(UUID id) {
        super("Asociación empleado-servicio no encontrada con ID: " + id);
    }

    public EmployeeServiceNotFoundException(String message) {
        super(message);
    }
}
