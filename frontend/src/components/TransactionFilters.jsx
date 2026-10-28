import "./TransactionFilters.css";

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

function TransactionFilters({
  filterType,
  setFilterType,
  filterCategory,
  setFilterCategory,
  startDate,
  setStartDate,
  endDate,
  setEndDate,
  minAmount,
  setMinAmount,
  maxAmount,
  setMaxAmount,
}) {
  const resetFilters = () => {
    setFilterType("ALL");
    setFilterCategory("ALL");
    setStartDate("");
    setEndDate("");
    setMinAmount("");
    setMaxAmount("");
  };

  const classificationOptions =
    filterType === "INCOME"
      ? incomeSources
      : filterType === "EXPENSE"
        ? expenseCategories
        : [
            ...expenseCategories,
            ...incomeSources.filter(
              (source) =>
                !expenseCategories.some(
                  (category) => category.value === source.value,
                ),
            ),
          ];

  return (
    <div className="transaction-filters">
      <div className="filter-row">
        <label>
          Type
          <select
            value={filterType}
            onChange={(event) => {
              setFilterType(event.target.value);
              setFilterCategory("ALL");
            }}
          >
            <option value="ALL">All Types</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
        </label>

        <label>
          Category / Source
          <select
            value={filterCategory}
            onChange={(event) => setFilterCategory(event.target.value)}
          >
            <option value="ALL">
              {filterType === "INCOME"
                ? "All Income Sources"
                : filterType === "EXPENSE"
                  ? "All Expense Categories"
                  : "All Categories & Sources"}
            </option>

            {classificationOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </label>
      </div>

      <div className="filter-row">
        <label>
          Start Date
          <input
            type="date"
            value={startDate}
            onChange={(event) => setStartDate(event.target.value)}
          />
        </label>

        <label>
          End Date
          <input
            type="date"
            value={endDate}
            min={startDate || undefined}
            onChange={(event) => setEndDate(event.target.value)}
          />
        </label>

        <label>
          Min Amount ($)
          <input
            type="number"
            min="0"
            step="0.01"
            value={minAmount}
            onChange={(event) => setMinAmount(event.target.value)}
            placeholder="No minimum"
          />
        </label>

        <label>
          Max Amount ($)
          <input
            type="number"
            min="0"
            step="0.01"
            value={maxAmount}
            onChange={(event) => setMaxAmount(event.target.value)}
            placeholder="No maximum"
          />
        </label>

        <button type="button" className="button-cancel" onClick={resetFilters}>
          Reset Filters
        </button>
      </div>
    </div>
  );
}

export default TransactionFilters;
