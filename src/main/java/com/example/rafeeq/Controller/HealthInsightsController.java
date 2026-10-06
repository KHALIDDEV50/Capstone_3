package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.HealthInsightResponseDTO;
import com.example.rafeeq.Service.HealthInsightsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/health-insights")
@AllArgsConstructor
public class HealthInsightsController {

    private final HealthInsightsService healthInsightsService;

    @GetMapping("/{userId}")
    public ResponseEntity<HealthInsightResponseDTO> getHealthInsights(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthInsightsService.getHealthInsights(userId));
    }
}