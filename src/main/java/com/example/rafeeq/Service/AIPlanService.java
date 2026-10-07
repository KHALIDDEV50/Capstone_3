package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.*;
import com.example.rafeeq.Model.*;
import com.example.rafeeq.Repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.rafeeq.DTO.NutritionShoppingListDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AIPlanService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final VitalSignRepository vitalSignRepository;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExercisePlanRepository exercisePlanRepository;
    private final AnthropicService anthropicService;
    private final ObjectMapper objectMapper;
    private final MedicationScheduleRepository medicationScheduleRepository;

    // =========================================
    // Generate Nutrition Plan
    // =========================================

    public NutritionPlan generateNutritionPlan(Integer userId) {

        // Check User
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }


        // Get Health Profile
        HealthProfile healthProfile =
                healthProfileRepository.findHealthProfileByUserId(userId);

        if (healthProfile == null) {
            throw new ApiException("Health profile not found");
        }


        // Get Vital Signs
        List<VitalSign> vitalSigns =
                vitalSignRepository
                        .findAllByUserIdOrderByMeasuredAtDesc(userId);


        // =========================================
        // Convert Health Profile To Simple String
        // =========================================

        String healthData =
                "Height: " + healthProfile.getHeightCm()
                        + ", Activity Level: " + healthProfile.getActivityLevel()
                        + ", Conditions: " + healthProfile.getConditions()
                        + ", Exercise Risk: " + healthProfile.getExerciseRisk();


        // =========================================
        // Convert Vital Signs To Simple String
        // =========================================

        StringBuilder vitalData = new StringBuilder();

        for (VitalSign vitalSign : vitalSigns) {

            vitalData.append("Type: ")
                    .append(vitalSign.getType())

                    .append(", Value: ")
                    .append(vitalSign.getValue())

                    .append(", Systolic: ")
                    .append(vitalSign.getSystolic())

                    .append(", Diastolic: ")
                    .append(vitalSign.getDiastolic())

                    .append(", Unit: ")
                    .append(vitalSign.getUnit())

                    .append(", Flag: ")
                    .append(vitalSign.getFlag())

                    .append(", Measured At: ")
                    .append(vitalSign.getMeasuredAt())

                    .append("\n");
        }


        // =========================================
        // Nutrition Prompt
        // =========================================

        String prompt =
                """
                Create a personalized nutrition plan for this user.

                Health Profile:
                %s

                Vital Signs:
                %s

                Return ONLY valid JSON in exactly this format:

                {
                  "nutritionSummary": "summary",
                  "nutritionItems": "nutrition plan",
                  "nutritionReason": "reason"
                }

                Requirements:

                - Write all response values in Arabic.
                - Keep JSON field names in English.
                - Consider the user's health conditions.
                - Consider the user's latest vital signs.
                - Consider the user's activity level.
                - Make the nutrition plan practical.
                - Include breakfast, lunch, dinner and snacks.
                - Consider diabetes if present.
                - Consider hypertension if present.
                - Do not diagnose diseases.
                - Do not prescribe medications.
                - Do not stop or change medications.
                - Do not invent health information.
                - Return ONLY valid JSON.
                """.formatted(
                        healthData,
                        vitalData.toString()
                );


        // =========================================
        // Send To Claude
        // =========================================

        String aiResult =
                anthropicService.generate(prompt);


        System.out.println("NUTRITION AI RESULT:");
        System.out.println(aiResult);


        // =========================================
        // Convert JSON To DTO
        // =========================================

        AINutritionResponse response;

        try {

            response = objectMapper.readValue(
                    aiResult,
                    AINutritionResponse.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to read AI nutrition response: "
                            + e.getMessage()
            );
        }


        // =========================================
        // Find Existing Plan
        // =========================================

        NutritionPlan nutritionPlan =
                nutritionPlanRepository
                        .findNutritionPlanByUserId(userId);


        if (nutritionPlan == null) {

            nutritionPlan = new NutritionPlan();

            nutritionPlan.setUser(user);
        }


        // =========================================
        // Set AI Result
        // =========================================

        nutritionPlan.setSummary(
                response.getNutritionSummary()
        );

        nutritionPlan.setItems(
                response.getNutritionItems()
        );

        nutritionPlan.setReason(
                response.getNutritionReason()
        );


        // Save
        nutritionPlanRepository.save(nutritionPlan);


        return nutritionPlan;
    }


    // =========================================
    // Generate Exercise Plan
    // =========================================

    public ExercisePlan generateExercisePlan(Integer userId) {

        // Check User
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }


        // Get Health Profile
        HealthProfile healthProfile =
                healthProfileRepository
                        .findHealthProfileByUserId(userId);

        if (healthProfile == null) {
            throw new ApiException("Health profile not found");
        }


        // Get Vital Signs
        List<VitalSign> vitalSigns =
                vitalSignRepository
                        .findAllByUserIdOrderByMeasuredAtDesc(userId);


        // =========================================
        // Convert Health Profile To Simple String
        // =========================================

        String healthData =
                "Height: " + healthProfile.getHeightCm()
                        + ", Activity Level: " + healthProfile.getActivityLevel()
                        + ", Conditions: " + healthProfile.getConditions()
                        + ", Exercise Risk: " + healthProfile.getExerciseRisk();


        // =========================================
        // Convert Vital Signs To Simple String
        // =========================================

        StringBuilder vitalData = new StringBuilder();

        for (VitalSign vitalSign : vitalSigns) {

            vitalData.append("Type: ")
                    .append(vitalSign.getType())

                    .append(", Value: ")
                    .append(vitalSign.getValue())

                    .append(", Systolic: ")
                    .append(vitalSign.getSystolic())

                    .append(", Diastolic: ")
                    .append(vitalSign.getDiastolic())

                    .append(", Unit: ")
                    .append(vitalSign.getUnit())

                    .append(", Flag: ")
                    .append(vitalSign.getFlag())

                    .append(", Measured At: ")
                    .append(vitalSign.getMeasuredAt())

                    .append("\n");
        }


        // =========================================
        // Exercise Prompt
        // =========================================

        String prompt =
                """
                Create a personalized exercise plan for this user.

                Health Profile:
                %s

                Vital Signs:
                %s

                Return ONLY valid JSON in exactly this format:

                {
                  "exerciseGoal": "goal",
                  "exercises": "exercise plan",
                  "exerciseSummary": "summary"
                }

                Requirements:

                - Write all response values in Arabic.
                - Keep JSON field names in English.

                - Consider the user's health conditions.
                - Consider the user's latest vital signs.
                - Consider the user's activity level.
                - Consider the user's exercise risk.

                - Give 3 to 5 exercises.

                For every exercise include:
                - Exercise name.
                - Number of sets.
                - Number of repetitions.
                - Number of days per week.

                Example:

                القرفصاء: 3 جولات × 12 تكرار × 3 أيام بالأسبوع

                - Keep exercises clear and concise.
                - Use safe exercises.
                - If exercise risk is high,
                  recommend low-risk exercises.
                - Avoid dangerous high-intensity exercises.
                - Do not diagnose diseases.
                - Do not prescribe medications.
                - Do not stop or change medications.
                - Do not invent health information.
                - Return ONLY valid JSON.
                """.formatted(
                        healthData,
                        vitalData.toString()
                );


        // =========================================
        // Send To Claude
        // =========================================

        String aiResult =
                anthropicService.generate(prompt);


        System.out.println("EXERCISE AI RESULT:");
        System.out.println(aiResult);


        // =========================================
        // Convert JSON To DTO
        // =========================================

        AIExerciseResponse response;

        try {

            response = objectMapper.readValue(
                    aiResult,
                    AIExerciseResponse.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to read AI exercise response: "
                            + e.getMessage()
            );
        }


        // =========================================
        // Find Existing Exercise Plan
        // =========================================

        ExercisePlan exercisePlan =
                exercisePlanRepository
                        .findExercisePlanByUserId(userId);


        if (exercisePlan == null) {

            exercisePlan = new ExercisePlan();

            exercisePlan.setUser(user);
        }


        // =========================================
        // Set AI Result
        // =========================================

        exercisePlan.setGoal(
                response.getExerciseGoal()
        );

        exercisePlan.setExercises(
                response.getExercises()
        );

        exercisePlan.setSummary(
                response.getExerciseSummary()
        );


        // Save
        exercisePlanRepository.save(exercisePlan);


        return exercisePlan;
    }
    public NutritionAlternativeResponse generateNutritionAlternative(
            Integer userId,
            AlternativeRequestDTO request) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        HealthProfile healthProfile =
                healthProfileRepository.findHealthProfileByUserId(userId);

        if (healthProfile == null) {
            throw new ApiException("Health profile not found");
        }

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }


        String healthData =
                "Height: " + healthProfile.getHeightCm()
                        + ", Activity Level: " + healthProfile.getActivityLevel()
                        + ", Conditions: " + healthProfile.getConditions()
                        + ", Exercise Risk: " + healthProfile.getExerciseRisk();


        String prompt = """
            The user already has this nutrition plan:

            %s

            User health information:

            %s

            The user requested an alternative because:

            %s

            Suggest ONE suitable alternative.

            The alternative must respect the user's health conditions.

            Return ONLY valid JSON exactly like this:

            {
              "originalItem": "item being replaced",
              "alternativeItem": "suggested alternative",
              "reason": "short reason"
            }

            Requirements:

            - Write all values in Arabic.
            - Keep JSON field names in English.
            - Do not change the entire nutrition plan.
            - Only replace the relevant food or meal.
            - Consider diabetes if present.
            - Consider hypertension if present.
            - Do not prescribe or change medication.
            - Do not invent health information.
            - Return ONLY valid JSON.
            """.formatted(
                nutritionPlan.getItems(),
                healthData,
                request.getReason()
        );


        String aiResult = anthropicService.generate(prompt);

        System.out.println("NUTRITION ALTERNATIVE AI RESULT:");
        System.out.println(aiResult);

        try {

            NutritionAlternativeResponse response =
                    objectMapper.readValue(
                            aiResult,
                            NutritionAlternativeResponse.class
                    );


            // Replace the old item with the alternative
            String updatedItems =
                    nutritionPlan.getItems().replace(
                            response.getOriginalItem(),
                            response.getAlternativeItem()
                    );


            // Update existing nutrition plan
            nutritionPlan.setItems(updatedItems);

            nutritionPlanRepository.save(nutritionPlan);


            return response;

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to read nutrition alternative: "
                            + e.getMessage()
            );
        }
    }


    public ExerciseAlternativeResponse generateExerciseAlternative(
            Integer userId,
            AlternativeRequestDTO request) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        HealthProfile healthProfile =
                healthProfileRepository.findHealthProfileByUserId(userId);

        if (healthProfile == null) {
            throw new ApiException("Health profile not found");
        }

        ExercisePlan exercisePlan =
                exercisePlanRepository.findExercisePlanByUserId(userId);

        if (exercisePlan == null) {
            throw new ApiException("Exercise plan not found");
        }


        String healthData =
                "Height: " + healthProfile.getHeightCm()
                        + ", Activity Level: " + healthProfile.getActivityLevel()
                        + ", Conditions: " + healthProfile.getConditions()
                        + ", Exercise Risk: " + healthProfile.getExerciseRisk();


        String prompt = """
            The user already has this exercise plan:

            %s

            User health information:

            %s

            The user requested an alternative because:

            %s

            Suggest ONE suitable exercise alternative.

            Return ONLY valid JSON exactly like this:

            {
              "originalExercise": "exercise being replaced",
              "alternativeExercise": "alternative exercise with sets, repetitions and days per week",
              "reason": "short reason"
            }

            Requirements:

            - Write all values in Arabic.
            - Keep JSON field names in English.
            - Replace only the relevant exercise.
            - Do not regenerate the entire exercise plan.
            - Consider the user's health conditions.
            - Consider the user's activity level.
            - Consider exercise risk.
            - The alternative should have sets, repetitions and days per week when applicable.
            - Avoid unsafe high-intensity exercises.
            - Do not diagnose diseases.
            - Do not invent health information.
            - Return ONLY valid JSON.
            """.formatted(
                exercisePlan.getExercises(),
                healthData,
                request.getReason()
        );


        String aiResult = anthropicService.generate(prompt);

        System.out.println("EXERCISE ALTERNATIVE AI RESULT:");
        System.out.println(aiResult);

        try {

            ExerciseAlternativeResponse response =
                    objectMapper.readValue(
                            aiResult,
                            ExerciseAlternativeResponse.class
                    );


            // Replace old exercise with alternative
            String updatedExercises =
                    exercisePlan.getExercises().replace(
                            response.getOriginalExercise(),
                            response.getAlternativeExercise()
                    );


            // Update existing exercise plan
            exercisePlan.setExercises(updatedExercises);

            exercisePlanRepository.save(exercisePlan);


            return response;

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to read exercise alternative: "
                            + e.getMessage()
            );
        }
    }
    public MedicationMealCheckDTO checkMedicationMealTiming(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        HealthProfile healthProfile =
                healthProfileRepository.findHealthProfileByUserId(userId);

        if (healthProfile == null) {
            throw new ApiException("Health profile not found");
        }

        List<MedicationSchedule> medications =
                medicationScheduleRepository.findByUser_IdAndIsActiveTrue(userId);

        if (medications.isEmpty()) {
            throw new ApiException("No active medications found");
        }


        StringBuilder medicationData = new StringBuilder();

        for (MedicationSchedule medication : medications) {

            medicationData.append("Medication: ")
                    .append(medication.getMedicationName())

                    .append(", Dosage: ")
                    .append(medication.getDosage())

                    .append(", Meal Relation: ")
                    .append(medication.getMealRelation())

                    .append(", Times: ")
                    .append(medication.getTimes())

                    .append("\n");
        }


        String healthData =
                "Conditions: " + healthProfile.getConditions()
                        + ", Activity Level: " + healthProfile.getActivityLevel();


        String prompt = """
            Check whether the user's medication meal schedule
            is compatible with the current nutrition plan.

            Nutrition Plan:
            %s

            Active Medications:
            %s

            Health Information:
            %s

            Important rules:

            - Only compare medication timing and meal relation with the nutrition plan.
            - Do not prescribe medication.
            - Do not change medication dosage.
            - Do not recommend stopping medication.
            - Do not invent medication instructions.
            - If the information is insufficient, clearly say that.
            - If there may be a medication-food interaction that cannot be confirmed from the provided data, recommend checking with a pharmacist or healthcare professional.

            Return ONLY valid JSON exactly like this:

            {
              "status": "OK or CHECK_NEEDED",
              "summary": "short summary",
              "issues": "issues found or no issues found",
              "recommendation": "safe recommendation"
            }

            Write all values in Arabic.
            Keep JSON field names in English.
            """.formatted(
                nutritionPlan.getItems(),
                medicationData.toString(),
                healthData
        );


        String aiResult = anthropicService.generate(prompt);


        try {

            return objectMapper.readValue(
                    aiResult,
                    MedicationMealCheckDTO.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to read medication meal check response: "
                            + e.getMessage()
            );
        }
    }
    public NutritionShoppingListDTO generateNutritionShoppingList(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        String prompt = """
            You are given an existing nutrition plan.

            Extract only the grocery items needed from the plan.

            Do not create a new nutrition plan.
            Do not add foods that are not mentioned in the plan.
            Do not change the user's nutrition plan.

            Organize the items into:
            proteins
            carbohydrates
            vegetablesAndFruits
            others

            Return all values in Arabic.
            JSON field names must remain in English.

            Return JSON only in this format:

            {
              "proteins": [],
              "carbohydrates": [],
              "vegetablesAndFruits": [],
              "others": []
            }

            Nutrition Plan:
            %s
            """.formatted(nutritionPlan.getItems());

        String aiResult = anthropicService.generate(prompt);

        try {

            return objectMapper.readValue(
                    aiResult,
                    NutritionShoppingListDTO.class
            );

        } catch (Exception e) {
            throw new ApiException("Failed to generate nutrition shopping list");
        }
    }
}