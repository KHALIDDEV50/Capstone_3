package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIPlanResponseDTO {

    private NutritionPlanResponseDTO nutritionPlan;

    private ExercisePlanResponseDTO exercisePlan;
}