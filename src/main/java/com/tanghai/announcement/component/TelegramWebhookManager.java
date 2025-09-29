package com.tanghai.announcement.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramWebhookManager {

    private final TelegramComponent telegramComponent;

    @Value("${app.base-url}")
    private String baseUrl;

    @Value("${telegram.bot.webhook-path:/telegram/webhook}")
    private String webhookPath;

    public TelegramWebhookManager(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    /** Step 1: Get current webhook info */
    public WebhookInfo getWebhookInfo() throws TelegramApiException {
        return telegramComponent.getWebhookInfo();
    }

    /** Step 2: Reset webhook */
    public void resetWebhook() throws TelegramApiException {
        // Delete old webhook
        telegramComponent.execute(new DeleteWebhook());
        System.out.println("Old webhook deleted");

        // Set new webhook
        String fullWebhookUrl = baseUrl + webhookPath;
        telegramComponent.setWebhook(new SetWebhook(fullWebhookUrl));
        System.out.println("Webhook reset to: " + fullWebhookUrl);
    }

    /** Step 3: Test webhook */
    public void testWebhook(String chatId, String text) {
        try {
            telegramComponent.execute(new SendMessage(chatId, text));
            System.out.println("Test message sent to chatId " + chatId);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
