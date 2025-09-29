package com.tanghai.announcement.dto.resp;

public class ForexCalendarResp {
    private String title;
    private String country;
    private String date;
    private String impact;
    private String forecast;
    private String previous;
    private String actual;

    // Constructors
    public ForexCalendarResp() {}

    public ForexCalendarResp(String title, String country, String date,
                             String impact, String forecast, String previous, String actual) {
        this.title = title;
        this.country = country;
        this.date = date;
        this.impact = impact;
        this.forecast = forecast;
        this.previous = previous;
        this.actual = actual;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImpact() {
        return impact;
    }

    public void setImpact(String impact) {
        this.impact = impact;
    }

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) Impact: %s | Forecast: %s | Previous: %s | Actual: %s",
                date, title, country, impact, forecast, previous, actual);
    }
}
