package com.tanghai.announcement.service;

import com.tanghai.announcement.component.TelegramSender;
import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.service.internet.GoldPriceService;
import com.tanghai.announcement.utilz.Formatter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramBotService {

    private final TelegramSender telegramSender;
    private ForexService forexService;
    private final GistService gistService;
    private final GoldPriceService goldPriceService;

    public TelegramBotService(TelegramSender telegramSender, GistService gistService, GoldPriceService goldPriceService) {
        this.telegramSender = telegramSender;
        this.gistService = gistService;
        this.goldPriceService = goldPriceService;
    }

    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String username = update.getMessage().getFrom().getUserName();
            String user = (username != null ? "@" + username :  update.getMessage().getFrom().getFirstName());
            String reply = processCommand(
                    chatId,
                    user,
                    update.getMessage().getText()
            );

            telegramSender.send(chatId, reply);
        }
    }

    private String processCommand(String chatId, String user, String command) {
        switch (command) {
            case "/calendar": return Formatter.formatForexCalendar(ForexService.economicCalendar());

            case "/gold": return Formatter.formatGoldPrice(ForexService.goldApiResp());

            case "/help":
                return "*🤖 Bot Commands Help*\n\n" +
                        "📅 /calendar \\- Show this week's important events (US)\n" +
                        "💰 /gold \\- Show the real-time live price of gold\n" +
                        "🔔 /subscribe \\- Receive alerts and important announcements\n" +
                        "❌ /unsubscribe \\- Stop receiving alerts and announcements\n\n" +
                        "* " + MessageConst.getRandomQuote() +
                        "\n\n_Use the commands exactly as shown above._";

            case "/subscribe": gistService.subscribeToGist(chatId);
                return "✅ *Subscription Successful!*\n\n" +
                        "You will now receive important alerts and announcements.\n\n" +
                        MessageConst.getRandomQuote();

            case "/unsubscribe": gistService.unSubscribeToGist(chatId);
                return "❌ *Unsubscription Successful!*\n\n" +
                        "You will no longer receive alerts and announcements. \n\n" +
                        MessageConst.getRandomQuote();

            case "/trend": return goldPriceService.showTechnicalAnalysis(false);

            default: return "Unknown command: " + command;
        }
    }
}
