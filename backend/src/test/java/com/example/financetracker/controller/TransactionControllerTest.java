package com.example.financetracker.controller;

import com.example.financetracker.dto.TransactionRequest;
import com.example.financetracker.entity.CategoryType;
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

    private TransactionRequest testRequest;
    private Transaction testTransaction;

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
    void addTransactionSuccess() throws Exception {
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description")
                        .value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type")
                        .value(testTransaction.getType().toString()))
                .andExpect(jsonPath("$.category")
                        .value(testTransaction.getCategory().toString()))
                .andExpect(jsonPath("$.date")
                        .value(testTransaction.getDate().toString()));
    }

    @Test
    void addTransactionFailureMissingAmount() throws Exception {
        testRequest.setAmount(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureNegativeAmount() throws Exception {
        testRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureZeroAmount() throws Exception {
        testRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionSuccessMissingDescription() throws Exception {
        testRequest.setDescription(null);
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void addTransactionSuccessEmptyDescription() throws Exception {
        testRequest.setDescription("");
        when(testService.saveTransaction(any(TransactionRequest.class)))
                .thenReturn(testTransaction);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void addTransactionFailureMissingType() throws Exception {
        testRequest.setType(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureMissingCategory() throws Exception {
        testRequest.setCategory(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void addTransactionFailureMissingDate() throws Exception {
        testRequest.setDate(null);

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getTransactionSuccess() throws Exception {
        when(testService.fetchTransaction(1L))
                .thenReturn(testTransaction);
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.type")
                        .value(testTransaction.getType().toString()))
                .andExpect(jsonPath("$.category")
                        .value(testTransaction.getCategory().toString()))
                .andExpect(jsonPath("$.date")
                        .value(testTransaction.getDate().toString()));
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
        List<Transaction> transactions = List.of(testTransaction);

        when(testService.fetchTransactionList())
                .thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].description")
                        .value(testTransaction.getDescription()))
                .andExpect(jsonPath("$[0].category")
                        .value(testTransaction.getCategory().toString()));
    }

    @Test
    void updateTransactionSuccess() throws Exception {
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description")
                        .value(testTransaction.getDescription()))
                .andExpect(jsonPath("$.amount")
                        .value(testTransaction.getAmount().doubleValue()));
    }

    @Test
    void updateTransactionFailure() throws Exception {
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenThrow(new TransactionNotFoundException("Transaction not found"));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTransactionFailureMissingAmount() throws Exception {
        testRequest.setAmount(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureNegativeAmount() throws Exception {
        testRequest.setAmount(new BigDecimal("-50.00"));

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureZeroAmount() throws Exception {
        testRequest.setAmount(BigDecimal.ZERO);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionSuccessMissingDescription() throws Exception {
        testRequest.setDescription(null);
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTransactionSuccessEmptyDescription() throws Exception {
        testRequest.setDescription("");
        when(testService.updateTransaction(any(TransactionRequest.class), anyLong()))
                .thenReturn(testTransaction);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTransactionFailureMissingType() throws Exception {
        testRequest.setType(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureMissingCategory() throws Exception {
        testRequest.setCategory(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());


        verifyNoInteractions(testService);
    }

    @Test
    void updateTransactionFailureMissingDate() throws Exception {
        testRequest.setDate(null);

        mockMvc.perform(put("/api/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
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