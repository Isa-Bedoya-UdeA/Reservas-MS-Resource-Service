package com.codefactory.reservasmsscheduleservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWorkScheduleRequestDTO {
    
    @NotNull(message = "El ID del empleado es requerido")
    private UUID employeeId;
    
    @NotBlank(message = "El día de la semana es requerido")
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
             message = "El día de la semana debe ser uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO")
    private String dayOfWeek;
    
    @NotNull(message = "La hora de inicio es requerida")
    private LocalTime startTime;
    
    @NotNull(message = "La hora de fin es requerida")
    private LocalTime endTime;
}
