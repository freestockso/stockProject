package com.cqq.stock.entity.dto;

import lombok.Data;

/**
 * 过滤条件
 *
 * @author qiqi.chen
 */
@Data
public class FilterDTO {
    /**
     * 最低价格
     */
    private Double minPrice;

    /**
     * 最高价格
     */
    private Double maxPrice;

    /**
     * 最小跌幅
     */
    private Double minChange;
    /**
     * 最大涨幅
     */
    private Double maxChange;

    /**
     * 是否显示号码
     */
    private Boolean showCode;

    /**
     * 最低换手
     */
    private Double minTurnOver;

    /**
     * 最高换手
     */
    private Double maxTurnOver;

    /**
     * 最低量比
     */
    private Double minVolRatio;

    /**
     * 最高量比
     */
    private Double maxVolRatio;

    /**
     * macd持续上涨天数
     */
    public Integer lastUpMacdDay;
}
