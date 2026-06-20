package com.panda.dao;

import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.panda.bean.t_etax_jieguoBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_zhongjian_shengaoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_zhongjian_shengaoDao.class.toString());

	public t_etax_jieguoExBean select_where_PK(t_etax_jieguoBean t_etax_jieguoBean) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_zhongjian_shengao.yyyymmdd_count"
					+ "     , t_etax_zhongjian_shengao.chuli_type"
					+ "     , t_etax_zhongjian_shengao.noufu_kubun"
					+ "     , t_etax_zhongjian_shengao.file_name"
					+ "     , t_etax_account_info.etax_pw"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_zhongjian_shengao"
					+ "      , t_etax_account_info"
					+ " WHERE t_etax_zhongjian_shengao.yyyymmdd_count=?"
					+ "   and t_etax_zhongjian_shengao.yyyy=?"
					+ "   and t_etax_zhongjian_shengao.chuli_type=?"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_zhongjian_shengao.yyyymmdd_count"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_account_info.yyyymmdd_count"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyy());
			preparedStatement.setString(++i, t_etax_jieguoBean.getChuli_type());

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));

				t_etax_jieguoExBean.setFile_name(resultSet.getString("file_name"));

//
//				t_etax_jieguoExBean.setShuunou_kikan_bangou(resultSet.getString("shuunou_kikan_bangou"));
				t_etax_jieguoExBean.setNoufu_kubun(resultSet.getString("noufu_kubun"));
//				t_etax_jieguoExBean.setYuukou_kigen(resultSet.getString("yuukou_kigen"));
//				t_etax_jieguoExBean.setNoufu_kingaku(resultSet.getString("noufu_kingaku"));
//
//
//

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}



	public int INSERT(t_etax_jieguoBean t_etax_jieguoBean) throws Exception {
		PreparedStatement preparedStatement = null;
		try {

			String sql = ""
					+ "INSERT INTO t_etax_zhongjian_shengao ("
					+ "	UPDATE_DATE"
					+ "	, yyyymmdd_count"
					+ "	, yyyy"
					+ "	, chuli_type"
					+ "	, file_name"
					+ "	, html"
					+ "	, event"


					+ "     , yuukou_kigen"

					+ ")"
					+ "	VALUES ("
					+ "	now(3)"
					+ " , ?"
					+ " , ?"
					+ " , ?"
					+ " , ?"
					+ " , ?"
					+ " , ?"

					+ " , ?"

					+ ")"
					+ "";


//	        sql = "INSERT INTO t_etax_jieguo (UPDATE_DATE, yyyymmdd_count, yyyy, chuli_type, file_name) VALUES (now(3), '20230830000002', '2023', '申告', 'PDSK230701')";
			preparedStatement = connection.prepareStatement(sql);

			int i = 0;
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyy());
			preparedStatement.setString(++i, t_etax_jieguoBean.getChuli_type());
			preparedStatement.setString(++i, t_etax_jieguoBean.getFile_name());


//			preparedStatement.setString(1, "20230830000002");
//			preparedStatement.setString(2, "2023");
//			preparedStatement.setString(3, "申告");
//			preparedStatement.setString(4, "PDSK230701");

//			preparedStatement.setString(++i, t_etax_jieguoBean.getHtml());
			Blob b1 = connection.createBlob();
			b1.setBytes(1, t_etax_jieguoBean.getHtml().getBytes());
			preparedStatement.setBlob(++i, b1);


			preparedStatement.setString(++i, t_etax_jieguoBean.getEvent());

			preparedStatement.setString(++i, t_etax_jieguoBean.getYuukou_kigen());

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public t_etax_jieguoExBean select_jietuo_by_bangou(String bangou, String chuli_type) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_zhongjian_shengao.*"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_zhongjian_shengao"
					+ " WHERE t_etax_account_res.yyyymmdd_count=t_etax_zhongjian_shengao.yyyymmdd_count"
					+ "   and t_etax_account_res.bangou=?"
					+ "   and t_etax_zhongjian_shengao.chuli_type=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, bangou);
			preparedStatement.setString(++i, chuli_type);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExBean.setFile_name(resultSet.getString("file_name"));
				t_etax_jieguoExBean.setHtml(resultSet.getString("html"));
				t_etax_jieguoExBean.setHtml_qr(resultSet.getString("Html_qr"));
				t_etax_jieguoExBean.setPdf_xiaofeishui_shengaoshu(resultSet.getBinaryStream("pdf_xiaofeishui_shengaoshu"));

				t_etax_jieguoExBean.setYuukou_kigen(resultSet.getString("yuukou_kigen"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}



	public int UPDATE_dianzi_nashui(t_etax_jieguoBean t_etax_jieguoBean) {


		PreparedStatement preparedStatement = null;

		try {
			/*
			ALTER TABLE t_etax_jieguo
ADD COLUMN shuunou_kikan_bangou VARCHAR(10) COMMENT '収納機関番号',
ADD COLUMN noufu_kubun VARCHAR(20) COMMENT '納付区分',
ADD COLUMN yuukou_kigen DATE COMMENT '有効期限',
ADD COLUMN noufu_kingaku DECIMAL(10,2) COMMENT '納付金額';



							t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoBean.setYyyy("2024");
							t_etax_jieguoBean.setChuli_type(chuli_type);
							t_etax_jieguoBean.setFile_name(PDSK);

			 */

			String sql = ""
					+ "UPDATE t_etax_zhongjian_shengao"
					+ "   SET shuunou_kikan_bangou = ?"
					+ "     , noufu_kubun = ?"
					+ "     , yuukou_kigen = ?"
					+ "     , noufu_kingaku = ?"
					+ " WHERE yyyymmdd_count = ?"
					+ " AND YYYY=?"
					+ " AND chuli_type=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_jieguoBean.getShuunou_kikan_bangou());
			preparedStatement.setString(++i, t_etax_jieguoBean.getNoufu_kubun());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYuukou_kigen());
			preparedStatement.setString(++i, t_etax_jieguoBean.getNoufu_kingaku());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYyyy());
			preparedStatement.setString(++i, t_etax_jieguoBean.getChuli_type());

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;



	}


	public void delete_where_yyyymmdd_count(String yyyymmdd_count) {


		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_etax_zhongjian_shengao"
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


}
