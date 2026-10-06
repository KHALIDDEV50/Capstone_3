package com.example.rafeeq.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AICompleteResponseDTO {

    private AINutritionResponseDTO nutrition;

    private AIExerciseResponseDTO exercise;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AINutritionResponseDTO {

        private String summary;

        private String reason;

        private JsonNode items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AIExerciseResponseDTO {

        private String goal;

        private String summary;

        private JsonNode exercises;
    }
}