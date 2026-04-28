package com.codefactory.reservasmsresourceservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDTO {
    private UUID id;
    private UUID providerId;
    private String fullName;
    private String phone;
    private Boolean active;
    private LocalDateTime hireDate;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
