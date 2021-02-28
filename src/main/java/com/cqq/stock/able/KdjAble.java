package com.cqq.stock.able;

/**
 * kdj可计算的接口
 *
 * @author qiqi.chen
 */
public interface KdjAble {

    Double kdjClose();

    Double kdjHigh();

    Double kdjLow();

    void changeK(Double v);

    void changeD(Double v);

    void changeJ(Double v);
}
