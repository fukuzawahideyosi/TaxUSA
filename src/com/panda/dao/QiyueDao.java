package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.panda.bean.QiyueBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class QiyueDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(QiyueDao.class.toString());

	//此方法实现注册功能，向数据库中写入新用户的信息
	public int addQiyue(String qIYUE_ID) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "insert into t_qiyue(CREATE_DATE,CREATE_SYSTEM,UPDATE_DATE,UPDATE_SYSTEM,QIYUE_ID)"
					+ "values(now(3),'" + QiyueDao.class.toString() + "',now(3),'" + QiyueDao.class.toString() + "'"
					+ ",'" + qIYUE_ID + "'"
					+ ");";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

	}

	public QiyueBean selectQiyue(String qIYUE_ID) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;


		QiyueBean QiyueBean = new QiyueBean();

		try {

			String sql = "select * from t_qiyue where QIYUE_ID='" + qIYUE_ID + "'";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {

				QiyueBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				QiyueBean.setQIYUE_ID(resultSet.getString("QIYUE_ID"));
				QiyueBean.setJP_ZHUANGTAI(resultSet.getString("JP_ZHUANGTAI"));
				QiyueBean.setJP_NAME(resultSet.getString("JP_NAME"));
				QiyueBean.setJP_UPDATE_DATE(resultSet.getString("JP_UPDATE_DATE"));
				QiyueBean.setCH_ZHUANGTAI(resultSet.getString("CH_ZHUANGTAI"));
				QiyueBean.setCH_NAME(resultSet.getString("CH_NAME"));
				QiyueBean.setCH_UPDATE_DATE(resultSet.getString("CH_UPDATE_DATE"));

			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return QiyueBean;
	}

	public int updateQiyueJP(QiyueBean qiyueBean) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "update t_qiyue"
					+ " set UPDATE_DATE=now(3), UPDATE_SYSTEM='" + QiyueDao.class.toString() + "'"
					+ "   , JP_ZHUANGTAI='" + qiyueBean.getJP_ZHUANGTAI() + "'"
					+ "   , JP_NAME='" + qiyueBean.getJP_NAME() + "'"
					+ "   , JP_UPDATE_DATE=now(3)"
					+ " where QIYUE_ID='" + qiyueBean.getQIYUE_ID() + "'"
					+ ";";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);


			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

	}

	public int updateQiyueCH(QiyueBean qiyueBean) throws SQLException {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {
			String sql = "update t_qiyue"
					+ " set UPDATE_DATE=now(3), UPDATE_SYSTEM='" + QiyueDao.class.toString() + "'"
					+ "   , CH_ZHUANGTAI='" + qiyueBean.getCH_ZHUANGTAI() + "'"
					+ "   , CH_NAME='" + qiyueBean.getCH_NAME() + "'"
					+ "   , CH_UPDATE_DATE=now(3)"
					+ " where QIYUE_ID='" + qiyueBean.getQIYUE_ID() + "'"
					+ ";";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

	}

}
