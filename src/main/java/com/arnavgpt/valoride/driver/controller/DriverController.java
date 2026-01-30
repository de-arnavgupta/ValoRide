package com.arnavgpt.valoride.driver.controller;

import com.arnavgpt.valoride.common.dto.ApiResponse;
import com.arnavgpt.valoride.config.CustomUserDetails;
import com.arnavgpt.valoride.driver.dto.AvailabilityUpdateRequest;
import com.arnavgpt.valoride.driver.dto.DriverRegistrationRequest;
import com.arnavgpt.valoride.driver.dto.DriverResponse;
import com.arnavgpt.valoride.driver.dto.LocationUpdateRequest;
import com.arnavgpt.valoride.driver.dto.NearbyDriversRequest;
import com.arnavgpt.valoride.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/drivers")
@Tag(name = "Drivers", description = "Driver management endpoints")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Register as driver", description = "Register driver profile with vehicle details")
    public ResponseEntity<ApiResponse<DriverResponse>> registerDriver(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody DriverRegistrationRequest request) {

        DriverResponse response = driverService.registerDriver(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Driver registration submitted for approval", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Get driver profile", description = "Get current driver's profile")
    public ResponseEntity<ApiResponse<DriverResponse>> getMyDriverProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        DriverResponse response = driverService.getDriverByUserId(userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Get driver by ID", description = "Get driver details by driver ID")
    public ResponseEntity<ApiResponse<DriverResponse>> getDriverById(
            @PathVariable UUID driverId) {

        DriverResponse response = driverService.getDriverById(driverId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/location")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Update location", description = "Update driver's current location")
    public ResponseEntity<ApiResponse<DriverResponse>> updateLocation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody LocationUpdateRequest request) {

        DriverResponse response = driverService.updateLocation(userDetails.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Location updated", response));
    }

    @PutMapping("/availability")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Update availability", description = "Toggle driver online/offline status")
    public ResponseEntity<ApiResponse<DriverResponse>> updateAvailability(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AvailabilityUpdateRequest request) {

        DriverResponse response = driverService.updateAvailability(userDetails.getId(), request);
        String message = request.getAvailable() ? "You are now online" : "You are now offline";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    @PostMapping("/nearby")
    @Operation(summary = "Find nearby drivers", description = "Find available drivers near a location")
    public ResponseEntity<ApiResponse<List<DriverResponse>>> findNearbyDrivers(
            @Valid @RequestBody NearbyDriversRequest request) {

        List<DriverResponse> drivers = driverService.findNearbyDrivers(request);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }
}