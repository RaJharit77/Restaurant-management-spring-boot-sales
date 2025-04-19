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
public class OrderDAOImpl implements OrderDAO {
    private final DataBaseSource dataBaseSource;
    private static final Logger logger = LoggerFactory.getLogger(OrderDAOImpl.class);

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
                order.setActualStatus(StatusType.valueOf(resultSet.getString("status")));

                order.setDishOrders(loadDishOrders(connection, order.getOrderId()));

                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all orders", e);
        }
        return orders;
    }

    private List<DishOrder> loadDishOrders(Connection connection, int orderId) throws SQLException {
        String query = "SELECT * FROM dish_order WHERE order_id = ?";
        List<DishOrder> dishOrders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
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

                dishOrder.setStatusHistory(loadDishOrderStatusHistory(connection, dishOrder.getDishOrderId()));

                dishOrders.add(dishOrder);
            }
        }
        return dishOrders;
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
                order.setActualStatus(StatusType.valueOf(resultSet.getString("status")));
                return order;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order", e);
        }
        return null;
    }

    @Override
    public List<Order> findByStatus(StatusType status) {
        String query = "SELECT * FROM \"Order\" WHERE status = ?::status_type";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, status.name());
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setReference(rs.getString("reference"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                order.setActualStatus(status);

                List<DishOrder> dishOrders = loadDishOrders(connection, order.getOrderId());
                order.setDishOrders(dishOrders);

                orders.add(order);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by status", e);
        }

        return orders;
    }

    @Override
    public Order findByReference(String reference) {
        try {
            String orderQuery = "SELECT * FROM \"Order\" WHERE reference = ?";
            try (Connection connection = dataBaseSource.getConnection();
                 PreparedStatement statement = connection.prepareStatement(orderQuery)) {

                statement.setString(1, reference);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setReference(rs.getString("reference"));
                    order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    order.setActualStatus(StatusType.valueOf(rs.getString("status")));
                    return order;
                }
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding order by reference: " + reference, e);
            throw new RuntimeException("Database error finding order", e);
        }
    }

    @Override
    public Order save(Order order) {
        String query = "INSERT INTO \"Order\" (reference, created_at) VALUES (?, ?) RETURNING order_id";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, order.getReference());
            statement.setTimestamp(2, Timestamp.valueOf(order.getCreatedAt()));

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                order.setOrderId(rs.getInt("order_id"));

                this.updateStatus(order.getOrderId(), order.getActualStatus());

                return order;
            }
            throw new RuntimeException("Échec de l'insertion");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur base de données : " + e.getMessage(), e);
        }
    }

    private void loadOrderStatusHistory(Connection connection, Order order) throws SQLException {
        String query = "SELECT * FROM Order_Status WHERE order_id = ? ORDER BY changed_at";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, order.getOrderId());
            ResultSet rs = statement.executeQuery();

            List<OrderStatus> statusHistory = new ArrayList<>();
            while (rs.next()) {
                statusHistory.add(new OrderStatus(
                        rs.getInt("order_status_id"),
                        StatusType.valueOf(rs.getString("status")),
                        rs.getTimestamp("changed_at").toLocalDateTime()
                ));
            }
            order.setStatusHistory(statusHistory);
        }
    }

    private List<DishOrderStatus> loadDishOrderStatusHistory(Connection connection, int dishOrderId) throws SQLException {
        String query = "SELECT * FROM dish_order_status " +
                "WHERE dish_order_id = ? " +
                "ORDER BY changed_at";

        List<DishOrderStatus> statusHistory = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishOrderId);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                DishOrderStatus status = new DishOrderStatus();
                status.setDishOrderStatusId(rs.getInt("dish_order_status_id"));
                status.setStatus(StatusType.valueOf(rs.getString("status")));
                status.setChangedAt(rs.getTimestamp("changed_at").toLocalDateTime());

                statusHistory.add(status);
            }
        }

        return statusHistory;
    }

    @Override
    public void updateStatus(int orderId, StatusType status) {
        String query = "UPDATE \"Order\" SET status = ?::status_type WHERE order_id = ?";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status.name());
            statement.setInt(2, orderId);
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("No order found with id: " + orderId);
            }
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
                order.setActualStatus(StatusType.valueOf(resultSet.getString("status")));
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