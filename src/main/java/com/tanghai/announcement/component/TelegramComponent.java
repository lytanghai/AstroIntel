package com.tanghai.announcement.component;

import com.tanghai.announcement.service.TelegramBotService;
import com.tanghai.announcement.utilz.Commander;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramComponent extends TelegramWebhookBot {

    private final TelegramBotService telegramBotService;
    private final String botToken;
    private final String botUsername;
    private final String botPath;

    public TelegramComponent(TelegramBotService telegramBotService,
                             @Value("${telegram.bot.token}") String botToken,
                             @Value("${telegram.bot.username}") String botUsername,
                             @Value("${telegram.bot.webhook-path}") String botPath) {
        this.telegramBotService = telegramBotService;
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.botPath = botPath;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String command = update.getMessage().getText();
            if (Commander.isValid(command)) {
                telegramBotService.processCommand(update);
            }
        }
        return null; // important!
    }

}
