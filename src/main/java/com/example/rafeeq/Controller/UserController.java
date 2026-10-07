package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.UserRequestDTO;
import com.example.rafeeq.DTO.UserResponseDTO;
import com.example.rafeeq.Service.AIPlanService;
import com.example.rafeeq.Service.DailyTimelineService;
import com.example.rafeeq.Service.HealthPlanStatusService;
import com.example.rafeeq.Service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final HealthPlanStatusService healthPlanStatusService;
    private final DailyTimelineService dailyTimelineService;
    private final AIPlanService aiPlanService;
    @GetMapping("/get")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {

        return ResponseEntity.status(200)
                .body(userService.getAllUsers());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @PathVariable Integer id) {

        return ResponseEntity.status(200)
                .body(userService.getUserById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addUser(
            @Valid @RequestBody UserRequestDTO userRequestDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        userService.addUser(userRequestDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse("User added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UserRequestDTO userRequestDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        userService.updateUser(id, userRequestDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse("User updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteUser(
            @PathVariable Integer id) {

        userService.deleteUser(id);

        return ResponseEntity.status(200)
                .body(new ApiResponse("User deleted successfully"));
    }

    @GetMapping("/{userId}/health-plan-status")
    public ResponseEntity<?> getHealthPlanStatus(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(healthPlanStatusService.getHealthPlanStatus(userId));
    }

    @GetMapping("/{userId}/daily-timeline")
    public ResponseEntity<?> getDailyTimeline(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(dailyTimelineService.getDailyTimeline(userId));
    }
    @PostMapping("/{userId}/nutrition-shopping-list")
    public ResponseEntity<?> generateNutritionShoppingList(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(aiPlanService.generateNutritionShoppingList(userId));
    }

}