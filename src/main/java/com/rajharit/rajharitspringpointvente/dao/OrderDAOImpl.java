package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.*;
import com.rajharit.rajharitspringpointvente.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderDAOImpl implements OrderDAO {
    private final DataBaseSource dataBaseSource;

    public OrderDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @Override
    public List<Order> getAll() {
        String query = "SELECT * FROM \"Order\"";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setReference(resultSet.getString("reference"));
                order.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                order.setStatus(StatusType.valueOf(resultSet.getString("status")));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders", e);
        }
        return orders;
    }

    @Override
    public Order findById(int id) {
        String query = "SELECT * FROM \"Order\" WHERE order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setReference(resultSet.getString("reference"));
                order.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                order.setStatus(StatusType.valueOf(resultSet.getString("status")));
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order", e);
        }
        return null;
    }

    @Override
    public Order findByReference(String reference) {
        String query = "SELECT * FROM \"Order\" WHERE reference = ?";
        String dishOrderQuery = "SELECT * FROM Dish_Order WHERE order_id = ?";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             PreparedStatement dishOrderStatement = connection.prepareStatement(dishOrderQuery)) {

            statement.setString(1, reference);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setReference(resultSet.getString("reference"));
                order.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                order.setStatus(StatusType.valueOf(resultSet.getString("status")));

                dishOrderStatement.setInt(1, order.getOrderId());
                ResultSet dishOrderResult = dishOrderStatement.executeQuery();

                List<DishOrder> dishOrders = new ArrayList<>();
                while (dishOrderResult.next()) {
                    DishOrder dishOrder = new DishOrder();
                    dishOrder.setDishOrderId(dishOrderResult.getInt("dish_order_id"));
                    dishOrder.setQuantity(dishOrderResult.getInt("quantity"));
                    dishOrder.setStatus(StatusType.valueOf(dishOrderResult.getString("status")));
                    dishOrders.add(dishOrder);
                }

                order.setDishOrders(dishOrders);
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order by reference", e);
        }
        return null;
    }

    @Override
    public Order save(Order order) {
        Order existingOrder = findByReference(order.getReference());
        if (existingOrder != null) {
            throw new RuntimeException("La référence de commande existe déjà : " + order.getReference());
        }

        String query = "INSERT INTO \"Order\" (reference, created_at, status) VALUES (?, ?, ?::status_type) RETURNING order_id";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, order.getReference());
            statement.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));
            statement.setString(3, order.getStatus().name());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                order.setOrderId(resultSet.getInt("order_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
        return order;
    }

    @Override
    public void updateStatus(int orderId, StatusType status) {
        String query = "UPDATE \"Order\" SET status = ?::status_type WHERE order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            statement.setInt(2, orderId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order status", e);
        }
    }

    @Override
    public List<Order> findByCustomerId(int customerId) {
        String query = "SELECT * FROM \"Order\" WHERE customer_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, customerId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setReference(resultSet.getString("reference"));
                order.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                order.setStatus(StatusType.valueOf(resultSet.getString("status")));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving orders by customer id", e);
        }
        return orders;
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM \"Order\" WHERE order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }
}