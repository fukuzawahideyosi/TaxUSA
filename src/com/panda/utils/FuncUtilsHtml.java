package com.panda.utils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class FuncUtilsHtml {

	private static Logger logger = Logger.getLogger(FuncUtilsHtml.class.toString());

	public static String getHtmlBykey(Document doc, String key) {
    	String amountText ="";
		Element Element = doc.select("td:contains("+key+")").next().first();
		if (Element != null) {
		    amountText = Element.text().replace("円", "").replace(" ", "").replace(",", "");
		} else {
			logger.warn("未找到：" + key);
		}
		return amountText;
	}

	public static void htmlToPdf(String htmlPath, String pdfPath) throws Exception {


		// 获取系统类型属性
		String osName = System.getProperty("os.name");
		// 您可以根据不同的系统类型执行不同的操作
		if (osName.toLowerCase().contains("windows")) {
//			logger.info("这是Windows系统");
//			logger.debug(data[0]);
		} else if (osName.toLowerCase().contains("linux")) {
//			logger.info("这是Linux系统");

		}

	    // 1️⃣ 读取 HTML
	    String html = Files.readString(
	            new File(htmlPath).toPath(),
	            StandardCharsets.UTF_8
	    );

	    // 2️⃣ 修正 HTML5 → XHTML（防止 meta / link / input 报错）
	    html = html.replaceAll("<meta([^>]*?)>", "<meta$1 />");
	    html = html.replaceAll("<link([^>]*?)>", "<link$1 />");
	    html = html.replaceAll("<img([^>]*?)>", "<img$1 />");
	    html = html.replaceAll("<br>", "<br />");
	    html = html.replaceAll("<hr>", "<hr />");
	    html = html.replaceAll("<input([^>]*?)>", "<input$1 />");


	    String fontStyle = "";
		// 您可以根据不同的系统类型执行不同的操作
		if (osName.toLowerCase().contains("windows")) {
//			logger.info("这是Windows系统");
//			logger.debug(data[0]);
			fontStyle =
		            "<style>\n" +
		            "body, table, td, th, div, span, p, li {\n" +
		            "    font-family: \"MS Gothic\" !important;\n" +
		            "}\n" +
		            "</style>\n";
		} else if (osName.toLowerCase().contains("linux")) {
//			logger.info("这是Linux系统");
			fontStyle =
		            "<style>\n" +
		            "body, table, td, th, div, span, p, li {\n" +
		            "    font-family: \"NotoSansCJK\" !important;\n" +
		            "}\n" +
		            "</style>\n";

		}

	    // 优先插入到 <head> 内
	    if (html.toLowerCase().contains("<head")) {
	    	html = html.replaceFirst(
	    			"(?i)(<head[^>]*>)",
	    			"$1\n" + fontStyle
	    			);
	    } else {
	    	// 没有 <head> 就直接包一个
	    	html = "<head>\n" + fontStyle + "</head>\n" + html;
	    }


	    try (OutputStream os = new FileOutputStream(pdfPath)) {
	        PdfRendererBuilder builder = new PdfRendererBuilder();

	     // A4: 210 × 297 mm → 70%
	        builder.useDefaultPageSize(
	                210f * 1.2f,
	                297f * 1.2f,
	                PdfRendererBuilder.PageSizeUnits.MM
	        );

			// 您可以根据不同的系统类型执行不同的操作
			if (osName.toLowerCase().contains("windows")) {
//				logger.info("这是Windows系统");
//				logger.debug(data[0]);


		        // 3️⃣ 嵌入日文字体（解决 ### 问题）
		        builder.useFont(
		                new File("C:/Windows/Fonts/msgothic.ttc"),
		                "MS Gothic",
		                400,
		                PdfRendererBuilder.FontStyle.NORMAL,
		                true   // ← 这一位非常重要（嵌入 + 子集）
		        );

			} else if (osName.toLowerCase().contains("linux")) {
//				logger.info("这是Linux系统");
				builder.useFont(
				        new File("/usr/share/fonts/google-noto-cjk/NotoSansCJK-Regular.ttc"),
				        "NotoSansCJK",
				        400,
				        PdfRendererBuilder.FontStyle.NORMAL,
				        true   // 嵌入 + 子集（必须）
				);
			}

	        builder.useFastMode();

	        // 4️⃣ 设置 HTML + baseUri（CSS / 图片路径用）
	        builder.withHtmlContent(
	                html,
	                new File(htmlPath).getParentFile().toURI().toString()
	        );

	        builder.toStream(os);
	        builder.run();

	        Files.deleteIfExists(Path.of(htmlPath));

	    }
	}


}