package com.example.financetracker.service;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
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

    private Transaction testExpenseTransaction;
    private Transaction testIncomeTransaction;
    private TransactionRequest testExpenseRequest;
    private TransactionRequest testIncomeRequest;

    @BeforeEach
    void setUp() {
        testExpenseTransaction = new Transaction(
                "Test transaction",
                new BigDecimal("25.50"),
                TransactionType.EXPENSE,
                ExpenseCategoryType.GROCERY,
                null,
                LocalDate.of(2026, 10, 1)
        );

        testIncomeTransaction = new Transaction(
                "Test Income",
                new BigDecimal("1500.00"),
                TransactionType.INCOME,
                null,
                IncomeSourceType.SALARY,
                LocalDate.of(2026, 10, 3)
        );

        testExpenseRequest = new TransactionRequest();
        testExpenseRequest.setDescription("Test transaction");
        testExpenseRequest.setAmount(new BigDecimal("25.50"));
        testExpenseRequest.setType(TransactionType.EXPENSE);
        testExpenseRequest.setExpenseCategory(ExpenseCategoryType.GROCERY);
        testExpenseRequest.setDate(LocalDate.of(2026, 10, 1));

        testIncomeRequest = new TransactionRequest();
        testIncomeRequest.setDescription("Test income");
        testIncomeRequest.setAmount(new BigDecimal("1500.00"));
        testIncomeRequest.setType(TransactionType.INCOME);
        testIncomeRequest.setIncomeSource(IncomeSourceType.SALARY);
        testIncomeRequest.setDate(LocalDate.of(2026, 10, 3));
    }

    @Test
    void saveTransactionThrowsWhenRequestIsNull() {
        assertThrows(
                NullPointerException.class,
                () -> transactionService.saveTransaction(null)
        );

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void saveExpenseMapsRequestToEntity() {
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        transactionService.saveTransaction(testExpenseRequest);

        ArgumentCaptor<Transaction> captor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(captor.capture());

        Transaction savedTransaction = captor.getValue();

        assertEquals("Test transaction", savedTransaction.getDescription());
        assertEquals(new BigDecimal("25.50"), savedTransaction.getAmount());
        assertEquals(TransactionType.EXPENSE, savedTransaction.getType());
        assertEquals(ExpenseCategoryType.GROCERY, savedTransaction.getExpenseCategory());
        assertEquals(LocalDate.of(2026, 10, 1), savedTransaction.getDate());
    }

    @Test
    void saveIncomeMapsRequestToEntity() {
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result = transactionService.saveTransaction(testIncomeRequest);

        assertEquals("Test income", result.getDescription());
        assertEquals(new BigDecimal("1500.00"), result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals(IncomeSourceType.SALARY, result.getIncomeSource());
        assertNull(result.getExpenseCategory());
        assertEquals(LocalDate.of(2026, 10, 3), result.getDate());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void fetchTransactionReturnsTransactionWhenFound() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testExpenseTransaction));

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
        List<Transaction> transactions = List.of(testExpenseTransaction);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.fetchTransactionList();

        assertEquals(1, result.size());
        assertEquals(testExpenseTransaction, result.getFirst());
        verify(transactionRepository).findAll();
    }

    @Test
    void updateTransactionThrowsWhenRequestIsNull() {
        when(transactionRepository.findById(1L))
                .thenReturn(Optional.of(testExpenseTransaction));

        assertThrows(
                NullPointerException.class,
                () -> transactionService.updateTransaction(null, 1L)
        );

        verify(transactionRepository).findById(1L);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void updateTransactionThrowsWhenNotFound() {
        when(transactionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                TransactionNotFoundException.class,
                () -> transactionService.updateTransaction(testExpenseRequest, 99L)
        );

        verify(transactionRepository).findById(99L);
    }

    @Test
    void updateTransactionDescriptionValid() {
        //set fields to null to isolate change
        clearExpenseRequestFields();
        // Arrange: change the description in the request
        testExpenseRequest.setDescription("Updated Test");

        mockSuccessfulUpdate(1L);

        // Act: call the service method
        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        // Assert: verify the description was updated
        assertEquals("Updated Test", result.getDescription());
        // Assert: verify no other fields were updated
        assertEquals(new BigDecimal("25.50"), result.getAmount());
        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(ExpenseCategoryType.GROCERY, result.getExpenseCategory());
        assertEquals(LocalDate.of(2026, 10, 1), result.getDate());

        // Verify that the repository saved the updated transaction
        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionDescriptionNull() {
        testExpenseRequest.setDescription(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getDescription(), result.getDescription());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionDescriptionEmpty() {
        clearExpenseRequestFields();
        testExpenseRequest.setDescription("");

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getDescription(), result.getDescription());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionAmountValid() {
        clearExpenseRequestFields();
        BigDecimal updated = new BigDecimal("3.50");
        testExpenseRequest.setAmount(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(updated, result.getAmount());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionAmountNull() {
        testExpenseRequest.setAmount(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionAmountZero() {
        clearExpenseRequestFields();
        testExpenseRequest.setAmount(BigDecimal.ZERO);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionAmountNegative() {
        clearExpenseRequestFields();
        BigDecimal updated = new BigDecimal("-3.50");
        testExpenseRequest.setAmount(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getAmount(), result.getAmount());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionTypeNull() {
        testExpenseRequest.setType(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getType(), result.getType());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateExpenseToIncomeWithNullSourcePreservesNullSource() {
        clearExpenseRequestFields();
        testExpenseRequest.setType(TransactionType.INCOME);

        mockSuccessfulUpdate(1L);

        Transaction result =
                transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(TransactionType.INCOME, result.getType());
        assertNull(result.getIncomeSource());
        assertNull(result.getExpenseCategory());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateIncomeToExpenseWithNullCategoryPreservesNullCategory() {
        testIncomeRequest.setType(TransactionType.EXPENSE);
        testIncomeRequest.setExpenseCategory(null);

        when(transactionRepository.findById(2L))
                .thenReturn(Optional.of(testIncomeTransaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result =
                transactionService.updateTransaction(testIncomeRequest, 2L);

        assertEquals(TransactionType.EXPENSE, result.getType());
        assertNull(result.getExpenseCategory());
        assertNull(result.getIncomeSource());

        verify(transactionRepository).save(testIncomeTransaction);
    }

    @Test
    void updateExpenseCategoryValid() {
        clearExpenseRequestFields();
        testExpenseRequest.setExpenseCategory(ExpenseCategoryType.EDUCATION);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(ExpenseCategoryType.EDUCATION, result.getExpenseCategory());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateExpenseCategoryNull() {
        testExpenseRequest.setExpenseCategory(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getExpenseCategory(), result.getExpenseCategory());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateIncomeSourceValid() {
        testIncomeRequest.setIncomeSource(IncomeSourceType.FREELANCE);

        when(transactionRepository.findById(2L))
                .thenReturn(Optional.of(testIncomeTransaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result =
                transactionService.updateTransaction(testIncomeRequest, 2L);

        assertEquals(IncomeSourceType.FREELANCE, result.getIncomeSource());
        assertNull(result.getExpenseCategory());

        verify(transactionRepository).save(testIncomeTransaction);
    }


    @Test
    void updateTransactionFromExpenseToIncomeClearsExpenseCategory() {
        clearExpenseRequestFields();
        testExpenseRequest.setType(TransactionType.INCOME);
        testExpenseRequest.setIncomeSource(IncomeSourceType.SALARY);

        mockSuccessfulUpdate(1L);

        Transaction result =
                transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals(IncomeSourceType.SALARY, result.getIncomeSource());
        assertNull(result.getExpenseCategory());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionFromIncomeToExpenseClearsIncomeSource() {
        clearExpenseRequestFields();
        testIncomeRequest.setType(TransactionType.EXPENSE);
        testIncomeRequest.setExpenseCategory(ExpenseCategoryType.GROCERY);

        when(transactionRepository.findById(2L))
                .thenReturn(Optional.of(testIncomeTransaction));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transaction result =
                transactionService.updateTransaction(testIncomeRequest, 2L);

        assertEquals(TransactionType.EXPENSE, result.getType());
        assertEquals(ExpenseCategoryType.GROCERY, result.getExpenseCategory());
        assertNull(result.getIncomeSource());

        verify(transactionRepository).save(testIncomeTransaction);
    }

    @Test
    void updateTransactionDateValid() {
        clearExpenseRequestFields();
        LocalDate updated = LocalDate.of(2026, 9, 28);
        testExpenseRequest.setDate(updated);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(updated, result.getDate());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void updateTransactionDateNull() {
        testExpenseRequest.setDate(null);

        mockSuccessfulUpdate(1L);

        Transaction result = transactionService.updateTransaction(testExpenseRequest, 1L);

        assertEquals(testExpenseTransaction.getDate(), result.getDate());

        verify(transactionRepository).save(testExpenseTransaction);
    }

    @Test
    void deleteTransactionIdNotFound() {
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
                .thenReturn(Optional.of(testExpenseTransaction));

        transactionService.deleteTransactionById(1L);

        verify(transactionRepository).findById(1L);
        verify(transactionRepository).delete(testExpenseTransaction);
    }

    private void mockSuccessfulUpdate(Long id) {
        // Tell Mockito that the transaction already exists
        when(transactionRepository.findById(id))
                .thenReturn(Optional.of(testExpenseTransaction));

        // Tell Mockito what to return when the updated transaction is saved
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private void clearExpenseRequestFields() {
        testExpenseRequest.setDescription(null);
        testExpenseRequest.setAmount(null);
        testExpenseRequest.setType(null);
        testExpenseRequest.setExpenseCategory(null);
        testExpenseRequest.setIncomeSource(null);
        testExpenseRequest.setDate(null);
    }
}