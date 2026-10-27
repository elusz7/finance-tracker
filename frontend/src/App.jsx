import "./App.css";
import { useEffect, useState } from "react";

function App() {
  // State to hold transactions
  const [transactions, setTransactions] = useState([]);

  // new transaction form state
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [type, setType] = useState("EXPENSE");
  const [category, setCategory] = useState("GROCERY");
  const [date, setDate] = useState("");
  const [formMessage, setFormMessage] = useState("");

  // Calculate total income
  const totalIncome = transactions
    .filter((transaction) => transaction.type === "INCOME")
    .reduce((sum, transaction) => sum + Number(transaction.amount), 0);

  // Calculate total expenses
  const totalExpenses = transactions
    .filter((transaction) => transaction.type === "EXPENSE")
    .reduce((sum, transaction) => sum + Number(transaction.amount), 0);

  // Calculate balance
  const balance = totalIncome - totalExpenses;

  const handleSubmit = async (event) => {
    event.preventDefault();
    setFormMessage("");

    const newTransaction = {
      description,
      amount: Number(amount),
      type,
      category,
      date,
    };

    // Validate the new transaction
    try {
      const response = await fetch("http://localhost:8080/api/transactions", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newTransaction),
      });

      if (!response.ok) {
        throw new Error(
          "Could not save transaction. Please check your entries.",
        );
      }

      const savedTransaction = await response.json();

      setTransactions((currentTransactions) => [
        ...currentTransactions,
        savedTransaction,
      ]);

      setDescription("");
      setAmount("");
      setType("EXPENSE");
      setCategory("GROCERY");
      setDate("");
      setFormMessage("Transaction added successfully!");
    } catch (error) {
      setFormMessage(error.message);
    }
  };

  //view transactions by category (select most recent 7 transactions)
  const [selectedCategory, setSelectedCategory] = useState("ALL");

  const filteredTransactions = transactions
    .filter(
      (transaction) =>
        selectedCategory === "ALL" || transaction.category === selectedCategory,
    )
    .sort((a, b) => new Date(b.date) - new Date(a.date))
    .slice(0, 7);

  useEffect(() => {
    fetch("http://localhost:8080/api/transactions")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to fetch transactions");
        }
        return response.json();
      })
      .then((data) => {
        setTransactions(data);
      })
      .catch((error) => {
        console.error("Error fetching transactions:", error);
      });
  }, []);

  return (
    <div className="app">
      <header>
        <h1>Personal Finance Tracker</h1>
        <p>Welcome back! Here's your financial overview.</p>
      </header>

      <main>
        <section className="add-transaction">
          <h2>Add Transaction</h2>

          <form onSubmit={handleSubmit}>
            <label>
              Description
              <input
                type="text"
                value={description}
                onChange={(event) => setDescription(event.target.value)}
                placeholder="e.g. Weekly Groceries"
              />
            </label>

            <label>
              Amount ($)
              <input
                type="number"
                min="0.01"
                step="0.01"
                value={amount}
                onChange={(event) => setAmount(event.target.value)}
                required
              />
            </label>

            <label>
              Type
              <select
                value={type}
                onChange={(event) => setType(event.target.value)}
              >
                <option value="EXPENSE">Expense</option>
                <option value="INCOME">Income</option>
              </select>
            </label>

            <label>
              Category
              <select
                value={category}
                onChange={(event) => setCategory(event.target.value)}
              >
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
            </label>

            <label>
              Date
              <input
                type="date"
                value={date}
                onChange={(event) => setDate(event.target.value)}
                required
              />
            </label>

            <button
              type="submit"
              className={
                type === "EXPENSE" ? "button-expense" : "button-income"
              }
            >
              Add {type === "EXPENSE" ? "Expense" : "Income"}
            </button>

            {formMessage && <p role="status">{formMessage}</p>}
          </form>
        </section>

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

        <section className="transactions">
          <div className="transactions-header">
            <h2>Recent Transactions</h2>

            <div className="transaction-filter">
              <label htmlFor="category-filter">Filter by category:</label>

              <select
                id="category-filter"
                value={selectedCategory}
                onChange={(event) => setSelectedCategory(event.target.value)}
              >
                <option value="ALL">All Categories</option>
                <option value="HOUSING">Housing</option>
                <option value="UTILITIES">Utilities</option>
                <option value="SUBSCRIPTION">Subscriptions</option>
                <option value="GROCERY">Groceries</option>
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

          {filteredTransactions.map((transaction) => (
            <div key={transaction.id} className="transaction-item">
              <div className="transaction-info">
                <h3>{transaction.description}</h3>
                <p>
                  {transaction.category} · {transaction.date}
                </p>
              </div>

              <p
                className={
                  transaction.type === "INCOME"
                    ? "transaction-income"
                    : "transaction-expense"
                }
              >
                {transaction.type === "INCOME" ? "+" : "−"}$
                {Number(transaction.amount).toLocaleString("en-US", {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2,
                })}
              </p>
            </div>
          ))}

          {filteredTransactions.length === 0 && (
            <p>No transactions found for this category.</p>
          )}
        </section>
      </main>
    </div>
  );
}

export default App;
