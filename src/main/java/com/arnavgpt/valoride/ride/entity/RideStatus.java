package com.arnavgpt.valoride.ride.entity;

public enum RideStatus {
    REQUESTED,      // Rider requested, waiting for driver match
    MATCHED,        // Driver assigned, heading to pickup
    ARRIVED,        // Driver arrived at pickup location
    STARTED,        // Ride in progress
    COMPLETED,      // Ride finished
    CANCELLED       // Cancelled by rider or driver
}