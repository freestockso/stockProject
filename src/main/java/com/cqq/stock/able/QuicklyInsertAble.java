package com.cqq.stock.able;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface QuicklyInsertAble {

    String getQuicklyInsertSqlTemplate();

    void insertSet(PreparedStatement ps) throws SQLException;
}
