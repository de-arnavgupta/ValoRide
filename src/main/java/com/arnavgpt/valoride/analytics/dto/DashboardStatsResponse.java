package com.arnavgpt.valoride.analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStatsResponse {

    // User stats
    private long totalUsers;
    private long totalRiders;
    private long totalDrivers;
    private long activeDrivers;

    // Driver approval stats
    private long pendingDriverApprovals;
    private long approvedDrivers;
    private long rejectedDrivers;

    // Ride stats
    private long totalRides;
    private long completedRides;
    private long cancelledRides;
    private long activeRides;

    // Revenue stats
    private BigDecimal totalRevenue;
    private BigDecimal todayRevenue;
    private BigDecimal averageFare;

    // Performance
    private BigDecimal averageRating;
    private double completionRate;
    private double cancellationRate;

    public DashboardStatsResponse() {
    }

    // Builder pattern for easy construction
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final DashboardStatsResponse stats = new DashboardStatsResponse();

        public Builder totalUsers(long val) { stats.totalUsers = val; return this; }
        public Builder totalRiders(long val) { stats.totalRiders = val; return this; }
        public Builder totalDrivers(long val) { stats.totalDrivers = val; return this; }
        public Builder activeDrivers(long val) { stats.activeDrivers = val; return this; }
        public Builder pendingDriverApprovals(long val) { stats.pendingDriverApprovals = val; return this; }
        public Builder approvedDrivers(long val) { stats.approvedDrivers = val; return this; }
        public Builder rejectedDrivers(long val) { stats.rejectedDrivers = val; return this; }
        public Builder totalRides(long val) { stats.totalRides = val; return this; }
        public Builder completedRides(long val) { stats.completedRides = val; return this; }
        public Builder cancelledRides(long val) { stats.cancelledRides = val; return this; }
        public Builder activeRides(long val) { stats.activeRides = val; return this; }
        public Builder totalRevenue(BigDecimal val) { stats.totalRevenue = val; return this; }
        public Builder todayRevenue(BigDecimal val) { stats.todayRevenue = val; return this; }
        public Builder averageFare(BigDecimal val) { stats.averageFare = val; return this; }
        public Builder averageRating(BigDecimal val) { stats.averageRating = val; return this; }
        public Builder completionRate(double val) { stats.completionRate = val; return this; }
        public Builder cancellationRate(double val) { stats.cancellationRate = val; return this; }

        public DashboardStatsResponse build() { return stats; }
    }

    // Getters and Setters
    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalRiders() {
        return totalRiders;
    }

    public void setTotalRiders(long totalRiders) {
        this.totalRiders = totalRiders;
    }

    public long getTotalDrivers() {
        return totalDrivers;
    }

    public void setTotalDrivers(long totalDrivers) {
        this.totalDrivers = totalDrivers;
    }

    public long getActiveDrivers() {
        return activeDrivers;
    }

    public void setActiveDrivers(long activeDrivers) {
        this.activeDrivers = activeDrivers;
    }

    public long getPendingDriverApprovals() {
        return pendingDriverApprovals;
    }

    public void setPendingDriverApprovals(long pendingDriverApprovals) {
        this.pendingDriverApprovals = pendingDriverApprovals;
    }

    public long getApprovedDrivers() {
        return approvedDrivers;
    }

    public void setApprovedDrivers(long approvedDrivers) {
        this.approvedDrivers = approvedDrivers;
    }

    public long getRejectedDrivers() {
        return rejectedDrivers;
    }

    public void setRejectedDrivers(long rejectedDrivers) {
        this.rejectedDrivers = rejectedDrivers;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }

    public long getCompletedRides() {
        return completedRides;
    }

    public void setCompletedRides(long completedRides) {
        this.completedRides = completedRides;
    }

    public long getCancelledRides() {
        return cancelledRides;
    }

    public void setCancelledRides(long cancelledRides) {
        this.cancelledRides = cancelledRides;
    }

    public long getActiveRides() {
        return activeRides;
    }

    public void setActiveRides(long activeRides) {
        this.activeRides = activeRides;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public BigDecimal getAverageFare() {
        return averageFare;
    }

    public void setAverageFare(BigDecimal averageFare) {
        this.averageFare = averageFare;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public double getCompletionRate() {
        return completionRate;
    }

    public void setCompletionRate(double completionRate) {
        this.completionRate = completionRate;
    }

    public double getCancellationRate() {
        return cancellationRate;
    }

    public void setCancellationRate(double cancellationRate) {
        this.cancellationRate = cancellationRate;
    }
}