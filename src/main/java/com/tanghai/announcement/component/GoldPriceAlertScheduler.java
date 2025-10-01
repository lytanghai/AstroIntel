package com.tanghai.announcement.component;

import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.service.internet.GoldPriceWebSocketService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class GoldPriceAlertScheduler {

    private final GoldPriceWebSocketService goldPriceWebSocketService;
    private final GistService gistService;
    private final TelegramSender telegramSender;

    public GoldPriceAlertScheduler(GoldPriceWebSocketService wsService,
                                   GistService gistService,
                                   TelegramSender telegramSender) {
        this.goldPriceWebSocketService = wsService;
        this.gistService = gistService;
        this.telegramSender = telegramSender;
    }

    @Scheduled(fixedRate = 120000) // every 2 minutes
    public void sendGoldPriceAlert() {
        Double price = goldPriceWebSocketService.getLatestPrice();
        if (price != null) {
            String message = "💰 *Gold Price Alert*\n\n" +
                             "Current XAU/USD price: $" + price;
            
            // Get chat IDs from Gist cache
            var json = gistService.getGistContent();
            var telegram = (Map<String, Object>) json.get("telegram");
            if (telegram != null) {
                var chatIds = (List<String>) telegram.get("chat_id");
                if (chatIds != null) {
                    for (String chatId : chatIds) {
                        telegramSender.send(chatId, message);
                    }
                }
            }
        }
    }
}
