package com.cqq.stock.entity;

import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class EMAStock {

    private StockTransactionInfo stock;
    private Map<Integer, Double> emaValue;
    private Map<Pair<Integer, Integer>, Double> difValue;
    private Map<Pair<Integer, Integer>, Double> deaValue;
    private double macd;

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public EMAStock(StockTransactionInfo stock) {
        emaValue = new HashMap<>();
        difValue = new HashMap<>();
        deaValue = new HashMap<>();
        this.stock = stock;
    }

    public Double getDifValue(int shortDay, int longDay) {
        return difValue.get(new Pair<>(shortDay, longDay));
    }

    public void putDifValue(int shortDay, int longDay, double value) {
        difValue.put(new Pair<>(shortDay, longDay), value);
    }

    public Double getDeaValue(int shortDay, int longDay) {
        return deaValue.get(new Pair<>(shortDay, longDay));
    }

    public void putDeaValue(int shortDay, int longDay, double value) {
        deaValue.put(new Pair<>(shortDay, longDay), value);
    }

    public Double getEmaValue(int n) {
        return emaValue.get(n);
    }

    public void putEmaValue(int n, double value) {
        emaValue.put(n, value);
    }

    public Long getDate() {
        return stock.getDate();
    }

    public String getCode() {
        return stock.getCode();
    }

    public void setCode(String code) {
        stock.setCode(code);
    }


    public Long getClose() {
        return stock.getClose();
    }

    @Override
    public String toString() {
        return "EMAStock{" +
                "stock=" + stock +
                ", emaValue=" + emaValue +
                ", difValue=" + difValue +
                ", deaValue=" + deaValue +
                ", macd=" + macd +
                '}';
    }
}
