package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.MedicationScheduleDTO;
import com.example.rafeeq.DTO.MedicationScheduleResponseDTO;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Service.AIPlanService;
import com.example.rafeeq.Service.MedicationScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/medication-schedule")
@RequiredArgsConstructor
public class MedicationScheduleController {

    private final MedicationScheduleService medicationScheduleService;
    private final AIPlanService aiPlanService;
    // Get All Medication Schedule
    @GetMapping("/get")
    public ResponseEntity<?> getAllMedicationSchedule() {

        List<MedicationScheduleResponseDTO> medicationSchedules = medicationScheduleService.getAllMedicationSchedule();

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Get Medication Schedule By ID
    @GetMapping("/get/{id}")
    public ResponseEntity<?> getMedicationScheduleById(
            @PathVariable Integer id) {

        MedicationScheduleResponseDTO medicationSchedule = medicationScheduleService.getMedicationScheduleById(id);

        return ResponseEntity.status(200).body(medicationSchedule);
    }

    // Get Medication Schedule By User ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getMedicationScheduleByUserId(@PathVariable Integer userId) {

        List<MedicationScheduleResponseDTO> medicationSchedules = medicationScheduleService.getMedicationScheduleByUserId(userId);

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Get Active Medication Schedule By User ID
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<?> getActiveMedicationScheduleByUserId(
            @PathVariable Integer userId) {

        List<MedicationScheduleResponseDTO> medicationSchedules = medicationScheduleService.getActiveMedicationScheduleByUserId(userId);

        return ResponseEntity.status(200).body(medicationSchedules);
    }

    // Add Medication Schedule
    @PostMapping("/add")
    public ResponseEntity<?> addMedicationSchedule(@RequestBody @Valid MedicationScheduleDTO medicationScheduleDTO) {

        medicationScheduleService.addMedicationSchedule(medicationScheduleDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Add Successful"));
    }

    // Update Medication Schedule
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMedicationSchedule(@PathVariable Integer id, @RequestBody @Valid MedicationScheduleDTO medicationScheduleDTO) {

        medicationScheduleService.updateMedicationSchedule(id, medicationScheduleDTO);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Update Successful"));
    }

    // Delete Medication Schedule
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMedicationSchedule(@PathVariable Integer id) {

        medicationScheduleService.deleteMedicationSchedule(id);

        return ResponseEntity.status(200).body(new ApiResponse("Medication Schedule Delete Successful"));
    }

    @GetMapping("/{userId}/medication-meal-check")
    public ResponseEntity<?> checkMedicationMealTiming(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(aiPlanService.checkMedicationMealTiming(userId));
    }

    @GetMapping("/user/{userId}/next-dose")
    public ResponseEntity<?> getNextMedicationDose(@PathVariable Integer userId) {

        return ResponseEntity.status(200).body(medicationScheduleService.getNextMedicationDose(userId));
    }
}