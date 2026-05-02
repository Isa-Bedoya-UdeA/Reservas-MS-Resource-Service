package com.codefactory.reservasmsresourceservice.controller;

import com.codefactory.reservasmsresourceservice.dto.request.CreateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.request.UpdateWorkScheduleRequestDTO;
import com.codefactory.reservasmsresourceservice.dto.response.WorkScheduleResponseDTO;
import com.codefactory.reservasmsresourceservice.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/schedule/work-schedules")
@RequiredArgsConstructor
@Tag(name = "Work Schedule Management", description = "API para gestionar horarios laborales recurrentes de empleados")
@SecurityRequirement(name = "bearerAuth")
public class WorkScheduleController {

    private final WorkScheduleService workScheduleService;

    @PostMapping
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Create work schedule",
        description = "Crea un nuevo horario laboral recurrente para un empleado. Solo el proveedor dueño del empleado puede crear horarios."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Work schedule created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Employee not found"),
        @ApiResponse(responseCode = "409", description = "Work schedule conflicts with existing schedule")
    })
    public ResponseEntity<WorkScheduleResponseDTO> createWorkSchedule(
            @Valid @RequestBody CreateWorkScheduleRequestDTO request) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        WorkScheduleResponseDTO response = workScheduleService.createWorkSchedule(request, providerId);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Update work schedule",
        description = "Actualiza un horario laboral existente. Solo el proveedor dueño del empleado puede modificarlo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work schedule updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    public ResponseEntity<WorkScheduleResponseDTO> updateWorkSchedule(
            @Parameter(description = "ID del horario laboral") @PathVariable UUID id,
            @Valid @RequestBody UpdateWorkScheduleRequestDTO request) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        WorkScheduleResponseDTO response = workScheduleService.updateWorkSchedule(id, request, providerId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Delete work schedule (Hard Delete)",
        description = "Elimina permanentemente un horario laboral. Solo el proveedor dueño del empleado puede eliminarlo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Work schedule deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    public ResponseEntity<Void> deleteWorkSchedule(
            @Parameter(description = "ID del horario laboral") @PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        workScheduleService.deleteWorkSchedule(id, providerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Get work schedule by ID",
        description = "Obtiene un horario laboral específico por su ID. Solo el proveedor dueño del empleado puede verlo."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work schedule found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Work schedule not found")
    })
    public ResponseEntity<WorkScheduleResponseDTO> getWorkScheduleById(
            @Parameter(description = "ID del horario laboral") @PathVariable UUID id) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        WorkScheduleResponseDTO response = workScheduleService.getWorkScheduleById(id, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('PROVEEDOR')")
    @Operation(
        summary = "Get all work schedules by employee",
        description = "Obtiene todos los horarios laborales de un empleado (activos e inactivos). Solo el proveedor dueño puede verlos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work schedules found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Does not have PROVEEDOR role or not the owner of the employee"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<WorkScheduleResponseDTO>> getWorkSchedulesByEmployee(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId) {
        String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID providerId = UUID.fromString(userIdStr);
        List<WorkScheduleResponseDTO> response = workScheduleService.getWorkSchedulesByEmployee(employeeId, providerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/active")
    @Operation(
        summary = "Get active work schedules by employee",
        description = "Obtiene los horarios laborales activos de un empleado ordenados por día y hora. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active work schedules found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<WorkScheduleResponseDTO>> getActiveWorkSchedulesByEmployee(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId) {
        List<WorkScheduleResponseDTO> response = workScheduleService.getActiveWorkSchedulesByEmployee(employeeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/{employeeId}/public")
    @Operation(
        summary = "Get work schedules by employee (Public)",
        description = "Obtiene los horarios laborales activos de un empleado para visualización pública. Disponible para cualquier usuario autenticado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Work schedules found"),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    public ResponseEntity<List<WorkScheduleResponseDTO>> getWorkSchedulesByEmployeePublic(
            @Parameter(description = "ID del empleado") @PathVariable UUID employeeId) {
        List<WorkScheduleResponseDTO> response = workScheduleService.getWorkSchedulesByEmployeePublic(employeeId);
        return ResponseEntity.ok(response);
    }
}
