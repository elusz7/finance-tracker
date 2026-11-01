
import "./SummaryReport.css";

function SummaryReport({ report, isLoading }) {
  if (isLoading) {
    return <section className="report-section">
      <h2>Summary</h2>
      <p>Loading summary...</p>
    </section>;
  }

  if (!report) {
    return <section className="report-section">
      <h2>Summary</h2>
      <p>Select a date range and generate your report.</p>
    </section>;
  }

  const summaryItems = [
    {
      label: "Total Income",
      value: report.totalIncome,
      className: "income",
    },
    {
      label: "Total Expenses",
      value: report.totalExpenses,
      className: "expense",
    },
    {
      label: "Net Cash Flow",
      value: report.netCashFlow,
      className: report.netCashFlow >= 0 ? "income" : "expense",
    },
    {
      label: "Savings Rate",
      value: `${report.savingsRate}%`,
      className: report.savingsRate >= 0 ? "income" : "expense",
      isPercentage: true,
    },
  ];

  const formatCurrency = (amount) =>
    new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);

  return (
    <section className="report-section summary-report">
      <h2>Summary</h2>

      <div className="summary-report-grid">
        {summaryItems.map((item) => (
          <div className="summary-report-card" key={item.label}>
            <p>{item.label}</p>
            <h3 className={item.className}>
              {item.isPercentage ? item.value : formatCurrency(item.value)}
            </h3>
          </div>
        ))}
      </div>
    </section>
  );
}

export default SummaryReport;
