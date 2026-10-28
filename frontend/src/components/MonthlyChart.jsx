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

function MonthlyChart({ transactions }) {
  const now = new Date();
  const currentYear = now.getFullYear();
  const currentMonth = now.getMonth();

  const monthNames = [
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec",
  ];

  const monthlyData = monthNames
    .slice(0, currentMonth + 1)
    .map((month, index) => {
      const monthTransactions = transactions.filter((transaction) => {
        const date = new Date(`${transaction.date}T00:00:00`);

        return date.getFullYear() === currentYear && date.getMonth() === index;
      });

      const income = monthTransactions
        .filter((transaction) => transaction.type === "INCOME")
        .reduce((total, transaction) => total + Number(transaction.amount), 0);

      const expenses = monthTransactions
        .filter((transaction) => transaction.type === "EXPENSE")
        .reduce((total, transaction) => total + Number(transaction.amount), 0);

      return {
        month,
        Income: income,
        Expenses: expenses,
      };
    });

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
    <section className="monthly-chart">
      <div className="monthly-chart-header">
        <h2>Monthly Income vs. Expenses</h2>
        <p>{currentYear} · Year to date</p>
      </div>

      <div className="monthly-chart-content">
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
      </div>
    </section>
  );
}

export default MonthlyChart;
