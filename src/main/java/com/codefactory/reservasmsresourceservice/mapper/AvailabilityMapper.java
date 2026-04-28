package com.codefactory.reservasmsresourceservice.mapper;

import com.codefactory.reservasmsresourceservice.entity.Availability;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper {
    Availability toEntity(Availability entity);
}
