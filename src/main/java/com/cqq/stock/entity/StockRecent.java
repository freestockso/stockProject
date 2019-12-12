package com.cqq.stock.entity;

import java.util.List;

public class StockRecent {
    /**
     * 股票名
     */
    private String name;

    /**
     * 股票代码
     */
    private String code;

    /**
     * 开盘价
     */
    private String open;
    /**
     * 收盘价
     */
    private String close;
    /**
     * 当前价
     */
    private String now;
    /**
     * 最高价
     */
    private String high;
    /**
     * 最低价
     */
    private String low;
    /**
     * 当前买入价
     */
    private String buyPrice;

    /**
     * 销售价
     */
    private String salePrice;

    /**
     * 成交股数
     */
    private String numberOfTransactions;


    /**
     * 成交价格
     */
    private String transactionPrice;

    /**
     * 前5买入价情况
     */
    private List<Buy> buyList;
    /**
     * 前5卖出价情况
     */
    private List<Sale> saleList;
    /**
     * 日期
     */
    private String dateTime;//yyyy-mm-dd
    /**
     * 时间
     */
    private String time;

    public String getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(String numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public String getTransactionPrice() {
        return transactionPrice;
    }

    public void setTransactionPrice(String transactionPrice) {
        this.transactionPrice = transactionPrice;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(String buyPrice) {
        this.buyPrice = buyPrice;
    }

    public String getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getClose() {
        return close;
    }

    public void setClose(String close) {
        this.close = close;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getLow() {
        return low;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public List<Buy> getBuyList() {
        return buyList;
    }

    public void setBuyList(List<Buy> buyList) {
        this.buyList = buyList;
    }

    public List<Sale> getSaleList() {
        return saleList;
    }

    public void setSaleList(List<Sale> saleList) {
        this.saleList = saleList;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "当前股价" +
                "\n股票名=" + name +
                "\n股票代号=" + code +
                ",\n 开盘价:" + open +
                ",\n 收盘价:" + close +
                ",\n 当前价:" + now +
                ",\n 最高价:" + high +
                ",\n 最低价:" + low +
                ",\n 买入价:" + buyPrice +
                ",\n 卖出价:" + salePrice +
                ",\n 成交量:" + numberOfTransactions +
                ",\n 成交额:" + transactionPrice +
//                ",\n 购买表:\n----------------------------------------\n" + showBuy(buyList) +
//                "\n 卖出表:\n----------------------------------------\n" + showSale(saleList) +
                "\n日期:" + dateTime +
                "\n时间:" + time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String showSale(List<Sale> list) {
        String s = "一二三四五";
        StringBuilder mean = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            mean.append(String.format("卖%s: 价格:%s 股数:%s\n",
                    s.charAt(i), list.get(i).getPrice(), list.get(i).getNumber()));
        }
        return mean.toString();
    }

    private String showBuy(List<Buy> list) {
        String s = "一二三四五";
        StringBuilder mean = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            mean.append(String.format("买%s: 价格:%s 股数:%s\n",
                    s.charAt(i), list.get(i).getPrice(), list.get(i).getNumber()));
        }
        return mean.toString();
    }
}


