package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.AIPlanResponseDTO;
import com.example.rafeeq.Service.AIService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}