package com.quantnexus.dto.financial;

import java.math.BigDecimal;

/**
 * Represents income vs expense trends for a specific month.
 */
public record MonthlyTrendDTO(
        String month,
        BigDecimal totalIncome,
        BigDecimal totalExpenses
) {}

