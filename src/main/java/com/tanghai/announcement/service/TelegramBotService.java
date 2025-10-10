package com.tanghai.announcement.service;

import com.tanghai.announcement.component.TelegramSender;
import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.service.internet.ForexService;
import com.tanghai.announcement.service.internet.GistService;
import com.tanghai.announcement.service.internet.GoldPriceService;
import com.tanghai.announcement.utilz.Formatter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.Map;

@Service
public class TelegramBotService {

    private final TelegramSender telegramSender;
    private ForexService forexService;
    private final GistService gistService;
    private final GoldPriceService goldPriceService;
    private final GeminiApiService aiService;

    private Map<String, Boolean> waitingForPrompt = new HashMap<>();

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

    private String proceedNextStep(String chatId, String prompt) throws Exception {

        if (waitingForPrompt.getOrDefault(chatId, false)) {

            waitingForPrompt.put(chatId, false); // clear waiting flag
            try {
                return "Respond: " + aiService.generateText(prompt);
            } catch (Exception e) {
                waitingForPrompt.put(chatId, true); // allow retry if AI call fails
                return "⚠️ Failed to generate AI response: "  + e.getMessage();
            }
        } else {
            return "Invalid Command!!!";
        }
    }
    private String getPrompt(boolean requirePermission, String chatId, String prompt) throws Exception {
        if (!"678134373".equals(chatId)) {
            return "❌ You have no privilege to use this command!";
        }
        if(requirePermission) {
            // STEP 1: User typed "/ask" (start of the flow)
            if (prompt == null || prompt.isEmpty() || "/ask".equalsIgnoreCase(prompt)) {
                waitingForPrompt.put(chatId, true);
                return "💬 Please enter your prompt for the AI: ";
            }
            // Default fallback
            return "❓ Unknown input. Use /ask to send a prompt to AI.";
        } else {
            return aiService.generateText(prompt);
        }

    }
    private String processCommand(String chatId, String command) throws Exception {

        if(!command.contains("/")) {
            return getPrompt(false ,chatId, command);
        }

        switch (command) {
            // command ask first before prompt
//            case "/ask":
//                return getPrompt(true ,chatId, command);

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

            default:
                return proceedNextStep(chatId,command);
        }
    }
}
