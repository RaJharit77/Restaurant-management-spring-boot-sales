INSERT INTO Dish (dish_id, name, unit_price)
VALUES (1, 'Hot Dog', 15000),
       (2, 'Omelette', 5000),
       (3, 'Saucisse frit', 3500)
on conflict do nothing;