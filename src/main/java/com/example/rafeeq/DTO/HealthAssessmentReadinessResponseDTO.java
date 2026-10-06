package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthAssessmentReadinessResponseDTO {

    private Integer userId;
    private Integer completionPercentage;
    private String assessmentStatus;

    private List<String> availableData;
    private List<String> missingData;

    private List<AssessmentAreaDTO> assessmentAreas;

    private String message;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AssessmentAreaDTO {

        private String area;
        private String status;
        private List<String> available;
        private List<String> missing;
    }
}