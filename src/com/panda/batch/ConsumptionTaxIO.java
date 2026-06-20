package com.panda.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;

/*
 * 消费税申告生成
 */
public class ConsumptionTaxIO {

	private static Logger logger = Logger.getLogger(ConsumptionTaxIO.class.toString());

	public static void main(String[] args) {

		// 获取当前日期时间
		Date currentDate = new Date();
		// 设置日期时间格式
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		// 格式化日期时间
		String yyyymmddhhmmss = dateFormat.format(currentDate);

		String mainPath = "C:/Users/Administrator/Desktop/消費税申告試算データ";

		//2 生成695-699的テンプレートフォルダ
		int countS = 701;
		int countE = 701;

		exe(yyyymmddhhmmss, mainPath, countS, countE);

		return;
	}


	public static void exe(String yyyymmddhhmmss, String mainPath, int countS, int countE) {
		try {


			//アップロードするフォルダ
			String sourceFilePath = mainPath + "/output" + yyyymmddhhmmss;;
			/*
			 * データ読み
			 */

//			Map<String, String> bugCSVHistory = new LinkedHashMap<String, String>();

			//20240206 22：36这两家麻烦单独处理一下
//			bugCSVHistory.put("Taiyuan QiQuTiaoDong DianZiShangWu Co ltd 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Beijing YiLangChuangXiang Technology Co. Ltd   2023 Amazon Transaction report.csv", "");

			//TODO


//			String path19 = "E:/日本-PANDASERVICE株式会社/电子申请数据/20240513　PANDA　SERVICE　R５消費税申告　お客様情報まとめ(1).xlsx";
//			int rowS = 685;			int rowE = -1;			int columnS = 1; int columnE = 19;	int column_excelDataHashMapKey = 1;

//			Map<String, Map<String, String>> excelDataHashMap19 = FuncUtils.get_excelDataHashMap(path19, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);


//			String pathSkip = "C:/Users/Administrator/Desktop/消費税申告試算データ/0204王迪不统一处理名单.xlsx";
//			Map<String, Map<String, String>> excelDataHashMapSkip = FuncUtils.get_excelDataHashMap(pathSkip, 0, rowE, columnS, columnE, column_excelDataHashMapKey);

			String path = mainPath + "/消費税申告試算データ.xlsx";
			File dataModelFile = new File(path);

//			String pathNCC = "C:/Users/Administrator/Desktop/消費税申告試算データ/NewFile20240203　消費税申告書テンプレート.txt";
			String pathNCC = mainPath + "/NewFile20240213　消費税異動届出書テンプレート.xml";
			File dataModelFileNCC = new File(pathNCC);
			if (dataModelFileNCC.length() == 0) {
				logger.debug(pathNCC + " → NG:ncc File data model ");
				return;
			}


			/*
			 * 登録データ準備
			 */
			int count = 0;
	        for (int k = countS; k <= countE; k++) {
				++count;
				logger.info("处理个数 : " + count);
				String PDSK= "PDSK23" + String.format("%04d", k);
				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);

				com.panda.dao.t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

				Map<String, String> excelValue = new HashMap<String, String>();
				excelValue.put("0", PDSK);
				excelValue.put("1", t_etax_account_infoExBean.getCompanyName_English());
//				0
//				管理番号
//				1
//				納税者名（英字）
//				4
//				ETAX番号
				excelValue.put("4", t_etax_account_infoExBean.getBangou());
//				5
//				法人番号
				excelValue.put("5", t_etax_account_infoExBean.getHoujinBangou());
//				7
//				納税者名（英字　２５全角文字）
				excelValue.put("7", FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25));
//				13
//				納税者名カタカナ（２５全角文字）
				excelValue.put("13", FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_pianjiaming(), 25));
//				9
//				代表者名（英字　２５全角文字）
				excelValue.put("9", FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getDaibiaoName_English(), 25));
//				15
//				代表者名カタカナ（２５全角文字）
				excelValue.put("15", FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getDaibiaoName_pianjiaming(), 25));






//				if (excelDataHashMapSkip.containsKey(excelValue.get("0"))) {
//					continue;
//				}

				//bugCSVHistory S
//				int count_bugCSVHistory = 0;
//				for (Map.Entry<String, String> entry : bugCSVHistory.entrySet()) {
//					String fileName = entry.getKey();
//                	fileName = fileName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
//					if (fileName.indexOf(excelValue.get("1").toLowerCase().replaceAll("[^a-zA-Z0-9]", "")) > -1) {
//						++count_bugCSVHistory;
//						continue;
//					} else {
//
//					}
//				}
//				if (count_bugCSVHistory > 0) {
//
//				} else {
//					continue;
//
//				}
				//bugCSVHistory E

				String destinationFilePath = sourceFilePath + "/" + excelValue.get("0") + "_" + excelValue.get("1");
				File directory = new File(destinationFilePath);
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}


//				directory = new File(destinationFilePath + "/nccファイル　xtxファイル");
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}
//				directory = new File(destinationFilePath + "/R５年度　Amazon Transaction report");
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}
//				directory = new File(destinationFilePath + "/前年度・前々年度売上資料");
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}
//				directory = new File(destinationFilePath + "/謄本・定款");
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}
//				directory = new File(destinationFilePath + "/提出済み税務書類");
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}



				directory = new File(destinationFilePath + "/nccファイル　xtxファイル");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(destinationFilePath + "/会計資料");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(destinationFilePath + "/申告確認書");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(destinationFilePath + "/謄本・定款");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(destinationFilePath + "/提出済み税務書類");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}





				String 納税者名 = excelValue.get("1");
				if (納税者名.length() > 25) {
					納税者名 = 納税者名.substring(0, 25);
				}


				destinationFilePath = destinationFilePath + "/" + excelValue.get("0") + "_" + 納税者名 + "_"
						+ dataModelFile.getName();
				FuncUtils.copyFile(path, destinationFilePath);
				logger.info("文件复制成功！" + destinationFilePath);

				String destinationFilePathExcel = destinationFilePath;

				// 读取 Excel 文件
				FileInputStream fileInputStream = new FileInputStream(destinationFilePathExcel);
				Workbook workbook = WorkbookFactory.create(fileInputStream);

				// 获取第一个工作表
				Sheet sheet = workbook.getSheetAt(0);

				Row row = sheet.getRow(1); // 行索引从 0 开始
				Cell cell = row.getCell(5); //列索引从 0 开始
				cell.setCellValue(excelValue.get("0"));

				cell = row.getCell(6); //列索引从 0 开始
				cell.setCellValue(納税者名);

				//	        输入列	列数
				//	        E	4
				//	        F	5
				//	        H	7
				//	        N	13
				//	        J	9
				//	        P	15
				row = sheet.getRow(3); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("4"));

				row = sheet.getRow(4); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("5"));

				row = sheet.getRow(5); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("7"));

				row = sheet.getRow(6); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("13"));

				row = sheet.getRow(7); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("9"));

				row = sheet.getRow(8); // 行索引从 0 开始
				cell = row.getCell(1); //列索引从 0 开始
				cell.setCellValue(excelValue.get("15"));

				/*
				 * csv查找
				 */
				String folderPath = "C:/Users/Administrator/Desktop/消費税申告試算データ/csv";
				String searchString = excelValue.get("1");

				List<String> matchingFiles = searchFiles(folderPath, searchString);
				// 循环处理匹配的文件
				for (String filePath : matchingFiles) {
					File folder = new File(filePath);

					File folder1 = new File(destinationFilePath);
					String absolutePath1 = folder1.getParent();

					destinationFilePath = absolutePath1 + "/R５年度　Amazon Transaction report/";
					directory = new File(destinationFilePath);
					if (!directory.exists() || !directory.isDirectory()) {
						directory.mkdirs();
					}

					FuncUtils.copyFile(filePath, destinationFilePath + folder.getName());

					if (folder.getName().indexOf("計算用") > -1) {

						try (BufferedReader reader = new BufferedReader(new FileReader(folder.getPath()))) {
							String line;
							String lastLine = null;

							// 逐行读取文件
							while ((line = reader.readLine()) != null) {
								// 每次读取都更新最后一行数据
								lastLine = line;
							}

							if (lastLine != null) {
								logger.info("最后一行数据: " + lastLine);
							} else {
								logger.info("CSV文件为空或读取失败。");
							}

							//			            N 对应索引 14
							//			            O 对应索引 15
							//			            P 对应索引 16
							//			            Q 对应索引 17
							//			            R 对应索引 18
							//			            S 对应索引 19
							//			            U 对应索引 21
							//			            V 对应索引 22
							String[] lineList = lastLine.replaceAll("\"", "").split(",");


							if  (Double.parseDouble(lineList[27]) <= 0.0) {
								FuncUtils.copyFile(folder.getPath(), folder.getParent() + "/csvTotal0/" + folder.getName());

							}



							int i = 2;
							int j = 12;
							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							++j;
							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

							row = sheet.getRow(++i); // 行索引从 0 开始
							cell = row.getCell(6); //列索引从 0 开始
							cell.setCellValue(Double.parseDouble(lineList[++j]));

						}

					}

				}

				// 强制Excel重新计算所有的公式
				workbook.setForceFormulaRecalculation(true);

				// 将修改后的工作簿保存到文件
				FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePathExcel);
				workbook.write(fileOutputStream);

				// 关闭文件流
				fileInputStream.close();
				fileOutputStream.close();


				/*
				 * ncc
				 */
				File folderExcel = new File(destinationFilePathExcel);
//				String pathNewNCC = folderExcel.getParent() + "/" + excelValue.get("7") + ".ncc";
//				String pathNewNCC = folderExcel.getParent() + "/" + excelValue.get("0") + "_" + 納税者名 + ".ncc";
				String pathNewNCC = sourceFilePath + "/" + excelValue.get("0") + "_" + 納税者名 + ".ncc";

				String fileContent = FuncUtils.readFileContent(dataModelFileNCC);

				FileInputStream fis = new FileInputStream(destinationFilePathExcel);
				workbook = WorkbookFactory.create(fis);
				sheet = workbook.getSheetAt(0);
				// 遍历每一行
				Iterator<Row> rowIterator = sheet.iterator();
				while (rowIterator.hasNext()) {
					row = rowIterator.next();
					// 获取 A 列和 B 列的数据
					Cell cellA = row.getCell(0, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
					Cell cellB = row.getCell(1, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

					//							// 跳过第一行（假设第一行为标题）
					//							if (row.getRowNum() == 0) {
					//								continue;
					//							}

					// 将 A 列和 B 列的数据存储到 excelData HashMap
					String key = (cellA != null) ? cellA.toString() : "";
					String value = "";

					if (cellB != null) {
						if (cellB.getCellType() == CellType.FORMULA) {
							// 如果单元格中包含公式，则计算并输出结果
							value = FuncUtils.evaluateFormulaCell(cellB, workbook);
						} else {
							// 如果是其他类型的单元格，直接输出值
							value = cellB.toString();
						}

					}

					//				logger.debug("key : " + key+ " value : " + value);
					fileContent = fileContent.replaceAll(key, value);
				}

				// 写入文件
				FileWriter writer = new FileWriter(pathNewNCC);
				writer.write(fileContent);
				writer.close();
				logger.debug("File saved: " + pathNewNCC);


//				/*
//				 * PDF
//				 */
//				String pathPDF = "C:/Users/Administrator/Desktop/消費税申告試算データ/消費税申告書テンプレーNEW.pdf";
//				File dataModelFilePDF = new File(pathPDF);
//				if (dataModelFilePDF.length() == 0) {
//					logger.debug(pathPDF + " → NG:excel File data model ");
//					return;
//				}
//
//				String destinationFilePathPDF = sourceFilePath + "/" + excelValue.get("0") + "_" + excelValue.get("1") + "/" + excelValue.get("1") + "消費税申告書.pdf";
//
//	            // 加载PDF模板
//	            PDDocument document = PDDocument.load(new File(pathPDF));
//	            // ドキュメントカタログを取得
//	            PDDocumentCatalog catalog = document.getDocumentCatalog();
//	            // AcroForm（交互式フォーム）を取得
//	            PDAcroForm acroForm = catalog.getAcroForm();
//	            // 假设 pdField 是你设置值的表单字段对象
//	            acroForm.setNeedAppearances(true);
//
//
//
//
//
////	            // 移除所有的安全设置，将文档设置为只读
////	            document.setAllSecurityToBeRemoved(true);
////	            // 关闭文档，不保存修改
////                document.setAllSecurityToBeRemoved(true);
////
////	            // アクセス許可を取得
////	            AccessPermission permission = new AccessPermission();
////
////	            // 文書を読み取り専用に設定
////	            permission.setReadOnly();
////
////	            // 標準の保護ポリシーを作成
////	            StandardProtectionPolicy policy = new StandardProtectionPolicy("", "", permission);
////
////	            // 保護ポリシーを文書に設定
////	            document.protect(policy);
//
//
//
//            	// 遍历所有表单字段
//	            for (PDField field : acroForm.getFieldTree()) {
//	            	// 设置为只读
//	                field.setReadOnly(true);
//	            }
//
//
////	            {0=PDSK230193
////	            		1=shen zhen shi yi shuo ke ji you xian gong si
////	            		2=615
////	            		Building8,1970 Technology Town,Mingzhi Community,Mingzhi Street,Longhua District,ShenzhenShi
////	            		3=Jiang Xueyuan
////	            		4=2344042800920001
////	            		5=5700150109350
////	            		6=T5700150109350
////	            		7=ｓｈｅｎ　ｚｈｅｎ　ｓｈｉ　ｙｉ　ｓｈｕｏ　ｋｅ
////	            		8=６１５，　Ｂｕｉｌｄｉｎｇ８，１９７０　Ｔｅｃｈｎ
////	            		9=Ｊｉａｎｇ　Ｘｕｅｙｕａｎ
////	            		10=シンセンシイセキカギユウゲンコウシ
////	            		11=シェン ジェン シー ロン フワー チー ミン ジー ジエ ダオ ミン ジー ショー チー コー ジー ユエン ドン
////	            		12=ジャンガクゲン
////	            		13=シンセンシイセキカギユウゲンコウシ
////	            		14=シェン ジェン シー ロン フワー チー ミン ジ
////	            		15=ジャンガクゲン
////	            		16=深圳市易硕科技有限公司
////	            		17=深圳市龙华区民治街道民治社区1970科技园8栋615
////	            		18=蒋学元}
//
//
//	            String value = "";
//	            String valueB6 = "";
//	            String valueB8 = "";
//	            PDTextField fieldB;
//
//
//
//
//	            cell = sheet.getRow(8).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            fieldB = (PDTextField) acroForm.getField("B9");fieldB.setValue(value);
//
//	            cell = sheet.getRow(7).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            fieldB = (PDTextField) acroForm.getField("B8");fieldB.setValue(value);
//	            valueB8 = value;
//
//	            //如果是个人，这三个地方是空白的
//	            //TODO 上系统了，用user_type管理
//	            cell = sheet.getRow(5).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            valueB6 = value;
//
//	            if (valueB8.equals(valueB6)) {
//
//	            } else {
//		            fieldB = (PDTextField) acroForm.getField("B6");fieldB.setValue(value);
//
//		            cell = sheet.getRow(6).getCell(1);
//		            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//		            fieldB = (PDTextField) acroForm.getField("B7");fieldB.setValue(value);
//
//		            cell = sheet.getRow(4).getCell(1);
//		            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//		            if (value.length() == 13) {
//		            	// 截取一位
//		            	String part1 = value.substring(0, 1);
//		            	// 截取四位
//		            	String part2 = value.substring(1, 5);
//		            	String part3 = value.substring(5, 9);
//		            	String part4 = value.substring(9, 13);
//		            	fieldB = (PDTextField) acroForm.getField("B5_1");fieldB.setValue(part1);
//		            	fieldB = (PDTextField) acroForm.getField("B5_2");fieldB.setValue(part2);
//		            	fieldB = (PDTextField) acroForm.getField("B5_3");fieldB.setValue(part3);
//		            	fieldB = (PDTextField) acroForm.getField("B5_4");fieldB.setValue(part4);
//		            }
//
//	            }
//
//	            cell = sheet.getRow(13).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B14");fieldB.setValue(value);
//
//	            cell = sheet.getRow(14).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B15");fieldB.setValue(value);
//
//	            cell = sheet.getRow(15).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B16");fieldB.setValue(value);
//
//	            cell = sheet.getRow(15).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B16");fieldB.setValue(value);
//
//	            cell = sheet.getRow(16).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B17");fieldB.setValue(value);
//
//	            cell = sheet.getRow(16).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B17");fieldB.setValue(value);
//
//	            cell = sheet.getRow(12).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B13");fieldB.setValue(value);
//
//	            cell = sheet.getRow(16).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B17");fieldB.setValue(value);
//
//	            cell = sheet.getRow(17).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B18");fieldB.setValue(value);
//
//	            cell = sheet.getRow(17).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B18");fieldB.setValue(value);
//
//	            cell = sheet.getRow(18).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B19");fieldB.setValue(value);
//
//	            cell = sheet.getRow(20).getCell(1);
//	            value = FuncUtils.evaluateFormulaCell(cell, workbook);
//	            if (value.length() > 3) {
//	            	value  = value.substring(0, value.length() - 3);
//	            } else {
//	            	value = "0";
//	            }
//	            value = new DecimalFormat("#,##0").format(Integer.parseInt(value));
//	            fieldB = (PDTextField) acroForm.getField("B21");fieldB.setValue(value);
//
//	            // 保存修改后的文档
//	            document.save(destinationFilePathPDF);
//	            // 关闭文档
//	            document.close();

			}

			ConsumptionTaxSearch.get_tax_negative_number(sourceFilePath);

		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}


		logger.info("end");
	}





	   public static List<String> searchFiles(String folderPath, String searchString) {
	        List<String> result = new ArrayList<>();
	        File folder = new File(folderPath);

	        if (folder.exists() && folder.isDirectory()) {
	            File[] files = folder.listFiles();

	            if (files != null) {
	                for (File file : files) {
	                	String fileName = file.getName();
	                	fileName = fileName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");;
	                	searchString = searchString.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");;
	                    if (file.isFile() && fileName.contains(searchString)) {
	                        result.add(file.getAbsolutePath());
	                    } else if (file.isDirectory()) {
	                        // 如果是子文件夹，递归搜索
//	                        result.addAll(searchFiles(file.getAbsolutePath(), searchString));
	                    }
	                }
	            } else {
	                logger.debug("文件夹为空或无法访问。");
	            }
	        } else {
	            logger.debug("指定路径不是一个有效的文件夹。");
	        }

	        return result;
	    }
}
