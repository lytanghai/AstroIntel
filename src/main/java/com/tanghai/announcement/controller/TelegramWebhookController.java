package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramWebhookManager;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.WebhookInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@RestController
@RequestMapping("/telegram/webhook-manager")
public class TelegramWebhookController {

    private final TelegramWebhookManager manager;

    public TelegramWebhookController(TelegramWebhookManager manager) {
        this.manager = manager;
    }

    @GetMapping("/health/check")
    public String healthCheck() {
        return "OK";
    }

    @GetMapping("/info")
    public WebhookInfo getInfo() throws TelegramApiException {
        return manager.getWebhookInfo();
    }

    @PostMapping("/reset")
    public String resetWebhook() throws TelegramApiException {
        manager.resetWebhook();
        return "Webhook has been reset!";
    }

    @PostMapping("/test")
    public String testWebhook(@RequestParam String chatId, @RequestParam String text) {
        manager.testWebhook(chatId, text);
        return "Test message sent!";
    }
}