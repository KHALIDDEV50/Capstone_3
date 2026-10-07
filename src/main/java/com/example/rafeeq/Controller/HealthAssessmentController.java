package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.AIHealthProgressDTO;
import com.example.rafeeq.DTO.AIHealthSummaryDTO;
import com.example.rafeeq.DTO.AIHealthTrendDTO;
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
    public ResponseEntity<?> addHealthAssessment(@RequestBody @Valid HealthAssessmentDTO healthAssessmentDTO) {

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


    // ====================================================
// =================== Extra Point ====================
// ====================================================

    @PostMapping("/ai-summary/{assessmentId}")
    public ResponseEntity<?> generateAIHealthSummary(
            @PathVariable Integer assessmentId) {

        // Generate the structured health summary using OpenAI
        AIHealthSummaryDTO summary = healthAssessmentService.generateAIHealthSummary(assessmentId);

        // Return the AI-generated Arabic health summary
        return ResponseEntity.status(200).body(summary);
    }


    // Generate the AI report and send it by email
    @PostMapping("/report/email/{assessmentId}")
    public ResponseEntity<?> sendHealthReportByEmail(
            @PathVariable Integer assessmentId) {

        // Generate the AI report and send it by email
        healthAssessmentService.generateAndSendAIHealthReport(
                assessmentId
        );

        return ResponseEntity.status(200).body(new ApiResponse("Health report sent successfully"));
    }

    // ====================================================
// =================== Extra Point ====================
// ================= AI Health Trend ==================
// ====================================================

    @PostMapping("/health-trend/{assessmentId}")
    public ResponseEntity<?> generateAIHealthTrend(
            @PathVariable Integer assessmentId) {

        // Generate the health trend by comparing
        // the current assessment with the previous assessment
        AIHealthTrendDTO trend = healthAssessmentService.generateAIHealthTrend(assessmentId);

        // Return the AI-generated health trend
        return ResponseEntity.status(200).body(trend);
    }

    //..
    @PostMapping("/health-trend/email/{assessmentId}")
    public ResponseEntity<?> sendHealthTrendByEmail(
            @PathVariable Integer assessmentId) {

        // Generate AI trend and send it by email
        healthAssessmentService.generateAndSendAIHealthTrendReport(assessmentId);

        return ResponseEntity.status(200).body(
                new ApiResponse("Health trend report sent successfully"));
    }

    // =========================================================
// AI Health Progress Report
// =========================================================

    // Generate AI Health Progress Report for a user
    @PostMapping("/health-progress/{userId}")
    public ResponseEntity<?> generateHealthProgressReport(@PathVariable Integer userId) {

        // Generate AI health progress report
        AIHealthProgressDTO progressReport = healthAssessmentService.generateHealthProgressReport(userId);

        // Return the AI progress report
        return ResponseEntity.ok(progressReport);
    }

    // =========================================================
// AI Health Progress HTML Report
// =========================================================

    @PostMapping("/health-progress/html/{userId}")
    public ResponseEntity<?> generateHealthProgressHtml(
            @PathVariable Integer userId) {

        // Check if the endpoint is reached
        System.out.println("===== HEALTH PROGRESS HTML ENDPOINT =====");

        System.out.println("USER ID: " + userId);

        // Generate HTML report
        String htmlReport = healthAssessmentService.generateHealthProgressHtmlReport(userId);

        // Check generated HTML
        System.out.println("HTML GENERATED");

        System.out.println("HTML LENGTH: " + htmlReport.length());

        // Return HTML
        return ResponseEntity.ok().header("Content-Type", "text/html; charset=UTF-8").body(htmlReport);
    }
}
