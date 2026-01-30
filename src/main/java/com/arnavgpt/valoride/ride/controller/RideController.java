package com.arnavgpt.valoride.ride.controller;

import com.arnavgpt.valoride.common.dto.ApiResponse;
import com.arnavgpt.valoride.config.CustomUserDetails;
import com.arnavgpt.valoride.ride.dto.CancelRideRequest;
import com.arnavgpt.valoride.ride.dto.FareEstimateRequest;
import com.arnavgpt.valoride.ride.dto.FareEstimateResponse;
import com.arnavgpt.valoride.ride.dto.RateRideRequest;
import com.arnavgpt.valoride.ride.dto.RideRequestDto;
import com.arnavgpt.valoride.ride.dto.RideResponse;
import com.arnavgpt.valoride.ride.service.FareService;
import com.arnavgpt.valoride.ride.service.RideService;
import com.arnavgpt.valoride.user.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/rides")
@Tag(name = "Rides", description = "Ride management endpoints")
public class RideController {

    private final RideService rideService;
    private final FareService fareService;

    public RideController(RideService rideService, FareService fareService) {
        this.rideService = rideService;
        this.fareService = fareService;
    }

    // ==================== RIDER ENDPOINTS ====================

    @PostMapping("/request")
    @PreAuthorize("hasRole('RIDER')")
    @Operation(summary = "Request a ride", description = "Rider requests a new ride")
    public ResponseEntity<ApiResponse<RideResponse>> requestRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody RideRequestDto request) {

        RideResponse response = rideService.requestRide(userDetails.getId(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created("Ride requested successfully", response));
    }

    @PostMapping("/estimate")
    @Operation(summary = "Get fare estimate", description = "Get fare estimate for a trip")
    public ResponseEntity<ApiResponse<FareEstimateResponse>> getFareEstimate(
            @Valid @RequestBody FareEstimateRequest request) {

        FareEstimateResponse response = fareService.calculateFareEstimate(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{rideId}/cancel")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    @Operation(summary = "Cancel a ride", description = "Cancel an active ride")
    public ResponseEntity<ApiResponse<RideResponse>> cancelRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId,
            @RequestBody(required = false) CancelRideRequest request) {

        Role role = extractRole(userDetails);
        RideResponse response = rideService.cancelRide(userDetails.getId(), rideId, request, role);
        return ResponseEntity.ok(ApiResponse.success("Ride cancelled", response));
    }

    @PostMapping("/{rideId}/rate")
    @PreAuthorize("hasRole('RIDER')")
    @Operation(summary = "Rate a ride", description = "Rate a completed ride")
    public ResponseEntity<ApiResponse<RideResponse>> rateRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId,
            @Valid @RequestBody RateRideRequest request) {

        RideResponse response = rideService.rateRide(userDetails.getId(), rideId, request);
        return ResponseEntity.ok(ApiResponse.success("Rating submitted", response));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('RIDER')")
    @Operation(summary = "Get ride history", description = "Get rider's ride history")
    public ResponseEntity<ApiResponse<Page<RideResponse>>> getRiderHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RideResponse> rides = rideService.getRiderHistory(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(rides));
    }

    // ==================== DRIVER ENDPOINTS ====================

    @PostMapping("/{rideId}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Accept a ride", description = "Driver accepts a ride request")
    public ResponseEntity<ApiResponse<RideResponse>> acceptRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        RideResponse response = rideService.acceptRide(userDetails.getId(), rideId);
        return ResponseEntity.ok(ApiResponse.success("Ride accepted", response));
    }

    @PostMapping("/{rideId}/arrived")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Arrived at pickup", description = "Driver arrived at pickup location")
    public ResponseEntity<ApiResponse<RideResponse>> arrivedAtPickup(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        RideResponse response = rideService.arrivedAtPickup(userDetails.getId(), rideId);
        return ResponseEntity.ok(ApiResponse.success("Marked as arrived", response));
    }

    @PostMapping("/{rideId}/start")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Start ride", description = "Driver starts the ride")
    public ResponseEntity<ApiResponse<RideResponse>> startRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        RideResponse response = rideService.startRide(userDetails.getId(), rideId);
        return ResponseEntity.ok(ApiResponse.success("Ride started", response));
    }

    @PostMapping("/{rideId}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Complete ride", description = "Driver completes the ride")
    public ResponseEntity<ApiResponse<RideResponse>> completeRide(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        RideResponse response = rideService.completeRide(userDetails.getId(), rideId);
        return ResponseEntity.ok(ApiResponse.success("Ride completed", response));
    }

    @GetMapping("/driver/history")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Get driver ride history", description = "Get driver's ride history")
    public ResponseEntity<ApiResponse<Page<RideResponse>>> getDriverHistory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<RideResponse> rides = rideService.getDriverHistory(userDetails.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(rides));
    }

    // ==================== COMMON ENDPOINTS ====================

    @GetMapping("/{rideId}")
    @Operation(summary = "Get ride by ID", description = "Get ride details")
    public ResponseEntity<ApiResponse<RideResponse>> getRideById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID rideId) {

        RideResponse response = rideService.getRideById(rideId, userDetails.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('RIDER', 'DRIVER')")
    @Operation(summary = "Get active ride", description = "Get user's currently active ride")
    public ResponseEntity<ApiResponse<RideResponse>> getActiveRide(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Role role = extractRole(userDetails);
        RideResponse response = rideService.getActiveRide(userDetails.getId(), role);

        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success("No active ride", null));
        }

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    private Role extractRole(CustomUserDetails userDetails) {
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_RIDER"))) {
            return Role.RIDER;
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_DRIVER"))) {
            return Role.DRIVER;
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return Role.ADMIN;
        }
        return Role.RIDER;
    }
}