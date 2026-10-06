package com.example.rafeeq.Controller;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.Api.ApiResponse;
import com.example.rafeeq.DTO.VitalSignRequestDTO;
import com.example.rafeeq.DTO.VitalSignResponseDTO;
import com.example.rafeeq.DTO.VitalSignUpdateDTO;
import com.example.rafeeq.Service.VitalSignService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/vital-signs")
@AllArgsConstructor
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @GetMapping("/get")
    public ResponseEntity<List<VitalSignResponseDTO>> getAllVitalSigns() {

        return ResponseEntity.status(200)
                .body(vitalSignService.getAllVitalSigns());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<VitalSignResponseDTO> getVitalSignById(
            @PathVariable Integer id) {

        return ResponseEntity.status(200)
                .body(vitalSignService.getVitalSignById(id));
    }

    @GetMapping("/get-by-user/{userId}")
    public ResponseEntity<List<VitalSignResponseDTO>> getVitalSignsByUser(
            @PathVariable Integer userId) {

        return ResponseEntity.status(200)
                .body(vitalSignService.getVitalSignsByUser(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addVitalSign(
            @Valid @RequestBody VitalSignRequestDTO vitalSignRequestDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        vitalSignService.addVitalSign(vitalSignRequestDTO);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Vital sign added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateVitalSign(
            @PathVariable Integer id,
            @Valid @RequestBody VitalSignUpdateDTO vitalSignUpdateDTO,
            Errors errors) {

        if (errors.hasErrors()) {
            throw new ApiException(
                    errors.getFieldError().getDefaultMessage()
            );
        }

        vitalSignService.updateVitalSign(
                id,
                vitalSignUpdateDTO
        );

        return ResponseEntity.status(200)
                .body(new ApiResponse("Vital sign updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteVitalSign(
            @PathVariable Integer id) {

        vitalSignService.deleteVitalSign(id);

        return ResponseEntity.status(200)
                .body(new ApiResponse("Vital sign deleted successfully"));
    }
}