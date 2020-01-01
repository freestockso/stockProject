package com.cqq.stock.entity;

import com.cqq.stock.interfaces.StockAble;
import lombok.Data;

import java.io.Serializable;

/**
 * 股票信息
 */
@Data
public class Stock implements Serializable, StockAble {


    /**
     * 股票码
     */
    private String code;

    /**
     * 日期
     */
    private Long date;
    /**
     * 开盘价，单位：分
     */
    private Long open;
    /**
     * /**
     * 最高价，单位：分
     */
    private Long high;

    /**
     * 最低价，单位：分
     */
    private Long low;

    /**
     * 收盘价，单位：分
     */
    private Long close;

    /**
     * 交易金额，单位：元
     */
    private Long amount;

    /**
     * 成交量，单位：股
     */
    private Long vol;

    /**
     * 保留，有时用来保存上一交易日收盘价
     */
    private Long reserv;

    private Double cci;


    @Override
    public String toString() {
        return "Stock{" +
                "code='" + code + '\'' +
                ", date=" + date +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                ", amount=" + amount +
                ", vol=" + vol +
                ", reserv=" + reserv +
                '}';
    }


    @Override
    public void setCci(Double value) {
        this.cci = value;
    }
}
