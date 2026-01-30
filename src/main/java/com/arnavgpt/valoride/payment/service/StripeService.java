package com.arnavgpt.valoride.payment.service;

import com.arnavgpt.valoride.exception.BusinessException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class StripeService {

    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);

    private final String stripeApiKey;
    private final String webhookSecret;

    public StripeService(
            @Value("${stripe.api-key}") String stripeApiKey,
            @Value("${stripe.webhook-secret}") String webhookSecret) {
        this.stripeApiKey = stripeApiKey;
        this.webhookSecret = webhookSecret;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        logger.info("Stripe initialized");
    }

    /**
     * Create a PaymentIntent for card payments
     */
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency, UUID rideId, UUID userId) {
        try {
            // Stripe expects amount in smallest currency unit (paise for INR)
            long amountInSmallestUnit = amount.multiply(BigDecimal.valueOf(100)).longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInSmallestUnit)
                    .setCurrency(currency.toLowerCase())
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("ride_id", rideId.toString())
                    .putMetadata("user_id", userId.toString())
                    .setDescription("ValoRide - Ride Payment")
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            logger.info("Created PaymentIntent: {} for ride: {}", paymentIntent.getId(), rideId);

            return paymentIntent;

        } catch (StripeException e) {
            logger.error("Stripe error creating PaymentIntent: {}", e.getMessage());
            throw new BusinessException("Payment initialization failed: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Retrieve a PaymentIntent
     */
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) {
        try {
            return PaymentIntent.retrieve(paymentIntentId);
        } catch (StripeException e) {
            logger.error("Stripe error retrieving PaymentIntent: {}", e.getMessage());
            throw new BusinessException("Failed to retrieve payment: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Cancel a PaymentIntent
     */
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return paymentIntent.cancel();
        } catch (StripeException e) {
            logger.error("Stripe error cancelling PaymentIntent: {}", e.getMessage());
            throw new BusinessException("Failed to cancel payment: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Create a refund
     */
    public Refund createRefund(String paymentIntentId, BigDecimal amount) {
        try {
            long amountInSmallestUnit = amount.multiply(BigDecimal.valueOf(100)).longValue();

            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .setAmount(amountInSmallestUnit)
                    .build();

            Refund refund = Refund.create(params);
            logger.info("Created refund: {} for PaymentIntent: {}", refund.getId(), paymentIntentId);

            return refund;

        } catch (StripeException e) {
            logger.error("Stripe error creating refund: {}", e.getMessage());
            throw new BusinessException("Refund failed: " + e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /**
     * Verify webhook signature
     */
    public boolean verifyWebhookSignature(String payload, String sigHeader) {
        try {
            com.stripe.net.Webhook.constructEvent(payload, sigHeader, webhookSecret);
            return true;
        } catch (Exception e) {
            logger.error("Webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }
}