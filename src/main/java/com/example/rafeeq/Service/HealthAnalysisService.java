package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.DoctorAppointmentEligibilityResponseDTO;
import com.example.rafeeq.DTO.DoctorFollowUpResponseDTO;
import com.example.rafeeq.DTO.HealthAssessmentReadinessResponseDTO;
import com.example.rafeeq.DTO.HealthInsightResponseDTO;
import com.example.rafeeq.DTO.HealthRiskResponseDTO;
import com.example.rafeeq.DTO.HealthSummaryResponseDTO;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthAnalysisService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

    // ==================== Health Risks ====================

    public HealthRiskResponseDTO getHealthRisks(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElse(null);

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        if (healthProfile == null && vitalSigns.isEmpty()) {
            throw new ApiException("No health data found for this user");
        }

        List<HealthRiskResponseDTO.RiskDTO> risks = new ArrayList<>();

        analyzeBloodPressure(userId, vitalSigns, risks);
        analyzeGlucose(userId, vitalSigns, risks);
        analyzeWeightAndWaist(userId, vitalSigns, risks);
        analyzeActivityAndExerciseRisk(healthProfile, risks);

        String overallRiskLevel = calculateOverallRisk(risks);

        return new HealthRiskResponseDTO(
                user.getId(),
                overallRiskLevel,
                risks
        );
    }

    private void analyzeBloodPressure(
            Integer userId,
            List<VitalSign> vitalSigns,
            List<HealthRiskResponseDTO.RiskDTO> risks) {

        List<VitalSign> bloodPressures = vitalSigns.stream()
                .filter(v -> v.getType().equals("BLOOD_PRESSURE"))
                .toList();

        if (bloodPressures.isEmpty()) {
            return;
        }

        int highCount = 0;

        for (VitalSign vitalSign : bloodPressures) {

            if (vitalSign.getSystolic() != null
                    && vitalSign.getDiastolic() != null) {

                if (vitalSign.getSystolic()
                        .compareTo(BigDecimal.valueOf(140)) >= 0
                        || vitalSign.getDiastolic()
                        .compareTo(BigDecimal.valueOf(90)) >= 0) {

                    highCount++;
                }
            }
        }

        if (highCount >= 2) {

            VitalSign latest = bloodPressures.get(0);

            List<String> evidence = new ArrayList<>();

            evidence.add(
                    "Latest blood pressure: "
                            + latest.getSystolic()
                            + "/"
                            + latest.getDiastolic()
            );

            evidence.add(
                    "Elevated readings detected: "
                            + highCount
            );

            risks.add(new HealthRiskResponseDTO.RiskDTO(
                    "Cardiovascular Risk",
                    highCount >= 3 ? "HIGH" : "MODERATE",
                    "Repeated elevated blood pressure measurements were observed.",
                    evidence,
                    "Consider discussing repeated elevated blood pressure readings with a healthcare professional."
            ));
        }
    }

    private void analyzeGlucose(
            Integer userId,
            List<VitalSign> vitalSigns,
            List<HealthRiskResponseDTO.RiskDTO> risks) {

        List<VitalSign> glucoseMeasurements = vitalSigns.stream()
                .filter(v -> v.getType().equals("GLUCOSE"))
                .toList();

        if (glucoseMeasurements.isEmpty()) {
            return;
        }

        int elevatedCount = 0;

        for (VitalSign vitalSign : glucoseMeasurements) {

            if (vitalSign.getValue() != null
                    && vitalSign.getValue()
                    .compareTo(BigDecimal.valueOf(140)) >= 0) {

                elevatedCount++;
            }
        }

        if (elevatedCount >= 2) {

            VitalSign latest = glucoseMeasurements.get(0);

            List<String> evidence = new ArrayList<>();

            evidence.add(
                    "Latest glucose: "
                            + latest.getValue()
                            + " "
                            + latest.getUnit()
            );

            evidence.add(
                    "Elevated readings detected: "
                            + elevatedCount
            );

            risks.add(new HealthRiskResponseDTO.RiskDTO(
                    "Metabolic Risk",
                    elevatedCount >= 3 ? "HIGH" : "MODERATE",
                    "Repeated elevated glucose measurements were observed.",
                    evidence,
                    "Consider monitoring glucose regularly and discussing persistent elevated readings with a healthcare professional."
            ));
        }
    }

    private void analyzeWeightAndWaist(
            Integer userId,
            List<VitalSign> vitalSigns,
            List<HealthRiskResponseDTO.RiskDTO> risks) {

        List<VitalSign> weightMeasurements = vitalSigns.stream()
                .filter(v -> v.getType().equals("WEIGHT"))
                .toList();

        List<VitalSign> waistMeasurements = vitalSigns.stream()
                .filter(v -> v.getType().equals("WAIST"))
                .toList();

        boolean increasingWeight = hasIncreasingTrend(weightMeasurements);
        boolean increasingWaist = hasIncreasingTrend(waistMeasurements);

        if (increasingWeight || increasingWaist) {

            List<String> evidence = new ArrayList<>();

            if (increasingWeight) {
                evidence.add("Weight measurements show an increasing pattern.");
            }

            if (increasingWaist) {
                evidence.add("Waist measurements show an increasing pattern.");
            }

            risks.add(new HealthRiskResponseDTO.RiskDTO(
                    "Metabolic Health Risk",
                    "MODERATE",
                    "An increasing weight or waist measurement pattern was observed.",
                    evidence,
                    "Maintaining regular physical activity and monitoring weight and waist measurements may be beneficial."
            ));
        }
    }

    private void analyzeActivityAndExerciseRisk(
            HealthProfile healthProfile,
            List<HealthRiskResponseDTO.RiskDTO> risks) {

        if (healthProfile == null) {
            return;
        }

        List<String> evidence = new ArrayList<>();

        if ("SEDENTARY".equals(healthProfile.getActivityLevel())) {
            evidence.add("Activity level is SEDENTARY.");
        }

        if ("HIGH".equals(healthProfile.getExerciseRisk())) {
            evidence.add("Exercise risk is HIGH.");
        }

        if (evidence.isEmpty()) {
            return;
        }

        risks.add(new HealthRiskResponseDTO.RiskDTO(
                "Lifestyle Risk",
                "MODERATE",
                "The health profile contains factors that may be associated with increased long-term health risk.",
                evidence,
                "Consider discussing a suitable activity plan with a qualified healthcare professional."
        ));
    }

    private boolean hasIncreasingTrend(List<VitalSign> vitalSigns) {

        if (vitalSigns.size() < 2) {
            return false;
        }

        VitalSign latest = vitalSigns.get(0);
        VitalSign previous = vitalSigns.get(1);

        if (latest.getValue() == null || previous.getValue() == null) {
            return false;
        }

        return latest.getValue().compareTo(previous.getValue()) > 0;
    }

    private String calculateOverallRisk(
            List<HealthRiskResponseDTO.RiskDTO> risks) {

        if (risks.isEmpty()) {
            return "LOW";
        }

        boolean hasHighRisk = risks.stream()
                .anyMatch(risk -> risk.getRiskLevel().equals("HIGH"));

        if (hasHighRisk) {
            return "HIGH";
        }

        boolean hasModerateRisk = risks.stream()
                .anyMatch(risk -> risk.getRiskLevel().equals("MODERATE"));

        if (hasModerateRisk) {
            return "MODERATE";
        }

        return "LOW";
    }

    // ==================== Health Insights ====================

    public HealthInsightResponseDTO getHealthInsights(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        List<HealthInsightResponseDTO.VitalInsightDTO> insights = new ArrayList<>();

        List<String> vitalTypes = List.of(
                "BLOOD_PRESSURE",
                "GLUCOSE",
                "WEIGHT",
                "WAIST",
                "HEART_RATE"
        );

        for (String type : vitalTypes) {

            List<VitalSign> vitalSigns =
                    vitalSignRepository.findTop2ByUserIdAndTypeOrderByMeasuredAtDesc(userId, type);

            if (!vitalSigns.isEmpty()) {
                insights.add(buildInsight(vitalSigns));
            }
        }

        if (insights.isEmpty()) {
            throw new ApiException("No vital signs found for this user");
        }

        return new HealthInsightResponseDTO(userId, insights);
    }

    private HealthInsightResponseDTO.VitalInsightDTO buildInsight(List<VitalSign> vitalSigns) {

        VitalSign latest = vitalSigns.get(0);

        if (latest.getType().equals("BLOOD_PRESSURE")) {
            return buildBloodPressureInsight(vitalSigns);
        }

        VitalSign previous = vitalSigns.size() > 1 ? vitalSigns.get(1) : null;

        BigDecimal change = null;
        BigDecimal changePercentage = null;
        String trend = "INSUFFICIENT_DATA";

        if (previous != null && latest.getValue() != null && previous.getValue() != null) {

            change = latest.getValue()
                    .subtract(previous.getValue())
                    .setScale(2, RoundingMode.HALF_UP);

            if (previous.getValue().compareTo(BigDecimal.ZERO) != 0) {
                changePercentage = change
                        .divide(previous.getValue(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);
            }

            trend = determineTrend(change);
        }

        return new HealthInsightResponseDTO.VitalInsightDTO(
                latest.getType(),
                latest.getUnit(),
                latest.getValue(),
                previous != null ? previous.getValue() : null,
                change,
                changePercentage,
                null,
                null,
                null,
                null,
                null,
                null,
                trend,
                latest.getFlag(),
                latest.getMeasuredAt(),
                previous != null ? previous.getMeasuredAt() : null
        );
    }

    private HealthInsightResponseDTO.VitalInsightDTO buildBloodPressureInsight(
            List<VitalSign> vitalSigns) {

        VitalSign latest = vitalSigns.get(0);
        VitalSign previous = vitalSigns.size() > 1 ? vitalSigns.get(1) : null;

        BigDecimal systolicChange = null;
        BigDecimal diastolicChange = null;
        String trend = "INSUFFICIENT_DATA";

        if (previous != null) {

            if (latest.getSystolic() != null && previous.getSystolic() != null) {
                systolicChange = latest.getSystolic()
                        .subtract(previous.getSystolic())
                        .setScale(2, RoundingMode.HALF_UP);
            }

            if (latest.getDiastolic() != null && previous.getDiastolic() != null) {
                diastolicChange = latest.getDiastolic()
                        .subtract(previous.getDiastolic())
                        .setScale(2, RoundingMode.HALF_UP);
            }

            trend = determineBloodPressureTrend(
                    systolicChange,
                    diastolicChange
            );
        }

        return new HealthInsightResponseDTO.VitalInsightDTO(
                latest.getType(),
                latest.getUnit(),
                null,
                null,
                null,
                null,
                latest.getSystolic(),
                latest.getDiastolic(),
                previous != null ? previous.getSystolic() : null,
                previous != null ? previous.getDiastolic() : null,
                systolicChange,
                diastolicChange,
                trend,
                latest.getFlag(),
                latest.getMeasuredAt(),
                previous != null ? previous.getMeasuredAt() : null
        );
    }

    private String determineTrend(BigDecimal change) {

        if (change.compareTo(BigDecimal.ZERO) > 0) {
            return "INCREASED";
        }

        if (change.compareTo(BigDecimal.ZERO) < 0) {
            return "DECREASED";
        }

        return "UNCHANGED";
    }

    private String determineBloodPressureTrend(
            BigDecimal systolicChange,
            BigDecimal diastolicChange) {

        if (systolicChange == null || diastolicChange == null) {
            return "INSUFFICIENT_DATA";
        }

        boolean systolicIncreased =
                systolicChange.compareTo(BigDecimal.ZERO) > 0;

        boolean diastolicIncreased =
                diastolicChange.compareTo(BigDecimal.ZERO) > 0;

        boolean systolicDecreased =
                systolicChange.compareTo(BigDecimal.ZERO) < 0;

        boolean diastolicDecreased =
                diastolicChange.compareTo(BigDecimal.ZERO) < 0;

        if (systolicIncreased && diastolicIncreased) {
            return "INCREASED";
        }

        if (systolicDecreased && diastolicDecreased) {
            return "DECREASED";
        }

        if (systolicChange.compareTo(BigDecimal.ZERO) == 0
                && diastolicChange.compareTo(BigDecimal.ZERO) == 0) {
            return "UNCHANGED";
        }

        return "MIXED";
    }

    // ==================== Health Summary ====================

    public HealthSummaryResponseDTO getHealthSummary(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        String bloodPressure = null;
        Double glucose = null;
        Double weight = null;
        Double waist = null;
        Double heartRate = null;

        String bloodPressureStatus = null;
        String glucoseStatus = null;
        String weightStatus = null;
        String waistStatus = null;
        String heartRateStatus = null;

        for (VitalSign vitalSign : vitalSigns) {

            switch (vitalSign.getType()) {

                case "BLOOD_PRESSURE":
                    if (bloodPressure == null) {
                        bloodPressure = vitalSign.getSystolic()
                                + "/"
                                + vitalSign.getDiastolic();

                        bloodPressureStatus = vitalSign.getFlag();
                    }
                    break;

                case "GLUCOSE":
                    if (glucose == null) {
                        glucose = vitalSign.getValue().doubleValue();
                        glucoseStatus = vitalSign.getFlag();
                    }
                    break;

                case "WEIGHT":
                    if (weight == null) {
                        weight = vitalSign.getValue().doubleValue();
                        weightStatus = vitalSign.getFlag();
                    }
                    break;

                case "WAIST":
                    if (waist == null) {
                        waist = vitalSign.getValue().doubleValue();
                        waistStatus = vitalSign.getFlag();
                    }
                    break;

                case "HEART_RATE":
                    if (heartRate == null) {
                        heartRate = vitalSign.getValue().doubleValue();
                        heartRateStatus = vitalSign.getFlag();
                    }
                    break;

                default:
                    break;
            }
        }

        HealthSummaryResponseDTO.LatestVitalsDTO latestVitals =
                new HealthSummaryResponseDTO.LatestVitalsDTO(
                        bloodPressure,
                        glucose,
                        weight,
                        waist,
                        heartRate
                );

        HealthSummaryResponseDTO.VitalStatusDTO status =
                new HealthSummaryResponseDTO.VitalStatusDTO(
                        bloodPressureStatus,
                        glucoseStatus,
                        weightStatus,
                        waistStatus,
                        heartRateStatus
                );

        return new HealthSummaryResponseDTO(
                userId,
                healthProfile.getActivityLevel(),
                healthProfile.getExerciseRisk(),
                latestVitals,
                status,
                vitalSigns.size()
        );
    }

    // ==================== Health Assessment Readiness ====================

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