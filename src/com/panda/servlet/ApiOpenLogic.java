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

import org.apache.log4j.Logger;

import com.panda.bean.m_nianmo_tiaozheng_geiyu_kongchuBean;
import com.panda.dao.m_nianmo_tiaozheng_geiyu_kongchuDao;
import com.panda.utils.FuncUtils;

@WebServlet("/ApiOpenLogic")
@MultipartConfig
public class ApiOpenLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(ApiOpenLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		logger.info("start******");

		FuncUtils FunctionUtils = new FuncUtils();

		String user_id = req.getParameter("license");
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();

		/*
		 * 登录功能 附件
		 */
		String hidden_key = req.getParameter("hidden_key");
		if ("jisuan_geiyu_kongchu".equals(hidden_key)) {
			/*
			 * 年末調整等のための給与所得控除後の給与等の金額
			 */
			try {

				long jisuan_jieguo = 0;
				long geiyu = Long.parseLong(req.getParameter("form_geiyu"));

				if (geiyu < 550000) {
					jisuan_jieguo = 0;

				} else if (551000 <= geiyu && geiyu < 1619000) {
					jisuan_jieguo = geiyu - 550000;

				} else if (1619000 <= geiyu && geiyu < 6600000) {
					m_nianmo_tiaozheng_geiyu_kongchuDao m_nianmo_tiaozheng_geiyu_kongchuDao = new m_nianmo_tiaozheng_geiyu_kongchuDao();
					m_nianmo_tiaozheng_geiyu_kongchuBean m_nianmo_tiaozheng_geiyu_kongchuBean = m_nianmo_tiaozheng_geiyu_kongchuDao.select(geiyu);
					jisuan_jieguo = Long.parseLong(m_nianmo_tiaozheng_geiyu_kongchuBean.getKongchuhou_geiyu());

				} else if (6600000 <= geiyu && geiyu < 8500000) {
					jisuan_jieguo = (long) (geiyu * 0.9 - 1100000);

				} else if (8500000 <= geiyu && geiyu < 20000000) {
					jisuan_jieguo = geiyu - 1950000;

				} else if (20000000 <= geiyu) {
					jisuan_jieguo = geiyu - 18050000;

				}
				out.print("{\"jisuan_jieguo\":" + jisuan_jieguo + "}");
//				out.print("{}");

			} catch (Exception e) {
				e.printStackTrace();
			}

			logger.info("end jisuan_jianyi_keshui");
			return;

		}

		logger.info("end");

		return;

	}

}