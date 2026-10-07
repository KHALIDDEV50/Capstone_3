package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.AIMedicationAnalysisDTO;
import com.example.rafeeq.DTO.AIMedicationImpactDTO;
import com.example.rafeeq.DTO.MedicationScheduleDTO;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Service.MedicationScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/medication-schedule")
@RequiredArgsConstructor
public class MedicationScheduleController {

    private final MedicationScheduleService medicationScheduleService;

    // Get All Medication Schedule
    @GetMapping("/get")
    public ResponseEntity<?> getAllMedicationSchedule() {

        List<MedicationSchedule> medicationSchedules = medicationScheduleService.getAllMedicationSchedule();

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Get Medication Schedule By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMedicationScheduleById(@PathVariable Integer id) {

        MedicationSchedule medicationSchedule = medicationScheduleService.getMedicationScheduleById(id);

        return ResponseEntity.status(200).body(medicationSchedule);
    }

    // Get Medication Schedule By User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMedicationScheduleByUserId(@PathVariable Integer userId) {

        List<MedicationSchedule> medicationSchedules = medicationScheduleService.getMedicationScheduleByUserId(userId);

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Get Active Medication Schedule By User ID
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveMedicationScheduleByUserId(@PathVariable Integer userId) {

        List<MedicationSchedule> medicationSchedules = medicationScheduleService.getActiveMedicationScheduleByUserId(userId);

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Add Medication Schedule
    @PostMapping("/add")
    public ResponseEntity<?> addMedicationSchedule(@RequestBody @Valid MedicationScheduleDTO medicationScheduleDTO) {

        medicationScheduleService.addMedicationSchedule(medicationScheduleDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Add Successful"));
    }

    // Update Medication Schedule
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMedicationSchedule(@PathVariable Integer id, @RequestBody @Valid MedicationScheduleDTO medicationScheduleDTO) {

        medicationScheduleService.updateMedicationSchedule(id, medicationScheduleDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Update Successful"));
    }

    // Delete Medication Schedule
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMedicationSchedule(@PathVariable Integer id) {

        medicationScheduleService.deleteMedicationSchedule(id);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Delete Successful"));
    }

    // ====================================================
// =================== Extra Point ====================
// =============== AI Medication Analysis =============
// ====================================================

    @PostMapping("/ai-analysis/{userId}")
    public ResponseEntity<?> generateAIMedicationAnalysis(
            @PathVariable Integer userId) {

        // Generate AI analysis for the user's medications
        AIMedicationAnalysisDTO analysis = medicationScheduleService.generateAIMedicationAnalysis(userId);

        // Return the AI-generated medication analysis
        return ResponseEntity.status(200).body(analysis);
    }
// ====================================================
// =================== Extra Point ====================
// ======= AI Medication Analysis + Email =============
// ====================================================

    @PostMapping("/ai-analysis/email/{userId}")
    public ResponseEntity<?> sendAIMedicationReport(
            @PathVariable Integer userId) {

        // Generate AI medication analysis
        // and send the HTML report to the user's email
        medicationScheduleService
                .generateAndSendAIMedicationReport(userId);

        return ResponseEntity.status(200).body(
                new ApiResponse(
                        "Medication report sent successfully"
                )
        );
    }

    //٠٠٠

    // ====================================================
// =================== Extra Point ====================
// ======== AI Medication Impact Report ===============
// ====================================================

    @PostMapping("/medication-impact/{userId}")
    public ResponseEntity<?> generateMedicationImpactReport(
            @PathVariable Integer userId) {

        // Generate AI medication impact report
        AIMedicationImpactDTO report = medicationScheduleService.generateMedicationImpactReport(userId);

        // Return AI report
        return ResponseEntity.status(200).body(report);
    }

    //..
    // ====================================================
// =================== Extra Point ====================
// ====== AI Medication Impact HTML Report ============
// ====================================================

    @PostMapping("/medication-impact/report/{userId}")
    public ResponseEntity<?> generateMedicationImpactHtmlReport(
            @PathVariable Integer userId) {

        // Generate HTML report
        String report = medicationScheduleService.generateMedicationImpactHtmlReport(userId);

        // Return HTML report
        return ResponseEntity.status(200).header("Content-Type", "text/html; charset=UTF-8").body(report);
    }

    //..

    // =========================================================
// AI Medication Impact HTML Report
// =========================================================

    // Generate AI Medication Impact HTML Report
    @PostMapping("/ai-medication-impact/html/{userId}")
    public ResponseEntity<?> generateMedicationImpactHtml(
            @PathVariable Integer userId) {

        // Generate HTML report using AI medication impact analysis
        String htmlReport = medicationScheduleService.generateMedicationImpactHtmlReport(userId);

        // Return the generated HTML report
        return ResponseEntity.ok(htmlReport);
    }
}