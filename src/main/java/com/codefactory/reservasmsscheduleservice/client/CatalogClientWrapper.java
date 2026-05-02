package com.codefactory.reservasmsscheduleservice.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.codefactory.reservasmsscheduleservice.dto.external.ExternalServiceOfferingDTO;
import com.codefactory.reservasmsscheduleservice.exception.ExternalServiceException;
import com.codefactory.reservasmsscheduleservice.exception.ServiceNotFoundInCatalogException;
import com.codefactory.reservasmsscheduleservice.exception.ServiceOwnershipException;

import java.util.UUID;

/**
 * Wrapper for CatalogClient that handles errors and exceptions.
 * Provides safe methods to interact with the Catalog Service.
 */
@Component
@RequiredArgsConstructor
public class CatalogClientWrapper {

    private final CatalogClient catalogClient;

    /**
     * Retrieves a service by ID or throws an exception if not found.
     *
     * @param serviceId the service ID
     * @return the service offering DTO
     * @throws ServiceNotFoundInCatalogException if the service is not found
     * @throws ExternalServiceException if the catalog service is unavailable
     */
    public ExternalServiceOfferingDTO getServiceOrThrow(UUID serviceId) {
        try {
            var response = catalogClient.getServiceById(serviceId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw ExternalServiceException.unavailable("catalog-service");
        } catch (FeignException.NotFound e) {
            throw new ServiceNotFoundInCatalogException(serviceId);
        } catch (FeignException e) {
            throw ExternalServiceException.unavailable("catalog-service");
        }
    }

    /**
     * Validates that a service exists and belongs to the specified provider.
     *
     * @param serviceId the service ID
     * @param providerId the expected provider ID
     * @return the service offering DTO
     * @throws ServiceNotFoundInCatalogException if the service is not found
     * @throws ExternalServiceException if the catalog service is unavailable
     */
    public ExternalServiceOfferingDTO validateServiceOwnership(UUID serviceId, UUID providerId) {
        ExternalServiceOfferingDTO service = getServiceOrThrow(serviceId);
        
        if (!service.getIdProveedor().equals(providerId)) {
            throw new ServiceOwnershipException("No tiene permiso para acceder a este servicio");
        }
        
        return service;
    }
}
