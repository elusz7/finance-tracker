package com.example.financetracker.service;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.DashboardResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    SummaryResponse getSummaryReport(LocalDate start, LocalDate end);
    List<MonthlyResponse> getMonthlyReport(LocalDate start, LocalDate end);
    List<CategoryResponse> getCategoryReport(LocalDate start, LocalDate end);
    DashboardResponse getDashboardReport();
}
