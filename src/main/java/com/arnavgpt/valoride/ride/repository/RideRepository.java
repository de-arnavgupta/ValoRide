package com.arnavgpt.valoride.ride.repository;

import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RideRepository extends JpaRepository<Ride, UUID> {

    // Find rides by rider
    Page<Ride> findByRiderIdOrderByCreatedAtDesc(UUID riderId, Pageable pageable);

    // Find rides by driver
    Page<Ride> findByDriverIdOrderByCreatedAtDesc(UUID driverId, Pageable pageable);

    // Find active ride for rider (not completed/cancelled)
    @Query("SELECT r FROM Ride r WHERE r.rider.id = :riderId AND r.status NOT IN ('COMPLETED', 'CANCELLED')")
    Optional<Ride> findActiveRideByRiderId(@Param("riderId") UUID riderId);

    // Find active ride for driver
    @Query("SELECT r FROM Ride r WHERE r.driver.id = :driverId AND r.status NOT IN ('COMPLETED', 'CANCELLED')")
    Optional<Ride> findActiveRideByDriverId(@Param("driverId") UUID driverId);

    // Find rides by status
    List<Ride> findByStatus(RideStatus status);

    // Find rides by rider and status
    Page<Ride> findByRiderIdAndStatusIn(UUID riderId, List<RideStatus> statuses, Pageable pageable);

    // Find rides by driver and status
    Page<Ride> findByDriverIdAndStatusIn(UUID driverId, List<RideStatus> statuses, Pageable pageable);

    // Count completed rides by driver
    @Query("SELECT COUNT(r) FROM Ride r WHERE r.driver.id = :driverId AND r.status = 'COMPLETED'")
    long countCompletedRidesByDriverId(@Param("driverId") UUID driverId);

    // Count rides by status
    long countByStatus(RideStatus status);

    // Analytics: rides in date range
    @Query("SELECT r FROM Ride r WHERE r.createdAt BETWEEN :startDate AND :endDate")
    List<Ride> findRidesInDateRange(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    // Analytics: completed rides in date range
    @Query("SELECT r FROM Ride r WHERE r.status = 'COMPLETED' AND r.completedAt BETWEEN :startDate AND :endDate")
    List<Ride> findCompletedRidesInDateRange(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

    // Check if rider has active ride
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ride r " +
            "WHERE r.rider.id = :riderId AND r.status NOT IN ('COMPLETED', 'CANCELLED')")
    boolean hasActiveRide(@Param("riderId") UUID riderId);

    // Check if driver has active ride
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Ride r " +
            "WHERE r.driver.id = :driverId AND r.status NOT IN ('COMPLETED', 'CANCELLED')")
    boolean driverHasActiveRide(@Param("driverId") UUID driverId);
}