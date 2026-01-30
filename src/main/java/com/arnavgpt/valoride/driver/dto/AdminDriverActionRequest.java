package com.arnavgpt.valoride.driver.dto;

public class AdminDriverActionRequest {

    private String reason; // Required for rejection, optional for approval

    public AdminDriverActionRequest() {
    }

    public AdminDriverActionRequest(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}