package com.panda.batch;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.panda.utils.FuncUtils;

/*
 *
 */
public class ConsumptionTaxSearch {

	private static Logger logger = Logger.getLogger(ConsumptionTaxSearch.class.toString());

	public static void main(String[] args) {

		try {
			/*
			 * 有没有最后的消费税是负数的
			 */
//			get_tax_negative_number("");

			/*
			 * GET公司名单
			 */
			get_gongsi_mingdan();

		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.info("end");
		return;


	}

	private static void get_gongsi_mingdan() throws IOException {
		Map<String, String> gongsiMap = new LinkedHashMap<String, String>();
		gongsiMap.put("Botaojiaye (Beijing) Sanitary Ware Co., Ltd   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("guangzhou keyuan shengwukeji youxiangongsi   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("hebeiruiguanguojimaoyiyouxiangongsi   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Jiaxing Sai en Trading Co.,Ltd 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Nan Yang Jia Bei Shang Mao  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Hope Ecommerce Co.LTD  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("dongguanshitaijingyundianzikejiyouxiangongsi   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("dongguanzhenyidakejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("He Haifang 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Wang Xiaoan 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("DONGGUAN XINRUI KEJI YOUXIANGONGSI 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("dongguanshijieshidaqingjieyongpinyouxiangongsi  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Chen Meiliang 2023 Amazon Transaction report.csv", "");
		gongsiMap.put("fujianshenglongyanshiluolingmaoyiyouxianzerengongsi  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("guangming li  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Guangzhou Bison Cloting Co.,Ltd  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("henankuaishouyunkejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("HONGKONG UMEDIA LIMITED  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Huizhou Zhongzhan Technology Co., Ltd.  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Jiangsu Zhenchang Educational Equipment Co., Ltd  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("JiaXingAiQuHuWaiYongPinYouXianGongSi   2023 Amazon Transaction report .csv", "");
		gongsiMap.put("kun shan shu lan xin xi ke ji you xian gong si  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("LiaochengChaoxingyitengdianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("LONGYAN SHI WANRUI NONGYE FAZHANG YOU XIAN GONG SI  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("ONEKEY LIMITED  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("pujiangxianjinchaidianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("pujiangxianliushicuidianzishangwuyouxiangongsi   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("pujiangxianrongxingdianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Quanzhou Hanyu Trading Co. LTD  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Ruian LIHER Trading Co., Ltd.  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shen Zhen Ding Jiang Ke Ji You Xian Gong Si   2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shen Zhen Shi Mo Deng Ke Ji You Xian Gong Si  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Bullfrog Technology Co., Ltd   2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Shenzhen Fast sales ecommerce Co.,Ltd.  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Luerman Maoyi Youxian Gongsi 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Qidacheng Trading Co., Ltd.  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Shenzhen Xingyiheng Technology Co., Ltd.  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Youmingda Technology Co., Ltd  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Shenzhen Zhuoshuo technology service co.ltd  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("shenzhenfangcaoyiyimaoyiyouxiangongsi  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("shenzhenshikaershangwudianzikejiyouxiangongsi   2023 Amazon Transaction report .csv", "");
		gongsiMap.put("shenzhenshishengyitangkejiyouxiangongsi  2023 Amazon Transaction report.csv", "");
		gongsiMap.put("shenzhishishizhirankejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Simpeak Technology Co.,Limited 2023 Amazon Transaction report  .csv", "");
		gongsiMap.put("Xiamen Yong Zheng Feng Trade Co., Ltd 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("YiWuShiRuiRanMaoYiYouXianGongSi 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("yuju home textile natong co., LTD  2023 Amazon Transaction report  .csv", "");
		gongsiMap.put("zhijiangshiguanghuoshangmaoyouxiangongsi 2023 Amazon Transaction report.csv", "");
		gongsiMap.put("Zyurong Development Limited 2023 Amazon Transaction report .csv", "");
		gongsiMap.put("Yiwushizuojundianzishangwuyouxiangongsi 2023 Amazon Transaction report.csv", "");

		int rowS = 2;		int rowE = -1;	int columnS = 1; int columnE = 19;	int column_excelDataHashMapKey = -1;
//		String path19_1 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\mingdan0203.xlsx";
//		Map<String, Map<String, String>> excelDataHashMap19_1 = FuncUtils.get_excelDataHashMap(path19_1, rowS, rowE, 19);
//		String path19_2 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\20240206名单 处理328行到386行 无跳过.xlsx";
//		Map<String, Map<String, String>> excelDataHashMap19_2 = FuncUtils.get_excelDataHashMap(path19_2, rowS, rowE, 19);
//
//        Map<String, Map<String, String>> excelDataHashMap19 = mergeMaps(excelDataHashMap19_1, excelDataHashMap19_2);


		String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\0217PDSK编号3.xlsx";
		Map<String, Map<String, String>> excelDataHashMap19 = FuncUtils.get_excelDataHashMap(path19, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);


		int count = 0;
		for (Map.Entry<String, String> entry : gongsiMap.entrySet()) {
			++count;
//			logger.info("处理个数 : " + count);

			String gongsiName = entry.getKey();
			gongsiName = gongsiName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
//			logger.debug("[gongsiMap]" + gongsiName);

			for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19.entrySet()) {
				Map<String, String> excelValue = entry_excelDataHashMap.getValue();



            	String fileName = excelValue.get("1").toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
				if (gongsiName.indexOf(fileName) > -1) {
			        // 将Map的值用逗号连接成字符串
			        String joinedValues = FuncUtils.joinMapValues(excelValue);
					logger.info("处理个数 : " + count + " " + joinedValues);
				}

			}

		}

	}


	 private static Map<String, Map<String, String>> mergeMaps(
	            Map<String, Map<String, String>> map1,
	            Map<String, Map<String, String>> map2) {
	        Map<String, Map<String, String>> mergedMap = new LinkedHashMap<>();

	        // 合并map1的值
	        for (Map.Entry<String, Map<String, String>> entry : map1.entrySet()) {
	            String key = entry.getKey();
	            Map<String, String> value = entry.getValue();
	            mergedMap.put(key, new LinkedHashMap<>(value)); // 使用新的HashMap以防止后续修改原始Map
	        }

	        // 合并map2的值
	        for (Map.Entry<String, Map<String, String>> entry : map2.entrySet()) {
	            String key = entry.getKey();
	            Map<String, String> value = entry.getValue();
	            mergedMap.merge(key, new LinkedHashMap<>(value), (map1Value, map2Value) -> {
	                map1Value.putAll(map2Value); // 使用merge函数合并内部Map的值
	                return map1Value;
	            });
	        }

	        return mergedMap;
	    }

	/*
	 * 有没有最后的消费税是负数的
	 */
	public static void get_tax_negative_number(String folderPath) {
		String keyword = "消費税申告試算データ";
		Map<String, String> tax_negative_number = new LinkedHashMap<String, String>();
		if (StringUtils.isEmpty(folderPath)) {
			//	String folderPath = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\outputALL";
			folderPath = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\output20240204";
		}


		List<String> foundFiles = findExcelFiles(folderPath, keyword);

//		if (foundFiles.isEmpty()) {
//			logger.info("未找到包含关键字的Excel文件。");
//		} else {
//			logger.info("包含关键字的Excel文件：");
//		    for (String fileName : foundFiles) {
//		    	logger.info(fileName);
//		    }
//		}


		for (String fileNameExcel : foundFiles) {
			File file = new File(fileNameExcel);
			if (file.isFile()) {
				String fileName = file.getName();
				String fileExtension = FuncUtils.getFileExtension(fileName);

				try {
					if ("xls".equalsIgnoreCase(fileExtension) || "xlsx".equalsIgnoreCase(fileExtension)) {
						FileInputStream fis = new FileInputStream(file);
						Workbook workbook = WorkbookFactory.create(fis);
						Sheet sheet = workbook.getSheetAt(0);
						Row row = sheet.getRow(18); // 行索引从 0 开始
						Cell cell = row.getCell(1); //列索引从 0 开始

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

						double my_double = Double.parseDouble(value);
						if (my_double < 0) {
							tax_negative_number.put(file.getPath(), "" + my_double);
						}

						fis.close();

					}
				} catch (Throwable e) {
					logger.debug("[my_double NG]" + file.getPath());
					e.printStackTrace();
				}

			}
		}

		for (Map.Entry<String, String> entry : tax_negative_number.entrySet()) {
			String fileName = entry.getKey();
			logger.debug("[tax_negative_number]" + fileName);
		}
	}

    private static List<String> findExcelFiles(String folderPath, String keyword) {
        List<String> result = new ArrayList<>();
        findFilesRecursively(new File(folderPath), keyword, result);
        return result;
    }

    private static void findFilesRecursively(File folder, String keyword, List<String> result) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    findFilesRecursively(file, keyword, result);
                }
            }
        } else if (folder.isFile() && folder.getName().toLowerCase().endsWith(".xlsx") && folder.getName().contains(keyword)) {
            result.add(folder.getAbsolutePath());
        }
    }

}
