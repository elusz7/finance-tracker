package com.example.financetracker.service;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.DashboardResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;
import com.example.financetracker.exception.InvalidDateRangeException;
import com.example.financetracker.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;

@Service
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;

    public ReportServiceImpl(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public SummaryResponse getSummaryReport(LocalDate start, LocalDate end) {
        validateDate(start, end);
        SummaryResponse response = reportRepository.getSummaryReport(start, end);

        BigDecimal expenses = response.getTotalExpenses();
        BigDecimal income = response.getTotalIncome();

        BigDecimal netCashFlow = income.subtract(expenses);
        BigDecimal savingRate;
        if (income.compareTo(BigDecimal.ZERO) != 0)
            savingRate = netCashFlow
                    .divide(income, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100.00"))
                    .setScale(2, RoundingMode.HALF_UP);
        else
            savingRate = BigDecimal.ZERO;

        response.setNetCashFlow(netCashFlow);
        response.setSavingsRate(savingRate);

        return response;
    }

    @Override
    public List<MonthlyResponse> getMonthlyReport(LocalDate start, LocalDate end) {
        validateDate(start, end);
        List<MonthlyResponse> responseList = reportRepository.getMonthlyReport(start, end);

        for (MonthlyResponse response : responseList){
            response.setNetCashFlow(response.getTotalIncome().subtract(response.getTotalExpenses()));
        }

        return responseList;
    }

    @Override
    public List<CategoryResponse> getCategoryReport(LocalDate start, LocalDate end) {
        validateDate(start, end);
        List<CategoryResponse> responseList = reportRepository.getCategoryReport(start, end);

        BigDecimal totalExpenses = responseList.stream()
                .map(CategoryResponse::getExpenseAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpenses.compareTo(BigDecimal.ZERO) > 0) {
            responseList.forEach(response -> {
                response.setSpendingPercentage(
                    response.getExpenseAmount()
                    .divide(totalExpenses, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP)
                );
            });
        }

        return responseList;
    }

    @Override
    public DashboardResponse getDashboardReport() {
        return reportRepository.getDashboardReport();
    }

    private void validateDate(LocalDate start, LocalDate end){
        if (start.isAfter(end)) {
            throw new InvalidDateRangeException("Start Date must before or equal to End Date");
        }
    }
}
