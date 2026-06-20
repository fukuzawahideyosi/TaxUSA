package com.cpsc.efiling.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CpscEfilingDbUtil {

    private static final Logger log = LogManager.getLogger(CpscEfilingDbUtil.class);

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = CpscEfilingDbUtil.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (in == null) {
                throw new RuntimeException("找不到 db.properties，请放到 src/main/resources 目录。");
            }

            PROPERTIES.load(in);
            log.info("db.properties加载成功。db.url={}", PROPERTIES.getProperty("db.url"));

            Class.forName("com.mysql.cj.jdbc.Driver");
            log.info("MySQL JDBC Driver加载成功。");
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库配置失败：" + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String url = PROPERTIES.getProperty("db.url");
        String user = PROPERTIES.getProperty("db.user");
        String password = PROPERTIES.getProperty("db.password");

        log.debug("获取数据库连接。url={}, user={}", url, user);
        return DriverManager.getConnection(url, user, password);
    }
}
