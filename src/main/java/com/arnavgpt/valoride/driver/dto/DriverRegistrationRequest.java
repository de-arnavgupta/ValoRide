package com.arnavgpt.valoride.driver.dto;

import com.arnavgpt.valoride.driver.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class DriverRegistrationRequest {

    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 50, message = "License number must be between 5 and 50 characters")
    private String licenseNumber;

    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}$",
            message = "Invalid vehicle number format (e.g., MH12AB1234)")
    private String vehicleNumber;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    public DriverRegistrationRequest() {
    }

    public DriverRegistrationRequest(String licenseNumber, String vehicleNumber, VehicleType vehicleType) {
        this.licenseNumber = licenseNumber;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}