package com.tanghai.announcement.component;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class BybitXauUsdClient extends WebSocketClient {

    public BybitXauUsdClient() {
        super(URI.create("wss://stream.bybit.com/realtime_public"));
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to Bybit");
        send("{\"op\": \"subscribe\", \"args\": [\"tickers.XAUUSD\"]}");
    }

    @Override
    public void onMessage(String message) {
        if (message.contains("\"topic\":\"tickers.XAUUSD\"")) {
            try {
                // Extract last_price (simplified parsing)
                String price = message.split("\"last_price\":\"")[1].split("\"")[0];
                System.out.println("XAU/USD Price: " + price);
            } catch (Exception e) {
                System.out.println("Parsing update: " + message);
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error: " + ex.getMessage());
    }
}   