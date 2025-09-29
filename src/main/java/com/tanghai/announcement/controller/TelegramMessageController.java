package com.tanghai.announcement.controller;

import com.tanghai.announcement.component.TelegramComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("telegram/webhook")
public class TelegramMessageController {

    private static final Logger log = LoggerFactory.getLogger(TelegramMessageController.class);
    private TelegramComponent telegramComponent;

    public TelegramMessageController(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    @PostMapping("/callback/telegram/webhook")
    public void sendMessage(@RequestBody Update update) {
        log.info("Incoming Request...");
        telegramComponent.onWebhookUpdateReceived(update);
    }
}
