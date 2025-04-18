CREATE TABLE IF NOT EXISTS Processing_Time (
    processing_time_id SERIAL PRIMARY KEY,
    dish_id INT NOT NULL,
    processing_time DOUBLE PRECISION NOT NULL,
    time_unit VARCHAR(10) NOT NULL,
    calculation_type VARCHAR(10) NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    calculation_date TIMESTAMP NOT NULL,
    FOREIGN KEY (dish_id) REFERENCES Dish(dish_id)
);