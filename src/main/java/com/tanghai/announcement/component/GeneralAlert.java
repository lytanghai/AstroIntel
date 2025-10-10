package com.tanghai.announcement.component;

import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.constant.TelegramConst;
import com.tanghai.announcement.dto.req.SupportResistanceReq;
import com.tanghai.announcement.dto.resp.GoldApiResp;
import com.tanghai.announcement.service.OperatorService;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.utilz.DateUtilz;
import com.tanghai.announcement.utilz.Formatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeneralAlert {
    private final TelegramSender telegramSender;
    private final GistService gistService;
    private final OperatorService operatorService;

    public GeneralAlert(TelegramSender telegramSender, GistService gistService, OperatorService operatorService) {
        this.telegramSender = telegramSender;
        this.gistService = gistService;
        this.operatorService = operatorService;
    }

    private Double PREVIOUS_30_MIN_PRICE = 0.0;

    private static final String MARKET_OPEN_MSG =
            "🌅 𝙈𝙖𝙧𝙠𝙚𝙩 𝙊𝙥𝙚𝙣* \n\n" +
            "📢 The Gold Market is now open!\n" +
            "⏰ Trading hours: Monday–Friday, 24h\n" +
            "💡 Happy Trading!\n\n" +
            "* " + MessageConst.getRandomQuote();


    private static final String MARKET_CLOSE_MSG =
        "🌙 𝙈𝙖𝙧𝙠𝙚𝙩 𝘾𝙡𝙤𝙨𝙚𝙙* \n\n"+
        "📢 The Gold Market is now closed.\n" +
        "⏰ Reopens: Monday 05:00 AM (Phnom Penh time)\n" +
        "🎉 Happy Weekend!\n\n" +
        "* " + MessageConst.getRandomQuote();

    private static final String ASIA_SESSION_MSG =
            "🌏 𝘼𝙨𝙞𝙖 𝙎𝙚𝙨𝙨𝙞𝙤𝙣* \n\n" +
            "📢 The Asia trading session has started.\n" +
            "⏰ Trading hours: 07:00 AM – 04:00 PM GMT\n" +
            "⏰ Average Pips Movement: 20-40 Points\n" +
            "💡 Watch out for volatility in Asian markets!\n\n" +
            "* " + MessageConst.getRandomQuote();

    private static final String LONDON_SESSION_MSG =
            "🇬🇧 𝙇𝙤𝙣𝙙𝙤𝙣 𝙎𝙚𝙨𝙨𝙞𝙤𝙣* \n\n" +
            "📢 The London trading session has started.\n" +
            "⏰ Trading hours: 02:00 PM – 11:00 PM GMT\n" +
            "⏰ Average Pips Movement: 30-70 Points\n" +
            "💡 Expect increased activity in gold and forex markets!\n\n" +
            "* " + MessageConst.getRandomQuote();

    private static final String NEW_YORK_SESSION_MSG =
            "🇺🇸 *𝙉𝙚𝙬 𝙔𝙤𝙧𝙠 𝙎𝙚𝙨𝙨𝙞𝙤𝙣 \n\n" +
            "📢 The New York trading session has started.\n" +
            "⏰ Trading hours: 08:00 PM – 05:00 AM GMT\n" +
            "⏰ Average Pips Movement: 40-80 Points\n" +
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

    private Map<String, Object> fetchGistAsMap(Double goldPrice) {
        Map<String, Object> json = gistService.getGistContent(true, TelegramConst.PRICE_JSON);
        if (json == null) json = new HashMap<>();

        Map<String, Object> xauHistory = (Map<String, Object>) json.get("xau_history");
        if (xauHistory == null) xauHistory = new HashMap<>();

        xauHistory.put(DateUtilz.format(new Date()), goldPrice);
        json.put("xau_history", xauHistory);

        return json;
    }

    @Scheduled(cron = "0 0,30 7-23 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void alertPrice30Min() {
        GoldApiResp goldResponse = ForexService.goldApiResp();
        sendToAllSubscribers(Formatter.autoAlertGoldPrice(
                goldResponse,
                PREVIOUS_30_MIN_PRICE,
                operatorService.getSupportResistanceReq()));
        if(goldResponse.getPrice() != null) {
            this.fetchGistAsMap(goldResponse.getPrice());
            PREVIOUS_30_MIN_PRICE = goldResponse.getPrice();
        }
    }

    @Scheduled(cron = "0 0 5 ? * MON", zone = "GMT")
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
       String msg =  "🌏 *𝘼𝙨𝙞𝙖 𝙎𝙚𝙨𝙨𝙞𝙤𝙣 𝘾𝙡𝙤𝙨𝙚𝙙 \n" +
        "⏰ 16:00:00 (4PM) \n\n* " + MessageConst.getRandomQuote();
        sendToAllSubscribers(msg);
    }

    // London Session: 2:00 PM – 11:00 PM
    @Scheduled(cron = "0 0 14 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void londonSessionOpen() {
        sendToAllSubscribers(LONDON_SESSION_MSG);
    }

    @Scheduled(cron = "0 0 23 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void londonSessionClose() {
        String msg = "🇬🇧 *𝙇𝙤𝙣𝙙𝙤𝙣 𝙎𝙚𝙨𝙨𝙞𝙤𝙣 𝘾𝙡𝙤𝙨𝙚𝙙" +
                "⏰ 23:00:00 (11PM) \n\n" +
                "* " + MessageConst.getRandomQuote();
        sendToAllSubscribers(msg);
    }

    // New York Session: 8:00 PM – 5:00 AM (next day)
    @Scheduled(cron = "0 0 20 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void newYorkSessionOpen() {
        sendToAllSubscribers(NEW_YORK_SESSION_MSG);
    }

    @Scheduled(cron = "0 0 5 ? * TUE-SAT", zone = "Asia/Phnom_Penh") // next day close
    void newYorkSessionClose() {
        String msg = "🇺🇸 *𝙉𝙚𝙬 𝙔𝙤𝙧𝙠 𝙎𝙚𝙨𝙨𝙞𝙤𝙣 𝘾𝙡𝙤𝙨𝙚𝙙* \n" + "⏰ 05:00:00 (5AM) \n\n" +
                "* " + MessageConst.getRandomQuote();
        sendToAllSubscribers(msg);
    }

    @Scheduled(cron = "0 0 5 ? * MON-FRI", zone = "Asia/Phnom_Penh")
    void clearSupportResistance() {
        operatorService.clear();
    }

}
