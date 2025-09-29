package com.tanghai.announcement.service;

import com.tanghai.announcement.component.TelegramComponent;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramBotService {

    private final TelegramComponent telegramComponent;

    public TelegramBotService(TelegramComponent telegramComponent) {
        this.telegramComponent = telegramComponent;
    }

    /** Main entry point from controller */
    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String text = update.getMessage().getText();

            // Process command and get reply
            String reply = processCommand(text);

            // Send reply
            sendMessage(chatId, reply);
        }
    }

    /** Business logic for commands */
    private String processCommand(String command) {
        switch (command) {
            case "/start":
                return "Hello! I’m your bot 🤖\nType /ping to test.";
            case "/ping":
                return "Pong!";
            case "/help":
                return "Available commands:\n/start - Welcome message\n/ping - Test bot";
            default:
                return "Unknown command: " + command;
        }
    }

    /** Send a message back to Telegram */
    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            telegramComponent.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            System.err.println("Failed to send message to chatId " + chatId);
        }
    }
}
