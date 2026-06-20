package com.panda.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.QiyueBean;
import com.panda.dao.QiyueDao;
import com.panda.servlet.JPKI.JPKIBasicData_CerToUserinfo;

@WebServlet("/CheckCertificateLogic")
@MultipartConfig
public class CheckCertificateLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(CheckCertificateLogic.class.toString());

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		doPost(req, resp);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) {

		logger.info("start");

		try {

			boolean file = false;
			try {
				request.getParts();
				//初次认证
				file = true;
			} catch (ServletException e) {
				// TODO 自動生成された catch ブロック
				//				e.printStackTrace();
				//何もしない
			}

			HttpSession session = request.getSession();
			int count;

			String QIYUE_ID = request.getParameter("QIYUE_ID");
			if (StringUtils.isEmpty(QIYUE_ID) == true && file == false) {
				//契约画面初始表示
				session.setAttribute("QiyueBean", null);
				request.getRequestDispatcher("/checkCertificate.jsp").forward(request, response);
				return;

			}

			QiyueDao QiyueDao = new QiyueDao();
			if (file == true) {
				//添加契约
				QIYUE_ID = UUID.randomUUID().toString();
				count = QiyueDao.addQiyue(QIYUE_ID);
				logger.info("[addQiyue]" + count);

			} else {
				QiyueBean QiyueBean = QiyueDao.selectQiyue(QIYUE_ID);
				//				if (QiyueBean == null) {
				//					logger.info("[selectQiyue]0");
				//					count = QiyueDao.addQiyue(UUID.randomUUID().toString());
				//					logger.info("[addQiyue]" + count);
				//					QiyueBean = QiyueDao.selectQiyue(QIYUE_ID);
				//					session.setAttribute("QiyueBean", QiyueBean);
				//				} else {
				//
				//
				//				}

				session.setAttribute("QiyueBean", QiyueBean);
				request.getRequestDispatcher("/checkCertificate.jsp").forward(request, response);
				return;
			}

			request.setCharacterEncoding("utf-8");

			String str = "";
			boolean flag1 = false;
			boolean flag2 = false;
			String nowtime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());

			QiyueBean QiyueBean = new QiyueBean();
			QiyueBean.setQIYUE_ID(QIYUE_ID);
			// 拡張for文
			for (int j = 0; j < request.getParts().size(); j++) {
				//name属性がfileのファイルをPartオブジェクトとして取得
				Part part = request.getPart("file[" + j + "]");
				//ファイル名を取得
				//String filename=part.getSubmittedFileName();//ie対応が不要な場合
				String filename = "";
				filename = nowtime + "_" + QIYUE_ID + "_";
				filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();
				//アップロードするフォルダ
				String path = getServletContext().getRealPath("/fileQiyue");
				//書き込み
				part.write(path + File.separator + filename);

				String fe = FilenameUtils.getExtension(filename);

				if ("cer".equals(fe)) {
					byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

					// 基本４情報を取得
					JPKIBasicData_CerToUserinfo res = JPKIBasicData_CerToUserinfo.parseBasicData(cert);

					str = res.getName();
					QiyueBean.setJP_NAME(res.getName());

					if (StringUtils.isEmpty(str) == false) {
						flag1 = true;
					}
				}

			}

			if (flag1 == false) {
				str = "";
			} else {
				QiyueBean.setJP_ZHUANGTAI("OK");
				count = QiyueDao.updateQiyueJP(QiyueBean);
				logger.info("[updateQiyue]" + count);
				QiyueBean = QiyueDao.selectQiyue(QIYUE_ID);
				session.setAttribute("QiyueBean", QiyueBean);
			}

			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");

			PrintWriter pw = response.getWriter();

			//			JSONObject json = new JSONObject();
			//			json.put("JP_ZHUANGTAI",""+QiyueBean.getJP_ZHUANGTAI()+"");

			pw.println("{\"JP_ZHUANGTAI\":\"" + QiyueBean.getJP_ZHUANGTAI() + "\""
					+ ",\"JP_NAME\":\"" + QiyueBean.getJP_NAME() + "\""
					+ ",\"JP_UPDATE_DATE\":\"" + QiyueBean.getJP_UPDATE_DATE() + "\""
					+ "}");
			pw.flush();
			pw.close();

			logger.info("end");

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (ServletException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}