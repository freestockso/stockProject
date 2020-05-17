package com.cqq.stock.util;

import com.cqq.stock.entity.CalculateStockTransactionInfo;
import com.cqq.stock.interfaces.StockAble;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 快速将股票数据插入库中
 */
public class QuicklyInsertUtil {

    private static List<Function<StockAble, Object>> functionList = new ArrayList<Function<StockAble, Object>>() {{
        add(StockAble::getCode);
        add(StockAble::getOpen);
        add(StockAble::getClose);
        add(StockAble::getHigh);
        add(StockAble::getLow);
        add(StockAble::getVol);
        add(StockAble::getAmount);
        add(StockAble::getDate);
        add(StockAble::getCci);
    }};

    public static void quicklySaveToDatabase(List<StockAble> list) {

        for (int i = 0; i < list.size(); i++) {

            StockAble s = list.get(i);
            s.setCci(s.getCci() == null || Double.isInfinite(s.getCci()) || Double.isNaN(s.getCci()) ? 10000 : s.getCci());
            s.setCci(((int) (s.getCci() * 100)) / 100.0);
            s.setVol(0L);
            s.setAmount(0L);
            list.set(i, s);
        }
        new TimingClock("start put");

        quicklySaveToDatabase("insert into stock_transaction_info(code, open, close, high, low, vol, amount, date, cci)  values(?, ?, ?, ?, ?, ?, ?, ?,?)", list, functionList);
    }


    private static <T extends StockAble> void quicklySaveToDatabase(String sql, List<T> list, List<Function<T, Object>> consumerList) {
        String url = "jdbc:mysql://localhost:3306/stock_project?rewriteBatchedStatements=true&serverTimezone=UTC";
        String classname = "com.mysql.jdbc.Driver";
        try {
            Class.forName(classname);
            PreparedStatement ps = DriverManager.getConnection(url, "root", "root").prepareStatement(sql);
            for (T t : list) {
                for (int j = 0; j < consumerList.size(); j++) {
                    Function<T, Object> function = consumerList.get(j);
                    int finalJ = j;
                    Optional.ofNullable(function.apply(t)).map(Object::toString).ifPresent(string -> {
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
        } catch (SQLException e) {
            String message = e.getMessage();
            Pattern compile = Pattern.compile("[0-9]+");
            Matcher matcher = compile.matcher(message);
            if (matcher.find()) {
                String group = matcher.group(0);
                int integer = Integer.parseInt(group);
                System.out.println(list.get(integer - 1));
                System.out.println(list.get(integer));
                System.out.println(list.get(integer + 1));

            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void quicklySaveToDatabase(String sql, List<CalculateStockTransactionInfo> list) {
        String url = "jdbc:mysql://localhost:3306/stock_project?rewriteBatchedStatements=true&serverTimezone=UTC";
        String classname = "com.mysql.jdbc.Driver";
        try {
            Class.forName(classname);
            PreparedStatement ps = DriverManager.getConnection(url, "root", "root").prepareStatement(sql);
            for (int i = 0; i < list.size(); i++) {
                CalculateStockTransactionInfo s = list.get(i);
                ps.setString(1, s.getCode());
                ps.setLong(2, s.getOpen());
                ps.setLong(3, s.getClose());
                ps.setLong(4, s.getHigh());
                ps.setLong(5, s.getLow());
                ps.setLong(6, s.getVol());
                ps.setLong(7, s.getAmount());
                ps.setLong(8, s.getDate());
                ps.setString(9, s.getCci() + "");
                ps.addBatch();
            }

            ps.executeBatch();
            ps.close();
        } catch (SQLException e) {
            String message = e.getMessage();
            Pattern compile = Pattern.compile("[0-9]+");
            Matcher matcher = compile.matcher(message);
            if (matcher.find()) {
                String group = matcher.group(0);
                int integer = Integer.parseInt(group);
                System.out.println(list.get(integer - 1));
                System.out.println(list.get(integer));
                System.out.println(list.get(integer + 1));

            }
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void quicklySaveToDatabaseCalculateStockTransactionInfo(List<CalculateStockTransactionInfo> list) {
        for (int i = 0; i < list.size(); i++) {
            CalculateStockTransactionInfo s = list.get(i);
            if(s==null)continue;
            s.setCci(s.getCci() == null || Double.isInfinite(s.getCci()) || Double.isNaN(s.getCci()) ? 10000 : s.getCci());
            s.setCci(((int) (s.getCci() * 100)) / 100.0);
            s.setVol(0L);
            s.setAmount(0L);
            list.set(i, s);
        }
        TimingClock start_put = new TimingClock("start put");
        quicklySaveToDatabase("insert into stock_transaction_info(code, open, close, high, low, vol, amount, date, cci)  values(?, ?, ?, ?, ?, ?, ?, ?,?)", list);
        start_put.call("over");
    }
}

