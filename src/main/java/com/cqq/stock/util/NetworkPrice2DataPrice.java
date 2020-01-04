package com.cqq.stock.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 将网络中获取的价格转换成真实的价格
 */
public class NetworkPrice2DataPrice {

    /**
     * 直接将价格进行转换
     *
     * @param code  sh000001
     * @param value 2.123
     * @return 2123
     */
    public static long getPrice(String code, String value) {
        BigDecimal networkValue = new BigDecimal(value).multiply(new BigDecimal(1000)).setScale(0, RoundingMode.HALF_DOWN);
        if (!StockJudgeUtil.isLow(code)) {
            networkValue = networkValue.divide(new BigDecimal(10), 0, RoundingMode.HALF_UP);
        }
        return networkValue.longValue();
    }
}
