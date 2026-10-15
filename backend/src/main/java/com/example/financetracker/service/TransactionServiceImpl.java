package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.exception.TransactionNotFoundException;
import com.example.financetracker.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository){
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Transaction saveTransaction(TransactionRequest transactionRequest){
        Transaction transaction = new Transaction(
                transactionRequest.getDescription(),
                transactionRequest.getAmount(),
                transactionRequest.getType(),
                transactionRequest.getCategory(),
                transactionRequest.getDate());

        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction fetchTransaction(Long transactionId){
        return transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFoundException(String.format("Fetch Transaction Error. Id: %s", transactionId)));
    }

    @Override
    public List<Transaction> fetchTransactionList() {
        return transactionRepository.findAll();
    }

    @Override
    public Transaction updateTransaction(TransactionRequest transactionRequest, Long transactionId) {
        //Find Existing Transaction
        Transaction dbTransaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFoundException(String.format("Update Transaction Error. Id: %s", transactionId)));

        //update fields, check that field is neither null nor empty
        if (Objects.nonNull(transactionRequest.getDescription()) && !"".equalsIgnoreCase((transactionRequest.getDescription())))
            dbTransaction.setDescription(transactionRequest.getDescription());
        if (Objects.nonNull(transactionRequest.getAmount()) && transactionRequest.getAmount().compareTo(BigDecimal.ZERO) > 0)
            dbTransaction.setAmount(transactionRequest.getAmount());
        if (Objects.nonNull(transactionRequest.getType()))
            dbTransaction.setType(transactionRequest.getType());
        if (Objects.nonNull(transactionRequest.getCategory()))
            dbTransaction.setCategory(transactionRequest.getCategory());
        if (Objects.nonNull(transactionRequest.getDate()))
            dbTransaction.setDate((transactionRequest.getDate()));

        //if dbTransaction isn't null, return the updated transaction
        return transactionRepository.save(dbTransaction);
    }

    @Override
    public void deleteTransactionById(Long transactionId) {
        Transaction dbTransaction = transactionRepository.findById(transactionId).orElseThrow(
                () -> new TransactionNotFoundException(String.format("Deletion Transaction Error. Id: %s", transactionId)));

        transactionRepository.delete(dbTransaction);
    }
}
