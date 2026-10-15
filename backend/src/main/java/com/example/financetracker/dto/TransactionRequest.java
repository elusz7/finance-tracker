package com.example.financetracker.dto;

import com.example.financetracker.entity.CategoryType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {
    private String description;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0", inclusive = false, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction Type is required")
    private TransactionType type;

    @NotNull(message = "Category Type is required")
    private CategoryType category;

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

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
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
