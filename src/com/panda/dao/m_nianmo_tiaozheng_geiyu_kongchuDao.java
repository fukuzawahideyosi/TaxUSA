package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.panda.bean.m_nianmo_tiaozheng_geiyu_kongchuBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class m_nianmo_tiaozheng_geiyu_kongchuDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(m_nianmo_tiaozheng_geiyu_kongchuDao.class.toString());

	public m_nianmo_tiaozheng_geiyu_kongchuBean select(long geiyu) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		m_nianmo_tiaozheng_geiyu_kongchuBean m_nianmo_tiaozheng_geiyu_kongchuBean = new m_nianmo_tiaozheng_geiyu_kongchuBean();
		try {

			String sql = ""
					+ "select * from m_nianmo_tiaozheng_geiyu_kongchu"
					+ " where geiyu_yishang<=? and ?<geiyu_weiman"
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setLong(++i, geiyu);
			preparedStatement.setLong(++i, geiyu);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				m_nianmo_tiaozheng_geiyu_kongchuBean.setCREATE_DATE(resultSet.getString("CREATE_DATE"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setCREATE_SYSTEM(resultSet.getString("CREATE_SYSTEM"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setUPDATE_SYSTEM(resultSet.getString("UPDATE_SYSTEM"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setGeiyu_yishang(resultSet.getString("geiyu_yishang"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setGeiyu_weiman(resultSet.getString("geiyu_weiman"));
				m_nianmo_tiaozheng_geiyu_kongchuBean.setKongchuhou_geiyu(resultSet.getString("kongchuhou_geiyu"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return m_nianmo_tiaozheng_geiyu_kongchuBean;
	}

}
