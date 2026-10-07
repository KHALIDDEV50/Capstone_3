
package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIMedicationImpactDTO {

    // Overall summary of the medication and health assessment data
    private String summary;

    // Medication-related observations
    private List<MedicationImpact> medications;

    // Health changes observed between assessments
    private List<String> healthChanges;

    // Important points that need follow-up
    private List<String> followUp;

    // General recommendations
    private List<String> recommendations;


    // Represents the relationship between
    // a medication and the recorded health changes
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MedicationImpact {

        // Medication name
        private String name;

        // Medication start date
        private String startDate;

        // Medication end date
        private String endDate;

        // Health observation related to the timeline
        private String healthObservation;

        // AI interpretation based only on available data
        private String generalNote;
    }
}
