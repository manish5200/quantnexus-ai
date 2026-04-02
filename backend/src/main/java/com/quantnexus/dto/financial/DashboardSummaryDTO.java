package com.quantnexus.dto.financial;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Consolidates all dashboard data into a single response for frontend efficiency.
 */
public record DashboardSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        Map<String, BigDecimal> categoryTotals,
        List<MonthlyTrendDTO> monthlyTrends,
        List<RecentActivityDTO> recentActivity,
        FinancialHealthDTO healthScore
) {}
