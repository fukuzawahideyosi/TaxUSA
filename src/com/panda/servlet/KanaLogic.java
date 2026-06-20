package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;

/**
 * 社保计算
 */
@WebServlet("/KanaLogic")
public class KanaLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(KanaLogic.class.toString());

	private static final long serialVersionUID = 1L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		logger.info("start");

		// 返却値に設定
		response.setContentType("text/html; charset=UTF-8");
		//解决前端跨域
		//			response.setHeader("Access-Control-Allow-Origin","*");
		//			request.setCharacterEncoding("utf-8");

		PrintWriter out = response.getWriter();
		try {
			FuncUtils FuncUtils  = new FuncUtils();
			String str = "";
			String strNew = "";
			Map<String, String[]> HashMapParameterMap = request.getParameterMap();
			for (String key : HashMapParameterMap.keySet()) {

				if ("pinyin".equals(key)) {
					/*
					 *     http://127.0.0.1:8080/PandaServiceMA/KanaLogic?pinyin=ni%20<hao
					 *     https://www.pandaservicejapan.com/KanaLogic?pinyin=ni%20<hao
					 *
					 */
					str = request.getParameter("pinyin");
					logger.info(str);
					String[] strList = str.split(" ");
					for (int i = 0; i < strList.length; i++) {
						String value = strList[i];
						try {
							logger.info(value);
							Map<String, String> fydmcPinYinMap = FuncUtils.changeChinesePinyin(value);
							logger.info(fydmcPinYinMap.get("fullPinyin"));
							logger.info(FuncUtils.HashMapPinyinKana.get(fydmcPinYinMap.get("fullPinyin")));
							strNew = strNew + FuncUtils.HashMapPinyinKana.get(fydmcPinYinMap.get("fullPinyin")).split(",")[0]
									+ " ";
						} catch (Exception e) {
							logger.info(value + " -> skip");
						}

					}

				} else if ("hanziToPinyin".equals(key)) {
					/*
					 *     http://127.0.0.1:8080/PandaServiceMA/KanaLogic?pinyin=ni%20<hao
					 *     https://www.pandaservicejapan.com/KanaLogic?pinyin=ni%20<hao
					 *
					 */
					str = request.getParameter("hanziToPinyin");
					logger.info(str);
					String[] strList = str.split(" ");
					for (int i = 0; i < strList.length; i++) {
						String value = strList[i];
						try {
							logger.info(value);
							Map<String, String> fydmcPinYinMap = FuncUtils.changeChinesePinyin(value);
							logger.info(fydmcPinYinMap.get("fullPinyin"));
							strNew = strNew + fydmcPinYinMap.get("fullPinyin")
									+ " ";
						} catch (Exception e) {
							logger.info(value + " -> skip");
						}

					}
				} else if ("hanzi".equals(key)) {
					strNew = FuncUtils.fn_hanzi(request.getParameter("hanzi"));

				} else if ("wareki".equals(key)) {

					//https://www.pandaservicejapan.com/KanaLogic?wareki=20230212
					//http://127.0.0.1:8080/PandaServiceMA/KanaLogic?wareki=20230212
					str = request.getParameter("wareki");

					strNew = getWareki(str);

				} else if ("license".equals(key)) {
					str = request.getParameter("license");
					logger.info(str);
					User_infoDao LicenseDao = new User_infoDao();
					strNew = LicenseDao.select(str).getLicense_yyyymmdd();
					logger.info(strNew);

				} else if ("UpdateXml".equals(key)) {
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.UpdateXml(request.getParameter("UpdateXml"));

				} else if ("UpdateWord".equals(key)) {
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.UpdateWord(request.getParameter("UpdateWord"));

				} else if ("UpdatePdf".equals(key)) {
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.UpdatePdf(request.getParameter("UpdatePdf"));
				}
			}

			out.print(strNew);

		} catch (

		Exception e) {
			e.printStackTrace();
			out.print(e.toString());
		} finally {

		}

		logger.info("end");
	}


	public String getWareki(String str) throws ParseException {
		String strNew;
		logger.info(str);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sdf.setLenient(false);
		Date date = sdf.parse(str);
		Locale locale = new Locale("ja", "JP", "JP");
		DateFormat format = new SimpleDateFormat("GGGGyyyy年 MM月 dd日", locale);
		logger.info(format.format(date));
		format = new SimpleDateFormat("GGGG", locale);
//		logger.info(format.format(date));
		String nenngou = format.format(date);
//		logger.info(FuncUtils.HashMapWareki.get(nenngou));
		format = new SimpleDateFormat("yyyy", locale);
//		logger.info(format.format(date));
		if ("元".equals(format.format(date)) == true) {
			strNew = FuncUtils.HashMapWareki.get(nenngou) + ",1";

		} else {
			strNew = FuncUtils.HashMapWareki.get(nenngou) + "," + format.format(date);
		}

//		logger.info(strNew);
		return strNew;
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

//	public static void main(String[] args) {
//
//		Date date = new Date("2023/1/1");
//
//		Locale locale = new Locale("ja", "JP", "JP");
//		DateFormat format = new SimpleDateFormat("GGGGyyyy年 MM月 dd日", locale);
//		logger.info(format.format(date));
//		format = new SimpleDateFormat("GGGG", locale);
//		logger.info(format.format(date));
//		String nenngou = format.format(date);
//		logger.info((new KanaLogic()).HashMapWareki.get(nenngou));
//		format = new SimpleDateFormat("yyyy", locale);
//		logger.info(format.format(date));
//		logger.info(nenngou + "," + format.format(date));
//
//		//		     ロケールの設定（和暦用のロケール）
//		//		    Locale locale = new Locale("ja","JP","JP");
//		//		    // カレンダーオブジェクトの生成
//		//		    Calendar cal = Calendar.getInstance();
//		//
//		//		    // 和暦のロケールを指定して年をフォーマットする
//		//		    SimpleDateFormat sdf = new SimpleDateFormat("GGGGy年",locale);
//		//		    String dateStr = sdf.format(cal.getTime());
//		//
//		//		    // フォーマットされた日付の表示
//		//		    logger.info(dateStr);
//		//		    // 年の短縮名を表示
//		//		    sdf.applyPattern("Gy");
//		//		    dateStr = sdf.format(cal.getTime());
//		//		    logger.info(dateStr);
//		//
//		//		    // 100年前の和暦を表示してみる
//		//		    sdf.applyPattern("GGGGy年");
//		//		    cal.add(Calendar.YEAR, -100);
//		//		    dateStr = sdf.format(cal.getTime());
//		//		    logger.info(dateStr);
//
//		//			 java.time.chrono.JapaneseDate.get(YEAR_OF_ERA);
//
//		//			  DateTimeFormatter.ofPattern("GGGGy年M月d日").withChronology(JapaneseChronology.INSTANCE).withLocale(Locale.JAPAN).format(JapaneseDate.of(2020, 2, 1));
//		//			  // => “令和2年2月1日”
//		//
//		//			  logger.info(
//		//			  DateTimeFormatter.ofPattern("u-M-d").format(DateTimeFormatter.o)fPattern("GGGGy年M月d日").withChronology(JapaneseChronology.INSTANCE).withLocale(Locale.JAPAN).withResolverStyle(ResolverStyle.STRICT).parse("令和2年2月1日"))
//		//			  // => “2020-2-1”
//		//					  );
//		//			  logger.info(
//		//			  JapaneseEra.of(3).getDisplayName(TextStyle.FULL,Locale.forLanguageTag("ja-JP-u-ca-japanese"))
//		//			  // => “令和”
//		//					  );
//	}

}