package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.HealthAssessmentDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Service.HealthAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/health-assessment")
@RequiredArgsConstructor
public class HealthAssessmentController {

    private final HealthAssessmentService healthAssessmentService;

    // Get All Health Assessments
    @GetMapping("/get")
    public ResponseEntity<?> getAllHealthAssessments() {

        List<HealthAssessment> healthAssessments = healthAssessmentService.getAllHealthAssessments();

        return ResponseEntity.status(200).body(healthAssessments);
    }

    // Get Health Assessment By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getHealthAssessmentById(@PathVariable Integer id) {

        HealthAssessment healthAssessment = healthAssessmentService.getHealthAssessmentById(id);

        return ResponseEntity.status(200).body(healthAssessment);
    }

    // Get Health Assessments By User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getHealthAssessmentsByUserId(@PathVariable Integer userId) {

        List<HealthAssessment> healthAssessments = healthAssessmentService.getHealthAssessmentsByUserId(userId);

        return ResponseEntity.status(200).body(healthAssessments);
    }

    // Get Current Health Assessment By User ID
    @GetMapping("/user/{userId}/current")
    public ResponseEntity<?> getCurrentHealthAssessmentsByUserId(@PathVariable Integer userId) {

        List<HealthAssessment> healthAssessments = healthAssessmentService.getCurrentHealthAssessmentsByUserId(userId);

        return ResponseEntity.status(200).body(healthAssessments);
    }

    // Add Health Assessment
    @PostMapping("/add")
    public ResponseEntity<?> addHealthAssessment(
            @RequestBody @Valid HealthAssessmentDTO healthAssessmentDTO) {

        healthAssessmentService.addHealthAssessment(healthAssessmentDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Health Assessment Add Successful"));
    }

    // Update Health Assessment
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHealthAssessment(
            @PathVariable Integer id,
            @RequestBody @Valid HealthAssessmentDTO healthAssessmentDTO) {

        healthAssessmentService.updateHealthAssessment(id, healthAssessmentDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Health Assessment Update Successful"));
    }

    // Delete Health Assessment
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteHealthAssessment(@PathVariable Integer id) {

        healthAssessmentService.deleteHealthAssessment(id);

        return ResponseEntity.status(200).body(new ApiResponse("Health Assessment Delete Successful"));
    }
}