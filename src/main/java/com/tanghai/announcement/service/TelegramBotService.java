package com.tanghai.announcement.service;

import com.tanghai.announcement.component.TelegramSender;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramBotService {

    private final TelegramSender telegramSender;

    public TelegramBotService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String command = update.getMessage().getText();

            String reply = processCommand(command);

            telegramSender.send(chatId, reply);
        }
    }

    private String processCommand(String command) {
        switch (command) {
            case "/start": return "Hello! I’m your bot 🤖";
            case "/ping": return "Pong!";
            case "/help": return "Commands:\n/start\n/ping\n/help";
            default: return "Unknown command: " + command;
        }
    }
}
