package com.quantnexus.dto.financial;

import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for professional, partial ledger updates.
 */
public record TransactionUpdateRequest(
        @Positive(message = "Amount must be strictly positive if provided")
        BigDecimal amount,

        TransactionType type,
        TransactionCategory category,
        String description,

        @PastOrPresent(message = "Future transactions are not permitted in the historical ledger")
        LocalDate transactionDate,

        Map<String, String> metadata
) {}
