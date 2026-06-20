package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsURL;

@WebServlet("/ApiLogic")
@MultipartConfig
public class ApiLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(ApiLogic.class.toString());

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

		String user_id = req.getParameter("license");
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();

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
		String pw = req.getParameter("pw");
		session.setAttribute("pw", pw);
		if (StringUtils.isEmpty(User_infoBean.getUser_id()) && !StringUtils.isEmpty(user_id)) {
			User_infoBean = LicenseDao.select(user_id);
		}
		session.setAttribute("User_infoBean", User_infoBean);

		String license = User_infoBean.getLicense_yyyymmdd();
		logger.info(license);
		if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == false) {
			logger.debug("PandaServiceTools → License invalid");

			out.write("PandaServiceMA → License invalid");
			return;
		}

		/*
		 * 登录功能 附件
		 */
		String hidden_key = req.getParameter("hidden_key");
		String hidden_value = req.getParameter("hidden_value");
		if ("auto_faren_fanhao".equals(hidden_key)) {
			/*
			 * 法人番号
			 */
			try {
				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.SelectExKeyValue("HoujinBangou", hidden_value);

				if (StringUtils.isEmpty(EtaxAccountInfoBean.getYyyymmdd_count())) {
					EtaxAccountInfoBean = FuncUtils.sendGetHoujinBangou(hidden_value);

				}

				// 将 EtaxAccountInfoBean 对象转换为 JSON 字符串
				Gson gson = new Gson();
				String json = gson.toJson(EtaxAccountInfoBean);

				//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
				out.print(json);

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			logger.info("end auto_faren_fanhao");
			return;

		} else if ("auto_xiaofeishui_shuihao".equals(hidden_key)) {
			/*
			 * 消费税税号
			 */
			try {
				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("HoujinBangou", hidden_value);

				if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count())) {
					t_etax_account_infoExBean = FuncUtils.sendGetHoujinBangou(hidden_value);

					if (StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_English())) {
						t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou("T" + hidden_value);

					}
				}

				String CompanyName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoExBean.getAddress_Chinese());
				String address_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoExBean.getAddress_Chinese());
				String DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoExBean.getDaibiaoName_Chinese());
				CompanyName_pianjiaming = FuncUtils.toFullWidthAndTruncate(CompanyName_pianjiaming, 25);
				address_pianjiaming = FuncUtils.toFullWidthAndTruncate(address_pianjiaming, 25);
				DaibiaoName_pianjiaming = FuncUtils.toFullWidthAndTruncate(DaibiaoName_pianjiaming, 25);
				t_etax_account_infoExBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
				t_etax_account_infoExBean.setAddress_pianjiaming(address_pianjiaming);
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);

				if (StringUtils.isEmpty(t_etax_account_infoExBean.getNashuidi())) {
					t_etax_account_infoExBean.setNashuidi_youbian1("112");
					t_etax_account_infoExBean.setNashuidi_youbian2("0011");
					t_etax_account_infoExBean.setKsaTodofuken("東京都");
					t_etax_account_infoExBean.setNashuidi("東京都文京区千石４丁目１４番９号１階");
					t_etax_account_infoExBean.setNashuidi_tel1("03");
					t_etax_account_infoExBean.setNashuidi_tel2("5981");
					t_etax_account_infoExBean.setNashuidi_tel3("8383");
					t_etax_account_infoExBean.setGuanxia_shuiwushu("小石川税務署");


				}

				// 将 EtaxAccountInfoBean 对象转换为 JSON 字符串
				Gson gson = new Gson();
				String json = gson.toJson(t_etax_account_infoExBean);

				//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
				out.print(json);

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

			logger.info("end auto_xiaofeishui_shuihao");
			return;


		} else if ("Get_etax_pw".equals(hidden_key)) {

			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("bangou", hidden_value);

			out.print("{\"res\":\"" + t_etax_account_infoExBean.getEtax_pw() + "\"}");
			logger.info("end Get_etax_pw");
			return;


		} else if ("auto_guanxia_shuiwushu".equals(hidden_key)) {
			/*
			 * 管辖税务署
			 */
			String[] hidden_value_list = hidden_value.split(",");

	        String KSTYPE = "";
	        String TODOFUKEN_TO_ASCII = hidden_value_list[2];
	        String ADDR_TO_ASCII = hidden_value_list[3];
	        ADDR_TO_ASCII = ADDR_TO_ASCII.replaceAll(TODOFUKEN_TO_ASCII, "");
	        String kszc1 = hidden_value_list[0];
	        String kszc2 = hidden_value_list[1];
	        String guanxia_shuiwushu = "";


	        if (!StringUtils.isEmpty(kszc1) && !StringUtils.isEmpty(kszc2)) {
		         KSTYPE = "ksz";
//		         kszc1 = "107";
//		         kszc2 = "0052";
		         guanxia_shuiwushu = FuncUtilsURL.getGuanxia_shuiwushu(KSTYPE,TODOFUKEN_TO_ASCII,ADDR_TO_ASCII, kszc1, kszc2);
		        logger.debug("guanxia_shuiwushu: " + guanxia_shuiwushu);
	        }
	        if (StringUtils.isEmpty(guanxia_shuiwushu)) {
		         KSTYPE = "ksa";
//		         TODOFUKEN_TO_ASCII = "東京都";
//		         ADDR_TO_ASCII = "港区";
		         guanxia_shuiwushu = FuncUtilsURL.getGuanxia_shuiwushu(KSTYPE,TODOFUKEN_TO_ASCII,ADDR_TO_ASCII, kszc1, kszc2);
			        logger.debug("guanxia_shuiwushu: " + guanxia_shuiwushu);

	        } else {

	        }

			out.print("{\"guanxia_shuiwushu\":\"" + guanxia_shuiwushu + "\"}");
//			out.print(labelValue);

			logger.info("end auto_guanxia_shuiwushu");
			return;
		}


		logger.info("end");

		return;

	}



}