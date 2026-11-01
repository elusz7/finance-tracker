package com.example.financetracker.dto;

import java.math.BigDecimal;

public class DashboardResponse {
    private BigDecimal ytdExpenses;
    private BigDecimal ytdIncome;
    private BigDecimal ytdBalance;

    public BigDecimal getYtdExpenses() {
        return ytdExpenses;
    }

    public void setYtdExpenses(BigDecimal ytdExpenses) {
        this.ytdExpenses = ytdExpenses;
    }

    public BigDecimal getYtdIncome() {
        return ytdIncome;
    }

    public void setYtdIncome(BigDecimal ytdIncome) {
        this.ytdIncome = ytdIncome;
    }

    public BigDecimal getYtdBalance() {
        return ytdBalance;
    }

    public void setYtdBalance(BigDecimal ytdBalance) {
        this.ytdBalance = ytdBalance;
    }
}
