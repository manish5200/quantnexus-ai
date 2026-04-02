package com.quantnexus.dto.financial;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A projection of a transaction for the "Recent Activity" feed.
 * Uses the public referenceNumber instead of the internal UUID.
 */
public record RecentActivityDTO(
        String referenceNumber,
        BigDecimal amount,
        String type,
        String category,
        LocalDate date,
        String description
) {}
