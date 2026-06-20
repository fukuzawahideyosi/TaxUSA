package com.cpsc.efiling.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("找不到 db.properties，请确认 src/main/resources/db.properties 已存在。 ");
            }
            PROPERTIES.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("数据库配置初始化失败：" + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }
}
