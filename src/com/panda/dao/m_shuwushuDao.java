package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.panda.bean.m_shuwushuBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class m_shuwushuDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(m_shuwushuDao.class.toString());

	public LinkedHashMap<String, String> selectAll() {
		LinkedHashMap<String, String> m_shuwushuBean_LinkedHashMap = new LinkedHashMap<String, String>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "select id, REPLACE(REPLACE(name, '　', ''), ' ', '') AS name from m_shuwushu "
					+ " ORDER BY id asc"
					+ "";

			preparedStatement = connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				m_shuwushuBean_LinkedHashMap.put(resultSet.getString("id"), resultSet.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return m_shuwushuBean_LinkedHashMap;
	}
	public m_shuwushuBean select(String id) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		m_shuwushuBean m_shuwushuBean = new m_shuwushuBean();
		try {

			String sql = ""
					+ "select * from m_shuwushu where id=?"
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(++i, id);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				m_shuwushuBean.setId(resultSet.getString("id"));
				m_shuwushuBean.setName(resultSet.getString("name"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return m_shuwushuBean;
	}

}
