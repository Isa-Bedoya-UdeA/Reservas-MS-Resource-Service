package com.codefactory.reservasmsscheduleservice.exception;

/**
 * Exception thrown when a provider tries to access a service that doesn't belong to them.
 */
public class ServiceOwnershipException extends RuntimeException {

    public ServiceOwnershipException(String message) {
        super(message);
    }
}
