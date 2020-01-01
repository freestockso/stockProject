package com.cqq.stock.controller;


import com.cqq.stock.service.StockNewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("stockNew")
@AllArgsConstructor
public class StockNewController {
    private StockNewService stockNewService;


    /**
     * 将新浪的数据同步至数据库
     *
     * @return String
     * @param safely
     */
    @RequestMapping("syncData")
    public String syncData(boolean safely) {
        stockNewService.syncDataFromNetwork(safely);
        return "success";
    }
}
