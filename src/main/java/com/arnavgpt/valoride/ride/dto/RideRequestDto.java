package com.arnavgpt.valoride.ride.dto;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class RideRequestDto {

    @NotNull(message = "Pickup latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal pickupLatitude;

    @NotNull(message = "Pickup longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal pickupLongitude;

    @Size(max = 500, message = "Pickup address too long")
    private String pickupAddress;

    @NotNull(message = "Drop latitude is required")
    @DecimalMin(value = "-90.0", message = "Invalid latitude")
    @DecimalMax(value = "90.0", message = "Invalid latitude")
    private BigDecimal dropLatitude;

    @NotNull(message = "Drop longitude is required")
    @DecimalMin(value = "-180.0", message = "Invalid longitude")
    @DecimalMax(value = "180.0", message = "Invalid longitude")
    private BigDecimal dropLongitude;

    @Size(max = 500, message = "Drop address too long")
    private String dropAddress;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    public RideRequestDto() {
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

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}