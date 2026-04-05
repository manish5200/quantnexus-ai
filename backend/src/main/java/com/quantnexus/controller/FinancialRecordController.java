package com.quantnexus.controller;

import com.quantnexus.domain.enums.TransactionCategory;
import com.quantnexus.domain.enums.TransactionType;
import com.quantnexus.dto.financial.TransactionRequest;
import com.quantnexus.dto.financial.TransactionResponse;
import com.quantnexus.dto.financial.TransactionUpdateRequest;
import com.quantnexus.security.SecurityUser;
import com.quantnexus.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * REST Controller for the Financial Ledger.
 * Maps incoming HTTP requests to our Business Intelligence Engine.
 * @author Manish Singh
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;

    /**
     * Records a new transaction in the ledger.
     * ADMIN ONLY: "Can create, update, and manage records"
     * Returns 201 Created on success.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse>createTransaction(
            @AuthenticationPrincipal SecurityUser securityUser,
            @Valid @RequestBody TransactionRequest request){
        Long userId = securityUser.getId();
        log.info("API Request: Create transaction for User {}", userId);
        TransactionResponse response = recordService.createTransaction(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a ledger entry by its unique reference number.
     * Implements dual-path security: validates record ownership or staff bypass.
     */
    @GetMapping("/{refNumber}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<TransactionResponse>getByReferenceNumber(
            @PathVariable String refNumber){
        log.info("API Request: Fetch company record {}", refNumber);
        return ResponseEntity.ok(recordService.getByReference(refNumber));
    }

    /**
     * DATABASE-LEVEL DYNAMIC FILTERING
     *Fetches a paginated history of all company transactions based on criteria.
     * Example: /api/v1/records?type=EXPENSE&category=FOOD&startDate=2024-01-01
     */
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<Page<TransactionResponse>>getTransactionsHistory(
            // --- The Standout Filtering Params ---
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) TransactionCategory category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            // --- Pagination & Sorting ---
            @PageableDefault(size = 15, sort = "transactionDate", direction = Sort.Direction.DESC) Pageable pageable){

        log.info("API Request: Fetch filtered global company history");
        return ResponseEntity.ok(recordService.getHistory(type, category, startDate, endDate, pageable));
    }

    //Updating the existing record using transaction reference number
    @PutMapping("/{refNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable String refNumber,
            @Valid @RequestBody TransactionUpdateRequest request) {
        log.info("API Request: Admin update for record [{}]", refNumber);
        return ResponseEntity.ok(recordService.updateRecord(refNumber, request));
    }

    /**
     * Soft-deletes a record from the active ledger.
     * Only accessible by ADMIN (Enforced in Service layer).
     */
    @DeleteMapping("/{refNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable String refNumber) {
        log.warn("API Request: Delete record {}", refNumber);
        recordService.deleteRecord(refNumber);
        return ResponseEntity.noContent().build();
    }

}
