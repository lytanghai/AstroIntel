package com.tanghai.announcement.service.internet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanghai.announcement.component.GistProperties;
import com.tanghai.announcement.constant.TelegramConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GistService {

    private static final Logger log = LoggerFactory.getLogger(GistService.class);
    private final GistProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // --- Cache ---
    private static final long CACHE_TTL_MS = 15 * 60 * 1000; // 15 minutes
    private Map<String, Object> cachedGist = null;
    private Instant cacheTime = Instant.EPOCH;

    public GistService(GistProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", properties.getGithubToken().trim());
        headers.setAccept(MediaType.parseMediaTypes("application/vnd.github.v3+json"));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "AstroApp");

        return headers;
    }


    public void clearCache() {
        cachedGist = null;
        cacheTime = Instant.EPOCH;
    }

    public boolean isCacheAvailable() {
        return cachedGist != null && Instant.now().minusMillis(CACHE_TTL_MS).isBefore(cacheTime);
    }


    public Map<String, Object> getGistContent() {
        if (cachedGist != null && Instant.now().minusMillis(CACHE_TTL_MS).isBefore(cacheTime)) {
            log.info("Gist cache is available");
            return cachedGist;
        }

        HttpEntity<Void> entity = new HttpEntity<>(getHeaders());

        log.info("Gist ID {}", properties.getGistId().substring(0, 10));

        ResponseEntity<Map> response = restTemplate.exchange(
                TelegramConst.GIST_BASE_URL + properties.getGistId(),
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map files = (Map) response.getBody().get(TelegramConst.FILES);
        Map file = (Map) files.get(TelegramConst.GIST_FILE);
        String content = (String) file.get(TelegramConst.CONTENT);

        try {
            Map<String, Object> json = objectMapper.readValue(content, Map.class);
            log.info("Gist cache is not available!");
            cachedGist = json;
            cacheTime = Instant.now();

            return json;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse gist JSON", e);
        }
    }

    public void updateGistContent(Map<String, Object> updatedContent) {
        try {
            String jsonString = objectMapper.writeValueAsString(updatedContent);
            Map<String, Object> body = Map.of(
                    TelegramConst.FILES, Map.of(TelegramConst.GIST_FILE, Map.of(TelegramConst.CONTENT, jsonString))
            );
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
            restTemplate.exchange(TelegramConst.GIST_BASE_URL + properties.getGistId(), HttpMethod.PATCH, entity, Map.class);

            // Clear cache after update
            cachedGist = null;
            cacheTime = Instant.EPOCH;

        } catch (Exception e) {
            throw new RuntimeException("Failed to update gist JSON", e);
        }
    }

    public void subscribeToGist(String chatId) {
        Map<String, Object> json = getGistContent();

        Map<String, Object> telegram = (Map<String, Object>) json.get(TelegramConst.TELEGRAM);
        if (telegram == null) telegram = Map.of(TelegramConst.CHAT_ID, new ArrayList<String>());

        List<String> chatIds = (List<String>) telegram.get(TelegramConst.CHAT_ID);
        if (chatIds == null) chatIds = new ArrayList<>();
        if (!chatIds.contains(chatId)) chatIds.add(chatId);

        telegram.put(TelegramConst.CHAT_ID, chatIds);
        json.put(TelegramConst.TELEGRAM, telegram);

        updateGistContent(json);
    }

    public void unSubscribeToGist(String chatId) {
        Map<String, Object> json = getGistContent();

        Map<String, Object> telegram = (Map<String, Object>) json.get(TelegramConst.TELEGRAM);
        if (telegram == null) return; // nothing to remove

        List<String> chatIds = (List<String>) telegram.get(TelegramConst.CHAT_ID);
        if (chatIds == null || !chatIds.contains(chatId)) return;

        chatIds.remove(chatId);
        telegram.put(TelegramConst.CHAT_ID, chatIds);
        json.put(TelegramConst.TELEGRAM, telegram);

        updateGistContent(json);
    }

}