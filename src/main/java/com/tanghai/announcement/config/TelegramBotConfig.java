package com.tanghai.announcement.config;

import com.tanghai.announcement.component.TelegramPollingBot;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    private final TelegramPollingBot bot;

    public TelegramBotConfig(TelegramPollingBot bot) {
        this.bot = bot;
    }

    @javax.annotation.PostConstruct
    public void registerBot() {
        try {
            // Step 1: Delete webhook (avoid 409 conflict)
            bot.execute(DeleteWebhook.builder()
                    .dropPendingUpdates(true) // also clears queue
                    .build());
            System.out.println("✅ Webhook deleted (polling mode enabled)");

            // Step 2: Register bot for polling
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            System.out.println("🤖 Polling bot registered successfully!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
