package com.tanghai.announcement.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;

@Component
public class TelegramWebhookInitializer {

    private static final Logger log = LoggerFactory.getLogger(TelegramWebhookInitializer.class);
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
            String fullWebhookUrl = baseUrl + "/telegram/webhook/callback/telegram/webhook";
            SetWebhook setWebhook = SetWebhook.builder().url(fullWebhookUrl).build();
            telegramComponent.setWebhook(setWebhook);
            log.info("Successfully set webhook url: {} ", setWebhook.getUrl());
            log.info("Webhook URL: {}", fullWebhookUrl);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
