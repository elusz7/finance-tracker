package com.example.financetracker.repository;

import com.example.financetracker.dto.CategoryResponse;
import com.example.financetracker.dto.MonthlyResponse;
import com.example.financetracker.dto.SummaryResponse;
import com.example.financetracker.entity.ExpenseCategoryType;
import com.example.financetracker.exception.QueryLoadingException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Repository
public class ReportRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final String summaryReportQuery;
    private final String monthlyReportQuery;
    private final String categoryReportQuery;

    public ReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

        try {
            this.summaryReportQuery = getQuery("queries/summary-report.sql");
            this.monthlyReportQuery = getQuery("queries/monthly-report.sql");
            this.categoryReportQuery = getQuery("queries/category-report.sql");
        }
        catch (IOException e){
            throw new QueryLoadingException(e.getMessage(), e);
        }
    }

    public SummaryResponse getSummaryReport(LocalDate start, LocalDate end){
        return jdbcTemplate.queryForObject(summaryReportQuery,
                new MapSqlParameterSource()
                        .addValue("start", start)
                        .addValue("end", end),
                (rs, rowNum) -> {
                    SummaryResponse response = new SummaryResponse();
                    response.setTotalIncome(rs.getBigDecimal("totalIncome"));
                    response.setTotalExpenses(rs.getBigDecimal("totalExpenses"));
                    return response;
                });
    }

    public List<MonthlyResponse> getMonthlyReport(LocalDate start, LocalDate end) {
        return jdbcTemplate.query(monthlyReportQuery,
                new MapSqlParameterSource()
                        .addValue("start", start)
                        .addValue("end", end),
                (rs, rowNum) -> {
                    MonthlyResponse response = new MonthlyResponse();
                    response.setYearMonth(YearMonth.from(rs.getDate("yearMonth").toLocalDate()));
                    response.setTotalIncome(rs.getBigDecimal("totalIncome"));
                    response.setTotalExpenses(rs.getBigDecimal("totalExpenses"));
                    return response;
                });
    }

    public List<CategoryResponse> getCategoryReport(LocalDate start, LocalDate end){
        return jdbcTemplate.query(categoryReportQuery,
                new MapSqlParameterSource()
                        .addValue("start", start)
                        .addValue("end", end),
                (rs, rowNum) -> {
                    CategoryResponse response = new CategoryResponse();
                    response.setExpenseAmount(rs.getBigDecimal("expense"));
                    response.setExpenseCategory(ExpenseCategoryType.valueOf(rs.getString("category")));
                    return response;
                });
    }

    private String getQuery(String sqlFile) throws IOException{
        ClassPathResource resource = new ClassPathResource(sqlFile);
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }
}
