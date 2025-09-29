package com.tanghai.announcement.component;

import com.tanghai.announcement.service.TelegramBotService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TelegramPollingBot extends TelegramLongPollingBot {
    private final TelegramProperties telegramProperties;

    private final TelegramBotService telegramBotService;

    public TelegramPollingBot(TelegramProperties telegramProperties, TelegramBotService telegramBotService) {
        this.telegramProperties = telegramProperties;
        this.telegramBotService = telegramBotService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String command = update.getMessage().getText();
            String reply = telegramBotService.processCommandText(command);

            try {
                execute(new SendMessage(chatId, reply));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getBotToken();
    }
}
