package com.arnavgpt.valoride.admin.controller;

import com.arnavgpt.valoride.admin.service.AdminService;
import com.arnavgpt.valoride.common.dto.ApiResponse;
import com.arnavgpt.valoride.driver.dto.AdminDriverActionRequest;
import com.arnavgpt.valoride.driver.dto.DriverResponse;
import com.arnavgpt.valoride.user.dto.UserResponse;
import com.arnavgpt.valoride.user.entity.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Get paginated list of all active users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) Role role) {

        Page<UserResponse> users;
        if (role != null) {
            users = adminService.getUsersByRole(role, pageable);
        } else {
            users = adminService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/drivers/pending")
    @Operation(summary = "Get pending drivers", description = "Get drivers pending approval")
    public ResponseEntity<ApiResponse<Page<DriverResponse>>> getPendingDrivers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<DriverResponse> drivers = adminService.getPendingDrivers(pageable);
        return ResponseEntity.ok(ApiResponse.success(drivers));
    }

    @PostMapping("/drivers/{driverId}/approve")
    @Operation(summary = "Approve driver", description = "Approve a pending driver registration")
    public ResponseEntity<ApiResponse<DriverResponse>> approveDriver(
            @PathVariable UUID driverId,
            @RequestBody(required = false) AdminDriverActionRequest request) {

        if (request == null) {
            request = new AdminDriverActionRequest();
        }

        DriverResponse response = adminService.approveDriver(driverId, request);
        return ResponseEntity.ok(ApiResponse.success("Driver approved successfully", response));
    }

    @PostMapping("/drivers/{driverId}/reject")
    @Operation(summary = "Reject driver", description = "Reject a pending driver registration")
    public ResponseEntity<ApiResponse<DriverResponse>> rejectDriver(
            @PathVariable UUID driverId,
            @RequestBody AdminDriverActionRequest request) {

        DriverResponse response = adminService.rejectDriver(driverId, request);
        return ResponseEntity.ok(ApiResponse.success("Driver rejected", response));
    }

    @GetMapping("/stats/drivers")
    @Operation(summary = "Get driver stats", description = "Get driver statistics")
    public ResponseEntity<ApiResponse<DriverStats>> getDriverStats() {
        DriverStats stats = new DriverStats(
                adminService.getPendingDriversCount(),
                adminService.getAvailableDriversCount()
        );
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    public static class DriverStats {
        private final long pendingApproval;
        private final long availableOnline;

        public DriverStats(long pendingApproval, long availableOnline) {
            this.pendingApproval = pendingApproval;
            this.availableOnline = availableOnline;
        }

        public long getPendingApproval() {
            return pendingApproval;
        }

        public long getAvailableOnline() {
            return availableOnline;
        }
    }
}