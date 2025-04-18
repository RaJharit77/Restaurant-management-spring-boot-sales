INSERT INTO Dish_Order (order_id, dish_id, quantity, status)
VALUES (1, 1, 2, 'CREATED'),
       (1, 2, 1, 'CREATED'),
       (2, 3, 1, 'CONFIRMED'),
       (2, 4, 1, 'CONFIRMED'),
       (3, 1, 3, 'IN_PREPARATION')
on conflict do nothing;