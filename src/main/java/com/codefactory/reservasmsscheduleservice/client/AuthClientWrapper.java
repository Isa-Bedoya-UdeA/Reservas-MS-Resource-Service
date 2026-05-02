package com.codefactory.reservasmsscheduleservice.client;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import com.codefactory.reservasmsscheduleservice.dto.external.ExternalClientDTO;
import com.codefactory.reservasmsscheduleservice.dto.external.ExternalProviderDTO;
import com.codefactory.reservasmsscheduleservice.exception.ExternalServiceException;

@Component
@RequiredArgsConstructor
public class AuthClientWrapper {

    private final AuthClient authClient;

    public ExternalClientDTO getClientOrThrow(Long clientId) {
        try {
            var response = authClient.getClientById(clientId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException.NotFound e) {
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException e) {
            throw ExternalServiceException.unavailable("auth-service");
        }
    }

    public ExternalProviderDTO getProviderOrThrow(Long providerId) {
        try {
            var response = authClient.getProviderById(providerId);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException.NotFound e) {
            throw ExternalServiceException.unavailable("auth-service");
        } catch (FeignException e) {
            throw ExternalServiceException.unavailable("auth-service");
        }
    }
}