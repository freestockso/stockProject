package com.cqq.stock.entity.vo;

import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.enums.StockStatusEnum;
import lombok.Data;

@Data
public class StockData {

    private String code;
    private String name;
    private String price;
    private String date;
    private String status;
    private String cci;

    public StockData(StockTransactionInfo s) {
        this.code = s.getCode();
        this.price = String.valueOf(s.getClose());
        this.cci = s.getCci().toString();
        this.status = StockStatusEnum.TODO.getValue();
        this.date = String.valueOf(s.getDate());
        this.name = "unkwon";
    }
}
