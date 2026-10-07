package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.HealthAssessmentDTO;
import com.example.rafeeq.Model.HealthAssessment;
import com.example.rafeeq.Model.User;
import com.example.rafeeq.Repository.HealthAssessmentRepository;
import com.example.rafeeq.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthAssessmentService {

    private final HealthAssessmentRepository healthAssessmentRepository;
    private final UserRepository userRepository;


    // Get All Health Assessments
    public List<HealthAssessment> getAllHealthAssessments() {

        return healthAssessmentRepository.findAll();
    }


    // Get Health Assessment By ID
    public HealthAssessment getHealthAssessmentById(Integer id) {

        HealthAssessment healthAssessment = healthAssessmentRepository.findById(id).orElse(null);

        if (healthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        return healthAssessment;
    }


    // Get Health Assessments By User ID
    public List<HealthAssessment> getHealthAssessmentsByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return healthAssessmentRepository.findByUser_Id(userId);
    }


    // Get Current Health Assessment By User ID
    public List<HealthAssessment> getCurrentHealthAssessmentsByUserId(Integer userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }

        return healthAssessmentRepository.findByUser_IdAndIsCurrentTrue(userId);
    }


    // Add Health Assessment
    public void addHealthAssessment(HealthAssessmentDTO healthAssessmentDTO) {

        // Find User
        User user = userRepository
                .findById(healthAssessmentDTO.getUserId())
                .orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }


        // Create Health Assessment
        HealthAssessment healthAssessment = new HealthAssessment();

        healthAssessment.setUser(user);


        // Set Previous Assessment
        if (healthAssessmentDTO.getPreviousAssessmentId() != null) {

            HealthAssessment previousAssessment =
                    healthAssessmentRepository
                            .findById(healthAssessmentDTO.getPreviousAssessmentId())
                            .orElse(null);

            if (previousAssessment == null) {
                throw new ApiException("Previous Health Assessment not found");
            }

            healthAssessment.setPreviousAssessment(previousAssessment);
        }


        healthAssessment.setAttachments(
                healthAssessmentDTO.getAttachments()
        );

        healthAssessment.setUserNotes(
                healthAssessmentDTO.getUserNotes()
        );

        healthAssessment.setProfileSnapshot(
                healthAssessmentDTO.getProfileSnapshot()
        );

        healthAssessment.setExtractedValues(
                healthAssessmentDTO.getExtractedValues()
        );

        healthAssessment.setAiConclusion(
                healthAssessmentDTO.getAiConclusion()
        );

        healthAssessment.setTrend(
                healthAssessmentDTO.getTrend()
        );

        healthAssessment.setIsCurrent(
                healthAssessmentDTO.getIsCurrent() != null
                        ? healthAssessmentDTO.getIsCurrent()
                        : false
        );

        healthAssessment.setAssessmentDate(
                healthAssessmentDTO.getAssessmentDate()
        );

        healthAssessment.setNextDueDate(
                healthAssessmentDTO.getNextDueDate()
        );


        healthAssessmentRepository.save(healthAssessment);
    }


    // Update Health Assessment
    public void updateHealthAssessment(
            Integer id,
            HealthAssessmentDTO healthAssessmentDTO) {

        HealthAssessment oldHealthAssessment =
                healthAssessmentRepository.findById(id).orElse(null);

        if (oldHealthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }


        // Find User
        User user = userRepository
                .findById(healthAssessmentDTO.getUserId())
                .orElse(null);

        if (user == null) {
            throw new ApiException("User not found");
        }


        oldHealthAssessment.setUser(user);


        // Set Previous Assessment
        if (healthAssessmentDTO.getPreviousAssessmentId() != null) {

            HealthAssessment previousAssessment =
                    healthAssessmentRepository
                            .findById(healthAssessmentDTO.getPreviousAssessmentId())
                            .orElse(null);

            if (previousAssessment == null) {
                throw new ApiException("Previous Health Assessment not found");
            }

            oldHealthAssessment.setPreviousAssessment(previousAssessment);

        } else {

            oldHealthAssessment.setPreviousAssessment(null);
        }


        oldHealthAssessment.setAttachments(
                healthAssessmentDTO.getAttachments()
        );

        oldHealthAssessment.setUserNotes(
                healthAssessmentDTO.getUserNotes()
        );

        oldHealthAssessment.setProfileSnapshot(
                healthAssessmentDTO.getProfileSnapshot()
        );

        oldHealthAssessment.setExtractedValues(
                healthAssessmentDTO.getExtractedValues()
        );

        oldHealthAssessment.setAiConclusion(
                healthAssessmentDTO.getAiConclusion()
        );

        oldHealthAssessment.setTrend(
                healthAssessmentDTO.getTrend()
        );

        oldHealthAssessment.setIsCurrent(
                healthAssessmentDTO.getIsCurrent() != null
                        ? healthAssessmentDTO.getIsCurrent()
                        : false
        );

        oldHealthAssessment.setAssessmentDate(
                healthAssessmentDTO.getAssessmentDate()
        );

        oldHealthAssessment.setNextDueDate(
                healthAssessmentDTO.getNextDueDate()
        );


        healthAssessmentRepository.save(oldHealthAssessment);
    }


    // Delete Health Assessment
    public void deleteHealthAssessment(Integer id) {

        HealthAssessment healthAssessment =
                healthAssessmentRepository.findById(id).orElse(null);

        if (healthAssessment == null) {
            throw new ApiException("Health Assessment not found");
        }

        healthAssessmentRepository.delete(healthAssessment);
    }
}