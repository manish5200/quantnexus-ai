package com.quantnexus.domain;

import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 *   Financial Ledger Entity representing a single user transaction.
 * * @author Manish Singh
 */
@Entity
@Table(name = "financial_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@SoftDelete  //records are hidden but never destroyed.
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Human-readable, collision-resistant public identifier (e.g., TXN-20260402-A1B2C3).
     * Immutable to ensure tracking consistency across customer support and external APIs.
     */
    @Column(nullable = false, unique = true, updatable = false)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "Record must be associated with a user")
    private User user;

    /**
     * DB-level precision (19 digits total, 4 decimal places) to prevent
     * floating-point rounding errors native to financial calculations.
     */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    /**
     * The Audit: An immutable snapshot of the user's account balance
     * exactly at the moment this transaction was processed.
     */
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Transaction type is required")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Category is required")
    private TransactionCategory transactionCategory;

    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate transactionDate;

    @NotBlank(message = "Description is required.")
    private String description;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    /**
     * Schemaless extension field for frontend flexibility (e.g., storing merchant data,
     * receipt URLs, or geolocation) without requiring database migrations.
     * Mapped securely for H2/PostgreSQL compatibility.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> metadata;

    // Enables Optimistic Locking to prevent concurrent update race conditions
    @Version
    private Long version;

    /**
     * Entity lifecycle hook to auto-generate the reference number prior to DB insertion.
     * Uses a YYYYMMDD prefix for fast sorting and a 6-character hex for collision safety.
     */
    @PrePersist
    protected void onPrePersist() {
        if (this.referenceNumber == null) {
            String datePart = LocalDate.now().toString().replace("-", "");
            String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            this.referenceNumber = "TXN-" + datePart + "-" + randomPart;
        }
    }
}