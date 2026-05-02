package com.codefactory.reservasmsscheduleservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.codefactory.reservasmsscheduleservice.dto.external.ExternalServiceOfferingDTO;

/**
 * DTO representing an employee with their associated services.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeWithServicesResponseDTO {

    private UUID id;
    private UUID providerId;
    private String fullName;
    private String phone;
    private Boolean active;
    private LocalDateTime hireDate;
    private String notes;
    private List<ExternalServiceOfferingDTO> services;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
