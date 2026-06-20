package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.panda.bean.t_jct_shenqingBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_jct_shenqingDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_jct_shenqingDao.class.toString());

	public int INSERT(t_jct_shenqingBean t_jct_shenqingBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = "INSERT INTO t_jct_shenqing ("
			        + " UPDATE_DATE"
			        + ", yyyymmdd_count"
			        + ", INSQ"
			        + ", tianxie_YYYY"

			        + ", riben_kaishi_shiye_YYYY"
			        + ", riben_kaishi_shiye_MM"
			        + ", riben_kaishi_shiye_DD"
			        + ", xiaoshouerYYYY_2"
			        + ", xiaoshouerYYYY_1_half"

			        + ", keshui_or_mianshui"
			        + ", YYYY_1"
			        + ", keshui_shiyezhe_wenshu"
			        + ") "
			        + "VALUES ("
			        + " ?, ?, ?, ?"
			        + ", ?, ?, ?, ?, ?"
			        + ", ?, ?, ?"
			        + ")";



			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_jct_shenqingBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_jct_shenqingBean.getINSQ());
			preparedStatement.setString(++i, t_jct_shenqingBean.getTianxie_YYYY());


			preparedStatement.setString(++i, t_jct_shenqingBean.getRiben_kaishi_shiye_YYYY());
			preparedStatement.setString(++i, t_jct_shenqingBean.getRiben_kaishi_shiye_MM());
			preparedStatement.setString(++i, t_jct_shenqingBean.getRiben_kaishi_shiye_DD());
			preparedStatement.setString(++i, t_jct_shenqingBean.getXiaoshouerYYYY_2());
			preparedStatement.setString(++i, t_jct_shenqingBean.getXiaoshouerYYYY_1_half());

			preparedStatement.setString(++i, t_jct_shenqingBean.getKeshui_or_mianshui());
			preparedStatement.setString(++i, t_jct_shenqingBean.getYYYY_1());
			preparedStatement.setString(++i, t_jct_shenqingBean.getKeshui_shiyezhe_wenshu());


			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public void delete_where_yyyymmdd_count(String yyyymmdd_count) {


		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_jct_shenqing"
					+ " where yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}


	}

	public t_jct_shenqingBean SelectKeyValue(String key, String value) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_jct_shenqingBean t_jct_shenqingBean = new t_jct_shenqingBean();

		try {

			String sql = ""
					+ "SELECT"
					+ "    *"
					+ " from"
					+ "    t_jct_shenqing"
					+ " where"
					+ "    " + key + "=?"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				t_jct_shenqingBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_jct_shenqingBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));

				t_jct_shenqingBean.setINSQ(resultSet.getString("INSQ"));
				t_jct_shenqingBean.setTianxie_YYYY(resultSet.getString("tianxie_YYYY"));

				t_jct_shenqingBean.setRiben_kaishi_shiye_YYYY(resultSet.getString("riben_kaishi_shiye_YYYY"));
				t_jct_shenqingBean.setRiben_kaishi_shiye_MM(resultSet.getString("riben_kaishi_shiye_MM"));
				t_jct_shenqingBean.setRiben_kaishi_shiye_DD(resultSet.getString("riben_kaishi_shiye_DD"));

				t_jct_shenqingBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_jct_shenqingBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_jct_shenqingBean.setKeshui_or_mianshui(resultSet.getString("keshui_or_mianshui"));
				t_jct_shenqingBean.setYYYY_1(resultSet.getString("YYYY_1"));
				t_jct_shenqingBean.setKeshui_shiyezhe_wenshu(resultSet.getString("keshui_shiyezhe_wenshu"));

//				t_jct_shenqingBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
//				t_jct_shenqingBean.setTel_country(resultSet.getString("tel_country"));
//				t_jct_shenqingBean.setTel_1(resultSet.getString("tel_1"));
//				t_jct_shenqingBean.setTel_2(resultSet.getString("tel_2"));
//				t_jct_shenqingBean.setTel_3(resultSet.getString("tel_3"));
//				t_jct_shenqingBean.setZhice_ziben(resultSet.getString("zhice_ziben"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_jct_shenqingBean;
	}


	public static String CREATE_TABL = ""
			+ "CREATE TABLE `t_jct_shenqing` (\r\n"
			+ "  `UPDATE_DATE` timestamp(6) NOT NULL,\r\n"
			+ "  `yyyymmdd_count` bigint NOT NULL,\r\n"
			+ "  `INSQ` varchar(45) DEFAULT NULL COMMENT 'JCT管理番号',\r\n"
			+ "  `riben_kaishi_shiye_YYYY` varchar(4) DEFAULT NULL COMMENT '在日本开始事业的年',\r\n"
			+ "  `riben_kaishi_shiye_MM` varchar(2) DEFAULT NULL COMMENT '在日本开始事业的月',\r\n"
			+ "  `riben_kaishi_shiye_DD` varchar(2) DEFAULT NULL COMMENT '在日本开始事业的日',\r\n"
			+ "  `xiaoshouerYYYY_2` varchar(45) DEFAULT NULL COMMENT '基准期间在日本的课税销售额（含税日元金额）',\r\n"
			+ "  `xiaoshouerYYYY_1_half` varchar(45) DEFAULT NULL COMMENT '特定期间在日本的课税销售额（含税日元金额）',\r\n"
			+ "  `keshui_or_mianshui` varchar(45) DEFAULT NULL COMMENT '该主体此刻是课税的还是免税的',\r\n"
			+ "  `YYYY_1` varchar(45) DEFAULT NULL COMMENT '该主体是第一年在日本开始事业吗',\r\n"
			+ "  `keshui_shiyezhe_wenshu` varchar(45) DEFAULT NULL COMMENT '该主体应该递交课税事业者文件吗',\r\n"
			+ "  PRIMARY KEY (`yyyymmdd_count`),\r\n"
			+ "  UNIQUE KEY `INSQ_UNIQUE` (`INSQ`)\r\n"
			+ ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
}
