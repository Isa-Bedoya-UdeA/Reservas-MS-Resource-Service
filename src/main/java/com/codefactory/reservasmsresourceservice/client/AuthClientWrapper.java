package com.codefactory.reservasmsresourceservice.client;

import com.codefactory.reservasmsresourceservice.dto.external.ExternalClientDTO;
import com.codefactory.reservasmsresourceservice.dto.external.ExternalProviderDTO;
import com.codefactory.reservasmsresourceservice.exception.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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