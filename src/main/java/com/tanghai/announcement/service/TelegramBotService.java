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

    public void processCommand(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();

        switch (text) {
            case "/start":
                sendMessage(chatId, "Hello! I’m your bot.");
                break;
            case "/ping":
                sendMessage(chatId, "Pong!");
                break;
            default:
                sendMessage(chatId, "Unknown command: " + text);
        }
    }

    private void sendMessage(String chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        try {
            telegramComponent.execute(message); // sends message via Telegram
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
