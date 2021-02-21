package com.cqq.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqq.stock.entity.po.StockDayData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StockDayDataMapper extends BaseMapper<StockDayData> {
    @Update(" truncate table stock_day_data ")
    void clearAllData();
}
