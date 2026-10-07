package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AICompleteResponseDTO;
import com.example.rafeeq.DTO.AICompleteResponseDTO.AIExerciseResponseDTO;
import com.example.rafeeq.DTO.AICompleteResponseDTO.AINutritionResponseDTO;
import com.example.rafeeq.DTO.AIPlanResponseDTO;
import com.example.rafeeq.DTO.EvidenceReferenceDTO;
import com.example.rafeeq.DTO.AIPlanResponseDTO.ExercisePlanResponseDTO;
import com.example.rafeeq.DTO.AIPlanResponseDTO.NutritionPlanResponseDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Model.HealthProfile;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.ExercisePlanRepository;
import com.example.rafeeq.Repository.HealthProfileRepository;
import com.example.rafeeq.Repository.NutritionPlanRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AIService {

    private final EvidenceService evidenceService;
    private final AnthropicService anthropicService;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExercisePlanRepository exercisePlanRepository;
    private final VitalSignRepository vitalSignRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final HealthProfileRepository healthProfileRepository;

    // ==================== AI Plan Generation ====================

    @Transactional
    public AIPlanResponseDTO generatePlans(Integer userId) {

        Map<String, Object> patientContext =
                buildPatientContext(userId);

        validateSafety(patientContext, userId);

        List<EvidenceReferenceDTO> evidence =
                evidenceService.getRelevantEvidence(patientContext);

        if (evidence.isEmpty()) {
            throw new ApiException(
                    "No evidence references are available for this patient"
            );
        }

        AICompleteResponseDTO aiResponse =
                anthropicService.generatePlan(
                        patientContext,
                        evidence
                );

        validateAIResponse(aiResponse);

        NutritionPlan nutritionPlan =
                saveNutritionPlan(
                        userId,
                        aiResponse.getNutrition(),
                        evidence
                );

        ExercisePlan exercisePlan =
                saveExercisePlan(
                        userId,
                        aiResponse.getExercise(),
                        evidence
                );

        return new AIPlanResponseDTO(
                convertNutritionResponse(nutritionPlan),
                convertExerciseResponse(exercisePlan)
        );
    }

    private void validateSafety(
            Map<String, Object> patientContext,
            Integer userId
    ) {

        Object exerciseRisk =
                patientContext.get("exerciseRisk");

        if ("HIGH".equals(exerciseRisk)) {
            throw new ApiException(
                    "Exercise plan cannot be automatically generated because the patient's exercise risk is HIGH"
            );
        }

        VitalSign bloodPressure =
                vitalSignRepository
                        .findFirstByUserIdAndTypeOrderByMeasuredAtDesc(
                                userId,
                                "BLOOD_PRESSURE"
                        )
                        .orElse(null);

        if (bloodPressure == null) {
            return;
        }

        BigDecimal systolic =
                bloodPressure.getSystolic();

        BigDecimal diastolic =
                bloodPressure.getDiastolic();

        if (systolic == null || diastolic == null) {
            return;
        }

        boolean severeBloodPressure =
                systolic.compareTo(new BigDecimal("180")) >= 0
                        ||
                        diastolic.compareTo(new BigDecimal("120")) >= 0;

        if (severeBloodPressure) {
            throw new ApiException(
                    "Exercise plan cannot be automatically generated because the latest blood pressure is severely elevated. Medical evaluation is required."
            );
        }
    }

    private void validateAIResponse(
            AICompleteResponseDTO response
    ) {

        if (response == null ||
                response.getNutrition() == null ||
                response.getExercise() == null) {

            throw new ApiException(
                    "AI returned an incomplete health plan"
            );
        }

        if (response.getNutrition().getSummary() == null ||
                response.getNutrition().getReason() == null ||
                response.getNutrition().getItems() == null) {

            throw new ApiException(
                    "AI returned an invalid nutrition plan"
            );
        }

        if (response.getExercise().getGoal() == null ||
                response.getExercise().getSummary() == null ||
                response.getExercise().getExercises() == null) {

            throw new ApiException(
                    "AI returned an invalid exercise plan"
            );
        }
    }

    private NutritionPlan saveNutritionPlan(
            Integer userId,
            AINutritionResponseDTO aiNutrition,
            List<EvidenceReferenceDTO> evidence
    ) {

        try {

            NutritionPlan plan =
                    nutritionPlanRepository
                            .findByUserId(userId)
                            .orElse(new NutritionPlan());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(
                                    () -> new ApiException(
                                            "User not found"
                                    )
                            );

            plan.setUser(user);
            plan.setSummary(aiNutrition.getSummary());
            plan.setReason(aiNutrition.getReason());
            plan.setItems(
                    objectMapper.writeValueAsString(
                            aiNutrition.getItems()
                    )
            );
            plan.setEvidence(
                    objectMapper.writeValueAsString(evidence)
            );

            return nutritionPlanRepository.save(plan);

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to save nutrition plan"
            );
        }
    }

    private ExercisePlan saveExercisePlan(
            Integer userId,
            AIExerciseResponseDTO aiExercise,
            List<EvidenceReferenceDTO> evidence
    ) {

        try {

            ExercisePlan plan =
                    exercisePlanRepository
                            .findByUserId(userId)
                            .orElse(new ExercisePlan());

            User user =
                    userRepository.findById(userId)
                            .orElseThrow(
                                    () -> new ApiException(
                                            "User not found"
                                    )
                            );

            plan.setUser(user);
            plan.setGoal(aiExercise.getGoal());
            plan.setSummary(aiExercise.getSummary());
            plan.setExercises(
                    objectMapper.writeValueAsString(
                            aiExercise.getExercises()
                    )
            );
            plan.setEvidence(
                    objectMapper.writeValueAsString(evidence)
            );

            return exercisePlanRepository.save(plan);

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to save exercise plan"
            );
        }
    }

    private NutritionPlanResponseDTO convertNutritionResponse(
            NutritionPlan plan) {

        try {

            JsonNode items =
                    objectMapper.readTree(
                            plan.getItems()
                    );

            JsonNode evidence =
                    objectMapper.readTree(
                            plan.getEvidence()
                    );

            return new NutritionPlanResponseDTO(
                    plan.getId(),
                    plan.getUser().getId(),
                    items,
                    plan.getSummary(),
                    plan.getReason(),
                    evidence,
                    plan.getCreatedAt(),
                    plan.getUpdatedAt()
            );

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to convert nutrition plan response"
            );
        }
    }

    private ExercisePlanResponseDTO convertExerciseResponse(
            ExercisePlan plan) {

        try {

            JsonNode exercises =
                    objectMapper.readTree(
                            plan.getExercises()
                    );

            JsonNode evidence =
                    objectMapper.readTree(
                            plan.getEvidence()
                    );

            return new ExercisePlanResponseDTO(
                    plan.getId(),
                    plan.getUser().getId(),
                    plan.getGoal(),
                    exercises,
                    plan.getSummary(),
                    evidence,
                    plan.getCreatedAt(),
                    plan.getUpdatedAt()
            );

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to convert exercise plan response"
            );
        }
    }

    private Map<String, Object> buildPatientContext(
            Integer userId) {

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () -> new ApiException(
                                        "User not found"
                                )
                        );

        HealthProfile healthProfile =
                healthProfileRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () -> new ApiException(
                                        "Health profile not found"
                                )
                        );

        List<VitalSign> vitalSigns =
                vitalSignRepository
                        .findByUserIdOrderByMeasuredAtDesc(userId);

        Map<String, Object> context =
                new LinkedHashMap<>();

        context.put("userId", user.getId());
        context.put("age", calculateAge(user));
        context.put("gender", user.getGender());
        context.put("activityLevel",
                healthProfile.getActivityLevel());
        context.put("conditions",
                healthProfile.getConditions());
        context.put("exerciseRisk",
                healthProfile.getExerciseRisk());
        context.put("heightCm",
                healthProfile.getHeightCm());

        List<Map<String, Object>> vitals =
                vitalSigns.stream()
                        .map(this::convertVital)
                        .toList();

        context.put("vitalSigns", vitals);

        return context;
    }

    private Map<String, Object> convertVital(
            VitalSign vitalSign) {

        Map<String, Object> vital =
                new LinkedHashMap<>();

        vital.put("type", vitalSign.getType());
        vital.put("value", vitalSign.getValue());
        vital.put("systolic", vitalSign.getSystolic());
        vital.put("diastolic", vitalSign.getDiastolic());
        vital.put("unit", vitalSign.getUnit());
        vital.put("flag", vitalSign.getFlag());
        vital.put("measuredAt", vitalSign.getMeasuredAt());

        return vital;
    }

    private Integer calculateAge(User user) {

        return java.time.Period.between(
                user.getDateOfBirth(),
                java.time.LocalDate.now()
        ).getYears();
    }

    // ----------- last extra endpoint ----------

    public Map<String, Object> mealSwap(
            Integer userId,
            Map<String, Object> request) {

        if (request == null ||
                request.get("meal") == null) {

            throw new ApiException(
                    "Meal is required"
            );
        }

        String meal =
                request.get("meal").toString();

        String reason =
                request.get("reason") == null
                        ? "healthier alternative"
                        : request.get("reason").toString();

        Map<String, Object> patientContext =
                buildPatientContext(userId);

        List<EvidenceReferenceDTO> evidence =
                evidenceService.getRelevantEvidence(
                        patientContext
                );

        if (evidence.isEmpty()) {
            throw new ApiException(
                    "No evidence references are available for this patient"
            );
        }

        return anthropicService.generateMealSwap(
                patientContext,
                evidence,
                meal,
                reason
        );
    }

    // ----------- last extra endpoint ----------

    public Map<String, Object> adaptExercise(
            Integer userId,
            Map<String, Object> request) {

        Map<String, Object> patientContext =
                buildPatientContext(userId);

        validateSafety(patientContext, userId);

        ExercisePlan exercisePlan =
                exercisePlanRepository
                        .findByUserId(userId)
                        .orElseThrow(
                                () -> new ApiException(
                                        "No exercise plan found for this user. Generate an AI plan first."
                                )
                        );

        String difficulty =
                request != null &&
                        request.get("difficulty") != null
                        ? request.get("difficulty").toString()
                        : "TOO_HARD";

        String availableMinutes =
                request != null &&
                        request.get("availableMinutes") != null
                        ? request.get("availableMinutes").toString()
                        : "20";

        String equipment =
                request != null &&
                        request.get("equipment") != null
                        ? request.get("equipment").toString()
                        : "NONE";

        List<EvidenceReferenceDTO> evidence =
                evidenceService.getRelevantEvidence(
                        patientContext
                );

        if (evidence.isEmpty()) {
            throw new ApiException(
                    "No evidence references are available for this patient"
            );
        }

        return anthropicService.generateExerciseAdaptation(
                patientContext,
                evidence,
                exercisePlan,
                difficulty,
                availableMinutes,
                equipment
        );
    }
}