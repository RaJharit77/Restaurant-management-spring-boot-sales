CREATE TABLE IF NOT EXISTS Dish_Order
(
    dish_order_id SERIAL PRIMARY KEY,
    order_id      INT REFERENCES "Order" (order_id) ON DELETE CASCADE,
    dish_id       INT REFERENCES Dish (dish_id) ON DELETE CASCADE,
    quantity      INT NOT NULL,
    status        status_type DEFAULT 'CREATED'
);