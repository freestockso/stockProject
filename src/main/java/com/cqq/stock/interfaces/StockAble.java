package com.cqq.stock.interfaces;


/**
 * 可以读取股票信息的
 */
public interface StockAble {

    String getCode();

    Long getOpen();

    Long getClose();

    Long getHigh();

    Long getLow();

    Long getVol();

    Long getAmount();

    Long getDate();

    Double getCci();

    void setCci(Double value);
    void setVol(Long value);

    void setAmount(Long value);

}
