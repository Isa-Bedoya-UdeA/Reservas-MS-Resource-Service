package com.codefactory.reservasmsscheduleservice.mapper;

import org.mapstruct.*;

import com.codefactory.reservasmsscheduleservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsscheduleservice.dto.response.WorkScheduleResponseDTO;
import com.codefactory.reservasmsscheduleservice.entity.WorkSchedule;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    
    WorkSchedule toEntity(CreateWorkScheduleRequestDTO requestDTO);
    
    WorkScheduleResponseDTO toDto(WorkSchedule workSchedule);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateWorkScheduleRequestDTO requestDTO, @MappingTarget WorkSchedule workSchedule);
}
