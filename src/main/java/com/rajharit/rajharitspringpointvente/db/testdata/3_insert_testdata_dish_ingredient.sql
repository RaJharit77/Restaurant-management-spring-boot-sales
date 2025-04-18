INSERT INTO Dish_Ingredient (dish_id, ingredient_id, quantity, unit)
VALUES (1, 1, 100, 'G'),
       (1, 2, 0.15, 'L'),
       (1, 3, 1, 'U'),
       (1, 4, 1, 'U')
on conflict do nothing;