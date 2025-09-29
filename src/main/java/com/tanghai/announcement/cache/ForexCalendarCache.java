package com.tanghai.announcement.cache;

import com.tanghai.announcement.dto.resp.ForexCalendarResp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ForexCalendarCache {
    private final Map<String, ForexCalendarResp> cache = new ConcurrentHashMap<>();

    private String rawStr = "";

    public void setRawStr(String rawStr) {
        this.rawStr = rawStr;
    }

    public String getRawStr() {
        return rawStr;
    }

    // Build a unique key (title + date should be unique enough)
    private String buildKey(ForexCalendarResp event) {
        return event.getTitle() + "|" + event.getDate();
    }

    public void put(ForexCalendarResp event) {
        cache.put(buildKey(event), event);
    }

    public List<ForexCalendarResp> getAll() {
        return new ArrayList<>(cache.values());
    }

    public List<ForexCalendarResp> getByCountry(String country) {
        return cache.values().stream()
                .filter(e -> e.getCountry().equalsIgnoreCase(country))
                .collect(Collectors.toList());
    }

    public void clear() {
        cache.clear();
    }
}