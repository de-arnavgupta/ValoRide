package com.arnavgpt.valoride.payment.service;

import com.arnavgpt.valoride.exception.BusinessException;
import com.arnavgpt.valoride.exception.ForbiddenException;
import com.arnavgpt.valoride.exception.ResourceNotFoundException;
import com.arnavgpt.valoride.payment.dto.CreatePaymentRequest;
import com.arnavgpt.valoride.payment.dto.PaymentResponse;
import com.arnavgpt.valoride.payment.entity.Payment;
import com.arnavgpt.valoride.payment.entity.PaymentMethod;
import com.arnavgpt.valoride.payment.entity.PaymentStatus;
import com.arnavgpt.valoride.payment.repository.PaymentRepository;
import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import com.arnavgpt.valoride.ride.service.RideService;
import com.arnavgpt.valoride.user.entity.User;
import com.arnavgpt.valoride.user.service.UserService;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final RideService rideService;
    private final UserService userService;
    private final StripeService stripeService;

    public PaymentService(PaymentRepository paymentRepository, RideService rideService,
                          UserService userService, StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.rideService = rideService;
        this.userService = userService;
        this.stripeService = stripeService;
    }

    /**
     * Create a payment for a completed ride
     */
    @Transactional
    public PaymentResponse createPayment(UUID userId, CreatePaymentRequest request) {
        Ride ride = rideService.findById(request.getRideId());

        // Verify the ride is completed
        if (ride.getStatus() != RideStatus.COMPLETED) {
            throw new BusinessException("Payment can only be made for completed rides", HttpStatus.BAD_REQUEST);
        }

        // Verify the user is the rider
        if (!ride.getRider().getId().equals(userId)) {
            throw new ForbiddenException("You can only pay for your own rides");
        }

        // Check if payment already exists
        if (paymentRepository.existsByRideId(ride.getId())) {
            throw new BusinessException("Payment already exists for this ride", HttpStatus.CONFLICT);
        }

        User user = userService.findById(userId);
        BigDecimal amount = ride.getFinalFare();

        Payment payment = new Payment();
        payment.setRide(ride);
        payment.setUser(user);
        payment.setAmount(amount);
        payment.setCurrency("INR");
        payment.setPaymentMethod(request.getPaymentMethod());

        if (request.getPaymentMethod() == PaymentMethod.CARD) {
            // Create Stripe PaymentIntent
            PaymentIntent paymentIntent = stripeService.createPaymentIntent(
                    amount, "INR", ride.getId(), userId);

            payment.setStripePaymentIntentId(paymentIntent.getId());
            payment.setStripeClientSecret(paymentIntent.getClientSecret());
            payment.setStatus(PaymentStatus.PROCESSING);
        } else if (request.getPaymentMethod() == PaymentMethod.CASH) {
            // Cash payment - mark as pending until driver confirms
            payment.setStatus(PaymentStatus.PENDING);
        } else {
            // UPI/Wallet - for MVP, treat as pending
            payment.setStatus(PaymentStatus.PENDING);
        }

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment created: {} for ride: {}", savedPayment.getId(), ride.getId());

        return PaymentResponse.fromEntity(savedPayment);
    }

    /**
     * Confirm cash payment (by driver)
     */
    @Transactional
    public PaymentResponse confirmCashPayment(UUID driverUserId, UUID paymentId) {
        Payment payment = findById(paymentId);

        // Verify the payment is for cash
        if (payment.getPaymentMethod() != PaymentMethod.CASH) {
            throw new BusinessException("This payment is not a cash payment", HttpStatus.BAD_REQUEST);
        }

        // Verify the payment is pending
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Payment is not pending", HttpStatus.BAD_REQUEST);
        }

        // Verify the driver is assigned to this ride
        Ride ride = payment.getRide();
        if (ride.getDriver() == null || !ride.getDriver().getUser().getId().equals(driverUserId)) {
            throw new ForbiddenException("You are not authorized to confirm this payment");
        }

        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setPaidAt(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Cash payment confirmed: {}", paymentId);

        return PaymentResponse.fromEntity(savedPayment);
    }

    /**
     * Handle Stripe webhook for payment confirmation
     */
    @Transactional
    public void handleStripeWebhook(String paymentIntentId, String status) {
        Payment payment = paymentRepository.findByStripePaymentIntentId(paymentIntentId)
                .orElse(null);

        if (payment == null) {
            logger.warn("Payment not found for PaymentIntent: {}", paymentIntentId);
            return;
        }

        switch (status) {
            case "succeeded":
                payment.setStatus(PaymentStatus.COMPLETED);
                payment.setPaidAt(LocalDateTime.now());
                logger.info("Payment succeeded: {}", payment.getId());
                break;
            case "payment_failed":
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment failed");
                logger.info("Payment failed: {}", payment.getId());
                break;
            case "canceled":
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment canceled");
                logger.info("Payment canceled: {}", payment.getId());
                break;
            default:
                logger.debug("Unhandled payment status: {}", status);
                return;
        }

        paymentRepository.save(payment);
    }

    /**
     * Get payment by ride ID
     */
    public PaymentResponse getPaymentByRideId(UUID rideId, UUID userId) {
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "rideId", rideId));

        // Verify access
        Ride ride = payment.getRide();
        boolean isRider = ride.getRider().getId().equals(userId);
        boolean isDriver = ride.getDriver() != null && ride.getDriver().getUser().getId().equals(userId);

        if (!isRider && !isDriver) {
            throw new ForbiddenException("You don't have access to this payment");
        }

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(UUID paymentId, UUID userId) {
        Payment payment = findById(paymentId);

        // Verify access
        if (!payment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You don't have access to this payment");
        }

        return PaymentResponse.fromEntity(payment);
    }

    /**
     * Get user's payment history
     */
    public Page<PaymentResponse> getUserPaymentHistory(UUID userId, Pageable pageable) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(PaymentResponse::fromEntity);
    }

    /**
     * Refund a payment (admin only)
     */
    @Transactional
    public PaymentResponse refundPayment(UUID paymentId, BigDecimal refundAmount) {
        Payment payment = findById(paymentId);

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new BusinessException("Only completed payments can be refunded", HttpStatus.BAD_REQUEST);
        }

        if (refundAmount == null) {
            refundAmount = payment.getAmount();
        }

        if (refundAmount.compareTo(payment.getAmount()) > 0) {
            throw new BusinessException("Refund amount cannot exceed payment amount", HttpStatus.BAD_REQUEST);
        }

        // If it's a Stripe payment, create refund in Stripe
        if (payment.getPaymentMethod() == PaymentMethod.CARD &&
                payment.getStripePaymentIntentId() != null) {
            stripeService.createRefund(payment.getStripePaymentIntentId(), refundAmount);
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setRefundAmount(refundAmount);

        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment refunded: {} - Amount: {}", paymentId, refundAmount);

        return PaymentResponse.fromEntity(savedPayment);
    }

    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", paymentId));
    }
}