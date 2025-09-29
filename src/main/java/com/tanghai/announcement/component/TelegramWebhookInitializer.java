package com.tanghai.announcement.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class TelegramWebhookInitializer {

    private final TelegramComponent telegramComponent;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;

    public TelegramWebhookInitializer(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    @PostConstruct
    public void initWebhook() {
        try {
            String fullWebhookUrl = baseUrl + webhookPath;
            SetWebhook setWebhook = SetWebhook.builder().url(fullWebhookUrl).build();
            telegramComponent.setWebhook(setWebhook);
            System.out.println("✅ Webhook set to: " + fullWebhookUrl);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
