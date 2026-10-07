package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIHealthTrendDTO {

    // Overall health trend between the current and previous assessment
    private String overallTrend;

    // Health indicators that have improved
    private List<String> improvements;

    // Health indicators that need attention
    private List<String> concerns;

    // General recommendations based on the changes
    private List<String> recommendations;
}