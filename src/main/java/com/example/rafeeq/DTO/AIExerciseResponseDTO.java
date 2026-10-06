package com.example.rafeeq.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIExerciseResponseDTO {

    private String goal;

    private String summary;

    private JsonNode exercises;
}