package com.codefactory.reservasmsresourceservice.exception;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }

    public EmployeeNotFoundException(UUID employeeId) {
        super(String.format("Empleado no encontrado con id: %s", employeeId));
    }
}
