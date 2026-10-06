package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AICompleteResponseDTO;
import com.example.rafeeq.DTO.AIExerciseResponseDTO;
import com.example.rafeeq.DTO.AINutritionResponseDTO;
import com.example.rafeeq.DTO.AIPlanResponseDTO;
import com.example.rafeeq.DTO.EvidenceReferenceDTO;
import com.example.rafeeq.DTO.ExercisePlanResponseDTO;
import com.example.rafeeq.DTO.NutritionPlanResponseDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.ExercisePlanRepository;
import com.example.rafeeq.Repository.NutritionPlanRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class AIService {

    private final PatientContextService patientContextService;
    private final EvidenceService evidenceService;
    private final AnthropicService anthropicService;

    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExercisePlanRepository exercisePlanRepository;
    private final VitalSignRepository vitalSignRepository;
    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    @Transactional
    public AIPlanResponseDTO generatePlans(Integer userId) {

        Map<String, Object> patientContext =
                patientContextService.buildPatientContext(userId);

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
                            .orElseGet(NutritionPlan::new);

            plan.setItems(
                    objectMapper.writeValueAsString(
                            aiNutrition.getItems()
                    )
            );

            plan.setSummary(
                    aiNutrition.getSummary()
            );

            plan.setReason(
                    aiNutrition.getReason()
            );

            plan.setEvidence(
                    objectMapper.writeValueAsString(
                            evidence
                    )
            );

            if (plan.getUser() == null) {

                plan.setUser(
                        userRepository.findById(userId)
                                .orElseThrow(
                                        () -> new ApiException("User not found")
                                )
                );
            }

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
                            .orElseGet(ExercisePlan::new);

            plan.setGoal(
                    aiExercise.getGoal()
            );

            plan.setExercises(
                    objectMapper.writeValueAsString(
                            aiExercise.getExercises()
                    )
            );

            plan.setSummary(
                    aiExercise.getSummary()
            );

            plan.setEvidence(
                    objectMapper.writeValueAsString(
                            evidence
                    )
            );

            if (plan.getUser() == null) {

                plan.setUser(
                        userRepository.findById(userId)
                                .orElseThrow(
                                        () -> new ApiException("User not found")
                                )
                );
            }

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
            NutritionPlan plan
    ) {

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
                    "Failed to read nutrition plan"
            );
        }
    }

    private ExercisePlanResponseDTO convertExerciseResponse(
            ExercisePlan plan
    ) {

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
                    "Failed to read exercise plan"
            );
        }
    }
}