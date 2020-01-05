package com.cqq.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data
public class StockForecasting extends Model<StockForecasting> {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String code;

    private String upRate;

    private String param;

    private Integer date;
    private String cciHistory;
    private String cciNow;
}
