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

public class FinancialRecordSpecs {

    public static Specification<FinancialRecord>getFilteredRecords(
            Long userId, TransactionType type, TransactionCategory category,
            LocalDate startDate, LocalDate endDate){

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Must belong to the user
            predicates.add(criteriaBuilder.equal(root.get("user").get("id"), userId));

            // 2. Optional Filters
            if(type != null){
                predicates.add(criteriaBuilder.equal(root.get("transactionType"), type));
            }
            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("transactionCategory"), category));
            }
            if(startDate != null){
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("transactionDate"), startDateTime));
            }
            if (endDate != null) {
                LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("transactionDate"), endOfDay));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
