package com.cqq.stock.entity;

public class Buy {
    /**
     * 报价
     */
    private String price;

    /**
     * 股数
     */
    private String number;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Buy{" +
                "price=" + price +
                ", number=" + number +
                '}';
    }
}
