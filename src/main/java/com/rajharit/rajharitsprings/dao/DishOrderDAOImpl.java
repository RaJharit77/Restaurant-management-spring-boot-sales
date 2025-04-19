package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.*;
import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishOrderDAOImpl implements DishOrderDAO {
    private final DataBaseSource dataBaseSource;
    private DishDAO dishDAO;
    private static final Logger logger = LoggerFactory.getLogger(DishOrderDAOImpl.class);

    public DishOrderDAOImpl(DataBaseSource dataBaseSource, DishDAO dishDAO) {
        this.dataBaseSource = dataBaseSource;
        this.dishDAO = dishDAO;
    }

    @Override
    public DishOrder findById(int id) {
        String query = "SELECT * FROM Dish_Order WHERE dish_order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return mapDishOrder(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish order", e);
        }
        return null;
    }

    @Override
    public List<DishOrder> findByOrderId(int orderId) {
        String query = "SELECT dish_order_id, order_id, dish_id, quantity, status " +
                "FROM dish_order WHERE order_id = ?";

        List<DishOrder> dishOrders = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, orderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                DishOrder dishOrder = new DishOrder();
                dishOrder.setDishOrderId(rs.getInt("dish_order_id"));
                dishOrder.setQuantity(rs.getInt("quantity"));
                dishOrder.setStatus(StatusType.valueOf(rs.getString("status")));

                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dishOrder.setDish(dish);

                dishOrders.add(dishOrder);
            }
            return dishOrders;
        } catch (SQLException e) {
            logger.error("Error finding dish orders by order id: " + orderId, e);
            throw new RuntimeException("Error finding dish orders by order id", e);
        }
    }

    @Override
    public DishOrder save(DishOrder dishOrder) {
        String query = "INSERT INTO Dish_Order (order_id, dish_id, quantity, status) VALUES (?, ?, ?, ?::status_type) RETURNING dish_order_id";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (dishOrder.getOrder() == null || dishOrder.getDish() == null) {
                throw new RuntimeException("Order or Dish is null");
            }

            statement.setInt(1, dishOrder.getOrder().getOrderId());
            statement.setInt(2, dishOrder.getDish().getId());
            statement.setInt(3, dishOrder.getQuantity());
            statement.setString(4, dishOrder.getStatus().name());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                dishOrder.setDishOrderId(resultSet.getInt("dish_order_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish order: " + e.getMessage(), e);
        }
        return dishOrder;
    }

    @Override
    public List<DishOrder> findByDishId(int dishId) {
        String query = "SELECT * FROM Dish_Order WHERE dish_id = ?";
        List<DishOrder> dishOrders = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dishOrders.add(mapDishOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish orders by dish id", e);
        }
        return dishOrders;
    }

    @Override
    public void updateStatus(DishOrder dishOrder) {
        String query = "UPDATE Dish_Order SET quantity = ?, status = ?::status_type WHERE dish_order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, dishOrder.getQuantity());
            statement.setString(2, dishOrder.getStatus().name());
            statement.setInt(3, dishOrder.getDishOrderId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new RuntimeException("No dish order updated");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish order status", e);
        }
    }

    private DishOrder mapDishOrder(ResultSet resultSet) throws SQLException {
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDishOrderId(resultSet.getInt("dish_order_id"));

        Dish dish = new Dish();
        dish.setId(resultSet.getInt("dish_id"));
        /*dish.setName(resultSet.getString("name"));
        dish.setUnitPrice(resultSet.getDouble("unit_price"));*/

        dishOrder.setDish(dish);
        dishOrder.setQuantity(resultSet.getInt("quantity"));
        dishOrder.setStatus(StatusType.valueOf(resultSet.getString("status")));
        return dishOrder;
    }
}