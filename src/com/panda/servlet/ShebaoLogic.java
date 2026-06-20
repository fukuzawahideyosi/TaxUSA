package com.panda.servlet;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.dao.ShebaoBaoxianlvDao;
import com.panda.dao.ShebaomBaochouDengjiDao;

/**
 * 社保计算
 */
@WebServlet("/ShebaoLogic")
public class ShebaoLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(ShebaoLogic.class.toString());

	private static final long serialVersionUID = 1L;

	static HashMap<String, String> ShebaoHashMap = new HashMap<String, String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ShebaoLogic() {
		super();
		ShebaoBaoxianlvDao ShebaoDAo = new ShebaoBaoxianlvDao();
		ShebaoHashMap = ShebaoDAo.select();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("start");

		String YUEXIN = request.getParameter("YUEXIN");
		String NIANLING = request.getParameter("NIANLING");
		if (StringUtils.isEmpty(NIANLING) == true) {
			NIANLING = "0";
		}
		String TIIKI = request.getParameter("TIIKI");

		int NIANLING_int = Integer.parseInt(NIANLING);

		String baoxianlv = ShebaoHashMap.get(TIIKI);
		String[] baoxianlvList = baoxianlv.split(",");
		Float baoxianlv_double = Float.parseFloat(baoxianlvList[0]);

		if (NIANLING_int >= 40) {
			baoxianlv_double = baoxianlv_double + Float.parseFloat(baoxianlvList[2]);
		}

		ShebaomBaochouDengjiDao ShebaomBaochouDengjiDao = new ShebaomBaochouDengjiDao();
		int YUEE = ShebaomBaochouDengjiDao.select(YUEXIN);

		float jiankang_baoxian = YUEE * baoxianlv_double;

		float yanglao_baoxian = YUEE * Float.parseFloat(baoxianlvList[1]);
		if (yanglao_baoxian < 16104) {
			yanglao_baoxian = (float) 16104.0;

		} else if (yanglao_baoxian > 118950) {
			yanglao_baoxian = (float) 118950;

		}

		/*
		 * 儿童基金 0.0036
		 */
		float ertong_jijin = YUEE * 36 / 10000;
		if (YUEE >= 665000) {
			ertong_jijin = 650000 * 36 / 10000;
		}

		jiankang_baoxian = jiankang_baoxian / 2;
		yanglao_baoxian = yanglao_baoxian / 2;

        // 创建一个 NumberFormat 实例并指定所需的区域设置（Locale）
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);

		String result1 = ""
				+ "健康保险："
				+ currencyFormatter.format(jiankang_baoxian).substring(1)
				+ "（月）"
				+ currencyFormatter.format(jiankang_baoxian*12).substring(1)
				+ "（年）"
				+ "\r\n"
				+ "厚生年金："
				+ currencyFormatter.format(yanglao_baoxian).substring(1)
				+ "（月）"
				+ currencyFormatter.format(yanglao_baoxian*12).substring(1)
				+ "（年）"
				+ "";

		String result2 = result1
				+ "\r\n"
				+ "儿童基金："
				+ currencyFormatter.format(ertong_jijin).substring(1)
				+ "（月）"
				+ currencyFormatter.format(ertong_jijin*12).substring(1)
				+ "（年）"
				+ "";

		result1 = result1
				+ "\r\n"
				+ "儿童基金：0（月）0（年）"
				+ "";

		// 返却値に設定
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(result1
				+ ";"
				+ result2
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