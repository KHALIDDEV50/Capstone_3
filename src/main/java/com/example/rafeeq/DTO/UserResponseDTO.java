package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {

    private Integer id; // Unique ID of the user

    private String fullName; // Full name of the user

    private String email; // Email of the user

    private String phone; // Phone number of the user

    private LocalDate dateOfBirth; // Date of birth of the user

    private String gender; // MALE or FEMALE

    private Boolean whatsappOptIn; // Whether the user agreed to WhatsApp notifications

    private String role; // USER or ADMIN

    private LocalDateTime createdAt; // Date and time when the user was created

    private LocalDateTime updatedAt; // Date and time when the user was last updated
}