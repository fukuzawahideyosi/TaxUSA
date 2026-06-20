package com.panda.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipOutputStream;

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
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_jct_shenqingBean;
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_sequenceDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_jct_shenqingDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;

@WebServlet("/SetUserInfoLogic2")
@MultipartConfig
public class SetUserInfoLogic2 extends HttpServlet {

	private static Logger logger = Logger.getLogger(SetUserInfoLogic2.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		String yyyy = "2025";

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");


		String hidden_key = req.getParameter("hidden_key");
		String hidden_value = req.getParameter("hidden_value");
		String INSQ = req.getParameter("INSQ");

		SimpleDateFormat SimpleDateFormat = new SimpleDateFormat("yyyy");
		String tianxie_YYYY = SimpleDateFormat.format(new Date());

		/*
		 * 激活码处理
		 */
		String activation_code = req.getParameter("activation_code");
		logger.debug("activation_code " + activation_code);

		if (!StringUtils.isEmpty(activation_code)) {
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.selectByActivation_code(activation_code);
			yyyymmdd_count = EtaxAccountInfoBean.getYyyymmdd_count();

			if (StringUtils.isEmpty(yyyymmdd_count)) {
				out.print("激活码无效，请联系客服！给您造成的不便，深感抱歉。");

			} else {

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				EtaxDao EtaxDao = new EtaxDao();
				try {
					EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
							t_etax_account_resDao, EtaxDao, "");
					t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

				} catch (SQLException e) {
					t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

				}

				User_infoDao LicenseDao = new User_infoDao();
				User_infoBean User_infoBean = new User_infoBean();
				User_infoBean = LicenseDao.selectByTiaojian("user_id", EtaxAccountInfoBean.getUser_id());
				EtaxAccountInfoBean.setEmail(User_infoBean.getEmail());
				//登录信息发邮件给客户
				FuncUtils.sendMail_activation_code(EtaxAccountInfoBean);

				out.print("尊敬的【"+EtaxAccountInfoBean.getCompanyName_Chinese()+"】，您已成功激活。我们将为您申请日本消费税税号，请耐心等待，如有进展，系统将自动为您发送邮件。");
			}
			logger.debug("end activation_code");
			return;

		}




		/*
		 * 邀请码有效性验证
		 */
		String yaoqing_no = req.getParameter("yaoqing_no");
		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();
		if (StringUtils.isEmpty(yaoqing_no)) {

		} else {
			User_infoBean = LicenseDao.selectByTiaojian("yaoqing_no", yaoqing_no);
			if (StringUtils.isEmpty(User_infoBean.getYaoqing_no())) {
				out.print("邀请码【"+yaoqing_no+"】无效，请联系客服！给您造成的不便，深感抱歉。");
				return;

			} else {
				session.setAttribute("yaoqing_no", yaoqing_no);
				session.setAttribute("User_infoBean", User_infoBean);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yaoqing_no, " + yaoqing_no);
				logger.debug("yaoqing_no -> ok");

			}
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
				logger.debug("PandaServiceTools → License invalid");
				out.write("PandaServiceMA → License invalid");
				logger.info("end " + hidden_key);
				return;
			}
		}


//		if (StringUtils.isEmpty(yyyymmdd_count)) {
//			m_sequenceDao m_sequenceDao = new m_sequenceDao();
//			yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
//		}

		/*
		 * 登录功能 附件
		 */

		if (StringUtils.isEmpty(yyyymmdd_count) && "SetUserInfoLogic2".equals(hidden_key)) {

			//アップロードするフォルダ
			String path = getServletContext().getRealPath("/fileData");

			try {

				yyyymmdd_count = FuncUtils.filesUp(req, path);

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


//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
//			out.print(yyyymmdd_count);
//			logger.info("end " + hidden_key);
//			return;
		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "FilesJieguoShangchuan".equals(hidden_key)) {

				try {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoBean EtaxAccountInfoBean = t_etax_account_infoDao.select(yyyymmdd_count);

					String form_CompanyName_Chinese = EtaxAccountInfoBean.getCompanyName_Chinese();
					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
					form_CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_Chinese);

					//アップロードするフォルダ
					String path = getServletContext().getRealPath("/fileDataJieguo");
					String falieName = yyyymmdd_count + "_" + form_CompanyName_Chinese;
					path = path + "/" + falieName;

					File directory = new File(path);

					if (directory.exists()) {
						FuncUtils.deleteFolder(directory);
					}

					//mkdir
					boolean hasSucceeded = directory.mkdir();
					logger.info("创建文件夹结果（不含父文件夹）：" + hasSucceeded);

					req.getParts();
					req.setCharacterEncoding("utf-8");

					// 拡張for文
					for (int j = 0; j < req.getParts().size(); j++) {
						//name属性がfileのファイルをPartオブジェクトとして取得
						Part part = req.getPart("file[" + j + "]");
						//ファイル名を取得
						//String filename=part.getSubmittedFileName();//ie対応が不要な場合
						String filename = yyyymmdd_count + "_";
						filename = filename + Paths.get(part.getSubmittedFileName()).getFileName().toString();

						//書き込み
						part.write(path + File.separator + filename);

						String fe = FilenameUtils.getExtension(filename);

						if ("pdf".equals(fe)) {
							byte[] cert = Files.readAllBytes(Paths.get(path + File.separator + filename));

						}

					}

//
//
//					directory = new File(path);
//			        if (directory.exists()) {
//
//			        	/*
//						 * 生成文件ZIP
//						 */
//						// 源文件夹的路径
//						String sourceFolderPath = path;
//						// 目标ZIP文件的路径
//						String targetZipFilePath = path + ".zip";
//						try {
//							// 创建一个输出流，将文件写入ZIP文件
//							FileOutputStream fos = new FileOutputStream(targetZipFilePath);
//							ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//							// 调用递归方法将文件夹及其内容添加到ZIP文件中
//							FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);
//
//							// 关闭ZIP文件输出流
//							zipOut.close();
//							fos.close();
//
//							logger.info("ZIP文件创建成功：" + targetZipFilePath);
//
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//
//			        }

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.UpdateKeyValue(yyyymmdd_count, "output_file_jieguo", falieName);

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


				//			out.print("{\"yyyymmdd_count\":" + yyyymmdd_count + "}");
				out.print(yyyymmdd_count);
				return;

			} else if ("etaxonly".equals(hidden_key)) {


				// 获取当前日期时间
				Date currentDate = new Date();
				// 设置日期时间格式
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				// 格式化日期时间
				String yyyymmddhhmmss = dateFormat.format(currentDate);

				//アップロードするフォルダ
				String path = getServletContext().getRealPath("/fileDataEtaxonly");

				/*
				 * license確認
				 */
				// 不要

				/*
				 * 登录功能 附件
				 */
				try {
					FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
				} catch (Throwable e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				/*
				 * 获取上传的多个文件部分
				 */
				Map<String, String> excelDataTitle = new LinkedHashMap<>();
				Map<String, String> excelData = new LinkedHashMap<>();
				Map<String, Map<String, String>> excelDataHashMap = new LinkedHashMap<>();
				File directory = new File(path + "/" + yyyymmddhhmmss);

				if (!directory.exists() || !directory.isDirectory()) {
					logger.info("指定的路径不是一个有效的目录。");
					return;
				}

				File[] files = directory.listFiles();

				if (files == null || files.length == 0) {
					logger.info("目录下没有文件。");
					return;
				}

				int count = 0;
				for (File file : files) {
					if (file.isFile()) {
						String fileName = file.getName();
						String fileExtension = FuncUtils.getFileExtension(fileName);

						try {
							if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
								FileInputStream fis = new FileInputStream(file);
								Workbook workbook = WorkbookFactory.create(fis);
								Sheet sheet = workbook.getSheetAt(0);
								// 遍历每一行
								Iterator<Row> rowIterator = sheet.iterator();
								while (rowIterator.hasNext()) {
									Row row = rowIterator.next();

//									// 跳过第1行（假设第1行为标题）
									if (row.getRowNum() < 1) {
										continue;
									}

									for (int i = 0; i <= 6; i++) {
										// 获取 N 列的数据
										Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
										// 将 A 列和 B 列的数据存储到 excelData HashMap
										String key = (cell != null) ? cell.toString() : "";
										String value = "";

										if (cell != null) {
											if (cell.getCellType() == CellType.FORMULA) {
												// 如果单元格中包含公式，则计算并输出结果
												value = FuncUtils.evaluateFormulaCell(cell, workbook);
											} else {
												// 如果是其他类型的单元格，直接输出值
												value = cell.toString();
											}

										}

										excelData.put("" + i, value);

									}
									if (StringUtils.isEmpty(excelData.get("1")) || StringUtils.isEmpty(excelData.get("3")) || StringUtils.isEmpty(excelData.get("5"))) {
										continue;
									}
									excelDataHashMap.put("" + (++count), excelData);
									excelData = new HashMap<>();

								}

							}
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}

				/*
				 * 登録データ準備
				 */
				for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap.entrySet()) {
					Map<String, String> excelValue = entry_excelDataHashMap.getValue();

					String CompanyName_English = excelValue.get("1");
					String CompanyName_Chinese = excelValue.get("2");
					String address_English = excelValue.get("3");
					String address_Chinese = excelValue.get("4");
					String DaibiaoName_English = excelValue.get("5");
					String DaibiaoName_Chinese = excelValue.get("6");


					if (StringUtils.isEmpty(CompanyName_English) || StringUtils.isEmpty(address_English) || StringUtils.isEmpty(DaibiaoName_English)) {
						continue;
					}


					String hojinmeiKana = "";
					String HoujinBangou = "";
					String InvoiceBangou = "";

					try {

						t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetHoujinBangouByHoujinName(FuncUtils.toFullWidth(CompanyName_English));
						HoujinBangou = t_etax_account_infoExBean.getHoujinBangou();
						hojinmeiKana = t_etax_account_infoExBean.getCompanyName_pianjiaming();
						if (StringUtils.isEmpty(t_etax_account_infoExBean.getAddress_English())) {
							// 何もしない
						} else {
							address_English = t_etax_account_infoExBean.getAddress_English();

						}

					} catch (Exception e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
						return;
					}


					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
					CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
					CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
					address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
					address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
					DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);
					DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);




					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
					t_etax_account_infoBean.setUser_id("etaxonly");
					t_etax_account_infoBean.setUser_type("公司");

					t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
					t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
					t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
					t_etax_account_infoBean.setDaibiaoName_English(DaibiaoName_English);
					t_etax_account_infoBean.setAddress_Chinese(address_Chinese);
					t_etax_account_infoBean.setAddress_English(address_English);

					t_etax_account_infoBean.setYaoqing_no(yaoqing_no);

					t_etax_account_infoBean.setSyouninn_status("待处理");//承認無

					UUID uuid = UUID.randomUUID();
					t_etax_account_infoBean.setActivation_code("etaxonly-" + uuid.toString());


		            FuncUtils FuncUtils = new FuncUtils();
		    		String CompanyName_pianjiaming = "";
					if (StringUtils.isEmpty(hojinmeiKana)) {
						CompanyName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getCompanyName_Chinese());
					} else {
						CompanyName_pianjiaming = hojinmeiKana;

					}

		    		String address_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getAddress_Chinese());
		    		String DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(t_etax_account_infoBean.getDaibiaoName_Chinese());

		    		t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
		    		t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
		    		t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);




			        int digits = 8; // 需要生成的位数
			        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
			        t_etax_account_infoBean.setEtax_pw(etax_pw);

			        t_etax_account_infoBean.setEtax_pw_flag("0");


					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_pianjiaming, " + CompanyName_pianjiaming);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_pianjiaming, " + address_pianjiaming);
					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_pianjiaming, " + DaibiaoName_pianjiaming);


					/*
					 * 登録
					 */
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

					try {
						t_etax_account_infoDao.INSERT(t_etax_account_infoBean);

					} catch (Exception e) {
						// TODO 登录失败怎么办
						e.printStackTrace();
						return;
					}

					/*
					 * 激活
					 */
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					EtaxDao EtaxDao = new EtaxDao();
					try {


						t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
								t_etax_account_resDao, EtaxDao, hojinmeiKana);
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");

					} catch (SQLException e) {
						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");

					} catch (Exception e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						return;
					}


					//HoujinBangou
					if (StringUtils.isEmpty(HoujinBangou)) {
						// 何もしない
					} else {
						logger.info("yyyymmdd_count: " + yyyymmdd_count + ", HoujinBangou, " + HoujinBangou);
						try {
							t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, HoujinBangou);
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return;
						}

					}

					//InvoiceBangou
					t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou("T" + HoujinBangou);
					InvoiceBangou = t_etax_account_infoExBean.getInvoiceBangou();
					if (StringUtils.isEmpty(InvoiceBangou)) {
						// 何もしない
					} else {
						logger.info("yyyymmdd_count: " + yyyymmdd_count + ", InvoiceBangou, " + InvoiceBangou);
						try {
							t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, InvoiceBangou);
						} catch (SQLException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
							return;
						}

					}




				}

				logger.info("end");
				return;




//			} else if ("etaxonly".equals(hidden_key)) {
//
//				// 获取当前日期时间
//				Date currentDate = new Date();
//				// 设置日期时间格式
//				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
//				// 格式化日期时间
//				String yyyymmddhhmmss = dateFormat.format(currentDate);
//
//				//アップロードするフォルダ
//				String path = getServletContext().getRealPath("/fileDataEtaxonly");
//
//				/*
//				 * license確認
//				 */
//				// 不要
//
//				/*
//				 * 登录功能 附件
//				 */
//				try {
//					FuncUtils.filesUp_yyyymmddhhmmss(req, path, yyyymmddhhmmss);
//				} catch (Throwable e) {
//					// TODO 自動生成された catch ブロック
//					e.printStackTrace();
//				}
//
//				/*
//				 * 获取上传的多个文件部分
//				 */
//				Map<String, String> excelDataTitle = new LinkedHashMap<>();
//				Map<String, String> excelData = new LinkedHashMap<>();
//				Map<String, Map<String, String>> excelDataHashMap = new LinkedHashMap<>();
//				File directory = new File(path + "/" + yyyymmddhhmmss);
//
//				if (!directory.exists() || !directory.isDirectory()) {
//					logger.info("指定的路径不是一个有效的目录。");
//					return;
//				}
//
//				File[] files = directory.listFiles();
//
//				if (files == null || files.length == 0) {
//					logger.info("目录下没有文件。");
//					return;
//				}
//
//				int count = 0;
//				for (File file : files) {
//					if (file.isFile()) {
//						String fileName = file.getName();
//						String fileExtension = FuncUtils.getFileExtension(fileName);
//
//						try {
//							if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
//								FileInputStream fis = new FileInputStream(file);
//								Workbook workbook = WorkbookFactory.create(fis);
//								Sheet sheet = workbook.getSheetAt(0);
//								// 遍历每一行
//								Iterator<Row> rowIterator = sheet.iterator();
//								while (rowIterator.hasNext()) {
//									Row row = rowIterator.next();
//
//									// 跳过第2行（假设第1,2行为标题）
//									if (row.getRowNum() < 3) {
//										continue;
//									}
//
//									for (int i = 0; i <= 32; i++) {
//										// 获取 N 列的数据
//										Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
//										// 将 A 列和 B 列的数据存储到 excelData HashMap
//										String key = (cell != null) ? cell.toString() : "";
//										String value = "";
//
//										if (cell != null) {
//											if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
//												// 如果单元格中包含公式，则计算并输出结果
//												value = FuncUtils.evaluateFormulaCell(cell, workbook);
//											} else {
//												// 如果是其他类型的单元格，直接输出值
//												value = cell.toString();
//											}
//
//										}
//
//
//
//										if (row.getRowNum() == 2) {
//											excelDataTitle.put("" + i, value.replaceAll("\n", ""));
//
//										} else {
//											excelData.put("" + i, value);
//
//										}
//
//
//									}
//									if (StringUtils.isEmpty(excelData.get("4"))) {
//										break;
//									}
//									excelDataHashMap.put("" + (++count), excelData);
//									excelData = new HashMap<>();
//
//								}
//
//							}
//						} catch (Throwable e) {
//							e.printStackTrace();
//						}
//					}
//				}
//
//				/*
//				 * 登録データ準備
//				 */
//				for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap.entrySet()) {
//					Map<String, String> excelValue = entry_excelDataHashMap.getValue();
////						0		"法人番号（書）（非）"
////						1		"組織前後（選）（非）"
////						2		"組織名称（選）（非）"
////						3		"法人名称フリガナ（書）（必）【自動】"
////						4	CompanyName_English	"法人名称（書）（必）"
//					String CompanyName_Chinese = excelValue.get("4");
//					String CompanyName_English = excelValue.get("5");
////						5		"納税地郵便番号前三（書）（非）"
////						6		"納税地郵便番号後四（書）（非）"
////						7		"納税地都道府県（選）（必）"
////						8		"納税地市区町村（書）（必）"
////						9		"納税地丁目番地等（書）（必）"
////						10		"納税地建物名・号室（書）（非）"
////						11		"納税地電話番号前三（書）（非）"
////						12		"納税地電話番号中四（書）（非）"
////						13		"納税地電話番号後四（書）（非）"
////						14		"提出先税務署都道府県（選）（必）"
////						15		"提出先税務署税務署（選）（必）"
////						16		"代表者氏名姓フリガナ（書）（必）【自動】"
////						17		"代表者氏名名フリガナ（書）（必）【自動】"
////						18	DaibiaoName_Chinese1	"代表者氏名姓漢字（書）（必）"
////						19	DaibiaoName_Chinese2	"代表者氏名名漢字（書）（必）"
//					String DaibiaoName_Chinese = excelValue.get("19") + " " + excelValue.get("20");
////						20		"代表者住所郵便番号前三（書）（非）"
////						21		"代表者住所郵便番号後四（書）（非）"
////						22		"代表者住所都道府県（選）（必）"
////						23		"代表者住所市区町村（書）（必）"
////						24		"代表者住所丁目番地等（書）（必）"
////						25		"代表者住所建物名・号室（書）（非）"
////						26		"代表者住所電話番号前三（書）（非）"
////						27		"代表者住所電話番号中四（書）（非）"
////						28		"代表者住所電話番号後四（書）（非）"
////						29	etax_pw	"暗証番号（書）（必）"
////						30		"暗証番号確認（書）（必）"
////						31		"納税用確認番号（書）（必）"
////						32		"納税用カナ氏名・名称（書）（必）【自動】"
//
//
//					//去掉字符串里的TAB，首尾半角空格，首尾全角空格
//					CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
//					CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
//					DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
//
//
//					m_sequenceDao m_sequenceDao = new m_sequenceDao();
//					yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();
//
//
//					t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
//					t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
//					t_etax_account_infoBean.setUser_id("etaxonly");
//					t_etax_account_infoBean.setUser_type("公司");
//					t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
//					t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
//					t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
//					t_etax_account_infoBean.setYaoqing_no(yaoqing_no);
//
//					t_etax_account_infoBean.setSyouninn_status("待处理");//承認無
//
//					UUID uuid = UUID.randomUUID();
//					t_etax_account_infoBean.setActivation_code("etaxonly-" + uuid.toString());
//
//
//					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
//					logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
//
//
//					/*
//					 * 登録
//					 */
//					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
//
//					try {
//						t_etax_account_infoDao.INSERT(t_etax_account_infoBean);
//
//					} catch (Exception e) {
//						// TODO 登录失败怎么办
//						e.printStackTrace();
//						return;
//					}
//
//					/*
//					 * 激活
//					 */
//					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//					EtaxDao EtaxDao = new EtaxDao();
//					try {
//
//
//						t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
//								t_etax_account_resDao, EtaxDao);
//						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");
//
//					} catch (SQLException e) {
//						t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");
//
//					}
//
//
//
//
//
//				}
//
//				return;
//
//



				/*
				 * xtx
				 */
			} else if (!StringUtils.isEmpty(yyyymmdd_count) && "get_file".equals(hidden_key) && "xtx".equals(hidden_value)) {
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", INSQ, " + INSQ);

				try {
					FuncUtilsAiEtax FuncUtilsAiEtax = new FuncUtilsAiEtax();
					String path = FuncUtilsAiEtax.getXtx(req, INSQ);

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
							out.print("{\"res\":\"" + file.getName() + ".zip" + "\"}");

						} catch (IOException e) {
							e.printStackTrace();


						}

					} else {
						out.print("{\"res\":\"结果文件不存在\"}");
						logger.info("end " + hidden_key);
						return;
					}

				} catch (Exception e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();

					out.print("{\"res\":\""+e1+"\"}");
					logger.info("end " + hidden_key);
					return;
				}



				logger.info("end " + hidden_value);
				return;




			}

		if ("SetUserInfoLogic2".equals(hidden_key)) {

			/*
			 * 登录功能 客户数据
			 */
			try {

				String user_type = req.getParameter("hidden_user_type");
				String DaibiaoName_English = req.getParameter("form_DaibiaoName_English");
				String company_DD = req.getParameter("form_company_DD");
				String company_MM = req.getParameter("form_company_MM");
				String company_YYYY = req.getParameter("form_company_YYYY");
				String tel_1 = req.getParameter("form_tel_1");
				String tel_2 = req.getParameter("form_tel_2");
				String tel_3 = req.getParameter("form_tel_3");
				String tel_country = req.getParameter("form_tel_country");
				String xiaoshouerYYYY_1 = req.getParameter("form_xiaoshouerYYYY_1");
				String xiaoshouerYYYY_1_half = req.getParameter("form_xiaoshouerYYYY_1_half");
				String xiaoshouerYYYY_2 = req.getParameter("form_xiaoshouerYYYY_2");
				String zhice_ziben = req.getParameter("form_zhice_ziben");
				String address_Chinese = req.getParameter("form_address_Chinese");
				String CompanyName_Chinese = req.getParameter("form_CompanyName_Chinese");
				String CompanyName_English = req.getParameter("form_CompanyName_English");
				String DaibiaoName_Chinese = req.getParameter("form_DaibiaoName_Chinese");
				String geren_dianpu_address = req.getParameter("form_geren_dianpu_address");
				String geren_dianpu_name = req.getParameter("form_geren_dianpu_name");
				String changshe_jigou_Select = req.getParameter("form_changshe_jigou_Select");
				String jianyi_keshui_Select = req.getParameter("form_jianyi_keshui_Select");
				String address_English = req.getParameter("form_address_English");
				String jianyi_keshui_type = req.getParameter("form_jianyi_keshui_type");
				String tokutei_kikann_siharai_kyuuyo = req.getParameter("form_tokutei_kikann_siharai_kyuuyo");
				String shouri_kaishi_denglu_xiayige = req.getParameter("form_shouri_kaishi_denglu_xiayige");
				String shouri_kaishi_denglu_ben = req.getParameter("form_shouri_kaishi_denglu_ben");




				String CompanyName_pianjiaming = req.getParameter("form_CompanyName_pianjiaming");
				String address_pianjiaming = req.getParameter("form_address_pianjiaming");
				String DaibiaoName_pianjiaming = req.getParameter("form_DaibiaoName_pianjiaming");
				String DaibiaoName_address_pianjiaming = req.getParameter("form_DaibiaoName_address_pianjiaming");



//				user_id = req.getParameter("form_user_id");
				String etax_no = req.getParameter("form_etax_no");
				yaoqing_no = req.getParameter("form_yaoqing_no");



				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				CompanyName_Chinese = FuncUtils.trimWhitespaceAndTabs(CompanyName_Chinese);
				CompanyName_English = FuncUtils.trimWhitespaceAndTabs(CompanyName_English);
				CompanyName_pianjiaming = FuncUtils.trimWhitespaceAndTabs(CompanyName_pianjiaming);
				address_Chinese = FuncUtils.trimWhitespaceAndTabs(address_Chinese);
				address_English = FuncUtils.trimWhitespaceAndTabs(address_English);
				address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(address_pianjiaming);
				DaibiaoName_Chinese = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_Chinese);
				DaibiaoName_English = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_English);
				DaibiaoName_address_pianjiaming = FuncUtils.trimWhitespaceAndTabs(DaibiaoName_address_pianjiaming);


				String xiaoshouerYYYY_2_title = req.getParameter("form_xiaoshouerYYYY_2_title");
				String xiaoshouerYYYY_1_half_title = req.getParameter("form_xiaoshouerYYYY_1_half_title");
				String tokutei_kikann_siharai_kyuuyo_title = req.getParameter("form_tokutei_kikann_siharai_kyuuyo_title");
				String xiaoshouerYYYY_1_YYYYMMDD_title = req.getParameter("form_xiaoshouerYYYY_1_YYYYMMDD_title");
				String shuoming = req.getParameter("form_shuoming");


				String history = req.getParameter("form_history");


				String DaibiaoName_address_Chinese = req.getParameter("form_DaibiaoName_address_Chinese");
				String DaibiaoName_address_English = req.getParameter("form_DaibiaoName_address_English");
				String riben_kaishi_shiye_YYYY = req.getParameter("form_riben_kaishi_shiye_YYYY");
				String riben_kaishi_shiye_MM = req.getParameter("form_riben_kaishi_shiye_MM");
				String riben_kaishi_shiye_DD = req.getParameter("form_riben_kaishi_shiye_DD");


				String keshui_or_mianshui = req.getParameter("form_keshui_or_mianshui");
				String YYYY_1 = req.getParameter("form_YYYY_1");
				String keshui_shiyezhe_wenshu = req.getParameter("form_keshui_shiyezhe_wenshu");














				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", yaoqing_no, " + yaoqing_no);

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", user_type, " + user_type);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_English, " + DaibiaoName_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_DD, " + company_DD);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_MM, " + company_MM);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", company_YYYY, " + company_YYYY);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_1, " + tel_1);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_2, " + tel_2);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_3, " + tel_3);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tel_country, " + tel_country);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", zhice_ziben, " + zhice_ziben);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_Chinese, " + address_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", address_English, " + address_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_Chinese, " + CompanyName_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", CompanyName_English, " + CompanyName_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_Chinese, " + DaibiaoName_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", geren_dianpu_address, " + geren_dianpu_address);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", geren_dianpu_name, " + geren_dianpu_name);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_2, " + xiaoshouerYYYY_2);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_half, " + xiaoshouerYYYY_1_half);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tokutei_kikann_siharai_kyuuyo, " + tokutei_kikann_siharai_kyuuyo);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1, " + xiaoshouerYYYY_1);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", changshe_jigou_Select, " + changshe_jigou_Select);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", jianyi_keshui_Select, " + jianyi_keshui_Select);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", jianyi_keshui_type, " + jianyi_keshui_type);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shouri_kaishi_denglu_xiayige, " + shouri_kaishi_denglu_xiayige);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shouri_kaishi_denglu_ben, " + shouri_kaishi_denglu_ben);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", etax_no, " + etax_no);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_2_title" + xiaoshouerYYYY_2_title);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_half_title" + xiaoshouerYYYY_1_half_title);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", tokutei_kikann_siharai_kyuuyo_title" + tokutei_kikann_siharai_kyuuyo_title);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", xiaoshouerYYYY_1_YYYYMMDD_title" + xiaoshouerYYYY_1_YYYYMMDD_title);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", shuoming, " + shuoming);

				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", history, " + history);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_Chinese, " + DaibiaoName_address_Chinese);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", DaibiaoName_address_English, " + DaibiaoName_address_English);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_YYYY, " + riben_kaishi_shiye_YYYY);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_MM, " + riben_kaishi_shiye_MM);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", riben_kaishi_shiye_DD, " + riben_kaishi_shiye_DD);


				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshui_or_mianshui, " + keshui_or_mianshui);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", YYYY_1, " + YYYY_1);
				logger.info("yyyymmdd_count: " + yyyymmdd_count + ", keshui_shiyezhe_wenshu, " + keshui_shiyezhe_wenshu);



				//TODO
//				DaibiaoName_English =	"hou yi";
//				company_DD =			"9";
//				company_MM =			"9";
//				company_YYYY =			"2009";
//				tel_1 =					"123";
//				tel_2 =					"4567";
//				tel_3 =					"8901";
//				tel_country =			"86";
//				xiaoshouerYYYY_1 =		"95";
//				xiaoshouerYYYY_1_half =	"91";
//				xiaoshouerYYYY_2 =		"92";
//				zhice_ziben =			"666666";
//				address_Chinese =		"北京海淀区";
//				CompanyName_Chinese =	"太阳有限公司";
//				CompanyName_English =	"taiyang Co.,Ltd.";
//				DaibiaoName_Chinese =	"后 裔";
////				geren_dianpu_address =	"";
////				geren_dianpu_name =		"";
////				changshe_jigou_Select =	"";
////				jianyi_keshui_Select =	"";
//				address_English =		"beijing,China";
////				jianyi_keshui_type =	"";

				if (history == null) {
					history = "";
				}
				if ("YES".equals(history.toUpperCase())) {
					session.setAttribute("user_type", user_type);
					session.setAttribute("DaibiaoName_English", DaibiaoName_English);
					session.setAttribute("company_DD", company_DD);
					session.setAttribute("company_MM", company_MM);
					session.setAttribute("company_YYYY", company_YYYY);
					session.setAttribute("tel_1", tel_1);
					session.setAttribute("tel_2", tel_2);
					session.setAttribute("tel_3", tel_3);
					session.setAttribute("tel_country", tel_country);
					session.setAttribute("xiaoshouerYYYY_1", xiaoshouerYYYY_1);
					session.setAttribute("xiaoshouerYYYY_1_half", xiaoshouerYYYY_1_half);
					session.setAttribute("xiaoshouerYYYY_2", xiaoshouerYYYY_2);
					session.setAttribute("zhice_ziben", zhice_ziben);
					session.setAttribute("address_Chinese", address_Chinese);
					session.setAttribute("CompanyName_Chinese", CompanyName_Chinese);
					session.setAttribute("CompanyName_English", CompanyName_English);
					session.setAttribute("DaibiaoName_Chinese", DaibiaoName_Chinese);
					session.setAttribute("geren_dianpu_address", geren_dianpu_address);
					session.setAttribute("geren_dianpu_name", geren_dianpu_name);
					session.setAttribute("changshe_jigou_Select", changshe_jigou_Select);
					session.setAttribute("jianyi_keshui_Select", jianyi_keshui_Select);
					session.setAttribute("address_English", address_English);
					session.setAttribute("jianyi_keshui_type", jianyi_keshui_type);
					session.setAttribute("tokutei_kikann_siharai_kyuuyo", tokutei_kikann_siharai_kyuuyo);
					session.setAttribute("shouri_kaishi_denglu_xiayige", shouri_kaishi_denglu_xiayige);
					session.setAttribute("shouri_kaishi_denglu_ben", shouri_kaishi_denglu_ben);

					session.setAttribute("etax_no", etax_no);



				} else {
					session.removeAttribute("user_type"			);
					session.removeAttribute("DaibiaoName_English"			);
					session.removeAttribute("company_DD"					);
					session.removeAttribute("company_MM"					);
					session.removeAttribute("company_YYYY"				);
					session.removeAttribute("tel_1"						);
					session.removeAttribute("tel_2"						);
					session.removeAttribute("tel_3"						);
					session.removeAttribute("tel_country"					);
					session.removeAttribute("xiaoshouerYYYY_1"			);
					session.removeAttribute("xiaoshouerYYYY_1_half"		);
					session.removeAttribute("xiaoshouerYYYY_2"			);
					session.removeAttribute("zhice_ziben"					);
					session.removeAttribute("address_Chinese"				);
					session.removeAttribute("CompanyName_Chinese"			);
					session.removeAttribute("CompanyName_English"			);
					session.removeAttribute("DaibiaoName_Chinese"			);
					session.removeAttribute("geren_dianpu_address"		);
					session.removeAttribute("geren_dianpu_name"			);
					session.removeAttribute("changshe_jigou_Select"		);
					session.removeAttribute("jianyi_keshui_Select"		);
					session.removeAttribute("address_English"				);
					session.removeAttribute("jianyi_keshui_type"			);
					session.removeAttribute("tokutei_kikann_siharai_kyuuyo");
					session.removeAttribute("shouri_kaishi_denglu_xiayige");
					session.removeAttribute("shouri_kaishi_denglu_ben"	);
					session.removeAttribute("etax_no"						);



				}



				t_etax_account_infoBean t_etax_account_infoBean = new t_etax_account_infoBean();
				t_etax_account_infoBean.setUser_id(User_infoBean.getUser_id());
				t_etax_account_infoBean.setUser_type(user_type);
				t_etax_account_infoBean.setCompanyName_Chinese(CompanyName_Chinese);
				t_etax_account_infoBean.setCompanyName_English(CompanyName_English);
				t_etax_account_infoBean.setDaibiaoName_Chinese(DaibiaoName_Chinese);
				t_etax_account_infoBean.setDaibiaoName_English(DaibiaoName_English);
				t_etax_account_infoBean.setCompany_DD(company_DD);
				t_etax_account_infoBean.setCompany_MM(company_MM);
				t_etax_account_infoBean.setCompany_YYYY(company_YYYY);
				t_etax_account_infoBean.setTel_1(tel_1);
				t_etax_account_infoBean.setTel_2(tel_2);
				t_etax_account_infoBean.setTel_3(tel_3);
				t_etax_account_infoBean.setTel_country(tel_country);
				t_etax_account_infoBean.setXiaoshouerYYYY_1(xiaoshouerYYYY_1);
				t_etax_account_infoBean.setXiaoshouerYYYY_1_half(xiaoshouerYYYY_1_half);
				t_etax_account_infoBean.setXiaoshouerYYYY_2(xiaoshouerYYYY_2);
				t_etax_account_infoBean.setZhice_ziben(zhice_ziben);
				t_etax_account_infoBean.setAddress_Chinese(address_Chinese);
				t_etax_account_infoBean.setGeren_dianpu_address(geren_dianpu_address);
				t_etax_account_infoBean.setGeren_dianpu_name(geren_dianpu_name);
				t_etax_account_infoBean.setChangshe_jigou_Select(changshe_jigou_Select);
				t_etax_account_infoBean.setJianyi_keshui_Select(jianyi_keshui_Select);
				t_etax_account_infoBean.setAddress_English(address_English);
				t_etax_account_infoBean.setJianyi_keshui_type(jianyi_keshui_type);
				t_etax_account_infoBean.setTokutei_kikann_siharai_kyuuyo(tokutei_kikann_siharai_kyuuyo);
				t_etax_account_infoBean.setShouri_kaishi_denglu_xiayige(shouri_kaishi_denglu_xiayige);
				t_etax_account_infoBean.setShouri_kaishi_denglu_ben(shouri_kaishi_denglu_ben);
				t_etax_account_infoBean.setEtax_no(etax_no);
				t_etax_account_infoBean.setYaoqing_no(yaoqing_no);

				t_etax_account_infoBean.setDaibiaoName_address_Chinese(DaibiaoName_address_Chinese);
				t_etax_account_infoBean.setDaibiaoName_address_English(DaibiaoName_address_English);

				t_etax_account_infoBean.setSyouninn_status("待处理");//承認無


		        int digits = 8; // 需要生成的位数
		        String etax_pw = "ps" + FuncUtils.generateRandomNumber(digits);
		        t_etax_account_infoBean.setEtax_pw(etax_pw);

		        t_etax_account_infoBean.setEtax_pw_flag("0");

				if (StringUtils.isEmpty(CompanyName_pianjiaming)) {
					CompanyName_pianjiaming = FuncUtils.fn_hanzi(CompanyName_Chinese);
					CompanyName_pianjiaming = FuncUtils.toFullWidthAndTruncate(CompanyName_pianjiaming, 500);
					t_etax_account_infoBean.setCompanyName_pianjiaming(CompanyName_pianjiaming);
				}


				if (StringUtils.isEmpty(address_pianjiaming)) {
					address_pianjiaming = FuncUtils.fn_hanzi(address_Chinese);
					address_pianjiaming = FuncUtils.toFullWidthAndTruncate(address_pianjiaming, 1000);
					t_etax_account_infoBean.setAddress_pianjiaming(address_pianjiaming);
				}


				if (StringUtils.isEmpty(DaibiaoName_pianjiaming)) {
					DaibiaoName_pianjiaming = FuncUtils.fn_hanzi(DaibiaoName_Chinese);
					DaibiaoName_pianjiaming = FuncUtils.toFullWidthAndTruncate(DaibiaoName_pianjiaming, 500);
					t_etax_account_infoBean.setDaibiaoName_pianjiaming(DaibiaoName_pianjiaming);
				}

				if (StringUtils.isEmpty(DaibiaoName_address_pianjiaming)) {
					DaibiaoName_address_pianjiaming = FuncUtils.fn_hanzi(DaibiaoName_address_Chinese);
					DaibiaoName_address_pianjiaming = FuncUtils.toFullWidthAndTruncate(DaibiaoName_address_pianjiaming, 1000);
					t_etax_account_infoBean.setDaibiaoName_address_pianjiaming(DaibiaoName_address_pianjiaming);
				}



				m_sequenceDao m_sequenceDao = new m_sequenceDao();
//				yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

				INSQ ="INSQ" + yyyy.substring(2, 4) + m_sequenceDao.selectMax_INSQ();

		        t_jct_shenqingBean t_jct_shenqingBean = new t_jct_shenqingBean();
		        t_jct_shenqingBean.setYyyymmdd_count(yyyymmdd_count);

		        t_jct_shenqingBean.setINSQ(INSQ);
		        t_jct_shenqingBean.setTianxie_YYYY(tianxie_YYYY);



		        t_jct_shenqingBean.setRiben_kaishi_shiye_YYYY(riben_kaishi_shiye_YYYY);
		        t_jct_shenqingBean.setRiben_kaishi_shiye_MM(riben_kaishi_shiye_MM);
		        t_jct_shenqingBean.setRiben_kaishi_shiye_DD(riben_kaishi_shiye_DD);
		        t_jct_shenqingBean.setXiaoshouerYYYY_2(xiaoshouerYYYY_2);
		        t_jct_shenqingBean.setXiaoshouerYYYY_1_half(xiaoshouerYYYY_1_half);

		        t_jct_shenqingBean.setKeshui_or_mianshui(keshui_or_mianshui);
		        t_jct_shenqingBean.setYYYY_1(YYYY_1);
		        t_jct_shenqingBean.setKeshui_shiyezhe_wenshu(keshui_shiyezhe_wenshu);


				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_jct_shenqingDao t_jct_shenqingDao = new t_jct_shenqingDao();

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				EtaxDao EtaxDao = new EtaxDao();

				Map<String, String[]> HashMapParameterMap = req.getParameterMap();
				for (String key : HashMapParameterMap.keySet()) {
					if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
						continue;
					}

					if ("hidden_key".equals(key) && "SetUserInfoLogic2".equals(HashMapParameterMap.get(key)[0])) {
						UUID uuid = UUID.randomUUID();
						t_etax_account_infoBean.setActivation_code(uuid.toString());

//						m_sequenceDao m_sequenceDao = new m_sequenceDao();
						//TODO
//						yyyymmdd_count = m_sequenceDao.selectMax_yyyymmdd_count();

						t_etax_account_infoBean.setYyyymmdd_count(yyyymmdd_count);
						try {

							t_etax_account_infoDao.INSERT(t_etax_account_infoBean);
							t_jct_shenqingDao.INSERT(t_jct_shenqingBean);

							t_etax_account_infoBean.setEmail(User_infoBean.getEmail());


							if ("个人".equals(user_type)) {
								CompanyName_Chinese = DaibiaoName_Chinese.replace(" ", "");
							}

							//アップロードするフォルダ
							String path = getServletContext().getRealPath("/fileData");
							path = path + "/" + yyyymmdd_count + "_" + CompanyName_Chinese;
							//登录信息发邮件给客户
							FuncUtils.sendMail_jct_shengao(t_etax_account_infoBean, t_jct_shenqingBean, path, xiaoshouerYYYY_2_title, xiaoshouerYYYY_1_half_title, tokutei_kikann_siharai_kyuuyo_title, xiaoshouerYYYY_1_YYYYMMDD_title, shuoming);

						} catch (Exception e) {
							// TODO 登录失败怎么办
							e.printStackTrace();
						}




						//TODO 删除URL里边的key SetUserInfo 防止重复登录
						session.removeAttribute("SetUserInfo"					);
						out.print("{\"res\":\"ok\"}");
						logger.info("end");
						return;

					} else if ("delete".equals(key)) {
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
								t_etax_account_infoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
										t_etax_account_resDao, EtaxDao, "");

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

					}
				}



				session.setAttribute("SetUserInfo", "OK");
				session.setAttribute("User_infoBean", User_infoBean);
				req.getRequestDispatcher("/setUserInfo2.jsp").forward(req, resp);

			} catch (Exception e) {
				e.printStackTrace();
			}



		} else if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
			if (StringUtils.isEmpty(INSQ) == true) {
				session.setAttribute("t_etax_account_infoExBean", new t_etax_account_infoExBean());
				session.setAttribute("t_jct_shenqingBean", new t_jct_shenqingBean());

				req.getRequestDispatcher("/setUserInfo2.jsp?fromBackend=true").forward(req, resp);
				logger.info("end");
				return;

//			} else {
//
//				logger.debug("PandaServiceTools → INSQ invalid");
//				out.write("PandaServiceMA → INSQ invalid");
//				logger.info("end");
//				return;
			}


			try {

				t_jct_shenqingDao t_jct_shenqingDao = new t_jct_shenqingDao();
				t_jct_shenqingBean t_jct_shenqingBean = t_jct_shenqingDao.SelectKeyValue("INSQ", INSQ);
//				t_xiaofeishui_shengaoBean.setActivation_code("");
				yyyymmdd_count = t_jct_shenqingBean.getYyyymmdd_count();
				if (StringUtils.isEmpty(yyyymmdd_count) == false) {
					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(t_jct_shenqingBean.getYyyymmdd_count());
					session.setAttribute("t_etax_account_infoExBean", t_etax_account_infoExBean);
					session.setAttribute("t_jct_shenqingBean", t_jct_shenqingBean);

				} else {

					//session.setAttribute("User_infoBean", new User_infoBean());

					logger.debug("PandaServiceTools → INSQ invalid");
					out.write("PandaServiceMA → INSQ invalid");
					logger.info("end");
					return;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}


			req.getRequestDispatcher("/setUserInfo2.jsp?fromBackend=true").forward(req, resp);
			logger.info("end");
			return;





		} else {

			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");

		}





		logger.debug("end");

		return;

	}

    public static void main(String[] args) {

    	String address_pianjiaming = (new FuncUtils()).fn_hanzi("公司地址20250711192057");
		logger.debug(FuncUtils.toFullWidthAndTruncate(address_pianjiaming, 500));
    }



}