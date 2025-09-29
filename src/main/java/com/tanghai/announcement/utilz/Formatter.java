package com.tanghai.announcement.utilz;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;

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

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
