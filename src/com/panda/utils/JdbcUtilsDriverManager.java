package com.panda.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:31
 */
public class JdbcUtilsDriverManager {

	private static Logger logger = Logger.getLogger(JdbcUtilsDriverManager.class.toString());

    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String dv = null;

    static{
        Properties prop = new Properties();
        InputStream in = JdbcUtilsDriverManager.class.getResourceAsStream("/db.properties");

        try {
//            prop.load(in);
//            url = prop.getProperty("url");
//            user = prop.getProperty("user");
//            password = prop.getProperty("password");
//            dv = prop.getProperty("driver");


            url = "jdbc:mysql://101.132.188.190:3306/psma?autoReconnect=true&characterEncoding=UTF-8&connectTimeout=300000&socketTimeout=300000";
            user = "ps";
            password = "Hu4U9s4FzdmTps";
            dv = "com.mysql.cj.jdbc.Driver";

            //注册驱动
            Class.forName(dv);

//        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static Connection getconn() throws SQLException {
    	logger.debug("DriverManager.getConnection S");
        Connection connection = DriverManager.getConnection(url,user,password);
        logger.debug("DriverManager.getConnection E");
        return connection;

    }

    public static void close(ResultSet resultSet, Statement statement, Connection connection){
        if(resultSet != null) {
            try {
            	resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    	if(statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    	/*
        if(connection != null){
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        }
        */
    }

    public static void close(PreparedStatement preparedStatement, Connection connection, ResultSet result) {
        if(preparedStatement != null){
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
//            try {
//                connection.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        }
        if(result != null){
            try {
                result.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
