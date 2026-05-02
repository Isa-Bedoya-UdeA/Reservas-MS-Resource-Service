package com.codefactory.reservasmsresourceservice.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleBlockResponseDTO {
    
    private UUID id;
    private UUID employeeId;
    private UUID reservationId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String blockType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
