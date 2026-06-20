package com.panda.batch;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.panda.utils.SendMail;

public class SearchKono {

	private static Logger logger = Logger.getLogger(SearchKono.class.toString());

	static String[] line1;

	public static void main(String[] args) throws Exception {

		Properties properties = new Properties();

		// プロパティファイルのパスを指定する
		//        String strpass = "/Users/Shared/java/properties/java.properties";
		String strpass = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\src\\SearchKono.properties";

		String key= "id";
		String id = getProperty(properties, strpass, key);
		logger.info("[idOld]" + id);


		StringBuffer htmlSb = sendGet();
		String htmlStr = htmlSb.toString();
		//解析字符串为Document对象
		Document doc = Jsoup.parse(htmlStr);

		Element myElement = doc.getElementById("t01");

		//			logger.info(myElement);

		//	      //获取body元素，获取class="fc"的table元素
		//	      Elements table = doc.body().getElementsByClass("fc");
		//	      //获取tbody元素
		//	      Elements children = table.first().children();
		//获取tr元素集合
		Elements tr = myElement.getElementsByTag("tr");

//		Element e1 = tr.get(1);
//		Elements td = e1.getElementsByTag("td");
//		String value = td.get(0).text();
//		logger.info("[idNew]" + value);
//
//		int idOld =  Integer.parseInt(id);
//		int idNew =  Integer.parseInt(value);
//		if (idNew > idOld) {
//			setProperty(properties, strpass, value);
////			sendMail();
//		}



			      //遍历tr元素，获取td元素，并打印
			      for(int i=1; i<tr.size(); i++){
			          Element e1 = tr.get(i);
			          Elements td = e1.getElementsByTag("td");
			          for(int j=0; j<td.size(); j++){
			              String value = td.get(j).text();
			              logger.info("  "+value);
			          }
			          logger.info("");
			      }

		//		logger.debug("updateDateMax " + updateDateMax);

	}



	private static void sendMail() {

		String namedata ="";
		String mailarea ="43936834@qq.com";
		String textboxdata ="";

		String title = "【盼达商务服务】AI智能检测・会社売買ねっと.biz"
				+ "";



		 textboxdata = namedata
			 		+ "<br>"
			 		+ "<br>您好。"
			 		+ "<br>"
			 		+ "<br>盼达商务服务检AI智能检测"
			 		+ "<br>"
			 		+ "<br>****************"
			 		+ "<br>会社売買ねっと.biz有新公司追加、请尽快确认。"
			 		+ "<br>http://xn--j9jkn5252a9jkmh1cbr2a.biz/list.html"
			 		+ "<br>****************"
			 		+ "<br>"
			 		+ "<br>盼达商务服务"
				+ "";



		SendMail SendMail = new SendMail();

		SendMail.sendMessage(null, mailarea, "info@pandaservicejapan.com", title, textboxdata);
	}


	private static String getProperty(Properties properties, String strpass, String key) {
		try {
			InputStream istream = new FileInputStream(strpass);
			properties.load(istream);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Mapに格納
		Map<String, String> propMap = new HashMap<>();
		for (Map.Entry<Object, Object> e : properties.entrySet()) {
			propMap.put(e.getKey().toString(), e.getValue().toString());
		}

		// 配列に格納
		String str = properties.getProperty(key);
//		String[] arr = str.split("."); // ピリオドで分割

//		logger.info(propMap);
//		logger.info(Arrays.asList(arr));

		return str;
	}

	private static void setProperty(Properties properties, String strpass, String value) {
		try {
			// 書き込み
			properties.setProperty("id", value);
			properties.store(new FileOutputStream(strpass), "Comments");

			// 読み込み
			InputStream istream = new FileInputStream(strpass);
			properties.load(istream);
			istream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Mapに格納
		Map<String, String> propMap = new HashMap<>();
		for (Map.Entry<Object, Object> e : properties.entrySet()) {
			propMap.put(e.getKey().toString(), e.getValue().toString());
		}

		logger.info(propMap);
	}

	private static StringBuffer sendGet() throws Exception {

		StringBuffer htmlSb = new StringBuffer();
		URL url;
		try {

			String url_s = "http://xn--j9jkn5252a9jkmh1cbr2a.biz/list.html";
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();

			//			InputStream in = conn.getInputStream();
			//			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			InputStreamReader isr = new InputStreamReader(conn.getInputStream(), "Shift-JIS");
			BufferedReader br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {

				htmlSb.append(line);
				//				logger.info(line);

			}
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return htmlSb;
	}

}
