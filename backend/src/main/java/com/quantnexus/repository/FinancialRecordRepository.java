package com.quantnexus.repository;

import com.quantnexus.domain.FinancialRecord;
import com.quantnexus.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, UUID> , JpaSpecificationExecutor<FinancialRecord> {

    //Calculate totals dynamically handled by the DB
    @Query("select coalesce(sum(f.amount),0) from FinancialRecord f where f.transactionType = :type")
    BigDecimal getTotalByType(@Param("type") TransactionType type);

    //Fetch recent activity directly (using Pageable to limit to top 5)
    @Query("select f from FinancialRecord f order by f.transactionDate desc, f.createdAt desc")
    List<FinancialRecord>findRecentActivity(Pageable pageable);

    //Identifies all records for a specific user ID.
    List<FinancialRecord> findByUserId(Long userId);

    //Resolves a unique transaction by its public-facing reference number.
    //Uses: TXN-YYYYMMDD-XXXX
    Optional<FinancialRecord> findByReferenceNumber(String referenceNumber);

    //Helper in Transaction Creation
    //Fetches only the single most recent record to calculate 'balanceAfter' snapshot.
    Optional<FinancialRecord>findFirstByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId);

    //User's Transaction History
    Page<FinancialRecord> findFirstByUserIdOrderByTransactionDateDescCreatedAtDesc(Long userId, Pageable pageable);
}
