package com.panda.dao;

import java.io.Reader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.t_freeeBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_freeeDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_freeeDao.class.toString());

	public int INSERT(List<t_freeeBean> rowDataList, String max_torihiki_bi) throws Exception {

        // 解析日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    	if (StringUtils.isEmpty(max_torihiki_bi)) {
        	max_torihiki_bi = "2000-01-01";
        }
        LocalDate maxDate = LocalDate.parse(max_torihiki_bi, formatter);

		Reader reader = null;
		CSVParser csvParser = null;
		PreparedStatement preparedStatement = null;
		try {

			String sql = generateInsertSQL(header);
			preparedStatement = connection.prepareStatement(sql);

			int count_new = 0;
			int batchSize = 0;

			int[] count_executeBatch;
			// 循环rowDataList并打印每个bean的值
			for (t_freeeBean bean : rowDataList) {

				LocalDate koushinDate = LocalDate.parse(bean.getKoushin_bi(), formatter);

		        // 比较日期
		        if (koushinDate.isAfter(maxDate)) {
//		            System.out.println("koushin_bi 比 max_torihiki_bi 大");
		        } else if (koushinDate.isBefore(maxDate)) {
//		            System.out.println("koushin_bi 比 max_torihiki_bi 小");
		        	continue;
		        } else {
//		            System.out.println("koushin_bi 等于 max_torihiki_bi");
		        	continue;
		        }

				++count_new;

				int i = 0;
				preparedStatement.setString(++i, bean.getKouza_mei());
				preparedStatement.setString(++i, bean.getTorihiki_bi());
				preparedStatement.setString(++i, bean.getTorihiki_naiyou());
				preparedStatement.setString(++i, bean.getNyuukin_gaku().replace(",", ""));
				preparedStatement.setString(++i, bean.getShukkin_gaku().replace(",", ""));
				preparedStatement.setString(++i, bean.getZandaka().replace(",", ""));
				preparedStatement.setString(++i, bean.getJoukyou());
				preparedStatement.setString(++i, bean.getKoushin_bi());
				preparedStatement.setString(++i, bean.getShutoku_bi());


				//大量ログのため、出力しない
//				logger.debug(preparedStatement.toString());
				preparedStatement.addBatch();
				batchSize++;

				if (batchSize % 100 == 0) {
//						logger.debug("executeBatch S");
					count_executeBatch = preparedStatement.executeBatch();
//						logger.debug("executeBatch E");
					logger.debug("count_all " + rowDataList.size() + " count_new " + count_new + " executeBatch " + count_executeBatch.length);
					batchSize = 0;
				}

			}

			count_executeBatch = preparedStatement.executeBatch(); // Execute the remaining batch
			logger.debug("count_all " + rowDataList.size() + " count_new " + count_new + " executeBatch " + count_executeBatch.length);

			return count_new;
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

	public LinkedHashMap<String, t_freeeBean> selectAll_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_freeeBean> LinkedHashMap_t_freeeBean = new LinkedHashMap<String, t_freeeBean>();
		try {
			/*
//	  `UPDATE_DATE` timestamp(6) NOT NULL,
//	  `yyyymmdd_count` bigint NOT NULL,
//	  `yyyy` varchar(45) NOT NULL,
//	  `transaction_date` date NOT NULL COMMENT '日期',
//	  `customer` varchar(255) NOT NULL COMMENT '客户',
//	  `product` varchar(255) NOT NULL COMMENT '商品',
//	  `net_amount` decimal(10,2) NOT NULL COMMENT '不含消费税金额',
//	  `tax_amount` decimal(10,2) NOT NULL COMMENT '消费税金额',


	);



			 */



			String sql = ""
					+ "SELECT *"
					+ "  FROM t_freee"
					+ " ORDER BY UPDATE_DATE ASC"
					+ " LIMIT 100"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_freeeBean t_freeeBean = new t_freeeBean();
				t_freeeBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);



				LinkedHashMap_t_freeeBean.put("" + ++count, t_freeeBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_freeeBean;
	}

	public LinkedHashMap<String, t_freeeBean> selectAll() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_freeeBean> LinkedHashMap_t_freeeBean = new LinkedHashMap<String, t_freeeBean>();
		try {
			/*
CREATE TABLE `t_freee` (
  `UPDATE_DATE` timestamp(6) NOT NULL COMMENT '更新时间',
  `kouza_mei` varchar(255) NOT NULL COMMENT '口座名',
  `torihiki_bi` date NOT NULL COMMENT '取引日',
  `torihiki_naiyou` varchar(255) NOT NULL COMMENT '取引内容',
  `nyuukin_gaku` decimal(15,0) NOT NULL COMMENT '入金額',
  `shukkin_gaku` decimal(15,0) NOT NULL COMMENT '出金額',
  `zandaka` decimal(15,0) NOT NULL COMMENT '残高',
  `joukyou` varchar(50) NOT NULL COMMENT '状態',
  `koushin_bi` date NOT NULL COMMENT '更新日',
  `shutoku_bi` date NOT NULL COMMENT '取得日'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

	);



			 */

			String sql = ""
					+ "SELECT *"
					+ "  FROM t_freee"
//					+ " where torihiki_naiyou like 'ﾋｼﾑｹｿｳｷﾝ%'"
					+ " ORDER BY torihiki_bi DESC,torihiki_naiyou DESC,UPDATE_DATE DESC"
					+ " LIMIT 100"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_freeeBean t_freeeBean = new t_freeeBean();
				t_freeeBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);



				t_freeeBean.setKouza_mei(resultSet.getString("kouza_mei"));
				t_freeeBean.setTorihiki_bi(resultSet.getString("torihiki_bi"));
				t_freeeBean.setTorihiki_naiyou(resultSet.getString("torihiki_naiyou"));
				t_freeeBean.setNyuukin_gaku(resultSet.getString("nyuukin_gaku").replace(",", ""));
				t_freeeBean.setShukkin_gaku(resultSet.getString("shukkin_gaku").replace(",", ""));
				t_freeeBean.setZandaka(resultSet.getString("zandaka").replace(",", ""));
				t_freeeBean.setJoukyou(resultSet.getString("joukyou"));
				t_freeeBean.setKoushin_bi(resultSet.getString("koushin_bi"));
				t_freeeBean.setShutoku_bi(resultSet.getString("shutoku_bi"));


				LinkedHashMap_t_freeeBean.put("" + ++count, t_freeeBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_freeeBean;
	}


	public String selectAll_max_torihiki_bi() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT MAX(torihiki_bi) as max_torihiki_bi  FROM t_freee"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("max_torihiki_bi");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";

	}

	public String selectAll_max_UPDATE_DATE() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT MAX(UPDATE_DATE) as max_UPDATE_DATE  FROM t_freee"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("max_UPDATE_DATE");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";

	}

	public void delete_where_yyyymmdd_count_and_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_kuaiji_a"
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

	public void delete_where_max_torihiki_bi() {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ ""

//					+ "DELETE t1 "
//					+ "  FROM t_freee t1"
//					+ "  JOIN (SELECT MAX(torihiki_bi) AS max_torihiki_bi FROM t_freee) t2"
//					+ "    ON t1.torihiki_bi = t2.max_torihiki_bi"

					+ "DELETE FROM t_freee"
					+ ""
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	private String generateInsertSQL(String[] header) {
		// Assuming 'header' contains column names
		String columns = String.join(",", header);
		String placeholders = String.join(",", java.util.Collections.nCopies(header.length - 1, "?"));

		return "INSERT INTO t_freee (" + columns + ") VALUES (now(3), " + placeholders + ")";
	}

	private int setParameters(PreparedStatement preparedStatement, String[] values, int j) throws Exception {
		for (int i = 0; i < values.length; i++) {
//			if (i == 0) {
//				Date parsedDate = new SimpleDateFormat("dd-M月-yyyy").parse(values[i]);
//	            String formattedDate = new SimpleDateFormat("yyyyMMdd").format(parsedDate);
//				values[i] =  formattedDate;
//			}
			preparedStatement.setString(++j, values[i]);
		}
		return j;
	}

	// 字段名列表
	String[] header = {
			"UPDATE_DATE	",
			"kouza_mei		",
			"torihiki_bi		",
			"torihiki_naiyou	",
			"nyuukin_gaku	",
			"shukkin_gaku	",
			"zandaka			",
			"joukyou			",
			"koushin_bi		",
			"shutoku_bi		",
	};


}
