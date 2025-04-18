INSERT INTO Ingredient (ingredient_id, name, unit_price, unit, update_datetime)
VALUES (1, 'Saucisse', 20, 'G', '2025-01-01 00:00'),
       (2, 'Huile', 10000, 'L', '2025-01-01 00:00'),
       (3, 'Oeuf', 1000, 'U', '2025-01-01 00:00'),
       (4, 'Pain', 1000, 'U', '2025-01-01 00:00')
on conflict do nothing;