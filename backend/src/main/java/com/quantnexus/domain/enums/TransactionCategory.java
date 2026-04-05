package com.quantnexus.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionCategory {
    // --- Income Categories ---
    REVENUE("Incoming business income", TransactionType.INCOME),
    INVESTMENT("Funding, capital injection, or interest", TransactionType.INCOME),

    // --- Expense Categories ---
    SALARY("Employee payments and payroll", TransactionType.EXPENSE),
    INFRASTRUCTURE("Cloud, servers, and software tools", TransactionType.EXPENSE),
    OPERATIONS("Day-to-day operational costs", TransactionType.EXPENSE),
    MARKETING("Ads, promotions, and sponsorships", TransactionType.EXPENSE),
    TAX("Government payments and compliance", TransactionType.EXPENSE),
    OTHER("Miscellaneous expenses", TransactionType.EXPENSE);

    private final String description;
    private final TransactionType allowedType;

    TransactionCategory(String description, TransactionType allowedType) {
        this.description = description;
        this.allowedType = allowedType;
    }
}