package com.codefactory.reservasmsscheduleservice.mapper;

import org.mapstruct.*;

import com.codefactory.reservasmsscheduleservice.dto.request.CreateScheduleBlockRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.ScheduleBlockResponseDTO;
import com.codefactory.reservasmsscheduleservice.entity.ScheduleBlock;

@Mapper(componentModel = "spring")
public interface ScheduleBlockMapper {
    
    ScheduleBlock toEntity(CreateScheduleBlockRequestDTO requestDTO);
    
    ScheduleBlockResponseDTO toDto(ScheduleBlock scheduleBlock);
}
