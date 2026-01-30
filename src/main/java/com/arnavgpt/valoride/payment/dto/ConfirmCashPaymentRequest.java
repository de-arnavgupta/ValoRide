package com.arnavgpt.valoride.payment.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ConfirmCashPaymentRequest {

    @NotNull(message = "Payment ID is required")
    private UUID paymentId;

    public ConfirmCashPaymentRequest() {
    }

    public ConfirmCashPaymentRequest(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }
}