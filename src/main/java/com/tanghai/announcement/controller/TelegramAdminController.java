package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
@RequestMapping("/telegram/webhook")
public class TelegramAdminController {

    private final TelegramComponent telegramComponent;
    private final String baseUrl;
    private final String webhookPath;

    public TelegramAdminController(TelegramComponent telegramComponent,
                                   @Value("${app.base-url}") String baseUrl,
                                   @Value("${telegram.bot.webhook-path}") String webhookPath) {
        this.telegramComponent = telegramComponent;
        this.baseUrl = baseUrl;
        this.webhookPath = webhookPath;
    }

    /** Get current webhook info */
    @GetMapping("/status")
    public WebhookInfo getWebhookInfo() {
        try {
            return telegramComponent.execute(new GetWebhookInfo());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Delete webhook and clear pending updates */
    @PostMapping("/delete")
    public String deleteWebhook() {
        try {
            telegramComponent.execute(DeleteWebhook.builder().dropPendingUpdates(true).build());
            return "✅ Webhook deleted & pending updates cleared!";
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "❌ Failed to delete webhook: " + e.getMessage();
        }
    }

    /** Set or reset webhook */
    @PostMapping("/reset")
    public String resetWebhook() {
        try {
            String fullUrl = baseUrl + webhookPath;
            telegramComponent.execute(SetWebhook.builder().url(fullUrl).build());
            return "✅ Webhook reset to: " + fullUrl;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "❌ Failed to reset webhook: " + e.getMessage();
        }
    }
}
