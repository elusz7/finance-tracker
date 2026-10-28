import "./SummaryCards.css";

function SummaryCards({ balance, totalIncome, totalExpenses }) {
  return (
    <section className="summary">
      <div className="card">
        <h3>Current Balance</h3>
        <p
          className={
            balance >= 0 ? "transaction-income" : "transaction-expense"
          }
        >
          {balance >= 0 ? "+" : "−"}$
          {Math.abs(balance).toLocaleString("en-US", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      </div>

      <div className="card">
        <h3>Income</h3>
        <p className="transaction-income">
          +$
          {totalIncome.toLocaleString("en-US", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      </div>

      <div className="card">
        <h3>Expenses</h3>
        <p className="transaction-expense">
          −$
          {totalExpenses.toLocaleString("en-US", {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2,
          })}
        </p>
      </div>
    </section>
  );
}

export default SummaryCards;