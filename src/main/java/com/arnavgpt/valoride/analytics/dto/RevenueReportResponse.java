package com.arnavgpt.valoride.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RevenueReportResponse {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private long totalRides;
    private BigDecimal averageFare;
    private List<DailyRevenue> dailyBreakdown;
    private List<VehicleTypeRevenue> vehicleTypeBreakdown;

    public RevenueReportResponse() {
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public BigDecimal getAverageFare() {
        return averageFare;
    }

    public void setAverageFare(BigDecimal averageFare) {
        this.averageFare = averageFare;
    }

    public List<DailyRevenue> getDailyBreakdown() {
        return dailyBreakdown;
    }

    public void setDailyBreakdown(List<DailyRevenue> dailyBreakdown) {
        this.dailyBreakdown = dailyBreakdown;
    }

    public List<VehicleTypeRevenue> getVehicleTypeBreakdown() {
        return vehicleTypeBreakdown;
    }

    public void setVehicleTypeBreakdown(List<VehicleTypeRevenue> vehicleTypeBreakdown) {
        this.vehicleTypeBreakdown = vehicleTypeBreakdown;
    }

    public static class DailyRevenue {
        private LocalDate date;
        private BigDecimal revenue;
        private long rides;

        public DailyRevenue() {
        }

        public DailyRevenue(LocalDate date, BigDecimal revenue, long rides) {
            this.date = date;
            this.revenue = revenue;
            this.rides = rides;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public long getRides() {
            return rides;
        }

        public void setRides(long rides) {
            this.rides = rides;
        }
    }

    public static class VehicleTypeRevenue {
        private String vehicleType;
        private BigDecimal revenue;
        private long rides;
        private BigDecimal averageFare;

        public VehicleTypeRevenue() {
        }

        public VehicleTypeRevenue(String vehicleType, BigDecimal revenue, long rides, BigDecimal averageFare) {
            this.vehicleType = vehicleType;
            this.revenue = revenue;
            this.rides = rides;
            this.averageFare = averageFare;
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public void setVehicleType(String vehicleType) {
            this.vehicleType = vehicleType;
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        public void setRevenue(BigDecimal revenue) {
            this.revenue = revenue;
        }

        public long getRides() {
            return rides;
        }

        public void setRides(long rides) {
            this.rides = rides;
        }

        public BigDecimal getAverageFare() {
            return averageFare;
        }

        public void setAverageFare(BigDecimal averageFare) {
            this.averageFare = averageFare;
        }
    }
}