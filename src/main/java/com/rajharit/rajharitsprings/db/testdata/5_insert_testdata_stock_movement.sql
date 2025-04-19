INSERT INTO Stock_Movement (ingredient_id, movement_type, quantity, unit, movement_date)
VALUES (1, 'IN', 20000, 'G', '2025-04-01 08:00:00'),
       (2, 'IN', 20, 'L', '2025-04-01 08:00:00'),
       (3, 'IN', 100, 'U', '2025-04-01 08:00:00'),
       (4, 'IN', 50, 'U', '2025-04-01 08:00:00'),
       (2, 'OUT',1.5, 'L', '2025-04-01 10:00:00'),
       (3, 'OUT',49, 'U', '2025-04-01 10:00:00')
on conflict do nothing;