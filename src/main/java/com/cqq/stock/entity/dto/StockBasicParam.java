package com.cqq.stock.entity.dto;

import com.cqq.stock.interfaces.TuShareParam;
import lombok.Data;

/**
 * 股票基本信息的参数
 *
 * @author qiqi.chen
 */
@Data
public class StockBasicParam implements TuShareParam {
    private String list_status;


}
