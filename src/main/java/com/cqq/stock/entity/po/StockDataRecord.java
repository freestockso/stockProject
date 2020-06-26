package com.cqq.stock.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("stock_day_record")
public class StockDataRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer date;
    private String msg;

    public StockDataRecord(Integer date) {
        this.date = date;
    }
    public StockDataRecord(Integer date,String msg) {
        this.date = date;
        this.msg = msg;
    }
}
