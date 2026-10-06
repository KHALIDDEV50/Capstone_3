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
public class HealthInsightResponseDTO {

    private Integer userId;
    private List<VitalInsightDTO> insights;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VitalInsightDTO {

        private String type;
        private String unit;

        private BigDecimal latestValue;
        private BigDecimal previousValue;
        private BigDecimal change;
        private BigDecimal changePercentage;

        private BigDecimal latestSystolic;
        private BigDecimal latestDiastolic;

        private BigDecimal previousSystolic;
        private BigDecimal previousDiastolic;

        private BigDecimal systolicChange;
        private BigDecimal diastolicChange;

        private String trend;
        private String currentFlag;

        private LocalDateTime measuredAt;
        private LocalDateTime previousMeasuredAt;
    }
}