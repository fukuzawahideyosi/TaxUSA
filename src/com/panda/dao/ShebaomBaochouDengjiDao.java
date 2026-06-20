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
public class ShebaomBaochouDengjiDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(ShebaomBaochouDengjiDao.class.toString());

	public int select(String yUEXIN) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "select * from m_shebao_baochou_dengji"
					+ " where FANWEI_MIN<= " + yUEXIN + " AND " + yUEXIN + "<FANWEI_MAX";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getInt("YUEE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return -1;
	}

}
