package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.NutritionPlanRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionPlanService {

    private final NutritionPlanRepository nutritionPlanRepository;
    private final UserRepository userRepository;


    public List<NutritionPlan> getAllNutritionPlans() {
        return nutritionPlanRepository.findAll();
    }


    public void addNutritionPlan(Integer userId, NutritionPlan nutritionPlan) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        NutritionPlan oldPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (oldPlan != null) {
            throw new ApiException("User already has a nutrition plan");
        }

        nutritionPlan.setUser(user);

        nutritionPlanRepository.save(nutritionPlan);
    }


    public void updateNutritionPlan(Integer id, NutritionPlan nutritionPlan) {

        NutritionPlan oldPlan =
                nutritionPlanRepository.findNutritionPlanById(id);

        if (oldPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        oldPlan.setItems(nutritionPlan.getItems());
        oldPlan.setSummary(nutritionPlan.getSummary());

        nutritionPlanRepository.save(oldPlan);
    }


    public void deleteNutritionPlan(Integer id) {

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanById(id);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        nutritionPlanRepository.delete(nutritionPlan);
    }


    public NutritionPlan getNutritionPlanByUserId(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanByUserId(userId);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        return nutritionPlan;
    }

    // Get Nutrition Plan By ID
    public NutritionPlan getNutritionPlanById(Integer id) {

        NutritionPlan nutritionPlan =
                nutritionPlanRepository.findNutritionPlanById(id);

        if (nutritionPlan == null) {
            throw new ApiException("Nutrition plan not found");
        }

        return nutritionPlan;
    }
}
