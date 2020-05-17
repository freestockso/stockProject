package com.cqq.stock.controller;

import com.cqq.stock.entity.*;
import com.cqq.stock.service.StockService;
import com.cqq.stock.util.AnalysisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("stock")
@AllArgsConstructor
@Slf4j
public class StockController {

    private StockService stockService;

    @RequestMapping("calculate")
    public List<CalculateStockTransactionInfo> hello(String code) {
        return stockService.calculateStockAdditionalInformation(code);
    }

    static int cnt = 0;

    @RequestMapping("test")
    public String test() {
        List<String> allCodeList = stockService.getAllCodeList();
        List<StockInfo> collect = allCodeList.stream().map(code -> {
            List<CalculateStockTransactionInfo> calculateStockTransactionInfos = stockService.calculateStockAdditionalInformation(code);
            if (calculateStockTransactionInfos.size() == 0) {
                return null;
            }
            CalculateStockTransactionInfo calculateStockTransactionInfo = calculateStockTransactionInfos.get(calculateStockTransactionInfos.size() - 1);
            StockInfo stockInfo = stockService.selectByCode(code);
            stockInfo.setLastCci(calculateStockTransactionInfo.getCci());
            AnalysisUtil analysisUtil = new AnalysisUtil();
            GoodPricePoint goodPricePoint = analysisUtil.judgeMaxAndMinGoodPrice(calculateStockTransactionInfos);
            stockInfo.setBuyPrice((long) goodPricePoint.getBuyPrice());
            stockInfo.setSalePrice((long) goodPricePoint.getSalePrice());
            stockInfo.setLastPrice((long) goodPricePoint.getLastPrice());
            System.out.println(cnt++);
            return stockInfo;
        }).peek(data -> Optional.ofNullable(data).ifPresent(d -> {
            if (Double.isNaN(d.getLastCci())) {
                data.setLastCci(-999999D);
            } else if (Double.isInfinite(d.getLastCci())) {
                data.setLastCci(-999999D);
            }
        })).filter(Objects::nonNull).collect(Collectors.toList());

        stockService.updateBatchById(collect);
        return "success";

    }

    @GetMapping("canBuy")
    public List<StockInfo> canBuy() {
        return stockService.getCanBuyAndGoUpStock();
    }


    public List<StockTransactionInfo> getCciListByCode(String code) {
        return stockService.getCciListByCode(code);

    }

    @GetMapping("dif/{code}")
    public List<EMAStock> getDif(@PathVariable("code") String code) {
        List<EMAStock> emaStocks = this.stockService.calculateDIF(code);
        emaStocks.forEach(System.out::println);
        return emaStocks;
    }

    @GetMapping("macd")
    public List<StockInfo> getMACDGoodStock() {
        return this.stockService.getMACDGoodStock();
    }

    @GetMapping("cci/{code}")
    public List<StockTransactionInfo> getMACDGoodStock(@PathVariable("code") String code) {
        return this.stockService.calculateCCI(code);
    }

    @GetMapping("saveCCI")
    public String recordCCI() {
        this.stockService.recordCCI();
        return "success";
    }

    @GetMapping("getMoney")
    public void getMoney(String code) {
        this.stockService.getMoney(code);
    }

    @GetMapping("guess")
    public double guess(String code, Long money) {
        return this.stockService.guess(code, money);

    }

    @GetMapping("autoGuess")
    public GoodPricePoint autoGuess(String code) {
        return this.stockService.autoGuess(code);

    }

    @GetMapping("autoGuessAll")
    public void autoGuessAll() {
        this.stockService.autoGuessAll();
    }

    @GetMapping("calculateHui5DayLine")
    public void calculateHui5DayLine(String code, Integer day) {
        this.stockService.calculateHui5DayLine(code, day);
    }

}
