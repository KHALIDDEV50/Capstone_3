package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthPlanStatusDTO;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthPlanStatusService {

    private final UserRepository userRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExercisePlanRepository exercisePlanRepository;
    private final VitalSignRepository vitalSignRepository;


    public HealthPlanStatusDTO getHealthPlanStatus(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }


        HealthProfile healthProfile =
                healthProfileRepository.findHealthProfileByUserId(userId);


        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);


        ExercisePlan exercisePlan =
                exercisePlanRepository.findExercisePlanByUserId(userId);


        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);


        boolean hasHealthProfile = healthProfile != null;

        boolean hasNutritionPlan = nutritionPlan != null;

        boolean hasExercisePlan = exercisePlan != null;

        boolean hasVitalSigns =
                vitalSigns != null && !vitalSigns.isEmpty();


        String exerciseRisk = null;

        if (healthProfile != null) {
            exerciseRisk = healthProfile.getExerciseRisk();
        }


        String status;
        String message;


        if (healthProfile == null) {

            status = "INCOMPLETE";

            message = "Health profile is missing";


        } else if (!hasVitalSigns) {

            status = "INCOMPLETE";

            message = "Vital signs are missing";


        } else if (nutritionPlan == null
                && exercisePlan == null) {

            status = "INCOMPLETE";

            message = "Nutrition and exercise plans are missing";


        } else if (nutritionPlan == null) {

            status = "INCOMPLETE";

            message = "Nutrition plan is missing";


        } else if (exercisePlan == null) {

            status = "INCOMPLETE";

            message = "Exercise plan is missing";


        } else if ("HIGH".equals(exerciseRisk)) {

            status = "CAUTION";

            message =
                    "Health plan is complete but exercise risk is high";


        } else {

            status = "COMPLETE";

            message =
                    "User health plan is complete and ready to follow";
        }


        return new HealthPlanStatusDTO(
                status,
                hasHealthProfile,
                hasNutritionPlan,
                hasExercisePlan,
                hasVitalSigns,
                exerciseRisk,
                message
        );
    }
}