package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationMealCheckDTO {

    private String status;
    private String summary;
    private String issues;
    private String recommendation;
}