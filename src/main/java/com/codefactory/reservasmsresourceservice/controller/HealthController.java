package com.codefactory.reservasmsresourceservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Endpoints for service health verification")
public class HealthController {

    @GetMapping("/")
    @Operation(
        summary = "Health check",
        description = "Verifies if the service is functioning correctly. Returns status and current timestamp."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Service functioning correctly")
    })
    @SecurityRequirements
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "timestamp", Instant.now()
        ));
    }

    @GetMapping("/version")
    @Operation(
        summary = "Service version",
        description = "Returns the service version and microservice name."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Version information returned successfully")
    })
    @SecurityRequirements
    public ResponseEntity<Map<String, String>> version() {
        return ResponseEntity.ok(Map.of(
                "version", "1.0.0-SNAPSHOT",
                "service", "Reservas-MS-Schedule-Service"
        ));
    }
}