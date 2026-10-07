package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.AlternativeRequestDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Service.AIPlanService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/ai")
@AllArgsConstructor
public class AIController {

    private final AIPlanService aiPlanService;


    // Generate Nutrition Plan
    @PostMapping("/nutrition/{userId}")
    public ResponseEntity<?> generateNutritionPlan(
            @PathVariable Integer userId) {

        NutritionPlan nutritionPlan = aiPlanService.generateNutritionPlan(userId);

        return ResponseEntity.status(200).body(nutritionPlan);
    }


    // Generate Exercise Plan
    @PostMapping("/exercise/{userId}")
    public ResponseEntity<?> generateExercisePlan(
            @PathVariable Integer userId) {

        ExercisePlan exercisePlan =aiPlanService.generateExercisePlan(userId);

        return ResponseEntity.status(200).body(exercisePlan);
    }


    // Nutrition Alternative
    @PostMapping("/nutrition/{userId}/alternative")
    public ResponseEntity<?> nutritionAlternative(
            @PathVariable Integer userId,
            @RequestBody @Valid AlternativeRequestDTO request) {

        return ResponseEntity.status(200).body(aiPlanService.generateNutritionAlternative(userId,request));
    }


    // Exercise Alternative
    @PostMapping("/exercise/{userId}/alternative")
    public ResponseEntity<?> exerciseAlternative(@PathVariable Integer userId, @RequestBody @Valid AlternativeRequestDTO request) {

        return ResponseEntity.status(200).body(aiPlanService.generateExerciseAlternative(userId,request));
    }
}