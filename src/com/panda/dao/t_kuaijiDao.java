package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_jieguoExExBean;
import com.panda.bean.t_kuaijiBean;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_kuaijiDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_kuaijiDao.class.toString());

	/*
CREATE TABLE `t_kuaiji` (
`UPDATE_DATE` timestamp(6) NOT NULL,
`yyyymmdd_count` bigint NOT NULL,
`yyyy` varchar(45) NOT NULL,
`kuaiji_type` varchar(45) NOT NULL,
`input_file` varchar(128) DEFAULT NULL,
`output_file` varchar(128) DEFAULT NULL,
PRIMARY KEY (`yyyymmdd_count`,`yyyy`,`kuaiji_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
	 */

	public int INSERT(t_kuaijiBean t_kuaijiBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = ""
					+ ""
					+ "INSERT INTO `psma`.`t_kuaiji`"
					+ "(`UPDATE_DATE`,"
					+ "`yyyymmdd_count`,"
					+ "`yyyy`,"
					+ "`kuaiji_type`,"
					+ "`input_file`"
					+ ", output_file"
					+ ") VALUES (?, ?, ?, ?, ?, ?)";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_kuaijiBean.getYyyymmdd_count());
			preparedStatement.setString(++i, t_kuaijiBean.getYyyy());
			preparedStatement.setString(++i, t_kuaijiBean.getKuaiji_type());
			preparedStatement.setString(++i, t_kuaijiBean.getInput_file());
			preparedStatement.setString(++i, t_kuaijiBean.getOutput_file());

			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}


	public void delete_where_yyyymmdd_count_and_yyyy_and_kuaiji_type(String yyyymmdd_count, String yyyy, String kuaiji_type) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "DELETE FROM t_kuaiji"
					+ " where yyyymmdd_count = ? and yyyy=? and kuaiji_type=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);
			preparedStatement.setString(++i, kuaiji_type);
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
					+ "DELETE FROM t_kuaiji"
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


	public LinkedHashMap<String, t_kuaijiBean> selectAll_where_yyyymmdd_count_by_yyyy(String yyyymmdd_count, String yyyy) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_kuaijiBean> LinkedHashMap_t_kuaijiBean = new LinkedHashMap<String, t_kuaijiBean>();
		try {




			String sql = ""
					+ "SELECT *"
					+ "  FROM t_kuaiji"
					+ " where yyyymmdd_count=? and yyyy=?"
					+ "";


			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			int count = 0;
			while (resultSet.next()) {
				t_kuaijiBean t_kuaijiBean = new t_kuaijiBean();
				t_kuaijiBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_kuaijiBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_kuaijiBean.setYyyy(resultSet.getString("yyyy"));

				t_kuaijiBean.setKuaiji_type(String.valueOf(resultSet.getString("kuaiji_type")));
				t_kuaijiBean.setInput_file(String.valueOf(resultSet.getString("input_file")));
				t_kuaijiBean.setOutput_file(String.valueOf(resultSet.getString("output_file_jieguo")));

				LinkedHashMap_t_kuaijiBean.put("" + ++count, t_kuaijiBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_kuaijiBean;
	}





	public boolean UpdateKeyValue(String yyyymmdd_count, String yyyy, String key, String value) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_res SET ?=?"
					+ " where yyyymmdd_count=?"
					+ "   and yyyy=?"
					+ "   and kuaiji_type=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int count=0;
			preparedStatement.setString(++count, key);
			preparedStatement.setString(++count, value);
			preparedStatement.setString(++count, yyyymmdd_count);
			preparedStatement.setString(++count, yyyy);


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

					+ "     , t_jct.updateDate"
					+ "     , (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)) AS subtracted_date"


					+ "     , tej.UPDATE_DATE as tej_UPDATE_DATE"
					+ "     , tej.yyyy"
					+ "     , tej.file_name"
					+ "     , tej.html"


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


				t_etax_jieguoExExBean.setTear_yyyymmdd_count(resultSet.getString("tear_yyyymmdd_count"));
				t_etax_jieguoExExBean.setBangou(resultSet.getString("bangou"));
				t_etax_jieguoExExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_jieguoExExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_jieguoExExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_jieguoExExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_jieguoExExBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_jieguoExExBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_jieguoExExBean.setUser_type(resultSet.getString("user_type"));


				t_etax_jieguoExExBean.setInvoice_updateDate(resultSet.getString("updateDate"));

				t_etax_jieguoExExBean.setYyyy(resultSet.getString("yyyy"));
				t_etax_jieguoExExBean.setFile_name(resultSet.getString("file_name"));
				t_etax_jieguoExExBean.setHtml(resultSet.getString("html"));

				if (StringUtils.isEmpty(t_etax_jieguoExExBean.getHtml()) == false) {

					String user_type=t_etax_jieguoExExBean.getUser_type();

//					logger.debug(t_etax_jieguoExExBean.getYyyymmdd_count());

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


				LinkedHashMap_t_etax_jieguoExExBean.put(t_etax_jieguoExExBean.getYyyymmdd_count(), t_etax_jieguoExExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_jieguoExExBean;
	}

}
