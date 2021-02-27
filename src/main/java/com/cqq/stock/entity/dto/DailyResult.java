package com.cqq.stock.entity.dto;

import com.cqq.stock.able.MACDAble;
import lombok.Data;

/**
 * @author qiqi.chen
 */
@Data
public class DailyResult implements MACDAble {

    private String ts_code;
    private String trade_date;
    private String open;
    private String high;
    private String low;
    private String close;
    private String pre_close;
    private String change;
    private String pct_chg;
    private String vol;
    private String amount;

    private Double macd;

    @Override
    public Long close() {
        return (long) (Double.parseDouble(close) * 100);
    }

    @Override
    public void changeMacd(Double macd) {
        this.macd = macd;
    }
}
