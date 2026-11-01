package com.example.financetracker.controller;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.DashboardResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.exception.InvalidDateRangeException;
import com.example.financetracker.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {
    final String validGetUrl = "/api/transactions/reports/%s?startDate=%s&endDate=%s";
    final String invalidStartDateFormatUrl = "/api/transactions/reports/%s?startDate=03/07/2026&endDate=%s";
    final String invalidEndDateFormatUrl = "/api/transactions/reports/%s?startDate=%s&endDate=10/07/2026";
    final String missingStartDateUrl = "/api/transactions/reports/%s?endDate=%s";
    final String missingEndDateUrl = "/api/transactions/reports/%s?startDate=%s";

    @MockitoBean
    private ReportService testService;
    @Autowired
    private MockMvc mockMvc;

    private SummaryResponse summaryResponse;
    private MonthlyResponse monthlyResponse;
    private CategoryResponse categoryResponse;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        summaryResponse = new SummaryResponse();
        summaryResponse.setTotalExpenses(new BigDecimal("10.00"));
        summaryResponse.setTotalIncome(new BigDecimal("20.00"));
        summaryResponse.setNetCashFlow(new BigDecimal("10.00"));
        summaryResponse.setSavingsRate(new BigDecimal("50.00"));

        monthlyResponse = new MonthlyResponse();
        monthlyResponse.setYearMonth(YearMonth.of(2026, 9));
        monthlyResponse.setTotalExpenses(new BigDecimal("10.00"));
        monthlyResponse.setTotalIncome(new BigDecimal("20.00"));
        monthlyResponse.setNetCashFlow(new BigDecimal("10.00"));

        categoryResponse = new CategoryResponse();
        categoryResponse.setExpenseCategory(ExpenseCategoryType.CHILDCARE);
        categoryResponse.setExpenseAmount(new BigDecimal("100.00"));
        categoryResponse.setSpendingPercentage(new BigDecimal("12.67"));

        startDate = LocalDate.of(2026, 9, 1);
        endDate = LocalDate.of(2026, 9, 30);
    }

    @Test
    void getSummaryReportSuccess() throws Exception {
        when(testService.getSummaryReport(startDate, endDate))
                .thenReturn(summaryResponse);

        String url = String.format(validGetUrl, "summary", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExpenses")
                        .value(summaryResponse.getTotalExpenses().doubleValue()))
                .andExpect(jsonPath("$.totalIncome")
                        .value(summaryResponse.getTotalIncome().doubleValue()))
                .andExpect(jsonPath("$.netCashFlow")
                        .value(summaryResponse.getNetCashFlow().doubleValue()))
                .andExpect(jsonPath("$.savingsRate")
                        .value(summaryResponse.getSavingsRate().doubleValue()));
    }

    @Test
    void getSummaryReportFailureInvalidStartDate() throws Exception {
        startDate = LocalDate.of(2026, 10, 15);

        when(testService.getSummaryReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "summary", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getSummaryReportFailureInvalidEndDate() throws Exception {
        endDate = LocalDate.of(2026, 8, 15);

        when(testService.getSummaryReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "summary", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getMonthlyReportSuccess() throws Exception {
        when(testService.getMonthlyReport(startDate, endDate))
                .thenReturn(List.of(monthlyResponse));

        String url = String.format(validGetUrl, "monthly", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].totalExpenses")
                        .value(monthlyResponse.getTotalExpenses().doubleValue()))
                .andExpect(jsonPath("$[0].totalIncome")
                        .value(monthlyResponse.getTotalIncome().doubleValue()))
                .andExpect(jsonPath("$[0].netCashFlow")
                        .value(monthlyResponse.getNetCashFlow().doubleValue()))
                .andExpect(jsonPath("$[0].yearMonth")
                        .value(monthlyResponse.getYearMonth().toString()));
    }

    @Test
    void getMonthlyReportFailureInvalidStartDate() throws Exception {
        startDate = LocalDate.of(2026, 10, 15);

        when(testService.getMonthlyReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "monthly", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getMonthlyReportFailureInvalidEndDate() throws Exception {
        endDate = LocalDate.of(2026, 8, 15);

        when(testService.getMonthlyReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "monthly", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getCategoryReportSuccess() throws Exception {
        when(testService.getCategoryReport(startDate, endDate))
                .thenReturn(List.of(categoryResponse));

        String url = String.format(validGetUrl, "category", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].expenseAmount")
                        .value(categoryResponse.getExpenseAmount().doubleValue()))
                .andExpect(jsonPath("$[0].expenseCategory")
                        .value(categoryResponse.getExpenseCategory().toString()))
                .andExpect(jsonPath("$[0].spendingPercentage")
                        .value(categoryResponse.getSpendingPercentage().doubleValue()));
    }

    @Test
    void getCategoryReportFailureInvalidStartDate() throws Exception {
        startDate = LocalDate.of(2026, 10, 15);

        when(testService.getCategoryReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "category", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getCategoryReportFailureInvalidEndDate() throws Exception {
        endDate = LocalDate.of(2026, 8, 15);

        when(testService.getCategoryReport(startDate, endDate))
                .thenThrow(new InvalidDateRangeException("Invalid date range"));

        String url = String.format(validGetUrl, "category", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid input: Invalid date range"));
    }

    @Test
    void getSummaryReportMissingStartDate() throws Exception {
        String url = String.format(missingStartDateUrl, "summary", endDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getSummaryReportInvalidStartDateFormat() throws Exception {
        String url = String.format(invalidStartDateFormatUrl, "summary", endDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getSummaryReportEmptyResults() throws Exception {
        summaryResponse.setSavingsRate(new BigDecimal("0.0"));
        summaryResponse.setNetCashFlow(new BigDecimal("0.0"));
        summaryResponse.setTotalIncome(new BigDecimal("0.0"));
        summaryResponse.setTotalExpenses(new BigDecimal("0.0"));

        when(testService.getSummaryReport(startDate, endDate))
                .thenReturn(summaryResponse);

        String url = String.format(validGetUrl, "summary", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalExpenses")
                        .value(summaryResponse.getTotalExpenses().doubleValue()))
                .andExpect(jsonPath("$.totalIncome")
                        .value(summaryResponse.getTotalIncome().doubleValue()))
                .andExpect(jsonPath("$.netCashFlow")
                        .value(summaryResponse.getNetCashFlow().doubleValue()))
                .andExpect(jsonPath("$.savingsRate")
                        .value(summaryResponse.getSavingsRate().doubleValue()));
    }

    @Test
    void getMonthlyReportMissingEndDate() throws Exception {
        String url = String.format(missingEndDateUrl, "monthly", startDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getMonthlyReportInvalidStartDateFormat() throws Exception {
        String url = String.format(invalidStartDateFormatUrl, "monthly", endDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getMonthlyReportNoTransactionsInRange() throws Exception {
        monthlyResponse.setYearMonth(YearMonth.of(2026, 9));
        monthlyResponse.setTotalExpenses(new BigDecimal("0.00"));
        monthlyResponse.setTotalIncome(new BigDecimal("0.00"));
        monthlyResponse.setNetCashFlow(new BigDecimal("0.00"));

        when(testService.getMonthlyReport(startDate, endDate))
                .thenReturn(List.of(monthlyResponse));

        String url = String.format(validGetUrl, "monthly", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].totalExpenses")
                        .value(monthlyResponse.getTotalExpenses().doubleValue()))
                .andExpect(jsonPath("$[0].totalIncome")
                        .value(monthlyResponse.getTotalIncome().doubleValue()))
                .andExpect(jsonPath("$[0].netCashFlow")
                        .value(monthlyResponse.getNetCashFlow().doubleValue()))
                .andExpect(jsonPath("$[0].yearMonth")
                        .value(monthlyResponse.getYearMonth().toString()));
    }

    @Test
    void getCategoryReportMissingStartDate() throws Exception {
        String url = String.format(missingStartDateUrl, "category", endDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getCategoryReportInvalidEndDateFormat() throws Exception {
        String url = String.format(invalidEndDateFormatUrl, "category", startDate);

        mockMvc.perform(get(url))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(testService);
    }

    @Test
    void getCategoryReportNoExpensesInRange() throws Exception {
        when(testService.getCategoryReport(startDate, endDate))
                .thenReturn(List.of());

        String url = String.format(validGetUrl, "category", startDate, endDate);
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getDashboardSuccess() throws Exception {
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setYtdIncome(new BigDecimal("500.00"));
        dashboardResponse.setYtdExpenses(new BigDecimal("250.00"));
        dashboardResponse.setYtdBalance(new BigDecimal("250.00"));

        when(testService.getDashboardReport())
                .thenReturn(dashboardResponse);

        mockMvc.perform(get("/api/transactions/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ytdIncome")
                        .value(dashboardResponse.getYtdIncome().doubleValue()))
                .andExpect(jsonPath("$.ytdExpenses")
                        .value(dashboardResponse.getYtdExpenses().doubleValue()))
                .andExpect(jsonPath("$.ytdBalance")
                        .value(dashboardResponse.getYtdBalance().doubleValue()));
    }

    @Test
    void getDashboardSuccessNoTransactionsFound() throws Exception {
        DashboardResponse dashboardResponse = new DashboardResponse();
        dashboardResponse.setYtdIncome(BigDecimal.ZERO);
        dashboardResponse.setYtdExpenses(BigDecimal.ZERO);
        dashboardResponse.setYtdBalance(BigDecimal.ZERO);

        when(testService.getDashboardReport())
                .thenReturn(dashboardResponse);

        mockMvc.perform(get("/api/transactions/reports/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ytdIncome")
                        .value(dashboardResponse.getYtdIncome().doubleValue()))
                .andExpect(jsonPath("$.ytdExpenses")
                        .value(dashboardResponse.getYtdExpenses().doubleValue()))
                .andExpect(jsonPath("$.ytdBalance")
                        .value(dashboardResponse.getYtdBalance().doubleValue()));
    }

    //void getDashboardFailure -- shouldn't occur bc either values will be calculated or will get 0's?
}