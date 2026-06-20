package com.panda.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.MABean;
import com.panda.dao.UserDao;

@WebServlet("/SearchLogic")
public class SearchLogic extends HttpServlet {

    private static Logger logger = Logger.getLogger(SearchLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		logger.info("start");

		HttpSession session = req.getSession();
		session.setAttribute("SendMail", "");

		HashMap<String, String> HashMap = setIni();;

		String KEY_VALUE = req.getParameter("KEY_VALUE");
		String CATEGORY = req.getParameter("CATEGORY");
		String URIAGE_MIN = req.getParameter("URIAGE_MIN");
		String URIAGE_MAX = req.getParameter("URIAGE_MAX");
		String JYOUTO_KAGAKU_MIN = req.getParameter("JYOUTO_KAGAKU_MIN");
		String JYOUTO_KAGAKU_MAX = req.getParameter("JYOUTO_KAGAKU_MAX");
		String TIIKI = req.getParameter("TIIKI");
		String SOUGYOU_YYYY = req.getParameter("SOUGYOU_YYYY");
		String ANNKENN_NO = req.getParameter("ANNKENN_NO");




		session.setAttribute("KEY_VALUE", KEY_VALUE);
		session.setAttribute("CATEGORY", CATEGORY);
		session.setAttribute("URIAGE_MIN", URIAGE_MIN);
		session.setAttribute("URIAGE_MAX", URIAGE_MAX);
		session.setAttribute("JYOUTO_KAGAKU_MIN", JYOUTO_KAGAKU_MIN);
		session.setAttribute("JYOUTO_KAGAKU_MAX", JYOUTO_KAGAKU_MAX);
		session.setAttribute("TIIKI", TIIKI);
		session.setAttribute("SOUGYOU_YYYY", SOUGYOU_YYYY);
		session.setAttribute("ANNKENN_NO", ANNKENN_NO);

		int pageNo = 1;
		if (StringUtils.isEmpty(req.getParameter("pageNo")) == false) {
			pageNo = Integer.parseInt(req.getParameter("pageNo"));

		}
		session.setAttribute("pageNo", pageNo);
		logger.debug("pageNo " + pageNo);

		ArrayList<MABean> maList = new ArrayList<MABean>();
		UserDao userDAo = new UserDao();

		int pageSize = 20;
		session.setAttribute("pageSize", pageSize);
		logger.debug("pageSize " + pageSize);

		maList = userDAo.select(req, pageNo, pageSize, HashMap);
		session.setAttribute("maList", maList);
		logger.debug("maList " + maList.size());

		int maListSize = userDAo.selectSize(req);
		session.setAttribute("maListSize", maListSize);
		logger.debug("maListSize " + maListSize);

		req.getRequestDispatcher("/search.jsp").forward(req,resp);

		logger.info("end");

		return;

	}

	/**
	 * @return
	 *
	 */
	private HashMap<String, String> setIni() {
		HashMap<String, String> HashMap = new HashMap<>();
	       HashMap.put("飲食店・食品", "01飲食店・食品");
	        HashMap.put("医療・介護", "02医療・介護");
	        HashMap.put("調剤薬局・化学・医薬品", "03調剤薬局・化学・医薬品");
	        HashMap.put("小売業・EC", "04小売業・EC");
	        HashMap.put("IT・Webサイト・ソフトウェア", "05IT・Webサイト・ソフトウェア");
	        HashMap.put("美容・理容", "06美容・理容");
	        HashMap.put("アパレル・ファッション", "07アパレル・ファッション");
	        HashMap.put("旅行業・宿泊施設", "08旅行業・宿泊施設");
	        HashMap.put("教育サービス", "09教育サービス");
	        HashMap.put("娯楽・レジャー", "10娯楽・レジャー");
	        HashMap.put("建設・土木・工事", "11建設・土木・工事");
	        HashMap.put("不動産", "12不動産");
	        HashMap.put("印刷・広告・出版", "13印刷・広告・出版");
	        HashMap.put("サービス業（消費者向け）", "14サービス業（消費者向け）");
	        HashMap.put("サービス業（法人向け）", "15サービス業（法人向け）");
	        HashMap.put("製造・卸売業（日用品）", "16製造・卸売業（日用品）");
	        HashMap.put("製造業（機械・電機・電子部品）", "17製造業（機械・電機・電子部品）");
	        HashMap.put("製造業（金属・プラスチック）", "18製造業（金属・プラスチック）");
	        HashMap.put("運送業・海運", "19運送業・海運");
	        HashMap.put("農林水産業", "20農林水産業");
	        HashMap.put("エネルギー", "21エネルギー");
	        HashMap.put("産廃・リサイクル", "22産廃・リサイクル");
	        HashMap.put("病院", "23病院");
	        HashMap.put("農業", "24農業");
	        HashMap.put("ホテル・旅館", "25ホテル・旅館");
	        HashMap.put("ドラッグストア", "26ドラッグストア");
	        HashMap.put("学習塾", "27学習塾");
	        HashMap.put("人材派遣", "28人材派遣");
	        HashMap.put("カフェ", "29カフェ");
	        HashMap.put("AI", "30AI");
	        HashMap.put("デイサービス", "31デイサービス");
	        HashMap.put("エステ", "32エステ");
	        HashMap.put("その他", "33その他");
			return HashMap;
	}

}