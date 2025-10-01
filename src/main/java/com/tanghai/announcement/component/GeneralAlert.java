package com.tanghai.announcement.component;

import com.tanghai.announcement.constant.TelegramConst;
import com.tanghai.announcement.service.internet.GistService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GeneralAlert {
    private final TelegramSender telegramSender;
    private final GistService gistService;

    public GeneralAlert(TelegramSender telegramSender, GistService gistService) {
        this.telegramSender = telegramSender;
        this.gistService = gistService;
    }

    private static final String MARKET_OPEN_MSG =
            "🌅 *Market Open Alert*\n\n" +
            "📢 The Gold Market is now open!\n" +
            "⏰ Trading hours: Monday–Friday, 24h\n" +
            "💡 Happy Trading!";


    private static final String MARKET_CLOSE_MSG =
        "🌙 *Market Closed*\n\n" +
        "📢 The Gold Market is now closed.\n" +
        "⏰ Reopens: Monday 05:00 AM (Phnom Penh time)\n" +
        "🎉 Happy Weekend!";

    /** Fetch subscribed chat IDs from Gist and send message to each */
    private void sendToAllSubscribers(String message) {
        Map<String, Object> json = gistService.getGistContent();
        Map<String, Object> telegram = (Map<String, Object>) json.get(TelegramConst.TELEGRAM);

        if (telegram == null) return;

        List<String> chatIds = (List<String>) telegram.get(TelegramConst.CHAT_ID);
        if (chatIds == null || chatIds.isEmpty()) return;

        for (String chatId : chatIds) {
            telegramSender.send(chatId, message);
        }
    }

    @Scheduled(cron = "0 0 5 ? * MON-FRI", zone = "GMT")
    void marketOpen() {
        sendToAllSubscribers(MARKET_OPEN_MSG);
    }

    @Scheduled(cron = "0 0 4 ? * SAT", zone = "GMT")
    void marketClose() {
        sendToAllSubscribers(MARKET_CLOSE_MSG);
    }
}
