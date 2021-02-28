package com.cqq.stock.entity.dto;

import com.cqq.stock.able.KdjAble;
import com.cqq.stock.able.MACDAble;
import lombok.Data;

/**
 * @author qiqi.chen
 */
@Data
public class DailyResult implements MACDAble, KdjAble {

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

    private Double diff;
    private Double k;
    private Double d;
    private Double j;

    @Override
    public Long close() {
        return (long) (Double.parseDouble(close) * 100);
    }

    @Override
    public Double kdjClose() {
        return Double.valueOf(close);
    }

    @Override
    public Double kdjHigh() {
        return Double.valueOf(high);
    }

    @Override
    public Double kdjLow() {
        return Double.valueOf(low);
    }

    @Override
    public void changeK(Double v) {
        this.k = v;

    }

    @Override
    public void changeD(Double v) {
        this.d = v;

    }

    @Override
    public void changeJ(Double v) {
        this.j = v;

    }


    @Override
    public void changeMacd(Double macd) {
        this.macd = macd;
    }

    @Override
    public void changeDiff(Double diff) {
        this.diff = diff;

    }
}
