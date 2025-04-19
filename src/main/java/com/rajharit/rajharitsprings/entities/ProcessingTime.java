package com.rajharit.rajharitsprings.entities;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProcessingTime {
    private int id;
    private int dishId;
    private double processingTime;
    private String timeUnit;
    private String calculationType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime calculationDate;
}
