package com.panda.batch;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.log4j.Logger;

import com.panda.utils.FuncUtils;

public class FileLineCounter {


	private static Logger logger = Logger.getLogger(FileLineCounter.class.toString());

    public static void main(String[] args) {

        // 指定文件夹路径
//        String folderPath = "C:\\Users\\Administrator\\Desktop\\csv整理";
        String folderPath = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv";

         getFileLineCounter(folderPath);

    }

    public static LinkedHashMap<String, Integer> getFileLineCounter(String folderPath) {

		LinkedHashMap<String, Integer> HashMap_path_FileLineCounter = new LinkedHashMap<>();
		// 获取文件夹
		File folder = new File(folderPath);

		// 检查文件夹是否存在
		if (folder.exists() && folder.isDirectory()) {
			// 获取文件夹下所有文件
			File[] files = folder.listFiles();

			// 初始化行数计数器
			int totalLines = 0;

			// 遍历文件列表
			for (File file : files) {
				if (file.isFile()) {
					// 统计每个文件的行数并累加到总行数
					int linesInFile = countLines(file);
					totalLines += linesInFile;

					HashMap_path_FileLineCounter.put(file.getName(), linesInFile);
					logger.debug("Lines: " + linesInFile + ", File: " + file.getName());
				}
			}

			logger.debug("Total Lines in Folder: " + totalLines);
			logger.debug("Total shijian 1000/6 : " + totalLines/1000*6/60);
		} else {
			logger.debug("Folder does not exist or is not a directory.");
		}
		return HashMap_path_FileLineCounter;
	}

    // 统计文件的行数
	public static int countLines(File file) {
		CSVParser csvParser = null;
		Reader reader = null;
		int lines = 0;
		try {
			String charset = FuncUtils.detectCharset(file.getPath());
//			logger.debug("CSV 文件编码是: " + charset);

			String csvFilePath = file.getPath();

			reader = new FileReader(csvFilePath, Charset.forName(charset));
			csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
			lines = csvParser.getRecords().size() + 1; //加上标题的一行
			csvParser.close();

//			BufferedReader reader = new BufferedReader(new FileReader(file));
//			while (reader.readLine() != null) {
//				lines++;
//			}
//			reader.close();

//			String[] nextLine;
//			CSVReader csvParser = new CSVReader(new FileReader(csvFilePath, Charset.forName(charset)));
//
//			while ((nextLine = csvParser.readNext()) != null) {
//				lines++;
//			}
//			csvParser.close();

//			logger.debug("Number of rows in CSV file: " + lines);

		} catch (Exception e) {
			logger.debug("File NG: " + file.getName());
			e.printStackTrace();

			// 移动文件
            try {
				if (csvParser != null) {
					csvParser.close();
				}
				if (reader != null) {
					reader.close();
				}


            	String path_csvToDBNG = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv\\666csvToDBNG";
				File FileNew = new File(path_csvToDBNG + "\\" + file.getName());
	            Files.move(Paths.get(file.getPath()), Paths.get(FileNew.getPath()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}

		}
		return lines;
	}
}
