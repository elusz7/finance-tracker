import "./TransactionModal.css";

const expenseCategories = [
  { value: "HOUSING", label: "Housing" },
  { value: "UTILITIES", label: "Utilities" },
  { value: "PHONE_INTERNET", label: "Phone & Internet" },
  { value: "SUBSCRIPTION", label: "Subscription" },
  { value: "GROCERY", label: "Grocery" },
  { value: "DINING", label: "Dining" },
  { value: "ENTERTAINMENT", label: "Entertainment" },
  { value: "TRANSPORTATION", label: "Transportation" },
  { value: "GAS_FUEL", label: "Gas & Fuel" },
  { value: "AUTO_MAINTENANCE", label: "Auto Maintenance" },
  { value: "HEALTHCARE", label: "Healthcare" },
  { value: "INSURANCE", label: "Insurance" },
  { value: "TRAVEL", label: "Travel" },
  { value: "SHOPPING", label: "Shopping" },
  { value: "PERSONAL_CARE", label: "Personal Care" },
  { value: "EDUCATION", label: "Education" },
  { value: "PETS", label: "Pets" },
  { value: "GIFTS_DONATIONS", label: "Gifts & Donations" },
  { value: "TAXES", label: "Taxes" },
  { value: "DEBT_PAYMENT", label: "Debt Payment" },
  { value: "CHILDCARE", label: "Childcare" },
  { value: "FITNESS", label: "Fitness" },
  { value: "HOME_MAINTENANCE", label: "Home Maintenance" },
  { value: "FEES", label: "Fees" },
  { value: "WORK_EXPENSES", label: "Work Expenses" },
  { value: "OTHER", label: "Other" },
];

const incomeSources = [
  { value: "SALARY", label: "Salary" },
  { value: "FREELANCE", label: "Freelance" },
  { value: "INVESTMENT", label: "Investment" },
  { value: "INTEREST", label: "Interest" },
  { value: "GIFT", label: "Gift" },
  { value: "REFUND", label: "Refund" },
  { value: "OTHER", label: "Other" },
];

function TransactionModal({
  title,
  description,
  amount,
  type,
  expenseCategory,
  incomeSource,
  date,
  message,
  onDescriptionChange,
  onAmountChange,
  onTypeChange,
  onExpenseCategoryChange,
  onIncomeSourceChange,
  onDateChange,
  onSubmit,
  onClose,
  onCancel,
  submitLabel,
}) {
  const isExpense = type === "EXPENSE";

  return (
    <div className="modal-overlay" onClick={onClose}>
      <section
        className="add-transaction modal-content"
        role="dialog"
        aria-modal="true"
        aria-labelledby="transaction-modal-title"
        onClick={(event) => event.stopPropagation()}
      >
        <div className="modal-header">
          <h2 id="transaction-modal-title">{title}</h2>

          <button
            type="button"
            className="modal-close"
            aria-label="Close modal"
            onClick={onClose}
          >
            ×
          </button>
        </div>

        <form onSubmit={onSubmit}>
          <label>
            Description
            <input
              type="text"
              value={description}
              onChange={(event) => onDescriptionChange(event.target.value)}
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
              onChange={(event) => onAmountChange(event.target.value)}
              required
            />
          </label>

          <label>
            Type
            <select
              value={type}
              onChange={(event) => onTypeChange(event.target.value)}
              required
            >
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
          </label>

          {isExpense ? (
            <label>
              Expense Category
              <select
                value={expenseCategory}
                onChange={(event) =>
                  onExpenseCategoryChange(event.target.value)
                }
                required
              >
                {expenseCategories.map((category) => (
                  <option key={category.value} value={category.value}>
                    {category.label}
                  </option>
                ))}
              </select>
            </label>
          ) : (
            <label>
              Income Source
              <select
                value={incomeSource}
                onChange={(event) => onIncomeSourceChange(event.target.value)}
                required
              >
                {incomeSources.map((source) => (
                  <option key={source.value} value={source.value}>
                    {source.label}
                  </option>
                ))}
              </select>
            </label>
          )}

          <label>
            Date
            <input
              type="date"
              value={date}
              onChange={(event) => onDateChange(event.target.value)}
              required
            />
          </label>

          {message && <p role="alert">{message}</p>}

          <div className="modal-actions">
            {onCancel && (
              <button
                type="button"
                className="button-cancel"
                onClick={onCancel}
              >
                Cancel
              </button>
            )}

            <button
              type="submit"
              className={isExpense ? "button-expense" : "button-income"}
            >
              {submitLabel}
            </button>
          </div>
        </form>
      </section>
    </div>
  );
}

export default TransactionModal;
