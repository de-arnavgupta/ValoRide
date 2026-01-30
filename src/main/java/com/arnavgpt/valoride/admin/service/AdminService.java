package com.arnavgpt.valoride.admin.service;

import com.arnavgpt.valoride.driver.dto.AdminDriverActionRequest;
import com.arnavgpt.valoride.driver.dto.DriverResponse;
import com.arnavgpt.valoride.driver.entity.ApprovalStatus;
import com.arnavgpt.valoride.driver.entity.Driver;
import com.arnavgpt.valoride.driver.repository.DriverRepository;
import com.arnavgpt.valoride.exception.BusinessException;
import com.arnavgpt.valoride.exception.ResourceNotFoundException;
import com.arnavgpt.valoride.notification.service.NotificationService;
import com.arnavgpt.valoride.user.dto.UserResponse;
import com.arnavgpt.valoride.user.entity.Role;
import com.arnavgpt.valoride.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public AdminService(DriverRepository driverRepository, UserRepository userRepository,
                        NotificationService notificationService) {
        this.driverRepository = driverRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public Page<DriverResponse> getPendingDrivers(Pageable pageable) {
        Page<Driver> drivers = driverRepository.findByApprovalStatus(ApprovalStatus.PENDING, pageable);
        return drivers.map(DriverResponse::fromEntity);
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findByActiveTrue(pageable)
                .map(UserResponse::fromEntity);
    }

    public Page<UserResponse> getUsersByRole(Role role, Pageable pageable) {
        return userRepository.findByRole(role, pageable)
                .map(UserResponse::fromEntity);
    }

    @Transactional
    public DriverResponse approveDriver(UUID driverId, AdminDriverActionRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        if (driver.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException(
                    "Driver is already " + driver.getApprovalStatus().name().toLowerCase(),
                    HttpStatus.BAD_REQUEST
            );
        }

        driver.setApprovalStatus(ApprovalStatus.APPROVED);
        driver.setRejectionReason(null);

        Driver savedDriver = driverRepository.save(driver);
        logger.info("Driver approved: {}", driverId);

        // Send approval notification
        notificationService.sendDriverApprovedNotification(savedDriver);

        return DriverResponse.fromEntity(savedDriver);
    }

    @Transactional
    public DriverResponse rejectDriver(UUID driverId, AdminDriverActionRequest request) {
        if (request.getReason() == null || request.getReason().isBlank()) {
            throw new BusinessException("Rejection reason is required", HttpStatus.BAD_REQUEST);
        }

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        if (driver.getApprovalStatus() != ApprovalStatus.PENDING) {
            throw new BusinessException(
                    "Driver is already " + driver.getApprovalStatus().name().toLowerCase(),
                    HttpStatus.BAD_REQUEST
            );
        }

        driver.setApprovalStatus(ApprovalStatus.REJECTED);
        driver.setRejectionReason(request.getReason());
        driver.setAvailable(false);

        Driver savedDriver = driverRepository.save(driver);
        logger.info("Driver rejected: {} - Reason: {}", driverId, request.getReason());

        // Send rejection notification
        notificationService.sendDriverRejectedNotification(savedDriver);

        return DriverResponse.fromEntity(savedDriver);
    }

    public long getPendingDriversCount() {
        return driverRepository.countByApprovalStatus(ApprovalStatus.PENDING);
    }

    public long getAvailableDriversCount() {
        return driverRepository.countAvailableDrivers();
    }
}