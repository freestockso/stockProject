package com.cqq.stock.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cqq.stock.able.CCIAble;
import com.cqq.stock.able.MakeDataAble;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Stream;

/**
 * 股票日线数据
 */
@Data
@TableName("stock_day_data")
@NoArgsConstructor
public class StockDayData implements CCIAble, MakeDataAble {

    /**
     * 无意义自增
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 股票代码 sh234011
     */
    private String code;

    /**
     * 开盘价
     */
    private Double open;

    /**
     * 收盘价
     */
    private Double close;
    /**
     * 最高价
     */
    private Double high;

    /**
     * 最低价
     */
    private Double low;

    /**
     * cci
     */
    private Double cci;

    /**
     * 成交量(手)
     */
    private Double vol;

    /**
     * 成交额(千元)
     */
    private Double amount;

    /**
     * 涨幅
     */
    private Double changeRate;

    /**
     * 日期 20200626
     */
    private Long date;

    @Override
    public Double getPrice() {
        if (open == null || close == null || high == null || low == null) {
            return null;
        }
        return Stream.of(close, high, low).mapToDouble(s -> s).summaryStatistics().getAverage();
    }


    public StockDayData(String date, Double open, Double close, Double high, Double low) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.date = 0L;
    }

    @Override
    public Double getClosePrice() {
        return close;
    }
}
