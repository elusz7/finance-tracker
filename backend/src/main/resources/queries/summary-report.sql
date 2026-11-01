SELECT
    COALESCE(SUM(t.amount) FILTER (WHERE t.type = 'EXPENSE'), 0) AS totalExpenses,
    COALESCE(SUM(t.amount) FILTER (WHERE t.type = 'INCOME'), 0) AS totalIncome
FROM finance_tracker.public.transactions t
WHERE t.date BETWEEN :start AND :end