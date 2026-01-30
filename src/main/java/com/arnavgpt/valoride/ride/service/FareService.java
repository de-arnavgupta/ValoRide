package com.arnavgpt.valoride.ride.service;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.arnavgpt.valoride.ride.dto.FareEstimateRequest;
import com.arnavgpt.valoride.ride.dto.FareEstimateResponse;
import com.arnavgpt.valoride.ride.entity.FareConfig;
import com.arnavgpt.valoride.ride.repository.FareConfigRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class FareService {

    private static final Logger logger = LoggerFactory.getLogger(FareService.class);
    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final int AVERAGE_SPEED_KMH = 25; // Average city speed

    private final FareConfigRepository fareConfigRepository;

    public FareService(FareConfigRepository fareConfigRepository) {
        this.fareConfigRepository = fareConfigRepository;
    }

    /**
     * Initialize default fare configs if not present
     */
    @PostConstruct
    public void initDefaultFareConfigs() {
        createIfNotExists(VehicleType.AUTO, new BigDecimal("25"), new BigDecimal("12"), new BigDecimal("1"), new BigDecimal("30"));
        createIfNotExists(VehicleType.BIKE, new BigDecimal("15"), new BigDecimal("8"), new BigDecimal("0.5"), new BigDecimal("20"));
        createIfNotExists(VehicleType.SEDAN, new BigDecimal("50"), new BigDecimal("15"), new BigDecimal("2"), new BigDecimal("80"));
        createIfNotExists(VehicleType.SUV, new BigDecimal("80"), new BigDecimal("20"), new BigDecimal("3"), new BigDecimal("120"));
        logger.info("Fare configurations initialized");
    }

    private void createIfNotExists(VehicleType type, BigDecimal baseFare, BigDecimal perKm,
                                   BigDecimal perMin, BigDecimal minFare) {
        if (!fareConfigRepository.existsByVehicleType(type)) {
            FareConfig config = new FareConfig(type, baseFare, perKm, perMin, minFare);
            fareConfigRepository.save(config);
            logger.debug("Created fare config for {}", type);
        }
    }

    /**
     * Calculate fare estimate for a trip
     */
    public FareEstimateResponse calculateFareEstimate(FareEstimateRequest request) {
        BigDecimal distanceKm = calculateDistance(
                request.getPickupLatitude().doubleValue(),
                request.getPickupLongitude().doubleValue(),
                request.getDropLatitude().doubleValue(),
                request.getDropLongitude().doubleValue()
        );

        int estimatedMins = calculateEstimatedDuration(distanceKm);

        List<FareEstimateResponse.VehicleFare> fares = new ArrayList<>();

        if (request.getVehicleType() != null) {
            // Calculate for specific vehicle type
            FareEstimateResponse.VehicleFare fare = calculateVehicleFare(request.getVehicleType(), distanceKm);
            if (fare != null) {
                fares.add(fare);
            }
        } else {
            // Calculate for all vehicle types
            for (VehicleType type : VehicleType.values()) {
                FareEstimateResponse.VehicleFare fare = calculateVehicleFare(type, distanceKm);
                if (fare != null) {
                    fares.add(fare);
                }
            }
        }

        return new FareEstimateResponse(
                distanceKm.setScale(2, RoundingMode.HALF_UP),
                estimatedMins,
                fares
        );
    }

    /**
     * Calculate fare for a specific vehicle type
     */
    public BigDecimal calculateFare(VehicleType vehicleType, BigDecimal distanceKm) {
        FareConfig config = fareConfigRepository.findByVehicleTypeAndActiveTrue(vehicleType)
                .orElse(getDefaultConfig(vehicleType));

        BigDecimal distanceCharge = distanceKm.multiply(config.getPerKmRate());
        BigDecimal totalFare = config.getBaseFare().add(distanceCharge);

        // Apply minimum fare
        if (totalFare.compareTo(config.getMinFare()) < 0) {
            totalFare = config.getMinFare();
        }

        return totalFare.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    public BigDecimal calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;

        // Add 20% buffer for actual road distance vs straight line
        distance = distance * 1.2;

        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Estimate trip duration based on distance
     */
    public int calculateEstimatedDuration(BigDecimal distanceKm) {
        // Time = Distance / Speed (in hours), convert to minutes
        double hours = distanceKm.doubleValue() / AVERAGE_SPEED_KMH;
        int minutes = (int) Math.ceil(hours * 60);

        // Minimum 5 minutes
        return Math.max(minutes, 5);
    }

    private FareEstimateResponse.VehicleFare calculateVehicleFare(VehicleType type, BigDecimal distanceKm) {
        FareConfig config = fareConfigRepository.findByVehicleTypeAndActiveTrue(type)
                .orElse(getDefaultConfig(type));

        if (config == null) {
            return null;
        }

        BigDecimal distanceCharge = distanceKm.multiply(config.getPerKmRate());
        BigDecimal totalFare = config.getBaseFare().add(distanceCharge);

        // Apply minimum fare
        if (totalFare.compareTo(config.getMinFare()) < 0) {
            totalFare = config.getMinFare();
        }

        return new FareEstimateResponse.VehicleFare(
                type,
                totalFare.setScale(2, RoundingMode.HALF_UP),
                config.getBaseFare(),
                distanceCharge.setScale(2, RoundingMode.HALF_UP)
        );
    }

    private FareConfig getDefaultConfig(VehicleType type) {
        return switch (type) {
            case AUTO -> new FareConfig(type, new BigDecimal("25"), new BigDecimal("12"), new BigDecimal("1"), new BigDecimal("30"));
            case BIKE -> new FareConfig(type, new BigDecimal("15"), new BigDecimal("8"), new BigDecimal("0.5"), new BigDecimal("20"));
            case SEDAN -> new FareConfig(type, new BigDecimal("50"), new BigDecimal("15"), new BigDecimal("2"), new BigDecimal("80"));
            case SUV -> new FareConfig(type, new BigDecimal("80"), new BigDecimal("20"), new BigDecimal("3"), new BigDecimal("120"));
        };
    }
}