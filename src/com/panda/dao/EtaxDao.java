package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.JdbcUtils;
import com.panda.utils.dataUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class EtaxDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(EtaxDao.class.toString());

	public boolean INSERT(HashMap<String, String> HashMapKeyValueHtml, String max_yyyymmdd_count) throws SQLException {

		Statement stmt = null;

		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();


			ArrayList<String> sqlBatch = new ArrayList<>();
			dataUtils dataUtils = new dataUtils();
			for (String key : HashMapKeyValueHtml.keySet()) {
				String value = HashMapKeyValueHtml.get(key);
				if ("gNChiTodohuken".equals(key)
						|| "gTTodohuken".equals(key)
						|| "gDTodohuken".equals(key)) {
					value = dataUtils.HashMapTodohuken.get(value);
				}
				if ("gTZeimushomei".equals(key)) {
					value = dataUtils.HashMapZeimushomei.get(value);
				}

				String sql = ""
						+ "INSERT INTO t_etax_account ("
						+ "	UPDATE_DATE"
						+ "	, yyyymmdd_count"
						+ "	, html_id"
						+ "	, html_value"
						+ ")"
						+ "	value("
						+ "	now(3)"
						+ ", " + max_yyyymmdd_count + ""
						+ "	, '" + key + "'"
						+ "	, '" + value + "'"
						+ ")"
						+ "";
				logger.debug(sql);

				stmt.addBatch(sql);
				sqlBatch.add(sql);

			}


//			logger.debug(stmt.toString());
            for (String sql : sqlBatch) {
            	logger.debug(sql);
            }


			// 提交一批要执行的更新命令
			int[] updateCounts = stmt.executeBatch();
			for (int i : updateCounts) {
				logger.debug("SQLの更新件数は、" + i + "件です");
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			try {
				connection.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			JdbcUtils.close(null, stmt, connection);
		}
	}

	public LinkedHashMap<String, HashMap<String, String>> selectAll(String yyyymmdd_count) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		LinkedHashMap<String, HashMap<String, String>> HashMapKeyValueHtmlAll = new LinkedHashMap<String, HashMap<String, String>>();

		if (StringUtils.isEmpty(yyyymmdd_count) == true) {
			return HashMapKeyValueHtmlAll;
		}

		try {

			String sql = ""
					+ "SELECT * FROM t_etax_account"
					+ " WHERE yyyymmdd_count=" + yyyymmdd_count
					+ " ORDER BY yyyymmdd_count, html_id;"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();
				HashMapKeyValueHtml.put("yyyymmdd_count", resultSet.getBigDecimal("yyyymmdd_count").toString());
				HashMapKeyValueHtml.put("html_id", resultSet.getString("html_id"));
				HashMapKeyValueHtml.put("html_value",
						changeAlpHalfToFull(HashMapKeyValueHtml.get("html_id"), resultSet.getString("html_value")));
				HashMapKeyValueHtmlAll.put(
						HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"),
						HashMapKeyValueHtml);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return HashMapKeyValueHtmlAll;
	}

	/**
	 * <p>[概 要] 半角英字⇒全角英字への変換</p>
	 * <p>[詳 細] </p>
	 * <p>[備 考] </p>
	 * @param  str 変換対象文字列
	 * @return 変換後文字列
	 */
	public static String changeAlpHalfToFull(String key, String value) {

		if ("gHojinmeiKana".equals(key)
				|| "gHojinmei".equals(key)
				|| "gNChiTodohuken".equals(key)
				|| "gNChiAdd1".equals(key)
				|| "gNChiAdd2".equals(key)
				|| "gTTodohuken".equals(key)
				|| "gTZeimushomei".equals(key)
				|| "gTZeimushomei".equals(key)
				|| "gTZeimushomei".equals(key)
				|| "gDSeiKana".equals(key)
				|| "gDmeiKana".equals(key)
				|| "gDSei".equals(key)
				|| "gDmei".equals(key)
				|| "gDTodohuken".equals(key)
				|| "gDAdd1".equals(key)
				|| "gDAdd2".equals(key)

		) {
		} else {
			return value;

		}
		String result = null;
		if (value != null) {
			StringBuilder sb = new StringBuilder(value);
			for (int i = 0; i < sb.length(); i++) {
				int c = (int) sb.charAt(i);
				if ((c >= 0x41 && c <= 0x5A) || (c >= 0x61 && c <= 0x7A)) {
					sb.setCharAt(i, (char) (c + 0xFEE0));
				}
			}
			result = sb.toString();

			if ("gHojinmeiKana".equals(key)) {
				if (result.length() > 59) {
					result = result.substring(0, 59);
				}
			}
			if ("gHojinmei".equals(key)) {
				if (result.length() > 24) {
					result = result.substring(0, 24);
				}
			}
			if ("gNChiAdd1".equals(key)
					|| "gNChiAdd2".equals(key)
					|| "gDAdd1".equals(key)
					|| "gDAdd2".equals(key)

			) {
				if (result.length() > 50) {
					result = result.substring(0, 50);
				}
			}
			if ("gDSeiKana".equals(key)
					|| "gDmeiKana".equals(key)

			) {
				result = result.replace(" ", "").replace("　", "");
				if (result.length() > 22) {
					result = result.substring(0, 22);
				}
			}

			if ("gDSei".equals(key)
					|| "gDmei".equals(key)

			) {
				result = result.replace(" ", "").replace("　", "");
				if (result.length() > 14) {
					result = result.substring(0, 14);
				}
			}
		}
		return result;
	}

	public int DELETE(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_etax_account"
					+ " where yyyymmdd_count = ?"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, yyyymmdd_count);

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
