package com.arnavgpt.valoride.notification.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private UUID id;
    private NotificationType type;
    private UUID userId;
    private String userEmail;
    private String userName;
    private String subject;
    private String message;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;

    public NotificationEvent() {
        this.id = UUID.randomUUID();
        this.metadata = new HashMap<>();
        this.createdAt = LocalDateTime.now();
    }

    public NotificationEvent(NotificationType type, UUID userId, String userEmail, String userName) {
        this();
        this.type = type;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
    }

    public static NotificationEvent create(NotificationType type, UUID userId,
                                           String userEmail, String userName) {
        return new NotificationEvent(type, userId, userEmail, userName);
    }

    public NotificationEvent withSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public NotificationEvent withMessage(String message) {
        this.message = message;
        return this;
    }

    public NotificationEvent withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "id=" + id +
                ", type=" + type +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                '}';
    }
}