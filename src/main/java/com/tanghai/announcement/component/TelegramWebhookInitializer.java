package com.tanghai.announcement.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Component
public class TelegramWebhookInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(TelegramWebhookInitializer.class);


    private final TelegramComponent telegramComponent;

    @Value("${telegram.bot.webhook-path:/telegram/webhook}")
    private String webhookPath;

    @Value("${app.base-url:https://astrointel.onrender.com}")
    private String baseUrl; // Your public Render URL

    public TelegramWebhookInitializer(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Setting Webhook");
        String fullWebhookUrl = baseUrl + webhookPath;
        SetWebhook setWebhook = new SetWebhook(fullWebhookUrl);
        telegramComponent.setWebhook(setWebhook);
        logger.info("Webhook set to: {} ", fullWebhookUrl);
    }
}
