package com.example.rafeeq.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evidence_references")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvidenceReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 250)
    private String title;

    @Column(nullable = false, length = 100)
    private String organization;

    @Column(nullable = false, length = 100)
    private String topic;

    @Column(columnDefinition = "text", nullable = false)
    private String recommendation;

    @Column(columnDefinition = "text")
    private String authors;

    @Column(length = 100)
    private String doi;

    @Column(columnDefinition = "text")
    private String sourceUrl;
}