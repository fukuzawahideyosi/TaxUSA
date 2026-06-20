package com.panda.dao;

import java.sql.Blob;
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
import com.panda.bean.t_user_account_amountBean;
import com.panda.utils.FuncUtils;
import com.panda.utils.JdbcUtils;

/**
 * Created by ForMe
 * com.demo
 * 2018/12/1
 * 15:59
 */
public class t_user_account_amountDao extends ConnectionDao {

	private static Logger logger = Logger.getLogger(t_user_account_amountDao.class.toString());

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
								+ "<br>【納付金額】<br>" + t_etax_account_infoExBean.getNoufu_kingaku()
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


	public LinkedHashMap<String, t_etax_account_infoExBean> selectAll_by_freee(User_infoBean user_infoBean, String maxNo) {

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
					+ "     , tear.bangou"
					+ "     , tear.HoujinBangou"
					+ "     , tear.InvoiceBangou"
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


				t_etax_account_infoExBean.setNashui_guanliren(resultSet.getString("nashui_guanliren"));
				t_etax_account_infoExBean.setNashui_CompanyName(resultSet.getString("nashui_CompanyName"));
				t_etax_account_infoExBean.setNashui_DaibiaoName(resultSet.getString("nashui_DaibiaoName"));
				t_etax_account_infoExBean.setNashui_address(resultSet.getString("nashui_address"));
				t_etax_account_infoExBean.setNashui_di_biangengqian(resultSet.getString("nashui_di_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(resultSet.getString("nashui_shuiwushu_biangengqian"));
				t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(resultSet.getString("nashui_shuiwushu_fanhao"));

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


	public LinkedHashMap<String, ArrayList<t_user_account_amountBean>> selectAll() {
		LinkedHashMap<String, ArrayList<t_user_account_amountBean>> LinkedHashMap_t_user_account_amountBeanArrayList = new LinkedHashMap<String, ArrayList<t_user_account_amountBean>>();

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try {

			String sql = ""
					+ "\r\n"
					+ "SELECT\r\n"
					+ "    tuaa.* \r\n"
					+ "FROM\r\n"
					+ "    t_user_account_amount tuaa \r\n"
					+ "    LEFT JOIN t_etax_account_info teai \r\n"
					+ "        on teai.yyyymmdd_count = tuaa.yyyymmdd_count \r\n"
					+ "ORDER BY\r\n"
					+ "    teai.UPDATE_DATE desc\r\n"
					+ ""
					+ "";

			int i = 0;
			preparedStatement = connection.prepareStatement(sql);

			logger.debug(preparedStatement.toString());
			resultSet = preparedStatement.executeQuery();
/*
CREATE TABLE `t_user_account_amount` (
  `UPDATE_DATE` timestamp(6) NOT NULL COMMENT '更新时间',
  `yyyymmdd_count` bigint NOT NULL COMMENT '日期计数',
  `yyyy` varchar(45) NOT NULL COMMENT '年份',
  `CompanyName_Chinese` varchar(256) DEFAULT NULL COMMENT '公司中文名称',
  `CompanyName_English` varchar(256) DEFAULT NULL COMMENT '公司英文名称',
  `bangou` varchar(16) DEFAULT NULL COMMENT 'etax 番号',
  `InvoiceBangou` varchar(45) DEFAULT NULL COMMENT '消费税税号',
  `amount` decimal(18,0) NOT NULL DEFAULT '0' COMMENT '交易金额',
  `zhifu_pingzheng` varchar(125) DEFAULT NULL COMMENT '支付凭证',
  `huikuan_pingzheng` varchar(125) DEFAULT NULL COMMENT '汇款凭证'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户 账户资金表'
 */
			while (resultSet.next()) {
				t_user_account_amountBean t_user_account_amountBean = new t_user_account_amountBean();
				t_user_account_amountBean.setUPDATE_DATE(resultSet.getString("UPDATE_DATE"));
				t_user_account_amountBean.setYyyymmdd_count(resultSet.getString("yyyymmdd_count"));

				t_user_account_amountBean.setYyyy(resultSet.getString("yyyy"));
				t_user_account_amountBean.setCompanyName_Chinese(resultSet.getString("CompanyName_Chinese"));
				t_user_account_amountBean.setCompanyName_English(resultSet.getString("CompanyName_English"));
				t_user_account_amountBean.setBangou(resultSet.getString("bangou"));
				t_user_account_amountBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_user_account_amountBean.setAmount(resultSet.getString("amount"));
				t_user_account_amountBean.setZhifu_pingzheng(resultSet.getString("zhifu_pingzheng"));
				t_user_account_amountBean.setHuikuan_pingzheng(resultSet.getString("huikuan_pingzheng"));

//				String.format("%,d", t_user_account_amountBean.getAmount());

				ArrayList<t_user_account_amountBean>  t_user_account_amountBeanArrayList = new ArrayList<>();
				if (LinkedHashMap_t_user_account_amountBeanArrayList.containsKey(t_user_account_amountBean.getYyyymmdd_count())) {
					t_user_account_amountBeanArrayList = LinkedHashMap_t_user_account_amountBeanArrayList.get(t_user_account_amountBean.getYyyymmdd_count());
				}
				t_user_account_amountBeanArrayList.add(t_user_account_amountBean);
				LinkedHashMap_t_user_account_amountBeanArrayList.put(t_user_account_amountBean.getYyyymmdd_count(), t_user_account_amountBeanArrayList);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JdbcUtils.close(resultSet, preparedStatement, connection);
		}
		return LinkedHashMap_t_user_account_amountBeanArrayList;
	}



	public int UPDATE_blob(String key, String yyyymmdd_count, String yyyy, String amount, String html) {

		PreparedStatement preparedStatement = null;

        Blob blob = null;
		try {

			String sql = ""
					+ "UPDATE t_user_account_amount SET "//UPDATE_DATE = now(3)
					+ "";

			if ("huikuan_pingzheng".equals(key)) {

				sql = sql
						+ " huikuan_pingzheng = ?"
						+ "";

			} else if ("zhifu_pingzheng".equals(key)) {
				sql = sql
						+ " zhifu_pingzheng = ?"
						+ "";

			} else {
				return -1;
			}

			sql = sql
					+ " where yyyymmdd_count = ? and yyyy = ? and amount = ?"
					+ "";
			preparedStatement = (PreparedStatement) connection.prepareStatement(sql);

			int i = 0;
			blob = connection.createBlob();
			blob.setBytes(++i, html.getBytes());
			preparedStatement.setBlob(i, blob);
			logger.debug("blob : " + blob);

			preparedStatement.setString(++i, yyyymmdd_count);
			preparedStatement.setString(++i, yyyy);
			preparedStatement.setString(++i, amount);

			logger.debug(preparedStatement.toString());
			int count = preparedStatement.executeUpdate();
			logger.debug("SQL " + count);


			return count;

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

		return -1;
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



	public int INSERT(t_user_account_amountBean bean) throws SQLException {
		PreparedStatement preparedStatement = null;
		try {
		    String sql = "INSERT INTO t_user_account_amount ("
	                   + "UPDATE_DATE, yyyymmdd_count, yyyy, CompanyName_Chinese, CompanyName_English, "
	                   + "bangou, InvoiceBangou, amount, zhifu_pingzheng, huikuan_pingzheng) "
	                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			preparedStatement = connection.prepareStatement(sql);
			int i = 0;
            preparedStatement.setTimestamp(++i, new Timestamp(System.currentTimeMillis())); // UPDATE_DATE
            preparedStatement.setString(++i, bean.getYyyymmdd_count()); // yyyymmdd_count
            preparedStatement.setString(++i, bean.getYyyy()); // yyyy
            preparedStatement.setString(++i, bean.getCompanyName_Chinese()); // CompanyName_Chinese
            preparedStatement.setString(++i, bean.getCompanyName_English()); // CompanyName_English
            preparedStatement.setString(++i, bean.getBangou()); // bangou
            preparedStatement.setString(++i, bean.getInvoiceBangou()); // InvoiceBangou
            preparedStatement.setString(++i, bean.getAmount()); // amount
            preparedStatement.setString(++i, bean.getZhifu_pingzheng()); // zhifu_pingzheng
            preparedStatement.setString(++i, bean.getHuikuan_pingzheng()); // huikuan_pingzheng

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

				t_etax_account_infoExBean.setBangou(resultSet.getString("bangou"));
				t_etax_account_infoExBean.setInvoiceBangou(resultSet.getString("InvoiceBangou"));
				t_etax_account_infoExBean.setPDSK(resultSet.getString("PDSK"));


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

}
