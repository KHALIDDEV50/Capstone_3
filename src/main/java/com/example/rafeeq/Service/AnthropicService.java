package com.example.rafeeq.Service;

import com.example.rafeeq.Api.ApiException;
import com.fasterxml.jackson.databind.JsonNode;
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

    @Value("${anthropic.api.model}")
    private String model;

    public AnthropicService(RestClient anthropicRestClient) {
        this.anthropicRestClient = anthropicRestClient;
    }


    public String generate(String prompt) {

        try {

            Map<String, Object> request = new HashMap<>();

            request.put("model", model);

            request.put("max_tokens", 2000);

            request.put(
                    "system",
                    """
                    You are an AI health planning assistant.

                    Follow these rules:

                    1. Do not diagnose diseases.
                    2. Do not prescribe medications.
                    3. Do not stop or change medications.
                    4. Do not invent health information.
                    5. Use the health information provided by the backend.
                    6. Consider the user's health conditions.
                    7. Consider the user's vital signs.
                    8. Consider the user's activity level.
                    9. Exercise recommendations must respect exercise risk.
                    10. Recommendations must be practical and safe.

                    RESPONSE RULES:

                    - Write all response values in Arabic.
                    - Keep JSON field names in English.
                    - Return ONLY valid JSON.
                    - Do not use markdown.
                    - Do not use ```json.
                    - Do not write anything before the JSON.
                    - Do not write anything after the JSON.
                    - Follow exactly the JSON structure requested
                      in the user prompt.
                    """
            );

            request.put(
                    "messages",
                    List.of(
                            Map.of(
                                    "role", "user",
                                    "content", prompt
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


            String responseText = extractText(response);

            return cleanJson(responseText);


        } catch (ApiException e) {

            throw e;

        } catch (Exception e) {

            e.printStackTrace();

            throw new ApiException(
                    "Failed to connect to Claude: "
                            + e.getMessage()
            );
        }
    }


    private String extractText(JsonNode response) {

        JsonNode content = response.get("content");

        if (content == null || !content.isArray()) {

            throw new ApiException(
                    "Invalid response from Claude"
            );
        }


        for (JsonNode block : content) {

            if ("text".equals(
                    block.path("type").asText()
            )) {

                String text =
                        block.path("text").asText();

                if (text == null || text.isBlank()) {

                    throw new ApiException(
                            "Claude returned empty response"
                    );
                }

                return text;
            }
        }


        throw new ApiException(
                "Claude did not return text"
        );
    }


    private String cleanJson(String responseText) {

        String clean = responseText.trim();


        if (clean.startsWith("```json")) {

            clean = clean.substring(7);

        } else if (clean.startsWith("```")) {

            clean = clean.substring(3);
        }


        if (clean.endsWith("```")) {

            clean = clean.substring(
                    0,
                    clean.length() - 3
            );
        }


        return clean.trim();
    }
}