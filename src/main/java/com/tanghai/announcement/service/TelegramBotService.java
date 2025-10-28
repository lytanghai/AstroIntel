package com.tanghai.announcement.service;

import com.tanghai.announcement.cache.MonthlyReserveCache;
import com.tanghai.announcement.component.TelegramComponent;
import com.tanghai.announcement.component.TelegramSender;
import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.constant.TelegramConst;
import com.tanghai.announcement.dto.req.MonthlyCryptoReq;
import com.tanghai.announcement.dto.resp.SummaryCryptoSavingResp;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.service.internet.GoldPriceService;
import com.tanghai.announcement.utilz.DateUtilz;
import com.tanghai.announcement.utilz.Formatter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TelegramBotService {

    private final Logger log = LoggerFactory.getLogger(TelegramBotService.class);
    private final TelegramSender telegramSender;
    private final GistService gistService;
    private final GoldPriceService goldPriceService;
    private final GeminiApiService aiService;

    private final Map<Integer, ScheduledFuture<?>> recurringTasks = new ConcurrentHashMap<>();
    private final Map<Integer, String> recurringMessages = new ConcurrentHashMap<>();
    private final Map<Integer, Long> recurringIntervals = new ConcurrentHashMap<>(); // seconds
    private final ScheduledExecutorService schedulerControl = Executors.newScheduledThreadPool(2);
    private final AtomicInteger reminderCounter = new AtomicInteger(1);

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

    private String listReminder() {
        if (recurringMessages.isEmpty()) {
            return "📭 No active looping reminders.";
        }
        StringBuilder sb = new StringBuilder("📋 *Active Looping Reminders:*\n");
        recurringMessages.forEach((index, msgText) -> {
            ScheduledFuture<?> task = recurringTasks.get(index);
            Long interval = recurringIntervals.get(index);
            if (task != null && !task.isCancelled()) {
                long secondsLeft = task.getDelay(TimeUnit.SECONDS);
                sb.append(index)
                        .append(". ")
                        .append(msgText)
                        .append(" — every ")
                        .append(DateUtilz.formatDuration(interval))
                        .append(", next in ")
                        .append(DateUtilz.formatDuration(secondsLeft))
                        .append("\n");
            }
        });
        return sb.toString();
    }
    public String handleLoopRemindMe(String chatId, String message) {
        try {
            String[] parts = message.trim().split(" ", 3);
            String action = parts[1].trim();

            // --- 2️⃣ CLEAR ALL ---
            if (action.equals("*")) {
                recurringTasks.values().forEach(task -> task.cancel(true));
                recurringTasks.clear();
                recurringMessages.clear();
                recurringIntervals.clear();
                reminderCounter.set(1);
                return "🧹 All looping reminders cleared.";
            }

            // --- 3️⃣ REMOVE BY INDEX ---
            if (action.equals("-")) {
                try {
                    int index = Integer.parseInt(parts[2]);
                    ScheduledFuture<?> task = recurringTasks.remove(index);
                    String msgText = recurringMessages.remove(index);
                    recurringIntervals.remove(index);
                    if (task != null) {
                        task.cancel(true);
                        return "❌ Removed reminder #" + index + ": " + msgText;
                    } else {
                        return "⚠️ No reminder found at index #" + index;
                    }
                } catch (NumberFormatException e) {
                    return "❌ Invalid index. Must be a number.";
                }
            }

            // --- 4️⃣ ADD NEW LOOP REMINDER ---
            if (action.startsWith("+")) {
                if (parts.length < 3) {
                    return "Usage: /loop +[time] [message]";
                }

                String timePart = action.substring(1); // remove '+'
                String reminderText = parts[2];
                long delaySeconds = DateUtilz.parseTimeToSeconds(timePart);

                int index = reminderCounter.getAndIncrement();

                ScheduledFuture<?> task = schedulerControl.scheduleAtFixedRate(() -> {
                    telegramSender.send(chatId, "🔁 Reminder #" + index + ": " + reminderText);
                }, delaySeconds, delaySeconds, TimeUnit.SECONDS);

                recurringTasks.put(index, task);
                recurringMessages.put(index, reminderText);
                recurringIntervals.put(index, delaySeconds);

                return "✅ Added reminder #" + index + " every " + timePart + ": " + reminderText;
            }

            return "❌ Unknown command type. Use +, -, list, or *.";

        } catch (Exception e) {
            return "⚠️ Error processing /loop: " + e.getMessage();
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
                        "*𝗥𝗲𝘀𝗲𝗿𝘃𝗲[𝘼𝘾𝙀𝙇𝙀𝘿𝘼] (20%%): $%.2f\n" +
                        "\n" +
                        "*𝗕𝗮𝘀𝗶𝗰 𝗡𝗲𝗲𝗱[𝘼𝘽𝘼] (30%%): $%.2f\n" +
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

    public static MonthlyCryptoReq parseMonthlyMessage(String message) {
        MonthlyCryptoReq req = new MonthlyCryptoReq();
        message = message.replaceFirst("^\\*asset:\\s*", "").trim();
        String[] lines = message.split("\\r?\\n");

        for (String line : lines) {
            if (!line.contains(":")) continue;

            String[] parts = line.split(":", 2);
            String key = parts[0].trim().toLowerCase();
            String value = parts[1].trim();

            try {
                switch (key) {
                    case "amount":
                        req.setAmount(Double.parseDouble(value));
                        break;
                    case "converted":
                        req.setConverted(Double.parseDouble(value));
                        break;
                    case "symbol":
                        req.setSymbol(value);
                        break;
                    case "buy_at":
                        req.setBuyAt(Double.parseDouble(value));
                        break;
                    case "exchange":
                        req.setExchangeName(value);
                        break;
                    case "network_type":
                        req.setNetworkType(value);
                        break;
                    case "network_fee":
                        req.setNetworkFee(Double.parseDouble(value));
                        break;
                    default:
                        break;
                }
            } catch (NumberFormatException e) {}
        }

        return req;
    }

    private String registerAsset(String message) {
        MonthlyCryptoReq monthlyCryptoReq = parseMonthlyMessage(message);

        // Validation
        if (monthlyCryptoReq.getSymbol() == null)
            return "❌ Symbol must be filled!";
        if (monthlyCryptoReq.getExchangeName() == null)
            return "❌ Exchange name must be filled!";
        if (monthlyCryptoReq.getAmount() == null)
            return "❌ Amount must be filled!";
        if (monthlyCryptoReq.getConverted() == null)
            return "❌ Converted amount must be filled!";
        if (monthlyCryptoReq.getBuyAt() == null)
            return "❌ Buy-at must be filled!";

        if (monthlyCryptoReq.getNetworkType() == null || monthlyCryptoReq.getNetworkType().isEmpty())
            monthlyCryptoReq.setNetworkType("N/A");
        if (monthlyCryptoReq.getNetworkFee() == null)
            monthlyCryptoReq.setNetworkFee(0.0);

        Map<String, Object> gistUpdate = new LinkedHashMap<>();
        gistUpdate.put("date", DateUtilz.format(new Date()));
        gistUpdate.put("amount", monthlyCryptoReq.getAmount());
        gistUpdate.put("converted", monthlyCryptoReq.getConverted());
        gistUpdate.put("symbol", monthlyCryptoReq.getSymbol());
        gistUpdate.put("exchange", monthlyCryptoReq.getExchangeName());
        gistUpdate.put("network_type", monthlyCryptoReq.getNetworkType());
        gistUpdate.put("network_fee", monthlyCryptoReq.getNetworkFee());
        gistUpdate.put("buy_at", monthlyCryptoReq.getBuyAt());

        Map<String, Object> current = gistService.getGistContent(false, TelegramConst.MONTHLY);
        List<Map<String, Object>> dataList = new ArrayList<>();

        if (current == null || current.isEmpty()) {
            current = new HashMap<>();
        } else {
            Object dataObj = current.get("data");

            if (dataObj instanceof List) {
                dataList = (List<Map<String, Object>>) dataObj;
            } else if (dataObj instanceof Map) {
                dataList.add((Map<String, Object>) dataObj);
            } else if (dataObj == null) {
                dataList = new ArrayList<>();
            }
        }

        dataList.add(gistUpdate);
        current.put("data", dataList);
        gistService.updateGistContent(current, false, TelegramConst.MONTHLY);

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════╗\n");
        sb.append("║ 💠 *MONTHLY CRYPTO UPDATE* 💠 ║\n");
        sb.append("╚════════════════════════════════╝\n\n");

        sb.append(String.format("📌 Symbol       : %s\n", monthlyCryptoReq.getSymbol()));
        sb.append(String.format("💰 Amount       : %.2f USDT\n", monthlyCryptoReq.getAmount()).concat(" ").concat(monthlyCryptoReq.getSymbol()));
        sb.append(String.format("🔄 Converted    : %.4f %s\n", monthlyCryptoReq.getConverted(), monthlyCryptoReq.getSymbol()));
        sb.append(String.format("🛒 Buy Price    : %.2f\n", monthlyCryptoReq.getBuyAt()));
        sb.append(String.format("🏦 Exchange     : %s\n", monthlyCryptoReq.getExchangeName()));
        sb.append(String.format("🌐 Network      : %s\n", monthlyCryptoReq.getNetworkType()));
        sb.append(String.format("⚡ Network Fee  : %.4f %s\n", monthlyCryptoReq.getNetworkFee(), monthlyCryptoReq.getNetworkType()));

        // Optional: progress bar showing investment portion
        double percentage = monthlyCryptoReq.getConverted() / monthlyCryptoReq.getAmount(); // simple example
        int bars = (int) (percentage * 10);
        sb.append("📊 Progress     : [");
        for (int i = 0; i < 10; i++) {
            if (i < bars) sb.append("█");
            else sb.append("░");
        }
        sb.append("] ").append(String.format("%.0f%%\n\n", percentage * 100));

        sb.append("🎯 *Consistency builds wealth. Stay focused!* 🚀\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");


        return sb.toString();
    }

    public List<SummaryCryptoSavingResp> summaryCryptoSavingResp() {
        Map<String, Object> monthlyJson = gistService.getGistContent(false, TelegramConst.MONTHLY);
        if (monthlyJson == null || monthlyJson.isEmpty()) {
            return Collections.emptyList();
        }

        JSONArray dataArray = new JSONObject(monthlyJson).optJSONArray("data");
        if (dataArray == null || dataArray.length() == 0) {
            return Collections.emptyList();
        }

        Map<String, SummaryCryptoSavingResp> summaryMap = new HashMap<>();

        for (Object each : dataArray) {
            JSONObject eachObject = (JSONObject) each;
            String symbol = eachObject.optString("symbol");
            Double amount = eachObject.optDouble("amount", 0.0);
            Double converted = eachObject.optDouble("converted", 0.0);
            String exchangeName = eachObject.optString("exchange", "");

            SummaryCryptoSavingResp summary = summaryMap.getOrDefault(symbol, new SummaryCryptoSavingResp());
            summary.setSymbol(symbol);

            summary.setAmount((summary.getAmount() != null ? summary.getAmount() : 0.0) + amount);
            summary.setConverted((summary.getConverted() != null ? summary.getConverted() : 0.0) + converted);

            String existingExchanges = summary.getExchangeName();
            if (existingExchanges == null || existingExchanges.isEmpty()) {
                summary.setExchangeName(exchangeName);
            } else if (!existingExchanges.contains(exchangeName)) {
                summary.setExchangeName(existingExchanges + ", " + exchangeName);
            }

            summaryMap.put(symbol, summary);
        }

        return new ArrayList<>(summaryMap.values());
    }

    public String formatSummaryForTelegram(List<SummaryCryptoSavingResp> summaryList) {
        if (summaryList == null || summaryList.isEmpty()) {
            return "No crypto data available for this month.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════╗\n");
        sb.append("║ 💠 *MONTHLY CRYPTO SUMMARY* 💠 ║\n");
        sb.append("╚════════════════════════════════════╝\n\n");

// Calculate total amount for portfolio percentage
        double totalAmount = summaryList.stream()
                .mapToDouble(SummaryCryptoSavingResp::getAmount)
                .sum();

// Sort the list descending by amount
        summaryList.sort((a, b) -> Double.compare(b.getAmount(), a.getAmount()));

        for (SummaryCryptoSavingResp summary : summaryList) {
            sb.append(String.format("📌 *Symbol*     : %s\n", summary.getSymbol()));
            sb.append(String.format("💰 *Amount*     : %.2f USDT\n", summary.getAmount()));
            sb.append(String.format("🔄 *Converted*  : %.4f %s\n", summary.getConverted(), summary.getSymbol()));
            sb.append(String.format("🏦 *Exchange*   : %s\n", summary.getExchangeName()));

            // Calculate asset percentage
            double assetPercent = summary.getAmount() / totalAmount;
            int bars = (int) (assetPercent * 10); // 10-bar scale
            sb.append("📊 *Portfolio*  : [");
            for (int i = 0; i < 10; i++) {
                sb.append(i < bars ? "█" : "░");
            }
            sb.append("] ").append(String.format("%.0f%%\n", assetPercent * 100));

            sb.append("───────────────────────────────\n");
        }

        sb.append("🎯 *Keep stacking consistently! 🚀*");

        return sb.toString();
    }

    private String processCommand(String chatId, String command) throws Exception {

        log.info("incoming command {}", command);

        if (command.trim().startsWith("*monthly:")) {
            return this.budgetBreakdown(command, chatId);
        }

        if(command.startsWith("*asset:")) {
            if (!"678134373".equals(chatId)) {
                return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
            } else {
                return this.registerAsset(command);
            }
        }

        if(command.startsWith("/loop")) {
            return this.handleLoopRemindMe(chatId, command);
        }

        switch (command) {
            case "/llist":
                return this.listReminder();

            case "/assets":
                if (!"678134373".equals(chatId)) {
                    return "𝙔𝙤𝙪 𝙝𝙖𝙫𝙚 𝙣𝙤 𝙥𝙧𝙞𝙫𝙞𝙡𝙚𝙜𝙚 𝙩𝙤 𝙪𝙨𝙚 𝙩𝙝𝙞𝙨 𝙘𝙤𝙢𝙢𝙖𝙣𝙙❗";
                } else {
                    return this.formatSummaryForTelegram(summaryCryptoSavingResp());
                }
            //-----------------------------------------------------------------
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

            case "/assettemplate":
                return Formatter.assetRegisterTemplate();

            case "/help":
                return
                    "*🤖 Ｂｏｔ Ｃｏｍｍａｎｄｓ Ｈｅｌｐ*\n\n" +
                    "📅 /calendar \\- Show this week's important events (US)\n" +
                    "💰 /gold \\- Show the real\\-time live price of gold\n" +
                    "🔔 /subscribe \\- Receive alerts and important announcements\n" +
                    "❌ /unsubscribe \\- Stop receiving alerts and announcements\n" +
                    
                    "📊 /budget \\- Check monthly budget breakdown\n" +
                    "⭐ /clsbud \\- Clear monthly budget\n" +
                    
                    "🔁 /llist \\- List recurring alerts\n" +
                    "⏰ /loop \\- Manage looping reminders\n" +
                        "• *Add:* `/loop +10m drink water`\n" +
                        "• *Remove:* `/loop - 1`\n" +
                        "• *Clear:* `/loop *`\n" +
                    "⭐ /assettemplate \\-Get Asset Register Template\n" +
                    "💡 *Tip:* _Use the commands exactly as shown above._\n\n"+
                      MessageConst.getRandomQuote();


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

    public String resetWebhook() {
        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = "https://astrointel.onrender.com/telegram/webhook/reset";
        return restTemplate.getForObject(fullUrl, String.class);
    }
}
