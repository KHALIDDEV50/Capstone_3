package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.AIPlanResponseDTO;
import com.example.rafeeq.Service.AIService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/ai")
@AllArgsConstructor
public class AIController {

    private final AIService aiService;

    @PostMapping("/generate-plan/{userId}")
    public ResponseEntity<AIPlanResponseDTO> generatePlan(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(aiService.generatePlans(userId));
    }

    // ----------- last extra endpoint ----------

    @PostMapping("/meal-swap/{userId}")
    public ResponseEntity<Map<String, Object>> mealSwap(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.status(200)
                .body(aiService.mealSwap(userId, request));
    }

    // ----------- last extra endpoint ----------

    @PostMapping("/exercise-adapt/{userId}")
    public ResponseEntity<Map<String, Object>> adaptExercise(
            @PathVariable Integer userId,
            @RequestBody Map<String, Object> request) {

        return ResponseEntity.status(200)
                .body(aiService.adaptExercise(userId, request));
    }
}