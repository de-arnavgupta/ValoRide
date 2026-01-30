package com.arnavgpt.valoride.payment.controller;

import com.arnavgpt.valoride.common.dto.ApiResponse;
import com.arnavgpt.valoride.config.CustomUserDetails;
import com.arnavgpt.valoride.payment.dto.ConfirmCashPaymentRequest;
import com.arnavgpt.valoride.payment.dto.CreatePaymentRequest;
import com.arnavgpt.valoride.payment.dto.PaymentResponse;
import com.arnavgpt.valoride.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/payments")
@Tag(name = "Payments", description = "Payment management endpoints")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('RIDER')")
    @Operation(summary = "Create payment", description = "Create payment for a completed ride")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreatePaymentRequest request) {

        PaymentResponse response = paymentService.createPayment(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Payment initiated", response));
    }

    @PostMapping("/cash/confirm")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Confirm cash payment", description = "Driver confirms cash received")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmCashPayment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ConfirmCashPaymentRequest request) {

        PaymentResponse response = paymentService.confirmCashPayment(
                userDetails.getId(), request.getPaymentId());
        return ResponseEntity.ok(ApiResponse.success("Cash payment confirmed", response));
    }

    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Get payment details")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID paymentId) {

        PaymentResponse response = paymentService.getPaymentById(paymentId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment by ride", description = "Get payment for a specific ride")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByRideId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        PaymentResponse response = paymentService.getPaymentByRideId(rideId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('RIDER')")
    @Operation(summary = "Get payment history", description = "Get user's payment history")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> getPaymentHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<PaymentResponse> payments = paymentService.getUserPaymentHistory(
                userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(payments));
    }
}