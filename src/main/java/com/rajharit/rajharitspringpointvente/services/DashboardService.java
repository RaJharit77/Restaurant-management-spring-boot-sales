package com.rajharit.rajharitspringpointvente.services;

import com.rajharit.rajharitspringpointvente.dtos.BestSalesDto;
import com.rajharit.rajharitspringpointvente.dtos.ProcessingTimeDto;
import com.rajharit.rajharitspringpointvente.entities.*;
import com.rajharit.rajharitspringpointvente.dao.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {
    private final OrderDAO orderDAO;
    private final DishOrderDAO dishOrderDAO;
    private final DishOrderStatusDAO dishOrderStatusDAO;
    private final DashboardDAO dashboardDAO;

    public DashboardService(OrderDAO orderDAO, DishOrderDAO dishOrderDAO, DishOrderStatusDAO dishOrderStatusDAO, DashboardDAO dashboardDAO) {
        this.orderDAO = orderDAO;
        this.dishOrderDAO = dishOrderDAO;
        this.dishOrderStatusDAO = dishOrderStatusDAO;
        this.dashboardDAO = dashboardDAO;
    }

    public List<BestSalesDto> getBestSales(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> finishedOrders = orderDAO.getAll().stream()
                .filter(order -> order.getStatus() == StatusType.FINISHED)
                .filter(order -> !order.getCreatedAt().isBefore(startDate) && !order.getCreatedAt().isAfter(endDate))
                .collect(Collectors.toList());

        Map<Dish, Integer> dishQuantityMap = new HashMap<>();
        Map<Dish, Double> dishAmountMap = new HashMap<>();

        for (Order order : finishedOrders) {
            List<DishOrder> dishOrders = dishOrderDAO.findByOrderId(order.getOrderId());
            for (DishOrder dishOrder : dishOrders) {
                Dish dish = dishOrder.getDish();
                double dishPrice = dish.getUnitPrice();
                int quantity = dishOrder.getQuantity();

                dishQuantityMap.merge(dish, quantity, Integer::sum);

                dishAmountMap.merge(dish, dishPrice * quantity, Double::sum);
            }
        }

        return dishQuantityMap.entrySet().stream()
                .sorted(Map.Entry.<Dish, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    BestSalesDto dto = new BestSalesDto();
                    dto.setDishName(entry.getKey().getName());
                    dto.setQuantitySold(entry.getValue());
                    dto.setTotalAmount(dishAmountMap.get(entry.getKey()));
                    dto.setLastUpdated(LocalDateTime.now());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ProcessingTimeDto getDishProcessingTime(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                                   String timeUnit, String calculationType) {
        List<DishOrder> dishOrders = dishOrderDAO.findByDishId(dishId).stream()
                .filter(dishOrder -> dishOrder.getStatus() == StatusType.FINISHED)
                .filter(dishOrder -> {
                    Optional<DishOrderStatus> firstStatus = dishOrderStatusDAO.findByDishOrderId(dishOrder.getDishOrderId())
                            .stream()
                            .filter(dos -> dos.getStatus() == StatusType.IN_PROGRESS)
                            .findFirst();

                    return firstStatus.isPresent() &&
                            !firstStatus.get().getChangedAt().isBefore(startDate) &&
                            !firstStatus.get().getChangedAt().isAfter(endDate);
                })
                .collect(Collectors.toList());

        List<Long> processingTimesInSeconds = new ArrayList<>();

        for (DishOrder dishOrder : dishOrders) {
            List<DishOrderStatus> statusHistory = dishOrderStatusDAO.findByDishOrderId(dishOrder.getDishOrderId());

            Optional<DishOrderStatus> inProgressStatus = statusHistory.stream()
                    .filter(dos -> dos.getStatus() == StatusType.IN_PROGRESS)
                    .findFirst();

            Optional<DishOrderStatus> finishedStatus = statusHistory.stream()
                    .filter(dos -> dos.getStatus() == StatusType.FINISHED)
                    .findFirst();

            if (inProgressStatus.isPresent() && finishedStatus.isPresent()) {
                long duration = ChronoUnit.SECONDS.between(
                        inProgressStatus.get().getChangedAt(),
                        finishedStatus.get().getChangedAt());
                processingTimesInSeconds.add(duration);
            }
        }

        if (processingTimesInSeconds.isEmpty()) {
            throw new RuntimeException("No processing time data available for the given parameters");
        }

        double result;
        String unit;

        switch (timeUnit.toLowerCase()) {
            case "minutes":
                result = convertToMinutes(processingTimesInSeconds, calculationType);
                unit = "minutes";
                break;
            case "hours":
                result = convertToHours(processingTimesInSeconds, calculationType);
                unit = "hours";
                break;
            default:
                result = convertToSeconds(processingTimesInSeconds, calculationType);
                unit = "seconds";
        }

        ProcessingTimeDto dto = new ProcessingTimeDto();
        dto.setProcessingTime(result);
        dto.setTimeUnit(unit);
        dto.setCalculationType(calculationType);
        dto.setLastUpdated(LocalDateTime.now());

        return dto;
    }

    private double convertToSeconds(List<Long> times, String calculationType) {
        switch (calculationType.toLowerCase()) {
            case "minimum":
                return times.stream().min(Long::compare).orElse(0L);
            case "maximum":
                return times.stream().max(Long::compare).orElse(0L);
            default: // average
                return times.stream().mapToLong(Long::longValue).average().orElse(0);
        }
    }

    private double convertToMinutes(List<Long> times, String calculationType) {
        switch (calculationType.toLowerCase()) {
            case "minimum":
                return times.stream().min(Long::compare).orElse(0L) / 60.0;
            case "maximum":
                return times.stream().max(Long::compare).orElse(0L) / 60.0;
            default:
                return times.stream().mapToLong(Long::longValue).average().orElse(0) / 60.0;
        }
    }

    private double convertToHours(List<Long> times, String calculationType) {
        switch (calculationType.toLowerCase()) {
            case "minimum":
                return times.stream().min(Long::compare).orElse(0L) / 3600.0;
            case "maximum":
                return times.stream().max(Long::compare).orElse(0L) / 3600.0;
            default: // average
                return times.stream().mapToLong(Long::longValue).average().orElse(0) / 3600.0;
        }
    }

    @Transactional
    public void calculateAndSaveBestSalesData(LocalDateTime startDate, LocalDateTime endDate) {
        List<BestSalesDto> bestSales = getBestSales(Integer.MAX_VALUE, startDate, endDate);

        List<BestSales> dataToSave = bestSales.stream()
                .map(dto -> {
                    BestSales data = new BestSales();
                    data.setDishName(dto.getDishName());
                    data.setQuantitySold(dto.getQuantitySold());
                    data.setTotalAmount(dto.getTotalAmount());
                    data.setStartDate(startDate);
                    data.setEndDate(endDate);
                    data.setCalculationDate(LocalDateTime.now());
                    return data;
                })
                .collect(Collectors.toList());

        dashboardDAO.saveBestSalesData(dataToSave);
    }

    @Transactional
    public void calculateAndSaveProcessingTimeData(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                                   String timeUnit, String calculationType) {
        ProcessingTimeDto dto = getDishProcessingTime(dishId, startDate, endDate, timeUnit, calculationType);

        ProcessingTime data = new ProcessingTime();
        data.setDishId(dishId);
        data.setProcessingTime(dto.getProcessingTime());
        data.setTimeUnit(timeUnit);
        data.setCalculationType(calculationType);
        data.setStartDate(startDate);
        data.setEndDate(endDate);
        data.setCalculationDate(LocalDateTime.now());

        dashboardDAO.saveProcessingTimeData(data);
    }

    public List<BestSalesDto> getPreCalculatedBestSales(LocalDateTime startDate, LocalDateTime endDate) {
        List<BestSales> data = dashboardDAO.getBestSalesData(startDate, endDate);

        return data.stream()
                .map(d -> {
                    BestSalesDto dto = new BestSalesDto();
                    dto.setDishName(d.getDishName());
                    dto.setQuantitySold(d.getQuantitySold());
                    dto.setTotalAmount(d.getTotalAmount());
                    dto.setLastUpdated(d.getCalculationDate());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ProcessingTimeDto getPreCalculatedProcessingTime(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                                            String timeUnit, String calculationType) {
        ProcessingTime data = dashboardDAO.getProcessingTimeData(dishId, startDate, endDate, timeUnit, calculationType);

        if (data == null) {
            throw new RuntimeException("No pre-calculated data available for the given parameters");
        }

        ProcessingTimeDto dto = new ProcessingTimeDto();
        dto.setProcessingTime(data.getProcessingTime());
        dto.setTimeUnit(data.getTimeUnit());
        dto.setCalculationType(data.getCalculationType());
        dto.setLastUpdated(data.getCalculationDate());

        return dto;
    }

    public List<BestSalesDto> getBestSalesFromDB(int limit, LocalDateTime startDate, LocalDateTime endDate) {
        List<BestSales> data = dashboardDAO.getBestSalesDataFromDB(startDate, endDate, limit);

        return data.stream()
                .map(d -> {
                    BestSalesDto dto = new BestSalesDto();
                    dto.setDishName(d.getDishName());
                    dto.setQuantitySold(d.getQuantitySold());
                    dto.setTotalAmount(d.getTotalAmount());
                    dto.setLastUpdated(LocalDateTime.now());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public ProcessingTimeDto getProcessingTimeFromDB(int dishId, LocalDateTime startDate, LocalDateTime endDate,
                                                     String timeUnit, String calculationType) {
        double processingTime = dashboardDAO.getProcessingTimeFromDB(
                dishId, startDate, endDate, timeUnit, calculationType);

        ProcessingTimeDto dto = new ProcessingTimeDto();
        dto.setProcessingTime(processingTime);
        dto.setTimeUnit(timeUnit);
        dto.setCalculationType(calculationType);
        dto.setLastUpdated(LocalDateTime.now());

        return dto;
    }
}