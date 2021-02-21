package com.cqq.stock.entity.dto;

import lombok.Data;

/**
 * @author qiqi.chen
 */
@Data
public class DailyResult {

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
}
