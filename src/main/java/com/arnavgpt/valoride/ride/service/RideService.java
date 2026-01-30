package com.arnavgpt.valoride.ride.service;

import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.repository.DriverRepository;
import com.arnavgpt.valoride.driver.service.DriverService;
import com.arnavgpt.valoride.exception.BusinessException;
import com.arnavgpt.valoride.exception.ForbiddenException;
import com.arnavgpt.valoride.exception.InvalidRideStateException;
import com.arnavgpt.valoride.exception.ResourceNotFoundException;
import com.arnavgpt.valoride.notification.service.NotificationService;
import com.arnavgpt.valoride.ride.dto.CancelRideRequest;
import com.arnavgpt.valoride.ride.dto.RateRideRequest;
import com.arnavgpt.valoride.ride.dto.RideRequestDto;
import com.arnavgpt.valoride.ride.dto.RideResponse;
import com.arnavgpt.valoride.ride.entity.CancelledBy;
import com.arnavgpt.valoride.ride.entity.Ride;
import com.arnavgpt.valoride.ride.entity.RideStatus;
import com.arnavgpt.valoride.ride.repository.RideRepository;
import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.entity.User;
import com.arnavgpt.valoride.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RideService {

    private static final Logger logger = LoggerFactory.getLogger(RideService.class);

    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final UserService userService;
    private final DriverService driverService;
    private final FareService fareService;
    private final NotificationService notificationService;

    public RideService(RideRepository rideRepository, DriverRepository driverRepository,
                       UserService userService, DriverService driverService, FareService fareService,
                       NotificationService notificationService) {
        this.rideRepository = rideRepository;
        this.driverRepository = driverRepository;
        this.userService = userService;
        this.driverService = driverService;
        this.fareService = fareService;
        this.notificationService = notificationService;
    }

    /**
     * Request a new ride (Rider)
     */
    @Transactional
    public RideResponse requestRide(UUID riderId, RideRequestDto request) {
        User rider = userService.findById(riderId);

        // Verify user is a RIDER
        if (rider.getRole() != Role.RIDER) {
            throw new ForbiddenException("Only riders can request rides");
        }

        // Check if rider already has an active ride
        if (rideRepository.hasActiveRide(riderId)) {
            throw new BusinessException("You already have an active ride", HttpStatus.CONFLICT);
        }

        // Calculate distance and fare
        BigDecimal distanceKm = fareService.calculateDistance(
                request.getPickupLatitude().doubleValue(),
                request.getPickupLongitude().doubleValue(),
                request.getDropLatitude().doubleValue(),
                request.getDropLongitude().doubleValue()
        );

        BigDecimal estimatedFare = fareService.calculateFare(request.getVehicleType(), distanceKm);
        int estimatedMins = fareService.calculateEstimatedDuration(distanceKm);

        // Create ride
        Ride ride = new Ride();
        ride.setRider(rider);
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setVehicleType(request.getVehicleType());
        ride.setDistanceKm(distanceKm);
        ride.setEstimatedFare(estimatedFare);
        ride.setEstimatedDurationMins(estimatedMins);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride requested: {} by rider: {}", savedRide.getId(), riderId);

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Accept a ride (Driver)
     */
    @Transactional
    public RideResponse acceptRide(UUID driverUserId, UUID rideId) {
        Driver driver = driverService.findByUserId(driverUserId);

        // Verify driver is approved and available
        if (!driver.isApproved()) {
            throw new ForbiddenException("Driver account is not approved");
        }
        if (!driver.isAvailable()) {
            throw new BusinessException("You must be online to accept rides", HttpStatus.BAD_REQUEST);
        }

        // Check if driver already has an active ride
        if (rideRepository.driverHasActiveRide(driver.getId())) {
            throw new BusinessException("You already have an active ride", HttpStatus.CONFLICT);
        }

        Ride ride = findById(rideId);

        // Verify ride can be accepted
        if (!ride.canBeAccepted()) {
            throw new InvalidRideStateException(ride.getStatus().name(), "accept");
        }

        // Verify vehicle type matches
        if (ride.getVehicleType() != driver.getVehicleType()) {
            throw new BusinessException("Your vehicle type doesn't match the ride request", HttpStatus.BAD_REQUEST);
        }

        // Assign driver
        ride.setDriver(driver);
        ride.setStatus(RideStatus.MATCHED);
        ride.setMatchedAt(LocalDateTime.now());

        // Set driver as unavailable
        driver.setAvailable(false);
        driverRepository.save(driver);

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride {} accepted by driver {}", rideId, driver.getId());

        // Send notification to rider
        notificationService.sendRideAcceptedNotification(savedRide);

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Driver arrived at pickup location
     */
    @Transactional
    public RideResponse arrivedAtPickup(UUID driverUserId, UUID rideId) {
        Driver driver = driverService.findByUserId(driverUserId);
        Ride ride = findById(rideId);

        // Verify this is the assigned driver
        verifyRideDriver(ride, driver);

        if (!ride.canArriveAtPickup()) {
            throw new InvalidRideStateException(ride.getStatus().name(), "arrive");
        }

        ride.setStatus(RideStatus.ARRIVED);
        ride.setArrivedAt(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        logger.info("Driver arrived for ride {}", rideId);

        // Send notification to rider
        notificationService.sendDriverArrivedNotification(savedRide);

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Start the ride (Driver)
     */
    @Transactional
    public RideResponse startRide(UUID driverUserId, UUID rideId) {
        Driver driver = driverService.findByUserId(driverUserId);
        Ride ride = findById(rideId);

        verifyRideDriver(ride, driver);

        if (!ride.canStart()) {
            throw new InvalidRideStateException(ride.getStatus().name(), "start");
        }

        ride.setStatus(RideStatus.STARTED);
        ride.setStartedAt(LocalDateTime.now());

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride {} started", rideId);

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Complete the ride (Driver)
     */
    @Transactional
    public RideResponse completeRide(UUID driverUserId, UUID rideId) {
        Driver driver = driverService.findByUserId(driverUserId);
        Ride ride = findById(rideId);

        verifyRideDriver(ride, driver);

        if (!ride.canComplete()) {
            throw new InvalidRideStateException(ride.getStatus().name(), "complete");
        }

        // Calculate final fare (same as estimated for MVP)
        ride.setFinalFare(ride.getEstimatedFare());
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());

        // Update driver stats
        driver.incrementTotalRides();
        driver.addEarnings(ride.getFinalFare());
        driver.setAvailable(true); // Driver is available again
        driverRepository.save(driver);

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride {} completed. Fare: {}", rideId, ride.getFinalFare());

        // Send notification to both rider and driver
        notificationService.sendRideCompletedNotification(savedRide);

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Cancel a ride (Rider or Driver)
     */
    @Transactional
    public RideResponse cancelRide(UUID userId, UUID rideId, CancelRideRequest request, Role role) {
        Ride ride = findById(rideId);

        if (!ride.canBeCancelled()) {
            throw new InvalidRideStateException(ride.getStatus().name(), "cancel");
        }

        // Verify the user is part of this ride
        CancelledBy cancelledBy;
        if (role == Role.RIDER) {
            if (!ride.getRider().getId().equals(userId)) {
                throw new ForbiddenException("You can only cancel your own rides");
            }
            cancelledBy = CancelledBy.RIDER;
        } else if (role == Role.DRIVER) {
            Driver driver = driverService.findByUserId(userId);
            if (ride.getDriver() == null || !ride.getDriver().getId().equals(driver.getId())) {
                throw new ForbiddenException("You can only cancel rides assigned to you");
            }
            cancelledBy = CancelledBy.DRIVER;

            // Make driver available again
            driver.setAvailable(true);
            driverRepository.save(driver);
        } else {
            throw new ForbiddenException("Invalid role for cancellation");
        }

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancelledAt(LocalDateTime.now());
        ride.setCancelledBy(cancelledBy);
        ride.setCancelReason(request != null ? request.getReason() : null);

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride {} cancelled by {}", rideId, cancelledBy);

        // Send cancellation notification
        notificationService.sendRideCancelledNotification(savedRide, cancelledBy.name());

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Rate a completed ride (Rider)
     */
    @Transactional
    public RideResponse rateRide(UUID riderId, UUID rideId, RateRideRequest request) {
        Ride ride = findById(rideId);

        // Verify rider owns this ride
        if (!ride.getRider().getId().equals(riderId)) {
            throw new ForbiddenException("You can only rate your own rides");
        }

        if (!ride.canBeRated()) {
            throw new BusinessException("This ride cannot be rated", HttpStatus.BAD_REQUEST);
        }

        ride.setRating(request.getRating());
        ride.setRatingComment(request.getComment());

        // Update driver's average rating
        if (ride.getDriver() != null) {
            updateDriverRating(ride.getDriver(), request.getRating());
        }

        Ride savedRide = rideRepository.save(ride);
        logger.info("Ride {} rated: {}", rideId, request.getRating());

        return RideResponse.fromEntity(savedRide);
    }

    /**
     * Get ride by ID
     */
    public RideResponse getRideById(UUID rideId, UUID userId) {
        Ride ride = findById(rideId);

        // Verify user has access to this ride
        boolean isRider = ride.getRider().getId().equals(userId);
        boolean isDriver = ride.getDriver() != null &&
                ride.getDriver().getUser().getId().equals(userId);

        if (!isRider && !isDriver) {
            throw new ForbiddenException("You don't have access to this ride");
        }

        return RideResponse.fromEntity(ride);
    }

    /**
     * Get active ride for user
     */
    public RideResponse getActiveRide(UUID userId, Role role) {
        Ride ride;

        if (role == Role.RIDER) {
            ride = rideRepository.findActiveRideByRiderId(userId).orElse(null);
        } else if (role == Role.DRIVER) {
            Driver driver = driverService.findByUserId(userId);
            ride = rideRepository.findActiveRideByDriverId(driver.getId()).orElse(null);
        } else {
            return null;
        }

        return ride != null ? RideResponse.fromEntity(ride) : null;
    }

    /**
     * Get ride history for rider
     */
    public Page<RideResponse> getRiderHistory(UUID riderId, Pageable pageable) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId, pageable)
                .map(RideResponse::fromEntity);
    }

    /**
     * Get ride history for driver
     */
    public Page<RideResponse> getDriverHistory(UUID driverUserId, Pageable pageable) {
        Driver driver = driverService.findByUserId(driverUserId);
        return rideRepository.findByDriverIdOrderByCreatedAtDesc(driver.getId(), pageable)
                .map(RideResponse::fromEntity);
    }

    public Ride findById(UUID rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride", "id", rideId));
    }

    private void verifyRideDriver(Ride ride, Driver driver) {
        if (ride.getDriver() == null || !ride.getDriver().getId().equals(driver.getId())) {
            throw new ForbiddenException("This ride is not assigned to you");
        }
    }

    private void updateDriverRating(Driver driver, BigDecimal newRating) {
        long totalRides = rideRepository.countCompletedRidesByDriverId(driver.getId());

        if (totalRides <= 1) {
            driver.setRating(newRating);
        } else {
            // Calculate new average: ((oldAvg * (n-1)) + newRating) / n
            BigDecimal currentAvg = driver.getRating() != null ? driver.getRating() : BigDecimal.ZERO;
            BigDecimal totalPreviousRatings = currentAvg.multiply(BigDecimal.valueOf(totalRides - 1));
            BigDecimal newAverage = totalPreviousRatings.add(newRating)
                    .divide(BigDecimal.valueOf(totalRides), 2, RoundingMode.HALF_UP);
            driver.setRating(newAverage);
        }

        driverRepository.save(driver);
    }
}