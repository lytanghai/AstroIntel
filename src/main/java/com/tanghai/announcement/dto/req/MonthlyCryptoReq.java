package com.tanghai.announcement.dto.req;

public class MonthlyCryptoReq {
    private String symbol; //XAUT
     private String exchangeName; //Binance, OKX
     private Double amount; // 50
     private Double converted; // 0.11121
     private Double buyAt; //4000.0
     private String networkType; //POLYGON
     private Double networkFee; //0.11

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getConverted() {
        return converted;
    }

    public void setConverted(Double converted) {
        this.converted = converted;
    }

    public Double getBuyAt() {
        return buyAt;
    }

    public void setBuyAt(Double buyAt) {
        this.buyAt = buyAt;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public Double getNetworkFee() {
        return networkFee;
    }

    public void setNetworkFee(Double networkFee) {
        this.networkFee = networkFee;
    }
}