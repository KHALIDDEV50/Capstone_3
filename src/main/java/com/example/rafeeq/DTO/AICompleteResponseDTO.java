package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AICompleteResponseDTO {

    private AINutritionResponseDTO nutrition;

    private AIExerciseResponseDTO exercise;
}