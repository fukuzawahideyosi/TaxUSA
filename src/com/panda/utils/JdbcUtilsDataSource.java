package com.panda.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:31
 */
public class JdbcUtilsDataSource {

	private static Logger logger = Logger.getLogger(JdbcUtilsDataSource.class.toString());

    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String dv = null;
	private static BasicDataSource dataSource;

    static{
        Properties prop = new Properties();
        InputStream in = JdbcUtilsDataSource.class.getResourceAsStream("/db.properties");


//            prop.load(in);
//            url = prop.getProperty("url");
//            user = prop.getProperty("user");
//            password = prop.getProperty("password");
//            dv = prop.getProperty("driver");


//            url = "jdbc:mysql://101.132.188.190:3306/psma?autoReconnect=true&characterEncoding=UTF-8&connectTimeout=5000&socketTimeout=10000&rewriteBatchedStatements=true";
            url = "jdbc:mysql://8.211.146.202:3306/psma?autoReconnect=true&characterEncoding=UTF-8&connectTimeout=5000&socketTimeout=10000&rewriteBatchedStatements=true";



//            url = "jdbc:mysql://8.211.146.202:3306/psma"
//            	    + "?useSSL=false"
//            	    + "&allowPublicKeyRetrieval=true"
//            	    + "&serverTimezone=Asia/Tokyo"
//            	    + "&characterEncoding=UTF-8"
//            	    + "&autoReconnect=true"
//            	    + "&connectTimeout=10000"
//            	    + "&socketTimeout=600000"
//            	    + "&tcpKeepAlive=true"
//            	    + "&rewriteBatchedStatements=true"
//            	    + "&useServerPrepStmts=true"
//            	    + "&cachePrepStmts=true";


            user = "ps";
            password = "Hu4U9s4FzdmTps$";
            dv = "com.mysql.cj.jdbc.Driver";


    		dataSource = new BasicDataSource();
    		dataSource.setDriverClassName(dv);
    		dataSource.setUrl(url);
    		dataSource.setUsername(user);
    		dataSource.setPassword(password);

    		dataSource.setInitialSize(20); // 增加初始连接数
    		dataSource.setMaxTotal(100);   // 增加最大连接数
    		dataSource.setMaxIdle(20);     // 增加最大空闲连接数
    		dataSource.setMinIdle(10);      // 保持最小空闲连接数

    		//确保连接池能够及时关闭长时间空闲的连接。可以通过设置 removeAbandoned 和 removeAbandonedTimeout 参数来实现：
    		dataSource.setRemoveAbandonedOnBorrow(true);
    		dataSource.setRemoveAbandonedOnMaintenance(true);
    		dataSource.setRemoveAbandonedTimeout(120); // 60秒

    		dataSource.setMaxConnLifetimeMillis(60000); // 60秒
    		dataSource.setRemoveAbandonedOnBorrow(true);
    		dataSource.setRemoveAbandonedOnMaintenance(true);
    		dataSource.setRemoveAbandonedTimeout(120); // 60秒
    }

    public static Connection getconn() throws SQLException {
    	logger.debug("dataSource.getConnection S");
    	Connection connection = dataSource.getConnection();
    	logger.debug("dataSource.getConnection E");
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
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
