package com.tanghai.announcement.utilz;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;
import com.tanghai.announcement.dto.resp.GoldApiResp;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
                impactLevel = "🟠";
            }
            sb.append("📅 ").append(e.getDate()).append("\n")
                    .append("| ចំណងជើង: ").append(e.getTitle()).append("\n")
                    .append("| រូបិយប័ណ្ណ: ").append(e.getCountry()).append("\n")
                    .append("| ផលប៉ះពាល់: ").append(e.getImpact()).append(" ").append(impactLevel).append("\n")
                    .append("| ទិន្ន័យចាស់: ").append(e.getPrevious() != null ? e.getPrevious() : "-").append("\n")
                    .append("| ការទស្សន៍ទាយ: ").append(e.getForecast() != null ? e.getForecast() : "-")
                    .append("\n\n\n");
        }
        return sb.toString().trim();
    }

    static double calculateToLocalPrice(double price) {
        return price * 1.2;
    }

    public static String autoAlertGoldPrice(GoldApiResp gold, double previous) {
        if(gold != null) {
            double calculateAvg30MinPrice = gold.getPrice() - previous;
            DecimalFormat df = new DecimalFormat("#.####"); // Pattern for up to 4 decimal places
            df.setRoundingMode(RoundingMode.HALF_UP); // Set rounding mode (e.g., half up)
            String formattedValue = df.format(calculateAvg30MinPrice);
            String trendType = "sideway (~)";
            if(calculateAvg30MinPrice < 0) {
                trendType = "bearish (-)";
            } else if(calculateAvg30MinPrice > 0) {
                trendType = "bullish (+)" ;
            }

            return  "🏆 *Gold Market Update* 🏆\n\n" +
                    "💰 Current Price: " + gold.getPrice().toString().substring(0,8) + " USD/oz\n" +
                    "💱 ≈ " + calculateToLocalPrice(gold.getPrice()) + " ដុល្លារ/តម្លឹង\n" +
                    "📈 30-Min Change: " + formattedValue + " pts " + trendType + "\n" +
                    "🔥 Stay alert — market is " + (trendType.contains("Bullish") ? "🟢 heating up!" : "🔴 cooling down!");


//            return  "🔥" + " Updated: Gold [XAU] \n" +
//                    "តម្លៃបច្ចុប្បន្ន: " + gold.getPrice().toString().substring(0,8) +
//                    "/អោន ≈ "
//                    + calculateToLocalPrice(gold.getPrice())
//                    + "ដុល្លារ/តម្លឹង \n"
//                    +"🔥 Average Change (30min): "
//                    + formattedValue + " Points " + trendType;
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
