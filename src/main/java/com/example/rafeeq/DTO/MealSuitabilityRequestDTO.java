package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MealSuitabilityRequestDTO {

    @NotEmpty(message = "Meal cannot be empty")
    private String meal;
}