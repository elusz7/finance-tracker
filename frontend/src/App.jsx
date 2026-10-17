import './App.css'
import { useEffect, useState } from 'react';

function App() {
  
    const [transactions, setTransactions] = useState([]);

    const sortedTransactions = [...transactions].sort((a, b) => {
      return new Date(b.date) - new Date(a.date);
    });
    useEffect(() => {
      fetch('http://localhost:8080/api/transactions')
        .then((response) => {
          if (!response.ok) {
            throw new Error('Failed to fetch transactions');
          }
          return response.json();
        })
        .then((data) => {
          setTransactions(data);
          console.log('Transactions loaded:', data);
        })
        .catch((error) => {
          console.error('Error fetching transactions:', error);
        });
    }, []);

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
          {
            sortedTransactions.map((transaction) => (
            <div key={transaction.id} className="transaction-item">
              <div className="transaction-info">
                <h3>{transaction.description}</h3>
                <p>{transaction.category} · {transaction.date}</p>
              </div>

              <p className={
                transaction.type === 'INCOME'
                  ? 'transaction-income'
                  : 'transaction-expense'
              }>
                {transaction.type === 'INCOME' ? '+' : '−'}
                ${Number(transaction.amount).toLocaleString('en-US', {
                  minimumFractionDigits: 2,
                  maximumFractionDigits: 2
                })}
              </p>
            </div>
          ))}
        </section>
      </main>
    </div>
  )
}

export default App