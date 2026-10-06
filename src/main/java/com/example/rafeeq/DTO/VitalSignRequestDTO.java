package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignRequestDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Integer userId;

    @NotBlank(message = "Type is required")
    @Pattern(
            regexp = "BLOOD_PRESSURE|GLUCOSE|WEIGHT|WAIST|HEART_RATE",
            message = "Type must be BLOOD_PRESSURE, GLUCOSE, WEIGHT, WAIST, or HEART_RATE"
    )
    private String type;

    @Positive(message = "Value must be positive")
    private BigDecimal value;

    @Positive(message = "Systolic value must be positive")
    private BigDecimal systolic;

    @Positive(message = "Diastolic value must be positive")
    private BigDecimal diastolic;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Measured date is required")
    private LocalDateTime measuredAt;
}