package com.example.rafeeq.DTO;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExercisePlanResponseDTO {

    private Integer id;

    private Integer userId;

    private String goal;

    private JsonNode exercises;

    private String summary;

    private JsonNode evidence;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}