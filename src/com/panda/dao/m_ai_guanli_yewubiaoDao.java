package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class m_ai_guanli_yewubiaoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(m_ai_guanli_yewubiaoDao.class.toString());

	public LinkedHashMap<String, LinkedHashMap<String, String>> selectAll() {
		// 第一层 LinkedHashMap，key=biaoming，value=第二层 LinkedHashMap
		LinkedHashMap<String, LinkedHashMap<String, String>> All_guanli_yewubiao_LinkedHashMap = new LinkedHashMap<>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			String sql = ""
					+ "SELECT * "
					+ "FROM m_ai_guanli_yewubiao "
					+ "ORDER BY biaoming, etax_shiyangshu ASC";

			preparedStatement = connection.prepareStatement(sql);
			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				String biaoming = resultSet.getString("biaoming").replace("AI_T_", "");
				String etax_shiyangshu = resultSet.getString("etax_shiyangshu");
				String shiyong = resultSet.getString("shiyong");

				if (StringUtils.isEmpty(biaoming) == true) {
					biaoming = "新建表";
				}

				// 获取或创建第二层 LinkedHashMap
				LinkedHashMap<String, String> etax_shiyangshu_Map = All_guanli_yewubiao_LinkedHashMap.get(biaoming);
				if (etax_shiyangshu_Map == null) {
					etax_shiyangshu_Map = new LinkedHashMap<>();
					All_guanli_yewubiao_LinkedHashMap.put(biaoming, etax_shiyangshu_Map);
				}

				// 放入第二层 Map
				etax_shiyangshu_Map.put(etax_shiyangshu, shiyong);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}

		return All_guanli_yewubiao_LinkedHashMap;

	}


	public LinkedHashMap<String, String> selectAll_guanli_yewubiao() {
		LinkedHashMap<String, String> All_guanli_yewubiao_LinkedHashMap = new LinkedHashMap<String, String>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT etax_shiyangshu "
					+ "  FROM m_ai_guanli_yewubiao"
					+ " GROUP BY etax_shiyangshu"
					+ ""
					;

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				All_guanli_yewubiao_LinkedHashMap.put(resultSet.getString("etax_shiyangshu"), resultSet.getString("etax_shiyangshu"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return All_guanli_yewubiao_LinkedHashMap;
	}

	public LinkedHashMap<String, String> select(String biaoming, String etax_shiyangshu) {
		LinkedHashMap<String, String> m_ai_guanli_yewubiaoBean_LinkedHashMap = new LinkedHashMap<String, String>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "select * from m_ai_guanli_yewubiao "
					+ " WHERE biaoming = ? AND etax_shiyangshu = ?"
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(++i, biaoming);
			preparedStatement.setString(++i, etax_shiyangshu);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				m_ai_guanli_yewubiaoBean_LinkedHashMap.put(resultSet.getString("etax_shiyangshu"), resultSet.getString("biaoming"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return m_ai_guanli_yewubiaoBean_LinkedHashMap;
	}

	public LinkedHashMap<String, String> select(String biaoming) {
		LinkedHashMap<String, String> m_ai_guanli_yewubiaoBean_LinkedHashMap = new LinkedHashMap<String, String>();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "select * from m_ai_guanli_yewubiao "
					+ " WHERE biaoming = ? "
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(++i, biaoming);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				m_ai_guanli_yewubiaoBean_LinkedHashMap.put(resultSet.getString("etax_shiyangshu"), resultSet.getString("shiyong"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return m_ai_guanli_yewubiaoBean_LinkedHashMap;
	}

	// 插入方法
	public int insert(String user_id, String biaoming, String etax_shiyangshu, String shiyong) throws SQLException {
		String updateSystem = "";
		String sql = "INSERT INTO m_ai_guanli_yewubiao "
				+ "(biaoming, etax_shiyangshu, UPDATE_DATE, UPDATE_SYSTEM, user_id, shiyong) "
				+ "VALUES (?, ?, NOW(6), ?, ?, ?)";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			int i = 0;
			ps.setString(++i, biaoming);
			ps.setString(++i, etax_shiyangshu);
			ps.setString(++i, updateSystem);
			ps.setString(++i, user_id);
			ps.setString(++i, shiyong);

			logger.debug(ps.toString());
			int count = ps.executeUpdate();
			logger.debug("SQL " + count);
			return count;
		} finally {
			JdbcUtils.close(null, null, connection);
		}
	}

	// 更新方法
	public int update(String user_id, String biaoming, String etax_shiyangshu, String shiyong) throws SQLException {
		String updateSystem = "";
		String sql = "UPDATE m_ai_guanli_yewubiao SET "
				+ "UPDATE_DATE = NOW(6), "
				+ "UPDATE_SYSTEM = ?, "
				+ "user_id = ?, "
				+ "shiyong = ? "
				+ "WHERE biaoming = ? AND etax_shiyangshu = ?";

		try (PreparedStatement ps = connection.prepareStatement(sql)) {
			int i = 0;
			ps.setString(++i, updateSystem);
			ps.setString(++i, user_id);
			ps.setString(++i, shiyong);
			ps.setString(++i, biaoming);
			ps.setString(++i, etax_shiyangshu);

			logger.debug(ps.toString());
			int count = ps.executeUpdate();
			logger.debug("SQL " + count);
			return count;
		} finally {
			JdbcUtils.close(null, null, connection);
		}
	}

}
