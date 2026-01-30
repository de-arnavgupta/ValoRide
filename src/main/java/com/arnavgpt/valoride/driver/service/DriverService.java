package com.arnavgpt.valoride.driver.service;

import com.arnavgpt.valoride.driver.dto.AvailabilityUpdateRequest;
import com.arnavgpt.valoride.driver.dto.DriverRegistrationRequest;
import com.arnavgpt.valoride.driver.dto.DriverResponse;
import com.arnavgpt.valoride.driver.dto.LocationUpdateRequest;
import com.arnavgpt.valoride.driver.dto.NearbyDriversRequest;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.repository.DriverRepository;
import com.arnavgpt.valoride.exception.BusinessException;
import com.arnavgpt.valoride.exception.DuplicateResourceException;
import com.arnavgpt.valoride.exception.ForbiddenException;
import com.arnavgpt.valoride.exception.ResourceNotFoundException;
import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.entity.User;
import com.arnavgpt.valoride.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private static final Logger logger = LoggerFactory.getLogger(DriverService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;

    public DriverService(DriverRepository driverRepository, UserRepository userRepository) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DriverResponse registerDriver(UUID userId, DriverRegistrationRequest request) {
        logger.info("Registering driver for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Verify user has DRIVER role
        if (user.getRole() != Role.DRIVER) {
            throw new ForbiddenException("Only users with DRIVER role can register as drivers");
        }

        // Check if already registered as driver
        if (driverRepository.existsByUserId(userId)) {
            throw new DuplicateResourceException("Driver", "userId", userId);
        }

        // Validate unique license number
        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver", "licenseNumber", request.getLicenseNumber());
        }

        // Validate unique vehicle number
        if (driverRepository.existsByVehicleNumber(request.getVehicleNumber().toUpperCase())) {
            throw new DuplicateResourceException("Driver", "vehicleNumber", request.getVehicleNumber());
        }

        Driver driver = new Driver();
        driver.setUser(user);
        driver.setLicenseNumber(request.getLicenseNumber().toUpperCase().trim());
        driver.setVehicleNumber(request.getVehicleNumber().toUpperCase().trim());
        driver.setVehicleType(request.getVehicleType());

        Driver savedDriver = driverRepository.save(driver);
        logger.info("Driver registered successfully: {}", savedDriver.getId());

        return DriverResponse.fromEntity(savedDriver);
    }

    public DriverResponse getDriverByUserId(UUID userId) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "userId", userId));

        return DriverResponse.fromEntity(driver);
    }

    public DriverResponse getDriverById(UUID driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        return DriverResponse.fromEntity(driver);
    }

    @Transactional
    public DriverResponse updateLocation(UUID userId, LocationUpdateRequest request) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "userId", userId));

        if (!driver.isApproved()) {
            throw new BusinessException("Driver account is not approved", HttpStatus.FORBIDDEN);
        }

        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());

        Driver updatedDriver = driverRepository.save(driver);
        logger.debug("Updated location for driver: {}", driver.getId());

        return DriverResponse.fromEntity(updatedDriver);
    }

    @Transactional
    public DriverResponse updateAvailability(UUID userId, AvailabilityUpdateRequest request) {
        Driver driver = driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "userId", userId));

        if (!driver.isApproved()) {
            throw new BusinessException("Driver account is not approved", HttpStatus.FORBIDDEN);
        }

        // Require location before going available
        if (request.getAvailable() &&
                (driver.getCurrentLatitude() == null || driver.getCurrentLongitude() == null)) {
            throw new BusinessException("Please update your location before going online", HttpStatus.BAD_REQUEST);
        }

        driver.setAvailable(request.getAvailable());

        Driver updatedDriver = driverRepository.save(driver);
        logger.info("Driver {} is now {}", driver.getId(), request.getAvailable() ? "online" : "offline");

        return DriverResponse.fromEntity(updatedDriver);
    }

    public List<DriverResponse> findNearbyDrivers(NearbyDriversRequest request) {
        List<Driver> drivers;

        if (request.getVehicleType() != null) {
            drivers = driverRepository.findNearbyAvailableDriversByVehicleType(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadiusKm(),
                    request.getVehicleType().name(),
                    request.getLimit()
            );
        } else {
            drivers = driverRepository.findNearbyAvailableDrivers(
                    request.getLatitude(),
                    request.getLongitude(),
                    request.getRadiusKm(),
                    request.getLimit()
            );
        }

        return drivers.stream()
                .map(driver -> {
                    double distance = calculateDistance(
                            request.getLatitude().doubleValue(),
                            request.getLongitude().doubleValue(),
                            driver.getCurrentLatitude().doubleValue(),
                            driver.getCurrentLongitude().doubleValue()
                    );
                    DriverResponse response = DriverResponse.fromEntityPublic(driver);
                    response.setDistanceKm(Math.round(distance * 100.0) / 100.0); // Round to 2 decimal places
                    response.setCurrentLatitude(driver.getCurrentLatitude());
                    response.setCurrentLongitude(driver.getCurrentLongitude());
                    return response;
                })
                .collect(Collectors.toList());
    }

    public Driver findById(UUID driverId) {
        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
    }

    public Driver findByUserId(UUID userId) {
        return driverRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "userId", userId));
    }

    public Driver findAvailableDriverById(UUID driverId) {
        Driver driver = findById(driverId);

        if (!driver.isApproved()) {
            throw new BusinessException("Driver is not approved", HttpStatus.BAD_REQUEST);
        }

        if (!driver.isAvailable()) {
            throw new BusinessException("Driver is not available", HttpStatus.BAD_REQUEST);
        }

        return driver;
    }

    /**
     * Haversine formula to calculate distance between two points
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}