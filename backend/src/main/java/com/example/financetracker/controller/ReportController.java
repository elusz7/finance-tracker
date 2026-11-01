package com.example.financetracker.controller;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.DashboardResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;
import com.example.financetracker.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(path = "/api/transactions/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    public SummaryResponse getSummaryReport(@RequestParam("startDate") LocalDate start, @RequestParam("endDate") LocalDate end) {
        return reportService.getSummaryReport(start, end);
    }

    @GetMapping("/monthly")
    public List<MonthlyResponse> getMonthlyReport(@RequestParam("startDate") LocalDate start, @RequestParam("endDate") LocalDate end) {
        return reportService.getMonthlyReport(start, end);
    }

    @GetMapping("/category")
    public List<CategoryResponse> getCategoryReport(@RequestParam("startDate") LocalDate start, @RequestParam("endDate") LocalDate end) {
        return reportService.getCategoryReport(start, end);
    }

    @GetMapping("/dashboard")
    public DashboardResponse getDashboardReport() {
        return reportService.getDashboardReport();
    }
}
