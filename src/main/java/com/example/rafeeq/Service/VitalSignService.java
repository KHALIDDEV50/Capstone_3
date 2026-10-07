package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.VitalSignRequestDTO;
import com.example.rafeeq.DTO.VitalSignResponseDTO;
import com.example.rafeeq.DTO.VitalSignUpdateDTO;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Model.VitalSign;
import com.example.rafeeq.Repository.UserRepository;
import com.example.rafeeq.Repository.VitalSignRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
public class VitalSignService {

    private final VitalSignRepository vitalSignRepository;
    private final UserRepository userRepository;

    public List<VitalSignResponseDTO> getAllVitalSigns() {
        return vitalSignRepository.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public VitalSignResponseDTO getVitalSignById(Integer id) {
        VitalSign vitalSign = vitalSignRepository.findById(id)
                .orElseThrow(() -> new ApiException("Vital sign not found"));

        return convertToResponseDTO(vitalSign);
    }

    public List<VitalSignResponseDTO> getVitalSignsByUser(Integer userId) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        return vitalSignRepository.findByUserIdOrderByMeasuredAtDesc(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public VitalSignResponseDTO getLatestVitalSign(Integer userId, String type) {

        if (!userRepository.existsById(userId)) {
            throw new ApiException("User not found");
        }

        VitalSign vitalSign = vitalSignRepository
                .findFirstByUserIdAndTypeOrderByMeasuredAtDesc(userId, type)
                .orElseThrow(() -> new ApiException("Vital sign not found"));

        return convertToResponseDTO(vitalSign);
    }

    public void addVitalSign(VitalSignRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ApiException("User not found"));

        if (dto.getType().equals("BLOOD_PRESSURE")) {

            if (dto.getSystolic() == null || dto.getDiastolic() == null) {
                throw new ApiException(
                        "Systolic and diastolic values are required for blood pressure"
                );
            }

        } else {

            if (dto.getValue() == null) {
                throw new ApiException("Value is required for this vital sign type");
            }
        }

        VitalSign vitalSign = new VitalSign();

        vitalSign.setUser(user);
        vitalSign.setType(dto.getType());
        vitalSign.setValue(dto.getValue());
        vitalSign.setSystolic(dto.getSystolic());
        vitalSign.setDiastolic(dto.getDiastolic());
        vitalSign.setUnit(dto.getUnit());
        vitalSign.setMeasuredAt(dto.getMeasuredAt());

        vitalSign.setFlag(calculateFlag(vitalSign));

        vitalSignRepository.save(vitalSign);
    }

    public void updateVitalSign(Integer id, VitalSignUpdateDTO dto) {

        VitalSign vitalSign = vitalSignRepository.findById(id)
                .orElseThrow(() -> new ApiException("Vital sign not found"));

        if (dto.getType().equals("BLOOD_PRESSURE")) {

            if (dto.getSystolic() == null || dto.getDiastolic() == null) {
                throw new ApiException(
                        "Systolic and diastolic values are required for blood pressure"
                );
            }

        } else {

            if (dto.getValue() == null) {
                throw new ApiException("Value is required for this vital sign type");
            }
        }

        vitalSign.setType(dto.getType());
        vitalSign.setValue(dto.getValue());
        vitalSign.setSystolic(dto.getSystolic());
        vitalSign.setDiastolic(dto.getDiastolic());
        vitalSign.setUnit(dto.getUnit());
        vitalSign.setMeasuredAt(dto.getMeasuredAt());

        vitalSign.setFlag(calculateFlag(vitalSign));

        vitalSignRepository.save(vitalSign);
    }

    public void deleteVitalSign(Integer id) {

        VitalSign vitalSign = vitalSignRepository.findById(id)
                .orElseThrow(() -> new ApiException("Vital sign not found"));

        vitalSignRepository.delete(vitalSign);
    }

    private String calculateFlag(VitalSign vitalSign) {

        if (vitalSign.getType().equals("BLOOD_PRESSURE")) {

            BigDecimal systolic = vitalSign.getSystolic();
            BigDecimal diastolic = vitalSign.getDiastolic();

            if (systolic.compareTo(BigDecimal.valueOf(180)) >= 0
                    || diastolic.compareTo(BigDecimal.valueOf(120)) >= 0) {
                return "CRITICAL";
            }

            if (systolic.compareTo(BigDecimal.valueOf(140)) >= 0
                    || diastolic.compareTo(BigDecimal.valueOf(90)) >= 0) {
                return "HIGH";
            }

            if (systolic.compareTo(BigDecimal.valueOf(90)) < 0
                    || diastolic.compareTo(BigDecimal.valueOf(60)) < 0) {
                return "LOW";
            }

            return "NORMAL";
        }

        BigDecimal value = vitalSign.getValue();

        if (value == null) {
            return "NORMAL";
        }

        switch (vitalSign.getType()) {

            case "GLUCOSE":

                if (value.compareTo(BigDecimal.valueOf(200)) >= 0) {
                    return "CRITICAL";
                }

                if (value.compareTo(BigDecimal.valueOf(140)) >= 0) {
                    return "HIGH";
                }

                if (value.compareTo(BigDecimal.valueOf(70)) < 0) {
                    return "LOW";
                }

                return "NORMAL";

            case "HEART_RATE":

                if (value.compareTo(BigDecimal.valueOf(120)) >= 0) {
                    return "HIGH";
                }

                if (value.compareTo(BigDecimal.valueOf(50)) < 0) {
                    return "LOW";
                }

                return "NORMAL";

            case "WEIGHT":
            case "WAIST":
                return "NORMAL";

            default:
                return "NORMAL";
        }
    }

    private VitalSignResponseDTO convertToResponseDTO(VitalSign vitalSign) {

        VitalSignResponseDTO responseDTO = new VitalSignResponseDTO();

        responseDTO.setId(vitalSign.getId());
        responseDTO.setUserId(vitalSign.getUser().getId());
        responseDTO.setType(vitalSign.getType());
        responseDTO.setValue(vitalSign.getValue());
        responseDTO.setSystolic(vitalSign.getSystolic());
        responseDTO.setDiastolic(vitalSign.getDiastolic());
        responseDTO.setUnit(vitalSign.getUnit());
        responseDTO.setFlag(vitalSign.getFlag());
        responseDTO.setMeasuredAt(vitalSign.getMeasuredAt());
        responseDTO.setCreatedAt(vitalSign.getCreatedAt());

        return responseDTO;
    }



}