package com.quantnexus.repository.specification;

import com.quantnexus.domain.FinancialRecord;
import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dynamic SQL Query Builder for the Company Ledger.
 * Constructs complex filters without writing raw SQL.
 */
public class FinancialRecordSpecs {

    public static Specification<FinancialRecord>getFilteredRecords(
            TransactionType type,
            TransactionCategory category,
            LocalDate startDate,
            LocalDate endDate){

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Filter by Type (e.g., INCOME vs EXPENSE)
            if(type != null){
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), type));
            }

            // 2. Filter by Category (e.g., INFRASTRUCTURE vs MARKETING)
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionCategory"), category));
            }

            // 3. Filter by Date Range (e.g., "Show me Q1 2024")
            if(startDate != null){
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDateTime));
            }
            if (endDate != null) {
                LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endOfDay));
            }

            //combine all active filters with an SQL "AND"
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
