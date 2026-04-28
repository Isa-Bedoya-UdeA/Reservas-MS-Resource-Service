package com.codefactory.reservasmsresourceservice.service;

import com.codefactory.reservasmsresourceservice.dto.request.CreateEmployeeRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateEmployeeRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.EmployeeResponseDTO;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO request, UUID providerIdFromJWT);
    EmployeeResponseDTO updateEmployee(UUID id, UpdateEmployeeRequestDTO request, UUID providerIdFromJWT);
    void deleteEmployee(UUID id, UUID providerIdFromJWT);
    void deactivateEmployee(UUID id, UUID providerIdFromJWT);
    void activateEmployee(UUID id, UUID providerIdFromJWT);
    EmployeeResponseDTO getEmployeeById(UUID id, UUID providerIdFromJWT);
    List<EmployeeResponseDTO> getEmployeesByProvider(UUID providerId, UUID providerIdFromJWT);
    List<EmployeeResponseDTO> getActiveEmployeesByProvider(UUID providerId, UUID providerIdFromJWT);
    List<EmployeeResponseDTO> getActiveEmployees();
}
