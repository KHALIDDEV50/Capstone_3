package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.Model.ExercisePlan;
import com.example.rafeeq.Service.AIPlanService;
import com.example.rafeeq.Service.ExercisePlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercise-plan")
@RequiredArgsConstructor
public class ExercisePlanController {

    private final ExercisePlanService exercisePlanService;
    private final AIPlanService aiPlanService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllExercisePlans() {
        List<ExercisePlan> exercisePlans = exercisePlanService.getAllExercisePlans();

        return ResponseEntity.status(200).body(exercisePlans);
    }


    @PostMapping("/add/{userId}")
    public ResponseEntity<?> addExercisePlan(
            @PathVariable Integer userId,
            @RequestBody @Valid ExercisePlan exercisePlan) {

        exercisePlanService.addExercisePlan(userId, exercisePlan);

        return ResponseEntity.status(200).body(new ApiResponse("Exercise plan added successfully"));
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateExercisePlan(
            @PathVariable Integer id,
            @RequestBody @Valid ExercisePlan exercisePlan) {

        exercisePlanService.updateExercisePlan(id, exercisePlan);

        return ResponseEntity.status(200).body(new ApiResponse("Exercise plan updated successfully"));
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteExercisePlan(@PathVariable Integer id) {

        exercisePlanService.deleteExercisePlan(id);

        return ResponseEntity.status(200).body(new ApiResponse("Exercise plan deleted successfully"));
    }


    @GetMapping("/user-Exercise/{userId}")
    public ResponseEntity<?> getExercisePlanByUserId(@PathVariable Integer userId) {

        ExercisePlan exercisePlan = exercisePlanService.getExercisePlanByUserId(userId);

        return ResponseEntity.status(200).body(exercisePlan);
    }

    @GetMapping("/user/{userId}/review-status")
    public ResponseEntity<?> getExercisePlanReviewStatus(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(exercisePlanService.getExercisePlanReviewStatus(userId));
    }


}