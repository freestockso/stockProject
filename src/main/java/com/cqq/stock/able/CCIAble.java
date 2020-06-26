package com.cqq.stock.able;

public interface CCIAble {


    /**
     * 获取价格
     *
     * @return price
     */
    Double getPrice();

    /**
     * 设置CCI
     *
     * @param cci cci
     */
    void setCci(Double cci);

    Double getCci();


    Long getDate();

}
