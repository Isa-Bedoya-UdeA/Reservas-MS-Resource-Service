package com.codefactory.reservasmsresourceservice.entity;

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
 * Entity representing the many-to-many relationship between employees and services.
 * Table: empleado_servicio
 */
@Entity
@Table(name = "empleado_servicio")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_empleado_servicio")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false, foreignKey = @ForeignKey(name = "fk_empleado_servicio_empleado"))
    private Employee employee;

    @Column(name = "id_servicio", nullable = false)
    private UUID serviceId;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime assignmentDate;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
