package com.quantnexus.repository;

import com.quantnexus.domain.FinancialRecord;
import com.quantnexus.domain.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> {

    List<FinancialRecord>findByUserIdAndTransactionType(Long userId, TransactionType transactionType);

    List<FinancialRecord> findByUserId(Long userId);

    // Used for searching transactions by the human-readable Business Key
    Optional<FinancialRecord> findByReferenceNumber(String refNumber);
}
