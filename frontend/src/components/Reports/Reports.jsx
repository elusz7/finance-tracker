import "./Reports.css";
import SummaryReport from "./SummaryReport";
import MonthlyReport from "./MonthlyReport";
import CategoryReport from "./CategoryReport";
import { useState, useEffect } from "react";

function Reports() {
  const today = new Date();

  const getMonthStart = () => {
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");

    return `${year}-${month}-01`;
  };

  const getTodayDate = () => {
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
  };

  const resetFilters = () => {
    setStartDate(getMonthStart());
    setEndDate(getTodayDate());
  };

  const [startDate, setStartDate] = useState(getMonthStart());
  const [endDate, setEndDate] = useState(getTodayDate());

  const [summaryReport, setSummaryReport] = useState(null);
  const [monthlyReport, setMonthlyReport] = useState([]);
  const [categoryReport, setCategoryReport] = useState([]);

  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleGenerateReports = async () => {
    setErrorMessage("");
    setIsLoading(true);

    const query = new URLSearchParams({
      startDate,
      endDate,
    });

    try {
      const [summaryResponse, monthlyResponse, categoryResponse] =
        await Promise.all([
          fetch(
            `http://localhost:8080/api/transactions/reports/summary?${query}`,
          ),
          fetch(
            `http://localhost:8080/api/transactions/reports/monthly?${query}`,
          ),
          fetch(
            `http://localhost:8080/api/transactions/reports/category?${query}`,
          ),
        ]);

      if (!summaryResponse.ok || !monthlyResponse.ok || !categoryResponse.ok) {
        throw new Error("Failed to load one or more reports.");
      }

      const [summaryData, monthlyData, categoryData] = await Promise.all([
        summaryResponse.json(),
        monthlyResponse.json(),
        categoryResponse.json(),
      ]);
      console.log("Monthly report data:", monthlyData);

      setSummaryReport(summaryData);
      setMonthlyReport(monthlyData);
      setCategoryReport(categoryData);
    } catch (error) {
      setErrorMessage(`Error generating reports: ${error.message}`);
      setSummaryReport(null);
      setMonthlyReport([]);
      setCategoryReport([]);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const loadInitialReports = async () => {
      await handleGenerateReports();
    };

    loadInitialReports();
  }, []);

  return (
    <div className="reports-page">
      <header className="reports-header">
        <h1>Reports</h1>
        <p>Analyze your income and expenses over time.</p>
      </header>

      <section className="report-date-range">
        <div className="report-filters input">
          <div className="filter-row label">
            <label>
              Start Date
              <input
                type="date"
                value={startDate}
                onChange={(event) => {
                  setStartDate(event.target.value);
                  if (event.target.value > endDate)
                    setEndDate(event.target.value);
                }}
              />
            </label>

            <label>
              End Date
              <input
                type="date"
                value={endDate}
                min={startDate || undefined}
                onChange={(event) => setEndDate(event.target.value)}
              />
            </label>

            <button
              type="button"
              className="button-cancel"
              onClick={resetFilters}
            >
              Reset Filters
            </button>

            <button
              type="button"
              onClick={handleGenerateReports}
              disabled={isLoading}
            >
              {isLoading ? "Generating..." : "Generate Reports"}
            </button>
          </div>

          {errorMessage && <p className="report-error">{errorMessage}</p>}
        </div>
      </section>

      <SummaryReport report={summaryReport} isLoading={isLoading} />
      <MonthlyReport report={monthlyReport} isLoading={isLoading} />
      <CategoryReport report={categoryReport} isLoading={isLoading} />
    </div>
  );
}

export default Reports;
