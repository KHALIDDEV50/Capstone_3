package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.HealthAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthAssessmentRepository
        extends JpaRepository<HealthAssessment, Integer> {

    // Get Health Assessments By User ID
    List<HealthAssessment> findByUser_Id(Integer userId);

    // Get Current Health Assessment By User ID
    List<HealthAssessment> findByUser_IdAndIsCurrentTrue(Integer userId);
}