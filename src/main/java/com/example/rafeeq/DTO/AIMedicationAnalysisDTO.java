package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIMedicationAnalysisDTO {

    // General summary of the user's medication schedule
    private String summary;

    // Analysis of each medication
    private List<MedicationAnalysis> medications;

    // Points that require follow-up
    private List<String> followUp;

    // General recommendations
    private List<String> recommendations;


    // Represents the analysis of one medication
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicationAnalysis {

        // Medication name
        private String name;

        // Medication dosage
        private String dosage;

        // Relation to meals
        private String mealRelation;

        // Medication schedule times
        private String times;

        // General AI note about the medication schedule
        private String generalNote;
    }
}