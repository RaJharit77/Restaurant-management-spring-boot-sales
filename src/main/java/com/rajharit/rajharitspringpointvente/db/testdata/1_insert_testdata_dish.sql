INSERT INTO Dish (dish_id, name, unit_price)
VALUES (1, 'Hot Dog', 15000),
       (2, 'Cheeseburger', 7.99),
       (3, 'Hamburger', 6.99),
       (4, 'Salade', 4.99)
on conflict do nothing;