package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * User sends /start
 *       ↓
 * Telegram server sends update → Webhook URL
 *       ↓
 * Spring Boot controller receives update
 *       ↓
 * TelegramComponent calls TelegramBotService
 *       ↓
 * Service generates reply text
 *       ↓
 * Bot sends reply via execute(SendMessage)
 *       ↓
 * User sees reply in Telegram
 * */

@RestController
public class TelegramController {

    private final Logger  logger = LoggerFactory.getLogger(TelegramController.class);

    private final TelegramComponent telegramBot;

    public TelegramController(TelegramComponent telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostMapping("/callback")
    public void receiveUpdate(@RequestBody Update update) {
        logger.info("Received Update {}", update.getUpdateId());
        telegramBot.onWebhookUpdateReceived(update);
    }
}
