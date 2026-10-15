package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.Transaction;

import java.util.List;

public interface TransactionService {
    Transaction saveTransaction(TransactionRequest transactionRequest);
    Transaction fetchTransaction(Long transactionId);
    List<Transaction> fetchTransactionList();
    Transaction updateTransaction(TransactionRequest transactionRequest, Long transactionId);
    void deleteTransactionById(Long transactionId);
}
