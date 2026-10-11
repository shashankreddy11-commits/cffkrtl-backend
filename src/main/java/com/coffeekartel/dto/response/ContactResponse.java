package com.coffeekartel.dto.response;

import java.time.Instant;

public class ContactResponse {

    private Long id;
    private String name;
    private String contactInfo;
    private String message;
    private boolean resolved;
    private Instant createdAt;

    public ContactResponse() {
    }

    public ContactResponse(Long id, String name, String contactInfo, String message, boolean resolved, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.message = message;
        this.resolved = resolved;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
