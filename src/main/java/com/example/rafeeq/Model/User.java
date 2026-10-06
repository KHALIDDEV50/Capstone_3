package com.example.rafeeq.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Unique ID of the user

    @Column(nullable = false, length = 100)
    private String fullName; // Full name of the user

    @Column(nullable = false, unique = true, length = 100)
    private String email; // Unique email of the user

    @Column(nullable = false, unique = true, length = 20)
    private String phone; // Unique phone number of the user

    @Column(nullable = false)
    private String password; // Password of the user

    @Column(nullable = false)
    private LocalDate dateOfBirth; // Date of birth of the user

    @Column(nullable = false, length = 10)
    private String gender; // MALE or FEMALE

    @Column(nullable = false)
    private Boolean whatsappOptIn; // Whether the user agreed to WhatsApp notifications

    @Column(nullable = false, length = 10)
    private String role; // USER or ADMIN

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private HealthProfile healthProfile; // Health profile belonging to the user

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VitalSign> vitalSigns; // Vital signs belonging to the user

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private NutritionPlan nutritionPlan;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private ExercisePlan exercisePlan;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Date and time when the user was created

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Date and time when the user was last updated
}