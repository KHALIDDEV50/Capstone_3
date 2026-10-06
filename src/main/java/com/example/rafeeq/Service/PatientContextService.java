package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PatientContextService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;

    public Map<String, Object> buildPatientContext(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException("User not found"));

        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        Map<String, Object> patient = new LinkedHashMap<>();

        patient.put("age", calculateAge(user));
        patient.put("gender", user.getGender());

        patient.put("heightCm", healthProfile.getHeightCm());
        patient.put("activityLevel", healthProfile.getActivityLevel());
        patient.put("conditions", healthProfile.getConditions());
        patient.put("exerciseRisk", healthProfile.getExerciseRisk());

        patient.put("latestBloodPressure",
                getLatestVital(userId, "BLOOD_PRESSURE"));

        patient.put("latestGlucose",
                getLatestVital(userId, "GLUCOSE"));

        patient.put("latestWeight",
                getLatestVital(userId, "WEIGHT"));

        patient.put("latestWaist",
                getLatestVital(userId, "WAIST"));

        patient.put("latestHeartRate",
                getLatestVital(userId, "HEART_RATE"));

        return patient;
    }

    private Map<String, Object> getLatestVital(Integer userId, String type) {

        Optional<VitalSign> optionalVital =
                vitalSignRepository.findFirstByUserIdAndTypeOrderByMeasuredAtDesc(
                        userId,
                        type
                );

        if (optionalVital.isEmpty()) {
            return null;
        }

        VitalSign vital = optionalVital.get();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("type", vital.getType());
        result.put("value", vital.getValue());
        result.put("systolic", vital.getSystolic());
        result.put("diastolic", vital.getDiastolic());
        result.put("unit", vital.getUnit());
        result.put("flag", vital.getFlag());
        result.put("measuredAt", vital.getMeasuredAt());

        return result;
    }

    private int calculateAge(User user) {

        return java.time.Period.between(
                user.getDateOfBirth(),
                java.time.LocalDate.now()
        ).getYears();
    }
}