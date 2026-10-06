package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthAssessmentReadinessResponseDTO;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthAssessmentReadinessService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

    public HealthAssessmentReadinessResponseDTO getReadiness(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElse(null);

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        List<String> availableData = new ArrayList<>();
        List<String> missingData = new ArrayList<>();

        int totalDataPoints = 0;
        int availableDataPoints = 0;

        /*
         * Basic user information
         */

        totalDataPoints += 2;

        if (user.getDateOfBirth() != null) {
            availableData.add("Age");
            availableDataPoints++;
        } else {
            missingData.add("Age");
        }

        if (user.getGender() != null && !user.getGender().isBlank()) {
            availableData.add("Gender");
            availableDataPoints++;
        } else {
            missingData.add("Gender");
        }

        /*
         * Health profile
         */

        totalDataPoints += 2;

        if (healthProfile != null) {

            if (healthProfile.getActivityLevel() != null) {
                availableData.add("Activity Level");
                availableDataPoints++;
            } else {
                missingData.add("Activity Level");
            }

            if (healthProfile.getConditions() != null) {
                availableData.add("Health Conditions");
                availableDataPoints++;
            } else {
                missingData.add("Health Conditions");
            }

        } else {

            missingData.add("Activity Level");
            missingData.add("Health Conditions");
        }

        /*
         * Vital signs
         */

        String[] vitalTypes = {
                "BLOOD_PRESSURE",
                "GLUCOSE",
                "WEIGHT",
                "WAIST",
                "HEART_RATE"
        };

        for (String vitalType : vitalTypes) {

            totalDataPoints++;

            if (hasVitalSign(vitalSigns, vitalType)) {

                availableData.add(formatVitalName(vitalType));
                availableDataPoints++;

            } else {

                missingData.add(formatVitalName(vitalType));
            }
        }

        /*
         * Calculate completion percentage
         */

        int completionPercentage =
                (availableDataPoints * 100) / totalDataPoints;

        String assessmentStatus =
                calculateAssessmentStatus(completionPercentage);

        /*
         * Assessment areas
         */

        List<HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO>
                assessmentAreas = new ArrayList<>();

        assessmentAreas.add(
                buildCardiovascularArea(vitalSigns)
        );

        assessmentAreas.add(
                buildMetabolicArea(vitalSigns, healthProfile)
        );

        assessmentAreas.add(
                buildBodyCompositionArea(vitalSigns)
        );

        assessmentAreas.add(
                buildLifestyleArea(healthProfile, user)
        );

        String message = buildMessage(
                assessmentStatus,
                missingData
        );

        return new HealthAssessmentReadinessResponseDTO(
                userId,
                completionPercentage,
                assessmentStatus,
                availableData,
                missingData,
                assessmentAreas,
                message
        );
    }

    private boolean hasVitalSign(
            List<VitalSign> vitalSigns,
            String type) {

        return vitalSigns.stream()
                .anyMatch(vitalSign ->
                        vitalSign.getType().equals(type));
    }

    private String formatVitalName(String type) {

        switch (type) {

            case "BLOOD_PRESSURE":
                return "Blood Pressure";

            case "GLUCOSE":
                return "Glucose";

            case "WEIGHT":
                return "Weight";

            case "WAIST":
                return "Waist";

            case "HEART_RATE":
                return "Heart Rate";

            default:
                return type;
        }
    }

    private String calculateAssessmentStatus(
            int completionPercentage) {

        if (completionPercentage >= 80) {
            return "READY";
        }

        if (completionPercentage >= 50) {
            return "PARTIALLY_READY";
        }

        return "NOT_READY";
    }

    private HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO
    buildCardiovascularArea(List<VitalSign> vitalSigns) {

        List<String> available = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        if (hasVitalSign(vitalSigns, "BLOOD_PRESSURE")) {
            available.add("Blood Pressure");
        } else {
            missing.add("Blood Pressure");
        }

        if (hasVitalSign(vitalSigns, "HEART_RATE")) {
            available.add("Heart Rate");
        } else {
            missing.add("Heart Rate");
        }

        return buildAssessmentArea(
                "Cardiovascular",
                available,
                missing
        );
    }

    private HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO
    buildMetabolicArea(
            List<VitalSign> vitalSigns,
            HealthProfile healthProfile) {

        List<String> available = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        if (hasVitalSign(vitalSigns, "GLUCOSE")) {
            available.add("Glucose");
        } else {
            missing.add("Glucose");
        }

        if (healthProfile != null
                && healthProfile.getActivityLevel() != null) {

            available.add("Activity Level");

        } else {

            missing.add("Activity Level");
        }

        return buildAssessmentArea(
                "Metabolic",
                available,
                missing
        );
    }

    private HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO
    buildBodyCompositionArea(
            List<VitalSign> vitalSigns) {

        List<String> available = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        if (hasVitalSign(vitalSigns, "WEIGHT")) {
            available.add("Weight");
        } else {
            missing.add("Weight");
        }

        if (hasVitalSign(vitalSigns, "WAIST")) {
            available.add("Waist");
        } else {
            missing.add("Waist");
        }

        return buildAssessmentArea(
                "Body Composition",
                available,
                missing
        );
    }

    private HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO
    buildLifestyleArea(
            HealthProfile healthProfile,
            User user) {

        List<String> available = new ArrayList<>();
        List<String> missing = new ArrayList<>();

        if (user.getDateOfBirth() != null) {
            available.add("Age");
        } else {
            missing.add("Age");
        }

        if (user.getGender() != null
                && !user.getGender().isBlank()) {

            available.add("Gender");

        } else {

            missing.add("Gender");
        }

        if (healthProfile != null
                && healthProfile.getConditions() != null) {

            available.add("Health Conditions");

        } else {

            missing.add("Health Conditions");
        }

        return buildAssessmentArea(
                "Lifestyle",
                available,
                missing
        );
    }

    private HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO
    buildAssessmentArea(
            String area,
            List<String> available,
            List<String> missing) {

        String status;

        if (missing.isEmpty()) {
            status = "READY";
        } else if (available.isEmpty()) {
            status = "INCOMPLETE";
        } else {
            status = "PARTIAL";
        }

        return new HealthAssessmentReadinessResponseDTO.AssessmentAreaDTO(
                area,
                status,
                available,
                missing
        );
    }

    private String buildMessage(
            String assessmentStatus,
            List<String> missingData) {

        if (assessmentStatus.equals("READY")) {
            return "The available health data is sufficient for a comprehensive assessment.";
        }

        if (assessmentStatus.equals("PARTIALLY_READY")) {
            return "The assessment is partially ready. Add the missing health data to improve the assessment.";
        }

        return "More health data is required before a reliable assessment can be performed.";
    }
}