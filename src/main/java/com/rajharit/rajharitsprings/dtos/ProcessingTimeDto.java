package com.rajharit.rajharitsprings.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProcessingTimeDto {
    private double processingTime;
    private String timeUnit;
    private String calculationType;
    private LocalDateTime lastUpdated;
}
