package com.cqq.stock.controller;

import com.cqq.stock.entity.dto.*;
import com.cqq.stock.entity.vo.FilterVO;
import com.cqq.stock.entity.vo.R;
import com.cqq.stock.service.TuShareService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author qiqi.chen
 */
@RestController
@AllArgsConstructor
@RequestMapping("tuShare")
public class TuShareController {
    private TuShareService tuShareService;

    /**
     * 获取日线的数据
     *
     * @param dailyParam dailyParam
     * @return R
     */
    @PostMapping("dailyMore")
    public R<Map<String, List<DailyResult>>> dailyMore(@RequestBody DailyParam dailyParam) {
        return tuShareService.dailyMore(dailyParam);
    }

    /**
     * 获取可以使用的股票
     *
     * @return R
     */
    @PostMapping("stockBasic")
    public R<List<StockBasicResult>> stockBasic() {
        return tuShareService.stockBasic();
    }

    /**
     * 将所有上市股票的日线数据加载到 redis中
     *
     * @return R
     */
    @PostMapping("load")
    public R<String> load() {
        return tuShareService.loadDaily();
    }

    @PostMapping("filter")
    public R<FilterVO> filter(@RequestBody FilterDTO filterDTO) {
        return tuShareService.filter(filterDTO);
    }


    @PostMapping("loadStockBak")
    public R<?> loadStockBak() {
        return tuShareService.loadStockBak();
    }

    @PostMapping("tradeCal")
    public R<?> tradeCal() {
        return tuShareService.tradeCal();
    }

    @PostMapping("getDay")
    public R<?> getDay(@RequestBody DayDTO dayDTO) {
        return tuShareService.getDay(dayDTO.getBaseDay(), dayDTO.getOffset());
    }

    @PostMapping("stockBakMore")
    public R<Map<String, List<StockComplexInfo>>> stockBakMore() {
        return tuShareService.stockBakMore();
    }
}
