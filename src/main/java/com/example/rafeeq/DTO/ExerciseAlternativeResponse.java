package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseAlternativeResponse {

    private String originalExercise;
    private String alternativeExercise;
    private String reason;
}