package com.cqq.stock.util;

import com.cqq.stock.entity.StockInfo;
import com.cqq.stock.entity.StockTransactionInfo;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuicklyInsertUtil {

    public static void main(List<StockTransactionInfo> list) {
        List<Doing<StockTransactionInfo>> doing = new ArrayList<>();

        list = list.stream().peek(s -> {

            s.setVol(Optional.ofNullable(s.getVol()).orElse(0L));
            s.setAmount(Optional.ofNullable(s.getAmount()).orElse(0L));
        }).collect(Collectors.toList());

        doing.add(StockTransactionInfo::getCode);
        doing.add(StockTransactionInfo::getOpen);
        doing.add(StockTransactionInfo::getClose);
        doing.add(StockTransactionInfo::getHigh);
        doing.add(StockTransactionInfo::getLow);
        doing.add(StockTransactionInfo::getVol);
        doing.add(StockTransactionInfo::getAmount);
        doing.add(StockTransactionInfo::getDate);
        main("insert into stock_transaction_info_2019(code, open, close, high, low, vol, amount, date)  values(?, ?, ?, ?, ?, ?, ?, ?)", list, doing);
    }

    private static <T> void main(String sql, List<T> list, List<Doing<T>> consumerList) {
        String url = "jdbc:mysql://localhost:3306/stock_project?rewriteBatchedStatements=true&serverTimezone=UTC";
        String classname = "com.mysql.jdbc.Driver";
        try {
            Class.forName(classname);
            PreparedStatement ps = DriverManager.getConnection(url, "root", "root").prepareStatement(sql);
            for (T t : list) {
                for (int j = 0; j < consumerList.size(); j++) {
                    Doing<T> tDoing = consumerList.get(j);
                    int finalJ = j;
                    Optional.ofNullable(tDoing.accept(t)).map(Object::toString).ifPresent(string -> {
                        try {
                            ps.setString(finalJ + 1, string);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
                }
                ps.addBatch();
            }
            ps.executeBatch();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

interface Doing<T> {
    Object accept(T data);
}
