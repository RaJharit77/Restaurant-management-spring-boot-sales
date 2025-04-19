package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.*;
import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class DishDAOImpl implements DishDAO {
    private final DataBaseSource dataBaseSource;

    public DishDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @Override
    public List<Dish> getAll() {
        String query = "SELECT * FROM Dish";
        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("name"));
                dish.setUnitPrice(resultSet.getDouble("unit_price"));
                dish.setIngredients(getIngredientsForDish(dish.getId()));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all dishes", e);
        }

        return dishes;
    }

    @Override
    public Dish findById(int id) {
        String query = "SELECT * FROM Dish WHERE dish_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("name"));
                dish.setUnitPrice(resultSet.getDouble("unit_price"));
                dish.setIngredients(getIngredientsForDish(id));
                return dish;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving the dish", e);
        }
        return null;
    }

    @Override
    public List<Dish> saveAll(List<Dish> dishes) {
        String insertQuery = "INSERT INTO Dish (name, unit_price) VALUES (?, ?)";
        String updateQuery = "UPDATE Dish SET name = ?, unit_price = ? WHERE dish_id = ?";
        String dishIngredientQuery = "INSERT INTO Dish_Ingredient (dish_id, ingredient_id, quantity, unit) VALUES (?, ?, ?, ?::unit_type) "
                + "ON CONFLICT (dish_id, ingredient_id) DO UPDATE SET quantity = EXCLUDED.quantity, unit = EXCLUDED.unit";

        try (Connection connection = dataBaseSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
                 PreparedStatement dishIngredientStatement = connection.prepareStatement(dishIngredientQuery)) {

                for (Dish dish : dishes) {
                    if (dish.getId() == 0) {
                        insertStatement.setString(1, dish.getName());
                        insertStatement.setDouble(2, dish.getUnitPrice());
                        insertStatement.executeUpdate();

                        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            dish.setId(generatedKeys.getInt(1));
                        }
                    } else {
                        updateStatement.setString(1, dish.getName());
                        updateStatement.setDouble(2, dish.getUnitPrice());
                        updateStatement.setInt(3, dish.getId());
                        updateStatement.executeUpdate();
                    }

                    if (dish.getIngredients() != null) {
                        for (Ingredient ingredient : dish.getIngredients()) {
                            dishIngredientStatement.setInt(1, dish.getId());
                            dishIngredientStatement.setInt(2, ingredient.getId());
                            dishIngredientStatement.setDouble(3, ingredient.getAvailableQuantity());
                            dishIngredientStatement.setString(4, ingredient.getUnit().name());
                            dishIngredientStatement.executeUpdate();
                        }
                    }
                }

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error saving dishes", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing database connection", e);
        }

        return dishes;
    }

    @Override
    public void deleteDish(int id) {
        String query = "DELETE FROM Dish WHERE dish_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting dish", e);
        }
    }

    @Override
    public List<Dish> filterDish(String name, double unitPrice, List<Ingredient> dishIngredients) {
        StringBuilder query = new StringBuilder("SELECT DISTINCT d.dish_id, d.name, d.unit_price FROM Dish d WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (name != null && !name.trim().isEmpty()) {
            query.append(" AND d.name ILIKE ?");
            parameters.add("%" + name.trim() + "%");
        }

        if (unitPrice > 0) {
            query.append(" AND d.unit_price <= ?");
            parameters.add(unitPrice);
        }

        if (dishIngredients != null && !dishIngredients.isEmpty()) {
            query.append(" AND d.dish_id IN (SELECT di.dish_id FROM Dish_Ingredient di WHERE di.ingredient_id IN (");
            String placeholders = String.join(",", Collections.nCopies(dishIngredients.size(), "?"));
            query.append(placeholders).append(") GROUP BY di.dish_id HAVING COUNT(DISTINCT di.ingredient_id) = ?)");

            for (Ingredient ingredient : dishIngredients) {
                parameters.add(ingredient.getId());
            }
            parameters.add(dishIngredients.size());
        }

        query.append(" LIMIT 50");

        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query.toString())) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Dish dish = new Dish();
                dish.setId(resultSet.getInt("dish_id"));
                dish.setName(resultSet.getString("name"));
                dish.setUnitPrice(resultSet.getDouble("unit_price"));
                dish.setIngredients(getIngredientsForDish(dish.getId()));
                dishes.add(dish);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering dishes", e);
        }

        return dishes;
    }

    private List<Ingredient> getIngredientsForDish(int dishId) {
        String query = "SELECT i.ingredient_id, i.name, i.unit_price, i.unit, i.update_datetime, di.quantity " +
                "FROM Ingredient i " +
                "JOIN Dish_Ingredient di ON i.ingredient_id = di.ingredient_id " +
                "WHERE di.dish_id = ?";

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient(
                        resultSet.getInt("ingredient_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("unit_price"),
                        Unit.valueOf(resultSet.getString("unit")),
                        resultSet.getTimestamp("update_datetime").toLocalDateTime(),
                        resultSet.getDouble("quantity")
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredients", e);
        }

        return ingredients;
    }
}