package com.quantnexus.controller;

import com.quantnexus.dto.financial.DashboardSummaryDTO;
import com.quantnexus.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Gateway for Financial Intelligence.
 * Aggregates high-level metrics, trends, and health scores for the primary UI dashboard.
 * @author Manish Singh
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Compiles a comprehensive financial pulse for the user.
     * Consolidates totals, category splits, and trend analysis into a single optimized payload.
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')") // Everyone can see the company pulse!
    public ResponseEntity<DashboardSummaryDTO> getDashboardSummary() {
        log.info("API Request: Compiling real-time company financial intelligence summary.");

        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
