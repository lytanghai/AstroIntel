package com.tanghai.announcement.dto.resp;

import java.time.LocalDateTime;

public class GoldPrice {
    private LocalDateTime timestamp;
    private double price;

    public GoldPrice(LocalDateTime timestamp, double price) {
        this.timestamp = timestamp;
        this.price = price;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public double getPrice() { return price; }
}
