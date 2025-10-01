package com.tanghai.announcement.service.internet;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.PostConstruct;

@Service
public class GoldPriceWebSocketService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicReference<Double> latestPrice = new AtomicReference<>(null);
    private WebSocketClient client;

    private final String BYBIT_WS_URL = "wss://stream.bybit.com/realtime";

    @PostConstruct
    public void init() {
        // Delay 30 seconds after app startup
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
        try {
            client = new WebSocketClient(new URI(BYBIT_WS_URL)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("✅ Connected to Bybit WebSocket");
                    // Subscribe to XAUUSD ticker
                    send("{\"op\": \"subscribe\", \"args\": [\"tickers.XAUUSD\"]}");
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JsonNode json = objectMapper.readTree(message);
                        if (json.has("data")) {
                            JsonNode data = json.get("data").get(0);
                            double price = data.get("last_price").asDouble();
                            latestPrice.set(price);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("⚠️ WebSocket closed, reconnecting...");
                    scheduleReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                    System.out.println("⚠️ WebSocket error: " + ex.getMessage());
                    scheduleReconnect();
                }
            };

            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            scheduleReconnect();
        }
    }

    private void scheduleReconnect() {
        // Run reconnect in a separate thread with delay
        new Thread(() -> {
            try {
                Thread.sleep(10000); // 10-second delay before reconnect
                connect();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public Double getLatestPrice() {
        return latestPrice.get();
    }
}
