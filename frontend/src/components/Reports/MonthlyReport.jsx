import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

import "./MonthlyReport.css";

function MonthlyReport({ report, isLoading }) {
  if (isLoading) {
    return (
      <section className="report-section">
        <h2>Monthly Report</h2>
        <p>Loading monthly report...</p>
      </section>
    );
  }

  if (!report || report.length === 0) {
    return (
      <section className="report-section">
        <h2>Monthly Report</h2>
        <p>No monthly data available for this date range.</p>
      </section>
    );
  }

  const monthlyData = report.map((item) => ({
    month: item.yearMonth,
    Income: Number(item.totalIncome),
    Expenses: Number(item.totalExpenses),
    NetCashFlow: Number(item.netCashFlow),
  }));

  const formatMonth = (yearMonth) => {
    const [year, month] = yearMonth.split("-").map(Number);

    return new Date(year, month - 1, 1).toLocaleDateString("en-US", {
      month: "short",
      year: "numeric",
    });
  };

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

  return (
    <section className="report-section monthly-report">
      <div className="monthly-chart-header">
        <h2>Monthly Income, Expenses & Net Cash Flow</h2>
      </div>

      <div className="monthly-chart-content">
        <ResponsiveContainer width="100%" height={350}>
          <LineChart
            data={monthlyData}
            margin={{ top: 10, right: 20, left: 12, bottom: 5 }}
          >
            <CartesianGrid
              strokeDasharray="3 3"
              stroke="#334155"
              vertical={false}
            />

            <XAxis
              dataKey="month"
              tickFormatter={formatMonth}
              stroke="#94a3b8"
              tickLine={false}
              axisLine={false}
              tick={{ fontSize: 12 }}
              tickMargin={10}
            />

            <YAxis
              stroke="#94a3b8"
              tickLine={false}
              axisLine={false}
              tickFormatter={formatAxisAmount}
              width={80}
              tick={{ fontSize: 12 }}
            />

            <Tooltip
              labelFormatter={formatMonth}
              formatter={(value, name) => [
                formatTooltipAmount(value),
                name === "NetCashFlow" ? "Net Cash Flow" : name,
              ]}
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
            />

            <Legend />

            <Line
              type="monotone"
              dataKey="Income"
              stroke="#38bdf8"
              strokeWidth={3}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />

            <Line
              type="monotone"
              dataKey="Expenses"
              stroke="#fb923c"
              strokeWidth={3}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />

            <Line
              type="monotone"
              dataKey="NetCashFlow"
              name="Net Cash Flow"
              stroke="#a78bfa"
              strokeWidth={3}
              dot={{ r: 4 }}
              activeDot={{ r: 6 }}
            />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export default MonthlyReport;