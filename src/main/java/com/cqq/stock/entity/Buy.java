package com.cqq.stock.entity;

import lombok.Data;

@Data
public class Buy {
    /**
     * 报价
     */
    private String price;

    /**
     * 股数
     */
    private String number;


    @Override
    public String toString() {
        return "Buy{" +
                "price=" + price +
                ", number=" + number +
                '}';
    }
}
