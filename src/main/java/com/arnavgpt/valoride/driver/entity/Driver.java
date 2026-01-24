package com.arnavgpt.valoride.driver.entity;

import com.arnavgpt.valoride.common.entity.BaseEntity;
import com.arnavgpt.valoride.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "drivers")
public class Driver extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "license_number", nullable = false, length = 50)
    private String licenseNumber;

    @Column(name = "vehicle_number", nullable = false, length = 20)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(name = "is_available")
    private boolean available;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating;

    @Column(name = "total_rides")
    private int totalRides;

    @Column(name = "total_earnings", precision = 12, scale = 2)
    private BigDecimal totalEarnings;

    public Driver() {
        this.available = false;
        this.approvalStatus = ApprovalStatus.PENDING;
        this.rating = BigDecimal.ZERO;
        this.totalRides = 0;
        this.totalEarnings = BigDecimal.ZERO;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public boolean isApproved() {
        return this.approvalStatus == ApprovalStatus.APPROVED;
    }

    public void incrementTotalRides() {
        this.totalRides++;
    }

    public void addEarnings(BigDecimal amount) {
        if (this.totalEarnings == null) {
            this.totalEarnings = BigDecimal.ZERO;
        }
        this.totalEarnings = this.totalEarnings.add(amount);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + getId() +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", vehicleNumber='" + vehicleNumber + '\'' +
                ", vehicleType=" + vehicleType +
                ", available=" + available +
                ", approvalStatus=" + approvalStatus +
                '}';
    }
}