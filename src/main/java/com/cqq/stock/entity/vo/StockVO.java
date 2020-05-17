package com.cqq.stock.entity.vo;

import lombok.Data;

@Data
public class StockVO {
    private String name;
    private String code;
    private Double rate;
    private Long closePrice;
    private Long date;
}
