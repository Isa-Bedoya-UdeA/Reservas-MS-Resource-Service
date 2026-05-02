package com.codefactory.reservasmsscheduleservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleResponseDTO {
    
    private UUID id;
    private UUID employeeId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
