import "./App.css";
import { useEffect, useState } from "react";

function App() {
  // State to hold transactions
  const [transactions, setTransactions] = useState([]);

  // State to manage the active page (DASHBOARD, TRANSACTIONS, REPORTS)
  const [activePage, setActivePage] = useState("DASHBOARD");

  // State to manage the visibility of the add transaction modal
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  // new transaction form state
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [type, setType] = useState("EXPENSE");
  const [category, setCategory] = useState("GROCERY");
  const getTodayDate = () => {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, "0");
    const day = String(today.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
  };

  const [date, setDate] = useState(getTodayDate());
  const [formMessage, setFormMessage] = useState("");

  // transaction success message state
  const [successMessage, setSuccessMessage] = useState("");
  const [successType, setSuccessType] = useState("EXPENSE");

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

  // Handle form submission for adding a new transaction
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
      setDate(getTodayDate());
      setFormMessage("");
      setSuccessType(savedTransaction.type);
      setSuccessMessage(
        `${savedTransaction.type === "INCOME" ? "Income" : "Expense"} added successfully!`,
      );
      setIsAddModalOpen(false);
    } catch (error) {
      setFormMessage(error.message);
    }
  };

  // Function to close the add transaction modal and reset form fields
  const closeAddModal = () => {
    setIsAddModalOpen(false);
    setDescription("");
    setAmount("");
    setType("EXPENSE");
    setCategory("GROCERY");
    setDate(getTodayDate());
    setFormMessage("");
  };

  // Clear success message after 3 seconds
  useEffect(() => {
    if (!successMessage) return;

    const timer = setTimeout(() => {
      setSuccessMessage("");
    }, 3000);

    return () => clearTimeout(timer);
  }, [successMessage]);

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
    <div className="app-layout">
      <aside className="sidebar">
        <h2 className="sidebar-title">Finance Tracker</h2>

        <nav className="sidebar-nav">
          <button
            className={`nav-button ${activePage === "DASHBOARD" ? "active" : ""}`}
            onClick={() => setActivePage("DASHBOARD")}
          >
            Dashboard
          </button>

          <button
            className={`nav-button ${activePage === "TRANSACTIONS" ? "active" : ""}`}
            onClick={() => setActivePage("TRANSACTIONS")}
          >
            Transactions
          </button>

          <button
            className={`nav-button ${activePage === "REPORTS" ? "active" : ""}`}
            onClick={() => setActivePage("REPORTS")}
          >
            Reports
          </button>
        </nav>

        <button
          className="add-transaction-button"
          onClick={() => setIsAddModalOpen(true)}
        >
          + Add Transaction
        </button>
      </aside>

      <main className="main-content">
        {activePage === "DASHBOARD" && (
          <>
            {
              <div className="app">
                <header>
                  <h1>Personal Finance Tracker</h1>
                  <p>Welcome back! Here's your financial overview.</p>
                </header>

                <div>
                  <section className="summary">
                    <div className="card">
                      <h3>Current Balance</h3>
                      <p
                        className={
                          balance >= 0
                            ? "transaction-income"
                            : "transaction-expense"
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
                        <label htmlFor="category-filter">
                          Filter by category:
                        </label>

                        <select
                          id="category-filter"
                          value={selectedCategory}
                          onChange={(event) =>
                            setSelectedCategory(event.target.value)
                          }
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
                </div>
              </div>
            }
          </>
        )}

        {activePage === "TRANSACTIONS" && (
          <section className="transactions-page">
            <header>
              <h1>Transactions</h1>
              <p>View and manage all your income and expenses.</p>
            </header>

            <section className="transactions">
              <h2>All Transactions</h2>
              <div className="transaction-list">
                {transactions
                  .slice()
                  .sort((a, b) => new Date(b.date) - new Date(a.date))
                  .map((transaction) => (
                    <div key={transaction.id} className="transaction-item">
                      <div className="transaction-info">
                        <h3>
                          {transaction.description || "Untitled transaction"}
                        </h3>
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

                {transactions.length === 0 && (
                  <p>
                    No transactions yet. Click + Add Transaction to get started.
                  </p>
                )}
              </div>
            </section>
          </section>
        )}

        {isAddModalOpen && (
          <div className="modal-overlay" onClick={() => closeAddModal()}>
            <section
              className="add-transaction modal-content"
              role="dialog"
              aria-modal="true"
              aria-labelledby="modal-title"
              onClick={(event) => event.stopPropagation()}
            >
              <div className="modal-header">
                <h2 id="modal-title">Add Transaction</h2>

                <button
                  type="button"
                  className="modal-close"
                  aria-label="Close modal"
                  onClick={closeAddModal}
                >
                  ×
                </button>
              </div>

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

                <div className="modal-actions">
                  <button
                    type="submit"
                    className={
                      type === "EXPENSE" ? "button-expense" : "button-income"
                    }
                  >
                    Add {type === "EXPENSE" ? "Expense" : "Income"}
                  </button>
                </div>

                {formMessage && <p role="status">{formMessage}</p>}
              </form>
            </section>
          </div>
        )}

        {successMessage && (
          <div
            className={`success-toast ${
              successType === "INCOME" ? "toast-income" : "toast-expense"
            }`}
            role="status"
          >
            <span className="success-toast-icon">✓</span>
            {successMessage}
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
