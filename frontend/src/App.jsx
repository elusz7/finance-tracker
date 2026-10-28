import "./App.css";
import Sidebar from "./components/Sidebar";
import SummaryCards from "./components/SummaryCards";
import TransactionList from "./components/TransactionList";
import TransactionModal from "./components/TransactionModal";
import TransactionFilters from "./components/TransactionFilters";
import SuccessToast from "./components/SuccessToast";
import MonthlyChart from "./components/MonthlyChart";

import { useEffect, useState } from "react";

function App() {
  // State to hold transactions
  const [transactions, setTransactions] = useState([]);

  // State to manage the active page (DASHBOARD, TRANSACTIONS, REPORTS)
  const [activePage, setActivePage] = useState("DASHBOARD");

  // State to manage the visibility of the add transaction modal
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  //state for editing a transaction
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);

  // state for transaction filtering
  const [filterType, setFilterType] = useState("ALL");
  const [filterCategory, setFilterCategory] = useState("ALL");
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [minAmount, setMinAmount] = useState("");
  const [maxAmount, setMaxAmount] = useState("");

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

  //YTD calculations

  const now = new Date();

  const currentYear = now.getFullYear();

  const today = [
    currentYear,
    String(now.getMonth() + 1).padStart(2, "0"),
    String(now.getDate()).padStart(2, "0"),
  ].join("-");

  const yearStart = `${currentYear}-01-01`;

  const ytdTransactions = transactions.filter((transaction) => {
    return transaction.date >= yearStart && transaction.date <= today;
  });

  const ytdIncome = ytdTransactions
    .filter((transaction) => transaction.type === "INCOME")
    .reduce((total, transaction) => total + Number(transaction.amount), 0);

  const ytdExpenses = ytdTransactions
    .filter((transaction) => transaction.type === "EXPENSE")
    .reduce((total, transaction) => total + Number(transaction.amount), 0);

  const ytdBalance = ytdIncome - ytdExpenses;

  // Handle deleting a transaction
  const handleDelete = async (transaction) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${transaction.description || "Untitled transaction"}"?`,
    );

    if (!confirmed) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/transactions/${transaction.id}`,
        {
          method: "DELETE",
        },
      );

      if (!response.ok) {
        throw new Error("Could not delete transaction.");
      }

      setTransactions((currentTransactions) =>
        currentTransactions.filter((item) => item.id !== transaction.id),
      );

      setSuccessType(transaction.type);
      setSuccessMessage("Transaction deleted successfully!");
    } catch (error) {
      window.alert(error.message);
    }
  };

  //handle editing a transaction
  const [editDescription, setEditDescription] = useState("");
  const [editAmount, setEditAmount] = useState("");
  const [editType, setEditType] = useState("EXPENSE");
  const [editCategory, setEditCategory] = useState("GROCERY");
  const [editDate, setEditDate] = useState(getTodayDate());
  const [editFormMessage, setEditFormMessage] = useState("");

  const handleEdit = (transaction) => {
    setEditingTransaction(transaction);
    setEditDescription(transaction.description || "");
    setEditAmount(String(transaction.amount));
    setEditType(transaction.type);
    setEditCategory(transaction.category);
    setEditDate(transaction.date);
    setEditFormMessage("");
    setIsEditModalOpen(true);
  };

  const handleEditSubmit = async (event) => {
    event.preventDefault();
    setEditFormMessage("");

    if (!editingTransaction) return;

    const updatedTransaction = {
      description: editDescription,
      amount: Number(editAmount),
      type: editType,
      category: editCategory,
      date: editDate,
    };

    try {
      const response = await fetch(
        `http://localhost:8080/api/transactions/${editingTransaction.id}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(updatedTransaction),
        },
      );

      if (!response.ok) {
        throw new Error(
          "Could not update transaction. Please check your entries.",
        );
      }

      const savedTransaction = await response.json();

      setTransactions((currentTransactions) =>
        currentTransactions.map((transaction) =>
          transaction.id === savedTransaction.id
            ? savedTransaction
            : transaction,
        ),
      );

      setSuccessType(savedTransaction.type);
      setSuccessMessage("Transaction updated successfully!");
      setIsEditModalOpen(false);
      setEditingTransaction(null);
    } catch (error) {
      setEditFormMessage(error.message);
    }
  };

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

    // // Submit the new transaction
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

  // Filter transactions based on the selected filters
  const allFilteredTransactions = transactions
    .filter((transaction) => {
      if (filterType !== "ALL" && transaction.type !== filterType) {
        return false;
      }

      if (filterCategory !== "ALL" && transaction.category !== filterCategory) {
        return false;
      }

      if (startDate && transaction.date < startDate) {
        return false;
      }

      if (endDate && transaction.date > endDate) {
        return false;
      }

      if (minAmount !== "" && Number(transaction.amount) < Number(minAmount)) {
        return false;
      }

      if (maxAmount !== "" && Number(transaction.amount) > Number(maxAmount)) {
        return false;
      }

      return true;
    })
    .slice()
    .sort((a, b) => new Date(b.date) - new Date(a.date));

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
      <Sidebar
        activePage={activePage}
        setActivePage={setActivePage}
        onAddTransaction={() => setIsAddModalOpen(true)}
      />

      <main className="main-content">
        {activePage === "DASHBOARD" && (
          <div className="app">
            <header>
              <h1>Personal Finance Tracker</h1>
            </header>

            <div>
              <SummaryCards
                ytdBalance={ytdBalance}
                ytdIncome={ytdIncome}
                ytdExpenses={ytdExpenses}
              />

              <MonthlyChart transactions={transactions} />
            </div>
          </div>
        )}

        {activePage === "TRANSACTIONS" && (
          <section className="transactions-page">
            <header>
              <h1>Transactions</h1>
              <p>View and manage all your income and expenses.</p>
            </header>

            <TransactionFilters
              filterType={filterType}
              setFilterType={setFilterType}
              filterCategory={filterCategory}
              setFilterCategory={setFilterCategory}
              startDate={startDate}
              setStartDate={setStartDate}
              endDate={endDate}
              setEndDate={setEndDate}
              minAmount={minAmount}
              setMinAmount={setMinAmount}
              maxAmount={maxAmount}
              setMaxAmount={setMaxAmount}
            />

            <section className="transactions">
              <h2>All Transactions ({allFilteredTransactions.length})</h2>
            </section>

            <TransactionList
              transactions={allFilteredTransactions}
              onEdit={handleEdit}
              onDelete={handleDelete}
            />
          </section>
        )}

        {isAddModalOpen && (
          <TransactionModal
            title="Add Transaction"
            description={description}
            amount={amount}
            type={type}
            category={category}
            date={date}
            message={formMessage}
            onDescriptionChange={setDescription}
            onAmountChange={setAmount}
            onTypeChange={setType}
            onCategoryChange={setCategory}
            onDateChange={setDate}
            onSubmit={handleSubmit}
            onClose={closeAddModal}
            submitLabel={`Add ${type === "EXPENSE" ? "Expense" : "Income"}`}
          />
        )}

        {isEditModalOpen && editingTransaction && (
          <TransactionModal
            title="Edit Transaction"
            description={editDescription}
            amount={editAmount}
            type={editType}
            category={editCategory}
            date={editDate}
            message={editFormMessage}
            onDescriptionChange={setEditDescription}
            onAmountChange={setEditAmount}
            onTypeChange={setEditType}
            onCategoryChange={setEditCategory}
            onDateChange={setEditDate}
            onSubmit={handleEditSubmit}
            onClose={() => {
              setIsEditModalOpen(false);
              setEditingTransaction(null);
              setEditFormMessage("");
            }}
            onCancel={() => {
              setIsEditModalOpen(false);
              setEditingTransaction(null);
              setEditFormMessage("");
            }}
            submitLabel="Save Changes"
          />
        )}

        <SuccessToast message={successMessage} type={successType} />
      </main>
    </div>
  );
}

export default App;
