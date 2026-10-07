package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Integer> {

    boolean existsByUserId(Integer userId);
HealthProfile findHealthProfileByUserId(Integer id);
    Optional<HealthProfile> findByUserId(Integer userId);
}