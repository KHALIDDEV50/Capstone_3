package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignUpdateDTO {

    @NotBlank(message = "Type is required")
    @Pattern(
            regexp = "BLOOD_PRESSURE|GLUCOSE|WEIGHT|WAIST|HEART_RATE",
            message = "Type must be BLOOD_PRESSURE, GLUCOSE, WEIGHT, WAIST, or HEART_RATE"
    )
    private String type;

    private BigDecimal value;

    private BigDecimal systolic;

    private BigDecimal diastolic;

    private String unit;

    @NotNull(message = "Measured date is required")
    private LocalDateTime measuredAt;
}