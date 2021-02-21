package com.cqq.stock.constants;


import lombok.Data;

@Data
public class StockConfig {
    /**
     * 日线数据执行的脚本
     */
    private String dayStockScriptPath;

    /**
     * 日线数据使用的编译器
     */
    private String dayStockExePath;

    /**
     * 日线数据
     */
    private String dayStockPath;

    private String dayStockDataTxt;

    /**
     * 结果集路径
     */
    private String resultDir;

    /**
     * logicX 的存放路径
     */
    private String logicXDir;

    /**
     * day stock dir
     */
    private String dayStockDir;

    /**
     * 结果集文件的路径
     */
    private String resultTxt;
}
