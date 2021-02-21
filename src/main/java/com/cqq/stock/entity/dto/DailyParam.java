package com.cqq.stock.entity.dto;

import com.cqq.stock.interfaces.TuShareParam;
import lombok.Data;

/**
 * 调用tuShare的股票参数
 *
 * @author qiqi.chen
 */
@Data
public class DailyParam implements TuShareParam {
    /**
     * 股票代码（支持多个股票同时提取，逗号分隔）
     */
    private String ts_code;
    /**
     * 交易日期（YYYYMMDD）
     */
    private String trade_date;
    /**
     * 开始日期(YYYYMMDD)
     */
    private String start_date;
    /**
     * 结束日期(YYYYMMDD)
     */
    private String end_date;

}
