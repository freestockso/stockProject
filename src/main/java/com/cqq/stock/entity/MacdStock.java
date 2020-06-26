package com.cqq.stock.entity;


import com.cqq.stock.able.MACDAble;
import lombok.Data;

@Data
public class MacdStock implements MACDAble {
    private Long close;
    private Double macd;


}
