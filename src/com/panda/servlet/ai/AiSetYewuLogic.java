package com.panda.servlet.ai;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.panda.bean.User_infoBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_ai_guanli_yewubiaoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.XMLCalculator;


@WebServlet("/AiSetYewuLogic")
public class AiSetYewuLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(AiSetYewuLogic.class.toString());

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


		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");
		String form_shouxu = req.getParameter("form_shouxu");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("User_infoBean", new User_infoBean());
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);



		/*
		 * 邀请码有效性验证
		 */


		/*
		 * license確認
		 */
		if (StringUtils.isEmpty(user_id) == true) {
			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");
			logger.info("end " + hidden_key);
			return;

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

		if (!"admin".equals(User_infoBean.getPermissions())) {
			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");
			logger.info("end " + hidden_key);
			return;

		}



		try {
			String err_msg = "";
	    	TableServiceDao TableServiceDao = new TableServiceDao();
			m_ai_guanli_yewubiaoDao m_ai_guanli_yewubiaoDao = new m_ai_guanli_yewubiaoDao();
			if ("setTable".equals(hidden_key)) {

				 Gson gson = new Gson();
			        TableDefinition def = gson.fromJson(req.getReader(), TableDefinition.class);

			        try {

//			        	TableServiceDao.dropTrigger(def);
			        	TableServiceDao.createTableWithHistory(def);
			        	TableServiceDao.createTrigger(def);


		            	String biaoming_new = "biaoming_new";
			            for (String col : def.columns_chk_etax) {
			            	String[] cols = col.split("\\#\\,\\#");
			            	String biaoming = cols[0];

			            	if (biaoming.contains(def.tableName)) {
			            		biaoming_new = def.tableName;
		            			continue;

			            	}
			            }

			            // 循环遍历列表
			            for (String col : def.columns_chk_etax) {
			            	String[] cols = col.split("\\#\\,\\#");
			            	String biaoming = cols[0];
			            	String etax_shiyangshu = cols[1];
			            	String shiyong = "true".equals(cols[2]) ? "shiyong" : "shiyong_no";

			            	if ("新建表".equals(biaoming)) {
				            	if ("biaoming_new".equals(biaoming_new)) {

				            	} else {
			            			continue;
				            	}

			            	} else {
			            		if (!def.tableName.equals(biaoming)) {
			            			continue;
			            		}

			            	}

			            	if ("biaoming_new".equals(biaoming_new)) {
			            		biaoming = def.tableName;
			            	}

			            	LinkedHashMap<String, String> m_ai_guanli_yewubiaoBean_LinkedHashMap= m_ai_guanli_yewubiaoDao.select("AI_T_" + biaoming, etax_shiyangshu);
							if (m_ai_guanli_yewubiaoBean_LinkedHashMap.size() == 0) {
								m_ai_guanli_yewubiaoDao.insert(user_id, "AI_T_" + biaoming, etax_shiyangshu, shiyong);

							} else {
								m_ai_guanli_yewubiaoDao.update(user_id, "AI_T_" + biaoming, etax_shiyangshu, shiyong);

							}
			            }

			            resp.getWriter().write("创建成功:業務表，業務履历表，觸發器");
			        } catch (Exception e) {
			            e.printStackTrace();
			            resp.getWriter().write("创建失败: " + e.getMessage());
			        }

				} else {
//					LinkedHashMap<String, String> All_guanli_yewubiao_LinkedHashMap = m_ai_guanli_yewubiaoDao.selectAll_guanli_yewubiao();
					LinkedHashMap<String, LinkedHashMap<String, String>> All_guanli_yewubiao_LinkedHashMap = m_ai_guanli_yewubiaoDao.selectAll();
					session.setAttribute("All_guanli_yewubiao_LinkedHashMap", All_guanli_yewubiao_LinkedHashMap);

					session.setAttribute("tableList", TableServiceDao.getAiTables());
					session.setAttribute("tableColumnsList", TableServiceDao.getTableColumns());


				    Map<String, String> XMLCalculator_roundingRules = (new XMLCalculator()).roundingRules;
				    session.setAttribute("XMLCalculator_roundingRules", XMLCalculator_roundingRules);

					req.getRequestDispatcher("/AiSetYewu.jsp?fromBackend=true").forward(req, resp);
					logger.info("end " + hidden_key);
					return;

				}

			logger.info("end " + hidden_key);
			return;
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

    }


}

/*


TABLE_NAME	COLUMN_NAME	COLUMN_TYPE	IS_NULLABLE	comment_quoted
AI_T_JCT_SHENQING_250907	UPDATE_DATE	timestamp(6)	NO
AI_T_JCT_SHENQING_250907	yyyymmdd_count	bigint	NO
AI_T_JCT_SHENQING_250907	user_id	varchar(45)	NO
AI_T_JCT_SHENQING_250907	activation_code	varchar(45)	NO
AI_T_JCT_SHENQING_250907	status	varchar(45)	NO
AI_T_JCT_SHENQING_250907	file_name	varchar(128)	YES
AI_T_JCT_SHENQING_250907	col_name_0	varchar(512)	YES	用户类型#,##单选,公司,个人（非公司）##,#只有在您的营业执照上明确出现【公司】、【Company】等字样时才是公司，其他情况请选择【个人（非公司）】#,#
AI_T_JCT_SHENQING_250907	col_name_1	varchar(512)	YES	公司名称或本人姓名（所在地区文字）#,##,#如是公司，请与营业执照内容完全一致。如非公司，请写身份证件上的人名。#,#
AI_T_JCT_SHENQING_250907	col_name_2	varchar(512)	YES	公司名称或本人姓名（英文）#,##,#如是公司，写法由各公司自行决定。内容请与中文版保持统一。不可缺漏增改。如非公司，请写人名的拼音字母。#,#
AI_T_JCT_SHENQING_250907	col_name_3	varchar(512)	YES	公司名称或本人姓名（日文片假名）#,##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_SHENQING_250907	col_name_4	varchar(512)	YES	公司地址或本人住址（所在地区文字）#,##,#如是公司，请与营业执照内容完全一致。如非公司，请写身份证件上的地址。#,#
AI_T_JCT_SHENQING_250907	col_name_5	varchar(512)	YES	公司地址或本人住址（英文）#,##,#如是公司，写法由各公司自行决定。内容请与中文版保持统一。不可缺漏增改。如非公司，请写身份证件上的地址对应的英文。#,#
AI_T_JCT_SHENQING_250907	col_name_6	varchar(512)	YES	公司地址或本人住址（日文片假名）#,##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_SHENQING_250907	col_name_7	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（所在地区文字）#,##,#如是公司，请与营业执照内容完全一致。如非公司且有工商个体户执照，可以写营业场所名称。如【XX商店】【XX经营部】等。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_SHENQING_250907	col_name_8	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（英文）#,##,#如是公司，请写代表人身份证件上人名的拼音字母。如非公司且有工商个体户执照，可以写营业场所名称的英文翻译。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_SHENQING_250907	col_name_9	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（日文片假名）#,##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_SHENQING_250907	col_name_10	varchar(512)	YES	公司代表人住址或个体户经营场所地址（所在地区文字）#,##,#如是公司，请与代表人身份证件内容完全一致。如非公司且有工商个体户执照，可以写营业场所地址。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_SHENQING_250907	col_name_11	varchar(512)	YES	公司代表人住址或个体户经营场所地址（英文）#,##,#如是公司，请与代表人身份证件内容的英文翻译。如非公司且有工商个体户执照，可以写营业场所地址的英文翻译。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_SHENQING_250907	col_name_12	varchar(512)	YES	公司代表人住址或个体户经营场所地址（日文片假名）#,##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_SHENQING_250907	col_name_13	int	YES	电话第一段（国家码）#,##,#不多于4位，不可写加号或其他符号。中国电话国家码为0086#,#
AI_T_JCT_SHENQING_250907	col_name_14	int	YES	电话第二段#,##,#不多于4位，不可写加号或其他符号#,#
AI_T_JCT_SHENQING_250907	col_name_15	int	YES	电话第三段#,##,#不多于4位，不可写加号或其他符号#,#
AI_T_JCT_SHENQING_250907	col_name_16	int	YES	电话第四段#,##,#不多于4位，不可写加号或其他符号#,#
AI_T_JCT_SHENQING_250907	col_name_17	int	YES	公司注册资本金（请换算为日元）#,##,#如是公司，请与营业执照内容一致，并换算为日元，汇率使用本年度初日汇率。如非公司，无需填写。#,#
AI_T_JCT_SHENQING_250907	col_name_18	int	YES	公司成立年或本人出生年#,##,#如是公司，请与营业执照内容一致。如非公司，请填写个人的出生年。#,#
AI_T_JCT_SHENQING_250907	col_name_19	int	YES	公司成立月或本人出生月#,##,#如是公司，请与营业执照内容一致。如非公司，请填写个人的出生月。#,#
AI_T_JCT_SHENQING_250907	col_name_20	int	YES	公司成立日或本人出生日#,##,#如是公司，请与营业执照内容一致。如非公司，请填写个人的出生日。#,#
AI_T_JCT_SHENQING_250907	col_name_21	int	YES	在日本开始事业的年#,##,#时间不必绝对精确。相对准确，可以反映事实情况即可。原则上税号不发给在日本没有经营事业的主体，所以不得晚于税号申请日。#,#
AI_T_JCT_SHENQING_250907	col_name_22	int	YES	在日本开始事业的月#,##,#时间不必绝对精确。相对准确，可以反映事实情况即可。原则上税号不发给在日本没有经营事业的主体，所以不得晚于税号申请日。#,#
AI_T_JCT_SHENQING_250907	col_name_23	int	YES	在日本开始事业的日#,##,#时间不必绝对精确。相对准确，可以反映事实情况即可。原则上税号不发给在日本没有经营事业的主体，所以不得晚于税号申请日。#,#
AI_T_JCT_SHENQING_250907	col_name_24	int	YES	基准期间在日本的课税销售额（含税日元金额）#,##,#以今年作为X年
A. （X）年在日本开始事业的，没有基准期间
B. （X-1）年在日本开始事业的，没有基准期间
C. 其他情况，基准期间是（X-2）年1月1日至（X-2）年12月31日#,#
AI_T_JCT_SHENQING_250907	col_name_25	int	YES	特定期间在日本的课税销售额（含税日元金额）#,##,#以今年作为X年
A. （X）年在日本开始事业的，没有特定期间
B. （X-1）年（1～4）月在日本开始事业的，特定期间是开始事业后的6个月
C. （X-1）年（5）月在日本开始事业的，特定期间是开始事业日至同年10月31日
D. （X-1）年（6～12）月在日本开始事业的，没有特定期间
E. 其他情况，特定期间是（X-2）年1月1日至（X-2）年6月30日#,#
AI_T_JCT_ZHUANDAILI_20250907	UPDATE_DATE	timestamp(6)	NO
AI_T_JCT_ZHUANDAILI_20250907	yyyymmdd_count	bigint	NO
AI_T_JCT_ZHUANDAILI_20250907	user_id	varchar(45)	NO
AI_T_JCT_ZHUANDAILI_20250907	activation_code	varchar(45)	NO
AI_T_JCT_ZHUANDAILI_20250907	status	varchar(45)	NO
AI_T_JCT_ZHUANDAILI_20250907	file_name	varchar(128)	YES
AI_T_JCT_ZHUANDAILI_20250907	col_name_0	int	YES	ETAX账号#,##客户不显示##,##,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_1	varchar(512)	YES	ETAX密码#,##客户不显示##,##,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_2	int	YES	日本消费税税号或法人番号#,##确认书显示##,#请填写除英文字母T之外的13位数字#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_3	varchar(512)	YES	用户类型#,##客户不显示##,#根据税号的API直接获得，不让客户填写。公司或非公司。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_4	varchar(512)	YES	公司名称或本人姓名（所在地区文字）#,##确认书显示##,#如是公司，请与营业执照内容完全一致。如非公司，请写身份证件上的人名。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_5	varchar(512)	YES	公司名称或本人姓名（英文）#,##确认书显示##,#如是公示信息，请与网上的公示信息完全一致#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_6	varchar(512)	YES	公司名称或本人姓名（日文片假名）#,##确认书显示##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_7	varchar(512)	YES	公司地址或本人住址（所在地区文字）#,##确认书显示##,#如是公司，请与营业执照内容完全一致。如非公司，请写身份证件上的人名。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_8	varchar(512)	YES	公司地址或本人住址（英文）#,##确认书显示##,#如是公示信息，请与网上的公示信息完全一致#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_9	varchar(512)	YES	公司地址或本人住址（日文片假名）#,##确认书显示##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_10	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（所在地区文字）#,##确认书显示##,#如是公司，请与营业执照内容完全一致。如非公司且有工商个体户执照，可以写营业场所名称。如【XX商店】【XX经营部】等。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_11	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（英文）#,##确认书显示##,#如是公示信息，请与网上的公示信息完全一致#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_12	varchar(512)	YES	公司代表人姓名或个体户经营场所名称（日文片假名）#,##确认书显示##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_13	varchar(512)	YES	公司代表人住址或个体户经营场所地址（所在地区文字）#,##确认书显示##,#如是公司，请与代表人身份证件内容完全一致。如非公司且有工商个体户执照，可以写营业场所地址。如非公司但没有工商个体户执照，不要填写。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_14	varchar(512)	YES	公司代表人住址或个体户经营场所地址（英文）#,##确认书显示##,#如是公示信息，请与网上的公示信息完全一致#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_15	varchar(512)	YES	公司代表人住址或个体户经营场所地址（日文片假名）#,##确认书显示##,#可不填写。若不填写，视为本人对读法无任何意见，全权委托经办方指定日文片假名读音。#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_16	int	YES	公司成立年或本人出生年#,##确认书显示##,#请填写4位数字#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_17	int	YES	公司成立月或本人出生月#,##确认书显示##,#请填写2位数字，不足2位以0补足#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_18	int	YES	公司成立日或本人出生日#,##确认书显示##,#请填写2位数字，不足2位以0补足#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_19	varchar(512)	YES	上任纳税管理人公司名称#,##确认书显示##,#如果知道，就写；不知道，留空即可#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_20	varchar(512)	YES	上任纳税管理人公司代表人姓名#,##确认书显示##,#如果知道，就写；不知道，留空即可#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_21	varchar(512)	YES	上任纳税管理人公司地址#,##确认书显示##,#如果知道，就写；不知道，留空即可#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_22	varchar(512)	YES	上任纳税地#,##确认书显示##,#如果知道，就写；不知道，留空即可#,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_23	varchar(512)	YES	上任管辖税务署#,##客户不显示##,##,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_24	varchar(512)	YES	上任管辖税务署番号#,##客户不显示##,##,#
AI_T_JCT_ZHUANDAILI_20250907	col_name_25	varchar(512)	YES	添付文件#,##输入框不显示#、#确认书显示##,#请添付可以辅助确认上任纳税管理人的文件。包括但不限于下号通知单；申告书；纳税管理人指定文件等。#,#
AI_T_JCT_shenqing	UPDATE_DATE	timestamp(6)	NO
AI_T_JCT_shenqing	yyyymmdd_count	bigint	NO
AI_T_JCT_shenqing	user_id	varchar(45)	NO
AI_T_JCT_shenqing	activation_code	varchar(45)	NO
AI_T_JCT_shenqing	status	varchar(45)	NO
AI_T_JCT_shenqing	file_name	varchar(128)	YES
AI_T_JCT_shenqing	col_name_0	varchar(512)	YES	【该主体是第一年在日本开始事业吗】#,##单选,是,否##,##,#
AI_T_JCT_shenqing	col_name_1	varchar(512)	YES	【该主体此刻是课税的还是免税的】#,##单选,课税,免税##,##,#
AI_T_JCT_shenqing	col_name_2	varchar(512)	YES	确认书显示#,##确认书显示##,##,#
AI_T_JCT_shenqing	col_name_3	varchar(512)	YES	客户不显示#,##客户不显示##,##,#
AI_T_test	UPDATE_DATE	timestamp(6)	NO
AI_T_test	yyyymmdd_count	bigint	NO
AI_T_test	user_id	varchar(45)	NO
AI_T_test	activation_code	varchar(45)	NO
AI_T_test	status	varchar(45)	NO
AI_T_test	file_name	varchar(128)	YES
AI_T_test	col_name_0	int	YES	必须#,##,##,#
AI_T_test	col_name_1	int	NO	可空#,##,##,#
AI_T_test2	UPDATE_DATE	timestamp(6)	NO
AI_T_test2	yyyymmdd_count	bigint	NO
AI_T_test2	user_id	varchar(45)	NO
AI_T_test2	activation_code	varchar(45)	NO
AI_T_test2	status	varchar(45)	NO
AI_T_test2	file_name	varchar(128)	YES
AI_T_test2	col_name_0	int	YES	test2#,##,##,#
AI_T_test3	UPDATE_DATE	timestamp(6)	NO
AI_T_test3	yyyymmdd_count	bigint	NO
AI_T_test3	user_id	varchar(45)	NO
AI_T_test3	activation_code	varchar(45)	NO
AI_T_test3	status	varchar(45)	NO
AI_T_test3	file_name	varchar(128)	YES
AI_T_test3	col_name_0	int	YES	test3#,##,##,#
AI_T_xiaofeishui_shengao	UPDATE_DATE	timestamp(6)	NO
AI_T_xiaofeishui_shengao	yyyymmdd_count	bigint	NO
AI_T_xiaofeishui_shengao	user_id	varchar(45)	NO
AI_T_xiaofeishui_shengao	activation_code	varchar(45)	NO
AI_T_xiaofeishui_shengao	status	varchar(45)	NO
AI_T_xiaofeishui_shengao	file_name	varchar(128)	YES
AI_T_xiaofeishui_shengao	col_name_0	int	NO	【本申告主体在基准期间的日本课税销售额】#,##输入框不可1编辑##,#(2022年1月1日～2022年12月31日)#,#
AI_T_xiaofeishui_shengao	col_name_1	int	NO	【本申告主体在特定期间的日本课税销售额】#,##输入框不显示##,##,#
AI_T_xiaofeishui_shengao	col_name_2	int	YES	【本申告主体在上一会计年度的日本课税销售额】#,##客户不显示##,##,#
AI_T_xiaofeishui_shengao	col_name_3	varchar(512)	YES	【去年是否申告过消费税】#,##单选》是》否##,##,#
AI_T_xiaofeishui_shengao	col_name_4	varchar(512)	YES	【去年消费税申告书的⑨差引税額】#,##数字##,##,#
AI_T_xiaofeishui_shengao	col_name_5	varchar(512)	YES	【本申告主体在该会计年度计算消费税时采用】#,##单选》简易课税（零售业）》原则课税》原则课税（2割特例）##,##,#
AI_T_xiaofeishui_shengao	col_name_6	varchar(512)	YES	客户不显示#,##客户不显示##,##,#
AI_T_xiaofeishui_shengao	col_name_7	varchar(512)	YES	【YYYYMMDD】#,##非必须输入##正则表达式》请输入正确的日期格式：YYYYMMDD》^\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])$##,#西洋纪年#,#
AI_T_xiaofeishui_shengao	col_name_8	varchar(512)	YES	【日本纪年xtx】#,##,##,##【日本纪年xtx】，【YYYYMMDD】#
AI_T_xiaofeishui_shengao	col_name_9	varchar(512)	YES	【千円未満切り捨て】#,##,##,##【千円未満切り捨て】，【YYYYMMDD】#
AI_T_xiaofeishui_shengao	col_name_10	varchar(512)	YES	【纳税管理公司名】#,##默认输入》ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社##,##,#







*/