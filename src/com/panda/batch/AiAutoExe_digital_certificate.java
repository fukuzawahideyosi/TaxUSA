package com.panda.batch;

import java.util.LinkedHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_jieguoBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.chrome.pandaWebDriver;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsHtml;

public class AiAutoExe_digital_certificate {

	private static Logger logger = Logger.getLogger(AiAutoExe_digital_certificate.class.toString());

	public static void main(String[] args) throws Exception {
		logger.debug("START ");

		System.setProperty("https.protocols", "TLSv1.2");



		if(args.length > 0) {
			//電子証明書有効期限
			if("digital_certificate".equals(args[0])) {
				int max_DAY = 31;

				User_infoDao LicenseDao = new User_infoDao();
				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue(
						"CURDATE()", " > ", "DATE_SUB(teai.digital_certificate, INTERVAL " + max_DAY + " DAY)");

				for (t_etax_account_infoExBean t_etax_account_infoExBean : LinkedHashMap_t_etax_account_infoExBean.values()) {
					logger.info(t_etax_account_infoExBean.getYyyymmdd_count() + " : [digital_certificate]" + t_etax_account_infoExBean.getDigital_certificate());

					User_infoBean User_infoBean = LicenseDao.selectByTiaojian("user_id", t_etax_account_infoExBean.getUser_id());
					t_etax_account_infoExBean.setEmail(User_infoBean.getEmail());
					FuncUtils.sendMail_digital_certificate(t_etax_account_infoExBean);

				}

			} else if("get_shengaojieguo".equals(args[0])) {

			}





		}



		String res = "申告";
		String chuli_type = "申告";

		String yyyy = "2024";
		String event = "消費税%";
		t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
		LinkedHashMap<String, t_etax_jieguoExBean> LinkedHashMap = t_etax_jieguoDao.SelectExWhwer_shuilishi_id(yyyy, event);

		for (String yyyymmdd_count : LinkedHashMap.keySet()) {
			t_etax_jieguoExBean t_etax_jieguoExBean = LinkedHashMap.get(yyyymmdd_count);
			String PDSK = t_etax_jieguoExBean.getPDSK();

			if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
//				continue;
			}


			if (!StringUtils.isEmpty(t_etax_jieguoExBean.getEvent())) {
				continue;
			}


			/*
			 *取得：お知らせ・受信通知
			 */
			pandaWebDriver testNoWEB = new pandaWebDriver(null);


			t_etax_jieguoBean t_etax_jieguoBean = null;
			if ("申告".equals(chuli_type)) {
				t_etax_jieguoBean = testNoWEB.getShenqingJieguo(t_etax_jieguoExBean);

			} else if ("转代理".equals(chuli_type)) {
				t_etax_jieguoBean = testNoWEB.get_zhuandaili_jieguo(t_etax_jieguoExBean);

			}

			if(t_etax_jieguoBean == null) {
				res = res + PDSK + "：没有申请结果<br>";
				continue;

			} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
				res = res + PDSK + "：国税局系统维护中<br>";
				continue;

			} else {
				t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
				t_etax_jieguoBean.setYyyy("2024");
				t_etax_jieguoBean.setChuli_type(chuli_type);
				t_etax_jieguoBean.setFile_name(PDSK);

	        	Document doc = Jsoup.parse(t_etax_jieguoBean.getHtml());
	            event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
	            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
	            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
				t_etax_jieguoBean.setEvent(event);
				t_etax_jieguoBean.setTaxable_amount(taxable_amount);
				t_etax_jieguoBean.setTotal_tax_amount(total_tax_amount.replace("△", "-"));

				t_etax_jieguoDao.INSERT(t_etax_jieguoBean);


			}





		}

		logger.info("res\n " + res.replace("<br>", "\n"));


		logger.debug("END ");

		return;

	}

}
