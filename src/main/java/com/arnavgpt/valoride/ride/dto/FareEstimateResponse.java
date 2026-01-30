package com.arnavgpt.valoride.ride.dto;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FareEstimateResponse {

    private BigDecimal distanceKm;
    private Integer estimatedDurationMins;
    private List<VehicleFare> fares;

    public FareEstimateResponse() {
    }

    public FareEstimateResponse(BigDecimal distanceKm, Integer estimatedDurationMins, List<VehicleFare> fares) {
        this.distanceKm = distanceKm;
        this.estimatedDurationMins = estimatedDurationMins;
        this.fares = fares;
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

    public List<VehicleFare> getFares() {
        return fares;
    }

    public void setFares(List<VehicleFare> fares) {
        this.fares = fares;
    }

    public static class VehicleFare {

        private VehicleType vehicleType;
        private BigDecimal estimatedFare;
        private BigDecimal baseFare;
        private BigDecimal distanceCharge;

        public VehicleFare() {
        }

        public VehicleFare(VehicleType vehicleType, BigDecimal estimatedFare,
                           BigDecimal baseFare, BigDecimal distanceCharge) {
            this.vehicleType = vehicleType;
            this.estimatedFare = estimatedFare;
            this.baseFare = baseFare;
            this.distanceCharge = distanceCharge;
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

        public BigDecimal getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(BigDecimal baseFare) {
            this.baseFare = baseFare;
        }

        public BigDecimal getDistanceCharge() {
            return distanceCharge;
        }

        public void setDistanceCharge(BigDecimal distanceCharge) {
            this.distanceCharge = distanceCharge;
        }
    }
}