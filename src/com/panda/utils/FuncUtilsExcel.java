package com.panda.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;

public class FuncUtilsExcel {

	private static Logger logger = Logger.getLogger(FuncUtilsExcel.class.toString());



	// 获取单元格值并转换为字符串，处理整数和文本
	public static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // 如果单元格值是整数，则格式化为整数显示
                double numericValue = cell.getNumericCellValue();
                if (numericValue == (int) numericValue) {
                    return String.valueOf((int) numericValue); // 将数值转换为整数
                } else {
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }


	 /**
	  * 用 LibreOffice 处理 CSV 并另存为新的 CSV
	 * @param charset
	  */



	public static BufferedReader convertCSVWithLibreOffice(String inputCsvPath, String charset) throws IOException {
	    File inputFile = new File(inputCsvPath);
	    File renamedFile = new File(inputFile.getParent(), "Amazon_jiaoyi_jilu3.csv");

	    // **1. 复制文件（创建 Amazon_jiaoyi_jilu.csv）**
	    Files.copy(inputFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

	    // **2. 确定 LibreOffice 执行路径**
	    String sofficePath;
	    if (System.getProperty("os.name").toLowerCase().contains("win")) {
	        File libreOfficeExe = new File("C:\\Program Files\\LibreOffice\\program\\soffice.exe");
	        File libreOfficeCom = new File("C:\\Program Files\\LibreOffice\\program\\soffice.com");
	        if (libreOfficeExe.exists()) {
	            sofficePath = libreOfficeExe.getAbsolutePath();
	        } else if (libreOfficeCom.exists()) {
	            sofficePath = libreOfficeCom.getAbsolutePath();
	        } else {
	            throw new FileNotFoundException("LibreOffice 未找到，请检查安装路径！");
	        }
	    } else {
	        sofficePath = "/usr/bin/soffice";  // Linux
	    }

	    // **3. 另存为新的 CSV 文件**
	    File outputCsvFile = new File(renamedFile.getParent(), "Amazon_jiaoyi_jilu_toExcel.csv");

	    List<String> command = Arrays.asList(
	        sofficePath,
	        "--headless",
//	        "--convert-to", "csv:Text - txt - csv (StarCalc):UTF8,charset=" + charset,
	        "--convert-to", "csv:Text - txt - csv (StarCalc):UTF8",  // ✅ 修正格式
	        "--outdir", renamedFile.getParent(),
	        renamedFile.getAbsolutePath()
	    );

	    System.out.println("执行 LibreOffice 命令: " + String.join(" ", command));

	    ProcessBuilder processBuilder = new ProcessBuilder(command);
	    processBuilder.redirectErrorStream(true);
	    Process process = processBuilder.start();

	    // **4. 读取 LibreOffice 输出日志**
	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
	        String line;
	        while ((line = reader.readLine()) != null) {
	            System.out.println("[LibreOffice] " + line);
	        }
	    }

	    int exitCode;
	    try {
	        exitCode = process.waitFor();
	        if (exitCode != 0) {
	            throw new IOException("LibreOffice 执行失败，错误码：" + exitCode);
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        throw new IOException("LibreOffice 转换被中断", e);
	    }

	    // **5. 确保文件转换成功**
	    if (!outputCsvFile.exists()) {
	        throw new FileNotFoundException("转换后的 CSV 文件未找到：" + outputCsvFile.getAbsolutePath());
	    }

	    // **6. 读取新 CSV 并返回 BufferedReader**
	    return new BufferedReader(new FileReader(outputCsvFile, StandardCharsets.UTF_8));
	}
	   public static void main(String[] args) {
	        try {
	        	String filePath="E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileDataToolsReplacegetAmazonCsvFormat\\20250208135154\\STLB  2024Jan1-2024Dec31CustomTransaction-JP-最终申报数据.csv";
	        	filePath="E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileDataToolsReplacegetAmazonCsvFormat\\20250208135154\\STLB  2024Jan1-2024Dec31CustomTransaction-JP-最终申报数据_saved.csv";
	            String charset = FuncUtils.detectCharset(filePath);

	            System.out.println("🔍 检测到的字符集：" + charset);

				if ("IBM866".equals(charset)) {
					charset = "GB18030";
				}
//	            // 读取 CSV 并打印前 10 行
//	            readFirst10Lines(filePath, charset);
//
//
//	            // 保存到新文件
//	            String outputFilePath = filePath.replace(".csv", "_saved.csv");
//	            saveCsvToFile(filePath, outputFilePath, charset);

	            BufferedReader reader = convertCSVWithLibreOffice(filePath , charset);
	            System.out.println("转换成功，开始读取 CSV...");
	            reader.lines().forEach(System.out::println);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	   /**
	     * 读取 CSV 并保存到新的 CSV 文件中
	     */
	    public static void saveCsvToFile(String inputFilePath, String outputFilePath, String charset) {
	        try (
	            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), charset));
	            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8))
	        ) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                writer.write(line);
	                writer.newLine(); // 换行
	            }
	            System.out.println("✅ CSV 内容已成功保存到：" + outputFilePath);
	        } catch (IOException e) {
	            System.err.println("❌ 处理 CSV 文件时出错：" + e.getMessage());
	        }
	    }

	    /**
	     * 自动检测文件编码（UTF-8, GBK, SHIFT_JIS）
	     */
	    public static String detectCharset(String filePath) {
	        try {
	            byte[] buffer = new byte[4096]; // 读取前 4KB 数据
	            FileInputStream fis = new FileInputStream(filePath);
	            int bytesRead = fis.read(buffer);
	            fis.close();

	            if (bytesRead == -1) return null;

	            // **常见字符集尝试**
	            String[] charsets = {"UTF-8", "GBK", "SHIFT_JIS", "ISO-8859-1"};
	            for (String cs : charsets) {
	                if (new String(buffer, cs).contains(",")) {
	                    return cs;
	                }
	            }
	        } catch (Exception e) {
	            System.err.println("⚠️ 自动检测字符集失败：" + e.getMessage());
	        }
	        return null; // 检测失败
	    }

	   public static void readFirst10Lines(String filePath, String charset) {
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), charset))) {
	            String line;
	            int count = 0;

	            System.out.println("📌 CSV 文件 [" + filePath + "] 前 10 行内容：");
	            while ((line = reader.readLine()) != null && count < 10) {
	                System.out.println((count + 1) + ": " + line);
	                count++;
	            }

	            if (count == 0) {
	                System.out.println("⚠️ 文件为空，未读取到内容！");
	            }

	        } catch (IOException e) {
	            System.err.println("❌ 读取 CSV 文件失败：" + e.getMessage());
	        }
	    }




}