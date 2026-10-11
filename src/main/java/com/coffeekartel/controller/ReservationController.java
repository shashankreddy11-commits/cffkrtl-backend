package com.coffeekartel.controller;

import com.coffeekartel.dto.request.ReservationRequest;
import com.coffeekartel.dto.request.UpdateStatusRequest;
import com.coffeekartel.dto.response.ReservationResponse;
import com.coffeekartel.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "Reservations", description = "Endpoints for café table booking and reservation management")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    @Operation(summary = "Book a table reservation", description = "Public endpoint to submit table reservation requests")
    public ResponseEntity<ReservationResponse> submitReservation(@Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reservations/{confirmationCode}")
    @Operation(summary = "Lookup reservation by confirmation reference", description = "Retrieve reservation details using confirmation code")
    public ResponseEntity<ReservationResponse> lookupReservation(@PathVariable String confirmationCode) {
        ReservationResponse response = reservationService.getByConfirmationCode(confirmationCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all reservations", description = "Admin endpoint to view all reservations ordered by newest first")
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PatchMapping("/admin/reservations/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update reservation status", description = "Admin endpoint to confirm, seat, or cancel reservations")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(reservationService.updateStatus(id, request.getStatus()));
    }
}
