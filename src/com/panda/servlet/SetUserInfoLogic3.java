package com.panda.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.batch.ExeDb;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_jct_shenqingBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_jct_shenqingDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;

@WebServlet("/SetUserInfoLogic3")
@MultipartConfig
public class SetUserInfoLogic3 extends HttpServlet {

	private static Logger logger = Logger.getLogger(SetUserInfoLogic3.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		String msg = "";
		String res = "";
		String yyyy = "2025";

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);

		String hidden_key = req.getParameter("hidden_key");
		String hidden_value = req.getParameter("hidden_value");
		String INSQ = req.getParameter("INSQ");

		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy");
		String tianxie_YYYY = SimpleDateFormat.format(new Date());

		/*
		 * 激活码处理
		 */



		/*
		 * 邀请码有效性验证
		 */
		String yaoqing_no = req.getParameter("yaoqing_no");
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();
		if (StringUtils.isEmpty(yaoqing_no)) {

		} else {

		}




		if (StringUtils.isEmpty(user_id) == true) {

		} else {

			/*
			 * license確認
			 */
			String pw = req.getParameter("pw");
			session.setAttribute("pw", pw);
//			PrintWriter out = resp.getWriter();
			FuncUtils FunctionUtils = new FuncUtils();
			User_infoBean = LicenseDao.select(user_id);
			if ("open_id".equals(user_id)) {
				session.setAttribute("User_infoBean", new User_infoBean());

			} else {
				session.setAttribute("User_infoBean", User_infoBean);

			}

			String license = User_infoBean.getLicense_yyyymmdd();
			logger.info("license YYYYMMDD" +  license);
			if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == false) {
				logger.debug("PandaServiceTools → License invalid");
				out.write("PandaServiceMA → License invalid");
				logger.info("end " + hidden_key);
				return;
			}
		}


//		if (StringUtils.isEmpty(yyyymmdd_count)) {
//			m_sequenceDao m_sequenceDao = new m_sequenceDao();
//			yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
//		}

		/*
		 * 登录功能 附件
		 */

		if (StringUtils.isEmpty(yyyymmdd_count) && "SetUserInfoLogic3".equals(hidden_key)) {

			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/fileData");

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
//			out.print(yyyymmdd_count);
//			logger.info("end " + hidden_key);
//			return;


		} else if ("button-search".equals(hidden_key)) {


			try {

				String key = req.getParameter("key");
				String value = req.getParameter("value");


//				t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou_json(value);
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

				if ("guanli_fanhao".toLowerCase().equals(key.toLowerCase())) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("teai.yyyymmdd_count", value);
				} else if ("tatujin_id".toLowerCase().equals(key.toLowerCase())) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("tatujin_id", value);
				} else if ("xiaofeishui_shuihao".toLowerCase().equals(key.toLowerCase())) {
					value = "T" + value;
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("InvoiceBangou", value);
				} else if ("ETAX_Bangou".toLowerCase().equals(key.toLowerCase())) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("Bangou", value);
				} else if ("CompanyName_Chinese".toLowerCase().equals(key.toLowerCase())) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("CompanyName_Chinese", value);
				} else if ("CompanyName_English".toLowerCase().equals(key.toLowerCase())) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("CompanyName_English", value);
				}










				//TEST公司
//				if ("T1234567890123".equals(value)) {
//					t_etax_account_infoExBean = new t_etax_account_infoExBean();
//					t_etax_account_infoExBean.setUser_type("2");
//					t_etax_account_infoExBean.setInvoiceBangou("T1234567890123");
//
//					t_etax_account_infoExBean.setCompanyName_English("TEST CompanyName_English");
//					t_etax_account_infoExBean.setCompanyName_Chinese("测试公司中文");
//					t_etax_account_infoExBean.setAddress_English("TEST Address_English");
//					t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
//					t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
//					t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");
//
//					t_etax_account_infoExBean.setCompany_YYYY("2000");
//					t_etax_account_infoExBean.setCompany_MM("01");
//					t_etax_account_infoExBean.setCompany_DD("02");
//
//				} else if ("T0987654321098".equals(value)) {
//					//TEST个人
//					t_etax_account_infoExBean = new t_etax_account_infoExBean();
//					t_etax_account_infoExBean.setUser_type("1");
//					t_etax_account_infoExBean.setInvoiceBangou("T0987654321098");
//					t_etax_account_infoExBean.setCompanyName_English("TEST geren_English");
//					t_etax_account_infoExBean.setCompanyName_Chinese("测试个人中文");
//					t_etax_account_infoExBean.setAddress_English("TEST Address_English");
//					t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
//					t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
//					t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");
//
//					t_etax_account_infoExBean.setCompany_YYYY("2001");
//					t_etax_account_infoExBean.setCompany_MM("11");
//					t_etax_account_infoExBean.setCompany_DD("22");
//				}


				// 将Java对象转换为JSON字符串
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(t_etax_account_infoExBean);


				msg = jsonString;
				session.setAttribute("msg", msg);out.print(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}



			logger.info("end " + hidden_key);
			return;






		} else if ("button-UserInfo".equals(hidden_key)) {

				String key = req.getParameter("key");
				String value = req.getParameter("value");

				/*
				 * 登录功能 客户数据
				 */
				try {




					String zhice_ziben = req.getParameter("zhice_ziben");
//					user_id = req.getParameter("user_id");
					yaoqing_no = req.getParameter("yaoqing_no");



					String xiaofeishui_shuihao = req.getParameter("xiaofeishui_shuihao");
					String etax_no = req.getParameter("etax_no");

					String user_type_guanfang = req.getParameter("user_type_guanfang");
					String user_type_zixuan = req.getParameter("user_type_zixuan");


					String CompanyName_Chinese = req.getParameter("CompanyName_Chinese");
					String CompanyName_English = req.getParameter("CompanyName_English");
					String CompanyName_pianjiaming = req.getParameter("CompanyName_pianjiaming");


					String address_Chinese = req.getParameter("address_Chinese");
					String address_English = req.getParameter("address_English");
					String address_pianjiaming = req.getParameter("address_pianjiaming");


					String DaibiaoName_Chinese = req.getParameter("DaibiaoName_Chinese");
					String DaibiaoName_English = req.getParameter("DaibiaoName_English");
					String DaibiaoName_pianjiaming = req.getParameter("DaibiaoName_pianjiaming");

					String DaibiaoName_address_Chinese = req.getParameter("DaibiaoName_address_Chinese");
					String DaibiaoName_address_English = req.getParameter("DaibiaoName_address_English");
					String DaibiaoName_address_pianjiaming = req.getParameter("DaibiaoName_address_pianjiaming");



					String company_DD = req.getParameter("company_DD");
					String company_MM = req.getParameter("company_MM");
					String company_YYYY = req.getParameter("company_YYYY");

					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
					CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
					CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
					CompanyName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(CompanyName_pianjiaming);

					address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
					address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
					address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(address_pianjiaming);

					DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
					DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);
					DaibiaoName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_pianjiaming);

					DaibiaoName_address_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_address_Chinese);
					DaibiaoName_address_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_address_English);
					DaibiaoName_address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_address_pianjiaming);

					//全角英字から半角英字への変換
					CompanyName_Chinese = FuncUtils.toHalfWidth(CompanyName_Chinese);
					CompanyName_English = FuncUtils.toHalfWidth(CompanyName_English);
					CompanyName_pianjiaming = FuncUtils.toHalfWidth(CompanyName_pianjiaming);

					address_Chinese = FuncUtils.toHalfWidth(address_Chinese);
					address_English = FuncUtils.toHalfWidth(address_English);
					address_pianjiaming = FuncUtils.toHalfWidth(address_pianjiaming);

					DaibiaoName_Chinese = FuncUtils.toHalfWidth(DaibiaoName_Chinese);
					DaibiaoName_English = FuncUtils.toHalfWidth(DaibiaoName_English);
					DaibiaoName_pianjiaming = FuncUtils.toHalfWidth(DaibiaoName_pianjiaming);

					DaibiaoName_address_Chinese = FuncUtils.toHalfWidth(DaibiaoName_address_Chinese);
					DaibiaoName_address_English = FuncUtils.toHalfWidth(DaibiaoName_address_English);
					DaibiaoName_address_pianjiaming = FuncUtils.toHalfWidth(DaibiaoName_address_pianjiaming);


					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaofeishui_shuihao, " + xiaofeishui_shuihao);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", etax_no, " + etax_no);

					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type_guanfang, " + user_type_guanfang);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type_zixuan, " + user_type_zixuan);

					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);

					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);


					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_pianjiaming, " + address_pianjiaming);

					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_Chinese, " + DaibiaoName_address_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_English, " + DaibiaoName_address_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_pianjiaming, " + DaibiaoName_address_pianjiaming);


					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_DD, " + company_DD);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_MM, " + company_MM);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_YYYY, " + company_YYYY);


					t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

					if ("add".equals(key)){
						yyyymmdd_count = "";

						if (StringUtils.isEmpty(xiaofeishui_shuihao) == false) {
							t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("InvoiceBangou", "T" + xiaofeishui_shuihao);
							if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
								msg = "本数据，已经存在，无法追加！ InvoiceBangou " + xiaofeishui_shuihao;
								res = res + msg;
//							res = res + "<br>";
								logger.info("end " + msg);
								msg = "{\"res\":\"" + res + "\"}";
								session.setAttribute("msg", msg);out.print(msg);
								return;

							}
						}
						if (StringUtils.isEmpty(etax_no) == false) {
							t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("Bangou", etax_no);
							if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
								msg = "本数据，已经存在，无法追加！ etax_no " + etax_no;
								res = res + msg;
//							res = res + "<br>";
								logger.info("end " + msg);
								msg = "{\"res\":\"" + res + "\"}";
								session.setAttribute("msg", msg);out.print(msg);
								return;

							}
						}


						if (StringUtils.isEmpty(CompanyName_Chinese) == false) {
							t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("CompanyName_Chinese", CompanyName_Chinese);
							if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
								msg = "本数据，已经存在，无法追加！ CompanyName_Chinese " + CompanyName_Chinese;
								res = res + msg;
//							res = res + "<br>";
								logger.info("end " + msg);
								msg = "{\"res\":\"" + res + "\"}";
								session.setAttribute("msg", msg);out.print(msg);
								return;

							}
						}

						if (StringUtils.isEmpty(CompanyName_English) == false) {
							t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("CompanyName_English", CompanyName_English);
							if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
								msg = "本数据，已经存在，无法追加！ CompanyName_English " + CompanyName_English;
								res = res + msg;
//							res = res + "<br>";
								logger.info("end " + msg);
								msg = "{\"res\":\"" + res + "\"}";
								session.setAttribute("msg", msg);out.print(msg);
								return;

							}
						}

					} else if ("up".equals(key)){
						if (StringUtils.isEmpty(yyyymmdd_count) == true) {
							msg = "没有指定，管理ID！";
							res = res + msg;
//						res = res + "<br>";
							logger.info("end " + msg);
							msg = "{\"res\":\"" + res + "\"}";
							session.setAttribute("msg", msg);out.print(msg);
							return;


						}

						t_etax_account_infoExBean db_t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
						if (StringUtils.isEmpty(db_t_etax_account_infoExBean.getYyyymmdd_count()) == true) {
							msg = "指定的管理ID，数据不存在！";
							res = res + msg;
//						res = res + "<br>";
							logger.info("end " + msg);
							msg = "{\"res\":\"" + res + "\"}";
							session.setAttribute("msg", msg);out.print(msg);
							return;
						}


						t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
						String my_yyyymmdd_count = t_etax_account_resDao.selec_where_InvoiceBangou("T" + xiaofeishui_shuihao);
						if (yyyymmdd_count.equals(my_yyyymmdd_count)) {

						} else {
							if (StringUtils.isEmpty(my_yyyymmdd_count) == false) {
								msg = "指定的日本消费税税号，已经存在！";
								res = res + msg;
//						res = res + "<br>";
								logger.info("end " + msg);
								msg = "{\"res\":\"" + res + "\"}";
								session.setAttribute("msg", msg);out.print(msg);
								return;
							}

						}


					}






					msg = "";
					res = "";

					t_etax_account_infoExBean = new t_etax_account_infoExBean();


					t_etax_account_infoExBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_account_infoExBean.setUser_id(User_infoBean.getUser_id());

					t_etax_account_infoExBean.setXiaofeishui_shuihao(xiaofeishui_shuihao);
					t_etax_account_infoExBean.setEtax_no(etax_no);


					t_etax_account_infoExBean.setUser_type_guanfang(user_type_guanfang);
					t_etax_account_infoExBean.setUser_type_zixuan(user_type_zixuan);
					t_etax_account_infoExBean.setUser_type(user_type_zixuan);


					t_etax_account_infoExBean.setCompanyName_Chinese(CompanyName_Chinese);
					t_etax_account_infoExBean.setCompanyName_English(CompanyName_English);
					t_etax_account_infoExBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);

					t_etax_account_infoExBean.setAddress_Chinese(address_Chinese);
					t_etax_account_infoExBean.setAddress_English(address_English);
					t_etax_account_infoExBean.setAddress_pianjiaming(address_pianjiaming);

					t_etax_account_infoExBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
					t_etax_account_infoExBean.setDaibiaoName_English(DaibiaoName_English);
					t_etax_account_infoExBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);

					t_etax_account_infoExBean.setDaibiaoName_address_Chinese(DaibiaoName_address_Chinese);
					t_etax_account_infoExBean.setDaibiaoName_address_English(DaibiaoName_address_English);
					t_etax_account_infoExBean.setDaibiaoName_address_pianjiaming(DaibiaoName_address_pianjiaming);

					t_etax_account_infoExBean.setCompany_DD(company_DD);
					t_etax_account_infoExBean.setCompany_MM(company_MM);
					t_etax_account_infoExBean.setCompany_YYYY(company_YYYY);

					t_etax_account_infoExBean.setZhice_ziben(zhice_ziben);
					t_etax_account_infoExBean.setYaoqing_no(yaoqing_no);



					if (StringUtils.isEmpty(xiaofeishui_shuihao) == false) {
						xiaofeishui_shuihao = "T" + xiaofeishui_shuihao;
						t_etax_account_infoExBean t_etax_account_infoExBean_forAPI = FuncUtils.sendGetInvoiceBangou_json(xiaofeishui_shuihao);
		                //人格区分 1个人 2法人
						if ("2".equals(t_etax_account_infoExBean_forAPI.getUser_type())) {
							t_etax_account_infoExBean.setUser_type_guanfang("法人");

						} else {
							t_etax_account_infoExBean.setUser_type_guanfang("个人");

						}

						t_etax_account_infoExBean.setCompanyName_pianjiaming(t_etax_account_infoExBean_forAPI.getCompanyName_pianjiaming());

					}

					if ("up".equals(key)){
						t_etax_account_infoDao.Update_user_info_AI(yyyymmdd_count, t_etax_account_infoExBean);
//						ExeDb.set_pianjiaming(yyyymmdd_count);
						msg = "ok";
						res = res + msg;
//						res = res + "<br>";

						/*
						 * 再激活
						 */
						t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
						EtaxDao EtaxDao = new EtaxDao();
						try {

							String hojinmeiKana = t_etax_account_infoExBean.getCompanyName_pianjiaming();

							t_etax_account_infoBean t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
									t_etax_account_resDao, EtaxDao, hojinmeiKana);
							t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

						} catch (SQLException e) {
							t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

						} catch (Exception e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return;
						}



						if (!StringUtils.isEmpty(xiaofeishui_shuihao)) {
							t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, xiaofeishui_shuihao);

						}


				        logger.info("end add ok");

						msg = "{"
				        		+ " \"res\":\"" + res + "\""
//				        		+ ",\"activation_code\":\"" + uuid + "\""
				        		+ "}";
				        session.setAttribute("msg", msg);out.print(msg);




				        return;

					}


			        int digits = 8; // 需要生成的位数
			        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
			        t_etax_account_infoExBean.setEtax_pw(etax_pw);


			        t_etax_account_infoExBean.setEtax_pw_flag("0");

					if (StringUtils.isEmpty(xiaofeishui_shuihao) == false) {
						t_etax_account_infoExBean.setInvoiceBangou(xiaofeishui_shuihao);
					}


					if ("个人".equals(t_etax_account_infoExBean.getUser_type()) && StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_Chinese())) {
						t_etax_account_infoExBean.setCompanyName_Chinese(t_etax_account_infoExBean.getDaibiaoName_Chinese());
					}

					if (StringUtils.isEmpty(yyyymmdd_count) == true) {
						m_sequenceDao m_sequenceDao = new m_sequenceDao();
						yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
					}


					t_etax_account_infoExBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_account_infoExBean.setSyouninn_status("待处理");//承認無
					t_etax_account_infoExBean.setActivation_code(yyyymmdd_count);
					t_etax_account_infoExBean.setEtax_pw_flag("0");


					String tatujin_id = "V"
					        + yyyymmdd_count.substring(2, 8)
					        + yyyymmdd_count.substring(11, 14);

					t_etax_account_infoExBean.setTatujin_id(tatujin_id);

					t_etax_account_infoDao.INSERT(t_etax_account_infoExBean);

//					t_etax_account_infoDao.connection.setAutoCommit(fa lse);
//					t_etax_account_infoDao.connection.rollback();

					/*
					 *
					 */
					ExeDb.exe_activation(yyyymmdd_count);

					/*
					 * UPDATE t_etax_account_res SET HoujinBangou='9700150118570', InvoiceBangou='T9700150118570' WHERE yyyymmdd_count='20240603990001';
					 */

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, t_etax_account_infoExBean.getHoujinBangou());
					if (!StringUtils.isEmpty(t_etax_account_infoExBean.getInvoiceBangou())) {
						t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, t_etax_account_infoExBean.getInvoiceBangou());

					}

					ExeDb.set_pianjiaming(yyyymmdd_count);
//					ExeDb.set_DaibiaoName_English(yyyymmdd_count);
//					ExeDb.setInfoForAPI(yyyymmdd_count);

					//		Etax		12
					if (!StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
						t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "bangou", t_etax_account_infoExBean.getBangou());
					}



					/*
					 *登录信息发邮件给客户
					 */
//					t_etax_account_infoExBean.setEmail(User_infoBean.getEmail());
//					FuncUtils.sendMail_shengao(t_etax_account_infoExBean, t_xiaofeishui_shengaoBean, path, User_infoBean_groupAdmin);

					msg = "ok";
					res = res + msg;
//					res = res + "<br>";


			        logger.info("end add ok");

					msg = "{"
			        		+ " \"res\":\"" + res + "\""
//			        		+ ",\"activation_code\":\"" + uuid + "\""
			        		+ "}";
			        session.setAttribute("msg", msg);out.print(msg);
			        return;




				} catch (Exception e) {
					// TODO 登录失败怎么办
					e.printStackTrace();
					msg = "登录失败，请联系客服！给您造成的不便，深感抱歉：" + e.getMessage();
					res = res + msg;
					res = res + "<br>";
					logger.info(res);

					msg = "{\"res\":\"" + res + "\"}";
					session.setAttribute("msg", msg);out.print(msg);

					return;

				}







		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "FilesJieguoShangchuan".equals(hidden_key)) {

				try {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);

					String form_CompanyName_Chinese = EtaxAccountInfoBean.getCompanyName_Chinese();
					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
					form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

					//アップロードするフォルダ
					String path = getServletContext().getRealPath("/fileDataJieguo");
					String falieName = yyyymmdd_count + "_" + form_CompanyName_Chinese;
					path = path + "/" + falieName;

					File directory = new File(path);

					if (directory.exists()) {
						FuncUtils.deleteFolder(directory);
					}

					//mkdir
					boolean hasSucceeded = directory.mkdir();
					logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);

					req.getParts();
					req.setCharacterEncoding("utf-8");

					// 拡張for文
					for (int j = 0; j < req.getParts().size(); j++) {
						//name属性がfileのファイルをPartオブジェクトとして取得
						Part part = req.getPart("file[" + j + "]");
						//ファイル名を取得
						//String filename=part.getSubmittedFileName();//ie対応が不要な場合
						String filename = yyyymmdd_count + "_";
						filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();

						//書き込み
						part.write(path + File.separator + filename);

						String fe = FilenameUtils.getExtension(filename);

						if ("pdf".equals(fe)) {
							byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

						}

					}

//
//
//					directory = new File(path);
//			        if (directory.exists()) {
//
//			        	/*
//						 * 生成文件ZIP
//						 */
//						// 源文件夹的路径
//						String sourceFolderPath = path;
//						// 目标ZIP文件的路径
//						String targetZipFilePath = path + ".zip";
//						try {
//							// 创建一个输出流，将文件写入ZIP文件
//							FileOutputStream fos = new FileOutputStream(targetZipFilePath);
//							ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//							// 调用递归方法将文件夹及其内容添加到ZIP文件中
//							FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);
//
//							// 关闭ZIP文件输出流
//							zipOut.close();
//							fos.close();
//
//							logger.info("ZIP文件创建成功：" + targetZipFilePath);
//
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//
//			        }

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "output_file_jieguo", falieName);

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

			} else if ("etaxonly".equals(hidden_key)) {


				// 获取当前日期时间
				Date currentDate = new Date();
				// 设置日期时间格式
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				// 格式化日期时间
				String yyyymmddhhmmss = dateFormat.format(currentDate);

				//アップロードするフォルダ
				String path = getServletContext().getRealPath("/fileDataEtaxonly");

				/*
				 * license確認
				 */
				// 不要

				/*
				 * 登录功能 附件
				 */
				try {
					FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
				} catch (Throwable e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				/*
				 * 获取上传的多个文件部分
				 */
				Map<String, String> excelDataTitle = new LinkedHashMap<>();
				Map<String, String> excelData = new LinkedHashMap<>();
				Map<String, Map<String, String>> excelDataHashMap = new LinkedHashMap<>();
				File directory = new File(path + "/" + yyyymmddhhmmss);

				if (!directory.exists() || !directory.isDirectory()) {
					logger.info("指定的路径不是一个有效的目录。");
					return;
				}

				File[] files = directory.listFiles();

				if (files == null || files.length == 0) {
					logger.info("目录下没有文件。");
					return;
				}

				int count = 0;
				for (File file : files) {
					if (file.isFile()) {
						String fileName = file.getName();
						String fileExtension = FuncUtils.getFileExtension(fileName);

						try {
							if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
								FileInputStream fis = new FileInputStream(file);
								Workbook workbook = WorkbookFactory.create(fis);
								Sheet sheet = workbook.getSheetAt(0);
								// 遍历每一行
								Iterator<Row> rowIterator = sheet.iterator();
								while (rowIterator.hasNext()) {
									Row row = rowIterator.next();

//									// 跳过第1行（假设第1行为标题）
									if (row.getRowNum() < 1) {
										continue;
									}

									for (int i = 0; i <= 6; i++) {
										// 获取 N 列的数据
										Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
										// 将 A 列和 B 列的数据存储到 excelData HashMap
										String key = (cell != null) ? cell.toString() : "";
										String value = "";

										if (cell != null) {
											if (cell.getCellType() == CellType.FORMULA) {
												// 如果单元格中包含公式，则计算并输出结果
												value = FuncUtils.evaluateFormulaCell(cell, workbook);
											} else {
												// 如果是其他类型的单元格，直接输出值
												value = cell.toString();
											}

										}

										excelData.put("" + i, value);

									}
									if (StringUtils.isEmpty(excelData.get("1")) || StringUtils.isEmpty(excelData.get("3")) || StringUtils.isEmpty(excelData.get("5"))) {
										continue;
									}
									excelDataHashMap.put("" + (++count), excelData);
									excelData = new HashMap<>();

								}

							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}

				/*
				 * 登録データ準備
				 */
				for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap.entrySet()) {
					Map<String, String> excelValue = entry_excelDataHashMap.getValue();

					String CompanyName_English = excelValue.get("1");
					String CompanyName_Chinese = excelValue.get("2");
					String address_English = excelValue.get("3");
					String address_Chinese = excelValue.get("4");
					String DaibiaoName_English = excelValue.get("5");
					String DaibiaoName_Chinese = excelValue.get("6");


					if (StringUtils.isEmpty(CompanyName_English) || StringUtils.isEmpty(address_English) || StringUtils.isEmpty(DaibiaoName_English)) {
						continue;
					}


					String hojinmeiKana = "";
					String HoujinBangou = "";
					String InvoiceBangou = "";

					try {

						t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetHoujinBangouByHoujinName(FuncUtils.toFullWidth(CompanyName_English));
						HoujinBangou = t_etax_account_infoExBean.getHoujinBangou();
						hojinmeiKana = t_etax_account_infoExBean.getCompanyName_pianjiaming();
						if (StringUtils.isEmpty(t_etax_account_infoExBean.getAddress_English())) {
							// 何もしない
						} else {
							address_English = t_etax_account_infoExBean.getAddress_English();

						}

					} catch (Exception e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
						return;
					}


					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
					CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
					CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
					address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
					address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
					DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);
					DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);




					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_account_infoBean.setUser_id("etaxonly");
					t_etax_account_infoBean.setUser_type("公司");

					t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
					t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
					t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
					t_etax_account_infoBean.setDaibiaoName_English(DaibiaoName_English);
					t_etax_account_infoBean.setAddress_Chinese(address_Chinese);
					t_etax_account_infoBean.setAddress_English(address_English);

					t_etax_account_infoBean.setYaoqing_no(yaoqing_no);

					t_etax_account_infoBean.setSyouninn_status("待处理");//承認無

					UUID uuid = UUID.randomUUID();
					t_etax_account_infoBean.setActivation_code("etaxonly-" + uuid.toString());


		            FuncUtils FuncUtils = new FuncUtils();
		    		String CompanyName_pianjiaming = "";
					if (StringUtils.isEmpty(hojinmeiKana)) {
						CompanyName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getCompanyName_Chinese());
					} else {
						CompanyName_pianjiaming = hojinmeiKana;

					}

		    		String address_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getAddress_Chinese());
		    		String DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getDaibiaoName_Chinese());

		    		t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
		    		t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
		    		t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);




			        int digits = 8; // 需要生成的位数
			        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
			        t_etax_account_infoBean.setEtax_pw(etax_pw);

			        t_etax_account_infoBean.setEtax_pw_flag("0");


					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_pianjiaming, " + address_pianjiaming);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);


					/*
					 * 登録
					 */
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

					try {
						t_etax_account_infoDao.INSERT(t_etax_account_infoBean);

					} catch (Exception e) {
						// TODO 登录失败怎么办
						e.printStackTrace();
						return;
					}

					/*
					 * 激活
					 */
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					EtaxDao EtaxDao = new EtaxDao();
					try {


						t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
								t_etax_account_resDao, EtaxDao, hojinmeiKana);
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

					} catch (SQLException e) {
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return;
					}


					//HoujinBangou
					if (StringUtils.isEmpty(HoujinBangou)) {
						// 何もしない
					} else {
						logger.info("yyyymmdd_count: " + yyyymmdd_count + ", HoujinBangou, " + HoujinBangou);
						try {
							t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, HoujinBangou);
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return;
						}

					}

					//InvoiceBangou
					t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou("T" + HoujinBangou);
					InvoiceBangou = t_etax_account_infoExBean.getInvoiceBangou();
					if (StringUtils.isEmpty(InvoiceBangou)) {
						// 何もしない
					} else {
						logger.info("yyyymmdd_count: " + yyyymmdd_count + ", InvoiceBangou, " + InvoiceBangou);
						try {
							t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, InvoiceBangou);
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return;
						}

					}




				}

				logger.info("end");
				return;




//			} else if ("etaxonly".equals(hidden_key)) {
//
//				// 获取当前日期时间
//				Date currentDate = new Date();
//				// 设置日期时间格式
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//				// 格式化日期时间
//				String yyyymmddhhmmss = dateFormat.format(currentDate);
//
//				//アップロードするフォルダ
//				String path = getServletContext().getRealPath("/fileDataEtaxonly");
//
//				/*
//				 * license確認
//				 */
//				// 不要
//
//				/*
//				 * 登录功能 附件
//				 */
//				try {
//					FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
//				} catch (Throwable e) {
//					// TODO 自動生成された catch ブロック
//					e.printStackTrace();
//				}
//
//				/*
//				 * 获取上传的多个文件部分
//				 */
//				Map<String, String> excelDataTitle = new LinkedHashMap<>();
//				Map<String, String> excelData = new LinkedHashMap<>();
//				Map<String, Map<String, String>> excelDataHashMap = new LinkedHashMap<>();
//				File directory = new File(path + "/" + yyyymmddhhmmss);
//
//				if (!directory.exists() || !directory.isDirectory()) {
//					logger.info("指定的路径不是一个有效的目录。");
//					return;
//				}
//
//				File[] files = directory.listFiles();
//
//				if (files == null || files.length == 0) {
//					logger.info("目录下没有文件。");
//					return;
//				}
//
//				int count = 0;
//				for (File file : files) {
//					if (file.isFile()) {
//						String fileName = file.getName();
//						String fileExtension = FuncUtils.getFileExtension(fileName);
//
//						try {
//							if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
//								FileInputStream fis = new FileInputStream(file);
//								Workbook workbook = WorkbookFactory.create(fis);
//								Sheet sheet = workbook.getSheetAt(0);
//								// 遍历每一行
//								Iterator<Row> rowIterator = sheet.iterator();
//								while (rowIterator.hasNext()) {
//									Row row = rowIterator.next();
//
//									// 跳过第2行（假设第1,2行为标题）
//									if (row.getRowNum() < 3) {
//										continue;
//									}
//
//									for (int i = 0; i <= 32; i++) {
//										// 获取 N 列的数据
//										Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
//										// 将 A 列和 B 列的数据存储到 excelData HashMap
//										String key = (cell != null) ? cell.toString() : "";
//										String value = "";
//
//										if (cell != null) {
//											if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
//												// 如果单元格中包含公式，则计算并输出结果
//												value = FuncUtils.evaluateFormulaCell(cell, workbook);
//											} else {
//												// 如果是其他类型的单元格，直接输出值
//												value = cell.toString();
//											}
//
//										}
//
//
//
//										if (row.getRowNum() == 2) {
//											excelDataTitle.put("" + i, value.replaceAll("\n", ""));
//
//										} else {
//											excelData.put("" + i, value);
//
//										}
//
//
//									}
//									if (StringUtils.isEmpty(excelData.get("4"))) {
//										break;
//									}
//									excelDataHashMap.put("" + (++count), excelData);
//									excelData = new HashMap<>();
//
//								}
//
//							}
//						} catch (Throwable e) {
//							e.printStackTrace();
//						}
//					}
//				}
//
//				/*
//				 * 登録データ準備
//				 */
//				for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap.entrySet()) {
//					Map<String, String> excelValue = entry_excelDataHashMap.getValue();
////						0		"法人番号（書）（非）"
////						1		"組織前後（選）（非）"
////						2		"組織名称（選）（非）"
////						3		"法人名称フリガナ（書）（必）【自動】"
////						4	CompanyName_English	"法人名称（書）（必）"
//					String CompanyName_Chinese = excelValue.get("4");
//					String CompanyName_English = excelValue.get("5");
////						5		"納税地郵便番号前三（書）（非）"
////						6		"納税地郵便番号後四（書）（非）"
////						7		"納税地都道府県（選）（必）"
////						8		"納税地市区町村（書）（必）"
////						9		"納税地丁目番地等（書）（必）"
////						10		"納税地建物名・号室（書）（非）"
////						11		"納税地電話番号前三（書）（非）"
////						12		"納税地電話番号中四（書）（非）"
////						13		"納税地電話番号後四（書）（非）"
////						14		"提出先税務署都道府県（選）（必）"
////						15		"提出先税務署税務署（選）（必）"
////						16		"代表者氏名姓フリガナ（書）（必）【自動】"
////						17		"代表者氏名名フリガナ（書）（必）【自動】"
////						18	DaibiaoName_Chinese1	"代表者氏名姓漢字（書）（必）"
////						19	DaibiaoName_Chinese2	"代表者氏名名漢字（書）（必）"
//					String DaibiaoName_Chinese = excelValue.get("19") + " " + excelValue.get("20");
////						20		"代表者住所郵便番号前三（書）（非）"
////						21		"代表者住所郵便番号後四（書）（非）"
////						22		"代表者住所都道府県（選）（必）"
////						23		"代表者住所市区町村（書）（必）"
////						24		"代表者住所丁目番地等（書）（必）"
////						25		"代表者住所建物名・号室（書）（非）"
////						26		"代表者住所電話番号前三（書）（非）"
////						27		"代表者住所電話番号中四（書）（非）"
////						28		"代表者住所電話番号後四（書）（非）"
////						29	etax_pw	"暗証番号（書）（必）"
////						30		"暗証番号確認（書）（必）"
////						31		"納税用確認番号（書）（必）"
////						32		"納税用カナ氏名・名称（書）（必）【自動】"
//
//
//					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
//					CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
//					CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
//					DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
//
//
//					m_sequenceDao m_sequenceDao = new m_sequenceDao();
//					yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
//
//
//					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
//					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
//					t_etax_account_infoBean.setUser_id("etaxonly");
//					t_etax_account_infoBean.setUser_type("公司");
//					t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
//					t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
//					t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
//					t_etax_account_infoBean.setYaoqing_no(yaoqing_no);
//
//					t_etax_account_infoBean.setSyouninn_status("待处理");//承認無
//
//					UUID uuid = UUID.randomUUID();
//					t_etax_account_infoBean.setActivation_code("etaxonly-" + uuid.toString());
//
//
//					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
//					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
//
//
//					/*
//					 * 登録
//					 */
//					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
//
//					try {
//						t_etax_account_infoDao.INSERT(t_etax_account_infoBean);
//
//					} catch (Exception e) {
//						// TODO 登录失败怎么办
//						e.printStackTrace();
//						return;
//					}
//
//					/*
//					 * 激活
//					 */
//					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//					EtaxDao EtaxDao = new EtaxDao();
//					try {
//
//
//						t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
//								t_etax_account_resDao, EtaxDao);
//						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");
//
//					} catch (SQLException e) {
//						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");
//
//					}
//
//
//
//
//
//				}
//
//				return;
//
//



				/*
				 * xtx
				 */
			} else if (!StringUtils.isEmpty(yyyymmdd_count) && "get_file".equals(hidden_key) && "xtx".equals(hidden_value)) {
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", INSQ, " + INSQ);

				try {
					FuncUtilsAiEtax FuncUtilsAiEtax = new FuncUtilsAiEtax();
					String path = FuncUtilsAiEtax.getXtx(req, INSQ);

					File file = new File(path);
					if (file.exists()) {

						/*
						 * 生成文件ZIP
						 */
						// 源文件夹的路径
						String sourceFolderPath = path;
						// 目标ZIP文件的路径
						String targetZipFilePath = path + ".zip";
						try {
							// 创建一个输出流，将文件写入ZIP文件
							FileOutputStream fos = new FileOutputStream(targetZipFilePath);
							ZipOutputStream zipOut = new ZipOutputStream(fos);

							// 调用递归方法将文件夹及其内容添加到ZIP文件中
							FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

							// 关闭ZIP文件输出流
							zipOut.close();
							fos.close();

							logger.info("ZIP文件创建成功：" + targetZipFilePath);
							out.print("{\"res\":\"" + file.getName() + ".zip" + "\"}");

						} catch (IOException e) {
							e.printStackTrace();


						}

					} else {
						out.print("{\"res\":\"结果文件不存在\"}");
						logger.info("end " + hidden_key);
						return;
					}

				} catch (Exception e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();

					out.print("{\"res\":\""+e1+"\"}");
					logger.info("end " + hidden_key);
					return;
				}



				logger.info("end " + hidden_value);
				return;




			}

		if ("SetUserInfoLogic3".equals(hidden_key)) {

			/*
			 * 登录功能 客户数据
			 */
			try {

				String user_type = req.getParameter("hidden_user_type");
				String DaibiaoName_English = req.getParameter("form_DaibiaoName_English");
				String company_DD = req.getParameter("form_company_DD");
				String company_MM = req.getParameter("form_company_MM");
				String company_YYYY = req.getParameter("form_company_YYYY");
				String tel_1 = req.getParameter("form_tel_1");
				String tel_2 = req.getParameter("form_tel_2");
				String tel_3 = req.getParameter("form_tel_3");
				String tel_country = req.getParameter("form_tel_country");
				String xiaoshouerYYYY_1 = req.getParameter("form_xiaoshouerYYYY_1");
				String xiaoshouerYYYY_1_half = req.getParameter("form_xiaoshouerYYYY_1_half");
				String xiaoshouerYYYY_2 = req.getParameter("form_xiaoshouerYYYY_2");
				String zhice_ziben = req.getParameter("form_zhice_ziben");
				String address_Chinese = req.getParameter("form_address_Chinese");
				String CompanyName_Chinese = req.getParameter("form_CompanyName_Chinese");
				String CompanyName_English = req.getParameter("form_CompanyName_English");
				String DaibiaoName_Chinese = req.getParameter("form_DaibiaoName_Chinese");
				String geren_dianpu_address = req.getParameter("form_geren_dianpu_address");
				String geren_dianpu_name = req.getParameter("form_geren_dianpu_name");
				String changshe_jigou_Select = req.getParameter("form_changshe_jigou_Select");
				String jianyi_keshui_Select = req.getParameter("form_jianyi_keshui_Select");
				String address_English = req.getParameter("form_address_English");
				String jianyi_keshui_type = req.getParameter("form_jianyi_keshui_type");
				String tokutei_kikann_siharai_kyuuyo = req.getParameter("form_tokutei_kikann_siharai_kyuuyo");
				String shouri_kaishi_denglu_xiayige = req.getParameter("form_shouri_kaishi_denglu_xiayige");
				String shouri_kaishi_denglu_ben = req.getParameter("form_shouri_kaishi_denglu_ben");




				String CompanyName_pianjiaming = req.getParameter("form_CompanyName_pianjiaming");
				String address_pianjiaming = req.getParameter("form_address_pianjiaming");
				String DaibiaoName_pianjiaming = req.getParameter("form_DaibiaoName_pianjiaming");
				String DaibiaoName_address_pianjiaming = req.getParameter("form_DaibiaoName_address_pianjiaming");



//				user_id = req.getParameter("form_user_id");
				String etax_no = req.getParameter("form_etax_no");
				yaoqing_no = req.getParameter("form_yaoqing_no");



				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
				CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
				CompanyName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(CompanyName_pianjiaming);
				address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
				address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
				address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(address_pianjiaming);
				DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
				DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);
				DaibiaoName_address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_address_pianjiaming);


				String xiaoshouerYYYY_2_title = req.getParameter("form_xiaoshouerYYYY_2_title");
				String xiaoshouerYYYY_1_half_title = req.getParameter("form_xiaoshouerYYYY_1_half_title");
				String tokutei_kikann_siharai_kyuuyo_title = req.getParameter("form_tokutei_kikann_siharai_kyuuyo_title");
				String xiaoshouerYYYY_1_YYYYMMDD_title = req.getParameter("form_xiaoshouerYYYY_1_YYYYMMDD_title");
				String shuoming = req.getParameter("form_shuoming");


				String history = req.getParameter("form_history");


				String DaibiaoName_address_Chinese = req.getParameter("form_DaibiaoName_address_Chinese");
				String DaibiaoName_address_English = req.getParameter("form_DaibiaoName_address_English");
				String riben_kaishi_shiye_YYYY = req.getParameter("form_riben_kaishi_shiye_YYYY");
				String riben_kaishi_shiye_MM = req.getParameter("form_riben_kaishi_shiye_MM");
				String riben_kaishi_shiye_DD = req.getParameter("form_riben_kaishi_shiye_DD");


				String keshui_or_mianshui = req.getParameter("form_keshui_or_mianshui");
				String YYYY_1 = req.getParameter("form_YYYY_1");
				String keshui_shiyezhe_wenshu = req.getParameter("form_keshui_shiyezhe_wenshu");



				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yaoqing_no, " + yaoqing_no);

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type, " + user_type);
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


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_Chinese, " + DaibiaoName_address_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_English, " + DaibiaoName_address_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_YYYY, " + riben_kaishi_shiye_YYYY);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_MM, " + riben_kaishi_shiye_MM);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_DD, " + riben_kaishi_shiye_DD);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshui_or_mianshui, " + keshui_or_mianshui);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", YYYY_1, " + YYYY_1);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshui_shiyezhe_wenshu, " + keshui_shiyezhe_wenshu);



				//TODO
//				DaibiaoName_English =	"hou yi";
//				company_DD =			"9";
//				company_MM =			"9";
//				company_YYYY =			"2009";
//				tel_1 =					"123";
//				tel_2 =					"4567";
//				tel_3 =					"8901";
//				tel_country =			"86";
//				xiaoshouerYYYY_1 =		"95";
//				xiaoshouerYYYY_1_half =	"91";
//				xiaoshouerYYYY_2 =		"92";
//				zhice_ziben =			"666666";
//				address_Chinese =		"北京海淀区";
//				CompanyName_Chinese =	"太阳有限公司";
//				CompanyName_English =	"taiyang Co.,Ltd.";
//				DaibiaoName_Chinese =	"后 裔";
////				geren_dianpu_address =	"";
////				geren_dianpu_name =		"";
////				changshe_jigou_Select =	"";
////				jianyi_keshui_Select =	"";
//				address_English =		"beijing,China";
////				jianyi_keshui_type =	"";

				if (history == null) {
					history = "";
				}
				if ("YES".equals(history.toUpperCase())) {
					session.setAttribute("user_type", user_type);
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



				} else {
					session.removeAttribute("user_type"			);
					session.removeAttribute("DaibiaoName_English"			);
					session.removeAttribute("company_DD"					);
					session.removeAttribute("company_MM"					);
					session.removeAttribute("company_YYYY"				);
					session.removeAttribute("tel_1"						);
					session.removeAttribute("tel_2"						);
					session.removeAttribute("tel_3"						);
					session.removeAttribute("tel_country"					);
					session.removeAttribute("xiaoshouerYYYY_1"			);
					session.removeAttribute("xiaoshouerYYYY_1_half"		);
					session.removeAttribute("xiaoshouerYYYY_2"			);
					session.removeAttribute("zhice_ziben"					);
					session.removeAttribute("address_Chinese"				);
					session.removeAttribute("CompanyName_Chinese"			);
					session.removeAttribute("CompanyName_English"			);
					session.removeAttribute("DaibiaoName_Chinese"			);
					session.removeAttribute("geren_dianpu_address"		);
					session.removeAttribute("geren_dianpu_name"			);
					session.removeAttribute("changshe_jigou_Select"		);
					session.removeAttribute("jianyi_keshui_Select"		);
					session.removeAttribute("address_English"				);
					session.removeAttribute("jianyi_keshui_type"			);
					session.removeAttribute("tokutei_kikann_siharai_kyuuyo");
					session.removeAttribute("shouri_kaishi_denglu_xiayige");
					session.removeAttribute("shouri_kaishi_denglu_ben"	);
					session.removeAttribute("etax_no"						);



				}



				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUser_id(User_infoBean.getUser_id());
				t_etax_account_infoBean.setUser_type(user_type);
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

				t_etax_account_infoBean.setDaibiaoName_address_Chinese(DaibiaoName_address_Chinese);
				t_etax_account_infoBean.setDaibiaoName_address_English(DaibiaoName_address_English);

				t_etax_account_infoBean.setSyouninn_status("待处理");//承認無


		        int digits = 8; // 需要生成的位数
		        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
		        t_etax_account_infoBean.setEtax_pw(etax_pw);

		        t_etax_account_infoBean.setEtax_pw_flag("0");

				if (StringUtils.isEmpty(CompanyName_pianjiaming)) {
					CompanyName_pianjiaming = FuncUtils.fn_hanzi(CompanyName_Chinese);
					CompanyName_pianjiaming = FuncUtils.toFullWidthAndTruncate(CompanyName_pianjiaming, 500);
					t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
				}


				if (StringUtils.isEmpty(address_pianjiaming)) {
					address_pianjiaming = FuncUtils.fn_hanzi(address_Chinese);
					address_pianjiaming = FuncUtils.toFullWidthAndTruncate(address_pianjiaming, 1000);
					t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
				}


				if (StringUtils.isEmpty(DaibiaoName_pianjiaming)) {
					DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(DaibiaoName_Chinese);
					DaibiaoName_pianjiaming = FuncUtils.toFullWidthAndTruncate(DaibiaoName_pianjiaming, 500);
					t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);
				}

				if (StringUtils.isEmpty(DaibiaoName_address_pianjiaming)) {
					DaibiaoName_address_pianjiaming = FuncUtils.fn_hanzi(DaibiaoName_address_Chinese);
					DaibiaoName_address_pianjiaming = FuncUtils.toFullWidthAndTruncate(DaibiaoName_address_pianjiaming, 1000);
					t_etax_account_infoBean.setDaibiaoName_address_pianjiaming(DaibiaoName_address_pianjiaming);
				}



				m_sequenceDao m_sequenceDao = new m_sequenceDao();
//				yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

				INSQ ="INSQ" + yyyy.substring(2, 4) + m_sequenceDao.selectMax_INSQ();

		        t_jct_shenqingBean t_jct_shenqingBean = new t_jct_shenqingBean();
		        t_jct_shenqingBean.setYyyymmdd_count(yyyymmdd_count);

		        t_jct_shenqingBean.setINSQ(INSQ);
		        t_jct_shenqingBean.setTianxie_YYYY(tianxie_YYYY);



		        t_jct_shenqingBean.setRiben_kaishi_shiye_YYYY(riben_kaishi_shiye_YYYY);
		        t_jct_shenqingBean.setRiben_kaishi_shiye_MM(riben_kaishi_shiye_MM);
		        t_jct_shenqingBean.setRiben_kaishi_shiye_DD(riben_kaishi_shiye_DD);
		        t_jct_shenqingBean.setXiaoshouerYYYY_2(xiaoshouerYYYY_2);
		        t_jct_shenqingBean.setXiaoshouerYYYY_1_half(xiaoshouerYYYY_1_half);

		        t_jct_shenqingBean.setKeshui_or_mianshui(keshui_or_mianshui);
		        t_jct_shenqingBean.setYYYY_1(YYYY_1);
		        t_jct_shenqingBean.setKeshui_shiyezhe_wenshu(keshui_shiyezhe_wenshu);


				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_jct_shenqingDao t_jct_shenqingDao = new t_jct_shenqingDao();

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				EtaxDao EtaxDao = new EtaxDao();

				Map<String, String[]> HashMapParameterMap = req.getParameterMap();
				for (String key : HashMapParameterMap.keySet()) {
					if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
						continue;
					}

					if ("hidden_key".equals(key) && "SetUserInfoLogic3".equals(HashMapParameterMap.get(key)[0])) {
						UUID uuid = UUID.randomUUID();
						t_etax_account_infoBean.setActivation_code(uuid.toString());

//						m_sequenceDao m_sequenceDao = new m_sequenceDao();
						//TODO
//						yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
						try {

							t_etax_account_infoDao.INSERT(t_etax_account_infoBean);
							t_jct_shenqingDao.INSERT(t_jct_shenqingBean);

							t_etax_account_infoBean.setEmail(User_infoBean.getEmail());


							if ("个人".equals(user_type)) {
								CompanyName_Chinese = DaibiaoName_Chinese.replace(" ", "");
							}

							//アップロードするフォルダ
							String path = getServletContext().getRealPath("/fileData");
							path = path + "/" + yyyymmdd_count + "_" + CompanyName_Chinese;
							//登录信息发邮件给客户
							FuncUtils.sendMail_jct_shengao(t_etax_account_infoBean, t_jct_shenqingBean, path, xiaoshouerYYYY_2_title, xiaoshouerYYYY_1_half_title, tokutei_kikann_siharai_kyuuyo_title, xiaoshouerYYYY_1_YYYYMMDD_title, shuoming);

						} catch (Exception e) {
							// TODO 登录失败怎么办
							e.printStackTrace();
						}




						//TODO 删除URL里边的key SetUserInfo 防止重复登录
						session.removeAttribute("SetUserInfo"					);
						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;

					} else if ("delete".equals(key)) {
						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;





					} else if ("Syouninn".equals(key)) {

						try {
							String syouninn_status = HashMapParameterMap.get("Syouninn_status")[0];
							//TODO
							yyyymmdd_count = HashMapParameterMap.get(key)[0];

							t_etax_account_infoDao.Update_syouninn_status(yyyymmdd_count, syouninn_status);

							if ("承認有".equals(syouninn_status)) {
								t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
										t_etax_account_resDao, EtaxDao, "");

							} else {
								t_etax_account_resDao.DELETE_res(yyyymmdd_count);
								EtaxDao.DELETE(yyyymmdd_count);

							}
						} catch (Exception e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}

						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;

					}
				}



				session.setAttribute("SetUserInfo", "OK");
				session.setAttribute("User_infoBean", User_infoBean);
				req.getRequestDispatcher("/setUserInfo3.jsp").forward(req, resp);

			} catch (Exception e) {
				e.printStackTrace();
			}



		} else if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
			if (StringUtils.isEmpty(INSQ) == true) {
				session.setAttribute("t_etax_account_infoExBean", new t_etax_account_infoExBean());
				session.setAttribute("t_jct_shenqingBean", new t_jct_shenqingBean());

				req.getRequestDispatcher("/setUserInfo3.jsp?fromBackend=true").forward(req, resp);
				logger.info("end");
				return;

//			} else {
//
//				logger.debug("PandaServiceTools → INSQ invalid");
//				out.write("PandaServiceMA → INSQ invalid");
//				logger.info("end");
//				return;
			}


			try {

				t_jct_shenqingDao t_jct_shenqingDao = new t_jct_shenqingDao();
				t_jct_shenqingBean t_jct_shenqingBean = t_jct_shenqingDao.SelectKeyValue("INSQ", INSQ);
//				t_xiaofeishui_shengaoBean.setActivation_code("");
				yyyymmdd_count = t_jct_shenqingBean.getYyyymmdd_count();
				if (StringUtils.isEmpty(yyyymmdd_count) == false) {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(t_jct_shenqingBean.getYyyymmdd_count());
					session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);
					session.setAttribute("t_jct_shenqingBean", t_jct_shenqingBean);

				} else {

					//session.setAttribute("User_infoBean", new User_infoBean());

					logger.debug("PandaServiceTools → INSQ invalid");
					out.write("PandaServiceMA → INSQ invalid");
					logger.info("end");
					return;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			req.getRequestDispatcher("/setUserInfo3.jsp?fromBackend=true").forward(req, resp);
			logger.info("end");
			return;





		} else {

			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");

		}





		logger.debug("end");

		return;

	}

    public static void main(String[] args) {

    	String address_pianjiaming = (new FuncUtils()).fn_hanzi("公司地址20250711192057");
		logger.debug(FuncUtils.toFullWidthAndTruncate(address_pianjiaming, 500));
    }



}