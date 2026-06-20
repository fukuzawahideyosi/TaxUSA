package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class User_infoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(User_infoDao.class.toString());

	public User_infoBean select(String user_id) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User_infoBean User_infoBean = new User_infoBean();
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
				User_infoBean.setUser_id(resultSet.getString("user_id"));
				User_infoBean.setLicense_yyyymmdd(resultSet.getString("license_yyyymmdd"));
				User_infoBean.setPermissions(resultSet.getString("permissions"));
				User_infoBean.setGroup_id(resultSet.getString("group_id"));
				User_infoBean.setPw(resultSet.getString("pw"));
				User_infoBean.setLicense_url(resultSet.getString("license_url"));
				User_infoBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				User_infoBean.setEmail(resultSet.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return User_infoBean;
	}

	public User_infoBean selectByYaoqing_no(String yaoqing_no) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User_infoBean User_infoBean = new User_infoBean();
		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_license"
					+ " where yaoqing_no = '" + yaoqing_no + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User_infoBean.setUser_id(resultSet.getString("user_id"));
				User_infoBean.setLicense_yyyymmdd(resultSet.getString("license_yyyymmdd"));
				User_infoBean.setPermissions(resultSet.getString("permissions"));
				User_infoBean.setGroup_id(resultSet.getString("group_id"));
				User_infoBean.setPw(resultSet.getString("pw"));
				User_infoBean.setLicense_url(resultSet.getString("license_url"));
				User_infoBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				User_infoBean.setEmail(resultSet.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return User_infoBean;
	}
	public LinkedHashMap<String, User_infoBean> selectByGroup_id(String group_id) {
		LinkedHashMap<String, User_infoBean> LinkedHashMap=new LinkedHashMap<String, User_infoBean>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User_infoBean User_infoBean = new User_infoBean();
		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_license"
					+ "";
			if (StringUtils.isEmpty(group_id) == false) {
				sql = sql
						+ " where group_id = '" + group_id + "'"
						+ "";

			}

			sql = sql
					+ " order by user_id asc"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User_infoBean.setUser_id(resultSet.getString("user_id"));
				User_infoBean.setLicense_yyyymmdd(resultSet.getString("license_yyyymmdd"));
				User_infoBean.setPermissions(resultSet.getString("permissions"));
				User_infoBean.setGroup_id(resultSet.getString("group_id"));
				LinkedHashMap.put(User_infoBean.getUser_id(), User_infoBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap;
	}

	public User_infoBean selectByTiaojian(String key, String user_id) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		User_infoBean User_infoBean = new User_infoBean();
		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_license"
					+ " where "+ key +" = '" + user_id + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				User_infoBean.setUser_id(resultSet.getString("user_id"));
				User_infoBean.setLicense_yyyymmdd(resultSet.getString("license_yyyymmdd"));
				User_infoBean.setPermissions(resultSet.getString("permissions"));
				User_infoBean.setGroup_id(resultSet.getString("group_id"));
				User_infoBean.setPw(resultSet.getString("pw"));
				User_infoBean.setLicense_url(resultSet.getString("license_url"));
				User_infoBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				User_infoBean.setEmail(resultSet.getString("email"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return User_infoBean;
	}
}
