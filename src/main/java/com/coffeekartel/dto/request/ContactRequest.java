package com.coffeekartel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ContactRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Contact info (email or phone) is required")
    @Size(max = 150, message = "Contact info must not exceed 150 characters")
    private String contactInfo;

    @NotBlank(message = "Message content is required")
    private String message;

    public ContactRequest() {
    }

    public ContactRequest(String name, String contactInfo, String message) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.message = message;
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
}
