
import "./TransactionModal.css";

function TransactionModal({
  title,
  description,
  amount,
  type,
  category,
  date,
  message,
  onDescriptionChange,
  onAmountChange,
  onTypeChange,
  onCategoryChange,
  onDateChange,
  onSubmit,
  onClose,
  onCancel,
  submitLabel,
}) {
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
              onChange={(event) =>
                onDescriptionChange(event.target.value)
              }
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

          <label>
            Category
            <select
              value={category}
              onChange={(event) =>
                onCategoryChange(event.target.value)
              }
              required
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
              className={type === "EXPENSE" ? "button-expense" : "button-income"}
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
