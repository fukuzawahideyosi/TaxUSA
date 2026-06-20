package com.panda.batch;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.FuncUtils;

public class ConsumptionTaxPDFGet {

	private static Logger logger = Logger.getLogger(ConsumptionTaxPDFGet.class.toString());

	public static void main(String[] args) {

		String path_output = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\output";
		// 3. 根据1的文件名字，查找有无同名的pdf，如果有，复制到1的相应文件夹下
		try {

//			String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\20240206名单 处理328行到386行 无跳过.xlsx";
			String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\0217PDSK编号3.xlsx";
			int rowS = 2;			int rowE = -1;			int columnS = 1; int columnE = 19;	int column_excelDataHashMapKey = 1;
			Map<String, Map<String, String>> excelDataHashMap19 = FuncUtils.get_excelDataHashMap(path19, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);

			Map<String, Map<String, String>> excelDataHashMap19New = new LinkedHashMap<>();
			Map<String, Map<String, String>> excelDataHashMap19NewNG = new LinkedHashMap<>();

			 // 指定路径
	        String pathPDF = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\pdf";

	        // 获取指定路径下所有文件夹路径
	        File rootFolder = new File(pathPDF);
	        if (rootFolder.isDirectory()) {
    			int count = 0;
	            for (File subFolder : rootFolder.listFiles(File::isDirectory)) {
					++count;
					logger.info("处理个数 : " + count);
	                String subFolderPath = subFolder.getAbsolutePath();
	                logger.debug("当前文件夹路径：" + subFolderPath);

	                int subFolderCount = 0;

	                // 循环当前文件夹路径下的所有 PDF 文件
//	                for (File pdfFile : subFolder.listFiles((dir, name) -> name.endsWith(".pdf"))) {
//	                for (File pdfFile : subFolder.listFiles()) {

	                for (File pdfFile : FuncUtils.listFilesInFolder(subFolder.getPath())) {
//	    			List<String> fileNamesIMPORT = FuncUtils.listFilesInFolder(subFolder.getPath());
//	    			for (int i = 0; i < fileNamesIMPORT.size(); i++) {
//	    				File pdfFile =new File(fileNamesIMPORT.get(i));


	                	if (subFolderCount > 0) {
//	                		break;
	                	}

	                    String pdfFileName = pdfFile.getName();
//	                    logger.debug("当前 PDF 文件名：" + pdfFileName);
						String fileExtension = FuncUtils.getFileExtension(pdfFileName);
						if  (StringUtils.isEmpty(fileExtension)) {
							continue;
						}

	                    // 循环 excelDataHashMap19 的键，判断是否与当前 PDF 文件名相同
	        			for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19.entrySet()) {
	        				Map<String, String> excelValue = entry_excelDataHashMap.getValue();
	        				String key =excelValue.get("1");
	        				key = FuncUtils.toHalfWidth(key);
	        				key = key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
	        				pdfFileName = FuncUtils.toHalfWidth(pdfFileName);
	                    	pdfFileName = pdfFileName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

	                    	String subFolderName = subFolder.getName().replaceAll(" ", "");
	                        if (pdfFileName.contains(key) || subFolderName.contains(excelValue.get("16"))) {
	                            // PDF 文件名与 HashMap 键匹配
//	                            logger.debug("PDF 文件名与键匹配：" + key);
	                            // 在这里可以进行进一步的处理
	                            File pdfFileNew = new File(path_output + "\\" + excelValue.get("0") + "_" + excelValue.get("1") + "_申告確認書");
	                            if (!pdfFileNew.exists()) {
	                            	pdfFileNew.mkdirs();
	                            }


	    						if ("pdf".equals(fileExtension.toLowerCase())) {
//	    							copyFolder(subFolder, pdfFileNew);
	    							FuncUtils.copyFile(pdfFile.getPath(), pdfFileNew + "/" + pdfFile.getName());

	    						} else {

	    						}

	                            excelDataHashMap19New.put(key, excelValue);
	                            ++subFolderCount;
	                        }
	                    }
	                }

                	if (subFolderCount == 0) {
                		excelDataHashMap19NewNG.put(subFolder.getPath(), null);
                	}
	            }

	        } else {
	            logger.debug("指定路径不是文件夹。");
	        }

			int count = 0;
			for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19New.entrySet()) {
				Map<String, String> excelValue = entry_excelDataHashMap.getValue();
			        // 将Map的值用逗号连接成字符串
				String joinedValues = FuncUtils.joinMapValues(excelValue);
				logger.debug("个数 : " + ++count + "[New]	" + joinedValues);
			}


			count = 0;
			for (String key : excelDataHashMap19NewNG.keySet()) {
				logger.debug("个数 : " + ++count + "[NewNG]	" + key);
			}


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            // 如果目标文件夹不存在，则创建它
            if (!destination.exists()) {
                destination.mkdirs();
            }

            // 获取源文件夹下的所有文件和子文件夹
            String[] files = source.list();

            if (files != null) {
                for (String file : files) {
                    // 递归复制子文件夹和文件
                    copyFolder(new File(source, file), new File(destination, file));
                }
            }
        } else {
            // 如果是文件，则直接复制
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }



}
