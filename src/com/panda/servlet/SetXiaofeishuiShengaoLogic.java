package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_xiaofeishuiBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_xiaofeishuiDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsRiyu;

@WebServlet("/SetXiaofeishuiShengaoLogic")
@MultipartConfig
public class SetXiaofeishuiShengaoLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SetXiaofeishuiShengaoLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		FuncUtils FunctionUtils = new FuncUtils();

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String auto_xiaofeishui_shuihao = req.getParameter("auto_xiaofeishui_shuihao");
		String auto_faren_fanhao = req.getParameter("auto_faren_fanhao");
		String auto_guanxia_shuiwushu = req.getParameter("auto_guanxia_shuiwushu");


		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileData");


		/*
		 * 邀请码有效性验证
		 */
		String yaoqing_no = req.getParameter("yaoqing_no");
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();
		if (StringUtils.isEmpty(yaoqing_no)) {

		} else {
			User_infoBean = LicenseDao.selectByTiaojian("yaoqing_no", yaoqing_no);
			if (StringUtils.isEmpty(User_infoBean.getYaoqing_no())) {
				out.print("邀请码【"+yaoqing_no+"】无效，请联系客服！给您造成的不便，深感抱歉。");
				return;

			} else {
				session.setAttribute("yaoqing_no", yaoqing_no);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yaoqing_no, " + yaoqing_no);
				logger.debug("yaoqing_no -> ok");

			}
		}



		/*
		 * license確認
		 */
		// 不要

		/*
		 * 登录功能 附件
		 */
		String hidden_key = req.getParameter("hidden_key");
		if (StringUtils.isEmpty(yyyymmdd_count) && "Files".equals(hidden_key)) {


			try {

				yyyymmdd_count = FuncUtils.filesUp(req, path);

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (ServletException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (Throwable e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


			//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
			out.print(yyyymmdd_count);
			return;


		}

		/*
		 * 登录功能 客户数据
		 */
		try {

			String DaibiaoName_English = req.getParameter("DaibiaoName_English");
			String company_DD = req.getParameter("company_DD");
			String company_MM = req.getParameter("company_MM");
			String company_YYYY = req.getParameter("company_YYYY");
			String tel_1 = req.getParameter("tel_1");
			String tel_2 = req.getParameter("tel_2");
			String tel_3 = req.getParameter("tel_3");
			String tel_country = req.getParameter("tel_country");
			String xiaoshouerYYYY_1 = req.getParameter("xiaoshouerYYYY_1");
			String xiaoshouerYYYY_1_half = req.getParameter("xiaoshouerYYYY_1_half");
			String xiaoshouerYYYY_2 = req.getParameter("xiaoshouerYYYY_2");
			String zhice_ziben = req.getParameter("zhice_ziben");
			String address_Chinese = req.getParameter("address_Chinese");
			String CompanyName_Chinese = req.getParameter("CompanyName_Chinese");
			String CompanyName_English = req.getParameter("CompanyName_English");
			String DaibiaoName_Chinese = req.getParameter("DaibiaoName_Chinese");
			String geren_dianpu_address = req.getParameter("geren_dianpu_address");
			String geren_dianpu_name = req.getParameter("geren_dianpu_name");
			String changshe_jigou_Select = req.getParameter("changshe_jigou_Select");
			String jianyi_keshui_Select = req.getParameter("jianyi_keshui_Select");
			String address_English = req.getParameter("address_English");
			String jianyi_keshui_type = req.getParameter("jianyi_keshui_type");
			String tokutei_kikann_siharai_kyuuyo = req.getParameter("tokutei_kikann_siharai_kyuuyo");
			String shouri_kaishi_denglu_xiayige = req.getParameter("shouri_kaishi_denglu_xiayige");
			String shouri_kaishi_denglu_ben = req.getParameter("shouri_kaishi_denglu_ben");
//			user_id = req.getParameter("user_id");
			String etax_no = req.getParameter("etax_no");
			yaoqing_no = req.getParameter("yaoqing_no");


			String user_type = req.getParameter("user_type");
			String xiaofeishui_shuihao = req.getParameter("xiaofeishui_shuihao");
			String CompanyName_pianjiaming = req.getParameter("CompanyName_pianjiaming");
			String address_pianjiaming = req.getParameter("address_pianjiaming");
			String DaibiaoName_pianjiaming = req.getParameter("DaibiaoName_pianjiaming");
			String nashuidi_youbian1 = req.getParameter("nashuidi_youbian1");
			String nashuidi_youbian2 = req.getParameter("nashuidi_youbian2");
			String nashuidi = req.getParameter("nashuidi");
			String ksaTodofuken = req.getParameter("ksaTodofuken");
			String nashuidi_pianjiaming = req.getParameter("nashuidi_pianjiaming");
			String nashuidi_tel1 = req.getParameter("nashuidi_tel1");
			String nashuidi_tel2 = req.getParameter("nashuidi_tel2");
			String nashuidi_tel3 = req.getParameter("nashuidi_tel3");
			String guanxia_shuiwushu = req.getParameter("guanxia_shuiwushu");
			String liyongzhe_shibie_fanhao = req.getParameter("liyongzhe_shibie_fanhao");
			String shengao_shiqishou_YYYYMMDD = req.getParameter("shengao_shiqishou_YYYYMMDD");
			String shengao_shiqimo_YYYYMMDD = req.getParameter("shengao_shiqimo_YYYYMMDD");
			String yuanze_or_jianyi = req.getParameter("yuanze_or_jianyi");


			String keshui_maishang_2_xiaomai = req.getParameter("keshui_maishang_2_xiaomai");
			String zhongjian_nafu_shuie = req.getParameter("zhongjian_nafu_shuie");
			String zhongjian_nafu_durang = req.getParameter("zhongjian_nafu_durang");

			String xiaoshoue_10 = req.getParameter("xiaoshoue_10");
			String xiaoshoue_8 = req.getParameter("xiaoshoue_8");
			String fapiao_10 = req.getParameter("fapiao_10");
			String fapiao_8 = req.getParameter("fapiao_8");
			String fapiao_10_20231001 = req.getParameter("fapiao_10_20231001");
			String fapiao_10_20261001 = req.getParameter("fapiao_10_20261001");
			String fapiao_8_20231001 = req.getParameter("fapiao_8_20231001");
			String fapiao_8_20261001 = req.getParameter("fapiao_8_20261001");


			String jinkou_xiaofeishui_guoshui = req.getParameter("jinkou_xiaofeishui_guoshui");
			String jinkou_xiaofeishui_dishui = req.getParameter("jinkou_xiaofeishui_dishui");

			//去掉字符串里的TAB，首尾半角空格，首尾全角空格
			CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
			CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
			address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
			address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
			DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
			DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);


			CompanyName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(CompanyName_pianjiaming);
			address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(address_pianjiaming);
			DaibiaoName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_pianjiaming);
			nashuidi_youbian1 = FuncUtils.trimWhitespaceAndTabs(nashuidi_youbian1);
			nashuidi_youbian2 = FuncUtils.trimWhitespaceAndTabs(nashuidi_youbian2);
			nashuidi = FuncUtils.trimWhitespaceAndTabs(nashuidi);
			nashuidi_pianjiaming = FuncUtilsRiyu.changeRiyuToPianjiaming(nashuidi);
			nashuidi_tel1 = FuncUtils.trimWhitespaceAndTabs(nashuidi_tel1);
			nashuidi_tel2 = FuncUtils.trimWhitespaceAndTabs(nashuidi_tel2);
			nashuidi_tel3 = FuncUtils.trimWhitespaceAndTabs(nashuidi_tel3);
			guanxia_shuiwushu = FuncUtils.trimWhitespaceAndTabs(guanxia_shuiwushu);
			liyongzhe_shibie_fanhao = FuncUtils.trimWhitespaceAndTabs(liyongzhe_shibie_fanhao);
			shengao_shiqishou_YYYYMMDD = FuncUtils.trimWhitespaceAndTabs(shengao_shiqishou_YYYYMMDD);
			shengao_shiqimo_YYYYMMDD = FuncUtils.trimWhitespaceAndTabs(shengao_shiqimo_YYYYMMDD);
			yuanze_or_jianyi = FuncUtils.trimWhitespaceAndTabs(yuanze_or_jianyi);


			String xiaoshouerYYYY_2_title = req.getParameter("xiaoshouerYYYY_2_title");
			String xiaoshouerYYYY_1_half_title = req.getParameter("xiaoshouerYYYY_1_half_title");
			String tokutei_kikann_siharai_kyuuyo_title = req.getParameter("tokutei_kikann_siharai_kyuuyo_title");
			String xiaoshouerYYYY_1_YYYYMMDD_title = req.getParameter("xiaoshouerYYYY_1_YYYYMMDD_title");
			String shuoming = req.getParameter("shuoming");


			String history = req.getParameter("history");


			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_DD, " + company_DD);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_MM, " + company_MM);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_YYYY, " + company_YYYY);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_1, " + tel_1);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_2, " + tel_2);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_3, " + tel_3);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_country, " + tel_country);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhice_ziben, " + zhice_ziben);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", geren_dianpu_address, " + geren_dianpu_address);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", geren_dianpu_name, " + geren_dianpu_name);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_2, " + xiaoshouerYYYY_2);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_half, " + xiaoshouerYYYY_1_half);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tokutei_kikann_siharai_kyuuyo, " + tokutei_kikann_siharai_kyuuyo);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1, " + xiaoshouerYYYY_1);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", changshe_jigou_Select, " + changshe_jigou_Select);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", jianyi_keshui_Select, " + jianyi_keshui_Select);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", jianyi_keshui_type, " + jianyi_keshui_type);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shouri_kaishi_denglu_xiayige, " + shouri_kaishi_denglu_xiayige);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shouri_kaishi_denglu_ben, " + shouri_kaishi_denglu_ben);


			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", etax_no, " + etax_no);


			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_2_title" + xiaoshouerYYYY_2_title);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_half_title" + xiaoshouerYYYY_1_half_title);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tokutei_kikann_siharai_kyuuyo_title" + tokutei_kikann_siharai_kyuuyo_title);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_YYYYMMDD_title" + xiaoshouerYYYY_1_YYYYMMDD_title);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shuoming, " + shuoming);

			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", history, " + history);

			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type, " + user_type);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaofeishui_shuihao, " + xiaofeishui_shuihao);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_pianjiaming, " + address_pianjiaming);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_youbian1, " + nashuidi_youbian1);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_youbian2, " + nashuidi_youbian2);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", ksaTodofuken, " + ksaTodofuken);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi, " + nashuidi);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_pianjiaming, " + nashuidi_pianjiaming);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_tel1, " + nashuidi_tel1);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_tel2, " + nashuidi_tel2);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", nashuidi_tel3, " + nashuidi_tel3);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", guanxia_shuiwushu, " + guanxia_shuiwushu);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", liyongzhe_shibie_fanhao, " + liyongzhe_shibie_fanhao);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shengao_shiqishou_YYYYMMDD, " + shengao_shiqishou_YYYYMMDD);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shengao_shiqimo_YYYYMMDD, " + shengao_shiqimo_YYYYMMDD);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yuanze_or_jianyi, " + yuanze_or_jianyi);


			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshui_maishang_2_xiaomai, " + keshui_maishang_2_xiaomai);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhongjian_nafu_shuie, " + zhongjian_nafu_shuie);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhongjian_nafu_durang, " + zhongjian_nafu_durang);


			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , xiaoshoue_10" + xiaoshoue_10);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , xiaoshoue_8" + xiaoshoue_8);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_10" + fapiao_10);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_8" + fapiao_8);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_10_20231001" + fapiao_10_20231001);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_10_20261001" + fapiao_10_20261001);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_8_20231001" + fapiao_8_20231001);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , fapiao_8_20261001" + fapiao_8_20261001);

			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , jinkou_xiaofeishui_guoshui" + jinkou_xiaofeishui_guoshui);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", , jinkou_xiaofeishui_dishui" + jinkou_xiaofeishui_dishui);



			if (history == null) {
				history = "";
			}
//			history = "YES";
			if ("YES".equals(history.toUpperCase())) {
				session.setAttribute("DaibiaoName_English", DaibiaoName_English);
				session.setAttribute("company_DD", company_DD);
				session.setAttribute("company_MM", company_MM);
				session.setAttribute("company_YYYY", company_YYYY);
				session.setAttribute("tel_1", tel_1);
				session.setAttribute("tel_2", tel_2);
				session.setAttribute("tel_3", tel_3);
				session.setAttribute("tel_country", tel_country);
				session.setAttribute("xiaoshouerYYYY_1", xiaoshouerYYYY_1);
				session.setAttribute("xiaoshouerYYYY_1_half", xiaoshouerYYYY_1_half);
				session.setAttribute("xiaoshouerYYYY_2", xiaoshouerYYYY_2);
				session.setAttribute("zhice_ziben", zhice_ziben);
				session.setAttribute("address_Chinese", address_Chinese);
				session.setAttribute("CompanyName_Chinese", CompanyName_Chinese);
				session.setAttribute("CompanyName_English", CompanyName_English);
				session.setAttribute("DaibiaoName_Chinese", DaibiaoName_Chinese);
				session.setAttribute("geren_dianpu_address", geren_dianpu_address);
				session.setAttribute("geren_dianpu_name", geren_dianpu_name);
				session.setAttribute("changshe_jigou_Select", changshe_jigou_Select);
				session.setAttribute("jianyi_keshui_Select", jianyi_keshui_Select);
				session.setAttribute("address_English", address_English);
				session.setAttribute("jianyi_keshui_type", jianyi_keshui_type);
				session.setAttribute("tokutei_kikann_siharai_kyuuyo", tokutei_kikann_siharai_kyuuyo);
				session.setAttribute("shouri_kaishi_denglu_xiayige", shouri_kaishi_denglu_xiayige);
				session.setAttribute("shouri_kaishi_denglu_ben", shouri_kaishi_denglu_ben);

				session.setAttribute("etax_no", etax_no);

				session.setAttribute("user_type", user_type);
				session.setAttribute("xiaofeishui_shuihao", xiaofeishui_shuihao);
				session.setAttribute("CompanyName_pianjiaming", CompanyName_pianjiaming);
				session.setAttribute("address_pianjiaming", address_pianjiaming);
				session.setAttribute("DaibiaoName_pianjiaming", DaibiaoName_pianjiaming);
				session.setAttribute("nashuidi_youbian1", nashuidi_youbian1);
				session.setAttribute("nashuidi_youbian2", nashuidi_youbian2);
				session.setAttribute("ksaTodofuken",ksaTodofuken);
				session.setAttribute("nashuidi", nashuidi);
				session.setAttribute("nashuidi_pianjiaming", nashuidi_pianjiaming);
				session.setAttribute("nashuidi_tel1", nashuidi_tel1);
				session.setAttribute("nashuidi_tel2", nashuidi_tel2);
				session.setAttribute("nashuidi_tel3", nashuidi_tel3);
				session.setAttribute("guanxia_shuiwushu", guanxia_shuiwushu);
				session.setAttribute("liyongzhe_shibie_fanhao", liyongzhe_shibie_fanhao);
				session.setAttribute("shengao_shiqishou_YYYYMMDD", shengao_shiqishou_YYYYMMDD);
				session.setAttribute("shengao_shiqimo_YYYYMMDD", shengao_shiqimo_YYYYMMDD);
				session.setAttribute("yuanze_or_jianyi", yuanze_or_jianyi);


				session.setAttribute("keshui_maishang_2_xiaomai", keshui_maishang_2_xiaomai);
				session.setAttribute("zhongjian_nafu_shuie", zhongjian_nafu_shuie);
				session.setAttribute("zhongjian_nafu_durang", zhongjian_nafu_durang);


				session.setAttribute("xiaoshoue_10", xiaoshoue_10);
				session.setAttribute("xiaoshoue_8", xiaoshoue_8);
				session.setAttribute("fapiao_10", fapiao_10);
				session.setAttribute("fapiao_8", fapiao_8);
				session.setAttribute("fapiao_10_20231001", fapiao_10_20231001);
				session.setAttribute("fapiao_10_20261001", fapiao_10_20261001);
				session.setAttribute("fapiao_8_20231001", fapiao_8_20231001);
				session.setAttribute("fapiao_8_20261001", fapiao_8_20261001);

				session.setAttribute("jinkou_xiaofeishui_guoshui", jinkou_xiaofeishui_guoshui);
				session.setAttribute("jinkou_xiaofeishui_dishui", jinkou_xiaofeishui_dishui);

//				// 获取所有属性的名称
//				Enumeration<String> attributeNames = session.getAttributeNames();
//	            // 创建类的实例
//				SetXiaofeishuiShengaoLogic instance = new SetXiaofeishuiShengaoLogic();
//
//
//				// 循环遍历属性名称
//				while (attributeNames.hasMoreElements()) {
//				    String attributeName = attributeNames.nextElement();
//				    Object attributeValue = session.getAttribute(attributeName);
//
//				    // 打印属性名称和值
//				    logger.info("Attribute Name: " + attributeName);
//				    logger.info("Attribute Value: " + attributeValue);
//
//		            Field field = instance.getClass().getDeclaredField(attributeName);
////		            String user_type1 = (String) field.get(null);  // 这里假设 user_type 是一个静态变量
//					session.setAttribute(attributeName, field.get(instance));
//
//				}


			} else {

//
//				// 获取所有属性的名称
//				Enumeration<String> attributeNames = session.getAttributeNames();
//
//				// 循环遍历属性名称
//				while (attributeNames.hasMoreElements()) {
//				    String attributeName = attributeNames.nextElement();
//				    Object attributeValue = session.getAttribute(attributeName);
//
//				    // 打印属性名称和值
//				    logger.info("Attribute Name: " + attributeName);
//				    logger.info("Attribute Value: " + attributeValue);
//
//					session.removeAttribute(attributeName);
//				}
//				  // 获取请求属性的名称枚举
//		        attributeNames = req.getAttributeNames();
//
//		        // 循环遍历请求属性名称
//		        while (attributeNames.hasMoreElements()) {
//		            String attributeName = attributeNames.nextElement();
//		            Object attributeValue = req.getAttribute(attributeName);
//
//		            // 输出属性名称和值
//		            logger.info("Attribute Name: " + attributeName);
//		            logger.info("Attribute Value: " + attributeValue);;
//					req.removeAttribute(attributeName);
//		        }


				session.removeAttribute("DaibiaoName_English");
				session.removeAttribute("company_DD");
				session.removeAttribute("company_MM");
				session.removeAttribute("company_YYYY");
				session.removeAttribute("tel_1");
				session.removeAttribute("tel_2");
				session.removeAttribute("tel_3");
				session.removeAttribute("tel_country");
				session.removeAttribute("xiaoshouerYYYY_1");
				session.removeAttribute("xiaoshouerYYYY_1_half");
				session.removeAttribute("xiaoshouerYYYY_2");
				session.removeAttribute("zhice_ziben");
				session.removeAttribute("address_Chinese");
				session.removeAttribute("CompanyName_Chinese");
				session.removeAttribute("CompanyName_English");
				session.removeAttribute("DaibiaoName_Chinese");
				session.removeAttribute("geren_dianpu_address");
				session.removeAttribute("geren_dianpu_name");
				session.removeAttribute("changshe_jigou_Select");
				session.removeAttribute("jianyi_keshui_Select");
				session.removeAttribute("address_English");
				session.removeAttribute("jianyi_keshui_type");
				session.removeAttribute("tokutei_kikann_siharai_kyuuyo");
				session.removeAttribute("shouri_kaishi_denglu_xiayige");
				session.removeAttribute("shouri_kaishi_denglu_ben");
				session.removeAttribute("etax_no");


				session.removeAttribute("user_type");
				session.removeAttribute("xiaofeishui_shuihao");
				session.removeAttribute("CompanyName_pianjiaming");
				session.removeAttribute("address_pianjiaming");
				session.removeAttribute("DaibiaoName_pianjiaming");
				session.removeAttribute("nashuidi_youbian1");
				session.removeAttribute("nashuidi_youbian2");
				session.removeAttribute("ksaTodofuken");
				session.removeAttribute("nashuidi");
				session.removeAttribute("nashuidi_pianjiaming");
				session.removeAttribute("nashuidi_tel1");
				session.removeAttribute("nashuidi_tel2");
				session.removeAttribute("nashuidi_tel3");
				session.removeAttribute("guanxia_shuiwushu");
				session.removeAttribute("liyongzhe_shibie_fanhao");
				session.removeAttribute("shengao_shiqishou_YYYYMMDD");
				session.removeAttribute("shengao_shiqimo_YYYYMMDD");
				session.removeAttribute("yuanze_or_jianyi");
				session.removeAttribute("myCheckbox1");
				session.removeAttribute("myCheckbox2");


				session.removeAttribute("keshui_maishang_2_xiaomai");
				session.removeAttribute("zhongjian_nafu_shuie");
				session.removeAttribute("zhongjian_nafu_durang");


				session.removeAttribute("xiaoshoue_10");
				session.removeAttribute("xiaoshoue_8");
				session.removeAttribute("fapiao_10");
				session.removeAttribute("fapiao_8");
				session.removeAttribute("fapiao_10_20231001");
				session.removeAttribute("fapiao_10_20261001");
				session.removeAttribute("fapiao_8_20231001");
				session.removeAttribute("fapiao_8_20261001");

				session.removeAttribute("jinkou_xiaofeishui_guoshui");
				session.removeAttribute("jinkou_xiaofeishui_dishui");

			}


			Map<String, String[]> HashMapParameterMap = req.getParameterMap();
			for (String key : HashMapParameterMap.keySet()) {
				if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
					continue;
				}

				if ("SetXiaofeishuiShengao".equals(key)) {
					try {

						t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
						t_etax_account_infoBean t_etax_account_infoBean = t_etax_account_infoDao.select(yyyymmdd_count);

						boolean sendMail_denglu_xiaofeishui = false;

						if (StringUtils.isEmpty(CompanyName_pianjiaming)) {
							CompanyName_pianjiaming = FuncUtils.fn_hanzi(CompanyName_Chinese);
						}
						if (StringUtils.isEmpty(address_pianjiaming)) {
							address_pianjiaming = FuncUtils.fn_hanzi(address_Chinese);
						}
						if (StringUtils.isEmpty(DaibiaoName_pianjiaming)) {
							DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(DaibiaoName_Chinese);
						}
						if (StringUtils.isEmpty(nashuidi_pianjiaming)) {
							nashuidi_pianjiaming = FuncUtils.fn_hanzi(nashuidi);
						}

						t_etax_account_infoBean = new t_etax_account_infoBean();
						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_account_infoBean.setUser_id(User_infoBean.getUser_id());
						t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
						t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
						t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
						t_etax_account_infoBean.setDaibiaoName_English(DaibiaoName_English);
						t_etax_account_infoBean.setCompany_DD(company_DD);
						t_etax_account_infoBean.setCompany_MM(company_MM);
						t_etax_account_infoBean.setCompany_YYYY(company_YYYY);
						t_etax_account_infoBean.setTel_1(tel_1);
						t_etax_account_infoBean.setTel_2(tel_2);
						t_etax_account_infoBean.setTel_3(tel_3);
						t_etax_account_infoBean.setTel_country(tel_country);
						t_etax_account_infoBean.setXiaoshouerYYYY_1(xiaoshouerYYYY_1);
						t_etax_account_infoBean.setXiaoshouerYYYY_1_half(xiaoshouerYYYY_1_half);
						t_etax_account_infoBean.setXiaoshouerYYYY_2(xiaoshouerYYYY_2);
						t_etax_account_infoBean.setZhice_ziben(zhice_ziben);
						t_etax_account_infoBean.setAddress_Chinese(address_Chinese);
						t_etax_account_infoBean.setGeren_dianpu_address(geren_dianpu_address);
						t_etax_account_infoBean.setGeren_dianpu_name(geren_dianpu_name);
						t_etax_account_infoBean.setChangshe_jigou_Select(changshe_jigou_Select);
						t_etax_account_infoBean.setJianyi_keshui_Select(jianyi_keshui_Select);
						t_etax_account_infoBean.setAddress_English(address_English);
						t_etax_account_infoBean.setJianyi_keshui_type(jianyi_keshui_type);
						t_etax_account_infoBean.setTokutei_kikann_siharai_kyuuyo(tokutei_kikann_siharai_kyuuyo);
						t_etax_account_infoBean.setShouri_kaishi_denglu_xiayige(shouri_kaishi_denglu_xiayige);
						t_etax_account_infoBean.setShouri_kaishi_denglu_ben(shouri_kaishi_denglu_ben);
						t_etax_account_infoBean.setEtax_no(etax_no);
						t_etax_account_infoBean.setYaoqing_no(yaoqing_no);


				        int digits = 8; // 需要生成的位数
				        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
				        t_etax_account_infoBean.setEtax_pw(etax_pw);

						t_etax_account_infoBean.setUser_type(user_type);
						t_etax_account_infoBean.setXiaofeishui_shuihao(xiaofeishui_shuihao);
						t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
						t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
						t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);
						t_etax_account_infoBean.setNashuidi_youbian1(nashuidi_youbian1);
						t_etax_account_infoBean.setNashuidi_youbian2(nashuidi_youbian2);
						t_etax_account_infoBean.setKsaTodofuken(ksaTodofuken);
						t_etax_account_infoBean.setNashuidi(nashuidi);
						t_etax_account_infoBean.setNashuidi_pianjiaming(nashuidi_pianjiaming);
						t_etax_account_infoBean.setNashuidi_tel1(nashuidi_tel1);
						t_etax_account_infoBean.setNashuidi_tel2(nashuidi_tel2);
						t_etax_account_infoBean.setNashuidi_tel3(nashuidi_tel3);
						t_etax_account_infoBean.setGuanxia_shuiwushu(guanxia_shuiwushu);
						t_etax_account_infoBean.setLiyongzhe_shibie_fanhao(liyongzhe_shibie_fanhao);

				        t_etax_account_infoBean.setEtax_pw_flag("0");

						if (StringUtils.isEmpty(t_etax_account_infoBean.getYyyymmdd_count())) {
							UUID uuid = UUID.randomUUID();
							t_etax_account_infoBean.setActivation_code(uuid.toString());
							//基本情报登录
							t_etax_account_infoDao.INSERT(t_etax_account_infoBean);

						} else {
							//消费税信息更新
							t_etax_account_infoDao.Update_xiaofeishui(t_etax_account_infoBean.getYyyymmdd_count(), t_etax_account_infoBean);

						}

						sendMail_denglu_xiaofeishui =  true;

						t_etax_account_xiaofeishuiDao t_etax_account_xiaofeishuiDao = new t_etax_account_xiaofeishuiDao();
						t_etax_account_xiaofeishuiBean t_etax_account_xiaofeishuiBean = t_etax_account_xiaofeishuiDao.select_where_yyyymmdd_count(yyyymmdd_count);
						if (StringUtils.isEmpty(t_etax_account_xiaofeishuiBean.getYyyymmdd_count())) {

						} else {
							t_etax_account_xiaofeishuiDao.delete_where_yyyymmdd_count(yyyymmdd_count);
						}
						t_etax_account_xiaofeishuiBean.setUPDATE_USER(User_infoBean.getUser_id());
						t_etax_account_xiaofeishuiBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_account_xiaofeishuiBean.setShengao_shiqishou_YYYYMMDD(shengao_shiqishou_YYYYMMDD);
						t_etax_account_xiaofeishuiBean.setShengao_shiqimo_YYYYMMDD(shengao_shiqimo_YYYYMMDD);
						t_etax_account_xiaofeishuiBean.setYuanze_or_jianyi(yuanze_or_jianyi);
						t_etax_account_xiaofeishuiBean.setKeshui_maishang_2_xiaomai(keshui_maishang_2_xiaomai);
						t_etax_account_xiaofeishuiBean.setZhongjian_nafu_shuie(zhongjian_nafu_shuie);
						t_etax_account_xiaofeishuiBean.setZhongjian_nafu_durang(zhongjian_nafu_durang);


						t_etax_account_xiaofeishuiBean.setXiaoshoue_10(xiaoshoue_10);
						t_etax_account_xiaofeishuiBean.setXiaoshoue_8(xiaoshoue_8);
						t_etax_account_xiaofeishuiBean.setFapiao_10(fapiao_10);
						t_etax_account_xiaofeishuiBean.setFapiao_8(fapiao_8);
						t_etax_account_xiaofeishuiBean.setFapiao_10_20231001(fapiao_10_20231001);
						t_etax_account_xiaofeishuiBean.setFapiao_10_20261001(fapiao_10_20261001);
						t_etax_account_xiaofeishuiBean.setFapiao_8_20231001(fapiao_8_20231001);
						t_etax_account_xiaofeishuiBean.setFapiao_8_20261001(fapiao_8_20261001);

						t_etax_account_xiaofeishuiBean.setJinkou_xiaofeishui_guoshui(jinkou_xiaofeishui_guoshui);
						t_etax_account_xiaofeishuiBean.setJinkou_xiaofeishui_dishui(jinkou_xiaofeishui_dishui);


						t_etax_account_xiaofeishuiDao.INSERT(t_etax_account_xiaofeishuiBean);

						if (sendMail_denglu_xiaofeishui) {
							t_etax_account_infoBean.setEmail(User_infoBean.getEmail());
							path = path + "/" + yyyymmdd_count + "_" + CompanyName_Chinese;
							//登录信息发邮件给客户
							FuncUtils.sendMail_denglu_xiaofeishui(t_etax_account_infoBean, t_etax_account_xiaofeishuiBean, path, xiaoshouerYYYY_2_title, xiaoshouerYYYY_1_half_title, tokutei_kikann_siharai_kyuuyo_title, xiaoshouerYYYY_1_YYYYMMDD_title, shuoming);

						} else {

						}


					} catch (Exception e) {
						// TODO 登录失败怎么办
						e.printStackTrace();
					}






//				} else if ("delete".equals(key)) {
//					out.print("{\"res\":\"ok\"}");
//					logger.info("end");
//					return;
//
//
//
//
//
//				} else if ("Syouninn".equals(key)) {
//
//					try {
//						String syouninn_status = HashMapParameterMap.get("Syouninn_status")[0];
//						//TODO
//						yyyymmdd_count = HashMapParameterMap.get(key)[0];
//
//						t_etax_account_infoDao.Update_syouninn_status(yyyymmdd_count, syouninn_status);
//
//						if ("承認有".equals(syouninn_status)) {
//							EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
//									t_etax_account_resDao, EtaxDao);
//
//						} else {
//							t_etax_account_resDao.DELETE_res(yyyymmdd_count);
//							EtaxDao.DELETE(yyyymmdd_count);
//
//						}
//					} catch (Exception e) {
//						// TODO 自動生成された catch ブロック
//						e.printStackTrace();
//					}
//
//					out.print("{\"res\":\"ok\"}");
//					logger.info("end");
//					return;


				}
			}



			//TODO 删除URL里边的key SetXiaofeishuiShengao 防止重复登录
			session.removeAttribute("SetXiaofeishuiShengao"					);

//			session.setAttribute("SetXiaofeishuiShengao", "OK");
			session.setAttribute("User_infoBean", new User_infoBean());
			req.getRequestDispatcher("/setXiaofeishuiShengao.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}




		logger.debug("end");

		return;

	}



}