package com.panda.batch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_amazon_csvDao;
import com.panda.utils.FuncUtils;

/*
 * 销售额数据抽出和计算
 */
public class AmazonCsvToDB {

	private static Logger logger = Logger.getLogger(AmazonCsvToDB.class.toString());

	public static void main(String[] args) {
		try {

			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_amazon_csvDao t_etax_amazon_csvDao = new t_etax_amazon_csvDao();

			String yyyy = "2023";

//			String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\20240206名单 处理328行到386行 无跳过.xlsx";
			String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\0217PDSK编号3.xlsx";
			int rowS = 2;			int rowE = -1;			int columnS = 1; int columnE = 19;	int column_excelDataHashMapKey = 1;
			Map<String, Map<String, String>> excelDataHashMap19 = FuncUtils.get_excelDataHashMap(path19, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);

			String path = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv";
			String path_csvToDB = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv\\666csvToDB";
			String path_csvToDBSkip = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv\\666csvToDBSkip";
			String path_csvToDBNG = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv\\666csvToDBNG";


			LinkedHashMap<String, File> HashMap_path_csvToDB = new LinkedHashMap<>();
            for (File myFile : FuncUtils.listFilesInFolder(path_csvToDB)) {
            	HashMap_path_csvToDB.put(myFile.getName(), myFile);
            }

			LinkedHashMap<String, Integer> HashMap_path_FileLineCounter = FileLineCounter.getFileLineCounter(path);

			/*
			 * 获取上传的多个文件部分
			 */
			File directory = new File(path);

			if (!directory.exists() || !directory.isDirectory()) {
				logger.debug("指定的路径不是一个有效的目录。");
				return;
			}

			File[] files = directory.listFiles();

			if (files == null || files.length == 0) {
				logger.debug("目录下没有文件。");
				return;
			}

			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					int fileLineCounter = HashMap_path_FileLineCounter.get(file.getName());
					logger.debug("Lines: " + fileLineCounter + ", File: " + file.getName());

					fileName = FuncUtils.toHalfWidth(fileName);
					fileName = fileName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

					int count_t_etax_amazon_csvDao = -1;
					for (Entry<String, File> entry_excelDataHashMap : HashMap_path_csvToDB.entrySet()) {
						File excelValue = entry_excelDataHashMap.getValue();
						String key = FuncUtils.toHalfWidth(excelValue.getName());
						key = key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

						if (fileName.contains(key)) {
							File FileNew = new File(path_csvToDBSkip + "\\" + file.getName());
				            Files.move(Paths.get(file.getPath()), Paths.get(FileNew.getPath()), StandardCopyOption.REPLACE_EXISTING);
							count_t_etax_amazon_csvDao = -2;
				            break;
						}
					}
					if (count_t_etax_amazon_csvDao == -2) {
						continue;
					}

					// 循环 excelDataHashMap19 的键，判断是否与当前 PDF 文件名相同
					for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19.entrySet()) {
						Map<String, String> excelValue = entry_excelDataHashMap.getValue();
						String key = excelValue.get("1");
						key = FuncUtils.toHalfWidth(key);
						key = key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

						if (fileName.contains(key) || fileName.contains(excelValue.get("16")) || fileName.contains(excelValue.get("0").toLowerCase())) {
							try {
								String yyyymmdd_count = t_etax_account_resDao.selec_where_bangou(excelValue.get("4"));

								int count =  t_etax_amazon_csvDao.select_SIZE_where_yyyymmdd_count_by_yyyy(yyyymmdd_count, yyyy);

								if (fileLineCounter == 0) {
									count_t_etax_amazon_csvDao = 0;

								} else if (fileLineCounter - 2 > count) {
									t_etax_amazon_csvDao.delete_yyyy_where_yyyymmdd_count(yyyy, yyyymmdd_count);
									count_t_etax_amazon_csvDao = t_etax_amazon_csvDao.INSERT(yyyymmdd_count, yyyy, file.getPath(), fileLineCounter);

								} else {

								}

								break;

							} catch (Exception e) {
								e.printStackTrace();

								File FileNew = new File(path_csvToDBNG + "\\" + file.getName());
					            Files.move(Paths.get(file.getPath()), Paths.get(FileNew.getPath()), StandardCopyOption.REPLACE_EXISTING);
								count_t_etax_amazon_csvDao = -2;

							}
							break;

						}
					}

					if (count_t_etax_amazon_csvDao == -2) {
						continue;
					}

					File FileNew;
					if (count_t_etax_amazon_csvDao == -1) {
						FileNew = new File(path_csvToDBSkip + "\\" + file.getName());

					} else {
						FileNew = new File(path_csvToDB + "\\" + file.getName());

					}
					Files.move(Paths.get(file.getPath()), Paths.get(FileNew.getPath()), StandardCopyOption.REPLACE_EXISTING);
				}



			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
