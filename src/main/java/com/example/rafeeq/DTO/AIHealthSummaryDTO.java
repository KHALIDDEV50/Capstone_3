package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIHealthSummaryDTO {

    // General summary of the user's health assessment
    private String summary;

    // Important health findings
    private List<String> keyFindings;

    // Points that require follow-up
    private List<String> followUp;

    // General recommendations
    private List<String> recommendations;
}