package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationScheduleDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Medication name is required")
    @Size(max = 255, message = "Medication name must not exceed 255 characters")
    private String medicationName;

    @NotBlank(message = "Dosage is required")
    @Size(max = 100, message = "Dosage must not exceed 100 characters")
    private String dosage;

    @Size(max = 50, message = "Meal relation must not exceed 50 characters")
    private String mealRelation;

    private String times;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isActive;
}