package com.panda.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.panda.batch.AmazonCsvFormat;
import com.panda.batch.FileLineCounter;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.bean.t_etax_amazon_csvBean;
import com.panda.bean.t_kuaijiBean;
import com.panda.bean.t_kuaiji_aBean;
import com.panda.bean.t_kuaiji_bcBean;
import com.panda.bean.t_kuaiji_dBean;
import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.dao.ConnectionDao;
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_amazon_csvDao;
import com.panda.dao.t_kuaijiDao;
import com.panda.dao.t_kuaiji_aDao;
import com.panda.dao.t_kuaiji_bcDao;
import com.panda.dao.t_kuaiji_dDao;
import com.panda.utils.FuncUtils;

@WebServlet("/SearchKuaijiLogic")
@MultipartConfig
public class SearchKuaijiLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SearchKuaijiLogic.class.toString());


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



		String yyyy = req.getParameter("yyyy");
		//TODO
		yyyy = "2024";

		String hidden_key = req.getParameter("hidden_key");
		if (hidden_key == null) {
			hidden_key = "";
		}
		String maxNo = req.getParameter("maxNo");


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



		/*
		 * 登录功能 附件
		 */
		if (!StringUtils.isEmpty(yyyymmdd_count) && "kuaiji_import".equals(hidden_key)) {
			try {


//				kuaiji_import(req, out, yyyymmdd_count, yyyy);

			} catch (Throwable e) {
				e.printStackTrace();
			}



			//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
			out.print(yyyymmdd_count);
			logger.debug("end " + hidden_key);
			return;

		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "kuaiji_output".equals(hidden_key)) {


			try {

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoBean t_etax_account_infoBean = t_etax_account_infoDao.select(yyyymmdd_count);
				String form_CompanyName_Chinese = t_etax_account_infoBean.getCompanyName_Chinese();
				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

				//アップロードするフォルダ
				String path = getServletContext().getRealPath("/kuaiji_import");
				String path_kuaiji_output = getServletContext().getRealPath("/kuaiji_output");
				String fileName = yyyymmdd_count + "_" + yyyy + "_" + form_CompanyName_Chinese;
				path = path + "/" + fileName;
				path_kuaiji_output = path_kuaiji_output + "/" + fileName;



				long l = 0;

				l = kuaiji_amazon_to_file(yyyymmdd_count, yyyy, t_etax_account_infoBean, path_kuaiji_output, l);

				//1收入的账本.xlsx
				l = kuaiji_a_to_file(yyyymmdd_count, yyyy, t_etax_account_infoBean, path_kuaiji_output, l);

				//4进口的账本.xlsx
				l = kuaiji_d_to_file(yyyymmdd_count, yyyy, t_etax_account_infoBean, path_kuaiji_output, l);

				//2合规发票账本.xlsx
				l = kuaiji_bc_to_file(yyyymmdd_count, yyyy, t_etax_account_infoBean, path_kuaiji_output, l);

				//3非合规发票账本.xlsx

			} catch (Throwable e) {
				e.printStackTrace();
				out.print("NG");
				logger.debug("end " + hidden_key);
				return;
			}


			//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
			out.print("OK");
			logger.debug("end " + hidden_key);
			return;




		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "kuaiji_output_zip".equals(hidden_key)) {
			/*
			 * 会計　ZIP下载
			 */
			try {

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoBean t_etax_account_infoBean = t_etax_account_infoDao.select(yyyymmdd_count);
				String form_CompanyName_Chinese = t_etax_account_infoBean.getCompanyName_Chinese();
				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

				String path = getServletContext().getRealPath("/kuaiji_output");
				String fileName = yyyymmdd_count + "_" + yyyy + "_" +  form_CompanyName_Chinese;
				path = path + "/" + fileName;


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

					} catch (IOException e) {
						e.printStackTrace();

						out.print("{\"res\":\"ZIP生成错误\"}");
						logger.debug("end " + hidden_key);
						return;
					}

		        }

				out.print("{\"res\":\"" + (fileName + ".zip").replace("\\", "\\\\")+ "\"}");
				logger.debug("end " + hidden_key);
				return;

			} catch (Throwable e) {
				e.printStackTrace();
				out.print("{\"res\":\"ZIP文件不存在\"}");
				logger.debug("end " + hidden_key);
				return;
			}



		} else if ("getUserInfoTatujin".equals(hidden_key)) {
			getUserInfoTatujin(resp);

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
			String fileName = yyyymmdd_count + "_" + form_CompanyName_Chinese;
			path = path + "/" + fileName;



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
					out.print("{\"res\":\"" + fileName + ".zip" + "\"}");

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
						.selectAll(User_infoBean, maxNo, yyyy, null, null);
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



				req.getRequestDispatcher("/SearchKuaiji.jsp").forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		logger.debug("end");

		return;

	}

	public String kuaiji_import(HttpServletRequest req, PrintWriter out, t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean) throws Exception {

		String PDSK = t_xiaofeishui_shengaoBean.getPDSK();
		String yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();
		String yyyy = t_xiaofeishui_shengaoBean.getYyyy();

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);
		String form_CompanyName_Chinese = EtaxAccountInfoBean.getCompanyName_Chinese();
		//去掉字符串里的TAB，首尾半角空格，首尾全角空格
		form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);



		//アップロードするフォルダ
		String path = req.getServletContext().getRealPath("/kuaiji_import");
		String path_kuaiji_output = req.getServletContext().getRealPath("/kuaiji_output");
		String folderName = PDSK + "_" + yyyymmdd_count + "_" + yyyy;
		path = path + "/" + folderName;
		path_kuaiji_output = path_kuaiji_output + "/" + folderName;


		File directory = new File(path);
		boolean hasSucceeded = directory.mkdir();
		logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);


		File directory_kuaiji_output = new File(path_kuaiji_output);
		boolean hasSucceeded_kuaiji_output = directory_kuaiji_output.mkdir();
		logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded_kuaiji_output);

		req.getParts();
		req.setCharacterEncoding("utf-8");


		String hidden_key = req.getParameter("hidden_key");

		if ("send-pdf".equals(hidden_key) == false) {
			//删除文件夹下所有csv
			for (int j = 0; j < req.getParts().size(); j++) {
				//name属性がfileのファイルをPartオブジェクトとして取得
				Part part = req.getPart("file[" + j + "]");
				//ファイル名を取得
				//String filename=part.getSubmittedFileName();//ie対応が不要な場合
				String filename = yyyymmdd_count + "_" + yyyy + "_";
				filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();

				String fe = FilenameUtils.getExtension(filename);

				if ("csv".equals(fe.toLowerCase())) {
		            // 删除所有CSV文件kuaiji_import
					File folder = new File(path);
			        File[] csvFiles = folder.listFiles(new FilenameFilter() {
			            @Override
			            public boolean accept(File dir, String name) {
			                return name.toLowerCase().endsWith(".csv");
			            }
			        });

			        if (csvFiles != null) {
			            for (File csvFile : csvFiles) {
			            	csvFile.delete();
			            }
			        }

		            // 删除所有CSV文件kuaiji_output
			        folder = new File(path_kuaiji_output);
			        csvFiles = folder.listFiles(new FilenameFilter() {
			            @Override
			            public boolean accept(File dir, String name) {
			                return name.toLowerCase().endsWith(".csv");
			            }
			        });

			        if (csvFiles != null) {
			            // 删除所有CSV文件
			            for (File csvFile : csvFiles) {
			            	csvFile.delete();
			            }
			        }
				}
			}
		}


		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		LinkedHashMap<String, String> LinkedHashMap_kuaiji_import = new LinkedHashMap<>();
		// 拡張for文
		for (int j = 0; j < req.getParts().size(); j++) {
			//name属性がfileのファイルをPartオブジェクトとして取得
			Part part = req.getPart("file[" + j + "]");
			//ファイル名を取得
			//String filename=part.getSubmittedFileName();//ie対応が不要な場合
			String fileName = Paths.get(part.getSubmittedFileName()).getFileName().toString();
			String fileExtension = "." +  FuncUtils.getFileExtension(fileName);
			fileName = form_CompanyName_Chinese + "_" + fileName.replace(fileExtension, "")  + "_" + yyyymmddhhmmss + fileExtension;

			String filePath = path + File.separator + fileName;

			//書き込み
			part.write(filePath);

			LinkedHashMap_kuaiji_import.put(fileName, "");
		}



		if ("send-pdf".equals(hidden_key) == false) {
			/*
			 * toFlie+toDB
			 */

			t_kuaijiDao t_kuaijiDao = new t_kuaijiDao();
			File[] files = directory.listFiles();
			if (files == null || files.length == 0) {
				logger.debug("目录下没有文件。");
				return null;
			}



			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					if (LinkedHashMap_kuaiji_import.containsKey(fileName)) {

					} else {
						continue;
					}

					String fileExtension = FuncUtils.getFileExtension(fileName);
					logger.debug(fileName);




					String file_path = file.getPath();
					String kuaiji_type = "";
					if (fileName.toLowerCase().contains(".csv")) {
						kuaiji_type = "t_etax_amazon_csv";
						AmazonCsvFormat.kuaiji_amazon_to_csv(path_kuaiji_output, file);
						String pathNew = path_kuaiji_output + "/計算用" + file.getName();
						kuaiji_amazon_to_DB(yyyymmdd_count, yyyy, pathNew);

					} else if (fileName.toLowerCase().contains(".xls")) {
//						FileInputStream fis = new FileInputStream(file);
//						Workbook workbook = WorkbookFactory.create(fis);
//						Sheet sheet = workbook.getSheetAt(0);
//
//						if ("账本A收入".equals(sheet.getSheetName())) {// || fileName.toLowerCase().contains("账本A收入")
//							kuaiji_type = "t_kuaiji_a";
//							kuaiji_a_to_DB(yyyymmdd_count, yyyy, file_path);
//
//						} else if ("账本BC支出".equals(sheet.getSheetName())) {// || fileName.toLowerCase().contains("账本BC支出")
//							kuaiji_type = "t_kuaiji_bc";
//							kuaiji_bc_to_DB(yyyymmdd_count, yyyy, file_path);
//
//						} else if ("账本D进口".equals(sheet.getSheetName())) {// || fileName.toLowerCase().contains("账本D进口")
//							kuaiji_type = "t_kuaiji_d";
//							kuaiji_d_to_DB(yyyymmdd_count, yyyy, file_path);
//
//						} else {
////							out.print("NG File " + fileName);
////							return null;
//						}



					} else {
//						out.print("NG File " + fileName);
//						return null;
					}

					t_kuaijiDao.delete_where_yyyymmdd_count_and_yyyy_and_kuaiji_type(yyyymmdd_count, yyyy, kuaiji_type);
					t_kuaijiBean t_kuaijiBean = new t_kuaijiBean();
					t_kuaijiBean.setYyyymmdd_count(yyyymmdd_count);
					t_kuaijiBean.setYyyy(yyyy);
					t_kuaijiBean.setKuaiji_type(kuaiji_type);
					t_kuaijiBean.setInput_file(fileName);
					t_kuaijiDao.INSERT(t_kuaijiBean);



				}
			}
		}


		return path;

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



	public static void main(String[] args) {

		try {
			String yyyymmdd_count ="20230801000003";
//			String yyyymmdd_count ="20230526000011";
			String yyyy ="2023";

			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);


			SearchKuaijiLogic SearchKuaijiLogic = new SearchKuaijiLogic();

//			String path_kuaiji = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\kuaiji";
			String path_kuaiji_output = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\kuaiji_output";
			long l = 0;

			String path = "";


//			path = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\kuaiji\\２弥生アマゾン収入インポート用.txt";
//			SearchKuaijiLogic.kuaiji_amazon_to_DB(yyyymmdd_count, yyyy, path);
//			l = SearchKuaijiLogic.kuaiji_amazon_to_file(yyyymmdd_count, yyyy, t_etax_account_infoExBean, path_kuaiji_output, l);

			//
//			path = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\１客向普通収入帳簿.xlsx";
			path = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\1收入的账本.xlsx";
			SearchKuaijiLogic.kuaiji_a_to_DB(yyyymmdd_count, yyyy, path);
			l = SearchKuaijiLogic.kuaiji_a_to_file(yyyymmdd_count, yyyy, t_etax_account_infoExBean, path_kuaiji_output, l);


//			path = "E:\\日本-PANDASERVICE株式会社\\电子申请数据\\弥生会計インポートシステム\\３客向輸入貨物帳簿.xlsx";
//			SearchKuaijiLogic.kuaiji_d_to_DB(yyyymmdd_count, yyyy, path);
//			SearchKuaijiLogic.kuaiji_d_to_file(yyyymmdd_count, yyyy, t_etax_account_infoExBean, path_kuaiji_output, l);


		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			Connection connection = ConnectionDao.connection;
	        if(connection != null){
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
		}
	}

	private static t_kuaiji_bcDao kuaiji_bc_to_DB(String yyyymmdd_count, String yyyy, String path) throws IOException, Exception {
		int rowS = 3;			int rowE = -1;	int columnS = 1; int columnE = 5;	int column_excelDataHashMapKey = -1;
		Map<String, Map<String, String>> excelDataHashMap = FuncUtils.get_excelDataHashMap(path, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);


		t_kuaiji_bcDao t_kuaiji_bcDao = new t_kuaiji_bcDao();
		t_kuaiji_bcDao.delete_where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);
		t_kuaiji_bcDao.INSERT(yyyymmdd_count, yyyy, excelDataHashMap);
		return t_kuaiji_bcDao;
	}


	private void kuaiji_d_to_DB(String yyyymmdd_count, String yyyy, String path) throws IOException, Exception {
		int rowS = 3;			int rowE = -1;	int columnS = 1; int columnE = 5;	int column_excelDataHashMapKey = -1;
		Map<String, Map<String, String>> excelDataHashMap = FuncUtils.get_excelDataHashMap(path, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);


		t_kuaiji_dDao t_kuaiji_dDao = new t_kuaiji_dDao();
		t_kuaiji_dDao.delete_where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);
		t_kuaiji_dDao.INSERT(yyyymmdd_count, yyyy, excelDataHashMap);

	}

	private long kuaiji_d_to_file(String yyyymmdd_count, String yyyy, t_etax_account_infoBean t_etax_account_infoBean, String path_kuaiji_output, long l) throws IOException {
		String path = getServletContext().getRealPath("/kuaiji_moban") +  "/３弥生輸入貨物インポート用.txt";

		t_kuaiji_dDao t_kuaiji_dDao = new t_kuaiji_dDao();
		File dataModelFile = new File(path);
		String fileContent = FuncUtils.readFileContent(dataModelFile);
		StringBuilder fileContentSB = new StringBuilder();

		LinkedHashMap<String, t_kuaiji_dBean> LinkedHashMap_t_kuaiji_dBean = t_kuaiji_dDao.selectAll_where_yyyymmdd_count_by_yyyy(yyyymmdd_count, yyyy);
		Iterator<Map.Entry<String, t_kuaiji_dBean>> iterator = LinkedHashMap_t_kuaiji_dBean.entrySet().iterator();
		while (iterator.hasNext()) {
			++l;
			String fileContentNew = fileContent;
			Map.Entry<String, t_kuaiji_dBean> entry = iterator.next();
			String key = entry.getKey();
			t_kuaiji_dBean t_kuaiji_dBean = entry.getValue();



			/*
"2110",[番号],"","[B]","","","","対象外",0,0,"買掛金","","","対象外",[E+F+G],0,"[C&D 全角32半角64]","","",3,"","","0","0","no"
"2100",[番号],"","[B]","仕入高","","","課対輸本7.8%",[E],0,"","","","対象外",0,0,"輸入貨物価格","","",3,"","","0","0","no"
"2100",[番号],"","[B]","仮払消費税等","","","課対輸税7.8%",[F],0,"","","","対象外",0,0,"輸入消費税国","","",3,"","","0","0","no"
"2101",[番号],"","[B]","仮払消費税等","","","地消貨割2.2%",[G],0,"","","","対象外",0,0,"輸入消費税地方","","",3,"","","0","0","no"

			 */
			fileContentNew = fileContentNew.replace("[番号]", "" + l);

		    String formattedDate = japaneseDateFormatter(t_kuaiji_dBean.getDeclaration_date() + " 0:00:00");
			fileContentNew = fileContentNew.replace("[B]", formattedDate);

			Double value = Double.parseDouble(t_kuaiji_dBean.getDeclared_price_cif())
					+ Double.parseDouble(t_kuaiji_dBean.getConsumption_tax_national())
					+ Double.parseDouble(t_kuaiji_dBean.getConsumption_tax_local());
			String valueString = String.format("%.0f", value);
			fileContentNew = fileContentNew.replace("[E+F+G]", valueString);

			String originalString = t_kuaiji_dBean.getDeclaration_number() +  t_kuaiji_dBean.getProduct_name() ;
		    String substring = originalString.substring(0, Math.min(originalString.length(), 32));
			fileContentNew = fileContentNew.replace("[C&D 全角32半角64]", "" + substring);

			valueString = String.format("%.0f", Double.parseDouble(t_kuaiji_dBean.getDeclared_price_cif()));
			fileContentNew = fileContentNew.replace("[E]", valueString);

			valueString = String.format("%.0f", Double.parseDouble(t_kuaiji_dBean.getConsumption_tax_national()));
			fileContentNew = fileContentNew.replace("[F]", valueString);

			valueString = String.format("%.0f",Double.parseDouble( t_kuaiji_dBean.getConsumption_tax_local()));
			fileContentNew = fileContentNew.replace("[G]", valueString);


			fileContentSB.append(fileContentNew);
			fileContentSB.append("\r\n");
			// 处理 key 和 value
		}

		// 写入文件

		String path_outputNew = path_kuaiji_output + "/" + t_etax_account_infoBean.getCompanyName_English() + "_" + dataModelFile.getName();
		FileWriter writer = new FileWriter(path_outputNew, Charset.forName("SJIS"));
		writer.write(fileContentSB.toString());
		writer.close();

//		logger.debug("\r\n" + fileContentSB.toString());
		logger.debug("File saved: " + path_outputNew);
		return l;
	}

	private long kuaiji_bc_to_file(String yyyymmdd_count, String yyyy, t_etax_account_infoBean t_etax_account_infoBean, String path_kuaiji_output, long l) throws IOException {
		String path = getServletContext().getRealPath("/kuaiji_moban") +  "/４弥生普通支出インポート用　適格　非適格.txt";

		t_kuaiji_bcDao t_kuaiji_bcDao = new t_kuaiji_bcDao();
		File dataModelFile = new File(path);
		String fileContent = FuncUtils.readFileContent(dataModelFile);
		StringBuilder fileContentSB = new StringBuilder();

		LinkedHashMap<String, t_kuaiji_bcBean> LinkedHashMap_t_kuaiji_bcBean = t_kuaiji_bcDao.selectAll_where_yyyymmdd_count_by_yyyy(yyyymmdd_count, yyyy);
		Iterator<Map.Entry<String, t_kuaiji_bcBean>> iterator = LinkedHashMap_t_kuaiji_bcBean.entrySet().iterator();
		while (iterator.hasNext()) {
			++l;
			String fileContentNew = fileContent;
			Map.Entry<String, t_kuaiji_bcBean> entry = iterator.next();
			String key = entry.getKey();
			t_kuaiji_bcBean t_kuaiji_bcBean = entry.getValue();



			/*
"2110",[番号],"","[B日付]","","","","対象外",0,0,"未払金","","","対象外",[F+G],0,"[E　全角32半角64]","","",3,"","","0","0","no"
"2100",[番号],"","[B日付]","支払手数料","","","課対仕入別10%適格",[F],0,"","","","対象外",0,0,"['T' &D& '税抜金額']","","",3,"","","0","0","no"
"2101",[番号],"","[B日付]","仮払消費税等","","","課対仕入別10%適格",[G],0,"","","","対象外",0,0,"['T' &D& '消費税']","","",3,"","","0","0","no"


"2110",[番号],"","[B日付]","","","","対象外",0,0,"未払金","","","対象外",[F+G],0,"[E　全角32半角64]","","",3,"","","0","0","no"
"2100",[番号],"","[B日付]","支払手数料","","","課対仕入別10%区分80%",[F],0,"","","","対象外",0,0,"['T' &D& '税抜金額']","","",3,"","","0","0","no"
"2101",[番号],"","[B日付]","仮払消費税等","","","課対仕入別10%区分80%",[G],0,"","","","対象外",0,0,"['T' &D& '消費税']","","",3,"","","0","0","no"

getInvoice_date
getIssuer
getConsumption_tax_number
getProduct_or_service_description
getExclusive_tax_amount
getConsumption_tax_amount

			 */
			fileContentNew = fileContentNew.replace("[番号]", "" + l);

		    String formattedDate = japaneseDateFormatter(t_kuaiji_bcBean.getInvoice_date() + " 0:00:00");
			fileContentNew = fileContentNew.replace("[B日付]", formattedDate);

			Double value = Double.parseDouble(t_kuaiji_bcBean.getTotal_amount_with_tax());
			String valueString = String.format("%.0f", value);
			fileContentNew = fileContentNew.replace("[F+G]", valueString);

			String originalString = t_kuaiji_bcBean.getProduct_or_service_description() ;
		    String substring = originalString.substring(0, Math.min(originalString.length(), 32));
			fileContentNew = fileContentNew.replace("[E　全角32半角64]", "" + substring);

			valueString = String.format("%.0f", Double.parseDouble(t_kuaiji_bcBean.getTotal_amount_with_tax())/1.1);
			fileContentNew = fileContentNew.replace("[F]", valueString);

			valueString = String.format("%.0f", Double.parseDouble(t_kuaiji_bcBean.getTotal_amount_with_tax())/1.1*0.1);
			fileContentNew = fileContentNew.replace("[G]", valueString);


			originalString = t_kuaiji_bcBean.getConsumption_tax_number();
			if (!StringUtils.isEmpty(originalString)) {
				originalString = "T" + originalString;
			}
			originalString = originalString + "税抜金額";
			fileContentNew = fileContentNew.replace("['T' &D& '税抜金額']", "" + originalString);
			originalString = t_kuaiji_bcBean.getConsumption_tax_number();
			if (!StringUtils.isEmpty(originalString)) {
				originalString = "T" + originalString;
			}
			originalString = originalString + "消費税";
			fileContentNew = fileContentNew.replace("\"['T' &D& '消費税']", "" + originalString);


			fileContentSB.append(fileContentNew);
			fileContentSB.append("\r\n");
			// 处理 key 和 value
		}

		// 写入文件

		String path_outputNew = path_kuaiji_output + "/" + t_etax_account_infoBean.getCompanyName_English() + "_" + dataModelFile.getName();
		FileWriter writer = new FileWriter(path_outputNew, Charset.forName("SJIS"));
		writer.write(fileContentSB.toString());
		writer.close();

//		logger.debug("\r\n" + fileContentSB.toString());
		logger.debug("File saved: " + path_outputNew);
		return l;
	}


	private void kuaiji_amazon_to_DB(String yyyymmdd_count, String yyyy, String path) throws Exception {

			t_etax_amazon_csvDao t_etax_amazon_csvDao = new t_etax_amazon_csvDao();

			File file = new File(path);

			int fileLineCounter = FileLineCounter.countLines(file);
			logger.debug("Lines: " + fileLineCounter + ", File: " + file.getName());

			if (fileLineCounter > 2) {
				t_etax_amazon_csvDao.delete_yyyy_where_yyyymmdd_count(yyyy, yyyymmdd_count);
				t_etax_amazon_csvDao.INSERT(yyyymmdd_count, yyyy, file.getPath(), fileLineCounter);

			}

	}

	private void kuaiji_a_to_DB(String yyyymmdd_count, String yyyy, String path) throws IOException, Exception {
		t_kuaiji_aDao t_kuaiji_aDao = new t_kuaiji_aDao();
		int rowS = 3;			int rowE = -1;	int columnS = 1; int columnE = 4;	int column_excelDataHashMapKey = -1;
		Map<String, Map<String, String>> excelDataHashMap = FuncUtils.get_excelDataHashMap(path, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);


		t_kuaiji_aDao.delete_where_yyyymmdd_count_and_yyyy(yyyymmdd_count, yyyy);
		t_kuaiji_aDao.INSERT(yyyymmdd_count, yyyy, excelDataHashMap);
	}

	private long kuaiji_a_to_file(String yyyymmdd_count, String yyyy, t_etax_account_infoBean t_etax_account_infoBean, String path_kuaiji_output, long l) throws IOException {
		String path = getServletContext().getRealPath("/kuaiji_moban") +  "/１弥生普通収入インポート用.txt";

		t_kuaiji_aDao t_kuaiji_aDao = new t_kuaiji_aDao();

		File dataModelFile = new File(path);
		String fileContent = FuncUtils.readFileContent(dataModelFile);
		StringBuilder fileContentSB = new StringBuilder();

		LinkedHashMap<String, t_kuaiji_aBean> LinkedHashMap_t_kuaiji_aBean = t_kuaiji_aDao.selectAll_where_yyyymmdd_count_by_yyyy(yyyymmdd_count, yyyy);
		Iterator<Map.Entry<String, t_kuaiji_aBean>> iterator = LinkedHashMap_t_kuaiji_aBean.entrySet().iterator();
		while (iterator.hasNext()) {
			++l;
			String fileContentNew = fileContent;
			Map.Entry<String, t_kuaiji_aBean> entry = iterator.next();
			String key = entry.getKey();
			t_kuaiji_aBean t_kuaiji_aBean = entry.getValue();



			/*
"2110",[A+100000],"","[B]","売掛金","","","対象外",[E+F],0,"売上高","","","課税売上別10%",[E],0,"[C&D 上限32全角/64半角]","","",3,"","","0","0","no"
"2101",[A+100000],"","[B]","","","","対象外",0,0,"仮受消費税等","","","課税売上別10%",[F],0,"[消費税&C&D 上限32全角/64半角]","","",3,"","","0","0","no"

			 */
			fileContentNew = fileContentNew.replace("[A+100000]", "" + l);

		    String formattedDate = japaneseDateFormatter(t_kuaiji_aBean.getTransaction_date() + " 0:00:00");
			fileContentNew = fileContentNew.replace("[B]", formattedDate);

			Double value = Double.parseDouble(t_kuaiji_aBean.getTotal_amount_with_tax());
			fileContentNew = fileContentNew.replace("[E+F]", "" + (int) Double.parseDouble(String.valueOf(value)));

			fileContentNew = fileContentNew.replace("[E]", "" + (int) (Double.parseDouble(t_kuaiji_aBean.getTotal_amount_with_tax())/1.1));


			String originalString = t_kuaiji_aBean.getCustomer() +  t_kuaiji_aBean.getProduct() ;
		    String substring = originalString.substring(0, Math.min(originalString.length(), 32));
		    //TODO 全角怎么办
			fileContentNew = fileContentNew.replace("[C&D 上限32全角/64半角]", "" + substring);

			fileContentNew = fileContentNew.replace("[F]", "" + (int) (Double.parseDouble(t_kuaiji_aBean.getTotal_amount_with_tax())/1.1*0.1));

			originalString = "消費税"+t_kuaiji_aBean.getCustomer() +  t_kuaiji_aBean.getProduct() ;
		    substring = originalString.substring(0, Math.min(originalString.length(), 32));
			//TODO 全角怎么办
			fileContentNew = fileContentNew.replace("[消費税&C&D 上限32全角/64半角]", "" + substring);

			fileContentSB.append(fileContentNew);
			fileContentSB.append("\r\n");
			// 处理 key 和 value
		}

		// 写入文件

		String path_output_amazonNew = path_kuaiji_output + "/" + t_etax_account_infoBean.getCompanyName_English() + "_" + dataModelFile.getName();
		FileWriter writer = new FileWriter(path_output_amazonNew, Charset.forName("SJIS"));
		writer.write(fileContentSB.toString());
		writer.close();

//			logger.debug("\r\n" + fileContentSB.toString());
		logger.debug("File saved: " + path_output_amazonNew);
		return l;
	}



	private long kuaiji_amazon_to_file(String yyyymmdd_count, String yyyy,
			t_etax_account_infoBean t_etax_account_infoBean, String path_kuaiji_output,long l) throws IOException {
		t_etax_amazon_csvDao t_etax_amazon_csvDao = new t_etax_amazon_csvDao();
		String path = getServletContext().getRealPath("/kuaiji_moban") +  "/２弥生アマゾン収入インポート用.txt";

		File dataModelFile = new File(path);
		String fileContent = FuncUtils.readFileContent(dataModelFile);
		StringBuilder fileContentSB = new StringBuilder();

		LinkedHashMap<String, t_etax_amazon_csvBean> LinkedHashMap_t_etax_amazon_csvBean = t_etax_amazon_csvDao.selectAll_where_yyyymmdd_count_by_yyyy(yyyymmdd_count, yyyy);
		Iterator<Map.Entry<String, t_etax_amazon_csvBean>> iterator = LinkedHashMap_t_etax_amazon_csvBean.entrySet().iterator();
		while (iterator.hasNext()) {
			++l;
			String fileContentNew = fileContent;
			Map.Entry<String, t_etax_amazon_csvBean> entry = iterator.next();
			String key = entry.getKey();
			t_etax_amazon_csvBean t_etax_amazon_csvBean = entry.getValue();

			/*

	`UPDATE_DATE` timestamp(6) NOT NULL,
	`yyyymmdd_count` bigint NOT NULL,
1	A 	`getTransaction_datetime` datetime NOT NULL COMMENT '日付/時間',
2	B 	`getSettlement_number` varchar(255) NOT NULL COMMENT '決済番号',
3	C 	`getTransaction_type` varchar(255) NOT NULL COMMENT 'トランザクションの種類',
4	D 	`getOrder_number` varchar(255) NOT NULL COMMENT '注文番号',
5	E 	`getSku` varchar(255) NOT NULL COMMENT 'SKU',
6	F 	`getDescription` varchar(255) NOT NULL COMMENT '説明',
7	G 	`getQuantity` int NOT NULL COMMENT '数量',
8	H 	`getAmazon_listing_service` varchar(255) DEFAULT NULL COMMENT 'Amazon 出品サービス',
9	I 	`getFulfillment` varchar(255) DEFAULT NULL COMMENT 'フルフィルメント',
10	J 	`getCity` varchar(255) DEFAULT NULL COMMENT '市町村',
11	K 	`getPrefecture` varchar(255) DEFAULT NULL COMMENT '都道府県',
12	L 	`getPostal_code` varchar(255) DEFAULT NULL COMMENT '郵便番号',
13	M 	`getTax_collection_type` varchar(255) DEFAULT NULL COMMENT '税金徴収型',
14	N 	`getProduct_sales` decimal(10,2) NOT NULL COMMENT '商品売上',
15	O 	`getProduct_sales_tax` decimal(10,2) DEFAULT NULL COMMENT '商品の売上税',
16	P 	`getShipping_fee` decimal(10,2) DEFAULT NULL COMMENT '配送料',
17	Q 	`getShipping_tax` decimal(10,2) DEFAULT NULL COMMENT '配送料の税金',
18	R 	`getGift_wrapping_fee` decimal(10,2) DEFAULT NULL COMMENT 'ギフト包装手数料',
19	S 	`getGift_wrapping_tax` decimal(10,2) DEFAULT NULL COMMENT 'ギフト包装クレジットの税金',
20	T 	`getAmazon_point_fee` decimal(10,2) DEFAULT NULL COMMENT 'Amazonポイントの費用',
21	U 	`getPromotion_discount` decimal(10,2) DEFAULT NULL COMMENT 'プロモーション割引額',
22	V 	`getPromotion_discount_tax` decimal(10,2) DEFAULT NULL COMMENT 'プロモーション割引の税金',
23	W 	`getMarketplace_withholding_tax` decimal(10,2) DEFAULT NULL COMMENT '源泉徴収税を伴うマーケットプレイス',
24	X 	`getCommission` decimal(10,2) DEFAULT NULL COMMENT '手数料',
25	Y 	`getFba_fee` decimal(10,2) DEFAULT NULL COMMENT 'FBA 手数料',
26	Z 	`getOther_transaction_fees` decimal(10,2) DEFAULT NULL COMMENT 'トランザクションに関するその他の手数料',
27	AA	`getOther` varchar(255) DEFAULT NULL COMMENT 'その他',
28	AB	`getTotal_amount` decimal(10,2) DEFAULT NULL COMMENT '合計',


			 */

			/*
"2110",[番号],"","[日付]","売掛金","","","対象外",[N+O+P+Q+R+S+T+U+V],0,"","","","対象外",0,0,"[F　半角64以内]","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"売上高","","","課税売上別10%",[N],0,"商品売上","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"仮受消費税等","","","課税売上別10%",[O],0,"商品の売上税","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"売上高","","","課税売上別10%",[P],0,"配送料","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"仮受消費税等","","","課税売上別10%",[Q],0,"配送料の税金","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"売上高","","","課税売上別10%",[R],0,"ギフト包装手数料","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","","","","対象外",0,0,"仮受消費税等","","","課税売上別10%",[S],0,"ギフト包装クレジットの税金","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","雑損失","","","対外仕入",[-T],0,"","","","対象外",0,0,"Amazonポイントの費用","","",3,"","","0","0","no"
"2100",[番号],"","[日付]","売上高","","","課税売上別10%",[-U],0,"","","","対象外",0,0,"プロモーション割引額","","",3,"","","0","0","no"
"2101",[番号],"","[日付]","仮受消費税等","","","課税売上別10%",[-V],0,"","","","対象外",0,0,"プロモーション割引の税金","","",3,"","","0","0","no"

			 */
			fileContentNew = fileContentNew.replace("[番号]", "" + l);

		    String formattedDate = japaneseDateFormatter(t_etax_amazon_csvBean.getTransaction_datetime());
			fileContentNew = fileContentNew.replace("[日付]", formattedDate);

			Double value = Double.parseDouble(t_etax_amazon_csvBean.getProduct_sales())
					+Double.parseDouble(t_etax_amazon_csvBean.getProduct_sales_tax())
					+Double.parseDouble(t_etax_amazon_csvBean.getShipping_fee())
					+Double.parseDouble(t_etax_amazon_csvBean.getShipping_tax())
					+Double.parseDouble(t_etax_amazon_csvBean.getGift_wrapping_fee())
					+Double.parseDouble(t_etax_amazon_csvBean.getGift_wrapping_tax())
					+Double.parseDouble(t_etax_amazon_csvBean.getAmazon_point_fee())
					+Double.parseDouble(t_etax_amazon_csvBean.getPromotion_discount())
					+Double.parseDouble(t_etax_amazon_csvBean.getPromotion_discount_tax())
					;
			fileContentNew = fileContentNew.replace("[N+O+P+Q+R+S+T+U+V]", "" + (int) Double.parseDouble(String.valueOf(value)));

			String originalString = t_etax_amazon_csvBean.getDescription();
		    String substring = originalString.substring(0, Math.min(originalString.length(), 32));
		    //TODO 全角怎么办
			fileContentNew = fileContentNew.replace("[F　半角64以内]", "" + substring);

			fileContentNew = fileContentNew.replace("[N]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getProduct_sales()));
			fileContentNew = fileContentNew.replace("[O]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getProduct_sales_tax()));
			fileContentNew = fileContentNew.replace("[P]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getShipping_fee()));
			fileContentNew = fileContentNew.replace("[Q]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getShipping_tax()));
			fileContentNew = fileContentNew.replace("[R]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getGift_wrapping_fee()));
			fileContentNew = fileContentNew.replace("[S]", "" + (int) Double.parseDouble(t_etax_amazon_csvBean.getGift_wrapping_tax()));
			fileContentNew = fileContentNew.replace("[-T]", "" + (int) -Double.parseDouble(t_etax_amazon_csvBean.getAmazon_point_fee()));
			fileContentNew = fileContentNew.replace("[-U]", "" + (int) -Double.parseDouble(t_etax_amazon_csvBean.getPromotion_discount()));
			fileContentNew = fileContentNew.replace("[-V]", "" + (int) -Double.parseDouble(t_etax_amazon_csvBean.getPromotion_discount_tax()));


			fileContentSB.append(fileContentNew);
			fileContentSB.append("\r\n");
			// 处理 key 和 value
		}

		// 写入文件

		String path_output_amazonNew = path_kuaiji_output + "/" + t_etax_account_infoBean.getCompanyName_English() + "_" + dataModelFile.getName();
		FileWriter writer = new FileWriter(path_output_amazonNew, Charset.forName("SJIS"));
		writer.write(fileContentSB.toString());
		writer.close();

//		logger.debug("\r\n" + fileContentSB.toString());
		logger.debug("File saved: " + path_output_amazonNew);
		return l;
	}

	private static String japaneseDateFormatter(String dateString) {
		// 陽暦の文字列をLocalDateに変換
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss");
		LocalDate gregorianDate = LocalDate.parse(dateString, formatter);
		// LocalDateをJapaneseDateに変換
		JapaneseDate japaneseDate = JapaneseDate.from(gregorianDate);
		// 和暦を表示
		DateTimeFormatter japaneseFormatter = DateTimeFormatter.ofPattern("G yy/MM/dd");
		String formattedDate = japaneseDate.format(japaneseFormatter);

		FuncUtils FuncUtils = new FuncUtils();
		formattedDate = FuncUtils.getHashMapWarekiEnglish().get(formattedDate.split(" ")[0]) + "." + formattedDate.split(" ")[1];
		return formattedDate;
	}


}