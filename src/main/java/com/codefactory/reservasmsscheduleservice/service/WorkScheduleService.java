package com.codefactory.reservasmsscheduleservice.service;

import java.util.List;
import java.util.UUID;

import com.codefactory.reservasmsscheduleservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.WorkScheduleResponseDTO;

public interface WorkScheduleService {
    
    WorkScheduleResponseDTO createWorkSchedule(CreateWorkScheduleRequestDTO request, UUID providerIdFromJWT);
    
    WorkScheduleResponseDTO updateWorkSchedule(UUID id, UpdateWorkScheduleRequestDTO request, UUID providerIdFromJWT);
    
    void deleteWorkSchedule(UUID id, UUID providerIdFromJWT);
    
    WorkScheduleResponseDTO getWorkScheduleById(UUID id, UUID providerIdFromJWT);
    
    List<WorkScheduleResponseDTO> getWorkSchedulesByEmployee(UUID employeeId, UUID providerIdFromJWT);
    
    List<WorkScheduleResponseDTO> getActiveWorkSchedulesByEmployee(UUID employeeId);
    
    List<WorkScheduleResponseDTO> getWorkSchedulesByEmployeePublic(UUID employeeId);
}
