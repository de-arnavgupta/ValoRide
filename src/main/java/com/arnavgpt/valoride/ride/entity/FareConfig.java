package com.arnavgpt.valoride.ride.entity;

import com.arnavgpt.valoride.common.entity.BaseEntity;
import com.arnavgpt.valoride.driver.entity.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "fare_config")
public class FareConfig extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, unique = true)
    private VehicleType vehicleType;

    @Column(name = "base_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseFare;

    @Column(name = "per_km_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal perKmRate;

    @Column(name = "per_min_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal perMinRate;

    @Column(name = "min_fare", nullable = false, precision = 10, scale = 2)
    private BigDecimal minFare;

    @Column(name = "is_active")
    private boolean active;

    public FareConfig() {
        this.active = true;
    }

    public FareConfig(VehicleType vehicleType, BigDecimal baseFare, BigDecimal perKmRate,
                      BigDecimal perMinRate, BigDecimal minFare) {
        this();
        this.vehicleType = vehicleType;
        this.baseFare = baseFare;
        this.perKmRate = perKmRate;
        this.perMinRate = perMinRate;
        this.minFare = minFare;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(BigDecimal baseFare) {
        this.baseFare = baseFare;
    }

    public BigDecimal getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(BigDecimal perKmRate) {
        this.perKmRate = perKmRate;
    }

    public BigDecimal getPerMinRate() {
        return perMinRate;
    }

    public void setPerMinRate(BigDecimal perMinRate) {
        this.perMinRate = perMinRate;
    }

    public BigDecimal getMinFare() {
        return minFare;
    }

    public void setMinFare(BigDecimal minFare) {
        this.minFare = minFare;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}