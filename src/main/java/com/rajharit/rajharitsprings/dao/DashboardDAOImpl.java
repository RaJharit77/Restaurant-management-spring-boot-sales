package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.config.DataBaseSource;
import com.rajharit.rajharitsprings.entities.BestSales;
import com.rajharit.rajharitsprings.entities.ProcessingTime;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DashboardDAOImpl implements DashboardDAO {
    private final DataBaseSource dataBaseSource;

    public DashboardDAOImpl(DataBaseSource dataBaseSource) {
        this.dataBaseSource = dataBaseSource;
    }

    @Override
    public void saveBestSalesData(List<BestSales> data) {
        String query = "INSERT INTO Best_Sales (dish_name, quantity_sold, total_amount, start_date, end_date, calculation_date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataBaseSource.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (BestSales item : data) {
                    statement.setString(1, item.getDishName());
                    statement.setInt(2, item.getQuantitySold());
                    statement.setDouble(3, item.getTotalAmount());
                    statement.setTimestamp(4, Timestamp.valueOf(item.getStartDate()));
                    statement.setTimestamp(5, Timestamp.valueOf(item.getEndDate()));
                    statement.setTimestamp(6, Timestamp.valueOf(item.getCalculationDate()));
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Error saving best sales data", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error managing database connection", e);
        }
    }

    @Override
    public List<BestSales> getBestSalesData() {
        String query = "SELECT * FROM Best_Sales WHERE start_date = ? AND end_date = ? ORDER BY quantity_sold DESC";
        List<BestSales> bestSales = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                BestSales data = new BestSales();
                data.setId(resultSet.getInt("id"));
                data.setDishName(resultSet.getString("dish_name"));
                data.setQuantitySold(resultSet.getInt("quantity_sold"));
                data.setTotalAmount(resultSet.getDouble("total_amount"));
                data.setCalculationDate(resultSet.getTimestamp("calculation_date").toLocalDateTime());
                bestSales.add(data);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving best sales data", e);
        }

        return bestSales;
    }

    @Override
    public void saveProcessingTimeData(ProcessingTime data) {
        String query = "INSERT INTO Processing_Time (dish_id, processing_time, time_unit, calculation_type, " +
                "start_date, end_date, calculation_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, data.getDishId());
            statement.setDouble(2, data.getProcessingTime());
            statement.setString(3, data.getTimeUnit());
            statement.setString(4, data.getCalculationType());
            statement.setTimestamp(5, Timestamp.valueOf(data.getStartDate()));
            statement.setTimestamp(6, Timestamp.valueOf(data.getEndDate()));
            statement.setTimestamp(7, Timestamp.valueOf(data.getCalculationDate()));

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving processing time data", e);
        }
    }

    @Override
    public ProcessingTime getProcessingTimeData(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                                String timeUnit, String calculationType) {
        String query = "SELECT * FROM Processing_Time WHERE dish_id = ? AND start_date = ? AND end_date = ? " +
                "AND time_unit = ? AND calculation_type = ?";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishId);
            statement.setTimestamp(2, Timestamp.valueOf(startDate));
            statement.setTimestamp(3, Timestamp.valueOf(endDate));
            statement.setString(4, timeUnit);
            statement.setString(5, calculationType);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                ProcessingTime data = new ProcessingTime();
                data.setId(resultSet.getInt("id"));
                data.setDishId(resultSet.getInt("dish_id"));
                data.setProcessingTime(resultSet.getDouble("processing_time"));
                data.setTimeUnit(resultSet.getString("time_unit"));
                data.setCalculationType(resultSet.getString("calculation_type"));
                data.setStartDate(resultSet.getTimestamp("start_date").toLocalDateTime());
                data.setEndDate(resultSet.getTimestamp("end_date").toLocalDateTime());
                data.setCalculationDate(resultSet.getTimestamp("calculation_date").toLocalDateTime());
                return data;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving processing time data", e);
        }

        return null;
    }

    @Override
    public List<BestSales> getBestSalesDataFromDB() {
        String query = "SELECT * FROM get_best_sales(?, ?, ?)";
        List<BestSales> bestSales = new ArrayList<>();

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                BestSales data = new BestSales();
                data.setDishName(resultSet.getString("dish_name"));
                data.setQuantitySold(resultSet.getInt("quantity_sold"));
                data.setTotalAmount(resultSet.getDouble("total_amount"));
                bestSales.add(data);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving best sales data from database function", e);
        }

        return bestSales;
    }

    @Override
    public double getProcessingTimeFromDB(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                          String timeUnit, String calculationType) {
        String query = "SELECT get_processing_time(?, ?, ?, ?, ?) AS processing_time";

        try (Connection connection = dataBaseSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, dishId);
            statement.setTimestamp(2, Timestamp.valueOf(startDate));
            statement.setTimestamp(3, Timestamp.valueOf(endDate));
            statement.setString(4, timeUnit);
            statement.setString(5, calculationType);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("processing_time");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving processing time from database function", e);
        }

        return 0;
    }
}