package com.cqq.stock.entity.dto;

import lombok.Data;

/**
 * 备用行情
 * https://waditu.com/document/2?doc_id=255
 *
 * @author qiqi.chen
 */
@Data
public class StockComplexInfo {
    /**
     * 股票代码
     */
    private String ts_code;


    /**
     * 交易日期
     */
    private String trade_date;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 涨跌幅
     */
    private String pct_change;

    /**
     * 收盘价
     */
    private String close;

    /**
     * 涨跌额
     */
    private String change;

    /**
     * 开盘价
     */
    private String open;

    /**
     * 最高价
     */
    private String high;

    /**
     * 最低价
     */
    private String low;


    /**
     * 昨收价
     */
    private String pre_close;

    /**
     * 量比
     */
    private String vol_ratio;

    /**
     * 换手率
     */
    private String turn_over;

    /**
     * 振幅
     */
    private String swing;

    /**
     * 成交量
     */
    private String vol;

    /**
     * 成交额
     */
    private String amount;

    /**
     * 内盘（主动卖，手）
     */
    private String selling;

    /**
     * 外盘（主动买，手）
     */
    private String buying;


    /**
     * 总股本(万)
     */
    private String total_share;


    /**
     * 流通股本(万)
     */
    private String float_share;


    /**
     * 市盈(动)
     */
    private String pe;

    /**
     * 所属行业
     */
    private String industry;

    /**
     * 所属地域
     */
    private String area;

    /**
     * 流通市值
     */
    private String float_mv;

    /**
     * 总市值
     */
    private String total_mv;

    /**
     * 平均价
     */
    private String avg_price;

    /**
     * 强弱度(%)
     */
    private String strength;

    /**
     * 活跃度(%)
     */
    private String activity;

    /**
     * 笔换手
     */
    private String avg_turnover;

    /**
     * 攻击波(%)
     */
    private String attack;

    /**
     * 近3月涨幅
     */
    private String interval_3;

    /**
     * 近6月涨幅
     */
    private String interval_6;

}
