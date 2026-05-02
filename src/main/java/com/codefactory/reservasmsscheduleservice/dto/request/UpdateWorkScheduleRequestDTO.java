package com.codefactory.reservasmsscheduleservice.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWorkScheduleRequestDTO {
    
    @Pattern(regexp = "^(LUNES|MARTES|MIERCOLES|JUEVES|VIERNES|SABADO|DOMINGO)$", 
             message = "El día de la semana debe ser uno de: LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO")
    private String dayOfWeek;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
}
