package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.Model.NutritionPlan;
import com.example.rafeeq.Service.NutritionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/nutrition-plans")
@RequiredArgsConstructor
public class NutritionPlanController {

    private final NutritionPlanService nutritionPlanService;


    // Get All Nutrition Plans
    @GetMapping("/get")
    public ResponseEntity<List<NutritionPlan>> getAllNutritionPlans() {

        List<NutritionPlan> nutritionPlans =
                nutritionPlanService.getAllNutritionPlans();

        return ResponseEntity.status(200)
                .body(nutritionPlans);
    }


    // Get Nutrition Plan By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<NutritionPlan> getNutritionPlanById(
            @PathVariable Integer id) {

        NutritionPlan nutritionPlan = nutritionPlanService.getNutritionPlanById(id);

        return ResponseEntity.status(200)
                .body(nutritionPlan);
    }


    // Get Nutrition Plan By User ID
    @GetMapping("/get-by-user/{userId}")
    public ResponseEntity<NutritionPlan> getNutritionPlanByUserId(
            @PathVariable Integer userId) {

        NutritionPlan nutritionPlan =
                nutritionPlanService.getNutritionPlanByUserId(userId);

        return ResponseEntity.status(200)
                .body(nutritionPlan);
    }


    // Update Nutrition Plan
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateNutritionPlan(
            @PathVariable Integer id,
            @RequestBody @Valid NutritionPlan nutritionPlan) {

        nutritionPlanService.updateNutritionPlan(id, nutritionPlan);

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Nutrition plan updated successfully"
                ));
    }


    // Delete Nutrition Plan
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteNutritionPlan(
            @PathVariable Integer id) {

        nutritionPlanService.deleteNutritionPlan(id);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Nutrition plan deleted successfully"));
    }
}