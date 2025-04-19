package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.*;
import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class IngredientDAOImpl implements IngredientDAO {
    private final DataBaseSource dataBaseSource;

    public IngredientDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @Override
    public List<Ingredient> getAll() {
        String query = "SELECT * FROM Ingredient";
        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient(
                        resultSet.getInt("ingredient_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("unit_price"),
                        Unit.valueOf(resultSet.getString("unit")),
                        resultSet.getTimestamp("update_datetime").toLocalDateTime(),
                        0
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all ingredients", e);
        }

        return ingredients;
    }

    @Override
    public Ingredient findById(int id) {
        String query = "SELECT * FROM Ingredient WHERE ingredient_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setId(resultSet.getInt("ingredient_id"));
                ingredient.setName(resultSet.getString("name"));
                ingredient.setActualPrice(resultSet.getDouble("unit_price"));
                ingredient.setUnit(Unit.valueOf(resultSet.getString("unit")));
                ingredient.setUpdateDateTime(resultSet.getTimestamp("update_datetime").toLocalDateTime());
                return ingredient;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredient", e);
        }
        return null;
    }

    @Override
    public List<Ingredient> saveAll(List<Ingredient> ingredients) {
        String insertQuery = "INSERT INTO Ingredient (ingredient_id, name, unit_price, unit, update_datetime) " +
                "VALUES (?, ?, ?, ?::unit_type, ?) " +
                "ON CONFLICT (ingredient_id) DO UPDATE SET " +
                "name = EXCLUDED.name, " +
                "unit_price = EXCLUDED.unit_price, " +
                "unit = EXCLUDED.unit, " +
                "update_datetime = EXCLUDED.update_datetime";

        String priceHistoryQuery = "INSERT INTO Price_History (ingredient_id, price, date) VALUES (?, ?, ?) " +
                "ON CONFLICT DO NOTHING";

        try (Connection connection = dataBaseSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement ingredientStatement = connection.prepareStatement(insertQuery);
                 PreparedStatement priceStatement = connection.prepareStatement(priceHistoryQuery)) {

                for (Ingredient ingredient : ingredients) {
                    ingredientStatement.setInt(1, ingredient.getId());
                    ingredientStatement.setString(2, ingredient.getName());
                    ingredientStatement.setDouble(3, ingredient.getActualPrice());
                    ingredientStatement.setString(4, ingredient.getUnit().name());
                    ingredientStatement.setTimestamp(5, Timestamp.valueOf(ingredient.getUpdateDateTime()));
                    ingredientStatement.addBatch();
                }

                ingredientStatement.executeBatch();

                for (Ingredient ingredient : ingredients) {
                    if (ingredient.getPriceHistory() != null) {
                        for (PriceHistory price : ingredient.getPriceHistory()) {
                            priceStatement.setInt(1, ingredient.getId());
                            priceStatement.setDouble(2, price.getPrice());
                            priceStatement.setTimestamp(3, Timestamp.valueOf(price.getDate()));
                            priceStatement.addBatch();
                        }
                    }
                }

                priceStatement.executeBatch();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error saving ingredients: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing database connection: " + e.getMessage(), e);
        }

        return ingredients;
    }

    public List<PriceHistory> getPriceHistoryForIngredient(int ingredientId) {
        String query = "SELECT price, date FROM Price_History WHERE ingredient_id = ? ORDER BY date DESC";
        List<PriceHistory> priceHistory = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ingredientId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                priceHistory.add(new PriceHistory(
                        resultSet.getDouble("price"),
                        resultSet.getTimestamp("date").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving price history", e);
        }

        return priceHistory;
    }

    @Override
    public List<StockMovement> getStockMovementsByIngredientId(int ingredientId) {
        String query = "SELECT * FROM Stock_Movement WHERE ingredient_id = ? ORDER BY movement_date";
        List<StockMovement> movements = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, ingredientId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                StockMovement movement = new StockMovement(
                        resultSet.getInt("movement_id"),
                        resultSet.getInt("ingredient_id"),
                        MovementType.valueOf(resultSet.getString("movement_type")),
                        resultSet.getDouble("quantity"),
                        Unit.valueOf(resultSet.getString("unit")),
                        resultSet.getTimestamp("movement_date").toLocalDateTime()
                );
                movements.add(movement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving stock movements", e);
        }

        return movements;
    }

    @Override
    public void deleteIngredient(int id) {
        String query = "DELETE FROM Ingredient WHERE ingredient_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'ingrédient", e);
        }
    }

    @Override
    public List<Ingredient> filterIngredients(String name, Unit unit, Double minPrice, Double maxPrice, int page, int pageSize) {
        String query = "SELECT * FROM Ingredient WHERE 1=1";
        List<Object> parameters = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            query += " AND name ILIKE ?";
            parameters.add("%" + name + "%");
        }
        if (unit != null) {
            query += " AND unit = ?::unit_type";
            parameters.add(unit.name());
        }
        if (minPrice != null) {
            query += " AND unit_price >= ?";
            parameters.add(minPrice);
        }
        if (maxPrice != null) {
            query += " AND unit_price <= ?";
            parameters.add(maxPrice);
        }

        query += " LIMIT ? OFFSET ?";
        parameters.add(pageSize);
        parameters.add((page - 1) * pageSize);

        List<Ingredient> ingredients = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Ingredient ingredient = new Ingredient(
                        resultSet.getInt("ingredient_id"),
                        resultSet.getString("name"),
                        resultSet.getDouble("unit_price"),
                        Unit.valueOf(resultSet.getString("unit")),
                        resultSet.getTimestamp("update_datetime").toLocalDateTime(),
                        0
                );
                ingredients.add(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error filtering ingredients", e);
        }

        return ingredients;
    }

    @Override
    public void addStockMovement(StockMovement movement) {
        String query = "INSERT INTO Stock_Movement (ingredient_id, movement_type, quantity, unit, movement_date) VALUES (?, ?::movement_type, ?, ?::unit_type, ?)";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, movement.getIngredientId());
            statement.setString(2, movement.getMovementType().name());
            statement.setDouble(3, movement.getQuantity());
            statement.setString(4, movement.getUnit().name());
            statement.setTimestamp(5, java.sql.Timestamp.valueOf(movement.getMovementDate()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding stock movement", e);
        }
    }

    @Override
    public Ingredient findByIdAndPriceAndDateAndStockDate(int ingredientId, LocalDateTime priceDate, LocalDateTime stockDate) {
        String ingredientQuery = "SELECT * FROM Ingredient WHERE ingredient_id = ?";
        String priceQuery = "SELECT price FROM Price_History WHERE ingredient_id = ? AND date <= ? ORDER BY date DESC LIMIT 1";
        String stockQuery = "SELECT * FROM Stock_Movement WHERE ingredient_id = ? AND movement_date <= ?";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement ingredientStatement = connection.prepareStatement(ingredientQuery);
             PreparedStatement priceStatement = connection.prepareStatement(priceQuery);
             PreparedStatement stockStatement = connection.prepareStatement(stockQuery)) {

            ingredientStatement.setInt(1, ingredientId);
            ResultSet ingredientResultSet = ingredientStatement.executeQuery();

            if (ingredientResultSet.next()) {
                Ingredient ingredient = new Ingredient(
                        ingredientResultSet.getInt("ingredient_id"),
                        ingredientResultSet.getString("name"),
                        ingredientResultSet.getDouble("unit_price"),
                        Unit.valueOf(ingredientResultSet.getString("unit")),
                        ingredientResultSet.getTimestamp("update_datetime").toLocalDateTime(),
                        0
                );

                priceStatement.setInt(1, ingredientId);
                priceStatement.setTimestamp(2, Timestamp.valueOf(priceDate));
                ResultSet priceResultSet = priceStatement.executeQuery();

                if (priceResultSet.next()) {
                    double priceAtDate = priceResultSet.getDouble("price");
                    ingredient.setActualPrice(priceAtDate);
                }

                stockStatement.setInt(1, ingredientId);
                stockStatement.setTimestamp(2, Timestamp.valueOf(stockDate));
                ResultSet stockResultSet = stockStatement.executeQuery();

                double availableQuantity = 0;
                while (stockResultSet.next()) {
                    MovementType movementType = MovementType.valueOf(stockResultSet.getString("movement_type"));
                    double quantity = stockResultSet.getDouble("quantity");

                    if (movementType == MovementType.IN) {
                        availableQuantity += quantity;
                    } else if (movementType == MovementType.OUT) {
                        availableQuantity -= quantity;
                    }
                }

                ingredient.setAvailableQuantity(availableQuantity);

                return ingredient;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving ingredient with price and stock details", e);
        }

        return null;
    }

    @Override
    public void savePrices(int ingredientId, List<PriceHistory> prices) {
        String query = "INSERT INTO Price_History (ingredient_id, price, date) VALUES (?, ?, ?)";

        try (Connection connection = dataBaseSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (PriceHistory price : prices) {
                    statement.setInt(1, ingredientId);
                    statement.setDouble(2, price.getPrice());
                    statement.setTimestamp(3, Timestamp.valueOf(price.getDate()));
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error saving prices", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing database connection", e);
        }
    }

    @Override
    public void saveStockMovements(int ingredientId, List<StockMovement> movements) {
        String query = "INSERT INTO Stock_Movement (ingredient_id, movement_type, quantity, unit, movement_date) " +
                "VALUES (?, ?::movement_type, ?, ?::unit_type, ?)";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            connection.setAutoCommit(false);

            for (StockMovement movement : movements) {
                statement.setInt(1, ingredientId);
                statement.setString(2, movement.getMovementType().name());
                statement.setDouble(3, movement.getQuantity());
                statement.setString(4, movement.getUnit().name());
                statement.setTimestamp(5, Timestamp.valueOf(movement.getMovementDate()));
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            try (Connection connection = dataBaseSource.getConnection();){
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException("Error during rollback", ex);
            }
            throw new RuntimeException("Error saving stock movements", e);
        } finally {
            try (Connection connection = dataBaseSource.getConnection();) {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException("Error resetting auto-commit", e);
            }
        }
    }

    @Override
    public void updateCurrentPrice(int ingredientId, double price, LocalDateTime date) {
        String query = "UPDATE Ingredient SET unit_price = ?, update_datetime = ? WHERE ingredient_id = ?";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, price);
            statement.setTimestamp(2, Timestamp.valueOf(date));
            statement.setInt(3, ingredientId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating current price", e);
        }
    }
}