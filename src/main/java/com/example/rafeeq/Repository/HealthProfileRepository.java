package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.HealthProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, Integer> {

    boolean existsByUserId(Integer userId);

    Optional<HealthProfile> findByUserId(Integer userId);
}