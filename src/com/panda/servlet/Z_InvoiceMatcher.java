package com.panda.servlet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
public class Z_InvoiceMatcher {

	private static Logger logger = Logger.getLogger(SetXiaofeishuiShengaoLogic.class.toString());

    public static void main(String[] args) {


        String dbFilePath = "C:\\Users\\Administrator\\Documents\\FeedbackHub\\dbInvoiceBangouIsNull.txt";
        String allNewCsvFilePath = "C:\\Users\\Administrator\\Documents\\FeedbackHub\\allNew_country2.csv";
//        String allNewCsvFilePath = "E:\\小工具\\merge\\test\\allNew.csv";
        String okCsvFilePath = "C:\\Users\\Administrator\\Documents\\FeedbackHub\\ok.csv";

        ExecutorService executor = Executors.newFixedThreadPool(10); // 设置线程池大小
        AtomicInteger count = new AtomicInteger(0);

        try {
            // 创建ok.csv文件或清空已存在的文件
            BufferedWriter writer = new BufferedWriter(new FileWriter(okCsvFilePath));
            writer.close();

            String dbLine;
            BufferedReader dbReader = new BufferedReader(new FileReader(dbFilePath));

            while ((dbLine = dbReader.readLine()) != null) {
                String finalDbLine = dbLine; // 为了能在lambda表达式中使用

                executor.submit(() -> {
                    try {
                        String allNewLine;
                        BufferedReader allNewReader = new BufferedReader(new FileReader(allNewCsvFilePath));

                        while ((allNewLine = allNewReader.readLine()) != null) {
                            int currentCount = count.incrementAndGet();
                            if (currentCount % 100000 == 0) {
                            	logger.info("Processed " + currentCount + " records: " + finalDbLine);
                            }

                            // 去掉全角空格和半角空格并比较
                            String trimmedAllNewLine = allNewLine.replaceAll(" ", "").replaceAll("　", "");
                            String trimmedDbLine = finalDbLine.replaceAll(" ", "").replaceAll("　", "");

                            // 如果在allNew.csv中找到匹配的行，将其追加到ok.csv文件
                            if (trimmedAllNewLine.contains(trimmedDbLine)) {
                                BufferedWriter okWriter = new BufferedWriter(new FileWriter(okCsvFilePath, true));
                                okWriter.write(allNewLine + "\n");
                                okWriter.close();
                                logger.info("Found: " + allNewLine);
                            }
                        }
                        allNewReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }
            dbReader.close();

            // 关闭线程池
            executor.shutdown();

            // 输出处理完成的信息
            logger.info("Processing completed");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}