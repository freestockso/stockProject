package com.cqq.stock.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.StockTransactionInfo;
import com.cqq.stock.mapper.StockTransactionInfoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StockTransactionInfoService extends ServiceImpl<StockTransactionInfoMapper, StockTransactionInfo> {
}
