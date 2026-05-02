package com.codefactory.reservasmsresourceservice.mapper;

import com.codefactory.reservasmsresourceservice.dto.request.CreateScheduleBlockRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.ScheduleBlockResponseDTO;
import com.codefactory.reservasmsresourceservice.entity.ScheduleBlock;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ScheduleBlockMapper {
    
    ScheduleBlock toEntity(CreateScheduleBlockRequestDTO requestDTO);
    
    ScheduleBlockResponseDTO toDto(ScheduleBlock scheduleBlock);
}
