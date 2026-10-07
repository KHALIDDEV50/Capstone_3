package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.MedicationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicationScheduleRepository
        extends JpaRepository<MedicationSchedule, Integer> {

    // Get Medication Schedules By User ID
    List<MedicationSchedule> findByUser_Id(Integer userId);

    // Get Active Medication Schedules By User ID
    List<MedicationSchedule> findByUser_IdAndIsActiveTrue(Integer userId);
    List<MedicationSchedule> findAllByUserId(Integer userId);
    MedicationSchedule findMedicationScheduleById(Integer id);
}