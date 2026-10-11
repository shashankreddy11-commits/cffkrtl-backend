package com.coffeekartel.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationRequest {

    @NotBlank(message = "Guest name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Contact phone number is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Reservation date is required")
    private LocalDate date;

    @NotNull(message = "Reservation time is required")
    private LocalTime time;

    @Min(value = 1, message = "Party size must be at least 1 guest")
    @Max(value = 20, message = "Party size cannot exceed 20 guests")
    private int partySize;

    private String specialRequests;

    public ReservationRequest() {
    }

    public ReservationRequest(String name, String phone, LocalDate date, LocalTime time, int partySize, String specialRequests) {
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.specialRequests = specialRequests;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public int getPartySize() {
        return partySize;
    }

    public void setPartySize(int partySize) {
        this.partySize = partySize;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }
}
