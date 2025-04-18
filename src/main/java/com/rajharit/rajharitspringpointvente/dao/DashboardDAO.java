package com.rajharit.rajharitspringpointvente.dao;

import com.rajharit.rajharitspringpointvente.entities.BestSales;
import com.rajharit.rajharitspringpointvente.entities.ProcessingTime;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardDAO {
    List<BestSales> getBestSalesDataFromDB(LocalDateTime startDate, LocalDateTime endDate, int limit);

    double getProcessingTimeFromDB(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                   String timeUnit, String calculationType);

    void saveBestSalesData(List<BestSales> data);
    List<BestSales> getBestSalesData(LocalDateTime startDate, LocalDateTime endDate);

    void saveProcessingTimeData(ProcessingTime data);
    ProcessingTime getProcessingTimeData(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                             String timeUnit, String calculationType);
}