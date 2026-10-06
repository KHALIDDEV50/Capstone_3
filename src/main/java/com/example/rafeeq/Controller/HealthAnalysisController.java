package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.DoctorAppointmentEligibilityResponseDTO;
import com.example.rafeeq.DTO.DoctorFollowUpResponseDTO;
import com.example.rafeeq.DTO.HealthAssessmentReadinessResponseDTO;
import com.example.rafeeq.DTO.HealthInsightResponseDTO;
import com.example.rafeeq.DTO.HealthRiskResponseDTO;
import com.example.rafeeq.DTO.HealthSummaryResponseDTO;
import com.example.rafeeq.Service.DoctorService;
import com.example.rafeeq.Service.HealthAnalysisService;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api")
@AllArgsConstructor
public class HealthAnalysisController {

    private final HealthAnalysisService healthAnalysisService;
    private final DoctorService doctorService;

    // ==================== Health Risks ====================

    @GetMapping("/health-risks/{userId}")
    public ResponseEntity<HealthRiskResponseDTO> getHealthRisks(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthAnalysisService.getHealthRisks(userId));
    }

    // ==================== Health Insights ====================

    @GetMapping("/health-insights/{userId}")
    public ResponseEntity<HealthInsightResponseDTO> getHealthInsights(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthAnalysisService.getHealthInsights(userId));
    }

    // ==================== Health Summary ====================

    @GetMapping("/health-summary/{userId}")
    public ResponseEntity<HealthSummaryResponseDTO> getHealthSummary(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthAnalysisService.getHealthSummary(userId));
    }

    // ==================== Health Assessment Readiness ====================

    @GetMapping("/health-assessment/readiness/{userId}")
    public ResponseEntity<HealthAssessmentReadinessResponseDTO> getReadiness(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthAnalysisService.getReadiness(userId));
    }

    // ==================== Doctor Follow-Up ====================

    @GetMapping("/doctor-follow-up/{userId}")
    public ResponseEntity<DoctorFollowUpResponseDTO> getDoctorFollowUp(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(doctorService.getDoctorFollowUp(userId));
    }

    // ==================== Doctor Appointment Eligibility ====================

    @GetMapping("/doctor-appointments/eligibility/{userId}")
    public ResponseEntity<DoctorAppointmentEligibilityResponseDTO> checkEligibility(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(doctorService.checkEligibility(userId));
    }
}