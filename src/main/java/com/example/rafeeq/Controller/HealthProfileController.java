package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.HealthProfileRequestDTO;
import com.example.rafeeq.DTO.HealthProfileResponseDTO;
import com.example.rafeeq.Service.HealthProfileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/health-profiles")
@AllArgsConstructor
public class HealthProfileController {

    private final HealthProfileService healthProfileService;

    @GetMapping("/get")
    public ResponseEntity<List<HealthProfileResponseDTO>> getAllHealthProfiles() {

        return ResponseEntity.status(200)
                .body(healthProfileService.getAllHealthProfiles());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HealthProfileResponseDTO> getHealthProfileById(
            @PathVariable Integer id) {

        return ResponseEntity.status(200)
                .body(healthProfileService.getHealthProfileById(id));
    }

    @GetMapping("/get-by-user/{userId}")
    public ResponseEntity<HealthProfileResponseDTO> getHealthProfileByUser(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(healthProfileService.getHealthProfileByUser(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addHealthProfile(
            @Valid @RequestBody HealthProfileRequestDTO healthProfileRequestDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        healthProfileService.addHealthProfile(
                healthProfileRequestDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Health profile added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateHealthProfile(
            @PathVariable Integer id,
            @Valid @RequestBody HealthProfileRequestDTO healthProfileRequestDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        healthProfileService.updateHealthProfile(
                id,
                healthProfileRequestDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Health profile updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteHealthProfile(
            @PathVariable Integer id) {

        healthProfileService.deleteHealthProfile(id);

        return ResponseEntity.status(200)
                .body(new ApiResponse(
                        "Health profile deleted successfully"));
    }
}