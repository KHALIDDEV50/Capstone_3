package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSignResponseDTO {

    private Integer id; // Vital sign ID

    private Integer userId; // ID of the user

    private String type; // Type of vital sign

    private BigDecimal value; // General measurement value

    private BigDecimal systolic; // Systolic blood pressure

    private BigDecimal diastolic; // Diastolic blood pressure

    private String unit; // Measurement unit

    private String flag; // Automatically calculated health status

    private LocalDateTime measuredAt; // Measurement date and time

    private LocalDateTime createdAt; // Record creation date and time
}