package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.*;
import com.rajharit.rajharitspringpointvente.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishOrderDAOImpl implements DishOrderDAO {
    private final DataBaseSource dataBaseSource;

    public DishOrderDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
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
        String query = "SELECT * FROM Dish_Order WHERE order_id = ?";
        List<DishOrder> dishOrders = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dishOrders.add(mapDishOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving dish orders by order id", e);
        }
        return dishOrders;
    }

    @Override
    public DishOrder save(DishOrder dishOrder) {
        String query = "INSERT INTO Dish_Order (order_id, dish_id, quantity, status) VALUES (?, ?, ?, ?::status_type) RETURNING dish_order_id";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishOrder.getOrder().getOrderId());
            statement.setInt(2, dishOrder.getDish().getId());
            statement.setInt(3, dishOrder.getQuantity());
            statement.setString(4, dishOrder.getStatus().name());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                dishOrder.setDishOrderId(resultSet.getInt("dish_order_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving dish order", e);
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
    public void updateStatus(int dishOrderId, StatusType status) {
        String query = "UPDATE Dish_Order SET status = ?::dish_status WHERE dish_order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            statement.setInt(2, dishOrderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating dish order status", e);
        }
    }

    private DishOrder mapDishOrder(ResultSet resultSet) throws SQLException {
        DishOrder dishOrder = new DishOrder();
        dishOrder.setDishOrderId(resultSet.getInt("dish_order_id"));
        dishOrder.setDish(new Dish(resultSet.getInt("dish_id"), null, 0, null));
        dishOrder.setQuantity(resultSet.getInt("quantity"));
        dishOrder.setStatus(StatusType.valueOf(resultSet.getString("status")));
        return dishOrder;
    }
}