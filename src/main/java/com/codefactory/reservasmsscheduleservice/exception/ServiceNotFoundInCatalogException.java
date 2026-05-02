package com.codefactory.reservasmsscheduleservice.exception;

import java.util.UUID;

/**
 * Exception thrown when a service is not found in the Catalog Service.
 */
public class ServiceNotFoundInCatalogException extends RuntimeException {

    public ServiceNotFoundInCatalogException(UUID serviceId) {
        super("Servicio no encontrado en el catálogo con ID: " + serviceId);
    }

    public ServiceNotFoundInCatalogException(String message) {
        super(message);
    }
}
