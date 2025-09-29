package com.tanghai.announcement.component;

import com.tanghai.announcement.service.TelegramBotService;
import com.tanghai.announcement.utilz.Commander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramComponent extends TelegramWebhookBot {

    private final TelegramBotService telegramBotService;

    public TelegramComponent(TelegramBotService telegramBotService) {
        this.telegramBotService = telegramBotService;
    }

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.webhook-path:/telegram/webhook}")
    private String botPath;

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
        System.out.println("Message Received!");
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String command = update.getMessage().getText();
            System.out.println("Chat Id: " + chatId + " Command: " + command);

            if (Commander.isValid(command)) {
                String reply = telegramBotService.processCommandText(command);
                SendMessage message = new SendMessage(chatId, reply);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void setWebhook(SetWebhook setWebhook) throws TelegramApiException {
        super.setWebhook(setWebhook);
    }
}