import "./TransactionList.css";

function TransactionList({ transactions, onEdit, onDelete }) {
  return (
    <div className="transaction-list">
      {transactions.map((transaction) => (
        <div className="transaction-item" key={transaction.id}>
          {(onEdit || onDelete) && (
            <div className="transaction-actions">
              {onEdit && (
                <button
                  className="edit-button"
                  onClick={() => onEdit(transaction)}
                  aria-label={`Edit ${transaction.description || "transaction"}`}
                  title="Edit transaction"
                >
                  ✎
                </button>
              )}

              {onDelete && (
                <button
                  className="delete-button"
                  onClick={() => onDelete(transaction)}
                  aria-label={`Delete ${transaction.description || "transaction"}`}
                  title="Delete transaction"
                >
                  ×
                </button>
              )}
            </div>
          )}

          <div className="transaction-info">
            <h3>{transaction.description || "Untitled transaction"}</h3>
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
            {Number(transaction.amount).toFixed(2)}
          </p>
        </div>
      ))}

      {transactions.length === 0 && (
        <p className="empty-transactions">No transactions to display.</p>
      )}
    </div>
  );
}

export default TransactionList;
