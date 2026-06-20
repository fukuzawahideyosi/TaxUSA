package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.JCTToukeiBean;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.dao.JCTOverseasDao;
import com.panda.dao.JCTToukeiDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;

@WebServlet("/SearchJCTToukeiLogic")
public class SearchJCTToukeiLogic extends HttpServlet {

    private static Logger logger = Logger.getLogger(SearchJCTToukeiLogic.class.toString());

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
		resp.setContentType("text/html; charset=UTF-8");
		String user_id = req.getParameter("license");
		String maxNo = req.getParameter("maxNo");
		
		session.setAttribute("license", user_id);

		/*
		 * license確認
		 */
		String pw = req.getParameter("pw");
		session.setAttribute("pw", pw);
		PrintWriter out = resp.getWriter();
		FuncUtils FunctionUtils = new FuncUtils();
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = LicenseDao.select(user_id);
		session.setAttribute("User_infoBean", User_infoBean);

		String license = User_infoBean.getLicense_yyyymmdd();
		logger.info(license);
		if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == false) {
			logger.debug("PandaServiceTools → License invalid");

			out.write("PandaServiceMA → License invalid");
			return;
		}

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

		ArrayList<JCTToukeiBean> maListJCT = new ArrayList<JCTToukeiBean>();
		JCTToukeiDao JCTToukeiDao = new JCTToukeiDao();

		int pageSize = 20;
		session.setAttribute("pageSize", pageSize);
		logger.debug("pageSize " + pageSize);


		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
		LinkedHashMapEtaxBean = t_etax_account_resDao.selectInvoiceBangouKey();

		maListJCT = JCTToukeiDao.select(LinkedHashMapEtaxBean, maxNo);
		session.setAttribute("maListJCT", maListJCT);
		logger.debug("maListJCT " + maListJCT.size());

		int maListJCTSize = JCTToukeiDao.selectSize();
		session.setAttribute("maListJCTSize", maListJCTSize);
		logger.debug("maListJCTSize " + maListJCTSize);

		String updateDateMax = JCTOverseasDao.selectUpdateDateMax();
		session.setAttribute("updateDateMax", updateDateMax);
		logger.debug("updateDateMax " + updateDateMax);

		req.getRequestDispatcher("/searchJCTToukei.jsp").forward(req,resp);

		logger.info("end");

		return;

	}

}