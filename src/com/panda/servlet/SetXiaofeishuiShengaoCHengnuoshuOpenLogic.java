package com.panda.servlet;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.panda.batch.ExeDb;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_user_info_shoujiBean;
import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_user_info_shoujiDao;
import com.panda.dao.t_xiaofeishui_shengaoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;
import com.panda.utils.FuncUtilsExcel;
import com.panda.utils.XMLCalculator;

@WebServlet("/SetXiaofeishuiShengaoCHengnuoshuOpenLogic")
@MultipartConfig
public class SetXiaofeishuiShengaoCHengnuoshuOpenLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SetXiaofeishuiShengaoCHengnuoshuOpenLogic.class.toString());


	private static String shouxu_sheetName = "手続内帳票対応表（申告）";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();


		HttpSession session = req.getSession();

		String msg = "";
		String res = "";
		String yyyy = "2024";
		String activation_code = req.getParameter("activation_code");
		String PDSK = req.getParameter("PDSK");

		String hidden_key = req.getParameter("hidden_key");
		if (hidden_key == null) {
			hidden_key = "";
		}

		if (StringUtils.isEmpty(activation_code) == false && !activation_code.contains("激活完了pdf")) {

			t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
			t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("activation_code", activation_code);
			String yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();
			if (StringUtils.isEmpty(yyyymmdd_count) == false) {

				if ("".equals(hidden_key)) {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
					t_etax_account_infoExBean = t_etax_account_infoDao.select(t_xiaofeishui_shengaoBean.getYyyymmdd_count());
					session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);
					session.setAttribute("t_xiaofeishui_shengaoBean", t_xiaofeishui_shengaoBean);
					session.setAttribute("User_infoBean", new User_infoBean());
					req.getRequestDispatcher("/setXiaofeishuiShengaoCHengnuoshuOpen.jsp?fromBackend=true").forward(req, resp);

				} else if ("send-pdf".equals(hidden_key)) {
					try {
						SearchKuaijiLogic SearchKuaijiLogic = new SearchKuaijiLogic();
						String path = SearchKuaijiLogic.kuaiji_import(req, out, t_xiaofeishui_shengaoBean);
						t_xiaofeishui_shengaoDao.UpdateKeyValue(yyyymmdd_count, "activation_code", "激活完了pdf");


//						/*
//						 *客户信息收集
//						 */
//						String form_mailarea = req.getParameter("form_mailarea");
//						t_user_info_shoujiDao t_user_info_shoujiDao = new t_user_info_shoujiDao();
//						t_user_info_shoujiBean t_user_info_shoujiBean = t_user_info_shoujiDao.Select_email_and_yyyymmdd_count(form_mailarea, yyyymmdd_count);
//						if (StringUtils.isEmpty(t_user_info_shoujiBean.getForm_mailarea())) {
//							t_user_info_shoujiBean.setForm_mailarea(form_mailarea);
//							t_user_info_shoujiBean.setYyyymmdd_count(yyyymmdd_count);
//							t_user_info_shoujiDao.INSERT(t_user_info_shoujiBean);
//						}

						/*
						 *登录信息发邮件给客户
						 */
						t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
						t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

						User_infoDao User_infoDao = new User_infoDao();
						User_infoBean User_infoBean = User_infoDao.selectByYaoqing_no(t_etax_account_infoExBean.getYaoqing_no());

						t_etax_account_infoExBean.setEmail(User_infoBean.getEmail());
						t_xiaofeishui_shengaoBean.setDiv_chengren(req.getParameter("div_chengren") + req.getParameter("form_dailiName"));
						FuncUtils.sendMail_shengao_chengren(t_etax_account_infoExBean, t_xiaofeishui_shengaoBean, path);


						msg = "申告数据提交成功，請確認郵箱";
						res = res + msg;
				        logger.info("end add ok");

						msg = "{\"res\":\"" + res + "\"}";
				        session.setAttribute("msg", msg);out.print(msg);
				        return;

					} catch (Exception e) {
						// TODO 登录失败怎么办
						e.printStackTrace();
						msg = "登录失败，请联系客服！给您造成的不便，深感抱歉：" + e.getMessage();
						res = res + msg;
						res = res + "<br>";
						logger.info(res);


						msg = "{\"res\":\"" + res + "\"}";
						session.setAttribute("msg", msg);out.print(msg);
						return;

					}

				}

			} else {

				msg = "激活码【"+activation_code+"】无效，请联系客服！给您造成的不便，深感抱歉。";
				logger.debug(msg);
				session.setAttribute("msg", msg);out.print(msg);
			}

			logger.info("end");
			return;
		}



		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();
		User_infoBean User_infoBean_groupAdmin = new User_infoBean();

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("User_infoBean", new User_infoBean());
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);




		/*
		 * 邀请码有效性验证
		 */
		String yaoqing_no = req.getParameter("yaoqing_no");
		if (StringUtils.isEmpty(yaoqing_no) == false) {
			User_infoBean = LicenseDao.selectByTiaojian("yaoqing_no", yaoqing_no);
			if (StringUtils.isEmpty(User_infoBean.getYaoqing_no())) {

				msg = "邀请码【"+yaoqing_no+"】无效，请联系客服！给您造成的不便，深感抱歉。";
				logger.debug(msg);
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;

			} else {
				session.setAttribute("yaoqing_no", yaoqing_no);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yaoqing_no, " + yaoqing_no);
				logger.debug("yaoqing_no -> ok");

			}
			if (StringUtils.isEmpty(User_infoBean.getGroup_id()) == false) {
				User_infoBean_groupAdmin = LicenseDao.selectByTiaojian("user_id", User_infoBean.getGroup_id());
			}

		} else if ("".equals(yaoqing_no) == true) {
			msg = "邀请码没有输入，请输入邀请码。";
			logger.debug(msg);
			session.setAttribute("msg", msg);out.print(msg);
			logger.info("end " + hidden_key);
			return;
		}


		if (StringUtils.isEmpty(user_id) == true) {

		} else {

			/*
			 * license確認
			 */
			String pw = req.getParameter("pw");
			session.setAttribute("pw", pw);
//			PrintWriter out = resp.getWriter();
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
				msg = "PandaServiceTools → License invalid";
				logger.debug(msg);
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;
			}
		}





		if (!StringUtils.isEmpty(PDSK) && "get_file".equals(hidden_key) && "ZIP下载".equals(hidden_value)) {
			/*
			 * 申告上传文件下载
			 */

			t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
			t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("PDSK", PDSK);

			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(t_xiaofeishui_shengaoBean.getYyyymmdd_count());

			if (EtaxAccountInfoBean == null) {
				msg = "{\"res\":\"结果文件不存在\"}";
				logger.debug(msg);
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;
			}

			yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();
			String form_CompanyName_Chinese = EtaxAccountInfoBean.getCompanyName_Chinese();
			//去掉字符串里的TAB，首尾半角空格，首尾全角空格
			form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);



			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/kuaiji_import");
			String falieName = PDSK + "_" + yyyymmdd_count + "_" + yyyy;
			path = path + "/" + falieName;



			/*
			 * 文件名变更
			 */
//	        // 创建表示文件夹的File对象
//	        File directory = new File(path);
//	        // 检查文件夹是否存在
//	        if (directory.exists() && directory.isDirectory()) {
//	            // 获取文件夹中的所有文件
//	            File[] files = directory.listFiles();
//
//	            if (files != null) {
//	                // 循环遍历文件并修改文件名
//	                for (File file : files) {
//	                    // 检查文件是否是普通文件
//	                    if (file.isFile()) {
//	                        // 获取文件名
//	                        String oldFileName = file.getName();
//
//	                        if (oldFileName.indexOf(EtaxAccountInfoBean.getUser_id()) == 0) {
//	                        	continue;
//	                        }
//
//	                        // 新的文件名（在这里，将文件名更改为"new_" + 旧文件名）
//							String newFileName = EtaxAccountInfoBean.getUser_id() + "_" + EtaxAccountInfoBean.getYyyymmdd_count() + "_" + form_CompanyName_Chinese;
//							newFileName = newFileName + oldFileName.replaceAll(EtaxAccountInfoBean.getYyyymmdd_count(), "");
//
//	                        // 构建新的File对象，指定新文件名
//	                        File newFile = new File(path +"/"+ newFileName);
//
//	                        // 重命名文件
//	                        if (file.renameTo(newFile)) {
//
//	                        } else {
//	                        	logger.debug("无法更名文件 " + oldFileName);
//	                        }
//	                    }
//	                }
//	            } else {
//	            	logger.warn("文件夹中没有文件。" + path);
//	            }
//	        } else {
//	        	logger.warn("指定的文件夹路径不存在或不是文件夹。" + path);
//	        }



			File file = new File(path);
	        if (file.exists()) {

	        	/*
				 * 生成文件ZIP
				 */
				// 源文件夹的路径
				String sourceFolderPath = path;
				// 目标ZIP文件的路径
				String targetZipFilePath = path + ".zip";
				try {
					// 创建一个输出流，将文件写入ZIP文件
					FileOutputStream fos = new FileOutputStream(targetZipFilePath);
					ZipOutputStream zipOut = new ZipOutputStream(fos);

					// 调用递归方法将文件夹及其内容添加到ZIP文件中
					FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

					// 关闭ZIP文件输出流
					zipOut.close();
					fos.close();

					logger.info("ZIP文件创建成功：" + targetZipFilePath);

					msg = "{\"res\":\"" + falieName + ".zip" + "\"}";
					session.setAttribute("msg", msg);out.print(msg);

				} catch (IOException e) {
					e.printStackTrace();
				}




	        } else {

				msg = "{\"res\":\"结果文件不存在\"}";
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;
	        }


			logger.info("end " + hidden_key);
			return;





			/*
			 * ncc
			 */
		} else if ("get_file".equals(hidden_key) && "ncc".equals(hidden_value)) {
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", PDSK, " + PDSK);

			try {

				if (StringUtils.isEmpty(yyyymmdd_count) == true) {
					msg = "没有指定，管理ID！";
					res = res + msg;
//				res = res + "<br>";
					logger.info("end " + msg);
					msg = "{\"res\":\"" + res + "\"}";
					session.setAttribute("msg", msg);out.print(msg);
					return;


				}

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == true) {
					msg = "指定的管理ID，数据不存在！";
					res = res + msg;
//				res = res + "<br>";
					logger.info("end " + msg);
					msg = "{\"res\":\"" + res + "\"}";
					session.setAttribute("msg", msg);out.print(msg);
					return;
				}
				PDSK = t_etax_account_infoExBean.getTatujin_id() ;
				String path_ETAX_output = FuncUtils.projectPath + "ETAX_output/ncc";


				get_ncc(PDSK, path_ETAX_output, t_etax_account_infoExBean);

				String fileName = PDSK + "_" + FuncUtils.cut(t_etax_account_infoExBean.getCompanyName_English(), 25) + ".ncc";
				String pathNewNCC = path_ETAX_output + "/" + fileName;
				String path = pathNewNCC;
				File file = new File(path);
				if (file.exists()) {

					msg = "{"
			        		+ " \"res\":\"" + "ok" + "\""
			        		+ ",\"path\":\"" + fileName + "\""
			        		+ "}";
					session.setAttribute("msg", msg);out.print(msg);

				} else {

					msg = "{\"res\":\"结果文件不存在\"}";
					session.setAttribute("msg", msg);out.print(msg);
					logger.info("end " + hidden_key);
					return;
				}

			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();

				msg = "{\"res\":\""+e1+"\"}";
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;
			}



			logger.info("end " + hidden_value);
			return;




			/*
			 * xtx
			 */
		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "get_file".equals(hidden_key) && "xtx".equals(hidden_value)) {
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", PDSK, " + PDSK);

			try {

				String path = getXtx(req, PDSK);

				File file = new File(path);
				if (file.exists()) {

					/*
					 * 生成文件ZIP
					 */
					// 源文件夹的路径
					String sourceFolderPath = path;
					// 目标ZIP文件的路径
					String targetZipFilePath = path + ".zip";
					try {
						// 创建一个输出流，将文件写入ZIP文件
						FileOutputStream fos = new FileOutputStream(targetZipFilePath);
						ZipOutputStream zipOut = new ZipOutputStream(fos);

						// 调用递归方法将文件夹及其内容添加到ZIP文件中
						FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);

						// 关闭ZIP文件输出流
						zipOut.close();
						fos.close();

						logger.info("ZIP文件创建成功：" + targetZipFilePath);

						msg = "{\"res\":\"" + file.getName() + ".zip" + "\"}";
						session.setAttribute("msg", msg);out.print(msg);

					} catch (IOException e) {
						e.printStackTrace();


					}

				} else {

					msg = "{\"res\":\"结果文件不存在\"}";
					session.setAttribute("msg", msg);out.print(msg);
					logger.info("end " + hidden_key);
					return;
				}

			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();

				msg = "{\"res\":\""+e1+"\"}";
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end " + hidden_key);
				return;
			}



			logger.info("end " + hidden_value);
			return;


		} else if ("SetXiaofeishuiShengaoOpen".equals(hidden_key)) {
			// && "xiaofeishui".equals(hidden_value)

			/*
			 * 登录功能 客户数据
			 */
			try {

				String company_DD = req.getParameter("company_DD");
				String company_MM = req.getParameter("company_MM");
				String company_YYYY = req.getParameter("company_YYYY");
				String tel_1 = req.getParameter("tel_1");
				String tel_2 = req.getParameter("tel_2");
				String tel_3 = req.getParameter("tel_3");
				String tel_country = req.getParameter("tel_country");
				String xiaoshouerYYYY_1 = req.getParameter("xiaoshouerYYYY_1");
				String xiaoshouerYYYY_1_half = req.getParameter("xiaoshouerYYYY_1_half");
				String xiaoshouerYYYY_2 = req.getParameter("xiaoshouerYYYY_2");
				String zhice_ziben = req.getParameter("zhice_ziben");
				String address_Chinese = req.getParameter("address_Chinese");
				String CompanyName_Chinese = req.getParameter("CompanyName_Chinese");
				String CompanyName_English = req.getParameter("CompanyName_English");
				String DaibiaoName_Chinese = req.getParameter("DaibiaoName_Chinese");
				String DaibiaoName_English = req.getParameter("DaibiaoName_English");
				String geren_dianpu_address = req.getParameter("geren_dianpu_address");
				String geren_dianpu_name = req.getParameter("geren_dianpu_name");
				String changshe_jigou_Select = req.getParameter("changshe_jigou_Select");
				String jianyi_keshui_Select = req.getParameter("jianyi_keshui_Select");
				String address_English = req.getParameter("address_English");
				String jianyi_keshui_type = req.getParameter("jianyi_keshui_type");
				String tokutei_kikann_siharai_kyuuyo = req.getParameter("tokutei_kikann_siharai_kyuuyo");
				String shouri_kaishi_denglu_xiayige = req.getParameter("shouri_kaishi_denglu_xiayige");
				String shouri_kaishi_denglu_ben = req.getParameter("shouri_kaishi_denglu_ben");
//				user_id = req.getParameter("user_id");
				String etax_no = req.getParameter("etax_no");
				yaoqing_no = req.getParameter("yaoqing_no");


				String user_type = req.getParameter("user_type");
				String xiaofeishui_shuihao = req.getParameter("xiaofeishui_shuihao");
				String CompanyName_pianjiaming = req.getParameter("CompanyName_pianjiaming");
				String address_pianjiaming = req.getParameter("address_pianjiaming");
				String DaibiaoName_pianjiaming = req.getParameter("DaibiaoName_pianjiaming");
				String nashuidi_youbian1 = req.getParameter("nashuidi_youbian1");
				String nashuidi_youbian2 = req.getParameter("nashuidi_youbian2");
				String nashuidi = req.getParameter("nashuidi");
				String ksaTodofuken = req.getParameter("ksaTodofuken");
				String nashuidi_pianjiaming = req.getParameter("nashuidi_pianjiaming");
				String nashuidi_tel1 = req.getParameter("nashuidi_tel1");
				String nashuidi_tel2 = req.getParameter("nashuidi_tel2");
				String nashuidi_tel3 = req.getParameter("nashuidi_tel3");
				String guanxia_shuiwushu = req.getParameter("guanxia_shuiwushu");
				String liyongzhe_shibie_fanhao = req.getParameter("liyongzhe_shibie_fanhao");
				String shengao_shiqishou_YYYYMMDD = req.getParameter("shengao_shiqishou_YYYYMMDD");
				String shengao_shiqimo_YYYYMMDD = req.getParameter("shengao_shiqimo_YYYYMMDD");
				String yuanze_or_jianyi = req.getParameter("yuanze_or_jianyi");


				String form_mailarea = req.getParameter("form_mailarea");




				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
				CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
				address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
				address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
				DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
				DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);


				CompanyName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(CompanyName_pianjiaming);
				address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(address_pianjiaming);
				DaibiaoName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_pianjiaming);
				guanxia_shuiwushu = FuncUtils.trimWhitespaceAndTabs(guanxia_shuiwushu);
				liyongzhe_shibie_fanhao = FuncUtils.trimWhitespaceAndTabs(liyongzhe_shibie_fanhao);
				shengao_shiqishou_YYYYMMDD = FuncUtils.trimWhitespaceAndTabs(shengao_shiqishou_YYYYMMDD);
				shengao_shiqimo_YYYYMMDD = FuncUtils.trimWhitespaceAndTabs(shengao_shiqimo_YYYYMMDD);
				yuanze_or_jianyi = FuncUtils.trimWhitespaceAndTabs(yuanze_or_jianyi);


				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();


				xiaofeishui_shuihao = "T" + xiaofeishui_shuihao;

				if (StringUtils.isEmpty(PDSK)) {
					t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("InvoiceBangou", xiaofeishui_shuihao);

				} else {
					t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

				}
				yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_DD, " + company_DD);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_MM, " + company_MM);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_YYYY, " + company_YYYY);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", etax_no, " + etax_no);



				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type, " + user_type);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_pianjiaming, " + address_pianjiaming);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);






//				if (StringUtils.isEmpty(yyyymmdd_count) == false) {
//					t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
//				}

				t_etax_account_infoExBean.setYyyymmdd_count(yyyymmdd_count);
				t_etax_account_infoExBean.setUser_id(User_infoBean.getUser_id());
				t_etax_account_infoExBean.setCompanyName_Chinese(CompanyName_Chinese);
				t_etax_account_infoExBean.setCompanyName_English(CompanyName_English);
				t_etax_account_infoExBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
				t_etax_account_infoExBean.setDaibiaoName_English(DaibiaoName_English);
				t_etax_account_infoExBean.setCompany_DD(company_DD);
				t_etax_account_infoExBean.setCompany_MM(company_MM);
				t_etax_account_infoExBean.setCompany_YYYY(company_YYYY);
				t_etax_account_infoExBean.setTel_1(tel_1);
				t_etax_account_infoExBean.setTel_2(tel_2);
				t_etax_account_infoExBean.setTel_3(tel_3);
				t_etax_account_infoExBean.setTel_country(tel_country);
				t_etax_account_infoExBean.setXiaoshouerYYYY_1(xiaoshouerYYYY_1);
				t_etax_account_infoExBean.setXiaoshouerYYYY_1_half(xiaoshouerYYYY_1_half);
				t_etax_account_infoExBean.setXiaoshouerYYYY_2(xiaoshouerYYYY_2);
				t_etax_account_infoExBean.setZhice_ziben(zhice_ziben);
				t_etax_account_infoExBean.setAddress_Chinese(address_Chinese);
				t_etax_account_infoExBean.setGeren_dianpu_address(geren_dianpu_address);
				t_etax_account_infoExBean.setGeren_dianpu_name(geren_dianpu_name);
				t_etax_account_infoExBean.setChangshe_jigou_Select(changshe_jigou_Select);
				t_etax_account_infoExBean.setJianyi_keshui_Select(jianyi_keshui_Select);
				t_etax_account_infoExBean.setAddress_English(address_English);
				t_etax_account_infoExBean.setJianyi_keshui_type(jianyi_keshui_type);
				t_etax_account_infoExBean.setTokutei_kikann_siharai_kyuuyo(tokutei_kikann_siharai_kyuuyo);
				t_etax_account_infoExBean.setShouri_kaishi_denglu_xiayige(shouri_kaishi_denglu_xiayige);
				t_etax_account_infoExBean.setShouri_kaishi_denglu_ben(shouri_kaishi_denglu_ben);
				t_etax_account_infoExBean.setEtax_no(etax_no);
				t_etax_account_infoExBean.setYaoqing_no(yaoqing_no);




		        int digits = 8; // 需要生成的位数
		        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
		        t_etax_account_infoExBean.setEtax_pw(etax_pw);

				t_etax_account_infoExBean.setUser_type(user_type);
				t_etax_account_infoExBean.setXiaofeishui_shuihao(xiaofeishui_shuihao);
				t_etax_account_infoExBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
				t_etax_account_infoExBean.setAddress_pianjiaming(address_pianjiaming);
				t_etax_account_infoExBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);
				t_etax_account_infoExBean.setNashuidi_youbian1(nashuidi_youbian1);
				t_etax_account_infoExBean.setNashuidi_youbian2(nashuidi_youbian2);
				t_etax_account_infoExBean.setKsaTodofuken(ksaTodofuken);
				t_etax_account_infoExBean.setNashuidi(nashuidi);
				t_etax_account_infoExBean.setNashuidi_pianjiaming(nashuidi_pianjiaming);
				t_etax_account_infoExBean.setNashuidi_tel1(nashuidi_tel1);
				t_etax_account_infoExBean.setNashuidi_tel2(nashuidi_tel2);
				t_etax_account_infoExBean.setNashuidi_tel3(nashuidi_tel3);
				t_etax_account_infoExBean.setGuanxia_shuiwushu(guanxia_shuiwushu);
				t_etax_account_infoExBean.setLiyongzhe_shibie_fanhao(liyongzhe_shibie_fanhao);

		        t_etax_account_infoExBean.setEtax_pw_flag("0");

				if (StringUtils.isEmpty(xiaofeishui_shuihao) == false) {
					t_etax_account_infoExBean.setInvoiceBangou(xiaofeishui_shuihao);
				}


				if ("个人".equals(t_etax_account_infoExBean.getUser_type()) && StringUtils.isEmpty(t_etax_account_infoExBean.getCompanyName_Chinese())) {
					t_etax_account_infoExBean.setCompanyName_Chinese(t_etax_account_infoExBean.getDaibiaoName_Chinese());
				}

				if (StringUtils.isEmpty(yyyymmdd_count) == true) {
					m_sequenceDao m_sequenceDao = new m_sequenceDao();
					yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

					t_etax_account_infoExBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_account_infoExBean.setSyouninn_status("待处理");//承認無
					t_etax_account_infoExBean.setActivation_code(yyyymmdd_count);
					t_etax_account_infoExBean.setEtax_pw_flag("0");

					t_etax_account_infoDao.INSERT(t_etax_account_infoExBean);


					/*
					 *
					 */
					ExeDb.exe_activation(yyyymmdd_count);

					/*
					 * UPDATE t_etax_account_res SET HoujinBangou='9700150118570', InvoiceBangou='T9700150118570' WHERE yyyymmdd_count='20240603990001';
					 */

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, t_etax_account_infoExBean.getHoujinBangou());
					t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, t_etax_account_infoExBean.getInvoiceBangou());

					ExeDb.set_pianjiaming(yyyymmdd_count);
//					ExeDb.set_DaibiaoName_English(yyyymmdd_count);
//					ExeDb.setInfoForAPI(yyyymmdd_count);

					t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
					if (StringUtils.isEmpty(EtaxAccountInfoBean.getCompanyName_English())) {
						User_infoBean = LicenseDao.select(user_id);
						t_etax_account_infoDao.DELETE(User_infoBean, yyyymmdd_count);

						res = res + "税务局API同步失败<br>";
						logger.info(res);

					}

					t_etax_account_resDao.DELETE_res(yyyymmdd_count);
					ExeDb.exe_activation(yyyymmdd_count);
					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, t_etax_account_infoExBean.getHoujinBangou());
					t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, t_etax_account_infoExBean.getInvoiceBangou());

					//		Etax		12
					if (!StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
						t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "bangou", t_etax_account_infoExBean.getBangou());
					}
				} else {
					t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
					t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.Select_Where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);


					if ("T1234567890123".equals(t_etax_account_infoExBean.getInvoiceBangou()) || "T0987654321098".equals(t_etax_account_infoExBean.getInvoiceBangou())) {

					} else {
						if ("激活完了pdf".equals(t_xiaofeishui_shengaoBean.getActivation_code())) {
							msg = "本申告数据，已经提交过确认书，无法再次提交！";
							res = res + msg;
//						res = res + "<br>";
							logger.info("end " + msg);

							msg = "{\"res\":\"" + res + "\"}";
							session.setAttribute("msg", msg);out.print(msg);
							return;
						}

					}

					//admin 更新时 User_id 不变
					if ("admin".equals(User_infoBean.getPermissions())) {
						t_etax_account_infoExBean.setUser_id("");
					}

					t_etax_account_infoDao.Update_user_info(yyyymmdd_count, t_etax_account_infoExBean);

				}

				String form_shengao_qijian_from = req.getParameter("form_shengao_qijian_from");
				String form_shengao_qijian_to = req.getParameter("form_shengao_qijian_to");
				String form_jizhun_qijian = req.getParameter("form_jizhun_qijian");
				String form_teding_qijian = req.getParameter("form_teding_qijian");
				String form_shangyi_niandu = req.getParameter("form_shangyi_niandu");
				String form_qunian_xiaofeishui_shengao = req.getParameter("form_qunian_xiaofeishui_shengao");
				String form_keshui_type = req.getParameter("form_keshui_type");
				String form_qunian_xiaofeishui_guoshui = req.getParameter("form_qunian_xiaofeishui_guoshui");

				if (StringUtils.isEmpty(form_qunian_xiaofeishui_guoshui)) {
					form_qunian_xiaofeishui_guoshui = "0";
				}

				String form_hanshui_zongxiaoshoue = req.getParameter("form_hanshui_zongxiaoshoue");
				String form_shige_qingqiushu_zongzhichue = req.getParameter("form_shige_qingqiushu_zongzhichue");
				String form_fei_shige_qingqiushu_zongzhichue = req.getParameter("form_fei_shige_qingqiushu_zongzhichue");
				String form_jinkou_xiaofeishui_guoshui_zonge = req.getParameter("form_jinkou_xiaofeishui_guoshui_zonge");



				String fading_zhongjian_shengao_cishu  = req.getParameter("form_B9");
				String fading_zhongjian_shengao_danci_duiying_yueshu  = req.getParameter("form_B10");
				String fading_zhongjian_shengao_danci_guoshui_e  = req.getParameter("form_B11");
				String fading_zhongjian_shengao_danci_difangshui_e  = req.getParameter("form_B12");
				String buhan_shui_xiaoshou_e  = req.getParameter("form_B13");
				String keshuibiao_zhun_e  = req.getParameter("form_B14");
				String xiaofeishui_e_guoshui_bufen  = req.getParameter("form_B15");
				String kongchu_shui_e_guoshui_bufen_you_hegui_fapiao  = req.getParameter("form_B16");
				String kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao  = req.getParameter("form_B17");
				String kongchu_shui_e_guoshui_bufen_jinkou  = req.getParameter("form_B18");
				String kongchu_shui_e_guoshui_bufen_heji  = req.getParameter("form_B19");
				String quannian_yingjiao_xiaofeishui_guoshui_bufen  = req.getParameter("form_B20");
				String zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen  = req.getParameter("form_B21");
				String queren_shengao_yingjiao_xiaofeishui_guoshui_bufen  = req.getParameter("form_B22");
				String quannian_yingjiao_xiaofeishui_difangshui_bufen  = req.getParameter("form_B23");
				String zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen  = req.getParameter("form_B24");
				String queren_shengao_yingjiao_xiaofeishui_difangshui_bufen  = req.getParameter("form_B25");
				String quannian_yingjiao_xiaofeishui_heji  = req.getParameter("form_B26");
				String zhongjian_shengao_yingjiao_xiaofeishui_heji  = req.getParameter("form_B27");
				String queren_shengao_yingjiao_xiaofeishui_heji = req.getParameter("form_B28");



				t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = new t_xiaofeishui_shengaoBean();
				t_xiaofeishui_shengaoBean.setYyyymmdd_count(yyyymmdd_count);
				t_xiaofeishui_shengaoBean.setYyyy(yyyy);
				t_xiaofeishui_shengaoBean.setShengao_qijian_from(form_shengao_qijian_from);
				t_xiaofeishui_shengaoBean.setShengao_qijian_to(form_shengao_qijian_to);
				t_xiaofeishui_shengaoBean.setJizhun_qijian(form_jizhun_qijian);
				t_xiaofeishui_shengaoBean.setTeding_qijian(form_teding_qijian);
				t_xiaofeishui_shengaoBean.setShangyi_niandu(form_shangyi_niandu);
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_shengao(form_qunian_xiaofeishui_shengao);
				t_xiaofeishui_shengaoBean.setKeshui_type(form_keshui_type);
				t_xiaofeishui_shengaoBean.setQunian_xiaofeishui_guoshui(form_qunian_xiaofeishui_guoshui);
				t_xiaofeishui_shengaoBean.setHanshui_zongxiaoshoue(form_hanshui_zongxiaoshoue);
				t_xiaofeishui_shengaoBean.setShige_qingqiushu_zongzhichue(form_shige_qingqiushu_zongzhichue);
				t_xiaofeishui_shengaoBean.setFei_shige_qingqiushu_zongzhichue(form_fei_shige_qingqiushu_zongzhichue);
				t_xiaofeishui_shengaoBean.setJinkou_xiaofeishui_guoshui_zonge(form_jinkou_xiaofeishui_guoshui_zonge);


				t_xiaofeishui_shengaoBean.setForm_mailarea(form_mailarea);

				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_cishu(fading_zhongjian_shengao_cishu);
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_duiying_yueshu(fading_zhongjian_shengao_danci_duiying_yueshu);
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_guoshui_e(fading_zhongjian_shengao_danci_guoshui_e);
				t_xiaofeishui_shengaoBean.setFading_zhongjian_shengao_danci_difangshui_e(fading_zhongjian_shengao_danci_difangshui_e);
				t_xiaofeishui_shengaoBean.setBuhan_shui_xiaoshou_e(buhan_shui_xiaoshou_e);
				t_xiaofeishui_shengaoBean.setKeshuibiao_zhun_e(keshuibiao_zhun_e);
				t_xiaofeishui_shengaoBean.setXiaofeishui_e_guoshui_bufen(xiaofeishui_e_guoshui_bufen);
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_you_hegui_fapiao(kongchu_shui_e_guoshui_bufen_you_hegui_fapiao);
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_wu_hegui_fapiao(kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao);
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_jinkou(kongchu_shui_e_guoshui_bufen_jinkou);
				t_xiaofeishui_shengaoBean.setKongchu_shui_e_guoshui_bufen_heji(kongchu_shui_e_guoshui_bufen_heji);
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_guoshui_bufen(quannian_yingjiao_xiaofeishui_guoshui_bufen);
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen(zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen);
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_guoshui_bufen(queren_shengao_yingjiao_xiaofeishui_guoshui_bufen);
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_difangshui_bufen(quannian_yingjiao_xiaofeishui_difangshui_bufen);
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen(zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen);
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_difangshui_bufen(queren_shengao_yingjiao_xiaofeishui_difangshui_bufen);
				t_xiaofeishui_shengaoBean.setQuannian_yingjiao_xiaofeishui_heji(quannian_yingjiao_xiaofeishui_heji);
				t_xiaofeishui_shengaoBean.setZhongjian_shengao_yingjiao_xiaofeishui_heji(zhongjian_shengao_yingjiao_xiaofeishui_heji);
				t_xiaofeishui_shengaoBean.setQueren_shengao_yingjiao_xiaofeishui_heji(queren_shengao_yingjiao_xiaofeishui_heji);

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yyyy, " + yyyy);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_shengao_qijian_from, " + form_shengao_qijian_from);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_shengao_qijian_to, " + form_shengao_qijian_to);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_jizhun_qijian, " + form_jizhun_qijian);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_teding_qijian, " + form_teding_qijian);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_shangyi_niandu, " + form_shangyi_niandu);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_qunian_xiaofeishui_shengao, " + form_qunian_xiaofeishui_shengao);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_keshui_type, " + form_keshui_type);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_qunian_xiaofeishui_guoshui, " + form_qunian_xiaofeishui_guoshui);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_hanshui_zongxiaoshoue, " + form_hanshui_zongxiaoshoue);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_shige_qingqiushu_zongzhichue, " + form_shige_qingqiushu_zongzhichue);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_fei_shige_qingqiushu_zongzhichue, " + form_fei_shige_qingqiushu_zongzhichue);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_jinkou_xiaofeishui_guoshui_zonge, " + form_jinkou_xiaofeishui_guoshui_zonge);

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", form_mailarea, " + form_mailarea);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", fading_zhongjian_shengao_cishu , " + fading_zhongjian_shengao_cishu );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", fading_zhongjian_shengao_danci_duiying_yueshu , " + fading_zhongjian_shengao_danci_duiying_yueshu );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", fading_zhongjian_shengao_danci_guoshui_e , " + fading_zhongjian_shengao_danci_guoshui_e );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", fading_zhongjian_shengao_danci_difangshui_e , " + fading_zhongjian_shengao_danci_difangshui_e );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", buhan_shui_xiaoshou_e , " + buhan_shui_xiaoshou_e );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshuibiao_zhun_e , " + keshuibiao_zhun_e );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaofeishui_e_guoshui_bufen , " + xiaofeishui_e_guoshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", kongchu_shui_e_guoshui_bufen_you_hegui_fapiao , " + kongchu_shui_e_guoshui_bufen_you_hegui_fapiao );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao , " + kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", kongchu_shui_e_guoshui_bufen_jinkou , " + kongchu_shui_e_guoshui_bufen_jinkou );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", kongchu_shui_e_guoshui_bufen_heji , " + kongchu_shui_e_guoshui_bufen_heji );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", quannian_yingjiao_xiaofeishui_guoshui_bufen , " + quannian_yingjiao_xiaofeishui_guoshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen , " + zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", queren_shengao_yingjiao_xiaofeishui_guoshui_bufen , " + queren_shengao_yingjiao_xiaofeishui_guoshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", quannian_yingjiao_xiaofeishui_difangshui_bufen , " + quannian_yingjiao_xiaofeishui_difangshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen , " + zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", queren_shengao_yingjiao_xiaofeishui_difangshui_bufen , " + queren_shengao_yingjiao_xiaofeishui_difangshui_bufen );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", quannian_yingjiao_xiaofeishui_heji , " + quannian_yingjiao_xiaofeishui_heji );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhongjian_shengao_yingjiao_xiaofeishui_heji , " + zhongjian_shengao_yingjiao_xiaofeishui_heji );
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", queren_shengao_yingjiao_xiaofeishui_heji, " + queren_shengao_yingjiao_xiaofeishui_heji);

				/*
				 *
				 */
				t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
				t_xiaofeishui_shengaoBean my_t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.Select_Where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);
				PDSK = my_t_xiaofeishui_shengaoBean.getPDSK();


				String uuid = UUID.randomUUID().toString();
				t_xiaofeishui_shengaoBean.setActivation_code(uuid);
				m_sequenceDao m_sequenceDao = new m_sequenceDao();

				if (StringUtils.isEmpty(PDSK)) {
					PDSK = "PDSK" + yyyy.substring(2, 4) + m_sequenceDao.selectMax_PDSK();
				}
				t_xiaofeishui_shengaoBean.setPDSK(PDSK);

				t_xiaofeishui_shengaoDao.delete_where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);
				t_xiaofeishui_shengaoDao.INSERT(t_xiaofeishui_shengaoBean);

				/*
				 *上传附件
				 */
				//删除原有数据
				String path_kuaiji_import = req.getServletContext().getRealPath("/kuaiji_import");
				String fileName = PDSK + "_" + yyyymmdd_count + "_" + yyyy;
				path_kuaiji_import = path_kuaiji_import + "/" + fileName;
				FuncUtils.deleteFolder(new File(path_kuaiji_import));

				SearchKuaijiLogic SearchKuaijiLogic = new SearchKuaijiLogic();
				String path = SearchKuaijiLogic.kuaiji_import(req, out, t_xiaofeishui_shengaoBean);


//				if (StringUtils.isEmpty(req.getParameter("PDSK"))) {
//
//				} else {
//					msg = "数据登录成功";
//					res = res + msg;
////					res = res + "<br>";
//				}

				 /*
				  *客户信息收集
				  */
				t_user_info_shoujiDao t_user_info_shoujiDao = new t_user_info_shoujiDao();
				t_user_info_shoujiBean t_user_info_shoujiBean = t_user_info_shoujiDao.Select_email_and_yyyymmdd_count(form_mailarea, yyyymmdd_count);
				if (StringUtils.isEmpty(t_user_info_shoujiBean.getForm_mailarea())) {
					t_user_info_shoujiBean.setForm_mailarea(form_mailarea);
					t_user_info_shoujiBean.setYyyymmdd_count(yyyymmdd_count);
					t_user_info_shoujiDao.INSERT(t_user_info_shoujiBean);
				}

				/*
				 *登录信息发邮件给客户
				 */
				t_etax_account_infoExBean.setEmail(User_infoBean.getEmail());
				FuncUtils.sendMail_shengao(t_etax_account_infoExBean, t_xiaofeishui_shengaoBean, path, User_infoBean_groupAdmin);

				msg = "数据登录成功，請確認郵箱";
				res = res + msg;
//				res = res + "<br>";


				if ("KXT_iRStvO".equals(yaoqing_no) || "admin".equals(User_infoBean.getPermissions())) {

				} else {
					uuid = "";
				}

		        logger.info("end add ok");

				msg = "{"
		        		+ " \"res\":\"" + res + "\""
		        		+ ",\"activation_code\":\"" + uuid + "\""
		        		+ "}";
		        session.setAttribute("msg", msg);out.print(msg);
		        return;

			} catch (Exception e) {
				// TODO 登录失败怎么办
				e.printStackTrace();
				msg = "登录失败，请联系客服！给您造成的不便，深感抱歉：" + e.getMessage();
				res = res + msg;
				res = res + "<br>";
				logger.info(res);

				msg = "{\"res\":\"" + res + "\"}";
				session.setAttribute("msg", msg);out.print(msg);

				return;

			}


		} else if ("button-search".equals(hidden_key)) {


			try {

				String key = req.getParameter("key");
				String value = req.getParameter("value");
				if ("InvoiceBangou".toLowerCase().equals(key.toLowerCase())) {
					value = "T" + value;
				}

				t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou_json(value);

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean db_t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("InvoiceBangou", value);
				if (StringUtils.isEmpty(db_t_etax_account_infoExBean.getYyyymmdd_count()) == false) {
//					t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
//					t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.Select_Where_yyyymmdd_count_and_yyyy(db_t_etax_account_infoExBean.getYyyymmdd_count(), yyyy);
//					if (t_xiaofeishui_shengaoBean.getActivation_code().contains("激活完了pdf")) {
//						t_etax_account_infoExBean = new t_etax_account_infoExBean();
//
//					} else {
//
//
//					}

					t_etax_account_infoExBean.setCompanyName_Chinese(db_t_etax_account_infoExBean.getCompanyName_Chinese());
					t_etax_account_infoExBean.setDaibiaoName_Chinese(db_t_etax_account_infoExBean.getDaibiaoName_Chinese());
					t_etax_account_infoExBean.setAddress_Chinese(db_t_etax_account_infoExBean.getAddress_Chinese());

					t_etax_account_infoExBean.setDaibiaoName_English(db_t_etax_account_infoExBean.getDaibiaoName_English());

					t_etax_account_infoExBean.setCompany_YYYY(db_t_etax_account_infoExBean.getCompany_YYYY());
					t_etax_account_infoExBean.setCompany_MM(db_t_etax_account_infoExBean.getCompany_MM());
					t_etax_account_infoExBean.setCompany_DD(db_t_etax_account_infoExBean.getCompany_DD());
				}

				//TEST公司
				if ("T1234567890123".equals(value)) {
					t_etax_account_infoExBean = new t_etax_account_infoExBean();
					t_etax_account_infoExBean.setUser_type("2");
					t_etax_account_infoExBean.setInvoiceBangou("T1234567890123");

					t_etax_account_infoExBean.setCompanyName_English("TEST CompanyName_English");
					t_etax_account_infoExBean.setCompanyName_Chinese("测试公司中文");
					t_etax_account_infoExBean.setAddress_English("TEST Address_English");
					t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
					t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
					t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");

					t_etax_account_infoExBean.setCompany_YYYY("2000");
					t_etax_account_infoExBean.setCompany_MM("01");
					t_etax_account_infoExBean.setCompany_DD("02");

				} else if ("T0987654321098".equals(value)) {
					//TEST个人
					t_etax_account_infoExBean = new t_etax_account_infoExBean();
					t_etax_account_infoExBean.setUser_type("1");
					t_etax_account_infoExBean.setInvoiceBangou("T0987654321098");
					t_etax_account_infoExBean.setCompanyName_English("TEST geren_English");
					t_etax_account_infoExBean.setCompanyName_Chinese("测试个人中文");
					t_etax_account_infoExBean.setAddress_English("TEST Address_English");
					t_etax_account_infoExBean.setAddress_Chinese("测试地址中文");
					t_etax_account_infoExBean.setDaibiaoName_English("TEST DaibiaoName_English");
					t_etax_account_infoExBean.setDaibiaoName_Chinese("测试代表中文");

					t_etax_account_infoExBean.setCompany_YYYY("2001");
					t_etax_account_infoExBean.setCompany_MM("11");
					t_etax_account_infoExBean.setCompany_DD("22");
				}


				// 将Java对象转换为JSON字符串
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(t_etax_account_infoExBean);


				msg = jsonString;
				session.setAttribute("msg", msg);out.print(msg);

			} catch (Exception e) {
				e.printStackTrace();
			}



			logger.info("end " + hidden_key);
			return;

		} else if (StringUtils.isEmpty(yaoqing_no) == false) {
			session.setAttribute("t_etax_account_infoExBean", new t_etax_account_infoExBean());
			session.setAttribute("t_xiaofeishui_shengaoBean",  new t_xiaofeishui_shengaoBean());
			req.getRequestDispatcher("/setXiaofeishuiShengaoCHengnuoshuOpen.jsp?fromBackend=true").forward(req, resp);

			logger.info("end");
			return;

		} else if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
			if (StringUtils.isEmpty(PDSK) == true) {
				msg = "PandaServiceTools → PDSK invalid";
				logger.debug(msg);
				session.setAttribute("msg", msg);out.print(msg);
				logger.info("end");
				return;
			}


			try {

				t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
				t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("PDSK", PDSK);
//				t_xiaofeishui_shengaoBean.setActivation_code("");
				yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();
				if (StringUtils.isEmpty(yyyymmdd_count) == false) {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(t_xiaofeishui_shengaoBean.getYyyymmdd_count());
					session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);
					session.setAttribute("t_xiaofeishui_shengaoBean", t_xiaofeishui_shengaoBean);

				} else {

					//session.setAttribute("User_infoBean", new User_infoBean());

					msg = "PandaServiceTools → PDSK invalid";
					logger.debug(msg);
					session.setAttribute("msg", msg);out.print(msg);
					logger.info("end");
					return;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			req.getRequestDispatcher("/setXiaofeishuiShengaoCHengnuoshuOpen.jsp?fromBackend=true").forward(req, resp);
			logger.info("end");
			return;



		} else {




			msg = "PandaServiceMA → License invalid";
			logger.debug(msg);
			session.setAttribute("msg", msg);out.print(msg);

			logger.info("end");
			return;
		}
	}



    public static void main(String[] args)  {




//        String folderPath = "E:\\日本-软件开发\\e-Tax仕様書一覧全仕様書（一括ダウンロード）\\11XML構造設計書等【消費税】";
//        folderPath = "E:\\日本-软件开发\\e-Tax仕様書一覧全仕様書（一括ダウンロード）\\17XML構造設計書等【その他】";
//        folderPath = "E:\\日本-软件开发\\11XML構造設計書等【消費税】";
//        File folder = new File(folderPath);

//    	String PDSK = "PDSK240027";
//        try {
//			getXtx(null, PDSK);
//		} catch (Exception e) {
//			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
//		}






    	/*
    	 *
    	 *
    	 *
    	 */

        // 递归遍历目录并读取csv文件
        try {
    		logger.info("Processed file and updated processCsvFile_shouxu.");
            Files.walk(Paths.get(directoryPath + "/07手続一覧等"))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_shouxu(path.toFile()));


    		logger.info("Processed file and updated processCsvFile_zhangpiao.");
            Files.walk(Paths.get(directoryPath))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_zhangpiao(path.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 输出结果，查看 PropertyCsvMap_shouxu
//        logger.debug(PropertyCsvMap_shouxu);



    }






	private static String getXtx(HttpServletRequest req, String PDSK) throws Exception {

		ZipSecureFile.setMinInflateRatio(0);

		String path = FuncUtils.projectPath + "ETAX_moban";
		String path_Excel = path + "/e-Tax仕様書一覧全仕様書（PS）" + "/RSH0020";
		String path_ETAX_output = FuncUtils.projectPath + "ETAX_output";

		t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
		t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("PDSK", PDSK);


		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(t_xiaofeishui_shengaoBean.getYyyymmdd_count());

		String fileName = PDSK + "_" + t_etax_account_infoExBean.getCompanyName_English() + "_xtx";
    	path_ETAX_output = path_ETAX_output + "/" + fileName;


    	FuncUtils.deleteFile(path_ETAX_output + ".zip");
    	FuncUtils.deleteFolder(new File(path_ETAX_output));


        StringBuffer loggerStringBuffer = new StringBuffer();
        writeToFile(loggerStringBuffer.toString(), path_ETAX_output + ".log");


		// 将字符串转换为 XML Document 对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // 启用命名空间支持
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(Template)));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String shouxu_id = null;
        String  shouxu_banben = null;

    	LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

    	/*
		 * xtx作成
		 */
		xtxMap = getXtx_sheet(PDSK, path, path_Excel, path_ETAX_output, t_xiaofeishui_shengaoBean, t_etax_account_infoExBean, xtxMap);

		/*
		 * ncc
		 */

		get_ncc(PDSK, path_ETAX_output, t_etax_account_infoExBean);



		/*
		 * IT
		 */
		LinkedHashMap<String, String> xtxMap_IT = new LinkedHashMap<String, String>();
		XPathExpression expression = xpath.compile("//*[@id='IT']");
		Element element = (Element) expression.evaluate(document, XPathConstants.NODE);

		if (element != null) {
			xtxMap_IT = getXtx_sheet_IT(PDSK, path, path_Excel, path_ETAX_output, t_xiaofeishui_shengaoBean, t_etax_account_infoExBean, xtxMap);

			String[] shouxu_liset = xtxMap.get(shouxu_sheetName).split(",");
			shouxu_id = shouxu_liset[0];
			shouxu_banben = shouxu_liset[1];

			xtxMap.remove(shouxu_sheetName);
//            // 新建一个临时 LinkedHashMap 并添加新元素在最前面
//            LinkedHashMap<String, String> newXtxMap = new LinkedHashMap<>();
//            newXtxMap.put("PS_IT部", xtxMap_IT.get("PS_IT部")); // 新元素添加在最前
//            // 将原始元素添加到新 LinkedHashMap
//            newXtxMap.putAll(xtxMap);
//            // 用新的 map 覆盖旧的 map
//            xtxMap = newXtxMap;

            for (String key : xtxMap_IT.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap_IT.get(key));
//                element.appendChild(newChild.getChildNodes());
                // 循环 newChild 的所有子节点并添加到 element
                NodeList childNodes = newChild.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    // 采用 importNode 方法确保正确的父子结构
                    Node importedChild = document.importNode(childNode, true);
                    element.appendChild(importedChild);
                }
            }

		} else {
			logger.error("未找到 ID 为 'IT' 的元素。");
		}



        /*
         * CONTENTS
         */
        expression = xpath.compile("//*[@id='CONTENTS']");
        element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap.get(key));
                element.appendChild(newChild);
            }



        } else {
            logger.error("未找到 ID 为 'IT' 的元素。");
        }


        /*
         * CATALOG
         */

        // 使用 XPath 查找 <rdf:Seq> 元素
         xPathFactory = XPathFactory.newInstance();
         xpath = xPathFactory.newXPath();

        // 设置命名空间上下文以解析带有前缀的元素
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if ("rdf".equals(prefix)) {
                    return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                }
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }

            public Iterator<String> getPrefixes(String uri) {
                return null;
            }
        });

        // 查找 <rdf:Seq> 标签
		expression = xpath.compile("//rdf:Seq");
		element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            logger.debug("找到的元素：<rdf:Seq>");
//            logger.debug("内容：" + element.getTextContent());

            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
//    			+ "              <rdf:li>"
//    			+ "                <rdf:description about=\"#SHA010-1\" />"
//    			+ "              </rdf:li>"


                //TENPU
				if (key.contains("soz")) {

				} else {
					Element newChild = document.createElement("rdf:li");
					newChild.setTextContent("<rdf:description about=\"#" + key.toUpperCase().split("_")[0] + "-1\" />");
					element.appendChild(newChild);

				}
            }




        } else {
            logger.error("未找到 <rdf:Seq> 元素。");
        }



        File folder = new File(path_ETAX_output);
		String fileExtension = ".xtx"; // 需要删除的文件扩展名
		FuncUtils.deleteFilesInFolder(folder, fileExtension);





/*

<	&amp;lt;
"	&amp;quot;
>	&amp;gt;


 */

        /*
         * all.xtx
         */
        // 输出修改后的 XML 到文件
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String outputString = writer.toString();

        for (String key : xtxMap.keySet()) {
        	outputString = outputString.replace("<"+key+">", "");
        	outputString = outputString.replace("</"+key+">", "");
        }

    	outputString = outputString.replace("&lt;", "<");
    	outputString = outputString.replace("&gt;", ">");
    	outputString = outputString.replace("&#13;", "");

    	outputString = outputString.replace("PS_shouxu_id_PS", shouxu_id);
    	outputString = outputString.replace("PS_shouxu_banben_PS", shouxu_banben);

    	outputString = outputString.replace("xmlns=\"\"", "");

    	String key = "PS_ROOT";
    	outputString = outputString.replace("<"+key+">", "");
    	outputString = outputString.replace("</"+key+">", "");

        // 移除空行
        outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");


        // 输出格式化后的 XML 字符串到后台
//        logger.info("XML 格式化输出：");
//        logger.info("\n" + outputString);


        String outputPath = path_ETAX_output + "/" + PDSK + "_all.xtx"; // 输出文件路径
        Files.write(Paths.get(outputPath), outputString.getBytes());
        logger.debug("字符串已成功输出到文件：" + outputPath);


       	return path_ETAX_output;

	}

	private static void get_ncc(String PDSK, String path_ETAX_output, t_etax_account_infoExBean t_etax_account_infoExBean) throws IOException {
		String pathNCC0 = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/ps_ncc.ncc";
		File dataModelFileNCC0 = new File(pathNCC0);
		String fileContent0 = FuncUtils.readFileContent(dataModelFileNCC0);
		String pathNewNCC = path_ETAX_output + "/" + PDSK + "_" + FuncUtils.cut(t_etax_account_infoExBean.getCompanyName_English(), 25) + ".ncc";

		// 将 A 列和 B 列的数据存储到 excelData HashMap
		String ncc_key = "";
		String ncc_value = "";



//		#利用者識別番号#
		ncc_key = "#利用者識別番号#";
		ncc_value = t_etax_account_infoExBean.getBangou();
		if (StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
			ncc_value = "";
		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);


//			#法人番号#
		ncc_key = "#法人番号#";
		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			ncc_value = "";
		} else {
			ncc_value = t_etax_account_infoExBean.getHoujinBangou();
			if (StringUtils.isEmpty(t_etax_account_infoExBean.getHoujinBangou())) {
				ncc_value = "";
			}

		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#事業者名#
		ncc_key = "#事業者名#";
		ncc_value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25);
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#事業者名カナ#
		ncc_key = "#事業者名カナ#";
		ncc_value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_pianjiaming(), 25);
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#代表人氏名#
		ncc_key = "#代表人氏名#";
		ncc_value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getDaibiaoName_English());
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#代表人氏名カナ#
		ncc_key = "#代表人氏名カナ#";
		ncc_value = t_etax_account_infoExBean.getDaibiaoName_pianjiaming();
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#個人法人フラグ#	個人0	法人1
		ncc_key = "#個人法人フラグ#";
		ncc_value = "1";
		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			ncc_value = "0";

		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);



		// 写入文件
		FileWriter writer = new FileWriter(pathNewNCC);
		writer.write(fileContent0);
		writer.close();
		logger.debug("File saved: " + pathNewNCC);
	}

	private static LinkedHashMap<String, String> getXtx_sheet(String PDSK, String path, String path_Excel, String path_ETAX_output
			, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

        try {

    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";

        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";

        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";

        		}

    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";

        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";

        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";

        		}

    		}

        	xtxMap = findSheetsStartingWithPS(t_etax_account_infoExBean, t_xiaofeishui_shengaoBean, path, path_Excel, path_ETAX_output + "/" + PDSK);

        } catch (Exception e) {
//            e.printStackTrace();
            throw e;
        }

		return xtxMap;
	}

	private static LinkedHashMap<String, String> getXtx_sheet_IT(String PDSK, String path, String path_Excel, String path_ETAX_output, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

//		path_Excel = path_Excel + "/4 IT部仕様書/IT部PS仕様書.xlsx";
        try {
    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";

        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";

        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";

        		}

    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";

        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";

        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";

        		}

    		}

        	xtxMap = findSheetsStartingWithPS_IT(t_etax_account_infoExBean, t_xiaofeishui_shengaoBean, path, path_Excel, path_ETAX_output + "/" + PDSK, xtxMap);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }


		return xtxMap;
	}




	static String Template = ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
			+ "<DATA id=\"DATA\" xmlns=\"http://xml.e-tax.nta.go.jp/XSD/shohi\" xmlns:gen=\"http://xml.e-tax.nta.go.jp/XSD/general\" xmlns:kyo=\"http://xml.e-tax.nta.go.jp/XSD/kyotsu\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "  <PS_shouxu_id_PS VR=\"PS_shouxu_banben_PS\" id=\"PS_shouxu_id_PS\">"
			+ "    <CATALOG id=\"CATALOG\">"
			+ "      <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
			+ "        <rdf:description id=\"REPORT\">"
			+ "          <SEND_DATA />"
			+ "          <IT_SEC>"
			+ "            <rdf:description about=\"#IT\" />"
			+ "          </IT_SEC>"
			+ "          <FORM_SEC>"
			+ "            <rdf:Seq>"
//			+ "              <rdf:li>"
//			+ "                <rdf:description about=\"#SHA010-1\" />"
//			+ "              </rdf:li>"
			+ "            </rdf:Seq>"
			+ "          </FORM_SEC>"
			+ ""
			+ ""
			+ "          <TENPU_SEC>"
			+ "            <rdf:Seq>"
			+ "              <rdf:li>"
			+ "                <rdf:description about=\"#TENPU\" />"
			+ "              </rdf:li>"
			+ "            </rdf:Seq>"
			+ "          </TENPU_SEC>"
			+ ""
			+ "          <XBRL_SEC />"
			+ "          <XBRL2_1_SEC />"
			+ "          <SOFUSHO_SEC />"
			+ "          <ATTACH_SEC />"
			+ "          <CSV_SEC />"
			+ "        </rdf:description>"
			+ "      </rdf:RDF>"
			+ "    </CATALOG>"
			+ "    <CONTENTS id=\"CONTENTS\">"
			+ "      <IT VR=\"1.5\" id=\"IT\">"
			+ ""
			+ ""
//			+ "        <ZEIMUSHO ID=\"ZEIMUSHO\">"
//			+ "          <gen:zeimusho_CD>01115</gen:zeimusho_CD>"
//			+ "          <gen:zeimusho_NM>小石川</gen:zeimusho_NM>"
//			+ "        </ZEIMUSHO>"
//			+ "        <NOZEISHA_ID ID=\"NOZEISHA_ID\">2043062810920066</NOZEISHA_ID>"
//			+ "        <NOZEISHA_BANGO ID=\"NOZEISHA_BANGO\">"
//			+ "          <gen:hojinbango>9700150121748</gen:hojinbango>"
//			+ "        </NOZEISHA_BANGO>"
//			+ "        <NOZEISHA_NM_KN ID=\"NOZEISHA_NM_KN\">チェジエン　ジンジュエ　ディエンツ　シャンウ　カンパニーリミテッド</NOZEISHA_NM_KN>"
//			+ "        <NOZEISHA_NM ID=\"NOZEISHA_NM\">Ｚｈｅ　Ｊｉａｎｇ　Ｊｉｎｇ　Ｊｕｅ　Ｄｉａｎ　Ｚｉ　Ｓｈａｎｇ　Ｗｕ　Ｃｏ．，Ｌｔｄ</NOZEISHA_NM>"
//			+ "        <NOZEISHA_ZIP ID=\"NOZEISHA_ZIP\">"
//			+ "          <gen:zip1>112</gen:zip1>"
//			+ "          <gen:zip2>0011</gen:zip2>"
//			+ "        </NOZEISHA_ZIP>"
//			+ "        <NOZEISHA_ADR_KN ID=\"NOZEISHA_ADR_KN\">トウキョウトブンキョウクセンゴク</NOZEISHA_ADR_KN>"
//			+ "        <NOZEISHA_ADR ID=\"NOZEISHA_ADR\">東京都文京区千石４丁目１４番９号１階</NOZEISHA_ADR>"
//			+ "        <NOZEISHA_TEL ID=\"NOZEISHA_TEL\">"
//			+ "          <gen:tel1>03</gen:tel1>"
//			+ "          <gen:tel2>5981</gen:tel2>"
//			+ "          <gen:tel3>8383</gen:tel3>"
//			+ "        </NOZEISHA_TEL>"
//			+ "        <DAIHYO_NM_KN ID=\"DAIHYO_NM_KN\">ツォン フアン ボー</DAIHYO_NM_KN>"
//			+ "        <DAIHYO_NM ID=\"DAIHYO_NM\">ｃｅｎｇ　ｈｕａｎｇｂｏ</DAIHYO_NM>"
//			+ "        <DAIHYO_ZIP ID=\"DAIHYO_ZIP\">"
//			+ "          <gen:zip1>000</gen:zip1>"
//			+ "          <gen:zip2>0000</gen:zip2>"
//			+ "        </DAIHYO_ZIP>"
//			+ "        <DAIHYO_ADR ID=\"DAIHYO_ADR\">国外</DAIHYO_ADR>"
//			+ "        <DAIRI_ID ID=\"DAIRI_ID\">2674011631920063</DAIRI_ID>"
//			+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">アイタックス</DAIRI_NM_KN>"
//			+ "        <DAIRI_NM ID=\"DAIRI_NM\">ｉＴＡＸ税理士法人</DAIRI_NM>"
//			+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">"
//			+ "          <gen:zip1>101</gen:zip1>"
//			+ "          <gen:zip2>0064</gen:zip2>"
//			+ "        </DAIRI_ZIP>"
//			+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</DAIRI_ADR>"
//			+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">"
//			+ "          <gen:tel1>03</gen:tel1>"
//			+ "          <gen:tel2>6272</gen:tel2>"
//			+ "          <gen:tel3>8525</gen:tel3>"
//			+ "        </DAIRI_TEL>"
//			+ "        <TETSUZUKI ID=\"TETSUZUKI\">"
//			+ "          <procedure_CD>RSH0020</procedure_CD>"
//			+ "          <procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
//			+ "        </TETSUZUKI>"
//			+ "        <KAZEI_KIKAN_FROM ID=\"KAZEI_KIKAN_FROM\">"
//			+ "          <gen:era>5</gen:era>"
//			+ "          <gen:yy>5</gen:yy>"
//			+ "          <gen:mm>1</gen:mm>"
//			+ "          <gen:dd>1</gen:dd>"
//			+ "        </KAZEI_KIKAN_FROM>"
//			+ "        <KAZEI_KIKAN_TO ID=\"KAZEI_KIKAN_TO\">"
//			+ "          <gen:era>5</gen:era>"
//			+ "          <gen:yy>5</gen:yy>"
//			+ "          <gen:mm>12</gen:mm>"
//			+ "          <gen:dd>31</gen:dd>"
//			+ "        </KAZEI_KIKAN_TO>"
//			+ "        <SHINKOKU_KBN ID=\"SHINKOKU_KBN\">"
//			+ "          <kubun_CD>1</kubun_CD>"
//			+ "        </SHINKOKU_KBN>"
			+ ""

			+ "      </IT>"

//			+ "      <SHA010 VR=\"10.0\" id=\"SHA010-1\" page=\"1\" sakuseiDay=\"2024-03-25\" sakuseiNM=\"Ｓｈｅｎｚｈｅｎ　ＬｅｉＭｉｎｇＹｕＧｕａｎｇＤｉａｎ　Ｃｏ．，Ｌｔｄ\" softNM=\"ntaclient\">"
//			+ ""
//			+ "      </SHA010-1>"
			+ ""
			+ ""
			+ "    </CONTENTS>"
			+ "  </PS_shouxu_id_PS>"
			+ "</DATA>"
			+ ""
			+ ""
			+ "";

//	static String Template_CATALOG = ""
//			+ ""
////			+ "    <CATALOG id=\"CATALOG\">"
//			+ "      <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
//			+ "        <rdf:description id=\"REPORT\">"
//			+ "          <SEND_DATA />"
//			+ "          <IT_SEC>"
//			+ "            <rdf:description about=\"#IT\" />"
//			+ "          </IT_SEC>"
//			+ "          <FORM_SEC>"
//			+ "            <rdf:Seq>"
////			+ "              <rdf:li>"
////			+ "                <rdf:description about=\"#SHA010-1\" />"
////			+ "              </rdf:li>"
//			+ "            </rdf:Seq>"
//			+ "          </FORM_SEC>"
//			+ "          <TENPU_SEC />"
//			+ "          <XBRL_SEC />"
//			+ "          <XBRL2_1_SEC />"
//			+ "          <SOFUSHO_SEC />"
//			+ "          <ATTACH_SEC />"
//			+ "          <CSV_SEC />"
//			+ "        </rdf:description>"
//			+ "      </rdf:RDF>"
////			+ "    </CATALOG>"
//			+ ""
//			+ ""
//			+ "";


	String IT1 = ""
			+ ""
			+ "      <IT VR=\"1.5\" id=\"IT\">"
			+ "        <ZEIMUSHO ID=\"ZEIMUSHO\">"
			+ "          <gen:zeimusho_CD>01115</gen:zeimusho_CD>"
			+ "          <gen:zeimusho_NM>小石川</gen:zeimusho_NM>"
			+ "        </ZEIMUSHO>"
			+ "        <NOZEISHA_ID ID=\"NOZEISHA_ID\">2849012601910002</NOZEISHA_ID>"
			+ "        <NOZEISHA_BANGO ID=\"NOZEISHA_BANGO\">"
			+ "          <gen:hojinbango>2700150120426</gen:hojinbango>"
			+ "        </NOZEISHA_BANGO>"
			+ "        <NOZEISHA_NM_KN ID=\"NOZEISHA_NM_KN\">シンセンシレイメイウコウデンユウゲンコウシ</NOZEISHA_NM_KN>"
			+ "        <NOZEISHA_NM ID=\"NOZEISHA_NM\">Ｓｈｅｎｚｈｅｎ　ＬｅｉＭｉｎｇＹｕＧｕａｎｇＤｉａｎ　Ｃｏ．，Ｌｔｄ</NOZEISHA_NM>"
			+ "        <NOZEISHA_ZIP ID=\"NOZEISHA_ZIP\">"
			+ "          <gen:zip1>112</gen:zip1>"
			+ "          <gen:zip2>0011</gen:zip2>"
			+ "        </NOZEISHA_ZIP>"
			+ "        <NOZEISHA_ADR_KN ID=\"NOZEISHA_ADR_KN\">トウキョウトブンキョウクセンゴク</NOZEISHA_ADR_KN>"
			+ "        <NOZEISHA_ADR ID=\"NOZEISHA_ADR\">東京都文京区千石４丁目１４番９号１階</NOZEISHA_ADR>"
			+ "        <NOZEISHA_TEL ID=\"NOZEISHA_TEL\">"
			+ "          <gen:tel1>03</gen:tel1>"
			+ "          <gen:tel2>5981</gen:tel2>"
			+ "          <gen:tel3>8383</gen:tel3>"
			+ "        </NOZEISHA_TEL>"
			+ "        <JIGYO_NAIYO ID=\"JIGYO_NAIYO\">小売業</JIGYO_NAIYO>"
			+ "        <DAIHYO_NM_KN ID=\"DAIHYO_NM_KN\">ドン マン ジョン</DAIHYO_NM_KN>"
			+ "        <DAIHYO_NM ID=\"DAIHYO_NM\">Ｄｅｎｇ　Ｍａｎ　Ｚｈｅｎｇ</DAIHYO_NM>"
			+ "        <DAIHYO_ZIP ID=\"DAIHYO_ZIP\">"
			+ "          <gen:zip1>000</gen:zip1>"
			+ "          <gen:zip2>0000</gen:zip2>"
			+ "        </DAIHYO_ZIP>"
			+ "        <DAIHYO_ADR ID=\"DAIHYO_ADR\">国外</DAIHYO_ADR>"
			+ "        <TETSUZUKI ID=\"TETSUZUKI\">"
			+ "          <procedure_CD>RSH0020</procedure_CD>"
			+ "          <procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
			+ "        </TETSUZUKI>"
			+ "        <KAZEI_KIKAN_FROM ID=\"KAZEI_KIKAN_FROM\">"
			+ "          <gen:era>5</gen:era>"
			+ "          <gen:yy>5</gen:yy>"
			+ "          <gen:mm>1</gen:mm>"
			+ "          <gen:dd>1</gen:dd>"
			+ "        </KAZEI_KIKAN_FROM>"
			+ "        <KAZEI_KIKAN_TO ID=\"KAZEI_KIKAN_TO\">"
			+ "          <gen:era>5</gen:era>"
			+ "          <gen:yy>5</gen:yy>"
			+ "          <gen:mm>12</gen:mm>"
			+ "          <gen:dd>31</gen:dd>"
			+ "        </KAZEI_KIKAN_TO>"
			+ "        <SHINKOKU_KBN ID=\"SHINKOKU_KBN\">"
			+ "          <kubun_CD>1</kubun_CD>"
			+ "        </SHINKOKU_KBN>"
			+ "      </IT>"
			+ "";



	 /**
     * 将指定内容写入文件。
     *
     * @param content 文件内容
     * @param filePath 文件路径
     * @throws IOException 如果写入文件时发生错误
     */
    public static void writeToFile(String content, String filePath) throws IOException {
        File file = new File(filePath);

        // 如果文件不存在，创建新文件
        if (!file.exists()) {
            file.getParentFile().mkdirs(); // 创建父目录
            file.createNewFile();           // 创建新文件
        }

        // 使用 BufferedWriter 将内容写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content); // 写入内容
        }
    }


	public static File findFileByNameRecursive(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        // 检查路径是否是目录且存在
        if (directory.exists() && directory.isDirectory()) {
            // 列出目录下的所有文件和子文件夹
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
                        return file; // 返回找到的文件对象
                    } else if (file.isDirectory()) {
                        // 递归查找子文件夹
                        File found = findFileByNameRecursive(file.getAbsolutePath(), fileName);
                        if (found != null) {
                            return found; // 返回找到的文件对象
                        }
                    }
                }
            }
        }

        return null; // 如果未找到匹配文件则返回 null
    }







/*
CREATE TABLE `t_xiaofeishui_shengao` (
  `UPDATE_DATE` timestamp(6) NOT NULL,
  `PDSK` varchar(45) NOT NULL,
  `yyyymmdd_count` bigint NOT NULL,
  `yyyy` varchar(45) NOT NULL,
  `shengao_qijian_from` varchar(8) NOT NULL COMMENT '申告期间',
  `shengao_qijian_to` varchar(8) NOT NULL,
  `jizhun_qijian` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在基准期间的日本课税销售额',
  `teding_qijian` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在特定期间的日本课税销售额',
  `shangyi_niandu` decimal(15,0) DEFAULT NULL COMMENT '本申告主体在上一会计年度的日本课税销售额',
  `qunian_xiaofeishui_shengao` varchar(45) DEFAULT NULL COMMENT '去年是否申告过消费税',
  `keshui_type` varchar(45) DEFAULT NULL COMMENT '本申告主体在该会计年度计算消费税时采用',
  `qunian_xiaofeishui_guoshui` decimal(15,0) DEFAULT NULL COMMENT '去年消费税申告的消费税国税额',
  `hanshui_zongxiaoshoue` decimal(15,0) DEFAULT NULL COMMENT '含税总销售额',
  `shige_qingqiushu_zongzhichue` decimal(15,0) DEFAULT NULL COMMENT '适格请求书总支出额',
  `fei_shige_qingqiushu_zongzhichue` decimal(15,0) DEFAULT NULL COMMENT '非适格请求书总支出额',
  `jinkou_xiaofeishui_guoshui_zonge` decimal(15,0) DEFAULT NULL COMMENT '进口消费税国税部分总额',
  `activation_code` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `fading_zhongjian_shengao_cishu` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告次数',
  `fading_zhongjian_shengao_danci_duiying_yueshu` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次对应月数',
  `fading_zhongjian_shengao_danci_guoshui_e` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次国税额',
  `fading_zhongjian_shengao_danci_difangshui_e` decimal(15,0) DEFAULT NULL COMMENT '法定中间申告单次地方税额',
  `buhan_shui_xiaoshou_e` decimal(15,0) DEFAULT NULL COMMENT '不含税销售额',
  `keshuibiao_zhun_e` decimal(15,0) DEFAULT NULL COMMENT '课税标准额',
  `xiaofeishui_e_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '消费税额国税部分',
  `kongchu_shui_e_guoshui_bufen_you_hegui_fapiao` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（有合规发票部分）',
  `kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（无合规发票部分）',
  `kongchu_shui_e_guoshui_bufen_jinkou` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（进口部分）',
  `kongchu_shui_e_guoshui_bufen_heji` decimal(15,0) DEFAULT NULL COMMENT '控除税额国税部分（合计）',
  `quannian_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税国税部分',
  `zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税国税部分',
  `queren_shengao_yingjiao_xiaofeishui_guoshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税国税部分',
  `quannian_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税地方税部分',
  `zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税地方税部分',
  `queren_shengao_yingjiao_xiaofeishui_difangshui_bufen` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税地方税部分',
  `quannian_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '全年应缴消费税合计额',
  `zhongjian_shengao_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '中间申告应缴消费税合计额',
  `queren_shengao_yingjiao_xiaofeishui_heji` decimal(15,0) DEFAULT NULL COMMENT '确定申告应缴消费税合计额',
  PRIMARY KEY (`yyyymmdd_count`,`yyyy`,`shengao_qijian_from`,`shengao_qijian_to`),
  UNIQUE KEY `PDSK_UNIQUE` (`PDSK`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='消费税申告'
 */
	// 定义主数据存储结构
	static TreeMap<String, TreeMap<String, String[]>> PropertyCsvMap_shouxu = new TreeMap<>();

	// 处理每个CSV文件的示例方法
	public static void processCsvFile_shouxu(File csvFile) {
//	    logger.info("Processing csvFile：" + csvFile);

	    if (!csvFile.getName().startsWith("手続一覧（申告）")) {
	        return;
	    }

	    String line = null;
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8))) {

	        while ((line = br.readLine()) != null) {
	            line = line.trim();
	            String[] values = line.split(",");

	            // 获取csv第一列作为第一层的key
	            String firstColumn = values[0];
	            // 获取版本号作为第二层的key
	            String versionKey = values[7]; // 假设第8列是版本

	            // 获取完整行内容，存储为String[]
	            String[] rowData = values;

	            // 构建数据结构，添加每一层的key和内容
	            PropertyCsvMap_shouxu
	                .computeIfAbsent(firstColumn, k -> new TreeMap<>())
	                .put(versionKey, rowData);
	        }

	    } catch (Exception e) {
	    	logger.error("Processing csv：" + line);
	        e.printStackTrace();
	    }

	}

	// 定义主数据存储结构
	static TreeMap<String, TreeMap<String, TreeMap<String, TreeMap<String, String[]>>>> PropertyCsvMap_zhangpiao = new TreeMap<>();

	// 处理每个CSV文件的示例方法
	public static void processCsvFile_zhangpiao(File File) {
//	    logger.info("Processing csvFile：" + File);

	    if (!File.getName().startsWith("手続内帳票対応表(申告）")) {
	        return;
	    }

	    String line = null;
	    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File), StandardCharsets.UTF_8))) {
	        int i = 0;
	        while ((line = br.readLine()) != null) {
	            line = line.trim();
	            String[] values = line.split(",");

	            if (values.length < 10) {
	            	if (line.startsWith("※")) {
	            	}
//		            logger.info("values[" + ++i + "]：" + line);
	            	continue;

	            }

	            // 获取csv每列的值作为不同层级的key
	            String firstKey = values[0];
	            String secondKey = values[8];
	            String thirdKey = values[2];
	            String fourthKey = values[9];

	            // 获取完整行内容，存储为String[]
	            String[] rowData = values;

	            // 构建数据结构，添加每一层的key和内容
	            PropertyCsvMap_zhangpiao
	                .computeIfAbsent(firstKey, k -> new TreeMap<>())
	                .computeIfAbsent(secondKey, k -> new TreeMap<>())
	                .computeIfAbsent(thirdKey, k -> new TreeMap<>())
	                .put(fourthKey, rowData);
	        }

	    } catch (Exception e) {
		    logger.info("Processing csv：" + line);
	        e.printStackTrace();
	    }




	}


	static LinkedHashMap<String, String> tblMap = new LinkedHashMap<>();
    // 假设文件夹路径
    static String directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）";


    static{

 		logger.info("start master Load.");

        // 递归遍历目录并读取csv文件
        try {

    		logger.info("Processed file and updated processCsvFile_shouxu.");
            Files.walk(Paths.get(directoryPath+"/07手続一覧等"))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_shouxu(path.toFile()));

    		logger.info("Processed file and updated processCsvFile_zhangpiao.");
            Files.walk(Paths.get(directoryPath+"/07手続一覧等"))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_zhangpiao(path.toFile()));

        } catch (IOException e) {
            e.printStackTrace();
        }

    	/*
    	 * 定义t_etax_account_info
    	 */
    	//主名字
    	tblMap.put("t_etax_account_info.申告主体名称（英文）", "CompanyName_English");
    	tblMap.put("t_etax_account_info.申告主体地址（英文）", "address_English");

    	//复数名字定义
    	tblMap.put("t_etax_account_info.公司カタガナ", "CompanyName_pianjiaming");
    	tblMap.put("t_etax_account_info.公司英文名称", "CompanyName_English");
    	tblMap.put("t_etax_account_info.代表人读音カタガナ", "DaibiaoName_pianjiaming");
    	tblMap.put("t_etax_account_info.代表人名字英文", "DaibiaoName_English");
    	tblMap.put("t_etax_account_info.申告主体类别", "user_type");




    	/*
    	 * 定义t_etax_account_res
    	 */
    	//主名字
    	tblMap.put("t_etax_account_res.etax番号", "bangou");
    	tblMap.put("t_etax_account_res.法人番号", "HoujinBangou");
    	tblMap.put("t_etax_account_res.インボイス番号", "InvoiceBangou");




    	/*
    	 * 定义t_xiaofeishui_shengao
    	 */

    	//主名字


        tblMap.put("UPDATE_DATE", "UPDATE_DATE");
        tblMap.put("PDSK编号", "PDSK");
        tblMap.put("yyyymmdd_count", "yyyymmdd_count");
        tblMap.put("yyyy", "yyyy");
        tblMap.put("会计年度自", "shengao_qijian_from");
        tblMap.put("会计年度至", "shengao_qijian_to");
        tblMap.put("本申告主体在基准期间的日本课税销售额", "jizhun_qijian");
        tblMap.put("本申告主体在特定期间的日本课税销售额", "teding_qijian");
        tblMap.put("本申告主体在上一会计年度的日本课税销售额", "shangyi_niandu");
        tblMap.put("去年是否申告过消费税", "qunian_xiaofeishui_shengao");
        tblMap.put("本申告主体在该会计年度计算消费税时采用", "keshui_type");
        tblMap.put("去年消费税申告的消费税国税额", "qunian_xiaofeishui_guoshui");
        tblMap.put("含税总销售额", "hanshui_zongxiaoshoue");
        tblMap.put("适格请求书总支出额", "shige_qingqiushu_zongzhichue");
        tblMap.put("非适格请求书总支出额", "fei_shige_qingqiushu_zongzhichue");
        tblMap.put("进口消费税国税部分总额", "jinkou_xiaofeishui_guoshui_zonge");
        tblMap.put("activation_code", "activation_code");
        tblMap.put("email", "email");
        tblMap.put("法定中间申告次数", "fading_zhongjian_shengao_cishu");
        tblMap.put("法定中间申告单次对应月数", "fading_zhongjian_shengao_danci_duiying_yueshu");
        tblMap.put("法定中间申告单次国税额", "fading_zhongjian_shengao_danci_guoshui_e");
        tblMap.put("法定中间申告单次地方税额", "fading_zhongjian_shengao_danci_difangshui_e");
        tblMap.put("不含税销售额", "buhan_shui_xiaoshou_e");
        tblMap.put("课税标准额", "keshuibiao_zhun_e");
        tblMap.put("消费税额国税部分", "xiaofeishui_e_guoshui_bufen");
        tblMap.put("控除税额国税部分（有合规发票部分）", "kongchu_shui_e_guoshui_bufen_you_hegui_fapiao");
        tblMap.put("控除税额国税部分（无合规发票部分）", "kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao");
        tblMap.put("控除税额国税部分（进口部分）", "kongchu_shui_e_guoshui_bufen_jinkou");
        tblMap.put("控除税额国税部分（合计）", "kongchu_shui_e_guoshui_bufen_heji");

        //简易课税（零售业）  OR  原则课税（2割特例）
        tblMap.put("默认抵扣额国税部分", "kongchu_shui_e_guoshui_bufen_heji");

        tblMap.put("全年应缴消费税国税部分", "quannian_yingjiao_xiaofeishui_guoshui_bufen");
        tblMap.put("中间申告应缴消费税国税部分", "zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen");
        tblMap.put("确定申告应缴消费税国税部分", "queren_shengao_yingjiao_xiaofeishui_guoshui_bufen");
        tblMap.put("全年应缴消费税地方税部分", "quannian_yingjiao_xiaofeishui_difangshui_bufen");
        tblMap.put("中间申告应缴消费税地方税部分", "zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen");
        tblMap.put("确定申告应缴消费税地方税部分", "queren_shengao_yingjiao_xiaofeishui_difangshui_bufen");
        tblMap.put("全年应缴消费税合计额", "quannian_yingjiao_xiaofeishui_heji");
        tblMap.put("中间申告应缴消费税合计额", "zhongjian_shengao_yingjiao_xiaofeishui_heji");
        tblMap.put("确定申告应缴消费税合计额", "queren_shengao_yingjiao_xiaofeishui_heji");


 		logger.info("end");

    }



	private static LinkedHashMap<String, String> findSheetsStartingWithPS_IT(t_etax_account_infoExBean t_etax_account_infoExBean
			, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean, String filePath, String path_Excel, String path_ETAX_output
			, LinkedHashMap<String, String> xtxMap0) throws Exception {
		StringBuffer loggerStringBuffer = new StringBuffer();
		LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

		FileInputStream fis = null;
        Workbook workbook = null;
        XMLCalculator XMLCalculator = new XMLCalculator();
        try {
            // 打开 Excel 文件
            fis = new FileInputStream(path_Excel);
            workbook = WorkbookFactory.create(fis);

            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "PS_" 开头
                if (sheetName.startsWith("PS_IT部")) {
                	logger.debug("找到符合条件的工作表: " + sheetName);


                    // 读取整个文件的内容为字符串
                    String content = "";

                    //先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 2; j++) {
                    	// 从第四行开始输出 M 列和 S 列的值
                    	for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {

                            	//項番
                                if (StringUtils.isEmpty(FuncUtilsExcel.getCellValueAsString(row.getCell(0))) == true) {
                                	break;
                                }

//                            	Cell aCell = row.getCell(0);  // A 列（索引 0）	//項番
                            	Cell bCell = row.getCell(1);  // B 列（索引 1）	//入力型
                            	Cell cCell = row.getCell(2);  // C 列（索引 2）	//帳票項番
                            	Cell dCell = row.getCell(3);  // D 列（索引 3）	//項目（ｸﾞﾙｰﾌﾟ）名
                            	Cell eCell = row.getCell(4);  // E 列（索引 4）	//項目名
//                            	Cell fCell = row.getCell(5);  // F 列（索引 5）	//繰返し回数
                            	Cell gCell = row.getCell(6);  // G 列（索引 6）	//書式
//                            	Cell hCell = row.getCell(7);  // H 列（索引 7）	//入力ﾁｪｯｸ
//                            	Cell iCell = row.getCell(8);  // I 列（索引 8）	//計算
                            	Cell jCell = row.getCell(9);  // J 列（索引 9）	//値の範囲
//                            	Cell kCell = row.getCell(10); // K 列（索引 10）	//計算No
                            	Cell lCell = row.getCell(11); // L 列（索引 11）	//計算／備考
                            	Cell mCell = row.getCell(12); // M 列（索引 12）	//ＸＭＬタグ
//                            	Cell nCell = row.getCell(13); // N 列（索引 13）	//順位
//                            	Cell oCell = row.getCell(14); // O 列（索引 14）	//ID属性
                            	Cell pCell = row.getCell(15); // P 列（索引 15）	//IDREF属性

//                            	Cell qCell = row.getCell(16); // Q 列（索引 16）
                            	Cell rCell = row.getCell(17); // R 列（索引 17）
                            	Cell sCell = row.getCell(18); // S 列（索引 18）
                            	Cell tCell = row.getCell(19); // S 列（索引 19）


//                            	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//項番
                            	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//入力型
                            	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//帳票項番
                            	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//項目（ｸﾞﾙｰﾌﾟ）名
                            	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//項目名
//                            	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//繰返し回数
                            	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//書式
//                            	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//入力ﾁｪｯｸ
//                            	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//計算
                            	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//値の範囲
//                            	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//計算No
                            	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//計算／備考
                            	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//ＸＭＬタグ
//                            	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//順位
//                            	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//ID属性
                            	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//IDREF属性


//                            	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
                            	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//区分
                            	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//对象元素/値
                            	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//运算关系

                                if ("ATC00120".equals(cValue)) {
                                	cValue=cValue;
                                }

                                if (j == 1) {

                                    if ("NOZEISHA_BANGO".equals(cValue)) {
                                    	cValue=cValue;
                                    }

                                    //PS区分
                                    if ("删除".equals(rValue)) {
                                        continue;
                                    }



                                    //PS区分
                                    if (rValue.equals("固定")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);


                                    } else if (rValue.contains("DB")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	if (sValue.startsWith("【") && sValue.endsWith("】")) {
                            				String sValueOld = sValue;
                            				sValue = sValue.replace("【", "").replace("】", "");
                                			sValue = tblMap.get(sValue);
                                    		if (StringUtils.isEmpty(sValue) == true) {
                                                logger.error("没有找到DB定义项目:" + sValue);
                                            	continue;

                                    		} else {
                                    			if (sValueOld.contains("t_etax_account_info") || sValueOld.contains("t_etax_account_res")) {
                                    				sValue = FuncUtils.getBeanValue(t_etax_account_infoExBean, sValue);

                                    			} else {
                                    				sValue = FuncUtils.getBeanValue(t_xiaofeishui_shengaoBean, sValue);

                                    			}

                                			}


                                		} else {
                                			logger.error("没有找到DB定义项目【】格式写法:" + sValue);
                                        	continue;

                                		}



                                    } else  if (rValue.contains("定义")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	String[] shouxu_liset = xtxMap0.get(shouxu_sheetName).split(",");
                                    	String shouxu_id = shouxu_liset[0];
                                    	String shouxu_banben = shouxu_liset[1];

                                    	TreeMap<String, String[]> shouxu_all = PropertyCsvMap_shouxu.getOrDefault(shouxu_id, new TreeMap<>());
//                                    	PropertyCsvMap_shouxu
//                                    	└── firstColumn (String)
//                                    	    └── versionKey (String) : rowData (String[])
                                    	String[] csv_shouxu = shouxu_all.get(shouxu_banben);


//                                    	TreeMap<String, String[]> zhangpiao = PropertyCsvMap_zhangpiao.getOrDefault(sValue, new TreeMap<>());
//                                    	String[] csv_zhangpiao = zhangpiao.get(csv_shouxu[0]);

										if (csv_shouxu != null) {
											sValue = ""
													+ ""
													+ "<procedure_CD>" + csv_shouxu[0] + "</procedure_CD>"
													+ "<procedure_NM>" + csv_shouxu[1] + "</procedure_NM>"
													+ ""
													+ "";

	                                    	//TODO
//											sValue = ""
//													+ "<procedure_CD>RSH0020</procedure_CD>"
//													+ "<procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
//													+ ""
//													+ "";
										} else {
                                    	    logger.error("TreeMap is empty.");
                                    	}

                                    }



                                    // 目标字符串作成
                                    sValue = "<" + cValue + " ID=\"" + cValue + "\">" + sValue + "</" + cValue + ">";
                                    content = content +"\n"+ sValue;


                                } else if (j == 2) {
                                    if ("NOZEISHA_BANGO".equals(cValue)) {
                                    	cValue=cValue;
                                    	tValue="/^\\d{13}$/.test('【】') ? '<gen:hojinbango>【】</gen:hojinbango>' : ''";
                                    }

                                    //PS区分
                                    if (rValue.contains("【計算】")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	//PS运算关系
                                    	String formula = tValue;
                                    	if (StringUtils.isEmpty(formula) == true) {
                                    		//ETAX式样計算
                                    		//計算／備考
                                    		formula = lValue;
                                    	}

                                    	formula = formula.replace("【計算】", "");
        								formula = FuncUtils.toHalfWidth(formula);


                                    	if (StringUtils.isEmpty(formula) == true) {
                                			logger.error("没有計算式:" + formula);
//                                        	continue;
                                    	} else {
                                    		//自动识别，自定义计算公式
                                            for (Entry<String, String> entry : XMLCalculator.roundingRules.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();

                                                if (formula.contains(key)) {
                                                    formula = formula.replace(key, "");
                                                    formula = formula + "\n" + value.replace("【】", cValue);
                                                }
                                            }

            								formula = formula.replace("・", "");
            								formula = formula.replace("()", "");
//            								if (formula.contains("(") || formula.contains(")")) {
//            									logger.error("有未定义的运算逻辑项目:" + formula);
//            									continue;
//            								}

            						        String[] formulaLines = formula.split("\n"); // 按行分割公式字符串

            						        for (String line : formulaLines) {
            						            line = line.trim(); // 去除行首尾的空白
            						            if (!line.isEmpty()) {
													line = line.replace("【】", "【" + cValue + "】");
            						            	String result = (String) XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer);

            										sValue = "" + result;

            										String regex = "<(" + cValue + ")([^>]*)>(.*?)</\\1>";
            										Pattern pattern = Pattern.compile(regex);
            										Matcher matcher = pattern.matcher(content);
            										content = matcher.replaceAll("<$1$2>" + sValue + "</$1>");


            						            }
            						        }

                                    	}
                                    }

                                }

                            }
                    	}
                    }


                    /*
                     *
                     */
                    content = "<PS_ROOT>" + content + "</PS_ROOT>";

                    // 将字符串转换为 XML Document 对象
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(content)));

                    // 将 XML Document 转换为格式化的字符串
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");



					/*
					 *
					 */
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String outputString = writer.toString();


                	String key = "PS_ROOT";
                	outputString = outputString.replace("<"+key+">", "");
                	outputString = outputString.replace("</"+key+">", "");

                    // 移除空行
                    outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");

                    xtxMap.put(sheetName, outputString);

                    // 输出格式化后的 XML 字符串到后台
//                    logger.info("XML 格式化输出：");
//                    logger.info("\n" + outputString);

                    writeToFile(outputString, path_ETAX_output+ "_" + sheetName + ".xtx");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            // 关闭资源
            if (workbook != null) {
            	workbook.close();
            }
            if (fis != null) {
                fis.close();
            }

        }
		return xtxMap;
	}


	private static LinkedHashMap<String, String> findSheetsStartingWithPS(t_etax_account_infoExBean t_etax_account_infoExBean
			, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean, String filePath, String path_Excel, String path_ETAX_output) throws Exception {
    	logger.debug("\n" + "path_Excel: " + path_Excel);

        StringBuffer loggerStringBuffer = new StringBuffer();

		loggerStringBuffer.append(t_xiaofeishui_shengaoDao.CREATE_TABL);
		loggerStringBuffer.append("\n");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = objectMapper.writeValueAsString(t_xiaofeishui_shengaoBean);
		loggerStringBuffer.append("\n");
        loggerStringBuffer.append("t_xiaofeishui_shengaoBean");
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append(json);
        loggerStringBuffer.append("\n");

        json = objectMapper.writeValueAsString(tblMap);
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append("tblMap");
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append(json);
        loggerStringBuffer.append("\n");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = formatter.format(now);
        loggerStringBuffer.append(formattedDate);
        loggerStringBuffer.append("\n");

        logger.debug(loggerStringBuffer.toString());


		LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

		FileInputStream fis = null;
        Workbook workbook = null;
        XMLCalculator XMLCalculator = new XMLCalculator();
        try {
            // 打开 Excel 文件
            fis = new FileInputStream(path_Excel);
            workbook = WorkbookFactory.create(fis);

            String shouxu_id = "";
            String shouxu_banben = "";
            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "手続内帳票対応表（申告）" 开头
                if (sheetName.startsWith(shouxu_sheetName)) {

                    // 先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 1; j++) {
                        // 从第四行开始输出 A 列和 I 列的值
                        for (int rowIndex = 4; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                            Row row = sheet.getRow(rowIndex);

                            // 如果该行为空，跳过
                            if (row == null) {
                                continue;
                            }

                            // 跳过隐藏的行
                            if (row.getZeroHeight()) {
                                continue;
                            }



                        	Cell aCell = row.getCell(0);  // A 列（索引 0）	//手続きＩＤ
//                        	Cell bCell = row.getCell(1);  // B 列（索引 1）	//
//                        	Cell cCell = row.getCell(2);  // C 列（索引 2）	//
//                        	Cell dCell = row.getCell(3);  // D 列（索引 3）	//
//                        	Cell eCell = row.getCell(4);  // E 列（索引 4）	//
//                        	Cell fCell = row.getCell(5);  // F 列（索引 5）	//
//                        	Cell gCell = row.getCell(6);  // G 列（索引 6）	//
//                        	Cell hCell = row.getCell(7);  // H 列（索引 7）	//
                        	Cell iCell = row.getCell(8);  // I 列（索引 8）	//バージョン
//                        	Cell jCell = row.getCell(9);  // J 列（索引 9）	//
//                        	Cell kCell = row.getCell(10); // K 列（索引 10）	//
//                        	Cell lCell = row.getCell(11); // L 列（索引 11）	//
//                        	Cell mCell = row.getCell(12); // M 列（索引 12）	//
//                        	Cell nCell = row.getCell(13); // N 列（索引 13）	//
//                        	Cell oCell = row.getCell(14); // O 列（索引 14）	//
//                        	Cell pCell = row.getCell(15); // P 列（索引 15）	//

//                        	Cell qCell = row.getCell(16); // Q 列（索引 16）
//                        	Cell rCell = row.getCell(17); // R 列（索引 17）
//                        	Cell sCell = row.getCell(18); // S 列（索引 18）
//                        	Cell tCell = row.getCell(19); // S 列（索引 19）


                        	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//手続きＩＤ
//                        	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//
//                        	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//
//                        	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//
//                        	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//
//                        	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//
//                        	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//
//                        	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//
                        	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//バージョン
//                        	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//
//                        	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//
//                        	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//
//                        	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//
//                        	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//
//                        	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//
//                        	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//



//                        	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
//                        	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//
//                        	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//
//                        	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//
                            // 输出或处理 A 列和 I 列的值
                            System.out.println("A列值: " + aValue + ", I列值: " + iValue);

                            shouxu_id = aValue;
                            shouxu_banben = iValue;
                            xtxMap.put(shouxu_sheetName, aValue + "," + iValue);

                            // 找到第一个非隐藏行后，跳出循环
                            break;
                        }
                    }
                    break; // 找到符合条件的工作表后，退出外层循环
                }
            }



            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "PS_" 开头
                if (sheetName.startsWith("PS_")) {
                	logger.debug("找到符合条件的工作表: " + sheetName);

                	if (sheetName.contains("PS_IT部")) {
                		continue;
                	}

                	String xtx_fileName = sheetName.toLowerCase().replace("ps_", "").replace(".0", "x");
                	xtx_fileName = xtx_fileName.substring(0, 6) + "_ver" + xtx_fileName.substring(7) + ".xml";

                    File file = findFileByNameRecursive(filePath, xtx_fileName);
                    // 读取整个文件的内容为字符串
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);

                    //先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 2; j++) {
                    	// 从第四行开始输出 M 列和 S 列的值
                    	for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {

                            	//項番
                                if (StringUtils.isEmpty(FuncUtilsExcel.getCellValueAsString(row.getCell(0))) == true) {
                                	break;
                                }

//                            	Cell aCell = row.getCell(0);  // A 列（索引 0）	//項番
                            	Cell bCell = row.getCell(1);  // B 列（索引 1）	//入力型
//                            	Cell cCell = row.getCell(2);  // C 列（索引 2）	//帳票項番
                            	Cell dCell = row.getCell(3);  // D 列（索引 3）	//項目（ｸﾞﾙｰﾌﾟ）名
                            	Cell eCell = row.getCell(4);  // E 列（索引 4）	//項目名
//                            	Cell fCell = row.getCell(5);  // F 列（索引 5）	//繰返し回数
                            	Cell gCell = row.getCell(6);  // G 列（索引 6）	//書式
//                            	Cell hCell = row.getCell(7);  // H 列（索引 7）	//入力ﾁｪｯｸ
//                            	Cell iCell = row.getCell(8);  // I 列（索引 8）	//計算
                            	Cell jCell = row.getCell(9);  // J 列（索引 9）	//値の範囲
//                            	Cell kCell = row.getCell(10); // K 列（索引 10）	//計算No
                            	Cell lCell = row.getCell(11); // L 列（索引 11）	//計算／備考
                            	Cell mCell = row.getCell(12); // M 列（索引 12）	//ＸＭＬタグ
//                            	Cell nCell = row.getCell(13); // N 列（索引 13）	//順位
//                            	Cell oCell = row.getCell(14); // O 列（索引 14）	//ID属性
                            	Cell pCell = row.getCell(15); // P 列（索引 15）	//IDREF属性

                            	Cell qCell = row.getCell(16); // Q 列（索引 16）
                            	Cell rCell = row.getCell(17); // R 列（索引 17）
                            	Cell sCell = row.getCell(18); // S 列（索引 18）
                            	Cell tCell = row.getCell(19); // S 列（索引 19）


//                            	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//項番
                            	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//入力型
//                            	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//帳票項番
                            	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//項目（ｸﾞﾙｰﾌﾟ）名
                            	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//項目名
//                            	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//繰返し回数
                            	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//書式
//                            	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//入力ﾁｪｯｸ
//                            	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//計算
                            	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//値の範囲
//                            	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//計算No
                            	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//計算／備考
                            	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//ＸＭＬタグ
//                            	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//順位
//                            	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//ID属性
                            	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//IDREF属性


                            	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
                            	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//区分
                            	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//对象元素/値
                            	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//运算关系

                                if ("ABI00050".equals(mValue)) {
                                	mValue=mValue;
                                }

                                if (j == 1) {

                                    //PS区分
                                    if (rValue.equals("删除")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                		String regex = "<" + mValue + "(.*?)>(.*?)</" + mValue + ">";
                                		Pattern pattern = Pattern.compile(regex);
                                		Matcher matcher = pattern.matcher(content);
                                		content = matcher.replaceAll("");

                                        continue;
                                    }

                                    if (qValue.contains("個人の場合のIDREF属性:")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                		String regex = "<" + mValue + "(.*?)>(.*?)</" + mValue + ">";
                                		Pattern pattern = Pattern.compile(regex);
                                		Matcher matcher = pattern.matcher(content);
                                		regex = "<" + mValue + " IDREF=\""+qValue.replace("個人の場合のIDREF属性:", "")+"\"></" + mValue + ">";
                                		content = matcher.replaceAll(regex);

                                        continue;
                                    }

                                }

                            	//IDREF属性
                                if (StringUtils.isEmpty(pValue) == true) {

                                } else {
                                	continue;
                                }


                                if (j == 1) {
                                    if ("ABI00050".equals(mValue)) {
                                    	mValue=mValue;
                                    }
                                    //PS区分
                                    if (rValue.equals("固定")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                        if ("区分".equals(bValue)) {

                                        	if ("元号".equals(eValue)) {
                                        		continue;
                                        	}


                                        	//TODO
//                                        	if ("振替継続希望区分".equals(dValue)) {
//                                        		sValue = "<kubun_CD>"+sValue+"</kubun_CD>";
//                                            }


//                                            if (StringUtils.isEmpty(sValue) == false) {
//                                        		sValue = "<kubun_CD>"+sValue+"</kubun_CD>";
//                                            }

//
//                                        	if (jValue.contains("4:平成") || jValue.contains("5:令和")) {
//                                        		//削除
//                                        		// 使用正则表达式匹配指定标签内的值
//                                        		String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
//                                        		Pattern pattern = Pattern.compile(regex);
//                                        		Matcher matcher = pattern.matcher(content);
//                                        		// 进行替换，将匹配到的值替换为 newValue
//                                        		content = matcher.replaceAll("");
//
//
//                                        		//書換
////                                        		sValue = " <gen:era>5</gen:era>"
////                                        				+ " <gen:yy>5</gen:yy>"
////                                        				+ " <gen:mm>12</gen:mm>"
////                                        				+ " <gen:dd>31</gen:dd>";
////                            					content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");
//
//                                        	} else {
//
//                                        	}

                                        	sValue = "<kubun_CD>"+sValue+"</kubun_CD>";

                                        }

                                    } else if (rValue.contains("DB")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                            			if (sValue.startsWith("【") && sValue.endsWith("】")) {
                            				String sValueOld = sValue;
                            				sValue = sValue.replace("【", "").replace("】", "");
                                			sValue = tblMap.get(sValue);
                                    		if (StringUtils.isEmpty(sValue) == true) {
                                                logger.error("没有找到DB定义项目:" + sValue);
                                            	continue;

                                    		} else {
                                    			if (sValueOld.contains("t_etax_account_info") || sValueOld.contains("t_etax_account_res")) {
                                    				sValue = FuncUtils.getBeanValue(t_etax_account_infoExBean, sValue);
                                    		        // 替换非法字符
                                    				sValue = sValue.replaceAll("<(\\d+[-]\\d+)>", "&lt;$1&gt;");
                                    			} else {
                                    				sValue = FuncUtils.getBeanValue(t_xiaofeishui_shengaoBean, sValue);

                                    			}

                                			}


                                		} else {
                                			logger.error("没有找到DB定义项目【】格式写法:" + sValue);
                                        	continue;

                                		}



                                    }
                                } else if (j == 2) {


                                    if ("ATB00250".equals(mValue)) {
                                    	mValue=mValue;
//                                    	return xtxMap;
                                    }

                                    //PS区分
                                    if (rValue.contains("【計算】")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	//PS运算关系
                                    	String formula = tValue;
                                    	if (StringUtils.isEmpty(formula) == true) {
                                    		//ETAX式样計算
                                    		//計算／備考
                                    		formula = lValue;
                                    	}

                                    	formula = formula.replace("【計算】", "");
                                    	//百分数表示，不是计算公式，不需要计算，删除
                                    	formula = formula.replace("％", "");
        								formula = FuncUtils.toHalfWidth(formula);


                                    	if (StringUtils.isEmpty(formula) == true) {
                                			logger.error("没有計算式:" + formula);
//                                        	continue;
                                    	} else {
                                    		//自动识别，自定义计算公式
                                            for (Entry<String, String> entry : XMLCalculator.roundingRules.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();

                                                if (formula.contains(key)) {
                                                    formula = formula.replace(key, "");
                                                    formula = formula + "\n" + value.replace("【】", mValue);
                                                }
                                            }

            								formula = formula.replace("・", "");
            								formula = formula.replace("()", "");
//            								if (formula.contains("(") || formula.contains(")")) {
//            									logger.error("有未定义的运算逻辑项目:" + formula);
//            									continue;
//            								}

            						        String[] formulaLines = formula.split("\n"); // 按行分割公式字符串

            						        for (String line : formulaLines) {
            						            line = line.trim(); // 去除行首尾的空白
            						            if (!line.isEmpty()) {
            						            	if (line.indexOf("角") > -1) {
            						            		line = line.replace("【】", "【" + mValue + "】");
            						            		String result = (String) XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer);
            						            		sValue = "" + result;

            						            	} else {
            						            		double result = Double.parseDouble(XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer).toString());
            						            		sValue = "" + result;

            						            	}


            										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
            										Pattern pattern = Pattern.compile(regex);
            										Matcher matcher = pattern.matcher(content);
            										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");

            						            }
            						        }

                                    	}



                                    	if (StringUtils.isEmpty(gValue) == true) {

        						        } else {
        						            logger.debug("["+sheetName+"]数据格式化: " + gValue);


        						        	gValue = gValue.replace(",", "").replace("Z", "#");
        						            DecimalFormat oneDecimalFormat = new DecimalFormat(gValue); // ZZZ.Z 格式
        						            sValue = oneDecimalFormat.format(Double.parseDouble(sValue));


    										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
    										Pattern pattern = Pattern.compile(regex);
    										Matcher matcher = pattern.matcher(content);
    										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");

        						        }

                                      	if ("元号NG".equals(eValue)) {
                                      		sValue = ""
                                      				+ "<gen:era>5</gen:era>"
                                      				+ "<gen:yy>5</gen:yy>"
                                      				+ "<gen:mm>1</gen:mm>"
                                      				+ "<gen:dd>1</gen:dd>"
                                      				+ ""
                                      				+ "";

                    						// 使用正则表达式匹配指定标签内的值
    										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
    										Pattern pattern = Pattern.compile(regex);
    										Matcher matcher = pattern.matcher(content);
    										// 进行替换，将匹配到的值替换为 newValue
    										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");
    										continue;

//          						        } else if ("年".equals(eValue)) {
//                                      		sValue = "<gen:yy>6</gen:yy>";
//          						        } else if ("月".equals(eValue)) {
//          						        	//TODO
//                                      		sValue = "<gen:mm>1</gen:mm>";
//          						        } else if ("日".equals(eValue)) {
//                                      		sValue = "<gen:dd>2</gen:dd>";

                    			        }

                                    }
                                }

                                // 替换目标字符串
                                mValue = "<" + mValue + "></" + mValue + ">";
                                sValue = ">" + sValue + "<";
                                content = content.replace(mValue, mValue.replace("><", sValue));

                            }

                    	}

                    }


                    /*
                     *
                     */

                    String key ="";
                    String value ="";

                    key = "softNM=\"\"" ;
                    value = "softNM=\"ntaclient\"" ;
                    content = content.replace(key, value);

                    key = "sakuseiNM=\"\"" ;
                    value = "sakuseiNM=\""+ FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25)  +"\"" ;
                    content = content.replace(key, value);

                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    formattedDate = formatter.format(now);

                    key = "sakuseiDay=\"\"" ;
                    value = "sakuseiDay=\""+ formattedDate  +"\"" ;
                    content = content.replace(key, value);


                    /*
                     *
                     */
                    //TODO
                    //TENPU
					if (sheetName.contains("SOZ")) {
						content = "<TENPU id=\"TENPU\">" + content + "</TENPU>";
					}


                    // 将字符串转换为 XML Document 对象
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(content)));

                    // 将 XML Document 转换为格式化的字符串
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                    XPathFactory xPathFactory = XPathFactory.newInstance();
                    XPath xpath = xPathFactory.newXPath();


                    //TENPU
					if (sheetName.contains("SOZ")) {
						//                    	content = "<TENPU id=\"TENPU\">" + content + "</TENPU>";

						XPathExpression expression = xpath.compile("//SOZ074");
						Element element = (Element) expression.evaluate(document, XPathConstants.NODE);

						// 添加命名空间属性
						if (element != null) {
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://xml.e-tax.nta.go.jp/XSD/somu");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gen", "http://xml.e-tax.nta.go.jp/XSD/general");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:kyo", "http://xml.e-tax.nta.go.jp/XSD/kyotsu");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

						}

					}



					/*
					 *
					 */
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String outputString = writer.toString();

                    // 移除空行
                    outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");

                    xtxMap.put(xtx_fileName, outputString);

                    // 输出格式化后的 XML 字符串到后台
//                    logger.info("XML 格式化输出：");
//                    logger.info("\n" + outputString);

                    writeToFile(outputString, path_ETAX_output+ "_" + file.getName().replace(".xml", ".xtx"));
//                    writeToFile(XmlConverter.convertXmlToEscaped(outputString), path_ETAX_output+ "_" + file.getName().replace(".xml", ".XmlToEscaped"));

					if (sheetName.contains("SOZ")) {
						outputString = outputString.replace("<TENPU id=\"TENPU\">", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
						outputString = outputString.replace("</TENPU>", "");

						TreeMap<String, String[]> zhangpiaoTreeMap = FuncUtilsAiEtax.PropertyCsvMap_zhangpiao.get(shouxu_id).get(shouxu_banben);
						String xml_fileName = "";
						for (Entry<String, String[]> entry : zhangpiaoTreeMap.entrySet()) {
				            if (entry.getKey().startsWith(xtx_fileName.split("_")[0].toUpperCase())) {
				            	xml_fileName = entry.getValue()[0];
				            }
						}
						xml_fileName = "_" + xml_fileName + "_" + file.getName();
	                    writeToFile(outputString, path_ETAX_output + xml_fileName);
//	                    writeToFile(XmlConverter.convertXmlToEscaped(outputString), path_ETAX_output+ "_" + xml_fileName.replace(".xml", ".XmlToEscaped"));

					}

            		//TODO
//            		break;
                }

            }
        } catch (Exception e) {
        	loggerStringBuffer.append(e);
//            e.printStackTrace();
            throw e;

        } finally {
            // 关闭资源
            if (workbook != null) {
            	workbook.close();
            }
            if (fis != null) {
                fis.close();
            }

        }
        writeToFile(loggerStringBuffer.toString(), path_ETAX_output + ".log");
		return xtxMap;
	}


}