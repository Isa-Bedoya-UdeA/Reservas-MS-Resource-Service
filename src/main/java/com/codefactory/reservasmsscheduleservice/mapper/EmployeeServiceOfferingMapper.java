package com.codefactory.reservasmsscheduleservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.codefactory.reservasmsscheduleservice.dto.response.EmployeeServiceResponseDTO;
import com.codefactory.reservasmsscheduleservice.entity.EmployeeServiceOffering;

/**
 * Mapper for EmployeeServiceOffering entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface EmployeeServiceOfferingMapper {

    /**
     * Maps an EmployeeServiceOffering entity to a response DTO.
     *
     * @param entity the entity to map
     * @return the mapped DTO
     */
    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "serviceId", target = "serviceId")
    @Mapping(source = "active", target = "active")
    @Mapping(source = "assignmentDate", target = "assignmentDate")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    EmployeeServiceResponseDTO toDto(EmployeeServiceOffering entity);
}
