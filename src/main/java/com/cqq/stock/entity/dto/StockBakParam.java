package com.cqq.stock.entity.dto;

import com.cqq.stock.interfaces.TuShareParam;
import lombok.Data;

/**
 * @author qiqi.chen
 */
@Data
public class StockBakParam implements TuShareParam {

    /**
     * 股票代码
     */
    private String ts_code;

    /**
     * 交易日期
     */
    private String trade_date;

    /**
     * 开始日期
     */
    private String start_date;

    /**
     * 结束日期
     */
    private String end_date;

    /**
     * 开始行数
     */
    private String offset;

    /**
     * 最大行数
     */
    private String limit;
}
