package com.rajharit.rajharitsprings.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BestSalesResponse {
    private LocalDateTime updatedAt;
    private List<SalesElement> sales;
}

