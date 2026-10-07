package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VitalSignRepository extends JpaRepository<VitalSign, Integer> {

    List<VitalSign> findByUserIdOrderByMeasuredAtDesc(Integer userId);

    Optional<VitalSign> findFirstByUserIdAndTypeOrderByMeasuredAtDesc(
            Integer userId,
            String type
    );

    List<VitalSign> findTop2ByUserIdAndTypeOrderByMeasuredAtDesc(
            Integer userId,
            String type
    );

    List<VitalSign> findByUserIdAndTypeOrderByMeasuredAtAsc(
            Integer userId,
            String type
    );

   List<VitalSign> findAllByUserIdOrderByMeasuredAtDesc(Integer id);
}