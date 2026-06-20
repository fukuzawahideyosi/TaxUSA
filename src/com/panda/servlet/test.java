package com.panda.servlet;
import org.apache.log4j.Logger;
public class test {

	private static Logger logger = Logger.getLogger(SetXiaofeishuiShengaoLogic.class.toString());

    public static void main(String[] args) {




//        // Google Cloud Translation API の認証情報を設定
//        Translate translate = TranslateOptions.getDefaultInstance().getService();
//
//        // 翻訳したい英語のテキスト
//        String englishText = "Hello, world!";
//
//        // テキストをカタカナに翻訳
//        Translation translation = translate.translate(englishText, Translate.TranslateOption.targetLanguage("ja"));
//
//        // カタカナに翻訳されたテキストを取得
//        String katakanaText = translation.getTranslatedText();
//
//        // 結果を表示
//        logger.info("English: " + englishText);
//        logger.info("Katakana: " + katakanaText);

//String json="XPPEN TECHNOLOGY CO.";
//FuncUtils.
//    	logger.info(json);




//		SendMail SendMail = new SendMail();
//
//		SendMail.sendMessage("info@pandaservicejapan.com", "43936834@qq.com", "title", "textboxdata", "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileData\\20231009000052_TEST");





//        Tesseract tesseract = new Tesseract();
//        tesseract.setDatapath("C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\IMPORT_WORDXTXnew\\tessdata"); // Tesseractの言語データへのパスを設定
//        try {
//            String text = tesseract.doOCR(new File("C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\IMPORT_WORDXTXnew\\d5672ff37bf7d50f15ae819f32b1c02.png"));
//            logger.info("抽出されたテキスト: " + text);
//        } catch (TesseractException e) {
//            System.err.println("OCRエラー: " + e.getMessage());
//        }
    }



    public static String trimWhitespaceAndTabs(String input) {
        if (input == null) {
            return null;
        }

        // 使用正则表达式替换制表符和首尾空格为空字符串
        input = input.replaceAll("\\s+$|^\\s+|\\t+", "");

        // 使用正则表达式替换首尾全角空格为""
        input = input.replaceAll("^[　]+|[　]+$", "");

        // 使用正则表达式替换首尾半角空格为""
        input = input.replaceAll("^\\s+|\\s+$", "");

        return input;
    }
}