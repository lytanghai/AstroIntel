package com.tanghai.announcement.component;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KeepAlivePing {

    private final RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedRate = 10 * 60 * 1000) // every 10 min
    public void pingSelf() {
        try {
            restTemplate.getForObject("https://astrointel.onrender.com/telegram/webhook", String.class);
            System.out.println("Pinged self to prevent sleep");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
