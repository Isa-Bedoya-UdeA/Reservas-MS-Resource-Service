package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

/**
 * Exception thrown when trying to activate an already active employee-service association.
 */
public class EmployeeServiceAlreadyActiveException extends RuntimeException {

    public EmployeeServiceAlreadyActiveException(UUID id) {
        super("La asociación empleado-servicio con ID " + id + " ya está activa");
    }
}
