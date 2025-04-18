INSERT INTO Stock_Movement (ingredient_id, movement_type, quantity, unit, movement_date)
VALUES (1, 'ENTRY', 100, 'U', '2025-02-01 08:00:00'),
       (2, 'ENTRY', 50, 'U', '2025-02-01 08:00:00'),
       (3, 'ENTRY', 10000, 'G', '2025-02-01 08:00:00'),
       (4, 'ENTRY', 20, 'L', '2025-02-01 08:00:00')
on conflict do nothing;