package com.cqq.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cqq.stock.interfaces.StockAble;
import lombok.Data;

@Data
@TableName("stock_transaction_info")
public class StockTransactionInfo implements StockAble {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String code;
    private Long open;
    private Long close;
    private Long high;
    private Long low;
    private Long vol;
    private Long amount;
    private Long date;
    private Double cci;

    public Long avg() {
        return (open + close + low + high) / 4;
    }

    public Double getCci() {
        return cci;
    }

    @Override
    public void setCci(Double value) {
        this.cci = value;

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getOpen() {
        return open;
    }

    public void setOpen(Long open) {
        this.open = open;
    }

    public Long getClose() {
        return close;
    }

    public void setClose(Long close) {
        this.close = close;
    }

    @Override
    public void setReserv(Long value) {

    }

    public Long getHigh() {
        return high;
    }

    public void setHigh(Long high) {
        this.high = high;
    }

    public Long getLow() {
        return low;
    }

    public void setLow(Long low) {
        this.low = low;
    }

    public Long getVol() {
        return vol;
    }

    public void setVol(Long vol) {
        this.vol = vol;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "StockTransactionInfo{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", vol=" + vol +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }

    public StockTransactionInfo() {
    }

    public StockTransactionInfo(Long open, Long high, Long close, Long low) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
    }
}
