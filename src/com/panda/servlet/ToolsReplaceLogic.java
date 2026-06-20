package com.panda.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.opencsv.CSVReader;
import com.panda.batch.ConsumptionTaxIO;
import com.panda.batch.ExeDb;
import com.panda.batch.PDFToImageToPDF;
import com.panda.batch.set_shuiwu_quanxian_daili_zhengshu;
import com.panda.batch.shengaojieguo_chuli;
import com.panda.batch.zhuandailiIO;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resExBean;
import com.panda.bean.t_etax_jieguoBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.bean.t_freeeBean;
import com.panda.bean.t_user_info_shoujiBean;
import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.chrome.pandaWebDriver;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.m_shuwushuDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.dao.t_etax_zhongjian_shengaoDao;
import com.panda.dao.t_freeeDao;
import com.panda.dao.t_user_info_shoujiDao;
import com.panda.dao.t_xiaofeishui_shengaoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsHtml;

@WebServlet("/ToolsReplaceLogic")
@MultipartConfig
public class ToolsReplaceLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(ToolsReplaceLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

//		URL = "https://sunmoonjp.com";
//		URL = "https://www.japanetax.com";
//		URL = "http://127.0.0.1:8080/PandaServiceMA";

//		resp.setHeader("Access-Control-Allow-Origin", "https://www.pandaservicejapan.com"); // 允许所有域访问
		resp.setHeader("Access-Control-Allow-Origin", "https://sunmoonjp.com"); // 允许所有域访问

		resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
		resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
		resp.setHeader("Access-Control-Allow-Credentials", "true");

		logger.info("start");


//		org.openqa.selenium.internal.Require sfasdf = new org.openqa.selenium.internal.Require();

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();

		String user_id = req.getParameter("license");
		String hidden_key = req.getParameter("hidden_key");

		session.setAttribute("license", user_id);

		try {


			if ("getAmazonCsvToExcel".equals(hidden_key)) {
				//账本保护密码	Fr6SaH94
				getAmazonCsvToExcel(req, out);
				logger.debug("end");
				return;

			} else if ("get_xiaofeishui_shuihao".equals(hidden_key)) {
				get_xiaofeishui_shuihao(req, out);
				logger.debug("end");
				return;

			}

			if ("xiaolei".equals(user_id.toLowerCase()) && "getShenqingJieguo".equals(hidden_key)) {
				getShenqingJieguo(req, out);
				logger.debug("end");
				return;
			}


			User_infoDao LicenseDao = new User_infoDao();
			User_infoBean User_infoBean = new User_infoBean();


			/*
			 * license確認
			 */
			String pw = req.getParameter("pw");
			session.setAttribute("pw", pw);
//			PrintWriter out = resp.getWriter();
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
				logger.info("end " + hidden_key);
				return;
			}



			logger.info("hidden_key：" + hidden_key);
			if ("getTextReplace".equals(hidden_key)) {
				getTextReplace(req, out);

			} else if ("getAmazonCsvFormat".equals(hidden_key)) {
				getAmazonCsvFormat(req, out);

			} else if ("getAmazonCsvToExcel".equals(hidden_key)) {
				getAmazonCsvToExcel(req, out);


			} else if ("setPDSK".equals(hidden_key)) {
				setPDSK(req, out);

			} else if ("getXiaofeishuiYidongjie".equals(hidden_key)) {
				getXiaofeishuiYidongjie(req, out);

			} else if ("GetZhengshu".equals(hidden_key)) {
				GetZhengshu(req, out);


			} else if ("setZhuandaili".equals(hidden_key)) {
				setZhuandaili(req, out);

			} else if ("del_t_etax_account_info".equals(hidden_key)) {
				del_t_etax_account_info(req, out);

			} else if ("setZhuandaili_async".equals(hidden_key)) {
				setZhuandaili_async(req, out);

			} else if ("getZhuandaili".equals(hidden_key)) {
				getZhuandaili(req, out);

			} else if ("getShenqingJieguo".equals(hidden_key)) {
				getShenqingJieguo(req, out);
			} else if ("getShenqingJieguoTatujin".equals(hidden_key)) {
				getShenqingJieguoTatujin(req, out);


			} else if ("get_shengao_zhongjian".equals(hidden_key)) {
				get_shengao_zhongjian(req, out);

			} else if ("getShenqingJieguo_async".equals(hidden_key)) {
				getShenqingJieguo_async(req, out);

			} else if ("CheckEtaxNo".equals(hidden_key)) {
				CheckEtaxNo(req, out);


			} else if ("getEtaxNoAll".equals(hidden_key)) {
				getEtaxNoAll(req, out);

			} else if ("getEtaxNoAll_async".equals(hidden_key)) {
				getEtaxNoAll_async(req, out);


			} else if ("jiansuo".equals(hidden_key)) {
				jiansuo(req, out);

			} else if ("add".equals(hidden_key)) {
				add(req, out);


			} else if ("get_freee".equals(hidden_key)) {
				get_freee(req, out);

			} else if ("exc_letian_zhifu".equals(hidden_key)) {
				exc_letian_zhifu(req, out);

			} else {

				req.getRequestDispatcher("/toolsReplace.jsp?fromBackend=true").forward(req, resp);
				logger.info("end");
				return;
			}

		} catch (Throwable e) {
			e.printStackTrace();
			out.print("{\"info\":\""+e.getMessage()+"\"}");
		}

		logger.debug("end");

		return;

	}


	private void exc_letian_zhifu(HttpServletRequest req, PrintWriter out) {

		try {



/*
PDSK241004
【収納機関番号】
00200
【納付区分】
7424102073
【有効期限】
2025-04-21
【納付金額 】
200
 */

			t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
//		t_etax_jieguoExBean.setBangou("2333092810920096");
//		t_etax_jieguoExBean.setEtax_pw("");
//		t_etax_jieguoExBean.setShuunou_kikan_bangou("00200");
//		t_etax_jieguoExBean.setNoufu_kubun("7324108720");
//		t_etax_jieguoExBean.setNoufu_kingaku("164100");
//		t_etax_jieguoExBean.setCompanyName_pianjiaming("ジヨージヤンアーフイコージーヨウシエンゴンスー");

//			t_etax_jieguoExBean.setBangou("2646042410910064");
//			t_etax_jieguoExBean.setEtax_pw("");
//			t_etax_jieguoExBean.setShuunou_kikan_bangou("00200");
//			t_etax_jieguoExBean.setNoufu_kubun("7424102073");
//			t_etax_jieguoExBean.setNoufu_kingaku("200");
//			t_etax_jieguoExBean.setCompanyName_pianjiaming("シエンジエンシーモーベイコージーヨウシエンゴンスー");



			String yyyy = req.getParameter("yyyy");
			String PDSK = req.getParameter("PDSK").replace(",", "");
			t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
			t_etax_jieguoExBean = t_etax_jieguoDao.Select_where_YYYY_AND_PDSK(yyyy, PDSK);


//			pandaWebDriver2test testNoWEB = new pandaWebDriver2test();

//			pandaWebDriver testNoWEB = new pandaWebDriver("DevTools");
			t_etax_jieguoExBean.setYyyy(yyyy);
//			String msg = testNoWEB.exc_letian_zhifu(t_etax_jieguoExBean);

			String msg = "";
				msg = ""
						+ "<html>\r\n"
						+ " <head>\r\n"
						+ " \r\n"
						+ " <link rel=\"stylesheet\" href=\"/rb/xfes/css/basicStyle.css\" type=\"text/css\">\r\n"
						+ " \r\n"
						+ " </head>\r\n"
						+ " <body>\r\n"
						+ "  <table width=\"740\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\r\n"
						+ "   <tbody>\r\n"
						+ "    <tr>\r\n"
						+ "     <td><img src=\"/rb/xfes/img/common/parts_table03_top_nottl.gif\" alt=\"\" height=\"15\" width=\"740\" class=\"margin0\"></td>\r\n"
						+ "    </tr>\r\n"
						+ "    <tr>\r\n"
						+ "     <td class=\"bgG08\" align=\"center\">\r\n"
						+ "      <div class=\"innerbox01\">\r\n"
						+ "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"710\" class=\"table01\">\r\n"
						+ "        <tbody>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            出金口座\r\n"
						+ "           </div></th>\r\n"
						+ "          <td width=\"510\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            楽天銀行&nbsp;&nbsp;第三営業支店&nbsp;&nbsp;普通&nbsp;&nbsp;7295344\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            口座名義\r\n"
						+ "           </div></th>\r\n"
						+ "          <td width=\"510\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            ＰＡＮＤＡ　ＳＥＲＶＩＣＥ　株式会社\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "        </tbody>\r\n"
						+ "       </table>\r\n"
						+ "       <div align=\"center\">\r\n"
						+ "        <img src=\"/rb/xfes/img/common/parts_table_bottom_try_s.gif\" alt=\"\" height=\"12\" width=\"23\">\r\n"
						+ "       </div>\r\n"
						+ "       <div class=\"margintop2\"></div>\r\n"
						+ "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"710\" class=\"table01\">\r\n"
						+ "        <tbody>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            払込先\r\n"
						+ "           </div></th>\r\n"
						+ "          <td class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            小石川税務署\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            納付番号\r\n"
						+ "           </div></th>\r\n"
						+ "          <td class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            2452042202930078\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            払込人名\r\n"
						+ "           </div></th>\r\n"
						+ "          <td class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            シンセンシラクコクミライボウエキユウゲンコウ\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "        </tbody>\r\n"
						+ "       </table>\r\n"
						+ "       <div class=\"margintop5\"></div>\r\n"
						+ "       <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"710\" class=\"table01\">\r\n"
						+ "        <tbody>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            払込内容\r\n"
						+ "           </div></th>\r\n"
						+ "          <td colspan=\"3\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            消地０６／０１／０１確定\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th rowspan=\"2\" class=\"th02short\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            払込金額\r\n"
						+ "           </div></th>\r\n"
						+ "          <td width=\"170\" rowspan=\"2\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            31,000円\r\n"
						+ "           </div></td>\r\n"
						+ "          <th width=\"170\" class=\"th02\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            内延滞金\r\n"
						+ "           </div></th>\r\n"
						+ "          <td width=\"170\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            ‐\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th width=\"170\" class=\"th02\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            内手数料\r\n"
						+ "           </div></th>\r\n"
						+ "          <td width=\"170\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            ‐\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "         <tr>\r\n"
						+ "          <th class=\"th02\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            支払状態\r\n"
						+ "           </div></th>\r\n"
						+ "          <td colspan=\"3\" class=\"td03\">\r\n"
						+ "           <div class=\"innercell\">\r\n"
						+ "            支払済\r\n"
						+ "           </div></td>\r\n"
						+ "         </tr>\r\n"
						+ "        </tbody>\r\n"
						+ "       </table>\r\n"
						+ "      </div></td>\r\n"
						+ "    </tr>\r\n"
						+ "   </tbody>\r\n"
						+ "  </table>\r\n"
						+ " </body>\r\n"
						+ "</html>"
						+ "";
				if (msg.contains("払込人名")) {
					//http://127.0.0.1:8080/rb/xfes/img/common/parts_table_bottom_try_s.gif
				out.print(msg.replace("/rb/", "https://fes.rakuten-bank.co.jp/rb/"));
			} else {

				out.print("{\"info\":\""+msg+"\"}");
			}



		} catch (Exception e) {
			e.printStackTrace();
			out.print("{\"info\":\""+"楽天支付失敗"+"\"}");
			logger.info("end get_freee");
			return;
		}

		logger.info("end get_freee");
		return;

	}


	private void get_freee(HttpServletRequest req, PrintWriter out) {

		try {
			pandaWebDriver testNoWEB = new pandaWebDriver("DevTools");
			List<t_freeeBean> rowDataList =null;
			for (int i = 0; i < 3; i++) {
				rowDataList = testNoWEB.get_freee();
				if (rowDataList != null) {
					break;
				}
				Thread.sleep(1000 * 10); // 等待 1000 毫秒，即 1 秒
			}

			if (rowDataList == null) {
				out.print("{\"info\":\""+"freee同步失敗"+"\"}");
				logger.info("end get_freee");
				return;
			}

	        t_freeeDao t_freeeDao = new t_freeeDao();
	        t_freeeDao.delete_where_max_torihiki_bi();
	        String max_torihiki_bi = t_freeeDao.selectAll_max_torihiki_bi();
	        t_freeeDao.INSERT(rowDataList, max_torihiki_bi);

		} catch (Exception e) {
			e.printStackTrace();
			out.print("{\"info\":\""+"freee同步失敗"+"\"}");
			logger.info("end get_freee");
			return;
		}

		out.print("{\"info\":\""+"freee同步完了"+"\"}");
		logger.info("end get_freee");
		return;

	}



	private void get_xiaofeishui_shuihao(HttpServletRequest req, PrintWriter out) {



		try {

			String key = req.getParameter("key");
			String value = req.getParameter("value");
			if ("InvoiceBangou".toLowerCase().equals(key.toLowerCase())) {
				value = "T" + value;
			}

			t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou_json(value);

			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoExBean db_t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("InvoiceBangou", value);
			if (StringUtils.isEmpty(db_t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
//				t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
//				t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.Select_Where_yyyymmdd_count_and_yyyy(db_t_etax_account_infoExBean.getYyyymmdd_count(), yyyy);
//				if (t_xiaofeishui_shengaoBean.getActivation_code().contains("激活完了pdf")) {
//					t_etax_account_infoExBean = new t_etax_account_infoExBean();
//
//				} else {
//
//
//				}

				t_etax_account_infoExBean.setCompanyName_Chinese(db_t_etax_account_infoExBean.getCompanyName_Chinese());
				t_etax_account_infoExBean.setDaibiaoName_Chinese(db_t_etax_account_infoExBean.getDaibiaoName_Chinese());
				t_etax_account_infoExBean.setAddress_Chinese(db_t_etax_account_infoExBean.getAddress_Chinese());

				t_etax_account_infoExBean.setDaibiaoName_English(db_t_etax_account_infoExBean.getDaibiaoName_English());

				t_etax_account_infoExBean.setCompany_YYYY(db_t_etax_account_infoExBean.getCompany_YYYY());
				t_etax_account_infoExBean.setCompany_MM(db_t_etax_account_infoExBean.getCompany_MM());
				t_etax_account_infoExBean.setCompany_DD(db_t_etax_account_infoExBean.getCompany_DD());
			}

			//TEST公司
			if ("T1234567890123".equals(value)) {
				t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUser_type("2");
				t_etax_account_infoExBean.setInvoiceBangou("T1234567890123");

				t_etax_account_infoExBean.setCompanyName_English("TEST CompanyName_English");
				t_etax_account_infoExBean.setCompanyName_Chinese("测试公司中文");
				t_etax_account_infoExBean.setAddress_English("TEST Address_English");
				t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
				t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
				t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");

				t_etax_account_infoExBean.setCompany_YYYY("2000");
				t_etax_account_infoExBean.setCompany_MM("01");
				t_etax_account_infoExBean.setCompany_DD("02");

			} else if ("T0987654321098".equals(value)) {
				//TEST个人
				t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean.setUser_type("1");
				t_etax_account_infoExBean.setInvoiceBangou("T0987654321098");
				t_etax_account_infoExBean.setCompanyName_English("TEST geren_English");
				t_etax_account_infoExBean.setCompanyName_Chinese("测试个人中文");
				t_etax_account_infoExBean.setAddress_English("TEST Address_English");
				t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
				t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
				t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");

				t_etax_account_infoExBean.setCompany_YYYY("2001");
				t_etax_account_infoExBean.setCompany_MM("11");
				t_etax_account_infoExBean.setCompany_DD("22");
			}


			t_etax_account_infoExBean.setYyyymmdd_count(db_t_etax_account_infoExBean.getYyyymmdd_count());

			// 将Java对象转换为JSON字符串
			ObjectMapper objectMapper = new ObjectMapper();
			String jsonString = objectMapper.writeValueAsString(t_etax_account_infoExBean);

			out.print(jsonString);

		} catch (Exception e) {
			e.printStackTrace();
		}



		logger.info("end get_xiaofeishui_shuihao");
		return;



	}

	private void getEtaxNoAll(HttpServletRequest req, PrintWriter out) throws Exception {

		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();

		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = t_etax_account_resDao.selecBangouIsNull();
		String msg = "";
		String res = "";
		int count = 0;
		for (String yyyymmdd_count : LinkedHashMap_t_etax_account_resExBean.keySet()) {
			++count;
			t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);
			pandaWebDriver testNoWEB = new pandaWebDriver(null);
			msg = testNoWEB.getEtaxNo(yyyymmdd_count);
			if("国税局系统维护中".equals(msg)) {
				out.print(msg);
				logger.info("end" + msg);
				return;

			} else if (!StringUtils.isEmpty(msg)) {
				res = res + msg + "<br>";

			}


		}
		msg ="Etax拿号处理件数：" + count + "件";
		res = res + msg + "<br>";

		msg ="※温情提示：请刷新【客户信息收集表一览】画面确认结果。";
		res = res + msg + "<br>";

        logger.info("end getEtaxNoAll");
        out.print("{\"info\":\"" + res + "\"}");
        return;

	}







	private void getEtaxNoAll_async(HttpServletRequest req, PrintWriter out) throws Exception {
		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		t_etax_account_resExBean t_etax_account_resExBean = t_etax_account_resDao.getLastCreatedRecord(yyyymmddhhmmss);

		if (StringUtils.isEmpty(t_etax_account_resExBean.getCompanyName_Chinese())) {
			out.print("{"
					+ "  \"info\":\"\""
					+ "}");
			return;
		}
//        String res = "Etax拿号完了：" + t_etax_account_resExBean.getCompanyName_Chinese() + "," + t_etax_account_resExBean.getBangou() + "<br>";
        String res = "Etax拿号完了：" + t_etax_account_resExBean.getCompanyName_Chinese() + "<br>";
		out.print("{"
				+ "  \"info\":\"" + res + "\""
				+ "}");


}




	private void add(HttpServletRequest req, PrintWriter out) throws Exception {

		String user_id = req.getParameter("license");
		user_id ="wangzihao";
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = LicenseDao.select(user_id);

        String formData = req.getParameter("form_data");
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
		String line;
		String res = "";

	     // 逐行读取formData
        try (BufferedReader reader = new BufferedReader(new StringReader(formData))) {
            while ((line = reader.readLine()) != null) {
    			if (!StringUtils.isEmpty(line)) {
                    // 根据每一行的内容判断并赋值

    				try {
						if (line.startsWith("bangou:")) {
							t_etax_account_infoExBean.setBangou(line.split(":")[1].trim());
						} else if (line.startsWith("HoujinBangou:")) {
							t_etax_account_infoExBean.setHoujinBangou(line.split(":")[1].trim());
						} else if (line.startsWith("InvoiceBangou:")) {
							t_etax_account_infoExBean.setInvoiceBangou(line.split(":")[1].trim());
						} else if (line.startsWith("DaibiaoName_English:")) {
							t_etax_account_infoExBean.setDaibiaoName_English(line.split(":")[1].trim());
						} else if (line.startsWith("company_DD:")) {
							t_etax_account_infoExBean.setCompany_DD(line.split(":")[1].trim());
						} else if (line.startsWith("company_MM:")) {
							t_etax_account_infoExBean.setCompany_MM(line.split(":")[1].trim());
						} else if (line.startsWith("company_YYYY:")) {
							t_etax_account_infoExBean.setCompany_YYYY(line.split(":")[1].trim());
						} else if (line.startsWith("tel_1:")) {
							t_etax_account_infoExBean.setTel_1(line.split(":")[1].trim());
						} else if (line.startsWith("tel_2:")) {
							t_etax_account_infoExBean.setTel_2(line.split(":")[1].trim());
						} else if (line.startsWith("tel_3:")) {
							t_etax_account_infoExBean.setTel_3(line.split(":")[1].trim());
						} else if (line.startsWith("tel_country:")) {
							t_etax_account_infoExBean.setTel_country(line.split(":")[1].trim());
						} else if (line.startsWith("xiaoshouerYYYY_1:")) {
							t_etax_account_infoExBean.setXiaoshouerYYYY_1(line.split(":")[1].trim());
						} else if (line.startsWith("xiaoshouerYYYY_1_half:")) {
							t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(line.split(":")[1].trim());
						} else if (line.startsWith("xiaoshouerYYYY_2:")) {
							t_etax_account_infoExBean.setXiaoshouerYYYY_2(line.split(":")[1].trim());
						} else if (line.startsWith("zhice_ziben:")) {
							t_etax_account_infoExBean.setZhice_ziben(line.split(":")[1].trim());
						} else if (line.startsWith("address_Chinese:")) {
							t_etax_account_infoExBean.setAddress_Chinese(line.split(":")[1].trim());
						} else if (line.startsWith("CompanyName_Chinese:")) {
							t_etax_account_infoExBean.setCompanyName_Chinese(line.split(":")[1].trim());
						} else if (line.startsWith("CompanyName_English:")) {
							t_etax_account_infoExBean.setCompanyName_English(line.split(":")[1].trim());
						} else if (line.startsWith("DaibiaoName_Chinese:")) {
							t_etax_account_infoExBean.setDaibiaoName_Chinese(line.split(":")[1].trim());
						} else if (line.startsWith("geren_dianpu_address:")) {
							t_etax_account_infoExBean.setGeren_dianpu_address(line.split(":")[1].trim());
						} else if (line.startsWith("geren_dianpu_name:")) {
							t_etax_account_infoExBean.setGeren_dianpu_name(line.split(":")[1].trim());
						} else if (line.startsWith("changshe_jigou_Select:")) {
							t_etax_account_infoExBean.setChangshe_jigou_Select(line.split(":")[1].trim());
						} else if (line.startsWith("jianyi_keshui_Select:")) {
							t_etax_account_infoExBean.setJianyi_keshui_Select(line.split(":")[1].trim());
						} else if (line.startsWith("address_English:")) {
							t_etax_account_infoExBean.setAddress_English(line.split(":")[1].trim());
						} else if (line.startsWith("jianyi_keshui_type:")) {
							t_etax_account_infoExBean.setJianyi_keshui_type(line.split(":")[1].trim());
						} else if (line.startsWith("dataFileName:")) {
							t_etax_account_infoExBean.setDataFileName(line.split(":")[1].trim());
						} else if (line.startsWith("tokutei_kikann_siharai_kyuuyo:")) {
							t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(line.split(":")[1].trim());
						} else if (line.startsWith("shouri_kaishi_denglu_xiayige:")) {
							t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(line.split(":")[1].trim());
						} else if (line.startsWith("shouri_kaishi_denglu_ben:")) {
							t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(line.split(":")[1].trim());
						} else if (line.startsWith("email:")) {
							t_etax_account_infoExBean.setEmail(line.split(":")[1].trim());
						} else if (line.startsWith("yaoqing_no:")) {
							t_etax_account_infoExBean.setYaoqing_no(line.split(":")[1].trim());
						} else if (line.startsWith("etax_pw:")) {
							t_etax_account_infoExBean.setEtax_pw(line.split(":")[1].trim());
						} else if (line.startsWith("CompanyName_pianjiaming:")) {
							t_etax_account_infoExBean.setCompanyName_pianjiaming(line.split(":")[1].trim());
						} else if (line.startsWith("address_pianjiaming:")) {
							t_etax_account_infoExBean.setAddress_pianjiaming(line.split(":")[1].trim());
						} else if (line.startsWith("DaibiaoName_pianjiaming:")) {
							t_etax_account_infoExBean.setDaibiaoName_pianjiaming(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_youbian1:")) {
							t_etax_account_infoExBean.setNashuidi_youbian1(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_youbian2:")) {
							t_etax_account_infoExBean.setNashuidi_youbian2(line.split(":")[1].trim());
						} else if (line.startsWith("ksaTodofuken:")) {
							t_etax_account_infoExBean.setKsaTodofuken(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi:")) {
							t_etax_account_infoExBean.setNashuidi(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_pianjiaming:")) {
							t_etax_account_infoExBean.setNashuidi_pianjiaming(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_tel1:")) {
							t_etax_account_infoExBean.setNashuidi_tel1(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_tel2:")) {
							t_etax_account_infoExBean.setNashuidi_tel2(line.split(":")[1].trim());
						} else if (line.startsWith("nashuidi_tel3:")) {
							t_etax_account_infoExBean.setNashuidi_tel3(line.split(":")[1].trim());
						} else if (line.startsWith("guanxia_shuiwushu:")) {
							t_etax_account_infoExBean.setGuanxia_shuiwushu(line.split(":")[1].trim());
						} else if (line.startsWith("liyongzhe_shibie_fanhao:")) {
							t_etax_account_infoExBean.setLiyongzhe_shibie_fanhao(line.split(":")[1].trim());
						} else if (line.startsWith("user_type:")) {
							t_etax_account_infoExBean.setUser_type(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_guanliren:")) {
							t_etax_account_infoExBean.setNashui_guanliren(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_CompanyName:")) {
							t_etax_account_infoExBean.setNashui_CompanyName(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_DaibiaoName:")) {
							t_etax_account_infoExBean.setNashui_DaibiaoName(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_address:")) {
							t_etax_account_infoExBean.setNashui_address(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_di_biangengqian:")) {
							t_etax_account_infoExBean.setNashui_di_biangengqian(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_shuiwushu_biangengqian:")) {
							t_etax_account_infoExBean.setNashui_shuiwushu_biangengqian(line.split(":")[1].trim());
						} else if (line.startsWith("nashui_shuiwushu_fanhao:")) {
							t_etax_account_infoExBean.setNashui_shuiwushu_fanhao(line.split(":")[1].trim());
						}
					} catch (Exception e) {
					}

    			}
			}
		}


//		公司或个人名（中文）		0
		//		公司或个人		1
		//		JCT号（不要T）		2
		//		代表人名（中文）		3
		//		个人生日		4
		//		解约纳税管理人		5
		//		解任納税管理人会社名		6
		//		解任納税管理人代表者		7
		//		解任納税管理人住所		8
		//		解任納税地		9
		//		解任税務署		10
		//		解任税務署番号		11
		//		Etax		12
		//		etax_pw		13

		try {
			if ("个人".equals(t_etax_account_infoExBean.getUser_type()) && StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_Chinese())) {
				t_etax_account_infoExBean.setCompanyName_Chinese(t_etax_account_infoExBean.getDaibiaoName_Chinese());
			}

			/*
			 * 登録
			 */
			String yyyymmdd_count = req.getParameter("yyyymmdd_count");
			if (StringUtils.isEmpty(yyyymmdd_count)) {
//				t_etax_account_infoDao.Update_Del_where_CompanyName(t_etax_account_infoExBean);

			} else {
				t_etax_account_infoDao.Update_Del_where_yyyymmdd_count(yyyymmdd_count);
			}

			//		公司或个人名（中文）		0
			//		公司或个人		1
			//		JCT号（不要T）		2
			//		代表人名（中文）		3
			//		个人生日		4
			//		解约纳税管理人		5
			//		解任納税管理人会社名		6
			//		解任納税管理人代表者		7
			//		解任納税管理人住所		8
			//		解任納税地		9
			//		解任税務署		10
			//		解任税務署番号		11
			//		Etax		12
			//		etax_pw		13



//	        jsonObject.remove("UPDATE_DATE");
//	        jsonObject.remove("yyyymmdd_count");
//	        jsonObject.remove("user_id");
//	        jsonObject.remove("syouninn_status");
//	        jsonObject.remove("etax_no");
//	        jsonObject.remove("activation_code");
//	        jsonObject.remove("etax_pw");
//	        jsonObject.remove("xiaofeishui_shuihao");
//	        jsonObject.remove("etax_pw_flag");

			String yyyymmddhhmmss = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
			m_sequenceDao m_sequenceDao = new m_sequenceDao();
			yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

			t_etax_account_infoExBean.setYyyymmdd_count(yyyymmdd_count);
			t_etax_account_infoExBean.setUser_id("add_" + yyyymmddhhmmss);
			t_etax_account_infoExBean.setSyouninn_status("待处理");//承認無
			t_etax_account_infoExBean.setActivation_code(yyyymmdd_count);
			t_etax_account_infoExBean.setEtax_pw_flag("0");
			t_etax_account_infoExBean.setYaoqing_no(user_id);

			if (StringUtils.isEmpty(t_etax_account_infoExBean.getEtax_pw())) {
		        String etax_pw = "ps" + FuncUtils.generateRandomNumber(8);
		        t_etax_account_infoExBean.setEtax_pw(etax_pw);
			}

			t_etax_account_infoDao.INSERT(t_etax_account_infoExBean);


			/*
			 *
			 */
			ExeDb.exe_activation(yyyymmdd_count);

			/*
			 * UPDATE t_etax_account_res SET HoujinBangou='9700150118570', InvoiceBangou='T9700150118570' WHERE yyyymmdd_count='20240603990001';
			 */

			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, t_etax_account_infoExBean.getHoujinBangou());
			t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, t_etax_account_infoExBean.getInvoiceBangou());

			ExeDb.set_pianjiaming(yyyymmdd_count);
			ExeDb.set_DaibiaoName_English(yyyymmdd_count);
			ExeDb.setInfoForAPI(yyyymmdd_count, t_etax_account_infoDao);

			t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
			if (StringUtils.isEmpty(EtaxAccountInfoBean.getCompanyName_English())) {
				User_infoBean = LicenseDao.select(user_id);
				t_etax_account_infoDao.DELETE(User_infoBean, yyyymmdd_count);

				res = res + "税务局API同步失败<br>";
				logger.info(res);

			}

			t_etax_account_resDao.DELETE_res(yyyymmdd_count);
			ExeDb.exe_activation(yyyymmdd_count);
			t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, t_etax_account_infoExBean.getHoujinBangou());
			t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, t_etax_account_infoExBean.getInvoiceBangou());

			//		Etax		12
			if (!StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
				t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "bangou", t_etax_account_infoExBean.getBangou());
			}




			String msg = "数据登录成功：" + t_etax_account_infoExBean.getCompanyName_Chinese() + "," + yyyymmdd_count;
			res = res + msg;
			res = res + "<br>";
	        logger.info("end add");
	        out.print("{\"res\":\"" + res + "\"}");
	        return;

		} catch (Exception e) {
			// TODO 登录失败怎么办
			e.printStackTrace();
			res = res + "数据登录失败：" + e.getMessage() + "<br>";
			logger.info(res);
			out.print("{\"res\":\"" + res + "\"}");
			return;
		}


	}

	private void jiansuo(HttpServletRequest req, PrintWriter out) throws Exception {

		String yyyymmdd_count = req.getParameter("yyyymmdd_count");

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

        Gson gson = new Gson();
        String json = gson.toJson(t_etax_account_infoExBean);
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();
        jsonObject.remove("UPDATE_DATE");
        jsonObject.remove("yyyymmdd_count");
        jsonObject.remove("user_id");
        jsonObject.remove("syouninn_status");
        jsonObject.remove("etax_no");
        jsonObject.remove("activation_code");
//        jsonObject.remove("etax_pw");
        jsonObject.remove("xiaofeishui_shuihao");
        jsonObject.remove("etax_pw_flag");
        jsonObject.remove("dataFileName");


        String modifiedJson = gson.toJson(jsonObject);


//
        /*
         *
user_id:danyi_exe
syouninn_status:待处理
         *
         *
         */

		out.print(modifiedJson);

	}

	public void CheckEtaxNo(HttpServletRequest req, PrintWriter out) throws Exception {
        // 创建一个用于写入日志的文件路径
//        String filePath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\PandaServiceMA1.log";

        // 创建FileWriter和BufferedWriter
//        FileWriter fileWriter = new FileWriter(filePath, true);
//        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

		String user_id = req.getParameter("license");
		user_id ="wangzihao";
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = LicenseDao.select(user_id);


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

		loginEtaxLogic loginEtaxLogic = new loginEtaxLogic();
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(60 * 60 * 2); // 设置会话超时时间为30分钟

		int count = 0;
		String line;
		String res = "";
        // 读取请求体并存储到StringBuilder中
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
    			if (!StringUtils.isEmpty(line)) {
    				++count;

    				LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao.selectAll_ByLike_CompanyName(line);
    				for (Map.Entry<String, t_etax_account_infoExBean> entry : LinkedHashMap_t_etax_account_infoExBean.entrySet()) {
//			            String key = entry.getKey();
			            t_etax_account_infoExBean value = entry.getValue();

	    				loginEtaxLogic.CheckEtaxNo(session, value);
	    				String msg = (String)session.getAttribute("errorMessage");
	    				//莆田市百吉家居有限公司
	            		if (StringUtils.isEmpty(msg)) {
	            			continue;
	            		}

	                    JSONObject jsonObject = new JSONObject(msg.replace("\\n", ""));
	                    msg = jsonObject.getString("errorMessage");
						msg = value.getYyyymmdd_count() + value.getCompanyName_Chinese() + "," + "：" + msg;
	    				logger.info("CheckEtaxNo结果：" + msg);

						res = res + msg;
						res = res + "<br>";

//    					if (msg.contains("この利用者識別番号は廃止されている")) {
//    					}

			        }

    			}

			}
		}


		logger.info("CheckEtaxNo结果ALL：\n" + res.replace("<br>", "\n"));

		//全部Check
        if (count == 0) {
        	LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao
        			.selectAll(User_infoBean, null, null, null, null);
        	for (t_etax_account_infoExBean t_etax_account_infoExBean : LinkedHashMap_t_etax_account_infoExBean.values()) {


                // 每10次写入文件一次
                if (count % 10 == 0) {
//                    bufferedWriter.flush(); // 刷新缓冲区，确保写入文件
                    logger.info("已写入文件，继续处理...");
                }


        		if (StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
        			continue;
        		}
        		++count;
                logger.info("処理件数：" + count);

//        		if (count > 578) {
//                    bufferedWriter.flush(); // 刷新缓冲区，确保写入文件
//                    logger.info("已写入文件，继续处理...");
//        			return;
//        		}
//
//        		if (count < 568) {
//        			continue;
//
//        		}


        	    Random random = new Random();
                int randomSeconds = random.nextInt(2) + 1; // 生成1到2之间的随机整数
                Thread.sleep(randomSeconds * 1000); // 将秒转换为毫秒并等待
                logger.info("等待 " + randomSeconds + " 秒...");



				loginEtaxLogic.CheckEtaxNo(session, t_etax_account_infoExBean);
				String msg = (String)session.getAttribute("errorMessage");
        		if (StringUtils.isEmpty(msg)) {
        			continue;
        		}

				msg = t_etax_account_infoExBean.getCompanyName_Chinese() + "," + t_etax_account_infoExBean.getYyyymmdd_count() +"：" +msg;
				logger.info("CheckEtaxNo结果：" + msg);

                // 写入日志信息到文件
//                bufferedWriter.write("CheckEtaxNo结果：" + msg);
//                bufferedWriter.newLine();

				res = res + msg;
				res = res + "<br>";

        	}
        }

		String msg = "CheckEtaxNo件数：" +count;
		res = res + msg;
		res = res + "<br>";
//        bufferedWriter.flush(); // 刷新缓冲区，确保写入文件
        logger.info("end CheckEtaxNo");
        out.print("{\"res\":\"" + res + "\"}");

        /*

        杨北川,20240309980004：法人の利用者識別番号を入力してください。
        蒋国儿,20250106000756：法人の利用者識別番号を入力してください。
        蒋益容,20250121000452：法人の利用者識別番号を入力してください。
        钟显锋,20250121000453：法人の利用者識別番号を入力してください。
        廖师帅,20240612000855：法人の利用者識別番号を入力してください。
        余培君,20240202008889：法人の利用者識別番号を入力してください。

        深圳市众创无限电子商务有限公司,20240413980041：利用者識別番号が廃止されています。確認の上、再度入力してください。正しい利用者識別番号が分からない場合は、所轄の税務署に確認してください。
        东莞市常平融汇百货店,20231108000758：利用者識別番号が廃止されています。確認の上、再度入力してください。正しい利用者識別番号が分からない場合は、所轄の税務署に確認してください。
        运城经济技术开发区汇景电子商务有限公司,20240413980009：利用者識別番号が廃止されています。確認の上、再度入力してください。正しい利用者識別番号が分からない場合は、所轄の税務署に確認してください。
        深圳奕桦贸易有限公司,20240603990001：利用者識別番号が廃止されています。確認の上、再度入力してください。正しい利用者識別番号が分からない場合は、所轄の税務署に確認してください。
        CheckEtaxNo件数：104

         */

        return;

	}



	private void setZhuandaili(HttpServletRequest req, PrintWriter out) throws Exception {

		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);
//
//		String path = getServletContext().getRealPath("/fileDataTools/GetZhuandaili");


		String user_id = req.getParameter("license");
		//TODO
		user_id = "wangzihao";
		User_infoDao LicenseDao = new User_infoDao();


		m_sequenceDao m_sequenceDao = new m_sequenceDao();
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();

        com.panda.dao.m_shuwushuDao m_shuwushuDao = new m_shuwushuDao();
        LinkedHashMap<String, String> m_shuwushuBean_LinkedHashMap = m_shuwushuDao.selectAll();

        StringBuilder requestBody = new StringBuilder();

		String line;
		String res = "";
        // 读取请求体并存储到StringBuilder中
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
                requestBody.append(line).append(System.lineSeparator());

    			if (!StringUtils.isEmpty(line)) {
    				// 使用 TAB 分隔符拆分字符串
    				String[] values = line.split("\t");

					if (values.length < 2) {
						values = line.split(",");
					}
					if (values.length < 12) {
						res = res + "一条数据需要12个元素：" + values[0] + "<br>";
						continue;
					}


    			 	/*
    				 * 税务署チェック
    				 */
    		        if (!m_shuwushuBean_LinkedHashMap.containsKey(values[11])) {
						res = res + "税務署番号NG：" + values[0] + " " + values[10] + " " + values[11] + "<br>";
						continue;
    		        }
    		        if (!m_shuwushuBean_LinkedHashMap.get(values[11]).contains(values[10])) {
						res = res + "税務署番号NG：" + values[0] + " " + values[10] + " " + values[11] + "<br>";
						continue;
    		        }

					LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao.selectAll_ByLike_CompanyName(values[0]);
			        for (Map.Entry<String, t_etax_account_infoExBean> entry : LinkedHashMap_t_etax_account_infoExBean.entrySet()) {
			            String key = entry.getKey();
			            t_etax_account_infoBean value = entry.getValue();
    					if (!value.getCompanyName_Chinese().contains("（删除20")) {
    						res = res + "同名公司或个人已经存在：" + values[0] + "<br>";
    						continue;
    					}
			        }

    			}
            }
        }

        logger.info("数据检查结果：" + res);
        if (!StringUtils.isEmpty(res)) {
    		out.print("{\"res\":\"" + res + "\"}");
			return;

        }

        res = "";
        int count = 0;
        // 第二次读取请求体内容
        try (BufferedReader reader2 = new BufferedReader(new StringReader(requestBody.toString()))) {
            while ((line = reader2.readLine()) != null) {

    			if (!StringUtils.isEmpty(line)) {

    				// 使用 TAB 分隔符拆分字符串
    				String[] values = line.split("\t");
    				try {

    					if (StringUtils.isEmpty(values[3])) {
    						if ("个人".equals(values[1])) {
    							values[3] = values[0];
    						}
    					}

    					/*
    					 * 登録
    					 */
    					String yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

    					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
    					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
    					t_etax_account_infoBean.setUser_id("piliang_" + yyyymmddhhmmss);

    					//		公司或个人名（中文）		0
    					//		公司或个人		1
    					//		JCT号（不要T）		2
    					//		代表人名（中文）		3
    					//		个人生日		4
    					//		解约纳税管理人		5
    					//		解任納税管理人会社名		6
    					//		解任納税管理人代表者		7
    					//		解任納税管理人住所		8
    					//		解任納税地		9
    					//		解任税務署		10
    					//		解任税務署番号		11
    					//		Etax		12
    					//		etax_pw		13



    					t_etax_account_infoBean.setCompanyName_Chinese(values[0]);
    					t_etax_account_infoBean.setUser_type(values[1]);
    					if ("个人".equals(values[1])) {

    						// 定义日期格式化器
    						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    						// 解析日期字符串
    						LocalDate date = LocalDate.parse(values[4], formatter);

    						// 提取年、月、日
    						int year = date.getYear();
    						int month = date.getMonthValue();
    						int day = date.getDayOfMonth();

    						t_etax_account_infoBean.setCompany_YYYY("" + year);
    						t_etax_account_infoBean.setCompany_MM("" + month);
    						t_etax_account_infoBean.setCompany_DD("" + day);
    					}

    					t_etax_account_infoBean.setDaibiaoName_Chinese(values[3]);

    					t_etax_account_infoBean.setSyouninn_status("待处理");//承認無

    					t_etax_account_infoBean.setActivation_code(yyyymmdd_count);




    					t_etax_account_infoBean.setNashui_guanliren(values[5]);
    					t_etax_account_infoBean.setNashui_CompanyName(values[6]);
    					t_etax_account_infoBean.setNashui_DaibiaoName(values[7]);
    					t_etax_account_infoBean.setNashui_address(values[8]);
    					t_etax_account_infoBean.setNashui_di_biangengqian(values[9]);
    					t_etax_account_infoBean.setNashui_shuiwushu_biangengqian(values[10]);
    					t_etax_account_infoBean.setNashui_shuiwushu_fanhao(values[11]);

    					t_etax_account_infoBean.setEtax_pw_flag("0");
    					t_etax_account_infoBean.setYaoqing_no(user_id);

    					//		etax_pw		13
    					if (values.length >= 13) {
    						if(!StringUtils.isEmpty(values[13])){
    							t_etax_account_infoBean.setEtax_pw(values[13]);
    						}
    					} else {
    				        String etax_pw = "ps" + FuncUtils.generateRandomNumber(8);
    				        t_etax_account_infoBean.setEtax_pw(etax_pw);
    					}



    					t_etax_account_infoDao.INSERT(t_etax_account_infoBean);


    					/*
    					 *
    					 */
    					ExeDb.exe_activation(yyyymmdd_count);

    					/*
    					 * UPDATE t_etax_account_res SET HoujinBangou='9700150118570', InvoiceBangou='T9700150118570' WHERE yyyymmdd_count='20240603990001';
    					 */
    					String HoujinBangou = values[2].toUpperCase().replace("T", "");
    					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, HoujinBangou);
    					t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, "T" + HoujinBangou);

    					ExeDb.set_pianjiaming(yyyymmdd_count);
    					ExeDb.set_DaibiaoName_English(yyyymmdd_count);
    					ExeDb.setInfoForAPI(yyyymmdd_count, t_etax_account_infoDao);

    					t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
    					if (StringUtils.isEmpty(EtaxAccountInfoBean.getCompanyName_English())) {
    						User_infoBean User_infoBean = LicenseDao.select(user_id);
    						t_etax_account_infoDao.DELETE(User_infoBean, yyyymmdd_count);

    						res = res + "税务局API同步失败<br>";
    						logger.info(res);
    						continue;

    					}

    					t_etax_account_resDao.DELETE_res(yyyymmdd_count);
    					ExeDb.exe_activation(yyyymmdd_count);
    					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, HoujinBangou);
    					t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, "T" + HoujinBangou);

    					//		Etax		12
    					if (values.length >= 13) {
    						if (!StringUtils.isEmpty(values[12])) {
    							t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "bangou", values[12]);
    						}
    					}

    					++count;



    					/*
    					 *Etax番号取得
    					 */
//    					t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);
//    					pandaWebDriver testNoWEB = new pandaWebDriver();
//    					testNoWEB.getEtaxNo();


    					/*
    					 *NCC作成
    					 */
//    					zhuandailiIO.get_zhuandaili_ncc(path, yyyymmddhhmmss, yyyymmdd_count);



    				} catch (Exception e) {
    					// TODO 登录失败怎么办
    					e.printStackTrace();
    					res = res + "転代理失败：" + e.getMessage() + values[0] + "<br>";
    					logger.info(res);
    					out.print("{\"res\":\"" + res + "\"}");
    					return;
    				}
    			}




            }
        }

		res = res + "数据登录完了"+count+"件<br>";
		logger.info(res);
		out.print("{\"res\":\"" + res + "\"}");
		return;


//		/*
//		 * 结果下载
//		 */
//		path = path + "/output/" + yyyymmddhhmmss;
//		// 创建表示文件夹的File对象
//		File file = new File(path);
//		if (file.exists()) {
//
//			/*
//			 * 生成文件ZIP
//			 */
//			// 源文件夹的路径
//			String sourceFolderPath = path;
//			// 目标ZIP文件的路径
//			String targetZipFilePath = path + ".zip";
//			try {
//				// 创建一个输出流，将文件写入ZIP文件
//				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
//				ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//				// 调用递归方法将文件夹及其内容添加到ZIP文件中
//				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);
//
//				// 关闭ZIP文件输出流
//				zipOut.close();
//				fos.close();
//
//				logger.info("ZIP文件创建成功：" + targetZipFilePath);
//				out.print("{\"res\":\"" + "fileDataTools/GetZhuandaili/output/" + yyyymmddhhmmss + ".zip" + "\"}");
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		} else {
//			out.print("{\"res\":\"结果文件不存在\"}");
//		}

	}


	private void del_t_etax_account_info(HttpServletRequest req, PrintWriter out) throws Exception {

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

		int count = 0;
		String line;
		String res = "";
        // 读取请求体并存储到StringBuilder中
        try (BufferedReader reader = req.getReader()) {
            while ((line = reader.readLine()) != null) {
            	line = line.replace(" ", "");
    			if (!StringUtils.isEmpty(line)) {
    				++count;
    				String msg = "";
    				int del_count = t_etax_account_infoDao.Update_Del(line);
    				if (del_count == 1) {
        				msg = "伦理删除OK：" + line + ",伦理删除"+del_count+"件";
    				} else {
        				msg = "伦理删除NG：" + line + ",伦理删除"+del_count+"件";

    				}

					res = res + msg + "<br>";
			        logger.info(msg);

    			}
            }
        }

        logger.info("处理件数：" + count+"件");
		out.print("{\"res\":\"" + res + "\"}");
		return;

	}

	private void getZhuandaili(HttpServletRequest req, PrintWriter out) throws Exception {





		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);
		String path = getServletContext().getRealPath("/fileDataTools/GetZhuandaili");


		String user_id = req.getParameter("license");
		User_infoDao LicenseDao = new User_infoDao();

		BufferedReader reader = req.getReader();

		m_sequenceDao m_sequenceDao = new m_sequenceDao();
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();

		String line;
		String res = "";
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {

				// 使用 TAB 分隔符拆分字符串
				String[] values = line.split(",");
				values[0] = line;
				try {
//					if (values.length < 2) {
//						values = line.split("\t");
//					}
//					if (values.length != 1) {
//						//					logger.info("setPDSK完了：\n" + res);
//						res = "一条数据需要1个元素：" + values[0];
//						logger.debug(res);
//						out.print("{\"res\":\"" + res + "\"}");
//						return;
//					}

					LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao.selectAll_ByLike_CompanyName(values[0]);
					if (LinkedHashMap_t_etax_account_infoExBean.size() ==0) {
						res = "对象数据不存在：" + values[0];
						logger.debug(res);
						out.print("{\"res\":\"" + res + "\"}");
						return;

					} else if (LinkedHashMap_t_etax_account_infoExBean.size() >1) {
						res = "对象数据存在复数个：" + values[0];
						logger.debug(res);
						out.print("{\"res\":\"" + res + "\"}");
						return;

					}

					String yyyymmdd_count = "";
			        for (Entry<String, t_etax_account_infoExBean> outerEntry : LinkedHashMap_t_etax_account_infoExBean.entrySet()) {
			            t_etax_account_infoBean t_etax_account_infoBean = outerEntry.getValue();
			            yyyymmdd_count = t_etax_account_infoBean.getYyyymmdd_count();
			            break;
			        }

					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);

//					t_etax_account_infoBean.setNashui_guanliren(values[5]);
//					t_etax_account_infoBean.setNashui_CompanyName(values[6]);
//					t_etax_account_infoBean.setNashui_DaibiaoName(values[7]);
//					t_etax_account_infoBean.setNashui_address(values[8]);
//					t_etax_account_infoBean.setNashui_di_biangengqian(values[9]);
//					t_etax_account_infoBean.setNashui_shuiwushu_biangengqian(values[10]);
//					t_etax_account_infoBean.setNashui_shuiwushu_fanhao(values[11]);

//					t_etax_account_infoDao.Update_nashui(t_etax_account_infoBean);


					/*
					 *NCC作成
					 */
					zhuandailiIO.get_zhuandaili_ncc(path, yyyymmddhhmmss, yyyymmdd_count);

				} catch (Exception e) {
					// TODO 登录失败怎么办
					e.printStackTrace();
					res = "転代理失败：" + e.getMessage() + values[0];
					logger.debug(res);
					out.print("{\"res\":\"" + res + "\"}");
					return;
				}
			}



		}



		/*
		 * 结果下载
		 */
		path = path + "/output/" + yyyymmddhhmmss;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
				// 创建一个输出流，将文件写入ZIP文件
				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
				ZipOutputStream zipOut = new ZipOutputStream(fos);

				// 调用递归方法将文件夹及其内容添加到ZIP文件中
				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

				// 关闭ZIP文件输出流
				zipOut.close();
				fos.close();

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{\"res\":\"" + "fileDataTools/GetZhuandaili/output/" + yyyymmddhhmmss + ".zip" + "\"}");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}

	}

    // 解析时间字符串
    private static long parseTimeString(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            Date date = sdf.parse(timeString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }
    // 将时间转换为指定格式的字符串
    private static String formatDate(long timeMillis) {
        // 创建 SimpleDateFormat 实例，指定要转换的时间格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        // 将时间毫秒数转换为 Date 对象
        Date date = new Date(timeMillis);

        // 使用 SimpleDateFormat 格式化 Date 对象，并返回格式化后的字符串
        return sdf.format(date);
    }

    public static void main(String[] args) {




    	  // 创建HttpClient
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().disableContentCompression().build()) {

            // 构造 GET 请求
            String url = "https://chatgpt.com";
            HttpGet getRequest = new HttpGet(url);

            // 设置请求头
//            getRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
//            getRequest.setHeader("Accept-Encoding", "gzip, deflate, br, zstd");
//            getRequest.setHeader("Accept-Language", "ja");
//            getRequest.setHeader("Cache-Control", "max-age=0");
//            getRequest.setHeader("Priority", "u=0, i");
//            getRequest.setHeader("Referer", "https://chatgpt.com/");
//            getRequest.setHeader("Sec-Ch-Ua", "\"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"");
//            getRequest.setHeader("Sec-Ch-Ua-Mobile", "?0");
//            getRequest.setHeader("Sec-Ch-Ua-Platform", "\"Windows\"");
//            getRequest.setHeader("Sec-Fetch-Dest", "document");
//            getRequest.setHeader("Sec-Fetch-Mode", "navigate");
//            getRequest.setHeader("Sec-Fetch-Site", "same-origin");
//            getRequest.setHeader("Upgrade-Insecure-Requests", "1");
//            getRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");

            // 执行请求并获取响应
            CloseableHttpResponse response = httpClient.execute(getRequest);
            try {
                // 获取响应实体
                HttpEntity entity = response.getEntity();

                // 将响应实体转换为字符串，指定 UTF-8 字符集
                String responseBody = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                logger.info("Response Body:\n" + responseBody);
            } finally {
                // 关闭响应
                response.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }




//		/*
//		 * 登录功能 附件
//		 */
//		try {
//			String zipFilePath = "C:\\Users\\Administrator\\Desktop\\証書付け版wd_impot\\zhengshu.zip";
//	        String outputFolderPath = "C:\\Users\\Administrator\\Desktop\\証書付け版wd_impot/output/";
//	        File outputFolder = new File(outputFolderPath);
//	        File zipFile = new File(zipFilePath);
//	        FuncUtils.unzip(zipFilePath, outputFolderPath);
////	        FuncUtils.unzip(zipFile, outputFolderPath);
//
//
//
//
//		} catch (Throwable e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}
    }

	private void GetZhengshu(HttpServletRequest req, PrintWriter out) throws UnsupportedEncodingException, IOException, ServletException {
		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataTools/GetZhengshu");

		/*
		 * license確認
		 */
		// 不要

		/*
		 * 附件
		 */
		String inputFolderPath = path + "/impot/" + yyyymmddhhmmss;
		String outputFolderPath = path + "/output/" + yyyymmddhhmmss;
		FuncUtils.filesUp_yyyymmddhhmmss(req, path + "/impot", yyyymmddhhmmss);

		File directory = new File(outputFolderPath);
		directory.mkdir();
		FuncUtils.unzipAllZipsInFolder(inputFolderPath, inputFolderPath);

		String path_output = outputFolderPath;
		String path_impot = inputFolderPath;
		String path_moban = path + "/moban/";



		String exe_type = req.getParameter("hidden_user_type");
//		String exe_type = "itax";
//			static String exe_type = "bps";

		set_shuiwu_quanxian_daili_zhengshu.exe(exe_type, path_output, path_impot, path_moban);



		/*
		 * 结果下载
		 */
		path = outputFolderPath;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
				// 创建一个输出流，将文件写入ZIP文件
				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
				ZipOutputStream zipOut = new ZipOutputStream(fos);

				// 调用递归方法将文件夹及其内容添加到ZIP文件中
				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

				// 关闭ZIP文件输出流
				zipOut.close();
				fos.close();

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{\"res\":\"" + "/GetZhengshu/output/" + yyyymmddhhmmss + ".zip" + "\"}");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private void setPDSK(HttpServletRequest req, PrintWriter out) throws IOException {

		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		BufferedReader reader = req.getReader();

		String line;
		String res = "";
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {
				String[] values = line.split(",PDSK");
				if (values.length != 2) {
					values = line.split("	PDSK");
					if (values.length != 2) {
						res = "更新NG，格式错误 " + values[0];
						logger.info(res);
						out.print(res);
						return;
					}

				}


				String PDSK = "PDSK" + values[1];

				LinkedHashMap<String, t_etax_account_resExBean> t_etax_account_resExBeanLinkedHashMap= t_etax_account_resDao.selecExWhereKeyValue("CompanyName_Chinese", values[0]);
				if (t_etax_account_resExBeanLinkedHashMap.size() ==1) {
			        for (Entry<String, t_etax_account_resExBean> outerEntry : t_etax_account_resExBeanLinkedHashMap.entrySet()) {
			        	t_etax_account_resExBean t_etax_account_resExBean = outerEntry.getValue();
						if (!StringUtils.isEmpty(t_etax_account_resExBean.getPdsk())) {
							res = res + "更新NG,本数据PDSK已经被设定\n";
							logger.info(res);
							out.print(res);
							return;
						}
			        }
				}
				t_etax_account_resExBeanLinkedHashMap= t_etax_account_resDao.selecExWhereKeyValue("CompanyName_English", values[0]);
				if (t_etax_account_resExBeanLinkedHashMap.size() ==1) {
			        for (Entry<String, t_etax_account_resExBean> outerEntry : t_etax_account_resExBeanLinkedHashMap.entrySet()) {
			        	t_etax_account_resExBean t_etax_account_resExBean = outerEntry.getValue();
						if (!StringUtils.isEmpty(t_etax_account_resExBean.getPdsk())) {
							res = res + "更新NG,本数据PDSK已经被设定\n";
							logger.info(res);
							out.print(res);
							return;
						}
			        }
				}


				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
				if (StringUtils.isEmpty(yyyymmdd_count)) {
					int count = t_etax_account_resDao.UpdatePDSK(values[0], PDSK);
					if (count == 1) {
						res = res + "更新OK," + count + "件,"+values[0] +"\n";
					} else {
						res = res + "更新NG," + count + "件,"+values[0] +"\n";

					}
				} else {
					res = res + "更新NG,本PDSK番号使用中,"+values[0] +"\n";
				}
			}

		}

		logger.info(res);
//		out.print("{\"res\":\"" + res + "\"}");
		out.print(res);

	}

	private void getShenqingJieguo(HttpServletRequest req, PrintWriter out) throws Exception {
//		// 获取当前日期时间
//		Date currentDate = new Date();
//		// 设置日期时间格式
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		// 格式化日期时间
//		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataTools/getShenqingJieguo");


		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");
		String chuli_type = req.getParameter("hidden_shenqing_type");
		String hidden_shuilishi_del_type = req.getParameter("hidden_shuilishi_del_type");
		String PDSK = req.getParameter("PDSK");


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
		t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


		/*
		 * license確認
		 */
		// 不要

		String mainPath = path + "/output" + yyyymmddhhmmss;
		File directory = new File(mainPath);
		if (!directory.exists() || !directory.isDirectory()) {
			directory.mkdirs();
		}


		/*
		 * 文件生成
		 */
		BufferedReader reader = req.getReader();
		if(PDSK != null) {
			reader = new BufferedReader(new StringReader(PDSK));
		}


		HashMap<String, String> PDSK_HashMap = new HashMap<>();
		String res = "";
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {

				String[] value = line.split("-");
				//2 生成695-699的テンプレートフォルダ
				int countS = -1;
				int countE = -1;

				if (value.length == 2) {
					//2 生成695-699的テンプレートフォルダ
					countS = Integer.parseInt(value[0].replace("PDSK", ""));
					countE = Integer.parseInt(value[1].replace("PDSK", ""));
				}

		        for (int k = countS; k <= countE; k++) {

					PDSK= line;
					if (countS != -1) {
						PDSK= "PDSK" + String.format("%06d", k);
					}
					++count;
					logger.info("处理个数: " + count + "個目 " + PDSK);

					//去除重复的
					if (PDSK_HashMap.containsKey(PDSK)) {
						continue;
					}


//					String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
					t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("PDSK", PDSK);
					String yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();
					PDSK_HashMap.put(PDSK, yyyymmdd_count);
					if (StringUtils.isEmpty(yyyymmdd_count)) {
						res = res + PDSK + "：PDSK不存在<br>";
						continue;
					}


					if ("中間申告".equals(chuli_type)) {

						t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setYyyy("2025");
						t_etax_jieguoExBean.setChuli_type(chuli_type);

		        		t_etax_zhongjian_shengaoDao t_etax_zhongjian_shengaoDao = new t_etax_zhongjian_shengaoDao();
						t_etax_jieguoExBean = t_etax_zhongjian_shengaoDao.select_where_PK(t_etax_jieguoExBean);
						if (StringUtils.isEmpty(t_etax_jieguoExBean.getYyyymmdd_count())) {
							t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
							t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
							t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
							t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
							/*
							 *取得：お知らせ・受信通知
							 */
							pandaWebDriver testNoWEB = new pandaWebDriver("");
//							pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


							t_etax_jieguoBean t_etax_jieguoBean = null;
							if ("中間申告".equals(chuli_type)) {
								t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


							}

							if(t_etax_jieguoBean == null) {
								res = res + PDSK + "：没有申请结果<br>";
								continue;

							} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
								res = res + PDSK + "：国税局系统维护中<br>";
								continue;

							} else {
								t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoBean.setYyyy("2025");
								t_etax_jieguoBean.setChuli_type(chuli_type);
								t_etax_jieguoBean.setFile_name(PDSK);

					        	t_etax_zhongjian_shengaoDao.INSERT(t_etax_jieguoBean);


							}


						} else if (!StringUtils.isEmpty(t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji())
								&& !"0".equals(t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji())
								&& t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji().indexOf("-") == -1) {

							if (StringUtils.isEmpty(t_etax_jieguoExBean.getNoufu_kubun())) {
								//TODO
								t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
								t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
								t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
								t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
								/*
								 *取得：お知らせ・受信通知
								 */
								pandaWebDriver testNoWEB = new pandaWebDriver(null);
//								pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


								t_etax_jieguoBean t_etax_jieguoBean = null;
								if ("中間申告".equals(chuli_type)) {
									t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


								}
								if(t_etax_jieguoBean == null) {
									res = res + PDSK + "：没有申请结果<br>";
									continue;

								} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
									res = res + PDSK + "：国税局系统维护中<br>";
									continue;

								} else {
									t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
									t_etax_jieguoBean.setYyyy("2025");
									t_etax_jieguoBean.setChuli_type(chuli_type);
									t_etax_jieguoBean.setFile_name(PDSK);

									t_etax_zhongjian_shengaoDao.UPDATE_dianzi_nashui(t_etax_jieguoBean);


								}
							}


						}

						shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
						String directoryPath = shengaojieguo_chuli.exe_zhongjian_shengao(mainPath, chuli_type, k, k, line);

					} else {

						t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setYyyy("2025");
						t_etax_jieguoExBean.setChuli_type(chuli_type);
						t_etax_jieguoExBean = t_etax_jieguoDao.select_where_PK(t_etax_jieguoExBean);
						if (StringUtils.isEmpty(t_etax_jieguoExBean.getYyyymmdd_count())) {
							t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
							t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
							t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
							t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
							/*
							 *取得：お知らせ・受信通知
							 */
							pandaWebDriver testNoWEB = new pandaWebDriver("");
//							pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


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
								t_etax_jieguoBean.setYyyy("2025");
								t_etax_jieguoBean.setChuli_type(chuli_type);
								t_etax_jieguoBean.setFile_name(PDSK);

					        	Document doc = Jsoup.parse(t_etax_jieguoBean.getHtml());
					            String event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
					            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
					            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
								t_etax_jieguoBean.setEvent(event);
								t_etax_jieguoBean.setTaxable_amount(taxable_amount);
								t_etax_jieguoBean.setTotal_tax_amount(total_tax_amount.replace("△", "-"));

								t_etax_jieguoDao.INSERT(t_etax_jieguoBean);

							}


						} else if (!StringUtils.isEmpty(t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji())
								&& !"0".equals(t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji())
								&& t_xiaofeishui_shengaoBean.getQueren_shengao_yingjiao_xiaofeishui_heji().indexOf("-") == -1) {

							if (StringUtils.isEmpty(t_etax_jieguoExBean.getNoufu_kubun())) {
								//TODO
								t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
								t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
								t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
								t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
								/*
								 *取得：お知らせ・受信通知
								 */
								pandaWebDriver testNoWEB = new pandaWebDriver(null);
//								pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


								t_etax_jieguoBean t_etax_jieguoBean = null;
								if ("申告".equals(chuli_type)) {
									t_etax_jieguoBean = testNoWEB.getShenqingJieguo_dianzi_nashui(t_etax_jieguoExBean);

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
									t_etax_jieguoBean.setYyyy("2025");
									t_etax_jieguoBean.setChuli_type(chuli_type);
									t_etax_jieguoBean.setFile_name(PDSK);

									t_etax_jieguoDao.UPDATE_dianzi_nashui(t_etax_jieguoBean);


								}
							}


						}

						shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
						String directoryPath = shengaojieguo_chuli.exe(mainPath, chuli_type, k, k, line);

						if ("申告".equals(chuli_type) && hidden_shuilishi_del_type.contains("shuilishi_del_YES")) {
							//删除税理士信息
							PDFToImageToPDF.pdfToImageToPDF(directoryPath, hidden_shuilishi_del_type);

						}
					}



		        }
			}
		}

//		if (!StringUtils.isEmpty(res)) {
//			out.print("{\"info\":\""+res+"\"}");
//			return;
//		}




		/*
		 * 结果下载
		 */
		path = mainPath;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			// 获取文件夹中的所有文件和子目录
			File[] files = file.listFiles();
			// 检查文件夹是否为空
            if (files != null && files.length > 0) {

            } else {
    			out.print("{\"res\":\""+res+"\"}");
    			return;
            }


			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
	        	FuncUtils.zipDirectory(sourceFolderPath, targetZipFilePath);

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{"
						+ "\"res\":\"" + "fileDataTools/getShenqingJieguo/output" + yyyymmddhhmmss + ".zip" + "\""
						+ ", \"info\":\""+res+"\""
						+ "}");


			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private void getShenqingJieguoTatujin(HttpServletRequest req, PrintWriter out) throws Exception {
//		// 获取当前日期时间
//		Date currentDate = new Date();
//		// 设置日期时间格式
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		// 格式化日期时间
//		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataTools/getShenqingJieguo");


		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");
		String chuli_type = req.getParameter("hidden_shenqing_type");
		String hidden_shuilishi_del_type = req.getParameter("hidden_shuilishi_del_type");
		String tatujin_id = req.getParameter("tatujin_id");


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
		t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


		/*
		 * license確認
		 */
		// 不要

		String mainPath = path + "/output" + yyyymmddhhmmss;
		File directory = new File(mainPath);
		if (!directory.exists() || !directory.isDirectory()) {
			directory.mkdirs();
		}


		/*
		 * 文件生成
		 */
		BufferedReader reader = req.getReader();
		if(tatujin_id != null) {
			reader = new BufferedReader(new StringReader(tatujin_id));
		}


		HashMap<String, String> PDSK_HashMap = new HashMap<>();
		String res = "";
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {

				String[] value = line.split("-");
				//2 生成695-699的テンプレートフォルダ
				int countS = -1;
				int countE = -1;

				if (value.length == 2) {
					//2 生成695-699的テンプレートフォルダ
					countS = Integer.parseInt(value[0].replace("PDSK", ""));
					countE = Integer.parseInt(value[1].replace("PDSK", ""));
				}

		        for (int k = countS; k <= countE; k++) {

					tatujin_id= line;
					if (countS != -1) {
						tatujin_id= "PDSK" + String.format("%06d", k);
					}
					++count;
					logger.info("处理个数: " + count + "個目 " + tatujin_id);

					//去除重复的
					if (PDSK_HashMap.containsKey(tatujin_id)) {
						continue;
					}


//					String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("tatujin_id", tatujin_id);
					String yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();
					PDSK_HashMap.put(tatujin_id, yyyymmdd_count);
					if (StringUtils.isEmpty(yyyymmdd_count)) {
						res = res + tatujin_id + "：tatujin_id不存在<br>";
						continue;
					}


					if ("中間申告".equals(chuli_type)) {

						t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setYyyy("2025");
						t_etax_jieguoExBean.setChuli_type(chuli_type);

		        		t_etax_zhongjian_shengaoDao t_etax_zhongjian_shengaoDao = new t_etax_zhongjian_shengaoDao();
						t_etax_jieguoExBean = t_etax_zhongjian_shengaoDao.select_where_PK(t_etax_jieguoExBean);
						if (StringUtils.isEmpty(t_etax_jieguoExBean.getYyyymmdd_count())) {
							t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
							t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
							t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
							/*
							 *取得：お知らせ・受信通知
							 */
							pandaWebDriver testNoWEB = new pandaWebDriver("");
//							pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


							t_etax_jieguoBean t_etax_jieguoBean = null;
							if ("中間申告".equals(chuli_type)) {
								t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


							}

							if(t_etax_jieguoBean == null) {
								res = res + tatujin_id + "：没有申请结果<br>";
								continue;

							} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
								res = res + tatujin_id + "：国税局系统维护中<br>";
								continue;

							} else {
								t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoBean.setYyyy("2025");
								t_etax_jieguoBean.setChuli_type(chuli_type);
								t_etax_jieguoBean.setFile_name(tatujin_id);

					        	t_etax_zhongjian_shengaoDao.INSERT(t_etax_jieguoBean);


							}


						} else {

							if (StringUtils.isEmpty(t_etax_jieguoExBean.getNoufu_kubun())) {
								//TODO
								t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
								t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
								t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
								/*
								 *取得：お知らせ・受信通知
								 */
								pandaWebDriver testNoWEB = new pandaWebDriver(null);
//								pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


								t_etax_jieguoBean t_etax_jieguoBean = null;
								if ("中間申告".equals(chuli_type)) {
									t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


								}
								if(t_etax_jieguoBean == null) {
									res = res + tatujin_id + "：没有申请结果<br>";
									continue;

								} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
									res = res + tatujin_id + "：国税局系统维护中<br>";
									continue;

								} else {
									t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
									t_etax_jieguoBean.setYyyy("2025");
									t_etax_jieguoBean.setChuli_type(chuli_type);
									t_etax_jieguoBean.setFile_name(tatujin_id);

									t_etax_zhongjian_shengaoDao.UPDATE_dianzi_nashui(t_etax_jieguoBean);


								}
							}


						}

						shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
						String directoryPath = shengaojieguo_chuli.exe_zhongjian_shengao(mainPath, chuli_type, k, k, line);

					} else {

						t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setYyyy("2025");
						t_etax_jieguoExBean.setChuli_type(chuli_type);
						t_etax_jieguoExBean = t_etax_jieguoDao.select_where_PK(t_etax_jieguoExBean);
						if (StringUtils.isEmpty(t_etax_jieguoExBean.getYyyymmdd_count())) {
							t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
							t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
//							t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
							t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type_zixuan());
							/*
							 *取得：お知らせ・受信通知
							 */
							pandaWebDriver testNoWEB = new pandaWebDriver("");
//							pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


							t_etax_jieguoBean t_etax_jieguoBean = null;
							if ("申告".equals(chuli_type)) {
//								t_etax_jieguoBean = testNoWEB.getShenqingJieguo(t_etax_jieguoExBean);
								t_etax_jieguoBean = testNoWEB.getShenqingJieguoTatujin(t_etax_jieguoExBean);



							} else if ("转代理".equals(chuli_type)) {
								t_etax_jieguoBean = testNoWEB.get_zhuandaili_jieguo(t_etax_jieguoExBean);

							}

							if(t_etax_jieguoBean == null) {
								res = res + tatujin_id + "：没有申请结果<br>";
								continue;

							} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
								res = res + tatujin_id + "：国税局系统维护中<br>";
								continue;

							} else {
								t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
								t_etax_jieguoBean.setYyyy("2025");
								t_etax_jieguoBean.setChuli_type(chuli_type);
								t_etax_jieguoBean.setFile_name(tatujin_id);

					        	Document doc = Jsoup.parse(t_etax_jieguoBean.getHtml());
					            String event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
					            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
					            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
								t_etax_jieguoBean.setEvent(event);
								t_etax_jieguoBean.setTaxable_amount(taxable_amount.replace("―", "0"));
								t_etax_jieguoBean.setTotal_tax_amount(total_tax_amount.replace("△", "-"));

								t_etax_jieguoDao.INSERT(t_etax_jieguoBean);

							}


						} else {

//							if (StringUtils.isEmpty(t_etax_jieguoExBean.getNoufu_kubun())) {
//								//TODO
//								t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
//								t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
//								t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
//								t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
//								/*
//								 *取得：お知らせ・受信通知
//								 */
//								pandaWebDriver testNoWEB = new pandaWebDriver(null);
////								pandaWebDriver2test testNoWEB = new pandaWebDriver2test();
//
//
//								t_etax_jieguoBean t_etax_jieguoBean = null;
//								if ("申告".equals(chuli_type)) {
//									t_etax_jieguoBean = testNoWEB.getShenqingJieguo_dianzi_nashui(t_etax_jieguoExBean);
//
//								} else if ("转代理".equals(chuli_type)) {
//									t_etax_jieguoBean = testNoWEB.get_zhuandaili_jieguo(t_etax_jieguoExBean);
//
//								}
//
//								if(t_etax_jieguoBean == null) {
//									res = res + tatujin_id + "：没有申请结果<br>";
//									continue;
//
//								} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
//									res = res + tatujin_id + "：国税局系统维护中<br>";
//									continue;
//
//								} else {
//									t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
//									t_etax_jieguoBean.setYyyy("2025");
//									t_etax_jieguoBean.setChuli_type(chuli_type);
//									t_etax_jieguoBean.setFile_name(tatujin_id);
//
//									t_etax_jieguoDao.UPDATE_dianzi_nashui(t_etax_jieguoBean);
//
//
//								}
//							}


						}

						shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
						String directoryPath = shengaojieguo_chuli.exeTatujin(mainPath, chuli_type, k, k, line);

						if ("申告".equals(chuli_type) && hidden_shuilishi_del_type.contains("shuilishi_del_YES")) {
							//删除税理士信息
							PDFToImageToPDF.pdfToImageToPDF(directoryPath, hidden_shuilishi_del_type);

						}
					}



		        }
			}
		}

//		if (!StringUtils.isEmpty(res)) {
//			out.print("{\"info\":\""+res+"\"}");
//			return;
//		}




		/*
		 * 结果下载
		 */
		path = mainPath;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			// 获取文件夹中的所有文件和子目录
			File[] files = file.listFiles();
			// 检查文件夹是否为空
            if (files != null && files.length > 0) {

            } else {
    			out.print("{\"res\":\""+res+"\"}");
    			return;
            }


			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
	        	FuncUtils.zipDirectory(sourceFolderPath, targetZipFilePath);

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{"
						+ "\"res\":\"" + "fileDataTools/getShenqingJieguo/output" + yyyymmddhhmmss + ".zip" + "\""
						+ ", \"info\":\""+res+"\""
						+ "}");


			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private void get_shengao_zhongjian(HttpServletRequest req, PrintWriter out) throws Exception {
//		// 获取当前日期时间
//		Date currentDate = new Date();
//		// 设置日期时间格式
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//		// 格式化日期时间
//		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataTools/getShenqingJieguo");


		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");
		String chuli_type = req.getParameter("hidden_shenqing_type");
		String hidden_shuilishi_del_type = req.getParameter("hidden_shuilishi_del_type");
//		String PDSK = req.getParameter("PDSK");

		String yyyymmdd_count = req.getParameter("yyyymmdd_count");


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
		t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
		t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


		/*
		 * license確認
		 */
		// 不要

		String mainPath = path + "/output" + yyyymmddhhmmss;
		File directory = new File(mainPath);
		if (!directory.exists() || !directory.isDirectory()) {
			directory.mkdirs();
		}


		/*
		 * 文件生成
		 */
		BufferedReader reader = req.getReader();
		if(yyyymmdd_count != null) {
			reader = new BufferedReader(new StringReader(yyyymmdd_count));
		}


		HashMap<String, String> yyyymmdd_count_HashMap = new HashMap<>();
		String res = "";
		String line;
		int count = 0;
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {

	        	yyyymmdd_count= line;
				++count;
				logger.info("处理个数: " + count + "個目 " + yyyymmdd_count);

				//去除重复的
				if (yyyymmdd_count_HashMap.containsKey(yyyymmdd_count)) {
					continue;
				}


//				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
//				t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("yyyymmdd_count", yyyymmdd_count);
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();
				yyyymmdd_count_HashMap.put(yyyymmdd_count, yyyymmdd_count);
				if (StringUtils.isEmpty(yyyymmdd_count)) {
					res = res + yyyymmdd_count + "：不存在<br>";
					continue;
				}


				if ("中間申告".equals(chuli_type)) {

					t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
					t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_jieguoExBean.setYyyy("2025");
					t_etax_jieguoExBean.setChuli_type(chuli_type);

	        		t_etax_zhongjian_shengaoDao t_etax_zhongjian_shengaoDao = new t_etax_zhongjian_shengaoDao();
					t_etax_jieguoExBean = t_etax_zhongjian_shengaoDao.select_where_PK(t_etax_jieguoExBean);
					if (StringUtils.isEmpty(t_etax_jieguoExBean.getYyyymmdd_count())) {
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
						t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
						t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
						/*
						 *取得：お知らせ・受信通知
						 */
						pandaWebDriver testNoWEB = new pandaWebDriver("");
//						pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


						t_etax_jieguoBean t_etax_jieguoBean = null;
						if ("中間申告".equals(chuli_type)) {
							t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


						}

						if(t_etax_jieguoBean == null) {
							res = res + yyyymmdd_count + "：没有申请结果<br>";
							continue;

						} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
							res = res + yyyymmdd_count + "：国税局系统维护中<br>";
							continue;

						} else {
							t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoBean.setYyyy("2025");
							t_etax_jieguoBean.setChuli_type(chuli_type);
							 if (StringUtils.isEmpty(t_etax_jieguoBean.getHtml())) {
								 t_etax_jieguoBean.setFile_name("skip");

							 } else {
								 t_etax_jieguoBean.setFile_name(yyyymmdd_count);

							 }

				        	t_etax_zhongjian_shengaoDao.INSERT(t_etax_jieguoBean);


						}


//					} else if (!"skip".equals(t_etax_jieguoExBean.getFile_name())) {
					} else {


						//TODO
						t_etax_jieguoExBean.setYyyymmdd_count(yyyymmdd_count);
						t_etax_jieguoExBean.setBangou(t_etax_account_infoExBean.getBangou());
						t_etax_jieguoExBean.setEtax_pw(t_etax_account_infoExBean.getEtax_pw());
						t_etax_jieguoExBean.setUser_type(t_etax_account_infoExBean.getUser_type());
						/*
						 *取得：お知らせ・受信通知
						 */
						pandaWebDriver testNoWEB = new pandaWebDriver(null);
//						pandaWebDriver2test testNoWEB = new pandaWebDriver2test();


						t_etax_jieguoBean t_etax_jieguoBean = null;
						if ("中間申告".equals(chuli_type)) {
							t_etax_jieguoBean = testNoWEB.getZhongjianShengao(t_etax_jieguoExBean);


						}
						if(t_etax_jieguoBean == null) {
							res = res + yyyymmdd_count + "：没有申请结果<br>";
							continue;

						} else if("国税局系统维护中".equals(t_etax_jieguoBean.getYyyymmdd_count())) {
							res = res + yyyymmdd_count + "：国税局系统维护中<br>";
							continue;

						} else {
							t_etax_jieguoBean.setYyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoBean.setYyyy("2025");
							t_etax_jieguoBean.setChuli_type(chuli_type);
							 if (StringUtils.isEmpty(t_etax_jieguoBean.getHtml())) {
								 t_etax_jieguoBean.setFile_name("skip");

							 } else {
								 t_etax_jieguoBean.setFile_name(yyyymmdd_count);

							 }

							t_etax_zhongjian_shengaoDao.delete_where_yyyymmdd_count(yyyymmdd_count);
				        	t_etax_zhongjian_shengaoDao.INSERT(t_etax_jieguoBean);


						}



					}

					if ("skip".equals(t_etax_jieguoExBean.getFile_name())) {

					} else {
						shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
						String directoryPath = shengaojieguo_chuli.exe_zhongjian_shengao(mainPath, chuli_type, 1, 1, line);

					}

				}


			}
		}

//		if (!StringUtils.isEmpty(res)) {
//			out.print("{\"info\":\""+res+"\"}");
//			return;
//		}




		/*
		 * 结果下载
		 */
		path = mainPath;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			// 获取文件夹中的所有文件和子目录
			File[] files = file.listFiles();
			// 检查文件夹是否为空
            if (files != null && files.length > 0) {

            } else {
    			out.print("{\"res\":\""+res+"\"}");
    			return;
            }


			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
	        	FuncUtils.zipDirectory(sourceFolderPath, targetZipFilePath);

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{"
						+ "\"res\":\"" + "fileDataTools/getShenqingJieguo/output" + yyyymmddhhmmss + ".zip" + "\""
						+ ", \"info\":\""+res+"\""
						+ "}");


			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


    // 获取文件夹的最后更新时间
    private static long getLastModifiedTime(Path path) {
        try {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            return attr.lastModifiedTime().toMillis();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

	private void setZhuandaili_async(HttpServletRequest req, PrintWriter out) throws Exception {
		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.getLastCreatedRecord(yyyymmddhhmmss);

		if (StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_Chinese())) {
			out.print("{"
					+ "  \"info\":\"\""
					+ "}");
			return;
		}
        String res = "登录完了：" + t_etax_account_infoExBean.getCompanyName_Chinese() + "<br>";
		out.print("{"
				+ "  \"info\":\"" + res + "\""
				+ "}");

	}


	private void getShenqingJieguo_async(HttpServletRequest req, PrintWriter out) throws Exception {
		String path = getServletContext().getRealPath("/fileDataTools/getShenqingJieguo");
		String yyyymmddhhmmss = req.getParameter("yyyymmddhhmmss");

		String res ="";




        // 指定要检查的路径
        Path sourceFolderPath = Paths.get(path + "/output" + yyyymmddhhmmss);
//        Path sourceFolderPath = Paths.get("E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileDataTools\\getShenqingJieguo\\output20240608212409");



        // 获取文件或文件夹的创建时间
        FileTime creationTime = Files.readAttributes(sourceFolderPath, BasicFileAttributes.class).creationTime();
        yyyymmddhhmmss = formatDate(creationTime.toMillis());


        try (Stream<Path> paths = Files.list(sourceFolderPath)) {
            // ディレクトリを取得し、最終更新時間でソートする
            List<Path> sortedDirectories = paths
                .filter(Files::isDirectory)
                .sorted(Comparator.comparingLong(ToolsReplaceLogic::getLastModifiedTime))
                .collect(Collectors.toList());

            // 各ディレクトリの名前と、直前のディレクトリの更新から現在の更新までの差を秒単位で出力する
            for (int i = 0; i < sortedDirectories.size(); i++) {
                Path currentDir = sortedDirectories.get(i);
                long currentTime = getLastModifiedTime(currentDir);
                long prevTime = i > 0 ? getLastModifiedTime(sortedDirectories.get(i - 1)) : parseTimeString(yyyymmddhhmmss);
                long timeDifference = TimeUnit.MILLISECONDS.toSeconds(currentTime - prevTime);
                // 将时间差除以60以转换为分钟
                double minutes = (double) timeDifference / 60;
                // 保留小数点后一位
                minutes = Math.round(minutes * 10) / 10.0;
//                logger.info(currentDir.getFileName() + "、" + minutes + "分钟" + formatDate(currentTime));
                String PDSK = currentDir.getFileName().toString().split("_")[0].substring(0, 8).replace("PDSK", "PDSK23");
                res = res + "处理时间：" + minutes + "分钟、" + PDSK + "、" + formatDate(currentTime) + "-" + formatDate(prevTime) + "<br>";
//                res = res + "处理时间：" + minutes + "分钟、" +  PDSK + "、" + formatDate(prevTime) + "<br>";
            }



        } catch (IOException e) {
            e.printStackTrace();
        }


		out.print("{"
				+ "  \"info\":\"" + res + "\""
				+ "}");






	}



	private void getXiaofeishuiYidongjie(HttpServletRequest req, PrintWriter out) throws IOException {
		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataTools/getXiaofeishuiYidongjie");

		/*
		 * license確認
		 */
		// 不要

		String mainPath = path + "/output" + yyyymmddhhmmss;
		File directory = new File(mainPath);
		if (!directory.exists() || !directory.isDirectory()) {
			directory.mkdirs();
		}


		/*
		 * 文件生成
		 */
		BufferedReader reader = req.getReader();

		String line;
		while ((line = reader.readLine()) != null) {
			if (!StringUtils.isEmpty(line)) {

				String[] value = line.split("-");

				//2 生成695-699的テンプレートフォルダ
				int countS = Integer.parseInt(value[0]);
				int countE = Integer.parseInt(value[1]);

				ConsumptionTaxIO.exe(yyyymmddhhmmss, path, countS, countE);
			}



		}


		/*
		 * 结果下载
		 */
		path = mainPath;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
				// 创建一个输出流，将文件写入ZIP文件
//				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
//				ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//				// 调用递归方法将文件夹及其内容添加到ZIP文件中
//				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);
//
//				// 关闭ZIP文件输出流
//				zipOut.close();
//				fos.close();


	        	FuncUtils.zipDirectory(sourceFolderPath, targetZipFilePath);


				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{\"res\":\"" + "fileDataTools/getXiaofeishuiYidongjie/output" + yyyymmddhhmmss + ".zip" + "\"}");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private void getTextReplace(HttpServletRequest req, PrintWriter out) {
		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataToolsReplace");

		/*
		 * license確認
		 */
		// 不要

		/*
		 * 登录功能 附件
		 */
		try {
			FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		 * 获取上传的多个文件部分
		 */
		Map<String, String> textFiles = new HashMap<>();
		Map<String, String> excelData = new HashMap<>();
		Map<String, Map<String, String>> excelDataHashMap = new HashMap<>();
		File directory = new File(path + "/" + yyyymmddhhmmss);

		if (!directory.exists() || !directory.isDirectory()) {
			logger.info("指定的路径不是一个有效的目录。");
			return;
		}

		File[] files = directory.listFiles();

		if (files == null || files.length == 0) {
			logger.info("目录下没有文件。");
			return;
		}

		for (File file : files) {
			if (file.isFile()) {
				String fileName = file.getName();
				String fileExtension = FuncUtils.getFileExtension(fileName);

				try {
					if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
						FileInputStream fis = new FileInputStream(file);
						Workbook workbook = WorkbookFactory.create(fis);
						Sheet sheet = workbook.getSheetAt(0);
						// 遍历每一行
						Iterator<Row> rowIterator = sheet.iterator();
						while (rowIterator.hasNext()) {
							Row row = rowIterator.next();
							// 获取 A 列和 B 列的数据
							Cell cellA = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
							Cell cellB = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

							//							// 跳过第一行（假设第一行为标题）
							//							if (row.getRowNum() == 0) {
							//								continue;
							//							}

							// 将 A 列和 B 列的数据存储到 excelData HashMap
							String key = (cellA != null) ? cellA.toString() : "";
							String value = "";

							if ("#課税資産の譲渡等の対価の額#".equals(key)) {
								key = key;
							}

							if (cellB != null) {
								if (cellB.getCellType() == CellType.FORMULA) {
									// 如果单元格中包含公式，则计算并输出结果
									value = FuncUtils.evaluateFormulaCell(cellB, workbook);
								} else {
									// 如果是其他类型的单元格，直接输出值
									value = cellB.toString();
								}

							}

							excelData.put(key, value);
						}
						excelDataHashMap.put(fileName, excelData);
						excelData = new HashMap<>();

					} else {
						// 处理扩展名为text的文件
						String fileContent = FuncUtils.readFileContent(file);
						textFiles.put(fileName, fileContent);

					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		}

		/*
		 * 替换
		 */
		Map<String, String> textFilesNew = new HashMap<>();

		for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap.entrySet()) {
			Map<String, String> excelValue = entry_excelDataHashMap.getValue();

			// 循环 textFiles，获取每个键值对
			for (Map.Entry<String, String> entry : textFiles.entrySet()) {
				String originalKey = entry.getKey();
				String originalValue = entry.getValue();

				String shiyezhe_ming = "";
				// 循环 excelData，查找并替换
				Iterator<Map.Entry<String, String>> iterator = excelValue.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, String> excelEntry = iterator.next();
					String Key = excelEntry.getKey();
					String Value = excelEntry.getValue();
					if ("#事業者名#".equals(Key)) {
						shiyezhe_ming = Value;
					}

					// 在 textFiles 的值中查找 excelData 的键，进行替换
					originalValue = originalValue.replaceAll(Key, Value);
				}

				String fileExtension = FuncUtils.getFileExtension(originalKey);
				// 更新 textFiles 的值
				textFilesNew.put(shiyezhe_ming + "." + fileExtension, originalValue);
			}

		}

		/*
		 * 循环 textFilesNew，将每个键值对写入文件
		 */

		//mkdir
		directory = new File(path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss);
		boolean hasSucceeded = directory.mkdir();
		logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);
		if (!directory.exists() || !directory.isDirectory()) {
			logger.info("指定的路径不是一个有效的目录。");
			return;
		}
		for (Map.Entry<String, String> entry : textFilesNew.entrySet()) {
			String fileName = entry.getKey();
			String fileContent = entry.getValue();

			// 构建文件路径
			String filePath = path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss + "/" + fileName;

			// 写入文件
			try {
				FileWriter writer = new FileWriter(filePath);
				writer.write(fileContent);
				writer.close();
				logger.info("File saved: " + filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		/*
		 * 结果下载
		 */
		path = path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
				// 创建一个输出流，将文件写入ZIP文件
				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
				ZipOutputStream zipOut = new ZipOutputStream(fos);

				// 调用递归方法将文件夹及其内容添加到ZIP文件中
				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

				// 关闭ZIP文件输出流
				zipOut.close();
				fos.close();

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{\"res\":\"" + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss + ".zip" + "\"}");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private void getAmazonCsvToExcel(HttpServletRequest req, PrintWriter out) {

		String msg = "";
		String res = "";



		try {



			/*
			 * license確認
			 */
			String user_id = req.getParameter("license");
			String pw = req.getParameter("pw");
			logger.info("license [user_id]" + user_id + " [pw]" + pw);

			User_infoBean User_infoBean = new User_infoBean();
			if (StringUtils.isEmpty(user_id) == false) {
				User_infoDao LicenseDao = new User_infoDao();
				User_infoBean = LicenseDao.select(user_id);
				String license = User_infoBean.getLicense_yyyymmdd();
				logger.info("license YYYYMMDD" +  license);

			}


			String form_mailarea = req.getParameter("form_mailarea");

			if (!"admin".equals(User_infoBean.getPermissions())) {
				if (StringUtils.isEmpty(form_mailarea) == true) {
					res = "";
			        out.print("{\"res\":\"form_mailarea\"}");

			        logger.info("end Amazoncsvzhangben ok");
			        return;
				}
			}





			// 获取当前日期时间
			Date currentDate = new Date();
			// 设置日期时间格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			// 格式化日期时间
			String yyyymmddhhmmss = dateFormat.format(currentDate);

			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/fileDataToolsReplacegetAmazonCsvFormat");

			/*
			 * 循环 textFiles，将每个键值对写入文件
			 */
			String my_fileName = getAmazonCsvFormatToCsv(req, yyyymmddhhmmss, path);


	        // Excel 模板路径
	        String excelTemplatePath = FuncUtils.projectPath + "/kuaiji_moban_import/账本A收入.xlsx";


	        // 复制 Excel 模板
	        Workbook workbook = WorkbookFactory.create(new FileInputStream(excelTemplatePath));
	        Sheet sheet = workbook.getSheetAt(0);


			/*
			 * 获取上传的多个文件部分
			 */
			Map<String, String> textFiles = new HashMap<>();
			File directory = new File(path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss);

			if (!directory.exists() || !directory.isDirectory()) {
				logger.info("指定的路径不是一个有效的目录。");
				return;
			}

			File[] files = directory.listFiles();

			if (files == null || files.length == 0) {
				logger.info("目录下没有文件。");
				return;
			}


	        // 格式化日期（yyyy/MM/dd）
	        dateFormat = new SimpleDateFormat("yyyy/MM/dd");

			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					String fileExtension = FuncUtils.getFileExtension(fileName);

					if ("csv".equalsIgnoreCase(fileExtension)) {

				        // CSV 文件路径
				        String csvFilePath = file.getPath();

				        // 读取 CSV 文件
				        List<String[]> csvData = readCsvFile(csvFilePath);




				        // 从 CSV 数据填充 Excel（从第二行到倒数第二行）
				        for (int i = 1; i < csvData.size() - 1; i++) {
				            String[] csvRow = csvData.get(i);

							// 获取系统类型属性
							String osName = System.getProperty("os.name");
							// 您可以根据不同的系统类型执行不同的操作
							if (osName.toLowerCase().contains("windows")) {
//								logger.info("这是Windows系统");
								logger.debug(Arrays.toString(csvRow));
							} else if (osName.toLowerCase().contains("linux")) {
//								logger.info("这是Linux系统");

							}


				            Row row = sheet.createRow(i + 1);  // 从 Excel 的第 3 行开始写入数据

				            // 第一列：CSV 的第一列（日期，格式化为 yyyy/MM/dd）
				            String dateStr = csvRow[0];
				            Date date = dateFormat.parse(dateStr);
				            Cell dateCell = row.createCell(0);
				            dateCell.setCellValue(date);

				            // 第二列：固定值 "amazon consumer"
				            Cell fixedValueCell = row.createCell(1);
				            fixedValueCell.setCellValue("amazon consumer");

				            // 第三列：CSV 的第 6 列
				            String csvColumn6 = csvRow[5];
				            Cell column6Cell = row.createCell(2);
				            column6Cell.setCellValue(csvColumn6);

				            // 第四列：CSV 第 14 到 22 列的数字求和
				            // csvRow：从0开始，所以-1
				            double sum = 0;
				            for (int j = 14-1; j <= 22-1; j++) {
				            	if (j== 20-1) {
				            		continue;
				            	}
				                try {
				                    sum += Double.parseDouble(csvRow[j]);
				                } catch (NumberFormatException e) {
				                    // 如果无法解析为数字，跳过该列
				                	e.printStackTrace();
				                }
				            }
				            Cell sumCell = row.createCell(3);
				            sumCell.setCellValue(sum);
				        }

					}
				}
			}

	        // 强制重新计算 D1 单元格
	        FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
	        Row rowD1 = sheet.getRow(0); // D1 在第 1 行 (索引 0)
	        if (rowD1 != null) {
	            Cell cellD1 = rowD1.getCell(3); // D1 在第 4 列 (索引 3)
	            if (cellD1 != null) {
	                if (cellD1.getCellType() == CellType.FORMULA) {
	                    formulaEvaluator.evaluateFormulaCell(cellD1); // 重新计算
	                }
	            }
	        }

	        //bk计算过程中的文件
	        File newDirectory = new File(path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss + "_bk");
	        directory.renameTo(newDirectory);
			directory = new File(path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss);
			directory.mkdir();

	        // 输出的 Excel 文件路径
	        my_fileName = FuncUtils.toHalfWidthAndTruncate(my_fileName, 30);


			String fileExtension = FuncUtils.getFileExtension(my_fileName);
			my_fileName = my_fileName.replace("." +fileExtension , "");
	        my_fileName = "账本A收入_"+ my_fileName + "_" + yyyymmddhhmmss + ".xlsx";
	        String outputFilePath = "/fileDataToolsReplacegetAmazonCsvFormat/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss;
	        //+ "path/to/output/excel/账本A收入_输出.xlsx";

	        // 保存 Excel 文件
	        try (FileOutputStream fileOut = new FileOutputStream(FuncUtils.projectPath + outputFilePath + "/" + my_fileName)) {
	            workbook.write(fileOut);
	        }

	        // 关闭工作簿
	        workbook.close();

	        logger.debug("数据成功写入到 Excel " + outputFilePath + "/" + my_fileName);




			FuncUtils FunctionUtils = new FuncUtils();
			if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == true
					&& "admin".equals(User_infoBean.getPermissions())) {
		        files = directory.listFiles();
	            if (files != null && files.length > 0) {
	                // 使用 Stream API 生成逗号分隔的字符串
	            	res =  Stream.of(files)
	                        .map(file -> outputFilePath.substring(1) + "/" + file.getName()) // 获取文件名
	                        .collect(Collectors.joining(","));
	            }


				/*
				 * 结果下载
				 */
		        out.print("{\"res\":\"" + res + "\"}");


			} else {
				/*
				 *客户信息收集
				 */
				t_user_info_shoujiDao t_user_info_shoujiDao = new t_user_info_shoujiDao();
				t_user_info_shoujiBean t_user_info_shoujiBean = t_user_info_shoujiDao.SelectKeyValue("email", form_mailarea);
				if (StringUtils.isEmpty(t_user_info_shoujiBean.getForm_mailarea())) {
					t_user_info_shoujiBean.setForm_mailarea(form_mailarea);
					t_user_info_shoujiBean.setYyyymmdd_count(null);
					t_user_info_shoujiBean.setBeikao(my_fileName);
					t_user_info_shoujiDao.INSERT(t_user_info_shoujiBean);
				}


				/*
				 *登录信息发邮件给客户
				 */

//				HashMap<String, String> no_send_Files = new HashMap<>();
//				no_send_Files.
				FuncUtils.sendMail_Amazoncsvzhangben(form_mailarea, FuncUtils.projectPath + outputFilePath);


		        out.print("{\"res\":\"" + "" + "\"}");

			}




	        logger.info("end Amazoncsvzhangben ok");
	        return;


		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			out.print("{\"res\":\""+e.getMessage()+"\"}");
		}



	}

    // 读取 CSV 文件
    public static List<String[]> readCsvFile(String csvFilePath) throws Exception {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            String[] line;
            /*
             * bug
             * ,1,amazon.jp,Amazon,",",
             * PC22O-Blue Nuit\,
             */
            while ((line = reader.readNext()) != null) {
                data.add(line);

				// 获取系统类型属性
				String osName = System.getProperty("os.name");
				// 您可以根据不同的系统类型执行不同的操作
				if (osName.toLowerCase().contains("windows")) {
//					logger.info("这是Windows系统");
					logger.debug(Arrays.toString(line));
				} else if (osName.toLowerCase().contains("linux")) {
//					logger.info("这是Linux系统");

				}


            }
        }
        return data;
    }

	private void getAmazonCsvFormat(HttpServletRequest req, PrintWriter out) throws Exception {
		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		//アップロードするフォルダ
		String path = getServletContext().getRealPath("/fileDataToolsReplacegetAmazonCsvFormat");

		/*
		 * license確認
		 */
		// 不要

		getAmazonCsvFormatToCsv(req, yyyymmddhhmmss, path);

		/*
		 * 结果下载
		 */
		path = path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss;
		// 创建表示文件夹的File对象
		File file = new File(path);
		if (file.exists()) {

			/*
			 * 生成文件ZIP
			 */
			// 源文件夹的路径
			String sourceFolderPath = path;
			// 目标ZIP文件的路径
			String targetZipFilePath = path + ".zip";
			try {
				// 创建一个输出流，将文件写入ZIP文件
				FileOutputStream fos = new FileOutputStream(targetZipFilePath);
				ZipOutputStream zipOut = new ZipOutputStream(fos);

				// 调用递归方法将文件夹及其内容添加到ZIP文件中
				FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

				// 关闭ZIP文件输出流
				zipOut.close();
				fos.close();

				logger.info("ZIP文件创建成功：" + targetZipFilePath);
				out.print("{\"res\":\"" + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss + ".zip" + "\"}");

			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			out.print("{\"res\":\"结果文件不存在\"}");
		}
	}


	private String getAmazonCsvFormatToCsv(HttpServletRequest req, String yyyymmddhhmmss, String path) throws Exception {

		String my_fileName = "";
		/*
		 * 登录功能 附件
		 */
		try {
			FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		/*
		 * 获取上传的多个文件部分
		 */
		Map<String, String> textFiles = new HashMap<>();
		File directory = new File(path + "/" + yyyymmddhhmmss);
		File[] files = directory.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				String fileName = file.getName();
				String fileExtension = FuncUtils.getFileExtension(fileName);

				if ("csv".equalsIgnoreCase(fileExtension)) {
					my_fileName = fileName;

					BufferedReader br = null;
					String line = "";
					String cvsSplitBy = "\",\""; // 逗号分隔符
					String cvsSplitBy1 = ","; // 逗号分隔符
					Double total6 = 0.0;
					Double total13 = 0.0;
					Double total14 = 0.0;
					Double total15 = 0.0;
					Double total16 = 0.0;
					Double total17 = 0.0;
					Double total18 = 0.0;
					Double total19 = 0.0;
					Double total20 = 0.0;
					Double total21 = 0.0;
					Double total22 = 0.0;
					Double total23 = 0.0;
					Double total24 = 0.0;
					Double total25 = 0.0;
					Double total26 = 0.0;
					Double total27 = 0.0;
					StringBuilder sb = new StringBuilder();

					try {

			            String charset = FuncUtils.detectCharset(file.getPath());
			            logger.info("CSV 文件编码是: " + charset);

						if ("IBM866".equals(charset)) {
							charset = "GB18030";
						}

						FileInputStream fis = new FileInputStream(file);
//						InputStreamReader isr = new InputStreamReader(fis, Charset.forName("SJIS"));
						InputStreamReader isr = new InputStreamReader(fis, Charset.forName(charset));
						br = new BufferedReader(isr);

						int line_count = 0;
						boolean csv_falg = false;
						boolean csv_toExcel_falg = false;
						boolean csv_toExcel_err_falg = false;
						while ((line = br.readLine()) != null) {
							++line_count;
							// 去除双引号中的逗号
							//			            	line = removeCommasInsideQuotes(line);
							//							line.replaceAll("\\\",\\\"", "\\\"、\\\"");
							//							line.replaceAll(",", "");
							//							line.replaceAll( "\"、\"","\",\"");

							//			            	line = line.replaceAll("\"", "");

							// 使用逗号分隔符拆分每一行的数据

							//			                String[] data = line.split(cvsSplitBy);
							//			            	if (line.indexOf(cvsSplitBy) == -1) {
							//			            		data = line.split(cvsSplitBy1);
							//			            	}




							boolean csv_err_falg = false;
							String[] data = FuncUtils.splitCSV(line);
							if (csv_falg) {
								if (data.length != 28) {
									line = line.replace("\",\"", "\"，\"");
									int count = (int) line.chars().filter(ch -> ch == ',').count();
									if (count == 27) {
										data = FuncUtils.splitCSV(line.replace("\"", ""));
										if (data.length != 28) {
											String msg_err = "CSV格式不正确，请使用平台下载后原始CSV文件（28列，现在" + data.length + "列）";
								            logger.info("line: " + line);
								            csv_err_falg = true;
											throw new Exception(msg_err);
										}
//							line = "2024/01/01 0:16:54 JST,11578787493,注文,250-3755308-9636667,B35-11-01B10-24-6-JP4,Neverland Beauty ヘアマネキン 練習用 頭囲24\" マネキンヘッド ミックス毛 多毛量 カットウィッグ ヘアセット 編み?,1,amazon.jp,Amazon,,東京都,189-0001,,3900,390,373,37,0,0,0,-373,-37,0,-644,-514,0,0,3132";
//							FuncUtils.splitCSV(line);
									} else {
										String msg_err = "CSV格式不正确，请使用平台下载后原始CSV文件（28列，现在" + data.length + "列）";
							            logger.info(msg_err);
							            logger.info("line: " + line);

							            csv_err_falg = true;
										throw new Exception(msg_err);
									}
								}
							}

							//csv直接读取出错，用Excel打开转换成csv，再次读入
//							if (csv_toExcel_falg == false) {
//								if (csv_err_falg == true) {
//									br = FuncUtilsExcel.convertCSVWithLibreOffice(file.getPath(), charset);
//
//									csv_falg = false;
//									csv_err_falg = false;
//
//									csv_toExcel_falg = true;
//
//									continue;
//								}
//
//							}
//
//
//							if (csv_toExcel_falg == true) {
//								if (csv_err_falg == true) {
//									throw new Exception("Excel转换后：CSV格式不正确");
//								}
//
//							}


							data[0] = data[0].replaceAll("\"", "");

							String dateTimeString = data[0];
							String startDateStr = req.getParameter("form_S_YYYYMMDD");
							String endDateStr = req.getParameter("form_E_YYYYMMDD");
							boolean isLastYear = FuncUtils.isLastYear(startDateStr, endDateStr, dateTimeString);
							//	1 删去表头行之上所有行，保留表头行
							//	表头行：即 【日付/時間  決済番号……】的这一行
							if ("日付/時間".equals(data[0])) {
								csv_falg = true;
								data[27] = data[27].replaceAll("\"", "");
								sb.append("\"" + String.join("\",\"", data) + "\"").append("\r\n");
								continue;

								//	2 删去所需时间之外的所有行（除表头行）
								//	所需时间，比如2023/10/01~2023/12/31 这种
							} else if (isLastYear == true) {

							} else {
								continue;

							}

							// 获取系统类型属性
							String osName = System.getProperty("os.name");
							// 您可以根据不同的系统类型执行不同的操作
							if (osName.toLowerCase().contains("windows")) {
//								logger.info("这是Windows系统");
								logger.debug(data[0]);
							} else if (osName.toLowerCase().contains("linux")) {
//								logger.info("这是Linux系统");

							}

							data[27] = data[27].replaceAll("\"", "");

							//	3 保留C列为【注文】或【返金】的行，其他行删去（除表头行）
							if ("注文".equals(data[2]) || "返金".equals(data[2])) {

							} else {
								continue;

							}

							data[6] = StringUtils.isEmpty(data[6]) ? "0" : data[6].replaceAll(",", "");
							data[13] = StringUtils.isEmpty(data[13]) ? "0" : data[13].replaceAll(",", "");
							data[14] = StringUtils.isEmpty(data[14]) ? "0" : data[14].replaceAll(",", "");
							data[15] = StringUtils.isEmpty(data[15]) ? "0" : data[15].replaceAll(",", "");
							data[16] = StringUtils.isEmpty(data[16]) ? "0" : data[16].replaceAll(",", "");
							data[17] = StringUtils.isEmpty(data[17]) ? "0" : data[17].replaceAll(",", "");
							data[18] = StringUtils.isEmpty(data[18]) ? "0" : data[18].replaceAll(",", "");
							data[19] = StringUtils.isEmpty(data[19]) ? "0" : data[19].replaceAll(",", "");
							data[20] = StringUtils.isEmpty(data[20]) ? "0" : data[20].replaceAll(",", "");
							data[21] = StringUtils.isEmpty(data[21]) ? "0" : data[21].replaceAll(",", "");
							data[22] = StringUtils.isEmpty(data[22]) ? "0" : data[22].replaceAll(",", "");
							data[23] = StringUtils.isEmpty(data[23]) ? "0" : data[23].replaceAll(",", "");
							data[24] = StringUtils.isEmpty(data[24]) ? "0" : data[24].replaceAll(",", "");
							data[25] = StringUtils.isEmpty(data[25]) ? "0" : data[25].replaceAll(",", "");
							data[26] = StringUtils.isEmpty(data[26]) ? "0" : data[26].replaceAll(",", "");
							data[27] = StringUtils.isEmpty(data[27]) ? "0" : data[27].replaceAll(",", "");

							//	4 删去OQSV四列同时为0的行（除表头行）
							//	14		16		18			21
							if (Double.parseDouble(data[14]) == 0 && Double.parseDouble(data[16]) == 0
									&& Double.parseDouble(data[18]) == 0 && Double.parseDouble(data[21]) == 0) {
								continue;
							}

							//去除M列【不为空】的行
							if (StringUtils.isEmpty(data[12])) {

							} else {
								continue;

							}

							sb.append("\"" + String.join("\",\"", data) + "\"").append("\r\n");

							//	5 合计列数值并写入新加最后一行
							//	6	13-27
							total6 = total6 + Double.parseDouble(data[6]);
							total13 = total13 + Double.parseDouble(data[13]);
							total14 = total14 + Double.parseDouble(data[14]);
							total15 = total15 + Double.parseDouble(data[15]);
							total16 = total16 + Double.parseDouble(data[16]);
							total17 = total17 + Double.parseDouble(data[17]);
							total18 = total18 + Double.parseDouble(data[18]);
							total19 = total19 + Double.parseDouble(data[19]);
							total20 = total20 + Double.parseDouble(data[20]);
							total21 = total21 + Double.parseDouble(data[21]);
							total22 = total22 + Double.parseDouble(data[22]);
							total23 = total23 + Double.parseDouble(data[23]);
							total24 = total24 + Double.parseDouble(data[24]);
							total25 = total25 + Double.parseDouble(data[25]);
							total26 = total26 + Double.parseDouble(data[26]);
							total27 = total27 + Double.parseDouble(data[27]);

						}
					} catch (Throwable e) {
						e.printStackTrace();
						throw new Exception(e.getMessage());
					} finally {
						if (br != null) {
							try {
								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					String[] data = new String[28];
					data[6] = String.format("%.0f", total6);
					data[13] = String.format("%.0f", total13);
					data[14] = String.format("%.0f", total14);
					data[15] = String.format("%.0f", total15);
					data[16] = String.format("%.0f", total16);
					data[17] = String.format("%.0f", total17);
					data[18] = String.format("%.0f", total18);
					data[19] = String.format("%.0f", total19);
					data[20] = String.format("%.0f", total20);
					data[21] = String.format("%.0f", total21);
					data[22] = String.format("%.0f", total22);
					data[23] = String.format("%.0f", total23);
					data[24] = String.format("%.0f", total24);
					data[25] = String.format("%.0f", total25);
					data[26] = String.format("%.0f", total26);
					data[27] = String.format("%.0f", total27);

					sb.append("\"" + String.join("\",\"", data).replaceAll("null", "") + "\"");
					textFiles.put(fileName, sb.toString());

				}

			}
		}

		/*
		 * 循环 textFiles，将每个键值对写入文件
		 */

		//mkdir
		directory = new File(path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss);
		boolean hasSucceeded = directory.mkdir();
		logger.debug("创建文件夹结果（不含父文件夹）：" + hasSucceeded);
		for (Map.Entry<String, String> entry : textFiles.entrySet()) {
			String fileName = entry.getKey();
			String fileContent = entry.getValue();

			//	输出CSV，输出的文件名【計算用+初始文件名】
			// 构建文件路径
			String filePath = path + "/" + yyyymmddhhmmss + "/output" + yyyymmddhhmmss + "/計算用_" + fileName;

			// 写入文件
			try {
				FileWriter writer = new FileWriter(filePath);
				writer.write(fileContent);
				writer.close();
				logger.info("File saved: " + filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return my_fileName;
	}

	public static String removeCommasInsideQuotes(String csvData) {
		// 匹配双引号中的逗号
		String regex = "\"([^\"]*),([^\"]*)\"";
		return csvData.replaceAll(regex, "\"$1$2\"");
	}




	public static Map<String, String> readExcel(String filePath) {
		Map<String, String> excelData = new HashMap<>();
		FileInputStream fileInputStream = null;
		Workbook workbook = null;

		try {
			fileInputStream = new FileInputStream(filePath);
			workbook = WorkbookFactory.create(fileInputStream);

			// Assuming you are reading the first sheet (index 0)
			Sheet sheet = workbook.getSheetAt(0);

			// Iterate through each row
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				// Assuming column A is index 0, and column B is index 1
				Cell keyCell = row.getCell(0, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell valueCell = row.getCell(1, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

				String key = keyCell.toString().trim();
				String value = valueCell.toString().trim();

				excelData.put(key, value);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (workbook != null) {
					((FileInputStream) workbook).close();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return excelData;
	}


}