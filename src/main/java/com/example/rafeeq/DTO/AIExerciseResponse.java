package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIExerciseResponse {

    private String exerciseGoal;

    private String exercises;

    private String exerciseSummary;
}