package com.arnavgpt.valoride.payment.dto;

import com.arnavgpt.valoride.payment.entity.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreatePaymentRequest {

    @NotNull(message = "Ride ID is required")
    private UUID rideId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public CreatePaymentRequest() {
    }

    public CreatePaymentRequest(UUID rideId, PaymentMethod paymentMethod) {
        this.rideId = rideId;
        this.paymentMethod = paymentMethod;
    }

    public UUID getRideId() {
        return rideId;
    }

    public void setRideId(UUID rideId) {
        this.rideId = rideId;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}