package com.example.rafeeq.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "health_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Unique ID of the health profile

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user; // User who owns this health profile

    @Column(precision = 5, scale = 1)
    private BigDecimal heightCm; // User height in centimeters

    @Column(length = 20)
    private String activityLevel; // SEDENTARY, LIGHT, MODERATE, ACTIVE

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> conditions; // User health conditions such as Diabetes or Asthma

    @Column(length = 20)
    private String exerciseRisk; // Exercise risk level

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Date and time profile was created

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // Date and time profile was last updated
}