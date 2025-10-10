package com.tanghai.announcement.service;

import com.tanghai.announcement.dto.req.SupportResistanceReq;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OperatorService {

    private final Map<String, Object> supportResistant = new ConcurrentHashMap<>();

    public SupportResistanceReq drawSupportResistance(SupportResistanceReq req) {
        if (req != null) {
            if (req.getR1() != null) supportResistant.put("R1", req.getR1());
            if (req.getR2() != null) supportResistant.put("R2", req.getR2());
            if (req.getR3() != null) supportResistant.put("R3", req.getR3());
            if (req.getS1() != null) supportResistant.put("S1", req.getS1());
            if (req.getS2() != null) supportResistant.put("S2", req.getS2());
            if (req.getS3() != null) supportResistant.put("S3", req.getS3());
        }
        return req;
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

    public void put(String key, Object value) {
        supportResistant.put(key, value);
    }

    public Object get(String key) {
        return supportResistant.get(key);
    }

    public boolean contain(String key) {
        return supportResistant.containsKey(key);
    }

    public void clear() {
        supportResistant.clear();
    }
}
