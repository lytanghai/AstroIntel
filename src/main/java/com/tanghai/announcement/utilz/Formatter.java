package com.tanghai.announcement.utilz;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;
import com.tanghai.announcement.dto.resp.GoldApiResp;

import java.util.List;

public class Formatter {

    public static String formatForexCalendar(List<ForexCalendarResp> events) {
        StringBuilder sb = new StringBuilder();
        for (ForexCalendarResp e : events) {
            String impactLevel = "";
            if(e.getImpact().equals("Low")) {
                impactLevel = "🟢";
            } else if(e.getImpact().equals("High")) {
                impactLevel = "🔴" ;
            } else {
                impactLevel =  "🟠";
            }
            sb.append("📅 ").append(e.getDate()).append("\n")
                    .append("💡 ").append(e.getTitle()).append("\n")
                    .append("🌐 ").append(e.getCountry()).append("\n")
                    .append(" | Impact: ").append(e.getImpact()).append(" ").append(impactLevel).append("\n")
                    .append(" | Previous: ").append(e.getPrevious() != null ? e.getPrevious() : "-")
                    .append("📊 Forecast: ").append(e.getForecast() != null ? e.getForecast() : "-")
                    .append("\n\n");
        }
        return sb.toString().trim();
    }

    static double calculateToLocalPrice(double price) {
        return price * 1.2;
    }

    public static String autoAlertGoldPrice(GoldApiResp gold, double previous) {
        if(gold != null) {
            double calculateAvg30MinPrice = gold.getPrice() - previous;
            String trendType = "(Sideway)";
            if(calculateAvg30MinPrice < 0) {
                trendType = "(Negative)";
            } else if(calculateAvg30MinPrice > 0) {
                trendType = "(Positive)";
            }
            return  "🔥" + " Updated: Gold[XAU] \n" +
                    "តម្លៃបច្ចុប្បន្ន: " + gold.getPrice().toString() +
                    "/អោន ≈ "
                    + calculateToLocalPrice(gold.getPrice())
                    + "ដុល្លារ/តម្លឹង \n"
                    +"🔥 Average Change (30min): "
                    + calculateAvg30MinPrice + " Points " + trendType;
        }
        return null;
    }

    public static String formatGoldPrice(GoldApiResp gold) {
        if(gold != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("💰 *")
            .append(gold.getName())
            .append("|")
            .append(gold.getSymbol()).append("*\n")
            .append("💵 Price: `").append(gold.getPrice()).append(" USD`\n")
            .append("⏱ Last Updated: ").append(gold.getUpdatedAt());

            return sb.toString();
        } else {
            return null;
        }
    }
}
