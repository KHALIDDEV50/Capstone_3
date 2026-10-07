package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.MedicationScheduleDTO;
import com.example.rafeeq.DTO.MedicationScheduleResponseDTO;
import com.example.rafeeq.DTO.NextMedicationDoseDTO;
import com.example.rafeeq.Model.MedicationSchedule;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.MedicationScheduleRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

    private final MedicationScheduleRepository medicationScheduleRepository;
    private final UserRepository userRepository;


    // Get All Medication Schedule
    public List<MedicationScheduleResponseDTO> getAllMedicationSchedule() {

        return medicationScheduleRepository.findAll().stream().map(this::convertToResponseDTO).toList();
    }


    // Get Medication Schedule By ID
    public MedicationScheduleResponseDTO getMedicationScheduleById(Integer id) {

        MedicationSchedule medicationSchedule =
                medicationScheduleRepository.findMedicationScheduleById(id);

        if (medicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        return convertToResponseDTO(medicationSchedule);
    }


    // Get Medication Schedule By User ID
    public List<MedicationScheduleResponseDTO> getMedicationScheduleByUserId(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return medicationScheduleRepository.findByUser_Id(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // Get Active Medication Schedule By User ID
    public List<MedicationScheduleResponseDTO> getActiveMedicationScheduleByUserId(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return medicationScheduleRepository.findByUser_IdAndIsActiveTrue(userId)
                .stream()
                .map(this::convertToResponseDTO)
                .toList();
    }


    // Add Medication Schedule
    public void addMedicationSchedule(MedicationScheduleDTO medicationScheduleDTO) {

        User user = userRepository.findUserById(medicationScheduleDTO.getUserId());

        if (user == null) {
            throw new ApiException("User not found");
        }

        MedicationSchedule medicationSchedule = new MedicationSchedule();

        medicationSchedule.setUser(user);
        medicationSchedule.setMedicationName(medicationScheduleDTO.getMedicationName());
        medicationSchedule.setDosage(medicationScheduleDTO.getDosage());
        medicationSchedule.setMealRelation(medicationScheduleDTO.getMealRelation());
        medicationSchedule.setTimes(medicationScheduleDTO.getTimes());
        medicationSchedule.setStartDate(medicationScheduleDTO.getStartDate());
        medicationSchedule.setEndDate(medicationScheduleDTO.getEndDate());
        medicationSchedule.setIsActive(medicationScheduleDTO.getIsActive() != null ? medicationScheduleDTO.getIsActive() : true);

        medicationScheduleRepository.save(medicationSchedule);
    }


    // Update Medication Schedule
    public void updateMedicationSchedule(Integer id, MedicationScheduleDTO medicationScheduleDTO) {

        MedicationSchedule oldMedicationSchedule =
                medicationScheduleRepository.findMedicationScheduleById(id);

        if (oldMedicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        User user = userRepository.findUserById(medicationScheduleDTO.getUserId());

        if (user == null) {
            throw new ApiException("User not found");
        }

        oldMedicationSchedule.setUser(user);
        oldMedicationSchedule.setMedicationName(medicationScheduleDTO.getMedicationName());
        oldMedicationSchedule.setDosage(medicationScheduleDTO.getDosage());
        oldMedicationSchedule.setMealRelation(medicationScheduleDTO.getMealRelation());
        oldMedicationSchedule.setTimes(medicationScheduleDTO.getTimes());
        oldMedicationSchedule.setStartDate(medicationScheduleDTO.getStartDate());
        oldMedicationSchedule.setEndDate(medicationScheduleDTO.getEndDate());
        oldMedicationSchedule.setIsActive(medicationScheduleDTO.getIsActive() != null ? medicationScheduleDTO.getIsActive() : true);

        medicationScheduleRepository.save(oldMedicationSchedule);
    }


    // Delete Medication Schedule
    public void deleteMedicationSchedule(Integer id) {

        MedicationSchedule medicationSchedule =
                medicationScheduleRepository.findMedicationScheduleById(id);

        if (medicationSchedule == null) {
            throw new ApiException("Medication Schedule not found");
        }

        medicationScheduleRepository.delete(medicationSchedule);
    }


    // Convert Model To Response DTO
    private MedicationScheduleResponseDTO convertToResponseDTO(MedicationSchedule medicationSchedule) {

        MedicationScheduleResponseDTO responseDTO =
                new MedicationScheduleResponseDTO();

        responseDTO.setId(medicationSchedule.getId());
        responseDTO.setUserId(medicationSchedule.getUser().getId());
        responseDTO.setMedicationName(medicationSchedule.getMedicationName());
        responseDTO.setDosage(medicationSchedule.getDosage());
        responseDTO.setMealRelation(medicationSchedule.getMealRelation());
        responseDTO.setTimes(medicationSchedule.getTimes());
        responseDTO.setStartDate(medicationSchedule.getStartDate());
        responseDTO.setEndDate(medicationSchedule.getEndDate());
        responseDTO.setIsActive(medicationSchedule.getIsActive());
        responseDTO.setCreatedAt(medicationSchedule.getCreatedAt());
        responseDTO.setUpdatedAt(medicationSchedule.getUpdatedAt());

        return responseDTO;
    }

    public NextMedicationDoseDTO getNextMedicationDose(Integer userId) {

        User user = userRepository.findUserById(userId);

        if (user == null) {
            throw new ApiException("User not found");
        }

        List<MedicationSchedule> medications =
                medicationScheduleRepository.findByUser_IdAndIsActiveTrue(userId);

        if (medications.isEmpty()) {
            throw new ApiException("No active medications found");
        }

        LocalTime now = LocalTime.now();

        MedicationSchedule nextMedication = null;
        LocalTime nextTime = null;

        for (MedicationSchedule medication : medications) {

            if (medication.getTimes() == null || medication.getTimes().isBlank()) {
                continue;
            }

            String[] times = medication.getTimes().split(",");

            for (String time : times) {

                LocalTime medicationTime =
                        LocalTime.parse(time.trim());

                if (medicationTime.isAfter(now)) {

                    if (nextTime == null || medicationTime.isBefore(nextTime)) {

                        nextTime = medicationTime;
                        nextMedication = medication;
                    }
                }
            }
        }


        // If all today's doses are finished,
        // get the earliest dose for tomorrow
        if (nextMedication == null) {

            for (MedicationSchedule medication : medications) {

                if (medication.getTimes() == null || medication.getTimes().isBlank()) {
                    continue;
                }

                String[] times = medication.getTimes().split(",");

                for (String time : times) {

                    LocalTime medicationTime =
                            LocalTime.parse(time.trim());

                    if (nextTime == null || medicationTime.isBefore(nextTime)) {

                        nextTime = medicationTime;
                        nextMedication = medication;
                    }
                }
            }
        }


        if (nextMedication == null) {
            throw new ApiException("No medication times found");
        }

        return new NextMedicationDoseDTO(
                nextMedication.getMedicationName(),
                nextMedication.getDosage(),
                nextTime.toString(),
                nextMedication.getMealRelation()
        );
    }
}