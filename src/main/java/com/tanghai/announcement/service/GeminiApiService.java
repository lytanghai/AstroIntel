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
            return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
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

        String responseStr = root.path("candidates")
                .get(0).path("content").path("parts")
                .get(0).path("text").asText();

        if(responseStr.length() >= 4096) {
            responseStr = responseStr.substring(0, 4095);
        }
        // Extract generated text
        return responseStr;
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"");
    }
}
