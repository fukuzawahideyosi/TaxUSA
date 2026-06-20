package com.panda.batch;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.panda.utils.FuncUtils;

public class set_wenjian_zhengli {

	private static Logger logger = Logger.getLogger(set_wenjian_zhengli.class.toString());

	public static void main(String[] args) {



//		 // 源文件路径
//        Path sourcePath = Paths.get("C:\\Users\\Administrator\\Desktop\\文件自动整理_impot_消費税申告ncc\\PDSK230386_Wist Plastic & Metal Technology Limited\\Ｗｉｓｔ　Ｐｌａｓｔｉｃ　＆　Ｍｅｔａｌ　Ｔｅｃｈ.ncc");
//
//        // 目标文件夹路径
//        Path targetFolder = Paths.get("C:\\Users\\Administrator\\Desktop\\文件自动整理_impot\\PDSK230386_Wist Plastic & Metal Technology Limited");
//
//        // 目标文件路径
//        Path targetPath = targetFolder.resolve(sourcePath.getFileName());
//
//        try {
//            // 复制文件
//            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
//            logger.info("文件复制成功！");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
		String path_impot = "C:\\Users\\Administrator\\Desktop\\文件自动整理_impot";
		String path_impot_消費税申告ncc = "C:\\Users\\Administrator\\Desktop\\文件自动整理_impot_消費税申告ncc";
		String path_output = "C:\\Users\\Administrator\\Desktop\\文件自动整理_output";
		try {


			LinkedHashMap<String, File> LinkedHashMap_path_impot = new LinkedHashMap<>();
			LinkedHashMap<String, File> LinkedHashMap_path_impot_消費税申告ncc = new LinkedHashMap<>();


			File rootFolder = new File(path_impot_消費税申告ncc);
			for (File subFolder : rootFolder.listFiles(File::isDirectory)) {
				int count_消費税申告ncc = 0;
            	String flieName_subFolder =  subFolder.getName();
                for (File myFile : FuncUtils.listFilesInFolder(subFolder.getPath())) {
                	String flieName_myFile =  myFile.getName();
					String fileExtension = FuncUtils.getFileExtension(flieName_myFile);
                	if ("ncc".equals(fileExtension.toLowerCase())) {
                		++count_消費税申告ncc;
                		LinkedHashMap_path_impot_消費税申告ncc.put(subFolder.getName(), myFile);
                	}
                }
                if (count_消費税申告ncc > 1) {
  					logger.info("File ncc NG：" + flieName_subFolder);
                  }
			}

			rootFolder = new File(path_impot);
			for (File subFolder : rootFolder.listFiles(File::isDirectory)) {
				int count_ncc = 0;
            	String flieName_subFolder =  subFolder.getName();
				LinkedHashMap_path_impot.put(subFolder.getName(), subFolder);

                for (File myFile : FuncUtils.listFilesInFolder(subFolder.getPath())) {
                	String flieName_myFile =  myFile.getName();
					String fileExtension = FuncUtils.getFileExtension(flieName_myFile);
                	if ("ncc".equals(fileExtension.toLowerCase())) {
                		++count_ncc;
                	}
                }

                if (count_ncc > 1) {
					logger.info("File ncc NG：" + flieName_subFolder);
                }

			}


			/*
H:\共有ドライブ\ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社\８０１　お客様資料管理\ＪＣＴ\R５申告\0220オウテキ処理　0220最後69 386-454
1 只有消费税异动届出书的话，整个ncc删除
没有申告书，的删除

H:\共有ドライブ\ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社\８０１　お客様資料管理\ＪＣＴ\R５申告\0221オウテキ処理　0220最後69 386-454　消費税申告書ncc
2 从这个路径下把ncc拿过去
			*/


			LinkedHashMap<String, String> LinkedHashMap_path_output_ok = new LinkedHashMap<>();
			LinkedHashMap<String, String> LinkedHashMap_path_output_ok_skip = new LinkedHashMap<>();
			LinkedHashMap<String, String> LinkedHashMap_path_output_errN = new LinkedHashMap<>();
			LinkedHashMap<String, String> LinkedHashMap_path_output_err0 = new LinkedHashMap<>();
			LinkedHashMap<String, String> LinkedHashMap_path_output_get_ncc0 = new LinkedHashMap<>();



			for (File subFolder : rootFolder.listFiles(File::isDirectory)) {
				int count_ncc = 0;
            	String flieName_subFolder =  subFolder.getName();
				File myFile_消費税申告ncc = LinkedHashMap_path_impot_消費税申告ncc.get(flieName_subFolder);

                for (File myFile : FuncUtils.listFilesInFolder(subFolder.getPath())) {
                	String flieName_myFile =  myFile.getName();
					String fileExtension = FuncUtils.getFileExtension(flieName_myFile);

                	if ("ncc".equals(fileExtension.toLowerCase())) {
                		++count_ncc;
//    					logger.info("File：" + flieName_myFile);
    					String fileContent = FuncUtils.readFileContent(myFile);
    					if (fileContent.contains("消費税申告")) {
    						LinkedHashMap_path_output_ok_skip.put(subFolder.toPath().toString(), "");
    						break;

    					} else {
    						myFile.delete();
    						if (myFile_消費税申告ncc == null) {
//    							logger.info("File 消費税申告ncc get NG：" + flieName_subFolder);
    							LinkedHashMap_path_output_get_ncc0.put(subFolder.toPath().toString(), "");

    						} else {
    		                    // 源文件路径
    		                    Path sourcePath = Paths.get(myFile_消費税申告ncc.getPath());
    		                    // 目标文件夹路径
    		                    Path targetFolder = Paths.get(subFolder.getPath());
    		                    // 目标文件路径
    		                    Path targetPath = targetFolder.resolve(sourcePath.getFileName());

    		                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    							LinkedHashMap_path_output_ok.put(subFolder.toPath().toString(), "");
    						}
    					}

                	}



                }

                if (count_ncc > 1) {
//					logger.info("File ncc 複数 NG：" + flieName_subFolder);
					LinkedHashMap_path_output_errN.put(subFolder.toPath().toString(), "");

                } else if (count_ncc == 0) {
                    // 源文件路径
                    Path sourcePath = Paths.get(myFile_消費税申告ncc.getPath());
                    // 目标文件夹路径
                    Path targetFolder = Paths.get(subFolder.getPath());
                    // 目标文件路径
                    Path targetPath = targetFolder.resolve(sourcePath.getFileName());

                    Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                	LinkedHashMap_path_output_err0.put(subFolder.toPath().toString(), "");
                }

                //TODO
//            	return;

			}

			for (String key : LinkedHashMap_path_output_ok_skip.keySet()) {
				logger.info("File ncc ok skip：" + key);
			}
			for (String key : LinkedHashMap_path_output_ok.keySet()) {
				logger.info("File ncc ok：" + key);
			}
			for (String key : LinkedHashMap_path_output_err0.keySet()) {
				logger.info("File ncc 0 NG：" + key);
			}
			for (String key : LinkedHashMap_path_output_errN.keySet()) {
				logger.info("File ncc 複数 NG：" + key);
			}
			for (String key : LinkedHashMap_path_output_get_ncc0.keySet()) {
				logger.info("File 消費税申告ncc get NG：" + key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
