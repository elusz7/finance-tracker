import "./Sidebar.css";

function Sidebar({ activePage, setActivePage, onAddTransaction }) {
  return (
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
        onClick={onAddTransaction}
      >
        + Add Transaction
      </button>
    </aside>
  );
}

export default Sidebar;