package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.DurationUnit;
import lombok.Data;

@Data
public class BestProcessingTimeElement {
    private String salesPoint;
    private String dish;
    private Double preparationDuration;
    private DurationUnit durationUnit;
}
