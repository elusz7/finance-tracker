import "./RecentTransactions.css";
import TransactionList from "./TransactionList";

function RecentTransactions({
  transactions,
  selectedCategory,
  setSelectedCategory,
}) {
  return (
    <section className="transactions">
      <div className="transactions-header">
        <h2>Recent Transactions</h2>

        <div className="transaction-filter">
          <label htmlFor="recent-category-filter">Category:</label>

          <select
            id="recent-category-filter"
            value={selectedCategory}
            onChange={(event) => setSelectedCategory(event.target.value)}
          >
            <option value="ALL">All Categories</option>
            <option value="HOUSING">Housing</option>
            <option value="UTILITIES">Utilities</option>
            <option value="SUBSCRIPTION">Subscription</option>
            <option value="GROCERY">Grocery</option>
            <option value="DINING">Dining</option>
            <option value="ENTERTAINMENT">Entertainment</option>
            <option value="TRANSPORTATION">Transportation</option>
            <option value="HEALTHCARE">Healthcare</option>
            <option value="TRAVEL">Travel</option>
            <option value="SHOPPING">Shopping</option>
            <option value="PERSONAL_CARE">Personal Care</option>
            <option value="EDUCATION">Education</option>
            <option value="OTHER">Other</option>
          </select>
        </div>
      </div>

      <TransactionList transactions={transactions} />
    </section>
  );
}

export default RecentTransactions;
