package com.example.rafeeq.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlternativeRequestDTO {

    @NotEmpty(message = "Reason is required")
    private String reason;
}