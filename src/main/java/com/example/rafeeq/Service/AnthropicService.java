package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.example.rafeeq.DTO.AICompleteResponseDTO;
import com.example.rafeeq.DTO.EvidenceReferenceDTO;
import com.example.rafeeq.Model.ExercisePlan;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnthropicService {

    private final RestClient anthropicRestClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.model}")
    private String model;

    // ==================== Existing AI Plan ====================

    public AICompleteResponseDTO generatePlan(
            Map<String, Object> patientContext,
            List<EvidenceReferenceDTO> evidence) {

        try {

            String patientJson =
                    objectMapper.writeValueAsString(
                            patientContext
                    );

            String evidenceJson =
                    objectMapper.writeValueAsString(
                            evidence
                    );

            String prompt = """
                    Generate a safe personalized nutrition and exercise plan.

                    Patient information:
                    %s

                    Evidence references:
                    %s

                    Return ONLY valid JSON in this structure:

                    {
                      "nutrition": {
                        "summary": "string",
                        "reason": "string",
                        "items": [
                          {
                            "meal": "string",
                            "description": "string",
                            "frequency": "string"
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

                    SAFETY:
                    - Do not diagnose.
                    - Do not prescribe medication.
                    - Do not invent evidence.
                    - Respect the patient's exercise risk.
                    - Avoid dangerous exercise recommendations.
                    - Use only the supplied evidence.
                    """.formatted(
                    patientJson,
                    evidenceJson
            );

            Map<String, Object> request =
                    new HashMap<>();

            request.put(
                    "model",
                    model
            );

            request.put(
                    "max_tokens",
                    3000
            );

            request.put(
                    "system",
                    "You are a safe AI health assistant for BasirhAI. Return only valid JSON."
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
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
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

    // ----------- last extra endpoint ----------

    public Map<String, Object> generateMealSwap(
            Map<String, Object> patientContext,
            List<EvidenceReferenceDTO> evidence,
            String meal,
            String reason) {

        try {

            String patientJson =
                    objectMapper.writeValueAsString(
                            patientContext
                    );

            String evidenceJson =
                    objectMapper.writeValueAsString(
                            evidence
                    );

            String prompt = """
                    Suggest a healthier alternative to the following meal.

                    <patient>
                    %s
                    </patient>

                    <evidence>
                    %s
                    </evidence>

                    Current meal:
                    %s

                    User's reason:
                    %s

                    Return ONLY valid JSON:

                    {
                      "originalMeal": "string",
                      "replacementMeal": "string",
                      "reason": "string",
                      "benefits": ["string"],
                      "notes": "string"
                    }

                    SAFETY:
                    - Do not diagnose.
                    - Do not prescribe medication.
                    - Do not invent medical evidence.
                    - Use only the supplied patient information and evidence.
                    - Do not claim guaranteed health benefits.
                    """.formatted(
                    patientJson,
                    evidenceJson,
                    meal,
                    reason
            );

            Map<String, Object> request =
                    new HashMap<>();

            request.put(
                    "model",
                    model
            );

            request.put(
                    "max_tokens",
                    1500
            );

            request.put(
                    "system",
                    "You are a safe AI nutrition assistant for BasirhAI. Return only valid JSON."
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
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
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
                    Map.class
            );

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to generate meal replacement"
            );
        }
    }

    // ----------- last extra endpoint ----------

    public Map<String, Object> generateExerciseAdaptation(
            Map<String, Object> patientContext,
            List<EvidenceReferenceDTO> evidence,
            ExercisePlan exercisePlan,
            String difficulty,
            String availableMinutes,
            String equipment) {

        try {

            String patientJson =
                    objectMapper.writeValueAsString(
                            patientContext
                    );

            String evidenceJson =
                    objectMapper.writeValueAsString(
                            evidence
                    );

            String prompt = """
                    Adapt the patient's existing exercise plan.

                    <patient>
                    %s
                    </patient>

                    <evidence>
                    %s
                    </evidence>

                    <currentExercisePlan>
                    Goal:
                    %s

                    Summary:
                    %s

                    Exercises:
                    %s
                    </currentExercisePlan>

                    User feedback:
                    Difficulty: %s
                    Available minutes: %s
                    Equipment: %s

                    Return ONLY valid JSON:

                    {
                      "goal": "string",
                      "summary": "string",
                      "changes": ["string"],
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
                      ],
                      "reason": "string"
                    }

                    SAFETY:
                    - Do not diagnose.
                    - Do not prescribe medication.
                    - Do not invent medical evidence.
                    - Respect the patient's exercise risk and vital signs.
                    - Do not recommend dangerous high-intensity exercise.
                    - Prefer gradual and realistic progression.
                    """.formatted(
                    patientJson,
                    evidenceJson,
                    exercisePlan.getGoal(),
                    exercisePlan.getSummary(),
                    exercisePlan.getExercises(),
                    difficulty,
                    availableMinutes,
                    equipment
            );

            Map<String, Object> request =
                    new HashMap<>();

            request.put(
                    "model",
                    model
            );

            request.put(
                    "max_tokens",
                    2500
            );

            request.put(
                    "system",
                    "You are a safe AI exercise adaptation assistant for BasirhAI. Return only valid JSON."
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
                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )
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
                    Map.class
            );

        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            throw new ApiException(
                    "Failed to adapt exercise plan"
            );
        }
    }

    private String extractText(
            JsonNode response) {

        JsonNode content =
                response.get("content");

        if (content == null ||
                !content.isArray() ||
                content.isEmpty()) {

            throw new ApiException(
                    "Invalid response from Claude"
            );
        }

        JsonNode text =
                content.get(0).get("text");

        if (text == null) {
            throw new ApiException(
                    "Claude response does not contain text"
            );
        }

        return text.asText();
    }

    private String cleanJson(
            String responseText) {

        String cleaned =
                responseText.trim();

        if (cleaned.startsWith("```json")) {
            cleaned =
                    cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned =
                    cleaned.substring(3);
        }

        if (cleaned.endsWith("```")) {
            cleaned =
                    cleaned.substring(
                            0,
                            cleaned.length() - 3
                    );
        }

        return cleaned.trim();
    }
}