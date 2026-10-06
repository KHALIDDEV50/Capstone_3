package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EvidenceReferenceDTO {

    private Integer id;
    private String title;
    private String organization;
    private String topic;
    private String recommendation;
    private String authors;
    private String doi;
    private String sourceUrl;
}