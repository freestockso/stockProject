package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cqq.stock.entity.ListEntity;
import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.StockRecent;
import com.cqq.stock.mapper.StockInfoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class NoticeService {
    private StockInfoMapper stockInfoMapper;
    private StockService stockService;

    /**
     * 通知用户去买入股票
     *
     * @return
     */
    public ListEntity<StockInfo> noticeToBuy(String code) {
        List<StockInfo> stockInfos = this.stockInfoMapper.selectList(
                Wrappers.<StockInfo>query().lambda().eq(code != null, StockInfo::getCode, code)
        );
        Map<String, List<StockInfo>> map = stockInfos.stream().collect(Collectors.groupingBy(StockInfo::getCode));
        List<StockRecent> todayAllStockRecentByCodeList = stockService.getToDayAllStockRecentByCodeList();
        List<StockInfo> list = new ArrayList<>();
        todayAllStockRecentByCodeList.stream().forEach(realTimeStock -> {
            Optional.ofNullable(map.get(realTimeStock.getCode())).map(ls -> ls.get(0)).ifPresent(st -> {
                Long buyPrice = st.getBuyPrice();
                long now = (long) (Double.valueOf(realTimeStock.getNow()) * 1000);
                long high = (long) (Double.valueOf(realTimeStock.getHigh()) * 1000);
                long low = (long) (Double.valueOf(realTimeStock.getLow()) * 1000);
                if (Math.abs(high) < 1e-5 || Math.abs(low) < 1e-5) {
                    log.info("code:{} high or low price is error", st.getCode());
                    return;
                }
                if (buyPrice != null) {
                    long goodBuyValue = buyPrice * st.getTen() * 3 - high - low;
                    if (now < goodBuyValue) {
                        log.info("用户应该买入股票:{},此时股票价格低于{},价格为{}"
                                , realTimeStock.getCode(), goodBuyValue, now);
                        list.add(st);
                    }
                }
            });
        });

        return new ListEntity<>(list);

    }

    /**
     * 通知用户去出售股票
     *
     * @return
     */
    public ListEntity<StockInfo> noticeToSale(String code) {
        List<StockInfo> stockInfos = this.stockInfoMapper.selectList(
                Wrappers.<StockInfo>query().lambda().eq(code != null, StockInfo::getCode, code)
        );
        Map<String, List<StockInfo>> map = stockInfos.stream().collect(Collectors.groupingBy(StockInfo::getCode));
        List<StockRecent> todayAllStockRecentByCodeList = stockService.getToDayAllStockRecentByCodeList();
        List<StockInfo> list = new ArrayList<>();
        todayAllStockRecentByCodeList.stream().forEach(realTimeStock -> {
            Optional.ofNullable(map.get(realTimeStock.getCode())).map(ls -> ls.get(0)).ifPresent(st -> {
                Long salePrice = st.getSalePrice();
                long now = (long) (Double.valueOf(realTimeStock.getNow()) * 1000);
                long high = (long) (Double.valueOf(realTimeStock.getHigh()) * 1000);
                long low = (long) (Double.valueOf(realTimeStock.getLow()) * 1000);
                if (Math.abs(high) < 1e-5 || Math.abs(low) < 1e-5) {
                    log.info("code:{} high or low price is error", st.getCode());
                    return;
                }
                if (salePrice != null && salePrice != 0) {
                    long salePriceValue = salePrice * st.getTen() * 3 - high - low;
                    if (now > salePriceValue) {
                        log.info("用户应该卖出股票:{} {},此时股票价格高于{},价格为{}"
                                ,realTimeStock.getCode(), realTimeStock.getName(), salePriceValue, now);
                        list.add(st);
                    }
                }else if(salePrice== null){
                    log.info("code:{},salePrice is null",st.getCode());
                }else {
                    log.info("code:{},salePrice is zero",st.getCode());

                }
            });
        });

        return new ListEntity<>(list);
    }


}
