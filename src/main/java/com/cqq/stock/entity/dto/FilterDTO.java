package com.cqq.stock.entity.dto;

import lombok.Data;

/**
 * 过滤条件
 *
 * @author qiqi.chen
 */
@Data
public class FilterDTO {
    private Double minPrice;
    private Double maxPrice;

    private Double minChange;
    private Double maxChange;
}
