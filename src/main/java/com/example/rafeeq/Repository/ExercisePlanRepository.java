package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.ExercisePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExercisePlanRepository extends JpaRepository<ExercisePlan, Integer> {

    Optional<ExercisePlan> findByUserId(Integer userId);

    ExercisePlan findExercisePlanByUserId(Integer id);

    ExercisePlan findExercisePlanById(Integer id);
}