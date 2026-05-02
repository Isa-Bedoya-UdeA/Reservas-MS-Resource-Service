package com.codefactory.reservasmsresourceservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "bloqueo_horario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleBlock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_bloqueo")
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_empleado", nullable = false, foreignKey = @ForeignKey(name = "fk_bloqueo_horario_empleado"))
    private Employee employee;
    
    @Column(name = "id_reserva")
    private UUID reservationId;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate date;
    
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "hora_fin", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "tipo_bloqueo", nullable = false, length = 20)
    @Builder.Default
    private String blockType = "RESERVA";
    
    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private java.time.LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }
}
