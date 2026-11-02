package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(TransactionRequest transactionRequest);
    Transaction fetchTransaction(Long transactionId);
    List<Transaction> fetchTransactionList();
    List<Transaction> fetchFilteredTransactions(
            TransactionType type,
            ExpenseCategoryType expenseCategory,
            IncomeSourceType incomeSource,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount
    );
    Transaction updateTransaction(TransactionRequest transactionRequest, Long transactionId);
    void deleteTransactionById(Long transactionId);

}
