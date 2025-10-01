package com.tanghai.announcement.service.internet;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoldPriceWebSocketService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicReference<Double> latestPrice = new AtomicReference<>(null);
    private WebSocketClient client;

    public GoldPriceWebSocketService() {
        connect();
    }

    private void connect() {
        try {
            client = new WebSocketClient(new URI("wss://stream.bybit.com/realtime")) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to Bybit WebSocket");
                    // Subscribe to XAUUSD ticker
                    send("{\"op\": \"subscribe\", \"args\": [\"tickers.XAUUSD\"]}");
                }

                @Override
                public void onMessage(String message) {
                    try {
                        JsonNode json = objectMapper.readTree(message);
                        System.out.println("Fetched");
                        if (json.has("data")) {
                            JsonNode data = json.get("data").get(0);
                            double price = data.get("last_price").asDouble();
                            latestPrice.set(price);
                            // System.out.println("Latest XAU/USD: " + price);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("WebSocket closed, reconnecting...");
                    new Thread(() -> {
                        try {
                            Thread.sleep(2000); // optional: small delay before reconnect
                            connect(); // your method that creates a new WebSocketClient
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Double getLatestPrice() {
        return latestPrice.get();
    }
}
