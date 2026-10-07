package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.PlanReviewStatusDTO;
import com.example.rafeeq.Model.ExercisePlan;
//import com.example.capston3.Model.User;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.ExercisePlanRepository;
//import com.example.capston3.Repository.UserRepository;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercisePlanService {

    private final ExercisePlanRepository exercisePlanRepository;
      private final UserRepository userRepository;
    private final VitalSignRepository vitalSignRepository;

    public List<ExercisePlan> getAllExercisePlans() {
        return exercisePlanRepository.findAll();
    }


    public void addExercisePlan(Integer userId, ExercisePlan exercisePlan) {

      User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        ExercisePlan oldPlan = exercisePlanRepository.findExercisePlanByUserId(userId);

        if (oldPlan != null) {
            throw new ApiException("User already has an exercise plan");
        }

        exercisePlan.setUser(user);

        exercisePlanRepository.save(exercisePlan);
    }


    public void updateExercisePlan(Integer id, ExercisePlan exercisePlan) {

        ExercisePlan oldPlan = exercisePlanRepository.findExercisePlanByUserId(id);

        if (oldPlan == null) {
            throw new ApiException("Exercise plan not found");
        }

        oldPlan.setGoal(exercisePlan.getGoal());
        oldPlan.setExercises(exercisePlan.getExercises());
        oldPlan.setSummary(exercisePlan.getSummary());

        exercisePlanRepository.save(oldPlan);
    }


    public void deleteExercisePlan(Integer id) {

        ExercisePlan exercisePlan = exercisePlanRepository.findExercisePlanByUserId(id);

        if (exercisePlan == null) {
            throw new ApiException("Exercise plan not found");
        }

        exercisePlanRepository.delete(exercisePlan);
    }


    public ExercisePlan getExercisePlanByUserId(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        ExercisePlan exercisePlan = exercisePlanRepository.findExercisePlanByUserId(userId);

        if (exercisePlan == null) {
            throw new ApiException("Exercise plan not found");
        }

        return exercisePlan;
    }

    public PlanReviewStatusDTO getExercisePlanReviewStatus(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        ExercisePlan exercisePlan =
                exercisePlanRepository.findExercisePlanByUserId(userId);

        if (exercisePlan == null) {
            throw new ApiException("Exercise plan not found");
        }

        List<VitalSign> vitalSigns =
                vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId);

        for (VitalSign vitalSign : vitalSigns) {

            if (vitalSign.getMeasuredAt().isAfter(exercisePlan.getUpdatedAt())
                    && ("HIGH".equals(vitalSign.getFlag())
                    || "CRITICAL".equals(vitalSign.getFlag()))) {

                return new PlanReviewStatusDTO(
                        true,
                        "A new " + vitalSign.getFlag()
                                + " " + vitalSign.getType()
                                + " reading was recorded after the exercise plan was updated"
                );
            }
        }

        return new PlanReviewStatusDTO(
                false,
                "Exercise plan is up to date"
        );
    }
}