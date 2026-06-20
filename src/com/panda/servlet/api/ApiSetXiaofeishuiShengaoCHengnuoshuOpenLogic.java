package com.panda.servlet.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.panda.servlet.SetXiaofeishuiShengaoCHengnuoshuOpenLogic;

@WebServlet("/swagger/api/ApiSetXiaofeishuiShengaoCHengnuoshuOpenLogic")
public class ApiSetXiaofeishuiShengaoCHengnuoshuOpenLogic extends SetXiaofeishuiShengaoCHengnuoshuOpenLogic {
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	doPost(req, resp);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");

		try {


			BufferedReader reader = req.getReader();
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
			    sb.append(line);
			}
			String requestBody = sb.toString();

			if (StringUtils.isEmpty(requestBody) == true) {
				ObjectMapper mapper = new ObjectMapper();
				ObjectNode emptyJson = mapper.createObjectNode(); // 创建空 JSON 对象
				requestBody = emptyJson.toString();
			}
			// JSON 转 Map
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> jsonMap = objectMapper.readValue(requestBody, new TypeReference<Map<String, Object>>() {});

			// 包装 request
			MyRequestWrapper wrapped = new MyRequestWrapper(req);
			wrapped.setParameter("hidden_key", "SetXiaofeishuiShengaoOpen");

			String authHeader = req.getHeader("Authorization");
			String token = "";
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
			    //token = authHeader.substring(7); // 提取 Bearer 后的部分
			}


			if (StringUtils.isEmpty(token) == true) {
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 | 未认证（需登录）   |
				resp.getWriter().write("无权限访问！");
				return;
			}


			wrapped.setParameter("license", token);

			// 循环设置参数
			for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
			    String key = entry.getKey();
			    String value = entry.getValue() == null ? "" : entry.getValue().toString();
			    wrapped.setParameter(key, value);
			}


			super.doPost(wrapped, resp);

			HttpSession session = req.getSession();
			String msg = (String) session.getAttribute("msg");
			System.out.println("服务端记录的返回值：" + msg);



			if (msg.contains("登录失败")) {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 | 请求参数错误
			} else if (msg.contains("invalid")) {
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 | 未认证（需登录）   |
				resp.getWriter().write("无权限访问！");
			} else {
				resp.setStatus(HttpServletResponse.SC_OK); // 200 状态码
			}
			return;

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 状态码
			resp.getWriter().write("登录失敗！");
		}


		resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 | 请求参数错误
		resp.getWriter().write("登录失敗！");
    }

    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	doGet(req, resp);
    }

    /*
| 常量名（HttpServletResponse）             | 状态码 | 中文含义       |
| ------------------------------------ | --- | ---------- |
| `SC_CONTINUE`                        | 100 | 继续         |
| `SC_SWITCHING_PROTOCOLS`             | 101 | 切换协议       |
| `SC_OK`                              | 200 | 请求成功       |
| `SC_CREATED`                         | 201 | 创建成功       |
| `SC_ACCEPTED`                        | 202 | 接受处理，但未完成  |
| `SC_NO_CONTENT`                      | 204 | 成功，无返回内容   |
| `SC_MOVED_PERMANENTLY`               | 301 | 永久重定向      |
| `SC_FOUND`（或 `SC_MOVED_TEMPORARILY`） | 302 | 临时重定向      |
| `SC_SEE_OTHER`                       | 303 | 重定向到其他资源   |
| `SC_NOT_MODIFIED`                    | 304 | 资源未修改      |
| `SC_BAD_REQUEST`                     | 400 | 请求参数错误     |
| `SC_UNAUTHORIZED`                    | 401 | 未认证（需登录）   |
| `SC_FORBIDDEN`                       | 403 | 无权限访问      |
| `SC_NOT_FOUND`                       | 404 | 请求资源不存在    |
| `SC_METHOD_NOT_ALLOWED`              | 405 | 请求方法不被允许   |
| `SC_REQUEST_TIMEOUT`                 | 408 | 请求超时       |
| `SC_CONFLICT`                        | 409 | 资源冲突（如重复）  |
| `SC_UNSUPPORTED_MEDIA_TYPE`          | 415 | 不支持的请求类型   |
| `SC_INTERNAL_SERVER_ERROR`           | 500 | 服务器内部错误    |
| `SC_NOT_IMPLEMENTED`                 | 501 | 功能未实现      |
| `SC_BAD_GATEWAY`                     | 502 | 网关错误       |
| `SC_SERVICE_UNAVAILABLE`             | 503 | 服务不可用（维护中） |
| `SC_GATEWAY_TIMEOUT`                 | 504 | 网关超时       |

     */

}
