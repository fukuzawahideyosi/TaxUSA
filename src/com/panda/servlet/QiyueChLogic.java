package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.QiyueBean;
import com.panda.dao.QiyueDao;

@WebServlet("/QiyueChLogic")
@MultipartConfig
public class QiyueChLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(QiyueChLogic.class.toString());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		logger.info("start");

		try {


			HttpSession session = request.getSession();

			QiyueBean QiyueBean=(com.panda.bean.QiyueBean) session.getAttribute("QiyueBean");
			//阿里云API模拟
			QiyueBean.setCH_NAME("熊猫 花子");


			int count;

			String QIYUE_ID = request.getParameter("QIYUE_ID");
			if (StringUtils.isEmpty(QIYUE_ID) == true ) {

			}

			QiyueDao QiyueDao = new QiyueDao();

			String str = "";
			boolean flag1 = false;
			boolean flag2 = false;
			String nowtime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());


			if (flag1 == false || flag2 == false) {
				str = "";
			} else {
			}



			QiyueBean.setCH_ZHUANGTAI("OK");
			count = QiyueDao.updateQiyueCH(QiyueBean);
			logger.info("[updateQiyue]" + count);
			QiyueBean = QiyueDao.selectQiyue(QiyueBean.getQIYUE_ID());
			session.setAttribute("QiyueBean", QiyueBean);

			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");

			PrintWriter pw = response.getWriter();

			//			JSONObject json = new JSONObject();
			//			json.put("JP_ZHUANGTAI",""+QiyueBean.getJP_ZHUANGTAI()+"");

			pw.println("{\"CH_ZHUANGTAI\":\"" + QiyueBean.getCH_ZHUANGTAI() + "\""
					+ ",\"CH_NAME\":\"" + QiyueBean.getCH_NAME() + "\""
					+ ",\"CH_UPDATE_DATE\":\"" + QiyueBean.getCH_UPDATE_DATE() + "\""
					+ "}");
			pw.flush();
			pw.close();

			logger.info("end");

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}