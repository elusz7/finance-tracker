package com.example.financetracker.entity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;  //any notes about the transaction

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private ExpenseCategoryType expenseCategory;

    @Enumerated(EnumType.STRING)
    private IncomeSourceType incomeSource;

    @Column(nullable = false)
    private LocalDate date;

    protected Transaction() {}

    public Transaction(String description, BigDecimal amount, TransactionType type, ExpenseCategoryType expenseCategory, IncomeSourceType incomeSource, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.expenseCategory = expenseCategory;
        this.incomeSource = incomeSource;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

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

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public ExpenseCategoryType getExpenseCategory(){
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategoryType expenseCategory){
        this.expenseCategory = expenseCategory;
    }

    public IncomeSourceType getIncomeSource() { return incomeSource; }

    public void setIncomeSource(IncomeSourceType incomeSource) { this.incomeSource = incomeSource; }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
