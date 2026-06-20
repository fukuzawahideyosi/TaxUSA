package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.panda.bean.t_etax_account_xiaofeishuiBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_account_xiaofeishuiDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_account_xiaofeishuiDao.class.toString());


	public int INSERT(t_etax_account_xiaofeishuiBean t_etax_account_xiaofeishuiBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = ""
					+ ""
					+ "INSERT INTO `psma`.`t_etax_account_xiaofeishui`"
					+ "(`UPDATE_DATE`,"
					+ "`UPDATE_USER`,"
					+ "`yyyymmdd_count`,"
					+ "`shengao_shiqishou_YYYYMMDD`,"
					+ "`shengao_shiqimo_YYYYMMDD`,"
					+ "`yuanze_or_jianyi`"
					+ ", keshui_maishang_2_xiaomai"
					+ ", zhongjian_nafu_shuie"
					+ ", zhongjian_nafu_durang"
					+ ", xiaoshoue_10"
					+ ", xiaoshoue_8"
					+ ", fapiao_10"
					+ ", fapiao_8"
					+ ", fapiao_10_20231001"
					+ ", fapiao_10_20261001"
					+ ", fapiao_8_20231001"
					+ ", fapiao_8_20261001"
					+ ", jinkou_xiaofeishui_guoshui"
					+ ", jinkou_xiaofeishui_dishui"
					+ ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getUPDATE_USER());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getShengao_shiqishou_YYYYMMDD());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getShengao_shiqimo_YYYYMMDD());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getYuanze_or_jianyi());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getKeshui_maishang_2_xiaomai());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getZhongjian_nafu_shuie());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getZhongjian_nafu_durang());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getXiaoshoue_10());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getXiaoshoue_8());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_10());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_8());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_10_20231001());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_10_20261001());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_8_20231001());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getFapiao_8_20261001());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getJinkou_xiaofeishui_guoshui());
			preparedStatement.setString(++i, t_etax_account_xiaofeishuiBean.getJinkou_xiaofeishui_dishui());

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public boolean Update_res_horyuu(String yyyymmdd_count, String destinationFolderZip, String horyuu) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_xiaofeishui SET horyuu=?, output_file=? where yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, horyuu);
			preparedStatement.setString(2, destinationFolderZip);
			preparedStatement.setString(3, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.info("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public int UpdateExKeyValueWhereKeyValue(String u_key, String u_value, String w_key, String w_value) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_xiaofeishui_shengao SET " + u_key + "=? where " + w_key + "=?"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(++i, u_value);

			preparedStatement.setString(++i, w_value);


			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.info("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return -1;
	}


	public t_etax_account_xiaofeishuiBean select_where_yyyymmdd_count(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_xiaofeishuiBean t_etax_account_xiaofeishuiBean = new t_etax_account_xiaofeishuiBean();
		try {

			String sql = ""
					+ "SELECT * "
					+ " FROM t_etax_account_xiaofeishui"
					+ " where yyyymmdd_count='" + yyyymmdd_count + "'"
					+ " ORDER BY yyyymmdd_count desc"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_xiaofeishuiBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_xiaofeishuiBean.setUPDATE_USER(resultSet.getString("UPDATE_USER"));
				t_etax_account_xiaofeishuiBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_xiaofeishuiBean.setShengao_shiqishou_YYYYMMDD(resultSet.getString("shengao_shiqishou_YYYYMMDD"));
				t_etax_account_xiaofeishuiBean.setShengao_shiqimo_YYYYMMDD(resultSet.getString("shengao_shiqimo_YYYYMMDD"));
				t_etax_account_xiaofeishuiBean.setYuanze_or_jianyi(resultSet.getString("yuanze_or_jianyi"));
				t_etax_account_xiaofeishuiBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_account_xiaofeishuiBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_account_xiaofeishuiBean.setHoryuu(resultSet.getString("horyuu"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_account_xiaofeishuiBean;
	}

	public void delete_where_yyyymmdd_count(String yyyymmdd_count) {

			PreparedStatement preparedStatement = null;

			try {

				String sql = ""
						+ "DELETE FROM t_etax_account_xiaofeishui"
						+ " where yyyymmdd_count = ?"
						+ "";

				preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
				preparedStatement.setString(1, yyyymmdd_count);
				logger.debug(preparedStatement.toString());
				int i = preparedStatement.executeUpdate();
				logger.debug("SQL " + i);

			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				JdbcUtils.close(null, preparedStatement, connection);
			}
		}
}
