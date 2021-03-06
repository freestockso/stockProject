package com.cqq.stock.entity.dto;

import lombok.Data;

/**
 * @author qiqi.chen
 */
@Data
public class StockBasicResult {
    /**
     * TS代码
     */
    private String ts_code;
    /**
     * 股票代码
     */
    private String symbol;
    /**
     * 股票名称
     */
    private String name;
    /**
     * 所在地域
     */
    private String area;
    /**
     * 所属行业
     */
    private String industry;
    /**
     * 股票全称
     */
    private String fullname;
    /**
     * 英文全称
     */
    private String enname;
    /**
     * 市场类型（主板/中小板/创业板/科创板/CDR）
     */
    private String market;
    /**
     * 交易所代码
     */
    private String exchange;
    /**
     * 交易货币
     */
    private String curr_type;
    /**
     * 上市状态：L上市 D退市 P暂停上市
     */
    private String list_status;
    /**
     * 上市日期
     */
    private String list_date;
    /**
     * 退市日期
     */
    private String delist_date;
    /**
     * 是否沪深港通标的，N否H沪股通 S深股通
     */
    private String is_hs;
}
