WITH income AS (
    SELECT
        COALESCE(SUM(t.amount), 0) as ytdIncome
    FROM finance_tracker.public.transactions t
    WHERE t.date BETWEEN date_trunc('year', now()) AND current_date
      AND t.type = 'INCOME'
),
     expenses AS (
         SELECT
             COALESCE(SUM(t.amount), 0) as ytdExpenses
         FROM finance_tracker.public.transactions t
         WHERE t.date BETWEEN date_trunc('year', now()) AND current_date
           AND t.type = 'EXPENSE'
     )
SELECT
    income.ytdIncome,
    expenses.ytdExpenses,
    income.ytdIncome - expenses.ytdExpenses as ytdBalance
FROM income CROSS JOIN expenses