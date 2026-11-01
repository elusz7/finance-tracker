package com.example.financetracker.service;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.exception.InvalidDateRangeException;
import com.example.financetracker.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @Mock
    private ReportRepository testRepository;

    @InjectMocks
    private ReportServiceImpl testService;

    private SummaryResponse summaryResponse;
    private MonthlyResponse monthlyResponse;
    private CategoryResponse categoryResponse;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate invalidStartDate;
    private LocalDate invalidEndDate;
    private BigDecimal expectedNetCashFlow;
    private BigDecimal expectedSavingsRate;
    private BigDecimal expectedSpendingPercentage;

    @BeforeEach
    void setUp() {
        summaryResponse = new SummaryResponse();
        summaryResponse.setTotalExpenses(new BigDecimal("10.00"));
        summaryResponse.setTotalIncome(new BigDecimal("20.00"));

        monthlyResponse = new MonthlyResponse();
        monthlyResponse.setYearMonth(YearMonth.of(2026, 9));
        monthlyResponse.setTotalExpenses(new BigDecimal("10.00"));
        monthlyResponse.setTotalIncome(new BigDecimal("20.00"));

        categoryResponse = new CategoryResponse();
        categoryResponse.setExpenseCategory(ExpenseCategoryType.CHILDCARE);
        categoryResponse.setExpenseAmount(new BigDecimal("100.00"));

        startDate = LocalDate.of(2026, 9, 1);
        invalidStartDate = LocalDate.of(2026, 10, 15);
        endDate = LocalDate.of(2026, 9, 30);
        invalidEndDate = LocalDate.of(2026, 8, 15);

        expectedNetCashFlow = new BigDecimal("10.00");
        expectedSavingsRate = new BigDecimal("50.00");
        expectedSpendingPercentage = new BigDecimal("100.00");
    }

    @Test
    void getSummaryReportSuccess() {
        when(testRepository.getSummaryReport(startDate, endDate))
                .thenReturn(summaryResponse);

        SummaryResponse result = testService.getSummaryReport(startDate, endDate);

        assertEquals(summaryResponse.getTotalIncome(), result.getTotalIncome());
        assertEquals(summaryResponse.getTotalExpenses(), result.getTotalExpenses());
        assertEquals(expectedNetCashFlow, result.getNetCashFlow());
        assertEquals(expectedSavingsRate, result.getSavingsRate());

        verify(testRepository).getSummaryReport(startDate, endDate);
    }

    @Test
    void getSummaryReportSuccessStartDateEqualsEndDate() {
        when(testRepository.getSummaryReport(startDate, startDate))
                .thenReturn(summaryResponse);

        SummaryResponse result = testService.getSummaryReport(startDate, startDate);

        assertEquals(summaryResponse.getTotalIncome(), result.getTotalIncome());
        assertEquals(summaryResponse.getTotalExpenses(), result.getTotalExpenses());
        assertEquals(expectedNetCashFlow, result.getNetCashFlow());
        assertEquals(expectedSavingsRate, result.getSavingsRate());

        verify(testRepository).getSummaryReport(startDate, startDate);
    }

    @Test
    void getSummaryReportFailureInvalidStartDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getSummaryReport(invalidStartDate, endDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getSummaryReportFailureInvalidEndDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getSummaryReport(startDate, invalidEndDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getSummaryReportSuccessNoResultsFound() {
        summaryResponse.setTotalExpenses(BigDecimal.ZERO);
        summaryResponse.setTotalIncome(BigDecimal.ZERO);

        when(testRepository.getSummaryReport(startDate, endDate))
                .thenReturn(summaryResponse);

        SummaryResponse result = testService.getSummaryReport(startDate, endDate);

        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, result.getNetCashFlow());
        assertEquals(BigDecimal.ZERO, result.getSavingsRate());

        verify(testRepository).getSummaryReport(startDate, endDate);
    }

    @Test
    void getSummaryReportSuccessNegativeNetCashFlow() {
        summaryResponse.setTotalIncome(new BigDecimal("10.00"));
        summaryResponse.setTotalExpenses(new BigDecimal("20.00"));
        expectedNetCashFlow = new BigDecimal("-10.00");
        expectedSavingsRate = new BigDecimal("-100.00");

        when(testRepository.getSummaryReport(startDate, endDate))
                .thenReturn(summaryResponse);

        SummaryResponse result = testService.getSummaryReport(startDate, endDate);

        assertEquals(summaryResponse.getTotalIncome(), result.getTotalIncome());
        assertEquals(summaryResponse.getTotalExpenses(), result.getTotalExpenses());
        assertEquals(expectedNetCashFlow, result.getNetCashFlow());
        assertEquals(expectedSavingsRate, result.getSavingsRate());

        verify(testRepository).getSummaryReport(startDate, endDate);
    }

    @Test
    void getMonthlyReportSuccess() {
        when(testRepository.getMonthlyReport(startDate, endDate))
                .thenReturn(List.of(monthlyResponse));

        List<MonthlyResponse> resultList = testService.getMonthlyReport(startDate, endDate);
        assertEquals(1, resultList.size());

        MonthlyResponse result = resultList.getFirst();
        assertEquals(monthlyResponse.getTotalIncome(), result.getTotalIncome());
        assertEquals(monthlyResponse.getTotalExpenses(), result.getTotalExpenses());
        assertEquals(expectedNetCashFlow, result.getNetCashFlow());
        assertEquals(monthlyResponse.getYearMonth(), result.getYearMonth());

        verify(testRepository).getMonthlyReport(startDate, endDate);
    }

    @Test
    void getMonthlyReportInvalidStartDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getMonthlyReport(invalidStartDate, endDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getMonthlyReportInvalidEndDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getMonthlyReport(startDate, invalidEndDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getMonthlyReportSuccessNoResultsFound() {
        monthlyResponse.setTotalExpenses(BigDecimal.ZERO);
        monthlyResponse.setTotalIncome(BigDecimal.ZERO);

        when(testRepository.getMonthlyReport(startDate, endDate))
                .thenReturn(List.of(monthlyResponse));

        List<MonthlyResponse> resultList = testService.getMonthlyReport(startDate, endDate);
        assertEquals(1, resultList.size());

        MonthlyResponse result = resultList.getFirst();
        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpenses());
        assertEquals(BigDecimal.ZERO, result.getNetCashFlow());
        assertEquals(monthlyResponse.getYearMonth(), result.getYearMonth());

        verify(testRepository).getMonthlyReport(startDate, endDate);
    }

    @Test
    void getMonthlyReportSuccessNegativeNetCashFlow() {
        monthlyResponse.setTotalIncome(new BigDecimal("10.00"));
        monthlyResponse.setTotalExpenses(new BigDecimal("20.00"));
        expectedNetCashFlow = new BigDecimal("-10.00");

        when(testRepository.getMonthlyReport(startDate, endDate))
                .thenReturn(List.of(monthlyResponse));

        List<MonthlyResponse> resultList = testService.getMonthlyReport(startDate, endDate);
        assertEquals(1, resultList.size());

        MonthlyResponse result = resultList.getFirst();
        assertEquals(monthlyResponse.getTotalIncome(), result.getTotalIncome());
        assertEquals(monthlyResponse.getTotalExpenses(), result.getTotalExpenses());
        assertEquals(expectedNetCashFlow, result.getNetCashFlow());
        assertEquals(monthlyResponse.getYearMonth(), result.getYearMonth());

        verify(testRepository).getMonthlyReport(startDate, endDate);
    }

    @Test
    void getCategoryReportSuccess() {
        when(testRepository.getCategoryReport(startDate, endDate))
                .thenReturn(List.of(categoryResponse));

        List<CategoryResponse> resultList = testService.getCategoryReport(startDate, endDate);
        assertEquals(1, resultList.size());

        CategoryResponse result = resultList.getFirst();
        assertEquals(categoryResponse.getExpenseCategory(), result.getExpenseCategory());
        assertEquals(categoryResponse.getExpenseAmount(), result.getExpenseAmount());
        assertEquals(expectedSpendingPercentage, result.getSpendingPercentage());

        verify(testRepository).getCategoryReport(startDate, endDate);
    }

    @Test
    void getCategoryReportInvalidStartDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getCategoryReport(invalidStartDate, endDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getCategoryReportInvalidEndDate() {
        assertThrows(
                InvalidDateRangeException.class,
                () -> {
                    testService.getCategoryReport(startDate, invalidEndDate);
                }
        );

        verifyNoInteractions(testRepository);
    }

    @Test
    void getCategoryReportNoResultsFound() {
        when(testRepository.getCategoryReport(startDate, endDate))
                .thenReturn(List.of());

        List<CategoryResponse> resultList = testService.getCategoryReport(startDate, endDate);
        assertEquals(0, resultList.size());

        verify(testRepository).getCategoryReport(startDate, endDate);
    }

    @Test
    void getCategoryReportSuccessMultipleResults() {
        categoryResponse.setExpenseAmount(new BigDecimal("62.31"));
        CategoryResponse secondResponse = new CategoryResponse();
        secondResponse.setExpenseCategory(ExpenseCategoryType.DINING);
        secondResponse.setExpenseAmount(new BigDecimal("12.97"));

        when(testRepository.getCategoryReport(startDate, endDate))
                .thenReturn(List.of(categoryResponse, secondResponse));

        List<CategoryResponse> resultList = testService.getCategoryReport(startDate, endDate);
        assertEquals(2, resultList.size());

        CategoryResponse result = resultList.getFirst();
        assertEquals(categoryResponse.getExpenseCategory(), result.getExpenseCategory());
        assertEquals(categoryResponse.getExpenseAmount(), result.getExpenseAmount());
        assertEquals(new BigDecimal("82.77"), result.getSpendingPercentage());

        result = resultList.getLast();
        assertEquals(secondResponse.getExpenseCategory(), result.getExpenseCategory());
        assertEquals(secondResponse.getExpenseAmount(), result.getExpenseAmount());
        assertEquals(new BigDecimal("17.23"), result.getSpendingPercentage());

        verify(testRepository).getCategoryReport(startDate, endDate);
    }
}
