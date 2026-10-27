package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.CategoryType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;
import com.example.financetracker.exception.TransactionNotFoundException;
import com.example.financetracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction testTransaction;
    private TransactionRequest testRequest;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction(
                "Test transaction",
                new BigDecimal("25.50"),
                TransactionType.EXPENSE,
                CategoryType.GROCERY,
                LocalDate.of(2026, 10, 1)
        );

        testRequest = new TransactionRequest();
        testRequest.setDescription("Test transaction");
        testRequest.setAmount(new BigDecimal("25.50"));
        testRequest.setType(TransactionType.EXPENSE);
        testRequest.setCategory(CategoryType.GROCERY);
        testRequest.setDate(LocalDate.of(2026, 10, 1));
    }

    @Test
    void saveTransactionMapsRequestToEntity() {
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.saveTransaction(testRequest);

        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();

        assertEquals("Test transaction", savedTransaction.getDescription());
        assertEquals(new BigDecimal("25.50"), savedTransaction.getAmount());
        assertEquals(TransactionType.EXPENSE, savedTransaction.getType());
        assertEquals(CategoryType.GROCERY, savedTransaction.getCategory());
        assertEquals(LocalDate.of(2026, 10, 1), savedTransaction.getDate());
    }

    @Test
    void fetchTransactionReturnsTransactionWhenFound() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        Transaction result = transactionService.fetchTransaction(1L);

        assertEquals("Test transaction", result.getDescription());
        verify(transactionRepository).findById(1L);
    }

    @Test
    void fetchTransactionThrowsWhenNotFound() {
        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.fetchTransaction(99L)
        );

        verify(transactionRepository).findById(99L);
    }

    @Test
    void fetchTransactionListReturnsAllTransactions() {
        List<Transaction> transactions = List.of(testTransaction);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.fetchTransactionList();

        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
        verify(transactionRepository).findAll();
    }

    @Test
    void updateTransactionThrowsWhenNotFound(){
        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.updateTransaction(testRequest, 99L)
        );

        verify(transactionRepository).findById(99L);
    }

    @Test
    void updateTransactionDescriptionValid() {
        //set fields to null to isolate change
        clearRequestFields();
        // Arrange: change the description in the request
        testRequest.setDescription("Updated Test");

        mockSuccessfulUpdate(1L);

        // Act: call the service method
        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        // Assert: verify the description was updated
        assertEquals("Updated Test", result.getDescription());
        // Assert: verify no other fields were updated
        assertEquals(new BigDecimal("25.50"), result.getAmount());
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(CategoryType.GROCERY, result.getCategory());
        assertEquals(LocalDate.of(2026, 10, 1), result.getDate());

        // Verify that the repository saved the updated transaction
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionDescriptionNull(){
        testRequest.setDescription(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getDescription(), result.getDescription());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionDescriptionEmpty(){
        clearRequestFields();
        testRequest.setDescription("");

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getDescription(), result.getDescription());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionAmountValid(){
        clearRequestFields();
        BigDecimal updated = new BigDecimal("3.50");
        testRequest.setAmount(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(updated, result.getAmount());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionAmountNull(){
        testRequest.setAmount(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionAmountZero(){
        clearRequestFields();
        testRequest.setAmount(BigDecimal.ZERO);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionAmountNegative(){
        clearRequestFields();
        BigDecimal updated = new BigDecimal("-3.50");
        testRequest.setAmount(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionTypeValid(){
        clearRequestFields();
        testRequest.setType(TransactionType.INCOME);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(TransactionType.INCOME, result.getType());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionTypeNull(){
        testRequest.setType(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getType(), result.getType());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionCategoryValid(){
        clearRequestFields();
        testRequest.setCategory(CategoryType.EDUCATION);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(CategoryType.EDUCATION, result.getCategory());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionCategoryNull(){
        testRequest.setCategory(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getCategory(), result.getCategory());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionDateValid(){
        clearRequestFields();
        LocalDate updated = LocalDate.of(2026, 9, 28);
        testRequest.setDate(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(updated, result.getDate());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void updateTransactionDateNull(){
        testRequest.setDate(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testRequest, 1L);

        assertEquals(testTransaction.getDate(), result.getDate());

        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void deleteTransactionIdNotFound(){
        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.deleteTransactionById(99L)
        );

        verify(transactionRepository).findById(99L);
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }

    @Test
    void deleteTransactionSuccess() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransactionById(1L);

        verify(transactionRepository).findById(1L);
        verify(transactionRepository).delete(testTransaction);
    }

    private void mockSuccessfulUpdate(Long id) {
        // Tell Mockito that the transaction already exists
        when(transactionRepository.findById(id))
                .thenReturn(Optional.of(testTransaction));

        // Tell Mockito what to return when the updated transaction is saved
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void clearRequestFields() {
        testRequest.setDescription(null);
        testRequest.setAmount(null);
        testRequest.setType(null);
        testRequest.setCategory(null);
        testRequest.setDate(null);
    }
}