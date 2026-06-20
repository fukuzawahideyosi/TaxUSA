package com.panda.servlet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.utils.FuncUtils;
/**
 * 社保计算
 */
@WebServlet("/EtaxLogic")
//@CrossOrigin
public class EtaxLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(EtaxLogic.class.toString());

	private static final long serialVersionUID = 1L;

	HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EtaxLogic() {
		super();

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("start");

		// 返却値に設定
		response.setContentType("text/html; charset=UTF-8");
		//解决前端跨域
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();



		Map<String, String[]> HashMapParameterMap = request.getParameterMap();
		//TODO  key = request.getParameter("res_send_mae");
		//		HashMapParameterMap = new HashMap<String, String[]>();
		//		HashMapParameterMap.put("CreateData", null);

		for (String key : HashMapParameterMap.keySet()) {

			logger.info("key:" + key);
//			if("res_send_mae".equals(key)) 			key="res_send_go";
			if ("CreateData".equals(key)) {

				/*
				 *     http://127.0.0.1:8080/PandaServiceMA/EtaxLogic?CreateData=
				 *     http://127.0.0.1:8080/PandaServiceMA/EtaxLogic?res_send_mae=
				 *     http://127.0.0.1:8080/PandaServiceMA/EtaxLogic?res_send_go=
				 *
				 *     https://www.pandaservicejapan.com/KanaLogic?
				 *
				 */
				String yyyymmdd_count = HashMapParameterMap.get(key)[0];


				LinkedHashMap<String, HashMap<String, String>> HashMapKeyValueHtmlAll = getHashMapKeyValueHtmlAll(yyyymmdd_count);

				JSONObject json = new JSONObject(HashMapKeyValueHtmlAll);

				out.print(json);

			} else if ("MAX_yyyymmdd_count_active".equals(key)) {

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				String Max_yyyymmdd_count = t_etax_account_resDao.selectMax_yyyymmdd_count_active();

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoBean t_etax_account_infoBean = t_etax_account_infoDao.select(Max_yyyymmdd_count);

//				out.print("{\"yyyymmdd_count\":\"" + Max_yyyymmdd_count + "\"}");
				out.print("{"
						+ "  \"yyyymmdd_count\":\"" + Max_yyyymmdd_count + "\""
						+ ", \"user_type\":\"" + t_etax_account_infoBean.getUser_type() + "\""
						+ "}");

				return;

			} else if ("jietuo_by_bangou".equals(key)) {

				String bangou = request.getParameter("bangou");
				String keyValue = request.getParameter("jietuo_by_bangou");
				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_by_bangou(bangou, "申告");

				if ("html".equals(keyValue)) {
					out.print(t_etax_jieguoExBean.getHtml());

				} else	if ("html_qr".equals(keyValue)) {
					out.print(t_etax_jieguoExBean.getHtml_qr());

				} else {
					out.print("{"
							+ "  \"bangou\":\"" + t_etax_jieguoExBean.getBangou() + "\""
							+ ", \"yyyymmdd_count\":\"" + t_etax_jieguoExBean.getYyyymmdd_count() + "\""
							+ "}");
				}

				logger.info("jietuo_by_bangou end");
				return;

			} else if ("jietuo_active_by_bangou".equals(key)) {

				String bangou = request.getParameter("bangou");
				String keyValue = request.getParameter("jietuo_active_by_bangou");
				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_active_by_bangou(bangou);

				if ("html".equals(keyValue)) {
					out.print(t_etax_jieguoExBean.getHtml());

				} else	if ("html_qr".equals(keyValue)) {
						out.print(t_etax_jieguoExBean.getHtml_qr());

				} else {
					out.print("{"
							+ "  \"bangou\":\"" + t_etax_jieguoExBean.getBangou() + "\""
							+ ", \"yyyymmdd_count\":\"" + t_etax_jieguoExBean.getYyyymmdd_count() + "\""
							+ "}");
				}

				logger.info("jietuo_active_by_bangou end");
				return;

			} else if ("jietuo_active_max".equals(key)) {
				String keyValue = request.getParameter("jietuo_active_max");

				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_active_max(keyValue);

				out.print("{"
						+ "  \"bangou\":\"" + t_etax_jieguoExBean.getBangou() + "\""
						+ ", \"yyyymmdd_count\":\"" + t_etax_jieguoExBean.getYyyymmdd_count() + "\""
						+ ", \"user_type\":\"" + t_etax_jieguoExBean.getUser_type() + "\""
						+ ", \"exe_list\":\"" + t_etax_jieguoExBean.getExe_list() + "\""
						+ ", \"etax_pw\":\"" + t_etax_jieguoExBean.getEtax_pw() + "\""
						+ "}");

				logger.info("jietuo_active_max end");
				return;

			} else if ("jietuo_pdf_xiaofeishui_shengaoshu_up".equals(key)) {
				String bangou = request.getParameter("bangou");
				String keyValue = request.getParameter("jietuo_pdf_xiaofeishui_shengaoshu_up");
				if (StringUtils.isEmpty(bangou)) {
					t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
					t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_active_max(keyValue);
					bangou = t_etax_jieguoExBean.getBangou();
				}

				 // 获取上传文件的输入流
		        InputStream fileContent = request.getInputStream();
				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				if (t_etax_jieguoDao.UPDATE_jietuo_pdf_xiaofeishui_shengaoshu_up(bangou, fileContent, keyValue)) {
					out.print("{"
							+ "  \"bangou\":\"ok\""
							+ "}");
				} else {
					out.print("{"
							+ "  \"bangou\":\"err\""
							+ "}");
				}

				logger.info("jietuo_active_fileName_up end");
				return;


			} else if ("jietuo_active_fileName_up".equals(key)) {

				String keyValue = request.getParameter("jietuo_active_fileName_up");
				String bangou = request.getParameter("bangou");
				String fileName = request.getParameter("fileName");

				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				if (t_etax_jieguoDao.UPDATE_fileName(bangou, fileName, keyValue)) {
					out.print("{"
							+ "  \"bangou\":\"ok\""
							+ "}");
				} else {
					out.print("{"
							+ "  \"bangou\":\"err\""
							+ "}");
				}

				logger.info("jietuo_active_fileName_up end");
				return;


			} else if ("jietuo_active_html_up".equals(key)) {

				String keyValue = request.getParameter("jietuo_active_html_up");

				BufferedReader reader = request.getReader();
				Stream<String> lines = reader.lines();
				String lines_value = lines.collect(Collectors.joining("\r\n"));
				Document doc = Jsoup.parse(lines_value);
				String yyyymmdd_count = request.getParameter("yyyymmdd_count");
				String columnKey = request.getParameter("columnKey");

				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
				if (t_etax_jieguoDao.jietuo_active_html(yyyymmdd_count, doc.html(), columnKey, keyValue) == 1) {
					out.print("{\"res\":\"ok\"}");

				} else {
					out.print("{\"res\":\"err\"}");

				}

				logger.info("jietuo_active_html_up end");
				return;



			} else if ("res_send_mae".equals(key)) {
				BufferedReader reader = request.getReader();
				Stream<String> lines = reader.lines();
				String lines_value = lines.collect(Collectors.joining("\r\n"));
				Document doc = Jsoup.parse(lines_value);
				String yyyymmdd_count = doc.getElementById("yyyymmdd_count").val();
				//				doc.getElementById("center").attr("style", "transform:scale(0.1,0.1);");
				doc.getElementById("SU_teisei").remove();
				doc.getElementById("SU_send").remove();

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				if (t_etax_account_resDao.UPDATE_res(key, yyyymmdd_count, doc.html(), null) == false) {
					out.print("{\"res\":\"err\"}");
					return;
				}
//				t_etax_account_resDao.selec(yyyymmdd_count);
				out.print("{\"res\":\"ok\"}");

			} else if ("res_send_go".equals(key)) {
				BufferedReader reader = request.getReader();
				Stream<String> lines = reader.lines();
				String lines_value = lines.collect(Collectors.joining("\r\n"));
				Document doc = Jsoup.parse(lines_value);
				String yyyymmdd_count = doc.getElementById("yyyymmdd_count").val();
//				doc.getElementById("SU_close").remove();
				String bangou = doc.getElementById("bangou").text().replace(" ", "");

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				if (t_etax_account_resDao.UPDATE_res(key, yyyymmdd_count, doc.html(), bangou) == false) {
					out.print("{\"res\":\"err\"}");
					return;
				}

				t_etax_account_resDao.selec(yyyymmdd_count);
				out.print("{\"res\":\"ok\"}");
			}

			break;

		}

		logger.info("end");
	}

	public LinkedHashMap<String, HashMap<String, String>> getHashMapKeyValueHtmlAll(String yyyymmdd_count) {
		LinkedHashMap<String, HashMap<String, String>> HashMapKeyValueHtmlAll = new LinkedHashMap<String, HashMap<String, String>>();

		EtaxDao EtaxDao = new EtaxDao();
		HashMapKeyValueHtmlAll = EtaxDao.selectAll(yyyymmdd_count);

		if (HashMapKeyValueHtmlAll.size() == 0) {
			/*
			 * 激活
			 */
			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			try {
				String hojinmeiKana = "";
				t_etax_account_infoBean t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
						t_etax_account_resDao, EtaxDao, hojinmeiKana);
				t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

			} catch (SQLException e) {
				t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
				return HashMapKeyValueHtmlAll;
			}
		}


		HashMapKeyValueHtmlAll = EtaxDao.selectAll(yyyymmdd_count);

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();
			HashMapKeyValueHtml.put("yyyymmdd_count", yyyymmdd_count);
			HashMapKeyValueHtml.put("html_id", "gSNen");
			HashMapKeyValueHtml.put("html_value", t_etax_account_infoExBean.getCompany_YYYY());
			HashMapKeyValueHtmlAll.put(HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"), HashMapKeyValueHtml);


			int desiredLength = 2; // 期望的字符串长度
			String number = t_etax_account_infoExBean.getCompany_MM(); // 例：数字字符串
			// 使用 String.format() 格式化数字，并在前面补位 0
			String formattedNumber = String.format("%0" + desiredLength + "d", Integer.parseInt(number));
			HashMapKeyValueHtml = new HashMap<String, String>();
			HashMapKeyValueHtml.put("yyyymmdd_count", yyyymmdd_count);
			HashMapKeyValueHtml.put("html_id", "gSTuki");
			HashMapKeyValueHtml.put("html_value", formattedNumber);
			HashMapKeyValueHtmlAll.put(HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"), HashMapKeyValueHtml);

			number = t_etax_account_infoExBean.getCompany_DD(); // 例：数字字符串
			// 使用 String.format() 格式化数字，并在前面补位 0
			formattedNumber = String.format("%0" + desiredLength + "d", Integer.parseInt(number));
			HashMapKeyValueHtml = new HashMap<String, String>();
			HashMapKeyValueHtml.put("yyyymmdd_count", yyyymmdd_count);
			HashMapKeyValueHtml.put("html_id", "gSHi");
			HashMapKeyValueHtml.put("html_value", formattedNumber);
			HashMapKeyValueHtmlAll.put(HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"), HashMapKeyValueHtml);

		}

		String getCompanyName_pianjiaming = HashMapKeyValueHtmlAll.get(yyyymmdd_count + "gHojinmeiKana").get("html_value");
		HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();
		HashMapKeyValueHtml.put("yyyymmdd_count", yyyymmdd_count);
		HashMapKeyValueHtml.put("html_id", "gHojinmeiKana");
		HashMapKeyValueHtml.put("html_value", getCompanyName_pianjiaming.replace(" ", "").replace("　", ""));
		HashMapKeyValueHtmlAll.put(HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"), HashMapKeyValueHtml);

		String msg = "すでに利用者識別番号が発行された場合、今回申請する新規の利用者識別番号を有効にしてください。";
		if (!StringUtils.isEmpty(t_etax_account_infoExBean.getHoujinBangou())) {
			msg = msg +"法人番号：" + t_etax_account_infoExBean.getHoujinBangou() + "。";
		}
		if (!StringUtils.isEmpty(t_etax_account_infoExBean.getInvoiceBangou())) {
			msg = msg +"登録番号：" + t_etax_account_infoExBean.getInvoiceBangou() + "。";
		}

		HashMapKeyValueHtml = new HashMap<String, String>();
		HashMapKeyValueHtml.put("yyyymmdd_count", yyyymmdd_count);
		HashMapKeyValueHtml.put("html_id", "gSankoJiko");
		HashMapKeyValueHtml.put("html_value", msg);
		HashMapKeyValueHtmlAll.put(HashMapKeyValueHtml.get("yyyymmdd_count") + HashMapKeyValueHtml.get("html_id"), HashMapKeyValueHtml);


		return HashMapKeyValueHtmlAll;


	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}