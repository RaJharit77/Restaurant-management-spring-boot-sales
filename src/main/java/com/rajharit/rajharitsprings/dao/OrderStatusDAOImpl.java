package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.*;
import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderStatusDAOImpl implements OrderStatusDAO {
    private final DataBaseSource dataBaseSource;

    public OrderStatusDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @Override
    public List<OrderStatus> findByOrderId(int orderId) {
        String query = "SELECT * FROM Order_Status WHERE order_id = ?";
        List<OrderStatus> orderStatuses = new ArrayList<>();
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                OrderStatus orderStatus = new OrderStatus(
                        resultSet.getInt("order_status_id"),
                        StatusType.valueOf(resultSet.getString("status")),
                        resultSet.getTimestamp("changed_at").toLocalDateTime()
                );
                orderStatuses.add(orderStatus);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving order status history", e);
        }
        return orderStatuses;
    }

    @Override
    public OrderStatus save(OrderStatus orderStatus, int orderId) {
        String query = "INSERT INTO Order_Status (order_id, status, changed_at) VALUES (?, ?::status_type, ?) RETURNING order_status_id";
        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            statement.setString(2, orderStatus.getStatus().name());
            statement.setTimestamp(3, Timestamp.valueOf(orderStatus.getChangedAt()));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                orderStatus.setOrderStatusId(resultSet.getInt("order_status_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order status", e);
        }
        return orderStatus;
    }
}