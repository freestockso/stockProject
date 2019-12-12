package com.cqq.stock.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

public class StockInfo extends Model<StockInfo> {

    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String code;

    private Integer ten;
    private Long buyPrice;
    private Long salePrice;
    private Double lastCci;
    private Long lastPrice;
    private Long lastBuyDate;
    private Long lastSaleDate;
    private Long averageEarningCycle;
    private Long longestEarningCycle;
    private Long timesOfMakingMoney;

    public Integer getTen() {
        return ten;
    }

    public void setTen(Integer ten) {
        this.ten = ten;
    }

    public Long getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Long buyPrice) {
        this.buyPrice = buyPrice;
    }

    public Long getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Long salePrice) {
        this.salePrice = salePrice;
    }

    public Double getLastCci() {
        return lastCci;
    }

    public void setLastCci(Double lastCci) {
        this.lastCci = lastCci;
    }

    public Long getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Long lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Long getLastBuyDate() {
        return lastBuyDate;
    }

    public void setLastBuyDate(Long lastBuyDate) {
        this.lastBuyDate = lastBuyDate;
    }

    public Long getLastSaleDate() {
        return lastSaleDate;
    }

    public void setLastSaleDate(Long lastSaleDate) {
        this.lastSaleDate = lastSaleDate;
    }

    public Long getAverageEarningCycle() {
        return averageEarningCycle;
    }

    public void setAverageEarningCycle(Long averageEarningCycle) {
        this.averageEarningCycle = averageEarningCycle;
    }

    public Long getLongestEarningCycle() {
        return longestEarningCycle;
    }

    public void setLongestEarningCycle(Long longestEarningCycle) {
        this.longestEarningCycle = longestEarningCycle;
    }

    public Long getTimesOfMakingMoney() {
        return timesOfMakingMoney;
    }

    public void setTimesOfMakingMoney(Long timesOfMakingMoney) {
        this.timesOfMakingMoney = timesOfMakingMoney;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
