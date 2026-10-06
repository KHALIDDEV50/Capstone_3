package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.HealthContentResponseDTO;
import com.example.rafeeq.Service.HealthContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/health-content")
@AllArgsConstructor
public class HealthContentController {

    private final HealthContentService healthContentService;

    @GetMapping("/get")
    public ResponseEntity<HealthContentResponseDTO> getHealthContent() {

        return ResponseEntity.status(200)
                .body(healthContentService.getHealthContent());
    }
}