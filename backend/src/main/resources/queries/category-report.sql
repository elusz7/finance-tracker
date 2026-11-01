SELECT
    COALESCE(SUM(t.amount), 0) as expense,
    t.category as category
FROM finance_tracker.public.transactions t
WHERE t.date BETWEEN :start AND :end
  and t.type = 'EXPENSE'
GROUP BY category