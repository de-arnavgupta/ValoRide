package com.arnavgpt.valoride.payment.controller;

import com.arnavgpt.valoride.payment.service.PaymentService;
import com.arnavgpt.valoride.payment.service.StripeService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/payments/webhook")
@Tag(name = "Webhook", description = "Stripe webhook endpoint")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    private final PaymentService paymentService;
    private final StripeService stripeService;

    public StripeWebhookController(PaymentService paymentService, StripeService stripeService) {
        this.paymentService = paymentService;
        this.stripeService = stripeService;
    }

    @PostMapping
    @Operation(summary = "Stripe webhook", description = "Handle Stripe payment events")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeService.getWebhookSecret());
        } catch (Exception e) {
            logger.error("Webhook signature verification failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }

        String eventType = event.getType();
        logger.info("Received Stripe webhook: {}", eventType);

        try {
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(payload);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(payload);
                    break;
                case "payment_intent.canceled":
                    handlePaymentIntentCanceled(payload);
                    break;
                default:
                    logger.debug("Unhandled event type: {}", eventType);
            }
        } catch (Exception e) {
            logger.error("Error processing webhook: {}", e.getMessage());
            // Still return 200 to prevent Stripe from retrying
        }

        return ResponseEntity.ok("Received");
    }

    private void handlePaymentIntentSucceeded(String payload) {
        String paymentIntentId = extractPaymentIntentId(payload);
        if (paymentIntentId != null) {
            paymentService.handleStripeWebhook(paymentIntentId, "succeeded");
        }
    }

    private void handlePaymentIntentFailed(String payload) {
        String paymentIntentId = extractPaymentIntentId(payload);
        if (paymentIntentId != null) {
            paymentService.handleStripeWebhook(paymentIntentId, "payment_failed");
        }
    }

    private void handlePaymentIntentCanceled(String payload) {
        String paymentIntentId = extractPaymentIntentId(payload);
        if (paymentIntentId != null) {
            paymentService.handleStripeWebhook(paymentIntentId, "canceled");
        }
    }

    private String extractPaymentIntentId(String payload) {
        try {
            JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();
            JsonObject data = jsonObject.getAsJsonObject("data");
            JsonObject object = data.getAsJsonObject("object");
            return object.get("id").getAsString();
        } catch (Exception e) {
            logger.error("Failed to extract PaymentIntent ID: {}", e.getMessage());
            return null;
        }
    }
}