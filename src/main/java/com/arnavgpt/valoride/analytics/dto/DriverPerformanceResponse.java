package com.arnavgpt.valoride.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DriverPerformanceResponse {

    private List<DriverStats> topDrivers;
    private long totalActiveDrivers;
    private BigDecimal platformAverageRating;
    private double platformCompletionRate;

    public DriverPerformanceResponse() {
    }

    public List<DriverStats> getTopDrivers() {
        return topDrivers;
    }

    public void setTopDrivers(List<DriverStats> topDrivers) {
        this.topDrivers = topDrivers;
    }

    public long getTotalActiveDrivers() {
        return totalActiveDrivers;
    }

    public void setTotalActiveDrivers(long totalActiveDrivers) {
        this.totalActiveDrivers = totalActiveDrivers;
    }

    public BigDecimal getPlatformAverageRating() {
        return platformAverageRating;
    }

    public void setPlatformAverageRating(BigDecimal platformAverageRating) {
        this.platformAverageRating = platformAverageRating;
    }

    public double getPlatformCompletionRate() {
        return platformCompletionRate;
    }

    public void setPlatformCompletionRate(double platformCompletionRate) {
        this.platformCompletionRate = platformCompletionRate;
    }

    public static class DriverStats {
        private UUID driverId;
        private String driverName;
        private String vehicleType;
        private int totalRides;
        private BigDecimal totalEarnings;
        private BigDecimal rating;
        private int completedRides;
        private int cancelledRides;
        private double completionRate;

        public DriverStats() {
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

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
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

        public BigDecimal getRating() {
            return rating;
        }

        public void setRating(BigDecimal rating) {
            this.rating = rating;
        }

        public int getCompletedRides() {
            return completedRides;
        }

        public void setCompletedRides(int completedRides) {
            this.completedRides = completedRides;
        }

        public int getCancelledRides() {
            return cancelledRides;
        }

        public void setCancelledRides(int cancelledRides) {
            this.cancelledRides = cancelledRides;
        }

        public double getCompletionRate() {
            return completionRate;
        }

        public void setCompletionRate(double completionRate) {
            this.completionRate = completionRate;
        }
    }
}