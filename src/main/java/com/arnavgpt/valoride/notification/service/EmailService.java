package com.arnavgpt.valoride.notification.service;

import com.arnavgpt.valoride.notification.dto.NotificationEvent;
import com.arnavgpt.valoride.notification.dto.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromEmail;
    private final boolean emailEnabled;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:}") String fromEmail,
            @Value("${notification.email.enabled:false}") boolean emailEnabled) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
        this.emailEnabled = emailEnabled;
    }

    @Async
    public void sendNotification(NotificationEvent event) {
        if (!emailEnabled || fromEmail == null || fromEmail.isEmpty()) {
            logger.info("Email disabled. Would send to {}: {}", event.getUserEmail(), event.getSubject());
            return;
        }

        try {
            String subject = event.getSubject() != null ? event.getSubject() : getDefaultSubject(event.getType());
            String body = event.getMessage() != null ? event.getMessage() : buildEmailBody(event);

            sendHtmlEmail(event.getUserEmail(), subject, body);
            logger.info("Email sent to {}: {}", event.getUserEmail(), subject);

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", event.getUserEmail(), e.getMessage());
        }
    }

    @Async
    public void sendSimpleEmail(String to, String subject, String text) {
        if (!emailEnabled || fromEmail == null || fromEmail.isEmpty()) {
            logger.info("Email disabled. Would send to {}: {}", to, subject);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);
            logger.info("Simple email sent to {}", to);

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        if (!emailEnabled || fromEmail == null || fromEmail.isEmpty()) {
            logger.info("Email disabled. Would send HTML to {}: {}", to, subject);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);

        mailSender.send(message);
    }

    private String getDefaultSubject(NotificationType type) {
        return switch (type) {
            case WELCOME -> "Welcome to ValoRide!";
            case RIDE_REQUESTED -> "Your ride has been requested";
            case RIDE_ACCEPTED -> "Driver is on the way!";
            case DRIVER_ARRIVED -> "Your driver has arrived";
            case RIDE_STARTED -> "Your ride has started";
            case RIDE_COMPLETED -> "Ride completed - Thank you!";
            case RIDE_CANCELLED -> "Your ride has been cancelled";
            case PAYMENT_SUCCESS -> "Payment successful";
            case PAYMENT_FAILED -> "Payment failed";
            case DRIVER_APPROVED -> "Congratulations! You're approved as a driver";
            case DRIVER_REJECTED -> "Driver application update";
        };
    }

    private String buildEmailBody(NotificationEvent event) {
        String template = getEmailTemplate(event.getType());
        return populateTemplate(template, event);
    }

    private String getEmailTemplate(NotificationType type) {
        String header = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: #4F46E5; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background: #f9f9f9; }
                    .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; }
                    .btn { background: #4F46E5; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
            <div class="container">
                <div class="header">
                    <h1>ValoRide</h1>
                </div>
                <div class="content">
            """;

        String footer = """
                </div>
                <div class="footer">
                    <p>© 2024 ValoRide. All rights reserved.</p>
                    <p>This is an automated message, please do not reply.</p>
                </div>
            </div>
            </body>
            </html>
            """;

        String content = switch (type) {
            case WELCOME -> """
                <h2>Welcome, {{userName}}!</h2>
                <p>Thank you for joining ValoRide. We're excited to have you on board!</p>
                <p>Start your journey with us and experience convenient rides at your fingertips.</p>
                """;

            case RIDE_ACCEPTED -> """
                <h2>Driver Found!</h2>
                <p>Hi {{userName}},</p>
                <p>Good news! A driver has accepted your ride request.</p>
                <p><strong>Driver:</strong> {{driverName}}</p>
                <p><strong>Vehicle:</strong> {{vehicleNumber}}</p>
                <p>Your driver is on the way to pick you up.</p>
                """;

            case DRIVER_ARRIVED -> """
                <h2>Your Driver Has Arrived</h2>
                <p>Hi {{userName}},</p>
                <p>Your driver has arrived at the pickup location.</p>
                <p><strong>Vehicle:</strong> {{vehicleNumber}}</p>
                <p>Please proceed to meet your driver.</p>
                """;

            case RIDE_COMPLETED -> """
                <h2>Ride Completed</h2>
                <p>Hi {{userName}},</p>
                <p>Thank you for riding with ValoRide!</p>
                <p><strong>Distance:</strong> {{distance}} km</p>
                <p><strong>Fare:</strong> ₹{{fare}}</p>
                <p>We hope you had a great experience. Don't forget to rate your driver!</p>
                """;

            case RIDE_CANCELLED -> """
                <h2>Ride Cancelled</h2>
                <p>Hi {{userName}},</p>
                <p>Your ride has been cancelled.</p>
                <p><strong>Reason:</strong> {{reason}}</p>
                <p>We hope to serve you again soon.</p>
                """;

            case PAYMENT_SUCCESS -> """
                <h2>Payment Successful</h2>
                <p>Hi {{userName}},</p>
                <p>Your payment of <strong>₹{{amount}}</strong> has been processed successfully.</p>
                <p>Thank you for riding with ValoRide!</p>
                """;

            case PAYMENT_FAILED -> """
                <h2>Payment Failed</h2>
                <p>Hi {{userName}},</p>
                <p>Unfortunately, your payment could not be processed.</p>
                <p>Please try again or use a different payment method.</p>
                """;

            case DRIVER_APPROVED -> """
                <h2>Congratulations!</h2>
                <p>Hi {{userName}},</p>
                <p>Your driver application has been approved!</p>
                <p>You can now go online and start accepting rides.</p>
                <p>Welcome to the ValoRide driver community!</p>
                """;

            case DRIVER_REJECTED -> """
                <h2>Application Update</h2>
                <p>Hi {{userName}},</p>
                <p>We regret to inform you that your driver application could not be approved at this time.</p>
                <p><strong>Reason:</strong> {{reason}}</p>
                <p>You may reapply after addressing the issues mentioned.</p>
                """;

            default -> """
                <h2>ValoRide Notification</h2>
                <p>Hi {{userName}},</p>
                <p>{{message}}</p>
                """;
        };

        return header + content + footer;
    }

    private String populateTemplate(String template, NotificationEvent event) {
        String result = template
                .replace("{{userName}}", event.getUserName() != null ? event.getUserName() : "User");

        // Populate from metadata
        if (event.getMetadata() != null) {
            for (var entry : event.getMetadata().entrySet()) {
                String value = entry.getValue() != null ? entry.getValue().toString() : "";
                result = result.replace("{{" + entry.getKey() + "}}", value);
            }
        }

        // Clean up any remaining placeholders
        result = result.replaceAll("\\{\\{\\w+\\}\\}", "");

        return result;
    }
}