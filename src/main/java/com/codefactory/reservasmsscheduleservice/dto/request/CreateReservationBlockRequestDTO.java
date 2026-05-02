package com.codefactory.reservasmsscheduleservice.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateReservationBlockRequestDTO {

    @NotNull(message = "El ID del empleado es requerido")
    private UUID employeeId;

    @NotNull(message = "El ID de la reserva es requerido")
    private UUID reservationId;

    @NotNull(message = "La fecha de la reserva es requerida")
    @Future(message = "La fecha de la reserva debe ser futura")
    private LocalDate date;

    @NotNull(message = "La hora de inicio es requerida")
    private LocalTime startTime;

    @NotNull(message = "La hora de fin es requerida")
    private LocalTime endTime;
}
