package com.tanghai.announcement.service.internet;

import com.tanghai.announcement.dto.resp.GoldPrice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class GoldPriceService {

    private final int MAX_SIZE = 30 * 22 * 5; // 1 month approx, 22 prices/day, 5 days/week
    private final List<GoldPrice> prices = new ArrayList<>();

    private double fetchGoldPrice() {
        return ForexService.goldApiResp().getPrice();
    }

    public String showTechnicalAnalysis() {
        // 1️⃣ Fetch the new price
        double newPrice = fetchGoldPrice(); // your API or mock function
        GoldPrice gp = new GoldPrice(LocalDateTime.now(), newPrice);

        // 2️⃣ Keep array size within MAX_SIZE
        if (prices.size() >= MAX_SIZE) {
            prices.remove(0); // remove oldest
        }
        prices.add(gp); // add newest price

        // 3️⃣ SHORT TERM calculation
        GoldPrice last = gp;
        GoldPrice prev = prices.size() > 1 ? prices.get(prices.size() - 2) : null;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String shortTermTime;
        String shortTermGrowth;
        String shortTermTrend = "N/A";

        if (prev != null) {
            shortTermTime = String.format("%s → %s",
                    prev.getTimestamp().format(timeFormatter),
                    last.getTimestamp().format(timeFormatter));
            double growth = ((last.getPrice() - prev.getPrice()) / prev.getPrice()) * 100;
            shortTermGrowth = String.format("%.2f%%", growth);

            // Trend using last 10 prices
            int n = Math.min(10, prices.size());
            double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
            for (int i = 0; i < n; i++) {
                double x = i;
                double y = prices.get(prices.size() - n + i).getPrice();
                sumX += x;
                sumY += y;
                sumXY += x * y;
                sumX2 += x * x;
            }
            double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
            double threshold = 0.01;
            if (slope > threshold) shortTermTrend = "Uptrend";
            else if (slope < -threshold) shortTermTrend = "Downtrend";
            else shortTermTrend = "Sideways";

        } else {
            shortTermTime = last.getTimestamp().format(timeFormatter);
            shortTermGrowth = "N/A";
        }

        // 4️⃣ LONG TERM calculation
        GoldPrice first = prices.get(0);
        double longGrowth = ((last.getPrice() - first.getPrice()) / first.getPrice()) * 100;

        // Trend over full stored list
        int n = prices.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = prices.get(i).getPrice();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double threshold = 0.01;
        String longTermTrend;
        if (slope > threshold) longTermTrend = "Uptrend";
        else if (slope < -threshold) longTermTrend = "Downtrend";
        else longTermTrend = "Sideways";

        // 5️⃣ Build message
        StringBuilder sb = new StringBuilder();
        sb.append("📊 Technical Analysis\n\n");

        sb.append("Short Term\n")
                .append("Time: ").append(shortTermTime).append("\n")
                .append("Price: ").append(String.format("%.2f USD", last.getPrice())).append("\n")
                .append("Growth: ").append(shortTermGrowth).append("\n")
                .append("Trend: ").append(shortTermTrend).append("\n")
                .append("----------------------------\n");

        sb.append("Long Term\n")
                .append("Date: ").append(first.getTimestamp().format(dateFormatter))
                .append(" → ").append(last.getTimestamp().format(dateFormatter)).append("\n")
                .append("Previous Price: ").append(String.format("%.2f", first.getPrice()))
                .append(" - Current Price: ").append(String.format("%.2f", last.getPrice())).append("\n")
                .append("Growth: ").append(String.format("%.2f%%", longGrowth)).append("\n")
                .append("Trend: ").append(longTermTrend).append("\n");

        return sb.toString();
    }

}
