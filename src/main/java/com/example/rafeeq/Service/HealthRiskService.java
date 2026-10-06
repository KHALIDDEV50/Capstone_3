package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthRiskResponseDTO;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthRiskService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

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
}