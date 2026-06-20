package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * 社保计算
 */
@WebServlet("/GongsiShuijinLogic")
public class GongsiShuijinLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(GongsiShuijinLogic.class.toString());

	private static final long serialVersionUID = 1L;

	static HashMap<String, String> ShebaoHashMap = new HashMap<String, String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GongsiShuijinLogic() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("start");

		String LIRUN = request.getParameter("LIRUN");
		if (StringUtils.isEmpty(LIRUN) == true) {
			LIRUN = "0";
		}
		int LIRUN_int = Integer.parseInt(LIRUN);

		/*
		所得税	1法人税	累计税制：税前利润小于等于800万的部分税率0.15，超过800万的部分0.232.百位以下为0
			2地方法人税	1法人税的金额，千位及以下为0之后×0.103 ，百位以下为0
			3国税	1+2
		 */
		int suodeshui_faren = 0;
		int suodeshui_difang_faren = 0;
		int suodeshui_ALL = 0;
		if (LIRUN_int <= 800 * 10000) {
			suodeshui_faren = LIRUN_int / 1000 * 150;
		} else {
			suodeshui_faren = 800 * 10000 / 1000 * 150;
			suodeshui_faren = suodeshui_faren + (LIRUN_int - 800 * 10000) / 1000 * 232;
		}
		suodeshui_difang_faren = suodeshui_faren / 1000 * 103;

		suodeshui_faren = suodeshui_faren / 100 * 100;
		suodeshui_difang_faren = suodeshui_difang_faren / 100 * 100;

		suodeshui_ALL = suodeshui_faren + suodeshui_difang_faren;

		/*
		事业税	1事业税	累计税制：課税所得400万円以下の部分：3.4％ 課税所得400万円超800万円以下の部分：5.1％課税所得800万円超の部分：6.7％,百位以下为0
			2特别事业税	1×0.37，百位及以下为0
			3事业税	1+2
		 */
		int shiyeshui_shiyeshui = 0;
		int shiyeshui_tebie = 0;
		int shiyeshui_ALL = 0;
		if (LIRUN_int < 400 * 10000) {
			shiyeshui_shiyeshui = LIRUN_int / 1000 * 34;

		} else if (LIRUN_int < 800 * 10000) {
			shiyeshui_shiyeshui = 400 * 10000 / 1000 * 34;
			shiyeshui_shiyeshui = shiyeshui_shiyeshui + (LIRUN_int - 400 * 10000) / 1000 * 51;

		} else {
			shiyeshui_shiyeshui = 400 * 10000 / 1000 * 34;
			shiyeshui_shiyeshui = shiyeshui_shiyeshui + 400 * 10000 / 1000 * 51;
			shiyeshui_shiyeshui = shiyeshui_shiyeshui + (LIRUN_int - 800 * 10000) / 1000 * 67;

		}

		shiyeshui_tebie = shiyeshui_shiyeshui / 1000 * 370;

		shiyeshui_shiyeshui = shiyeshui_shiyeshui / 100 * 100;
		shiyeshui_tebie = shiyeshui_tebie / 100 * 100;

		shiyeshui_ALL = shiyeshui_shiyeshui + shiyeshui_tebie;

		/*
		住民税	1法人税割	法人税的金额，千位以下为0之后×0.07
			2均等割	70000
			3住民税	1+2
		 */
		int zhuminshui_faren = 0;
		int zhuminshuii_jundeng = 70000;
		int zhuminshui_ALL = 0;
		zhuminshui_faren = suodeshui_faren / 1000 * 70;

		zhuminshui_faren = zhuminshui_faren / 100 * 100;
		zhuminshuii_jundeng = zhuminshuii_jundeng / 100 * 100;

		zhuminshui_ALL = zhuminshui_faren + zhuminshuii_jundeng;

		// 返却値に設定
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print("所得税：" + String.format("%,d", suodeshui_ALL)
				+ "\r\n"
//				+ "所得税1：" + String.format("%,d", suodeshui_faren)
//				+ "\r\n"
//				+ "所得税2：" + String.format("%,d", suodeshui_difang_faren)
//				+ "\r\n"
				+ "事业税：" + String.format("%,d", shiyeshui_ALL)
				+ "\r\n"
//				+ "事业税1：" + String.format("%,d", shiyeshui_shiyeshui)
//				+ "\r\n"
//				+ "事业税2：" + String.format("%,d", shiyeshui_tebie)
//				+ "\r\n"
				+ "住民税：" + String.format("%,d", zhuminshui_ALL)
				+ "\r\n"
//				+ "住民税1：" + String.format("%,d", zhuminshui_faren)
//				+ "\r\n"
//				+ "住民税2：" + String.format("%,d", zhuminshuii_jundeng)
//				+ "\r\n"
				+ "合计税金：" + String.format("%,d", (suodeshui_ALL + shiyeshui_ALL + zhuminshui_ALL))
				+ "");

		logger.info("end");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}