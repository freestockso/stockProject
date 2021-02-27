package com.cqq.stock.able;

/**
 * 可计算MACD的
 */
public interface MACDAble {

    /**
     * 获取收盘价
     *
     * @return close price 单位(分)
     */
    Long close();

    /**
     * 设置MACD
     */
    void changeMacd(Double macd);
}
