package com.tanghai.announcement.service;

import org.springframework.stereotype.Service;

@Service
public class TelegramBotService {

    public String processCommandText(String text) {
        switch (text) {
            case "/start":
                return "Hello! I’m your bot.";
            case "/ping":
                return "Pong!";
            default:
                return "Unknown command: " + text;
        }
    }

}
