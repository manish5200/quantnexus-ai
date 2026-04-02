package com.quantnexus.dto.financial;

/**
 * Provides derived financial insights based on user spending patterns.
 */
public record FinancialHealthDTO(
        String status,         // e.g., "STABLE", "CRITICAL"
        double savingsRate,    // Calculated as (Net Balance / Total Income) * 100
        String recommendation  // Actionable advice
) {}