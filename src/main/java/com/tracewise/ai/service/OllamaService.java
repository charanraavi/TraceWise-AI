
package com.tracewise.ai.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Service
public class OllamaService {

    private final RestClient restClient;

    public OllamaService() {
        this.restClient = RestClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    public String generateResponse(String prompt) {
        Map<String, Object> request = Map.of(
                "model", "llama3.2:3b",
                "prompt", prompt,
                "stream", false
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(Map.class);

            if (response == null || response.get("response") == null) {
                throw new IllegalStateException(
                        "Ollama returned an empty response");
            }

            return response.get("response").toString();

        } catch (Exception e) {
            throw new IllegalStateException(
                    "AI analysis failed. Please ensure Ollama is running.",
                    e
            );
        }
    }
}