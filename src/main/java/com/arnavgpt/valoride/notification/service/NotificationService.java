package com.arnavgpt.valoride.notification.service;

import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.notification.dto.NotificationEvent;
import com.arnavgpt.valoride.notification.dto.NotificationType;
import com.arnavgpt.valoride.payment.entity.Payment;
import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final EmailService emailService;

    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Send welcome email on registration
     */
    public void sendWelcomeNotification(User user) {
        NotificationEvent event = NotificationEvent
                .create(NotificationType.WELCOME, user.getId(), user.getEmail(), user.getName());

        emailService.sendNotification(event);
        logger.info("Welcome notification sent to: {}", user.getEmail());
    }

    /**
     * Notify rider when driver accepts ride
     */
    public void sendRideAcceptedNotification(Ride ride) {
        User rider = ride.getRider();
        Driver driver = ride.getDriver();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.RIDE_ACCEPTED, rider.getId(), rider.getEmail(), rider.getName())
                .withMetadata("driverName", driver.getUser().getName())
                .withMetadata("vehicleNumber", driver.getVehicleNumber())
                .withMetadata("vehicleType", driver.getVehicleType().name())
                .withMetadata("driverPhone", driver.getUser().getPhone());

        emailService.sendNotification(event);
        logger.info("Ride accepted notification sent for ride: {}", ride.getId());
    }

    /**
     * Notify rider when driver arrives
     */
    public void sendDriverArrivedNotification(Ride ride) {
        User rider = ride.getRider();
        Driver driver = ride.getDriver();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.DRIVER_ARRIVED, rider.getId(), rider.getEmail(), rider.getName())
                .withMetadata("vehicleNumber", driver.getVehicleNumber())
                .withMetadata("driverName", driver.getUser().getName());

        emailService.sendNotification(event);
        logger.info("Driver arrived notification sent for ride: {}", ride.getId());
    }

    /**
     * Notify rider when ride starts
     */
    public void sendRideStartedNotification(Ride ride) {
        User rider = ride.getRider();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.RIDE_STARTED, rider.getId(), rider.getEmail(), rider.getName())
                .withMetadata("pickupAddress", ride.getPickupAddress())
                .withMetadata("dropAddress", ride.getDropAddress());

        emailService.sendNotification(event);
        logger.info("Ride started notification sent for ride: {}", ride.getId());
    }

    /**
     * Notify both rider and driver when ride completes
     */
    public void sendRideCompletedNotification(Ride ride) {
        // Notify rider
        User rider = ride.getRider();
        NotificationEvent riderEvent = NotificationEvent
                .create(NotificationType.RIDE_COMPLETED, rider.getId(), rider.getEmail(), rider.getName())
                .withMetadata("distance", ride.getDistanceKm())
                .withMetadata("fare", ride.getFinalFare())
                .withMetadata("driverName", ride.getDriver().getUser().getName());

        emailService.sendNotification(riderEvent);

        // Notify driver
        User driverUser = ride.getDriver().getUser();
        NotificationEvent driverEvent = NotificationEvent
                .create(NotificationType.RIDE_COMPLETED, driverUser.getId(), driverUser.getEmail(), driverUser.getName())
                .withMetadata("distance", ride.getDistanceKm())
                .withMetadata("fare", ride.getFinalFare())
                .withMetadata("riderName", rider.getName());

        emailService.sendNotification(driverEvent);

        logger.info("Ride completed notifications sent for ride: {}", ride.getId());
    }

    /**
     * Notify about ride cancellation
     */
    public void sendRideCancelledNotification(Ride ride, String cancelledByRole) {
        User rider = ride.getRider();

        NotificationEvent riderEvent = NotificationEvent
                .create(NotificationType.RIDE_CANCELLED, rider.getId(), rider.getEmail(), rider.getName())
                .withMetadata("reason", ride.getCancelReason() != null ? ride.getCancelReason() : "No reason provided")
                .withMetadata("cancelledBy", cancelledByRole);

        emailService.sendNotification(riderEvent);

        // If driver was assigned, notify them too
        if (ride.getDriver() != null) {
            User driverUser = ride.getDriver().getUser();
            NotificationEvent driverEvent = NotificationEvent
                    .create(NotificationType.RIDE_CANCELLED, driverUser.getId(), driverUser.getEmail(), driverUser.getName())
                    .withMetadata("reason", ride.getCancelReason() != null ? ride.getCancelReason() : "No reason provided")
                    .withMetadata("cancelledBy", cancelledByRole);

            emailService.sendNotification(driverEvent);
        }

        logger.info("Ride cancelled notifications sent for ride: {}", ride.getId());
    }

    /**
     * Notify about successful payment
     */
    public void sendPaymentSuccessNotification(Payment payment) {
        User user = payment.getUser();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.PAYMENT_SUCCESS, user.getId(), user.getEmail(), user.getName())
                .withMetadata("amount", payment.getAmount())
                .withMetadata("currency", payment.getCurrency())
                .withMetadata("paymentMethod", payment.getPaymentMethod().name());

        emailService.sendNotification(event);
        logger.info("Payment success notification sent for payment: {}", payment.getId());
    }

    /**
     * Notify about failed payment
     */
    public void sendPaymentFailedNotification(Payment payment) {
        User user = payment.getUser();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.PAYMENT_FAILED, user.getId(), user.getEmail(), user.getName())
                .withMetadata("amount", payment.getAmount())
                .withMetadata("reason", payment.getFailureReason());

        emailService.sendNotification(event);
        logger.info("Payment failed notification sent for payment: {}", payment.getId());
    }

    /**
     * Notify driver about approval
     */
    public void sendDriverApprovedNotification(Driver driver) {
        User user = driver.getUser();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.DRIVER_APPROVED, user.getId(), user.getEmail(), user.getName());

        emailService.sendNotification(event);
        logger.info("Driver approved notification sent to: {}", user.getEmail());
    }

    /**
     * Notify driver about rejection
     */
    public void sendDriverRejectedNotification(Driver driver) {
        User user = driver.getUser();

        NotificationEvent event = NotificationEvent
                .create(NotificationType.DRIVER_REJECTED, user.getId(), user.getEmail(), user.getName())
                .withMetadata("reason", driver.getRejectionReason() != null ? driver.getRejectionReason() : "Not specified");

        emailService.sendNotification(event);
        logger.info("Driver rejected notification sent to: {}", user.getEmail());
    }
}