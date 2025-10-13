package com.tanghai.announcement.service;

import com.tanghai.announcement.dto.req.SupportResistance;
import com.tanghai.announcement.dto.req.SupportResistanceReq;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OperatorService {

    private final Map<String, Object> supportResistant = new ConcurrentHashMap<>();

    public void drawSupportResistance(SupportResistance req) {

        if (req.getResistances() != null && !req.getResistances().isEmpty()) {
            List<Double> sortedResistances = new ArrayList<>(req.getResistances());
            Collections.sort(sortedResistances);
            for (int i = 0; i < sortedResistances.size(); i++) {
                supportResistant.put("R" + (i + 1), sortedResistances.get(i));
            }
        }

        if (req.getSupports() != null && !req.getSupports().isEmpty()) {
            List<Double> sortedSupports = new ArrayList<>(req.getSupports());
            Collections.sort(sortedSupports);
            for (int i = 0; i < sortedSupports.size(); i++) {
                supportResistant.put("S" + (i + 1), sortedSupports.get(i));
            }
        }

    }

    public SupportResistanceReq getSupportResistanceReq() {
        SupportResistanceReq req = new SupportResistanceReq();
        req.setR1((Double) supportResistant.get("R1"));
        req.setR2((Double) supportResistant.get("R2"));
        req.setR3((Double) supportResistant.get("R3"));
        req.setS1((Double) supportResistant.get("S1"));
        req.setS2((Double) supportResistant.get("S2"));
        req.setS3((Double) supportResistant.get("S3"));
        return req;
    }

    public void clear() {
        supportResistant.clear();
    }
}
