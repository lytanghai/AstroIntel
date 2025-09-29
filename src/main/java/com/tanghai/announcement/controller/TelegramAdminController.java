package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramPollingBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
public class TelegramAdminController {

    private final TelegramPollingBot bot;

    public TelegramAdminController(TelegramPollingBot bot) {
        this.bot = bot;
    }

    // Manually clear webhook + drop pending updates
    @PostMapping("/telegram/delete-pending-webhook")
    public String clearUpdates() {
        try {
            bot.execute(DeleteWebhook.builder()
                    .dropPendingUpdates(true)
                    .build());

            return "✅ Webhook deleted & all pending updates cleared!";
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "❌ Failed: " + e.getMessage();
        }
    }

    // Check webhook status
    @GetMapping("/telegram/status")
    public String getWebhookStatus() {
        try {
            WebhookInfo info = bot.execute(new GetWebhookInfo());
            return "ℹ️ Webhook URL: " + info.getUrl() +
                   "\nPending updates: " + info.getPendingUpdatesCount() +
                   "\nLast error: " + info.getLastErrorMessage();
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "❌ Failed: " + e.getMessage();
        }
    }
}
