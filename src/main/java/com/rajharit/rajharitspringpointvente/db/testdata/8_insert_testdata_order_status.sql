INSERT INTO Order_Status (order_id, status, changed_at)
VALUES (1, 'CREATED', '2025-03-01 10:00:00'),
       (1, 'CONFIRMED', '2025-03-01 10:05:00'),
       (2, 'CONFIRMED', '2025-03-01 10:15:00'),
       (2, 'IN_PREPARATION', '2025-03-01 10:20:00'),
       (3, 'IN_PREPARATION', '2025-03-01 10:30:00'),
       (3, 'COMPLETED', '2025-03-01 10:45:00')
on conflict do nothing;