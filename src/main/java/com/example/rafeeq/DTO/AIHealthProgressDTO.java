
package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIHealthProgressDTO {

    // Overall health progress summary
    private String overallProgress;

    // Progress score from 0 to 100
    private Integer progressScore;

    // AI summary of the user's health progress
    private String summary;

    // Health indicators that showed improvement
    private List<String> improvements;

    // Health indicators that remained stable
    private List<String> stableIndicators;

    // Areas that need follow-up
    private List<String> areasToFollowUp;

    // General and safe recommendations
    private List<String> recommendations;
}