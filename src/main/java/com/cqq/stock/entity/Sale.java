package com.cqq.stock.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Sale {
    /**
     * 报价
     */
    private String price;

    /**
     * 股数
     */
    private String number;


}
