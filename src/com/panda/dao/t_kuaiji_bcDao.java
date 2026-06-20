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

import com.panda.bean.t_kuaiji_bcBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_kuaiji_bcDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_kuaiji_bcDao.class.toString());

	public int select_SIZE_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT count(1) SIZE"
					+ "  FROM t_kuaiji_bc" + yyyy
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


	public LinkedHashMap<String, t_kuaiji_bcBean> selectAll_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_kuaiji_bcBean> LinkedHashMap_t_kuaiji_bcBean = new LinkedHashMap<String, t_kuaiji_bcBean>();
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
					+ "  FROM t_kuaiji_bc"
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
				t_kuaiji_bcBean t_kuaiji_bcBean = new t_kuaiji_bcBean();
				t_kuaiji_bcBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_kuaiji_bcBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_kuaiji_bcBean.setYyyy(resultSet.getString("yyyy"));

				t_kuaiji_bcBean.setInvoice_date(String.valueOf(resultSet.getString("invoice_date")));
				t_kuaiji_bcBean.setIssuer(String.valueOf(resultSet.getString("issuer")));
				t_kuaiji_bcBean.setConsumption_tax_number(String.valueOf(resultSet.getString("consumption_tax_number")));
				t_kuaiji_bcBean.setProduct_or_service_description(String.valueOf(resultSet.getString("product_or_service_description")));
				t_kuaiji_bcBean.setTotal_amount_with_tax(String.valueOf(resultSet.getDouble("total_amount_with_tax")));

				LinkedHashMap_t_kuaiji_bcBean.put("" + ++count, t_kuaiji_bcBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_kuaiji_bcBean;
	}




	public void delete_where_yyyymmdd_count_and_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_kuaiji_bc"
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
					+ "DELETE FROM t_kuaiji_bc"
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

		return "INSERT INTO t_kuaiji_bc (" + columns + ") VALUES (now(3), " + placeholders + ")";
	}

	private int setParameters(PreparedStatement preparedStatement, String[] values, int j) throws Exception {
		for (int i = 0; i < values.length; i++) {
			preparedStatement.setString(++j, values[i]);
		}
		return j;
	}

	// 字段名列表
	String[] header = {
			"UPDATE_DATE",
			"yyyymmdd_count",
			"yyyy",
			"invoice_date",
			"issuer",
			"consumption_tax_number",
			"product_or_service_description",
			"total_amount_with_tax"
	};
/*
CREATE TABLE `t_kuaiji_bc` (
  `UPDATE_DATE` timestamp(6) NOT NULL,
  `yyyymmdd_count` bigint NOT NULL,
  `yyyy` varchar(45) NOT NULL,
  `invoice_date` date NOT NULL COMMENT '日期',
  `issuer` varchar(255) NOT NULL COMMENT '开票人',
  `consumption_tax_number` varchar(255) NOT NULL COMMENT '开票人消费税税号（除T之外）',
  `product_or_service_description` varchar(255) NOT NULL COMMENT '商品或服务内容',
  `total_amount_with_tax` decimal(16,2) NOT NULL COMMENT '不含消费税金额',
  KEY `i_kuaiji_shuru` (`yyyymmdd_count`,`yyyy`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='账本BC支出'
 */

}
