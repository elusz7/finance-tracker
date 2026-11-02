
package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;
import com.example.financetracker.exception.TransactionNotFoundException;
import com.example.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction(
                transactionRequest.getDescription(),
                transactionRequest.getAmount(),
                transactionRequest.getType(),
                transactionRequest.getExpenseCategory(),
                transactionRequest.getIncomeSource(),
                transactionRequest.getDate()
        );

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction fetchTransaction(Long transactionId) {
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFoundException(
                        String.format("Fetch Transaction Error. Id: %s", transactionId)
                )
        );
    }

    @Override
    public List<Transaction> fetchTransactionList() {
        return transactionRepository.findAll();
    }

    @Override
    public List<Transaction> fetchFilteredTransactions(
            TransactionType type,
            ExpenseCategoryType expenseCategory,
            IncomeSourceType incomeSource,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {
        if (startDate != null && endDate != null
                && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date."
            );
        }

        if (minAmount != null && maxAmount != null
                && minAmount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException(
                    "Minimum amount cannot exceed maximum amount."
            );
        }

        return transactionRepository.findFilteredTransactions(
                type,
                expenseCategory,
                incomeSource,
                startDate,
                endDate,
                minAmount,
                maxAmount
        );
    }

    @Override
    public Transaction updateTransaction(
            TransactionRequest transactionRequest,
            Long transactionId
    ) {
        Transaction dbTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        String.format("Update Transaction Error. Id: %s", transactionId)
                ));

        if (Objects.nonNull(transactionRequest.getDescription())
                && !transactionRequest.getDescription().isBlank()) {
            dbTransaction.setDescription(transactionRequest.getDescription());
        }

        if (Objects.nonNull(transactionRequest.getAmount())
                && transactionRequest.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            dbTransaction.setAmount(transactionRequest.getAmount());
        }

        if (Objects.nonNull(transactionRequest.getType())) {
            dbTransaction.setType(transactionRequest.getType());
        }

        if (Objects.nonNull(transactionRequest.getDate())) {
            dbTransaction.setDate(transactionRequest.getDate());
        }

        // Keep the classification consistent with the effective transaction type.
        if (dbTransaction.getType() == TransactionType.EXPENSE) {
            if (Objects.nonNull(transactionRequest.getExpenseCategory())) {
                dbTransaction.setExpenseCategory(
                        transactionRequest.getExpenseCategory()
                );
            }

            dbTransaction.setIncomeSource(null);
        } else if (dbTransaction.getType() == TransactionType.INCOME) {
            if (Objects.nonNull(transactionRequest.getIncomeSource())) {
                dbTransaction.setIncomeSource(
                        transactionRequest.getIncomeSource()
                );
            }

            dbTransaction.setExpenseCategory(null);
        }

        return transactionRepository.save(dbTransaction);
    }

    @Override
    public void deleteTransactionById(Long transactionId) {
        Transaction dbTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(
                        String.format("Deletion Transaction Error. Id: %s", transactionId)
                ));

        transactionRepository.delete(dbTransaction);
    }
}