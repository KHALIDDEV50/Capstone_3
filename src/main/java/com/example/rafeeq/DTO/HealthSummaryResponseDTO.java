package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthSummaryResponseDTO {

    private Integer userId;
    private String activityLevel;
    private String exerciseRisk;
    private LatestVitalsDTO latestVitals;
    private VitalStatusDTO status;
    private Integer totalVitalMeasurements;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LatestVitalsDTO {

        private String bloodPressure;
        private Double glucose;
        private Double weight;
        private Double waist;
        private Double heartRate;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VitalStatusDTO {

        private String bloodPressure;
        private String glucose;
        private String weight;
        private String waist;
        private String heartRate;
    }
}