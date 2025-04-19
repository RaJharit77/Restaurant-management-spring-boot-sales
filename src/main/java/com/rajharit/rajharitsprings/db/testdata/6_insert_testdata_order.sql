INSERT INTO "Order" (order_id, reference, created_at, status)
VALUES (1,'CMD-001', '2025-04-01 10:00:00', 'CREATED'),
       (2,'CMD-002', '2025-04-01 10:15:00', 'CREATED'),
       (3,'CMD-003', '2025-04-01 10:30:00', 'CREATED')
on conflict do nothing;