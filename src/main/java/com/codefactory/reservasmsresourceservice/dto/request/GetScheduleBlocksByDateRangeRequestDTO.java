package com.codefactory.reservasmsresourceservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetScheduleBlocksByDateRangeRequestDTO {

    @NotNull(message = "El ID del empleado es requerido")
    private UUID employeeId;

    @NotNull(message = "La fecha de inicio es requerida")
    @Future(message = "La fecha de inicio debe ser futura")
    private LocalDate startDate;

    @NotNull(message = "La fecha de fin es requerida")
    @Future(message = "La fecha de fin debe ser futura")
    private LocalDate endDate;
}
