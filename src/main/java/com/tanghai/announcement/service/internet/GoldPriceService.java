package com.tanghai.announcement.service.internet;

import com.tanghai.announcement.constant.TelegramConst;
import com.tanghai.announcement.dto.resp.GoldPrice;
import com.tanghai.announcement.utilz.DateUtilz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class GoldPriceService {

    private final int MAX_SIZE = 30 * 22 * 5; // ~1 month, 22 prices/day, 5 days/week
    private final List<GoldPrice> prices = new ArrayList<>();

    @Autowired
    private GistService gistService;

    @Scheduled(cron = "0 0 */2 * * *", zone = "Asia/Phnom_Penh")
    public void saveGist() {
        showTechnicalAnalysis(false);
    }

    @Scheduled(cron = "0 0 0 L * ?", zone = "Asia/Phnom_Penh")
    public void resetGistPrice() {
        Map<String,Object> xauHistory =  new HashMap<>();
        xauHistory.put("xau_history", new HashMap<>());

        gistService.updateGistContent(xauHistory, true, TelegramConst.PRICE_JSON);
    }

    private Map<String, Object> fetchGistAsMap(Double goldPrice) {
        Map<String, Object> json = gistService.getGistContent(true, TelegramConst.PRICE_JSON);
        if (json == null) json = new HashMap<>();

        Map<String, Object> xauHistory = (Map<String, Object>) json.get("xau_history");
        if (xauHistory == null) xauHistory = new HashMap<>();

        xauHistory.put(DateUtilz.format(new Date()), goldPrice);
        json.put("xau_history", xauHistory);

        return json;
    }

    // Schedule / Auto mode: fetch price, update gist
    public List<GoldPrice> showListSchedulePrices() {
        double newPrice = ForexService.goldApiResp().getPrice();
        GoldPrice gp = new GoldPrice(LocalDateTime.now(), newPrice);

        if (prices.size() >= MAX_SIZE) {
            prices.remove(0);
        }
        prices.add(gp);

        Map<String, Object> priceHistory = fetchGistAsMap(gp.getPrice());
        gistService.updateGistContent(priceHistory, true, TelegramConst.PRICE_JSON);

        return new ArrayList<>(prices); // for analysis
    }

    // Technical Analysis: manual = read-only from gist
    public String showTechnicalAnalysis(boolean manual) {
        List<GoldPrice> calculationPrices = new ArrayList<>();

        if(manual) {
            // Manual: only read from gist
            reloadHistoryFromGist(calculationPrices);
            if (calculationPrices.isEmpty()) {
                return "⚠ No historical data found in gist.";
            }
        } else {
            // Auto: fetch and update
            calculationPrices = showListSchedulePrices();
            reloadHistoryFromGist(calculationPrices);
        }

        GoldPrice first = calculationPrices.get(0); // first index
        GoldPrice last = calculationPrices.get(calculationPrices.size() - 1); // last index

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // --- Short Term (last 4 prices max) ---
        List<GoldPrice> shortTermPoints = calculationPrices.subList(
                Math.max(0, calculationPrices.size() - 4),
                calculationPrices.size()
        );

        String shortTermTime;
        String shortTermGrowth;
        String shortTermTrend = "N/A";

        if (shortTermPoints.size() > 1) {
            GoldPrice shortFirst = shortTermPoints.get(shortTermPoints.size() - 2);
            GoldPrice shortLast = shortTermPoints.get(shortTermPoints.size() - 1);

            shortTermTime = String.format("%s → %s",
                    shortFirst.getTimestamp().format(timeFormatter),
                    shortLast.getTimestamp().format(timeFormatter));

            double growth = ((shortLast.getPrice() - shortFirst.getPrice()) / shortFirst.getPrice()) * 100;
            shortTermGrowth = formatGrowth(growth);

            double slope = calculateSlope(shortTermPoints);
            shortTermTrend = getTrendFromSlope(slope);
        } else {
            GoldPrice onlyPoint = shortTermPoints.get(0);
            shortTermTime = onlyPoint.getTimestamp().format(timeFormatter);
            shortTermGrowth = "N/A";
        }

        // --- Long Term (first -> last index) ---
        double longGrowth = ((last.getPrice() - first.getPrice()) / first.getPrice()) * 100;
        double slope = calculateSlope(calculationPrices);
        String longTermTrend = getTrendFromSlope(slope);

        // --- Build response ---
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
                .append("Growth: ").append(formatGrowth(longGrowth)).append("\n")
                .append("Trend: ").append(longTermTrend).append("\n");

        return sb.toString();
    }

    private void reloadHistoryFromGist(List<GoldPrice> calculationPrices) {
        Map<String, Object> json = gistService.getGistContent(false, TelegramConst.PRICE_JSON);
        if (json != null && json.get("xau_history") != null) {
            Map<String, Object> xauHistory = (Map<String, Object>) json.get("xau_history");
            for (Map.Entry<String, Object> entry : xauHistory.entrySet()) {
                LocalDateTime ts = DateUtilz.parse(entry.getKey());
                double price = Double.parseDouble(entry.getValue().toString());
                calculationPrices.add(new GoldPrice(ts, price));
            }
            calculationPrices.sort(Comparator.comparing(GoldPrice::getTimestamp));
        }
    }

    private String formatGrowth(double growth) {
        return Math.abs(growth) < 0.01 ? String.format("%.4f%%", growth) : String.format("%.2f%%", growth);
    }

    private String getTrendFromSlope(double slope) {
        double threshold = 0.001; // sensitive
        if (slope > threshold) return "Uptrend";
        else if (slope < -threshold) return "Downtrend";
        else return "Sideways";
    }

    private double calculateSlope(List<GoldPrice> points) {
        int n = points.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            double x = i;
            double y = points.get(i).getPrice();
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        return (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
    }
}

