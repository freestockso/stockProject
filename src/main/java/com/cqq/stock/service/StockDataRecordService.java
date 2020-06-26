package com.cqq.stock.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.po.StockDataRecord;
import com.cqq.stock.mapper.StockDataRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockDataRecordService extends ServiceImpl<StockDataRecordMapper, StockDataRecord> {

    public List<Long> getDateList() {
        List<StockDataRecord> list = this.list();
        return list.stream().map(s -> Long.valueOf(s.getDate())).collect(Collectors.toList());

    }

}
