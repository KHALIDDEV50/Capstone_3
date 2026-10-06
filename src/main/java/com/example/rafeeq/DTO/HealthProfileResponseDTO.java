package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthProfileResponseDTO {

    private Integer id; // Health profile ID

    private Integer userId; // ID of the user

    private BigDecimal heightCm; // User height in centimeters

    private String activityLevel; // User activity level

    private List<String> conditions; // User health conditions

    private String exerciseRisk; // Exercise risk level

    private LocalDateTime createdAt; // Profile creation date

    private LocalDateTime updatedAt; // Profile last update date
}