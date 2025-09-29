package com.tanghai.announcement.utilz;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtilz {

    public static final String DATE_WITH_TIME_1 = "dd-MM-yyyy HH:mm:ss";

    public static final String DATE_FORMAT_3 = "yyyy-MM-dd";

    public static String toPhnomPenhTime(String input) {
        ZonedDateTime sourceDateTime = ZonedDateTime.parse(input);

        // Convert to Asia/Phnom_Penh time zone
        ZonedDateTime phnomPenhTime = sourceDateTime.withZoneSameInstant(ZoneId.of("Asia/Phnom_Penh"));

        // Format output
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return phnomPenhTime.format(formatter);
    }

    public static Date convertToDateWithMidnight(String dateStr, Boolean includeHour) {
        DateTimeFormatter formatter;

        if(includeHour) {
            formatter = DateTimeFormatter.ofPattern(DATE_WITH_TIME_1);
        } else {
            formatter = DateTimeFormatter.ofPattern(DATE_FORMAT_3);
        }

        // Parse to LocalDate
        LocalDate localDate = LocalDate.parse(dateStr, formatter);

        // Convert to java.util.Date at 00:00:00 in Asia/Phnom_Penh
        return Date.from(localDate.atStartOfDay(ZoneId.of("Asia/Phnom_Penh")).toInstant());
    }

    public static String format(Date date) {
        return format(date, DATE_WITH_TIME_1);
    }


    public static String format(Date date, String format) {
        return format(date, format, null);
    }

    public static String format(Date date, String format, String defaultValue) {
        return date == null ? defaultValue : new SimpleDateFormat(format).format(date);
    }
}
