package com.tanghai.announcement.component;

import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.constant.TelegramConst;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.utilz.Formatter;
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
            "💡 Happy Trading!\n\n" +
            "* " + MessageConst.getRandomQuote();


    private static final String MARKET_CLOSE_MSG =
        "🌙 *Market Closed*\n\n" +
        "📢 The Gold Market is now closed.\n" +
        "⏰ Reopens: Monday 05:00 AM (Phnom Penh time)\n" +
        "🎉 Happy Weekend!\n\n" +
        "* " + MessageConst.getRandomQuote();

    private static final String ASIA_SESSION_MSG =
            "🌏 *Asia Session Alert*\n\n" +
            "📢 The Asia trading session has started.\n" +
            "⏰ Trading hours: 07:00 AM – 04:00 PM GMT\n" +
            "💡 Watch out for volatility in Asian markets!\n\n" +
            "* " + MessageConst.getRandomQuote();

    private static final String LONDON_SESSION_MSG =
            "🇬🇧 *London Session Alert*\n\n" +
            "📢 The London trading session has started.\n" +
            "⏰ Trading hours: 02:00 PM – 11:00 PM GMT\n" +
            "💡 Expect increased activity in gold and forex markets!\n\n" +
            "* " + MessageConst.getRandomQuote();

    private static final String NEW_YORK_SESSION_MSG =
            "🇺🇸 *New York Session Alert*\n\n" +
            "📢 The New York trading session has started.\n" +
            "⏰ Trading hours: 08:00 PM – 05:00 AM GMT\n" +
            "💡 Major economic news may impact gold prices!\n\n" +
            "* " + MessageConst.getRandomQuote();

    /** Fetch subscribed chat IDs from Gist and send message to each */
    private void sendToAllSubscribers(String message) {
        Map<String, Object> json = gistService.getGistContent(true, TelegramConst.DATA_JSON);
        Map<String, Object> telegram = (Map<String, Object>) json.get(TelegramConst.TELEGRAM);

        if (telegram == null) return;

        List<String> chatIds = (List<String>) telegram.get(TelegramConst.CHAT_ID);
        if (chatIds == null || chatIds.isEmpty()) return;

        for (String chatId : chatIds) {
            telegramSender.send(chatId, message);
        }
    }

    @Scheduled(cron = "0 0,30 7-23 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void alertPrice30Min() {
        sendToAllSubscribers(Formatter.autoAlertGoldPrice(ForexService.goldApiResp()));
    }

    @Scheduled(cron = "0 0 5 ? * MON-FRI", zone = "GMT")
    void marketOpen() {
        sendToAllSubscribers(MARKET_OPEN_MSG);
    }

    @Scheduled(cron = "0 0 4 ? * SAT", zone = "GMT")
    void marketClose() {
        sendToAllSubscribers(MARKET_CLOSE_MSG);
    }

    // Asia Session (Tokyo): 7:00 AM – 4:00 PM
    @Scheduled(cron = "0 0 7 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void asiaSessionOpen() {
        sendToAllSubscribers(ASIA_SESSION_MSG);
    }

    @Scheduled(cron = "0 0 16 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void asiaSessionClose() {
        sendToAllSubscribers("🌏 *Asia Session Closed* ⏰ 16:00:00 Phnom Penh time" + "\n\n* " + MessageConst.getRandomQuote());
    }

    // London Session: 2:00 PM – 11:00 PM
    @Scheduled(cron = "0 0 14 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void londonSessionOpen() {
        sendToAllSubscribers(LONDON_SESSION_MSG);
    }

    @Scheduled(cron = "0 0 23 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void londonSessionClose() {
        sendToAllSubscribers("🇬🇧 *London Session Closed* ⏰ 23:00:00 Phnom Penh time" + "\n\n* " + MessageConst.getRandomQuote());
    }

    // New York Session: 8:00 PM – 5:00 AM (next day)
    @Scheduled(cron = "0 0 20 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void newYorkSessionOpen() {
        sendToAllSubscribers(NEW_YORK_SESSION_MSG);
    }

    @Scheduled(cron = "0 0 5 ? * TUE-SAT", zone = "Asia/Phnom_Penh") // next day close
    void newYorkSessionClose() {
        sendToAllSubscribers("🇺🇸 *New York Session Closed* ⏰ 05:00:00 Phnom Penh time" + "\n\n* " + MessageConst.getRandomQuote());
    }

}
