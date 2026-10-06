package com.example.rafeeq.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAppointmentEligibilityResponseDTO {

    private Integer userId;
    private Boolean appointmentRecommended;
    private String priority;
    private String condition;
    private String latestReading;
    private String reason;
    private String message;
    private Boolean bookingAvailable;
}