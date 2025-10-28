package com.tanghai.announcement.utilz;

import com.tanghai.announcement.constant.MessageConst;
import com.tanghai.announcement.dto.req.SupportResistanceReq;
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
            if(e.getImpact().equals("𝙇𝙊𝙒")) {
                impactLevel = "🟢";
            } else if(e.getImpact().equals("𝙃𝙄𝙂𝙃")) {
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

    public static String autoAlertGoldPrice(GoldApiResp gold, double previous, SupportResistanceReq sr) {
        if (gold == null) return "⚠️ No gold price data available.";

        double currentPrice = gold.getPrice();
        double r1 = safeGet(sr != null ? sr.getR1() : null);
        double r2 = safeGet(sr != null ? sr.getR2() : null);
        double r3 = safeGet(sr != null ? sr.getR3() : null);
        double s1 = safeGet(sr != null ? sr.getS1() : null);
        double s2 = safeGet(sr != null ? sr.getS2() : null);
        double s3 = safeGet(sr != null ? sr.getS3() : null);

        // Calculate differences from previous 30min
        double diff30 = currentPrice - previous;

        DecimalFormat df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.HALF_UP);
        String formattedDiff = df.format(diff30);

        String trendType = diff30 > 0 ? "🚀 𝘽𝙐𝙇𝙇𝙄𝙎𝙃 🚀" : diff30 < 0 ? "❗𝘽𝙀𝘼𝙍𝙄𝙎𝙃❗" : "🔎 𝙎𝙄𝘿𝙀𝙒𝘼𝙔 🔎";

        // Calculate distances to S/R (optional)
        double dR1 = r1 > 0 ? r1 - currentPrice : 0;
        double dR2 = r2 > 0 ? r2 - currentPrice : 0;
        double dR3 = r3 > 0 ? r3 - currentPrice : 0;
        double dS1 = s1 > 0 ? currentPrice - s1 : 0;
        double dS2 = s2 > 0 ? currentPrice - s2 : 0;
        double dS3 = s3 > 0 ? currentPrice - s3 : 0;

        return  "✨ 𝙐𝙋𝘿𝘼𝙏𝙀 តម្លៃទីផ្សារមាស ✨\n"
                + "ᯓ★ Current Price: \n" + currentPrice
                + " USD/oz ≈ " + calculateToLocalPrice(currentPrice) + "$/តម្លឹង\n"
                + "⏱️ 30-min Change: \n" + formattedDiff + " pts → " + trendType + "\n\n"
                + "📊 𝙎𝙪𝙥𝙥𝙤𝙧𝙩 𝙇𝙚𝙫𝙚𝙡𝙨 \n"
                + "𝙎𝙪𝙥𝙥𝙤𝙧𝙩 1: " + s1 + " (" + formatDiff(dS1) + ")\n"
                + "𝙎𝙪𝙥𝙥𝙤𝙧𝙩 2: " + s2 + " (" + formatDiff(dS2) + ")\n"
                + "𝙎𝙪𝙥𝙥𝙤𝙧𝙩 3: " + s3 + " (" + formatDiff(dS3) + ")\n\n"
                + "📈 𝙍𝙚𝙨𝙞𝙨𝙩𝙖𝙣𝙘𝙚 𝙇𝙚𝙫𝙚𝙡𝙨 \n"
                + "𝙍𝙚𝙨𝙞𝙨𝙩𝙖𝙣𝙘𝙚 1: " + r1 + " (" + formatDiff(dR1) + ")\n"
                + "𝙍𝙚𝙨𝙞𝙨𝙩𝙖𝙣𝙘𝙚 2: " + r2 + " (" + formatDiff(dR2) + ")\n"
                + "𝙍𝙚𝙨𝙞𝙨𝙩𝙖𝙣𝙘𝙚 3: " + r3 + " (" + formatDiff(dR3) + ")\n\n"
                + MessageConst.getRandomQuote();

    }

    private static double safeGet(Double value) {
        return value != null ? value : 0.0;
    }

    private static String formatDiff(double value) {
        if (value == 0) return "—";
        return (value > 0 ? "+" : "") + String.format("%.2f", value);
    }


    public static String formatGoldPrice(GoldApiResp gold) {
        if(gold != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("💰 *")
            .append(gold.getName())
            .append("|")
            .append(gold.getSymbol()).append("*\n")
            .append("𝙋𝙍𝙄𝘾𝙀: `").append(gold.getPrice()).append( "𝙐𝙎𝘿`\n")
            .append("𝙇𝙖𝙨𝙩 𝙐𝙥𝙙𝙖𝙩𝙚𝙙: ").append(gold.getUpdatedAt()).append("\n\n")
            .append(MessageConst.getRandomQuote());

            return sb.toString();
        } else {
            return null;
        }
    }

    public static String assetRegisterTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("*asset:").append("\n")
        .append("amount: ").append("\n")
        .append("converted: ").append("\n")
        .append("symbol: ").append("\n")
        .append("exchange: ").append("\n")
        .append("network_type: ").append("\n")
        .append("network_fee: ").append("\n")
        .append("buy_at: ").append("\n");
        return sb.toString();
    }
}
