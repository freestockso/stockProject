package com.cqq.stock.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cqq.stock.entity.StockInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockInfoMapper extends BaseMapper<StockInfo> {

    @Update("update stock_info set ten = 1 where code = #{code}")
    Integer setTen(@Param("code") String code);

    @Select("SELECT count(1) from stock_info WHERE last_cci is not NULL")
    Integer getStockNumber(@Param("code") String code);

    @Select("SELECT * from stock_info WHERE last_cci < #{cciNumber} and last_cci is not NULL")
    Integer getCanBuyStockNumber(@Param("cciNumber") Integer cciNumber);

    @Select("SELECT * from stock_info WHERE last_cci > #{cciNumber} and last_cci is not NULL")
    Integer getCanSaleStockNumber(@Param("cciNumber") Integer cciNumber);

    @Select("SELECT * from stock_info WHERE last_cci < -100 and last_cci is not NULL")
    List<StockInfo> getCanBuyStock(@Param("cciNumber") Integer cciNumber);

}

