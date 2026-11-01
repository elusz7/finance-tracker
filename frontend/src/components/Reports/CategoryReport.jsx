import {
  PieChart,
  Pie,
  Cell,
  Tooltip,
  ResponsiveContainer,
  Sector,
} from "recharts";

import "./CategoryReport.css";

const colors = [
  "#a78bfa", // Lavender
  "#facc15", // Bright yellow
  "#8b5cf6", // Violet
  "#fde047", // Light yellow
  "#7c3aed", // Deep purple
  "#eab308", // Golden yellow
  "#c4b5fd", // Light lavender
  "#fef08a", // Pale yellow
  "#6d28d9", // Dark violet
  "#ca8a04", // Dark gold
];

// Expands the currently hovered slice
function renderActiveShape(props) {
  const { cx, cy, innerRadius, outerRadius, startAngle, endAngle, fill } =
    props;

  return (
    <Sector
      cx={cx}
      cy={cy}
      innerRadius={innerRadius}
      outerRadius={outerRadius + 8}
      startAngle={startAngle}
      endAngle={endAngle}
      fill={fill}
    />
  );
}

// Custom floating tooltip
function CategoryTooltip({ active, payload }) {
  if (!active || !payload || !payload.length) {
    return null;
  }

  const item = payload[0].payload;

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);

  return (
    <div className="category-tooltip">
      <p className="category-tooltip-name">
        {item.category.replaceAll("_", " ")}
      </p>

      <p>
        <strong>{formatCurrency(item.amount)}</strong>
      </p>

      <p>{item.percentage.toFixed(1)}% of total expenses</p>
    </div>
  );
}

function CategoryReport({ report, isLoading }) {
  if (isLoading) {
    return (
      <section className="report-section">
        <h2>Spending by Category</h2>
        <p>Loading category report...</p>
      </section>
    );
  }

  if (!report || report.length === 0) {
    return (
      <section className="report-section">
        <h2>Spending by Category</h2>
        <p>No expense data available for this date range.</p>
      </section>
    );
  }

  const chartData = report.map((item, index) => ({
    category: item.expenseCategory,
    amount: Number(item.expenseAmount),
    percentage: Number(item.spendingPercentage),
    value: Number(item.expenseAmount),
    fill: colors[index % colors.length],
  }));

  return (
    <section className="report-section category-report">
      <h2>Spending by Category</h2>

      <div className="category-chart-content">
        <ResponsiveContainer width="100%" height={350}>
          <PieChart>
            <Pie
              data={chartData}
              dataKey="value"
              nameKey="category"
              cx="50%"
              cy="50%"
              innerRadius={85}
              outerRadius={125}
              paddingAngle={2}
              activeShape={renderActiveShape}
            >
              {chartData.map((item, index) => (
                <Cell key={item.category} fill={item.fill} />
              ))}
            </Pie>

            <Tooltip content={<CategoryTooltip />} cursor={false} />
          </PieChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}

export default CategoryReport;
