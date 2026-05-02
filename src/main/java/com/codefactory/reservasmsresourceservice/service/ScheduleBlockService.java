package com.codefactory.reservasmsresourceservice.service;

import com.codefactory.reservasmsresourceservice.dto.request.CreateScheduleBlockRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.ScheduleBlockResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ScheduleBlockService {
    
    ScheduleBlockResponseDTO createScheduleBlock(CreateScheduleBlockRequestDTO request, UUID providerIdFromJWT);
    
    void deleteScheduleBlock(UUID id, UUID providerIdFromJWT);
    
    ScheduleBlockResponseDTO getScheduleBlockById(UUID id, UUID providerIdFromJWT);
    
    List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployee(UUID employeeId, UUID providerIdFromJWT);
    
    List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeePublic(UUID employeeId);
    
    List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeeAndDateRange(UUID employeeId, LocalDate startDate, LocalDate endDate);
    
    List<ScheduleBlockResponseDTO> getScheduleBlocksByEmployeeAndDate(UUID employeeId, LocalDate date);
    
    void createReservationBlock(UUID employeeId, UUID reservationId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime);
    
    void cancelReservationBlock(UUID reservationId);
    
    boolean isEmployeeAvailable(UUID employeeId, LocalDate date, java.time.LocalTime startTime, java.time.LocalTime endTime);
}
