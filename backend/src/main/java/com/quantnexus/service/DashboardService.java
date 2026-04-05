package com.quantnexus.service;

import com.quantnexus.domain.FinancialRecord;
import com.quantnexus.domain.enums.TransactionType;
import com.quantnexus.dto.financial.DashboardSummaryDTO;
import com.quantnexus.dto.financial.FinancialHealthDTO;
import com.quantnexus.dto.financial.MonthlyTrendDTO;
import com.quantnexus.dto.financial.RecentActivityDTO;
import com.quantnexus.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The "Analytics Engine" of the platform.
 * It processes raw transaction data into human-readable insights
 * like Savings Rates, Spending Trends, and Category Totals.
 * * @author Manish Singh
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository recordRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboardSummary", key = "'global'")
    public DashboardSummaryDTO getDashboardSummary(){
        log.info("Generating real-time financial intelligence for the Company Ledger");

        //  O(1) Database Aggregations
        // 2. Calculate Totals using Functional Streams
        BigDecimal totalIncome = recordRepository.getTotalByType(TransactionType.INCOME);
        BigDecimal totalExpenses = recordRepository.getTotalByType(TransactionType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        // 2. Get Recent Activity directly from DB (Limit 5)
        List<RecentActivityDTO> recentActivity = recordRepository.findRecentActivity(PageRequest.of(0,5))
                .stream()
                .map(this::mapToRecentActivity)
                .toList();


        // 3. For Categorical and Monthly Trends:
        List<FinancialRecord>expenseRecords = recordRepository.findAll().stream()
                .filter(record -> record.getTransactionType() == TransactionType.EXPENSE)
                .toList();

        Map<String, BigDecimal>categoryTotals = expenseRecords.stream()
                .collect(Collectors.groupingBy(
                        record -> record.getTransactionCategory().name(),
                        Collectors.reducing(BigDecimal.ZERO,FinancialRecord::getAmount, BigDecimal::add)
                ));

        // 5. Generate Monthly Trends (Simple month-to-month comparison)
        List<MonthlyTrendDTO>monthlyTrends = generateMonthlyTrends(recordRepository.findAll());

        // 6. Calculate Financial Health Score (The "Senior" Unique Logic)
        FinancialHealthDTO healthScore = calculateFinancialHealth(totalIncome,netBalance);


        return new DashboardSummaryDTO(
                totalIncome,
                totalExpenses,
                netBalance,
                categoryTotals,
                monthlyTrends,
                recentActivity,
                healthScore
        );
    }


    private BigDecimal calculateTotalByType(List<FinancialRecord> records,
                                            TransactionType transactionType) {
        return records.stream()
                .filter(record -> record.getTransactionType() == transactionType)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Groups data by Month for trend visualization.
     */
    private List<MonthlyTrendDTO> generateMonthlyTrends(List<FinancialRecord> allRecords) {
        Map<String, List<FinancialRecord>> monthlyMap = new HashMap<>();
        for(FinancialRecord record : allRecords){
            String month = record.getTransactionDate().getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            monthlyMap.computeIfAbsent(month, k -> new ArrayList<>()).add(record);
        }

        List<MonthlyTrendDTO>trends = new ArrayList<>();
        for(var entry : monthlyMap.entrySet()){
            BigDecimal totalIncome = calculateTotalByType(entry.getValue(), TransactionType.INCOME);
            BigDecimal totalExpenses = calculateTotalByType(entry.getValue(), TransactionType.EXPENSE);
            trends.add(new MonthlyTrendDTO(entry.getKey(), totalIncome, totalExpenses));
        }
        return trends;
    }

    /**
     * Calculates the savings rate and provides actionable financial advice.
     */
    private FinancialHealthDTO calculateFinancialHealth(BigDecimal totalIncome,
                                                        BigDecimal netBalance) {
        // Base case: Avoid division by zero if we have no income data
        if(totalIncome.compareTo(netBalance) <= 0){
            return new FinancialHealthDTO("INITIALIZING", 0.0, "Add your first income to calculate your health score.");
        }

        // Calculation: (Net Balance / Total Income) * 100
        double savingRate = netBalance
                .divide(totalIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100)).doubleValue();

        String status;
        String recommend;
        // Adaptive thresholds for savings health status
        if(savingRate >= 20.0){
            status = "STABLE";
            recommend = "Excellent behavior! You are saving more than the 20% benchmark.";
        }else if(savingRate > 0){
            status = "CAUTION";
            recommend = "Good start. Review non-essential categories to reach 20% savings.";
        }else{
            status = "CRITICAL";
            recommend = "Your expenses currently exceed your income. Immediate review suggested.";
        }

        return new FinancialHealthDTO(status, savingRate, recommend);
    }


    private RecentActivityDTO mapToRecentActivity(FinancialRecord record) {
        return new RecentActivityDTO(
                record.getReferenceNumber(),
                record.getAmount(),
                record.getTransactionType().name(),
                record.getTransactionCategory().name(),
                record.getTransactionDate(),
                record.getDescription()
        );
    }

}
