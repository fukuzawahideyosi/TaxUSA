package com.demo.service;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.demo.bean.User;
import com.panda.dao.UserDao;

/**
 * Created by ForMe
 * ${PACKAGE_NAME}
 * 2018/12/1
 * 15:57
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UserDao userDAo = new UserDao();
        User user = userDAo.login(username,password);
        if(user != null){
            request.getRequestDispatcher("success.jsp").forward(request,response);
        }
        else {
            request.getRequestDispatcher("defeat.jsp").forward(request,response);
        }
    }
}
