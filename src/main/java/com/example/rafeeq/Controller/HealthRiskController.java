package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.HealthRiskResponseDTO;
import com.example.rafeeq.Service.HealthRiskService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/health-risks")
@AllArgsConstructor
public class HealthRiskController {

    private final HealthRiskService healthRiskService;

    @GetMapping("/{userId}")
    public ResponseEntity<HealthRiskResponseDTO> getHealthRisks(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthRiskService.getHealthRisks(userId));
    }
}