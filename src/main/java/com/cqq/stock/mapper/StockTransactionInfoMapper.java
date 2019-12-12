package com.cqq.stock.mapper;


import com.cqq.stock.entity.StockTransactionInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockTransactionInfoMapper {

    @Select("select * from ${tableName} where date = #{date}")
    List<StockTransactionInfo> listByDate(@Param("tableName") String tableName, @Param("date") String date);

    @Select("select * from ${tableName} where code = #{code} and date >= #{beginDate} and date <= #{endDate}")
    List<StockTransactionInfo> selectList(@Param("tableName") String tableName,
                                          @Param("code") String code,
                                          @Param("beginDate") int beginDate,
                                          @Param("endDate") int endDate);

    @Select("update ${tableName} set cci = #{cci} where id = #{id}")
    List<StockTransactionInfo> updateCCIById(@Param("tableName") String tableName,
                                             @Param("id") Integer id,
                                             @Param("cci") double cci);

    void updateStockListCCIByCodeAndDate(@Param("tableName") String tableName, @Param("list") List<StockTransactionInfo> list);

    @Select("select * from ${tableName} ")
    List<StockTransactionInfo> selectAll(@Param("tableName") String tableName);

}
