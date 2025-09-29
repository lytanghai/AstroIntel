package com.tanghai.announcement.utilz;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;
import com.tanghai.announcement.dto.resp.GoldApiResp;

import java.util.List;

public class Formatter {

    public static String formatForexCalendar(List<ForexCalendarResp> events) {
        StringBuilder sb = new StringBuilder();

        for (ForexCalendarResp e : events) {
            sb.append("📅 ").append(e.getDate()).append("\n")
                    .append("💡 ").append(e.getTitle()).append("\n")
                    .append("🌐 ").append(e.getCountry()).append(" | Impact: ").append(e.getImpact()).append("\n")
                    .append("📊 Forecast: ").append(e.getForecast() != null ? e.getForecast() : "-")
                    .append(" | Previous: ").append(e.getPrevious() != null ? e.getPrevious() : "-")
                    .append(" | Actual: ").append(e.getActual() != null ? e.getActual() : "-")
                    .append("\n\n");
        }

        return sb.toString().trim();
    }

    public static String formatGoldPrice(GoldApiResp gold) {
        if(gold != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("💰 *").append(gold.getName()).append("*\n")
                    .append("💵 Price: `").append(gold.getPrice()).append(" USD`\n")
                    .append("🔹 Symbol: `").append(gold.getSymbol()).append("`\n")
                    .append("⏱ Updated: ").append(gold.getUpdatedAtReadable());

            return sb.toString();
        } else {
            return null;
        }

    }
}
