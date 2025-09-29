package com.tanghai.announcement.dto.resp;

public class GoldApiResp {
    private String name;
    private Double price;
    private String symbol;
    private String updatedAt;
    private String updatedAtReadable;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAtReadable() {
        return updatedAtReadable;
    }

    public void setUpdatedAtReadable(String updatedAtReadable) {
        this.updatedAtReadable = updatedAtReadable;
    }

    @Override
    public String toString() {
        return "GoldApiResp{" +
                "name='" + name + '\'' +
                ", price=" + price +
                ", symbol='" + symbol + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", updatedAtReadable='" + updatedAtReadable + '\'' +
                '}';
    }
}
