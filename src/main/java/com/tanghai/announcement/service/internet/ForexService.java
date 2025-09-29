package com.tanghai.announcement.service.internet;

import com.tanghai.announcement.cache.ForexCalendarCache;
import com.tanghai.announcement.dto.resp.ForexCalendarResp;
import com.tanghai.announcement.dto.resp.GoldApiResp;
import com.tanghai.announcement.utilz.DateUtilz;
import com.tanghai.announcement.utilz.ExternalAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ForexService {

    private static final ForexCalendarCache cache = new ForexCalendarCache();
    private static final Logger log = LoggerFactory.getLogger(ForexService.class);

    public static List<ForexCalendarResp> economicCalendar() {
        // 1️⃣ Return from cache if available
        if (!cache.getAll().isEmpty()) {
            System.out.println("from cache");
            return cache.getAll();
        }

        List<ForexCalendarResp> responseList = new ArrayList<>();
        String current = DateUtilz.format(new Date(), "yyyy-MM-dd");

        try {
            // 2️⃣ Check if economic.json exists in resources
            ClassPathResource resource = new ClassPathResource("economic.json");
            if (resource.exists()) {
                System.out.println("Loading from resources/economic.json");
                String jsonStr = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
                JSONArray result = new JSONArray(jsonStr);

                for (Object i : result) {
                    JSONObject each = (JSONObject) i;
                    if (each.optString("date").substring(0, 10).equals(current) &&
                            each.optString("country").equals("USD")) {

                        ForexCalendarResp eachResp = new ForexCalendarResp();
                        eachResp.setDate(DateUtilz.toPhnomPenhTime(each.optString("date").replaceAll("ICT", "")));
                        eachResp.setCountry(each.optString("country"));
                        eachResp.setForecast(each.optString("forecast"));
                        eachResp.setImpact(each.optString("impact"));
                        eachResp.setTitle(each.optString("title"));
                        eachResp.setPrevious(each.optString("previous"));
                        eachResp.setActual(each.optString("actual", null));

                        responseList.add(eachResp);
                        cache.put(eachResp);
                    }
                }
                return responseList;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to read economic.json, fetching from API...");
        }

        // 3️⃣ If no JSON file or error, fallback to API
        try {
            RestTemplate restTemplate = new RestTemplate();
//            String resultStr = restTemplate.getForObject(ExternalAPI.CALENDAR_ECONOMIC, String.class);
            String resultStr = "";
            JSONArray result = new JSONArray(resultStr);

            for (Object i : result) {
                JSONObject each = (JSONObject) i;
                if (each.optString("date").substring(0, 10).equals(current) &&
                        each.optString("country").equals("USD")) {

                    ForexCalendarResp eachResp = new ForexCalendarResp();
                    eachResp.setDate(DateUtilz.toPhnomPenhTime(each.optString("date").replaceAll("ICT", "")));
                    eachResp.setCountry(each.optString("country"));
                    eachResp.setForecast(each.optString("forecast"));
                    eachResp.setImpact(each.optString("impact"));
                    eachResp.setTitle(each.optString("title"));
                    eachResp.setPrevious(each.optString("previous"));
                    eachResp.setActual(each.optString("actual", null));

                    responseList.add(eachResp);
                    cache.put(eachResp);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseList;
    }

    public static GoldApiResp goldApiResp() {
        RestTemplate restTemplate = new RestTemplate();
        GoldApiResp resultStr = restTemplate.getForObject(ExternalAPI.GOLD_PRICE, GoldApiResp.class);

        if(resultStr != null) {
            resultStr.setUpdatedAt(DateUtilz.toPhnomPenhTime(resultStr.getUpdatedAt()).replaceAll("ICT", ""));
            return resultStr;
        } else {
            log.info("result is null");
            return null;
        }
    }
}
