package com.panda.servlet.api;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.panda.servlet.ai.AiSetYewuLogic;

@WebServlet("/swagger/api/ApiAiSetYewuLogic")
public class ApiAiSetYewuLogic extends AiSetYewuLogic {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.getWriter().write("Hello Swagger");
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

        resp.getWriter().write("doPost登录成功！");
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

        resp.getWriter().write("doPut登录成功！");
    }

}
