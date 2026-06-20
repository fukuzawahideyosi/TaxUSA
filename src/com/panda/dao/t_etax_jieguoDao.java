package com.panda.dao;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_jieguoBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.bean.t_etax_jieguoExExBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_jieguoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_jieguoDao.class.toString());

	public t_etax_jieguoExBean select_jietuo_active_max(String keyValue) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_jieguo.*"
					+ "     , CONCAT_WS(',', IF(t_etax_jieguo.html IS NULL or t_etax_jieguo.html = '', 'html_NULL', 'html')"
					+ "        				, IF(t_etax_jieguo.pdf_xiaofeishui_shengaoshu IS NULL or t_etax_jieguo.pdf_xiaofeishui_shengaoshu = '', 'pdf_xiaofeishui_shengaoshu_NULL', 'pdf_xiaofeishui_shengaoshu')"
					+ "        				, IF(t_etax_jieguo.html_qr IS NULL or t_etax_jieguo.html_qr = '', 'html_qr_NULL', 'html_qr')"
					+ "		) AS exe_list"
					+ "     , t_etax_account_info.user_type"
					+ "     , t_etax_account_info.etax_pw"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_jieguo"
					+ "      , t_etax_account_info"
					+ " WHERE t_etax_jieguo.yyyymmdd_count=("
					+ "       SELECT MAX(yyyymmdd_count) yyyymmdd_count，"
					+ "         FROM t_etax_jieguo"
					+ "         WHERE t_etax_jieguo.chuli_type='"+keyValue+"'"
					+ "   		  and t_etax_jieguo.yyyy='2023'"
					+ "  		  and (file_name is null or file_name=''"
					+ "					 or html is null or html=''"
					+ "					 or html_qr is null or html_qr=''"
					+ "					 or LENGTH(pdf_xiaofeishui_shengaoshu)=0)"
					+ "       )"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_jieguo.yyyymmdd_count"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_account_info.yyyymmdd_count"
					+ "   and t_etax_jieguo.chuli_type='"+keyValue+"'"
					+ "   and t_etax_jieguo.yyyy='2023'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExBean.setEtax_pw(resultSet.getString("etax_pw"));

				t_etax_jieguoExBean.setFile_name(resultSet.getString("file_name"));
				t_etax_jieguoExBean.setUser_type(resultSet.getString("user_type"));
				t_etax_jieguoExBean.setExe_list(resultSet.getString("exe_list"));


			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}



	public boolean UPDATE_fileName(String bangou, String file_name, String keyValue) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET UPDATE_DATE = now(3), file_name=?"
					+ "";

			if ("skip".equals(file_name)) {
				sql = sql + ""
						+ "     , html='skip' , html_qr='skip' "
						+ "";

			}

			sql = sql + ""
					+ " where yyyy='2023'"
					+ "   and yyyymmdd_count = (select yyyymmdd_count from t_etax_account_res where bangou=?)"
					+ "   and t_etax_jieguo.chuli_type='"+keyValue+"'"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			int i = 0;
			preparedStatement.setString(++i, file_name);
			preparedStatement.setString(++i, bangou);

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}


	public boolean UPDATE_jietuo_pdf_xiaofeishui_shengaoshu_up(String bangou, InputStream fileContent, String keyValue) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET UPDATE_DATE = now(3), pdf_xiaofeishui_shengaoshu=?"
					+ " where yyyy='2023'"
					+ "   and yyyymmdd_count = (select yyyymmdd_count from t_etax_account_res where bangou=?)"
					+ "   and chuli_type='"+keyValue+"'"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			int i = 0;
//			preparedStatement.setBinaryStream(++i, fileContent);

			Blob b1 = connection.createBlob();
			b1.setBytes(1, fileContent.readAllBytes());
			preparedStatement.setBlob(++i, b1);
			logger.debug("b1 : " + b1);



			preparedStatement.setString(++i, bangou);
			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public int UPDATE_jietuo_pdf_where_PDSK(String PDSK, String YYYY, String chuli_type, InputStream fileContent) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET UPDATE_DATE = now(3), pdf_xiaofeishui_shengaoshu=?"
					+ " where yyyy=?"
					+ "   and yyyymmdd_count = (select yyyymmdd_count from t_xiaofeishui_shengao where PDSK=?)"
					+ "   and chuli_type=?"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			int i = 0;
//			preparedStatement.setBinaryStream(++i, fileContent);

			Blob b1 = connection.createBlob();
			b1.setBytes(1, fileContent.readAllBytes());
			preparedStatement.setBlob(++i, b1);
			logger.debug("b1 : " + b1);


			preparedStatement.setString(++i, YYYY);
			preparedStatement.setString(++i, PDSK);
			preparedStatement.setString(++i, chuli_type);

			logger.debug(preparedStatement.toString());

			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}

	public t_etax_jieguoExBean select_jietuo_by_bangou(String bangou, String chuli_type) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_jieguo.*"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_jieguo"
					+ " WHERE t_etax_account_res.yyyymmdd_count=t_etax_jieguo.yyyymmdd_count"
					+ "   and t_etax_account_res.bangou=?"
					+ "   and t_etax_jieguo.chuli_type=?"
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
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}


	public t_etax_jieguoExBean select_jietuo(String yyyymmdd_count, String yyyy, String chuli_type) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_jieguo.*"
					+ "  FROM t_etax_jieguo"
					+ " WHERE yyyymmdd_count=?"
					+ "   and yyyy=?"
					+ "   and chuli_type=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);
			preparedStatement.setString(++i, chuli_type);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setFile_name(resultSet.getString("file_name"));
				t_etax_jieguoExBean.setHtml(resultSet.getString("html"));
				t_etax_jieguoExBean.setHtml_qr(resultSet.getString("Html_qr"));
				t_etax_jieguoExBean.setPdf_xiaofeishui_shengaoshu(resultSet.getBinaryStream("pdf_xiaofeishui_shengaoshu"));
				t_etax_jieguoExBean.setEtax_xtx(resultSet.getString("etax_xtx"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}

	public t_etax_jieguoExBean select_jietuo_active_by_bangou(String bangou) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_jieguo.*"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_jieguo"
					+ " WHERE t_etax_jieguo.yyyymmdd_count=("
					+ "       SELECT MAX(yyyymmdd_count) yyyymmdd_count，"
					+ "         FROM t_etax_jieguo"
					+ "         WHERE (file_name is null or file_name='')"
					+ "					 or html is null or html=''"
					+ "					 or html_qr is null or html_qr=''"
					+ "       )"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_jieguo.yyyymmdd_count"
					+ "   and t_etax_account_res.bangou=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, bangou);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExBean.setFile_name(resultSet.getString("file_name"));
				t_etax_jieguoExBean.setHtml(resultSet.getString("html"));
				t_etax_jieguoExBean.setHtml_qr(resultSet.getString("html_qr"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}

	public t_etax_jieguoExBean select_where_PK(t_etax_jieguoBean t_etax_jieguoBean) {
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		try {

			String sql = ""
					+ "SELECT t_etax_account_res.bangou"
					+ "     , t_etax_jieguo.yyyymmdd_count"
					+ "     , t_etax_jieguo.chuli_type"
					+ "     , t_etax_jieguo.noufu_kubun"
					+ "     , t_etax_account_info.etax_pw"
					+ "  FROM t_etax_account_res"
					+ "      , t_etax_jieguo"
					+ "      , t_etax_account_info"
					+ " WHERE t_etax_jieguo.yyyymmdd_count=?"
					+ "   and t_etax_jieguo.yyyy=?"
					+ "   and t_etax_jieguo.chuli_type=?"
					+ "   and t_etax_account_res.yyyymmdd_count=t_etax_jieguo.yyyymmdd_count"
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


	public t_etax_jieguoExBean Select_where_YYYY_AND_PDSK(String yyyy, String PDSK) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();

		try {

			String sql = ""
					+ ""
					+ "select"
					+ "    tej.shuunou_kikan_bangou"
					+ "    ,tej.noufu_kubun"
					+ "    ,tej.yuukou_kigen"
					+ "    ,tej.noufu_kingaku"
					+ "    ,teai.yyyymmdd_count"
					+ "    ,teai.CompanyName_pianjiaming"
					+ "    ,tear.bangou"
					+ "    ,txs.PDSK"
					+ " from"
					+ "    t_etax_jieguo tej"
					+ "    INNER JOIN  t_xiaofeishui_shengao txs"
					+ "        ON tej.yyyymmdd_count = txs.yyyymmdd_count "
					+ "        and tej.yyyy = ? "
					+ "        and txs.PDSK = ?"
					+ "    LEFT JOIN  t_etax_account_info teai "
					+ "        ON tej.yyyymmdd_count = teai.yyyymmdd_count "
					+ "    LEFT JOIN t_etax_account_res tear "
					+ "        ON tej.yyyymmdd_count = tear.yyyymmdd_count "
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i=0;
			preparedStatement.setString(++i, yyyy);
			preparedStatement.setString(++i, PDSK);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {


				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));

				t_etax_jieguoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExBean.setPDSK(resultSet.getString("PDSK"));

				t_etax_jieguoExBean.setShuunou_kikan_bangou(resultSet.getString("shuunou_kikan_bangou"));
				t_etax_jieguoExBean.setNoufu_kubun(resultSet.getString("noufu_kubun"));
				t_etax_jieguoExBean.setYuukou_kigen(resultSet.getString("yuukou_kigen"));
				t_etax_jieguoExBean.setNoufu_kingaku(resultSet.getString("noufu_kingaku"));


			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_etax_jieguoExBean;
	}



	public LinkedHashMap<String, t_etax_jieguoExBean> SelectExWhwer_shuilishi_id(String yyyy, String event) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		LinkedHashMap<String, t_etax_jieguoExBean> LinkedHashMap = new LinkedHashMap<String, t_etax_jieguoExBean>();

		try {

			String sql = ""
					+ "SELECT"
					+ "       txs.PDSK"
					+ "     , txs.yyyymmdd_count"
					+ "     , teai.user_type"
					+ "     , teai.etax_pw"
					+ "     , tear.bangou"
					+ "     , tej.*"
					+ "  from"
					+ "    t_xiaofeishui_shengao txs"
					+ " LEFT JOIN t_etax_jieguo tej"
					+ "        ON txs.yyyymmdd_count = tej.yyyymmdd_count"
					+ "       AND tej.yyyy = ?"
					+ "       AND tej.event like ?"
					+ " inner JOIN t_etax_account_info teai"
					+ "        ON txs.yyyymmdd_count = teai.yyyymmdd_count"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "        ON txs.yyyymmdd_count = tear.yyyymmdd_count"
					+ " where"
					+ "       COALESCE(txs.shuilishi_id, '') <> ''"
					+ "   and COALESCE(tej.event, '') = ''"
					+ "   and txs.yyyymmdd_count <> '20230711000010'"
//					+ "   and txs.yyyymmdd_count <> '20240930000325'"
					+ ";"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(++i, yyyy);
			preparedStatement.setString(++i, event);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
				t_etax_jieguoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExBean.setEvent(resultSet.getString("event"));

				t_etax_jieguoExBean.setPDSK(resultSet.getString("PDSK"));

				t_etax_jieguoExBean.setUser_type(resultSet.getString("user_type"));
				t_etax_jieguoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExBean.setEtax_pw(resultSet.getString("etax_pw"));

				LinkedHashMap.put(t_etax_jieguoExBean.getYyyymmdd_count(), t_etax_jieguoExBean);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return LinkedHashMap;
	}


	public int jietuo_active_html(String yyyymmdd_count, String html, String columnKey, String keyValue) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET UPDATE_DATE = now(3)"
					+ "     , " + columnKey + " = ?"
					+ " where yyyymmdd_count = ?"
					+ "   and chuli_type='"+keyValue+"'"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			Blob b1 = connection.createBlob();
			b1.setBytes(1, html.getBytes());
			preparedStatement.setBlob(1, b1);
			logger.debug("b1 : " + b1);
			preparedStatement.setString(2, yyyymmdd_count);
			logger.debug("yyyymmdd_count : " + yyyymmdd_count);

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

	public int INSERT(t_etax_jieguoBean t_etax_jieguoBean) throws Exception {
		PreparedStatement preparedStatement = null;
		try {

			String sql = ""
					+ "INSERT INTO t_etax_jieguo ("
					+ "	UPDATE_DATE"
					+ "	, yyyymmdd_count"
					+ "	, yyyy"
					+ "	, chuli_type"
					+ "	, file_name"
					+ "	, html"
					+ "	, pdf_xiaofeishui_shengaoshu"
					+ "	, html_qr"
					+ "	, event"
					+ "	, taxable_amount"
					+ "	, total_tax_amount"


					+ "     , shuunou_kikan_bangou"
					+ "     , noufu_kubun"
					+ "     , yuukou_kigen"
					+ "     , noufu_kingaku"

					+ "     , etax_xtx"

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
					+ " , ?"
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


			b1 = connection.createBlob();
			b1.setBytes(1, t_etax_jieguoBean.getPdf_xiaofeishui_shengaoshu().readAllBytes());
			preparedStatement.setBlob(++i, b1);

//			preparedStatement.setString(++i, t_etax_jieguoBean.getHtml_qr());
			b1 = connection.createBlob();
			b1.setBytes(1, t_etax_jieguoBean.getHtml_qr().getBytes());
			preparedStatement.setBlob(++i, b1);


			preparedStatement.setString(++i, t_etax_jieguoBean.getEvent());
			preparedStatement.setString(++i, t_etax_jieguoBean.getTaxable_amount());
			preparedStatement.setString(++i, t_etax_jieguoBean.getTotal_tax_amount());

			preparedStatement.setString(++i, t_etax_jieguoBean.getShuunou_kikan_bangou());
			preparedStatement.setString(++i, t_etax_jieguoBean.getNoufu_kubun());
			preparedStatement.setString(++i, t_etax_jieguoBean.getYuukou_kigen());
			preparedStatement.setString(++i, t_etax_jieguoBean.getNoufu_kingaku());


			b1 = connection.createBlob();
			b1.setBytes(1, t_etax_jieguoBean.getEtax_xtx().getBytes());
			preparedStatement.setBlob(++i, b1);

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;

		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public LinkedHashMap<String, t_etax_jieguoExExBean> selectAll(User_infoBean user_infoBean, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_jieguoExExBean> LinkedHashMap_t_etax_jieguoExExBean = new LinkedHashMap<String, t_etax_jieguoExExBean>();
		try {

			String sql = ""
					+ "SELECT teai.*"

					+ "     , tear.yyyymmdd_count as tear_yyyymmdd_count"
					+ "     , tear.bangou"
					+ "     , tear.HoujinBangou"
					+ "     , tear.InvoiceBangou"
					+ "     , tear.horyuu"
					+ "     , tear.output_file"
					+ "     , tear.output_file_jieguo"
					+ "     , tear.PDSK"

					+ "     , t_jct.updateDate"
					+ "     , (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)) AS subtracted_date"


					+ "     , tej.UPDATE_DATE as tej_UPDATE_DATE"
					+ "     , tej.yyyy"
					+ "     , tej.file_name"
//					+ "     , tej.html"
//					+ "     , tej.html_qr"
					+ "     , tej.event"
					+ "     , tej.taxable_amount"
					+ "     , tej.total_tax_amount"
					+ "     , tej.qr_payment_amount"


					+ "  FROM t_etax_jieguo tej"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "    ON tej.yyyymmdd_count = tear.yyyymmdd_count"
					+ "  LEFT JOIN t_jct"
					+ "        ON tear.InvoiceBangou = t_jct.registratedNumber "
					+ "  LEFT JOIN t_etax_account_info teai "
					+ "    ON tej.yyyymmdd_count = teai.yyyymmdd_count"
					+ "";
			if (!"admin".equals(user_infoBean.getPermissions()) && !"zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE (teai.user_id='" + user_infoBean.getUser_id() + "'"
						+ "";

				if ("groupAdmin".equals(user_infoBean.getPermissions())) {
					HashMap<String, User_infoBean> HashMap = user_infoBean.getGroup_id_user_id();
					for (Map.Entry<String, User_infoBean> entry : HashMap.entrySet()) {
						String key = entry.getKey(); // 获取键
//			            User_infoBean value = entry.getValue(); // 获取值
						sql = sql
								+ "    OR teai.user_id='" + key + "'"
								+ "";
					}

				}

				sql = sql
						+ ")"
						+ "";


				sql = sql
						+ "AND ((tear.bangou is null or tear.HoujinBangou is null or tear.InvoiceBangou is null)"
						+ "  or t_jct.updateDate > (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)))"
						+ "";


			} else if ("zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE tear.bangou is not null"
						+ "   AND tear.bangou  <> ''"
						+ "   AND teai.user_id  <> 'etaxonly'"
						+ "";
			}

			sql = sql


					//TODO
//					+ " where tear.yyyymmdd_count='20240220980005'"




					+ " ORDER BY tej.UPDATE_DATE desc"
					+ "";

			if(StringUtils.isEmpty(maxNo) == false) {
				sql = sql
						+ " LIMIT " + maxNo
						+ "";
			}



			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_jieguoExExBean t_etax_jieguoExExBean = new t_etax_jieguoExExBean();
				t_etax_jieguoExExBean.setUPDATE_DATE(resultSet.getString("tej_UPDATE_DATE").split("\\.")[0]);
				t_etax_jieguoExExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_jieguoExExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_jieguoExExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_jieguoExExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_jieguoExExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_jieguoExExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_jieguoExExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_jieguoExExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_jieguoExExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_jieguoExExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_jieguoExExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_jieguoExExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_jieguoExExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_jieguoExExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_jieguoExExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_jieguoExExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_jieguoExExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_jieguoExExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_jieguoExExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_jieguoExExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_jieguoExExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_jieguoExExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_jieguoExExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_jieguoExExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_jieguoExExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_jieguoExExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_jieguoExExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_jieguoExExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_jieguoExExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_jieguoExExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_jieguoExExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				t_etax_jieguoExExBean.setUser_type(resultSet.getString("user_type"));


				t_etax_jieguoExExBean.setTear_yyyymmdd_count(resultSet.getString("tear_yyyymmdd_count"));
				t_etax_jieguoExExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_jieguoExExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_jieguoExExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_jieguoExExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_jieguoExExBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_jieguoExExBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_jieguoExExBean.setPDSK(resultSet.getString("PDSK"));


				t_etax_jieguoExExBean.setInvoice_updateDate(resultSet.getString("updateDate"));

				t_etax_jieguoExExBean.setYyyy(resultSet.getString("yyyy"));
				t_etax_jieguoExExBean.setFile_name(resultSet.getString("file_name"));
//				t_etax_jieguoExExBean.setHtml(resultSet.getString("html"));
//				t_etax_jieguoExExBean.setHtml_qr(resultSet.getString("html_qr"));


				t_etax_jieguoExExBean.setEvent(resultSet.getString("event"));


				// 创建DecimalFormat对象，设置格式
				DecimalFormat decimalFormat = new DecimalFormat("#,###");
				String formattedNumber = "";
				// 格式化数字


				formattedNumber = resultSet.getString("taxable_amount");
				if (!StringUtils.isEmpty(formattedNumber)) {
					formattedNumber = decimalFormat.format(Long.parseLong(formattedNumber));
				}
				t_etax_jieguoExExBean.setTaxable_amount(formattedNumber);


				formattedNumber = resultSet.getString("total_tax_amount");
				if (!StringUtils.isEmpty(formattedNumber)) {
					formattedNumber = decimalFormat.format(Long.parseLong(formattedNumber));
				}
				t_etax_jieguoExExBean.setTotal_tax_amount(formattedNumber);


				formattedNumber = resultSet.getString("qr_payment_amount");
				if (!StringUtils.isEmpty(formattedNumber)) {
					formattedNumber = decimalFormat.format(Long.parseLong(formattedNumber));
				}
				t_etax_jieguoExExBean.setQr_payment_amount(formattedNumber);

//JSP
//<td><%= t_etax_jieguoExExBean.getEvent()%></td>
//<td><%= t_etax_jieguoExExBean.getTaxable_amount()%></td>
//<td><%= t_etax_jieguoExExBean.getTotal_tax_amount()%></td>
//<td><%= t_etax_jieguoExExBean.getQr_payment_amount()%></td>

//				logger.debug(t_etax_jieguoExExBean.getYyyymmdd_count());

				if ("20240202018935".equals(t_etax_jieguoExExBean.getYyyymmdd_count())) {
					logger.debug(t_etax_jieguoExExBean.getYyyymmdd_count());
				}

				if (StringUtils.isEmpty(t_etax_jieguoExExBean.getHtml()) == false
						&& t_etax_jieguoExExBean.getHtml().contains("消費税及び地方消費税の合計（納付又は還付）税額")) {

					String user_type=t_etax_jieguoExExBean.getUser_type();

					Document doc = Jsoup.parse(t_etax_jieguoExExBean.getHtml());
					// 找到表格元素
					Element table = doc.select("table").first();

					//種目
					Element cell = table.select("tr:eq(6) td:eq(1)").first();
					if ("个人".equals(user_type)) {
						cell = table.select("tr:eq(7) td:eq(1)").first();
					}
					t_etax_jieguoExExBean.setZhongmu(cell.text());

					//課税標準額
					cell = table.select("tr:eq(10) td:eq(1)").first();
					if ("个人".equals(user_type)) {
						cell = table.select("tr:eq(8) td:eq(1)").first();
					}
					t_etax_jieguoExExBean.setKeshui_jizhune(cell.text());


					//合計（納付又は還付）税額
					cell = table.select("tr:eq(11) td:eq(1)").first();
					if ("个人".equals(user_type)) {
						cell = table.select("tr:eq(9) td:eq(1)").first();
					}
					t_etax_jieguoExExBean.setHeji_shuie(cell.text());

				}


				if (StringUtils.isEmpty(t_etax_jieguoExExBean.getHtml_qr()) == false
						&& t_etax_jieguoExExBean.getHtml_qr().contains("納付金額")) {
					Document doc = Jsoup.parse(t_etax_jieguoExExBean.getHtml_qr());
					// 找到表格元素
					Element table = doc.select("table").first();

					//合計（納付又は還付）税額 QR
					Element cell = table.select("tr:eq(5) td:eq(1)").first();
					t_etax_jieguoExExBean.setHeji_shuie_qr(cell.text());

				}

				String 金额去逗号 = t_etax_jieguoExExBean.getHeji_shuie();
				if(!StringUtils.isEmpty(金额去逗号)) {
					金额去逗号 = 金额去逗号.replace("円", "").replace(",", "");
			        // 将字符串转换为整数
			        int 金额 = Integer.parseInt(金额去逗号);

			        if (金额 == 0) {
			        	if (t_etax_jieguoExExBean.getHtml_qr().contains("納付金額")) {
			        		t_etax_jieguoExExBean.setHeji_shuie_qr(t_etax_jieguoExExBean.getHeji_shuie_qr() + "_NG");
			        	}

			        	// 判断是否大于30万
			        } else if (金额 >= 300000) {
			        	if (t_etax_jieguoExExBean.getHtml_qr().contains("納付金額")) {
			        		t_etax_jieguoExExBean.setHeji_shuie_qr(t_etax_jieguoExExBean.getHeji_shuie_qr() + "_NG");
			        	}

			        } else {
				        if (!t_etax_jieguoExExBean.getHeji_shuie().equals(t_etax_jieguoExExBean.getHeji_shuie_qr())) {
							t_etax_jieguoExExBean.setHeji_shuie_qr(t_etax_jieguoExExBean.getHeji_shuie_qr() + "_NG");
						}
			        }
				} else {
			        if (!StringUtils.isEmpty(t_etax_jieguoExExBean.getFile_name()) && !"skip".equals(t_etax_jieguoExExBean.getFile_name())) {
						t_etax_jieguoExExBean.setHeji_shuie_qr(t_etax_jieguoExExBean.getHeji_shuie_qr() + "_NG");
					}
				}




				LinkedHashMap_t_etax_jieguoExExBean.put(t_etax_jieguoExExBean.getYyyymmdd_count(), t_etax_jieguoExExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_jieguoExExBean;
	}

	public int Update_key_value(String yyyymmdd_count, String key, String value) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET " + key + " = ?"
					+ " WHERE yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, value);
			preparedStatement.setString(++i, yyyymmdd_count);

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

	public int Update_event_taxable_amount_total_tax_amount(String yyyymmdd_count, String event, String taxable_amount, String total_tax_amount) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET event = ?"
					+ "     , taxable_amount = ?"
					+ "     , total_tax_amount = ?"
					+ " WHERE yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, event);
			preparedStatement.setString(++i, taxable_amount);
			preparedStatement.setString(++i, total_tax_amount);
			preparedStatement.setString(++i, yyyymmdd_count);

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



	public int Update_from_xtx(String yyyymmdd_count, t_etax_jieguoExBean t_etax_jieguoExBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_jieguo"
					+ "   SET taxable_amount = ?"
					+ "     , sashihiki_tax_amount = ?"
					+ "     , total_tax_amount = ?"
					+ "     , uketsuke_datetime = ?"
					+ "     , kazei_kikan = ?"
					+ " WHERE yyyymmdd_count = ? and yyyy='2025'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_jieguoExBean.getTaxable_amount());
			preparedStatement.setString(++i, t_etax_jieguoExBean.getSashihiki_tax_amount());
			preparedStatement.setString(++i, t_etax_jieguoExBean.getTotal_tax_amount());
			preparedStatement.setString(++i, t_etax_jieguoExBean.getUketsuke_datetime());
			preparedStatement.setString(++i, t_etax_jieguoExBean.getKazei_kikan());
			preparedStatement.setString(++i, yyyymmdd_count);

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
					+ "DELETE FROM t_etax_jieguo"
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
					+ "UPDATE t_etax_jieguo"
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
}
