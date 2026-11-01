import "./SummaryCards.css";
import { useState, useEffect } from "react";

function SummaryCards({ refresh }) {
  const [dashboardSummary, setDashboardSummary] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  const formatCurrency = (amount) =>
    Math.abs(Number(amount)).toLocaleString("en-US", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

  useEffect(() => {
    const loadDashboard = async () => {
      setIsLoading(true);
      setErrorMessage("");

      try {
        const response = await fetch(
          "http://localhost:8080/api/transactions/reports/dashboard",
        );

        if (!response.ok) {
          throw new Error("Failed to load dashboard summary.");
        }

        const data = await response.json();
        setDashboardSummary(data);
      } catch (error) {
        setErrorMessage(`Error loading dashboard: ${error.message}`);
        setDashboardSummary(null);
      } finally {
        setIsLoading(false);
      }
    };

    loadDashboard();
  }, [refresh]);

  if (isLoading) {
    return <section className="summary">Loading dashboard...</section>;
  }

  if (errorMessage) {
    return <section className="summary">{errorMessage}</section>;
  }

  if (!dashboardSummary) {
    return <section className="summary">No dashboard data available.</section>;
  }

  return (
    <section className="summary">
      <div className="card">
        <h3>YTD Net Balance</h3>
        <p
          className={
            dashboardSummary.ytdBalance >= 0
              ? "transaction-income"
              : "transaction-expense"
          }
        >
          {dashboardSummary.ytdBalance >= 0 ? "+" : "−"}$
          {formatCurrency(dashboardSummary.ytdBalance)}
        </p>
      </div>

      <div className="card">
        <h3>YTD Income</h3>
        <p className="transaction-income">
          +${formatCurrency(dashboardSummary.ytdIncome)}
        </p>
      </div>

      <div className="card">
        <h3>YTD Expenses</h3>
        <p className="transaction-expense">
          −${formatCurrency(dashboardSummary.ytdExpenses)}
        </p>
      </div>
    </section>
  );
}

export default SummaryCards;
