package com.tanghai.announcement.cache;

public class MonthlyReserveCache {
    private static String lastBudgetMessage;

    public static void save(String message) {
        lastBudgetMessage = message;
    }

    public static String get() {
        return lastBudgetMessage;
    }

    public static boolean hasCache() {
        return lastBudgetMessage != null;
    }

    public static void clear() {
        lastBudgetMessage = null;
    }
}
