INSERT INTO Dish_Order_Status (dish_order_status_id, status, changed_at)
VALUES (1, 'CREATED', '2025-03-01 10:00:00'),
       (1, 'CONFIRMED', '2025-03-01 10:05:00'),
       (2, 'CREATED', '2025-03-01 10:00:00'),
       (2, 'IN_PREPARATION', '2025-03-01 10:10:00'),
       (3, 'CONFIRMED', '2025-03-01 10:15:00'),
       (3, 'COMPLETED', '2025-03-01 10:30:00')
on conflict do nothing;