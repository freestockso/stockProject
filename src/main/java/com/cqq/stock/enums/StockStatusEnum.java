package com.cqq.stock.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockStatusEnum {
    TODO("未运行状态"),
    DOING("正在计算"),
    DONE("计算完成");
    String value;
}
