package com.tanghai.announcement.service;

import com.tanghai.announcement.cache.MonthlyReserveCache;
import com.tanghai.announcement.component.TelegramSender;
import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.service.internet.GoldPriceService;
import com.tanghai.announcement.utilz.DateUtilz;
import com.tanghai.announcement.utilz.Formatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Date;

@Service
public class TelegramBotService {

    private final Logger log = LoggerFactory.getLogger(TelegramBotService.class);
    private final TelegramSender telegramSender;
    private final GistService gistService;
    private final GoldPriceService goldPriceService;
    private final GeminiApiService aiService;

    public TelegramBotService(TelegramSender telegramSender, GistService gistService, GoldPriceService goldPriceService, GeminiApiService aiService) {
        this.telegramSender = telegramSender;
        this.gistService = gistService;
        this.goldPriceService = goldPriceService;
        this.aiService = aiService;
    }

    public void handleUpdate(Update update) throws Exception {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String reply = processCommand(
                    chatId,
                    update.getMessage().getText()
            );

            telegramSender.send(chatId, reply);
        }
    }

    private String budgetBreakdown(String preFixAmount, String chatId) {
        if (!"678134373".equals(chatId)) {
            return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
        }

        MonthlyReserveCache.clear();

        String amountStr = preFixAmount.split(":")[1].trim().replace("$", "");
        double amount = Double.parseDouble(amountStr);

        // --- Percentages ---
        double savingPercent = 0.50;
        double reservePercent = 0.20;
        double basicPercent = 0.30;

        // --- Calculate main categories ---
        double saving = amount * savingPercent;
        double reserve = amount * reservePercent;
        double basic = amount * basicPercent;

        // --- Saving breakdown ---
        double investment = 100;
        double bank = saving - investment;

        // --- Basic breakdown ---
        double wifi = 15;
        double gasoline = 10;
        double ptu = 5;
        double oil = 5;
        double basicItems = wifi + gasoline + ptu + oil;
        double remainingBasic = basic - basicItems;

        String date = DateUtilz.format(new  Date(), "yyyy-MM");

        if (remainingBasic < 0) {
            throw new IllegalArgumentException("Basic expenses exceed 30% of total!");
        }

        // --- Check total sum ---
        double totalCheck = bank + investment + reserve + basic;
        if (Math.abs(totalCheck - amount) > 0.01) {
            throw new IllegalStateException("Amounts do not sum up to total!");
        }

        // --- Telegram-friendly message ---
        String message = String.format(
                "*=== 💰𝗕𝘂𝗱𝗴𝗲𝘁 𝗕𝗿𝗲𝗮𝗸𝗱𝗼𝘄𝗻 ===*\n" +
                        "* 𝗗𝗮𝘁𝗲: %s\n" +
                        "\n" +
                        "*𝗦𝗮𝘃𝗶𝗻𝗴 (50%%): $%.2f\n" +
                        "    └─*Invest[𝗕𝗶𝗻𝗮𝗻𝗰𝗲 x 𝗘𝗫𝗡𝗘𝗦𝗦]: $%.2f\n" +
                        "    └─*Bank[𝗪𝗜𝗡𝗚]: $%.2f\n" +
                        "\n" +
                        "*𝗥𝗲𝘀𝗲𝗿𝘃𝗲[𝗔𝗕𝗔] (20%%): $%.2f\n" +
                        "\n" +
                        "*𝗕𝗮𝘀𝗶𝗰 𝗡𝗲𝗲𝗱  (30%%): $%.2f\n" +
                        "    └─*Wifi: $%.2f\n" +
                        "    └─*Gasoline: $%.2f\n" +
                        "    └─*PTU: $%.2f\n" +
                        "    └─*Oil: $%.2f\n" +
                        "    └─*Remaining: $%.2f\n" +
                        "\n" +
                        "-----------------------\n" +
                        "🧮 *𝗖𝗵𝗲𝗰𝗸 𝗧𝗼𝘁𝗮𝗹: $%.2f ✅",
                date,
                saving, investment, bank,
                reserve,
                basic, wifi, gasoline, ptu, oil, remainingBasic,
                totalCheck
        );

        MonthlyReserveCache.save(message);

        return message;
    }

    private String processCommand(String chatId, String command) throws Exception {

        log.info("incoming command {}", command);

        if (command.trim().startsWith("*monthly:")) {
            return this.budgetBreakdown(command, chatId);
        }

        switch (command) {

            case "/budget":
                if (!"678134373".equals(chatId)) {
                    return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
                } else {
                    if(MonthlyReserveCache.hasCache()) {
                        return MonthlyReserveCache.get();
                    } else {
                        return "𝗣𝗹𝗲𝗮𝘀𝗲 𝗰𝗿𝗲𝗮𝘁𝗲 𝗮 𝗻𝗲𝘄 𝗯𝘂𝗱𝗴𝗲𝘁 𝗯𝗿𝗲𝗮𝗸𝗱𝗼𝘄𝗻 𝗱𝘂𝗲 𝘁𝗼 𝘀𝗲𝗿𝘃𝗶𝗰𝗲 𝗿𝗲𝘀𝘁𝗮𝗿𝘁𝗲𝗱!!! eg. *monthly: xxx$";
                    }
                }

            case "/clsbud": MonthlyReserveCache.clear();
                        return "𝘔𝘰𝘯𝘵𝘩𝘭𝘺 𝘉𝘶𝘥𝘨𝘦𝘵 𝘩𝘢𝘴 𝘣𝘦𝘦𝘯 𝘤𝘭𝘦𝘢𝘳𝘦𝘥 𝘴𝘶𝘤𝘤𝘦𝘴𝘴𝘧𝘶𝘭𝘭𝘺!";

            case "/calendar":
                return Formatter.formatForexCalendar(ForexService.economicCalendar());

            case "/gold":
                return Formatter.formatGoldPrice(ForexService.goldApiResp());

            case "/help":
                return "*🤖 Ｂｏｔ Ｃｏｍｍａｎｄｓ Ｈｅｌｐ*\n\n" +
                        "📅 /calendar \\- Show this week's important events (US)\n" +
                        "💰 /gold \\- Show the real-time live price of gold\n" +
                        "🔔 /subscribe \\- Receive alerts and important announcements\n" +
                        "❌ /unsubscribe \\- Stop receiving alerts and announcements\n\n" +
                        "📅 /budget \\- Check monthly budget breakdown\n\n" +
                        "⭐ /clsbud  \\- Clear monthly budget\n\n" +
                        "* " + MessageConst.getRandomQuote() +
                        "\n\n_Use the commands exactly as shown above._";

            case "/subscribe":
                gistService.subscribeToGist(chatId);
                return "✅ *𝙎𝙪𝙗𝙨𝙘𝙧𝙞𝙥𝙩𝙞𝙤𝙣 𝙎𝙪𝙘𝙘𝙚𝙨𝙨𝙛𝙪𝙡!*\n\n" +
                        "𝙔𝙤𝙪 𝙬𝙞𝙡𝙡 𝙣𝙤𝙬 𝙧𝙚𝙘𝙚𝙞𝙫𝙚 𝙞𝙢𝙥𝙤𝙧𝙩𝙖𝙣𝙩 𝙖𝙡𝙚𝙧𝙩𝙨 𝙖𝙣𝙙 𝙖𝙣𝙣𝙤𝙪𝙣𝙘𝙚𝙢𝙚𝙣𝙩𝙨.\n\n" +
                        MessageConst.getRandomQuote();

            case "/unsubscribe":
                gistService.unSubscribeToGist(chatId);
                return "❌ *𝙐𝙣𝙨𝙪𝙗𝙨𝙘𝙧𝙞𝙥𝙩𝙞𝙤𝙣 𝙎𝙪𝙘𝙘𝙚𝙨𝙨𝙛𝙪𝙡!*\n\n" +
                        "𝙔𝙤𝙪 𝙬𝙞𝙡𝙡 𝙣𝙤 𝙡𝙤𝙣𝙜𝙚𝙧 𝙧𝙚𝙘𝙚𝙞𝙫𝙚 𝙖𝙡𝙚𝙧𝙩𝙨 𝙖𝙣𝙙 𝙖𝙣𝙣𝙤𝙪𝙣𝙘𝙚𝙢𝙚𝙣𝙩𝙨. \n\n" +
                        MessageConst.getRandomQuote();

            case "/trend":
                return goldPriceService.showTechnicalAnalysis(false);

            default:
                return aiService.generateText(chatId, command);
        }
    }
}
