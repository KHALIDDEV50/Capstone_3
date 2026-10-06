package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.NutritionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Integer> {

    Optional<NutritionPlan> findByUserId(Integer userId);
}