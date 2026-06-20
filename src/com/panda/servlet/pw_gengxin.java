package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.t_etax_account_infoDao;

@WebServlet("/pw_gengxin")
public class pw_gengxin extends HttpServlet {

	private static Logger logger = Logger.getLogger(pw_gengxin.class.toString());

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/plain");

		// 返却値に設定
		response.setContentType("text/html; charset=UTF-8");
		//解决前端跨域
		response.setHeader("Access-Control-Allow-Origin", "*");

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

		// 获取参数
		String type = request.getParameter("type");
		String yyyymmdd_count = request.getParameter("yyyymmdd_count");

		logger.info("type " + type);
		if ("GET".equals(type)) {

			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.selectWhere_etax_pw_activation();
			PrintWriter out = response.getWriter();
			out.print("{"
					+ "  \"bangou\":\"" + t_etax_account_infoExBean.getBangou() + "\""
					+ ", \"bangouPW\":\"" + t_etax_account_infoExBean.getEtax_pw() + "\""
					+ ", \"yyyymmdd_count\":\"" + t_etax_account_infoExBean.getYyyymmdd_count() + "\""
					+ ", \"user_type\":\"" + t_etax_account_infoExBean.getUser_type() + "\""
					+ "}");

		} else if ("Update_etax_pw_flag".equals(type)) {

			logger.info("yyyymmdd_count " + yyyymmdd_count);
			t_etax_account_infoDao.Update_key_value(yyyymmdd_count, "etax_pw_flag", "0");

		}

	}
}