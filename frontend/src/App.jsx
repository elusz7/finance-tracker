import "./App.css";
import Sidebar from "./components/Sidebar";
import SummaryCards from "./components/SummaryCards";
import TransactionList from "./components/TransactionList";
import TransactionModal from "./components/TransactionModal";
import TransactionFilters from "./components/TransactionFilters";
import SuccessToast from "./components/SuccessToast";
import MonthlyChart from "./components/MonthlyChart";
import Reports from "./components/Reports/Reports";

import { useEffect, useState } from "react";

function App() {
  // State to hold transactions
  const [transactions, setTransactions] = useState([]);

  // State to manage the active page (DASHBOARD, TRANSACTIONS, REPORTS)
  const [activePage, setActivePage] = useState("DASHBOARD");
  const [dashboardRefresh, setDashboardRefresh] = useState(0);

  // State to manage the visibility of the add transaction modal
  const [isAddModalOpen, setIsAddModalOpen] = useState(false);

  //state for editing a transaction
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);

  // state for transaction filtering
  const initialFilters = {
    filterType: "ALL",
    filterCategory: "ALL",
    startDate: "",
    endDate: "",
    minAmount: "",
    maxAmount: "",
  };

  const [draftFilters, setDraftFilters] = useState(initialFilters);
  const [appliedFilters, setAppliedFilters] = useState(initialFilters);

  //handle transactionfiltering
  const handleApplyFilters = () => {
    setAppliedFilters({ ...draftFilters });
  };

  const handleResetFilters = () => {
    setDraftFilters(initialFilters);
    setAppliedFilters(initialFilters);
  };

  // new transaction form state
  const [description, setDescription] = useState("");
  const [amount, setAmount] = useState("");
  const [type, setType] = useState("EXPENSE");
  const [expenseCategory, setExpenseCategory] = useState("GROCERY");
  const [incomeSource, setIncomeSource] = useState("SALARY");

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

      setDashboardRefresh((previous) => previous + 1);
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
  const [editExpenseCategory, setEditExpenseCategory] = useState("GROCERY");
  const [editIncomeSource, setEditIncomeSource] = useState("SALARY");
  const [editDate, setEditDate] = useState(getTodayDate());
  const [editFormMessage, setEditFormMessage] = useState("");

  const handleEdit = (transaction) => {
    setEditingTransaction(transaction);
    setEditDescription(transaction.description || "");
    setEditAmount(String(transaction.amount));
    setEditType(transaction.type);
    setEditExpenseCategory(transaction.expenseCategory || "GROCERY");
    setEditIncomeSource(transaction.incomeSource || "SALARY");
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
      expenseCategory: editType === "EXPENSE" ? editExpenseCategory : null,
      incomeSource: editType === "INCOME" ? editIncomeSource : null,
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

      setDashboardRefresh((previous) => previous + 1);
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
      expenseCategory: type === "EXPENSE" ? expenseCategory : null,
      incomeSource: type === "INCOME" ? incomeSource : null,
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
      setExpenseCategory("GROCERY");
      setIncomeSource("SALARY");
      setDate(getTodayDate());
      setFormMessage("");
      setSuccessType(savedTransaction.type);
      setSuccessMessage(
        `${savedTransaction.type === "INCOME" ? "Income" : "Expense"} added successfully!`,
      );
      setIsAddModalOpen(false);
      setDashboardRefresh((previous) => previous + 1);
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
    setExpenseCategory("GROCERY");
    setIncomeSource("SALARY");
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
      if (
        appliedFilters.filterType !== "ALL" &&
        transaction.type !== appliedFilters.filterType
      ) {
        return false;
      }

      if (
        appliedFilters.filterCategory !== "ALL" &&
        transaction.expenseCategory !== appliedFilters.filterCategory &&
        transaction.incomeSource !== appliedFilters.filterCategory
      ) {
        return false;
      }

      if (
        appliedFilters.startDate &&
        transaction.date < appliedFilters.startDate
      ) {
        return false;
      }

      if (appliedFilters.endDate && transaction.date > appliedFilters.endDate) {
        return false;
      }

      if (
        appliedFilters.minAmount !== "" &&
        Number(transaction.amount) < Number(appliedFilters.minAmount)
      ) {
        return false;
      }

      if (
        appliedFilters.maxAmount !== "" &&
        Number(transaction.amount) > Number(appliedFilters.maxAmount)
      ) {
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
              <SummaryCards refresh={dashboardRefresh} />

              <MonthlyChart refresh={dashboardRefresh} />
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
              startDate={draftFilters.startDate}
              setStartDate={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  startDate: value,
                }))
              }
              endDate={draftFilters.endDate}
              setEndDate={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  endDate: value,
                }))
              }
              minAmount={draftFilters.minAmount}
              setMinAmount={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  minAmount: value,
                }))
              }
              maxAmount={draftFilters.maxAmount}
              setMaxAmount={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  maxAmount: value,
                }))
              }
              filterType={draftFilters.filterType}
              setFilterType={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  filterType: value,
                  filterCategory: "ALL",
                }))
              }
              filterCategory={draftFilters.filterCategory}
              setFilterCategory={(value) =>
                setDraftFilters((current) => ({
                  ...current,
                  filterCategory: value,
                }))
              }
              onApplyFilters={handleApplyFilters}
              onResetFilters={handleResetFilters}
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

        {activePage === "REPORTS" && <Reports />}

        {isAddModalOpen && (
          <TransactionModal
            title="Add Transaction"
            description={description}
            amount={amount}
            type={type}
            expenseCategory={expenseCategory}
            incomeSource={incomeSource}
            date={date}
            message={formMessage}
            onDescriptionChange={setDescription}
            onAmountChange={setAmount}
            onTypeChange={setType}
            onExpenseCategoryChange={setExpenseCategory}
            onIncomeSourceChange={setIncomeSource}
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
            expenseCategory={editExpenseCategory}
            incomeSource={editIncomeSource}
            date={editDate}
            message={editFormMessage}
            onDescriptionChange={setEditDescription}
            onAmountChange={setEditAmount}
            onTypeChange={setEditType}
            onExpenseCategoryChange={setEditExpenseCategory}
            onIncomeSourceChange={setEditIncomeSource}
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
