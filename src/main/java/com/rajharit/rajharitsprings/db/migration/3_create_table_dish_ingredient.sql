CREATE TABLE IF NOT EXISTS Dish_Ingredient
(
    dish_id       INT REFERENCES Dish (dish_id),
    ingredient_id INT REFERENCES Ingredient (ingredient_id),
    quantity      DECIMAL(10, 2) NOT NULL,
    unit          unit_type      NOT NULL,
    PRIMARY KEY (dish_id, ingredient_id)
);