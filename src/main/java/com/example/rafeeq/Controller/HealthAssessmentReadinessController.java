package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.HealthAssessmentReadinessResponseDTO;
import com.example.rafeeq.Service.HealthAssessmentReadinessService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/health-assessment/readiness")
@AllArgsConstructor
public class HealthAssessmentReadinessController {

    private final HealthAssessmentReadinessService healthAssessmentReadinessService;

    @GetMapping("/{userId}")
    public ResponseEntity<HealthAssessmentReadinessResponseDTO> getReadiness(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthAssessmentReadinessService.getReadiness(userId));
    }
}