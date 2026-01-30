package com.arnavgpt.valoride.ride.dto;

import jakarta.validation.constraints.Size;

public class CancelRideRequest {

    @Size(max = 500, message = "Reason too long")
    private String reason;

    public CancelRideRequest() {
    }

    public CancelRideRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}