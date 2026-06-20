package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.panda.bean.t_user_info_shoujiBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_user_info_shoujiDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_user_info_shoujiDao.class.toString());


	public int INSERT(t_user_info_shoujiBean t_user_info_shoujiBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = ""
					+ "INSERT INTO t_user_info_shouji"
					+ " ( UPDATE_DATE"
					+ ",email"
					+ ",yyyymmdd_count"
					+ ",beikao"
					+ ") "
					+ "VALUES (?"
					+ ",?"
					+ ",?"
					+ ",?"
					+ ")";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_user_info_shoujiBean.getForm_mailarea());
			preparedStatement.setString(++i, t_user_info_shoujiBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_user_info_shoujiBean.getBeikao());

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}


	public t_user_info_shoujiBean SelectKeyValue(String key, String value) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_user_info_shoujiBean t_user_info_shoujiBean = new t_user_info_shoujiBean();

		try {

			String sql = ""
					+ "SELECT"
					+ "    *"
					+ " from"
					+ "    t_user_info_shouji"
					+ " where"
					+ "    " + key + "=?"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
//				if (StringUtils.isEmpty(t_user_info_shoujiBean.getYyyymmdd_count())) {
//					t_user_info_shoujiBean = new t_user_info_shoujiBean();
//					break;
//				}

				t_user_info_shoujiBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_user_info_shoujiBean.setForm_mailarea(resultSet.getString("email"));
				t_user_info_shoujiBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_user_info_shoujiBean.setBeikao(resultSet.getString("beikao"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_user_info_shoujiBean;
	}

	public t_user_info_shoujiBean Select_email_and_yyyymmdd_count(String email, String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_user_info_shoujiBean t_user_info_shoujiBean = new t_user_info_shoujiBean();

		try {

			String sql = ""
					+ "SELECT"
					+ "    *"
					+ " from"
					+ "    t_user_info_shouji"
					+ " where"
					+ "    email=? and yyyymmdd_count=?"
					+ ";"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(++i, email);
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
//				if (StringUtils.isEmpty(t_user_info_shoujiBean.getYyyymmdd_count())) {
//					t_user_info_shoujiBean = new t_user_info_shoujiBean();
//					break;
//				}

				t_user_info_shoujiBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_user_info_shoujiBean.setForm_mailarea(resultSet.getString("email"));
				t_user_info_shoujiBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_user_info_shoujiBean.setBeikao(resultSet.getString("beikao"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_user_info_shoujiBean;
	}

	public int UpdateKeyValue(String email, String key, String value) {
		if (value == null) {
			value = "";
		}

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_user_info_shouji SET " + key + "=? where email=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);
			preparedStatement.setString(2, email);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return i;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}


}
