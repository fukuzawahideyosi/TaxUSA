package com.panda.dao;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.csv.CSVParser;
import org.apache.log4j.Logger;

import com.panda.bean.t_kuaiji_dBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_kuaiji_dDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_kuaiji_dDao.class.toString());

	public int select_SIZE_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT count(1) SIZE"
					+ "  FROM t_kuaiji_d" + yyyy
					+ " WHERE yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();


			while (resultSet.next()) {
				return resultSet.getInt("SIZE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return -1;
	}


	public int INSERT(String yyyymmdd_count, String yyyy, Map<String, Map<String, String>> excelDataHashMap19) throws Exception {

		Reader reader = null;
		CSVParser csvParser = null;
		PreparedStatement preparedStatement = null;
		try {

			String sql = generateInsertSQL(yyyy, header);
			preparedStatement = connection.prepareStatement(sql);

			int count_new = 0;
			int batchSize = 0;

			int[] count_executeBatch;

			for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19.entrySet()) {
				Map<String, String> excelValue = entry_excelDataHashMap.getValue();
				String key = excelValue.get("1");

				String[] nextLine = new String[excelValue.size()];
				int j = 0;
				for (String value : excelValue.values()) {
					nextLine[j++] = value;
				}

				++count_new;



				int i = 0;
				preparedStatement.setString(++i, yyyymmdd_count);
				preparedStatement.setString(++i, yyyy);
//					logger.debug("setParameters S");
				i = setParameters(preparedStatement, nextLine, i);
//					logger.debug("setParameters E");

//					logger.debug(preparedStatement.toString());

				logger.debug(preparedStatement.toString());
				preparedStatement.addBatch();
				batchSize++;

				if (batchSize % 1000 == 0) {
//						logger.debug("executeBatch S");
					count_executeBatch = preparedStatement.executeBatch();
//						logger.debug("executeBatch E");
					logger.debug("count_all " + excelDataHashMap19.size() + " count_new " + count_new + " executeBatch " + count_executeBatch.length);
					batchSize = 0;
				}

			}

			count_executeBatch = preparedStatement.executeBatch(); // Execute the remaining batch
			logger.debug("count_all " + excelDataHashMap19.size() + " count_new " + count_new + " executeBatch " + count_executeBatch.length);

			return count_new - 1;
		} catch (Exception e) {
			throw e;
		} finally {
			if (csvParser != null) {
				csvParser.close();
			}
			if (reader != null) {
				reader.close();
			}
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}


	public LinkedHashMap<String, t_kuaiji_dBean> selectAll_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_kuaiji_dBean> LinkedHashMap_t_kuaiji_dBean = new LinkedHashMap<String, t_kuaiji_dBean>();
		try {
			/*
  `UPDATE_DATE` timestamp(6) NOT NULL,
  `yyyymmdd_count` bigint NOT NULL,
  `yyyy` varchar(45) NOT NULL,
  `declaration_date` date NOT NULL COMMENT '申告日期',
  `declaration_number` varchar(255) NOT NULL COMMENT '申告番号',
  `product_name` varchar(255) NOT NULL COMMENT '品名',
  `declared_price_cif` decimal(10,2) NOT NULL COMMENT '申告価格（CIF）',
  `consumption_tax_national` decimal(10,2) NOT NULL COMMENT '消費税（国税部分）',
  `consumption_tax_local` decimal(10,2) NOT NULL COMMENT '消費税（地方税部分）',
			 */



			String sql = ""
					+ "SELECT *"
					+ "  FROM t_kuaiji_d"
					+ " where yyyymmdd_count=? and yyyy=?"
					+ " ORDER BY UPDATE_DATE ASC"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_kuaiji_dBean t_kuaiji_dBean = new t_kuaiji_dBean();
				t_kuaiji_dBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_kuaiji_dBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_kuaiji_dBean.setYyyy(resultSet.getString("yyyy"));

				t_kuaiji_dBean.setDeclaration_date(String.valueOf(resultSet.getString("declaration_date")));
				t_kuaiji_dBean.setDeclaration_number(String.valueOf(resultSet.getString("declaration_number")));
				t_kuaiji_dBean.setProduct_name(String.valueOf(resultSet.getString("product_name")));
				t_kuaiji_dBean.setDeclared_price_cif(String.valueOf(resultSet.getDouble("declared_price_cif")));
				t_kuaiji_dBean.setConsumption_tax_national(String.valueOf(resultSet.getDouble("consumption_tax_national")));
				t_kuaiji_dBean.setConsumption_tax_local(String.valueOf(resultSet.getDouble("consumption_tax_local")));

				LinkedHashMap_t_kuaiji_dBean.put("" + ++count, t_kuaiji_dBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_kuaiji_dBean;
	}




	public void delete_where_yyyymmdd_count_and_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_kuaiji_d"
					+ " where yyyymmdd_count = ? and yyyy=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public void delete_where_yyyymmdd_count(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_kuaiji_d"
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
	private String generateInsertSQL(String yyyy, String[] header) {
		// Assuming 'header' contains column names
		String columns = String.join(",", header);
		String placeholders = String.join(",", java.util.Collections.nCopies(header.length - 1, "?"));

		return "INSERT INTO t_kuaiji_d (" + columns + ") VALUES (now(3), " + placeholders + ")";
	}

	private int setParameters(PreparedStatement preparedStatement, String[] values, int j) throws Exception {
		for (int i = 0; i < values.length; i++) {
//			if (i == 0) {
//				Date parsedDate = new SimpleDateFormat("mm/dd/yy").parse(values[i]);
//	            String formattedDate = new SimpleDateFormat("yyyyMMdd").format(parsedDate);
//				values[i] =  formattedDate;
//			}
			preparedStatement.setString(++j, values[i]);
		}
		return j;
	}

	// 字段名列表
	String[] header = {
			"UPDATE_DATE",
			"yyyymmdd_count",
			"yyyy",
			"declaration_date",
			"declaration_number",
			"product_name",
			"consumption_tax_national",
			"consumption_tax_local"
	};
/*
CREATE TABLE t_kuaiji_d (
  UPDATE_DATE timestamp(6) NOT NULL,
  yyyymmdd_count bigint NOT NULL,
  yyyy varchar(45) NOT NULL,
    declaration_date DATE NOT NULL COMMENT '申告日期',
    declaration_number VARCHAR(255) NOT NULL COMMENT '申告番号',
    product_name VARCHAR(255) NOT NULL COMMENT '品名',
    declared_price_cif DECIMAL(10, 2) NOT NULL COMMENT '申告価格（CIF）',
    consumption_tax_national DECIMAL(10, 2) NOT NULL COMMENT '消費税（国税部分）',
    consumption_tax_local DECIMAL(10, 2) NOT NULL COMMENT '消費税（地方税部分）',
  KEY i_kuaiji_d (`yyyymmdd_count`,`yyyy`)
);
 */

}
