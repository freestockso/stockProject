package com.cqq.stock.util;

import com.cqq.stock.able.QuicklyInsertAble;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class QuicklyInsertUtilV2 {

    /**
     * 需要快速插入的数据
     * @param list 所有的数据
     * @param batchSize 每批大小
     * @param dataSourceProperties 数据源
     * @param <T> t
     */
    public static <T extends QuicklyInsertAble> void quicklySaveToDatabase(List<T> list, int batchSize, DataSourceProperties dataSourceProperties) {
        if (list.isEmpty()) {
            return;
        }
        try {
            Class.forName(dataSourceProperties.getDriverClassName());
            QuicklyInsertAble quicklyInsertAble = list.get(0);
            Connection connection = DriverManager.getConnection(dataSourceProperties.getUrl(), dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
            ArrayGroupUtil.batch(list, batchSize).forEach(ls -> {
                TimingClock t = new TimingClock();
                quicklySaveToDatabase(connection, quicklyInsertAble.getQuicklyInsertSqlTemplate(), ls);
                t.call("save ok");
            });
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }


    private static <T extends QuicklyInsertAble> void quicklySaveToDatabase(Connection connection, String sql, List<T> list) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(sql);
            for (QuicklyInsertAble quicklyInsertAble : list) {
                quicklyInsertAble.insertSet(ps);
                ps.addBatch();
            }
            ps.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
