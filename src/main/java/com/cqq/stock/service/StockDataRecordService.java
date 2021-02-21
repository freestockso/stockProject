package com.cqq.stock.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cqq.stock.entity.po.StockDataRecord;
import com.cqq.stock.mapper.StockDataRecordMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class StockDataRecordService extends ServiceImpl<StockDataRecordMapper, StockDataRecord> {

    public List<Long> getDateList() {
        List<StockDataRecord> list = this.list();
        return list.stream().map(s -> Long.valueOf(s.getDate())).collect(Collectors.toList());

    }

    public StockDataRecord findHaveDataRecord() {
        List<StockDataRecord> success = this.list(Wrappers.<StockDataRecord>query().lambda()
                .eq(StockDataRecord::getMsg, "success")
                .eq(StockDataRecord::getInDb, 1)
        )
                .stream()
                .sorted(Comparator.comparing(StockDataRecord::getDate))
                .collect(Collectors.toList());
        if (success.isEmpty()) {
            return null;
        }
        return success.get(success.size() - 1);
    }

    public List<StockDataRecord> findErrorInDB() {
        return this.list(Wrappers.<StockDataRecord>query().lambda()
                .ne(StockDataRecord::getMsg, "success")
                .ne(StockDataRecord::getMsg, "no data")
        );

    }
}
