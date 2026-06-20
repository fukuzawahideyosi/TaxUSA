package com.panda.batch;

import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.panda.utils.FuncUtils;

/*
 * 销售额数据抽出和计算
 */
public class FileFormat {

	private static Logger logger = Logger.getLogger(FileFormat.class.toString());

	public static void main(String[] args) {
		try {


			String path19 = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\BPS改任切替名簿.xlsx";
			int rowS = 0;		int rowE = -1;	int columnS = 1; int columnE = 19;	int column_excelDataHashMapKey = -1;
			Map<String, Map<String, String>> excelDataHashMap19 = FuncUtils.get_excelDataHashMap(path19, rowS, rowE, columnS, columnE, column_excelDataHashMapKey);

			String pathNCC = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\BPS改任異動届出書 テンプレート.txt";
			File dataModelFileNCC = new File(pathNCC);
			if (dataModelFileNCC.length() == 0) {
				logger.debug(pathNCC + " → NG:ncc File data model ");
				return;
			}

			String fileContent = FuncUtils.readFileContent(dataModelFileNCC);

			String sourceFilePath = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\output";
			/*
			 * 登録データ準備
			 */
			int count = 0;

			for (Entry<String, Map<String, String>> entry_excelDataHashMap : excelDataHashMap19.entrySet()) {
				++count;
				logger.info("处理个数 : " + count);
				Map<String, String> excelValue = entry_excelDataHashMap.getValue();

				String fileContentNew = FuncUtils.readFileContent(dataModelFileNCC);


				/*
#利用者識別番号#	E	5
#法人番号#	F	6
#事業者名#	H	8
#事業者名カナ#	N	14
#代表人氏名#	J	10
#代表人氏名カナ#	P	16
#海外本店住所#	I	9

				 */
				fileContentNew = fileContentNew.replaceAll("#利用者識別番号#", excelValue.get("4"));
				fileContentNew = fileContentNew.replaceAll("#法人番号#", excelValue.get("5"));
				fileContentNew = fileContentNew.replaceAll("#事業者名#", excelValue.get("7"));
				fileContentNew = fileContentNew.replaceAll("#事業者名カナ#", excelValue.get("13"));
				fileContentNew = fileContentNew.replaceAll("#代表人氏名#", excelValue.get("9"));
				fileContentNew = fileContentNew.replaceAll("#代表人氏名カナ#", excelValue.get("15"));
				fileContentNew = fileContentNew.replaceAll("#海外本店住所#", excelValue.get("8"));

				/*
				 * ncc
				 */
				String pathNewNCC = sourceFilePath + "\\" + excelValue.get("0") + "_改任_" + excelValue.get("1") + ".ncc";


				// 写入文件
				FileWriter writer = new FileWriter(pathNewNCC);
				writer.write(fileContentNew);
				writer.close();
				logger.debug("File saved: " + pathNewNCC);


			}


		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}


		logger.info("end");
		return;




	}




}
