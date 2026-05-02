package com.codefactory.reservasmsscheduleservice.exception;

/**
 * Exception thrown when a provider tries to access or modify an employee-service association
 * that does not belong to them.
 */
public class EmployeeServiceOwnershipException extends RuntimeException {

    public EmployeeServiceOwnershipException() {
        super("No tienes permisos para gestionar esta asociación empleado-servicio");
    }

    public EmployeeServiceOwnershipException(String message) {
        super(message);
    }
}
