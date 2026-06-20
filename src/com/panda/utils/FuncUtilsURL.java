package com.panda.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
public class FuncUtilsURL {

	private static Logger logger = Logger.getLogger(FuncUtilsURL.class.toString());

	 public static String getGuanxia_shuiwushu(String KSTYPE,String TODOFUKEN_TO_ASCII,String ADDR_TO_ASCII, String kszc1, String kszc2) {
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        CloseableHttpResponse response = null;

            List<String> ElementList = new ArrayList<>();
	        try {
	            HttpPost httpPost = new HttpPost("https://www.nta.go.jp/cgi-bin/zeimusho/kensaku/kensakuprocess.php");

	            // 设置 POST 数据
	            List<NameValuePair> params = new ArrayList<>();
	            params.add(new BasicNameValuePair("KSTYPE", KSTYPE));
	            params.add(new BasicNameValuePair("TODOFUKEN_TO_ASCII", convertToAscii(TODOFUKEN_TO_ASCII)));
	            params.add(new BasicNameValuePair("ADDR_TO_ASCII", convertToAscii(ADDR_TO_ASCII)));
	            params.add(new BasicNameValuePair("kszc1", kszc1));
	            params.add(new BasicNameValuePair("kszc2", kszc2));
	            httpPost.setEntity(new UrlEncodedFormEntity(params));

	            // 发送 POST 请求
	            response = httpClient.execute(httpPost);
	            HttpEntity entity = response.getEntity();

	            if (entity != null) {


	                String htmlContent = EntityUtils.toString(entity);

	                // 解析 HTML 获取<label id='ksa'>的值
	                Document doc = Jsoup.parse(htmlContent);
	                // 查找具有 title='関連リンクのページへ移動します' 属性的<a>元素
	                Elements elements = doc.select("a[title='関連リンクのページへ移動します']");

	                for (Element element : elements) {
	                    // 获取元素的文本内容
	                    String linkText = element.text();
	                    logger.info("Link text: " + linkText);
	                    ElementList.add(linkText);
	                }

	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (response != null) {
	                    response.close();
	                }
	                httpClient.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }


            return ElementList.toString().replaceAll("\\[", "").replaceAll("\\]", "");
	    }
	    public static String convertToAscii(String str) {
	        StringBuilder strToAscii = new StringBuilder();
	        for (int i = 0; i < str.length(); i++) {
	            char c = str.charAt(i);
	            if (i != 0) {
	                strToAscii.append(" ");
	            }
	            strToAscii.append((int) c);
	        }
	        return strToAscii.toString();
	    }

	    public static void main(String[] args) {

	        String KSTYPE = "";
	        String TODOFUKEN_TO_ASCII = "";
	        String ADDR_TO_ASCII = "";
	        String kszc1 = "";
	        String kszc2 = "";
	        String labelValue = "";

	         KSTYPE = "ksa";
//	         TODOFUKEN_TO_ASCII = "26481 20140 37117";
	         TODOFUKEN_TO_ASCII = "東京都";
//	         ADDR_TO_ASCII = "28207 21306 36196 22338";
//	         ADDR_TO_ASCII = "28207 21306";
	         ADDR_TO_ASCII = "港区";
	         labelValue = getGuanxia_shuiwushu(KSTYPE,TODOFUKEN_TO_ASCII,ADDR_TO_ASCII, kszc1, kszc2);
	        logger.info("Label value: " + labelValue);

	         KSTYPE = "ksz";
	         TODOFUKEN_TO_ASCII = "東京都";
	         ADDR_TO_ASCII = "港区";
	         kszc1 = "107";
	         kszc2 = "0052";
	         labelValue = getGuanxia_shuiwushu(KSTYPE,TODOFUKEN_TO_ASCII,ADDR_TO_ASCII, kszc1, kszc2);
	        logger.info("Label value: " + labelValue);

	    }

}