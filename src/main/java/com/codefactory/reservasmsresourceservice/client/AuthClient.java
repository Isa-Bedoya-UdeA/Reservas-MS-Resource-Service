package com.codefactory.reservasmsresourceservice.client;

import com.codefactory.reservasmsresourceservice.config.FeignConfig;
import com.codefactory.reservasmsresourceservice.dto.external.ExternalClientDTO;
import com.codefactory.reservasmsresourceservice.dto.external.ExternalProviderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "auth-service",
        url = "${services.auth-service.url}",
        configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/api/users/clients/{id}")
    ResponseEntity<ExternalClientDTO> getClientById(@PathVariable("id") Long id);

    @GetMapping("/api/users/providers/{id}")
    ResponseEntity<ExternalProviderDTO> getProviderById(@PathVariable("id") Long id);
}