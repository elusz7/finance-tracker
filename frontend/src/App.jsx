import './App.css'

function App() {
  return (
    <div className="app">
      <header>
        <h1>Personal Finance Tracker</h1>
        <p>Welcome back! Here's your financial overview.</p>
      </header>

      <main>
        <section className="summary">
          <div className="card">
            <h3>Total Balance</h3>
            <p>$5,250.00</p>
          </div>

          <div className="card">
            <h3>Income</h3>
            <p>$3,500.00</p>
          </div>

          <div className="card">
            <h3>Expenses</h3>
            <p>$1,250.00</p>
          </div>
        </section>

        <section className="transactions">
          <h2>Recent Transactions</h2>
          <p>No transactions yet. Your transactions will appear here.</p>
        </section>
      </main>
    </div>
  )
}

export default App