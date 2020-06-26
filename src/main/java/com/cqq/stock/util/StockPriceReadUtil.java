package com.cqq.stock.util;

/**
 * 股票价格读取工具
 * 从python程序中读取股票的价格
 */

public class StockPriceReadUtil {

    private String path;

    /**
     * 传入获取股票的python程序位置
     * @param pythonPath C:\\a.py
     */
    public StockPriceReadUtil(String pythonPath){
        this.path = pythonPath;
    }

    /**
     * @param code      sh202002
     * @param startTime 20200304
     * @param endTime   20200307
     */
    public void readOneStock(String code, long startTime, long endTime) {


    }


}
