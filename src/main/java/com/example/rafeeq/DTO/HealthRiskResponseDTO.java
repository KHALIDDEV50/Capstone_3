package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthRiskResponseDTO {

    private Integer userId;
    private String overallRiskLevel;
    private List<RiskDTO> risks;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RiskDTO {

        private String condition;
        private String riskLevel;
        private String reason;
        private List<String> evidence;
        private String recommendation;
    }
}