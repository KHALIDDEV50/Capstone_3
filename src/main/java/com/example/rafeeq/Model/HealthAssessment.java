package com.example.rafeeq.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "health_assessments")
@AllArgsConstructor
@NoArgsConstructor
public class HealthAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User ID
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Previous Assessment ID
    @ManyToOne
    @JoinColumn(name = "previous_assessment_id")
    @JsonIgnore
    private HealthAssessment previousAssessment;

    // Attachments JSON
    @Column(columnDefinition = "text")
    private String attachments;

    // User Notes
    @Column(name = "user_notes", columnDefinition = "text")//
    private String userNotes;

    // Profile Snapshot JSON
    @Column(name = "profile_snapshot", columnDefinition = "text")//
    private String profileSnapshot;

    // Extracted Values JSON
    @Column(name = "extracted_values", columnDefinition = "text")//
    private String extractedValues;

    // AI Conclusion
    // change to Sammy
    @Column(name = "ai_conclusion", columnDefinition = "text")//
    private String aiConclusion;

    // Assessment Trend
    @Column
    private String trend;

    // Current Assessment
    @Column(name = "is_current", nullable = false)
    private Boolean isCurrent = false;

    // Assessment Date
    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    // Next Due Date
    @Column(name = "next_due_date")
    private LocalDate nextDueDate;

    // Created At
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}