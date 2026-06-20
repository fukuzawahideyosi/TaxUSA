package com.panda.servlet.ai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

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
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_ai_guanli_yewubiaoDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;
import com.panda.utils.XMLCalculator;

@MultipartConfig
@WebServlet("/AiSetYewuShujuLogic2")
public class AiSetYewuShujuLogic2 extends HttpServlet {

	private static Logger logger = Logger.getLogger(AiSetYewuShujuLogic2.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();

		String msg = "";
		String res = "";
		String yyyy = "2024";
		String PDSK = req.getParameter("PDSK");

		String hidden_key = req.getParameter("hidden_key");
		if (hidden_key == null) {
			hidden_key = "";
		}
		logger.debug("hidden_key " + hidden_key);

		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");
		String table_name = req.getParameter("table_name");
		if (StringUtils.isEmpty(table_name) == true) {
		} else {

			if (!table_name.startsWith("AI_T_")) {
				table_name = "AI_T_" + table_name;
			}
		}

		String status = req.getParameter("status");
		logger.debug("status " + status);


		String form_mailarea = req.getParameter("form_mailarea");
		if (StringUtils.isEmpty(form_mailarea) == true) {
//			form_mailarea = "43936834@qq.com";
		}

		String form_JCT_NO = req.getParameter("form_xiaofeishui_shuihao");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("User_infoBean", new User_infoBean());
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);
		session.setAttribute("table_name", table_name);
		session.setAttribute("yyyymmdd_count", yyyymmdd_count);
		session.setAttribute("status", status);

		/*
		 * 激活码处理
		 */
		String activation_code = req.getParameter("activation_code");
		session.setAttribute("activation_code", activation_code);
		logger.debug("activation_code " + activation_code);

		if (!StringUtils.isEmpty(activation_code)) {
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.selectByActivation_code(table_name, activation_code);
			yyyymmdd_count = EtaxAccountInfoBean.getYyyymmdd_count();

			if (StringUtils.isEmpty(yyyymmdd_count)) {
				out.print("激活码无效，请联系客服！给您造成的不便，深感抱歉。");
				logger.info("end 激活码无效，请联系客服！给您造成的不便，深感抱歉。");
				return;

			} else {

//				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//				EtaxDao EtaxDao = new EtaxDao();
//				try {
//					EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
//							t_etax_account_resDao, EtaxDao, "");
//					t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");
//
//				} catch (SQLException e) {
//					t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");
//
//				}
//
//				User_infoDao LicenseDao = new User_infoDao();
//				User_infoBean User_infoBean = new User_infoBean();
//				User_infoBean = LicenseDao.selectByTiaojian("user_id", EtaxAccountInfoBean.getUser_id());
//				EtaxAccountInfoBean.setEmail(User_infoBean.getEmail());
//				//登录信息发邮件给客户
//				FuncUtils.sendMail_activation_code(EtaxAccountInfoBean);
//
//				out.print("尊敬的【"+EtaxAccountInfoBean.getCompanyName_Chinese()+"】，您已成功激活。我们将为您申请日本消费税税号，请耐心等待，如有进展，系统将自动为您发送邮件。");

			}

		}





		/*
		 * 邀请码有效性验证
		 */


		/*
		 * license確認
		 */
		if (StringUtils.isEmpty(user_id) == true) {
//			logger.debug("PandaServiceTools → License invalid");
//			out.write("PandaServiceMA → License invalid");
//			logger.info("end " + hidden_key);
//			return;

		} else {

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




		try {
			String err_msg = "";


			if (StringUtils.isEmpty(user_id) == true) {
				//user_id = "wangzihao";

			}


			String user_type = req.getParameter("hidden_user_type");
			String CompanyName_Chinese = req.getParameter("form_CompanyName_Chinese");
			String CompanyName_English = req.getParameter("form_CompanyName_English");
			String CompanyName_pianjiaming = req.getParameter("form_CompanyName_pianjiaming");
			String address_Chinese = req.getParameter("form_address_Chinese");
			String address_English = req.getParameter("form_address_English");
			String address_pianjiaming = req.getParameter("form_address_pianjiaming");
			String DaibiaoName_Chinese = req.getParameter("form_DaibiaoName_Chinese");
			String DaibiaoName_English = req.getParameter("form_DaibiaoName_English");
			String DaibiaoName_pianjiaming = req.getParameter("form_DaibiaoName_pianjiaming");
			String DaibiaoName_address_Chinese = req.getParameter("form_DaibiaoName_address_Chinese");
			String DaibiaoName_address_English = req.getParameter("form_DaibiaoName_address_English");
			String DaibiaoName_address_pianjiaming = req.getParameter("form_DaibiaoName_address_pianjiaming");

			String tel_country = req.getParameter("form_tel_country");
			String tel_1 = req.getParameter("form_tel_1");
			String tel_2 = req.getParameter("form_tel_2");
			String tel_3 = req.getParameter("form_tel_3");
			String zhice_ziben = req.getParameter("form_zhice_ziben");
			String company_YYYY = req.getParameter("form_company_YYYY");
			String company_MM = req.getParameter("form_company_MM");
			String company_DD = req.getParameter("form_company_DD");



	        TableDefinition def = new TableDefinition();
	        // 取 JSON (前端 append("json", "...") 的部分)
	        String jsonString = req.getParameter("json");
	        if (jsonString != null) {
	            Gson gson = new Gson();
		    	def = gson.fromJson(jsonString, TableDefinition.class);

		    	if (def.columns != null && !def.columns.isEmpty()) {
		    	    for (ColumnDefinition col : def.columns) {
		    	        if (col.name != null && col.name.startsWith("col_name")) {
		    	        	String key = col.comment.split("#,#")[0];
		    	        	String value = col.value;
		    	            logger.info("yyyymmdd_count: " + yyyymmdd_count + ", " + key + ", " + value);
		    	            def.columnsValue.put(key, value);
		    	            if ("【日本消费税税号或法人番号】".equals(col.comment.split("#,#")[0])) {
		    	            	form_JCT_NO = col.value;
		    	            }

		    	            if ("【公司名称或本人姓名（所在地区文字）】".equals(col.comment.split("#,#")[0])) {
		    	            	CompanyName_Chinese = value;
		    	            }

		    	        }
		    	    }
		    	}

	        }

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


//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type, " + user_type);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", addressaddress_pianjiaming_Chinese, " + address_pianjiaming);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_Chinese, " + DaibiaoName_address_Chinese);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_English, " + DaibiaoName_address_English);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_pianjiaming, " + DaibiaoName_address_pianjiaming);
//
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_country, " + tel_country);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_1, " + tel_1);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_2, " + tel_2);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_3, " + tel_3);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhice_ziben, " + zhice_ziben);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_YYYY, " + company_YYYY);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_MM, " + company_MM);
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_DD, " + company_DD);
//
//			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_JCT_NO, " + form_JCT_NO);

			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", activation_code, " + activation_code);
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_mailarea, " + form_mailarea);


			t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
			t_etax_account_infoBean.setUser_id(user_id);
			t_etax_account_infoBean.setUser_type(user_type);
			t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
			t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
			t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
			t_etax_account_infoBean.setAddress_Chinese(address_Chinese);
			t_etax_account_infoBean.setAddress_English(address_English);
			t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
			t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
			t_etax_account_infoBean.setDaibiaoName_English(DaibiaoName_English);
			t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);
			t_etax_account_infoBean.setDaibiaoName_address_Chinese(DaibiaoName_address_Chinese);
			t_etax_account_infoBean.setDaibiaoName_address_English(DaibiaoName_address_English);
			t_etax_account_infoBean.setDaibiaoName_address_pianjiaming(DaibiaoName_address_pianjiaming);

			t_etax_account_infoBean.setTel_country(tel_country);
			t_etax_account_infoBean.setTel_1(tel_1);
			t_etax_account_infoBean.setTel_2(tel_2);
			t_etax_account_infoBean.setTel_3(tel_3);
			t_etax_account_infoBean.setZhice_ziben(zhice_ziben);
			t_etax_account_infoBean.setCompany_YYYY(company_YYYY);
			t_etax_account_infoBean.setCompany_MM(company_MM);
			t_etax_account_infoBean.setCompany_DD(company_DD);

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


			t_etax_account_infoBean.setEmail(form_mailarea);
			t_etax_account_infoBean.setActivation_code(activation_code);
//			if (StringUtils.isEmpty(form_JCT_NO) == true) {
//				form_JCT_NO = "-1234567890123";
//			}
			t_etax_account_infoBean.setJCT_NO(form_JCT_NO);

			t_etax_account_infoBean.setSyouninn_status("待处理");//承認無
			t_etax_account_infoBean.setEmail(form_mailarea);



			if (StringUtils.isEmpty(user_id) == true) {
				// 获取当前日期时间
				Date currentDate = new Date();
				// 设置日期时间格式
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				// 格式化日期时间
				String yyyymmddhhmmss = dateFormat.format(currentDate);
				user_id = "user_id_" + yyyymmddhhmmss;
			}
			t_etax_account_infoBean.setUser_id(user_id);


	    	TableServiceDao TableServiceDao = new TableServiceDao();

			if (("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == true && StringUtils.isEmpty(status) == true)) {
				try {

					if (StringUtils.isEmpty(form_JCT_NO) == true) {
						form_JCT_NO = "-1234567890123";
					} else {

						t_etax_account_infoBean EtaxAccountInfoBean = FuncUtils.sendGetHoujinBangou(form_JCT_NO);
						if(StringUtils.isEmpty(EtaxAccountInfoBean.getCompanyName_English()) == true) {
							t_etax_account_infoBean.setUser_type("个人");
						} else {
							t_etax_account_infoBean.setUser_type("公司");

						}


					}
					t_etax_account_infoBean.setJCT_NO(form_JCT_NO);

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.selectAI(t_etax_account_infoBean);
					// 基本情報存在の場合
					if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == true) {




						int digits = 8; // 需要生成的位数
						String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
						t_etax_account_infoBean.setEtax_pw(etax_pw);
						t_etax_account_infoBean.setEtax_pw_flag("0");

						UUID uuid = UUID.randomUUID();
						t_etax_account_infoBean.setActivation_code(uuid.toString());

						m_sequenceDao m_sequenceDao = new m_sequenceDao();
						yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);

//						t_etax_account_infoDao.INSERT(t_etax_account_infoBean);

						status = "jiben_qingbao0";

					} else {
						yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();
						status = "jiben_qingbao";

					}


					// 1. 查表列表
					LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
					// 2. 查字段列表
					LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
					session.setAttribute("tableList", aiTables);
					session.setAttribute("tableColumnsList", tableColumns);
					def = FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def);

					activation_code = UUID.randomUUID().toString();
//					TableServiceDao.insertDynamic0(yyyymmdd_count, user_id, activation_code, status, def);

					String file_name = "";
					TableServiceDao.insertDynamic(yyyymmdd_count, user_id, activation_code, status, file_name, def);

					t_etax_account_infoBean.setActivation_code(activation_code);

					//登录信息发邮件给客户
					FuncUtils.sendMail_ai(t_etax_account_infoBean, yyyymmdd_count, user_id, status, def);
					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}


			} else if ("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == false && "jiben_qingbao".equals(status)) {
				try {

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.selectAI(t_etax_account_infoBean);
					// 基本情報存在の場合
					if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == true) {
						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_account_infoDao.Update_user_info_AI(yyyymmdd_count, t_etax_account_infoBean);




					} else {
						yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();

					}

//					TableServiceDao.updateDynamic_status(yyyymmdd_count, user_id, activation_code, status, def);

					String file_name = "";
					TableServiceDao.updateDynamic(yyyymmdd_count, user_id, activation_code, status, file_name, def);

					//登录信息发邮件给客户
					FuncUtils.sendMail_ai(t_etax_account_infoBean, yyyymmdd_count, user_id, status, def);
					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}




			} else if ("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == false && "yewu_qingbao".equals(status)) {

				try {


				    // 1. 查表列表
				    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
				    // 2. 查字段列表
				    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
				    session.setAttribute("tableList", aiTables);
				    session.setAttribute("tableColumnsList", tableColumns);
					TableDefinition TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));

/*
0		UPDATE_DATE timestamp(6)
1		yyyymmdd_count bigint
2		user_id varchar(45)
3		activation_code varchar(45)
4		status varchar(45)
5		file_name varchar(45)
6		col_name_0 int
7		col_name_1 int
・・・・・・
*/
					//アップロードするフォルダ
					String path = getServletContext().getRealPath("/fileData/AI_T_" + def.tableName);
					File directory = new File(path);
					if (!directory.exists()) {
						//mkdir
						boolean hasSucceeded = directory.mkdir();
						logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);
					}
					String file_name = TableDefinition.columns.get(5).value;
					file_name = FuncUtils.filesUp_ai(req, path, yyyymmdd_count, file_name);


					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
					String form_CompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
				    session.setAttribute("form_CompanyName_Chinese", form_CompanyName_Chinese);

					//TODO
//					TableServiceDao.DELETE_where_activation_code(activation_code, table_name);
					TableServiceDao.updateDynamic(yyyymmdd_count, user_id, activation_code, status, file_name, def);

					t_etax_account_infoBean.setCompanyName_Chinese(form_CompanyName_Chinese);
					t_etax_account_infoBean.setEmail(form_mailarea);

					//登录信息发邮件给客户
					FuncUtils.sendMail_ai(t_etax_account_infoBean, yyyymmdd_count, user_id, status, def);
					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}


			} else if ("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == false && "houtai_queren".equals(status)) {
				if("admin".equals(User_infoBean.getPermissions())) {
				} else {
					logger.debug("PandaServiceMA → License invalid");
					out.write("PandaServiceMA → License invalid");
					logger.info("end " + hidden_key);
					return;

				}
				try {

					String queren_jieguo = req.getParameter("queren_jieguo");
					String statusNEW = "";

					if ("button-queren-ok".equals(queren_jieguo)) {
						statusNEW = status + "_ok";

					} else if ("button-queren-ng".equals(queren_jieguo)) {
						statusNEW = status + "_ng";
						status = "jiben_qingbao";

					} else {
						resp.getWriter().write("登錄失败: queren_jieguo " + null);
						logger.info("end");
						return;
					}


					TableServiceDao.updateDynamic_status(yyyymmdd_count, user_id, activation_code, status, def);



					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				    session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);

					t_etax_account_infoBean.setCompanyName_Chinese(t_etax_account_infoExBean.getCompanyName_Chinese());
					t_etax_account_infoBean.setEmail(form_mailarea);

					//登录信息发邮件给客户
					FuncUtils.sendMail_ai(t_etax_account_infoBean, yyyymmdd_count, user_id, statusNEW, def);
					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}

			} else if ("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == false && "queren_shu".equals(status)) {
				try {


				    // 1. 查表列表
				    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
				    // 2. 查字段列表
				    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
				    session.setAttribute("tableList", aiTables);
				    session.setAttribute("tableColumnsList", tableColumns);
					TableDefinition TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));

/*
0		UPDATE_DATE timestamp(6)
1		yyyymmdd_count bigint
2		user_id varchar(45)
3		activation_code varchar(45)
4		status varchar(45)
5		file_name varchar(45)
6		col_name_0 int
7		col_name_1 int
・・・・・・
*/
					//アップロードするフォルダ
					String path = getServletContext().getRealPath("/fileData/AI_T_" + def.tableName);
					File directory = new File(path);
					if (!directory.exists()) {
						//mkdir
						boolean hasSucceeded = directory.mkdir();
						logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);
					}
					String file_name = TableDefinition.columns.get(5).value;
					FuncUtils.filesUp_ai(req, path, yyyymmdd_count, file_name);

					TableServiceDao.updateDynamic_status(yyyymmdd_count, user_id, activation_code, status, def);

					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}


			} else if ("denglu".equals(hidden_key) && StringUtils.isEmpty(activation_code) == false && status.contains("houtai_queren_zuizhong")) {
				if("admin".equals(User_infoBean.getPermissions())) {
				} else {
					logger.debug("PandaServiceMA → License invalid");
					out.write("PandaServiceMA → License invalid");
					logger.info("end " + hidden_key);
					return;

				}
				try {


					//激活
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					try {
						t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
						EtaxDao EtaxDao = new EtaxDao();
						t_etax_account_infoBean EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
								t_etax_account_resDao, EtaxDao, "");
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

					} catch (SQLException e) {
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

					}

				    // 1. 查表列表
				    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
				    // 2. 查字段列表
				    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
				    session.setAttribute("tableList", aiTables);
				    session.setAttribute("tableColumnsList", tableColumns);
					TableDefinition TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));


//					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.selectAI(t_etax_account_infoBean);

					// 基本情報没有找到的时候，用既存的
					if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == true) {

					} else {
						yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();
						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);

						if ("jiben_qingbao0".equals(TableDefinition.columns.get(4).value)) {
							TableServiceDao.updateDynamic_status(yyyymmdd_count, user_id, activation_code, "jiben_qingbao", def);
						}

					}

					if (!"queren_shu".equals(TableDefinition.columns.get(4).value)) {
						//アップロードするフォルダ
						String path = getServletContext().getRealPath("/fileData/AI_T_" + def.tableName);
						File directory = new File(path);
						if (!directory.exists()) {
							//mkdir
							boolean hasSucceeded = directory.mkdir();
							logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);
						}
						String file_name = TableDefinition.columns.get(5).value;
						FuncUtils.filesUp_ai(req, path, yyyymmdd_count, file_name);

						t_etax_account_infoDao.Update_user_info_AI(yyyymmdd_count, t_etax_account_infoBean);

					}

					TableServiceDao.updateDynamic(yyyymmdd_count, user_id, activation_code, def);

					if ("houtai_queren_zuizhong_denglu".equals(status)) {

					} else {
						TableServiceDao.updateDynamic_status(yyyymmdd_count, user_id, activation_code, status, def);

						//登录信息发邮件给客户
						FuncUtils.sendMail_ai(t_etax_account_infoBean, yyyymmdd_count, user_id, status, def);

					}
					resp.getWriter().write("登錄成功！");
				} catch (Exception e) {
					e.printStackTrace();
					resp.getWriter().write("登錄失败: " + e.getMessage());
				}



			} else if ("get_file".equals(hidden_key) && "xtx下载".equals(hidden_value)) {

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", PDSK, " + PDSK);

				try {


				    // 1. 查表列表
				    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
				    // 2. 查字段列表
				    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
				    session.setAttribute("tableList", aiTables);
				    session.setAttribute("tableColumnsList", tableColumns);
					TableDefinition TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));

					m_ai_guanli_yewubiaoDao m_ai_guanli_yewubiaoDao = new m_ai_guanli_yewubiaoDao();
	            	LinkedHashMap<String, String> m_ai_guanli_yewubiaoBean_LinkedHashMap= m_ai_guanli_yewubiaoDao.select("AI_T_" + TableDefinition.tableName);

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(TableDefinition.columns.get(1).value);

					//TODO
					PDSK = t_etax_account_infoExBean.getCompanyName_Chinese();

			    	for (Map.Entry<String, String> entry : m_ai_guanli_yewubiaoBean_LinkedHashMap.entrySet()) {
			    	    String key = entry.getKey();
			    	    String value = entry.getValue();

			    	    List<String> columns_chk_etax = new ArrayList<>();
			    	    columns_chk_etax.add(key);
			    	    TableDefinition.columns_chk_etax = columns_chk_etax;

			    	    if ("shiyong".equals(value)) {
			    	    	String path = FuncUtilsAiEtax.getXtx_AI(req, PDSK, TableDefinition, key, t_etax_account_infoExBean);

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

									msg = "{\"res\":\"" + file.getName() + ".zip" + "\"}";
									session.setAttribute("msg", msg);out.print(msg);

								} catch (IOException e) {
									e.printStackTrace();


								}

							} else {

								msg = "{\"res\":\"结果文件不存在\"}";
								session.setAttribute("msg", msg);out.print(msg);
								logger.info("end " + hidden_key);
								return;
							}

			    	    }
			    	}




				} catch (Exception e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();

					msg = "{\"res\":\""+e1+"\"}";
					session.setAttribute("msg", msg);out.print(msg);
					logger.info("end " + hidden_key);
					return;
				}



				logger.info("end " + hidden_value);
				return;





			} else if ("get_file".equals(hidden_key) && "附件下载".equals(hidden_value)) {
					/*
					 * 申告上传文件下载
					 */


			    // 1. 查表列表
			    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
			    // 2. 查字段列表
			    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
			    session.setAttribute("tableList", aiTables);
			    session.setAttribute("tableColumnsList", tableColumns);
				TableDefinition TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));

/*
0		UPDATE_DATE timestamp(6)
1		yyyymmdd_count bigint
2		user_id varchar(45)
3		activation_code varchar(45)
4		status varchar(45)
5		file_name varchar(45)
6		col_name_0 int
7		col_name_1 int
・・・・・・
*/
				String file_name = TableDefinition.columns.get(5).value;

				if (StringUtils.isEmpty(file_name) == true) {
					msg = "{\"res\":\"结果文件不存在\"}";
					logger.debug(msg);
					session.setAttribute("msg", msg);
					out.print(msg);
					logger.info("end " + hidden_key);
					return;
				}


				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(TableDefinition.columns.get(1).value);

				String form_CompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

				String path = getServletContext().getRealPath("/fileData/AI_T_" + def.tableName);
				path = path + "/" + file_name;

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

						msg = "{\"res\":\"" + file_name + ".zip" + "\"}";
						session.setAttribute("msg", msg);
						out.print(msg);

					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {

					msg = "{\"res\":\"结果文件不存在\"}";
					session.setAttribute("msg", msg);
					out.print(msg);
					logger.info("end " + hidden_key);
					return;
				}



			} else {





			    // 1. 查表列表
			    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();
			    // 2. 查字段列表
			    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
			    session.setAttribute("tableList", aiTables);
			    session.setAttribute("tableColumnsList", tableColumns);

			    TableDefinition TableDefinition = new TableDefinition();
				if (StringUtils.isEmpty(table_name) == false) {
					TableDefinition = TableServiceDao.selectDynamic(activation_code, FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, def));
				}

				//TODO
//				set_AI_zidong_jisuan(TableDefinition);



				session.setAttribute("TableDefinition", TableDefinition);
//					session.setAttribute("TableDefinition_dateList", TableDefinition_dateList.get(0).columns);



				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
			    session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);

			    Map<String, String> XMLCalculator_roundingRules = (new XMLCalculator()).roundingRules;
			    session.setAttribute("XMLCalculator_roundingRules", XMLCalculator_roundingRules);


				// 1. 设置属性，不拼接在 URL 上
				req.setAttribute("fromBackend", true);

				// 2. Forward 到 JSP，不带参数
				req.getRequestDispatcher("/AiSetYewuShuju.jsp").forward(req, resp);

//			    req.getRequestDispatcher("/AiSetYewuShuju.jsp?fromBackend=true").forward(req, resp);


			}

			logger.info("end");
			return;

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

    }

	private void set_AI_zidong_jisuan(TableDefinition tableDefinition) {
		if (tableDefinition.columns == null) {
			return;
		}
		for (ColumnDefinition col : tableDefinition.columns) {
		    String[] comments = col.comment.split("\\#,\\#");  // 注意在Java中要转义$
		    String comment = "";
		    String col_comment_key = "";
		    String col_comment_AI_zidong_jisuan = "";

		    if (comments.length > 0) {
		        comment = comments[0];
		    }
		    if (comments.length > 1) {
		        col_comment_key = comments[1];
		    }
		    if (comments.length > 2) {
		        col_comment_AI_zidong_jisuan = comments[2];
		    }

		    String xmlContent = FuncUtilsAiEtax.convertColumnsToXml(tableDefinition.columns);

			StringBuffer loggerStringBuffer = new StringBuffer();
		    if (col_comment_AI_zidong_jisuan != null && !col_comment_AI_zidong_jisuan.isEmpty()) {
	            String line = col_comment_AI_zidong_jisuan.trim(); // 去除行首尾的空白

				String logger_info = "[表说明: " + tableDefinition.tableName_comment + "][字段名: " + (col.name) + "][字段说明: " + comment + "][AI计算公式: \n" + col_comment_AI_zidong_jisuan + "]";

            	try {
            		Object obj = XMLCalculator.calculate(line, xmlContent, logger_info, loggerStringBuffer);
            		String result = String.valueOf(obj);
					col.value = result;


				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
		    }
		}


	}


}

