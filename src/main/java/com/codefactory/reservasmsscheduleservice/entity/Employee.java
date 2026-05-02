package com.codefactory.reservasmsscheduleservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity representing an employee associated with a provider.
 * Table: empleado
 */
@Entity
@Table(name = "empleado")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_empleado")
    private UUID id;

    @Column(name = "id_proveedor", nullable = false)
    private UUID providerId;

    @Column(name = "nombre_completo", nullable = false, length = 150)
    private String fullName;

    @Column(name = "telefono", length = 20)
    private String phone;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "fecha_contratacion")
    private LocalDateTime hireDate;

    @Column(name = "notas", length = 500)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
