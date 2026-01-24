package com.arnavgpt.valoride.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorResponse {

    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final List<FieldError> fieldErrors;
    private final Map<String, Object> metadata;

    private ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message,
                             String path, List<FieldError> fieldErrors, Map<String, Object> metadata) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.fieldErrors = fieldErrors;
        this.metadata = metadata;
    }

    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, error, message, path, null, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public static class Builder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private List<FieldError> fieldErrors;
        private Map<String, Object> metadata;

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder fieldErrors(List<FieldError> fieldErrors) {
            this.fieldErrors = fieldErrors;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(timestamp, status, error, message, path, fieldErrors, metadata);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class FieldError {

        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}