package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.HealthSummaryResponseDTO;
import com.example.rafeeq.Service.HealthSummaryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/health-summary")
@AllArgsConstructor
public class HealthSummaryController {

    private final HealthSummaryService healthSummaryService;

    @GetMapping("/{userId}")
    public ResponseEntity<HealthSummaryResponseDTO> getHealthSummary(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthSummaryService.getHealthSummary(userId));
    }
}