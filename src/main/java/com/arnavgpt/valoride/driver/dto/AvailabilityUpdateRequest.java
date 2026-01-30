package com.arnavgpt.valoride.driver.dto;

import jakarta.validation.constraints.NotNull;

public class AvailabilityUpdateRequest {

    @NotNull(message = "Availability status is required")
    private Boolean available;

    public AvailabilityUpdateRequest() {
    }

    public AvailabilityUpdateRequest(Boolean available) {
        this.available = available;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}