package com.example.rafeeq.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NutritionPlanResponseDTO {

    private Integer id;

    private Integer userId;

    private JsonNode items;

    private String summary;

    private String reason;

    private JsonNode evidence;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}