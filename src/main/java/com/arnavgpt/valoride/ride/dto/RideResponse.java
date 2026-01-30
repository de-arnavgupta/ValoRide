package com.arnavgpt.valoride.ride.dto;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.arnavgpt.valoride.ride.entity.CancelledBy;
import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RideResponse {

    private UUID id;
    private RideStatus status;
    private VehicleType vehicleType;

    // Rider info
    private UUID riderId;
    private String riderName;
    private String riderPhone;

    // Driver info (if assigned)
    private UUID driverId;
    private String driverName;
    private String driverPhone;
    private String vehicleNumber;
    private BigDecimal driverRating;
    private BigDecimal driverLatitude;
    private BigDecimal driverLongitude;

    // Locations
    private BigDecimal pickupLatitude;
    private BigDecimal pickupLongitude;
    private String pickupAddress;
    private BigDecimal dropLatitude;
    private BigDecimal dropLongitude;
    private String dropAddress;

    // Fare
    private BigDecimal estimatedFare;
    private BigDecimal finalFare;
    private BigDecimal distanceKm;
    private Integer estimatedDurationMins;

    // Timestamps
    private LocalDateTime requestedAt;
    private LocalDateTime matchedAt;
    private LocalDateTime arrivedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // Cancellation
    private CancelledBy cancelledBy;
    private String cancelReason;

    // Rating
    private BigDecimal rating;
    private String ratingComment;

    private LocalDateTime createdAt;

    public RideResponse() {
    }

    public static RideResponse fromEntity(Ride ride) {
        RideResponse response = new RideResponse();
        response.setId(ride.getId());
        response.setStatus(ride.getStatus());
        response.setVehicleType(ride.getVehicleType());

        // Rider info
        response.setRiderId(ride.getRider().getId());
        response.setRiderName(ride.getRider().getName());
        response.setRiderPhone(ride.getRider().getPhone());

        // Driver info
        if (ride.getDriver() != null) {
            response.setDriverId(ride.getDriver().getId());
            response.setDriverName(ride.getDriver().getUser().getName());
            response.setDriverPhone(ride.getDriver().getUser().getPhone());
            response.setVehicleNumber(ride.getDriver().getVehicleNumber());
            response.setDriverRating(ride.getDriver().getRating());
            response.setDriverLatitude(ride.getDriver().getCurrentLatitude());
            response.setDriverLongitude(ride.getDriver().getCurrentLongitude());
        }

        // Locations
        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setPickupAddress(ride.getPickupAddress());
        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setDropAddress(ride.getDropAddress());

        // Fare
        response.setEstimatedFare(ride.getEstimatedFare());
        response.setFinalFare(ride.getFinalFare());
        response.setDistanceKm(ride.getDistanceKm());
        response.setEstimatedDurationMins(ride.getEstimatedDurationMins());

        // Timestamps
        response.setRequestedAt(ride.getRequestedAt());
        response.setMatchedAt(ride.getMatchedAt());
        response.setArrivedAt(ride.getArrivedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());
        response.setCancelledAt(ride.getCancelledAt());

        // Cancellation
        response.setCancelledBy(ride.getCancelledBy());
        response.setCancelReason(ride.getCancelReason());

        // Rating
        response.setRating(ride.getRating());
        response.setRatingComment(ride.getRatingComment());

        response.setCreatedAt(ride.getCreatedAt());

        return response;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public UUID getRiderId() {
        return riderId;
    }

    public void setRiderId(UUID riderId) {
        this.riderId = riderId;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public String getRiderPhone() {
        return riderPhone;
    }

    public void setRiderPhone(String riderPhone) {
        this.riderPhone = riderPhone;
    }

    public UUID getDriverId() {
        return driverId;
    }

    public void setDriverId(UUID driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public BigDecimal getDriverRating() {
        return driverRating;
    }

    public void setDriverRating(BigDecimal driverRating) {
        this.driverRating = driverRating;
    }

    public BigDecimal getDriverLatitude() {
        return driverLatitude;
    }

    public void setDriverLatitude(BigDecimal driverLatitude) {
        this.driverLatitude = driverLatitude;
    }

    public BigDecimal getDriverLongitude() {
        return driverLongitude;
    }

    public void setDriverLongitude(BigDecimal driverLongitude) {
        this.driverLongitude = driverLongitude;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}