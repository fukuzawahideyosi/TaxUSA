package com.cpsc.efiling.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DbUtil {
    private static final Logger log = LogManager.getLogger(DbUtil.class);
    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = DbUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("找不到 db.properties，请确认 src/main/resources/db.properties 已存在。 ");
            }
            PROPERTIES.load(in);
            log.info("db.properties加载成功。db.url={}", PROPERTIES.getProperty("db.url"));
            Class.forName("com.mysql.cj.jdbc.Driver");
            log.info("MySQL JDBC Driver加载成功。 ");
        } catch (Exception e) {
            throw new RuntimeException("数据库配置初始化失败：" + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");
        log.debug("获取数据库连接。url={}, user={}", url, user);
        return DriverManager.getConnection(url, user, password);
    }

    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            log.error("数据库连接测试失败。message={}", e.getMessage(), e);
            return false;
        }
    }
}
