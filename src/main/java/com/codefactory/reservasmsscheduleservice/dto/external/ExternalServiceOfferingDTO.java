package com.codefactory.reservasmsscheduleservice.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO representing a Service Offering from the Catalog Service.
 * Used for communication between Schedule Service and Catalog Service via Feign.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExternalServiceOfferingDTO {

    private UUID idServicio;
    private UUID idProveedor;
    private String nombreServicio;
    private Integer duracionMinutos;
    private BigDecimal precio;
    private String descripcion;
    private Boolean activo;
    private Integer capacidadMaxima;
    private LocalDateTime createdAt;
}
