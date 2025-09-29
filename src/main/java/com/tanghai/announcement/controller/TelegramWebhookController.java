package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramPollingBot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
public class TelegramWebhookController {

    private final TelegramPollingBot bot;

    public TelegramWebhookController(TelegramPollingBot bot) {
        this.bot = bot;
    }

    @PostMapping("/telegram/delete-webhook")
    public String deleteWebhook() {
        try {
            bot.execute(new DeleteWebhook());
            return "Webhook deleted successfully!";
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "Failed to delete webhook: " + e.getMessage();
        }
    }
    @PostMapping("/telegram/delete-pending-webhook")
    public String clearUpdates() {
        try {
            bot.execute(DeleteWebhook.builder()
                    .dropPendingUpdates(true) // this clears pending updates
                    .build());

            return "✅ Webhook deleted & all pending updates cleared!";
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return "❌ Failed: " + e.getMessage();
        }
    }
}