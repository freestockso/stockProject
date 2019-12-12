package com.cqq.stock.entity;

public class CalculateStockTransactionInfo extends StockTransactionInfo {

    public CalculateStockTransactionInfo(StockTransactionInfo stockTransactionInfo) {
        this.setClose(stockTransactionInfo.getClose());
        this.setCode(stockTransactionInfo.getCode());
        this.setDate(stockTransactionInfo.getDate());
        this.setOpen(stockTransactionInfo.getOpen());
        this.setHigh(stockTransactionInfo.getHigh());
        this.setLow(stockTransactionInfo.getLow());

    }

    private long ma;
    private long tp;
    private long md;
    private double cci;
    private double kValue;

    public double getkValue() {
        return kValue;
    }

    public void setkValue(double kValue) {
        this.kValue = kValue;
    }

    public long getMd() {
        return md;
    }

    public void setMd(long md) {
        this.md = md;
    }

    public double getCci() {
        return cci;
    }

    public void setCci(double cci) {
        this.cci = cci;
    }

    public long getMa() {
        return ma;
    }

    public void setMa(long ma) {
        this.ma = ma;
    }

    public long getTp() {
        return tp;
    }

    public void setTp(long tp) {
        this.tp = tp;
    }

    @Override
    public String toString() {
        return "CalculateStockTransactionInfo{" +
                "ma=" + ma +
                ", tp=" + tp +
                ", md=" + md +
                ", cci=" + cci +
                ", kValue=" + kValue +
                '}';
    }
}
