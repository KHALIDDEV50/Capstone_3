package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthPlanStatusDTO {

    private String status;

    private Boolean hasHealthProfile;

    private Boolean hasNutritionPlan;

    private Boolean hasExercisePlan;

    private Boolean hasVitalSigns;

    private String exerciseRisk;

    private String message;
}