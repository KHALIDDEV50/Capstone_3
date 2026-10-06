package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.DoctorAppointmentEligibilityResponseDTO;
import com.example.rafeeq.Service.DoctorAppointmentEligibilityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/doctor-appointments")
@AllArgsConstructor
public class DoctorAppointmentEligibilityController {

    private final DoctorAppointmentEligibilityService doctorAppointmentEligibilityService;

    @GetMapping("/eligibility/{userId}")
    public ResponseEntity<DoctorAppointmentEligibilityResponseDTO> checkEligibility(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(doctorAppointmentEligibilityService.checkEligibility(userId));
    }
}