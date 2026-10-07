package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthAssessmentDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    // Previous Assessment ID
    private Integer previousAssessmentId;

    // Attachments JSON
    private String attachments;

    // User Notes
    private String userNotes;

    // Profile Snapshot JSON
    private String profileSnapshot;

    // Extracted Values JSON
    private String extractedValues;

    // AI Conclusion
    private String aiConclusion;

    // Assessment Trend
    @Size(max = 50, message = "Trend must not exceed 50 characters")
    private String trend;

    // Current Assessment
    private Boolean isCurrent;

    // Assessment Date
    private LocalDate assessmentDate;

    // Next Due Date
    private LocalDate nextDueDate;
}