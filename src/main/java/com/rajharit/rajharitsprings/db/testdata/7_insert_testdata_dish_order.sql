INSERT INTO Dish_Order (order_id, dish_id, quantity, status)
VALUES (1, 1, 1, 'CREATED'),
       (1, 3, 1, 'CREATED'),
       (2, 2, 2, 'CREATED'),
       (3, 1, 1, 'CREATED')
on conflict do nothing;