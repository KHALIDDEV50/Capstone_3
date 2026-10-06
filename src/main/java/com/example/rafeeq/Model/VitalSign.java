package com.example.rafeeq.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Unique ID of the vital sign

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // User who owns this vital sign

    @Column(nullable = false, length = 20)
    private String type; // BLOOD_PRESSURE, GLUCOSE, WEIGHT, WAIST, HEART_RATE

    @Column(precision = 6, scale = 1)
    private BigDecimal value; // General measurement value

    @Column(precision = 6, scale = 1)
    private BigDecimal systolic; // Systolic blood pressure

    @Column(precision = 6, scale = 1)
    private BigDecimal diastolic; // Diastolic blood pressure

    @Column(nullable = false, length = 20)
    private String unit; // mg/dL, kg, cm, bpm, mmHg

    @Column(length = 10)
    private String flag; // Automatically calculated: NORMAL, HIGH, LOW, CRITICAL

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt; // Date and time of measurement

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // Date and time record was created
}