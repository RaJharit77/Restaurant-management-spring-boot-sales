CREATE TABLE IF NOT EXISTS best_sales (
    best_sales_id SERIAL PRIMARY KEY,
    dish_name VARCHAR(255) NOT NULL,
    quantity_sold INT NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    calculation_date TIMESTAMP NOT NULL
);