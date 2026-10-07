package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AIMedicationAnalysisDTO;
import com.example.rafeeq.DTO.AIMedicationImpactDTO;
import com.example.rafeeq.DTO.MedicationScheduleDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.HealthAssessmentRepository;
import com.example.rafeeq.Repository.MedicationScheduleRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {
    private final JavaMailSender mailSender;
    private final MedicationScheduleRepository medicationScheduleRepository;
    private final UserRepository userRepository;
    private final OpenAIService openAIService;
    private final MedicationReportService medicationReportService;
    private final HealthAssessmentRepository healthAssessmentRepository;
    private final MedicationImpactReportService medicationImpactReportService;

    // Get all Medication Schedule
    public List<MedicationSchedule> getAllMedicationSchedule() {

        return medicationScheduleRepository.findAll();
    }

    // Get Medication Schedule By ID
    public MedicationSchedule getMedicationScheduleById(Integer id) {

        MedicationSchedule medicationSchedule = medicationScheduleRepository.findById(id).orElse(null);

        if (medicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        return medicationSchedule;
    }

    // Get Medication Schedule By User ID
    public List<MedicationSchedule> getMedicationScheduleByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return medicationScheduleRepository.findByUser_Id(userId);
    }

    // Get Active Medication Schedule By User ID
    public List<MedicationSchedule>
    getActiveMedicationScheduleByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return medicationScheduleRepository.findByUser_IdAndIsActiveTrue(userId);
    }

    // Add Medication Schedule
    public void addMedicationSchedule(MedicationScheduleDTO medicationScheduleDTO) {

        // Find User
        User user = userRepository.findById(medicationScheduleDTO.getUserId()).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        // Create Medication Schedule
        MedicationSchedule medicationSchedule = new MedicationSchedule();

        // Set User
        medicationSchedule.setUser(user);

        medicationSchedule.setMedicationName(medicationScheduleDTO.getMedicationName());

        medicationSchedule.setDosage(medicationScheduleDTO.getDosage()
        );

        medicationSchedule.setMealRelation(medicationScheduleDTO.getMealRelation());

        medicationSchedule.setTimes(medicationScheduleDTO.getTimes());

        medicationSchedule.setStartDate(medicationScheduleDTO.getStartDate());

        medicationSchedule.setEndDate(medicationScheduleDTO.getEndDate());

        medicationSchedule.setIsActive(medicationScheduleDTO.getIsActive());

        medicationScheduleRepository.save(medicationSchedule);
    }

    // Update Medication Schedule
    public void updateMedicationSchedule(Integer id, MedicationScheduleDTO medicationScheduleDTO) {

        MedicationSchedule oldMedicationSchedule = medicationScheduleRepository.findById(id).orElse(null);

        if (oldMedicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        // Find User
        User user = userRepository.findById(medicationScheduleDTO.getUserId()).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        // Set User
        oldMedicationSchedule.setUser(user);

        oldMedicationSchedule.setMedicationName(medicationScheduleDTO.getMedicationName());

        oldMedicationSchedule.setDosage(medicationScheduleDTO.getDosage());

        oldMedicationSchedule.setMealRelation(medicationScheduleDTO.getMealRelation());

        oldMedicationSchedule.setTimes(medicationScheduleDTO.getTimes());

        oldMedicationSchedule.setStartDate(medicationScheduleDTO.getStartDate());

        oldMedicationSchedule.setEndDate(medicationScheduleDTO.getEndDate());

        oldMedicationSchedule.setIsActive(medicationScheduleDTO.getIsActive());

        medicationScheduleRepository.save(oldMedicationSchedule);
    }

    // Delete Medication Schedule
    public void deleteMedicationSchedule(Integer id) {

        MedicationSchedule medicationSchedule = medicationScheduleRepository.findById(id).orElse(null);

        if (medicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        medicationScheduleRepository.delete(medicationSchedule);
    }



    // =================== extra ====================

    // Generate AI analysis for the user's medication schedule
    public AIMedicationAnalysisDTO generateAIMedicationAnalysis(
            Integer userId) {

        // Get all medication schedules for the user
        List<MedicationSchedule> medications =
                medicationScheduleRepository
                        .findByUser_Id(userId);

        // Make sure the user has medications
        if (medications == null || medications.isEmpty()) {
            throw new ApiException(
                    "No medication schedules found for this user"
            );
        }

        // Build medication data to send to OpenAI
        StringBuilder medicationData =
                new StringBuilder();

        for (MedicationSchedule medication : medications) {

            medicationData.append("""
                
                Medication:
                Name: %s
                Dosage: %s
                Meal Relation: %s
                Times: %s
                Start Date: %s
                End Date: %s
                Active: %s
                """.formatted(
                    medication.getMedicationName(),
                    medication.getDosage(),
                    medication.getMealRelation(),
                    medication.getTimes(),
                    medication.getStartDate(),
                    medication.getEndDate(),
                    medication.getIsActive()
            ));
        }

        // Send the medication data to OpenAI
        return openAIService.generateMedicationAnalysis(
                medicationData.toString()
        );
    }

    // ....
    // ====================================================
// ========== Generate & Send AI Medication Report ====
// ====================================================

    public void generateAndSendAIMedicationReport(Integer userId) {

        // Get all medication schedules for the user
        List<MedicationSchedule> medications =
                medicationScheduleRepository.findByUser_Id(userId);

        // Make sure the user has medications
        if (medications == null || medications.isEmpty()) {
            throw new ApiException(
                    "No medication schedules found for this user"
            );
        }

        // Get the user from the medication record
        User user = medications.get(0).getUser();

        // Generate AI medication analysis
        AIMedicationAnalysisDTO aiAnalysis =
                generateAIMedicationAnalysis(userId);

        // Send the HTML report by email
        medicationReportService.sendMedicationReport(
                user,
                medications,
                aiAnalysis
        );
    }

    // ....

    // Generate AI Medication Impact Report
    public AIMedicationImpactDTO generateMedicationImpactReport(
            Integer userId) {

        // Get all medications for the user
        List<MedicationSchedule> medications =
                medicationScheduleRepository
                        .findByUser_Id(userId);

        // Make sure medications exist
        if (medications == null || medications.isEmpty()) {
            throw new ApiException(
                    "No medication schedules found for this user"
            );
        }

        // Get all health assessments for the user
        List<HealthAssessment> assessments = healthAssessmentRepository
                        .findByUser_Id(userId);

        // Make sure health assessments exist
        if (assessments == null || assessments.isEmpty()) {
            throw new ApiException(
                    "No health assessments found for this user"
            );
        }

        // Build medication data
        StringBuilder medicationData =
                new StringBuilder();

        for (MedicationSchedule medication : medications) {

            medicationData.append("""
                
                Medication:
                Name: %s
                Dosage: %s
                Meal Relation: %s
                Times: %s
                Start Date: %s
                End Date: %s
                Active: %s
                """.formatted(
                    medication.getMedicationName(),
                    medication.getDosage(),
                    medication.getMealRelation(),
                    medication.getTimes(),
                    medication.getStartDate(),
                    medication.getEndDate(),
                    medication.getIsActive()
            ));
        }

        // Build health assessment data
        StringBuilder healthAssessmentData =
                new StringBuilder();

        for (HealthAssessment assessment : assessments) {

            healthAssessmentData.append("""
                
                Health Assessment:
                Assessment Date: %s
                User Notes: %s
                Profile Snapshot: %s
                Extracted Values: %s
                Trend: %s
                Is Current: %s
                Next Due Date: %s
                """.formatted(
                    assessment.getAssessmentDate(),
                    assessment.getUserNotes(),
                    assessment.getProfileSnapshot(),
                    assessment.getExtractedValues(),
                    assessment.getTrend(),
                    assessment.getIsCurrent(),
                    assessment.getNextDueDate()
            ));
        }

        // Send both datasets to OpenAI
        return openAIService.generateMedicationImpactReport(medicationData.toString(), healthAssessmentData.toString());
    }


    //..

    // Generate AI Medication Impact HTML Report
    public String generateMedicationImpactHtmlReport(
            Integer userId) {

        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ApiException("User not found")
                );

        // Get medications
        List<MedicationSchedule> medications =
                medicationScheduleRepository.findByUser_Id(userId);

        // Get health assessments
        List<HealthAssessment> assessments =
                healthAssessmentRepository.findByUser_Id(userId);

        // Check medications
        if (medications.isEmpty()) {
            throw new ApiException(
                    "No medication schedules found for this user"
            );
        }

        // Build medication data
        String medicationData =
                medications.stream().map(medication ->
                                "Medication: " + medication.getMedicationName()
                                        + ", Dosage: " + medication.getDosage()
                                        + ", Meal Relation: " + medication.getMealRelation()
                                        + ", Times: " + medication.getTimes()
                                        + ", Start Date: " + medication.getStartDate()
                                        + ", End Date: " + medication.getEndDate()
                        )
                        .collect(Collectors.joining("\n"));

        // Build health assessment data
        String healthAssessmentData =
                assessments.stream()
                        .map(assessment ->
                                "Assessment Date: " + assessment.getAssessmentDate()
                                        + ", AI Conclusion: " + assessment.getAiConclusion()
                                        + ", Trend: " + assessment.getTrend()
                        )
                        .collect(Collectors.joining("\n"));

        // Generate AI report
        AIMedicationImpactDTO aiReport =
                openAIService.generateMedicationImpactReport(
                        medicationData,
                        healthAssessmentData
                );

        // Generate HTML report
        return medicationImpactReportService.buildMedicationImpactHtml(
                user,
                medications,
                assessments,
                aiReport
        );
    }
    //..



}