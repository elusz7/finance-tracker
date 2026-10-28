package com.example.financetracker.controller;


import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.entity.IncomeSourceType;
import com.example.financetracker.entity.Transaction;
import com.example.financetracker.entity.TransactionType;
import com.example.financetracker.exception.TransactionNotFoundException;
import com.example.financetracker.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @MockitoBean
    private TransactionService testService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    private TransactionRequest testExpenseRequest;
    private TransactionRequest testIncomeRequest;
    private Transaction testExpenseTransaction;
    private Transaction testIncomeTransaction;

    @BeforeEach
    void setUp() {
        testExpenseTransaction = new Transaction(
                "Test Expense",
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
        testExpenseRequest.setDescription("Test expense");
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
    void addExpenseSuccess() throws Exception {
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description")
                        .value(testExpenseTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testExpenseTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type")
                        .value(testExpenseTransaction.getType().toString()))
                .andExpect(jsonPath("$.expenseCategory")
                        .value(testExpenseTransaction.getExpenseCategory().toString()))
                .andExpect(jsonPath("$.date")
                        .value(testExpenseTransaction.getDate().toString()));
    }

    @Test
    void addIncomeSuccess() throws Exception {
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testIncomeTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.incomeSource").value("SALARY"))
                .andExpect(jsonPath("$.expenseCategory").value(nullValue()))
                .andExpect(jsonPath("$.description").value("Test Income"));

        verify(testService).saveTransaction(any(TransactionRequest.class));
    }

    @Test
    void addTransactionFailureMissingAmount() throws Exception {
        testExpenseRequest.setAmount(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureNegativeAmount() throws Exception {
        testExpenseRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureZeroAmount() throws Exception {
        testExpenseRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionSuccessMissingDescription() throws Exception {
        testExpenseRequest.setDescription(null);
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void addTransactionSuccessEmptyDescription() throws Exception {
        testExpenseRequest.setDescription("");
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void addTransactionFailureMissingType() throws Exception {
        testExpenseRequest.setType(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addIncomeFailureMissingSource() throws Exception {
        testIncomeRequest.setIncomeSource(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }


    @Test
    void addExpenseFailureMissingCategory() throws Exception {
        testExpenseRequest.setExpenseCategory(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureMissingDate() throws Exception {
        testExpenseRequest.setDate(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }


    @Test
    void addIncomeFailureWithExpenseCategory() throws Exception {
        testIncomeRequest.setExpenseCategory(ExpenseCategoryType.GROCERY);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addExpenseFailureWithIncomeSource() throws Exception {
        testExpenseRequest.setIncomeSource(IncomeSourceType.SALARY);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }


    @Test
    void getExpenseSuccess() throws Exception {
        when(testService.fetchTransaction(1L))
                .thenReturn(testExpenseTransaction);
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testExpenseTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testExpenseTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type")
                        .value(testExpenseTransaction.getType().toString()))
                .andExpect(jsonPath("$.expenseCategory")
                        .value(testExpenseTransaction.getExpenseCategory().toString()))
                .andExpect(jsonPath("$.date")
                        .value(testExpenseTransaction.getDate().toString()));
    }


    @Test
    void getIncomeSuccess() throws Exception {
        when(testService.fetchTransaction(2L))
                .thenReturn(testIncomeTransaction);

        mockMvc.perform(get("/api/transactions/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testIncomeTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testIncomeTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.incomeSource").value("SALARY"))
                .andExpect(jsonPath("$.date")
                        .value(testIncomeTransaction.getDate().toString()));
    }

    @Test
    void getTransactionFailure() throws Exception {
        when(testService.fetchTransaction(99L))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(get("/api/transactions/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllTransactionsSuccess() throws Exception {
        List<Transaction> transactions = List.of(testExpenseTransaction);

        when(testService.fetchTransactionList())
                .thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description")
                        .value(testExpenseTransaction.getDescription()))
                .andExpect(jsonPath("$[0].amount")
                        .value(testExpenseTransaction.getAmount().doubleValue()));
    }

    @Test
    void updateExpenseSuccess() throws Exception {
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testExpenseTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testExpenseTransaction.getAmount().doubleValue()));
    }


    @Test
    void updateIncomeSuccess() throws Exception {
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testIncomeTransaction);

        mockMvc.perform(put("/api/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testIncomeTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testIncomeTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.incomeSource").value("SALARY"));
    }

    @Test
    void updateTransactionFailure() throws Exception {
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransactionFailureMissingAmount() throws Exception {
        testExpenseRequest.setAmount(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureNegativeAmount() throws Exception {
        testExpenseRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureZeroAmount() throws Exception {
        testExpenseRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionSuccessMissingDescription() throws Exception {
        testExpenseRequest.setDescription(null);
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTransactionSuccessEmptyDescription() throws Exception {
        testExpenseRequest.setDescription("");
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testExpenseTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTransactionFailureMissingType() throws Exception {
        testExpenseRequest.setType(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateExpenseFailureMissingCategory() throws Exception {
        testExpenseRequest.setExpenseCategory(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateIncomeFailureMissingSource() throws Exception {
        testIncomeRequest.setIncomeSource(null);

        mockMvc.perform(put("/api/transactions/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testIncomeRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureMissingDate() throws Exception {
        testExpenseRequest.setDate(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testExpenseRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void deleteTransactionSuccess() throws Exception {
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isNoContent());

        verify(testService).deleteTransactionById(1L);
    }

    @Test
    void deleteTransactionFailure() throws Exception {
        doThrow(new TransactionNotFoundException("Transaction not found"))
                .when(testService).deleteTransactionById(99L);

        mockMvc.perform(delete("/api/transactions/99"))
                .andExpect(status().isNotFound());

        verify(testService).deleteTransactionById(99L);
    }
}