CREATE TABLE IF NOT EXISTS Dish_Order_Status
(
    dish_order_status_id SERIAL PRIMARY KEY,
    dish_order_id     INT REFERENCES Dish_Order (dish_order_id) ON DELETE CASCADE,
    status            status_type NOT NULL,
    changed_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
