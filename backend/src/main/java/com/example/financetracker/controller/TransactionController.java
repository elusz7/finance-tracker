package com.example.financetracker.controller;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService){
        this.transactionService = transactionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Transaction addTransaction(@RequestBody @Valid TransactionRequest transactionRequest){
        return transactionService.saveTransaction(transactionRequest);
    }

    @GetMapping("/{id}")
    public Transaction getTransaction(@PathVariable Long id){
        return transactionService.fetchTransaction(id);
    }

    @GetMapping
    public List<Transaction> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) ExpenseCategoryType expenseCategory,
            @RequestParam(required = false) IncomeSourceType incomeSource,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        return transactionService.fetchFilteredTransactions(
                type,
                expenseCategory,
                incomeSource,
                startDate,
                endDate,
                minAmount,
                maxAmount
        );
    }

    @PutMapping("/{id}")
    public Transaction updateTransaction(@RequestBody @Valid TransactionRequest transactionRequest, @PathVariable Long id){
        return transactionService.updateTransaction(transactionRequest, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(@PathVariable Long id){
        transactionService.deleteTransactionById(id);
    }
}
