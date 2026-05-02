package com.codefactory.reservasmsresourceservice.service;

import com.codefactory.reservasmsresourceservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.WorkScheduleResponseDTO;

import java.util.List;
import java.util.UUID;

public interface WorkScheduleService {
    
    WorkScheduleResponseDTO createWorkSchedule(CreateWorkScheduleRequestDTO request, UUID providerIdFromJWT);
    
    WorkScheduleResponseDTO updateWorkSchedule(UUID id, UpdateWorkScheduleRequestDTO request, UUID providerIdFromJWT);
    
    void deleteWorkSchedule(UUID id, UUID providerIdFromJWT);
    
    WorkScheduleResponseDTO getWorkScheduleById(UUID id, UUID providerIdFromJWT);
    
    List<WorkScheduleResponseDTO> getWorkSchedulesByEmployee(UUID employeeId, UUID providerIdFromJWT);
    
    List<WorkScheduleResponseDTO> getActiveWorkSchedulesByEmployee(UUID employeeId);
    
    List<WorkScheduleResponseDTO> getWorkSchedulesByEmployeePublic(UUID employeeId);
}
