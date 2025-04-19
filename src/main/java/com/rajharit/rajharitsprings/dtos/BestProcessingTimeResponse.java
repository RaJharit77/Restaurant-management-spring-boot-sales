package com.rajharit.rajharitsprings.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BestProcessingTimeResponse {
    private LocalDateTime updatedAt;
    private List<BestProcessingTimeElement> bestProcessingTimes;
}
