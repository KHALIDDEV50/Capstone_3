package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.AIPlanResponseDTO;
import com.example.rafeeq.DTO.AlternativeRequestDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Service.AIPlanService;
import com.example.rafeeq.Service.AIService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/ai")
@AllArgsConstructor
public class AIController {

    private final AIService aiService;
    private final AIPlanService aiPlanService;

    // ==================== Evidence-based full plan (nutrition + exercise) ====================

    @PostMapping("/generate-plan/{userId}")
    public ResponseEntity<AIPlanResponseDTO> generatePlan(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(aiService.generatePlans(userId));
    }

    // ==================== Separate nutrition / exercise plans ====================

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

        ExercisePlan exercisePlan = aiPlanService.generateExercisePlan(userId);

        return ResponseEntity.status(200).body(exercisePlan);
    }

    // ==================== Alternatives (replace one item in the saved plan) ====================

    // Nutrition Alternative
    @PostMapping("/nutrition/{userId}/alternative")
    public ResponseEntity<?> nutritionAlternative(
            @PathVariable Integer userId,
            @RequestBody @Valid AlternativeRequestDTO request) {

        return ResponseEntity.status(200).body(aiPlanService.generateNutritionAlternative(userId, request));
    }


    // Exercise Alternative
    @PostMapping("/exercise/{userId}/alternative")
    public ResponseEntity<?> exerciseAlternative(
            @PathVariable Integer userId,
            @RequestBody @Valid AlternativeRequestDTO request) {

        return ResponseEntity.status(200).body(aiPlanService.generateExerciseAlternative(userId, request));
    }

    // ==================== Meal swap / exercise adaptation (evidence-based suggestions) ====================

    @PostMapping("/meal-swap/{userId}")
    public ResponseEntity<Map<String, Object>> mealSwap(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.status(200)
                .body(aiService.mealSwap(userId, request));
    }

    @PostMapping("/exercise-adapt/{userId}")
    public ResponseEntity<Map<String, Object>> adaptExercise(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.status(200)
                .body(aiService.adaptExercise(userId, request));
    }
}
