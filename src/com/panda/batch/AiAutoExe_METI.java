package com.panda.batch;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.panda.utils.FuncUtils;


public class AiAutoExe_METI {

	private static Logger logger = Logger.getLogger(AiAutoExe_METI.class.toString());


	static LinkedHashMap<String, String> dataMap_PDSK_skip = new LinkedHashMap<>();


	public static void main(String[] args) throws Exception {
		logger.debug("START ");

		System.setProperty("https.protocols", "TLSv1.2");


        try  {


    		//TODO
    		set_dataMap_skip();




            String excelPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\METI_moban\\00000001　METI管理表.xlsx";
            String wordPath  = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\METI_moban\\連絡メールテンプレート260208.docx";

            // 当前年月 YYYY/MM
            String yearMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));

            // 读取 Word 模板内容
            String templateText = readWordAsText(wordPath);

        	LinkedHashMap<String, LinkedHashMap<String, String>> mails = new LinkedHashMap<>();
        	LinkedHashMap<String, String> mails_check = new LinkedHashMap<>();
        	LinkedHashMap<String, String> mails_check_err = new LinkedHashMap<>();

            	Workbook workbook = new XSSFWorkbook(new FileInputStream(excelPath));
            	Sheet sheet = workbook.getSheetAt(0);
                // 从第2行开始（index=1）
                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//
//                	//TODO TODO
//                	if (i<292+0) {
////                		continue;
//                	}
//                	if (i==68) {
//                	} else {
////                		continue;
//                	}
//
//                	if (i>114) {
////                		return;
//                	}


                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    String colB = getCellString(row.getCell(1)); //
                    String colC = getCellString(row.getCell(2)); //
                    String colD = getCellString(row.getCell(3)); //
                    String colG = getCellString(row.getCell(6)); //
                    String colI = getCellString(row.getCell(8)); //

//                    logger.info("col,"+colB+","+colC+","+colD);

                    if (colI != null && colI.contains("【不发送邮件】")) {
                    	continue;
                    }

                    String key0 = colB + colC + colD;
                    key0 = key0.replace(" ", "").replace("　", "").replace(" ", "");
                	LinkedHashMap<String, String> Mymail = new LinkedHashMap<>();
                    if (mails.containsKey(key0)) {
                    	Mymail = mails.get(key0);
                    	colG = Mymail.get("colG") + "," + colG;

                    }
                	Mymail.put("i", "" + i);
                	Mymail.put("colB", colB);
                	Mymail.put("colC", colC);
                	Mymail.put("colD", colD);
                	Mymail.put("colG", colG);
                	Mymail.put("colI", colI);
                	mails.put(key0, Mymail);


                	//同一公司，邮件不同，err
                    if (mails_check.containsKey(colB)) {
                    	if (mails_check.get(colB).equals(colD)) {

                    	} else {
                    		mails_check_err.put(colB, "同一公司，邮件不同，err");
                    	}

                    }
                    //邮件为空err
                    if (!StringUtils.isEmpty(colB) && StringUtils.isEmpty(colD)) {
                		mails_check_err.put(colB, "邮件为空err");
                    }

                    mails_check.put(colB, colD);


                }

                if (mails_check_err.size() > 0) {
//                	logger.info("mails_check_err "+colB+","+colC+","+colD);
                	for (Map.Entry<String, String> entry : mails_check_err.entrySet()) {
                		logger.info("key=" + entry.getKey() + ", value=" + entry.getValue());
                	}
                	return;
                }

                int count = 0;
            	for (Map.Entry<String, LinkedHashMap<String, String>> entry : mails.entrySet()) {
					++count;
                	//TODO
//                	//第一批次
//                	if (1<=count && count<=150) {
//                	} else {
////                		return;
//                	}
//                	//第二批次
//                	if (150<count && count<=99999) {
//                	} else {
////                		return;
//                	}

//                	if (count > 336) {
//                	} else {
//                		continue;
//                	}

            	    String key0 = entry.getKey();
            	    LinkedHashMap<String, String> innerMap = entry.getValue();
//            	    logger.info("外层key: " + key0);

                    int i = Integer.parseInt(innerMap.get("i"));
                    String colB = innerMap.get("colB"); //
                    String colC = innerMap.get("colC"); //
                    String colD = innerMap.get("colD"); //
                    String colG = innerMap.get("colG"); //
                    String colI = innerMap.get("colI"); //

                    //TODO TODO
//                    boolean exeFlg = false;
//                    for (String key : dataMap_PDSK_skip.keySet()) {
//                        if (key.contains(colB) && !key.contains("lixiweb@yahoo.co.jp") ) {
//                        	exeFlg = true;
//                        	break;
//                        } else {
//
//                        }
//                    }
//                    if (exeFlg) {
//                    } else {
//                    	continue;
//                    }
                    //TODO TODO


                    if (StringUtils.isEmpty(colB) || StringUtils.isEmpty(colC) || StringUtils.isEmpty(colD)) {
    					logger.error("i " + (i + 1));
    					continue;

    				}

                    String mailText = templateText
                            .replace("【B】", colB)
                            .replace("【C】", colC)
                            .replace("【D】", colD)
                            .replace("【G】", colG)
                            .replace("【年/月】", yearMonth)
                            .replace("【保存用邮箱】", "lixiweb@yahoo.co.jp");


                    String to = colD;
                    String subject = "";
                    String body = "";

    				// 统一换行符
    				String text = mailText.replace("\r\n", "\n");

    				// 1️⃣ 提取件名
    				for (String line : text.split("\n")) {
    					if (line.startsWith("件名：")) {
    						subject = line.substring("件名：".length()).trim();
    						break;
    					}
    				}

    				// 2️⃣ 提取正文
    				int bodyIndex = text.indexOf("本文：");
    				if (bodyIndex >= 0) {
    					body = text
    							.substring(bodyIndex + "本文：".length())
    							.trim();
    				} else {
    					body = "";
    				}

    				if (StringUtils.isEmpty(to) || StringUtils.isEmpty(subject) || StringUtils.isEmpty(body)) {
    					logger.error("i " + (i + 1));

    				} else {
                        logger.info("col,"+colB+","+colC+","+colD);
    					logger.info("i " + i + " count " + count);//番号
//    					logger.info("i " + (i + 1));


    					if (to.toLowerCase().indexOf("outlook") >-1 || to.toLowerCase().indexOf("hotmail") >-1) {
//        					logger.info("outlook skip");//番号
//    						continue;

    						int random = (int)(Math.random() * 10) + 1;
        					random = random + 60*1;
        					random = random*6;
        					logger.info("sleep " + random);//番号
        					Thread.sleep(random * 1000L);


    					} else {
//    						continue;
    					}


//    					if (to.toLowerCase().indexOf("pm@prudenpulse.com") >-1) {
//
//    					} else {
//    						continue;
//    					}

    					FuncUtils.sendMail_METI(to, subject, body);

    					int random = (int)(Math.random() * 10) + 1;
    					random = random + 60*1;
    					Thread.sleep(random * 1000L);
    				}
            	}



        } catch (Exception e) {
			e.printStackTrace();
        }



		logger.debug("END ");

		return;

	}


//	dataMap_PDSK_skip.put("","");
	private static void set_dataMap_skip() {


	}


    /** 读取 Word 文本 */
    private static String readWordAsText(String path) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(path))) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                sb.append(p.getText()).append("\n");
            }
        }
        return sb.toString();
    }

    /** 安全读取单元格字符串 */
    private static String getCellString(Cell cell) {
        if (cell == null) return "";
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

}

/*
 *


[2026-06-02 18:07:47,668]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-02 18:07:51,915]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-02 18:07:51,916]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社,PANDA SERVICE CO., LTD,info@pandaservicejapan.com
[2026-06-02 18:07:51,917]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 2 count 1
[2026-06-02 18:07:51,981]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail info@pandaservicejapan.com
[2026-06-02 18:07:51,981]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:07:58,338]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,info@pandaservicejapan.com
[2026-06-02 18:09:04,341]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳捷溪得信息咨询有限公司,JXD,471074906@qq.com
[2026-06-02 18:09:04,342]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 4 count 2
[2026-06-02 18:09:04,347]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 471074906@qq.com
[2026-06-02 18:09:04,347]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:09:08,758]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,471074906@qq.com
[2026-06-02 18:10:14,773]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,惠州海洛德科技有限公司,Huizhou Hailuode Technology Co., Ltd,2803738807@qq.com
[2026-06-02 18:10:14,773]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 5 count 3
[2026-06-02 18:10:14,780]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 2803738807@qq.com
[2026-06-02 18:10:14,780]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:10:19,623]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,2803738807@qq.com
[2026-06-02 18:11:28,633]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,漳州市龙文区米图思电子有限公司,Zhangzhoushi long wen qu mi tu si dian zi co.ltd,3534287257@qq.com
[2026-06-02 18:11:28,633]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 22 count 4
[2026-06-02 18:11:28,637]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 3534287257@qq.com
[2026-06-02 18:11:28,637]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:11:33,853]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,3534287257@qq.com
[2026-06-02 18:12:42,865]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,広州浩麗貿易有限公司,Guangzhou Haoli Trading Company Ltd.,13428813084@163.com
[2026-06-02 18:12:42,865]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 7 count 5
[2026-06-02 18:12:42,869]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 13428813084@163.com
[2026-06-02 18:12:42,869]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:12:47,452]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,13428813084@163.com
[2026-06-02 18:13:52,458]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市高达贸易有限公司,Shen Zhen Shi Gao Da Mao Yi Company Limited,1096671470@qq.com
[2026-06-02 18:13:52,458]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 8 count 6
[2026-06-02 18:13:52,464]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1096671470@qq.com
[2026-06-02 18:13:52,465]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:13:57,530]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1096671470@qq.com
[2026-06-02 18:15:03,541]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市亚缘利电子商务有限公司,shenzhenshiyayuanlidianzishangwuyouxiangongsi,3584141593@qq.com
[2026-06-02 18:15:03,541]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 9 count 7
[2026-06-02 18:15:03,545]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 3584141593@qq.com
[2026-06-02 18:15:03,545]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:15:07,942]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,3584141593@qq.com
[2026-06-02 18:16:17,945]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,厦门品豪锈转化贸易有限公司,xiamenpinhaoxiuzhuanhuamaoyiyouxiangongsi,xc8391@163.com
[2026-06-02 18:16:17,946]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 10 count 8
[2026-06-02 18:16:17,949]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xc8391@163.com
[2026-06-02 18:16:17,950]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:16:24,168]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xc8391@163.com
[2026-06-02 18:17:28,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,安庆虎遐商贸有限公司,Anqing Huxia Trading Co., Ltd.,rohou_jp@outlook.com
[2026-06-02 18:17:28,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 11 count 9
[2026-06-02 18:17:28,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail rohou_jp@outlook.com
[2026-06-02 18:17:28,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:17:33,356]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,rohou_jp@outlook.com
[2026-06-02 18:18:37,365]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,中山市海森网络科技有限公司,zhongshanshihaisenwangluokejiyouxiangongsi,rhea@ibayaqua.com
[2026-06-02 18:18:37,365]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 14 count 10
[2026-06-02 18:18:37,368]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail rhea@ibayaqua.com
[2026-06-02 18:18:37,369]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:18:42,339]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,rhea@ibayaqua.com
[2026-06-02 18:19:45,351]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市路飞网络技术有限公司,Shenzhenshilufeiwangluojishuyouxiangongsi,lufeiace1@163.com
[2026-06-02 18:19:45,351]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 13 count 11
[2026-06-02 18:19:45,355]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lufeiace1@163.com
[2026-06-02 18:19:45,355]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:19:50,493]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lufeiace1@163.com
[2026-06-02 18:20:52,507]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,广州市欧递国际物流有限公司,guangzhoushioudiguojiwuliuyouxiangongsi,596298590@qq.com
[2026-06-02 18:20:52,507]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 15 count 12
[2026-06-02 18:20:52,511]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 596298590@qq.com
[2026-06-02 18:20:52,511]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:20:57,739]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,596298590@qq.com
[2026-06-02 18:22:01,750]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳隆苹科技有限公司,Shenzhen Longping Technology CO.,LTD,lonpoojp@hotmail.com
[2026-06-02 18:22:01,750]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 16 count 13
[2026-06-02 18:22:01,754]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lonpoojp@hotmail.com
[2026-06-02 18:22:01,754]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:22:06,485]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lonpoojp@hotmail.com
[2026-06-02 18:23:14,495]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市无限星光创新科技有限公司,Shenzhen Infinite Starlight Innovation Technology Co., Ltd.,Infinitystarlight@outlook.com
[2026-06-02 18:23:14,495]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 17 count 14
[2026-06-02 18:23:14,498]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Infinitystarlight@outlook.com
[2026-06-02 18:23:14,499]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:23:18,813]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Infinitystarlight@outlook.com
[2026-06-02 18:24:23,816]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市星火星电子商务有限公司,SHENZHEN XINGMARS E-COMMERCE CO., LTD,yy712820740@gmail.com
[2026-06-02 18:24:23,816]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 18 count 15
[2026-06-02 18:24:23,819]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yy712820740@gmail.com
[2026-06-02 18:24:23,820]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:24:28,648]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yy712820740@gmail.com
[2026-06-02 18:25:37,655]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,潜江市懂滩厨具电器有限公司,Qianjiang Dongtan Kitchenware & Electrical Appliances Co., Ltd.,19186492237@163.com
[2026-06-02 18:25:37,655]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 19 count 16
[2026-06-02 18:25:37,659]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 19186492237@163.com
[2026-06-02 18:25:37,659]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:25:42,668]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,19186492237@163.com
[2026-06-02 18:26:47,677]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳维塔利科技有限公司,shenzhen weitali keji youxiangongsi,john@vitalitim.com
[2026-06-02 18:26:47,677]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 20 count 17
[2026-06-02 18:26:47,680]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail john@vitalitim.com
[2026-06-02 18:26:47,681]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:26:52,416]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,john@vitalitim.com
[2026-06-02 18:27:54,418]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市俩发发科技有限公司,shen zhen shi liang fa fa ke ji you xian gong si,szliangfafa@163.com
[2026-06-02 18:27:54,418]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 21 count 18
[2026-06-02 18:27:54,421]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail szliangfafa@163.com
[2026-06-02 18:27:54,422]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:27:58,976]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,szliangfafa@163.com
[2026-06-02 18:29:08,985]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市九天星辰科技有限公司,Shenzhen Jiutian Xingchen Technology Co., Ltd.,staverjp@outlook.com
[2026-06-02 18:29:08,985]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 23 count 19
[2026-06-02 18:29:08,989]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail staverjp@outlook.com
[2026-06-02 18:29:08,989]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:29:13,855]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,staverjp@outlook.com
[2026-06-02 18:30:21,857]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,海口砺珩贸易有限责任公司,haikoulihengmaoyiyouxianzerengongsi,hklm-u@outlook.com
[2026-06-02 18:30:21,857]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 24 count 20
[2026-06-02 18:30:21,860]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hklm-u@outlook.com
[2026-06-02 18:30:21,860]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:30:27,450]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hklm-u@outlook.com
[2026-06-02 18:31:37,461]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,汝鑫商贸(东莞)有限公司,Ru Xin Trading Dongguan Co Ltd,ruxinshangmao@outlook.com
[2026-06-02 18:31:37,461]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 25 count 21
[2026-06-02 18:31:37,465]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ruxinshangmao@outlook.com
[2026-06-02 18:31:37,465]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:31:42,982]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ruxinshangmao@outlook.com
[2026-06-02 18:32:44,996]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市佑维科技有限公司,Shenzhen Youwei Technology Co., Ltd.,291648561@qq.com
[2026-06-02 18:32:44,996]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 26 count 22
[2026-06-02 18:32:44,999]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 291648561@qq.com
[2026-06-02 18:32:45,000]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:32:50,711]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,291648561@qq.com
[2026-06-02 18:33:57,711]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,广州坤瀚电子商务有限公司,guangzhoukunhandianzishangwuyouxiangongsi,JiaChaoChenWu@outlook.com
[2026-06-02 18:33:57,711]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 27 count 23
[2026-06-02 18:33:57,714]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail JiaChaoChenWu@outlook.com
[2026-06-02 18:33:57,715]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:34:02,371]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,JiaChaoChenWu@outlook.com
[2026-06-02 18:35:11,376]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,保山劲创商贸有限公司,baoshanjinchuangshangmaoyouxiangongsi,2637642579@qq.com
[2026-06-02 18:35:11,376]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 28 count 24
[2026-06-02 18:35:11,379]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 2637642579@qq.com
[2026-06-02 18:35:11,380]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:35:16,903]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,2637642579@qq.com
[2026-06-02 18:36:25,914]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市丁佰贸易有限公司,shenzhenshidingbaimaoyiyouxiangongsi,dingbai2024@163.com
[2026-06-02 18:36:25,914]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 299 count 25
[2026-06-02 18:36:25,917]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail dingbai2024@163.com
[2026-06-02 18:36:25,918]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:36:31,135]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,dingbai2024@163.com
[2026-06-02 18:37:32,139]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市耀辉微科技有限公司,Shenzhenshi Yaohuiwei Kejiyouxiangongsi,motc975@126.com
[2026-06-02 18:37:32,139]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 30 count 26
[2026-06-02 18:37:32,143]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail motc975@126.com
[2026-06-02 18:37:32,143]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:37:37,384]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,motc975@126.com
[2026-06-02 18:38:39,397]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市富力美科技有限公司,Shenzhenshi Fulimei Keji Youxian Gongsi,515388865@qq.com
[2026-06-02 18:38:39,397]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 55 count 27
[2026-06-02 18:38:39,400]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 515388865@qq.com
[2026-06-02 18:38:39,400]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:38:44,569]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,515388865@qq.com
[2026-06-02 18:39:49,582]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市烯商电子商务有限公司,shenzhenshixishangdianzishangwuyouxiangongsi,xishangamazon@163.com
[2026-06-02 18:39:49,582]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 32 count 28
[2026-06-02 18:39:49,585]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xishangamazon@163.com
[2026-06-02 18:39:49,585]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:39:55,132]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xishangamazon@163.com
[2026-06-02 18:40:56,147]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,金华东威信息科技有限公司,Jinhua Dongwei Information Science & Technology Co., Ltd.,stevenlee1976@gmail.com
[2026-06-02 18:40:56,147]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 33 count 29
[2026-06-02 18:40:56,150]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail stevenlee1976@gmail.com
[2026-06-02 18:40:56,151]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:41:01,446]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,stevenlee1976@gmail.com
[2026-06-02 18:42:10,460]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市兴中时尚流行资讯网络有限公司,XINGZHONG FASHION CONSULTANT,xzbusiness@163.com
[2026-06-02 18:42:10,460]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 34 count 30
[2026-06-02 18:42:10,463]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xzbusiness@163.com
[2026-06-02 18:42:10,463]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:42:16,399]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xzbusiness@163.com
[2026-06-02 18:43:19,414]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,广州凤改改科技有限公司,guangzhoufenggaigaikejiyouxiangongsi,gzfenggaigai@163.com
[2026-06-02 18:43:19,414]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 35 count 31
[2026-06-02 18:43:19,418]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail gzfenggaigai@163.com
[2026-06-02 18:43:19,418]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:43:24,249]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,gzfenggaigai@163.com
[2026-06-02 18:44:34,261]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,广州聪冲冲科技有限公司,guangzhoucongchongchongkejiyouxiangongsi,samrtveiwergz@163.com
[2026-06-02 18:44:34,261]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 36 count 32
[2026-06-02 18:44:34,264]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail samrtveiwergz@163.com
[2026-06-02 18:44:34,265]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:44:39,149]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,samrtveiwergz@163.com
[2026-06-02 18:45:45,163]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市思然电商服务有限责任公司,Shenzhen Siran E-Commerce Service Co., Ltd.,siran2024@126.com
[2026-06-02 18:45:45,163]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 37 count 33
[2026-06-02 18:45:45,166]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail siran2024@126.com
[2026-06-02 18:45:45,167]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:45:50,495]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,siran2024@126.com
[2026-06-02 18:46:59,510]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市泽炫电子商务有限公司,shenzhenshizexuandianzishangwuyouxiangongsi,1336838743@qq.com
[2026-06-02 18:46:59,510]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 324 count 34
[2026-06-02 18:46:59,513]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1336838743@qq.com
[2026-06-02 18:46:59,513]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:47:04,422]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1336838743@qq.com
[2026-06-02 18:48:13,426]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,沈阳吞戾治商贸有限公司,shenyangtunlizhishangmaoyouxiangongsi,yifoi5657@outlook.com
[2026-06-02 18:48:13,426]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 39 count 35
[2026-06-02 18:48:13,429]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yifoi5657@outlook.com
[2026-06-02 18:48:13,429]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:48:18,974]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yifoi5657@outlook.com
[2026-06-02 18:49:26,978]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,深圳市思研电源科技有限公司,Shenzhen Siyan Power Technology Co., Ltd.,siyandianyuan@163.com
[2026-06-02 18:49:26,978]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 40 count 36
[2026-06-02 18:49:26,983]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail siyandianyuan@163.com
[2026-06-02 18:49:26,983]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:49:33,061]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,siyandianyuan@163.com
[2026-06-02 18:50:41,072]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,广州析木贸易有限公司,guangzhouximumaoyiyouxiangongsi,A13827081265@outlook.com
[2026-06-02 18:50:41,072]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 41 count 37
[2026-06-02 18:50:41,075]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail A13827081265@outlook.com
[2026-06-02 18:50:41,076]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 18:50:47,441]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,A13827081265@outlook.com
[2026-06-02 18:51:55,443]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:225)col,中山市鑫树聚照明科技有限公司,zhongshanshixinshujuzhaomingkejiyouxiangongsi,LYQ158159@outlook.com
[2026-06-02 18:51:55,443]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:226)i 42 count 38
[2026-06-02 18:51:55,446]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail LYQ158159@outlook.com
[2026-06-02 18:51:55,447]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
javax.mail.MessagingException: Exception reading response;
  nested exception is:
	java.net.SocketTimeoutException: Read timed out
	at com.sun.mail.smtp.SMTPTransport.readServerResponse(SMTPTransport.java:2202)
	at com.sun.mail.smtp.SMTPTransport.issueSendCommand(SMTPTransport.java:2087)
	at com.sun.mail.smtp.SMTPTransport.finishData(SMTPTransport.java:1889)
	at com.sun.mail.smtp.SMTPTransport.sendMessage(SMTPTransport.java:1120)
	at javax.mail.Transport.send0(Transport.java:195)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)
Caused by: java.net.SocketTimeoutException: Read timed out
	at java.base/java.net.SocketInputStream.socketRead0(Native Method)
	at java.base/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)
	at java.base/java.net.SocketInputStream.read(SocketInputStream.java:168)
	at java.base/java.net.SocketInputStream.read(SocketInputStream.java:140)
	at java.base/sun.security.ssl.SSLSocketInputRecord.read(SSLSocketInputRecord.java:484)
	at java.base/sun.security.ssl.SSLSocketInputRecord.readHeader(SSLSocketInputRecord.java:478)
	at java.base/sun.security.ssl.SSLSocketInputRecord.bytesInCompletePacket(SSLSocketInputRecord.java:70)
	at java.base/sun.security.ssl.SSLSocketImpl.readApplicationRecord(SSLSocketImpl.java:1455)
	at java.base/sun.security.ssl.SSLSocketImpl$AppInputStream.read(SSLSocketImpl.java:1066)
	at com.sun.mail.util.TraceInputStream.read(TraceInputStream.java:124)
	at java.base/java.io.BufferedInputStream.fill(BufferedInputStream.java:252)
	at java.base/java.io.BufferedInputStream.read(BufferedInputStream.java:271)
	at com.sun.mail.util.LineInputStream.readLine(LineInputStream.java:89)
	at com.sun.mail.smtp.SMTPTransport.readServerResponse(SMTPTransport.java:2182)
	... 8 more
javax.mail.MessagingException: Exception reading response;
  nested exception is:
	java.net.SocketTimeoutException: Read timed out
	at com.sun.mail.smtp.SMTPTransport.readServerResponse(SMTPTransport.java:2202)
	at com.sun.mail.smtp.SMTPTransport.issueSendCommand(SMTPTransport.java:2087)
	at com.sun.mail.smtp.SMTPTransport.finishData(SMTPTransport.java:1889)
	at com.sun.mail.smtp.SMTPTransport.sendMessage(SMTPTransport.java:1120)
	at javax.mail.Transport.send0(Transport.java:195)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)
Caused by: java.net.SocketTimeoutException: Read timed out
	at java.base/java.net.SocketInputStream.socketRead0(Native Method)
	at java.base/java.net.SocketInputStream.socketRead(SocketInputStream.java:115)
	at java.base/java.net.SocketInputStream.read(SocketInputStream.java:168)
	at java.base/java.net.SocketInputStream.read(SocketInputStream.java:140)
	at java.base/sun.security.ssl.SSLSocketInputRecord.read(SSLSocketInputRecord.java:484)
	at java.base/sun.security.ssl.SSLSocketInputRecord.readHeader(SSLSocketInputRecord.java:478)
	at java.base/sun.security.ssl.SSLSocketInputRecord.bytesInCompletePacket(SSLSocketInputRecord.java:70)
	at java.base/sun.security.ssl.SSLSocketImpl.readApplicationRecord(SSLSocketImpl.java:1455)
	at java.base/sun.security.ssl.SSLSocketImpl$AppInputStream.read(SSLSocketImpl.java:1066)
	at com.sun.mail.util.TraceInputStream.read(TraceInputStream.java:124)
	at java.base/java.io.BufferedInputStream.fill(BufferedInputStream.java:252)
	at java.base/java.io.BufferedInputStream.read(BufferedInputStream.java:271)
	at com.sun.mail.util.LineInputStream.readLine(LineInputStream.java:89)
	at com.sun.mail.smtp.SMTPTransport.readServerResponse(SMTPTransport.java:2182)
	... 8 more
[2026-06-02 18:52:09,174]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:245)END


[2026-06-02 20:37:12,756]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-02 20:37:17,058]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-02 20:37:17,060]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,张家港浩川纸制品有限公司,zhangjiaganghaochuanzhizhipinyouxiangongsi,fenxiangmobai19987@163.com
[2026-06-02 20:37:17,061]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 43 count 39
[2026-06-02 20:37:28,534]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail fenxiangmobai19987@163.com
[2026-06-02 20:37:28,534]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:37:35,954]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,fenxiangmobai19987@163.com
[2026-06-02 20:38:42,962]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,金华市铭柳逸电子商务有限公司,Jinhua Mingliuyi E-Commerce Co., Ltd.,mingliuyi168@126.com
[2026-06-02 20:38:42,962]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 186 count 40
[2026-06-02 20:38:42,967]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail mingliuyi168@126.com
[2026-06-02 20:38:42,967]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:38:47,310]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,mingliuyi168@126.com
[2026-06-02 20:39:55,321]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市启耀阳科技有限公司　,Shenzhen Qiyao Yang Technology Co., Ltd,qiyaoyang8866@163.com
[2026-06-02 20:39:55,322]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 58 count 41
[2026-06-02 20:39:55,327]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail qiyaoyang8866@163.com
[2026-06-02 20:39:55,328]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:40:00,264]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,qiyaoyang8866@163.com
[2026-06-02 20:41:03,276]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市多多米电子科技有限公司,dongguanshiduoduomidianzikejiyouxiangongsi,QIUYMI1264@outlook.com
[2026-06-02 20:41:03,276]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 405 count 42
[2026-06-02 20:41:03,280]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail QIUYMI1264@outlook.com
[2026-06-02 20:41:03,280]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:41:08,098]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,QIUYMI1264@outlook.com
[2026-06-02 20:42:11,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市西游悟空电子商务有限公司,shenzhenshixiyouwukongdianzishangwuyouxiangongsi,1963616665@qq.com
[2026-06-02 20:42:11,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 50 count 43
[2026-06-02 20:42:11,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1963616665@qq.com
[2026-06-02 20:42:11,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:42:15,949]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1963616665@qq.com
[2026-06-02 20:43:20,963]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳志真科技有限公司,Shenzhen Zhizhen kejiyouxiangongsi,f18038071565@outlook.com
[2026-06-02 20:43:20,963]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 48 count 44
[2026-06-02 20:43:20,967]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail f18038071565@outlook.com
[2026-06-02 20:43:20,967]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:43:26,022]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,f18038071565@outlook.com
[2026-06-02 20:44:32,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,海口仁青扎西销售有限公司,haikourenqingzhaxixiaoshouyouxiangongsi,renqingzha@163.com
[2026-06-02 20:44:32,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 49 count 45
[2026-06-02 20:44:32,042]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail renqingzha@163.com
[2026-06-02 20:44:32,042]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:44:36,895]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,renqingzha@163.com
[2026-06-02 20:45:44,898]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州鑫赛诺照明科技有限公司,guangzhouxinsainuozhaomingkejiyouxiangongsi,C19223675649@outlook.com
[2026-06-02 20:45:44,898]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 51 count 46
[2026-06-02 20:45:44,902]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail C19223675649@outlook.com
[2026-06-02 20:45:44,902]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:45:50,079]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,C19223675649@outlook.com
[2026-06-02 20:46:54,084]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳创青春科技有限公司,Shen Zhen Chuang Qing Chun Ke Ji You Xian Gong Si,abcpow-japan@outlook.com
[2026-06-02 20:46:54,084]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 52 count 47
[2026-06-02 20:46:54,088]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail abcpow-japan@outlook.com
[2026-06-02 20:46:54,089]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:46:59,612]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,abcpow-japan@outlook.com
[2026-06-02 20:48:03,626]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳星空开元科技有限公司,Shenzhen Xingkongkaiyuan Technology Co., Ltd,xkky6929@outlook.com
[2026-06-02 20:48:03,626]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 54 count 48
[2026-06-02 20:48:03,630]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xkky6929@outlook.com
[2026-06-02 20:48:03,630]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:48:08,744]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xkky6929@outlook.com
[2026-06-02 20:49:14,745]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市钰嘉网络科技有限公司,shenzhenshiyujiawangluokejiyouxiangongsi,wr545002@sina.com
[2026-06-02 20:49:14,745]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 56 count 49
[2026-06-02 20:49:14,749]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wr545002@sina.com
[2026-06-02 20:49:14,749]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:49:22,663]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wr545002@sina.com
[2026-06-02 20:50:27,663]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,陕西云端天幕网络科技有限责任公司,SHANXIYUNDUANTIANMUWANGLUOKEJIYOUXIANZERENGONGSI,HananTewell579@outlook.com
[2026-06-02 20:50:27,663]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 57 count 50
[2026-06-02 20:50:27,667]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HananTewell579@outlook.com
[2026-06-02 20:50:27,667]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:50:33,056]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HananTewell579@outlook.com
[2026-06-02 20:51:38,070]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,上海泰摩咖啡器具有限公司,ShangHai TIMEMORE Coffee equipment Co., Ltd.,amazon.us@timemore.com
[2026-06-02 20:51:38,070]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 59 count 51
[2026-06-02 20:51:38,074]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail amazon.us@timemore.com
[2026-06-02 20:51:38,074]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:51:42,816]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,amazon.us@timemore.com
[2026-06-02 20:52:44,828]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港萬卡達貿易有限公司,HONG KONG VANKADA TRADING CO., LIMITED,1508239@qq.com
[2026-06-02 20:52:44,828]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 60 count 52
[2026-06-02 20:52:44,832]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1508239@qq.com
[2026-06-02 20:52:44,832]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:52:50,018]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1508239@qq.com
[2026-06-02 20:53:55,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,ALPICOOL INC.　,ALPICOOL INC.,szjp@alpicool.com
[2026-06-02 20:53:55,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 107 count 53
[2026-06-02 20:53:55,024]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail szjp@alpicool.com
[2026-06-02 20:53:55,024]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:53:59,456]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,szjp@alpicool.com
[2026-06-02 20:55:05,461]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市者话科技有限公司,shenzhenshizhehuakejiyouxiangongsi,sxilinxin@163.com
[2026-06-02 20:55:05,461]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 62 count 54
[2026-06-02 20:55:05,466]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail sxilinxin@163.com
[2026-06-02 20:55:05,466]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:55:12,424]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,sxilinxin@163.com
[2026-06-02 20:56:17,438]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市来福拓科技有限公司,shenzhenshilaifutuokejiyouxiangongsi,672192595@qq.com
[2026-06-02 20:56:17,438]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 63 count 55
[2026-06-02 20:56:17,441]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 672192595@qq.com
[2026-06-02 20:56:17,442]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:56:23,393]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,672192595@qq.com
[2026-06-02 20:57:31,408]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,李振雄,ZhenXiong　Li,lee0008@163.com
[2026-06-02 20:57:31,408]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 64 count 56
[2026-06-02 20:57:31,412]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lee0008@163.com
[2026-06-02 20:57:31,412]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:57:37,047]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lee0008@163.com
[2026-06-02 20:58:45,048]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁思倩贸有限公司,xinningsiqianshangmaoyouxiangongsi,XNSIQIAN@163.COM
[2026-06-02 20:58:45,048]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 65 count 57
[2026-06-02 20:58:45,051]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail XNSIQIAN@163.COM
[2026-06-02 20:58:45,052]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 20:58:50,829]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,XNSIQIAN@163.COM
[2026-06-02 20:59:59,834]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州市信铭电子科技有限公司,GuangZhouShi XinMing DianZiKeji Co., Ltd,urchoiceltdjp@hotmail.com
[2026-06-02 20:59:59,834]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 66 count 58
[2026-06-02 20:59:59,839]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail urchoiceltdjp@hotmail.com
[2026-06-02 20:59:59,840]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:00:04,573]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,urchoiceltdjp@hotmail.com
[2026-06-02 21:01:08,584]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,恩施市鹏欣网络科技有限公司,EnShiShiPengXinWangLuoKeJiYouXianGongSi,PengXinWangLuo@outlook.com
[2026-06-02 21:01:08,584]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 67 count 59
[2026-06-02 21:01:08,587]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail PengXinWangLuo@outlook.com
[2026-06-02 21:01:08,587]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:01:14,987]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,PengXinWangLuo@outlook.com
[2026-06-02 21:02:16,990]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,武汉市克嘉芮商贸有限公司,Wuhan Kejiarui Trading Co., Ltd.,1939765894@qq.com
[2026-06-02 21:02:16,990]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 68 count 60
[2026-06-02 21:02:16,995]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1939765894@qq.com
[2026-06-02 21:02:16,996]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:02:22,511]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1939765894@qq.com
[2026-06-02 21:03:23,526]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,永康市锐名新创贸易有限公司,Yongkang Ruiming Xinchuang Trading Co., Ltd.,qjh@umayfit.com
[2026-06-02 21:03:23,526]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 69 count 61
[2026-06-02 21:03:23,530]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail qjh@umayfit.com
[2026-06-02 21:03:23,530]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:03:28,399]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,qjh@umayfit.com
[2026-06-02 21:04:35,415]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市伊莱雯服装有限公司,shenzhenshiyilaiwenfuzhuangyouxiangongsi,ylw-eu@hotmail.com
[2026-06-02 21:04:35,415]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 70 count 62
[2026-06-02 21:04:35,419]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ylw-eu@hotmail.com
[2026-06-02 21:04:35,419]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:04:39,789]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ylw-eu@hotmail.com
[2026-06-02 21:05:44,797]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市旅居猫文化传播有限公司,Lvjumao Culture Communication Co., Ltd,2388974779@qq.com
[2026-06-02 21:05:44,797]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 94 count 63
[2026-06-02 21:05:44,800]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 2388974779@qq.com
[2026-06-02 21:05:44,801]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:05:49,536]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,2388974779@qq.com
[2026-06-02 21:06:53,539]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,KEBTEK LTD,KEBTEK LTD,kebtekuk_jp@163.com
[2026-06-02 21:06:53,539]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 74 count 64
[2026-06-02 21:06:53,543]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kebtekuk_jp@163.com
[2026-06-02 21:06:53,543]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:06:59,700]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kebtekuk_jp@163.com
[2026-06-02 21:08:06,708]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞艾斯环保建材有限公司,Dong Guan Ai Si Huan Bao Jian Cai Co., Ltd.,361210890@qq.com
[2026-06-02 21:08:06,708]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 75 count 65
[2026-06-02 21:08:06,711]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 361210890@qq.com
[2026-06-02 21:08:06,712]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:08:14,216]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,361210890@qq.com
[2026-06-02 21:09:19,226]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新蔡县慕尚装饰装修工程有限公司,xincaixianmushangzhuangshizhuangxiugongchengyouxiangongsi,RRRRibenkj193@163.com
[2026-06-02 21:09:19,226]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 76 count 66
[2026-06-02 21:09:19,230]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail RRRRibenkj193@163.com
[2026-06-02 21:09:19,230]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:09:25,166]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,RRRRibenkj193@163.com
[2026-06-02 21:10:26,176]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州市承昇商贸有限公司,GuangZhouShiChengShengShangMaoYouXianGongSi,guoli202507@163.com
[2026-06-02 21:10:26,176]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 77 count 67
[2026-06-02 21:10:26,179]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail guoli202507@163.com
[2026-06-02 21:10:26,180]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:10:31,336]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,guoli202507@163.com
[2026-06-02 21:11:40,338]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市云之星科技有限公司,shenzhenshiyunzhixingkejiyouxiangongsi,eibiko0926@163.com
[2026-06-02 21:11:40,338]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 78 count 68
[2026-06-02 21:11:40,341]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail eibiko0926@163.com
[2026-06-02 21:11:40,342]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:11:45,546]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,eibiko0926@163.com
[2026-06-02 21:12:55,557]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,四会市杰航贸易有限公司,SiHuiShi JieHang Trading Limited Company,724468203@qq.com
[2026-06-02 21:12:55,557]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 79 count 69
[2026-06-02 21:12:55,562]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 724468203@qq.com
[2026-06-02 21:12:55,563]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:13:01,698]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,724468203@qq.com
[2026-06-02 21:14:05,712]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,钟健强,JianQiang　Zhong,yazhi-1987@163.com
[2026-06-02 21:14:05,712]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 80 count 70
[2026-06-02 21:14:05,715]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yazhi-1987@163.com
[2026-06-02 21:14:05,716]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:14:10,991]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yazhi-1987@163.com
[2026-06-02 21:15:19,994]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市睿萌科技有限公司,shen zhen shi rui meng ke ji you xian gong si,1359717056@qq.com
[2026-06-02 21:15:19,994]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 81 count 71
[2026-06-02 21:15:19,999]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1359717056@qq.com
[2026-06-02 21:15:19,999]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:15:25,215]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1359717056@qq.com
[2026-06-02 21:16:35,227]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新化县新一阳电子商务有限公司,xinhuaxianxinyiyangdianzishangwuyouxiangongsi,xinyiyang1031@163.com
[2026-06-02 21:16:35,228]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 82 count 72
[2026-06-02 21:16:35,232]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xinyiyang1031@163.com
[2026-06-02 21:16:35,233]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:16:40,598]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xinyiyang1031@163.com
[2026-06-02 21:17:43,602]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,上海昔有科技有限公司,Shanghai Xiyou Technology Co., Ltd.,voltstar@yeah.net
[2026-06-02 21:17:43,602]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 83 count 73
[2026-06-02 21:17:43,606]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail voltstar@yeah.net
[2026-06-02 21:17:43,606]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:17:48,915]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,voltstar@yeah.net
[2026-06-02 21:18:55,930]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,同心互聯(中國)有限公司,Tongxin Internet (China) Limited,593123637@qq.com
[2026-06-02 21:18:55,930]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 84 count 74
[2026-06-02 21:18:55,934]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 593123637@qq.com
[2026-06-02 21:18:55,934]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:19:02,813]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,593123637@qq.com
[2026-06-02 21:20:05,827]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,山东依晨珠宝有限公司,shandongyichenzhubaoyouxiangongsi,416305791@qq.com
[2026-06-02 21:20:05,827]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 233 count 75
[2026-06-02 21:20:05,831]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 416305791@qq.com
[2026-06-02 21:20:05,832]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:20:11,298]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,416305791@qq.com
[2026-06-02 21:21:19,304]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,惠州市静荣华科技有限公司,Huizhou Jingronghua Technology Co., Ltd.,13312936143@163.com
[2026-06-02 21:21:19,304]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 86 count 76
[2026-06-02 21:21:19,307]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 13312936143@163.com
[2026-06-02 21:21:19,307]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:21:24,269]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,13312936143@163.com
[2026-06-02 21:22:27,276]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市如鼎禾科技有限公司,Shenzhenshi Rudinghe Technology Co., Ltd,anniber_jp@163.com
[2026-06-02 21:22:27,276]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 87 count 77
[2026-06-02 21:22:27,279]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail anniber_jp@163.com
[2026-06-02 21:22:27,279]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:22:32,403]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,anniber_jp@163.com
[2026-06-02 21:23:36,414]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市聚诗科技有限公司,Shenzhenshi Jushi Kejiyouxiangongsi,ceciliaABC3996@163.com
[2026-06-02 21:23:36,414]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 88 count 78
[2026-06-02 21:23:36,417]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ceciliaABC3996@163.com
[2026-06-02 21:23:36,417]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:23:41,379]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ceciliaABC3996@163.com
[2026-06-02 21:24:43,382]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市杰泓包装有限公司,SHENZHEN SHI JIEHONG BAOZHUANG YOUXIAN GONGSI,laolee@outlook.jp
[2026-06-02 21:24:43,382]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 89 count 79
[2026-06-02 21:24:43,385]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail laolee@outlook.jp
[2026-06-02 21:24:43,385]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:24:52,796]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,laolee@outlook.jp
[2026-06-02 21:25:53,803]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,林建,LIN　JIAN,191681280@qq.com
[2026-06-02 21:25:53,803]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 90 count 80
[2026-06-02 21:25:53,806]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 191681280@qq.com
[2026-06-02 21:25:53,807]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:25:58,905]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,191681280@qq.com
[2026-06-02 21:27:07,914]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,赵惠野,zhao　huiye,564212527@qq.com
[2026-06-02 21:27:07,914]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 91 count 81
[2026-06-02 21:27:07,917]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 564212527@qq.com
[2026-06-02 21:27:07,917]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:27:13,313]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,564212527@qq.com
[2026-06-02 21:28:17,320]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州庆讯商贸有限公司,GUANGZHOUQINGXUNSHANGMAOYOUXIANGONGSI,CalnimptewaFetzer4009@outlook.com
[2026-06-02 21:28:17,320]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 92 count 82
[2026-06-02 21:28:17,323]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail CalnimptewaFetzer4009@outlook.com
[2026-06-02 21:28:17,323]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:28:22,179]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,CalnimptewaFetzer4009@outlook.com
[2026-06-02 21:29:23,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,山西创亿电子科技有限公司,Shan xi chuang yi dian zi ke ji you xian gong si,chuangyiyajianguk@163.com
[2026-06-02 21:29:23,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 93 count 83
[2026-06-02 21:29:23,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail chuangyiyajianguk@163.com
[2026-06-02 21:29:23,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:29:27,795]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,chuangyiyajianguk@163.com
[2026-06-02 21:30:31,800]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙止拍贸易有限公司,ChangShaZhiPaiMaoYiYouXianGongSi,xiaoyanlan19@163.com
[2026-06-02 21:30:31,800]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 95 count 84
[2026-06-02 21:30:31,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xiaoyanlan19@163.com
[2026-06-02 21:30:31,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:30:37,551]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xiaoyanlan19@163.com
[2026-06-02 21:31:47,552]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市如虎添亿科技有限公司,shenzhenshiruhutianyikejiyouxiangongsi,Sunnymtiger@outlook.com
[2026-06-02 21:31:47,552]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 96 count 85
[2026-06-02 21:31:47,555]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Sunnymtiger@outlook.com
[2026-06-02 21:31:47,555]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:31:53,288]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Sunnymtiger@outlook.com
[2026-06-02 21:33:02,289]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,莆田市城厢区恰纪议贸易有限公司,putianshichengxiangquqiajiyimaoyiyouxiangongsi,xpf289587@163.com
[2026-06-02 21:33:02,289]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 97 count 86
[2026-06-02 21:33:02,293]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xpf289587@163.com
[2026-06-02 21:33:02,294]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:33:07,151]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xpf289587@163.com
[2026-06-02 21:34:13,164]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,商丘拓岛商贸有限公司,Shangqiu Tuodao Trading Co., Ltd,tuodaoshangmao@163.com
[2026-06-02 21:34:13,164]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 98 count 87
[2026-06-02 21:34:13,169]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tuodaoshangmao@163.com
[2026-06-02 21:34:13,169]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:34:19,196]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tuodaoshangmao@163.com
[2026-06-02 21:35:27,210]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山韶明科技有限公司,zhongshanshaomingkejiyouxiangongsi,NH_mingming@hotmail.com
[2026-06-02 21:35:27,210]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 239 count 88
[2026-06-02 21:35:27,214]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail NH_mingming@hotmail.com
[2026-06-02 21:35:27,215]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 21:35:32,109]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,NH_mingming@hotmail.com
[2026-06-02 21:36:34,124]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市力信供应链科技有限公司,shenzhenshilixingongyingliankejiyouxiangongsi,lexonintl_jp@hotmail.com
[2026-06-02 21:36:34,124]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 100 count 89
[2026-06-02 21:36:34,127]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lexonintl_jp@hotmail.com
[2026-06-02 21:36:34,128]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
javax.mail.MessagingException: Can't send command to SMTP host;
  nested exception is:
	java.net.SocketException: Connection or outbound has closed
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2157)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2144)
	at com.sun.mail.smtp.SMTPTransport.close(SMTPTransport.java:1210)
	at javax.mail.Transport.send0(Transport.java:197)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:233)
Caused by: java.net.SocketException: Connection or outbound has closed
	at java.base/sun.security.ssl.SSLSocketImpl$AppOutputStream.write(SSLSocketImpl.java:1298)
	at com.sun.mail.util.TraceOutputStream.write(TraceOutputStream.java:128)
	at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
	at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2155)
	... 7 more
javax.mail.MessagingException: Can't send command to SMTP host;
  nested exception is:
	java.net.SocketException: Connection or outbound has closed
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2157)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2144)
	at com.sun.mail.smtp.SMTPTransport.close(SMTPTransport.java:1210)
	at javax.mail.Transport.send0(Transport.java:197)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:233)
Caused by: java.net.SocketException: Connection or outbound has closed
	at java.base/sun.security.ssl.SSLSocketImpl$AppOutputStream.write(SSLSocketImpl.java:1298)
	at com.sun.mail.util.TraceOutputStream.write(TraceOutputStream.java:128)
	at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
	at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2155)
	... 7 more
[2026-06-02 21:36:40,236]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:249)END

[2026-06-02 22:46:00,957]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-02 22:46:06,008]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-02 22:46:06,010]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市力信供应链科技有限公司,shenzhenshilixingongyingliankejiyouxiangongsi,lexonintl_jp@hotmail.com
[2026-06-02 22:46:06,010]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 100 count 89
[2026-06-02 22:46:06,093]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lexonintl_jp@hotmail.com
[2026-06-02 22:46:06,094]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:46:13,543]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lexonintl_jp@hotmail.com
[2026-06-02 22:47:17,552]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,山西尘栋商贸有限公司,ShanXiChenDongShangMaoCo.,Ltd,Liura2024@163.com
[2026-06-02 22:47:17,553]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 101 count 90
[2026-06-02 22:47:17,557]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Liura2024@163.com
[2026-06-02 22:47:17,557]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:47:22,656]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Liura2024@163.com
[2026-06-02 22:48:24,659]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市翔华元科技有限公司　,shenzhenshixianghuayuankejiyouxiangongsi,fivebox_jp@163.com
[2026-06-02 22:48:24,660]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 102 count 91
[2026-06-02 22:48:24,665]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail fivebox_jp@163.com
[2026-06-02 22:48:24,665]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:48:29,854]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,fivebox_jp@163.com
[2026-06-02 22:49:30,866]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市虞茗科技有限公司,Shenzhen Yuming Technology Co., Ltd.,32493249ym@sina.com
[2026-06-02 22:49:30,866]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 103 count 92
[2026-06-02 22:49:30,870]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 32493249ym@sina.com
[2026-06-02 22:49:30,871]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:49:36,264]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,32493249ym@sina.com
[2026-06-02 22:50:40,275]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市欧卜生电子有限公司,shenzhenshi oubosheng dianzi youxian gongsi,amazonjpfinder@163.com
[2026-06-02 22:50:40,275]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 105 count 93
[2026-06-02 22:50:40,280]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail amazonjpfinder@163.com
[2026-06-02 22:50:40,280]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:50:46,020]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,amazonjpfinder@163.com
[2026-06-02 22:51:53,030]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,洪洞县圣泰豪鑫商贸有限公司,hongtongxianshengtaihaoxinshangmaoyouxiangongsi,paridokondam7@hotmail.com
[2026-06-02 22:51:53,030]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 285 count 94
[2026-06-02 22:51:53,035]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail paridokondam7@hotmail.com
[2026-06-02 22:51:53,036]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:51:58,015]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,paridokondam7@hotmail.com
[2026-06-02 22:53:05,015]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市似鼎科技有限公司,shenzhenshisidingkejiyouxiangongsi,gigreen2024@163.com
[2026-06-02 22:53:05,015]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 108 count 95
[2026-06-02 22:53:05,021]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail gigreen2024@163.com
[2026-06-02 22:53:05,021]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:53:10,675]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,gigreen2024@163.com
[2026-06-02 22:54:13,689]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市振跃祥科技有限公司,shenzhenshizhenyuexiangkejiyouxiangongsi,ipoto_jp@163.com
[2026-06-02 22:54:13,689]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 109 count 96
[2026-06-02 22:54:13,693]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ipoto_jp@163.com
[2026-06-02 22:54:13,694]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:54:18,982]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ipoto_jp@163.com
[2026-06-02 22:55:22,984]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市酷而美创新科技有限公司,Shen Zhen Shi Ku Er Mei Chuang Xin Ke Ji You Xian Gong Si,tony.xia@coolmetech.com
[2026-06-02 22:55:22,984]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 110 count 97
[2026-06-02 22:55:22,988]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tony.xia@coolmetech.com
[2026-06-02 22:55:22,988]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:55:28,429]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tony.xia@coolmetech.com
[2026-06-02 22:56:29,444]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,河北雄安超昨商贸有限责任公司　,hebeixionganchaozuoshangmaoyouxianzerengongsi,meind1230@163.com
[2026-06-02 22:56:29,444]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 111 count 98
[2026-06-02 22:56:29,449]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail meind1230@163.com
[2026-06-02 22:56:29,449]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:56:34,979]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,meind1230@163.com
[2026-06-02 22:57:40,986]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,赣州同行建筑劳务有限公司　,ganzhoutongxingjianzhulaowuyouxiangongsi,tisca1879jon@gmail.com
[2026-06-02 22:57:40,986]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 112 count 99
[2026-06-02 22:57:40,989]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tisca1879jon@gmail.com
[2026-06-02 22:57:40,989]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:57:47,301]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tisca1879jon@gmail.com
[2026-06-02 22:58:55,306]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市集翼科技有限公司,shenzhenshijiyikejiyouxiangongsi,hootek-jp@outlook.com
[2026-06-02 22:58:55,306]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 113 count 100
[2026-06-02 22:58:55,309]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hootek-jp@outlook.com
[2026-06-02 22:58:55,309]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 22:59:01,208]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hootek-jp@outlook.com
[2026-06-02 23:00:08,211]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,Jinhua Yichuan Dianzi Shangwu CO.,Ltd,Jinhua Yichuan Dianzi Shangwu CO.,Ltd,barb@unitegreat.com
[2026-06-02 23:00:08,212]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 114 count 101
[2026-06-02 23:00:08,215]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail barb@unitegreat.com
[2026-06-02 23:00:08,216]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:00:13,230]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,barb@unitegreat.com
[2026-06-02 23:01:21,233]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市蓝久汇科技有限公司　,Shenzhenshi Lanjiuhui Technology Co., Ltd,hopepow-jp2017@outlook.com
[2026-06-02 23:01:21,233]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 115 count 102
[2026-06-02 23:01:21,237]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hopepow-jp2017@outlook.com
[2026-06-02 23:01:21,238]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:01:26,991]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hopepow-jp2017@outlook.com
[2026-06-02 23:02:34,992]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,韵琪家（广州）服装有限公司　,yunqijiaguangzhoufuzhuangyouxiangongsi,yunqijia1@126.com
[2026-06-02 23:02:34,992]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 116 count 103
[2026-06-02 23:02:34,996]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yunqijia1@126.com
[2026-06-02 23:02:34,996]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:02:39,619]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yunqijia1@126.com
[2026-06-02 23:03:41,633]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市宇博盛科技有限公司　,Shenzhenshi Yubosheng Kejiyouxiangongsi,wangyangangamz@163.com
[2026-06-02 23:03:41,633]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 117 count 104
[2026-06-02 23:03:41,637]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wangyangangamz@163.com
[2026-06-02 23:03:41,637]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:03:47,464]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wangyangangamz@163.com
[2026-06-02 23:04:51,473]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南京市江宁景媛健康科技有限公司,nanjingshijiangningjingyuanjiankangkejiyouxiangongsi,obyj49@163.com
[2026-06-02 23:04:51,473]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 118 count 105
[2026-06-02 23:04:51,477]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail obyj49@163.com
[2026-06-02 23:04:51,477]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:04:56,586]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,obyj49@163.com
[2026-06-02 23:06:00,602]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南通小磊电子商务有限公司　,Nantong Xiaolei Dianzishangwu Youxiangongsi,916959905@qq.com
[2026-06-02 23:06:00,602]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 120 count 106
[2026-06-02 23:06:00,606]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 916959905@qq.com
[2026-06-02 23:06:00,606]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:06:06,024]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,916959905@qq.com
[2026-06-02 23:07:11,039]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市光无限科技有限公司　,Shenzhenshi Guangwuxian Technology Co., Ltd,chrispow@163.com
[2026-06-02 23:07:11,039]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 121 count 107
[2026-06-02 23:07:11,043]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail chrispow@163.com
[2026-06-02 23:07:11,043]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:07:16,097]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,chrispow@163.com
[2026-06-02 23:08:22,100]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市杰可杰科技有限公司,shenzhenshijiekejiekejiyouxiangongsi,cupeisi.technology@gmail.com
[2026-06-02 23:08:22,100]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 130 count 108
[2026-06-02 23:08:22,106]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cupeisi.technology@gmail.com
[2026-06-02 23:08:22,107]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:08:27,939]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cupeisi.technology@gmail.com
[2026-06-02 23:09:29,944]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市伟义兄弟进出口有限公司,shenzhenshiweiyixiongdijinchukouyouxiangongsi,1507265048@qq.com
[2026-06-02 23:09:29,944]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 126 count 109
[2026-06-02 23:09:29,951]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1507265048@qq.com
[2026-06-02 23:09:29,951]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:09:35,465]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1507265048@qq.com
[2026-06-02 23:10:40,475]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳尊途科技有限公司,Shenzhenshi Zuntu Keji Youxiangongsi,c15889653183@163.com
[2026-06-02 23:10:40,475]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 124 count 110
[2026-06-02 23:10:40,480]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail c15889653183@163.com
[2026-06-02 23:10:40,480]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:10:45,863]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,c15889653183@163.com
[2026-06-02 23:11:46,865]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,袁详华,Yuan　Xianghua,buyhere2015@outlook.com
[2026-06-02 23:11:46,865]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 125 count 111
[2026-06-02 23:11:46,872]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail buyhere2015@outlook.com
[2026-06-02 23:11:46,872]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:11:51,783]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,buyhere2015@outlook.com
[2026-06-02 23:12:52,788]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,义乌市铉盟电子商务有限公司,Yiwu XuanmengE-commerce Co.,Ltd,huzhimo_20151212@163.com
[2026-06-02 23:12:52,788]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 301 count 112
[2026-06-02 23:12:52,792]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail huzhimo_20151212@163.com
[2026-06-02 23:12:52,792]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:12:58,663]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,huzhimo_20151212@163.com
[2026-06-02 23:14:07,675]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市酷佩斯科技有限公司,shenzhenshikupeisikejiyouxiangongsi,coopersjp.technology@gmail.com
[2026-06-02 23:14:07,675]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 129 count 113
[2026-06-02 23:14:07,678]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail coopersjp.technology@gmail.com
[2026-06-02 23:14:07,678]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:14:13,225]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,coopersjp.technology@gmail.com
[2026-06-02 23:15:20,241]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,济南悦海电子商务有限公司,JI Nan Yue Hai Dian Zi Shang Wu You Xian Gong Si,songming332@126.com
[2026-06-02 23:15:20,241]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 131 count 114
[2026-06-02 23:15:20,245]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail songming332@126.com
[2026-06-02 23:15:20,245]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:15:25,222]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,songming332@126.com
[2026-06-02 23:16:34,226]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市鑫华力电子商务有限公司　,shenzhenshixinhualidianzishangwuyouxiangongsi,xinhuali2024@163.com
[2026-06-02 23:16:34,226]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 132 count 115
[2026-06-02 23:16:34,230]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xinhuali2024@163.com
[2026-06-02 23:16:34,230]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:16:39,468]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xinhuali2024@163.com
[2026-06-02 23:17:47,474]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,厦门华洋兴电子科技有限公司　,Xiamen Huayangxing Electronic Techology Co., Ltd,Zaidtek88@163.com
[2026-06-02 23:17:47,474]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 133 count 116
[2026-06-02 23:17:47,478]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Zaidtek88@163.com
[2026-06-02 23:17:47,478]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:17:52,258]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Zaidtek88@163.com
[2026-06-02 23:18:59,267]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,揭阳市墨然贸易有限公司　,Jieyang Mo Ran Trading Co., Ltd.,jymr4877@163.com
[2026-06-02 23:18:59,267]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 134 count 117
[2026-06-02 23:18:59,271]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jymr4877@163.com
[2026-06-02 23:18:59,271]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:19:03,955]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jymr4877@163.com
[2026-06-02 23:20:07,969]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳钰桐鑫科技有限公司,shenzhenshiyutongxinkejiyouxiangongsi,yutong20230302@outlook.com
[2026-06-02 23:20:07,970]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 135 count 118
[2026-06-02 23:20:07,976]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yutong20230302@outlook.com
[2026-06-02 23:20:07,976]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:20:12,737]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yutong20230302@outlook.com
[2026-06-02 23:21:20,738]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山市中栩电器科技有限公司　,Zhongshan Zhongxu Electrical Technology Co., Ltd.,sales01@zchance.cn
[2026-06-02 23:21:20,738]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 136 count 119
[2026-06-02 23:21:20,742]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail sales01@zchance.cn
[2026-06-02 23:21:20,742]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:21:26,499]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,sales01@zchance.cn
[2026-06-02 23:22:31,507]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,泽成电子（中山）有限公司　,ZECHENGDIANZIZHONGSHANYOUXIANGONGSI,zcecom2024@163.com
[2026-06-02 23:22:31,507]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 137 count 120
[2026-06-02 23:22:31,512]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zcecom2024@163.com
[2026-06-02 23:22:31,513]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:22:36,579]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zcecom2024@163.com
[2026-06-02 23:23:41,588]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州佰嘉汇厨具有限公司　,WENZHOUBAIJIAHUICHUJUYOUXIANGONGSI,TaniaEmbt66@outlook.com
[2026-06-02 23:23:41,588]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 138 count 121
[2026-06-02 23:23:41,592]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail TaniaEmbt66@outlook.com
[2026-06-02 23:23:41,592]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:23:46,339]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,TaniaEmbt66@outlook.com
[2026-06-02 23:24:47,346]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市世能照明科技有限公司,Shenzhen Asign Lighting Technology Co., Ltd,michael.li@asignchinaled.com
[2026-06-02 23:24:47,346]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 139 count 122
[2026-06-02 23:24:47,349]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail michael.li@asignchinaled.com
[2026-06-02 23:24:47,349]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:24:52,595]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,michael.li@asignchinaled.com
[2026-06-02 23:26:02,607]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市可诺特商贸有限公司　,ShenZhen shi ke nuo te shangmao youxian gongsi,18948160807@163.com
[2026-06-02 23:26:02,607]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 140 count 123
[2026-06-02 23:26:02,610]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 18948160807@163.com
[2026-06-02 23:26:02,610]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:26:08,745]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,18948160807@163.com
[2026-06-02 23:27:11,750]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,苏州拓花贸易有限公司,Suzhou Tuohua Trading Co., Ltd,616730767@qq.com
[2026-06-02 23:27:11,751]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 143 count 124
[2026-06-02 23:27:11,757]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 616730767@qq.com
[2026-06-02 23:27:11,758]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:27:17,863]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,616730767@qq.com
[2026-06-02 23:28:23,867]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市盈泰电子商务有限公司　,Jiangmen YingTai E-Commerce Co.,Ltd,yingtaijp@outlook.com
[2026-06-02 23:28:23,867]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 142 count 125
[2026-06-02 23:28:23,871]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yingtaijp@outlook.com
[2026-06-02 23:28:23,871]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:28:29,227]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yingtaijp@outlook.com
[2026-06-02 23:29:38,240]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,李鑫　,Li　Xin,1792267603@qq.com
[2026-06-02 23:29:38,240]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 144 count 126
[2026-06-02 23:29:38,243]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1792267603@qq.com
[2026-06-02 23:29:38,244]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:29:44,107]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1792267603@qq.com
[2026-06-02 23:30:47,120]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中鈺企業社　,Zhong Yu Company,hulichung168@outlook.com
[2026-06-02 23:30:47,120]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 145 count 127
[2026-06-02 23:30:47,124]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hulichung168@outlook.com
[2026-06-02 23:30:47,124]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:30:53,444]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hulichung168@outlook.com
[2026-06-02 23:32:03,446]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南京隆赢家居家居科技有限公司　,Nanjing Longying Home Technology Co., Ltd.,jason@ecbarley.com
[2026-06-02 23:32:03,446]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 146 count 128
[2026-06-02 23:32:03,449]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jason@ecbarley.com
[2026-06-02 23:32:03,449]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:32:08,734]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jason@ecbarley.com
[2026-06-02 23:33:12,750]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市前沿智造科技有限公司　,Shenzhen shi Qianyan Zhizao Keji youxian gongsi,kimi@opuor.com.cn
[2026-06-02 23:33:12,750]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 147 count 129
[2026-06-02 23:33:12,753]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kimi@opuor.com.cn
[2026-06-02 23:33:12,753]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:33:18,662]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kimi@opuor.com.cn
[2026-06-02 23:34:27,672]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞福源国际贸易有限公司,DongGuanFuYuanGuoJiMaoYiYouXianGongSi,chao8882009@163.com
[2026-06-02 23:34:27,672]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 148 count 130
[2026-06-02 23:34:27,676]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail chao8882009@163.com
[2026-06-02 23:34:27,676]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:34:33,218]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,chao8882009@163.com
[2026-06-02 23:35:34,224]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州德名立科技有限公司,Guangzhou Demingli Technology Co.,Ltd.,demila0625@outlook.com
[2026-06-02 23:35:34,224]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 253 count 131
[2026-06-02 23:35:34,228]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail demila0625@outlook.com
[2026-06-02 23:35:34,229]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:35:39,147]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,demila0625@outlook.com
[2026-06-02 23:36:48,151]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市菖旺进出口有限公司,Jiangmen Changwang Import and Export Co., Ltd,changwanglighting@163.com
[2026-06-02 23:36:48,151]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 150 count 132
[2026-06-02 23:36:48,154]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail changwanglighting@163.com
[2026-06-02 23:36:48,154]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:36:53,220]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,changwanglighting@163.com
[2026-06-02 23:37:58,225]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州莱喆科科技有限公司,Guangzhou Laizheke Technology Co.,Ltd.,tagiyoon0312@outlook.com
[2026-06-02 23:37:58,225]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 292 count 133
[2026-06-02 23:37:58,228]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tagiyoon0312@outlook.com
[2026-06-02 23:37:58,228]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:38:03,531]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tagiyoon0312@outlook.com
[2026-06-02 23:39:09,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市汇瀛商贸有限公司　,Jiangmen Huiying Trading Co., Ltd,jmhysm0008@163.com
[2026-06-02 23:39:09,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 152 count 134
[2026-06-02 23:39:09,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jmhysm0008@163.com
[2026-06-02 23:39:09,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:39:14,344]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jmhysm0008@163.com
[2026-06-02 23:40:23,352]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖南鑫讯电子商务有限公司　,Hu Nan Xin Xun Dian Zi Shang Wu You Xian Gong Si,liuaizheng_123@outlook.com
[2026-06-02 23:40:23,352]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 153 count 135
[2026-06-02 23:41:05,176]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail liuaizheng_123@outlook.com
[2026-06-02 23:41:05,176]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-02 23:41:10,192]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,liuaizheng_123@outlook.com

[2026-06-03 21:28:59,909]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-03 21:29:04,755]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-03 21:29:04,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市融蓉科技有限公司　,shenzhenshirongrongkejiyouxiangongsi,tyyaoshengxin@163.com
[2026-06-03 21:29:04,758]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 154 count 136
[2026-06-03 21:30:04,703]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tyyaoshengxin@163.com
[2026-06-03 21:30:04,703]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:30:17,101]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tyyaoshengxin@163.com
[2026-06-03 21:31:24,116]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市福昌汇科技有限公司　,JiangMenShiFuChangHuiKeJiYouXianGongSi,fuchanghui66@163.com
[2026-06-03 21:31:24,116]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 155 count 137
[2026-06-03 21:31:24,120]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail fuchanghui66@163.com
[2026-06-03 21:31:24,121]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:31:33,910]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,fuchanghui66@163.com
[2026-06-03 21:32:35,913]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市皓诚灯饰有限公司　,Jiangmen Shi HaoCheng DengShi You Xian Gong Si,xiaohong870715@163.com
[2026-06-03 21:32:35,914]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 156 count 138
[2026-06-03 21:32:35,918]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xiaohong870715@163.com
[2026-06-03 21:32:35,918]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:32:45,930]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xiaohong870715@163.com
[2026-06-03 21:33:47,937]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市佳亿轩科技有限公司　,JiangMenShiJiaYiXuanKeJiYouXianGongSi,jiaqi20162025@163.com
[2026-06-03 21:33:47,938]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 157 count 139
[2026-06-03 21:33:47,941]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jiaqi20162025@163.com
[2026-06-03 21:33:47,941]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:33:57,240]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jiaqi20162025@163.com
[2026-06-03 21:35:04,249]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市广通实业发展有限公司　,shenzhenshiguangtongshiyefazhanyouxiangongsi,guangtongjapan@163.com
[2026-06-03 21:35:04,250]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 158 count 140
[2026-06-03 21:35:04,253]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail guangtongjapan@163.com
[2026-06-03 21:35:04,254]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:35:14,052]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,guangtongjapan@163.com
[2026-06-03 21:36:21,067]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市欧度利方科技有限公司　,Shenzhen Alldocube Science and Technology Co., Ltd.,wangzhihao@51cube.com
[2026-06-03 21:36:21,068]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 159 count 141
[2026-06-03 21:36:21,072]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wangzhihao@51cube.com
[2026-06-03 21:36:21,072]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:36:31,041]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wangzhihao@51cube.com
[2026-06-03 21:37:33,044]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市稳繁科技有限公司　,Shenzhen Wenfan Technology Co., Ltd.,wenfan-gl@outlook.com
[2026-06-03 21:37:33,045]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 160 count 142
[2026-06-03 21:37:33,049]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wenfan-gl@outlook.com
[2026-06-03 21:37:33,049]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:37:43,024]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wenfan-gl@outlook.com
[2026-06-03 21:38:48,037]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市佩金科技有限公司　,Shenzhen Peijin Technology Co., Ltd.,zoudan@51cube.com
[2026-06-03 21:38:48,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 161 count 143
[2026-06-03 21:38:48,042]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zoudan@51cube.com
[2026-06-03 21:38:48,043]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:38:57,889]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zoudan@51cube.com
[2026-06-03 21:40:06,896]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市频甄科技有限公司　,shenzhenshipinzhenkejiyouxiangongsi,Zhanlltyu@outlook.com
[2026-06-03 21:40:06,896]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 162 count 144
[2026-06-03 21:40:06,900]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Zhanlltyu@outlook.com
[2026-06-03 21:40:06,900]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:40:17,888]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Zhanlltyu@outlook.com
[2026-06-03 21:41:21,899]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山市羲和贸易有限公司　,zhongshanshixihemaoyiyouxiangongsi,xihe-jp@outlook.com
[2026-06-03 21:41:21,899]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 163 count 145
[2026-06-03 21:41:21,904]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xihe-jp@outlook.com
[2026-06-03 21:41:21,904]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:41:32,778]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xihe-jp@outlook.com
[2026-06-03 21:42:33,791]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,西安金钥网络科技有限公司,Xi'an Jinyao Network Technology Co., Ltd,joyfulokay@163.com
[2026-06-03 21:42:33,791]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 184 count 146
[2026-06-03 21:42:33,795]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail joyfulokay@163.com
[2026-06-03 21:42:33,795]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:42:43,205]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,joyfulokay@163.com
[2026-06-03 21:43:44,211]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市橙诺科技有限公司　,Shenzhenshi chengnuo keji youxian gongsi,kimi@opuor.com.cn
[2026-06-03 21:43:44,211]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 165 count 147
[2026-06-03 21:43:44,215]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kimi@opuor.com.cn
[2026-06-03 21:43:44,215]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:43:54,750]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kimi@opuor.com.cn
[2026-06-03 21:44:57,762]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市梯拓照明电器有限公司　,JM Tituo Lighting Electrical Appliance Company,tituozhaoming@163.com
[2026-06-03 21:44:57,763]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 166 count 148
[2026-06-03 21:44:57,767]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tituozhaoming@163.com
[2026-06-03 21:44:57,767]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:45:07,163]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tituozhaoming@163.com
[2026-06-03 21:46:09,177]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州双顶贸易发展有限公司,Guangzhou Shuangding Trade Development Co., Ltd.,jenygaogao163@yahoo.co.jp
[2026-06-03 21:46:09,177]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 168 count 149
[2026-06-03 21:46:09,181]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jenygaogao163@yahoo.co.jp
[2026-06-03 21:46:09,181]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:46:18,534]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jenygaogao163@yahoo.co.jp
[2026-06-03 21:47:27,546]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳小度电子有限公司,Shenzhen Xiaodu Electronics Co., Ltd,695858402@qq.com
[2026-06-03 21:47:27,546]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 169 count 150
[2026-06-03 21:47:27,549]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 695858402@qq.com
[2026-06-03 21:47:27,549]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:47:37,597]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,695858402@qq.com
[2026-06-03 21:48:42,602]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山市博纳进出口贸易有限公司,Zhongshanshi bona jinchukoumaoyi youxiangongsi,Emma.bn@outlook.com
[2026-06-03 21:48:42,603]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 170 count 151
[2026-06-03 21:48:42,606]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Emma.bn@outlook.com
[2026-06-03 21:48:42,607]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:48:53,412]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Emma.bn@outlook.com
[2026-06-03 21:50:03,415]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳锂言时代科技有限公司,Shenzhen Litime Technology Co.,Ltd,amazon@litime.com
[2026-06-03 21:50:03,415]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 171 count 152
[2026-06-03 21:50:03,419]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail amazon@litime.com
[2026-06-03 21:50:03,419]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:50:13,259]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,amazon@litime.com
[2026-06-03 21:51:23,273]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市芯技通电子科技有限公司,Dongguan Xinjitong Electronic Technology Co., Ltd.,service@powilling.cn
[2026-06-03 21:51:23,273]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 175 count 153
[2026-06-03 21:51:23,277]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail service@powilling.cn
[2026-06-03 21:51:23,277]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:51:33,126]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,service@powilling.cn
[2026-06-03 21:52:35,128]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,北京鸿达电子商务有限公司,beijinghongdadianzishangwuyouxiangongsi,hddztyj@outlook.com
[2026-06-03 21:52:35,128]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 173 count 154
[2026-06-03 21:52:35,131]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hddztyj@outlook.com
[2026-06-03 21:52:35,131]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:52:46,055]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hddztyj@outlook.com
[2026-06-03 21:53:52,070]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,HONGKONG AGILE OPS TECHNOLOGY CO., LIMITED,HONGKONG AGILE OPS TECHNOLOGY CO., LIMITED,pm@agile-operation.com
[2026-06-03 21:53:52,070]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 174 count 155
[2026-06-03 21:53:52,074]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail pm@agile-operation.com
[2026-06-03 21:53:52,075]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:54:02,110]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,pm@agile-operation.com
[2026-06-03 21:55:09,118]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市兴炫电子商务有限公司,Shenzhen Xingxuan E-commerce Co., Ltd.,xingxuan202408@163.com
[2026-06-03 21:55:09,118]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 194 count 156
[2026-06-03 21:55:09,122]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xingxuan202408@163.com
[2026-06-03 21:55:09,122]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:55:18,855]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xingxuan202408@163.com
[2026-06-03 21:56:27,863]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,厦门尔佃翎贸易有限公司,XIAMENERDIANLINGMAOYIYOUXIANGONGSI,EDL20S@yeah.net
[2026-06-03 21:56:27,863]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 177 count 157
[2026-06-03 21:56:27,867]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail EDL20S@yeah.net
[2026-06-03 21:56:27,867]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:56:39,167]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,EDL20S@yeah.net
[2026-06-03 21:57:44,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州必惠购电子商务有限公司,Guangzhou BiHuiGou E-Commerce Co. Ltd.,arclight@babylife.co.jp
[2026-06-03 21:57:44,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 178 count 158
[2026-06-03 21:57:44,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail arclight@babylife.co.jp
[2026-06-03 21:57:44,184]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:57:55,006]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,arclight@babylife.co.jp
[2026-06-03 21:59:03,021]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳蜥虎科技有限责任公司,Shenzhen Xihu Technology Co., Ltd.,xhkj_24all@163.com
[2026-06-03 21:59:03,022]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 179 count 159
[2026-06-03 21:59:03,027]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xhkj_24all@163.com
[2026-06-03 21:59:03,028]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 21:59:14,367]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xhkj_24all@163.com
[2026-06-03 22:00:21,380]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市尔丽新电子商务有限公司,shenzhenshierlixindianzishangwuyouxiangongsi,LiXin_E-commerce@outlook.com
[2026-06-03 22:00:21,380]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 181 count 160
[2026-06-03 22:00:21,384]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail LiXin_E-commerce@outlook.com
[2026-06-03 22:00:21,384]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:00:33,255]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,LiXin_E-commerce@outlook.com
[2026-06-03 22:01:34,266]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,優家雲選(香港)有限公司,UHOME CLOUDPICK (HONG KONG) LIMITED,km.twus@homselect.top
[2026-06-03 22:01:34,266]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 182 count 161
[2026-06-03 22:01:34,270]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail km.twus@homselect.top
[2026-06-03 22:01:34,270]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:01:45,099]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,km.twus@homselect.top
[2026-06-03 22:02:52,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,九江浩博电子商务有限公司,jiujianghaobodianzishangwuyouxiangongsi,haobo_jp@163.com
[2026-06-03 22:02:52,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 183 count 162
[2026-06-03 22:02:52,104]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail haobo_jp@163.com
[2026-06-03 22:02:52,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:03:02,524]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,haobo_jp@163.com
[2026-06-03 22:04:09,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市新鑫宏电子有限公司,Shenzhen XinXinHong Electronics Co. Ltd.,tradeone@babylife.co.jp
[2026-06-03 22:04:09,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 185 count 163
[2026-06-03 22:04:09,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tradeone@babylife.co.jp
[2026-06-03 22:04:09,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:04:19,645]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tradeone@babylife.co.jp
[2026-06-03 22:05:28,655]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,惠州市惠深服饰有限公司,huizhoushihuishenfushiyouxiangongsi,huishencool_jp@163.com
[2026-06-03 22:05:28,655]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 386 count 164
[2026-06-03 22:05:28,659]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail huishencool_jp@163.com
[2026-06-03 22:05:28,659]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:05:40,997]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,huishencool_jp@163.com
[2026-06-03 22:06:47,999]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,厦门市思明区晟恒裕日用百货店,XIAMENSHI SIMINGQU SHENGHENGYU RIYONGBAIHUODIAN,FF17SHY@126.com
[2026-06-03 22:06:47,999]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 188 count 165
[2026-06-03 22:06:48,002]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail FF17SHY@126.com
[2026-06-03 22:06:48,002]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:06:57,415]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,FF17SHY@126.com
[2026-06-03 22:08:02,429]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市渡澜贸易有限公司,dongguanshidulanmaoyiyouxiangongsi,joesinkurtyh@outlook.com
[2026-06-03 22:08:02,429]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 190 count 166
[2026-06-03 22:08:02,433]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail joesinkurtyh@outlook.com
[2026-06-03 22:08:02,433]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:08:13,161]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,joesinkurtyh@outlook.com
[2026-06-03 22:09:15,176]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市倍兔科技有限公司,Shenzhen Beitu Technology Co., Ltd.,875605380@qq.com
[2026-06-03 22:09:15,176]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 191 count 167
[2026-06-03 22:09:15,180]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 875605380@qq.com
[2026-06-03 22:09:15,180]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:09:24,606]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,875605380@qq.com
[2026-06-03 22:10:32,614]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,烁普电子（东莞）有限公司,Shuopu Electronics (Dongguan) Co., Ltd.,alex.shiyao@qq.com
[2026-06-03 22:10:32,614]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 192 count 168
[2026-06-03 22:10:32,617]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail alex.shiyao@qq.com
[2026-06-03 22:10:32,618]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:10:43,082]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,alex.shiyao@qq.com
[2026-06-03 22:11:50,087]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市万丰达塑胶制品有限公司,Shenzhen Wanfengda Plastic Products Co., Ltd,sharelife2019@126.com
[2026-06-03 22:11:50,087]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 195 count 169
[2026-06-03 22:11:50,090]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail sharelife2019@126.com
[2026-06-03 22:11:50,091]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:11:59,783]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,sharelife2019@126.com
[2026-06-03 22:13:07,790]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州息斯克电子商务有限公司,Guangzhou Xisike E-Commerce Co., Ltd.,thyescom1217@163.com
[2026-06-03 22:13:07,790]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 196 count 170
[2026-06-03 22:13:07,794]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail thyescom1217@163.com
[2026-06-03 22:13:07,794]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:13:18,349]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,thyescom1217@163.com
[2026-06-03 22:14:23,364]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市吉柏科技有限公司,shenzhenshijibaikejiyouxiangongsi,635307131@qq.com
[2026-06-03 22:14:23,364]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 197 count 171
[2026-06-03 22:14:23,367]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 635307131@qq.com
[2026-06-03 22:14:23,367]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:14:33,259]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,635307131@qq.com
[2026-06-03 22:15:39,270]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁县俊露商贸有限公司,xinningxianjunlushangmaoyouxiangongsi,junlusm0526@163.com
[2026-06-03 22:15:39,270]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 200 count 172
[2026-06-03 22:15:39,274]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail junlusm0526@163.com
[2026-06-03 22:15:39,275]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:15:49,876]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,junlusm0526@163.com
[2026-06-03 22:16:53,879]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港创想三维科技有限公司,CREALITY 3D (HK) TECHNOLOGY LIMITED,cxswamz@creality.com
[2026-06-03 22:16:53,879]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 199 count 173
[2026-06-03 22:16:53,883]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cxswamz@creality.com
[2026-06-03 22:16:53,883]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:17:04,446]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cxswamz@creality.com
[2026-06-03 22:18:14,452]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,成都莹诗元电子商务有限公司,chengduyingshiyuandianzishangwuyouxiangongsi,simegu95335@163.com
[2026-06-03 22:18:14,452]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 370 count 174
[2026-06-03 22:18:14,457]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail simegu95335@163.com
[2026-06-03 22:18:14,457]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:18:24,770]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,simegu95335@163.com
[2026-06-03 22:19:27,773]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市光逸科技创新有限公司,Shenzhen Velocity Technology Innovations Co.,Ltd.,ce@livelocity.com
[2026-06-03 22:19:27,773]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 321 count 175
[2026-06-03 22:19:27,776]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ce@livelocity.com
[2026-06-03 22:19:27,777]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:19:37,431]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ce@livelocity.com
[2026-06-03 22:20:41,438]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州帏红商贸有限公司,guangzhouweihongshangmaoyouxiangongsi,weihongshangmao188@yeah.net
[2026-06-03 22:20:41,438]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 203 count 176
[2026-06-03 22:20:41,441]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail weihongshangmao188@yeah.net
[2026-06-03 22:20:41,441]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:20:50,843]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,weihongshangmao188@yeah.net
[2026-06-03 22:21:58,859]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市连韵商贸有限公司,shaoyangshilianyunshangmaoyouxiangongsi,liannnnyunn@163.com
[2026-06-03 22:21:58,859]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 204 count 177
[2026-06-03 22:21:58,863]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail liannnnyunn@163.com
[2026-06-03 22:21:58,864]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:22:09,249]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,liannnnyunn@163.com
[2026-06-03 22:23:15,253]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁县支恒商贸有限公司,xinningxianzhihengshangmaoyouxiangongsi,xnzhiheng2025@163.com
[2026-06-03 22:23:15,253]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 205 count 178
[2026-06-03 22:23:15,256]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xnzhiheng2025@163.com
[2026-06-03 22:23:15,257]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:23:25,731]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xnzhiheng2025@163.com
[2026-06-03 22:24:35,740]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市澜慧莱电子科技有限公司,Dongguan Lanhuilai Electronic Technology Co., Ltd,yangli515918506@163.com
[2026-06-03 22:24:35,740]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 206 count 179
[2026-06-03 22:24:35,743]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail yangli515918506@163.com
[2026-06-03 22:24:35,743]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:24:46,058]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,yangli515918506@163.com
[2026-06-03 22:25:51,059]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,优亚科技（天津）有限公司,Youya Technology (Tianjin) Co., Ltd,3876474548@qq.com
[2026-06-03 22:25:51,059]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 207 count 180
[2026-06-03 22:25:51,063]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 3876474548@qq.com
[2026-06-03 22:25:51,063]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:26:00,605]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,3876474548@qq.com
[2026-06-03 22:27:05,615]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,利錄科技股份有限公司,EZ DUPE INC.,kevinwepihuang@ezdupe.com.tw
[2026-06-03 22:27:05,615]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 225 count 181
[2026-06-03 22:27:05,619]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kevinwepihuang@ezdupe.com.tw
[2026-06-03 22:27:05,619]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:27:15,171]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kevinwepihuang@ezdupe.com.tw
[2026-06-03 22:28:21,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,天枢岚途科技（深圳）有限责任公司,Tianshu Lantu Technology (Shenzhen) Co., Ltd.,TanLatlesOfficial@outlook.com
[2026-06-03 22:28:21,180]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 209 count 182
[2026-06-03 22:28:21,183]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail TanLatlesOfficial@outlook.com
[2026-06-03 22:28:21,184]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:28:31,330]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,TanLatlesOfficial@outlook.com
[2026-06-03 22:29:32,346]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州来根跑跑糖贸易有限公司,wenzhoulaigenpaopaotangmaoyiyouxiangongsi,532347467@qq.com
[2026-06-03 22:29:32,346]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 210 count 183
[2026-06-03 22:29:32,349]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 532347467@qq.com
[2026-06-03 22:29:32,349]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:29:41,733]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,532347467@qq.com
[2026-06-03 22:30:51,740]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,盐城市双裕环保工程有限公司,Yancheng Shuangyu Environmental Protection Engineering Co., Ltd,shuangyuhuanbao88@163.com
[2026-06-03 22:30:51,740]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 212 count 184
[2026-06-03 22:30:51,745]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail shuangyuhuanbao88@163.com
[2026-06-03 22:30:51,745]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:31:02,269]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,shuangyuhuanbao88@163.com
[2026-06-03 22:32:03,274]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市江格户外用品有限公司,Shenzhen Jiangge Outdoor Products Co., LTD,kuma@onetigris.cn
[2026-06-03 22:32:03,274]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 213 count 185
[2026-06-03 22:32:03,277]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kuma@onetigris.cn
[2026-06-03 22:32:03,278]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:32:12,793]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kuma@onetigris.cn
[2026-06-03 22:33:19,801]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市晟睿昌科技有限公司,Shenzhen Sheng Ruichang Technology Co., Ltd,shengruichang999@163.com
[2026-06-03 22:33:19,801]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 215 count 186
[2026-06-03 22:33:19,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail shengruichang999@163.com
[2026-06-03 22:33:19,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:33:29,883]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,shengruichang999@163.com
[2026-06-03 22:34:36,888]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,徐州勾藤电子科技有限公司,Xuzhou Goton Technology Co.,Ltd,gorden@byecold.cn
[2026-06-03 22:34:36,888]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 216 count 187
[2026-06-03 22:34:36,893]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail gorden@byecold.cn
[2026-06-03 22:34:36,894]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:34:46,697]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,gorden@byecold.cn
[2026-06-03 22:35:53,699]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市威仕茂科技有限公司,shenzhenshiweishimaokejiyouxiangongsi,tsq5211@163.com
[2026-06-03 22:35:53,699]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 217 count 188
[2026-06-03 22:35:53,702]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tsq5211@163.com
[2026-06-03 22:35:53,703]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:36:03,832]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tsq5211@163.com
[2026-06-03 22:37:13,833]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁县鸣恳贸易有限公司,xinningxianmingkenmaoyiyouxiangongsi,xnmingken@163.com
[2026-06-03 22:37:13,833]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 218 count 189
[2026-06-03 22:37:13,836]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xnmingken@163.com
[2026-06-03 22:37:13,836]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:37:24,144]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xnmingken@163.com
[2026-06-03 22:38:31,148]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳阿麻纵贸易有限公司,shaoyangamazongmaoyiyouxiangongsi,shaoyangamz@163.com
[2026-06-03 22:38:31,148]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 220 count 190
[2026-06-03 22:38:31,152]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail shaoyangamz@163.com
[2026-06-03 22:38:31,152]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:38:40,448]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,shaoyangamz@163.com
[2026-06-03 22:39:47,463]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州市富威科技有限公司,guangzhoushifuweikejiyouxiangongsi,admin@inner-king.com
[2026-06-03 22:39:47,463]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 221 count 191
[2026-06-03 22:39:47,466]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail admin@inner-king.com
[2026-06-03 22:39:47,466]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:39:57,895]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,admin@inner-king.com
[2026-06-03 22:41:03,895]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市里诚电子商务有限公司,Shenzhen Licheng E-Commerce Co., Ltd.,licheng156332@hotmail.com
[2026-06-03 22:41:03,895]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 222 count 192
[2026-06-03 22:41:03,900]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail licheng156332@hotmail.com
[2026-06-03 22:41:03,900]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:41:13,891]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,licheng156332@hotmail.com
[2026-06-03 22:42:22,906]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市路盈科技有限公司,Shenzhen Luying Technology Co.,ltd,luyinginc@hotmail.com
[2026-06-03 22:42:22,906]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 223 count 193
[2026-06-03 22:42:22,911]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail luyinginc@hotmail.com
[2026-06-03 22:42:22,911]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:42:32,615]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,luyinginc@hotmail.com
[2026-06-03 22:43:37,630]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳花花贝贝体育文化有限公司,shen zhen hua hua bei bei ti yu wen hua you xian gong si　,huahuabeibei2023@outlook.com
[2026-06-03 22:43:37,631]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 224 count 194
[2026-06-03 22:43:37,635]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail huahuabeibei2023@outlook.com
[2026-06-03 22:43:37,635]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:43:46,861]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,huahuabeibei2023@outlook.com
[2026-06-03 22:44:51,873]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,沈阳易珠锴商贸有限公司,Shenyang Yizhukai Trading Co., Ltd.,LopzeRoch14@hotmail.com
[2026-06-03 22:44:51,873]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 226 count 195
[2026-06-03 22:44:51,877]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail LopzeRoch14@hotmail.com
[2026-06-03 22:44:51,877]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:45:01,730]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,LopzeRoch14@hotmail.com
[2026-06-03 22:46:09,732]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,HONGKONG INLINK INDUSTRIAL CO., LIMITED,HONGKONG INLINK INDUSTRIAL CO., LIMITED,amazon@hkinlink.com
[2026-06-03 22:46:09,732]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 228 count 196
[2026-06-03 22:46:09,735]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail amazon@hkinlink.com
[2026-06-03 22:46:09,735]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:46:20,029]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,amazon@hkinlink.com
[2026-06-03 22:47:24,036]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,柒秒宇宙（杭州）电子商务有限公司,Qimiao Yuzhou (Hangzhou) E-commerce Co., Ltd.,qimiaoyuzhou@163.com
[2026-06-03 22:47:24,036]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 229 count 197
[2026-06-03 22:47:24,039]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail qimiaoyuzhou@163.com
[2026-06-03 22:47:24,040]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:47:33,733]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,qimiaoyuzhou@163.com
[2026-06-03 22:48:34,742]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州然蓦商贸有限公司‌,Guangzhou Ranmo Trading Co., Ltd.,MyottAlvino41@hotmail.com
[2026-06-03 22:48:34,742]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 230 count 198
[2026-06-03 22:48:34,744]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail MyottAlvino41@hotmail.com
[2026-06-03 22:48:34,744]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:48:44,124]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,MyottAlvino41@hotmail.com
[2026-06-03 22:49:49,132]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳德谱勒科技有限公司,ShenZhen DePuLe KeJi YouXianGongSi,depulerjp@qq.com
[2026-06-03 22:49:49,132]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 352 count 199
[2026-06-03 22:49:49,135]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail depulerjp@qq.com
[2026-06-03 22:49:49,136]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:49:59,005]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,depulerjp@qq.com
[2026-06-03 22:51:06,006]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,福州市晋安区丹柯商贸有限公司,fuzhoushijinanqudankeshangmaoyouxiangongsi,dankeshm@outlook.com
[2026-06-03 22:51:06,006]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 235 count 200
[2026-06-03 22:51:06,010]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail dankeshm@outlook.com
[2026-06-03 22:51:06,010]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:51:16,287]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,dankeshm@outlook.com
[2026-06-03 22:52:21,289]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市同创创精密电子有限公司,ShenZhenTongChuangChuangJingMiElectronics Co., Ltd,handfan.jp@outlook.com
[2026-06-03 22:52:21,289]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 400 count 201
[2026-06-03 22:52:21,292]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail handfan.jp@outlook.com
[2026-06-03 22:52:21,292]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:52:31,402]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,handfan.jp@outlook.com
[2026-06-03 22:53:39,411]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市明昕电子科技公司,dongguanmingxindianzikejiyouxiangongsi,kyz506@163.com
[2026-06-03 22:53:39,411]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 237 count 202
[2026-06-03 22:53:39,414]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail kyz506@163.com
[2026-06-03 22:53:39,414]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:53:48,732]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,kyz506@163.com
[2026-06-03 22:54:50,746]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,LINKTAP PTY LTD,LINKTAP PTY LTD,enquiry@link-tap.com
[2026-06-03 22:54:50,746]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 238 count 203
[2026-06-03 22:54:50,750]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail enquiry@link-tap.com
[2026-06-03 22:54:50,750]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:55:00,284]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,enquiry@link-tap.com
[2026-06-03 22:56:01,287]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳赛凯特科技有限公司,Shenzhen Saikaite Technology Co., Ltd.,houjinfeng@dxf-power.com
[2026-06-03 22:56:01,287]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 329 count 204
[2026-06-03 22:56:01,291]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail houjinfeng@dxf-power.com
[2026-06-03 22:56:01,291]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:56:10,715]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,houjinfeng@dxf-power.com
[2026-06-03 22:57:12,721]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市澳一墨科技有限公司,shenzhen AEM technology co., Ltd,776337910@qq.com
[2026-06-03 22:57:12,721]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 241 count 205
[2026-06-03 22:57:12,725]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 776337910@qq.com
[2026-06-03 22:57:12,725]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:57:23,013]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,776337910@qq.com
[2026-06-03 22:58:33,018]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市思创力维科技有限公司,Shenzhen Shi Sichuang Liwei Keji Co., LTD,xiaopeiyueliz@jpvat.com
[2026-06-03 22:58:33,018]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 296 count 206
[2026-06-03 22:58:33,021]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xiaopeiyueliz@jpvat.com
[2026-06-03 22:58:33,021]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:58:43,338]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xiaopeiyueliz@jpvat.com
[2026-06-03 22:59:45,348]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,厦门潜力无限科技有限公司,Xiamen Qianliwuxian Technology Co., Ltd,jackhuang@poteninfy.com
[2026-06-03 22:59:45,348]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 243 count 207
[2026-06-03 22:59:45,353]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jackhuang@poteninfy.com
[2026-06-03 22:59:45,353]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 22:59:55,323]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jackhuang@poteninfy.com
[2026-06-03 23:01:00,336]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市佳纳科技有限公司,Shenzhen Jiana Technology Co., Ltd,jianajapan@foxmail.com
[2026-06-03 23:01:00,336]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 244 count 208
[2026-06-03 23:01:00,340]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jianajapan@foxmail.com
[2026-06-03 23:01:00,340]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:01:10,369]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jianajapan@foxmail.com
[2026-06-03 23:02:17,383]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市草舍贸易有限公司,Shenzhen Shi Caoshe Maoyi Youxian Gongsi,wumei@jpvat.com
[2026-06-03 23:02:17,383]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 298 count 209
[2026-06-03 23:02:17,387]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wumei@jpvat.com
[2026-06-03 23:02:17,387]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:02:28,129]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wumei@jpvat.com
[2026-06-03 23:03:33,130]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳创盈芯实业有限公司　,Shenzhen CYX Industrial Co., Ltd.,cpeb@ctonetech.com
[2026-06-03 23:03:33,130]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 246 count 210
[2026-06-03 23:03:33,134]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cpeb@ctonetech.com
[2026-06-03 23:03:33,134]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:03:42,524]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cpeb@ctonetech.com
[2026-06-03 23:04:52,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,常州市肯迪电器制造有限公司,ChangzhoushiKendiDianqiZhizaoyouxiangongsi,cheng-jz@cantypowertools.com
[2026-06-03 23:04:52,538]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 247 count 211
[2026-06-03 23:04:52,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cheng-jz@cantypowertools.com
[2026-06-03 23:04:52,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:05:03,124]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cheng-jz@cantypowertools.com
[2026-06-03 23:06:09,129]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市森佰科技有限公司,Shenzhen Senbai Technology Co., Ltd.,senbai_keji@126.com
[2026-06-03 23:06:09,129]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 250 count 212
[2026-06-03 23:06:09,133]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail senbai_keji@126.com
[2026-06-03 23:06:09,133]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:06:18,538]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,senbai_keji@126.com
[2026-06-03 23:07:21,554]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,冷水江市存卦贸易有限公司,lengshuijiangshicunguamaoyiyouxiangongsi,cungua9XX@outlook.com
[2026-06-03 23:07:21,554]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 251 count 213
[2026-06-03 23:07:21,558]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cungua9XX@outlook.com
[2026-06-03 23:07:21,558]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:07:31,172]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cungua9XX@outlook.com
[2026-06-03 23:08:35,177]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,佛山市艾凯控股集团有限公司,Foshan Alpicool Holding Group Co, Ltd,jacky@alpicool.com
[2026-06-03 23:08:35,177]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 259 count 214
[2026-06-03 23:08:35,180]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jacky@alpicool.com
[2026-06-03 23:08:35,180]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:08:44,133]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jacky@alpicool.com
[2026-06-03 23:09:46,147]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市鹊琨商贸有限公司,shaoyangshiquekunshangmaoyouxiangongsi,vuneyes@163.com
[2026-06-03 23:09:46,147]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 254 count 215
[2026-06-03 23:09:46,150]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail vuneyes@163.com
[2026-06-03 23:09:46,151]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:09:56,532]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,vuneyes@163.com
[2026-06-03 23:10:59,537]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,谷城县求组商贸有限公司,guchengxianqiuzushangmaoyouxiangongsi,qiuzusm@163.com
[2026-06-03 23:10:59,537]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 255 count 216
[2026-06-03 23:10:59,541]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail qiuzusm@163.com
[2026-06-03 23:10:59,541]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:11:09,574]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,qiuzusm@163.com
[2026-06-03 23:12:10,576]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,彩迅工业（中山）有限公司,EXPRESS LUCK INDUSTRIAL (ZHONGSHAN) LIMITED,salesam01@expressluck.com
[2026-06-03 23:12:10,576]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 256 count 217
[2026-06-03 23:12:10,580]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail salesam01@expressluck.com
[2026-06-03 23:12:10,580]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:12:19,650]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,salesam01@expressluck.com
[2026-06-03 23:13:27,660]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,佛山市缤途科技有限公司,Foshanshibintukejiyouxiangongsi,jacky@alpicool.com
[2026-06-03 23:13:27,660]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 258 count 218
[2026-06-03 23:13:27,664]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jacky@alpicool.com
[2026-06-03 23:13:27,664]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:13:38,207]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jacky@alpicool.com
[2026-06-03 23:14:45,212]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市梅平贸易有限公司,shaoyangshimeipingshangmaoyouxiangongsi,wangcai16889@163.com
[2026-06-03 23:14:45,212]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 265 count 219
[2026-06-03 23:14:45,216]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wangcai16889@163.com
[2026-06-03 23:14:45,216]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:14:57,780]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wangcai16889@163.com
[2026-06-03 23:15:58,787]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南京龙赢家居科技有限公司,Nanjing Longying Home Technology Co., Ltd.,jason@ecbarley.com
[2026-06-03 23:15:58,787]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 262 count 220
[2026-06-03 23:15:58,790]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jason@ecbarley.com
[2026-06-03 23:15:58,790]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:16:08,450]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jason@ecbarley.com
[2026-06-03 23:17:15,452]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,佛山市文信五金有限公司,Foshanwenxinwujinyouxiangongsi,wenxinamazon@163.com
[2026-06-03 23:17:15,452]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 263 count 221
[2026-06-03 23:17:15,456]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wenxinamazon@163.com
[2026-06-03 23:17:15,456]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:17:25,091]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wenxinamazon@163.com
[2026-06-03 23:18:33,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市迈四方科技有限公司,Shenzhen Maisifang Technology Co., Ltd.,hyhphk@163.com
[2026-06-03 23:18:33,101]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 264 count 222
[2026-06-03 23:18:33,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hyhphk@163.com
[2026-06-03 23:18:33,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:18:44,353]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hyhphk@163.com
[2026-06-03 23:19:46,358]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,陶瓷照明灯饰有限公司,CERAMIC LED LIGHTING LIMITED,sansi.jp1@sansiled.com
[2026-06-03 23:19:46,358]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 267 count 223
[2026-06-03 23:19:46,362]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail sansi.jp1@sansiled.com
[2026-06-03 23:19:46,362]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:19:55,815]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,sansi.jp1@sansiled.com
[2026-06-03 23:20:59,818]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,周口往冰商贸有限公司,ZhouKouWangBingShangMaoYouXianGongSi,GSIUbdug@outlook.com
[2026-06-03 23:20:59,818]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 271 count 224
[2026-06-03 23:20:59,821]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail GSIUbdug@outlook.com
[2026-06-03 23:20:59,821]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:21:09,413]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,GSIUbdug@outlook.com
[2026-06-03 23:22:16,413]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南京博拉尔帝斯商贸有限公司,nanjingbolaerdisishangmaoyouxiangongsi,seller-us@draupnir3d.com
[2026-06-03 23:22:16,413]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 269 count 225
[2026-06-03 23:22:16,416]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail seller-us@draupnir3d.com
[2026-06-03 23:22:16,416]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:22:26,219]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,seller-us@draupnir3d.com
[2026-06-03 23:23:33,222]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市叶欧电子科技有限公司,shen zhen shi ye ou dian zi ke ji you xian gong si,wangqianqiancherry1588815@jpvat.com
[2026-06-03 23:23:33,222]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 270 count 226
[2026-06-03 23:23:33,226]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail wangqianqiancherry1588815@jpvat.com
[2026-06-03 23:23:33,227]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:23:43,012]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,wangqianqiancherry1588815@jpvat.com
[2026-06-03 23:24:44,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,漯河跃眠贸易有限公司,luoheyuemianmaoyiyouxiangongsi,LHYUEMIAN@163.com
[2026-06-03 23:24:44,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 272 count 227
[2026-06-03 23:24:44,024]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail LHYUEMIAN@163.com
[2026-06-03 23:24:44,025]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:24:54,691]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,LHYUEMIAN@163.com
[2026-06-03 23:26:03,696]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,临沂乐鲁花卉有限公司,LinYiLeLuHuaHuiYouXianGongSi,HanYongamz1@outlook.com
[2026-06-03 23:26:03,696]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 364 count 228
[2026-06-03 23:26:03,699]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HanYongamz1@outlook.com
[2026-06-03 23:26:03,699]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:26:13,087]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HanYongamz1@outlook.com
[2026-06-03 23:27:20,102]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙市银坐科技发展有限公司,changshashiyinzuokejifazhanyouxiangongsi,cs_hexian@163.com
[2026-06-03 23:27:20,102]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 276 count 229
[2026-06-03 23:27:20,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cs_hexian@163.com
[2026-06-03 23:27:20,105]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:27:29,742]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cs_hexian@163.com
[2026-06-03 23:28:37,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,连云港喜木农副产品有限公司,lianyungangximunongfuchanpinyouxiangongsi,bixi6751819@163.com
[2026-06-03 23:28:37,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 277 count 230
[2026-06-03 23:28:37,762]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail bixi6751819@163.com
[2026-06-03 23:28:37,763]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:28:47,802]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,bixi6751819@163.com
[2026-06-03 23:29:55,811]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,枝江市宅朋科技有限公司,zhi jiang shi zhai peng ke ji you xian gong si,zjzm16899@163.com
[2026-06-03 23:29:55,811]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 282 count 231
[2026-06-03 23:29:55,814]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zjzm16899@163.com
[2026-06-03 23:29:55,814]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:30:05,307]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zjzm16899@163.com
[2026-06-03 23:31:14,319]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,天創微米科技香港有限公司,Tianchuang Micron Technology Hong Kong Limited,mc418418@163.com
[2026-06-03 23:31:14,319]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 417 count 232
[2026-06-03 23:31:14,322]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail mc418418@163.com
[2026-06-03 23:31:14,322]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:31:23,959]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,mc418418@163.com
[2026-06-03 23:32:29,974]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市凯宏达科技有限公司,SHENZHEN KHD TECHNOLOGY CO., LTD.,khd-jp-rakuten@vecelo.com
[2026-06-03 23:32:29,974]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 281 count 233
[2026-06-03 23:32:29,977]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail khd-jp-rakuten@vecelo.com
[2026-06-03 23:32:29,977]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:32:40,608]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,khd-jp-rakuten@vecelo.com
[2026-06-03 23:33:49,621]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,宁波艾思电子商务有限公司,NINGBO AISI E-commerce Co.,Ltd,admin01@aglucky.com
[2026-06-03 23:33:49,621]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 284 count 234
[2026-06-03 23:33:49,625]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail admin01@aglucky.com
[2026-06-03 23:33:49,626]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:34:00,446]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,admin01@aglucky.com
[2026-06-03 23:35:07,459]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,厦门市洱诗流贸易有限公司,XIAMENSHI ERSHILIU MAOYI YOUXIANGONGSI,WCP26D@163.com
[2026-06-03 23:35:07,459]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 286 count 235
[2026-06-03 23:35:07,465]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail WCP26D@163.com
[2026-06-03 23:35:07,466]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:35:17,310]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,WCP26D@163.com
[2026-06-03 23:36:23,318]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市竹聪电子科技有限公司,Shenzhen Zhucong Electronic Technology Co., Ltd.,1393548660@QQ.com
[2026-06-03 23:36:23,318]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 287 count 236
[2026-06-03 23:36:23,322]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1393548660@QQ.com
[2026-06-03 23:36:23,322]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:36:32,779]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1393548660@QQ.com
[2026-06-03 23:37:42,783]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南宁东邮文化传播有限公司,NANNINGDONGYOUWENHUACHUANBOYOUXIANGONGSI,upupday20181215@163.com
[2026-06-03 23:37:42,783]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 288 count 237
[2026-06-03 23:37:42,787]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail upupday20181215@163.com
[2026-06-03 23:37:42,787]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:37:52,132]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,upupday20181215@163.com
[2026-06-03 23:39:01,143]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳竞煌光电科技有限公司,Shenzhen Jinghuang Optoelectronics Technology Co., Ltd.,787516181@qq.com
[2026-06-03 23:39:01,143]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 289 count 238
[2026-06-03 23:39:01,148]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 787516181@qq.com
[2026-06-03 23:39:01,148]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:39:10,856]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,787516181@qq.com
[2026-06-03 23:40:14,860]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,梅州市凯茂信息技术有限公司,meizhoushikaimaoxinxijishuyouxiangongsi,1508239@qq.com
[2026-06-03 23:40:14,860]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 290 count 239
[2026-06-03 23:40:14,865]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 1508239@qq.com
[2026-06-03 23:40:14,865]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:40:24,766]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,1508239@qq.com
[2026-06-03 23:41:28,769]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,安徽恩纪智能科技有限公司,Anhui Enji Intelligent Technology Co., Ltd,652663982@qq.com
[2026-06-03 23:41:28,769]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 291 count 240
[2026-06-03 23:41:28,772]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 652663982@qq.com
[2026-06-03 23:41:28,772]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:41:38,377]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,652663982@qq.com
[2026-06-03 23:42:39,382]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市金锐纵横科技有限公司,Shenzhen Jinrui Zongheng Technology Co., Ltd.,jrzt2025@163.com
[2026-06-03 23:42:39,382]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 293 count 241
[2026-06-03 23:42:39,385]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jrzt2025@163.com
[2026-06-03 23:42:39,386]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:42:49,310]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jrzt2025@163.com
[2026-06-03 23:43:58,323]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港上游科技创新有限公司,HONG KONG UPSTREAM INNOVATION TECHNOLOGY LIMITED,dingdipei@aukeys.com
[2026-06-03 23:43:58,323]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 295 count 242
[2026-06-03 23:43:58,328]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail dingdipei@aukeys.com
[2026-06-03 23:43:58,328]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:44:07,971]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,dingdipei@aukeys.com
[2026-06-03 23:45:14,976]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,惠州市果旺商贸有限公司,huizhoushiguowangshangmaoyouxiangongsi,GuowangTrading@163.com
[2026-06-03 23:45:14,976]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 297 count 243
[2026-06-03 23:45:14,979]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail GuowangTrading@163.com
[2026-06-03 23:45:14,979]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:45:25,409]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,GuowangTrading@163.com
[2026-06-03 23:46:28,413]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市一乐大叔贸易有限公司,Shenzhen Yile Uncle Trading Co., Ltd.,QiLiu@jpipr.com
[2026-06-03 23:46:28,413]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 300 count 244
[2026-06-03 23:46:28,416]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail QiLiu@jpipr.com
[2026-06-03 23:46:28,416]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:46:38,051]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,QiLiu@jpipr.com
[2026-06-03 23:47:42,059]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州合旗商业发展有限公司,Heqi Business Development (Guangzhou) Co., Ltd.,velarJP@outlook.com
[2026-06-03 23:47:42,059]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 434 count 245
[2026-06-03 23:47:42,063]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail velarJP@outlook.com
[2026-06-03 23:47:42,063]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:47:51,328]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,velarJP@outlook.com
[2026-06-03 23:48:57,335]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港卓朗貿易有限公司,Hong Kong Zhuo Lang Trading Co., Limited,drentech301@163.com
[2026-06-03 23:48:57,335]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 303 count 246
[2026-06-03 23:48:57,338]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail drentech301@163.com
[2026-06-03 23:48:57,338]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:49:06,539]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,drentech301@163.com
[2026-06-03 23:50:09,548]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,常德芮阳电子商务有限公司,changderuiyangdianzishangwuyouxiangongsi,cdruiyang2025@163.com
[2026-06-03 23:50:09,548]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 304 count 247
[2026-06-03 23:50:09,552]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cdruiyang2025@163.com
[2026-06-03 23:50:09,552]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:50:18,846]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cdruiyang2025@163.com
[2026-06-03 23:51:25,851]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,烟台隆晓电子商务有限公司,yantailongxiaodianzishangwuyouxiangongsi,Liuyongzhuojp@outlook.com
[2026-06-03 23:51:25,851]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 309 count 248
[2026-06-03 23:51:25,855]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Liuyongzhuojp@outlook.com
[2026-06-03 23:51:25,855]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:51:36,297]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Liuyongzhuojp@outlook.com
[2026-06-03 23:52:46,302]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,潮州市赛卢钠科技有限公司,chaozhoushisailunakejiyouxiangongsi,celunar402@outlook.com
[2026-06-03 23:52:46,302]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 306 count 249
[2026-06-03 23:52:46,307]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail celunar402@outlook.com
[2026-06-03 23:52:46,307]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:52:55,650]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,celunar402@outlook.com
[2026-06-03 23:53:57,652]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州乐桃科技有限公司,Guangzhou Letao Keji Youxiangongsi,Letokids2022@outlook.com
[2026-06-03 23:53:57,652]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 307 count 250
[2026-06-03 23:53:57,655]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Letokids2022@outlook.com
[2026-06-03 23:53:57,655]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:54:07,011]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Letokids2022@outlook.com
[2026-06-03 23:55:17,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市光之晨商贸有限公司,shenzhenshiguangzhichenshangmaoyouxiangongsi,gzc-amzjp@hotmail.com
[2026-06-03 23:55:17,020]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 308 count 251
[2026-06-03 23:55:17,023]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail gzc-amzjp@hotmail.com
[2026-06-03 23:55:17,023]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:55:26,395]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,gzc-amzjp@hotmail.com
[2026-06-03 23:56:29,398]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,宁波乐活户外用品有限公司,Ningbo Lohas Outdoor Products Co., Ltd.,HongLiu@jpipr.com
[2026-06-03 23:56:29,398]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 439 count 252
[2026-06-03 23:56:29,401]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HongLiu@jpipr.com
[2026-06-03 23:56:29,401]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:56:39,970]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HongLiu@jpipr.com
[2026-06-03 23:57:47,974]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,河津磊祥养殖有限公司,hejinleixiangyangzhiyouxiangongsi,dedumusong61082@163.com
[2026-06-03 23:57:47,974]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 311 count 253
[2026-06-03 23:57:47,977]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail dedumusong61082@163.com
[2026-06-03 23:57:47,978]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:57:57,411]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,dedumusong61082@163.com
[2026-06-03 23:59:00,421]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,佛山市椿吉如电子商务有限公司,foshanshichunjirudianzishangwuyouxiangongsi,guxingqiao88509645@163.com
[2026-06-03 23:59:00,421]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 312 count 254
[2026-06-03 23:59:00,424]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail guxingqiao88509645@163.com
[2026-06-03 23:59:00,424]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-03 23:59:09,733]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,guxingqiao88509645@163.com
[2026-06-04 00:00:15,733]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,山西星辰规划信息技术有限公司,shanxixingchenguihuaxinxijishuyouxiangongsi,e7f2g9h5k03@outlook.com
[2026-06-04 00:00:15,733]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 313 count 255
[2026-06-04 00:00:15,736]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail e7f2g9h5k03@outlook.com
[2026-06-04 00:00:15,736]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:00:24,931]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,e7f2g9h5k03@outlook.com
[2026-06-04 00:01:29,937]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州恒迈电子商务有限公司,Wenzhou Hengmai E-commerce Co., Ltd.,hengmaidzsw@outlook.com
[2026-06-04 00:01:29,937]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 314 count 256
[2026-06-04 00:01:29,940]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hengmaidzsw@outlook.com
[2026-06-04 00:01:29,940]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:01:40,138]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hengmaidzsw@outlook.com
[2026-06-04 00:02:44,149]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,瑞安市聚友机械配件有限公司,ruianshijuyoujixiepeijianyouxiangongsi,leweibk306754@163.com
[2026-06-04 00:02:44,149]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 315 count 257
[2026-06-04 00:02:44,152]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail leweibk306754@163.com
[2026-06-04 00:02:44,152]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:02:53,411]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,leweibk306754@163.com
[2026-06-04 00:04:03,420]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市星火星电子商务有限公司,shenzhenshixinghuoxingdianzishangwuyouxiangongsi,574796162@qq.com
[2026-06-04 00:04:03,420]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 316 count 258
[2026-06-04 00:04:03,423]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 574796162@qq.com
[2026-06-04 00:04:03,423]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:04:13,101]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,574796162@qq.com
[2026-06-04 00:05:19,114]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,重庆美天康年科技发展有限公司,Chongqing Meitiankangnian Tech Development Ltd,15826120951@163.com
[2026-06-04 00:05:19,114]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 317 count 259
[2026-06-04 00:05:19,119]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 15826120951@163.com
[2026-06-04 00:05:19,119]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:05:28,609]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,15826120951@163.com
[2026-06-04 00:06:29,617]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞生洋营销策划有限公司,Shengyang Marketing Consulting Co., Ltd,751796247@qq.com
[2026-06-04 00:06:29,617]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 318 count 260
[2026-06-04 00:06:29,620]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 751796247@qq.com
[2026-06-04 00:06:29,620]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:06:39,332]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,751796247@qq.com
[2026-06-04 00:07:45,332]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,鑫龙充气模型制品有限公司,Xin Long Chong Qi Mo Xing Zhi Pin You Xian Gong Si,tangqiuz6422@163.com
[2026-06-04 00:07:45,332]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 319 count 261
[2026-06-04 00:07:45,337]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tangqiuz6422@163.com
[2026-06-04 00:07:45,337]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:07:55,333]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tangqiuz6422@163.com
[2026-06-04 00:09:04,345]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市声学科技有限公司,shenzhenshishengxuekejiyouxiangongsi,shengxueamazon@163.com
[2026-06-04 00:09:04,345]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 320 count 262
[2026-06-04 00:09:04,348]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail shengxueamazon@163.com
[2026-06-04 00:09:04,348]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:09:13,726]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,shengxueamazon@163.com
[2026-06-04 00:10:16,728]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳星河无限科技有限公司,ShenzhenXinghewuxiankejiyouxiangongsi,xhwxamz@outlook.com
[2026-06-04 00:10:16,728]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 322 count 263
[2026-06-04 00:10:16,733]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xhwxamz@outlook.com
[2026-06-04 00:10:16,733]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:10:26,366]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xhwxamz@outlook.com
[2026-06-04 00:11:31,381]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广东新钱潮信息科技有限公司,Guangdong Xin Qian Chao Information Technology Co., Ltd.,Johnny@xinqianchao.com
[2026-06-04 00:11:31,381]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 412 count 264
[2026-06-04 00:11:31,386]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Johnny@xinqianchao.com
[2026-06-04 00:11:31,386]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:11:40,771]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Johnny@xinqianchao.com
[2026-06-04 00:12:50,777]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,淮北市优鲜商贸有限公司,huai bei shi you xian shang mao you xian gong si,hbyxna2@163.com
[2026-06-04 00:12:50,777]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 325 count 265
[2026-06-04 00:12:50,781]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hbyxna2@163.com
[2026-06-04 00:12:50,781]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:13:00,771]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hbyxna2@163.com
[2026-06-04 00:14:09,775]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州喀阁科技有限公司,guangzhoukagekejiyouxiangongsi,GZgegekeji654@163.com
[2026-06-04 00:14:09,775]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 373 count 266
[2026-06-04 00:14:09,780]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail GZgegekeji654@163.com
[2026-06-04 00:14:09,780]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:14:19,490]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,GZgegekeji654@163.com
[2026-06-04 00:15:20,491]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,揭阳市铭通科技有限公司,Jieyang Mingtong Technology Co., Ltd.,5577820@qq.com
[2026-06-04 00:15:20,491]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 327 count 267
[2026-06-04 00:15:20,494]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 5577820@qq.com
[2026-06-04 00:15:20,495]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:15:30,207]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,5577820@qq.com
[2026-06-04 00:16:34,207]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,途途國際有限公司,TuTu International Co., Limited,tutu@tutuelec.com
[2026-06-04 00:16:34,207]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 328 count 268
[2026-06-04 00:16:34,210]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tutu@tutuelec.com
[2026-06-04 00:16:34,210]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:16:43,489]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tutu@tutuelec.com
[2026-06-04 00:17:44,502]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,海口六伍八科技有限公司,haikouliuwubakejiyouxiangongsi,six-five-eight@outlook.com
[2026-06-04 00:17:44,502]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 330 count 269
[2026-06-04 00:17:44,506]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail six-five-eight@outlook.com
[2026-06-04 00:17:44,506]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:17:53,887]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,six-five-eight@outlook.com
[2026-06-04 00:19:01,887]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙市岑诺科技有限公司,changshashicennuokejiyouxiangongsi,275378975@qq.com
[2026-06-04 00:19:01,887]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 331 count 270
[2026-06-04 00:19:01,890]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 275378975@qq.com
[2026-06-04 00:19:01,890]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:19:11,334]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,275378975@qq.com
[2026-06-04 00:20:21,335]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市酷而美创新科技有限公司,Ku Er Mei Chuang Xin Ke Ji You Xian Gong Si,tony.xia@coolmetech.com
[2026-06-04 00:20:21,335]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 332 count 271
[2026-06-04 00:20:21,338]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail tony.xia@coolmetech.com
[2026-06-04 00:20:21,338]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:20:31,035]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,tony.xia@coolmetech.com
[2026-06-04 00:21:40,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞汇臻科技有限公司,dongguanhuizhenkejiyouxiangongsi,dongguanhuizhen2020@hotmail.com
[2026-06-04 00:21:40,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 334 count 272
[2026-06-04 00:21:40,043]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail dongguanhuizhen2020@hotmail.com
[2026-06-04 00:21:40,043]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:21:51,558]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,dongguanhuizhen2020@hotmail.com
[2026-06-04 00:22:57,570]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市澜晖贸易有限公司,Shenzhen Lanhui Trading Co., Ltd,LeileiZhang@jpipr.com
[2026-06-04 00:22:57,570]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 346 count 273
[2026-06-04 00:22:57,574]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail LeileiZhang@jpipr.com
[2026-06-04 00:22:57,574]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:23:06,680]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,LeileiZhang@jpipr.com
[2026-06-04 00:24:13,680]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,郑州市二七区祥发包装材料有限公司,ZHENGZHOUSHIERQIQUXIANGFABAOZHUANGCAILIAOYOUXIANGONGSI,pa4864@163.com
[2026-06-04 00:24:13,680]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 337 count 274
[2026-06-04 00:24:13,684]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail pa4864@163.com
[2026-06-04 00:24:13,684]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:24:23,802]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,pa4864@163.com
[2026-06-04 00:25:32,815]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,ICE-ELEC TECHNOLOGY CO., LIMITED,ICE-ELEC TECHNOLOGY CO., LIMITED,brianhe@iceelec.cn
[2026-06-04 00:25:32,815]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 338 count 275
[2026-06-04 00:25:32,820]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail brianhe@iceelec.cn
[2026-06-04 00:25:32,820]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:25:42,860]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,brianhe@iceelec.cn
[2026-06-04 00:26:43,869]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳尤兵电子商务有限公司,shaoyangyoubingdianzishangwuyouxiangongsi,Anserky@163.com
[2026-06-04 00:26:43,869]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 339 count 276
[2026-06-04 00:26:43,872]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Anserky@163.com
[2026-06-04 00:26:43,872]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:26:54,211]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Anserky@163.com
[2026-06-04 00:28:01,219]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,滨州市阳信县好好二手车销售有限责任公司,Binzhou Yangxin Haohao Used Car Sales Co., Ltd.,WangLiYongamz@outlook.com
[2026-06-04 00:28:01,219]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 341 count 277
[2026-06-04 00:28:01,223]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail WangLiYongamz@outlook.com
[2026-06-04 00:28:01,224]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 00:28:10,690]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,WangLiYongamz@outlook.com

[2026-06-04 09:07:07,766]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-04 09:07:12,627]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-04 09:07:12,629]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,民权县壹点设计装饰工程有限公司,Yidian Design Decoration Engineering Co., Ltd.,Guozhenxing7258@hotmail.com
[2026-06-04 09:07:12,629]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 342 count 278
[2026-06-04 09:07:41,588]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Guozhenxing7258@hotmail.com
[2026-06-04 09:07:41,588]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:07:51,184]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Guozhenxing7258@hotmail.com
[2026-06-04 09:08:53,187]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,曲沃县张元商贸有限责任公司,Quwo County Zhangyuan Trade Co., Ltd.,ZhangYuanYuan2957@outlook.com
[2026-06-04 09:08:53,188]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 344 count 279
[2026-06-04 09:08:53,192]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ZhangYuanYuan2957@outlook.com
[2026-06-04 09:08:53,192]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:09:00,239]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ZhangYuanYuan2957@outlook.com
[2026-06-04 09:10:04,247]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港创想三维投资有限公司,HONGKONG CREALITY 3D INVESTMENT CO.,LIMITED,crealityecosystem@outlook.com
[2026-06-04 09:10:04,248]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 345 count 280
[2026-06-04 09:10:04,253]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail crealityecosystem@outlook.com
[2026-06-04 09:10:04,254]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:10:11,205]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,crealityecosystem@outlook.com
[2026-06-04 09:11:15,210]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,佛山市明盛泰品牌运营有限公司,Foshan Mingshengtai Brand Operation Co., Ltd.,HeCongJunam6z@outlook.com
[2026-06-04 09:11:15,210]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 347 count 281
[2026-06-04 09:11:15,214]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HeCongJunam6z@outlook.com
[2026-06-04 09:11:15,214]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:11:22,387]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HeCongJunam6z@outlook.com
[2026-06-04 09:12:24,397]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,济源市诚助财税管理咨询有限公司,jiyuanshichengzhucaishuiguanlizixunyouxiangongsi,ttb89m@outlook.com
[2026-06-04 09:12:24,397]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 348 count 282
[2026-06-04 09:12:24,404]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ttb89m@outlook.com
[2026-06-04 09:12:24,404]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:12:31,381]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ttb89m@outlook.com
[2026-06-04 09:13:37,384]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,蓝山县潜凡白商贸有限公司,LANSHANXIANQIANFANBAISHANGMAOYOUXIANGONGSI　,FreemyerMclamb1508@outlook.com
[2026-06-04 09:13:37,384]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 349 count 283
[2026-06-04 09:13:37,388]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail FreemyerMclamb1508@outlook.com
[2026-06-04 09:13:37,389]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:13:44,677]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,FreemyerMclamb1508@outlook.com
[2026-06-04 09:14:47,681]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,珠海恒越电子商务有限公司,ZHUHAI HENGYUE E-COMMERCE CO., LTD,hengyue202311@outlook.com
[2026-06-04 09:14:47,682]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 350 count 284
[2026-06-04 09:14:47,686]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hengyue202311@outlook.com
[2026-06-04 09:14:47,687]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:14:54,820]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hengyue202311@outlook.com
[2026-06-04 09:15:58,821]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,泉州泉佰信息科技有限公司,Quanzhou Quanbai Information Technology Co., Ltd.,liwenyi0128@163.com
[2026-06-04 09:15:58,821]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 351 count 285
[2026-06-04 09:15:58,826]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail liwenyi0128@163.com
[2026-06-04 09:15:58,826]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:16:05,721]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,liwenyi0128@163.com
[2026-06-04 09:17:12,723]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,应城市义波百货有限公司,yingchengshiyibobaihuoyouxiangongsi,pyguohai333@163.com
[2026-06-04 09:17:12,723]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 353 count 286
[2026-06-04 09:17:12,728]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail pyguohai333@163.com
[2026-06-04 09:17:12,728]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:17:19,796]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,pyguohai333@163.com
[2026-06-04 09:18:23,799]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,禹州市炎莱百货有限公司,Yuzhou Shi Yanlai Baihuo Youxian Gongsi,huannaiban9990@163.com
[2026-06-04 09:18:23,799]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 354 count 287
[2026-06-04 09:18:23,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail huannaiban9990@163.com
[2026-06-04 09:18:23,805]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:18:31,009]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,huannaiban9990@163.com
[2026-06-04 09:19:40,012]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新沂市柔栀贸易有限公司,xinyishirouzhimaoyiyouxiangongsi,zkiajsw@outlook.com
[2026-06-04 09:19:40,012]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 355 count 288
[2026-06-04 09:19:40,016]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zkiajsw@outlook.com
[2026-06-04 09:19:40,016]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:19:47,163]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zkiajsw@outlook.com
[2026-06-04 09:20:54,165]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州眸序商贸有限公司,guangzhoumouxushangmaoyouxiangongsi,TongCaiXiaamz@outlook.com
[2026-06-04 09:20:54,165]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 356 count 289
[2026-06-04 09:20:54,169]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail TongCaiXiaamz@outlook.com
[2026-06-04 09:20:54,170]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:21:01,060]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,TongCaiXiaamz@outlook.com
[2026-06-04 09:22:08,063]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新安县群鑫园林绿化工程有限公司,xinanxianqunxinyuanlinlvhuagongchengyouxiangongsi,hao133155@163.com
[2026-06-04 09:22:08,063]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 357 count 290
[2026-06-04 09:22:08,066]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail hao133155@163.com
[2026-06-04 09:22:08,067]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:22:15,884]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,hao133155@163.com
[2026-06-04 09:23:20,888]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州涔非贸易有限公司,Wenzhou Cenfei Trading Co., Ltd.,luliujia0312@163.com
[2026-06-04 09:23:20,888]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 358 count 291
[2026-06-04 09:23:20,891]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail luliujia0312@163.com
[2026-06-04 09:23:20,891]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:23:28,156]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,luliujia0312@163.com
[2026-06-04 09:24:38,159]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁露玲电子商务有限公司,xinninglulingdianzishangwuyouxiangongsi,AuroraZ23@163.com
[2026-06-04 09:24:38,159]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 359 count 292
[2026-06-04 09:24:38,163]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail AuroraZ23@163.com
[2026-06-04 09:24:38,163]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:24:45,671]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,AuroraZ23@163.com
[2026-06-04 09:25:50,671]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市香砰商贸有限公司,shao yang shi xiang peng shang mao you xian gong s,xiangpeng2509@163.com
[2026-06-04 09:25:50,671]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 360 count 293
[2026-06-04 09:25:50,674]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xiangpeng2509@163.com
[2026-06-04 09:25:50,674]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:25:58,598]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xiangpeng2509@163.com
[2026-06-04 09:27:02,599]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,保定昌瑙科技有限公司,baodingchangnaokejiyouxiangongsi,scxxtz330563@outlook.com
[2026-06-04 09:27:02,599]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 361 count 294
[2026-06-04 09:27:02,602]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail scxxtz330563@outlook.com
[2026-06-04 09:27:02,602]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:27:09,674]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,scxxtz330563@outlook.com
[2026-06-04 09:28:13,675]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,济源市康复贸易有限公司,jiyuanshikangfujiayuanmaoyiyouxiangongsi,cyylyh5@163.com
[2026-06-04 09:28:13,675]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 362 count 295
[2026-06-04 09:28:13,680]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cyylyh5@163.com
[2026-06-04 09:28:13,680]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:28:22,209]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cyylyh5@163.com
[2026-06-04 09:29:31,213]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州清炜商贸有限公司,guang zhou qing wei shang mao you xian gong si,mahang7302@163.com
[2026-06-04 09:29:31,213]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 363 count 296
[2026-06-04 09:29:31,216]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail mahang7302@163.com
[2026-06-04 09:29:31,216]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:29:40,043]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,mahang7302@163.com
[2026-06-04 09:30:46,047]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州涛腾贸易有限公司,GuangZhou TaoTeng Trading CO.,LTD,lnstudio_jp@163.com
[2026-06-04 09:30:46,047]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 365 count 297
[2026-06-04 09:30:46,051]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lnstudio_jp@163.com
[2026-06-04 09:30:46,051]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:30:53,103]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lnstudio_jp@163.com
[2026-06-04 09:32:02,103]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,浙江星程照明有限公司,zhejiang xingcheng lighting Co.,Ltd,xingchengribenzhan@163.com
[2026-06-04 09:32:02,103]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 366 count 298
[2026-06-04 09:32:02,108]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xingchengribenzhan@163.com
[2026-06-04 09:32:02,108]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:32:09,328]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xingchengribenzhan@163.com
[2026-06-04 09:33:11,328]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,吉安县智慧跨境电商有限公司,Ji'an County Smart Cross-border E-commerce Co., Ltd,zhihuikuajing2026@yeah.net
[2026-06-04 09:33:11,328]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 367 count 299
[2026-06-04 09:33:11,333]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zhihuikuajing2026@yeah.net
[2026-06-04 09:33:11,334]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:33:18,312]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zhihuikuajing2026@yeah.net
[2026-06-04 09:34:21,313]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州张耿祥商贸有限公司,guangzhouzhanggengxiangshangmaoyouxiangongsi,zxiangxiang07@163.com
[2026-06-04 09:34:21,313]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 368 count 300
[2026-06-04 09:34:21,316]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zxiangxiang07@163.com
[2026-06-04 09:34:21,317]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:34:28,289]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zxiangxiang07@163.com
[2026-06-04 09:35:33,291]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市丽萍科技有限公司,Shaoyangshilipingkejiyouxiangongsi,lipingkj@outlook.com
[2026-06-04 09:35:33,292]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 369 count 301
[2026-06-04 09:35:33,296]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lipingkj@outlook.com
[2026-06-04 09:35:33,296]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:35:40,203]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lipingkj@outlook.com
[2026-06-04 09:36:50,203]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市京歌科技有限公司,Shenzhen Gingle Technology Co.,Ltd.,ginglepro@outlook.com
[2026-06-04 09:36:50,203]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 371 count 302
[2026-06-04 09:36:50,206]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ginglepro@outlook.com
[2026-06-04 09:36:50,206]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:36:57,336]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ginglepro@outlook.com
[2026-06-04 09:38:06,340]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,苏州旋能科技有限公司,Suzhou vortexpro technology Co., Ltd,YuanRuan@jpipr.com
[2026-06-04 09:38:06,340]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 372 count 303
[2026-06-04 09:38:06,343]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail YuanRuan@jpipr.com
[2026-06-04 09:38:06,343]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:38:13,392]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,YuanRuan@jpipr.com
[2026-06-04 09:39:23,395]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,义乌市培福贸易商行,YIWUSHIPEIFUMAOYISHANGHANG,ZZKJP0208@163.com
[2026-06-04 09:39:23,395]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 374 count 304
[2026-06-04 09:39:23,398]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail ZZKJP0208@163.com
[2026-06-04 09:39:23,398]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:39:30,385]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,ZZKJP0208@163.com
[2026-06-04 09:40:39,387]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,有電儲能科技有限公司,UDPOWER TECHNOLOGY CO., LIMITED,Lam@udpwr.com
[2026-06-04 09:40:39,387]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 375 count 305
[2026-06-04 09:40:39,390]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Lam@udpwr.com
[2026-06-04 09:40:39,390]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:40:46,314]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Lam@udpwr.com
[2026-06-04 09:41:51,317]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市东华文化传播有限公司,Shenzhenshi Donghua Wenhuachuanbo Youxiangongsi,JifuZhong@jpipr.com
[2026-06-04 09:41:51,317]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 379 count 306
[2026-06-04 09:41:51,320]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail JifuZhong@jpipr.com
[2026-06-04 09:41:51,321]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:41:58,228]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,JifuZhong@jpipr.com
[2026-06-04 09:43:07,230]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市永荣服装有限公司,Shen Zhen Yong Rong Apparel Co.,LTD,rong1xun@hotmail.com
[2026-06-04 09:43:07,230]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 378 count 307
[2026-06-04 09:43:07,234]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail rong1xun@hotmail.com
[2026-06-04 09:43:07,234]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:43:17,138]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,rong1xun@hotmail.com
[2026-06-04 09:44:22,142]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,宁波韦美影视器材有限公司,NingBoWeiMeiYingShiQiCai Co. Ltd.,info@wellmaking.cn
[2026-06-04 09:44:22,142]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 380 count 308
[2026-06-04 09:44:22,146]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail info@wellmaking.cn
[2026-06-04 09:44:22,146]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:44:32,000]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,info@wellmaking.cn
[2026-06-04 09:45:35,002]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,九江日贸通电子商务有限公司,jiujiangrimaotongdianzishangwuyouxiangongsi,elemai188@163.com
[2026-06-04 09:45:35,002]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 381 count 309
[2026-06-04 09:45:35,005]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail elemai188@163.com
[2026-06-04 09:45:35,005]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:45:45,282]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,elemai188@163.com
[2026-06-04 09:46:49,282]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,PROE INC.,PROE INC.,jxejxo_jp@163.com
[2026-06-04 09:46:49,282]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 382 count 310
[2026-06-04 09:46:49,286]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jxejxo_jp@163.com
[2026-06-04 09:46:49,286]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:46:58,879]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jxejxo_jp@163.com
[2026-06-04 09:48:06,880]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港巨海科技有限公司,HongKong juhai Technology Co., Limited,HanYongamz1@outlook.com
[2026-06-04 09:48:06,880]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 385 count 311
[2026-06-04 09:48:06,883]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HanYongamz1@outlook.com
[2026-06-04 09:48:06,883]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:48:16,635]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HanYongamz1@outlook.com
[2026-06-04 09:49:19,639]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,鑫之盛科技有限公司,XINZHISHENG TECHNOLOGY CO., LIMITED,lyncastinc@outlook.com
[2026-06-04 09:49:19,639]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 389 count 312
[2026-06-04 09:49:19,643]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail lyncastinc@outlook.com
[2026-06-04 09:49:19,643]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:49:31,879]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,lyncastinc@outlook.com
[2026-06-04 09:50:32,879]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳欧赛其电子商务有限公司,shenzhenshiousaiqidianzishangwuyouxiangongsi,18871133541@163.com
[2026-06-04 09:50:32,879]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 388 count 313
[2026-06-04 09:50:32,882]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 18871133541@163.com
[2026-06-04 09:50:32,882]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:50:42,097]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,18871133541@163.com
[2026-06-04 09:51:46,099]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,愛絲麗國際有限公司,Acelynn International Limited,jennakhoo@foxmail.com
[2026-06-04 09:51:46,099]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 390 count 314
[2026-06-04 09:51:46,103]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jennakhoo@foxmail.com
[2026-06-04 09:51:46,103]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:51:56,424]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jennakhoo@foxmail.com
[2026-06-04 09:53:00,425]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市江能发科技有限公司,shen zhen shi jiang neng fa ke ji you xian gong si,jiangnengfa168@163.com
[2026-06-04 09:53:00,425]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 391 count 315
[2026-06-04 09:53:00,428]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jiangnengfa168@163.com
[2026-06-04 09:53:00,428]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:53:10,296]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jiangnengfa168@163.com
[2026-06-04 09:54:13,298]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,重庆娅妮市政工程有限公司,Chongqing Yani Municipal Engineering Co., Ltd.,shashaLuo563@outlook.com
[2026-06-04 09:54:13,298]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 443 count 316
[2026-06-04 09:54:13,301]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail shashaLuo563@outlook.com
[2026-06-04 09:54:13,301]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:54:22,877]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,shashaLuo563@outlook.com
[2026-06-04 09:55:26,878]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港东宏电子贸易有限公司,HONGKONG UMEDIA LIMITED,amazon-jp@umediatec.com
[2026-06-04 09:55:26,878]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 393 count 317
[2026-06-04 09:55:26,881]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail amazon-jp@umediatec.com
[2026-06-04 09:55:26,881]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:55:36,964]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,amazon-jp@umediatec.com
[2026-06-04 09:56:45,967]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市峻佳电子有限公司,SHENZHENSHIJUNJIADIANZIYOUXIANGONGSI,13145978717@163.com
[2026-06-04 09:56:45,967]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 394 count 318
[2026-06-04 09:56:45,971]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 13145978717@163.com
[2026-06-04 09:56:45,971]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:56:55,578]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,13145978717@163.com
[2026-06-04 09:58:04,583]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市亮小亮科技有限公司,Shaoyangshiliangxiaoliangkejiyouxiangongsi,liangxiaoliangkeji@163.com
[2026-06-04 09:58:04,583]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 395 count 319
[2026-06-04 09:58:04,586]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail liangxiaoliangkeji@163.com
[2026-06-04 09:58:04,586]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:58:15,044]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,liangxiaoliangkeji@163.com
[2026-06-04 09:59:16,047]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳比格赛纳贸易有限公司,shenzhenbigesainamaoyiyouxiangongsi,szbgsn2023@outlook.com
[2026-06-04 09:59:16,047]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 397 count 320
[2026-06-04 09:59:16,052]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail szbgsn2023@outlook.com
[2026-06-04 09:59:16,052]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 09:59:25,119]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,szbgsn2023@outlook.com
[2026-06-04 10:00:32,120]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖州超越陶瓷有限公司,huzhouchaoyuetaociyouxiangongsi,cailihong1988@outlook.com
[2026-06-04 10:00:32,120]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 398 count 321
[2026-06-04 10:00:32,123]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail cailihong1988@outlook.com
[2026-06-04 10:00:32,123]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:00:42,555]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,cailihong1988@outlook.com
[2026-06-04 10:01:45,557]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,永康市铂科贸易有限公司,Yongkangshibokemaoyiyouxiangongsi,voltaga@163.com
[2026-06-04 10:01:45,557]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 399 count 322
[2026-06-04 10:01:45,560]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail voltaga@163.com
[2026-06-04 10:01:45,560]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:01:55,205]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,voltaga@163.com
[2026-06-04 10:02:58,209]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市维尔晶科技有限公司,SHENZHEN WEIKING TECHNOLOGY CO.,LTD,sales15@w-king.com
[2026-06-04 10:02:58,209]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 401 count 323
[2026-06-04 10:02:58,213]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail sales15@w-king.com
[2026-06-04 10:02:58,213]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:03:07,520]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,sales15@w-king.com
[2026-06-04 10:04:14,524]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,北京优美易家贸易有限公司,BEIJING YOUMEIYIJIA MAOYI YOUXIANGONGSI,zhiwei.lv@saintmossi.com
[2026-06-04 10:04:14,524]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 402 count 324
[2026-06-04 10:04:14,527]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail zhiwei.lv@saintmossi.com
[2026-06-04 10:04:14,527]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:04:23,992]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,zhiwei.lv@saintmossi.com
[2026-06-04 10:05:30,995]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖南省玉鸿贸易有限公司,hunanshengyuhongmaoyiyouxiangongsi,Starpark2025@163.com
[2026-06-04 10:05:30,995]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 403 count 325
[2026-06-04 10:05:30,998]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail Starpark2025@163.com
[2026-06-04 10:05:30,998]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:05:40,483]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,Starpark2025@163.com
[2026-06-04 10:06:42,485]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,華億通科技有限公司,HUAYITONG TECHNOLOGY CO., LIMITED,HayitongKJ@163.COM
[2026-06-04 10:06:42,485]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 404 count 326
[2026-06-04 10:06:42,488]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HayitongKJ@163.COM
[2026-06-04 10:06:42,489]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:06:53,117]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,HayitongKJ@163.COM
[2026-06-04 10:08:00,121]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市丰瑞源科技有限公司,SHENZHEN FENGRUIYUAN TECHNOLOGY CO.,LTD,jpfengruiyuan@sohu.com
[2026-06-04 10:08:00,121]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 408 count 327
[2026-06-04 10:08:00,124]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail jpfengruiyuan@sohu.com
[2026-06-04 10:08:00,124]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:08:09,754]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,jpfengruiyuan@sohu.com
[2026-06-04 10:09:17,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市叮咚音悦文化传播有限公司,Shenzhen Dingdong Yinyue Culture Communication Co., Ltd.,695398033@qq.com
[2026-06-04 10:09:17,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 409 count 328
[2026-06-04 10:09:17,760]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail 695398033@qq.com
[2026-06-04 10:09:17,760]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:09:26,896]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,695398033@qq.com
[2026-06-04 10:10:28,899]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,Zereni Technology LLC,Zereni Technology LLC,catherine@mysknbody.com
[2026-06-04 10:10:28,899]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 410 count 329
[2026-06-04 10:10:28,901]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail catherine@mysknbody.com
[2026-06-04 10:10:28,902]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:10:38,270]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,catherine@mysknbody.com
[2026-06-04 10:11:45,273]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,天津络施崎贸易有限公司,Tianjin Luoshiqi Trading Co., Ltd.,CalmaraAuto35@outlook.com
[2026-06-04 10:11:45,273]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 411 count 330
[2026-06-04 10:11:45,278]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail CalmaraAuto35@outlook.com
[2026-06-04 10:11:45,278]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:11:55,036]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,CalmaraAuto35@outlook.com
[2026-06-04 10:13:00,040]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,HONG KONG PRUDENT PULSE TECHNOLOGY CO., LIMITED,HONG KONG PRUDENT PULSE TECHNOLOGY CO., LIMITED,pm@prudenpulse.com
[2026-06-04 10:13:00,040]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 413 count 331
[2026-06-04 10:13:00,044]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail pm@prudenpulse.com
[2026-06-04 10:13:00,044]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:13:09,918]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,pm@prudenpulse.com
[2026-06-04 10:14:17,921]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,贵州英俭贸易有限公司,guizhouyingjianmaoyiyouxiangongsi,AIJTrade@163.com
[2026-06-04 10:14:17,921]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 414 count 332
[2026-06-04 10:14:17,926]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail AIJTrade@163.com
[2026-06-04 10:14:17,926]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:14:27,834]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,AIJTrade@163.com
[2026-06-04 10:15:34,834]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳华飞通科技有限公司,ShenZhenHuaFeiTongKeJiYouXianGongSi,caizhenyi666@163.com
[2026-06-04 10:15:34,834]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 419 count 333
[2026-06-04 10:15:34,837]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail caizhenyi666@163.com
[2026-06-04 10:15:34,837]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:15:44,352]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,caizhenyi666@163.com
[2026-06-04 10:16:45,353]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,星卓商貿科技有限公司,XINGZHUO TECHNOLOGY & TRADE CO., LIMITED,xzsm03152026@163.com
[2026-06-04 10:16:45,353]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 416 count 334
[2026-06-04 10:16:45,357]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail xzsm03152026@163.com
[2026-06-04 10:16:45,358]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:16:55,694]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,xzsm03152026@163.com
[2026-06-04 10:18:04,695]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞搜谷国际贸易有限公司,Dongguansouguguojimaoyiyouxiangongsi,SGguojijp@163.com
[2026-06-04 10:18:04,695]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 418 count 335
[2026-06-04 10:18:04,698]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail SGguojijp@163.com
[2026-06-04 10:18:04,698]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:18:14,742]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,SGguojijp@163.com
[2026-06-04 10:19:15,745]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市速迈达电子商务有限公司,Shenzhenshi Sumaidadianzishangwu CO LTD,m13714490599@sina.com
[2026-06-04 10:19:15,745]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 420 count 336
[2026-06-04 10:19:15,748]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail m13714490599@sina.com
[2026-06-04 10:19:15,749]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 10:19:25,667]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:156)An email has been sent,m13714490599@sina.com
[2026-06-04 10:20:26,668]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,HONG KONG FED FITNESS LIMITED,HONG KONG FED FITNESS LIMITED,HKFEDFITEU@outlook.com
[2026-06-04 10:20:26,668]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 421 count 337
[2026-06-04 10:20:26,671]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:102)toEmail HKFEDFITEU@outlook.com
[2026-06-04 10:20:26,671]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:103)bccEmail lixiweb@yahoo.co.jp
javax.mail.MessagingException: Can't send command to SMTP host;
  nested exception is:
	java.net.SocketException: Connection or outbound has closed
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2157)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2144)
	at com.sun.mail.smtp.SMTPTransport.close(SMTPTransport.java:1210)
	at javax.mail.Transport.send0(Transport.java:197)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:233)
Caused by: java.net.SocketException: Connection or outbound has closed
	at java.base/sun.security.ssl.SSLSocketImpl$AppOutputStream.write(SSLSocketImpl.java:1298)
	at com.sun.mail.util.TraceOutputStream.write(TraceOutputStream.java:128)
	at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
	at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2155)
	... 7 more
javax.mail.MessagingException: Can't send command to SMTP host;
  nested exception is:
	java.net.SocketException: Connection or outbound has closed
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2157)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2144)
	at com.sun.mail.smtp.SMTPTransport.close(SMTPTransport.java:1210)
	at javax.mail.Transport.send0(Transport.java:197)
	at javax.mail.Transport.send(Transport.java:124)
	at com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:155)
	at com.panda.utils.FuncUtils.sendMail_METI(FuncUtils.java:2403)
	at com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:233)
Caused by: java.net.SocketException: Connection or outbound has closed
	at java.base/sun.security.ssl.SSLSocketImpl$AppOutputStream.write(SSLSocketImpl.java:1298)
	at com.sun.mail.util.TraceOutputStream.write(TraceOutputStream.java:128)
	at java.base/java.io.BufferedOutputStream.flushBuffer(BufferedOutputStream.java:81)
	at java.base/java.io.BufferedOutputStream.flush(BufferedOutputStream.java:142)
	at com.sun.mail.smtp.SMTPTransport.sendCommand(SMTPTransport.java:2155)
	... 7 more
[2026-06-04 10:20:34,917]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:249)END



[2026-06-04 15:39:46,626]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-04 15:39:51,494]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-04 15:39:51,501]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,沈阳吞戾治商贸有限公司,shenyangtunlizhishangmaoyouxiangongsi,yifoi5657@outlook.com
[2026-06-04 15:39:51,501]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 39 count 35
[2026-06-04 15:42:21,453]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail yifoi5657@outlook.com
[2026-06-04 15:42:21,453]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:42:34,071]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,yifoi5657@outlook.com
[2026-06-04 15:43:37,074]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山市鑫树聚照明科技有限公司,zhongshanshixinshujuzhaomingkejiyouxiangongsi,LYQ158159@outlook.com
[2026-06-04 15:43:37,075]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 42 count 38
[2026-06-04 15:44:19,386]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail LYQ158159@outlook.com
[2026-06-04 15:44:19,386]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:44:29,511]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,LYQ158159@outlook.com
[2026-06-04 15:45:36,513]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳志真科技有限公司,Shenzhen Zhizhen kejiyouxiangongsi,f18038071565@outlook.com
[2026-06-04 15:45:36,514]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 48 count 44
[2026-06-04 15:46:56,148]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail f18038071565@outlook.com
[2026-06-04 15:46:56,148]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:47:06,521]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,f18038071565@outlook.com
[2026-06-04 15:48:11,524]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,陕西云端天幕网络科技有限责任公司,SHANXIYUNDUANTIANMUWANGLUOKEJIYOUXIANZERENGONGSI,HananTewell579@outlook.com
[2026-06-04 15:48:11,525]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 57 count 50
[2026-06-04 15:48:59,532]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail HananTewell579@outlook.com
[2026-06-04 15:48:59,533]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:49:10,532]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,HananTewell579@outlook.com
[2026-06-04 15:50:12,536]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市如鼎禾科技有限公司,Shenzhenshi Rudinghe Technology Co., Ltd,anniber_jp@163.com
[2026-06-04 15:50:12,536]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 87 count 77
[2026-06-04 15:50:12,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail anniber_jp@163.com
[2026-06-04 15:50:12,542]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:50:23,415]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,anniber_jp@163.com
[2026-06-04 15:51:26,418]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市聚诗科技有限公司,Shenzhenshi Jushi Kejiyouxiangongsi,ceciliaABC3996@163.com
[2026-06-04 15:51:26,418]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 88 count 78
[2026-06-04 15:51:26,422]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail ceciliaABC3996@163.com
[2026-06-04 15:51:26,422]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:51:36,132]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,ceciliaABC3996@163.com
[2026-06-04 15:52:41,136]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市杰泓包装有限公司,SHENZHEN SHI JIEHONG BAOZHUANG YOUXIAN GONGSI,laolee@outlook.jp
[2026-06-04 15:52:41,137]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 89 count 79
[2026-06-04 15:52:41,139]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail laolee@outlook.jp
[2026-06-04 15:52:41,139]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 15:52:51,560]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,laolee@outlook.jp
[2026-06-04 15:53:55,562]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,林建,LIN　JIAN,191681280@qq.com
[2026-06-04 15:53:55,562]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 90 count 80


[2026-06-04 16:01:59,640]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:32)START
[2026-06-04 16:02:04,117]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:132)key=深圳市星火星电子商务有限公司, value=同一公司，邮件不同，err
[2026-06-04 16:02:04,124]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,林建,LIN　JIAN,191681280@qq.com
[2026-06-04 16:02:04,125]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 90 count 80
[2026-06-04 16:02:10,264]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 191681280@qq.com
[2026-06-04 16:02:10,264]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:02:22,877]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,191681280@qq.com
[2026-06-04 16:03:23,880]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,赵惠野,zhao　huiye,564212527@qq.com
[2026-06-04 16:03:23,880]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 91 count 81
[2026-06-04 16:03:23,886]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 564212527@qq.com
[2026-06-04 16:03:23,886]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:03:35,719]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,564212527@qq.com
[2026-06-04 16:04:44,720]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州庆讯商贸有限公司,GUANGZHOUQINGXUNSHANGMAOYOUXIANGONGSI,CalnimptewaFetzer4009@outlook.com
[2026-06-04 16:04:44,721]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 92 count 82
[2026-06-04 16:04:44,721]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:05:28,512]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,山西创亿电子科技有限公司,Shan xi chuang yi dian zi ke ji you xian gong si,chuangyiyajianguk@163.com
[2026-06-04 16:05:28,530]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 93 count 83
[2026-06-04 16:05:28,583]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail chuangyiyajianguk@163.com
[2026-06-04 16:05:28,584]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:05:38,219]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,chuangyiyajianguk@163.com
[2026-06-04 16:06:41,248]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙止拍贸易有限公司,ChangShaZhiPaiMaoYiYouXianGongSi,xiaoyanlan19@163.com
[2026-06-04 16:06:41,249]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 95 count 84
[2026-06-04 16:06:41,302]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail xiaoyanlan19@163.com
[2026-06-04 16:06:41,303]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:06:51,728]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,xiaoyanlan19@163.com
[2026-06-04 16:07:53,757]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市如虎添亿科技有限公司,shenzhenshiruhutianyikejiyouxiangongsi,Sunnymtiger@outlook.com
[2026-06-04 16:07:53,758]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 96 count 85
[2026-06-04 16:07:53,760]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:07:59,264]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,莆田市城厢区恰纪议贸易有限公司,putianshichengxiangquqiajiyimaoyiyouxiangongsi,xpf289587@163.com
[2026-06-04 16:07:59,265]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 97 count 86
[2026-06-04 16:07:59,320]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail xpf289587@163.com
[2026-06-04 16:07:59,321]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:08:11,801]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,xpf289587@163.com
[2026-06-04 16:09:15,829]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,商丘拓岛商贸有限公司,Shangqiu Tuodao Trading Co., Ltd,tuodaoshangmao@163.com
[2026-06-04 16:09:15,830]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 98 count 87
[2026-06-04 16:09:15,893]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail tuodaoshangmao@163.com
[2026-06-04 16:09:15,894]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:09:28,195]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,tuodaoshangmao@163.com
[2026-06-04 16:10:37,229]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中山韶明科技有限公司,zhongshanshaomingkejiyouxiangongsi,NH_mingming@hotmail.com
[2026-06-04 16:10:37,230]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 239 count 88
[2026-06-04 16:10:37,231]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:10:37,259]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市力信供应链科技有限公司,shenzhenshilixingongyingliankejiyouxiangongsi,lexonintl_jp@hotmail.com
[2026-06-04 16:10:37,260]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 100 count 89
[2026-06-04 16:10:37,261]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:10:37,437]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市酷而美创新科技有限公司,Shen Zhen Shi Ku Er Mei Chuang Xin Ke Ji You Xian Gong Si,tony.xia@coolmetech.com
[2026-06-04 16:10:37,438]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 110 count 97
[2026-06-04 16:10:37,492]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail tony.xia@coolmetech.com
[2026-06-04 16:10:37,493]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:10:48,042]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,tony.xia@coolmetech.com
[2026-06-04 16:11:58,503]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州佰嘉汇厨具有限公司　,WENZHOUBAIJIAHUICHUJUYOUXIANGONGSI,TaniaEmbt66@outlook.com
[2026-06-04 16:11:58,504]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 138 count 121
[2026-06-04 16:11:58,505]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:11:58,627]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,中鈺企業社　,Zhong Yu Company,hulichung168@outlook.com
[2026-06-04 16:11:58,628]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 145 count 127
[2026-06-04 16:11:58,629]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:11:58,657]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,南京隆赢家居家居科技有限公司　,Nanjing Longying Home Technology Co., Ltd.,jason@ecbarley.com
[2026-06-04 16:11:58,658]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 146 count 128
[2026-06-04 16:11:58,710]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail jason@ecbarley.com
[2026-06-04 16:11:58,711]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:12:07,830]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,jason@ecbarley.com
[2026-06-04 16:13:14,860]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市前沿智造科技有限公司　,Shenzhen shi Qianyan Zhizao Keji youxian gongsi,kimi@opuor.com.cn
[2026-06-04 16:13:14,861]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 147 count 129
[2026-06-04 16:13:14,919]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail kimi@opuor.com.cn
[2026-06-04 16:13:14,920]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:13:24,926]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,kimi@opuor.com.cn
[2026-06-04 16:14:32,958]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞福源国际贸易有限公司,DongGuanFuYuanGuoJiMaoYiYouXianGongSi,chao8882009@163.com
[2026-06-04 16:14:32,959]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 148 count 130
[2026-06-04 16:14:33,022]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail chao8882009@163.com
[2026-06-04 16:14:33,023]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:14:43,642]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,chao8882009@163.com
[2026-06-04 16:15:47,677]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州德名立科技有限公司,Guangzhou Demingli Technology Co.,Ltd.,demila0625@outlook.com
[2026-06-04 16:15:47,678]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 253 count 131
[2026-06-04 16:15:47,679]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:15:47,707]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市菖旺进出口有限公司,Jiangmen Changwang Import and Export Co., Ltd,changwanglighting@163.com
[2026-06-04 16:15:47,708]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 150 count 132
[2026-06-04 16:15:47,768]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail changwanglighting@163.com
[2026-06-04 16:15:47,769]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:15:58,216]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,changwanglighting@163.com
[2026-06-04 16:17:07,248]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州莱喆科科技有限公司,Guangzhou Laizheke Technology Co.,Ltd.,tagiyoon0312@outlook.com
[2026-06-04 16:17:07,249]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 292 count 133
[2026-06-04 16:17:07,250]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:17:07,279]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,江门市汇瀛商贸有限公司　,Jiangmen Huiying Trading Co., Ltd,jmhysm0008@163.com
[2026-06-04 16:17:07,281]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 152 count 134
[2026-06-04 16:17:07,348]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail jmhysm0008@163.com
[2026-06-04 16:17:07,349]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:17:17,572]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,jmhysm0008@163.com
[2026-06-04 16:18:22,615]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖南鑫讯电子商务有限公司　,Hu Nan Xin Xun Dian Zi Shang Wu You Xian Gong Si,liuaizheng_123@outlook.com
[2026-06-04 16:18:22,617]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 153 count 135
[2026-06-04 16:18:22,617]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:18:23,426]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,成都莹诗元电子商务有限公司,chengduyingshiyuandianzishangwuyouxiangongsi,simegu95335@163.com
[2026-06-04 16:18:23,427]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 370 count 174
[2026-06-04 16:18:23,482]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail simegu95335@163.com
[2026-06-04 16:18:23,483]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:18:34,296]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,simegu95335@163.com
[2026-06-04 16:19:44,333]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市光逸科技创新有限公司,Shenzhen Velocity Technology Innovations Co.,Ltd.,ce@livelocity.com
[2026-06-04 16:19:44,334]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 321 count 175
[2026-06-04 16:19:44,396]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail ce@livelocity.com
[2026-06-04 16:19:44,397]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:19:55,082]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,ce@livelocity.com
[2026-06-04 16:20:58,116]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州帏红商贸有限公司,guangzhouweihongshangmaoyouxiangongsi,weihongshangmao188@yeah.net
[2026-06-04 16:20:58,117]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 203 count 176
[2026-06-04 16:20:58,195]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail weihongshangmao188@yeah.net
[2026-06-04 16:20:58,196]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:21:08,635]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,weihongshangmao188@yeah.net
[2026-06-04 16:22:12,672]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市连韵商贸有限公司,shaoyangshilianyunshangmaoyouxiangongsi,liannnnyunn@163.com
[2026-06-04 16:22:12,674]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 204 count 177
[2026-06-04 16:22:12,732]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail liannnnyunn@163.com
[2026-06-04 16:22:12,733]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:22:22,208]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,liannnnyunn@163.com
[2026-06-04 16:23:28,248]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,新宁县支恒商贸有限公司,xinningxianzhihengshangmaoyouxiangongsi,xnzhiheng2025@163.com
[2026-06-04 16:23:28,249]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 205 count 178
[2026-06-04 16:23:28,303]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail xnzhiheng2025@163.com
[2026-06-04 16:23:28,304]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:23:38,707]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,xnzhiheng2025@163.com
[2026-06-04 16:24:43,743]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞市澜慧莱电子科技有限公司,Dongguan Lanhuilai Electronic Technology Co., Ltd,yangli515918506@163.com
[2026-06-04 16:24:43,744]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 206 count 179
[2026-06-04 16:24:43,802]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail yangli515918506@163.com
[2026-06-04 16:24:43,804]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:24:53,909]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,yangli515918506@163.com
[2026-06-04 16:25:57,943]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,优亚科技（天津）有限公司,Youya Technology (Tianjin) Co., Ltd,3876474548@qq.com
[2026-06-04 16:25:57,944]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 207 count 180
[2026-06-04 16:25:58,000]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 3876474548@qq.com
[2026-06-04 16:25:58,001]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:26:09,001]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,3876474548@qq.com
[2026-06-04 16:27:13,038]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,利錄科技股份有限公司,EZ DUPE INC.,kevinwepihuang@ezdupe.com.tw
[2026-06-04 16:27:13,039]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 225 count 181
[2026-06-04 16:27:13,101]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail kevinwepihuang@ezdupe.com.tw
[2026-06-04 16:27:13,103]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:27:22,372]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,kevinwepihuang@ezdupe.com.tw
[2026-06-04 16:28:27,417]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,天枢岚途科技（深圳）有限责任公司,Tianshu Lantu Technology (Shenzhen) Co., Ltd.,TanLatlesOfficial@outlook.com
[2026-06-04 16:28:27,418]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 209 count 182
[2026-06-04 16:28:27,419]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:28:27,456]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,温州来根跑跑糖贸易有限公司,wenzhoulaigenpaopaotangmaoyiyouxiangongsi,532347467@qq.com
[2026-06-04 16:28:27,457]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 210 count 183
[2026-06-04 16:28:27,512]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 532347467@qq.com
[2026-06-04 16:28:27,513]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:28:37,748]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,532347467@qq.com
[2026-06-04 16:29:40,958]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市里诚电子商务有限公司,Shenzhen Licheng E-Commerce Co., Ltd.,licheng156332@hotmail.com
[2026-06-04 16:29:40,959]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 222 count 192
[2026-06-04 16:29:40,960]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:29:41,565]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,陶瓷照明灯饰有限公司,CERAMIC LED LIGHTING LIMITED,sansi.jp1@sansiled.com
[2026-06-04 16:29:41,566]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 267 count 223
[2026-06-04 16:29:41,630]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail sansi.jp1@sansiled.com
[2026-06-04 16:29:41,631]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:29:51,490]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,sansi.jp1@sansiled.com
[2026-06-04 16:30:58,527]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,周口往冰商贸有限公司,ZhouKouWangBingShangMaoYouXianGongSi,GSIUbdug@outlook.com
[2026-06-04 16:30:58,528]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 271 count 224
[2026-06-04 16:30:58,529]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:30:58,593]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市叶欧电子科技有限公司,shen zhen shi ye ou dian zi ke ji you xian gong si,wangqianqiancherry1588815@jpvat.com
[2026-06-04 16:30:58,594]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 270 count 226
[2026-06-04 16:30:58,649]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail wangqianqiancherry1588815@jpvat.com
[2026-06-04 16:30:58,649]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:31:08,467]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,wangqianqiancherry1588815@jpvat.com
[2026-06-04 16:32:17,497]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,漯河跃眠贸易有限公司,luoheyuemianmaoyiyouxiangongsi,LHYUEMIAN@163.com
[2026-06-04 16:32:17,498]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 272 count 227
[2026-06-04 16:32:17,570]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail LHYUEMIAN@163.com
[2026-06-04 16:32:17,571]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:32:27,175]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,LHYUEMIAN@163.com
[2026-06-04 16:33:30,221]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,临沂乐鲁花卉有限公司,LinYiLeLuHuaHuiYouXianGongSi,HanYongamz1@outlook.com
[2026-06-04 16:33:30,222]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 364 count 228
[2026-06-04 16:33:30,223]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:33:30,260]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙市银坐科技发展有限公司,changshashiyinzuokejifazhanyouxiangongsi,cs_hexian@163.com
[2026-06-04 16:33:30,261]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 276 count 229
[2026-06-04 16:33:30,322]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail cs_hexian@163.com
[2026-06-04 16:33:30,323]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:33:40,040]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,cs_hexian@163.com
[2026-06-04 16:34:46,078]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,连云港喜木农副产品有限公司,lianyungangximunongfuchanpinyouxiangongsi,bixi6751819@163.com
[2026-06-04 16:34:46,079]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 277 count 230
[2026-06-04 16:34:46,135]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail bixi6751819@163.com
[2026-06-04 16:34:46,136]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:34:56,148]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,bixi6751819@163.com
[2026-06-04 16:36:03,184]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,枝江市宅朋科技有限公司,zhi jiang shi zhai peng ke ji you xian gong si,zjzm16899@163.com
[2026-06-04 16:36:03,185]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 282 count 231
[2026-06-04 16:36:03,255]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail zjzm16899@163.com
[2026-06-04 16:36:03,256]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:36:13,511]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,zjzm16899@163.com
[2026-06-04 16:37:15,841]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,广州乐桃科技有限公司,Guangzhou Letao Keji Youxiangongsi,Letokids2022@outlook.com
[2026-06-04 16:37:15,842]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 307 count 250
[2026-06-04 16:37:15,843]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:37:15,881]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市光之晨商贸有限公司,shenzhenshiguangzhichenshangmaoyouxiangongsi,gzc-amzjp@hotmail.com
[2026-06-04 16:37:15,882]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 308 count 251
[2026-06-04 16:37:15,883]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:37:16,215]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,海口六伍八科技有限公司,haikouliuwubakejiyouxiangongsi,six-five-eight@outlook.com
[2026-06-04 16:37:16,216]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 330 count 269
[2026-06-04 16:37:16,217]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:37:16,247]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,长沙市岑诺科技有限公司,changshashicennuokejiyouxiangongsi,275378975@qq.com
[2026-06-04 16:37:16,248]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 331 count 270
[2026-06-04 16:37:16,295]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 275378975@qq.com
[2026-06-04 16:37:16,295]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:37:25,734]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,275378975@qq.com
[2026-06-04 16:38:33,788]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市酷而美创新科技有限公司,Ku Er Mei Chuang Xin Ke Ji You Xian Gong Si,tony.xia@coolmetech.com
[2026-06-04 16:38:33,789]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 332 count 271
[2026-06-04 16:38:33,853]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail tony.xia@coolmetech.com
[2026-06-04 16:38:33,854]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:38:44,467]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,tony.xia@coolmetech.com
[2026-06-04 16:39:47,512]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,东莞汇臻科技有限公司,dongguanhuizhenkejiyouxiangongsi,dongguanhuizhen2020@hotmail.com
[2026-06-04 16:39:47,513]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 334 count 272
[2026-06-04 16:39:47,514]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:39:47,560]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市澜晖贸易有限公司,Shenzhen Lanhui Trading Co., Ltd,LeileiZhang@jpipr.com
[2026-06-04 16:39:47,561]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 346 count 273
[2026-06-04 16:39:47,617]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail LeileiZhang@jpipr.com
[2026-06-04 16:39:47,618]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:39:57,948]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,LeileiZhang@jpipr.com
[2026-06-04 16:41:02,998]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,郑州市二七区祥发包装材料有限公司,ZHENGZHOUSHIERQIQUXIANGFABAOZHUANGCAILIAOYOUXIANGONGSI,pa4864@163.com
[2026-06-04 16:41:02,999]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 337 count 274
[2026-06-04 16:41:03,066]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail pa4864@163.com
[2026-06-04 16:41:03,067]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:41:13,732]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,pa4864@163.com
[2026-06-04 16:42:17,795]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳尤兵电子商务有限公司,shaoyangyoubingdianzishangwuyouxiangongsi,Anserky@163.com
[2026-06-04 16:42:17,796]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 339 count 276
[2026-06-04 16:42:17,854]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail Anserky@163.com
[2026-06-04 16:42:17,855]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:42:27,679]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,Anserky@163.com
[2026-06-04 16:43:32,721]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,滨州市阳信县好好二手车销售有限责任公司,Binzhou Yangxin Haohao Used Car Sales Co., Ltd.,WangLiYongamz@outlook.com
[2026-06-04 16:43:32,722]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 341 count 277
[2026-06-04 16:43:32,723]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:32,800]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港创想三维投资有限公司,HONGKONG CREALITY 3D INVESTMENT CO.,LIMITED,crealityecosystem@outlook.com
[2026-06-04 16:43:32,801]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 345 count 280
[2026-06-04 16:43:32,802]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:32,932]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,珠海恒越电子商务有限公司,ZHUHAI HENGYUE E-COMMERCE CO., LTD,hengyue202311@outlook.com
[2026-06-04 16:43:32,933]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 350 count 284
[2026-06-04 16:43:32,934]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:33,410]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市永荣服装有限公司,Shen Zhen Yong Rong Apparel Co.,LTD,rong1xun@hotmail.com
[2026-06-04 16:43:33,412]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 378 count 307
[2026-06-04 16:43:33,413]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:33,511]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港巨海科技有限公司,HongKong juhai Technology Co., Limited,HanYongamz1@outlook.com
[2026-06-04 16:43:33,512]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 385 count 311
[2026-06-04 16:43:33,513]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:33,555]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,鑫之盛科技有限公司,XINZHISHENG TECHNOLOGY CO., LIMITED,lyncastinc@outlook.com
[2026-06-04 16:43:33,556]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 389 count 312
[2026-06-04 16:43:33,557]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:43:33,633]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市江能发科技有限公司,shen zhen shi jiang neng fa ke ji you xian gong si,jiangnengfa168@163.com
[2026-06-04 16:43:33,635]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 391 count 315
[2026-06-04 16:43:33,688]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail jiangnengfa168@163.com
[2026-06-04 16:43:33,689]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:43:44,037]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,jiangnengfa168@163.com
[2026-06-04 16:44:50,086]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,重庆娅妮市政工程有限公司,Chongqing Yani Municipal Engineering Co., Ltd.,shashaLuo563@outlook.com
[2026-06-04 16:44:50,087]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 443 count 316
[2026-06-04 16:44:50,088]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:44:50,136]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,香港东宏电子贸易有限公司,HONGKONG UMEDIA LIMITED,amazon-jp@umediatec.com
[2026-06-04 16:44:50,137]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 393 count 317
[2026-06-04 16:44:50,194]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail amazon-jp@umediatec.com
[2026-06-04 16:44:50,194]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:44:59,878]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,amazon-jp@umediatec.com
[2026-06-04 16:46:03,937]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市峻佳电子有限公司,SHENZHENSHIJUNJIADIANZIYOUXIANGONGSI,13145978717@163.com
[2026-06-04 16:46:03,939]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 394 count 318
[2026-06-04 16:46:04,000]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail 13145978717@163.com
[2026-06-04 16:46:04,001]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:46:14,217]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,13145978717@163.com
[2026-06-04 16:47:23,268]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,邵阳市亮小亮科技有限公司,Shaoyangshiliangxiaoliangkejiyouxiangongsi,liangxiaoliangkeji@163.com
[2026-06-04 16:47:23,269]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 395 count 319
[2026-06-04 16:47:23,325]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail liangxiaoliangkeji@163.com
[2026-06-04 16:47:23,326]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:47:33,146]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,liangxiaoliangkeji@163.com
[2026-06-04 16:48:43,192]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳比格赛纳贸易有限公司,shenzhenbigesainamaoyiyouxiangongsi,szbgsn2023@outlook.com
[2026-06-04 16:48:43,193]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 397 count 320
[2026-06-04 16:48:43,194]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:48:43,241]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖州超越陶瓷有限公司,huzhouchaoyuetaociyouxiangongsi,cailihong1988@outlook.com
[2026-06-04 16:48:43,242]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 398 count 321
[2026-06-04 16:48:43,243]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:235)outlook skip
[2026-06-04 16:48:43,289]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,永康市铂科贸易有限公司,Yongkangshibokemaoyiyouxiangongsi,voltaga@163.com
[2026-06-04 16:48:43,290]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 399 count 322
[2026-06-04 16:48:43,351]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail voltaga@163.com
[2026-06-04 16:48:43,352]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:48:54,545]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,voltaga@163.com
[2026-06-04 16:49:56,586]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,深圳市维尔晶科技有限公司,SHENZHEN WEIKING TECHNOLOGY CO.,LTD,sales15@w-king.com
[2026-06-04 16:49:56,587]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 401 count 323
[2026-06-04 16:49:56,641]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail sales15@w-king.com
[2026-06-04 16:49:56,642]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:50:07,388]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,sales15@w-king.com
[2026-06-04 16:51:08,441]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,北京优美易家贸易有限公司,BEIJING YOUMEIYIJIA MAOYI YOUXIANGONGSI,zhiwei.lv@saintmossi.com
[2026-06-04 16:51:08,442]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 402 count 324
[2026-06-04 16:51:08,497]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail zhiwei.lv@saintmossi.com
[2026-06-04 16:51:08,498]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:51:18,691]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,zhiwei.lv@saintmossi.com
[2026-06-04 16:52:24,748]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,湖南省玉鸿贸易有限公司,hunanshengyuhongmaoyiyouxiangongsi,Starpark2025@163.com
[2026-06-04 16:52:24,749]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 403 count 325
[2026-06-04 16:52:24,810]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail Starpark2025@163.com
[2026-06-04 16:52:24,811]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:52:34,546]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,Starpark2025@163.com
[2026-06-04 16:53:37,583]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,華億通科技有限公司,HUAYITONG TECHNOLOGY CO., LIMITED,HayitongKJ@163.COM
[2026-06-04 16:53:37,584]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 404 count 326
[2026-06-04 16:53:37,629]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail HayitongKJ@163.COM
[2026-06-04 16:53:37,630]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:53:47,667]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,HayitongKJ@163.COM
[2026-06-04 16:54:53,806]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:229)col,HONG KONG PRUDENT PULSE TECHNOLOGY CO., LIMITED,HONG KONG PRUDENT PULSE TECHNOLOGY CO., LIMITED,pm@prudenpulse.com
[2026-06-04 16:54:53,807]:INFO com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:230)i 413 count 331
[2026-06-04 16:54:53,890]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:107)toEmail pm@prudenpulse.com
[2026-06-04 16:54:53,891]:INFO com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:108)bccEmail lixiweb@yahoo.co.jp
[2026-06-04 16:55:03,323]:DEBUG com.panda.utils.SendMailMETI.sendMessage(SendMailMETI.java:161)An email has been sent,pm@prudenpulse.com
[2026-06-04 16:56:04,752]:ERROR com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:186)i 1112
[2026-06-04 16:56:04,753]:DEBUG com.panda.batch.AiAutoExe_METI.main(AiAutoExe_METI.java:255)END




您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,广州庆讯商贸有限公司,CalnimptewaFetzer4009@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,深圳市如虎添亿科技有限公司,Sunnymtiger@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,温州佰嘉汇厨具有限公司　,TaniaEmbt66@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,中鈺企業社　,hulichung168@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,广州德名立科技有限公司,demila0625@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,广州莱喆科科技有限公司,tagiyoon0312@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,湖南鑫讯电子商务有限公司　,liuaizheng_123@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,天枢岚途科技（深圳）有限责任公司,TanLatlesOfficial@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,周口往冰商贸有限公司,GSIUbdug@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,临沂乐鲁花卉有限公司,HanYongamz1@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,广州乐桃科技有限公司,Letokids2022@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,海口六伍八科技有限公司,six-five-eight@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,滨州市阳信县好好二手车销售有限责任公司,WangLiYongamz@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,香港创想三维投资有限公司,crealityecosystem@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,珠海恒越电子商务有限公司,hengyue202311@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,香港巨海科技有限公司,HanYongamz1@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,鑫之盛科技有限公司,lyncastinc@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,重庆娅妮市政工程有限公司,shashaLuo563@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,深圳比格赛纳贸易有限公司,szbgsn2023@outlook.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,湖州超越陶瓷有限公司,cailihong1988@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,深圳万创钱潮科技有限公司,unigearjp@outlook.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,陕西云端天幕网络科技有限责任公司,HananTewell579@outlook.com


您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,中山韶明科技有限公司,NH_mingming@hotmail.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,深圳市力信供应链科技有限公司,lexonintl_jp@hotmail.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,深圳市里诚电子商务有限公司,licheng156332@hotmail.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,深圳市光之晨商贸有限公司,gzc-amzjp@hotmail.com
您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,东莞汇臻科技有限公司,dongguanhuizhen2020@hotmail.com
收件方地址不存在，或暂时不可用，导致邮件被系统退回。,深圳市永荣服装有限公司,rong1xun@hotmail.com


您的账号外发频率超过邮件系统限制，导致邮件被系统退回。,邵阳尤兵电子商务有限公司,Anserky@163.com
接收地址不存在、或者接收地址被禁用。,HONG KONG PRUDENT PULSE TECHNOLOGY CO., LIMITED,pm@prudenpulse.com






 *
 *
 *
 */



