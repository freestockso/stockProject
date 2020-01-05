package com.cqq.stock.controller;


import com.cqq.stock.entity.dto.StartDateDTO;
import com.cqq.stock.service.StockNewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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


    /**
     * 将 本地 磁盘 的 数据 重新导入数据库
      *
     * @param startDateDTO@return
     */
    @RequestMapping("syncDataByDesk")
    public String syncDataByDesk(@RequestBody @Valid StartDateDTO startDateDTO) {
        stockNewService.syncDataByDesk(startDateDTO.getDate());
        return "success";
    }

}
