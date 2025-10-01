package com.tanghai.announcement.service.internet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tanghai.announcement.component.TelegramSender;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class GoldPriceWebSocketService {

    private static final Logger log = LoggerFactory.getLogger(GoldPriceWebSocketService.class);
    private static final String BYBIT_WS_URL = "wss://stream.bybit.com/realtime_public";

    private final GistService gistService;
    private final OkHttpClient client;
    private WebSocket webSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicReference<String> latestPrice = new AtomicReference<>("0");

    private final TelegramSender telegramSender; // your existing service

    public GoldPriceWebSocketService(GistService gistService, TelegramSender telegramSender) {
        this.gistService = gistService;
        this.telegramSender = telegramSender;
        this.client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // keep alive
                .build();
    }

    @PostConstruct
    public void startWebSocket() {
        // Delay startup 30s to ensure app fully initialized
        new Thread(() -> {
            try {
                Thread.sleep(30000);
                connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void connect() {
        Request request = new Request.Builder().url(BYBIT_WS_URL).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                log.info("✅ Connected to Bybit WS");
                subscribeXAUUSD();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JsonNode node = objectMapper.readTree(text);
                    if (node.has("topic") && node.get("topic").asText().startsWith("tickers")) {
                        String price = node.get("data").get(0).get("last_price").asText();
                        latestPrice.set(price);
                        log.info("Gold Price: {}", price);
                    }
                } catch (Exception e) {
                    log.error("Failed to parse WS message", e);
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                log.error("WebSocket failure: {}. Reconnecting in 5s...", t.getMessage());
                reconnectWithDelay();
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                log.warn("WebSocket closed: {} - {}. Reconnecting in 5s...", code, reason);
                reconnectWithDelay();
            }
        });
    }

    private void reconnectWithDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
                connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void subscribeXAUUSD() {
        String subscribeJson = "{\"op\":\"subscribe\",\"args\":[\"tickers.XAUUSD\"]}";
        webSocket.send(subscribeJson);
        log.info("Subscribed to XAUUSD ticker");
    }

    @Scheduled(fixedRate = 120000) // every 2 minutes
    public void sendGoldPriceAlert() {
        String price = latestPrice.get();
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