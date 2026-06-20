package com.panda.dao;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.bean.t_etax_account_resExBean;
import com.panda.utils.FuncUtils;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_account_resDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_account_resDao.class.toString());

    public t_etax_account_resExBean getLastCreatedRecord(String yyyymmddhhmmss) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(yyyymmddhhmmss, formatter);

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_resExBean t_etax_account_resExBean = new t_etax_account_resExBean();

		try {

			String sql = ""
					+ "SELECT tear.bangou"
					+ "     , tear.UPDATE_DATE"
					+ "     , tear.yyyymmdd_count"
					+ "     , teai.CompanyName_Chinese"
					+ "  FROM t_etax_account_res tear"
					+ " LEFT JOIN t_etax_account_info teai"
					+ "        ON tear.yyyymmdd_count = teai.yyyymmdd_count"
	                + " WHERE tear.UPDATE_DATE > ?"
//	                + "   AND COALESCE(tear.bangou, '') <> ''"
					+ " ORDER BY UPDATE_DATE DESC"
					+ " LIMIT 1;"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
	        preparedStatement.setTimestamp(++i, Timestamp.valueOf(localDateTime));

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_resExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_resExBean.setBangou(resultSet.getString("bangou"));

				t_etax_account_resExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_etax_account_resExBean;



    }


	public String selectMax_yyyymmdd_count_active() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT MAX(yyyymmdd_count) yyyymmdd_count"
					+ " FROM t_etax_account_res "
					+ "WHERE (horyuu ='待处理' or horyuu is null or horyuu ='')"
					+ "  and (bangou is null or bangou='')"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				BigDecimal selectMax_yyyymmdd_count = resultSet.getBigDecimal("yyyymmdd_count");
				if (selectMax_yyyymmdd_count == null) {
					return "";

				} else {
					return selectMax_yyyymmdd_count.toString();

				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}

	public String selec(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_etax_account_res"
					+ " where yyyymmdd_count='" + yyyymmdd_count + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				Blob res_send_mae = resultSet.getBlob("res_send_mae");
				byte[] bdata = res_send_mae.getBytes(1, (int) res_send_mae.length());
				String s = new String(bdata);
				return s;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}


	public int selecCount(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT count(1) as count"
					+ " FROM t_etax_account_res"
					+ " where yyyymmdd_count='" + yyyymmdd_count + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getInt("count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return 0;
	}

	public String selecByDataFileName(String dataFileName) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT yyyymmdd_count"
					+ " FROM t_etax_account_res"
					+ " where dataFileName='" + dataFileName + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("yyyymmdd_count");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}

	public String selecBangou(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_etax_account_res"
					+ " where yyyymmdd_count='" + yyyymmdd_count + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("bangou");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}

	public LinkedHashMap<String, t_etax_account_resExBean> selecBangouIsNull() {
		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ ""
					+ ""
					+ "SELECT res.*"
					+ "  FROM t_etax_account_res AS res"
					+ "  JOIN t_etax_account_info AS info"
					+ "    ON res.yyyymmdd_count = info.yyyymmdd_count"
					+ " WHERE (res.bangou IS NULL OR res.bangou ='')"
					+ "   AND info.user_id  LIKE 'piliang%'"


//					+ " AND res.yyyymmdd_count in ("
//					+ "'20240413980008'"
//					+ ""
//					+ ")"

					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				t_etax_account_resExBean t_etax_account_resExBean = new t_etax_account_resExBean();
				t_etax_account_resExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_resExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_resExBean.setgHojinmei(resultSet.getString("gHojinmei"));
				t_etax_account_resExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_resExBean.setWord(resultSet.getString("word"));
				t_etax_account_resExBean.setXml(resultSet.getString("xml"));
				t_etax_account_resExBean.setPdf(resultSet.getString("pdf"));
				t_etax_account_resExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_resExBean.setDataFileName(resultSet.getString("dataFileName"));
				t_etax_account_resExBean.setDataFile(resultSet.getString("dataFile"));
				t_etax_account_resExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_resExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_resExBean.setOutput_file("output_file");
				LinkedHashMap_t_etax_account_resExBean.put(t_etax_account_resExBean.getYyyymmdd_count(), t_etax_account_resExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_resExBean;
	}


	public String selecByPDSK(String PDSK) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_etax_account_res"
					+ " where PDSK='" + PDSK + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("yyyymmdd_count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}

	public String selec_where_bangou(String bangou) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_etax_account_res"
					+ " where bangou='" + bangou + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("yyyymmdd_count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}


	public String selec_where_InvoiceBangou(String InvoiceBangou) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ "SELECT *"
					+ " FROM t_etax_account_res"
					+ " where InvoiceBangou='" + InvoiceBangou + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getString("yyyymmdd_count");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return "";
	}


	public LinkedHashMap<String, t_etax_account_resExBean> selectAll() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();
		FuncUtils FuncUtils = new FuncUtils();
		try {

			String sql = ""
					+ "SELECT * "
					//					+ "UPDATE_DATE,"
					//					+ "yyyymmdd_count,"
					//					+ "gHojinmei,"
					//					+ "res_send_mae,"
					//					+ "res_send_go,"
					//					+ "bangou,"
					//					+ "word,"
					//					+ "xml,"
					//					+ "pdf,"
					//					+ "horyuu,"
					//					+ "dataFileName,"
					//					+ "dataFile"
					+ ""
					+ " FROM t_etax_account_res"
					+ " ORDER BY yyyymmdd_count desc"
					+ " LIMIT 30"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resExBean t_etax_account_resExBean = new t_etax_account_resExBean();
				t_etax_account_resExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_resExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_resExBean.setgHojinmei(resultSet.getString("gHojinmei"));
				t_etax_account_resExBean.setRes_send_mae(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_mae")));
				t_etax_account_resExBean.setRes_send_go(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_go")));
				t_etax_account_resExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_resExBean.setWord(resultSet.getString("word"));
				t_etax_account_resExBean.setXml(resultSet.getString("xml"));
				t_etax_account_resExBean.setPdf(resultSet.getString("pdf"));
				t_etax_account_resExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_resExBean.setDataFileName(resultSet.getString("dataFileName"));
				t_etax_account_resExBean.setDataFile(resultSet.getString("dataFile"));
				t_etax_account_resExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_resExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_resExBean.setOutput_file("output_file");
				LinkedHashMap_t_etax_account_resExBean.put(t_etax_account_resExBean.getYyyymmdd_count(), t_etax_account_resExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_resExBean;
	}

	public LinkedHashMap<String, t_etax_account_resExBean> selectSPEED(String web, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();
		try {

			String sql = ""
					+ "SELECT "
					+ "t_etax_account_res.UPDATE_DATE,"
					+ "t_etax_account_res.yyyymmdd_count,"
					+ "t_etax_account_res.gHojinmei,"
					//					+ "t_etax_account_res.res_send_mae,"
					//					+ "t_etax_account_res.res_send_go,"
					+ "t_etax_account_res.bangou,"
					+ "t_etax_account_res.word,"
					+ "t_etax_account_res.xml,"
					+ "t_etax_account_res.pdf,"
					+ "t_etax_account_res.horyuu,"
					+ "t_etax_account_res.dataFileName,"
					+ "t_etax_account_res.dataFile,"
					+ "t_etax_account_res.HoujinBangou,"
					+ "t_etax_account_res.InvoiceBangou,"
					+ "t_etax_account_res.output_file,"
					+ "t_jct.updateDate"
					+ ", t_etax_account_info.CompanyName_Chinese"
					+ " FROM t_etax_account_res"
					+ " LEFT JOIN t_jct"
					+ "        ON t_etax_account_res.InvoiceBangou=t_jct.registratedNumber"
					+ " LEFT JOIN  t_etax_account_info"
					+ "        ON t_etax_account_res.yyyymmdd_count = t_etax_account_info.yyyymmdd_count"
					+ ""
					+ ""
					+ "";

			if ("KakuninAuto".equals(web)) {
				sql = sql
						+ " WHERE t_etax_account_res.horyuu = '待处理'"
						+ "   AND t_etax_account_res.bangou is not null"
						+ "";
			} else if ("Kakunin".equals(web)) {

				sql = sql
						+ " WHERE t_etax_account_res.bangou is not null"
								+ "   AND t_etax_account_res.bangou  <> ''"
						+ "";
			} else {
				sql = sql
						+ " WHERE t_etax_account_res.yyyymmdd_count = '" + web + "'"
						+ "";
			}

			sql = sql
					+ ""
					+ " ORDER BY t_etax_account_res.yyyymmdd_count desc"
					+ "";

			if (StringUtils.isEmpty(maxNo) == false) {
				sql = sql
						+ " LIMIT " + maxNo
						+ "";
			}

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resExBean t_etax_account_resExBean = new t_etax_account_resExBean();
				t_etax_account_resExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_resExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_resExBean.setgHojinmei(resultSet.getString("gHojinmei"));
				//				EtaxBean.setRes_send_mae(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_mae")));
				//				EtaxBean.setRes_send_go(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_go")));
				t_etax_account_resExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_resExBean.setWord(resultSet.getString("word"));
				t_etax_account_resExBean.setXml(resultSet.getString("xml"));
				t_etax_account_resExBean.setPdf(resultSet.getString("pdf"));
				t_etax_account_resExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_resExBean.setDataFileName(resultSet.getString("dataFileName"));
				t_etax_account_resExBean.setDataFile(resultSet.getString("dataFile"));
				t_etax_account_resExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_resExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_resExBean.setInvoice_updateDate(resultSet.getString("updateDate"));
				t_etax_account_resExBean.setOutput_file(resultSet.getString("output_file"));
				//				logger.debug(EtaxBean.getInvoiceBangou());
				t_etax_account_resExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				LinkedHashMap_t_etax_account_resExBean.put(t_etax_account_resExBean.getYyyymmdd_count(), t_etax_account_resExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_resExBean;
	}

	public LinkedHashMap<String, t_etax_account_resBean> selectHoujinBangou() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
		try {

			String sql = ""
					+ "SELECT "
					//					+ "UPDATE_DATE,"
					+ "yyyymmdd_count,"
					+ "gHojinmei,"
					//					+ "res_send_mae,"
					//					+ "res_send_go,"
					//					+ "bangou,"
					//					+ "word,"
					//					+ "xml,"
					//					+ "pdf,"
					//					+ "horyuu,"
					//					+ "dataFileName,"
					//					+ "dataFile,"
					+ "HoujinBangou"
					+ "  FROM t_etax_account_res"
					+ " WHERE IFNULL(HoujinBangou, '') = ''"
					+ " ORDER BY yyyymmdd_count asc"

					//TEST
//					+ "ORDER BY yyyymmdd_count DESC"
//					+ "   LIMIT 1;"

					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resBean EtaxBean = new t_etax_account_resBean();
				EtaxBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxBean.setgHojinmei(resultSet.getString("gHojinmei"));
				EtaxBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				LinkedHashMapEtaxBean.put(EtaxBean.getYyyymmdd_count(), EtaxBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMapEtaxBean;
	}

	public boolean UPDATE_res(String key, String yyyymmdd_count, String html, String bangou) {

		PreparedStatement preparedStatement = null;

        Blob blob = null;
		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET UPDATE_DATE = now(3)"
					+ "";

			if ("res_send_mae".equals(key)) {

				sql = sql
						+ ", res_send_mae = ?"
						+ "";

			} else if ("res_send_go".equals(key)) {
				sql = sql
						+ ", res_send_go = ?"
						+ ", bangou = ?"
						+ "";

			} else {
				return false;
			}

			sql = sql
					+ " where yyyymmdd_count = ?"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			blob = connection.createBlob();
			blob.setBytes(1, html.getBytes());
			preparedStatement.setBlob(1, blob);
			logger.debug("blob : " + blob);

			if ("res_send_mae".equals(key)) {
				preparedStatement.setString(2, yyyymmdd_count);
				logger.debug("yyyymmdd_count : " + yyyymmdd_count);
			} else if ("res_send_go".equals(key)) {
				preparedStatement.setString(2, bangou);
				logger.debug("bangou : " + bangou);
				preparedStatement.setString(3, yyyymmdd_count);
				logger.debug("yyyymmdd_count : " + yyyymmdd_count);

			} else {
				return false;
			}

			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			if (i == 1) {
				return true;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
	          // 关闭 Blob 对象
            if (blob != null) {
                try {
                    blob.free();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public int DELETE_res(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_etax_account_res"
					+ " where (bangou is null  or bangou='' ) and yyyymmdd_count = ?"
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

	public boolean Update_res_horyuu(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res"
					+ "	  SET UPDATE_DATE=now(3)"
					+ "     , horyuu=("
					+ "               CASE"
					+ "                 WHEN horyuu='保留有' THEN '待处理'"
					+ "                 ELSE '保留有'"
					+ "               END "
					+ "              )"
					+ " WHERE yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public boolean Update_res_horyuu(String yyyymmdd_count, String destinationFolderZip, String horyuu) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET horyuu=?, output_file=? where yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, horyuu);
			preparedStatement.setString(2, destinationFolderZip);
			preparedStatement.setString(3, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public boolean Update_res_HoujinBangou(String yyyymmdd_count, String HoujinBangou) throws SQLException {
		if (HoujinBangou == null) {
			HoujinBangou = "";
		}

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET HoujinBangou='" + HoujinBangou + "'"
					+ " where yyyymmdd_count = '" + yyyymmdd_count + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public boolean Update_HoujinBangou_geren_all() throws SQLException {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res AS res JOIN t_etax_account_info AS info"
					+ "    ON res.yyyymmdd_count = info.yyyymmdd_count"
					+ "   SET"
					+ "    res.HoujinBangou = '-'"
					+ " WHERE info.user_type = '个人'"
					+ "   AND res.HoujinBangou <> '-'"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}
	public boolean Update_res_InvoiceBangou(String yyyymmdd_count, String InvoiceBangou) throws SQLException {
		if (InvoiceBangou == null) {
			InvoiceBangou = "";
		}

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET InvoiceBangou='" + InvoiceBangou + "'"
					+ " where yyyymmdd_count = '" + yyyymmdd_count + "'"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public boolean UpdateXml(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET xml=xml+1  where yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public boolean UpdateWord(String yyyymmdd_count) {

		//	} else if ("UpdatePdf".equals(key)) {
		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET word=word+1  where yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, yyyymmdd_count);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public boolean UpdatePdf(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = "";

			if (StringUtils.isEmpty(yyyymmdd_count) == true) {
				sql = ""
						+ "UPDATE t_etax_account_res SET pdf = pdf +1 WHERE yyyymmdd_count IN (SELECT yyyymmdd_count FROM (SELECT MAX(yyyymmdd_count) yyyymmdd_count FROM t_etax_account_res) tmp);"
						+ "";

			} else {
				sql = ""
						+ "UPDATE t_etax_account_res SET pdf=pdf+1  where yyyymmdd_count = " + yyyymmdd_count + ""
						+ "";

			}
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);


			logger.debug(preparedStatement.toString());
			int i = preparedStatement.executeUpdate();
			logger.debug("SQL " + i);

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return false;
	}

	public int INSERT(HashMap<String, String> HashMapKeyValueHtml, String max_yyyymmdd_count, File myFile) {
		PreparedStatement preparedStatement = null;
		try {
			//https://qiita.com/taro_dev/items/99a1bd6463792964c917
			// ファイル読み込み

			String sql = ""
					+ "INSERT INTO t_etax_account_res ("
					+ "	UPDATE_DATE"
					+ "	, yyyymmdd_count"
					+ "	, gHojinmei"
					+ "	, res_send_mae"
					+ "	, res_send_go"
					+ "	, dataFileName"
					//					+ "	, dataFile"
					+ ")"
					+ "	value("
					+ "	now(3)"
					+ " , " + max_yyyymmdd_count + ""
					+ "	, '" + HashMapKeyValueHtml.get("gHojinmei") + "'"
					+ "	, null"
					+ "	, null"
					+ ", '" + myFile.getName() + "'"
					//					+ ", ?"
					+ ")"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			//			preparedStatement.setBinaryStream(1, finstream, (int) myFile.length());
			//			preparedStatement.setBytes(1, IOUtils.toByteArray(finstream));

			//			Blob b1 = connection.createBlob();
			//			b1.setBytes(1,IOUtils.toByteArray(finstream));
			//			preparedStatement.setBlob(1, b1);


			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}

	public int INSERT(String yyyymmdd_count, t_etax_account_infoBean etaxAccountInfoBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {

			String sql = ""
					+ "INSERT INTO t_etax_account_res ("
					+ "	UPDATE_DATE"
					+ "	, yyyymmdd_count"
					+ "	, gHojinmei"
					+ "	, dataFileName"
					+ "	, bangou"
					+ "	, horyuu"
					+ ")"
					+ "	value("
					+ "	now(3)"
					+ " , " + yyyymmdd_count + ""
					+ "	, '" + etaxAccountInfoBean.getCompanyName_English() + "'"
					+ ", 'WEB(" + etaxAccountInfoBean.getUser_id() + ")'"
					+ "	, '" + (etaxAccountInfoBean.getEtax_no() == null ? "" : etaxAccountInfoBean.getEtax_no()) + "'"
					+ ", '保留有'" 	//TODO商用环境要返回		待处理
					+ ")"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;

		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public LinkedHashMap<String, t_etax_account_resBean> selectInvoiceBangou() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
		try {

			String sql = ""
					+ "SELECT "
					//					+ "UPDATE_DATE,"
					+ "yyyymmdd_count,"
					+ "gHojinmei,"
					//					+ "res_send_mae,"
					//					+ "res_send_go,"
					//					+ "bangou,"
					//					+ "word,"
					//					+ "xml,"
					//					+ "pdf,"
					//					+ "horyuu,"
					//					+ "dataFileName,"
					//					+ "dataFile,"
					+ "HoujinBangou"
					+ " FROM t_etax_account_res"
					+ " WHERE InvoiceBangou is null"
					+ " ORDER BY yyyymmdd_count asc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resBean EtaxBean = new t_etax_account_resBean();
				EtaxBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxBean.setgHojinmei(resultSet.getString("gHojinmei"));
				EtaxBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				LinkedHashMapEtaxBean.put(EtaxBean.getYyyymmdd_count(), EtaxBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMapEtaxBean;
	}
	public LinkedHashMap<String, t_etax_account_resBean> selectInvoiceBangouNotNull() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
		try {

			String sql = ""
					+ "SELECT tear.yyyymmdd_count,tear.gHojinmei,HoujinBangou "
					+ "FROM"
					+ "    t_etax_account_info teai "
					+ "    LEFT JOIN t_etax_account_res tear "
					+ "        ON teai.yyyymmdd_count = tear.yyyymmdd_count "
					+ " WHERE tear.InvoiceBangou is not null and tear.InvoiceBangou <> ''"
					+ " ORDER BY yyyymmdd_count asc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resBean EtaxBean = new t_etax_account_resBean();
				EtaxBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxBean.setgHojinmei(resultSet.getString("gHojinmei"));
				EtaxBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				LinkedHashMapEtaxBean.put(EtaxBean.getYyyymmdd_count(), EtaxBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMapEtaxBean;
	}


	public LinkedHashMap<String, t_etax_account_resBean> selectInvoiceBangouKey() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
		try {

			String sql = ""
					+ "SELECT "
					//					+ "UPDATE_DATE,"
					+ "yyyymmdd_count,"
					+ "gHojinmei,"
					//					+ "res_send_mae,"
					//					+ "res_send_go,"
					//					+ "bangou,"
					//					+ "word,"
					//					+ "xml,"
					//					+ "pdf,"
					//					+ "horyuu,"
					//					+ "dataFileName,"
					//					+ "dataFile,"
					+ "InvoiceBangou"
					+ " FROM t_etax_account_res"
					+ " WHERE InvoiceBangou is not null"
					+ " ORDER BY yyyymmdd_count asc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resBean EtaxBean = new t_etax_account_resBean();
				EtaxBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxBean.setgHojinmei(resultSet.getString("gHojinmei"));
				EtaxBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				LinkedHashMapEtaxBean.put(EtaxBean.getInvoiceBangou(), EtaxBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMapEtaxBean;
	}

	public int UpdateKeyValue(String yyyymmdd_count, String key, String value) {
		if (value == null) {
			value = "";
		}

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET " + key + "=? where yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);
			preparedStatement.setString(2, yyyymmdd_count);


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

	public int UpdatePDSK(String key, String value) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET PDSK=?"
					+ " WHERE (PDSK IS NULL OR LENGTH(PDSK)=0) "
					+ "   AND yyyymmdd_count IN ("
					+ "			SELECT yyyymmdd_count"
					+ " 		  FROM t_etax_account_info"
					+ "";
			if  (FuncUtils.isNumeric(key)) {
				sql = sql + ""
						+ " 		 where yyyymmdd_count=?"
						+ "		)"
						+ "";

			} else {
				sql = sql + ""
						+ " 		 where CompanyName_Chinese=? or CompanyName_English=?"
						+ "		)"
						+ "";

			}

			sql = sql + ""
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(++i, value);

			if  (FuncUtils.isNumeric(key)) {
				preparedStatement.setString(++i, key);

			} else {
				preparedStatement.setString(++i, key);
				preparedStatement.setString(++i, key);

			}


			connection.setAutoCommit(false);

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			if (count > 1) {
				connection.rollback();
			}

			connection.setAutoCommit(true);

			return count;

		} catch (SQLException e) {
			e.printStackTrace();
			if (e.getMessage().indexOf("PDSK_UNIQUE") > 0) {
				return -1;
			}
			return -9999;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public LinkedHashMap<String, t_etax_account_resExBean> selecExWhereKeyValue(String key, String value) {


		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();
		FuncUtils FuncUtils = new FuncUtils();
		try {

			String sql = ""
					+ "SELECT * "
					+ " FROM t_etax_account_res"
					+ " where yyyymmdd_count in (SELECT yyyymmdd_count FROM t_etax_account_info where " + key +"=?)"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(++i, value);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_resExBean t_etax_account_resExBean = new t_etax_account_resExBean();
				t_etax_account_resExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_resExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_resExBean.setgHojinmei(resultSet.getString("gHojinmei"));
				t_etax_account_resExBean.setRes_send_mae(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_mae")));
				t_etax_account_resExBean.setRes_send_go(FuncUtils.delHtmlJavascript(resultSet.getString("res_send_go")));
				t_etax_account_resExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_resExBean.setWord(resultSet.getString("word"));
				t_etax_account_resExBean.setXml(resultSet.getString("xml"));
				t_etax_account_resExBean.setPdf(resultSet.getString("pdf"));
				t_etax_account_resExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_resExBean.setDataFileName(resultSet.getString("dataFileName"));
				t_etax_account_resExBean.setDataFile(resultSet.getString("dataFile"));
				t_etax_account_resExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_resExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_resExBean.setOutput_file("output_file");
				t_etax_account_resExBean.setPdsk(resultSet.getString("PDSK"));
				LinkedHashMap_t_etax_account_resExBean.put(t_etax_account_resExBean.getYyyymmdd_count(), t_etax_account_resExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_resExBean;


	}


}
