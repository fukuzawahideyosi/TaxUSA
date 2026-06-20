package com.panda.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.utils.FuncUtils;
import com.panda.utils.JdbcUtils;
import com.panda.utils.KanaUtil;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_etax_account_infoDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_etax_account_infoDao.class.toString());

	public LinkedHashMap<String, t_etax_account_infoExBean> selectAll(User_infoBean user_infoBean, String maxNo, String yyyy, String sort, String filter) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<String, t_etax_account_infoExBean>();
		try {

			String sql = ""
					+ "SELECT teai.*"

					+ "     , CASE"
					+ "           WHEN COUNT(*) OVER (PARTITION BY teai.CompanyName_Chinese) > 1 THEN 'Duplicate'"
					+ "           ELSE 'Unique'"
					+ "        END AS CompanyName_ChineseIsDuplicate"
					+ "     , CASE"
					+ "           WHEN COUNT(*) OVER (PARTITION BY teai.CompanyName_English) > 1 THEN 'Duplicate'"
					+ "           ELSE 'Unique'"
					+ "        END AS CompanyName_EnglishIsDuplicate"

					+ "     , tear.yyyymmdd_count as tear_yyyymmdd_count"
					+ "     , tear.bangou"
					+ "     , tear.HoujinBangou"
					+ "     , tear.InvoiceBangou"
					+ "     , tear.horyuu"
					+ "     , tear.output_file"
					+ "     , tear.output_file_jieguo"
					+ "     , tear.PDSK"

					+ "     , tea.html_value"

					+ "     , tjs.INSQ"

					+ "    , ( "
					+ "        SELECT"
					+ "            GROUP_CONCAT(CONCAT(PDSK, '_', activation_code, '_', IFNULL(NULLIF(TRIM(shuilishi_id), ''), '税理士ID')) SEPARATOR ',') "
					+ "        FROM"
					+ "            t_xiaofeishui_shengao txs "
					+ "        where"
					+ "            teai.yyyymmdd_count = txs.yyyymmdd_count "
					+ "    ) AS txs_PDSK "


					+ "    , ( "
					+ "        SELECT"
					+ "            GROUP_CONCAT( "
					+ "                CONCAT( "
					+ "                    file_name"
					+ "                    , '_'"
					+ "                    , shuunou_kikan_bangou"
					+ "                    , '_'"
					+ "                    , noufu_kubun"
					+ "                    , '_'"
					+ "                    , yuukou_kigen"
					+ "                    , '_'"
					+ "                    , noufu_kingaku"
					+ "                ) SEPARATOR ','"
					+ "            ) "
					+ "        FROM"
					+ "            t_etax_jieguo txj "
					+ "        where"
					+ "            teai.yyyymmdd_count = txj.yyyymmdd_count "
					+ "            and txj.yyyy = '2024'"
					+ "    ) AS txj_dianzi_nashui"

//					+ "     , t_jct.updateDate"
//					+ "     , (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)) AS subtracted_date"

					+ "";

			if (!StringUtils.isEmpty(yyyy)) {
				sql = sql
						+ "    , ( "
						+ "        SELECT"
						+ "            GROUP_CONCAT(tk.input_file) "
						+ "        FROM"
						+ "            t_kuaiji tk "
						+ "        where"
						+ "            teai.yyyymmdd_count = tk.yyyymmdd_count "
						+ "            and tk.yyyy = '" + yyyy + "'"
						+ "        ORDER BY"
						+ "            kuaiji_type desc"
						+ "    ) AS t_kuaiji_input_files"

						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本A收入<br>🟡', count(1), '件<br>🟡含消费税（10%）金额', FORMAT(SUM(total_amount_with_tax), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_a "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_a_info "

						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本BC支出<br>🟡', count(1), '件<br>🟡消费税金额', FORMAT(SUM(total_amount_with_tax), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_bc tkpz "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_bc_info "



						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本D进口<br>🟡', count(1), '件<br>🟡消費税（国税部分）', FORMAT(SUM(consumption_tax_national), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_d "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_d_info "



						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "客向アマゾン収入帳簿<br>🟡', count(1), '件<br>🟡合計', FORMAT(SUM(total_amount), 0), '日元') "
						+ "        FROM"
						+ "            t_etax_amazon_csv" + yyyy + " teac "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "    ) AS t_etax_amazon_csv_info "

						+ "";
			}

			sql = sql
					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "    ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ "  LEFT JOIN t_etax_account tea "
					+ "    ON teai.yyyymmdd_count = tea.yyyymmdd_count"
					+ "   AND tea.html_id = 'gHojinmeiKana'"
					+ "  LEFT JOIN t_jct_shenqing tjs  "
					+ "    ON teai.yyyymmdd_count = tjs.yyyymmdd_count"
//					+ "  LEFT JOIN t_jct"
//					+ "        ON tear.InvoiceBangou = t_jct.registratedNumber "
					+ "";

			if ("激活完了pdf".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_xiaofeishui_shengao txs "
						+ "     ON teai.yyyymmdd_count = txs.yyyymmdd_count "
						+ "    AND txs.yyyy = '2024' "
						+ "    AND txs.activation_code LIKE '激活完了pdf%' "
						+ "";

			} else if ("没有签字pdf".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_xiaofeishui_shengao txs "
						+ "     ON teai.yyyymmdd_count = txs.yyyymmdd_count "
						+ "    AND txs.yyyy = '2024' "
						+ "    AND txs.activation_code not LIKE '激活完了pdf%' "
						+ "    AND teai.yyyymmdd_count not in (select yyyymmdd_count from t_etax_account_info where CompanyName_Chinese in ('测试公司中文','ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社','测试个人中文','Forever株式会社'))"
						+ "";

			} else if ("申告结果".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_etax_jieguo tej "
						+ "        ON teai.yyyymmdd_count = tej.yyyymmdd_count "
						+ "        AND tej.yyyy = '2024' "
						+ "        AND tej.event like '消費税%' "
						+ "";
			}

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
								+ "    OR teai.yaoqing_no='" + key + "'"
								+ "";
					}

				}

				sql = sql
						+ ")"
						+ "";


				if (!"groupAdmin".equals(user_infoBean.getPermissions())) {
					sql = sql
							+ "AND ("
							+ "  (tear.bangou is null or tear.HoujinBangou is null or tear.InvoiceBangou is null)"
//							+ "  or t_jct.updateDate > (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY))"
							+ ")"
							+ "";
				}


			} else if ("zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE tear.bangou is not null"
						+ "   AND tear.bangou  <> ''"
						+ "   AND teai.user_id not like 'etaxonly%'"
						+ "   AND teai.user_id not like 'piliang_%'"
						+ "   AND teai.user_id not like 'add_%'"
						+ "";
			}

			sql = sql
					+ " ORDER BY teai." + sort + " desc"
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
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));


				t_etax_account_infoExBean.setCompanyName_ChineseIsDuplicate(resultSet.getString("CompanyName_ChineseIsDuplicate"));
				t_etax_account_infoExBean.setCompanyName_EnglishIsDuplicate(resultSet.getString("CompanyName_EnglishIsDuplicate"));


				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));


				t_etax_account_infoExBean.setTear_yyyymmdd_count(resultSet.getString("tear_yyyymmdd_count"));
				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_account_infoExBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_account_infoExBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_account_infoExBean.setPDSK(resultSet.getString("PDSK") + "," + resultSet.getString("txs_PDSK"));
//				t_etax_account_infoExBean.setPDSK(t_etax_account_infoExBean.getPDSK().replaceAll("null,", "").replaceAll("null", "").replaceAll(",", "<br>"));

				t_etax_account_infoExBean.setINSQ(resultSet.getString("INSQ"));




				t_etax_account_infoExBean.setTatujin_id(resultSet.getString("tatujin_id"));
				t_etax_account_infoExBean.setUser_type_guanfang(resultSet.getString("user_type_guanfang"));
				t_etax_account_infoExBean.setUser_type_zixuan(resultSet.getString("user_type_zixuan"));



				t_etax_account_infoExBean.setCountry(resultSet.getString("country"));
				t_etax_account_infoExBean.setDigital_certificate(resultSet.getString("digital_certificate"));

				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
		    	if (StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_pianjiaming()) == true) {
					logger.debug("getCompanyName_pianjiaming is null " + t_etax_account_infoExBean.getTear_yyyymmdd_count());
		    	}

//				t_etax_account_infoExBean.setInvoice_updateDate(resultSet.getString("updateDate"));
				if (!StringUtils.isEmpty(yyyy)) {
					String kuaiji_value = resultSet.getString("t_kuaiji_input_files");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_input_files(kuaiji_value);
					}

					kuaiji_value = resultSet.getString("t_etax_amazon_csv_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_etax_amazon_csv_info(kuaiji_value);
					}

					kuaiji_value = resultSet.getString("t_kuaiji_a_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_a_info(kuaiji_value);
					}
					kuaiji_value = resultSet.getString("t_kuaiji_d_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_d_info(kuaiji_value);
					}
					kuaiji_value = resultSet.getString("t_kuaiji_bc_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_bc_info(kuaiji_value);
					}

				}

				String txj_dianzi_nashui = resultSet.getString("txj_dianzi_nashui");

				if (!StringUtils.isEmpty(txj_dianzi_nashui)) {
					String[] txj_dianzi_nashui_list = txj_dianzi_nashui.split("_");
					if (txj_dianzi_nashui_list.length == 5) {
						int i =0;
						t_etax_account_infoExBean.setFile_name(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setShuunou_kikan_bangou(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setNoufu_kubun(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setYuukou_kigen(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setNoufu_kingaku(txj_dianzi_nashui_list[i++]);
//						収納機関番号	00200
//						納付番号	利用者識別番号を入力してください。
//						確認番号	納税用確認番号を入力してください。
//						納付区分	7124096256
//						有効期限	令和07年04月21日
//						納付金額	5,118,700 円
						t_etax_account_infoExBean.setDianzi_nashui(t_etax_account_infoExBean.getFile_name()
								+ "<br>【収納機関番号】<br>" + t_etax_account_infoExBean.getShuunou_kikan_bangou()
								+ "<br>【納付区分】<br>" + t_etax_account_infoExBean.getNoufu_kubun()
								+ "<br>【有効期限】<br>" + t_etax_account_infoExBean.getYuukou_kigen()
								+ "<br>【納付金額	】<br>" + t_etax_account_infoExBean.getNoufu_kingaku()
								);

					}
				}




				LinkedHashMap_t_etax_account_infoExBean.put(t_etax_account_infoExBean.getYyyymmdd_count(), t_etax_account_infoExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoExBean;
	}

	public LinkedHashMap<String, t_etax_account_infoBean> selectAll_Company(String table_name, User_infoBean user_infoBean, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap = new LinkedHashMap<String, t_etax_account_infoBean>();
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "  FROM "+table_name+" ait"
					+ "  LEFT JOIN t_etax_account_info teai "
					+ "    ON ait.yyyymmdd_count = teai.yyyymmdd_count"
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
								+ "    OR teai.yaoqing_no='" + key + "'"
								+ "";
					}

				}

				sql = sql
						+ ")"
						+ "";

			}



			sql = sql
					+ " ORDER BY ait.UPDATE_DATE desc"
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
				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_account_infoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));


				LinkedHashMap.put(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap;
	}




	public LinkedHashMap<String, t_etax_account_infoBean> selectAll_Company_AI(String table_name, User_infoBean user_infoBean, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap = new LinkedHashMap<String, t_etax_account_infoBean>();
		try {

			String sql = ""
					+ "SELECT ait.*"
					+ "  FROM "+table_name+" ait"
//					+ "  LEFT JOIN t_etax_account_info teai "
//					+ "    ON ait.yyyymmdd_count = teai.yyyymmdd_count"
					+ "";

			if (!"admin".equals(user_infoBean.getPermissions()) && !"zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE (ait.user_id='" + user_infoBean.getUser_id() + "'"
						+ "";

				if ("groupAdmin".equals(user_infoBean.getPermissions())) {
					HashMap<String, User_infoBean> HashMap = user_infoBean.getGroup_id_user_id();
					for (Map.Entry<String, User_infoBean> entry : HashMap.entrySet()) {
						String key = entry.getKey(); // 获取键
//			            User_infoBean value = entry.getValue(); // 获取值
						sql = sql
								+ "    OR ait.user_id='" + key + "'"
//								+ "    OR teai.yaoqing_no='" + key + "'"
								+ "";
					}

				}

				sql = sql
						+ ")"
						+ "";

			}



			sql = sql
					+ " ORDER BY ait.UPDATE_DATE desc"
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
				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_account_infoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoBean.setUser_id(resultSet.getString("user_id"));

				t_etax_account_infoBean.setCompanyName_Chinese(resultSet.getString("col_name_4"));
				t_etax_account_infoBean.setCompanyName_English(resultSet.getString("col_name_5"));
				t_etax_account_infoBean.setDaibiaoName_Chinese(resultSet.getString("col_name_10"));


				LinkedHashMap.put(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap;
	}


	public LinkedHashMap<String, t_etax_account_infoExBean> selectAll_Ex_dianzi_zhifu(User_infoBean user_infoBean, String maxNo, String yyyy, String sort, String filter) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<String, t_etax_account_infoExBean>();
		try {

			String sql = ""
					+ "SELECT teai.*"

					+ "     , CASE"
					+ "           WHEN COUNT(*) OVER (PARTITION BY teai.CompanyName_Chinese) > 1 THEN 'Duplicate'"
					+ "           ELSE 'Unique'"
					+ "        END AS CompanyName_ChineseIsDuplicate"
					+ "     , CASE"
					+ "           WHEN COUNT(*) OVER (PARTITION BY teai.CompanyName_English) > 1 THEN 'Duplicate'"
					+ "           ELSE 'Unique'"
					+ "        END AS CompanyName_EnglishIsDuplicate"

					+ "     , tear.yyyymmdd_count as tear_yyyymmdd_count"
					+ "     , tear.bangou"
					+ "     , tear.HoujinBangou"
					+ "     , tear.InvoiceBangou"
					+ "     , tear.horyuu"
					+ "     , tear.output_file"
					+ "     , tear.output_file_jieguo"
					+ "     , tear.PDSK"

					+ "     , tea.html_value"


					+ "    , ( "
					+ "        SELECT"
					+ "            GROUP_CONCAT(CONCAT(PDSK, '_', activation_code, '_', IFNULL(NULLIF(TRIM(shuilishi_id), ''), '税理士ID')) SEPARATOR ',') "
					+ "        FROM"
					+ "            t_xiaofeishui_shengao txs "
					+ "        where"
					+ "            teai.yyyymmdd_count = txs.yyyymmdd_count "
					+ "    ) AS txs_PDSK "


					+ "    , ( "
					+ "        SELECT"
					+ "            GROUP_CONCAT( "
					+ "                CONCAT( "
					+ "                    file_name"
					+ "                    , '_'"
					+ "                    , shuunou_kikan_bangou"
					+ "                    , '_'"
					+ "                    , noufu_kubun"
					+ "                    , '_'"
					+ "                    , yuukou_kigen"
					+ "                    , '_'"
					+ "                    , noufu_kingaku"
					+ "                ) SEPARATOR ','"
					+ "            ) "
					+ "        FROM"
					+ "            t_etax_jieguo txj "
					+ "        where"
					+ "            teai.yyyymmdd_count = txj.yyyymmdd_count "
					+ "            and txj.yyyy = '2024'"
					+ "    ) AS txj_dianzi_nashui"



					+ ", ( "
					+ "        SELECT"
					+ "            MAX(update_date) "
					+ "        FROM"
					+ "            t_user_account_amount tuaa "
					+ "        where"
					+ "            tear.InvoiceBangou = tuaa.InvoiceBangou"
					+ "    ) AS tuaa_update_date"
					+ "    , ( "
					+ "        SELECT DISTINCT"
					+ "            InvoiceBangou"
					+ "        FROM"
					+ "            t_user_account_amount tuaa "
					+ "        where"
					+ "            tear.InvoiceBangou = tuaa.InvoiceBangou"
					+ "    ) AS tuaa_InvoiceBangou"
//					+ "    , ( "
//					+ "        SELECT"
//					+ "            GROUP_CONCAT( "
//					+ "                CONCAT( "
//					+ "                DATE_FORMAT(UPDATE_DATE, '%Y-%m-%d %H:%i:%s') , '<_>',"
//					+ "                IFNULL(NULLIF(yyyymmdd_count, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(yyyy, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(CompanyName_Chinese, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(CompanyName_English, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(bangou, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(InvoiceBangou, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(amount, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(zhifu_pingzheng, ''), 'NULL'), '<_>',"
//					+ "                IFNULL(NULLIF(huikuan_pingzheng, ''), 'NULL'), '<_>'   , "
//					+ "                 ( "
//					+ "                        SELECT"
//					+ "                            CASE "
//					+ "                                WHEN COUNT(1) = 1 "
//					+ "                                    THEN CONCAT_WS('_', 'amount', COUNT(1), MAX(torihiki_naiyou)) "
//					+ "                                ELSE CONCAT_WS('_', 'amount', COUNT(1)) "
//					+ "                                END "
//					+ "                        FROM"
//					+ "                            t_freee "
//					+ "                        WHERE"
//					+ "                            nyuukin_gaku = amount"
//					+ "                            GROUP BY nyuukin_gaku"
//					+ "                    )"
//					+ "                ) SEPARATOR '<,>'"
//					+ "            ) "
//					+ "        FROM"
//					+ "            t_user_account_amount tuaa "
//					+ "        where"
//					+ "            tear.InvoiceBangou = tuaa.InvoiceBangou"
//					+ "    ) AS tuaa_dianzi_zhifu "



//					+ "     , t_jct.updateDate"
//					+ "     , (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)) AS subtracted_date"

					+ "";

			if (!StringUtils.isEmpty(yyyy)) {
				sql = sql
						+ "    , ( "
						+ "        SELECT"
						+ "            GROUP_CONCAT(tk.input_file) "
						+ "        FROM"
						+ "            t_kuaiji tk "
						+ "        where"
						+ "            teai.yyyymmdd_count = tk.yyyymmdd_count "
						+ "            and tk.yyyy = '" + yyyy + "'"
						+ "        ORDER BY"
						+ "            kuaiji_type desc"
						+ "    ) AS t_kuaiji_input_files"

						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本A收入<br>🟡', count(1), '件<br>🟡含消费税（10%）金额', FORMAT(SUM(total_amount_with_tax), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_a "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_a_info "

						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本BC支出<br>🟡', count(1), '件<br>🟡消费税金额', FORMAT(SUM(total_amount_with_tax), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_bc tkpz "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_bc_info "



						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "账本D进口<br>🟡', count(1), '件<br>🟡消費税（国税部分）', FORMAT(SUM(consumption_tax_national), 0), '日元') "
						+ "        FROM"
						+ "            t_kuaiji_d "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "            and yyyy = '" + yyyy + "'"
						+ "    ) AS t_kuaiji_d_info "



						+ "    , ( "
						+ "        SELECT"
						+ "            CONCAT('🔻" + yyyy + "客向アマゾン収入帳簿<br>🟡', count(1), '件<br>🟡合計', FORMAT(SUM(total_amount), 0), '日元') "
						+ "        FROM"
						+ "            t_etax_amazon_csv" + yyyy + " teac "
						+ "        where"
						+ "            teai.yyyymmdd_count = yyyymmdd_count "
						+ "    ) AS t_etax_amazon_csv_info "

						+ "";
			}

			sql = sql
					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "    ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ "   AND teai.CompanyName_Chinese not like '%（删除%'"
					+ "  LEFT JOIN t_etax_account tea "
					+ "    ON teai.yyyymmdd_count = tea.yyyymmdd_count"
					+ "   AND tea.html_id = 'gHojinmeiKana'"
//					+ "  LEFT JOIN t_jct"
//					+ "        ON tear.InvoiceBangou = t_jct.registratedNumber "
					+ "";

			if ("激活完了pdf".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_xiaofeishui_shengao txs "
						+ "     ON teai.yyyymmdd_count = txs.yyyymmdd_count "
						+ "    AND txs.yyyy = '2024' "
						+ "    AND txs.activation_code LIKE '激活完了pdf%' "
						+ "";

			} else if ("没有签字pdf".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_xiaofeishui_shengao txs "
						+ "     ON teai.yyyymmdd_count = txs.yyyymmdd_count "
						+ "    AND txs.yyyy = '2024' "
						+ "    AND txs.activation_code not LIKE '激活完了pdf%' "
						+ "    AND teai.yyyymmdd_count not in (select yyyymmdd_count from t_etax_account_info where CompanyName_Chinese in ('测试公司中文','ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社','测试个人中文','Forever株式会社'))"
						+ "";

			} else if ("申告结果".equals(filter)) {
				sql = sql
						+ "  INNER JOIN t_etax_jieguo tej "
						+ "        ON teai.yyyymmdd_count = tej.yyyymmdd_count "
						+ "        AND tej.yyyy = '2024' "
						+ "        AND tej.event like '消費税%' "
						+ "";
			}

			sql = sql
					+ " WHERE EXISTS ("
					+ "    SELECT 1"
					+ "    FROM t_user_account_amount tuaa "
					+ "    WHERE tear.InvoiceBangou = tuaa.InvoiceBangou"
					+ ")"
					+ "";

			sql = sql
					+ " ORDER BY tuaa_update_date desc"
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
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));


				t_etax_account_infoExBean.setCompanyName_ChineseIsDuplicate(resultSet.getString("CompanyName_ChineseIsDuplicate"));
				t_etax_account_infoExBean.setCompanyName_EnglishIsDuplicate(resultSet.getString("CompanyName_EnglishIsDuplicate"));


				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));
				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));


				t_etax_account_infoExBean.setTear_yyyymmdd_count(resultSet.getString("tear_yyyymmdd_count"));
				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_account_infoExBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_account_infoExBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_account_infoExBean.setPDSK(resultSet.getString("PDSK") + "," + resultSet.getString("txs_PDSK"));
//				t_etax_account_infoExBean.setPDSK(t_etax_account_infoExBean.getPDSK().replaceAll("null,", "").replaceAll("null", "").replaceAll(",", "<br>"));

				t_etax_account_infoExBean.setCountry(resultSet.getString("country"));
				t_etax_account_infoExBean.setDigital_certificate(resultSet.getString("digital_certificate"));


				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
		    	if (StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_pianjiaming()) == true) {
					logger.debug("getCompanyName_pianjiaming is null " + t_etax_account_infoExBean.getTear_yyyymmdd_count());
		    	}

//				t_etax_account_infoExBean.setInvoice_updateDate(resultSet.getString("updateDate"));
				if (!StringUtils.isEmpty(yyyy)) {
					String kuaiji_value = resultSet.getString("t_kuaiji_input_files");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_input_files(kuaiji_value);
					}

					kuaiji_value = resultSet.getString("t_etax_amazon_csv_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_etax_amazon_csv_info(kuaiji_value);
					}

					kuaiji_value = resultSet.getString("t_kuaiji_a_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_a_info(kuaiji_value);
					}
					kuaiji_value = resultSet.getString("t_kuaiji_d_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_d_info(kuaiji_value);
					}
					kuaiji_value = resultSet.getString("t_kuaiji_bc_info");
					if (!StringUtils.isEmpty(kuaiji_value)) {
						t_etax_account_infoExBean.setT_kuaiji_bc_info(kuaiji_value);
					}

				}

				String txj_dianzi_nashui = resultSet.getString("txj_dianzi_nashui");

				if (!StringUtils.isEmpty(txj_dianzi_nashui)) {
					String[] txj_dianzi_nashui_list = txj_dianzi_nashui.split("_");
					if (txj_dianzi_nashui_list.length == 5) {
						int i =0;
						t_etax_account_infoExBean.setFile_name(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setShuunou_kikan_bangou(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setNoufu_kubun(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setYuukou_kigen(txj_dianzi_nashui_list[i++]);
						t_etax_account_infoExBean.setNoufu_kingaku(txj_dianzi_nashui_list[i++]);
//						収納機関番号	00200
//						納付番号	利用者識別番号を入力してください。
//						確認番号	納税用確認番号を入力してください。
//						納付区分	7124096256
//						有効期限	令和07年04月21日
//						納付金額	5,118,700 円
						t_etax_account_infoExBean.setDianzi_nashui(t_etax_account_infoExBean.getFile_name()
								+ "<br>【収納機関番号】<br>" + t_etax_account_infoExBean.getShuunou_kikan_bangou()
								+ "<br>【納付区分】<br>" + t_etax_account_infoExBean.getNoufu_kubun()
								+ "<br>【有効期限】<br>" + t_etax_account_infoExBean.getYuukou_kigen()
								+ "<br>【納付金額	】<br><div>" + String.format("%,d", Integer.parseInt(t_etax_account_infoExBean.getNoufu_kingaku())) + "</div>"
								);

					}
				}



//				String tuaa_dianzi_zhifu = resultSet.getString("tuaa_dianzi_zhifu");
//		        t_etax_account_infoExBean.setTuaa_dianzi_zhifu(tuaa_dianzi_zhifu);

//				if (!StringUtils.isEmpty(tuaa_dianzi_zhifu)) {
//				    String[] tuaa_dianzi_zhifu_list = tuaa_dianzi_zhifu.split("<,>");
//
//			        StringBuilder sb = new StringBuilder();
//
//			        for (int j = 0; j < tuaa_dianzi_zhifu_list.length; j++) {
//					    String[] my_tuaa_dianzi_zhifu = tuaa_dianzi_zhifu_list[j].split("<_>");
//			        	if (my_tuaa_dianzi_zhifu.length == 10) {
//			        		int i =0;
//							sb.append(""
//									+ "<br>【更新日】" + my_tuaa_dianzi_zhifu[0]
//									+ "<br>【金額】" + my_tuaa_dianzi_zhifu[7]
//											);
//
//
//			        	} else {
//
//							sb.append(""
//									+ "<br>【解析错误】" + tuaa_dianzi_zhifu_list[j]
//											);
//			        	}
//
//				    }
//
//			        t_etax_account_infoExBean.setTuaa_dianzi_zhifu(sb.toString());
//
//				}

//				if (!StringUtils.isEmpty(tuaa_dianzi_zhifu)) {
//					String[] tuaa_dianzi_zhifu_list = tuaa_dianzi_zhifu.split(",");
//					if (tuaa_dianzi_zhifu_list.length == 9) {
//						int i =0;
//
//						t_etax_account_infoExBean.setTuaa_dianzi_zhifu(""
//								+ "<br>【口座名】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【取引日】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【取引内容】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【入金額】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【出金額】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【残高】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【状態】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【更新日】<br>" + tuaa_dianzi_zhifu_list[i++]
//								+ "<br>【取得日】<br>" + tuaa_dianzi_zhifu_list[i++]
//								);
//
//					}
//				}



				LinkedHashMap_t_etax_account_infoExBean.put(t_etax_account_infoExBean.getYyyymmdd_count(), t_etax_account_infoExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoExBean;
	}


	public LinkedHashMap<String, t_etax_account_infoExBean> selectAll_by_kuaiji(User_infoBean user_infoBean, String maxNo) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<String, t_etax_account_infoExBean>();
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

//					+ "     , t_jct.updateDate"
//					+ "     , (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY)) AS subtracted_date"


					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "    ON teai.yyyymmdd_count = tear.yyyymmdd_count"
//					+ "  LEFT JOIN t_jct"
//					+ "        ON tear.InvoiceBangou = t_jct.registratedNumber "
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
						+ "AND ("
						+ "     (tear.bangou is null or tear.HoujinBangou is null or tear.InvoiceBangou is null)"
//						+ "  or t_jct.updateDate > (SELECT DATE_SUB((SELECT MAX(updateDate) FROM t_jct), INTERVAL 30 DAY))"
						+ ")"
						+ "";


			} else if ("zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE tear.bangou is not null"
						+ "   AND tear.bangou  <> ''"
						+ "   AND teai.user_id  <> 'etaxonly'"
						+ "";
			}

			sql = sql
					+ " ORDER BY teai.yyyymmdd_count desc"
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
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE").split("\\.")[0]);
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));


				t_etax_account_infoExBean.setTear_yyyymmdd_count(resultSet.getString("tear_yyyymmdd_count"));
				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoryuu(resultSet.getString("horyuu"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_account_infoExBean.setOutput_file(resultSet.getString("output_file"));
				t_etax_account_infoExBean.setOutput_file_jieguo(resultSet.getString("output_file_jieguo"));
				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));


//				t_etax_account_infoExBean.setInvoice_updateDate(resultSet.getString("updateDate"));


				LinkedHashMap_t_etax_account_infoExBean.put(t_etax_account_infoExBean.getYyyymmdd_count(), t_etax_account_infoExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoExBean;
	}


	public t_etax_account_infoExBean select(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "     , IFNULL(tear.bangou, '') AS bangou"
					+ "     , IFNULL(tear.HoujinBangou, '') AS HoujinBangou"
					+ "     , IFNULL(tear.InvoiceBangou, '') AS InvoiceBangou"
					+ "     , CONCAT(company_YYYY, '', LPAD(company_MM, 2, '0'), '', LPAD(company_DD, 2, '0')) AS company_YYYYMMDD"
					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ " WHERE teai.yyyymmdd_count='" + yyyymmdd_count + "'"
					+ " ORDER BY teai.yyyymmdd_count desc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));

				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));

				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));

				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));

				t_etax_account_infoExBean.setDaibiaoName_address_Chinese(resultSet.getString("DaibiaoName_address_Chinese"));
				t_etax_account_infoExBean.setDaibiaoName_address_English(resultSet.getString("DaibiaoName_address_English"));
				t_etax_account_infoExBean.setDaibiaoName_address_pianjiaming(resultSet.getString("DaibiaoName_address_pianjiaming"));

				t_etax_account_infoExBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoExBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoExBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoExBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoExBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));

				t_etax_account_infoExBean.setCompany_YYYYMMDD(resultSet.getString("company_YYYYMMDD"));

				t_etax_account_infoExBean.setJCT_NO(resultSet.getString("JCT_NO"));
				t_etax_account_infoExBean.setEmail(resultSet.getString("email"));


				t_etax_account_infoExBean.setTatujin_id(resultSet.getString("tatujin_id"));
				t_etax_account_infoExBean.setUser_type_guanfang(resultSet.getString("user_type_guanfang"));
				t_etax_account_infoExBean.setUser_type_zixuan(resultSet.getString("user_type_zixuan"));

				return t_etax_account_infoExBean;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;
	}

	public t_etax_account_infoExBean selectAI(t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "     , IFNULL(tear.bangou, '') AS bangou"
					+ "     , IFNULL(tear.HoujinBangou, '') AS HoujinBangou"
					+ "     , IFNULL(tear.InvoiceBangou, '') AS InvoiceBangou"
					+ "     , CONCAT(company_YYYY, '', LPAD(company_MM, 2, '0'), '', LPAD(company_DD, 2, '0')) AS company_YYYYMMDD"
					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "    ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ " WHERE tear.InvoiceBangou='" + t_etax_account_infoBean.getJCT_NO() + "'"
					+ " ORDER BY teai.yyyymmdd_count desc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));

				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));

				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));


				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));

				t_etax_account_infoExBean.setDaibiaoName_address_Chinese(resultSet.getString("DaibiaoName_address_Chinese"));
				t_etax_account_infoExBean.setDaibiaoName_address_English(resultSet.getString("DaibiaoName_address_English"));
				t_etax_account_infoExBean.setDaibiaoName_address_pianjiaming(resultSet.getString("DaibiaoName_address_pianjiaming"));


				t_etax_account_infoExBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoExBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoExBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoExBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoExBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));


				t_etax_account_infoExBean.setCompany_YYYYMMDD(resultSet.getString("company_YYYYMMDD"));


				return t_etax_account_infoExBean;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;
	}

	public t_etax_account_infoExBean selectAI3(t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "     , IFNULL(tear.bangou, '') AS bangou"
					+ "     , IFNULL(tear.HoujinBangou, '') AS HoujinBangou"
					+ "     , IFNULL(tear.InvoiceBangou, '') AS InvoiceBangou"
					+ "     , CONCAT(company_YYYY, '', LPAD(company_MM, 2, '0'), '', LPAD(company_DD, 2, '0')) AS company_YYYYMMDD"
					+ "  FROM t_etax_account_info teai"
					+ "  LEFT JOIN t_etax_account_res tear "
					+ "ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ " WHERE teai.CompanyName_English='" + t_etax_account_infoBean.getCompanyName_English() + "'"
					+ "   and teai.DaibiaoName_English='" + t_etax_account_infoBean.getDaibiaoName_English() + "'"
					+ "   and teai.user_type='" + t_etax_account_infoBean.getUser_type() + "'"
					+ " ORDER BY teai.yyyymmdd_count desc"
					//					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));
				t_etax_account_infoExBean.setYaoqing_no(resultSet.getString("yaoqing_no"));

				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));

				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setHoujinBangou(resultSet.getString("HoujinBangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));


				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));

				t_etax_account_infoExBean.setDaibiaoName_address_Chinese(resultSet.getString("DaibiaoName_address_Chinese"));
				t_etax_account_infoExBean.setDaibiaoName_address_English(resultSet.getString("DaibiaoName_address_English"));
				t_etax_account_infoExBean.setDaibiaoName_address_pianjiaming(resultSet.getString("DaibiaoName_address_pianjiaming"));


				t_etax_account_infoExBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoExBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoExBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoExBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoExBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));


				t_etax_account_infoExBean.setCompany_YYYYMMDD(resultSet.getString("company_YYYYMMDD"));


				return t_etax_account_infoExBean;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;
	}

	public LinkedHashMap<String, t_etax_account_infoBean> selectWhere_pianjiaming_null(String byLike_yyyymmdd_count) {
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = new LinkedHashMap<String, t_etax_account_infoBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "  FROM t_etax_account_info teai"
					+ " where ((CompanyName_pianjiaming is null or CompanyName_pianjiaming='')"
					+ "    or (DaibiaoName_pianjiaming is null or DaibiaoName_pianjiaming='')"
					+ "    or (address_pianjiaming is null or address_pianjiaming='')"
					+ "		)"
					+ "";
			if (!StringUtils.isEmpty(byLike_yyyymmdd_count)) {
				sql = sql + ""
						+ "    and yyyymmdd_count like '"+byLike_yyyymmdd_count+"%'"
						+ "";
			}
			sql = sql + ""
					+ " ORDER BY teai.yyyymmdd_count desc"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoBean.setCompanyName_English(resultSet.getString("CompanyName_English"));

				t_etax_account_infoBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoBean.setDaibiaoName_address_Chinese(resultSet.getString("DaibiaoName_address_Chinese"));

				t_etax_account_infoBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoBean.setActivation_code(resultSet.getString("activation_code"));

				t_etax_account_infoBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));
				t_etax_account_infoBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoBean.setDaibiaoName_address_pianjiaming(resultSet.getString("DaibiaoName_address_pianjiaming"));

				LinkedHashMap_t_etax_account_infoBean.put(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoBean;
	}


    public t_etax_account_infoExBean getLastCreatedRecord(String yyyymmddhhmmss) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime localDateTime = LocalDateTime.parse(yyyymmddhhmmss, formatter);

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

		try {

			String sql = ""
					+ "SELECT *"
					+ "  FROM t_etax_account_info"
	                + " WHERE UPDATE_DATE > ?"
					+ " ORDER BY UPDATE_DATE DESC"
					+ " LIMIT 1;"
					+ "";

			int i = 0;
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
	        preparedStatement.setTimestamp(++i, Timestamp.valueOf(localDateTime));

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));


			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;



    }

	public LinkedHashMap<String, t_etax_account_infoBean> selectWhere_etax_pw_bpstax2302() {
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = new LinkedHashMap<String, t_etax_account_infoBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "  FROM t_etax_account_info teai"
					+ " where etax_pw='bpstax2302'"
					+ " ORDER BY teai.yyyymmdd_count desc"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoBean.setActivation_code(resultSet.getString("activation_code"));

				t_etax_account_infoBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));
				t_etax_account_infoBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));

				LinkedHashMap_t_etax_account_infoBean.put(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoBean;
	}

	public t_etax_account_infoExBean selectWhere_etax_pw_activation() {

		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "     , tear.bangou"
					+ "  FROM t_etax_account_info teai"
					+ "     , t_etax_account_res tear"
					+ " where teai.etax_pw_flag='1'"
					+ "   and teai.yyyymmdd_count=tear.yyyymmdd_count"
					+ " ORDER BY teai.yyyymmdd_count desc"
					+ " LIMIT 1"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));

				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));


				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;
	}


	public LinkedHashMap<String, t_etax_account_infoExBean> selectAll_ByLike_CompanyName(String CompanyName) {
		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<String, t_etax_account_infoExBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "     , tear.bangou"
					+ "  FROM t_etax_account_info teai"
					+ "    LEFT JOIN t_etax_account_res tear "
					+ "        ON teai.yyyymmdd_count = tear.yyyymmdd_count"
					+ " where "
					+ "    (CompanyName_Chinese like ?"
					+ "    or CompanyName_English like ?"
					+ "    or DaibiaoName_Chinese like ?"
					+ "    or DaibiaoName_English like ?"
					+ "     )"
					+ "   AND (CompanyName_Chinese NOT LIKE '%（删除20%'"
					+ "     AND CompanyName_English NOT LIKE '%（删除20%'"
					+ "     AND DaibiaoName_Chinese NOT LIKE '%（删除20%'"
					+ "     AND DaibiaoName_English NOT LIKE '%（删除20%'"
					+ "     )"
					+ " ORDER BY teai.yyyymmdd_count desc"
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setString(++i, CompanyName + "%");
			preparedStatement.setString(++i, CompanyName + "%");
			preparedStatement.setString(++i, CompanyName + "%");
			preparedStatement.setString(++i, CompanyName + "%");

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoExBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoExBean.setActivation_code(resultSet.getString("activation_code"));

				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));

				t_etax_account_infoExBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoExBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoExBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoExBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoExBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));


				t_etax_account_infoExBean.setUser_type(resultSet.getString("user_type"));

				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));
				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));

				LinkedHashMap_t_etax_account_infoExBean.put(t_etax_account_infoExBean.getYyyymmdd_count(), t_etax_account_infoExBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoExBean;
	}

	public LinkedHashMap<String, t_etax_account_infoBean> selectAll_ByLike_yyyymmdd_count(String yyyymmdd) {
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = new LinkedHashMap<String, t_etax_account_infoBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT teai.*"
					+ "  FROM t_etax_account_info teai"
					+ " where yyyymmdd_count like '"+yyyymmdd+"%'"
//					+ "   and user_type='公司'"
//					+ "   and user_type='个人'"
					+ " ORDER BY teai.yyyymmdd_count desc"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoBean.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));
				t_etax_account_infoBean.setEtax_no(resultSet.getString("etax_no"));
				t_etax_account_infoBean.setActivation_code(resultSet.getString("activation_code"));

				t_etax_account_infoBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));

				t_etax_account_infoBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));

				LinkedHashMap_t_etax_account_infoBean.put(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoBean;
	}


	public LinkedHashMap<String, ArrayList<String>> selectTatujin() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, ArrayList<String>> TatujinHashMap = new LinkedHashMap<String, ArrayList<String>>();
		try {

			String sql = ""
					+ "SELECT"
					+ "    ti.tatujin_id AS '事業者コード'"
					+ "    , SUBSTRING(ta_gHojinmeiKana.html_value, 1, 25) AS '事業者名フリガナ'"
					+ "    , SUBSTRING(ta_gHojinmei.html_value, 1, 25) AS '事業者名'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '法人' "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '個人' "
					+ "        ELSE '' "
					+ "        END AS '事業者法人個人区分'"
					+ "    , '白色' AS '事業者青白区分'"
					+ "    , '112' AS '事業者郵便番号1'"
					+ "    , '0011' AS '事業者郵便番号2'"
					+ "    , 'トウキョウトブンキョウクセンゴク' AS '事業者所在地フリガナ'"
					+ "    , '東京都文京区千石４丁目１４番９号１階' AS '事業者所在地'"
					+ "    , '03' AS '事業者電話番号1'"
					+ "    , '5981' AS '事業者電話番号2'"
					+ "    , '8383' AS '事業者電話番号3'"
					+ "    , '' AS '事業者FAX1'"
					+ "    , '' AS '事業者FAX2'"
					+ "    , '' AS '事業者FAX3'"
					+ "    , '' AS '事業者URL'"
					+ "    , '' AS '事業者メールアドレス'"
					+ "    , '' AS '関与開始日'"
					+ "    , '' AS '関与終了日'"
					+ "    , '' AS '業務区分(税務代理)'"
					+ "    , '' AS '業務区分(書類作成)'"
					+ "    , '' AS '業務区分(税務相談)'"
					+ "    , '' AS '税理士法第33条の2の書面添付'"
					+ "    , '' AS '予備1'"
					+ "    , '' AS '予備2'"
					+ "    , '' AS '予備3'"
					+ "    , '' AS '備考'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN CASE "
					+ "            WHEN LOWER(tr.HoujinBangou) = 'null' "
					+ "            OR tr.HoujinBangou = '-' "
					+ "                THEN '' "
					+ "            ELSE tr.HoujinBangou "
					+ "            END "
					+ "        ELSE '' "
					+ "        END AS '事業者法人番号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '外国法人' "
					+ "        ELSE '' "
					+ "        END AS '事業者法人区分'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者普通法人等区分'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者公益法人等区分'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '小売業' "
					+ "        ELSE '' "
					+ "        END AS '事業者事業内容'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者屋号フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者屋号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者法人整理番号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '小石川' "
					+ "        ELSE '' "
					+ "        END AS '事業者法人所轄税務署'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '12' "
					+ "        ELSE '' "
					+ "        END AS '決算月'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN tr.bangou "
					+ "        ELSE '' "
					+ "        END AS '法人利用者識別番号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '法人利用者ID'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN CONCAT( "
					+ "            ta_gDSeiKana.html_value"
					+ "            , ta_gDmeiKana.html_value"
					+ "        ) "
					+ "        ELSE '' "
					+ "        END AS '代表者名フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN DaibiaoName_English "
					+ "        ELSE '' "
					+ "        END AS '代表者名'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '代表取締役' "
					+ "        ELSE '' "
					+ "        END AS '代表者役職'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '000' "
					+ "        ELSE '' "
					+ "        END AS '代表者郵便番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '0000' "
					+ "        ELSE '' "
					+ "        END AS '代表者郵便番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN SUBSTRING(ti.DaibiaoName_address_pianjiaming, 1, 25) "
					+ "        ELSE '' "
					+ "        END AS '代表者住所フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN SUBSTRING(ti.DaibiaoName_address_English, 1, 25) "
					+ "        ELSE '' "
					+ "        END AS '代表者住所'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者電話番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者電話番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者電話番号3'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者連絡先1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者連絡先2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者連絡先3'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '代表者メールアドレス'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者名フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者名'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者郵便番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者郵便番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者住所フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者住所'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者電話番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者電話番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者電話番号3'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者連絡先1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者連絡先2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者連絡先3'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '公司' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '経理責任者メールアドレス'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者性別'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN CONCAT( "
					+ "            company_YYYY"
					+ "            , LPAD(company_MM, 2, '0')"
					+ "            , LPAD(company_DD, 2, '0')"
					+ "        ) "
					+ "        ELSE '' "
					+ "        END AS '事業者生年月日'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '自営業者' "
					+ "        ELSE '' "
					+ "        END AS '事業者職業'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者連絡先1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者連絡先2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者連絡先3'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者世帯主の氏名'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者世帯主との続柄'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業者個人整理番号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '小石川' "
					+ "        ELSE '' "
					+ "        END AS '事業者個人所轄税務署'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN tr.bangou "
					+ "        ELSE '' "
					+ "        END AS '個人利用者識別番号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '個人利用者ID'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN SUBSTRING(ti.DaibiaoName_pianjiaming, 1, 25) "
					+ "        ELSE '' "
					+ "        END AS '事業者屋号･雅号フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN SUBSTRING(ti.DaibiaoName_English, 1, 25) "
					+ "        ELSE '' "
					+ "        END AS '事業者屋号･雅号'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所郵便番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所郵便番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所所在地フリガナ'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所所在地'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所電話番号1'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所電話番号2'"
					+ "    , CASE "
					+ "        WHEN ti.user_type_zixuan = '个人' "
					+ "            THEN '' "
					+ "        ELSE '' "
					+ "        END AS '事業所電話番号3'"
					+ "    , '' AS 'アクセス権設定'"
					+ "    , '' AS 'アクセス権設定者' "
					+ "FROM"
					+ "    t_etax_account_info ti "
					+ "    LEFT JOIN t_etax_account ta_gHojinmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmeiKana.yyyymmdd_count "
					+ "        AND ta_gHojinmeiKana.html_id = 'gHojinmeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gHojinmei "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmei.yyyymmdd_count "
					+ "        AND ta_gHojinmei.html_id = 'gHojinmei' "
					+ "    LEFT JOIN t_etax_account ta_gDSei "
					+ "        ON ti.yyyymmdd_count = ta_gDSei.yyyymmdd_count "
					+ "        AND ta_gDSei.html_id = 'gDSei' "
					+ "    LEFT JOIN t_etax_account ta_gDSeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDSeiKana.yyyymmdd_count "
					+ "        AND ta_gDSeiKana.html_id = 'gDSeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gDmei "
					+ "        ON ti.yyyymmdd_count = ta_gDmei.yyyymmdd_count "
					+ "        AND ta_gDmei.html_id = 'gDmei' "
					+ "    LEFT JOIN t_etax_account ta_gDmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDmeiKana.yyyymmdd_count "
					+ "        AND ta_gDmeiKana.html_id = 'gDmeiKana' "
					+ "    LEFT JOIN t_etax_account_res tr "
					+ "        ON ti.yyyymmdd_count = tr.yyyymmdd_count "
					+ "where"
					+ "    tr.yyyymmdd_count is not null "
					+ "    and ( "
					+ "        ta_gHojinmei.html_value IS NULL "
					+ "        or ( "
					+ "            ta_gHojinmei.html_value not like '%ＴＥＳＴ%' "
					+ "            AND ta_gHojinmei.html_value not like '%Ｆｏｒｅｖｅｒ%'"
					+ "        )"
					+ "    ) "
					+ "    and ( "
					+ "        ti.CompanyName_Chinese NOT LIKE '%（删除20%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%（删除20%' "
					+ "        AND ti.DaibiaoName_Chinese NOT LIKE '%（删除20%' "
					+ "        AND ti.DaibiaoName_English NOT LIKE '%（删除20%'"

					+ "        AND ti.CompanyName_Chinese NOT LIKE '%ＴＥＳＴ%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%ＴＥＳＴ%' "
					+ "        AND ti.CompanyName_Chinese NOT LIKE '%Ｆｏｒｅｖｅｒ%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%Ｆｏｒｅｖｅｒ%'"

					+ "    ) "
//					+ "    and ti.tatujin_id in ('V240201647', 'V240202888') "
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				ArrayList<String> TatujinArrayList = new ArrayList<String>();

				TatujinArrayList.add(resultSet.getString("事業者コード"));
				TatujinArrayList.add(resultSet.getString("事業者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者名"));
				TatujinArrayList.add(resultSet.getString("事業者法人個人区分"));
				TatujinArrayList.add(resultSet.getString("事業者青白区分"));
				TatujinArrayList.add(resultSet.getString("事業者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("事業者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("事業者所在地フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者所在地"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号1"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号2"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号3"));
				TatujinArrayList.add(resultSet.getString("事業者FAX1"));
				TatujinArrayList.add(resultSet.getString("事業者FAX2"));
				TatujinArrayList.add(resultSet.getString("事業者FAX3"));
				TatujinArrayList.add(resultSet.getString("事業者URL"));
				TatujinArrayList.add(resultSet.getString("事業者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("関与開始日"));
				TatujinArrayList.add(resultSet.getString("関与終了日"));
				TatujinArrayList.add(resultSet.getString("業務区分(税務代理)"));
				TatujinArrayList.add(resultSet.getString("業務区分(書類作成)"));
				TatujinArrayList.add(resultSet.getString("業務区分(税務相談)"));
				TatujinArrayList.add(resultSet.getString("税理士法第33条の2の書面添付"));
				TatujinArrayList.add(resultSet.getString("予備1"));
				TatujinArrayList.add(resultSet.getString("予備2"));
				TatujinArrayList.add(resultSet.getString("予備3"));
				TatujinArrayList.add(resultSet.getString("備考"));
				TatujinArrayList.add(resultSet.getString("事業者法人番号"));
				TatujinArrayList.add(resultSet.getString("事業者法人区分"));
				TatujinArrayList.add(resultSet.getString("事業者普通法人等区分"));
				TatujinArrayList.add(resultSet.getString("事業者公益法人等区分"));
				TatujinArrayList.add(resultSet.getString("事業者事業内容"));
				TatujinArrayList.add(resultSet.getString("事業者屋号フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者屋号"));
				TatujinArrayList.add(resultSet.getString("事業者法人整理番号"));
				TatujinArrayList.add(resultSet.getString("事業者法人所轄税務署"));
				TatujinArrayList.add(resultSet.getString("決算月"));
				TatujinArrayList.add(resultSet.getString("法人利用者識別番号"));
				TatujinArrayList.add(resultSet.getString("法人利用者ID"));
				TatujinArrayList.add(resultSet.getString("代表者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("代表者名"));
				TatujinArrayList.add(resultSet.getString("代表者役職"));
				TatujinArrayList.add(resultSet.getString("代表者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("代表者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("代表者住所フリガナ"));
				TatujinArrayList.add(resultSet.getString("代表者住所"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号1"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号2"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号3"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先1"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先2"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先3"));
				TatujinArrayList.add(resultSet.getString("代表者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("経理責任者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("経理責任者名"));
				TatujinArrayList.add(resultSet.getString("経理責任者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("経理責任者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("経理責任者住所フリガナ"));
				TatujinArrayList.add(resultSet.getString("経理責任者住所"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号1"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号2"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号3"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先1"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先2"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先3"));
				TatujinArrayList.add(resultSet.getString("経理責任者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("事業者性別"));
				TatujinArrayList.add(resultSet.getString("事業者生年月日"));
				TatujinArrayList.add(resultSet.getString("事業者職業"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先1"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先2"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先3"));
				TatujinArrayList.add(resultSet.getString("事業者世帯主の氏名"));
				TatujinArrayList.add(resultSet.getString("事業者世帯主との続柄"));
				TatujinArrayList.add(resultSet.getString("事業者個人整理番号"));
				TatujinArrayList.add(resultSet.getString("事業者個人所轄税務署"));
				TatujinArrayList.add(resultSet.getString("個人利用者識別番号"));
				TatujinArrayList.add(resultSet.getString("個人利用者ID"));
				TatujinArrayList.add(resultSet.getString("事業者屋号･雅号フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者屋号･雅号"));
				TatujinArrayList.add(resultSet.getString("事業所郵便番号1"));
				TatujinArrayList.add(resultSet.getString("事業所郵便番号2"));
				TatujinArrayList.add(resultSet.getString("事業所所在地フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業所所在地"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号1"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号2"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号3"));
				TatujinArrayList.add(resultSet.getString("アクセス権設定"));
				TatujinArrayList.add(resultSet.getString("アクセス権設定者"));

				TatujinHashMap.put(TatujinArrayList.get(0),TatujinArrayList);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return TatujinHashMap;
	}

	public LinkedHashMap<String, ArrayList<String>> selectTatujinShouxin() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, ArrayList<String>> TatujinHashMap = new LinkedHashMap<String, ArrayList<String>>();
		try {

			String sql = ""
					+ "SELECT"
					+ "    SUBSTRING(ti.CompanyName_English, 1, 25) AS '利用者名(1～30文字)'"
					+ "    , SUBSTRING(ti.CompanyName_pianjiaming, 1, 20) AS 'フリガナ(利用者)(1～60文字)'"
					+ "    , tr.bangou AS '利用者識別番号(16桁)'"
					+ "    , CONCAT( "
					+ "        'V'"
					+ "        , SUBSTR(ti.yyyymmdd_count, 3, 6)"
					+ "        , SUBSTR(ti.yyyymmdd_count, 12, 3)"
					+ "    ) AS '事業者コード(1～10文字)'"
					+ "    , '' AS '旧暗証番号(8～50文字)'"
					+ "    , etax_pw AS '新暗証番号(8～50文字)'"
					+ "    , '1' AS '利用者種別(1：納税者,2：税理士)'"
					+ "    , CASE "
					+ "        WHEN user_type = '公司' "
					+ "            THEN '1' "
					+ "        ELSE '2' "
					+ "        END AS '法人個人区分(1：法人,2：個人)'"
					+ "    , '12' AS '決算月(1～12)'"
					+ "    , '' AS 'メモ(1～50文字)'"
					+ "    , '' AS 'メールアドレス登録(インポート対象外)'"
					+ "    , '' AS '事業者法人整理番号'"
					+ "    , '' AS 'メインメールアドレス(1～128文字)'"
					+ "    , '' AS 'サブメールアドレス１(1～128文字)'"
					+ "    , '' AS 'サブメールアドレス２(1～128文字)'"
					+ "    , '' AS '宛名(1～30文字)' "
					+ "FROM"
					+ "    t_etax_account_info ti "
					+ "    LEFT JOIN t_etax_account ta_gHojinmei "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmei.yyyymmdd_count "
					+ "        AND ta_gHojinmei.html_id = 'gHojinmei'  "
					+ "    LEFT JOIN ( "
					+ "        SELECT"
					+ "            tr1.* "
					+ "        FROM"
					+ "            t_etax_account_res tr1 JOIN ( "
					+ "                SELECT"
					+ "                    bangou"
					+ "                    , MAX(yyyymmdd_count) AS max_count "
					+ "                FROM"
					+ "                    t_etax_account_res "
					+ "                GROUP BY"
					+ "                    bangou"
					+ "            ) t2 "
					+ "                ON tr1.bangou = t2.bangou "
					+ "                AND tr1.yyyymmdd_count = t2.max_count"
					+ "    ) tr "
					+ "        ON ti.yyyymmdd_count = tr.yyyymmdd_count "
					+ "where tr.bangou is not null AND bangou <> ''"
					+ "  and"
					+ "    ( "
					+ "        ti.CompanyName_Chinese NOT LIKE '%（删除20%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%（删除20%' "
					+ "        AND ti.DaibiaoName_Chinese NOT LIKE '%（删除20%' "
					+ "        AND ti.DaibiaoName_English NOT LIKE '%（删除20%'"

					+ "        AND ti.CompanyName_Chinese NOT LIKE '%ＴＥＳＴ%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%ＴＥＳＴ%' "
					+ "        AND ti.CompanyName_Chinese NOT LIKE '%Ｆｏｒｅｖｅｒ%' "
					+ "        AND ti.CompanyName_English NOT LIKE '%Ｆｏｒｅｖｅｒ%'"
					+ "    )"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				ArrayList<String> TatujinArrayList = new ArrayList<String>();

				TatujinArrayList.add(resultSet.getString("利用者名(1～30文字)"));
				TatujinArrayList.add(resultSet.getString("フリガナ(利用者)(1～60文字)"));
				TatujinArrayList.add(resultSet.getString("利用者識別番号(16桁)"));
				TatujinArrayList.add(resultSet.getString("事業者コード(1～10文字)"));
				TatujinArrayList.add(resultSet.getString("旧暗証番号(8～50文字)"));
				TatujinArrayList.add(resultSet.getString("新暗証番号(8～50文字)"));
				TatujinArrayList.add(resultSet.getString("利用者種別(1：納税者,2：税理士)"));
				TatujinArrayList.add(resultSet.getString("法人個人区分(1：法人,2：個人)"));
				TatujinArrayList.add(resultSet.getString("決算月(1～12)"));
				TatujinArrayList.add(resultSet.getString("メモ(1～50文字)"));
				TatujinArrayList.add(resultSet.getString("メールアドレス登録(インポート対象外)"));
//				TatujinArrayList.add(resultSet.getString("事業者法人整理番号"));
				TatujinArrayList.add(resultSet.getString("メインメールアドレス(1～128文字)"));
				TatujinArrayList.add(resultSet.getString("サブメールアドレス１(1～128文字)"));
				TatujinArrayList.add(resultSet.getString("サブメールアドレス２(1～128文字)"));
				TatujinArrayList.add(resultSet.getString("宛名(1～30文字)"));

				TatujinHashMap.put(TatujinArrayList.get(3),TatujinArrayList);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return TatujinHashMap;
	}


	public LinkedHashMap<String, ArrayList<String>> selectTatujinOld() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, ArrayList<String>> TatujinHashMap = new LinkedHashMap<String, ArrayList<String>>();
		try {

			String sql = ""
					+ "SELECT"
					+ "    CONCAT('V', SUBSTR(ti.yyyymmdd_count, 3, 6), SUBSTR(ti.yyyymmdd_count, 12, 3)) AS '事業者コード'"
					+ "    , ta_gHojinmeiKana.html_value AS '事業者名フリガナ'"
					+ "    , SUBSTRING(ta_gHojinmei.html_value, 1, 25) AS '事業者名'"
					+ "    , '法人' AS '事業者法人個人区分'"
					+ "    , '白色' AS '事業者青白区分'"
					+ "    , '112' AS '事業者郵便番号1'"
					+ "    , '0011' AS '事業者郵便番号2'"
					+ "    , 'トウキョウトブンキョウクセンゴク' AS '事業者所在地フリガナ'"
					+ "    , '東京都文京区千石４丁目１４番９号１階' AS '事業者所在地'"
					+ "    , '03' AS '事業者電話番号1'"
					+ "    , '5981' AS '事業者電話番号2'"
					+ "    , '8383' AS '事業者電話番号3'"
					+ "    , '' AS '事業者FAX1'"
					+ "    , '' AS '事業者FAX2'"
					+ "    , '' AS '事業者FAX3'"
					+ "    , '' AS '事業者URL'"
					+ "    , '' AS '事業者メールアドレス'"
					+ "    , '' AS '関与開始日'"
					+ "    , '' AS '関与終了日'"
					+ "    , '非該当' AS '業務区分(税務代理)'"
					+ "    , '非該当' AS '業務区分(書類作成)'"
					+ "    , '非該当' AS '業務区分(税務相談)'"
					+ "    , '制度未適用' AS '税理士法第33条の2の書面添付'"
					+ "    , '簡易' AS '予備1'"
					+ "    , '' AS '予備2'"
					+ "    , '' AS '予備3'"
					+ "    , '' AS '備考'"

					+ "    , CASE "
					+ "        WHEN LOWER(tr.HoujinBangou) = 'null' "
					+ "        OR tr.HoujinBangou = '-' "
					+ "            THEN '' "
					+ "        ELSE tr.HoujinBangou "
					+ "        END AS '事業者法人番号'"

					+ "    , '外国法人' AS '事業者法人区分'"
					+ "    , '' AS '事業者普通法人等区分'"
					+ "    , '' AS '事業者公益法人等区分'"
					+ "    , '小売業' AS '事業者事業内容'"
					+ "    , '' AS '事業者屋号フリガナ'"
					+ "    , '' AS '事業者屋号'"
					+ "    , '' AS '事業者法人整理番号'"
					+ "    , '小石川' AS '事業者法人所轄税務署'"
					+ "    , '12' AS '決算月'"
					+ "    , tr.bangou AS '法人利用者識別番号'"
					+ "    , '' AS '法人利用者ID'"
					+ "    , CONCAT(ta_gDSeiKana.html_value, ta_gDmeiKana.html_value) AS '代表者名フリガナ'"
					+ "    , DaibiaoName_English AS '代表者名'"
					+ "    , '代表取締役' AS '代表者役職'"
					+ "    , '000' AS '代表者郵便番号1'"
					+ "    , '0000' AS '代表者郵便番号2'"
					+ "    , 'チュウカジンミンキョウワコク' AS '代表者住所フリガナ'"
					+ "    , '中華人民共和国' AS '代表者住所'"
					+ "    , '' AS '代表者電話番号1'"
					+ "    , '' AS '代表者電話番号2'"
					+ "    , '' AS '代表者電話番号3'"
					+ "    , '' AS '代表者連絡先1'"
					+ "    , '' AS '代表者連絡先2'"
					+ "    , '' AS '代表者連絡先3'"
					+ "    , '' AS '代表者メールアドレス'"
					+ "    , '' AS '経理責任者名フリガナ'"
					+ "    , '' AS '経理責任者名'"
					+ "    , '' AS '経理責任者郵便番号1'"
					+ "    , '' AS '経理責任者郵便番号2'"
					+ "    , '' AS '経理責任者住所フリガナ'"
					+ "    , '' AS '経理責任者住所'"
					+ "    , '' AS '経理責任者電話番号1'"
					+ "    , '' AS '経理責任者電話番号2'"
					+ "    , '' AS '経理責任者電話番号3'"
					+ "    , '' AS '経理責任者連絡先1'"
					+ "    , '' AS '経理責任者連絡先2'"
					+ "    , '' AS '経理責任者連絡先3'"
					+ "    , '' AS '経理責任者メールアドレス'"
					+ "    , '' AS '事業者性別'"
					+ "    , '' AS '事業者生年月日'"
					+ "    , '' AS '事業者職業'"
					+ "    , '' AS '事業者連絡先1'"
					+ "    , '' AS '事業者連絡先2'"
					+ "    , '' AS '事業者連絡先3'"
					+ "    , '' AS '事業者世帯主の氏名'"
					+ "    , '' AS '事業者世帯主との続柄'"
					+ "    , '' AS '事業者個人整理番号'"
					+ "    , '' AS '事業者個人所轄税務署'"
					+ "    , '' AS '個人利用者識別番号'"
					+ "    , '' AS '個人利用者ID'"
					+ "    , '' AS '事業者屋号･雅号フリガナ'"
					+ "    , '' AS '事業者屋号･雅号'"
					+ "    , '' AS '事業所郵便番号1'"
					+ "    , '' AS '事業所郵便番号2'"
					+ "    , '' AS '事業所所在地フリガナ'"
					+ "    , '' AS '事業所所在地'"
					+ "    , '' AS '事業所電話番号1'"
					+ "    , '' AS '事業所電話番号2'"
					+ "    , '' AS '事業所電話番号3'"
					+ "    , '' AS 'アクセス権設定'"
					+ "    , 'DefaultUser0048' AS 'アクセス権設定者'"
					+ " FROM"
					+ "    t_etax_account_info ti "
					+ "    LEFT JOIN t_etax_account ta_gHojinmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmeiKana.yyyymmdd_count "
					+ "        AND ta_gHojinmeiKana.html_id = 'gHojinmeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gHojinmei "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmei.yyyymmdd_count "
					+ "        AND ta_gHojinmei.html_id = 'gHojinmei' "
					+ "    LEFT JOIN t_etax_account ta_gDSei "
					+ "        ON ti.yyyymmdd_count = ta_gDSei.yyyymmdd_count "
					+ "        AND ta_gDSei.html_id = 'gDSei' "
					+ "    LEFT JOIN t_etax_account ta_gDSeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDSeiKana.yyyymmdd_count "
					+ "        AND ta_gDSeiKana.html_id = 'gDSeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gDmei "
					+ "        ON ti.yyyymmdd_count = ta_gDmei.yyyymmdd_count "
					+ "        AND ta_gDmei.html_id = 'gDmei' "
					+ "    LEFT JOIN t_etax_account ta_gDmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDmeiKana.yyyymmdd_count "
					+ "        AND ta_gDmeiKana.html_id = 'gDmeiKana' "
					+ "    LEFT JOIN t_etax_account_res tr "
					+ "        ON ti.yyyymmdd_count = tr.yyyymmdd_count"
					+ " where tr.yyyymmdd_count is not null"
					+ "   and (ta_gHojinmei.html_value IS NULL or (ta_gHojinmei.html_value not like '%ＴＥＳＴ%' AND ta_gHojinmei.html_value not like '%Ｆｏｒｅｖｅｒ%'))"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				ArrayList<String> TatujinArrayList = new ArrayList<String>();

				TatujinArrayList.add(resultSet.getString("事業者コード"));
				TatujinArrayList.add(resultSet.getString("事業者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者名"));
				TatujinArrayList.add(resultSet.getString("事業者法人個人区分"));
				TatujinArrayList.add(resultSet.getString("事業者青白区分"));
				TatujinArrayList.add(resultSet.getString("事業者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("事業者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("事業者所在地フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者所在地"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号1"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号2"));
				TatujinArrayList.add(resultSet.getString("事業者電話番号3"));
				TatujinArrayList.add(resultSet.getString("事業者FAX1"));
				TatujinArrayList.add(resultSet.getString("事業者FAX2"));
				TatujinArrayList.add(resultSet.getString("事業者FAX3"));
				TatujinArrayList.add(resultSet.getString("事業者URL"));
				TatujinArrayList.add(resultSet.getString("事業者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("関与開始日"));
				TatujinArrayList.add(resultSet.getString("関与終了日"));
				TatujinArrayList.add(resultSet.getString("業務区分(税務代理)"));
				TatujinArrayList.add(resultSet.getString("業務区分(書類作成)"));
				TatujinArrayList.add(resultSet.getString("業務区分(税務相談)"));
				TatujinArrayList.add(resultSet.getString("税理士法第33条の2の書面添付"));
				TatujinArrayList.add(resultSet.getString("予備1"));
				TatujinArrayList.add(resultSet.getString("予備2"));
				TatujinArrayList.add(resultSet.getString("予備3"));
				TatujinArrayList.add(resultSet.getString("備考"));
				TatujinArrayList.add(resultSet.getString("事業者法人番号"));
				TatujinArrayList.add(resultSet.getString("事業者法人区分"));
				TatujinArrayList.add(resultSet.getString("事業者普通法人等区分"));
				TatujinArrayList.add(resultSet.getString("事業者公益法人等区分"));
				TatujinArrayList.add(resultSet.getString("事業者事業内容"));
				TatujinArrayList.add(resultSet.getString("事業者屋号フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者屋号"));
				TatujinArrayList.add(resultSet.getString("事業者法人整理番号"));
				TatujinArrayList.add(resultSet.getString("事業者法人所轄税務署"));
				TatujinArrayList.add(resultSet.getString("決算月"));
				TatujinArrayList.add(resultSet.getString("法人利用者識別番号"));
				TatujinArrayList.add(resultSet.getString("法人利用者ID"));
				TatujinArrayList.add(resultSet.getString("代表者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("代表者名"));
				TatujinArrayList.add(resultSet.getString("代表者役職"));
				TatujinArrayList.add(resultSet.getString("代表者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("代表者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("代表者住所フリガナ"));
				TatujinArrayList.add(resultSet.getString("代表者住所"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号1"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号2"));
				TatujinArrayList.add(resultSet.getString("代表者電話番号3"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先1"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先2"));
				TatujinArrayList.add(resultSet.getString("代表者連絡先3"));
				TatujinArrayList.add(resultSet.getString("代表者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("経理責任者名フリガナ"));
				TatujinArrayList.add(resultSet.getString("経理責任者名"));
				TatujinArrayList.add(resultSet.getString("経理責任者郵便番号1"));
				TatujinArrayList.add(resultSet.getString("経理責任者郵便番号2"));
				TatujinArrayList.add(resultSet.getString("経理責任者住所フリガナ"));
				TatujinArrayList.add(resultSet.getString("経理責任者住所"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号1"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号2"));
				TatujinArrayList.add(resultSet.getString("経理責任者電話番号3"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先1"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先2"));
				TatujinArrayList.add(resultSet.getString("経理責任者連絡先3"));
				TatujinArrayList.add(resultSet.getString("経理責任者メールアドレス"));
				TatujinArrayList.add(resultSet.getString("事業者性別"));
				TatujinArrayList.add(resultSet.getString("事業者生年月日"));
				TatujinArrayList.add(resultSet.getString("事業者職業"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先1"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先2"));
				TatujinArrayList.add(resultSet.getString("事業者連絡先3"));
				TatujinArrayList.add(resultSet.getString("事業者世帯主の氏名"));
				TatujinArrayList.add(resultSet.getString("事業者世帯主との続柄"));
				TatujinArrayList.add(resultSet.getString("事業者個人整理番号"));
				TatujinArrayList.add(resultSet.getString("事業者個人所轄税務署"));
				TatujinArrayList.add(resultSet.getString("個人利用者識別番号"));
				TatujinArrayList.add(resultSet.getString("個人利用者ID"));
				TatujinArrayList.add(resultSet.getString("事業者屋号･雅号フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業者屋号･雅号"));
				TatujinArrayList.add(resultSet.getString("事業所郵便番号1"));
				TatujinArrayList.add(resultSet.getString("事業所郵便番号2"));
				TatujinArrayList.add(resultSet.getString("事業所所在地フリガナ"));
				TatujinArrayList.add(resultSet.getString("事業所所在地"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号1"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号2"));
				TatujinArrayList.add(resultSet.getString("事業所電話番号3"));
				TatujinArrayList.add(resultSet.getString("アクセス権設定"));
				TatujinArrayList.add(resultSet.getString("アクセス権設定者"));

				TatujinHashMap.put(TatujinArrayList.get(0),TatujinArrayList);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return TatujinHashMap;
	}


	public LinkedHashMap<String, ArrayList<String>> selectUserInfo() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, ArrayList<String>> TatujinHashMap = new LinkedHashMap<String, ArrayList<String>>();
		try {

			String sql = ""
					+ "SELECT"
					+ "    DATE_FORMAT(teai.UPDATE_DATE, '%Y-%m-%d %H:%i:%s') AS UPDATE_DATE"
					+ "    , teai.yyyymmdd_count as '管理ID'"
					+ "    , tatujin_id as '达人ID'"
					+ "    , REPLACE (tear.InvoiceBangou, 'T', '') AS '日本消费税税号'"
					+ "    , tear.bangou as 'ETAX账号'"
					+ "    , etax_pw as 'ETAX密码'"
					+ "    , user_type_guanfang as '官方用户类型'"
					+ "    , user_type_zixuan as '自选用户类型'"
					+ "    , CompanyName_Chinese as '公司名称或个体户本人姓名（所在地区文字）'"
					+ "    , CompanyName_English as '公司名称或个体户本人姓名（英文）'"
					+ "    , CompanyName_pianjiaming as '公司名称或个体户本人姓名（日文片假名）'"

					+ "    , address_Chinese as '公司地址或个体户本人住址（所在地区文字）'"
					+ "    , address_English as '公司地址或个体户本人住址（英文）'"
					+ "    , address_pianjiaming as '公司地址或个体户本人住址（日文片假名）'"

					+ "    , DaibiaoName_Chinese as '公司代表人姓名或个体户经营场所名称（所在地区文字）'"
					+ "    , DaibiaoName_English as '公司代表人姓名或个体户经营场所名称（英文）'"
					+ "    , DaibiaoName_pianjiaming as '公司代表人姓名或个体户经营场所名称（日文片假名）'"

					+ "    , DaibiaoName_address_Chinese as '公司代表人住址或个体户经营场所地址（所在地区文字）'"
					+ "    , DaibiaoName_address_English as '公司代表人住址或个体户经营场所地址（英文）'"
					+ "    , DaibiaoName_address_pianjiaming as '公司代表人住址或个体户经营场所地址（日文片假名）'"
					+ "    , company_YYYY as '公司成立年或个体户本人出生年'"
					+ "    , company_MM as '公司成立月或个体户本人出生月'"
					+ "    , company_DD as '公司成立日或个体户本人出生日' "
					+ "from"
					+ "    t_etax_account_info teai "
					+ "    LEFT JOIN t_etax_account_res tear "
					+ "        ON teai.yyyymmdd_count = tear.yyyymmdd_count "
//					+ "where"
//					+ "    ( "
//					+ "        CompanyName_Chinese NOT LIKE '%（删除20%' "
//					+ "        AND CompanyName_English NOT LIKE '%（删除20%' "
//					+ "        AND DaibiaoName_Chinese NOT LIKE '%（删除20%' "
//					+ "        AND DaibiaoName_English NOT LIKE '%（删除20%'"
//					+ "    ) "
					+ "ORDER BY"
					+ "    teai.UPDATE_DATE desc"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				ArrayList<String> TatujinArrayList = new ArrayList<String>();

				TatujinArrayList.add(resultSet.getString("UPDATE_DATE"));
				TatujinArrayList.add(resultSet.getString("管理ID"));
				TatujinArrayList.add(resultSet.getString("达人ID"));
				TatujinArrayList.add(resultSet.getString("日本消费税税号"));
				TatujinArrayList.add(resultSet.getString("ETAX账号"));
				TatujinArrayList.add(resultSet.getString("ETAX密码"));
				TatujinArrayList.add(resultSet.getString("官方用户类型"));
				TatujinArrayList.add(resultSet.getString("自选用户类型"));
				TatujinArrayList.add(resultSet.getString("公司名称或个体户本人姓名（所在地区文字）"));
				TatujinArrayList.add(resultSet.getString("公司名称或个体户本人姓名（英文）"));
				TatujinArrayList.add(resultSet.getString("公司名称或个体户本人姓名（日文片假名）"));

				TatujinArrayList.add(KanaUtil.toUpperCase(resultSet.getString("公司名称或个体户本人姓名（日文片假名）").replace(" ", "").replace("　", "")));

				TatujinArrayList.add(resultSet.getString("公司地址或个体户本人住址（所在地区文字）"));
				TatujinArrayList.add(resultSet.getString("公司地址或个体户本人住址（英文）"));
				TatujinArrayList.add(resultSet.getString("公司地址或个体户本人住址（日文片假名）"));
				TatujinArrayList.add(resultSet.getString("公司代表人姓名或个体户经营场所名称（所在地区文字）"));
				TatujinArrayList.add(resultSet.getString("公司代表人姓名或个体户经营场所名称（英文）"));
				TatujinArrayList.add(resultSet.getString("公司代表人姓名或个体户经营场所名称（日文片假名）"));
				TatujinArrayList.add(resultSet.getString("公司代表人住址或个体户经营场所地址（所在地区文字）"));
				TatujinArrayList.add(resultSet.getString("公司代表人住址或个体户经营场所地址（英文）"));
				TatujinArrayList.add(resultSet.getString("公司代表人住址或个体户经营场所地址（日文片假名）"));
				TatujinArrayList.add(resultSet.getString("公司成立年或个体户本人出生年"));
				TatujinArrayList.add(resultSet.getString("公司成立月或个体户本人出生月"));
				TatujinArrayList.add(resultSet.getString("公司成立日或个体户本人出生日"));

				TatujinHashMap.put(TatujinArrayList.get(1),TatujinArrayList);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return TatujinHashMap;
	}



	public LinkedHashMap<String, String> selectBangouNotNull() {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, String> LinkedHashMap = new LinkedHashMap<String, String>();
		try {

			String sql = ""
					+ "SELECT ti.*"
					+ "  FROM"
					+ "    t_etax_account_info ti "
					+ "  , t_etax_account_res tr"
					+ "  where ti.yyyymmdd_count = tr.yyyymmdd_count"
					+ "   and ti.syouninn_status <> '完了'"
					+ "   and tr.bangou is not null"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				LinkedHashMap.put(resultSet.getString("yyyymmdd_count"), resultSet.getString("CompanyName_Chinese"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap;
	}
	public t_etax_account_infoBean selectByActivation_code(String activation_code) {

		t_etax_account_infoBean EtaxAccountInfoBean = new t_etax_account_infoBean();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT ti.*"
					+ "  FROM"
					+ "    t_etax_account_info ti "
					+ "  where activation_code = '" + activation_code + "'"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				EtaxAccountInfoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				EtaxAccountInfoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxAccountInfoBean.setUser_id(resultSet.getString("user_id"));
				EtaxAccountInfoBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				EtaxAccountInfoBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				EtaxAccountInfoBean.setCompany_DD(resultSet.getString("company_DD"));
				EtaxAccountInfoBean.setCompany_MM(resultSet.getString("company_MM"));
				EtaxAccountInfoBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				EtaxAccountInfoBean.setTel_1(resultSet.getString("tel_1"));
				EtaxAccountInfoBean.setTel_2(resultSet.getString("tel_2"));
				EtaxAccountInfoBean.setTel_3(resultSet.getString("tel_3"));
				EtaxAccountInfoBean.setTel_country(resultSet.getString("tel_country"));
				EtaxAccountInfoBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				EtaxAccountInfoBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				EtaxAccountInfoBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				EtaxAccountInfoBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				EtaxAccountInfoBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				EtaxAccountInfoBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				EtaxAccountInfoBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				EtaxAccountInfoBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				EtaxAccountInfoBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				EtaxAccountInfoBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				EtaxAccountInfoBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				EtaxAccountInfoBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				EtaxAccountInfoBean.setAddress_English(resultSet.getString("address_English"));
				EtaxAccountInfoBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				EtaxAccountInfoBean
						.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				EtaxAccountInfoBean
						.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				EtaxAccountInfoBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));


				return EtaxAccountInfoBean;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return EtaxAccountInfoBean;
	}

	public t_etax_account_infoBean selectByActivation_code(String table_name, String activation_code) {

		t_etax_account_infoBean EtaxAccountInfoBean = new t_etax_account_infoBean();
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "SELECT *"
					+ "  FROM " + table_name
					+ "  where activation_code = '" + activation_code + "'"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				EtaxAccountInfoBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				EtaxAccountInfoBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				EtaxAccountInfoBean.setUser_id(resultSet.getString("user_id"));
				EtaxAccountInfoBean.setActivation_code(resultSet.getString("activation_code"));


				return EtaxAccountInfoBean;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return EtaxAccountInfoBean;
	}


	public LinkedHashMap<String, HashMap<String, String>> selectEx_t_etax_account(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, HashMap<String, String>> LinkedHashMap = new LinkedHashMap<String, HashMap<String, String>>();
		try {

			String sql = ""
//					+ "SELECT ti.* FROM t_etax_account_info ti where ti.syouninn_status = '保留無'"
					+ ""

					+ "SELECT ti.*"
					+ "    , CONCAT('V', SUBSTR(ti.yyyymmdd_count, 3, 6), SUBSTR(ti.yyyymmdd_count, 12, 3)) AS 'tatuji_code'"
					+ "    , tr.bangou"
					+ "    , tr.HoujinBangou"
					+ "    , ta_gHojinmeiKana.html_value AS 'ta_gHojinmeiKana'"
					+ "    , ta_gHojinmei.html_value AS 'ta_gHojinmei'"
					+ "    , ta_gDSei.html_value AS 'ta_gDSei'"
					+ "    , ta_gDSeiKana.html_value AS 'ta_gDSeiKana'"
					+ "    , ta_gDmei.html_value AS 'ta_gDmei'"
					+ "    , ta_gDmeiKana.html_value AS 'ta_gDmeiKana'"
					+ "    , teax.yuanze_or_jianyi"
					+ "    , teax.keshui_maishang_2_xiaomai"
					+ "    , teax.zhongjian_nafu_shuie"
					+ "    , teax.zhongjian_nafu_durang"
					+ "    , teax.xiaoshoue_10"
					+ "    , teax.xiaoshoue_8"
					+ "    , teax.fapiao_10"
					+ "    , teax.fapiao_8"
					+ "    , teax.fapiao_10_20231001"
					+ "    , teax.fapiao_10_20261001"
					+ "    , teax.fapiao_8_20231001"
					+ "    , teax.fapiao_8_20261001"
					+ "    , teax.jinkou_xiaofeishui_guoshui"
					+ "    , teax.jinkou_xiaofeishui_dishui"
					+ " FROM"
					+ "    t_etax_account_info ti "
					+ "    LEFT JOIN t_etax_account ta_gHojinmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmeiKana.yyyymmdd_count "
					+ "        AND ta_gHojinmeiKana.html_id = 'gHojinmeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gHojinmei "
					+ "        ON ti.yyyymmdd_count = ta_gHojinmei.yyyymmdd_count "
					+ "        AND ta_gHojinmei.html_id = 'gHojinmei' "
					+ "    LEFT JOIN t_etax_account ta_gDSei "
					+ "        ON ti.yyyymmdd_count = ta_gDSei.yyyymmdd_count "
					+ "        AND ta_gDSei.html_id = 'gDSei' "
					+ "    LEFT JOIN t_etax_account ta_gDSeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDSeiKana.yyyymmdd_count "
					+ "        AND ta_gDSeiKana.html_id = 'gDSeiKana' "
					+ "    LEFT JOIN t_etax_account ta_gDmei "
					+ "        ON ti.yyyymmdd_count = ta_gDmei.yyyymmdd_count "
					+ "        AND ta_gDmei.html_id = 'gDmei' "
					+ "    LEFT JOIN t_etax_account ta_gDmeiKana "
					+ "        ON ti.yyyymmdd_count = ta_gDmeiKana.yyyymmdd_count "
					+ "        AND ta_gDmeiKana.html_id = 'gDmeiKana' "
					+ "    LEFT JOIN t_etax_account_res tr "
					+ "        ON ti.yyyymmdd_count = tr.yyyymmdd_count"
					+ "    LEFT JOIN t_etax_account_xiaofeishui teax "
					+ "        ON ti.yyyymmdd_count = teax.yyyymmdd_count"
					+ "  where ti.yyyymmdd_count = '"+yyyymmdd_count+"' "

//					+ "  where tr.horyuu = '待处理' "
//					+ "   and (tr.bangou is not null or tr.bangou != '')"


					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				HashMap<String, String> HashMap = new HashMap<String, String>();
				HashMap.put("UPDATE_DATE", resultSet.getString("UPDATE_DATE"));
				HashMap.put("yyyymmdd_count", resultSet.getString("yyyymmdd_count"));
				HashMap.put("user_id", resultSet.getString("user_id"));
				HashMap.put("syouninn_status", resultSet.getString("syouninn_status"));
				HashMap.put("DaibiaoName_English", resultSet.getString("DaibiaoName_English"));
				HashMap.put("company_DD", resultSet.getString("company_DD"));
				HashMap.put("company_MM", resultSet.getString("company_MM"));
				HashMap.put("company_YYYY", resultSet.getString("company_YYYY"));
				HashMap.put("tel_1", resultSet.getString("tel_1"));
				HashMap.put("tel_2", resultSet.getString("tel_2"));
				HashMap.put("tel_3", resultSet.getString("tel_3"));
				HashMap.put("tel_country", resultSet.getString("tel_country"));
				HashMap.put("xiaoshouerYYYY_1", resultSet.getString("xiaoshouerYYYY_1"));
				HashMap.put("xiaoshouerYYYY_1_half", resultSet.getString("xiaoshouerYYYY_1_half"));
				HashMap.put("xiaoshouerYYYY_2", resultSet.getString("xiaoshouerYYYY_2"));
				HashMap.put("zhice_ziben", resultSet.getString("zhice_ziben"));
				HashMap.put("address_Chinese", resultSet.getString("address_Chinese"));
				HashMap.put("CompanyName_Chinese", resultSet.getString("CompanyName_Chinese"));
				HashMap.put("CompanyName_English", resultSet.getString("CompanyName_English"));
				HashMap.put("DaibiaoName_Chinese", resultSet.getString("DaibiaoName_Chinese"));
				HashMap.put("geren_dianpu_address", resultSet.getString("geren_dianpu_address"));
				HashMap.put("geren_dianpu_name", resultSet.getString("geren_dianpu_name"));
				HashMap.put("changshe_jigou_Select", resultSet.getString("changshe_jigou_Select"));
				HashMap.put("jianyi_keshui_Select", resultSet.getString("jianyi_keshui_Select"));
				HashMap.put("address_English", resultSet.getString("address_English"));
				HashMap.put("jianyi_keshui_type", resultSet.getString("jianyi_keshui_type"));
				HashMap.put("tokutei_kikann_siharai_kyuuyo", resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				HashMap.put("shouri_kaishi_denglu_xiayige", resultSet.getString("shouri_kaishi_denglu_xiayige"));
				HashMap.put("shouri_kaishi_denglu_ben", resultSet.getString("shouri_kaishi_denglu_ben"));


				HashMap.put("etax_no", resultSet.getString("etax_no"));
				HashMap.put("activation_code", resultSet.getString("activation_code"));
				HashMap.put("yaoqing_no", resultSet.getString("yaoqing_no"));
				HashMap.put("etax_pw", resultSet.getString("etax_pw"));
				HashMap.put("CompanyName_pianjiaming", resultSet.getString("CompanyName_pianjiaming"));
				HashMap.put("address_pianjiaming", resultSet.getString("address_pianjiaming"));
				HashMap.put("DaibiaoName_pianjiaming", resultSet.getString("DaibiaoName_pianjiaming"));
				HashMap.put("nashuidi_youbian1", resultSet.getString("nashuidi_youbian1"));
				HashMap.put("nashuidi_youbian2", resultSet.getString("nashuidi_youbian2"));
				HashMap.put("ksaTodofuken", resultSet.getString("ksaTodofuken"));
				HashMap.put("nashuidi", resultSet.getString("nashuidi"));
				HashMap.put("nashuidi_pianjiaming", resultSet.getString("nashuidi_pianjiaming"));
				HashMap.put("nashuidi_tel1", resultSet.getString("nashuidi_tel1"));
				HashMap.put("nashuidi_tel2", resultSet.getString("nashuidi_tel2"));
				HashMap.put("nashuidi_tel3", resultSet.getString("nashuidi_tel3"));
				HashMap.put("guanxia_shuiwushu", resultSet.getString("guanxia_shuiwushu"));
				HashMap.put("liyongzhe_shibie_fanhao", resultSet.getString("liyongzhe_shibie_fanhao"));
				HashMap.put("user_type", resultSet.getString("user_type"));
				HashMap.put("xiaofeishui_shuihao", resultSet.getString("xiaofeishui_shuihao"));
				HashMap.put("yuanze_or_jianyi", resultSet.getString("yuanze_or_jianyi"));


				HashMap.put("tatuji_code", resultSet.getString("tatuji_code"));
				HashMap.put("bangou", resultSet.getString("bangou"));
				HashMap.put("HoujinBangou", resultSet.getString("HoujinBangou"));
				HashMap.put("ta_gHojinmeiKana", resultSet.getString("ta_gHojinmeiKana"));
				HashMap.put("ta_gHojinmei", resultSet.getString("ta_gHojinmei"));
				HashMap.put("ta_gDSei", resultSet.getString("ta_gDSei"));
				HashMap.put("ta_gDSeiKana", resultSet.getString("ta_gDSeiKana"));
				HashMap.put("ta_gDmei", resultSet.getString("ta_gDmei"));
				HashMap.put("ta_gDmeiKana", resultSet.getString("ta_gDmeiKana"));

				HashMap.put("keshui_maishang_2_xiaomai", resultSet.getString("keshui_maishang_2_xiaomai"));
				HashMap.put("zhongjian_nafu_shuie", resultSet.getString("zhongjian_nafu_shuie"));
				HashMap.put("zhongjian_nafu_durang", resultSet.getString("zhongjian_nafu_durang"));
				HashMap.put("xiaoshoue_10", resultSet.getString("xiaoshoue_10"));
				HashMap.put("xiaoshoue_8", resultSet.getString("xiaoshoue_8"));
				HashMap.put("fapiao_10", resultSet.getString("fapiao_10"));
				HashMap.put("fapiao_8", resultSet.getString("fapiao_8"));
				HashMap.put("fapiao_10_20231001", resultSet.getString("fapiao_10_20231001"));
				HashMap.put("fapiao_10_20261001", resultSet.getString("fapiao_10_20261001"));
				HashMap.put("fapiao_8_20231001", resultSet.getString("fapiao_8_20231001"));
				HashMap.put("fapiao_8_20261001", resultSet.getString("fapiao_8_20261001"));
				HashMap.put("jinkou_xiaofeishui_guoshui", resultSet.getString("jinkou_xiaofeishui_guoshui"));
				HashMap.put("jinkou_xiaofeishui_dishui", resultSet.getString("jinkou_xiaofeishui_dishui"));

				LinkedHashMap.put(resultSet.getString("yyyymmdd_count"), HashMap);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap;
	}



	public int INSERT(t_etax_account_infoBean etaxAccountInfoBean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
			String sql = "INSERT INTO t_etax_account_info ("
			        + " UPDATE_DATE, yyyymmdd_count, user_id, syouninn_status, DaibiaoName_English, company_DD, company_MM, company_YYYY"
			        + ", tel_1, tel_2, tel_3, tel_country, xiaoshouerYYYY_1, xiaoshouerYYYY_1_half, xiaoshouerYYYY_2, zhice_ziben"
			        + ", address_Chinese, address_English, CompanyName_Chinese, CompanyName_English, DaibiaoName_Chinese, geren_dianpu_address"
			        + ", geren_dianpu_name, changshe_jigou_Select, jianyi_keshui_Select, jianyi_keshui_type, dataFileName"
			        + ", tokutei_kikann_siharai_kyuuyo, shouri_kaishi_denglu_xiayige, shouri_kaishi_denglu_ben, etax_no, activation_code, yaoqing_no, etax_pw"
			        + ", CompanyName_pianjiaming, address_pianjiaming, DaibiaoName_pianjiaming"
			        + ", DaibiaoName_address_Chinese, DaibiaoName_address_English, DaibiaoName_address_pianjiaming"
			        + ", nashuidi_youbian1, nashuidi_youbian2, ksaTodofuken, nashuidi"
			        + ", nashuidi_pianjiaming, nashuidi_tel1, nashuidi_tel2, nashuidi_tel3, guanxia_shuiwushu, liyongzhe_shibie_fanhao, user_type"
			        + ", nashui_guanliren, nashui_CompanyName, nashui_DaibiaoName, nashui_address, nashui_di_biangengqian, nashui_shuiwushu_biangengqian, nashui_shuiwushu_fanhao"
			        + ", etax_pw_flag, email, JCT_NO, tatujin_id, user_type_guanfang, user_type_zixuan) "
			        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			        + ", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			        + ", ?, ?, ?, ?, ?, ?)";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, etaxAccountInfoBean.getYyyymmdd_count());
			preparedStatement.setString(++i, etaxAccountInfoBean.getUser_id());
			preparedStatement.setString(++i, etaxAccountInfoBean.getSyouninn_status());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_English());
			preparedStatement.setString(++i, etaxAccountInfoBean.getCompany_DD());
			preparedStatement.setString(++i, etaxAccountInfoBean.getCompany_MM());
			preparedStatement.setString(++i, etaxAccountInfoBean.getCompany_YYYY());
			preparedStatement.setString(++i, etaxAccountInfoBean.getTel_1());
			preparedStatement.setString(++i, etaxAccountInfoBean.getTel_2());
			preparedStatement.setString(++i, etaxAccountInfoBean.getTel_3());
			preparedStatement.setString(++i, etaxAccountInfoBean.getTel_country());
			preparedStatement.setString(++i, etaxAccountInfoBean.getXiaoshouerYYYY_1());
			preparedStatement.setString(++i, etaxAccountInfoBean.getXiaoshouerYYYY_1_half());
			preparedStatement.setString(++i, etaxAccountInfoBean.getXiaoshouerYYYY_2());
			preparedStatement.setString(++i, etaxAccountInfoBean.getZhice_ziben());
			preparedStatement.setString(++i, etaxAccountInfoBean.getAddress_Chinese());
			preparedStatement.setString(++i, etaxAccountInfoBean.getAddress_English());
			preparedStatement.setString(++i, etaxAccountInfoBean.getCompanyName_Chinese());
			preparedStatement.setString(++i, etaxAccountInfoBean.getCompanyName_English());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_Chinese());
			preparedStatement.setString(++i, etaxAccountInfoBean.getGeren_dianpu_address());
			preparedStatement.setString(++i, etaxAccountInfoBean.getGeren_dianpu_name());
			preparedStatement.setString(++i, etaxAccountInfoBean.getChangshe_jigou_Select());
			preparedStatement.setString(++i, etaxAccountInfoBean.getJianyi_keshui_Select());
			preparedStatement.setString(++i, etaxAccountInfoBean.getJianyi_keshui_type());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDataFileName());
			preparedStatement.setString(++i, etaxAccountInfoBean.getTokutei_kikann_siharai_kyuuyo());
			preparedStatement.setString(++i, etaxAccountInfoBean.getShouri_kaishi_denglu_xiayige());
			preparedStatement.setString(++i, etaxAccountInfoBean.getShouri_kaishi_denglu_ben());
			preparedStatement.setString(++i, etaxAccountInfoBean.getEtax_no());
			preparedStatement.setString(++i, etaxAccountInfoBean.getActivation_code());
			preparedStatement.setString(++i, etaxAccountInfoBean.getYaoqing_no());
			preparedStatement.setString(++i, etaxAccountInfoBean.getEtax_pw());

			preparedStatement.setString(++i, etaxAccountInfoBean.getCompanyName_pianjiaming());
			preparedStatement.setString(++i, etaxAccountInfoBean.getAddress_pianjiaming());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_pianjiaming());


			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_address_Chinese());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_address_English());
			preparedStatement.setString(++i, etaxAccountInfoBean.getDaibiaoName_address_pianjiaming());


			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_youbian1());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_youbian2());
			preparedStatement.setString(++i, etaxAccountInfoBean.getKsaTodofuken());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_pianjiaming());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_tel1());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_tel2());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashuidi_tel3());
			preparedStatement.setString(++i, etaxAccountInfoBean.getGuanxia_shuiwushu());
			preparedStatement.setString(++i, etaxAccountInfoBean.getLiyongzhe_shibie_fanhao());
			preparedStatement.setString(++i, etaxAccountInfoBean.getUser_type());


			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_guanliren());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_CompanyName());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_DaibiaoName());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_address());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_di_biangengqian());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_shuiwushu_biangengqian());
			preparedStatement.setString(++i, etaxAccountInfoBean.getNashui_shuiwushu_fanhao());

			preparedStatement.setString(++i, etaxAccountInfoBean.getEtax_pw_flag());

			preparedStatement.setString(++i, etaxAccountInfoBean.getEmail());
			preparedStatement.setString(++i, etaxAccountInfoBean.getJCT_NO());


			preparedStatement.setString(++i, etaxAccountInfoBean.getTatujin_id());
			preparedStatement.setString(++i, etaxAccountInfoBean.getUser_type_guanfang());
			preparedStatement.setString(++i, etaxAccountInfoBean.getUser_type_zixuan());


			logger.debug(preparedStatement.toString());
			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}



	public int Update_syouninn_status(String yyyymmdd_count, String syouninn_status) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
					+ "   SET syouninn_status='" + syouninn_status + "'"
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

	public int Update_activation_code(String yyyymmdd_count, String key) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
					+ "   SET activation_code= ?"
					+ " where yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, key);
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

	public int Update_xiaofeishui(String yyyymmdd_count, t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET"
					+ " `UPDATE_DATE` = ?"
					+ " , `CompanyName_pianjiaming` = ?"
					+ " , `address_pianjiaming` = ?"
					+ " , `DaibiaoName_pianjiaming` = ?"
					+ " , `nashuidi_youbian1` = ?"
					+ " , `nashuidi_youbian2` = ?"
					+ " , `ksaTodofuken` = ?"
					+ " , `nashuidi` = ?"
					+ " , `nashuidi_pianjiaming` = ?"
					+ " , `nashuidi_tel1` = ?"
					+ " , `nashuidi_tel2` = ?"
					+ " , `nashuidi_tel3` = ?"
					+ " , `guanxia_shuiwushu` = ?"
					+ " , `liyongzhe_shibie_fanhao` = ?"
					+ " , `xiaofeishui_shuihao` = ?"
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_pianjiaming());
			preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_pianjiaming());
			preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_pianjiaming());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_youbian1());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_youbian2());
			preparedStatement.setString(++i, t_etax_account_infoBean.getKsaTodofuken());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_pianjiaming());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_tel1());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_tel2());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashuidi_tel3());
			preparedStatement.setString(++i, t_etax_account_infoBean.getGuanxia_shuiwushu());
			preparedStatement.setString(++i, t_etax_account_infoBean.getLiyongzhe_shibie_fanhao());
			preparedStatement.setString(++i, t_etax_account_infoBean.getXiaofeishui_shuihao());
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


	public int Update_user_info(String yyyymmdd_count, t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET"
					+ " `UPDATE_DATE` = ?"
					+ " , `user_id` = CASE "
					+ "        WHEN ? IS NULL OR ? = '' THEN `user_id`"
					+ "        ELSE ?"
					+ "    END"
					+ " , `CompanyName_Chinese` = ?"
					+ " , `address_Chinese` = ?"
					+ " , `DaibiaoName_English` = ?"
					+ " , `DaibiaoName_Chinese` = ?"
					+ " , `company_YYYY` = ?"
					+ " , `company_MM` = ?"
					+ " , `company_DD` = ?"
					+ " , `yaoqing_no` = ?"
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(++i, t_etax_account_infoBean.getUser_id());
			preparedStatement.setString(++i, t_etax_account_infoBean.getUser_id());
			preparedStatement.setString(++i, t_etax_account_infoBean.getUser_id());
			preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_Chinese());
			preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_Chinese());
			preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_English());
			preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_Chinese());
			preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_YYYY());
			preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_MM());
			preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_DD());
			preparedStatement.setString(++i, t_etax_account_infoBean.getYaoqing_no());
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

	public int Update_user_info_AI(String yyyymmdd_count, t_etax_account_infoBean t_etax_account_infoBean) {

	    PreparedStatement preparedStatement = null;

	    try {

	        String sql = ""
	                + " UPDATE `psma`.`t_etax_account_info`"
	                + " SET"
	                + " `UPDATE_DATE` = ?"
	                + " , `user_id` = ?"
	                + " , `user_type` = ?"
	                + " , `CompanyName_English` = ?"
	                + " , `CompanyName_Chinese` = ?"
	                + " , `CompanyName_pianjiaming` = ?"
	                + " , `DaibiaoName_English` = ?"
	                + " , `DaibiaoName_Chinese` = ?"
	                + " , `DaibiaoName_pianjiaming` = ?"
	                + " , `address_English` = ?"
	                + " , `address_Chinese` = ?"
	                + " , `address_pianjiaming` = ?"
	                + " , `DaibiaoName_address_Chinese` = ?"
	                + " , `DaibiaoName_address_English` = ?"
	                + " , `DaibiaoName_address_pianjiaming` = ?"
	                + " , `company_YYYY` = ?"
	                + " , `company_MM` = ?"
	                + " , `company_DD` = ?"
	                + " , `tel_country` = ?"
	                + " , `tel_1` = ?"
	                + " , `tel_2` = ?"
	                + " , `tel_3` = ?"
	                + " , `zhice_ziben` = ?"
	                + " , `JCT_NO` = ?"
	                + " , `email` = ?"
	                + " WHERE `yyyymmdd_count` = ?";

	        preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
	        int i = 0;

	        preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis())); // UPDATE_DATE
	        preparedStatement.setString(++i, t_etax_account_infoBean.getUser_id());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getUser_type());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_English());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_Chinese());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_pianjiaming());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_English());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_Chinese());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_pianjiaming());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_English());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_Chinese());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_pianjiaming());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_address_Chinese());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_address_English());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_address_pianjiaming());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_YYYY());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_MM());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getCompany_DD());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getTel_country());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getTel_1());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getTel_2());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getTel_3());

	        preparedStatement.setString(++i, t_etax_account_infoBean.getZhice_ziben());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getJCT_NO());
	        preparedStatement.setString(++i, t_etax_account_infoBean.getEmail());

	        preparedStatement.setString(++i, yyyymmdd_count); // WHERE 条件

	        logger.debug(preparedStatement.toString());
	        int count = preparedStatement.executeUpdate();
	        logger.debug("SQL Update count = " + count);

	        return count;

	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        JdbcUtils.close(null, preparedStatement, connection);
	    }
	    return 0;
	}


	public int Update_pianjiaming(String yyyymmdd_count, t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET"
					+ "";

			if (!StringUtils.isEmpty(t_etax_account_infoBean.getCompanyName_pianjiaming())) {
				sql = sql + ""
						+ "   `CompanyName_pianjiaming` = ?"
						+ "";
			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_pianjiaming())) {
				sql = sql + ""
						+ " , `DaibiaoName_pianjiaming` = ?"
						+ "";
			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getAddress_pianjiaming())) {
				sql = sql + ""
						+ " , `address_pianjiaming` = ?"
						+ "";

			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_address_pianjiaming())) {
				sql = sql + ""
						+ " , `DaibiaoName_address_pianjiaming` = ?"
						+ "";

			}


			sql = sql + ""
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;

			if (!StringUtils.isEmpty(t_etax_account_infoBean.getCompanyName_pianjiaming())) {
				preparedStatement.setString(++i, t_etax_account_infoBean.getCompanyName_pianjiaming());
			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_pianjiaming())) {
				preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_pianjiaming());
			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getAddress_pianjiaming())) {
				preparedStatement.setString(++i, t_etax_account_infoBean.getAddress_pianjiaming());
			}
			if (!StringUtils.isEmpty(t_etax_account_infoBean.getDaibiaoName_address_pianjiaming())) {
				preparedStatement.setString(++i, t_etax_account_infoBean.getDaibiaoName_address_pianjiaming());
			}

			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			int count = 0;
			count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}


	public int Update_etax_pw(String yyyymmdd_count, t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET `etax_pw` = ?"
					+ "   , etax_pw_flag=1"
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_account_infoBean.getEtax_pw());
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			int count = 0;
			count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return 0;
	}

	public int Update_For_API(t_etax_account_infoExBean t_etax_account_infoExBeanAPI) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET"
					+ "   `CompanyName_English` = ?"
					+ " , `CompanyName_pianjiaming` = ?"
					+ " , `address_English` = ?"
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, FuncUtils.toHalfWidth(t_etax_account_infoExBeanAPI.getCompanyName_English()));
			preparedStatement.setString(++i, t_etax_account_infoExBeanAPI.getCompanyName_pianjiaming());
			preparedStatement.setString(++i, FuncUtils.toHalfWidth(t_etax_account_infoExBeanAPI.getAddress_English()));
			preparedStatement.setString(++i, FuncUtils.toHalfWidth(t_etax_account_infoExBeanAPI.getYyyymmdd_count()));

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

	public int Update_nashui(t_etax_account_infoBean t_etax_account_infoBean) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ " UPDATE `psma`.`t_etax_account_info`"
					+ " SET nashui_guanliren = ?"
					+ "   , nashui_CompanyName = ?"
					+ "   , nashui_DaibiaoName = ?"
					+ "   , nashui_address = ?"
					+ "   , nashui_di_biangengqian = ?"
					+ "   , nashui_shuiwushu_biangengqian = ?"
					+ "   , nashui_shuiwushu_fanhao = ?"
					+ " WHERE `yyyymmdd_count` = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_guanliren());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_CompanyName());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_DaibiaoName());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_address());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_di_biangengqian());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_shuiwushu_biangengqian());
			preparedStatement.setString(++i, t_etax_account_infoBean.getNashui_shuiwushu_fanhao());

			preparedStatement.setString(++i, FuncUtils.toHalfWidth(t_etax_account_infoBean.getYyyymmdd_count()));

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


	public int Update_key_value(String yyyymmdd_count, String key, String value) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
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

	public int Update_Del(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
					+ "   SET CompanyName_Chinese = CONCAT(CompanyName_Chinese, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）')"
					+ "     , CompanyName_English = CONCAT(CompanyName_English, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）') "
					+ " WHERE yyyymmdd_count = ?"
					+ "   AND (CompanyName_Chinese not like '%（删除%' OR CompanyName_English not like '%（删除%')"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			e.printStackTrace();
			return -9999;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}


	public int Update_Del_where_yyyymmdd_count(String yyyymmdd_count) throws SQLException {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
					+ "   SET CompanyName_Chinese = CONCAT(CompanyName_Chinese, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）')"
					+ "     , CompanyName_English = CONCAT(CompanyName_English, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）') "
					+ " WHERE yyyymmdd_count = ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}

	public int Update_Del_where_CompanyName(t_etax_account_infoExBean t_etax_account_infoExBean) throws SQLException {

		PreparedStatement preparedStatement = null;

		try {

			String sql = ""
					+ "UPDATE t_etax_account_info"
					+ "   SET CompanyName_Chinese = CONCAT(CompanyName_Chinese, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）')"
					+ "     , CompanyName_English = CONCAT(CompanyName_English, '（删除', DATE_FORMAT(now(3), '%Y%m%d%H%i%s'), '）') "
					+ " WHERE CompanyName_Chinese like ?"
					+ "    OR CompanyName_English like ?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, t_etax_account_infoExBean.getCompanyName_Chinese());
			preparedStatement.setString(++i, t_etax_account_infoExBean.getCompanyName_English());

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);

			return count;
		} catch (SQLException e) {
			throw e;
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
	}



	public LinkedHashMap<String, LinkedHashMap<String, String>> selectTongji(User_infoBean user_infoBean) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		LinkedHashMap<String, LinkedHashMap<String, String>> LinkedHashMapTongji = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		try {

			String sql = ""
					+ "SELECT"
					+ "    month"
					+ "    , user_id"
					+ "    , COUNT(1) AS operation_count "
					+ "FROM"
					+ "    ( "
					+ "        SELECT"
					+ "            DATE_FORMAT(UPDATE_DATE, '%Y-%m') AS month"
					+ "";

			if ("admin".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ "            , user_id "
						+ "";

			} else {
				sql = sql
						+ "            , CASE "
						+ "                WHEN user_id LIKE 'add_%' "
						+ "                OR user_id LIKE 'piliang_%' "
						+ "                    THEN yaoqing_no "
						+ "                ELSE user_id "
						+ "                END AS user_id "
						+ "";


			}

			sql = sql
					+ "        FROM"
					+ "            t_etax_account_info teai "
					+ "";


			if (!"admin".equals(user_infoBean.getPermissions()) && !"zeirisi".equals(user_infoBean.getPermissions())) {
				sql = sql
						+ " WHERE teai.user_id='" + user_infoBean.getUser_id() + "'"
						+ "";

				if ("groupAdmin".equals(user_infoBean.getPermissions())) {
					HashMap<String, User_infoBean> HashMap = user_infoBean.getGroup_id_user_id();
					for (Map.Entry<String, User_infoBean> entry : HashMap.entrySet()) {
						String key = entry.getKey(); // 获取键
//	            User_infoBean value = entry.getValue(); // 获取值
						sql = sql
								+ "    OR teai.user_id='" + key + "'"
								+ "    OR teai.yaoqing_no='" + key + "'"
								+ "";
					}
				}
				sql = sql
						+ "";
			}

				sql = sql
						+ "    ) AS subquery "
					+ "GROUP BY"
					+ "    month"
					+ "    , user_id "
					+ "ORDER BY"
					+ "    month DESC"
					+ "    , user_id;"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			String monthOld = "";
			LinkedHashMap<String, String> LinkedHashMap = new LinkedHashMap<String, String>();
			LinkedHashMap<String, String> LinkedHashMapNew = new LinkedHashMap<String, String>();
			int Total = 0;
			while (resultSet.next()) {
				String monthNew = resultSet.getString("month");

				if(!StringUtils.isEmpty(monthOld) && !monthOld.equals(monthNew)) {
					LinkedHashMapNew.put("合计", ""+Total);
					LinkedHashMapNew.putAll(LinkedHashMap);
					LinkedHashMapTongji.put(monthOld, LinkedHashMapNew);
					LinkedHashMap = new LinkedHashMap<String, String>();
					LinkedHashMapNew = new LinkedHashMap<String, String>();
					Total = 0;
				}
				int count =resultSet.getInt("operation_count");
				LinkedHashMap.put(resultSet.getString("user_id"), ""+count);
				Total = Total+ count;
				monthOld = monthNew;

			}
			LinkedHashMapNew.put("合计", ""+Total);
			LinkedHashMapNew.putAll(LinkedHashMap);
			LinkedHashMapTongji.put(monthOld, LinkedHashMapNew);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMapTongji;
	}


	public t_etax_account_infoExBean SelectExKeyValue(String key, String value) {

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

		try {

			String sql = ""
					+ ""
//					+ "SELECT"
//					+ "    * "
//					+ "from"
//					+ "    t_etax_account_info "
//					+ "where"
//					+ "    yyyymmdd_count = ( "
//					+ "        SELECT"
//					+ "            yyyymmdd_count "
//					+ "        from"
//					+ "            t_etax_account_res "
//					+ "        where " + key + "=?"
//					+ ")"
					+ ""
					+ ""
					+ "SELECT"
					+ "    teai.*"
					+ "    , tear.bangou "
					+ "    , tear.InvoiceBangou "
					+ "    , tear.PDSK "
					+ " from"
					+ "    t_etax_account_info teai "
					+ "    LEFT JOIN t_etax_account_res tear "
					+ "        ON teai.yyyymmdd_count = tear.yyyymmdd_count "
					+ " where"
					+ "    " + key + "=?"
					+ "   AND (CompanyName_Chinese NOT LIKE '%（删除20%'"
					+ "     AND CompanyName_English NOT LIKE '%（删除20%'"
					+ "     AND DaibiaoName_Chinese NOT LIKE '%（删除20%'"
					+ "     AND DaibiaoName_English NOT LIKE '%（删除20%'"
					+ "     )"
					+ " ORDER BY teai.UPDATE_DATE DESC"
					+ " LIMIT 1"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			preparedStatement.setString(1, value);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));

				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setDaibiaoName_address_Chinese(resultSet.getString("DaibiaoName_address_Chinese"));
				t_etax_account_infoExBean.setDaibiaoName_address_English(resultSet.getString("DaibiaoName_address_English"));

				t_etax_account_infoExBean.setCompanyName_pianjiaming(resultSet.getString("CompanyName_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(resultSet.getString("DaibiaoName_pianjiaming"));
				t_etax_account_infoExBean.setAddress_pianjiaming(resultSet.getString("address_pianjiaming"));
				t_etax_account_infoExBean.setDaibiaoName_address_pianjiaming(resultSet.getString("DaibiaoName_address_pianjiaming"));

				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean
						.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean
						.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));

				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setPDSK(resultSet.getString("PDSK"));



				t_etax_account_infoExBean.setTatujin_id(resultSet.getString("tatujin_id"));
				t_etax_account_infoExBean.setUser_type_guanfang(resultSet.getString("user_type_guanfang"));
				t_etax_account_infoExBean.setUser_type_zixuan(resultSet.getString("user_type_zixuan"));

				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));


			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return t_etax_account_infoExBean;
	}



	public LinkedHashMap<String, t_etax_account_infoExBean> SelectExKeyValue(String key, String tiaojian, String value) {

		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<String, t_etax_account_infoExBean>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {

			String sql = ""
					+ ""
					+ "SELECT"
					+ "    teai.*"
					+ " from"
					+ "    t_etax_account_info teai "
					+ " where"
					+ "    " + key + tiaojian + value
					+ " ORDER BY teai.UPDATE_DATE DESC"
					+ ";"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_etax_account_infoExBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));
				t_etax_account_infoExBean.setUser_id(resultSet.getString("user_id"));
				t_etax_account_infoExBean.setSyouninn_status(resultSet.getString("syouninn_status"));
				t_etax_account_infoExBean.setDaibiaoName_English(resultSet.getString("DaibiaoName_English"));
				t_etax_account_infoExBean.setCompany_DD(resultSet.getString("company_DD"));
				t_etax_account_infoExBean.setCompany_MM(resultSet.getString("company_MM"));
				t_etax_account_infoExBean.setCompany_YYYY(resultSet.getString("company_YYYY"));
				t_etax_account_infoExBean.setTel_1(resultSet.getString("tel_1"));
				t_etax_account_infoExBean.setTel_2(resultSet.getString("tel_2"));
				t_etax_account_infoExBean.setTel_3(resultSet.getString("tel_3"));
				t_etax_account_infoExBean.setTel_country(resultSet.getString("tel_country"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(resultSet.getString("xiaoshouerYYYY_1"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(resultSet.getString("xiaoshouerYYYY_1_half"));
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(resultSet.getString("xiaoshouerYYYY_2"));
				t_etax_account_infoExBean.setZhice_ziben(resultSet.getString("zhice_ziben"));
				t_etax_account_infoExBean.setAddress_Chinese(resultSet.getString("address_Chinese"));
				t_etax_account_infoExBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_etax_account_infoExBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_etax_account_infoExBean.setDaibiaoName_Chinese(resultSet.getString("DaibiaoName_Chinese"));
				t_etax_account_infoExBean.setGeren_dianpu_address(resultSet.getString("geren_dianpu_address"));
				t_etax_account_infoExBean.setGeren_dianpu_name(resultSet.getString("geren_dianpu_name"));
				t_etax_account_infoExBean.setChangshe_jigou_Select(resultSet.getString("changshe_jigou_Select"));
				t_etax_account_infoExBean.setJianyi_keshui_Select(resultSet.getString("jianyi_keshui_Select"));
				t_etax_account_infoExBean.setAddress_English(resultSet.getString("address_English"));
				t_etax_account_infoExBean.setJianyi_keshui_type(resultSet.getString("jianyi_keshui_type"));
				t_etax_account_infoExBean
						.setTokutei_kikann_siharai_kyuuyo(resultSet.getString("tokutei_kikann_siharai_kyuuyo"));
				t_etax_account_infoExBean
						.setShouri_kaishi_denglu_xiayige(resultSet.getString("shouri_kaishi_denglu_xiayige"));
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(resultSet.getString("shouri_kaishi_denglu_ben"));


				t_etax_account_infoExBean.setEtax_pw(resultSet.getString("etax_pw"));


				t_etax_account_infoExBean.setDigital_certificate(resultSet.getString("digital_certificate"));

				LinkedHashMap_t_etax_account_infoExBean.put(t_etax_account_infoExBean.getYyyymmdd_count(), t_etax_account_infoExBean);

			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(null, preparedStatement, connection);
		}
		return LinkedHashMap_t_etax_account_infoExBean;
	}

	public int DELETE(User_infoBean user_infoBean, String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {
			String sql = ""
					+ "DELETE FROM t_etax_account_info"
					+ " where yyyymmdd_count=?"
					+ "";

			if (!"admin".equals(user_infoBean.getPermissions()) && !"groupAdmin".equals(user_infoBean.getPermissions())) {
				sql = sql +""
						+ " and user_id=?"
						+ "";
			}
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
			preparedStatement.setString(++i, yyyymmdd_count);
			if (!"admin".equals(user_infoBean.getPermissions()) && !"groupAdmin".equals(user_infoBean.getPermissions())) {
				preparedStatement.setString(++i, user_infoBean.getUser_id());
			}

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


	public int DELETE(String yyyymmdd_count) {

		PreparedStatement preparedStatement = null;

		try {
			String sql = ""
					+ "DELETE FROM t_etax_account_info"
					+ " where yyyymmdd_count=?"
					+ "";

			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);
			int i = 0;
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
}
