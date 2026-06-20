package com.panda.servlet.ai;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;


@WebServlet("/AiSetYewuJinduLogic")
public class AiSetYewuJinduLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(AiSetYewuJinduLogic.class.toString());

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

		if (StringUtils.isEmpty(user_id) == true) {
			user_id = (String) session.getAttribute("license");
		}

		String table_name = req.getParameter("table_name");
		if (StringUtils.isEmpty(table_name) == true) {
		} else {

			if (!table_name.startsWith("AI_T_")) {
				table_name = "AI_T_" + table_name;
			}
		}
		String form_shouxu = req.getParameter("form_shouxu");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("User_infoBean", new User_infoBean());
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);


		String currentUrl = req.getRequestURL().toString();
		URL url = new URL(currentUrl);
		String domain = url.getHost();
		String ProjectName ="www.pandaservicejapan.com";

		if (domain.contains("127.0.0.1")) {
			ProjectName ="/PandaServiceMA";
		}
		if (currentUrl.split(ProjectName).length == 2) {
			session.setAttribute("redirect", currentUrl.split(ProjectName)[1]);

		} else {
			session.setAttribute("redirect", "");

		}
		String maxNo = req.getParameter("maxNo");

		/*
		 * 邀请码有效性验证
		 */


		/*
		 * license確認
		 */
		if (StringUtils.isEmpty(user_id) == true) {
			logger.debug("PandaServiceTools → License invalid");
//			out.write("PandaServiceMA → License invalid");
			resp.sendRedirect(ProjectName + "/LoginServletLogic");
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
			if (FunctionUtils.getLicensebooleanPW(pw, this.getServletName(), User_infoBean) == false) {
				logger.debug("PandaServiceTools → License invalid");
				out.write("PandaServiceMA → License invalid");
//				resp.sendRedirect(ProjectName + "/LoginServletLogic");
				logger.info("end " + hidden_key);
				return;
			}
		}




		try {
			String err_msg = "";
	    	TableServiceDao TableServiceDao = new TableServiceDao();
			if ("admin".equals(User_infoBean.getPermissions())) {


			    // 1. 查表列表
			    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();

			    // 2. 查字段列表
			    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();


				if (StringUtils.isEmpty(table_name) == true) {

					// 获取第一个 key
					String firstKey = aiTables.keySet().iterator().next();
					// 根据 key 获取对应的 value
					LinkedHashMap<String, String> aiTable = aiTables.get(firstKey);
					table_name = aiTable.get("name");
				} else {

				}

					List<TableDefinition> TableDefinition_dateList = null;
					TableDefinition_dateList = TableServiceDao.selectDynamicListAll(FuncUtils.buildTableDefinitionByName(table_name, aiTables, tableColumns, null), maxNo);

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

					LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao
							.selectAll_Company_AI(table_name, User_infoBean, null);
					session.setAttribute("LinkedHashMap_t_etax_account_infoBean", LinkedHashMap_t_etax_account_infoBean);

					session.setAttribute("table_name", table_name);
					session.setAttribute("tableList", aiTables);
					session.setAttribute("tableColumnsList", tableColumns);
					session.setAttribute("TableDefinition_dateList", TableDefinition_dateList);

					req.getRequestDispatcher("/AiSetYewuJindu.jsp?fromBackend=true").forward(req, resp);

			}

			logger.info("end");
			return;
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

    }


}

