package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthProfileRequestDTO;
import com.example.rafeeq.DTO.HealthProfileResponseDTO;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class HealthProfileService {

    private final HealthProfileRepository healthProfileRepository;
    private final UserRepository userRepository;
    private final VitalSignRepository vitalSignRepository;

    public List<HealthProfileResponseDTO> getAllHealthProfiles() {
        return healthProfileRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public HealthProfileResponseDTO getHealthProfileById(Integer id) {
        HealthProfile healthProfile = healthProfileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        return convertToResponseDTO(healthProfile);
    }

    public HealthProfileResponseDTO getHealthProfileByUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        HealthProfile healthProfile = healthProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        return convertToResponseDTO(healthProfile);
    }

    public void addHealthProfile(HealthProfileRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException("User not found"));

        if (healthProfileRepository.existsByUserId(dto.getUserId())) {
            throw new ApiException("Health profile already exists for this user");
        }

        HealthProfile healthProfile = new HealthProfile();

        healthProfile.setUser(user);
        healthProfile.setHeightCm(dto.getHeightCm());
        healthProfile.setActivityLevel(dto.getActivityLevel());
        healthProfile.setConditions(dto.getConditions());

        String exerciseRisk = calculateExerciseRisk(user, dto.getActivityLevel(), dto.getConditions());

        healthProfile.setExerciseRisk(exerciseRisk);

        healthProfileRepository.save(healthProfile);
    }

    public void updateHealthProfile(Integer id, HealthProfileRequestDTO dto) {

        HealthProfile healthProfile = healthProfileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        if (!healthProfile.getUser().getId().equals(dto.getUserId())) {
            throw new ApiException("Health profile belongs to a different user");
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException("User not found"));

        healthProfile.setHeightCm(dto.getHeightCm());
        healthProfile.setActivityLevel(dto.getActivityLevel());
        healthProfile.setConditions(dto.getConditions());

        String exerciseRisk = calculateExerciseRisk(
                user,
                dto.getActivityLevel(),
                dto.getConditions()
        );

        healthProfile.setExerciseRisk(exerciseRisk);

        healthProfileRepository.save(healthProfile);
    }

    public void deleteHealthProfile(Integer id) {

        HealthProfile healthProfile = healthProfileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Health profile not found"));

        healthProfileRepository.delete(healthProfile);
    }

    private String calculateExerciseRisk(
            User user,
            String activityLevel,
            List<String> conditions) {

        int score = 0;

        int age = calculateAge(user.getDateOfBirth());

        /*
         * Age
         */
        if (age >= 75) {
            score += 2;
        } else if (age >= 65) {
            score += 1;
        }

        /*
         * Activity level
         */
        if ("SEDENTARY".equals(activityLevel)) {
            score += 2;
        } else if ("LIGHT".equals(activityLevel)) {
            score += 1;
        }

        /*
         * Existing health conditions
         */
        if (conditions != null && !conditions.isEmpty()) {
            score += Math.min(conditions.size(), 2);
        }

        /*
         * Latest vital signs
         */
        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(user.getId());

        VitalSign latestBloodPressure = getLatestVital(vitalSigns, "BLOOD_PRESSURE");
        VitalSign latestGlucose = getLatestVital(vitalSigns, "GLUCOSE");
        VitalSign latestHeartRate = getLatestVital(vitalSigns, "HEART_RATE");

        /*
         * Blood pressure
         */
        if (latestBloodPressure != null) {

            if ("CRITICAL".equals(latestBloodPressure.getFlag())) {
                score += 3;
            } else if ("HIGH".equals(latestBloodPressure.getFlag())) {
                score += 2;
            } else if ("LOW".equals(latestBloodPressure.getFlag())) {
                score += 1;
            }
        }

        /*
         * Glucose
         */
        if (latestGlucose != null) {

            if ("CRITICAL".equals(latestGlucose.getFlag())) {
                score += 2;
            } else if ("HIGH".equals(latestGlucose.getFlag())) {
                score += 1;
            }
        }

        /*
         * Heart rate
         */
        if (latestHeartRate != null) {

            if ("CRITICAL".equals(latestHeartRate.getFlag())) {
                score += 2;
            } else if ("HIGH".equals(latestHeartRate.getFlag())
                    || "LOW".equals(latestHeartRate.getFlag())) {
                score += 1;
            }
        }

        /*
         * Final risk
         */
        if (score >= 6) {
            return "HIGH";
        }

        if (score >= 3) {
            return "MEDIUM";
        }

        return "LOW";
    }

    private int calculateAge(LocalDate dateOfBirth) {
        return LocalDate.now()
                .minusYears(dateOfBirth.getYear())
                .getYear();
    }

    private VitalSign getLatestVital(
            List<VitalSign> vitalSigns,
            String type) {

        return vitalSigns.stream()
                .filter(vitalSign -> vitalSign.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    private HealthProfileResponseDTO convertToResponseDTO(
            HealthProfile healthProfile) {

        HealthProfileResponseDTO responseDTO = new HealthProfileResponseDTO();

        responseDTO.setId(healthProfile.getId());
        responseDTO.setUserId(healthProfile.getUser().getId());
        responseDTO.setHeightCm(healthProfile.getHeightCm());
        responseDTO.setActivityLevel(healthProfile.getActivityLevel());
        responseDTO.setConditions(healthProfile.getConditions());
        responseDTO.setExerciseRisk(healthProfile.getExerciseRisk());
        responseDTO.setCreatedAt(healthProfile.getCreatedAt());
        responseDTO.setUpdatedAt(healthProfile.getUpdatedAt());

        return responseDTO;
    }
}