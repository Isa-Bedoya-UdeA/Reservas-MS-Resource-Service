package com.codefactory.reservasmsresourceservice.mapper;

import com.codefactory.reservasmsresourceservice.dto.request.CreateEmployeeRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateEmployeeRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.EmployeeResponseDTO;
import com.codefactory.reservasmsresourceservice.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EmployeeMapper {
    Employee toEntity(CreateEmployeeRequestDTO requestDTO);

    EmployeeResponseDTO toDto(Employee entity);

    void updateEntityFromDto(UpdateEmployeeRequestDTO requestDTO, @MappingTarget Employee entity);
}
