package com.cqq.stock.controller;


import com.cqq.stock.entity.dto.CodeAndDateDTO;
import com.cqq.stock.entity.dto.StartDateDTO;
import com.cqq.stock.service.StockNewService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("stockNew")
@CrossOrigin
public class StockNewController {
    private StockNewService stockNewService;


    /**
     * 将新浪的数据同步至数据库
     *
     * @param safely
     * @return String
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


    /**
     * 完成整个自动化流程
     */
    @RequestMapping("process")
    public void process() {
        stockNewService.process();
    }


    /**
     * 根据code与date，获取这种股票的盈利概率分布
     *
     * @return
     */
    @PostMapping("rate")
    public List<String> getRate(@RequestBody @Valid CodeAndDateDTO codeAndDateDTO) throws IOException {
        return stockNewService.getPrice(codeAndDateDTO.getCode(), codeAndDateDTO.getDate());

    }
}
