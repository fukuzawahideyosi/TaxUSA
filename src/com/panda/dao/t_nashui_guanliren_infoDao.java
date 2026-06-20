package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.panda.bean.t_nashui_guanliren_infoBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_nashui_guanliren_infoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_nashui_guanliren_infoDao.class.toString());

	public LinkedHashMap<String, t_nashui_guanliren_infoBean> selectAll() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_nashui_guanliren_infoBean> LinkedHashMap_t_nashui_guanliren_infoBean = new LinkedHashMap<String, t_nashui_guanliren_infoBean>();
		try {

			String sql = ""
					+ "SELECT *"
					+ "  FROM t_nashui_guanliren_info"
					+ "";



			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_nashui_guanliren_infoBean t_nashui_guanliren_infoBean = new t_nashui_guanliren_infoBean();
				t_nashui_guanliren_infoBean.setCompanyName(resultSet.getString("CompanyName"));
				t_nashui_guanliren_infoBean.setDaibiaoName(resultSet.getString("DaibiaoName"));
				t_nashui_guanliren_infoBean.setAddress(resultSet.getString("address"));
				t_nashui_guanliren_infoBean.setNashuidi(resultSet.getString("nashuidi"));
				t_nashui_guanliren_infoBean.setShuiwushu(resultSet.getString("shuiwushu"));
				t_nashui_guanliren_infoBean.setShuiwushu_fanhao(resultSet.getString("shuiwushu_fanhao"));

				LinkedHashMap_t_nashui_guanliren_infoBean.put(t_nashui_guanliren_infoBean.getCompanyName(), t_nashui_guanliren_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_nashui_guanliren_infoBean;
	}


}
