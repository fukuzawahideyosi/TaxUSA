package com.panda.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class ConnectionDao {

	private static Logger logger = Logger.getLogger(ConnectionDao.class.toString());

	public static Connection connection = null;

	public ConnectionDao() {
		super();

		if (connection == null) {
			try {
				connection = JdbcUtils.getconn();
			} catch (SQLException e) {
				e.printStackTrace();
				logger.info(e);
			}
		}

//		if (connectionTest() == false) {
//			try {
//				connection = JdbcUtils.getconn();
//			} catch (SQLException e) {
//				e.printStackTrace();
//				logger.info(e);
//			}
//		}

	}

	public boolean connectionTest() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String sql = "select 1";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				resultSet.getString("1");
				logger.info("connectionTestOK");
			}
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return false;
	}

}
