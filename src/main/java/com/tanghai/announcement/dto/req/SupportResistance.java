package com.tanghai.announcement.dto.req;

import java.util.List;

public class SupportResistance {

    private List<Double> supports;
    private List<Double> resistances;

    public List<Double> getSupports() {
        return supports;
    }

    public void setSupports(List<Double> supports) {
        this.supports = supports;
    }

    public List<Double> getResistances() {
        return resistances;
    }

    public void setResistances(List<Double> resistances) {
        this.resistances = resistances;
    }
}
