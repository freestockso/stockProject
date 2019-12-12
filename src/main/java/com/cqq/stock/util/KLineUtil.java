package com.cqq.stock.util;

import com.cqq.stock.entity.CalculateStockTransactionInfo;

import java.util.List;
import java.util.Optional;

public class KLineUtil {

    /**
     * 算每个股票N日来平均价格,其中N= 60可以表示该股票当前价与60日均价的平均差距
     *
     * @param list
     * @param n
     */
    public static void main(List<CalculateStockTransactionInfo> list, int n) {
        for (int i = n ; i < list.size(); i++) {
            List<CalculateStockTransactionInfo> calculateStockTransactionInfos = list.subList(i - n  , i );
            Optional<Long> sum = calculateStockTransactionInfos.stream().map(CalculateStockTransactionInfo::getTp).reduce(Long::sum);
            int finalI = i;
            sum.ifPresent(s -> list.get(finalI).setkValue(s / n));
        }


    }
}
