package com.tanghai.announcement.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiApiService {

    @Value("${google.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Generate text content from Gemini using a prompt
     */
    public String generateText(String chatId, String prompt) throws Exception {
        if (!"678134373".equals(chatId)) {
            return "❌ You have no privilege to use this command!";
        }
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

        // JSON request body
        String requestBody = "{\n" +
                "  \"contents\": [\n" +
                "    {\n" +
                "      \"role\": \"user\",\n" +
                "      \"parts\": [\n" +
                "        { \"text\": \"" + escapeJson(prompt) + "\" }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);  // Use x-goog-api-key header

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JsonNode root = objectMapper.readTree(response.getBody());

        // Extract generated text
        return root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText();
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }
}
