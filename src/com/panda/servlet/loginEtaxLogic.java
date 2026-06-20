package com.panda.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;

@WebServlet("/loginEtaxLogic")
@MultipartConfig
public class loginEtaxLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(loginEtaxLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);

		/*
		 * license確認
		 */
		String pw = req.getParameter("pw");
		session.setAttribute("pw", pw);
//		PrintWriter out = resp.getWriter();
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
			return;
		}


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
		CheckEtaxNo(session, t_etax_account_infoExBean);


        req.getRequestDispatcher("/loginEtax.jsp").forward(req, resp);



		logger.info("end");

		return;

	}

	public void CheckEtaxNo(HttpSession session, t_etax_account_infoExBean t_etax_account_infoExBean) {

		String oStUserId = t_etax_account_infoExBean.getBangou();
		String oStPassword = t_etax_account_infoExBean.getEtax_pw();
		String oStKjnHjnKbn = "2";
		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			oStKjnHjnKbn = "1";
		}

        String etaxSessionId="";
        // 创建HttpClient
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
        	// 创建 HttpGet 请求
        	HttpGet httpGet = new HttpGet("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");

        	// 设置请求头
        	httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        	httpGet.setHeader("Accept-Encoding", "gzip, deflate, br, zstd");
        	httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        	httpGet.setHeader("Cache-Control", "max-age=0");
        	httpGet.setHeader("Connection", "keep-alive");
        	httpGet.setHeader("Host", "login.e-tax.nta.go.jp");
        	httpGet.setHeader("Sec-Ch-Ua", "\"Microsoft Edge\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"");
        	httpGet.setHeader("Sec-Ch-Ua-Mobile", "?0");
        	httpGet.setHeader("Sec-Ch-Ua-Platform", "\"Windows\"");
        	httpGet.setHeader("Sec-Fetch-Dest", "document");
        	httpGet.setHeader("Sec-Fetch-Mode", "navigate");
        	httpGet.setHeader("Sec-Fetch-Site", "none");
        	httpGet.setHeader("Sec-Fetch-User", "?1");
        	httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        	httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0");


        	// 发送请求并获取响应
        	HttpResponse response = httpClient.execute(httpGet);
        	// 获取响应状态码
        	int statusCode = response.getStatusLine().getStatusCode();
        	if (statusCode == 200) {
        		// 从响应头中获取 Set-Cookie 字段
        		String cookieHeader = response.getFirstHeader("Set-Cookie").getValue();

        		// 使用正则表达式匹配 Set-Cookie 中的 EtaxSessionId
        		etaxSessionId = extractEtaxSessionId(cookieHeader);
        		logger.info("EtaxSessionId: " + etaxSessionId);
        	} else {
        		logger.info("Failed to get response from server. Response code: " + statusCode);
        		return;
        	}






        	// 创建Post请求
        	String url = "https://login.e-tax.nta.go.jp/login/reception/loginUserNumber";
        	HttpPost postRequest = new HttpPost(url);

        	// 设置请求头
        	postRequest.setHeader("Accept", "*/*");
        	postRequest.setHeader("Accept-Encoding", "gzip, deflate, br, zstd");
        	postRequest.setHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
        	postRequest.setHeader("Connection", "keep-alive");
        	postRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        	postRequest.setHeader("Host", "login.e-tax.nta.go.jp");
        	postRequest.setHeader("Origin", "https://login.e-tax.nta.go.jp");
        	postRequest.setHeader("Referer", "https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
        	postRequest.setHeader("Sec-Fetch-Dest", "empty");
        	postRequest.setHeader("Sec-Fetch-Mode", "cors");
        	postRequest.setHeader("Sec-Fetch-Site", "same-origin");
        	postRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36 Edg/125.0.0.0");
        	postRequest.setHeader("X-Requested-With", "XMLHttpRequest");
        	postRequest.setHeader("sec-ch-ua", "\"Microsoft Edge\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"");
        	postRequest.setHeader("sec-ch-ua-mobile", "?0");
        	postRequest.setHeader("sec-ch-ua-platform", "\"Windows\"");

        	// 设置Cookie
        	CookieStore cookieStore = new BasicCookieStore();
        	BasicClientCookie cookie = new BasicClientCookie("EtaxSessionId", etaxSessionId);
        	cookie.setDomain("login.e-tax.nta.go.jp");
        	cookie.setPath("/");
        	cookieStore.addCookie(cookie);

        	// 设置POST数据
        	String postData = "oStUserId="+oStUserId+"&oStPassword="+oStPassword+"&oStKjnHjnKbn="+oStKjnHjnKbn;
        	StringEntity entity = new StringEntity(postData);
        	postRequest.setEntity(entity);

        	// 创建一个包含CookieStore的HttpClient上下文
        	try (CloseableHttpClient httpClientWithCookies = HttpClients.custom().setDefaultCookieStore(cookieStore).build()) {
        		// 发送请求
        		response = httpClientWithCookies.execute(postRequest);

        		// 打印响应状态
        		logger.info("Response Code: " + response.getStatusLine().getStatusCode());

        		// 获取Content-Type头部
        		String contentType = response.getEntity().getContentType().getValue();
        		String charset = "UTF-8"; // 默认编码
        		if (contentType != null) {
        			Pattern pattern = Pattern.compile("charset=([^;]+)");
        			Matcher matcher = pattern.matcher(contentType);
        			if (matcher.find()) {
        				charset = matcher.group(1);
        			}
        		}

        		// 读取响应体并使用指定的编码
        		String responseBody = EntityUtils.toString(response.getEntity(), charset);
        		logger.info("Response Body: " + responseBody);

        		// 创建一个JSONObject
        		JSONObject jsonObject = new JSONObject(responseBody);

        		session.setAttribute("EtaxSessionId", etaxSessionId);

        		// 检查是否存在 oStHktgInf 键
        		if (jsonObject.has("oStHktgInf")) {
        			// 提取 oStHktgInf 的值
        			String oStHktgInf = jsonObject.getString("oStHktgInf");
        			session.setAttribute("resHktgInf", oStHktgInf);
        			session.removeAttribute("errorMessage");

        		} else if (jsonObject.has("errorMessage")) {
        			session.setAttribute("errorMessage", jsonObject.toString());

        		}

        	}



        } catch (Exception e) {
        	e.printStackTrace();
        }
	}


    // 使用正则表达式提取 EtaxSessionId
    private static String extractEtaxSessionId(String cookieHeader) {
        String etaxSessionId = null;
        String regex = "EtaxSessionId=([^;]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(cookieHeader);
        if (matcher.find()) {
            etaxSessionId = matcher.group(1);
        }
        return etaxSessionId;
    }
}