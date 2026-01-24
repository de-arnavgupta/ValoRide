package com.arnavgpt.valoride.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final LocalDateTime timestamp;
    private final int status;
    private final String message;
    private final T data;
    private final PageInfo page;

    private ApiResponse(LocalDateTime timestamp, int status, String message, T data, PageInfo page) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
        this.page = page;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(LocalDateTime.now(), 200, "Success", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(LocalDateTime.now(), 200, message, data, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(LocalDateTime.now(), 201, "Created successfully", data, null);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(LocalDateTime.now(), 201, message, data, null);
    }

    public static <T> ApiResponse<T> success(T data, PageInfo pageInfo) {
        return new ApiResponse<>(LocalDateTime.now(), 200, "Success", data, pageInfo);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public PageInfo getPage() {
        return page;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageInfo {

        private final int pageNumber;
        private final int pageSize;
        private final long totalElements;
        private final int totalPages;
        private final boolean first;
        private final boolean last;

        public PageInfo(int pageNumber, int pageSize, long totalElements, int totalPages, boolean first, boolean last) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.first = first;
            this.last = last;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public boolean isFirst() {
            return first;
        }

        public boolean isLast() {
            return last;
        }
    }
}