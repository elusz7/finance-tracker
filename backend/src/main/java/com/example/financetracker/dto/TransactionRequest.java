package com.example.financetracker.dto;

import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.TransactionType;
import com.example.financetracker.validation.ValidTransactionClassification;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@ValidTransactionClassification
public class TransactionRequest {
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", inclusive = false,
            message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction Type is required")
    private TransactionType type;

    private ExpenseCategoryType expenseCategory;

    private IncomeSourceType incomeSource;

    @NotNull(message = "Transaction Date is required")
    private LocalDate date;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ExpenseCategoryType getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategoryType expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public IncomeSourceType getIncomeSource() {
        return incomeSource;
    }

    public void setIncomeSource(IncomeSourceType incomeSource) {
        this.incomeSource = incomeSource;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
