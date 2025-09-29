package com.tanghai.announcement.service;

import com.tanghai.announcement.component.TelegramSender;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.utilz.Formatter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramBotService {

    private final TelegramSender telegramSender;
    private ForexService forexService;

    public TelegramBotService(TelegramSender telegramSender) {
        this.telegramSender = telegramSender;
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String username = update.getMessage().getFrom().getUserName(); // may be null
            String user = (username != null ? "@" + username :  update.getMessage().getFrom().getFirstName());
            String reply = processCommand(user, update.getMessage().getText());

            telegramSender.send(update.getMessage().getChatId().toString(), reply);
        }
    }

    private String processCommand(String user, String command) {
        switch (command) {
            case "/start": return "Greetings, " + user + "! Astro Bot at your service!";
            case "/ping": return "Astro Bot is up and healthy!";
            case "/calendar": return Formatter.formatForexCalendar(ForexService.economicCalendar());
            case "/gold": return Formatter.formatGoldPrice(ForexService.goldApiResp());
            case "/help": return "Commands:\n/start\n/ping\n/help";
            default: return "Unknown command: " + command;
        }
    }
}
