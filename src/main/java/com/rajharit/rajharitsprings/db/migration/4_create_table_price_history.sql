CREATE TABLE IF NOT EXISTS Price_History
(
    price_history_id SERIAL PRIMARY KEY,
    ingredient_id    INT REFERENCES Ingredient (ingredient_id),
    price            DOUBLE PRECISION NOT NULL,
    date             TIMESTAMP        NOT NULL
);