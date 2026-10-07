package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AINutritionResponse {

    private String nutritionSummary;

    private String nutritionItems;

    private String nutritionReason;
}