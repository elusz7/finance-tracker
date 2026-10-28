
import "./SuccessToast.css";

function SuccessToast({ message, type }) {
  if (!message) {
    return null;
  }

  return (
    <div
      className={`success-toast ${
        type === "INCOME" ? "toast-income" : "toast-expense"
      }`}
      role="status"
      aria-live="polite"
    >
      <span className="success-toast-icon" aria-hidden="true">
        ✓
      </span>
      {message}
    </div>
  );
}

export default SuccessToast;
