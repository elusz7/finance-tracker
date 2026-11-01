package com.example.financetracker.dto;

import com.example.financetracker.entity.ExpenseCategoryType;

import java.math.BigDecimal;

public class CategoryResponse {
    private ExpenseCategoryType expenseCategory;
    private BigDecimal expenseAmount;
    private BigDecimal spendingPercentage;

    public BigDecimal getExpenseAmount() {
        return expenseAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public ExpenseCategoryType getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategoryType expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public BigDecimal getSpendingPercentage() {
        return spendingPercentage;
    }

    public void setSpendingPercentage(BigDecimal spendingPercentage) {
        this.spendingPercentage = spendingPercentage;
    }
}
