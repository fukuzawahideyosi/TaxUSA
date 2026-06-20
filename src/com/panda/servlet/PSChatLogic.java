package com.panda.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.SendMail;


@WebServlet("/PSChatLogic")
public class PSChatLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(PSChatLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	public static void main(String[] args) {
        try {
            // 构建请求
            String apiKey = "YOUR_API_KEY";
            String message = "Hello, Chat API!";
            String senderId = "12345";
            String urlString = "https://api.chat.com/sendMessage?apiKey=" + apiKey + "&message=" + message + "&senderId=" + senderId;
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // 发送请求
            int responseCode = con.getResponseCode();

            // 处理响应
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String response = in.readLine();
                logger.info("Chat API response: " + response);
            } else {
                logger.info("Chat API request failed with response code " + responseCode);
            }
        } catch (Exception e) {
            logger.info("Exception: " + e);
        }
    }



	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {






		HttpSession session = req.getSession();

		String sendMail = req.getParameter("SendMail");

		if ("search".equals(sendMail)) {
			session.setAttribute("SendMail", null);
			req.getRequestDispatcher("/sendMail.jsp").forward(req,resp);
			logger.debug("success");
			return;
		}

		String namedata = req.getParameter("namedata");
		String mailarea = req.getParameter("mailarea");
		String notification = req.getParameter("notification");
		String value = req.getParameter("value");
		String no = req.getParameter("no");
		String textboxdata = req.getParameter("textboxdata");


		session.setAttribute("namedata", namedata);
		session.setAttribute("mailarea", mailarea);
		session.setAttribute("notification", notification);
		session.setAttribute("value", value);
		session.setAttribute("no", no);
		session.setAttribute("textboxdata", textboxdata);


		if(StringUtils.isEmpty(sendMail) == true) {
			session.setAttribute("SendMail", "NEW");
			req.getRequestDispatcher("/sendMail.jsp").forward(req,resp);
			return;
		}

		String title = "【盼达商务服务】感谢您的咨询"
				+ "";



		 textboxdata = namedata
			 		+ "<br>"
			 		+ "<br>您好。"
			 		+ "<br>"
			 		+ "<br>盼达商务服务已经收到您的如下咨询。"
			 		+ "<br>****************"
					+ "<br>[姓名]" + namedata + ""
					+ "<br>[联系方式]" + notification + ""
					+ "<br>[咨询内容]" + value + ""
					+ "<br>[案件No]" + no + ""
			 		+ "<br>[正文]<br>" + textboxdata+""
			 		+ "<br>****************"
			 		+ "<br>"
			 		+ "<br>我们将尽快与您联系。"
			 		+ "<br>感谢您对盼达商务服务的信赖与支持。"
			 		+ "<br>"
			 		+ "<br>盼达商务服务"
				+ "";



		SendMail SendMail = new SendMail();

		SendMail.sendMessage(null, mailarea, "info@pandaservicejapan.com", title, textboxdata);

		session.setAttribute("SendMail", "OK");

		req.getRequestDispatcher("/sendMail.jsp").forward(req,resp);

		logger.debug("success");

		return;

	}

}