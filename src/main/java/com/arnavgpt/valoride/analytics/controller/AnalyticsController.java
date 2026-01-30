package com.arnavgpt.valoride.analytics.controller;

import com.arnavgpt.valoride.analytics.dto.DashboardStatsResponse;
import com.arnavgpt.valoride.analytics.dto.DriverPerformanceResponse;
import com.arnavgpt.valoride.analytics.dto.RevenueReportResponse;
import com.arnavgpt.valoride.analytics.service.AnalyticsService;
import com.arnavgpt.valoride.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/analytics")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Analytics", description = "Admin analytics and reports")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats", description = "Get overall platform statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        DashboardStatsResponse stats = analyticsService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue report", description = "Get revenue report for date range")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getRevenueReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        RevenueReportResponse report = analyticsService.getRevenueReport(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(report));
    }

    @GetMapping("/drivers/performance")
    @Operation(summary = "Get driver performance", description = "Get top performing drivers")
    public ResponseEntity<ApiResponse<DriverPerformanceResponse>> getDriverPerformance(
            @RequestParam(defaultValue = "10") int limit) {

        DriverPerformanceResponse performance = analyticsService.getDriverPerformance(limit);
        return ResponseEntity.ok(ApiResponse.success(performance));
    }
}