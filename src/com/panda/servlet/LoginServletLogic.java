package com.panda.servlet;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.dao.User_infoDao;
import com.panda.utils.FuncUtils;

@WebServlet("/LoginServletLogic")
public class LoginServletLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(LoginServletLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

    private boolean authenticate(String user_id, String pw, HttpServletRequest request) {

		if (StringUtils.isEmpty(user_id) == true) {
	        request.setAttribute("error", "");
			return false;

		} else {
			FuncUtils FunctionUtils = new FuncUtils();
			User_infoDao User_infoDao = new User_infoDao();
			User_infoBean User_infoBean = User_infoDao.select(user_id);

			String license = User_infoBean.getLicense_yyyymmdd();
			logger.info("license YYYYMMDD" +  license);
			if (FunctionUtils.getLicensebooleanPW(pw, this.getServletName(), User_infoBean) == false) {
				logger.info("PandaServiceTools → License invalid");
	            request.setAttribute("error", "用户名或密码错误");
				return false;
			}

		}

        request.setAttribute("error", "");
		return true;




//        return "wangzihao".equals(username) && "8ryyg2ehx5mm1mmT".equals(password);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String currentUrl = request.getRequestURL().toString();
		URL url = new URL(currentUrl);
		String domain = url.getHost();
		String ProjectName = "";
		if (domain.contains("127.0.0.1")) {
			ProjectName = "/PandaServiceMA";
		}


		String username = request.getParameter("username");
        String password = request.getParameter("password");
        String redirect = request.getParameter("redirect");

        HttpSession session = request.getSession(true);
		if (StringUtils.isEmpty(redirect) == true) {
			redirect = (String) session.getAttribute("redirect");
		}
		if (StringUtils.isEmpty(redirect) == true) {
		} else {
			redirect = ProjectName + redirect;

		}

        if (authenticate(username, password, request)) {
            session.setAttribute("username", username);
            session.setAttribute("license", username);
            session.setAttribute("user_id", username);

            session.setMaxInactiveInterval(60 * 60 * 8); // 1天

            // 登录成功后跳转到原页面或默认首页
            if (redirect != null && !redirect.isEmpty()) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect(ProjectName + "/URLlistLogic");
            }

//            request.setAttribute("error", "");
        } else {
            request.getRequestDispatcher("/Panda-Login-Form/login.jsp").forward(request, response);
        }
    }
}
