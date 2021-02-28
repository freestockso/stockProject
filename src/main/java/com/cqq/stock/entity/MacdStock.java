package com.cqq.stock.entity;


import com.cqq.stock.able.MACDAble;
import lombok.Data;

@Data
public class MacdStock implements MACDAble {
    private Long close;
    private Double macd;


    @Override
    public Long close() {
        return close;
    }

    @Override
    public void changeMacd(Double macd) {
        this.macd = macd;
    }

    @Override
    public void changeDiff(Double diff) {

    }
}
