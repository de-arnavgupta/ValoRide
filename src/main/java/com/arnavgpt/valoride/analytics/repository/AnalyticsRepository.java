package com.arnavgpt.valoride.analytics.repository;

import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AnalyticsRepository extends JpaRepository<Ride, UUID> {

    // Count rides by status
    long countByStatus(RideStatus status);

    // Count active rides (not completed or cancelled)
    @Query("SELECT COUNT(r) FROM Ride r WHERE r.status NOT IN ('COMPLETED', 'CANCELLED')")
    long countActiveRides();

    // Total revenue (sum of final fare for completed rides)
    @Query("SELECT COALESCE(SUM(r.finalFare), 0) FROM Ride r WHERE r.status = 'COMPLETED'")
    BigDecimal getTotalRevenue();

    // Today's revenue
    @Query("SELECT COALESCE(SUM(r.finalFare), 0) FROM Ride r WHERE r.status = 'COMPLETED' AND r.completedAt >= :startOfDay")
    BigDecimal getTodayRevenue(@Param("startOfDay") LocalDateTime startOfDay);

    // Average fare for completed rides
    @Query("SELECT COALESCE(AVG(r.finalFare), 0) FROM Ride r WHERE r.status = 'COMPLETED'")
    BigDecimal getAverageFare();

    // Average rating across all rated rides
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Ride r WHERE r.rating IS NOT NULL")
    BigDecimal getAverageRating();

    // Revenue in date range
    @Query("SELECT COALESCE(SUM(r.finalFare), 0) FROM Ride r WHERE r.status = 'COMPLETED' " +
            "AND r.completedAt BETWEEN :startDate AND :endDate")
    BigDecimal getRevenueInDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    // Rides in date range
    @Query("SELECT r FROM Ride r WHERE r.status = 'COMPLETED' " +
            "AND r.completedAt BETWEEN :startDate AND :endDate ORDER BY r.completedAt")
    List<Ride> getCompletedRidesInDateRange(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

    // Count completed rides in date range
    @Query("SELECT COUNT(r) FROM Ride r WHERE r.status = 'COMPLETED' " +
            "AND r.completedAt BETWEEN :startDate AND :endDate")
    long countCompletedRidesInDateRange(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    // Revenue by vehicle type
    @Query("SELECT r.vehicleType, COALESCE(SUM(r.finalFare), 0), COUNT(r) FROM Ride r " +
            "WHERE r.status = 'COMPLETED' GROUP BY r.vehicleType")
    List<Object[]> getRevenueByVehicleType();

    // Revenue by vehicle type in date range
    @Query("SELECT r.vehicleType, COALESCE(SUM(r.finalFare), 0), COUNT(r) FROM Ride r " +
            "WHERE r.status = 'COMPLETED' AND r.completedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY r.vehicleType")
    List<Object[]> getRevenueByVehicleTypeInDateRange(@Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    // Daily revenue breakdown
    @Query("SELECT FUNCTION('DATE', r.completedAt), COALESCE(SUM(r.finalFare), 0), COUNT(r) FROM Ride r " +
            "WHERE r.status = 'COMPLETED' AND r.completedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', r.completedAt) ORDER BY FUNCTION('DATE', r.completedAt)")
    List<Object[]> getDailyRevenueInDateRange(@Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);
}