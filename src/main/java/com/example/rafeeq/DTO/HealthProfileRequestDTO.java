package com.example.rafeeq.DTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthProfileRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
    @DecimalMax(value = "300.0", message = "Height must not exceed 300 cm")
    private BigDecimal heightCm;

    @NotNull(message = "Activity level is required")
    @Pattern(
            regexp = "SEDENTARY|LIGHT|MODERATE|ACTIVE",
            message = "Activity level must be SEDENTARY, LIGHT, MODERATE, or ACTIVE"
    )
    private String activityLevel;

    private List<String> conditions;
}