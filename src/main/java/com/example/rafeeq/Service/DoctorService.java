package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.DoctorAppointmentEligibilityResponseDTO;
import com.example.rafeeq.DTO.DoctorFollowUpResponseDTO;
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
public class DoctorService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

    // ==================== Doctor Follow-Up ====================

    public DoctorFollowUpResponseDTO getDoctorFollowUp(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        HealthProfile healthProfile =
                healthProfileRepository.findByUserId(userId)
                        .orElse(null);

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items =
                new ArrayList<>();

        analyzeBloodPressure(vitalSigns, items);
        analyzeGlucose(vitalSigns, items);
        addExerciseRecommendation(healthProfile, items);
        addNutritionRecommendation(vitalSigns, items);
        addMissingDataRecommendation(vitalSigns, items, healthProfile);

        String followUpStatus = calculateFollowUpStatus(items);
        String priority = calculatePriority(items);
        String summary = buildSummary(items, followUpStatus);

        return new DoctorFollowUpResponseDTO(
                userId,
                followUpStatus,
                priority,
                summary,
                items
        );
    }

    private void analyzeBloodPressure(
            List<VitalSign> vitalSigns,
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        List<VitalSign> bloodPressures = vitalSigns.stream()
                .filter(v -> v.getType().equals("BLOOD_PRESSURE"))
                .toList();

        if (bloodPressures.isEmpty()) {
            return;
        }

        int elevatedCount = 0;

        for (VitalSign vitalSign : bloodPressures) {

            if (vitalSign.getSystolic() != null
                    && vitalSign.getDiastolic() != null
                    && (vitalSign.getSystolic().doubleValue() >= 140
                    || vitalSign.getDiastolic().doubleValue() >= 90)) {

                elevatedCount++;
            }
        }

        VitalSign latest = bloodPressures.get(0);

        if (elevatedCount >= 2) {

            String priority = elevatedCount >= 3
                    ? "HIGH"
                    : "MEDIUM";

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Blood Pressure",
                    priority,
                    latest.getSystolic()
                            + "/"
                            + latest.getDiastolic()
                            + " "
                            + latest.getUnit(),
                    "Repeated elevated blood pressure measurements were observed.",
                    "Review repeated blood pressure readings with a healthcare professional."
            ));

        } else if ("HIGH".equals(latest.getFlag())) {

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Blood Pressure",
                    "MEDIUM",
                    latest.getSystolic()
                            + "/"
                            + latest.getDiastolic()
                            + " "
                            + latest.getUnit(),
                    "An elevated blood pressure measurement was detected.",
                    "Continue monitoring blood pressure and discuss persistent elevated readings with a healthcare professional."
            ));
        }
    }

    private void analyzeGlucose(
            List<VitalSign> vitalSigns,
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        List<VitalSign> glucoseMeasurements = vitalSigns.stream()
                .filter(v -> v.getType().equals("GLUCOSE"))
                .toList();

        if (glucoseMeasurements.isEmpty()) {
            return;
        }

        int elevatedCount = 0;

        for (VitalSign vitalSign : glucoseMeasurements) {

            if (vitalSign.getValue() != null
                    && vitalSign.getValue().doubleValue() >= 140) {

                elevatedCount++;
            }
        }

        VitalSign latest = glucoseMeasurements.get(0);

        if (elevatedCount >= 2) {

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Glucose",
                    "HIGH",
                    latest.getValue()
                            + " "
                            + latest.getUnit(),
                    "Repeated elevated glucose measurements were observed.",
                    "Continue monitoring glucose and discuss persistent elevated readings with a healthcare professional."
            ));

        } else if ("HIGH".equals(latest.getFlag())) {

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Glucose",
                    "MEDIUM",
                    latest.getValue()
                            + " "
                            + latest.getUnit(),
                    "An elevated glucose measurement was detected.",
                    "Continue monitoring glucose and discuss persistent abnormal readings with a healthcare professional."
            ));
        }
    }

    private void addExerciseRecommendation(
            HealthProfile healthProfile,
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        if (healthProfile == null) {
            return;
        }

        String activityLevel = healthProfile.getActivityLevel();
        String exerciseRisk = healthProfile.getExerciseRisk();

        if ("SEDENTARY".equals(activityLevel)) {

            String priority = "HIGH".equals(exerciseRisk)
                    ? "HIGH"
                    : "MEDIUM";

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Exercise",
                    priority,
                    null,
                    "The user's activity level is SEDENTARY.",
                    "Consider suitable regular physical activity, such as walking, based on the user's exercise-risk classification."
            ));
        }
    }

    private void addNutritionRecommendation(
            List<VitalSign> vitalSigns,
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        boolean highBloodPressure = vitalSigns.stream()
                .filter(v -> v.getType().equals("BLOOD_PRESSURE"))
                .anyMatch(v -> "HIGH".equals(v.getFlag())
                        || "CRITICAL".equals(v.getFlag()));

        boolean highGlucose = vitalSigns.stream()
                .filter(v -> v.getType().equals("GLUCOSE"))
                .anyMatch(v -> "HIGH".equals(v.getFlag())
                        || "CRITICAL".equals(v.getFlag()));

        if (highBloodPressure || highGlucose) {

            List<String> reasons = new ArrayList<>();

            if (highBloodPressure) {
                reasons.add("elevated blood pressure");
            }

            if (highGlucose) {
                reasons.add("elevated glucose");
            }

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Nutrition",
                    "MEDIUM",
                    null,
                    "Dietary factors may be relevant because "
                            + String.join(" and ", reasons)
                            + " were detected.",
                    "Consider a balanced eating pattern emphasizing vegetables, whole foods, fiber-rich foods, and appropriate sodium intake. Discuss personalized dietary changes with a healthcare professional."
            ));
        }
    }

    private void addMissingDataRecommendation(
            List<VitalSign> vitalSigns,
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items,
            HealthProfile healthProfile) {

        List<String> missing = new ArrayList<>();

        if (!hasVitalSign(vitalSigns, "HEART_RATE")) {
            missing.add("heart rate");
        }

        if (!hasVitalSign(vitalSigns, "WEIGHT")) {
            missing.add("weight");
        }

        if (!hasVitalSign(vitalSigns, "WAIST")) {
            missing.add("waist");
        }

        if (!missing.isEmpty()) {

            items.add(new DoctorFollowUpResponseDTO.FollowUpItemDTO(
                    "Assessment Data",
                    "MEDIUM",
                    null,
                    "Important health measurements are missing: "
                            + String.join(", ", missing)
                            + ".",
                    "Collect the missing measurements to provide a more complete health assessment."
            ));
        }
    }

    private boolean hasVitalSign(
            List<VitalSign> vitalSigns,
            String type) {

        return vitalSigns.stream()
                .anyMatch(v -> v.getType().equals(type));
    }

    private String calculateFollowUpStatus(
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        if (items.isEmpty()) {
            return "NO_FOLLOW_UP_REQUIRED";
        }

        return "FOLLOW_UP_RECOMMENDED";
    }

    private String calculatePriority(
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items) {

        boolean high = items.stream()
                .anyMatch(item -> "HIGH".equals(item.getPriority()));

        if (high) {
            return "HIGH";
        }

        boolean medium = items.stream()
                .anyMatch(item -> "MEDIUM".equals(item.getPriority()));

        if (medium) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private String buildSummary(
            List<DoctorFollowUpResponseDTO.FollowUpItemDTO> items,
            String followUpStatus) {

        if ("NO_FOLLOW_UP_REQUIRED".equals(followUpStatus)) {
            return "No specific follow-up concerns were identified from the available health data.";
        }

        List<String> concerns = items.stream()
                .filter(item -> !"Assessment Data".equals(item.getArea()))
                .map(DoctorFollowUpResponseDTO.FollowUpItemDTO::getReason)
                .toList();

        if (concerns.isEmpty()) {
            return "Additional health data is recommended to improve the assessment.";
        }

        return String.join(" ", concerns);
    }

    // ==================== Doctor Appointment Eligibility ====================

    public DoctorAppointmentEligibilityResponseDTO checkEligibility(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        if (vitalSigns.isEmpty()) {
            return new DoctorAppointmentEligibilityResponseDTO(
                    userId,
                    false,
                    "LOW",
                    null,
                    null,
                    "No health measurements are available.",
                    "Add health measurements before checking doctor appointment eligibility.",
                    false
            );
        }

        VitalSign criticalVital = vitalSigns.stream()
                .filter(v -> "CRITICAL".equals(v.getFlag()))
                .findFirst()
                .orElse(null);

        if (criticalVital != null) {

            return new DoctorAppointmentEligibilityResponseDTO(
                    userId,
                    true,
                    "URGENT",
                    getConditionName(criticalVital.getType()),
                    getReading(criticalVital),
                    "A critical health measurement was detected.",
                    "A human healthcare professional should review this result.",
                    true
            );
        }

        VitalSign highVital = vitalSigns.stream()
                .filter(v -> "HIGH".equals(v.getFlag()))
                .findFirst()
                .orElse(null);

        if (highVital != null) {

            long highCount = vitalSigns.stream()
                    .filter(v -> "HIGH".equals(v.getFlag()))
                    .count();

            if (highCount >= 2) {

                return new DoctorAppointmentEligibilityResponseDTO(
                        userId,
                        true,
                        "HIGH",
                        getConditionName(highVital.getType()),
                        getReading(highVital),
                        "Repeated abnormal health measurements were detected.",
                        "A human healthcare professional should review the repeated abnormal measurements.",
                        true
                );
            }

            return new DoctorAppointmentEligibilityResponseDTO(
                    userId,
                    true,
                    "MEDIUM",
                    getConditionName(highVital.getType()),
                    getReading(highVital),
                    "An abnormal health measurement was detected.",
                    "Consider discussing this result with a healthcare professional.",
                    true
            );
        }

        return new DoctorAppointmentEligibilityResponseDTO(
                userId,
                false,
                "LOW",
                null,
                null,
                "No critical or significantly abnormal measurements were detected.",
                "Continue monitoring your health.",
                false
        );
    }

    private String getConditionName(String type) {

        return switch (type) {
            case "BLOOD_PRESSURE" -> "Blood Pressure";
            case "GLUCOSE" -> "Glucose";
            case "HEART_RATE" -> "Heart Rate";
            case "WEIGHT" -> "Weight";
            case "WAIST" -> "Waist";
            default -> type;
        };
    }

    private String getReading(VitalSign vitalSign) {

        if ("BLOOD_PRESSURE".equals(vitalSign.getType())) {
            return vitalSign.getSystolic()
                    + "/"
                    + vitalSign.getDiastolic()
                    + " "
                    + vitalSign.getUnit();
        }

        return vitalSign.getValue()
                + " "
                + vitalSign.getUnit();
    }
}
