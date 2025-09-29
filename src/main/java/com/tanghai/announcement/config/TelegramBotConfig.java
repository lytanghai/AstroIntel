package com.tanghai.announcement.config;

import com.tanghai.announcement.component.TelegramPollingBot;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final TelegramPollingBot bot;

    public TelegramBotConfig(TelegramPollingBot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void registerBot() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("Polling bot registered successfully!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
