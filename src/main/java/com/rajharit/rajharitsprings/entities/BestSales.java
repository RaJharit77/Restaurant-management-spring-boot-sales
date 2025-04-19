package com.rajharit.rajharitsprings.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BestSales {
    private int id;
    private String dishName;
    private int quantitySold;
    private double totalAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime calculationDate;
}