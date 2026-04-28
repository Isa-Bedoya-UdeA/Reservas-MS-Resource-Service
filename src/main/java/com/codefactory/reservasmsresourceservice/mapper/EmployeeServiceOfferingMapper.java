package com.codefactory.reservasmsresourceservice.mapper;

import com.codefactory.reservasmsresourceservice.entity.EmployeeServiceOffering;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeServiceOfferingMapper {
    EmployeeServiceOffering toEntity(EmployeeServiceOffering entity);
}
