package com.arnavgpt.valoride.ride.repository;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import com.arnavgpt.valoride.ride.entity.FareConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FareConfigRepository extends JpaRepository<FareConfig, UUID> {

    Optional<FareConfig> findByVehicleTypeAndActiveTrue(VehicleType vehicleType);

    Optional<FareConfig> findByVehicleType(VehicleType vehicleType);

    boolean existsByVehicleType(VehicleType vehicleType);
}