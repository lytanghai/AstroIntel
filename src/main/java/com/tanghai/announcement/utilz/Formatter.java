package com.tanghai.announcement.utilz;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;

import java.util.List;

public class Formatter {

    public static String formatForexCalendar(List<ForexCalendarResp> events) {
        StringBuilder sb = new StringBuilder();
        sb.append("```\n"); // start monospace block

        // Header
        sb.append(String.format("%-20s %-30s %-7s %-8s %-8s %-8s %-8s\n",
                "Date", "Title", "Country", "Impact", "Forecast", "Previous", "Actual"));
        sb.append("--------------------------------------------------------------------------------\n");

        // Rows
        for (ForexCalendarResp e : events) {
            sb.append(String.format("%-20s %-30s %-7s %-8s %-8s %-8s %-8s\n",
                    e.getDate(),
                    truncate(e.getTitle(), 30),
                    e.getCountry(),
                    e.getImpact(),
                    e.getForecast() != null ? e.getForecast() : "",
                    e.getPrevious() != null ? e.getPrevious() : "",
                    e.getActual() != null ? e.getActual() : "null"
            ));
        }

        sb.append("```"); // end monospace block
        return sb.toString();
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}
