import "./Sidebar.css";

const navItems = [
  { label: "Dashboard", initial: "D", page: "DASHBOARD" },
  { label: "Transactions", initial: "T", page: "TRANSACTIONS" },
  { label: "Reports", initial: "R", page: "REPORTS" },
];

function Sidebar({ activePage, setActivePage, onAddTransaction }) {
  return (
    <aside className="sidebar">
      <h2 className="sidebar-title">
        <span className="sidebar-full-title">Finance Tracker</span>
        <span className="sidebar-compact-title" aria-hidden="true">
          FT
        </span>
      </h2>

      <nav className="sidebar-nav" aria-label="Main navigation">
        {navItems.map((item) => (
          <button
            key={item.page}
            className={`nav-button ${activePage === item.page ? "active" : ""}`}
            onClick={() => setActivePage(item.page)}
            aria-label={item.label}
            aria-current={activePage === item.page ? "page" : undefined}
            title={item.label}
          >
            <span className="nav-label">{item.label}</span>
            <span className="nav-initial" aria-hidden="true">
              {item.initial}
            </span>
          </button>
        ))}
      </nav>

      <button
        className="add-transaction-button"
        onClick={onAddTransaction}
        aria-label="Add transaction"
        title="Add Transaction"
      >
        <span className="add-full-label">+ Add Transaction</span>
        <span className="add-compact-label" aria-hidden="true">
          +
        </span>
      </button>
    </aside>
  );
}

export default Sidebar;
