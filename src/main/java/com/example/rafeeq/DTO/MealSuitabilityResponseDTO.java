package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealSuitabilityResponseDTO {

    private String status;
    private String summary;
    private List<String> issues;
    private String suggestion;
}