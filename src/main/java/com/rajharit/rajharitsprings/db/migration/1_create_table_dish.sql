CREATE TABLE IF NOT EXISTS Dish
(
    dish_id    SERIAL PRIMARY KEY,
    name       VARCHAR(255)   NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL
);