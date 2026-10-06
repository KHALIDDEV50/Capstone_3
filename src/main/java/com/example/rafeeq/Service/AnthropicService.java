package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AICompleteResponseDTO;
import com.example.rafeeq.DTO.EvidenceReferenceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnthropicService {

    private final RestClient anthropicRestClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.model}")
    private String model;

    public AnthropicService(
            RestClient anthropicRestClient,
            ObjectMapper objectMapper
    ) {
        this.anthropicRestClient = anthropicRestClient;
        this.objectMapper = objectMapper;
    }

    public AICompleteResponseDTO generatePlan(
            Map<String, Object> patientContext,
            List<EvidenceReferenceDTO> evidence
    ) {

        try {

            String patientJson =
                    objectMapper.writeValueAsString(patientContext);

            String evidenceJson =
                    objectMapper.writeValueAsString(evidence);

            String prompt =
                    buildPrompt(patientJson, evidenceJson);

            Map<String, Object> request =
                    new HashMap<>();

            request.put("model", model);

            request.put(
                    "max_tokens",
                    4000
            );

            request.put(
                    "system",
                    buildSystemPrompt()
            );

            request.put(
                    "messages",
                    List.of(
                            Map.of(
                                    "role",
                                    "user",
                                    "content",
                                    prompt
                            )
                    )
            );

            JsonNode response =
                    anthropicRestClient
                            .post()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(JsonNode.class);

            if (response == null) {
                throw new ApiException(
                        "Empty response from Claude"
                );
            }

            String responseText =
                    extractText(response);

            String cleanJson =
                    cleanJson(responseText);

            return objectMapper.readValue(
                    cleanJson,
                    AICompleteResponseDTO.class
            );

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to generate AI health plan"
            );
        }
    }

    private String buildSystemPrompt() {

        return """
                You are the AI planning engine for BasirhAI.

                Your role is to generate personalized nutrition and
                exercise suggestions from the patient information and
                evidence supplied by the backend.

                IMPORTANT SAFETY RULES:

                1. You are not a doctor and must not diagnose disease.

                2. Do not prescribe, stop, or change medications.

                3. Do not invent medical research, studies, citations,
                   organizations, DOIs, URLs, or clinical guidelines.

                4. Use only the evidence supplied in the prompt.

                5. Do not claim that a recommendation is supported by
                   evidence unless the supplied evidence supports it.

                6. Nutrition recommendations must be individualized.

                7. Do not automatically prescribe a high-protein diet.

                8. Do not automatically eliminate carbohydrates.

                9. When glucose is elevated, favor appropriate portions,
                   high-fiber carbohydrate sources, minimally processed
                   foods, and minimizing sugar-sweetened beverages and
                   refined carbohydrates when supported by the supplied
                   evidence.

                10. When blood pressure is elevated, consider heart-healthy
                    eating patterns such as DASH and sodium reduction when
                    supported by the supplied evidence.

                11. Exercise must respect the patient's exercise risk,
                    age, activity level, conditions, and vital signs.

                12. Prefer gradual, realistic exercise progression.

                13. Include home and outdoor options when appropriate.

                14. Do not provide dangerous high-intensity exercise to a
                    patient who is not appropriate for it.

                15. Do not provide a false sense of medical certainty.

                OUTPUT RULE:

                Return ONLY valid JSON.

                Do not use markdown.
                Do not use ```json.
                Do not add explanations before or after the JSON.

                Required JSON structure:

                {
                  "nutrition": {
                    "summary": "string",
                    "reason": "string",
                    "items": [
                      {
                        "meal": "string",
                        "foods": ["string"],
                        "notes": "string"
                      }
                    ]
                  },
                  "exercise": {
                    "goal": "string",
                    "summary": "string",
                    "exercises": [
                      {
                        "name": "string",
                        "location": "HOME or OUTDOOR",
                        "durationMinutes": 0,
                        "frequencyPerWeek": 0,
                        "intensity": "LIGHT or MODERATE",
                        "sets": 0,
                        "repetitions": 0,
                        "instructions": "string"
                      }
                    ]
                  }
                }

                The exercise fields sets and repetitions may be 0
                when they are not applicable.

                Do not include evidence citations in the generated JSON.
                Evidence is managed by the backend.
                """;
    }

    private String buildPrompt(
            String patientJson,
            String evidenceJson
    ) {

        return """
                Create a personalized nutrition plan and exercise plan
                for the following patient.

                <patient>
                %s
                </patient>

                <evidence>
                %s
                </evidence>

                Apply the evidence to the patient's actual profile
                and latest available vital signs.

                Consider:
                - age
                - gender
                - height
                - activity level
                - health conditions
                - exercise risk
                - latest blood pressure
                - latest glucose
                - latest weight
                - latest waist
                - latest heart rate

                If a measurement is missing, do not invent it.

                The plan should be practical and understandable.
                """.formatted(
                patientJson,
                evidenceJson
        );
    }

    private String extractText(JsonNode response) {

        JsonNode content =
                response.get("content");

        if (content == null ||
                !content.isArray()) {

            throw new ApiException(
                    "Invalid Claude response"
            );
        }

        for (JsonNode block : content) {

            if ("text".equals(
                    block.path("type").asText()
            )) {

                return block
                        .path("text")
                        .asText();
            }
        }

        throw new ApiException(
                "Claude did not return text"
        );
    }

    private String cleanJson(String responseText) {

        String clean =
                responseText.trim();

        if (clean.startsWith("```json")) {

            clean =
                    clean.substring(7);

        } else if (clean.startsWith("```")) {

            clean =
                    clean.substring(3);
        }

        if (clean.endsWith("```")) {

            clean =
                    clean.substring(
                            0,
                            clean.length() - 3
                    );
        }

        return clean.trim();
    }
}