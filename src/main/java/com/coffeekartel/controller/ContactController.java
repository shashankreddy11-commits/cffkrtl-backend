package com.coffeekartel.controller;

import com.coffeekartel.dto.request.ContactRequest;
import com.coffeekartel.dto.response.ApiResponse;
import com.coffeekartel.dto.response.ContactResponse;
import com.coffeekartel.service.ContactService;
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
@Tag(name = "Contact", description = "Endpoints for guest inquiries and feedback messages")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/contact")
    @Operation(summary = "Submit contact message", description = "Public endpoint to send inquiries, feedback, or requests to the café")
    public ResponseEntity<ApiResponse<ContactResponse>> submitContact(@Valid @RequestBody ContactRequest request) {
        ContactResponse response = contactService.submitMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thank you for reaching out! We will respond shortly.", response));
    }

    @GetMapping("/admin/contact")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List contact messages (Admin)", description = "View guest inquiries and feedback")
    public ResponseEntity<List<ContactResponse>> getAllMessages() {
        return ResponseEntity.ok(contactService.getAllMessages());
    }

    @PatchMapping("/admin/contact/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark contact message resolved (Admin)", description = "Mark guest inquiry as handled")
    public ResponseEntity<ContactResponse> resolveMessage(
            @PathVariable Long id,
            @RequestParam boolean resolved) {
        return ResponseEntity.ok(contactService.markResolved(id, resolved));
    }
}
