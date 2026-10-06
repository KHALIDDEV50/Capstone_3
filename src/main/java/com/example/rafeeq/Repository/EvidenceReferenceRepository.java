package com.example.rafeeq.Repository;

import com.example.rafeeq.Model.EvidenceReference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvidenceReferenceRepository extends JpaRepository<EvidenceReference, Integer> {

    boolean existsByTitleAndTopic(String title, String topic);

    List<EvidenceReference> findByTopicIn(List<String> topics);
}