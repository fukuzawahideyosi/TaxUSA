package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class ShebaoBaoxianlvDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(ShebaoBaoxianlvDao.class.toString());

	public HashMap<String, String> select() {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		HashMap<String, String> ShebaoHashMap = new HashMap<String, String>();
		try {

			String sql = ""
					+ "select * from m_shebao_baoxianlv"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				ShebaoHashMap.put(resultSet.getString("DIYU"), resultSet.getFloat("JIANKANG_BAOXIANLV")
						+ "," + resultSet.getFloat("YANGLAO_BAOXIANLV")
						+ "," + resultSet.getFloat("JIANKANG_BAOXIANLV_40"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return ShebaoHashMap;
	}

}
