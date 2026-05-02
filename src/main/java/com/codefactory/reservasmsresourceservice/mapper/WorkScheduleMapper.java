package com.codefactory.reservasmsresourceservice.mapper;

import com.codefactory.reservasmsresourceservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.WorkScheduleResponseDTO;
import com.codefactory.reservasmsresourceservice.entity.WorkSchedule;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    
    WorkSchedule toEntity(CreateWorkScheduleRequestDTO requestDTO);
    
    WorkScheduleResponseDTO toDto(WorkSchedule workSchedule);
    
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UpdateWorkScheduleRequestDTO requestDTO, @MappingTarget WorkSchedule workSchedule);
}
