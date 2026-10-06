package com.example.rafeeq.Service;

import com.example.rafeeq.DTO.EvidenceReferenceDTO;
import com.example.rafeeq.Model.EvidenceReference;
import com.example.rafeeq.Repository.EvidenceReferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@AllArgsConstructor
public class EvidenceService {

    private final EvidenceReferenceRepository evidenceReferenceRepository;

    public List<EvidenceReferenceDTO> getRelevantEvidence(
            Map<String, Object> patientContext
    ) {

        List<String> topics = new ArrayList<>();

        List<?> conditions =
                (List<?>) patientContext.get("conditions");

        if (conditions != null) {

            for (Object condition : conditions) {

                if (condition == null) {
                    continue;
                }

                String value = condition.toString()
                        .toLowerCase(Locale.ROOT);

                if (value.contains("diabetes") ||
                        value.contains("prediabetes") ||
                        value.contains("glucose")) {

                    topics.add("DIABETES_NUTRITION");
                    topics.add("DIABETES_EXERCISE");
                }

                if (value.contains("hypertension") ||
                        value.contains("blood pressure")) {

                    topics.add("HYPERTENSION");
                    topics.add("HYPERTENSION_NUTRITION");
                }
            }
        }

        if (patientContext.get("latestBloodPressure") != null) {
            Map<?, ?> bp =
                    (Map<?, ?>) patientContext.get("latestBloodPressure");

            if ("HIGH".equals(bp.get("flag")) ||
                    "CRITICAL".equals(bp.get("flag"))) {

                topics.add("HYPERTENSION");
                topics.add("HYPERTENSION_NUTRITION");
            }
        }

        if (patientContext.get("latestGlucose") != null) {
            Map<?, ?> glucose =
                    (Map<?, ?>) patientContext.get("latestGlucose");

            if ("HIGH".equals(glucose.get("flag"))) {

                topics.add("DIABETES_NUTRITION");
                topics.add("DIABETES_EXERCISE");
            }
        }

        topics.add("GENERAL_NUTRITION");
        topics.add("GENERAL_EXERCISE");

        topics = topics.stream()
                .distinct()
                .toList();

        List<EvidenceReference> references =
                evidenceReferenceRepository.findByTopicIn(topics);

        return references.stream()
                .map(this::convertToDTO)
                .toList();
    }

    private EvidenceReferenceDTO convertToDTO(
            EvidenceReference evidence
    ) {

        return new EvidenceReferenceDTO(
                evidence.getId(),
                evidence.getTitle(),
                evidence.getOrganization(),
                evidence.getTopic(),
                evidence.getRecommendation(),
                evidence.getAuthors(),
                evidence.getDoi(),
                evidence.getSourceUrl()
        );
    }
}