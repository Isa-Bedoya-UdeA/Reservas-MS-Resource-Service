package com.codefactory.reservasmsresourceservice.controller;

import com.codefactory.reservasmsresourceservice.dto.request.CreateScheduleBlockRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.ScheduleBlockResponseDTO;
import com.codefactory.reservasmsresourceservice.service.ScheduleBlockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedule/schedule-blocks")
@RequiredArgsConstructor
@Tag(name = "Schedule Block Management", description = "API para gestionar bloqueos de horario por fecha específica")
@SecurityRequirement(name = "bearerAuth")
public class ScheduleBlockController {

    private final ScheduleBlockService scheduleBlockService;

    @PostMapping
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Create schedule block",
        description = "Crea un nuevo bloqueo de horario por fecha específica. Solo el proveedor dueño del empleado puede crear bloqueos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Schedule block created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "409", description = "Schedule block conflicts with existing block or employee doesn't work during this time")
    })
    public ResponseEntity<ScheduleBlockResponseDTO> createScheduleBlock(
            @Valid @RequestBody CreateScheduleBlockRequestDTO request) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        ScheduleBlockResponseDTO response = scheduleBlockService.createScheduleBlock(request, providerId);
        return ResponseEntity.status(201).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Delete schedule block (Soft Delete)",
        description = "Desactiva un bloqueo de horario (soft delete). Solo el proveedor dueño del empleado puede eliminarlo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Schedule block deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Schedule block not found")
    })
    public ResponseEntity<Void> deleteScheduleBlock(
            @Parameter(description = "ID del bloqueo de horario") @PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        scheduleBlockService.deleteScheduleBlock(id, providerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Get schedule block by ID",
        description = "Obtiene un bloqueo de horario específico por su ID. Solo el proveedor dueño del empleado puede verlo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule block found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Schedule block not found")
    })
    public ResponseEntity<ScheduleBlockResponseDTO> getScheduleBlockById(
            @Parameter(description = "ID del bloqueo de horario") @PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        ScheduleBlockResponseDTO response = scheduleBlockService.getScheduleBlockById(id, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Get all schedule blocks by employee",
        description = "Obtiene todos los bloqueos de horario activos de un empleado. Solo el proveedor dueño puede verlos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule blocks found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<ScheduleBlockResponseDTO>> getScheduleBlocksByEmployee(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        List<ScheduleBlockResponseDTO> response = scheduleBlockService.getScheduleBlocksByEmployee(employeeId, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/public")
    @Operation(
        summary = "Get schedule blocks by employee (Public)",
        description = "Obtiene los bloqueos de horario activos de un empleado para visualización pública. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule blocks found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<ScheduleBlockResponseDTO>> getScheduleBlocksByEmployeePublic(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId) {
        List<ScheduleBlockResponseDTO> response = scheduleBlockService.getScheduleBlocksByEmployeePublic(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/date-range")
    @Operation(
        summary = "Get schedule blocks by employee and date range",
        description = "Obtiene los bloqueos de horario de un empleado en un rango de fechas específico. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule blocks found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<ScheduleBlockResponseDTO>> getScheduleBlocksByEmployeeAndDateRange(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId,
            @Parameter(description = "Fecha de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Fecha de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ScheduleBlockResponseDTO> response = scheduleBlockService.getScheduleBlocksByEmployeeAndDateRange(employeeId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/date")
    @Operation(
        summary = "Get schedule blocks by employee and date",
        description = "Obtiene los bloqueos de horario de un empleado para una fecha específica. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Schedule blocks found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<ScheduleBlockResponseDTO>> getScheduleBlocksByEmployeeAndDate(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId,
            @Parameter(description = "Fecha específica") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ScheduleBlockResponseDTO> response = scheduleBlockService.getScheduleBlocksByEmployeeAndDate(employeeId, date);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reservation")
    @Operation(
        summary = "Create reservation block (Internal)",
        description = "Crea un bloqueo de horario para una reserva. Usado internamente por el microservicio de reservas. Requiere autenticación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Reservation block created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or employee doesn't work during this time"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "409", description = "Schedule block conflicts with existing block")
    })
    public ResponseEntity<Void> createReservationBlock(
            @Parameter(description = "ID del empleado") @RequestParam UUID employeeId,
            @Parameter(description = "ID de la reserva") @RequestParam UUID reservationId,
            @Parameter(description = "Fecha de la reserva") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Hora de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime startTime,
            @Parameter(description = "Hora de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime endTime) {
        scheduleBlockService.createReservationBlock(employeeId, reservationId, date, startTime, endTime);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/reservation/{reservationId}")
    @Operation(
        summary = "Cancel reservation block (Internal)",
        description = "Cancela un bloqueo de horario de una reserva. Usado internamente por el microservicio de reservas. Requiere autenticación."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Reservation block cancelled successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    public ResponseEntity<Void> cancelReservationBlock(
            @Parameter(description = "ID de la reserva") @PathVariable UUID reservationId) {
        scheduleBlockService.cancelReservationBlock(reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-availability")
    @Operation(
        summary = "Check employee availability",
        description = "Verifica si un empleado está disponible en una fecha y hora específicas. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Availability checked successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<Boolean> checkEmployeeAvailability(
            @Parameter(description = "ID del empleado") @RequestParam UUID employeeId,
            @Parameter(description = "Fecha") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Hora de inicio") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime startTime,
            @Parameter(description = "Hora de fin") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime endTime) {
        boolean isAvailable = scheduleBlockService.isEmployeeAvailable(employeeId, date, startTime, endTime);
        return ResponseEntity.ok(isAvailable);
    }
}
