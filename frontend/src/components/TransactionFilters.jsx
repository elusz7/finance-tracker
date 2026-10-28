import "./TransactionFilters.css";

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

  return (
    <div className="transaction-filters">
      <div className="filter-row">
        <label>
          Type
          <select
            value={filterType}
            onChange={(event) => setFilterType(event.target.value)}
          >
            <option value="ALL">All Types</option>
            <option value="INCOME">Income</option>
            <option value="EXPENSE">Expense</option>
          </select>
        </label>

        <label>
          Category
          <select
            value={filterCategory}
            onChange={(event) => setFilterCategory(event.target.value)}
          >
            <option value="ALL">All Categories</option>
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
