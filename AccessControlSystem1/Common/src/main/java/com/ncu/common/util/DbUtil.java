//文件: DbUtil.java ///////////////////////////////////////////////////////////////////////////////////////////

package com.ncu.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// 数据库管理类
public class DbUtil {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/access_sys_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "@32158566Abc#";

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

