package com.tanghai.announcement.dto.resp;

public class SummaryCryptoSavingResp {
    private String symbol;
    private Double amount;
    private Double converted;
    private String exchangeName;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
}