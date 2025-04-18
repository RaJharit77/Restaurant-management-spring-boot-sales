INSERT INTO "Order" (order_id, reference, created_at, status)
VALUES (1,'ORDER-001', '2025-03-01 10:00:00', 'CREATED'),
       (2,'ORDER-002', '2025-03-01 10:15:00', 'CONFIRMED'),
       (3,'ORDER-003', '2025-03-01 10:30:00', 'IN_PREPARATION')
on conflict do nothing;