package com.example.rafeeq.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO {

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name must not exceed 100 characters")
    private String fullName; // Full name of the user

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email; // Email of the user

    @NotBlank(message = "Phone is required")
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone; // Phone number of the user

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password; // Password of the user

    @NotNull(message = "Date of birth is required")
    private LocalDate dateOfBirth; // Date of birth of the user

    @NotBlank(message = "Gender is required")
    @Pattern(
            regexp = "MALE|FEMALE",
            message = "Gender must be MALE or FEMALE"
    )
    private String gender; // MALE or FEMALE

    @NotNull(message = "WhatsApp opt-in is required")
    private Boolean whatsappOptIn; // Whether the user agreed to WhatsApp notifications
}