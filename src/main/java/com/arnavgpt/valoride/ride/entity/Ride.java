package com.arnavgpt.valoride.ride.entity;

import com.arnavgpt.valoride.common.entity.BaseEntity;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.arnavgpt.valoride.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rides")
public class Ride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rider_id", nullable = false)
    private User rider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    // Pickup location
    @Column(name = "pickup_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal pickupLatitude;

    @Column(name = "pickup_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal pickupLongitude;

    @Column(name = "pickup_address", length = 500)
    private String pickupAddress;

    // Drop location
    @Column(name = "drop_latitude", nullable = false, precision = 10, scale = 8)
    private BigDecimal dropLatitude;

    @Column(name = "drop_longitude", nullable = false, precision = 11, scale = 8)
    private BigDecimal dropLongitude;

    @Column(name = "drop_address", length = 500)
    private String dropAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RideStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    // Fare details
    @Column(name = "estimated_fare", precision = 10, scale = 2)
    private BigDecimal estimatedFare;

    @Column(name = "final_fare", precision = 10, scale = 2)
    private BigDecimal finalFare;

    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "estimated_duration_mins")
    private Integer estimatedDurationMins;

    // Timestamps
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "matched_at")
    private LocalDateTime matchedAt;

    @Column(name = "arrived_at")
    private LocalDateTime arrivedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Cancellation details
    @Enumerated(EnumType.STRING)
    @Column(name = "cancelled_by")
    private CancelledBy cancelledBy;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    // Rating (rider rates driver)
    @Column(precision = 2, scale = 1)
    private BigDecimal rating;

    @Column(name = "rating_comment", length = 500)
    private String ratingComment;

    public Ride() {
        this.status = RideStatus.REQUESTED;
        this.requestedAt = LocalDateTime.now();
    }

    // State machine transitions
    public boolean canBeAccepted() {
        return this.status == RideStatus.REQUESTED;
    }

    public boolean canArriveAtPickup() {
        return this.status == RideStatus.MATCHED;
    }

    public boolean canStart() {
        return this.status == RideStatus.ARRIVED;
    }

    public boolean canComplete() {
        return this.status == RideStatus.STARTED;
    }

    public boolean canBeCancelled() {
        return this.status == RideStatus.REQUESTED ||
                this.status == RideStatus.MATCHED ||
                this.status == RideStatus.ARRIVED;
    }

    public boolean canBeRated() {
        return this.status == RideStatus.COMPLETED && this.rating == null;
    }

    // Getters and Setters
    public User getRider() {
        return rider;
    }

    public void setRider(User rider) {
        this.rider = rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public BigDecimal getPickupLatitude() {
        return pickupLatitude;
    }

    public void setPickupLatitude(BigDecimal pickupLatitude) {
        this.pickupLatitude = pickupLatitude;
    }

    public BigDecimal getPickupLongitude() {
        return pickupLongitude;
    }

    public void setPickupLongitude(BigDecimal pickupLongitude) {
        this.pickupLongitude = pickupLongitude;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public BigDecimal getDropLatitude() {
        return dropLatitude;
    }

    public void setDropLatitude(BigDecimal dropLatitude) {
        this.dropLatitude = dropLatitude;
    }

    public BigDecimal getDropLongitude() {
        return dropLongitude;
    }

    public void setDropLongitude(BigDecimal dropLongitude) {
        this.dropLongitude = dropLongitude;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getEstimatedFare() {
        return estimatedFare;
    }

    public void setEstimatedFare(BigDecimal estimatedFare) {
        this.estimatedFare = estimatedFare;
    }

    public BigDecimal getFinalFare() {
        return finalFare;
    }

    public void setFinalFare(BigDecimal finalFare) {
        this.finalFare = finalFare;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedDurationMins() {
        return estimatedDurationMins;
    }

    public void setEstimatedDurationMins(Integer estimatedDurationMins) {
        this.estimatedDurationMins = estimatedDurationMins;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }

    public LocalDateTime getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(LocalDateTime arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public CancelledBy getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(CancelledBy cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getRatingComment() {
        return ratingComment;
    }

    public void setRatingComment(String ratingComment) {
        this.ratingComment = ratingComment;
    }

    @Override
    public String toString() {
        return "Ride{" +
                "id=" + getId() +
                ", status=" + status +
                ", vehicleType=" + vehicleType +
                ", estimatedFare=" + estimatedFare +
                '}';
    }
}