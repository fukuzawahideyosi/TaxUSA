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

import com.panda.bean.JCTBean;
import com.panda.dao.JCTOverseasDao;

@WebServlet("/SearchJCTOverseasLogic")
public class SearchJCTOverseasLogic extends HttpServlet {

    private static Logger logger = Logger.getLogger(SearchJCTOverseasLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	private static final String ALLOWED_IP_ADDRESS = "13.114.220.65";//亚马逊一号机
    private static final String ALLOWED_DOMAIN = "japanetax.com";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {


		logger.info("start");

        String clientIpAddress = req.getRemoteAddr();
        String hostHeader = req.getServerName();
        if (ALLOWED_IP_ADDRESS.equals(clientIpAddress) || hostHeader.contains(ALLOWED_DOMAIN)) {
    		req.getRequestDispatcher("/welcome.html").forward(req,resp);
    		logger.info("end");
    		return;
        }


		HttpSession session = req.getSession();
		session.setAttribute("SendMail", "");

		HashMap<String, String> HashMap = setIni();;


		String sequenceNumber = req.getParameter("sequenceNumber");
		String registratedNumber = req.getParameter("registratedNumber");
		String process = req.getParameter("process");
		String correct = req.getParameter("correct");
		String kind = req.getParameter("kind");
		String country = req.getParameter("country");
		String countrySub = req.getParameter("countrySub");
		String latest = req.getParameter("latest");
		String registrationDate = req.getParameter("registrationDate");
		String updateDate_MIN = req.getParameter("updateDate_MIN");
		String updateDate_MAX = req.getParameter("updateDate_MAX");
		String disposalDate = req.getParameter("disposalDate");
		String expireDate = req.getParameter("expireDate");
		String address = req.getParameter("address");
		String addressPrefectureCode = req.getParameter("addressPrefectureCode");
		String addressCityCode = req.getParameter("addressCityCode");
		String addressRequest = req.getParameter("addressRequest");
		String addressRequestPrefectureCode = req.getParameter("addressRequestPrefectureCode");
		String addressRequestCityCode = req.getParameter("addressRequestCityCode");
		String kana = req.getParameter("kana");
		String name = req.getParameter("name");
		String addressInside = req.getParameter("addressInside");
		String addressInsidePrefectureCode = req.getParameter("addressInsidePrefectureCode");
		String addressInsideCityCode = req.getParameter("addressInsideCityCode");
		String tradeName = req.getParameter("tradeName");
		String popularName_previousName = req.getParameter("popularName_previousName");




		session.setAttribute("sequenceNumber", sequenceNumber);
		session.setAttribute("registratedNumber", registratedNumber);
		session.setAttribute("process", process);
		session.setAttribute("correct", correct);
		session.setAttribute("kind", kind);
		session.setAttribute("country", country);
		session.setAttribute("countrySub", countrySub);
		session.setAttribute("latest", latest);
		session.setAttribute("registrationDate", registrationDate);
		session.setAttribute("updateDate_MIN", updateDate_MIN);
		session.setAttribute("updateDate_MAX", updateDate_MAX);
		session.setAttribute("disposalDate", disposalDate);
		session.setAttribute("expireDate", expireDate);
		session.setAttribute("address", address);
		session.setAttribute("addressPrefectureCode", addressPrefectureCode);
		session.setAttribute("addressCityCode", addressCityCode);
		session.setAttribute("addressRequest", addressRequest);
		session.setAttribute("addressRequestPrefectureCode", addressRequestPrefectureCode);
		session.setAttribute("addressRequestCityCode", addressRequestCityCode);
		session.setAttribute("kana", kana);
		session.setAttribute("name", name);
		session.setAttribute("addressInside", addressInside);
		session.setAttribute("addressInsidePrefectureCode", addressInsidePrefectureCode);
		session.setAttribute("addressInsideCityCode", addressInsideCityCode);
		session.setAttribute("tradeName", tradeName);
		session.setAttribute("popularName_previousName", popularName_previousName);

		int pageNo = 1;
		if (StringUtils.isEmpty(req.getParameter("pageNo")) == false) {
			pageNo = Integer.parseInt(req.getParameter("pageNo"));

		}
		session.setAttribute("pageNo", pageNo);
		logger.debug("pageNo " + pageNo);

		ArrayList<JCTBean> maListJCT = new ArrayList<JCTBean>();
		JCTOverseasDao JCTOverseasDao = new JCTOverseasDao();

		int pageSize = 20;
		session.setAttribute("pageSize", pageSize);
		logger.debug("pageSize " + pageSize);

		maListJCT = JCTOverseasDao.select(req, pageNo, pageSize, HashMap);
		session.setAttribute("maListJCT", maListJCT);
		logger.debug("maListJCT " + maListJCT.size());

		int maListJCTSize = JCTOverseasDao.selectSize(req);
		session.setAttribute("maListJCTSize", maListJCTSize);
		logger.debug("maListJCTSize " + maListJCTSize);

		String updateDateMax = JCTOverseasDao.selectUpdateDateMax();
		session.setAttribute("updateDateMax", updateDateMax);
		logger.debug("updateDateMax " + updateDateMax);

		req.getRequestDispatcher("/searchJCTOverseas.jsp").forward(req,resp);

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