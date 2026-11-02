package com.example.financetracker.repository;

import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
    SELECT t
    FROM Transaction t
    WHERE (:type IS NULL OR t.type = :type)
      AND (:expenseCategory IS NULL OR t.expenseCategory = :expenseCategory)
      AND (:incomeSource IS NULL OR t.incomeSource = :incomeSource)
      AND (CAST(:startDate AS date) IS NULL OR t.date >= :startDate)
      AND (CAST(:endDate AS date) IS NULL OR t.date <= :endDate)
      AND (:minAmount IS NULL OR t.amount >= :minAmount)
      AND (:maxAmount IS NULL OR t.amount <= :maxAmount)
    ORDER BY t.date DESC
    """)
    List<Transaction> findFilteredTransactions(
            @Param("type") TransactionType type,
            @Param("expenseCategory") ExpenseCategoryType expenseCategory,
            @Param("incomeSource") IncomeSourceType incomeSource,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount
    );
}
