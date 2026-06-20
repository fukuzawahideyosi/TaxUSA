package com.panda.batch;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.bean.t_xiaofeishui_shengaoBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.dao.t_etax_zhongjian_shengaoDao;
import com.panda.dao.t_xiaofeishui_shengaoDao;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsHtml;
import com.panda.utils.FuncUtilsXML;

/*
 * 消费税申告生成
 */
public class shengaojieguo_chuli {

	private static Logger logger = Logger.getLogger(shengaojieguo_chuli.class.toString());

	public static void main(String[] args) {

		//アップロードするフォルダ
		String sourceFilePath = "C:\\Users\\Administrator\\Desktop\\申告结果出力/output";

		String chuli_type = "申告";
//		chuli_type = "转代理";

		int countS = 695;
		int countE = 699;
//		230700
		shengaojieguo_chuli shengaojieguo_chuli = new shengaojieguo_chuli();
		shengaojieguo_chuli.exe(sourceFilePath, chuli_type, countS, countE, "PDSK240074");

		//删除税理士信息
		PDFToImageToPDF.pdfToImageToPDF(sourceFilePath, "");

	}

	public String exe(String sourceFilePath, String chuli_type, int countS, int countE, String line) {
		String directoryPath ="";
		try {
			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
			t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


			/*
			 * 登録データ準備
			 */
			int count = 0;
			for (int k = countS; k <= countE; k++) {

				String PDSK= line;
				if (countS != -1) {
					PDSK= "PDSK" + String.format("%06d", k);
				}
				++count;
				logger.info("处理个数: " + count + "個目 " + PDSK);


//				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
				t_xiaofeishui_shengaoBean t_xiaofeishui_shengaoBean = t_xiaofeishui_shengaoDao.SelectKeyValue("PDSK", PDSK);
				String yyyymmdd_count = t_xiaofeishui_shengaoBean.getYyyymmdd_count();

				logger.info("yyyymmdd_count : " + yyyymmdd_count);
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_by_bangou(t_etax_account_infoExBean.getBangou(), chuli_type);

				String CompanyName_Englis = t_etax_account_infoExBean.getCompanyName_English();
				String getCompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
				PDSK = PDSK.replace("PDSK23", "PDSK") + generateSixDigitRandomNumber();


				directoryPath = sourceFilePath + "/" + PDSK + "_" + getCompanyName_Chinese;
				// 将字符串路径转换为Path对象
				Path path = Paths.get(directoryPath);
				// 创建目录，包括任何必要但不存在的父目录
				Files.createDirectories(path);

				if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml_qr())) {
					if (t_etax_jieguoExBean.getHtml_qr().contains("skip") == false) {

						//html转PDF报错对应
//			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml_qr());
//			            Elements inputs = doc.select("input");
//			            for (Element input : inputs) {
//			                if ("hidden".equals(input.attr("type"))) {
//			                    input.remove();
//			                }
//			            }
//			            t_etax_jieguoExBean.setHtml_qr(doc.html().replace("<br>", "<br />").replace("type=\"text/css\">", "type=\"text/css\" />"));


				        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"))) {
				            writer.write(t_etax_jieguoExBean.getHtml_qr());
				        }
//				        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.pdf")) {
//				        	PdfRendererBuilder builder = new PdfRendererBuilder();
//				        	builder.useFastMode();
//				        	builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"));
//				        	builder.toStream(os);
//				        	builder.run();
//				        }





					}
				}

		        if (t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu() != null) {
		        	try (FileOutputStream outputStream = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_提出消費税申告書.pdf")) {
		        		byte[] buffer = new byte[4096];
		        		int bytesRead;
		        		while ((bytesRead = t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu().read(buffer)) != -1) {
		        			outputStream.write(buffer, 0, bytesRead);
		        		}
		        	}
		        }


		        if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml())) {
			        if (t_etax_jieguoExBean.getHtml().contains("skip") == false) {

			        	//html转PDF报错对应
			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml());
			        	Elements mainElements = doc.select("main");
			        	for (Element main : mainElements) {
			        		main.removeAttr("inert");
			        	}
			        	t_etax_jieguoExBean.setHtml(doc.html().replace("<br>", "<br />").replace("type=\"text/css\">", "type=\"text/css\" />"));


//			            String event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
//			            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
//			            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
//			            if (!StringUtils.isEmpty(event + taxable_amount + total_tax_amount)) {
//			                t_etax_jieguoDao.Update_event_taxable_amount_total_tax_amount(yyyymmdd_count, event, taxable_amount, total_tax_amount);
//			            }

			        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + CompanyName_Englis + "_受信通知.html"))) {
			        		writer.write(t_etax_jieguoExBean.getHtml());
			        	}
//			        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_受信通知.pdf")) {
//			            PdfRendererBuilder builder = new PdfRendererBuilder();
//			            builder.useFastMode();
//			            builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html"));//, "UTF-8"
//			            builder.toStream(os);
//			            builder.run();
//			        }


			        }

		        }


//		        File FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html");
//		        FileHtml.delete();
//		        FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html");
//		        FileHtml.delete();

			}



		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.info("end");
		return directoryPath;
	}

	public String exeTatujin(String sourceFilePath, String chuli_type, int countS, int countE, String line) {
		String directoryPath ="";
		try {
			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
			t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


			/*
			 * 登録データ準備
			 */
			int count = 0;
			for (int k = countS; k <= countE; k++) {

				String tatujin_id= line;
				if (countS != -1) {
					tatujin_id= "PDSK" + String.format("%06d", k);
				}
				++count;
				logger.info("处理个数: " + count + "個目 " + tatujin_id);


//				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue("tatujin_id", tatujin_id);
				String yyyymmdd_count = t_etax_account_infoExBean.getYyyymmdd_count();

				logger.info("yyyymmdd_count : " + yyyymmdd_count);
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo(yyyymmdd_count, "2025", chuli_type);


				String CompanyName_Englis = t_etax_account_infoExBean.getCompanyName_English();
				String getCompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
//				tatujin_id = tatujin_id.replace("PDSK23", "PDSK") + generateSixDigitRandomNumber();


				directoryPath = sourceFilePath + "/" + tatujin_id + "_" + getCompanyName_Chinese + "/5受信通知";
				directoryPath = sourceFilePath + "/" + tatujin_id + "_" + getCompanyName_Chinese;
				// 将字符串路径转换为Path对象
				Path path = Paths.get(directoryPath);
				// 创建目录，包括任何必要但不存在的父目录
				Files.createDirectories(path);

//				if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml_qr())) {
//					if (t_etax_jieguoExBean.getHtml_qr().contains("skip") == false) {
//
//						//html转PDF报错对应
////			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml_qr());
////			            Elements inputs = doc.select("input");
////			            for (Element input : inputs) {
////			                if ("hidden".equals(input.attr("type"))) {
////			                    input.remove();
////			                }
////			            }
////			            t_etax_jieguoExBean.setHtml_qr(doc.html().replace("<br>", "<br />").replace("type=\"text/css\">", "type=\"text/css\" />"));
//
//
//				        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"))) {
//				            writer.write(t_etax_jieguoExBean.getHtml_qr());
//				        }
////				        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.pdf")) {
////				        	PdfRendererBuilder builder = new PdfRendererBuilder();
////				        	builder.useFastMode();
////				        	builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"));
////				        	builder.toStream(os);
////				        	builder.run();
////				        }
//
//
//
//
//
//					}
//				}

//		        if (t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu() != null) {
//		        	try (FileOutputStream outputStream = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_提出消費税申告書.pdf")) {
//		        		byte[] buffer = new byte[4096];
//		        		int bytesRead;
//		        		while ((bytesRead = t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu().read(buffer)) != -1) {
//		        			outputStream.write(buffer, 0, bytesRead);
//		        		}
//		        	}
//		        }


		        if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml())) {
			        if (t_etax_jieguoExBean.getHtml().contains("skip") == false) {

			        	//html转PDF报错对应
			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml());
			        	Elements mainElements = doc.select("main");
			        	for (Element main : mainElements) {
			        		main.removeAttr("inert");
			        	}

			        	t_etax_jieguoExBean.setHtml(doc.html());



//			        	// 方式1：根据name属性（最精准）
//			        	String uketsukeDate = doc.select("td[name=content_uketsuke_date]").text();
//
//			        	System.out.println("受付日時: " + uketsukeDate);
//
//			            Element td = doc.selectFirst("td.ttl:containsOwn(課税期間)");
//			            if (td == null) return "";
//
//			            Element tr1 = td.parent();
//			            Element tr2 = tr1.nextElementSibling();
//
//			            t_etax_jieguoExBean.setUketsuke_datetime(uketsukeDate);
//			            t_etax_jieguoExBean.setKazei_kikan(tr1.child(1).text() + "," + tr2.child(0).text());
//
//			            t_etax_jieguoDao.Update_uketsuke_datetime_AND_kazei_kikan(yyyymmdd_count, t_etax_jieguoExBean);


//			            String event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
//			            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
//			            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
//			            if (!StringUtils.isEmpty(event + taxable_amount + total_tax_amount)) {
//			                t_etax_jieguoDao.Update_event_taxable_amount_total_tax_amount(yyyymmdd_count, event, taxable_amount, total_tax_amount);
//			            }



			        	//TODO
			        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + CompanyName_Englis + "_受信通知.html"))) {
			        		writer.write(t_etax_jieguoExBean.getHtml());
			        	}

			        	String fileNameXTX = t_etax_account_infoExBean.getTatujin_id() + "_" + t_etax_account_infoExBean.getCompanyName_Chinese() + ".xtx";
			        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + fileNameXTX))) {
			        		writer.write(t_etax_jieguoExBean.getEtax_xtx());




							String xml = t_etax_jieguoExBean.getEtax_xtx(); // 你的XML字符串

							DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
							factory.setNamespaceAware(true); // ⚠ 必须开启

							DocumentBuilder builder = factory.newDocumentBuilder();
							org.w3c.dom.Document docXML = builder.parse(
									new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

							String kazeiStandard = FuncUtilsXML.getTagValue(docXML, "AAJ00010");
							String sashihikiZeigaku = FuncUtilsXML.getTagValue(docXML, "AAJ00100");
							String totalShohizei = FuncUtilsXML.getTagValue(docXML, "AAK00130");

							System.out.println("课税标准额: " + kazeiStandard);
							System.out.println("差引税额: " + sashihikiZeigaku);
							System.out.println("应缴消费税总额: " + totalShohizei);

							if (StringUtils.isEmpty(kazeiStandard)) {
								kazeiStandard = "0";
							}
							if (StringUtils.isEmpty(sashihikiZeigaku)) {
								sashihikiZeigaku = "0";
							}
						    t_etax_jieguoExBean.setTaxable_amount(kazeiStandard);
				            t_etax_jieguoExBean.setSashihiki_tax_amount(sashihikiZeigaku);
						    t_etax_jieguoExBean.setTotal_tax_amount(totalShohizei);

							XPath xpath = XPathFactory.newInstance().newXPath();
							String idValue = xpath.evaluate("//*[local-name()='Signature']/@Id", docXML);
						    String digits = idValue.replaceAll("\\D", "");

						    String timePart = digits.substring(digits.length() - 17);

						    DateTimeFormatter inputFmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
						    DateTimeFormatter outputFmt = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");

						    LocalDateTime dt = LocalDateTime.parse(timePart, inputFmt);


				            t_etax_jieguoExBean.setUketsuke_datetime(dt.format(outputFmt));
//				            t_etax_jieguoExBean.setKazei_kikan("");

				            System.out.println("申告日期: " + t_etax_jieguoExBean.getUketsuke_datetime());



				            String startDate = FuncUtilsXML.parseWarekiDate(docXML, "ATC00120");
				            String endDate   = FuncUtilsXML.parseWarekiDate(docXML, "ATC00130");

				            System.out.println("开始日期: " + startDate);
				            System.out.println("结束日期: " + endDate);
				            t_etax_jieguoExBean.setKazei_kikan(startDate + "-" + endDate);

			        		t_etax_jieguoDao.Update_from_xtx(yyyymmdd_count, t_etax_jieguoExBean);
			        	}

			        	//TODO
			        	String fileNamePDF = t_etax_account_infoExBean.getTatujin_id() + "_" + t_etax_account_infoExBean.getCompanyName_Chinese() + "_受信通知.pdf";
			        	FuncUtilsHtml.htmlToPdf(directoryPath + "/" + CompanyName_Englis + "_受信通知.html", directoryPath + "/" + fileNamePDF);






//			        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_受信通知.pdf")) {
//			            PdfRendererBuilder builder = new PdfRendererBuilder();
//			            builder.useFastMode();
//			            builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html"));//, "UTF-8"
//			            builder.toStream(os);
//			            builder.run();
//			        }


			        }

		        }


//		        File FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html");
//		        FileHtml.delete();
//		        FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html");
//		        FileHtml.delete();

			}



		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.info("end");
		return directoryPath;
	}

	public String exe_zhongjian_shengao(String sourceFilePath, String chuli_type, int countS, int countE, String line) {
		String directoryPath ="";
		try {
			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_zhongjian_shengaoDao t_etax_zhongjian_shengaoDao = new t_etax_zhongjian_shengaoDao();
			t_xiaofeishui_shengaoDao t_xiaofeishui_shengaoDao = new t_xiaofeishui_shengaoDao();


			/*
			 * 登録データ準備
			 */
			int count = 0;
			for (int k = countS; k <= countE; k++) {

				String yyyymmdd_count= line;
				++count;
				logger.info("处理个数: " + count + "個目 " + yyyymmdd_count);


//				String yyyymmdd_count = t_etax_account_resDao.selecByPDSK(PDSK);

				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_zhongjian_shengaoDao.select_jietuo_by_bangou(t_etax_account_infoExBean.getBangou(), chuli_type);
				yyyymmdd_count = t_etax_jieguoExBean.getYyyymmdd_count();
				logger.info("yyyymmdd_count : " + yyyymmdd_count);

				String CompanyName_Englis = t_etax_account_infoExBean.getCompanyName_English();
				String getCompanyName_Chinese = t_etax_account_infoExBean.getCompanyName_Chinese();
//				yyyymmdd_count = yyyymmdd_count.replace("PDSK23", "PDSK") + generateSixDigitRandomNumber();


				directoryPath = sourceFilePath + "/" + yyyymmdd_count + "_" + getCompanyName_Chinese;
				// 将字符串路径转换为Path对象
				Path path = Paths.get(directoryPath);
				// 创建目录，包括任何必要但不存在的父目录
				Files.createDirectories(path);

				if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml_qr())) {
					if (t_etax_jieguoExBean.getHtml_qr().contains("skip") == false) {

						//html转PDF报错对应
//			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml_qr());
//			            Elements inputs = doc.select("input");
//			            for (Element input : inputs) {
//			                if ("hidden".equals(input.attr("type"))) {
//			                    input.remove();
//			                }
//			            }
//			            t_etax_jieguoExBean.setHtml_qr(doc.html().replace("<br>", "<br />").replace("type=\"text/css\">", "type=\"text/css\" />"));


				        try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"))) {
				            writer.write(t_etax_jieguoExBean.getHtml_qr());
				        }
//				        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.pdf")) {
//				        	PdfRendererBuilder builder = new PdfRendererBuilder();
//				        	builder.useFastMode();
//				        	builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html"));
//				        	builder.toStream(os);
//				        	builder.run();
//				        }





					}
				}

		        if (t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu() != null) {
		        	try (FileOutputStream outputStream = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_提出消費税申告書.pdf")) {
		        		byte[] buffer = new byte[4096];
		        		int bytesRead;
		        		while ((bytesRead = t_etax_jieguoExBean.getPdf_xiaofeishui_shengaoshu().read(buffer)) != -1) {
		        			outputStream.write(buffer, 0, bytesRead);
		        		}
		        	}
		        }


		        if (!StringUtils.isEmpty(t_etax_jieguoExBean.getHtml())) {
			        if (t_etax_jieguoExBean.getHtml().contains("skip") == false) {

			        	//html转PDF报错对应
			        	Document doc = Jsoup.parse(t_etax_jieguoExBean.getHtml());
			        	Elements mainElements = doc.select("main");
			        	for (Element main : mainElements) {
			        		main.removeAttr("inert");
			        	}
			        	t_etax_jieguoExBean.setHtml(doc.html().replace("<br>", "<br />").replace("type=\"text/css\">", "type=\"text/css\" />"));


//			            String event = FuncUtilsHtml.getHtmlBykey(doc, "種目");
//			            String taxable_amount = FuncUtilsHtml.getHtmlBykey(doc, "課税標準額");
//			            String total_tax_amount = FuncUtilsHtml.getHtmlBykey(doc, "消費税及び地方消費税の合計（納付又は還付）税額");
//			            if (!StringUtils.isEmpty(event + taxable_amount + total_tax_amount)) {
//			                t_etax_jieguoDao.Update_event_taxable_amount_total_tax_amount(yyyymmdd_count, event, taxable_amount, total_tax_amount);
//			            }


			        	// 取得包含 "中間申告対象期間" 的 <td>
			        	Elements tds = doc.select("td");
			        	Element targetTd = null;
			        	for (Element td : tds) {

//			                System.out.println("" + td.text().trim());
			        	    if (td.text().trim().equals("中間申告対象期間")) {
			        	        targetTd = td;
			        	        break;
			        	    }
			        	}

        		        String dateFrom = ""; // 自令和 7年 1月 1日
        		        String dateTo = ""; // 至令和 7年 6月30日

			        	if (targetTd != null) {
			        		Element parent = targetTd.parent();

			        		// 1. 找到 <font> 中精确匹配“中間申告対象期間”的元素
			        		Element targetFont = parent.select("font").stream()
			        		    .filter(e -> e.ownText().trim().equals("中間申告対象期間"))
			        		    .findFirst()
			        		    .orElse(null);

			        		if (targetFont != null) {
			        		    // 2. 获取它父级的 <td>（左边单元格）
			        		    Element labelTd = targetFont.closest("td");

			        		    // 3. 获取它右边的 <td>（嵌套 table 的 td）
			        		    Element valueTd = labelTd.nextElementSibling();

			        		    // 4. 获取里面嵌套的两个日期的 td
			        		    Elements dateTds = valueTd.select("td[align=right]");
			        		    if (dateTds.size() >= 2) {
			        		         dateFrom = dateTds.get(0).text().trim(); // 自令和 7年 1月 1日
			        		         dateTo = dateTds.get(1).text().trim(); // 至令和 7年 6月30日


			        		         dateFrom = FuncUtils.convertJapaneseEraDate(dateFrom); // 自令和 7年 1月 1日
			        		         dateTo = FuncUtils.convertJapaneseEraDate(dateTo); // 至令和 7年 6月30日

//			        		        System.out.println("开始日期: " + FuncUtils.convertJapaneseEraDate(date1));
//			        		        System.out.println("结束日期: " + FuncUtils.convertJapaneseEraDate(date2));
			        		    }
			        		}

			        	} else {
			        		System.out.println("没有找到目标字体标签。");
			        	}




			        	// 取得包含 "消費税及び地方消費税の合計納付税額" 的 <td>
			        	for (Element td : tds) {
//			                System.out.println("" + td.text().trim());
			        	    if (td.text().trim().equals("消費税及び地方消費税の合計納付税額")) {
			        	        targetTd = td;
			        	        break;
			        	    }
			        	}

        		        String heji_nafu_shuie = "";

			        	if (targetTd != null) {
			        		Element parent = targetTd.parent();

			        		// 1. 找到 <font> 中精确匹配“中間申告対象期間”的元素
			        		Element targetFont = parent.select("font").stream()
			        		    .filter(e -> e.ownText().trim().equals("消費税及び地方消費税の合計納付税額"))
			        		    .findFirst()
			        		    .orElse(null);

			        		if (targetFont != null) {
			        		    // 2. 获取它父级的 <td>（左边单元格）
			        		    Element labelTd = targetFont.closest("td");

			        		    // 3. 获取它右边的 <td>（嵌套 table 的 td）
			        		    Element valueTd = labelTd.nextElementSibling();

			        		    // 4. 获取里面嵌套的两个日期的 td
			        		    Elements dateTds = valueTd.select("td[align=right]");
			        		    if (dateTds.size() >= 1) {
			        		    	heji_nafu_shuie = dateTds.get(0).text().trim().replace(",", "").replace("円", "");

//			        		        System.out.println("开始日期: " + FuncUtils.convertJapaneseEraDate(date1));
//			        		        System.out.println("结束日期: " + FuncUtils.convertJapaneseEraDate(date2));
			        		    }
			        		}

			        	} else {
			        		System.out.println("没有找到目标字体标签。");
			        	}

			            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");
			            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

			            LocalDate date = LocalDate.parse(t_etax_jieguoExBean.getYuukou_kigen(), inputFormatter);
			            String outputDate = date.format(outputFormatter);
			        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(directoryPath + "/" + getCompanyName_Chinese + "_"+ dateFrom + "_"+ dateTo + "_"+ heji_nafu_shuie +".html"))) {
			        		writer.write(t_etax_jieguoExBean.getHtml());
			        	}
//			        try (OutputStream os = new FileOutputStream(directoryPath + "/" + CompanyName_Englis + "_受信通知.pdf")) {
//			            PdfRendererBuilder builder = new PdfRendererBuilder();
//			            builder.useFastMode();
//			            builder.withFile(new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html"));//, "UTF-8"
//			            builder.toStream(os);
//			            builder.run();
//			        }


			        }

		        }


//		        File FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_受信通知.html");
//		        FileHtml.delete();
//		        FileHtml = new File(directoryPath + "/" + CompanyName_Englis + "_国税QR支付.html");
//		        FileHtml.delete();

			}



		} catch (Throwable e) {
			e.printStackTrace();
		}

		logger.info("end");
		return directoryPath;
	}


	public static int generateSixDigitRandomNumber() {
		Random random = new Random();
		// 生成一个在100000到999999之间的随机数
		return 100000 + random.nextInt(900000);
	}

}
