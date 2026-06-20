package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class m_sequenceDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(m_sequenceDao.class.toString());

	public String selectMax_yyyymmdd_count() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String yyyyMMdd = simpleDateFormat.format(new Date());

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String yyyymmdd_count = "";
		try {

			String sql = ""
					+ "SELECT NEXTVAL('t_etax_accountSeq') yyyymmdd_count"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				yyyymmdd_count = resultSet.getBigDecimal("yyyymmdd_count").toString();
				if (StringUtils.isEmpty(yyyymmdd_count) == true
						|| yyyyMMdd.equals(yyyymmdd_count.substring(0, 8)) == false) {

					//生成大于100小于1000的随机数的示例代码：
					Random random = new Random();
					int minValue = 100;
					int maxValue = 800;
					int randomValue = random.nextInt(maxValue - minValue) + minValue;

					yyyymmdd_count = yyyyMMdd + String.format("%06d", randomValue);
					sql = ""
							+ "SELECT SETVAL('t_etax_accountSeq', " + yyyymmdd_count + ")"
							+ "";
					preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

					logger.debug(preparedStatement.toString());
					preparedStatement.executeQuery();
					break;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return yyyymmdd_count;
	}


	public String selectMax_PDSK() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String PDSK = "";
		try {

			String sql = ""
					+ "SELECT NEXTVAL('t_xiaofeishui_shengaoSeq') PDSK"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				PDSK = resultSet.getBigDecimal("PDSK").toString();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return String.format("%04d", Integer.parseInt(PDSK));
	}

	public String selectMax_INSQ() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		String INSQ = "";
		try {

			String sql = ""
					+ "SELECT NEXTVAL('t_jct_shenqingSeq') INSQ"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				INSQ = resultSet.getBigDecimal("INSQ").toString();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return String.format("%04d", Integer.parseInt(INSQ));
	}

}
