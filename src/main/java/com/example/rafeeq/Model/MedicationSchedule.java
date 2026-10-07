package com.example.rafeeq.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medication_schedules")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // Medication Name
    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    // Dosage
    @Column(nullable = false)
    private String dosage;

    // Before / With / After Meal
    @Column(name = "meal_relation")
    private String mealRelation;

    // Medication Times
    @Column(columnDefinition = "text")//
    private String times;

    // Start Date
    @Column(name = "start_date")
    private LocalDate startDate;

    // End Date
    @Column(name = "end_date")
    private LocalDate endDate;

    // Active Status
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Created At
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Updated At
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}