package com.panda.servlet;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.bean.t_etax_account_resExBean;
import com.panda.bean.t_etax_account_xiaofeishuiBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_account_xiaofeishuiDao;
import com.panda.dao.t_etax_amazon_csvDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.dao.t_kuaijiDao;
import com.panda.dao.t_kuaiji_aDao;
import com.panda.dao.t_kuaiji_bcDao;
import com.panda.dao.t_kuaiji_dDao;
import com.panda.dao.t_xiaofeishui_shengaoDao;
import com.panda.servlet.ai.TableServiceDao;
import com.panda.utils.FuncUtils;

@WebServlet("/SearchUserInfoLogic")
@MultipartConfig
public class SearchUserInfoLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SearchUserInfoLogic.class.toString());

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
		String value_shengcheng_file_type = req.getParameter("value_shengcheng_file_type");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();




		String hidden_key = req.getParameter("hidden_key");
		String maxNo = req.getParameter("maxNo");
		String sort = req.getParameter("sort");
		if (StringUtils.isEmpty(sort)) {
			sort = "yyyymmdd_count";
		}

		String filter = req.getParameter("filter");
		if (StringUtils.isEmpty(filter)) {
			filter = "";
		}


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

		if ("getUserInfoTatujin".equals(hidden_key)) {
			getUserInfoTatujin(resp);

			logger.debug("end " + hidden_key);
			return;



		} else if ("getUserInfoTatujinShouxin".equals(hidden_key)) {
			getUserInfoTatujinShouxin(resp);

			logger.debug("end " + hidden_key);
			return;





		} else if ("getUserInfo".equals(hidden_key)) {
			getUserInfo(resp);

			logger.debug("end " + hidden_key);
			return;

		} else if ("getUserInfoTatujinShenqing".equals(hidden_key)) {
			/*
			 * 達人申請　ZIP下载
			 */
			//未使用
//			getUserInfoTatujinShenqing(resp, yyyymmdd_count);

			ArrayList<String> output_fileList = new ArrayList<String>();

			if (value_shengcheng_file_type.indexOf("ALL") > -1 || value_shengcheng_file_type.indexOf("JCT") > -1) {
				LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();
				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				LinkedHashMap_t_etax_account_resExBean = t_etax_account_resDao.selectSPEED(yyyymmdd_count, "1");
				t_etax_account_resBean EtaxBean = LinkedHashMap_t_etax_account_resExBean.get(yyyymmdd_count);
				output_fileList.add(EtaxBean.getOutput_file());
			}

			if (value_shengcheng_file_type.indexOf("消费税") > -1) {
				t_etax_account_xiaofeishuiDao t_etax_account_xiaofeishuiDao = new t_etax_account_xiaofeishuiDao();
				t_etax_account_xiaofeishuiBean t_etax_account_xiaofeishuiBean = t_etax_account_xiaofeishuiDao.select_where_yyyymmdd_count(yyyymmdd_count);
				output_fileList.add(t_etax_account_xiaofeishuiBean.getOutput_file());
			}
//			output_fileList = new ArrayList<String>();
//			output_fileList.add("20231024231359_20231024000666_公司名.zip");
//			output_fileList.add("20231024235935_20231024000667_公司名.zip");

			for (String fileName : output_fileList) {
				/*
				 * 達人申請　ZIP下载
				 */
				if (StringUtils.isEmpty(fileName)) {

					out.print("{\"res\":\"ZIP文件不存在\"}");
					logger.debug("end " + hidden_key);
					return;
				}

		        File file = new File(getServletContext().getRealPath("/output/ftpalist") + "/" + fileName);
		        if (file.exists()) {

		        } else {
					out.print("{\"res\":\"ZIP文件不存在\"}");
					logger.debug("end " + hidden_key);
					return;
		        }
			}

			out.print("{\"res\":\"" + output_fileList.toString().replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "") + "\"}");

			logger.debug("end " + hidden_key);
			return;


		} else if ("getUserInfoTatujinJieguo".equals(hidden_key)) {
			/*
			 * 達人　结果下载
			 */
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);

			if (EtaxAccountInfoBean == null) {
				out.print("{\"res\":\"结果文件不存在\"}");
				logger.debug("end " + hidden_key);
				return;
			}

			String form_CompanyName_Chinese = EtaxAccountInfoBean.getCompanyName_Chinese();
			//去掉字符串里的TAB，首尾半角空格，首尾全角空格
			form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/fileDataJieguo");
			String falieName = yyyymmdd_count + "_" + form_CompanyName_Chinese;
			path = path + "/" + falieName;



			/*
			 * 文件名变更
			 */

	        // 创建表示文件夹的File对象
	        File directory = new File(path);

	        // 检查文件夹是否存在
	        if (directory.exists() && directory.isDirectory()) {
	            // 获取文件夹中的所有文件
	            File[] files = directory.listFiles();

	            if (files != null) {
	                // 循环遍历文件并修改文件名
	                for (File file : files) {
	                    // 检查文件是否是普通文件
	                    if (file.isFile()) {
	                        // 获取文件名
	                        String oldFileName = file.getName();

	                        if (oldFileName.indexOf(EtaxAccountInfoBean.getUser_id()) == 0) {
	                        	continue;
	                        }

	                        // 新的文件名（在这里，将文件名更改为"new_" + 旧文件名）
							String newFileName = EtaxAccountInfoBean.getUser_id() + "_" + EtaxAccountInfoBean.getYyyymmdd_count() + "_" + form_CompanyName_Chinese;
							newFileName = newFileName + oldFileName.replaceAll(EtaxAccountInfoBean.getYyyymmdd_count(), "");

	                        // 构建新的File对象，指定新文件名
	                        File newFile = new File(path +"/"+ newFileName);

	                        // 重命名文件
	                        if (file.renameTo(newFile)) {

	                        } else {
	                        	logger.debug("无法更名文件 " + oldFileName);
	                        }
	                    }
	                }
	            } else {
	            	logger.debug("文件夹中没有文件。");
	            }
	        } else {
	        	logger.debug("指定的文件夹路径不存在或不是文件夹。");
	        }



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
					out.print("{\"res\":\"" + falieName + ".zip" + "\"}");

				} catch (IOException e) {
					e.printStackTrace();
				}




	        } else {
				out.print("{\"res\":\"结果文件不存在\"}");
				logger.debug("end " + hidden_key);
				return;
	        }


			logger.debug("end " + hidden_key);
			return;


		} else {

			try {




				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				EtaxDao EtaxDao = new EtaxDao();
				t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();
				t_kuaiji_aDao t_kuaiji_aDao = new t_kuaiji_aDao();
				t_kuaiji_bcDao t_kuaiji_bcDao = new t_kuaiji_bcDao();
				t_kuaiji_dDao t_kuaiji_dDao = new t_kuaiji_dDao();
				t_kuaijiDao t_kuaijiDao = new t_kuaijiDao();
				t_etax_amazon_csvDao t_etax_amazon_csvDao = new t_etax_amazon_csvDao();
				t_etax_account_xiaofeishuiDao t_etax_account_xiaofeishuiDao = new t_etax_account_xiaofeishuiDao();
				t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();

				TableServiceDao TableServiceDao = new TableServiceDao();
			    // 1. 查表列表
			    LinkedHashMap<String, LinkedHashMap<String, String>> aiTables = TableServiceDao.getAiTables();


				Map<String, String[]> HashMapParameterMap = req.getParameterMap();
				for (String key : HashMapParameterMap.keySet()) {
					if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
						continue;
					}

					if ("delete".equals(key)) {
						yyyymmdd_count = HashMapParameterMap.get(key)[0].toString();
						if (t_etax_account_infoDao.DELETE(User_infoBean, yyyymmdd_count) > 0 ) {
							EtaxDao.DELETE(yyyymmdd_count);
							t_etax_account_resDao.DELETE_res(yyyymmdd_count);

							t_xiaofeishui_shengaoDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_kuaiji_aDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_kuaiji_bcDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_kuaiji_dDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_kuaijiDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_etax_amazon_csvDao.delete_yyyy_where_yyyymmdd_count("2024", yyyymmdd_count);
							t_etax_account_xiaofeishuiDao.delete_where_yyyymmdd_count(yyyymmdd_count);
							t_etax_jieguoDao.delete_where_yyyymmdd_count(yyyymmdd_count);

							TableServiceDao.DELETE_where_yyyymmdd_count(aiTables, yyyymmdd_count);
						}

						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;

					} else if ("Syouninn".equals(key)) {

						try {
							String syouninn_status = HashMapParameterMap.get("Syouninn_status")[0];
							//TODO
							yyyymmdd_count = HashMapParameterMap.get(key)[0];

							t_etax_account_infoDao.Update_syouninn_status(yyyymmdd_count, syouninn_status);

							if ("承認有".equals(syouninn_status)) {
								try {
									t_etax_account_infoBean EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
											t_etax_account_resDao, EtaxDao, "");
									t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

								} catch (SQLException e) {
									t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

								}
							} else {
								t_etax_account_resDao.DELETE_res(yyyymmdd_count);
								EtaxDao.DELETE(yyyymmdd_count);

							}
						} catch (Exception e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}

						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;


					} else if ("set_shuilishi_id".equals(key)) {

						try {

							String PDSK = req.getParameter("PDSK");
							String set_shuilishi_id = req.getParameter("set_shuilishi_id");
							t_etax_account_xiaofeishuiDao.UpdateExKeyValueWhereKeyValue("shuilishi_id", set_shuilishi_id, "PDSK", PDSK);

						} catch (Exception e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}

						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;

					}



				}

				LinkedHashMap<String, User_infoBean> HashMapGroup_id_user_id = new LinkedHashMap<String, User_infoBean>();
				if (StringUtils.isEmpty(User_infoBean.getGroup_id()) == false) {
					User_infoDao User_infoDao = new User_infoDao();
					HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(User_infoBean.getGroup_id());
					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

				} else if ("admin".equals(User_infoBean.getPermissions())) {
					User_infoDao User_infoDao = new User_infoDao();
					HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(null);
					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

				} else {
					HashMapGroup_id_user_id.put(User_infoBean.getUser_id(), User_infoBean);
					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);

				}

				LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao
						.selectAll(User_infoBean, maxNo, null, sort, filter);
				session.setAttribute("LinkedHashMapt_etax_account_infoBean", LinkedHashMap_t_etax_account_infoExBean);

				//重复数据检查
				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanBKCompanyName_Chinese = new LinkedHashMap<String, t_etax_account_resBean>();
				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_Chinese = new LinkedHashMap<String, t_etax_account_resBean>();
				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanBKCompanyName_English = new LinkedHashMap<String, t_etax_account_resBean>();
				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_English = new LinkedHashMap<String, t_etax_account_resBean>();
//				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErr = new LinkedHashMap<String, t_etax_account_resBean>();
				for (t_etax_account_infoBean t_etax_account_infoBean : LinkedHashMap_t_etax_account_infoExBean.values()) {
					String CompanyName_Chinese = t_etax_account_infoBean.getCompanyName_Chinese();
					if (LinkedHashMapEtaxBeanBKCompanyName_Chinese.containsKey(CompanyName_Chinese) == true) {
						LinkedHashMapEtaxBeanErrCompanyName_Chinese.put(CompanyName_Chinese, null);
					} else {
						LinkedHashMapEtaxBeanBKCompanyName_Chinese.put(CompanyName_Chinese, null);
					}

					String CompanyName_English = t_etax_account_infoBean.getCompanyName_English();

					if (LinkedHashMapEtaxBeanBKCompanyName_English.containsKey(CompanyName_English) == true) {
						LinkedHashMapEtaxBeanErrCompanyName_English.put(CompanyName_English, null);
					} else {
						LinkedHashMapEtaxBeanBKCompanyName_English.put(CompanyName_English, null);
					}
				}

				session.setAttribute("LinkedHashMapEtaxBeanErrCompanyName_Chinese", LinkedHashMapEtaxBeanErrCompanyName_Chinese);
				session.setAttribute("LinkedHashMapEtaxBeanErrCompanyName_English", LinkedHashMapEtaxBeanErrCompanyName_English);


				if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
					LinkedHashMap<String, LinkedHashMap<String, String>> LinkedHashMapTongji = t_etax_account_infoDao.selectTongji(User_infoBean);
					session.setAttribute("LinkedHashMapTongji", LinkedHashMapTongji);
				}



			    // 2. 查字段列表
			    LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> tableColumns = TableServiceDao.getTableColumns();
					session.setAttribute("tableList", aiTables);
					session.setAttribute("tableColumnsList", tableColumns);
				session.setAttribute("getAllTablesYyyymmddCount", TableServiceDao.getAllTablesYyyymmddCount(aiTables, tableColumns));
				session.setAttribute("getAllTables_activation_code", TableServiceDao.getAllTables_activation_code(aiTables, tableColumns));




				req.getRequestDispatcher("/SearchUserInfo.jsp").forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		logger.debug("end");

		return;

	}

	private t_etax_account_infoBean exe_activation(String yyyymmdd_count, t_etax_account_infoDao t_etax_account_infoDao,
			t_etax_account_resDao t_etax_account_resDao, EtaxDao EtaxDao) throws SQLException {
		t_etax_account_infoBean EtaxAccountInfoBean;
		EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
		EtaxAccountInfoBean.setCompanyName_English(
				FuncUtils.toFullWidth(EtaxAccountInfoBean.getCompanyName_English()));
		t_etax_account_resDao.INSERT(yyyymmdd_count, EtaxAccountInfoBean);

		HashMap<String, String> HashMapKeyValueHtml = new HashMap<String, String>();
		HashMapKeyValueHtml.put("gHojinmei", EtaxAccountInfoBean.getCompanyName_English());

		String str = FuncUtils.fn_hanzi(EtaxAccountInfoBean.getCompanyName_Chinese());
		str = str.replaceAll(" ", "").replaceAll("　", "");
		if (str.length() > 59) {
			str = str.substring(0, 59);
		}
		//法人名称（フリガナ）
		HashMapKeyValueHtml.put("gHojinmeiKana", str);
		HashMapKeyValueHtml.put("gNChiTodohuken", "東京都");
		HashMapKeyValueHtml.put("gNChiAdd1", "文京区千石");
		HashMapKeyValueHtml.put("gNChiAdd2", "４丁目１４番９号１階");
		HashMapKeyValueHtml.put("gTTodohuken", "東京都");
		HashMapKeyValueHtml.put("gTZeimushomei", "小石川");
		str = EtaxAccountInfoBean.getDaibiaoName_Chinese();
		str = str.replaceAll(" ", "").replaceAll("　", "");
		HashMapKeyValueHtml.put("gDSeiKana", FuncUtils.fn_hanzi(str.substring(0, 1)));
		HashMapKeyValueHtml.put("gDmeiKana", FuncUtils.fn_hanzi(str.substring(1)));
		HashMapKeyValueHtml.put("gDSei", str.substring(0, 1));
		HashMapKeyValueHtml.put("gDmei", str.substring(1));
		HashMapKeyValueHtml.put("gDTodohuken", "東京都");
		HashMapKeyValueHtml.put("gDAdd1", "外国");
		HashMapKeyValueHtml.put("gDAdd2", "外国");
		HashMapKeyValueHtml.put("gPwd", "bpstax2302");
		HashMapKeyValueHtml.put("gPwd2", "bpstax2302");
		HashMapKeyValueHtml.put("gNKakuninBango", "123456");

		if (EtaxDao.INSERT(HashMapKeyValueHtml, yyyymmdd_count) == true) {
			logger.debug(yyyymmdd_count + " → etax DB import OK");

		} else {
			logger.debug(yyyymmdd_count + " → etax DB import NG");

		}
		return EtaxAccountInfoBean;
	}

	private void getUserInfoTatujinShenqing(HttpServletResponse response, String yyyymmdd_count) throws IOException {
        // 获取ZIP文件的真实路径
        String zipFilePath = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\ftpalist\\20231006025758_20230920000001_大连燕玉彤贸易有限公司.zip";

		// path是指欲下载的文件的路径
		File file = new File(zipFilePath);
		// 取得文件名
		String filename = file.getName();
		// 以流的形式下载文件
		InputStream fis;
		try {
			fis = new BufferedInputStream(new FileInputStream(zipFilePath));

			byte[] buffer = new byte[fis.available()];

			fis.read(buffer);

			fis.close();
			// 清空response
			response.reset();
			// 设置response的Header
			response.addHeader("Content-disposition", "attachment;filename=" + new String(filename.getBytes()));
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setContentType("application/x-msdownload");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			logger.debug("下载成功");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	private void getUserInfoTatujinShenqing1(HttpServletResponse response, String yyyymmdd_count) throws IOException {
		 // 设置响应的内容类型为ZIP文件
        response.setContentType("application/zip");

        // 设置响应头，告诉浏览器以附件方式下载文件
        response.setHeader("Content-Disposition", "attachment; filename=\"downloaded.zip\"");

        // 获取ZIP文件的真实路径
        String zipFilePath = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\ftpalist\\20231006025758_20230920000001_大连燕玉彤贸易有限公司.zip";

        // 读取ZIP文件并将其写入响应流
        try (FileInputStream fileInputStream = new FileInputStream(zipFilePath);
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	private void getUserInfoTatujinShenqing9(HttpServletResponse response) throws IOException {
		String yyyymmdd_count;

//		    // 创建一个临时目录来保存要压缩的文件
		String tempDir = getServletContext().getRealPath("/output");
//	        File tempDirectory = new File(tempDir);
//	        if (!tempDirectory.exists()) {
//	            tempDirectory.mkdirs();
//	        }

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, String> LinkedHashMap = t_etax_account_infoDao.selectBangouNotNull();
		ArrayList<String> folderPathList = new ArrayList<String>();
		for (String key : LinkedHashMap.keySet()) {
			yyyymmdd_count = key;
			String form_CompanyName_Chinese = LinkedHashMap.get(key);
			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/fileData");
			path = path + "\\" + yyyymmdd_count + "_" + form_CompanyName_Chinese;

		    // 在这个示例中，假设你要压缩的文件位于服务器上的某个目录下
		    String sourceDir = path; // 修改为实际文件所在的目录

		    // 使用Java的压缩库来压缩文件
		    String zipFileName = tempDir +"\\"+ yyyymmdd_count + "_" + form_CompanyName_Chinese + ".zip";
		    folderPathList.add(zipFileName);
		}


		 // 设置响应内容类型为二进制流
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=\"downloadedFile.txt\""); // 修改为实际文件名

		// 获取响应的输出流
		ServletOutputStream ServletOutputStream = response.getOutputStream();

		try {
		    // 假设你要下载的文件位于服务器上的某个路径
		    String filePath = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\output\\downloadedFile.txt"; // 修改为实际文件的路径

		    File file = new File(filePath);
		    FileInputStream fis = new FileInputStream(file);

		    // 创建缓冲区
		    byte[] buffer = new byte[4096];
		    int bytesRead;

		    // 将文件内容写入响应输出流
		    while ((bytesRead = fis.read(buffer)) > 0) {
		    	ServletOutputStream.write(buffer, 0, bytesRead);
		    }

		    fis.close();
		} catch (Exception e) {
		    e.printStackTrace();
		    ServletOutputStream.println("下载文件时发生错误。");
		} finally {
		    // 关闭输出流
			ServletOutputStream.close();
		}
	}

	private void getUserInfoTatujin(HttpServletResponse response) throws IOException {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectTatujin();


		 ArrayList<String> timu= new  ArrayList<String>();
		 timu.add("事業者コード");
		 timu.add("事業者名フリガナ");
		 timu.add("事業者名");
		 timu.add("事業者法人個人区分");
		 timu.add("事業者青白区分");
		 timu.add("事業者郵便番号1");
		 timu.add("事業者郵便番号2");
		 timu.add("事業者所在地フリガナ");
		 timu.add("事業者所在地");
		 timu.add("事業者電話番号1");
		 timu.add("事業者電話番号2");
		 timu.add("事業者電話番号3");
		 timu.add("事業者FAX1");
		 timu.add("事業者FAX2");
		 timu.add("事業者FAX3");
		 timu.add("事業者URL");
		 timu.add("事業者メールアドレス");
		 timu.add("関与開始日");
		 timu.add("関与終了日");
		 timu.add("業務区分(税務代理)");
		 timu.add("業務区分(書類作成)");
		 timu.add("業務区分(税務相談)");
		 timu.add("税理士法第33条の2の書面添付");
		 timu.add("予備1");
		 timu.add("予備2");
		 timu.add("予備3");
		 timu.add("備考");
		 timu.add("事業者法人番号");
		 timu.add("事業者法人区分");
		 timu.add("事業者普通法人等区分");
		 timu.add("事業者公益法人等区分");
		 timu.add("事業者事業内容");
		 timu.add("事業者屋号フリガナ");
		 timu.add("事業者屋号");
		 timu.add("事業者法人整理番号");
		 timu.add("事業者法人所轄税務署");
		 timu.add("決算月");
		 timu.add("法人利用者識別番号");
		 timu.add("法人利用者ID");
		 timu.add("代表者名フリガナ");
		 timu.add("代表者名");
		 timu.add("代表者役職");
		 timu.add("代表者郵便番号1");
		 timu.add("代表者郵便番号2");
		 timu.add("代表者住所フリガナ");
		 timu.add("代表者住所");
		 timu.add("代表者電話番号1");
		 timu.add("代表者電話番号2");
		 timu.add("代表者電話番号3");
		 timu.add("代表者連絡先1");
		 timu.add("代表者連絡先2");
		 timu.add("代表者連絡先3");
		 timu.add("代表者メールアドレス");
		 timu.add("経理責任者名フリガナ");
		 timu.add("経理責任者名");
		 timu.add("経理責任者郵便番号1");
		 timu.add("経理責任者郵便番号2");
		 timu.add("経理責任者住所フリガナ");
		 timu.add("経理責任者住所");
		 timu.add("経理責任者電話番号1");
		 timu.add("経理責任者電話番号2");
		 timu.add("経理責任者電話番号3");
		 timu.add("経理責任者連絡先1");
		 timu.add("経理責任者連絡先2");
		 timu.add("経理責任者連絡先3");
		 timu.add("経理責任者メールアドレス");
		 timu.add("事業者性別");
		 timu.add("事業者生年月日");
		 timu.add("事業者職業");
		 timu.add("事業者連絡先1");
		 timu.add("事業者連絡先2");
		 timu.add("事業者連絡先3");
		 timu.add("事業者世帯主の氏名");
		 timu.add("事業者世帯主との続柄");
		 timu.add("事業者個人整理番号");
		 timu.add("事業者個人所轄税務署");
		 timu.add("個人利用者識別番号");
		 timu.add("個人利用者ID");
		 timu.add("事業者屋号･雅号フリガナ");
		 timu.add("事業者屋号･雅号");
		 timu.add("事業所郵便番号1");
		 timu.add("事業所郵便番号2");
		 timu.add("事業所所在地フリガナ");
		 timu.add("事業所所在地");
		 timu.add("事業所電話番号1");
		 timu.add("事業所電話番号2");
		 timu.add("事業所電話番号3");
		 timu.add("アクセス権設定");
		 timu.add("アクセス権設定者");


		File tempDir = new File("D:/poi-temp");
		if (!tempDir.exists()) {
		    tempDir.mkdirs();
		}

		TempFile.setTempFileCreationStrategy(
		    new DefaultTempFileCreationStrategy(tempDir)
		);


		 SXSSFWorkbook workbook = null;
		    OutputStream out = null;

		try {
			// 生成Excel文件
//		Workbook workbook = new XSSFWorkbook();
			// ⭐ 关键：使用 SXSSF（流式）
			workbook = new SXSSFWorkbook(null, 100, false, true);

			// 创建工作表
			Sheet sheet = workbook.createSheet("tatujinData");

			// 写入数据
			int rowNum = 0;

			// 创建行并在每行中写入数据
			Row row = sheet.createRow(rowNum);
			for (int i = 0; i < timu.size(); i++) {
			    Cell cell = row.createCell(i);
			    cell.setCellValue(timu.get(i));
			}
			rowNum++;

			logger.debug("1 ");
			for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
			    ArrayList<String> data = entry.getValue();

			    // 创建行并在每行中写入数据
			    row = sheet.createRow(rowNum);
			    for (int i = 0; i < data.size(); i++) {
			        Cell cell = row.createCell(i);

			        String value = data.get(i);


			        if( "V230802001".equals(value)) {
			        	value = data.get(i);
			        }

			        //全角20
			        if (i == 1 || i == 39 || i == 44 || i == 78) {
			        	value = FuncUtils.toFullWidthAndTruncate(value, 20);

			        	//半角25
			        } else if (i == 2 || i == 40 || i == 45 || i == 79) {
			        	value = FuncUtils.toHalfWidthAndTruncate(value, 25);

			        }

//		        44	45
//		        代表者住所フリガナ	代表者住所
//		        ガイコク	外国
			    	if (i == 44 && StringUtils.isEmpty(value)) {
			    		value = "ガイコク";

			        } else if (i == 45 && StringUtils.isEmpty(value)) {
			    		value = "外国";

			    	}

			        cell.setCellValue(value);
			    }

			    rowNum++;


			     // ⭐ 每1000行打印日志（方便排查）
			    if (rowNum % 1000 == 0) {
					logger.debug("已写入行数: " + rowNum);
			    }
			}

			logger.debug("2 ");
			// 自动调整列宽
//		for (int i = 0; i < timu.size(); i++) {
//			sheet.autoSizeColumn(i);
//		}

			logger.debug("3 ");
			// 将Excel文件写入字节数组
//			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//			workbook.write(byteArrayOutputStream);

			logger.debug("4 ");
//			// 将Excel文件的字节数组作为Base64字符串发送给客户端
//			byte[] excelBytes = byteArrayOutputStream.toByteArray();
//			String excelBase64 = java.util.Base64.getEncoder().encodeToString(excelBytes);
//
//			// 设置响应内容类型
//			response.setContentType("text/plain");
//
//			// 将Excel文件的Base64字符串作为响应
//			response.getWriter().write(excelBase64);


			// ⭐ 防止之前写入
			response.reset();

			// ⭐ 日本时间（JST）
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			sdf.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
			String timeStr = sdf.format(new Date());

			// ⭐ 文件名
			String fileName = "tatujinData" + timeStr + ".xlsx";

			// ⭐ 编码（防乱码）
			String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

			// ⭐ 设置响应头
			response.reset();  // 强烈建议加上（避免之前有输出）
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

			// ⭐ 关键：写入下载完成标记（必须在 flush 之前）
			response.addHeader("Set-Cookie", "fileDownload=true; path=/");

			// ❗ 只允许这一种输出
			out = response.getOutputStream();

			workbook.write(out);

			out.flush();


		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
	    } finally {
	        // ⭐ 必须释放临时文件
	        if (out != null) {
	        	out.close();
	        }
	        // ⭐ 必须释放临时文件
	        if (workbook != null) {
	        	workbook.dispose();
	        }
		}
	}

	private void getUserInfoTatujinShouxinExcel(HttpServletResponse response) throws IOException {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectTatujinShouxin();

		ArrayList<String> timu= new  ArrayList<String>();
		timu.add("利用者名(1～30文字)");
		timu.add("フリガナ(利用者)(1～60文字)");
		timu.add("利用者識別番号(16桁)");
		timu.add("事業者コード");
		timu.add("旧暗証番号(8～50文字)");
		timu.add("新暗証番号(8～50文字)");
		timu.add("利用者種別(1：納税者,2：税理士)");
		timu.add("法人個人区分(1：法人,2：個人)");
		timu.add("決算月(1～12)");
		timu.add("メモ(1～50文字)");
		timu.add("メールアドレス登録(インポート対象外)");
		timu.add("事業者法人整理番号");
		timu.add("メインメールアドレス(1～128文字)");
		timu.add("サブメールアドレス１(1～128文字)");
		timu.add("サブメールアドレス２(1～128文字)");
		timu.add("宛名(1～30文字)");

		// 生成Excel文件
		Workbook workbook = new XSSFWorkbook();

		// 创建工作表
		Sheet sheet = workbook.createSheet("tatujinData");

		// 写入数据
		int rowNum = 0;

		// 创建行并在每行中写入数据
		Row row = sheet.createRow(rowNum);
		for (int i = 0; i < timu.size(); i++) {
		    Cell cell = row.createCell(i);
		    cell.setCellValue(timu.get(i));
		}
		rowNum++;

		logger.debug("1 ");
		for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
		    ArrayList<String> data = entry.getValue();

		    // 创建行并在每行中写入数据
		    row = sheet.createRow(rowNum);
		    for (int i = 0; i < data.size(); i++) {
		        Cell cell = row.createCell(i);
		        cell.setCellValue(data.get(i));
		    }

		    rowNum++;
		}

		logger.debug("2 ");
		// 自动调整列宽
//		for (int i = 0; i < timu.size(); i++) {
//			sheet.autoSizeColumn(i);
//		}

		logger.debug("3 ");
		// 将Excel文件写入字节数组
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);

		logger.debug("4 ");
		// 将Excel文件的字节数组作为Base64字符串发送给客户端
		byte[] excelBytes = byteArrayOutputStream.toByteArray();
		String excelBase64 = java.util.Base64.getEncoder().encodeToString(excelBytes);

		// 设置响应内容类型
		response.setContentType("text/plain");

		// 将Excel文件的Base64字符串作为响应
		response.getWriter().write(excelBase64);
	}

	private void getUserInfoTatujinShouxin(HttpServletResponse response) throws IOException {

	    t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
	    LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectTatujinShouxin();

	    ArrayList<String> timu = new ArrayList<>();
	    timu.add("利用者名(1～30文字)");
	    timu.add("フリガナ(利用者)(1～60文字)");
	    timu.add("利用者識別番号(16桁)");
	    timu.add("事業者コード(1～10文字)");
	    timu.add("旧暗証番号(8～50文字)");
	    timu.add("新暗証番号(8～50文字)");
//	    timu.add("\"利用者種別(1：納税者,2：税理士)\"");
//	    timu.add("\"法人個人区分(1：法人,2：個人)\"");

	    timu.add("利用者種別(1：納税者,2：税理士)");
	    timu.add("法人個人区分(1：法人,2：個人)");


	    timu.add("決算月(1～12)");
	    timu.add("メモ(1～50文字)");
	    timu.add("メールアドレス登録(インポート対象外)");
	    timu.add("事業者法人整理番号");
	    timu.add("メインメールアドレス(1～128文字)");
	    timu.add("サブメールアドレス１(1～128文字)");
	    timu.add("サブメールアドレス２(1～128文字)");
	    timu.add("宛名(1～30文字)");

	    // StringBuilder 用来构建 CSV
	    StringBuilder csv = new StringBuilder();

	    // CSV 需要处理双引号 (所有字段用双引号包裹)
	    // 写入标题列
	    for (int i = 0; i < timu.size(); i++) {
	        csv.append("\"").append(timu.get(i).replace("\"", "\"\"")).append("\"");
//	        csv.append(timu.get(i));
	        if (i < timu.size() - 1) csv.append(",");
	    }
	    csv.append("\n");

	    // 写入数据
	    for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
	        ArrayList<String> data = entry.getValue();
	        for (int i = 0; i < data.size(); i++) {
	            String cell = data.get(i) == null ? "" : data.get(i);

	            // CSV 安全：内部双引号需要转义成两个双引号
//	            cell = cell.replace("\"", "\"\"");

	            csv.append("\"").append(cell).append("\"");
//	            csv.append(cell);
	            if (i < data.size() - 1) csv.append(",");
	        }
	        csv.append("\n");
	    }

	    // CSV 转成 Shift-JIS 编码
	    byte[] csvBytes = csv.toString().getBytes("Shift_JIS");

	    // Base64
	    String csvBase64 = java.util.Base64.getEncoder().encodeToString(csvBytes);

	    response.setContentType("text/plain; charset=UTF-8");
	    response.getWriter().write(csvBase64);
	}


	private void getUserInfoTatujinOld(HttpServletResponse response) throws IOException {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectTatujin();


		 ArrayList<String> timu= new  ArrayList<String>();
		 timu.add("事業者コード");
		 timu.add("事業者名フリガナ");
		 timu.add("事業者名");
		 timu.add("事業者法人個人区分");
		 timu.add("事業者青白区分");
		 timu.add("事業者郵便番号1");
		 timu.add("事業者郵便番号2");
		 timu.add("事業者所在地フリガナ");
		 timu.add("事業者所在地");
		 timu.add("事業者電話番号1");
		 timu.add("事業者電話番号2");
		 timu.add("事業者電話番号3");
		 timu.add("事業者FAX1");
		 timu.add("事業者FAX2");
		 timu.add("事業者FAX3");
		 timu.add("事業者URL");
		 timu.add("事業者メールアドレス");
		 timu.add("関与開始日");
		 timu.add("関与終了日");
		 timu.add("業務区分(税務代理)");
		 timu.add("業務区分(書類作成)");
		 timu.add("業務区分(税務相談)");
		 timu.add("税理士法第33条の2の書面添付");
		 timu.add("予備1");
		 timu.add("予備2");
		 timu.add("予備3");
		 timu.add("備考");
		 timu.add("事業者法人番号");
		 timu.add("事業者法人区分");
		 timu.add("事業者普通法人等区分");
		 timu.add("事業者公益法人等区分");
		 timu.add("事業者事業内容");
		 timu.add("事業者屋号フリガナ");
		 timu.add("事業者屋号");
		 timu.add("事業者法人整理番号");
		 timu.add("事業者法人所轄税務署");
		 timu.add("決算月");
		 timu.add("法人利用者識別番号");
		 timu.add("法人利用者ID");
		 timu.add("代表者名フリガナ");
		 timu.add("代表者名");
		 timu.add("代表者役職");
		 timu.add("代表者郵便番号1");
		 timu.add("代表者郵便番号2");
		 timu.add("代表者住所フリガナ");
		 timu.add("代表者住所");
		 timu.add("代表者電話番号1");
		 timu.add("代表者電話番号2");
		 timu.add("代表者電話番号3");
		 timu.add("代表者連絡先1");
		 timu.add("代表者連絡先2");
		 timu.add("代表者連絡先3");
		 timu.add("代表者メールアドレス");
		 timu.add("経理責任者名フリガナ");
		 timu.add("経理責任者名");
		 timu.add("経理責任者郵便番号1");
		 timu.add("経理責任者郵便番号2");
		 timu.add("経理責任者住所フリガナ");
		 timu.add("経理責任者住所");
		 timu.add("経理責任者電話番号1");
		 timu.add("経理責任者電話番号2");
		 timu.add("経理責任者電話番号3");
		 timu.add("経理責任者連絡先1");
		 timu.add("経理責任者連絡先2");
		 timu.add("経理責任者連絡先3");
		 timu.add("経理責任者メールアドレス");
		 timu.add("事業者性別");
		 timu.add("事業者生年月日");
		 timu.add("事業者職業");
		 timu.add("事業者連絡先1");
		 timu.add("事業者連絡先2");
		 timu.add("事業者連絡先3");
		 timu.add("事業者世帯主の氏名");
		 timu.add("事業者世帯主との続柄");
		 timu.add("事業者個人整理番号");
		 timu.add("事業者個人所轄税務署");
		 timu.add("個人利用者識別番号");
		 timu.add("個人利用者ID");
		 timu.add("事業者屋号･雅号フリガナ");
		 timu.add("事業者屋号･雅号");
		 timu.add("事業所郵便番号1");
		 timu.add("事業所郵便番号2");
		 timu.add("事業所所在地フリガナ");
		 timu.add("事業所所在地");
		 timu.add("事業所電話番号1");
		 timu.add("事業所電話番号2");
		 timu.add("事業所電話番号3");
		 timu.add("アクセス権設定");
		 timu.add("アクセス権設定者");




		// 生成Excel文件
		Workbook workbook = new XSSFWorkbook();

		// 创建工作表
		Sheet sheet = workbook.createSheet("tatujinData");

		// 写入数据
		int rowNum = 0;

		// 创建行并在每行中写入数据
		Row row = sheet.createRow(rowNum);
		for (int i = 0; i < timu.size(); i++) {
		    Cell cell = row.createCell(i);
		    cell.setCellValue(timu.get(i));
		}
		rowNum++;

		logger.debug("1 ");
		for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
		    ArrayList<String> data = entry.getValue();

		    // 创建行并在每行中写入数据
		    row = sheet.createRow(rowNum);
		    for (int i = 0; i < data.size(); i++) {
		        Cell cell = row.createCell(i);
		        cell.setCellValue(data.get(i));
		    }

		    rowNum++;
		}

		logger.debug("2 ");
		// 自动调整列宽
//		for (int i = 0; i < timu.size(); i++) {
//			sheet.autoSizeColumn(i);
//		}

		logger.debug("3 ");
		// 将Excel文件写入字节数组
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);

		logger.debug("4 ");
		// 将Excel文件的字节数组作为Base64字符串发送给客户端
		byte[] excelBytes = byteArrayOutputStream.toByteArray();
		String excelBase64 = java.util.Base64.getEncoder().encodeToString(excelBytes);

		// 设置响应内容类型
		response.setContentType("text/plain");

		// 将Excel文件的Base64字符串作为响应
		response.getWriter().write(excelBase64);
	}


	private void getUserInfo(HttpServletResponse response) throws IOException {

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectUserInfo();


		 ArrayList<String> timu= new  ArrayList<String>();
		 timu.add("UPDATE_DATE");
		 timu.add("管理ID");
		 timu.add("达人ID");
		 timu.add("日本消费税税号");
		 timu.add("ETAX账号");
		 timu.add("ETAX密码");
		 timu.add("官方用户类型");
		 timu.add("自选用户类型");

		 timu.add("公司名称或个体户本人姓名（所在地区文字）");
		 timu.add("公司名称或个体户本人姓名（英文）");
		 timu.add("公司名称或个体户本人姓名（日文片假名）");

		 timu.add("公司名称或个体户本人姓名（全大日文片假名）");

		 timu.add("公司地址或个体户本人住址（所在地区文字）");
		 timu.add("公司地址或个体户本人住址（英文）");
		 timu.add("公司地址或个体户本人住址（日文片假名）");

		 timu.add("公司代表人姓名或个体户经营场所名称（所在地区文字）");
		 timu.add("公司代表人姓名或个体户经营场所名称（英文）");
		 timu.add("公司代表人姓名或个体户经营场所名称（日文片假名）");

		 timu.add("公司代表人住址或个体户经营场所地址（所在地区文字）");
		 timu.add("公司代表人住址或个体户经营场所地址（英文）");
		 timu.add("公司代表人住址或个体户经营场所地址（日文片假名）");

		 timu.add("公司成立年或个体户本人出生年");
		 timu.add("公司成立月或个体户本人出生月");
		 timu.add("公司成立日或个体户本人出生日");



		// 生成Excel文件
		Workbook workbook = new XSSFWorkbook();

		// 创建工作表
		Sheet sheet = workbook.createSheet("UserInfo");

		// 写入数据
		int rowNum = 0;

//	// 创建行并在每行中写入数据
//	Row row = sheet.createRow(rowNum);
//	for (int i = 0; i < timu.size(); i++) {
//	    Cell cell = row.createCell(i);
//	    cell.setCellValue(timu.get(i));
//	}
//	rowNum++;

		logger.debug("1 ");
//	for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
//	    ArrayList<String> data = entry.getValue();
//
//	    // 创建行并在每行中写入数据
//	    row = sheet.createRow(rowNum);
//	    for (int i = 0; i < data.size(); i++) {
//	        Cell cell = row.createCell(i);
//	        cell.setCellValue(data.get(i));
//	    }
//
//	    rowNum++;
//	}

		logger.debug("2 ");
		// 自动调整列宽
//	for (int i = 0; i < timu.size(); i++) {
//		sheet.autoSizeColumn(i);
//	}

		// 创建边框样式
		CellStyle borderStyle = workbook.createCellStyle();
		borderStyle.setBorderTop(BorderStyle.THIN);
		borderStyle.setBorderBottom(BorderStyle.THIN);
		borderStyle.setBorderLeft(BorderStyle.THIN);
		borderStyle.setBorderRight(BorderStyle.THIN);

		// 创建标题行样式（黄色背景 + 加粗 + 边框）
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.cloneStyleFrom(borderStyle);
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		// 写标题
		Row row = sheet.createRow(rowNum);
		for (int i = 0; i < timu.size(); i++) {
		    Cell cell = row.createCell(i);
		    cell.setCellValue(timu.get(i));
		    cell.setCellStyle(headerStyle);
		}
		rowNum++;

		// 写数据
		for (Map.Entry<String, ArrayList<String>> entry : tatujinHashMap.entrySet()) {
		    ArrayList<String> data = entry.getValue();
		    row = sheet.createRow(rowNum);

		    for (int i = 0; i < data.size(); i++) {
		    	Cell cell = row.createCell(i);
		    	cell.setCellValue(data.get(i));
		        cell.setCellStyle(borderStyle);
		    }

		    rowNum++;
		}

		// 自动列宽 A～I
		for (int i = 0; i <= 8; i++) {
		    sheet.autoSizeColumn(i);
		}

		// 冻结首行
		sheet.createFreezePane(0, 1);

		logger.debug("3 ");
		// 将Excel文件写入字节数组
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		workbook.write(byteArrayOutputStream);

		logger.debug("4 ");
		// 将Excel文件的字节数组作为Base64字符串发送给客户端
		byte[] excelBytes = byteArrayOutputStream.toByteArray();
		String excelBase64 = java.util.Base64.getEncoder().encodeToString(excelBytes);

		// 设置响应内容类型
		response.setContentType("text/plain");

		// 将Excel文件的Base64字符串作为响应
		response.getWriter().write(excelBase64);

	}

    // 将文件夹及其内容添加到ZIP的方法
    private void addFolderToZip(File folder, String folderName, ZipOutputStream zipOut) throws IOException {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 递归添加子文件夹及其内容
                        addFolderToZip(file, folderName + "/" + file.getName(), zipOut);
                    } else {
                        // 添加文件到ZIP
                        addFileToZip(file, zipOut, folderName);
                    }
                }
            }
        }
    }

    // 将文件添加到ZIP的方法
    private void addFileToZip(File file, ZipOutputStream zipOut, String folderName) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(folderName + "/" + file.getName());
        zipOut.putNextEntry(zipEntry);

        // 将文件内容写入ZIP输出流
        byte[] buffer = new byte[4096];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zipOut.write(buffer, 0, length);
        }
        fis.close();
    }

    // 将文件添加到ZIP的方法（不包括文件夹）
    private void addFileToZip(File file, ZipOutputStream zipOut) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zipOut.putNextEntry(zipEntry);

        // 将文件内容写入ZIP输出流
        byte[] buffer = new byte[4096];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zipOut.write(buffer, 0, length);
        }
        fis.close();
    }
    // 使用Java的压缩库来压缩文件夹
    private void compressFiles(String sourceDir, String zipFileName) throws IOException {
        File sourceDirectory = new File(sourceDir);
        FileOutputStream fos = new FileOutputStream(zipFileName);
        ZipOutputStream zos = new ZipOutputStream(fos);

        // 调用递归方法来压缩源文件夹
        zipDirectory(sourceDirectory, sourceDirectory.getName(), zos);

        zos.close();
        fos.close();
    }

 // 递归地压缩文件夹中的文件
    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                fis.close();
            }
        }
    }

    // 将文件内容写入输出流的方法
    private void copyFileToOutputStream(String filePath, ServletOutputStream out) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        byte[] buffer = new byte[4096];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        fis.close();
    }
	public static void main(String[] args) {
	}
}