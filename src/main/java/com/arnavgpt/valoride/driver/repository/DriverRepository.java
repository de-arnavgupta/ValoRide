package com.arnavgpt.valoride.driver.repository;

import com.arnavgpt.valoride.driver.entity.ApprovalStatus;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.entity.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DriverRepository extends JpaRepository<Driver, UUID> {

    Optional<Driver> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByLicenseNumber(String licenseNumber);

    boolean existsByVehicleNumber(String vehicleNumber);

    Page<Driver> findByApprovalStatus(ApprovalStatus status, Pageable pageable);

    List<Driver> findByAvailableTrueAndApprovalStatus(ApprovalStatus status);

    /**
     * Find nearby available drivers using Haversine formula
     * Distance is calculated in kilometers
     */
    @Query(value = """
            SELECT d.* FROM drivers d
            JOIN users u ON d.user_id = u.id
            WHERE d.is_available = true
            AND d.approval_status = 'APPROVED'
            AND u.is_active = true
            AND d.current_latitude IS NOT NULL
            AND d.current_longitude IS NOT NULL
            AND (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(d.current_latitude)) *
                    cos(radians(d.current_longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(d.current_latitude))
                )
            ) <= :radiusKm
            ORDER BY (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(d.current_latitude)) *
                    cos(radians(d.current_longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(d.current_latitude))
                )
            ) ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Driver> findNearbyAvailableDrivers(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") double radiusKm,
            @Param("limit") int limit
    );

    /**
     * Find nearby available drivers by vehicle type
     */
    @Query(value = """
            SELECT d.* FROM drivers d
            JOIN users u ON d.user_id = u.id
            WHERE d.is_available = true
            AND d.approval_status = 'APPROVED'
            AND d.vehicle_type = :vehicleType
            AND u.is_active = true
            AND d.current_latitude IS NOT NULL
            AND d.current_longitude IS NOT NULL
            AND (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(d.current_latitude)) *
                    cos(radians(d.current_longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(d.current_latitude))
                )
            ) <= :radiusKm
            ORDER BY (
                6371 * acos(
                    cos(radians(:latitude)) * cos(radians(d.current_latitude)) *
                    cos(radians(d.current_longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(d.current_latitude))
                )
            ) ASC
            LIMIT :limit
            """, nativeQuery = true)
    List<Driver> findNearbyAvailableDriversByVehicleType(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") double radiusKm,
            @Param("vehicleType") String vehicleType,
            @Param("limit") int limit
    );

    @Query("SELECT d FROM Driver d WHERE d.user.id = :userId AND d.approvalStatus = :status")
    Optional<Driver> findByUserIdAndApprovalStatus(
            @Param("userId") UUID userId,
            @Param("status") ApprovalStatus status
    );

    @Query("SELECT COUNT(d) FROM Driver d WHERE d.approvalStatus = :status")
    long countByApprovalStatus(@Param("status") ApprovalStatus status);

    @Query("SELECT COUNT(d) FROM Driver d WHERE d.available = true AND d.approvalStatus = 'APPROVED'")
    long countAvailableDrivers();
}