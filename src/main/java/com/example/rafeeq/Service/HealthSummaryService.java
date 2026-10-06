package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthSummaryResponseDTO;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HealthSummaryService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

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
}