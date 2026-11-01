WITH months AS (
        SELECT generate_series(
            DATE_TRUNC('month', CAST(:start AS date)),
            DATE_TRUNC('month', CAST(:end AS date)),
            INTERVAL '1 month'
        ) AS month
    ),
monthly_totals AS (
        SELECT
            COALESCE(SUM(t.amount) FILTER (WHERE t.type = 'EXPENSE'), 0) as totalExpenses,
            COALESCE(SUM(t.amount) FILTER (WHERE t.type = 'INCOME'), 0) as totalIncome,
            DATE_TRUNC('month', CAST(t.date AS date)) as yearMonth
        FROM finance_tracker.public.transactions t
        WHERE t.date BETWEEN :start AND :end
        GROUP BY yearMonth
    )
SELECT
    CAST(m.month as date) as yearMonth,
    COALESCE(mt.totalIncome, 0) as totalIncome,
    COALESCE(mt.totalExpenses, 0) as totalExpenses
FROM months m
LEFT JOIN monthly_totals mt
    ON mt.yearMonth = m.month
ORDER BY m.month