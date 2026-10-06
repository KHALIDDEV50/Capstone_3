package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.DoctorAppointmentEligibilityResponseDTO;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DoctorAppointmentEligibilityService {

    private final UserRepository userRepository;
    private final VitalSignRepository vitalSignRepository;

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