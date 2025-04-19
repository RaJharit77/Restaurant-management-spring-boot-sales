INSERT INTO price_history (ingredient_id, price, date)
VALUES (1, 10, '2025-04-01 08:00:00'),
       (2, 15000, '2025-04-01 08:00:00'),
       (3, 1000, '2025-04-01 08:00:00'),
       (4, 500, '2025-04-01 08:00:00')
on conflict do nothing;