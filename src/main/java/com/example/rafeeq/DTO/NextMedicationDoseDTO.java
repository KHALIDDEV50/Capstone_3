package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NextMedicationDoseDTO {

    private String medicationName;
    private String dosage;
    private String nextTime;
    private String mealRelation;
}