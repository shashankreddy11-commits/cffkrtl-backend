package com.coffeekartel.controller;

import com.coffeekartel.dto.response.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Public health and system readiness diagnostics")
public class HealthController {

    @GetMapping("/health")
    @Operation(
            summary = "Service Health Check",
            description = "Returns current service status and service name without requiring authentication."
    )
    @ApiResponse(responseCode = "200", description = "Service is UP and operational")
    public ResponseEntity<HealthResponse> getHealth() {
        return ResponseEntity.ok(new HealthResponse("UP", "The Coffee Kartel Backend"));
    }
}
