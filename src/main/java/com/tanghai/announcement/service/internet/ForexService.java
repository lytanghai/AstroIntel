package com.tanghai.announcement.service.internet;

import com.tanghai.announcement.cache.ForexCalendarCache;
import com.tanghai.announcement.dto.resp.ForexCalendarResp;
import com.tanghai.announcement.utilz.DateUtilz;
import com.tanghai.announcement.utilz.ExternalAPI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ForexService {

    private static final ForexCalendarCache cache = new ForexCalendarCache();

    public static List<ForexCalendarResp> economicCalendar() {

        if (!cache.getAll().isEmpty()) {
            System.out.println("from cache");
            return cache.getAll();
        }

        RestTemplate restTemplate = new RestTemplate();

        String resultStr = restTemplate.getForObject(ExternalAPI.CALENDAR_ECONOMIC, String.class);
        JSONArray result = new JSONArray(resultStr);

        List<ForexCalendarResp> responseList = new ArrayList<>();

        String current = DateUtilz.format(new Date(), "yyyy-MM-dd");
        for (Object i : result) {
            JSONObject each = (JSONObject) i;
            ForexCalendarResp eachResp = new ForexCalendarResp();

            if (each.optString("date").substring(0,10).equals(current) && each.optString("country").equals("USD")) {
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

}
