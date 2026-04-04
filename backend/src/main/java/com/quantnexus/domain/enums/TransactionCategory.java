package com.quantnexus.domain.enums;

import lombok.Getter;

@Getter
public enum TransactionCategory {
    // --- Income Categories ---
    SALARY("Monthly salary or wages", TransactionType.INCOME),
    INVESTMENT("Dividends, stocks, or interest", TransactionType.INCOME),
    GIFTS("Monetary gifts or bonuses", TransactionType.INCOME),

    // --- Expense Categories ---
    FOOD_AND_DINING("Restaurants, groceries, and cafes", TransactionType.EXPENSE),
    SHOPPING("Clothing, electronics, and personal items", TransactionType.EXPENSE),
    TRANSPORT("Fuel, public transport, or ride-sharing", TransactionType.EXPENSE),
    BILLS_AND_UTILITIES("Electricity, water, internet, or rent", TransactionType.EXPENSE),
    ENTERTAINMENT("Movies, gaming, or hobbies", TransactionType.EXPENSE),
    HEALTHCARE("Medicine, gym, or doctor visits", TransactionType.EXPENSE),
    EDUCATION("Tuition, books, or courses", TransactionType.EXPENSE),
    OTHERS("Miscellaneous transactions", TransactionType.EXPENSE);

    private final String description;
    private final TransactionType allowedType;
    TransactionCategory(String description, TransactionType allowedType) {
        this.description = description;
        this.allowedType = allowedType;
    }
}