package com.cqq.stock.entity.dto;

import com.cqq.stock.interfaces.PageAble;
import lombok.Data;

@Data
public class StockListCondition implements PageAble {
    private Long current;
    private Long limit;
    private Double minPrice;
    private Double maxPrice;
    private Double minCCI;
    private Double maxCCI;
}
