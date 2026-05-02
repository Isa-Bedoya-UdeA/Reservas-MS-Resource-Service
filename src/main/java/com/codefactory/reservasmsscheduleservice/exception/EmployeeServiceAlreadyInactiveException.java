package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

/**
 * Exception thrown when trying to deactivate an already inactive employee-service association.
 */
public class EmployeeServiceAlreadyInactiveException extends RuntimeException {

    public EmployeeServiceAlreadyInactiveException(UUID id) {
        super("La asociación empleado-servicio con ID " + id + " ya está inactiva");
    }
}
