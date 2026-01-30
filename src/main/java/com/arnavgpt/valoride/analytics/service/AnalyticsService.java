package com.arnavgpt.valoride.analytics.service;

import com.arnavgpt.valoride.analytics.dto.DashboardStatsResponse;
import com.arnavgpt.valoride.analytics.dto.DriverPerformanceResponse;
import com.arnavgpt.valoride.analytics.dto.RevenueReportResponse;
import com.arnavgpt.valoride.analytics.repository.AnalyticsRepository;
import com.arnavgpt.valoride.driver.entity.ApprovalStatus;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.repository.DriverRepository;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import com.arnavgpt.valoride.ride.repository.RideRepository;
import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);

    private final AnalyticsRepository analyticsRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final RideRepository rideRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository,
                            UserRepository userRepository,
                            DriverRepository driverRepository,
                            RideRepository rideRepository) {
        this.analyticsRepository = analyticsRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.rideRepository = rideRepository;
    }

    /**
     * Get dashboard statistics
     */
    public DashboardStatsResponse getDashboardStats() {
        logger.info("Generating dashboard stats");

        // User stats
        long totalUsers = userRepository.count();
        long totalRiders = userRepository.findByRole(Role.RIDER, PageRequest.of(0, 1)).getTotalElements();
        long totalDrivers = userRepository.findByRole(Role.DRIVER, PageRequest.of(0, 1)).getTotalElements();
        long activeDrivers = driverRepository.countAvailableDrivers();

        // Driver approval stats
        long pendingApprovals = driverRepository.countByApprovalStatus(ApprovalStatus.PENDING);
        long approvedDrivers = driverRepository.countByApprovalStatus(ApprovalStatus.APPROVED);
        long rejectedDrivers = driverRepository.countByApprovalStatus(ApprovalStatus.REJECTED);

        // Ride stats
        long totalRides = rideRepository.count();
        long completedRides = analyticsRepository.countByStatus(RideStatus.COMPLETED);
        long cancelledRides = analyticsRepository.countByStatus(RideStatus.CANCELLED);
        long activeRides = analyticsRepository.countActiveRides();

        // Revenue stats
        BigDecimal totalRevenue = analyticsRepository.getTotalRevenue();
        LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        BigDecimal todayRevenue = analyticsRepository.getTodayRevenue(startOfDay);
        BigDecimal averageFare = analyticsRepository.getAverageFare();

        // Performance stats
        BigDecimal averageRating = analyticsRepository.getAverageRating();
        double completionRate = totalRides > 0 ? (double) completedRides / totalRides * 100 : 0;
        double cancellationRate = totalRides > 0 ? (double) cancelledRides / totalRides * 100 : 0;

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalRiders(totalRiders)
                .totalDrivers(totalDrivers)
                .activeDrivers(activeDrivers)
                .pendingDriverApprovals(pendingApprovals)
                .approvedDrivers(approvedDrivers)
                .rejectedDrivers(rejectedDrivers)
                .totalRides(totalRides)
                .completedRides(completedRides)
                .cancelledRides(cancelledRides)
                .activeRides(activeRides)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .todayRevenue(todayRevenue != null ? todayRevenue : BigDecimal.ZERO)
                .averageFare(averageFare != null ? averageFare.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .averageRating(averageRating != null ? averageRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .cancellationRate(Math.round(cancellationRate * 100.0) / 100.0)
                .build();
    }

    /**
     * Get revenue report for date range
     */
    public RevenueReportResponse getRevenueReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating revenue report from {} to {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        RevenueReportResponse response = new RevenueReportResponse();
        response.setStartDate(startDate);
        response.setEndDate(endDate);

        // Total revenue in range
        BigDecimal totalRevenue = analyticsRepository.getRevenueInDateRange(startDateTime, endDateTime);
        response.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);

        // Total rides in range
        long totalRides = analyticsRepository.countCompletedRidesInDateRange(startDateTime, endDateTime);
        response.setTotalRides(totalRides);

        // Average fare
        if (totalRides > 0 && totalRevenue != null) {
            response.setAverageFare(totalRevenue.divide(BigDecimal.valueOf(totalRides), 2, RoundingMode.HALF_UP));
        } else {
            response.setAverageFare(BigDecimal.ZERO);
        }

        // Daily breakdown
        List<Object[]> dailyData = analyticsRepository.getDailyRevenueInDateRange(startDateTime, endDateTime);
        List<RevenueReportResponse.DailyRevenue> dailyBreakdown = new ArrayList<>();
        for (Object[] row : dailyData) {
            LocalDate date = row[0] instanceof java.sql.Date ?
                    ((java.sql.Date) row[0]).toLocalDate() : (LocalDate) row[0];
            BigDecimal revenue = row[1] instanceof BigDecimal ?
                    (BigDecimal) row[1] : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            long rides = ((Number) row[2]).longValue();
            dailyBreakdown.add(new RevenueReportResponse.DailyRevenue(date, revenue, rides));
        }
        response.setDailyBreakdown(dailyBreakdown);

        // Vehicle type breakdown
        List<Object[]> vehicleData = analyticsRepository.getRevenueByVehicleTypeInDateRange(startDateTime, endDateTime);
        List<RevenueReportResponse.VehicleTypeRevenue> vehicleBreakdown = new ArrayList<>();
        for (Object[] row : vehicleData) {
            String vehicleType = row[0].toString();
            BigDecimal revenue = row[1] instanceof BigDecimal ?
                    (BigDecimal) row[1] : BigDecimal.valueOf(((Number) row[1]).doubleValue());
            long rides = ((Number) row[2]).longValue();
            BigDecimal avgFare = rides > 0 ?
                    revenue.divide(BigDecimal.valueOf(rides), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            vehicleBreakdown.add(new RevenueReportResponse.VehicleTypeRevenue(vehicleType, revenue, rides, avgFare));
        }
        response.setVehicleTypeBreakdown(vehicleBreakdown);

        return response;
    }

    /**
     * Get driver performance metrics
     */
    public DriverPerformanceResponse getDriverPerformance(int limit) {
        logger.info("Getting top {} driver performance", limit);

        DriverPerformanceResponse response = new DriverPerformanceResponse();

        // Get approved drivers sorted by rating and total rides
        List<Driver> drivers = driverRepository.findByApprovalStatus(
                ApprovalStatus.APPROVED,
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "rating", "totalRides"))
        ).getContent();

        List<DriverPerformanceResponse.DriverStats> topDrivers = drivers.stream()
                .map(this::mapToDriverStats)
                .collect(Collectors.toList());

        response.setTopDrivers(topDrivers);
        response.setTotalActiveDrivers(driverRepository.countAvailableDrivers());

        BigDecimal avgRating = analyticsRepository.getAverageRating();
        response.setPlatformAverageRating(avgRating != null ? avgRating.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);

        // Calculate platform completion rate
        long totalRides = rideRepository.count();
        long completedRides = analyticsRepository.countByStatus(RideStatus.COMPLETED);
        double completionRate = totalRides > 0 ? (double) completedRides / totalRides * 100 : 0;
        response.setPlatformCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        return response;
    }

    private DriverPerformanceResponse.DriverStats mapToDriverStats(Driver driver) {
        DriverPerformanceResponse.DriverStats stats = new DriverPerformanceResponse.DriverStats();
        stats.setDriverId(driver.getId());
        stats.setDriverName(driver.getUser().getName());
        stats.setVehicleType(driver.getVehicleType().name());
        stats.setTotalRides(driver.getTotalRides());
        stats.setTotalEarnings(driver.getTotalEarnings() != null ? driver.getTotalEarnings() : BigDecimal.ZERO);
        stats.setRating(driver.getRating() != null ? driver.getRating() : BigDecimal.ZERO);

        // Get completed and cancelled rides for this driver
        long completedRides = rideRepository.countCompletedRidesByDriverId(driver.getId());
        stats.setCompletedRides((int) completedRides);

        // For simplicity, cancelled = total - completed (this is approximate)
        int totalAssigned = driver.getTotalRides();
        stats.setCancelledRides(Math.max(0, totalAssigned - (int) completedRides));

        double completionRate = totalAssigned > 0 ? (double) completedRides / totalAssigned * 100 : 0;
        stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        return stats;
    }
}