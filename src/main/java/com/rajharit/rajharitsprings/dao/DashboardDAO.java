package com.rajharit.rajharitsprings.dao;

import com.rajharit.rajharitsprings.entities.BestSales;
import com.rajharit.rajharitsprings.entities.ProcessingTime;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardDAO {
    List<BestSales> getBestSalesDataFromDB();

    double getProcessingTimeFromDB(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                   String timeUnit, String calculationType);

    void saveBestSalesData(List<BestSales> data);
    List<BestSales> getBestSalesData();

    void saveProcessingTimeData(ProcessingTime data);
    ProcessingTime getProcessingTimeData(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                             String timeUnit, String calculationType);
}