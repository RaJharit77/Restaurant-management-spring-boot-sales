package com.rajharit.rajharitspringpointvente.controllers;

import com.rajharit.rajharitspringpointvente.dtos.BestSalesDto;
import com.rajharit.rajharitspringpointvente.dtos.ProcessingTimeDto;
import com.rajharit.rajharitspringpointvente.services.DashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/bestSales")
    public ResponseEntity<List<BestSalesDto>> getBestSales(
            @RequestParam int limit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<BestSalesDto> bestSales = dashboardService.getBestSales(limit, startDate, endDate);
        return ResponseEntity.ok(bestSales);
    }

    @GetMapping("/dishes/{dishId}/processingTime")
    public ResponseEntity<ProcessingTimeDto> getDishProcessingTime(
            @PathVariable int dishId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "seconds") String timeUnit,
            @RequestParam(required = false, defaultValue = "average") String calculationType) {

        ProcessingTimeDto processingTime = dashboardService.getDishProcessingTime(
                dishId, startDate, endDate, timeUnit, calculationType);

        return ResponseEntity.ok(processingTime);
    }

    @PostMapping("/bestSales")
    public ResponseEntity<Void> calculateBestSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        dashboardService.calculateAndSaveBestSalesData(startDate, endDate);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/dishes/{dishId}/processingTime")
    public ResponseEntity<Void> calculateProcessingTime(
            @PathVariable int dishId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "seconds") String timeUnit,
            @RequestParam(required = false, defaultValue = "average") String calculationType) {

        dashboardService.calculateAndSaveProcessingTimeData(
                dishId, startDate, endDate, timeUnit, calculationType);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/preCalculated/bestSales")
    public ResponseEntity<List<BestSalesDto>> getPreCalculatedBestSales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<BestSalesDto> bestSales = dashboardService.getPreCalculatedBestSales(startDate, endDate);
        return ResponseEntity.ok(bestSales);
    }

    @GetMapping("/preCalculated/dishes/{dishId}/processingTime")
    public ResponseEntity<ProcessingTimeDto> getPreCalculatedProcessingTime(
            @PathVariable int dishId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "seconds") String timeUnit,
            @RequestParam(required = false, defaultValue = "average") String calculationType) {

        ProcessingTimeDto processingTime = dashboardService.getPreCalculatedProcessingTime(
                dishId, startDate, endDate, timeUnit, calculationType);

        return ResponseEntity.ok(processingTime);
    }

    @GetMapping("/dbCalculated/bestSales")
    public ResponseEntity<List<BestSalesDto>> getBestSalesFromDB(
            @RequestParam int limit,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<BestSalesDto> bestSales = dashboardService.getBestSalesFromDB(limit, startDate, endDate);
        return ResponseEntity.ok(bestSales);
    }

    @GetMapping("/dbCalculated/dishes/{dishId}/processingTime")
    public ResponseEntity<ProcessingTimeDto> getProcessingTimeFromDB(
            @PathVariable int dishId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "seconds") String timeUnit,
            @RequestParam(required = false, defaultValue = "average") String calculationType) {

        ProcessingTimeDto processingTime = dashboardService.getProcessingTimeFromDB(
                dishId, startDate, endDate, timeUnit, calculationType);

        return ResponseEntity.ok(processingTime);
    }
}