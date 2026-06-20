package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

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
import com.panda.dao.User_infoDao;
import com.panda.utils.FuncUtils;

@WebServlet("/URLlistLogic")
@MultipartConfig
public class URLlistLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(URLlistLogic.class.toString());

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
		String user_id = req.getParameter("license");

		if (StringUtils.isEmpty(user_id) == true) {
			user_id = (String) session.getAttribute("license");
		}

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		String hidden_key = req.getParameter("hidden_key");
		String maxNo = req.getParameter("maxNo");

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
//			resp.sendRedirect(ProjectName + "/LoginServletLogic");
			return;
		}


		req.getRequestDispatcher("/URLlist.jsp").forward(req, resp);
		logger.debug("end");

		return;

	}
}