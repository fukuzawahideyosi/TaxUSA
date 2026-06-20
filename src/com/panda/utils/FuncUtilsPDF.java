package com.panda.utils;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class FuncUtilsPDF {

	private static Logger logger = Logger.getLogger(FuncUtilsPDF.class.toString());

    public static void main(String[] args) {
//
//        String pdfFilePath = "input.pdf"; // 入力の PDF ファイルパス
//        String wordFilePath = "output.docx"; // 出力の Word ファイルパス
//
//        try {
//            // PDF ドキュメントを読み込む
//            PdfDocument pdfDocument = new PdfDocument();
//            pdfDocument.loadFromFile(pdfFilePath);
//
//            // PdfToWordConverter を初期化
//            PdfToWordConverter converter = new PdfToWordConverter(pdfDocument);
//
//            // Word ドキュメントに変換
//            converter.convertToWord().saveToFile(wordFilePath);
//
//            logger.info("PDF を Word に変換しました");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }



        try {
            String filePath = "C:\\Users\\Administrator\\Desktop\\ｓｈｅｎｚｈｅｎｓｈｉ　ｃｈｕｎｑｉｕ　ｄｉａｎｚｉｋｅｊｉ_消費税簡易課税制度選択届出.pdf"; // 替换为你的 PDF 文件路径
            String keyword = "適用開始課税期間"; // 替换为你要查找的关键字
            searchKeywordInPDF(filePath, keyword);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    public static void searchKeywordInPDF(String filePath, String keyword) throws IOException {
        File file = new File(filePath);
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();

        for (int page = 1; page <= document.getNumberOfPages(); page++) {
            pdfTextStripper.setStartPage(page);
            pdfTextStripper.setEndPage(page);
            String pageText = pdfTextStripper.getText(document);

            if (pageText.contains(keyword)) {
                logger.info("关键字 '" + keyword + "' 被找到在第 " + page + " 页");
                // 这里你可以执行其他操作，如记录页数或提取文本
            }
        }

        document.close();
    }

}