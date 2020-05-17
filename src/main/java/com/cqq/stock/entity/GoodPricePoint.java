package com.cqq.stock.entity;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 好的买卖点
 */
public class GoodPricePoint implements Serializable {
    private String code;
    private double buyPrice;
    private double salePrice;
    private double lastPrice;

    /**
     * 赚钱累计花费的时间(天）
     */

    private int earnLastTime;
    /**
     * 赚钱累计百分比
     */
    private double allEarnValue;
    /**
     * 累计CCI低买高卖次数
     */
    private int buySaleTime;
    /**
     * 其中赚钱的次数
     */
    private int earnTime;

    /**
     * 平均赚钱金额
     */
    private double earnValueEachDay;

    private int consecutive60DaysOfAverageRise;

    public double getAllEarnValue() {
        return allEarnValue;
    }

    public void setAllEarnValue(double allEarnValue) {
        this.allEarnValue = allEarnValue;
    }

    public int getEarnLastTime() {
        return earnLastTime;
    }

    public void setEarnLastTime(int earnLastTime) {
        this.earnLastTime = earnLastTime;
    }


    public int getBuySaleTime() {
        return buySaleTime;
    }

    public void setBuySaleTime(int buySaleTime) {
        this.buySaleTime = buySaleTime;
    }

    public int getEarnTime() {
        return earnTime;
    }

    public void setEarnTime(int earnTime) {
        this.earnTime = earnTime;
    }

    public List<String> getHint() {
        return hint;
    }

    public void setHint(List<String> hint) {
        this.hint = hint;
    }

    public int getConsecutive60DaysOfAverageRise() {
        return consecutive60DaysOfAverageRise;
    }

    public void setConsecutive60DaysOfAverageRise(int consecutive60DaysOfAverageRise) {
        this.consecutive60DaysOfAverageRise = consecutive60DaysOfAverageRise;
    }

    public double getLastCci() {
        return lastCci;
    }

    public void setLastCci(double lastCci) {
        this.lastCci = lastCci;
    }

    private double lastCci;

    private List<String> hint = new ArrayList<>();

    @Override
    public String toString() {
        String format = "GoodPricePoint{code=%s,buyPrice=%.3f,salePrice=%.3f,lastPrice=%.3f,lastCCI=%.3f,consecutive60DaysOfAverageRise=%d,"
                + "time=%d,earnLastTime=%d天,allEarnValue=%.3f,earnTime=%d,avgValue=%.0f百分点, successRate=%.3f}%s";
        return String.format(format, code, buyPrice, salePrice, lastPrice, lastCci, consecutive60DaysOfAverageRise,
                buySaleTime, earnLastTime, allEarnValue, earnTime, getAvgEarn(), getSuccessRate(),
                initHint());
    }

    public double getSuccessRate() {
        return buySaleTime == 0 ? -1.0 : earnTime*1.0 / buySaleTime;
    }

    public double getAvgEarn() {
        return allEarnValue * 100 / earnLastTime;
    }

    public String initHint() {
        hint = new ArrayList<>();
        if (lastPrice < buyPrice * 0.92) {
            hint.add("非常适合买入....");
        } else if (lastPrice < buyPrice) {
            hint.add("可以考虑买入.");
        }
        if (lastPrice > salePrice * 1.08) {
            hint.add("非常适合卖出!!!!!");
        } else if (lastPrice > salePrice) {
            hint.add("可以考虑卖出!");
        }
        return hint.stream().collect(Collectors.joining(","));
    }

    public double getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(double lastPrice) {
        this.lastPrice = lastPrice;
    }

    public double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
