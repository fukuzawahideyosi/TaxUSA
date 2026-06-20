package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

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
import com.panda.bean.t_etax_amazon_csvExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_amazon_csvDao;
import com.panda.utils.FuncUtils;

@WebServlet("/SearchAmazonRankingLogic")
@MultipartConfig
public class SearchAmazonRankingLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SearchAmazonRankingLogic.class.toString());

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
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();


		String hidden_key = req.getParameter("hidden_key");
		String maxNo = req.getParameter("maxNo");


		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);

		/*
		 * license確認
		 */
		String pw = req.getParameter("pw");
		session.setAttribute("pw", pw);
//		PrintWriter out = resp.getWriter();
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
			return;
		}



		try {

			t_etax_amazon_csvDao t_etax_amazon_csvDao = new t_etax_amazon_csvDao();

			Map<String, String[]> HashMapParameterMap = req.getParameterMap();
			for (String key : HashMapParameterMap.keySet()) {
				if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
					continue;
				}


			}

			LinkedHashMap<String, User_infoBean> HashMapGroup_id_user_id = new LinkedHashMap<String, User_infoBean>();
			if (StringUtils.isEmpty(User_infoBean.getGroup_id()) == false) {
				User_infoDao User_infoDao = new User_infoDao();
				HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(User_infoBean.getGroup_id());
				User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

			} else if ("admin".equals(User_infoBean.getPermissions())) {
				User_infoDao User_infoDao = new User_infoDao();
				HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(null);
				User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

			} else {
				HashMapGroup_id_user_id.put(User_infoBean.getUser_id(), User_infoBean);
				User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

			}

			LinkedHashMap<String, t_etax_amazon_csvExBean> LinkedHashMap_t_etax_amazon_csvExBean = t_etax_amazon_csvDao
					.selectAll("2023", User_infoBean, maxNo);
			session.setAttribute("LinkedHashMapt_etax_account_infoBean", LinkedHashMap_t_etax_amazon_csvExBean);


			if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
//				LinkedHashMap<String, LinkedHashMap<String, String>> LinkedHashMapTongji = t_etax_account_infoDao.selectTongji(User_infoBean);
//				session.setAttribute("LinkedHashMapTongji", LinkedHashMapTongji);
			}



			req.getRequestDispatcher("/SearchAmazonRanking.jsp").forward(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}



		logger.debug("end");

		return;

	}

}