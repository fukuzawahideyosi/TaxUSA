package com.panda.servlet;




import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.m_ai_guanli_yewubiaoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;
import com.panda.utils.FuncUtilsExcel;
import com.panda.utils.XMLCalculator;

@WebServlet("/AiEtaxLogic")
@MultipartConfig
public class AiEtaxLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(AiEtaxLogic.class.toString());

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

		String msg = "";
		String res = "";
		String yyyy = "2024";
		String PDSK = req.getParameter("PDSK");

		String hidden_key = req.getParameter("hidden_key");
		if (hidden_key == null) {
			hidden_key = "";
		}


		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");
		String form_shouxu = req.getParameter("form_shouxu");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("User_infoBean", new User_infoBean());
		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);




		/*
		 * 邀请码有效性验证
		 */


		/*
		 * license確認
		 */
		if (StringUtils.isEmpty(user_id) == true) {
			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");
			logger.info("end " + hidden_key);
			return;

		} else {

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


		String err_msg = "";


		/*
		 *上传PS设定式样书
		 */
		if ("set_file_ps".equals(hidden_key)) {
	 		logger.info("exe " + hidden_key);
			try {

				req.getParts();
				req.setCharacterEncoding("utf-8");

				// 获取当前日期时间
				Date currentDate = new Date();
				// 设置日期时间格式
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				// 格式化日期时间
				String yyyymmddhhmmss = dateFormat.format(currentDate);
				int count=0;
				// 拡張for文
				for (int j = 0; j < req.getParts().size(); j++) {
					//name属性がfileのファイルをPartオブジェクトとして取得
					Part part = req.getPart("file[" + j + "]");
					//ファイル名を取得
					//String filename=part.getSubmittedFileName();//ie対応が不要な場合
					String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
					String FolderName = filename.split("_")[0];

					String folderPath = FuncUtils.projectPath + "/ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/" + FolderName;
					String filePath = folderPath + "/" + filename;

			        File folder = new File(folderPath);
			        // 文件夹不存在则创建
			        if (!folder.exists()) {
			            folder.mkdirs();
			        }

					String fe = FilenameUtils.getExtension(filename);
					File sourceFile = FuncUtils.getLatestFile(folderPath, fe);
					if (sourceFile != null && sourceFile.exists()) {
					    String[] sourceFile_name = sourceFile.getName().split("\\.");
				        File targetFile = new File(sourceFile.getParent() + "/" + sourceFile_name[0] + "_" + yyyymmddhhmmss + "." + sourceFile_name[1]);
				        //现有PS式样书备份
					    sourceFile.renameTo(targetFile);
					} else {
					    sourceFile = new File(filePath);
					}

					if ("xlsx".equals(fe)) {

						//書き込み
						part.write(sourceFile.getPath());

				 		logger.info("UP file " + filename);
						++count;
						byte[] cert = Files.readAllBytes(Paths.get(sourceFile.getPath()));

/*
Table: m_ai_guanli_yewubiao
Columns:
UPDATE_DATE timestamp(6)
UPDATE_SYSTEM varchar(45)
user_id varchar(45)
biaoming varchar(45) PK
etax_shiyangshu varchar(128) PK
shiyong varchar(45)
*/
						String biaoming = "";
						String etax_shiyangshu = "/" + sourceFile.getParentFile().getName() + "/" + filename;
						String shiyong = "";
						m_ai_guanli_yewubiaoDao m_ai_guanli_yewubiaoDao = new m_ai_guanli_yewubiaoDao();
						LinkedHashMap<String, String> m_ai_guanli_yewubiaoBean_LinkedHashMap = m_ai_guanli_yewubiaoDao.select(biaoming, etax_shiyangshu);
						if (m_ai_guanli_yewubiaoBean_LinkedHashMap.size() == 0) {
							m_ai_guanli_yewubiaoDao.insert(user_id, biaoming, etax_shiyangshu, shiyong);

						} else {
							m_ai_guanli_yewubiaoDao.update(user_id, biaoming, etax_shiyangshu, shiyong);

						}

					}


				}

				out.print("{\"count\":" + count + "}");
//				out.print(yyyymmdd_count);

		 		logger.info("end ");
				return;

			} catch (Throwable e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();

				out.print("{\"count\":" + e + "}");
//				out.print(yyyymmdd_count);

		 		logger.info("end ");
				return;
			}



			/*
			 * 下载PS设定式样书模板
			 */
		} else if ("get_file".equals(hidden_key) && "get_file_ps_moban".equals(hidden_value)) {

			try {

				String form_shouxu_type = req.getParameter("form_shouxu_type");
				String form_zhangpiao = req.getParameter("form_zhangpiao");
				//记住用户操作
				FuncUtilsAiEtax.writePropertyCsvMap(form_shouxu_type, form_zhangpiao, user_id);

				String[] form_shouxu_list = form_shouxu.split(",");
				if (form_shouxu_list.length != 2) {
					out.print("{\"res\":\"手续式样文件不存在\"}");
					logger.info("end " + hidden_key);
					return;
				}

				String shouxu_id = form_shouxu_list[0];
				String shouxu_banben = form_shouxu_list[1];
//				shouxu_id = "RSH0020";
//				shouxu_banben = "23.2.0";
				logger.info("shouxu_id: " + shouxu_id + ", shouxu_banben, " + shouxu_banben);


				String path = get_ps_shiyangshu(shouxu_id, shouxu_banben, form_zhangpiao, form_shouxu_type);

				File file = new File(path);
				if (file.exists()) {
					String targetZipFilePath = path;

					logger.info("PS设定式样书创建成功：" + targetZipFilePath);
					out.print("{\"res\":\"" + targetZipFilePath.replace(FuncUtils.projectPath, "") + "\"}");


//					/*
//					 * 生成文件ZIP
//					 */
//					// 源文件夹的路径
//					String sourceFolderPath = path;
//					// 目标ZIP文件的路径
//					String targetZipFilePath = path + "_ps.zip";
//					try {
//						// 创建一个输出流，将文件写入ZIP文件
//						FileOutputStream fos = new FileOutputStream(targetZipFilePath);
//						ZipOutputStream zipOut = new ZipOutputStream(fos);
//
//						// 调用递归方法将文件夹及其内容添加到ZIP文件中
//						FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);
//
//						// 关闭ZIP文件输出流
//						zipOut.close();
//						fos.close();
//
//						logger.info("ZIP文件创建成功：" + targetZipFilePath);
//						out.print("{\"res\":\"" + PDSK + "_xtx.zip" + "\"}");
//
//					} catch (IOException e) {
//						e.printStackTrace();
//					}

				} else {
					out.print("{\"res\":\"结果文件不存在\"}");
					logger.info("end " + hidden_key);
					return;
				}

			} catch (Exception e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();

				out.print("{\"res\":\""+e+"\"}");
				logger.info("end " + hidden_key);
				return;
			}



			logger.info("end " + hidden_value);
			return;

		} else if ("admin".equals(User_infoBean.getPermissions())) {

			try {

				TreeMap<String, TreeMap<String, String[]>> PropertyCsvMap_shouxu =  FuncUtilsAiEtax.PropertyCsvMap_shouxu;
				TreeMap<String, TreeMap<String, TreeMap<String, String[]>>> PropertyCsvMap_zhangpiao =  FuncUtilsAiEtax.PropertyCsvMap_zhangpiao;
				TreeMap<String, TreeMap<String, String[]>> PropertyPsMap_帳票フィールド仕様書 = FuncUtilsAiEtax.PropertyPsMap_帳票フィールド仕様書;
				Map<String, String> XMLCalculator_roundingRules = (new XMLCalculator()).roundingRules;


				TreeMap<String, String> PropertyCsvMap_zhangpiao_user = FuncUtilsAiEtax.getPropertyCsvMap_zhangpiao_user();



				//TODO
				Iterator<Map.Entry<String, TreeMap<String, TreeMap<String, String[]>>>> outerIterator = PropertyCsvMap_zhangpiao.entrySet().iterator();
				while (outerIterator.hasNext()) {
					Map.Entry<String, TreeMap<String, TreeMap<String, String[]>>> outerEntry = outerIterator.next();
					String outerKey = outerEntry.getKey();

					//TODO
					// 如果外层 key 不包含 "RSH"，则直接移除整个 entry
//		            if (!outerKey.startsWith("RSH")) {
//		                outerIterator.remove();
//		                continue;
//		            }

				}


				session.setAttribute("PropertyCsvMap_shouxu", PropertyCsvMap_shouxu);
				session.setAttribute("PropertyCsvMap_zhangpiao", PropertyCsvMap_zhangpiao);
				session.setAttribute("PropertyPsMap_帳票フィールド仕様書", PropertyPsMap_帳票フィールド仕様書);
				session.setAttribute("XMLCalculator_roundingRules", XMLCalculator_roundingRules);
				session.setAttribute("PropertyCsvMap_zhangpiao_user", PropertyCsvMap_zhangpiao_user);


				session.setAttribute("projectPath", FuncUtils.projectPath);

	            String directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）/07手続一覧等/01手続一覧";









			} catch (Exception e) {
				e.printStackTrace();
			}


			req.getRequestDispatcher("/aiEtax.jsp?fromBackend=true").forward(req, resp);
			logger.info("end");
			return;

		}
	}




	public static void copyAndRenameFile(String sourceFilePath, String destinationDirectory, String newFileName) {
 		logger.info("start copyAndRenameFile");
		Path sourcePath = Paths.get(sourceFilePath);
		Path destinationPath = Paths.get(destinationDirectory, newFileName);

		try {
			// 复制文件到指定目录并重命名
			Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
            e.printStackTrace();
		}
		logger.info("end");
	}
	public static String get_ps_shiyangshuTEST(String shouxu_id, String shouxu_banben) {

        String excelFilePath = "C:\\Users\\Administrator\\Desktop\\PandaServiceMA\\ETAX_moban\\e-Tax仕様書一覧全仕様書（PS）\\excel-file.xlsx"; // 替换为你的文件路径
        String newSheetName = "NewSheetName"; // 新的工作表名称
        int sheetIndex = 0; // 你要修改名字的工作表的索引（从0开始）

        try (FileInputStream fis = new FileInputStream(excelFilePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            // 获取要修改的工作表
            Sheet sheet = workbook.getSheetAt(sheetIndex);

            // 修改工作表名字
            workbook.setSheetName(sheetIndex, newSheetName);

            // 保存修改后的工作簿
            try (FileOutputStream fos = new FileOutputStream(excelFilePath)) {
                workbook.write(fos);
            }

            System.out.println("工作表名字已修改为: " + newSheetName);

        } catch (Throwable e) {
            e.printStackTrace();
        }
		return newSheetName;

	}


	public static void main(String[] args) {

		String shouxu_id = "RSH0020";
		String shouxu_banben = "23.2.0";

		get_ps_shiyangshu(shouxu_id, shouxu_banben, "", "");

	}


	public static String get_ps_shiyangshu(String shouxu_id, String shouxu_banben, String form_zhangpiao, String form_shouxu_type) {

		try {
			/*
			 *
			 *
			 *
			 */

			//     // 显式调用 FuncUtilsAiEtax 类，确保静态初始化块执行
			//     FuncUtilsAiEtax funcUtils = new FuncUtilsAiEtax();
			//
			//     // 此方法将确保 FuncUtilsAiEtax 类的静态初始化块被执行
			//     FuncUtilsAiEtax.class.getDeclaredFields(); // 访问类字段，触发静态代码块执行

			// 确保 PropertyCsvMap_shouxu 数据已被填充
			TreeMap<String, TreeMap<String, String[]>> PropertyCsvMap_shouxu = FuncUtilsAiEtax.PropertyCsvMap_shouxu;
			TreeMap<String, TreeMap<String, String[]>> myPropertyCsvMap_shouxu_id = FuncUtilsAiEtax.PropertyCsvMap_zhangpiao.get(shouxu_id);
			TreeMap<String, String[]> myPropertyCsvMap_shouxu_banben = myPropertyCsvMap_shouxu_id.get(shouxu_banben);
			TreeMap<String, TreeMap<String, String[]>> PropertyPsMap_帳票フィールド仕様書 = FuncUtilsAiEtax.PropertyPsMap_帳票フィールド仕様書;

			String fileName = myPropertyCsvMap_shouxu_banben.get("PS_0.0.0")[0].replace(".csv", ".xlsx");

			// 指定源文件路径
			//        String sourceFilePath = directoryPath + "/ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）/07手続一覧等/02手続内帳票対応表/" + fileName;
			String sourceFilePath = myPropertyCsvMap_shouxu_banben.get("PS_0.0.0")[0].replace(".csv", ".xlsx");

			// 指定目标文件夹路径和重命名后的文件名
			String destinationDirectory = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/";

			File folder = new File(fileName);
			String newFileName = form_shouxu_type.split("/")[2].replace(".xlsx", "");
//			newFileName = newFileName + "_" + folder.getName().replace(".xlsx", "_PS_" + shouxu_id + "_" + shouxu_banben + ".xlsx");
//			newFileName = "PS_" + shouxu_id + "_" + shouxu_banben + "_" + newFileName + "_" + folder.getName();
			newFileName = "" + shouxu_id + "_" + shouxu_banben + "_" + newFileName + "_" + folder.getName();

            // 设置Zip文件的最小膨胀比率为0，禁用此限制
            ZipSecureFile.setMinInflateRatio(0);

			logger.info("newFileName: " + newFileName);
			// 执行复制并重命名
			copyAndRenameFile(sourceFilePath, destinationDirectory, newFileName);
			// 隐藏不要的行
			hideRowsBasedOnKey(destinationDirectory + newFileName, shouxu_id);

			// 循环遍历 shouxu_banben 中的每个条目
			for (Entry<String, String[]> entry : myPropertyCsvMap_shouxu_banben.entrySet()) {
				String versionKey = entry.getKey();
				String[] rowData = entry.getValue();

				String[] versionKeys = versionKey.split("_");

				String sheetName = versionKeys[0];

				String targetExcelPath = destinationDirectory + newFileName;
				String sourceExcelPath_ps = destinationDirectory + shouxu_id + form_shouxu_type;

				if ("PS_0.0.0".equals(versionKey)) {
					copySheetToNewFile_IT(null, sheetName, targetExcelPath, versionKeys, sourceExcelPath_ps);
					continue;
				}

				TreeMap<String, String[]> myTreeMap = FuncUtilsAiEtax.PropertyPsMap_帳票フィールド仕様書.get(sheetName);
				String[] 帳票フィールド仕様書 = myTreeMap.get(versionKeys[1]);

				String sourceExcelPath = myTreeMap.get("PS")[0] + "/" + 帳票フィールド仕様書[2];


				//TODO
				if("SOZ042".equals(sheetName)) {
			        sheetName = sheetName;
				}

				if (!StringUtils.isEmpty(form_zhangpiao) && !form_zhangpiao.contains(sheetName)) {
					continue;
				}
				logger.info("sheetName: " + sheetName + " versionKey: " + versionKey);

				//目前 Apache POI 是处理 Excel 文件的最强大、最常用的库，但它没有提供直接跨工作簿克隆 Sheet 的 API。
				//对于跨工作簿克隆 Sheet，最佳实践仍然是通过手动复制源 Sheet 的内容、样式和其他数据到目标 Workbook 来实现。
				copySheetToNewFile(sourceExcelPath, sheetName, targetExcelPath, versionKeys, sourceExcelPath_ps);

//				break;

			}

			return FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/" + newFileName;

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		 return null;
	}



	private static String findValueInColumn(Sheet sourceSheet, String searchValue, int searchColumnIndex, int targetColumnIndex) {
	    // 遍历所有行
	    for (Row row : sourceSheet) {
	        Cell searchCell = row.getCell(searchColumnIndex); // 获取 S列（指定的列）
	        if (searchCell != null && searchCell.getCellType() == CellType.STRING) {
	            if (searchValue.equals(searchCell.getStringCellValue())) {
	                // 获取对应行的 U列（目标列）
	                Cell targetCell = row.getCell(targetColumnIndex);
	                return targetCell != null ? targetCell.toString() : null;
	            }
	        }
	    }
	    return null; // 如果未找到匹配的值
	}





	 public static void copySheetToNewFile(String sourceExcelPath, String sheetName, String targetExcelPath, String[] versionKeys, String sourceExcelPath_ps) {
	        logger.info("start copySheetToNewFile");
	        FileInputStream fis = null;
	        Workbook targetWorkbook = null;


	        FileInputStream fis_xml = null;
	        Workbook targetWorkbook_xml = null;

	        FileOutputStream fos = null;
	        Workbook sourceWorkbook = null;

	        FileOutputStream fos_xml = null;
	        Workbook sourceWorkbook_xml = null;

	        Workbook sourceWorkbook_ps = null;
	        FileInputStream fis_ps = null;

	        try {
	            // 打开源文件并获取工作簿和指定的工作表
	            fis = new FileInputStream(new File(sourceExcelPath));
	            sourceWorkbook = WorkbookFactory.create(fis);
	            Sheet sourceSheet = sourceWorkbook.getSheet(sheetName);



	            if (sourceSheet == null) {
	                System.err.println("Sheet " + sheetName + " does not exist in the source file.");
	                return;
	            }

	            // 打开目标文件，如果不存在则创建新文件
	            if (new File(targetExcelPath).exists()) {
	                targetWorkbook = WorkbookFactory.create(new FileInputStream(targetExcelPath));
	            } else {
	                targetWorkbook = new XSSFWorkbook();
	            }


//	            getCellBackgroundColor(targetWorkbook);

	            // 在目标工作簿中创建复制的工作表
	            String newSheetName = "PS_" + sheetName + "V" + versionKeys[1];
	            Sheet newSheet = targetWorkbook.createSheet(newSheetName);



	            // 复制工作表的内容
	            copySheetContent(sourceSheet, newSheet);

	            // 复制合并的单元格区域
	            copyMergedRegions(sourceSheet, newSheet);

//	            1，从A列到J列，设置边框
//	            2，从A列到J列，从第一行到第四行，背景色为浅蓝色
	            formatSheet(targetWorkbook, newSheet);


	            fis_xml = new FileInputStream(new File(sourceExcelPath.replaceAll("/帳票フィールド仕様書", "/XML構造設計書")));
	            sourceWorkbook_xml = WorkbookFactory.create(fis_xml);
	            Sheet sourceSheet_xml = sourceWorkbook_xml.getSheet(sheetName);
	            if (sourceSheet_xml != null) {
	            	copySheetContent_xml(sourceSheet_xml, newSheet);

	            }


	            // 打开源文件并获取工作簿和指定的工作表
	            fis_ps = new FileInputStream(new File(sourceExcelPath_ps));
	            sourceWorkbook_ps = WorkbookFactory.create(fis_ps);
	            Sheet sourceSheet_ps = sourceWorkbook_ps.getSheet(newSheetName);
	            if (sourceSheet_ps == null) {
	            	copySheetContent_title(newSheet);

	            } else {
	            	//复制PS式样
	            	copySheetContent_ps(sourceSheet_ps, newSheet);

	            }

	            // 保存更改到目标文件
	            fos = new FileOutputStream(targetExcelPath);
	            targetWorkbook.write(fos);

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (targetWorkbook != null) targetWorkbook.close();
	                if (fis != null) fis.close();

	                if (sourceWorkbook != null) sourceWorkbook.close();
	                if (fos != null) fos.close();

	                if (sourceWorkbook_ps != null) sourceWorkbook_ps.close();
	                if (fis_ps != null) fis_ps.close();

	                if (sourceWorkbook_xml != null) sourceWorkbook_xml.close();
	                if (fis_xml != null) fis_xml.close();

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        logger.info("end copySheetToNewFile");
	    }



		private static void copySheetContent_xml(Sheet sourceSheet, Sheet newSheet) {

			// 获取 newSheet 和 sourceSheet_ps 的行数
			int newSheetRowCount = newSheet.getPhysicalNumberOfRows();
			int sourceSheetRowCount = sourceSheet.getPhysicalNumberOfRows();

			// 在 sourceSheet_xml 中查找匹配的 M 列值
			for (int sourceRowIdx = 4; sourceRowIdx < sourceSheetRowCount; sourceRowIdx++) {
				Row sourceRow = sourceSheet.getRow(sourceRowIdx);
				if (sourceRow == null) {
					continue; // 防止行为空

				}

				Cell sourceCellS = sourceRow.getCell(18); // S列的索引是18
				String sourceCellSValue = FuncUtilsExcel.getCellValueAsString(sourceCellS);
				if (StringUtils.isEmpty(sourceCellSValue)) {
					continue;
				}

				Cell sourceCellU = sourceRow.getCell(20); // U 列的索引是20
//				System.out.println("sourceCellU: " + FuncUtilsExcel.getCellValueAsString(sourceCellU));
                if (StringUtils.isEmpty(FuncUtilsExcel.getCellValueAsString(sourceCellU)) == true) {
                	continue;
                }


				// 从第3行开始循环遍历 newSheet
				for (int newRowIdx = 3; newRowIdx < newSheetRowCount; newRowIdx++) {
					// 获取 newSheet 中第 newRowIdx 行的 M 列值
					Row newRow = newSheet.getRow(newRowIdx);
					if (newRow == null) {
						continue; // 防止行为空
					}

					Cell newCellM = newRow.getCell(12); // M列的索引是12（从0开始）
					if (newCellM == null) {
						continue; // 如果 M 列为空，则跳过
					}

					String newCellMValue = newCellM.getStringCellValue(); // 获取 M 列的值
					if (sourceCellSValue.equals(newCellMValue)) {

						Cell newCellQ = newRow.createCell(16, CellType.STRING);
						copyCellContent(sourceCellU, newCellQ);
						break;

					}

				}

			}

		}

	public static void copySheetToNewFile_IT(String sourceExcelPath, String sheetName, String targetExcelPath, String[] versionKeys, String sourceExcelPath_ps) {
	        logger.info("start copySheetToNewFile_IT");
	        FileInputStream fis = null;
	        Workbook targetWorkbook = null;

	        FileOutputStream fos = null;
	        Workbook sourceWorkbook = null;

	        Workbook sourceWorkbook_ps = null;
	        FileInputStream fis_ps = null;

	        try {
	        	sourceExcelPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/4 IT部仕様書/IT部PS仕様書.xlsx";
	        	sheetName = "PS_IT部";
	            // 打开源文件并获取工作簿和指定的工作表
	            fis = new FileInputStream(new File(sourceExcelPath));
	            sourceWorkbook = WorkbookFactory.create(fis);
	            Sheet sourceSheet = sourceWorkbook.getSheet(sheetName);


	            if (sourceSheet == null) {
	                System.err.println("Sheet " + sheetName + " does not exist in the source file.");
	                return;
	            }

	            // 打开目标文件，如果不存在则创建新文件
	            if (new File(targetExcelPath).exists()) {
	                targetWorkbook = WorkbookFactory.create(new FileInputStream(targetExcelPath));
	            } else {
	                targetWorkbook = new XSSFWorkbook();
	            }


//	            getCellBackgroundColor(targetWorkbook);

	            // 在目标工作簿中创建复制的工作表
	            String newSheetName = "" + sheetName + "V" + versionKeys[1];
	            Sheet newSheet = targetWorkbook.createSheet(newSheetName);



	            // 复制工作表的内容
	            copySheetContent(sourceSheet, newSheet);

	            // 复制合并的单元格区域
	            copyMergedRegions(sourceSheet, newSheet);

//	            1，从A列到J列，设置边框
//	            2，从A列到J列，从第一行到第四行，背景色为浅蓝色
	            formatSheet(targetWorkbook, newSheet);


	            // 打开源文件并获取工作簿和指定的工作表
	            fis_ps = new FileInputStream(new File(sourceExcelPath_ps));
	            sourceWorkbook_ps = WorkbookFactory.create(fis_ps);
	            Sheet sourceSheet_ps = sourceWorkbook_ps.getSheet(newSheetName);
	            if (sourceSheet_ps == null) {
	            	copySheetContent_title(newSheet);


	    			for (int i = 7; i <= 15; i++) {
		    			newSheet.setColumnWidth(i, 500);
	    			}

	            } else {
	            	//复制PS式样
	            	copySheetContent_ps(sourceSheet_ps, newSheet);

	            }

	            // 保存更改到目标文件
	            fos = new FileOutputStream(targetExcelPath);
	            targetWorkbook.write(fos);

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (targetWorkbook != null) targetWorkbook.close();
	                if (fis != null) fis.close();

	                if (sourceWorkbook != null) sourceWorkbook.close();
	                if (fos != null) fos.close();

	                if (sourceWorkbook_ps != null) sourceWorkbook_ps.close();
	                if (fis_ps != null) fis_ps.close();

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        logger.info("end");
	    }


		private static void copySheetContent_ps_title(Sheet newSheet, Sheet sourceSheet_ps) {

			// 定义要复制的起始和结束行
			int startRow = 0; // 第一行，0 基索引
			int endRow = 2; // 第三行，0 基索引
			int[] columns = { 16, 17, 18, 19 }; // Q, R, S, T 列 (索引从 0 开始)

			// 遍历要复制的行
			for (int i = startRow; i <= endRow; i++) {
				Row sourceRow = sourceSheet_ps.getRow(i);
				if (sourceRow == null) {
					continue; // 如果源行为空，跳过
				}

				// 确保目标行存在
				Row newRow = newSheet.getRow(i);
				if (newRow == null) {
					newRow = newSheet.createRow(i);
				}

				// 遍历列
				for (int colIndex : columns) {
					Cell sourceCell = sourceRow.getCell(colIndex);
					if (sourceCell == null) {
						continue; // 如果源单元格为空，跳过
					}

					Cell newCell = newRow.createCell(colIndex, CellType.STRING);
					//PS设定式样书
					copyCellContent(sourceCell, newCell);
					copyCellStyle(sourceCell, newCell);
				}
			}

			// 遍历列
			for (int colIndex = 0; colIndex <= 19; colIndex++) {
				// 获取当前列的宽度
				int currentWidth = sourceSheet_ps.getColumnWidth(colIndex);
				newSheet.setColumnWidth(colIndex, currentWidth);

			}
		}

		private static void copySheetContent_title(Sheet newSheet) {
			// 获取工作簿对象
			Workbook workbook = newSheet.getWorkbook();
			// 创建单元格样式
			CellStyle redBackgroundStyle = workbook.createCellStyle();
			redBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			redBackgroundStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

			CellStyle greenBackgroundStyle = workbook.createCellStyle();
			greenBackgroundStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			greenBackgroundStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());


			newSheet.setColumnWidth(15, 5000);
			newSheet.setColumnWidth(16, 1000);
			newSheet.setColumnWidth(17, 4000);
			newSheet.setColumnWidth(18, 4000);
			newSheet.setColumnWidth(19, 4000);


			// 定义要复制的起始和结束行
			int startRow = 0; // 第一行，0 基索引
			int endRow = 2; // 第三行，0 基索引
			int[] columns = { 16, 17, 18, 19 }; // Q, R, S, T 列 (索引从 0 开始)

			// 遍历要复制的行
			for (int i = startRow; i <= endRow; i++) {
				// 确保目标行存在
				Row newRow = newSheet.getRow(i);
				if (newRow == null) {
					newRow = newSheet.createRow(i);
				}

				// 遍历列
				for (int colIndex : columns) {
					Cell newCell = newRow.createCell(colIndex, CellType.STRING);
					//默认值
					String value= "";
					if (i == 0) {
						if (colIndex == 16) {
							value= "Q列";
						} else if (17 <= colIndex && colIndex <= 19) {
							value= "修改时联系王迪";
						}
						// 应用红色背景样式
						newCell.setCellStyle(redBackgroundStyle);

					} else if (i == 1) {
						if (17 <= colIndex && colIndex <= 19) {
							value= "PS式样栏";
						}
						newCell.setCellStyle(greenBackgroundStyle);

					} else if (i == 2) {
						if (colIndex == 17) {
							value= "PS区分";
						} else if (colIndex == 18) {
							value= "PS对象元素/值";
						} else if (colIndex == 19) {
							value= "PS运算关系(最优先)";
						}
						newCell.setCellStyle(greenBackgroundStyle);
					}
					newCell.setCellValue(value);
				}
			}

		}


	    private static void copySheetContent_ps(Sheet sourceSheet_ps, Sheet newSheet) {
	    	Workbook targetWorkbook = newSheet.getWorkbook();

	        // 创建一个 CellStyle 并设置背景色为淡黄色
	        CellStyle cellStyle = targetWorkbook.createCellStyle();
			XSSFColor lightYellow = new XSSFColor(new Color(255, 255, 204), null);
	        cellStyle.setFillForegroundColor(lightYellow); // 设置前景色
	        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND); // 填充模式为实心

//	        int colIndex = 16;
//			int currentWidth = sourceSheet_ps.getColumnWidth(colIndex);
//			newSheet.setColumnWidth(colIndex, currentWidth*2);

	     // P 列是第 16 列（索引从 0 开始）
	        newSheet.setColumnWidth(15, 100 * 256); // 100 字符宽度

	    	  // 获取 newSheet 和 sourceSheet_ps 的行数
	        int newSheetRowCount = newSheet.getPhysicalNumberOfRows();
	        int sourceSheetRowCount = sourceSheet_ps.getPhysicalNumberOfRows();

	        // 从第3行开始循环遍历 newSheet
	        for (int newRowIdx = 2; newRowIdx < newSheetRowCount; newRowIdx++) {
	            // 获取 newSheet 中第 newRowIdx 行的 M 列值
	            Row newRow = newSheet.getRow(newRowIdx);
	            if (newRow == null) continue; // 防止行为空

	            Cell newCellM = newRow.getCell(12); // M列的索引是12（从0开始）
	            String newCellMValue = FuncUtilsExcel.getCellValueAsString(newCellM); // 获取 M 列的值
	            if(newSheet.getSheetName().contains("PS_IT部")) {
	            	Cell newCellC = newRow.getCell(2); // M列的索引是12（从0开始）
	            	newCellMValue = FuncUtilsExcel.getCellValueAsString(newCellC);
	            }
	            if (StringUtils.isEmpty(newCellMValue)) continue; // 如果 M 列为空，则跳过



	            boolean sourceSheet_ps_falg = false;
	            // 在 sourceSheet_ps 中查找匹配的 M 列值
	            for (int sourceRowIdx = 2; sourceRowIdx < sourceSheetRowCount; sourceRowIdx++) {
	                Row sourceRow = sourceSheet_ps.getRow(sourceRowIdx);
	                if (sourceRow == null) continue; // 防止行为空

	                Cell sourceCellM = sourceRow.getCell(12); // M列的索引是12
	                String sourceCellMValue = FuncUtilsExcel.getCellValueAsString(sourceCellM); // 获取 M 列的值
		            if(sourceSheet_ps.getSheetName().contains("PS_IT部")) {
		            	Cell sourceCellC = sourceRow.getCell(2); // M列的索引是12（从0开始）
		            	sourceCellMValue = FuncUtilsExcel.getCellValueAsString(sourceCellC);
		            }

	                // 如果 M 列的值相同，则复制 R, S, T 列的值到 newSheet 的对应行
	                if (newCellMValue.equals(sourceCellMValue)) {
	                    // 获取 sourceSheet_ps 中 R, S, T 列的值
	                    Cell sourceCellR = sourceRow.getCell(17); // R 列的索引是17
	                    Cell sourceCellS = sourceRow.getCell(18); // S 列的索引是18
	                    Cell sourceCellT = sourceRow.getCell(19); // T 列的索引是19

						Cell newCellR = newRow.createCell(17, CellType.STRING);
		                copyCellContent(sourceCellR, newCellR);
		    	        copyCellStyle(sourceCellR, newCellR);


						Cell newCellS = newRow.createCell(18, CellType.STRING);
		                copyCellContent(sourceCellS, newCellS);
		    	        copyCellStyle(sourceCellS, newCellS);


						Cell newCellT = newRow.createCell(19, CellType.STRING);
		                copyCellContent(sourceCellT, newCellT);
		    	        copyCellStyle(sourceCellT, newCellT);


	                    sourceSheet_ps_falg = true;
	                    break; // 找到对应行后跳出内层循环
	                }
	            }

	            if (sourceSheet_ps_falg == true) {
	            	sourceSheet_ps_falg = false;

	            } else {
                    Cell newCellQ = newRow.createCell(16, CellType.STRING);
	                newCellQ.setCellValue("NEW");
	                // 设置背景色
	                newCellQ.setCellStyle(cellStyle);
	            }

            }

        	copySheetContent_title(newSheet);
	    	copySheetContent_ps_title(newSheet, sourceSheet_ps);

	    }

		public static void getCellBackgroundColor(Workbook workbook) {


            // 获取指定的Sheet
            Sheet sheet = workbook.getSheet("手続内帳票対応表（申告）");
            if (sheet == null) {
                System.out.println("指定的Sheet不存在");
                return;
            }

            // 获取A1单元格
            Row row = sheet.getRow(0); // A1的行是第0行
            if (row == null) {
                System.out.println("第1行不存在");
                return;
            }

            Cell cell = row.getCell(0); // A1的列是第0列
            if (cell == null) {
                System.out.println("A1单元格不存在");
                return;
            }

            // 获取单元格样式
            CellStyle cellStyle = cell.getCellStyle();
            if (cellStyle == null) {
                System.out.println("A1单元格无样式");
                return;
            }

            // 获取背景颜色
            if (cellStyle instanceof XSSFCellStyle) {
                XSSFColor bgColor = ((XSSFCellStyle) cellStyle).getFillForegroundColorColor();
                if (bgColor != null) {
                    byte[] rgb = bgColor.getRGB();
                    System.out.printf("A1单元格背景色: RGB(%d, %d, %d)%n", rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF);
                } else {
                    System.out.println("A1单元格无背景色");
                }
            } else {
                System.out.println("A1单元格背景色不支持读取");
            }


	    }


		public static void formatSheet(Workbook workbook, Sheet sheet) {


			 // 获取当前列的宽度
	        int currentWidth = sheet.getColumnWidth(11);
	        // 设置新列宽为原来的 2 倍
	        int newWidth = currentWidth * 2;
	        sheet.setColumnWidth(11, newWidth);




			// 定义边框样式
			CellStyle borderedStyle = workbook.createCellStyle();
			borderedStyle.setBorderTop(BorderStyle.THIN);
			borderedStyle.setBorderBottom(BorderStyle.THIN);
			borderedStyle.setBorderLeft(BorderStyle.THIN);
			borderedStyle.setBorderRight(BorderStyle.THIN);



			CellStyle borderedStyletWrapText = workbook.createCellStyle();
			borderedStyletWrapText.cloneStyleFrom(borderedStyle); // 在背景样式上叠加边框
			//TODO
			borderedStyletWrapText.setWrapText(true); // 启用自动换行

			// 定义自定义浅蓝色背景样式
			XSSFCellStyle lightBlueStyle = (XSSFCellStyle) workbook.createCellStyle();
			lightBlueStyle.cloneStyleFrom(borderedStyle); // 在背景样式上叠加边框

			// 使用 RGB(153, 204, 255) 设置浅蓝色背景
			XSSFColor customLightBlue = new XSSFColor(new Color(153, 204, 255), null);
			lightBlueStyle.setFillForegroundColor(customLightBlue);
			lightBlueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

			int RowNo = 15;
			// 设置 A 列到 P 列的边框样式
			for (int i = 0; i <= RowNo; i++) {
				for (int j = 0; j < sheet.getLastRowNum() + 1; j++) {
					Row row = sheet.getRow(j);
					if (row == null) {
						row = sheet.createRow(j);
					}
					Cell cell = row.getCell(i);
					if (cell == null) {
						cell = row.createCell(i);
					}
					if (j == 11) {
						cell.setCellStyle(borderedStyletWrapText);

					} else {
						cell.setCellStyle(borderedStyle);

					}

				}
			}

			// 设置 A 列到 J 列的第 P 行到第 3 行的浅蓝色背景
			for (int i = 0; i <= RowNo; i++) {
				for (int j = 0; j <= 2; j++) {
					Row row = sheet.getRow(j);
					if (row == null) {
						row = sheet.createRow(j);
					}
					Cell cell = row.getCell(i);
					if (cell == null) {
						cell = row.createCell(i);
					}
					cell.setCellStyle(lightBlueStyle);
				}
			}

		}

	    private static void copySheetContent(Sheet sourceSheet, Sheet targetSheet) {
	        for (int i = 0; i <= sourceSheet.getLastRowNum(); i++) {
	            Row sourceRow = sourceSheet.getRow(i);
	            Row targetRow = targetSheet.createRow(i);

	            if (sourceRow != null) {
	                copyRowContent(sourceRow, targetRow);
	            }
	        }
	    }

	    private static void copyRowContent(Row sourceRow, Row targetRow) {
	        for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
	            Cell sourceCell = sourceRow.getCell(j);
	            Cell targetCell = targetRow.createCell(j);

	            if (sourceCell != null) {
	                copyCellContent(sourceCell, targetCell);
	            }
	        }
	    }

	    private static void copyCellContent(Cell sourceCell, Cell targetCell) {
	    	if (sourceCell == null || targetCell == null) {
	    		return;
	    	}
	        // 复制单元格的内容
	        switch (sourceCell.getCellType()) {
	            case STRING:
	                targetCell.setCellValue(sourceCell.getStringCellValue());
	                break;
	            case NUMERIC:
	                targetCell.setCellValue(sourceCell.getNumericCellValue());
	                break;
	            case BOOLEAN:
	                targetCell.setCellValue(sourceCell.getBooleanCellValue());
	                break;
	            case FORMULA:
	                targetCell.setCellFormula(sourceCell.getCellFormula());
	                break;
	            case BLANK:
	                targetCell.setBlank();
	                break;
	            default:
	                break;
	        }
	    }


	    private static void copyCellStyle(Cell sourceCell, Cell targetCell) {
	        if (sourceCell == null || targetCell == null) {
	            return;
	        }

	        // 获取源单元格样式和字体
	        CellStyle sourceCellStyle = sourceCell.getCellStyle();
	        Workbook sourceWorkbook = sourceCell.getSheet().getWorkbook();
	        Workbook targetWorkbook = targetCell.getSheet().getWorkbook();

	        // 创建目标单元格样式
	        CellStyle targetCellStyle = targetWorkbook.createCellStyle();
	        targetCellStyle.cloneStyleFrom(sourceCellStyle);

	        // 复制字体
	        Font sourceFont = sourceWorkbook.getFontAt(sourceCellStyle.getFontIndex());
	        Font targetFont = targetWorkbook.createFont();

	        // 手动复制字体属性
	        targetFont.setFontName(sourceFont.getFontName());
	        targetFont.setFontHeightInPoints(sourceFont.getFontHeightInPoints());

	     // 推荐使用
//	        targetFont.setFontHeightInPoints((short) 9); // 字体大小为 9 点
	        // 或者更精细地设置
//	        targetFont.setFontHeight((short) (9 * 20)); // 字体大小为 9 点（Twips 单位）

	        targetFont.setBold(sourceFont.getBold());
	        targetFont.setItalic(sourceFont.getItalic());
	        targetFont.setStrikeout(sourceFont.getStrikeout());
	        targetFont.setColor(sourceFont.getColor());
	        targetFont.setUnderline(sourceFont.getUnderline());
	        targetFont.setTypeOffset(sourceFont.getTypeOffset());

	        // 将新字体设置到目标单元格样式
	        targetCellStyle.setFont(targetFont);

	        // 应用样式到目标单元格
	        targetCell.setCellStyle(targetCellStyle);
	    }

	    private static void copyMergedRegions(Sheet sourceSheet, Sheet targetSheet) {
	        // 复制合并区域
	        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
	            CellRangeAddress sourceRange = sourceSheet.getMergedRegion(i);
	            // 复制合并区域到目标Sheet
	            targetSheet.addMergedRegion(new CellRangeAddress(
	                    sourceRange.getFirstRow(),
	                    sourceRange.getLastRow(),
	                    sourceRange.getFirstColumn(),
	                    sourceRange.getLastColumn()
	            ));
	        }
	    }




	public static void hideRowsBasedOnKey(String filePath, String key) {
 		logger.info("start hideRowsBasedOnKey");
		FileInputStream fis = null;
		Workbook workbook = null;
        try {

        	int rowIndex_hide = 4;
        	if (filePath.indexOf("手続内帳票対応表(申請）") > -1) {
        		rowIndex_hide = 3;
        	}
            // 打开 Excel 文件
            fis = new FileInputStream(filePath);
            workbook = WorkbookFactory.create(fis);

            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                Iterator<Row> rowIterator = sheet.iterator();
                int rowIndex = 0;

                String idOld = "";
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    rowIndex++;
                    if (rowIndex <= rowIndex_hide) {
                        continue;
                    }

                    int count = 0;
                    StringBuilder csvLine = new StringBuilder();
                    for (int colIndex = 0; colIndex < 1; colIndex++) {
                    	++count;

                        Cell cell = row.getCell(colIndex);

                        String value;
                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            // 如果单元格为空并且是合并单元格，则获取合并单元格的值
                            value = FuncUtilsAiEtax.isMergedRegion(sheet, rowIndex - 1, colIndex)
                                    ? FuncUtilsAiEtax.getMergedCellValue(sheet, rowIndex - 1, colIndex)
                                    : "";
                        } else {
                            value = FuncUtilsExcel.getCellValueAsString(cell).replaceAll("\r\n|\r|\n", "");
                        }


                        if (!value.equals(key)) {
                        	row.setZeroHeight(true); // 隐藏该行
                        }
                    }
                }
            }

            // 保存更改到文件
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                workbook.write(fos);
                fos.close();

            } finally {
                // 关闭资源
                if (fis != null) {
                    fis.close();
                }
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
 		logger.info("end");
    }


	public static void AiEtax_get_shiyangshu(String shouxu_id, String shouxu_banben) {





    }





	static String Template = ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
			+ "<DATA id=\"DATA\" xmlns=\"http://xml.e-tax.nta.go.jp/XSD/shohi\" xmlns:gen=\"http://xml.e-tax.nta.go.jp/XSD/general\" xmlns:kyo=\"http://xml.e-tax.nta.go.jp/XSD/kyotsu\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "  <RSH0020 VR=\"23.2.0\" id=\"RSH0020\">"
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
			+ "  </RSH0020>"
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

	static HashMap<String, String> tblMap = new HashMap<>();

	private static LinkedHashMap<String, String> findSheetsStartingWithPS_IT(t_etax_account_infoExBean t_etax_account_infoExBean
			, Object objBean, String filePath, String path_Excel, String path_ETAX_output) throws Exception {
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

                                    if ("NOZEISHA_NM_KN".equals(cValue)) {
                                    	cValue=cValue;
                                    }

                                    //PS区分
                                    if ("删除".equals(rValue)) {

                                        continue;
                                    }



                                    //PS区分
                                    if (rValue.equals("固定")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][" + rValue + "][M列值: " + mValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);


                                    } else if (rValue.contains("DB")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][" + rValue + "][M列值: " + mValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "]";
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
                                    				sValue = FuncUtils.getBeanValue(objBean, sValue);

                                    			}

                                			}


                                		} else {
                                			logger.error("没有找到DB定义项目【】格式写法:" + sValue);
                                        	continue;

                                		}



                                    } else  if (rValue.contains("定义")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][" + rValue + "][M列值: " + mValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	TreeMap<String, String[]> shouxu = FuncUtilsAiEtax.PropertyCsvMap_shouxu.getOrDefault(sValue, new TreeMap<>());
//                                    	PropertyCsvMap_shouxu
//                                    	└── firstColumn (String)
//                                    	    └── versionKey (String) : rowData (String[])
                                    	String[] csv_shouxu = shouxu.get("");


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
											sValue = ""
													+ "<procedure_CD>RSH0020</procedure_CD>"
													+ "<procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
													+ ""
													+ "";
										} else {
                                    	    logger.error("TreeMap is empty.");
                                    	}

                                    }



                                    // 目标字符串作成
                                    sValue = "<" + cValue + " ID=\"" + cValue + "\">" + sValue + "</" + cValue + ">";
                                    content = content +"\n"+ sValue;


                                } else if (j == 2) {


                                    if ("ATC00120".equals(cValue)) {
                                    	cValue=cValue;
                                    }

                                    //PS区分
                                    if (rValue.contains("【計算】")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][" + rValue + "][M列值: " + mValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "]";
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


            		// 获取系统类型属性
            		String osName = System.getProperty("os.name");
            		// 您可以根据不同的系统类型执行不同的操作
            		if (osName.toLowerCase().contains("windows")) {
            			logger.info("这是Windows系统");
            			// 在Windows系统上执行特定操作
            			//TODO
//            			break;

            		} else if (osName.toLowerCase().contains("linux")) {
            			logger.info("这是Linux系统");
            			// 在Linux系统上执行特定操作
            		}

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            // 关闭资源
            if (fis != null) {
                fis.close();
            }
        }
		return xtxMap;
	}




}