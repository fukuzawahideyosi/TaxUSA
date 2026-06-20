package com.panda.dao;

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
public class LicenseDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(LicenseDao.class.toString());

	public String select(String user_id) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_license"
					+ " where user_id = '" + user_id + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("license_yyyymmdd");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}


}
