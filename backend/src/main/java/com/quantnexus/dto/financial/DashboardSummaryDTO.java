package com.quantnexus.dto.financial;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * Consolidates all dashboard data into a single response for frontend efficiency.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
public record DashboardSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        Map<String, BigDecimal> categoryTotals,
        List<MonthlyTrendDTO> monthlyTrends,
        List<RecentActivityDTO> recentActivity,
        FinancialHealthDTO healthScore
) {}
