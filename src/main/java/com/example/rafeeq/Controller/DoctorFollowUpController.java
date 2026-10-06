package com.example.rafeeq.Controller;

import com.example.rafeeq.DTO.DoctorFollowUpResponseDTO;
import com.example.rafeeq.Service.DoctorFollowUpService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/doctor-follow-up")
@AllArgsConstructor
public class DoctorFollowUpController {

    private final DoctorFollowUpService doctorFollowUpService;

    @GetMapping("/{userId}")
    public ResponseEntity<DoctorFollowUpResponseDTO> getDoctorFollowUp(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(doctorFollowUpService.getDoctorFollowUp(userId));
    }
}