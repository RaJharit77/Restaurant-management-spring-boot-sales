package com.rajharit.rajharitsprings.dtos;

import com.rajharit.rajharitsprings.entities.StatusType;
import lombok.Data;

@Data
public class StatusUpdateDto {
    private StatusType status;
}
