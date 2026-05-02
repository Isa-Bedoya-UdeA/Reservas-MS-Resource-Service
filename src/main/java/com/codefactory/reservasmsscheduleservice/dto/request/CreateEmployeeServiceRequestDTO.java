package com.codefactory.reservasmsscheduleservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a new employee-service association.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeServiceRequestDTO {

    @NotNull(message = "El ID del empleado es obligatorio")
    private UUID employeeId;

    @NotNull(message = "El ID del servicio es obligatorio")
    private UUID serviceId;
}
