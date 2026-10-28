import "./SummaryCards.css";

function SummaryCards({ ytdBalance, ytdIncome, ytdExpenses }) {
  const formatCurrency = (amount) =>
    Math.abs(amount).toLocaleString("en-US", {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    });

  return (
    <section className="summary">
      <div className="card">
        <h3>YTD Net Balance</h3>
        <p
          className={
            ytdBalance >= 0 ? "transaction-income" : "transaction-expense"
          }
        >
          {ytdBalance >= 0 ? "+" : "−"}${formatCurrency(ytdBalance)}
        </p>
      </div>

      <div className="card">
        <h3>YTD Income</h3>
        <p className="transaction-income">+${formatCurrency(ytdIncome)}</p>
      </div>

      <div className="card">
        <h3>YTD Expenses</h3>
        <p className="transaction-expense">−${formatCurrency(ytdExpenses)}</p>
      </div>
    </section>
  );
}

export default SummaryCards;
