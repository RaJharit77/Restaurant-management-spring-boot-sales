package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.DishOrderStatus;
import com.rajharit.rajharitsprings.entities.StatusType;
import com.rajharit.rajharitsprings.config.DataBaseSource;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DishOrderStatusDAOImpl implements DishOrderStatusDAO {
    private final DataBaseSource dataSource;

    public DishOrderStatusDAOImpl(DataBaseSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public DishOrderStatus save(DishOrderStatus dishOrderStatus, int dishOrderId) {
        String sql = "INSERT INTO Dish_Order_Status (dish_order_id, status, changed_at) " +
                "VALUES (?, ?::status_type, ?) RETURNING dish_order_status_id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishOrderId);
            stmt.setString(2, dishOrderStatus.getStatus().name());
            stmt.setTimestamp(3, Timestamp.valueOf(dishOrderStatus.getChangedAt()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dishOrderStatus.setDishOrderStatusId(rs.getInt("dish_order_status_id"));
            }
            return dishOrderStatus;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save dish order status", e);
        }
    }

    @Override
    public List<DishOrderStatus> findByDishOrderId(int dishOrderId) {
        String sql = "SELECT * FROM Dish_Order_Status WHERE dish_order_id = ? ORDER BY changed_at";
        List<DishOrderStatus> statusHistory = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, dishOrderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                DishOrderStatus status = new DishOrderStatus(
                        rs.getInt("dish_order_status_id"),
                        StatusType.valueOf(rs.getString("status")),
                        rs.getTimestamp("changed_at").toLocalDateTime()
                );
                statusHistory.add(status);
            }
            return statusHistory;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find dish order status history", e);
        }
    }
}