package com.codefactory.reservasmsscheduleservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.codefactory.reservasmsscheduleservice.config.FeignConfig;
import com.codefactory.reservasmsscheduleservice.dto.external.ExternalServiceOfferingDTO;

import java.util.UUID;

/**
 * Feign Client for communicating with the Catalog Service.
 * Used to validate and retrieve service information.
 */
@FeignClient(
        name = "catalog-service",
        url = "${services.catalog-service.url}",
        configuration = FeignConfig.class
)
public interface CatalogClient {

    /**
     * Retrieves a service offering by its ID.
     *
     * @param serviceId the service ID
     * @return the service offering DTO
     */
    @GetMapping("/api/catalog/services/{id}")
    ResponseEntity<ExternalServiceOfferingDTO> getServiceById(@PathVariable("id") UUID serviceId);
}
