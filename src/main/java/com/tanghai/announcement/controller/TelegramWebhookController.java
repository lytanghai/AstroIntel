package com.tanghai.announcement.controller;

import com.tanghai.announcement.service.TelegramBotService;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/telegram")
public class TelegramWebhookController {

    private final TelegramBotService telegramBotService;

    public TelegramWebhookController(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @PostMapping("/webhook")
    public void onUpdateReceived(@RequestBody Update update) throws Exception {
        telegramBotService.handleUpdate(update);
    }
}
