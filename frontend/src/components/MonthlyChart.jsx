import {
  BarChart,
  Bar,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

import "./MonthlyChart.css";
import { useState, useEffect } from "react";

function MonthlyChart({ refresh }) {
  const [monthlyChart, setMonthlyChart] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const currentYear = new Date().getFullYear();

  const formatMonth = (yearMonth) => {
    const [year, month] = yearMonth.split("-").map(Number);

    return new Date(year, month - 1, 1).toLocaleDateString("en-US", {
      month: "short",
    });
  };

  const today = new Date();

  const getYearStart = () => {
    const year = today.getFullYear();

    return `${year}-01-01`;
  };

  const getTodayDate = () => {
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
  };

  const monthlyData = (monthlyChart || []).map((item) => ({
    month: formatMonth(item.yearMonth),
    Income: Number(item.totalIncome),
    Expenses: Number(item.totalExpenses),
  }));

  const formatAxisAmount = (amount) => {
    const value = Number(amount);

    if (Math.abs(value) >= 1_000_000) {
      return `$${(value / 1_000_000).toLocaleString("en-US", {
        maximumFractionDigits: 1,
      })}M`;
    }

    if (Math.abs(value) >= 1_000) {
      return `$${(value / 1_000).toLocaleString("en-US", {
        maximumFractionDigits: 1,
      })}K`;
    }

    return `$${value.toLocaleString("en-US", {
      maximumFractionDigits: 0,
    })}`;
  };

  const formatTooltipAmount = (amount) =>
    `$${Number(amount).toLocaleString("en-US", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;

  useEffect(() => {
    const loadMonthlyChart = async () => {
      setIsLoading(true);
      setErrorMessage("");

      const query = new URLSearchParams({
        startDate: getYearStart(),
        endDate: getTodayDate(),
      });

      try {
        const response = await fetch(
          `http://localhost:8080/api/transactions/reports/monthly?${query}`,
        );

        if (!response.ok) {
          throw new Error("Failed to load monthly chart data.");
        }

        const data = await response.json();
        setMonthlyChart(data);
      } catch (error) {
        setErrorMessage(`Error loading monthly chart: ${error.message}`);
        setMonthlyChart(null);
      } finally {
        setIsLoading(false);
      }
    };

    loadMonthlyChart();
  }, [refresh]);

  if (isLoading) {
    return <section className="monthly-chart">Loading chart...</section>;
  }

  if (errorMessage) {
    return <section className="monthly-chart">{errorMessage}</section>;
  }

  if (!monthlyChart) {
    return (
      <section className="monthly-chart">No monthly data available.</section>
    );
  }

  return (
    <section className="monthly-chart">
      <div className="monthly-chart-header">
        <h2>Monthly Income vs. Expenses</h2>
        <p>{currentYear} · Year to date</p>
      </div>

      <div className="monthly-chart-content">
        {monthlyData.length === 0 ? (
          <p>No monthly data available.</p>
        ) : (
          <ResponsiveContainer width="100%" height={320}>
            <BarChart
              data={monthlyData}
              margin={{ top: 10, right: 12, left: 12, bottom: 5 }}
            >
              <CartesianGrid
                strokeDasharray="3 3"
                stroke="#334155"
                vertical={false}
              />

              <XAxis
                dataKey="month"
                stroke="#94a3b8"
                tickLine={false}
                axisLine={false}
                tick={{ fontSize: 12 }}
                tickMargin={10}
                interval={0}
              />

              <YAxis
                stroke="#94a3b8"
                tickLine={false}
                axisLine={false}
                tickFormatter={formatAxisAmount}
                width={80}
                tick={{ fontSize: 12 }}
                allowDecimals={false}
              />

              <Tooltip
                formatter={(value, name) => [formatTooltipAmount(value), name]}
                contentStyle={{
                  backgroundColor: "#1b263b",
                  border: "1px solid #475569",
                  borderRadius: "8px",
                  color: "#f1f5f9",
                  padding: "10px 14px",
                }}
                labelStyle={{
                  color: "#f1f5f9",
                  fontWeight: "bold",
                  marginBottom: "6px",
                }}
                itemStyle={{
                  paddingTop: "2px",
                  paddingBottom: "2px",
                }}
                cursor={{ fill: "rgba(148, 163, 184, 0.08)" }}
              />

              <Legend />

              <Bar dataKey="Income" fill="#38bdf8" radius={[4, 4, 0, 0]} />

              <Bar dataKey="Expenses" fill="#fb923c" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        )}
      </div>
    </section>
  );
}

export default MonthlyChart;
