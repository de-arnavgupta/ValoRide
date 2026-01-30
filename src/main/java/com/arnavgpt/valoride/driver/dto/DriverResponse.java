package com.arnavgpt.valoride.driver.dto;

import com.arnavgpt.valoride.driver.entity.ApprovalStatus;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String phone;
    private String licenseNumber;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private BigDecimal currentLatitude;
    private BigDecimal currentLongitude;
    private boolean available;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private BigDecimal rating;
    private int totalRides;
    private BigDecimal totalEarnings;
    private Double distanceKm; // Distance from requested location
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DriverResponse() {
    }

    public static DriverResponse fromEntity(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setUserId(driver.getUser().getId());
        response.setName(driver.getUser().getName());
        response.setEmail(driver.getUser().getEmail());
        response.setPhone(driver.getUser().getPhone());
        response.setLicenseNumber(driver.getLicenseNumber());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setVehicleType(driver.getVehicleType());
        response.setCurrentLatitude(driver.getCurrentLatitude());
        response.setCurrentLongitude(driver.getCurrentLongitude());
        response.setAvailable(driver.isAvailable());
        response.setApprovalStatus(driver.getApprovalStatus());
        response.setRejectionReason(driver.getRejectionReason());
        response.setRating(driver.getRating());
        response.setTotalRides(driver.getTotalRides());
        response.setTotalEarnings(driver.getTotalEarnings());
        response.setCreatedAt(driver.getCreatedAt());
        response.setUpdatedAt(driver.getUpdatedAt());
        return response;
    }

    public static DriverResponse fromEntityWithDistance(Driver driver, Double distanceKm) {
        DriverResponse response = fromEntity(driver);
        response.setDistanceKm(distanceKm);
        return response;
    }

    // For public view - hide sensitive info
    public static DriverResponse fromEntityPublic(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setId(driver.getId());
        response.setName(driver.getUser().getName());
        response.setVehicleType(driver.getVehicleType());
        response.setVehicleNumber(driver.getVehicleNumber());
        response.setRating(driver.getRating());
        response.setTotalRides(driver.getTotalRides());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(BigDecimal currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public BigDecimal getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(BigDecimal currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}