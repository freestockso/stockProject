package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cqq.stock.entity.EMAStock;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.entity.dto.CalculateMACDDTO;
import com.cqq.stock.mapper.StockTransactionInfoMapper;
import com.cqq.stock.util.EMACalculateUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MacdService {


    private StockTransactionInfoMapper stockTransactionInfoMapper;

    public void calculateMACD(CalculateMACDDTO calculateMACDDTO) {
        Integer maxDate = stockTransactionInfoMapper.findMaxDate();
        List<StockTransactionInfo> stockTransactionInfoList = stockTransactionInfoMapper.selectList(Wrappers.<StockTransactionInfo>query().lambda()
                .eq(StockTransactionInfo::getDate, maxDate)
                .eq(StockTransactionInfo::getCode, calculateMACDDTO.getCode())
        );
        List<EMAStock> collect = stockTransactionInfoList.stream().map(EMAStock::new).collect(Collectors.toList());
        EMACalculateUtil.calculateAll(collect);
        for (EMAStock emaStock : collect) {
            System.out.println(emaStock);
        }

    }
}
