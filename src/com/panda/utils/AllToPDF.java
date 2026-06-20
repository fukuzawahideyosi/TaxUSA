package com.panda.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentBase;

public class AllToPDF {

	private static Logger logger = Logger.getLogger(AllToPDF.class.toString());
	//jpg图片转换成pdf
	public void imageToPDF(String fromPath, String toPath) throws Exception {
		BufferedImage img = ImageIO.read(new File(fromPath));
		FileOutputStream fos = new FileOutputStream(toPath);
		Document doc = new Document(null, 0, 0, 0, 0);
		doc.setPageSize(new Rectangle(img.getWidth(), img.getHeight()));
		Image image = Image.getInstance(fromPath);
		PdfWriter.getInstance(doc, fos);
		doc.open();
		doc.add(image);
		doc.close();
		fos.close();
	}

	public void wordToPDF(String fromPath, String toPath) throws Exception {
		logger.info("wordToPDF start");
		//实例化Document类的对象
		com.spire.doc.Document doc = new com.spire.doc.Document();
//		doc.getSettings().setPdfCustomFontName("Noto Sans CJK");
//		doc.getSettings().setPdfEmbedStandardFont(true);
		//加载Word
		doc.loadFromFile(fromPath);
		//保存为PDF格式
		doc.saveToFile(toPath, com.spire.doc.FileFormat.PDF);
		logger.info("wordToPDF end");

	}

	public void wordToPDF2(String fromPath, String toPath) throws Exception {
		logger.info("wordToPDF2 start");
	    // 加载 Word 文档
		com.aspose.words.Document doc = new com.aspose.words.Document(fromPath);
	    // 将 Word 文档保存为 PDF
	    doc.save(toPath, com.aspose.words.SaveFormat.PDF);
		logger.info("wordToPDF2 end");

	}



	public void mergePDF(List<String> fileNames, String toPath) throws Exception {

		List<InputStream> sources = new ArrayList<InputStream>();

		//文件循环
		for (String fileName : fileNames) {
			File file_first = new File(fileName);
			InputStream is0 = new FileInputStream(file_first);
			sources.add(is0);
		}

		FileOutputStream mergedPDFOutputStream = new FileOutputStream(toPath);
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		pdfMerger.addSources(sources);
		pdfMerger.setDestinationStream(mergedPDFOutputStream);

		//PDFのMerge出力
		pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());

	}

	public void mergePDFp10(List<String> fileNames, String toPath) throws Exception {
		String[] strArr = fileNames.toArray(new String[fileNames.size()]);
		//				//結合する PDF ファイルのパスを取得し、String 配列に格納する
		//				String[] filesList = new String[] {
		//						"C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\MergerPDF\\合并1\\temp\\１１.pdf",
		//						"C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\MergerPDF\\合并1\\temp\\１２.pdf",
		//						"C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\MergerPDF\\合并1\\temp\\１３.pdf",
		//						"C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\MergerPDF\\合并1\\temp\\１panda.pdf"
		//						};

		//これらのドキュメントを結合する
		PdfDocumentBase doc1 = PdfDocument.mergeFiles(strArr);

		//結合されたファイルを保存する
		doc1.save(toPath, FileFormat.PDF);

		try {
			doc1.getPages().get(9);
			File file = new File(toPath);
			String[] fileName = file.getName().split("_");
			toPath = file.getPath().replace(file.getName(), "")
					+ file.getName().replace(fileName[0], fileName[0] + "pageNo10");
			//結合されたファイルを保存する
			doc1.save(toPath, FileFormat.PDF);
			file.delete();
		} catch (Exception e) {
		}

		//		//创建 PdfDocument 类的对象
		//		PdfDocument doc = new PdfDocument();
		//		//载入PDF文档
		//		doc.loadFromFile(toPath);
		//
		//			//获取文档的第一页
		//			PdfPageBase page = doc.getPages().get(0);
		//
		//			//搜索文本”心理治疗师“
		//			PdfTextFindCollection collection = page.findText("Warning", false);
		//
		//			//指定替换文本”心理医生“
		//			String newText = "";
		//
		//			//创建 PdfTrueTypeFont 类的对象以设置字体
		//			java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, 14);
		//			PdfTrueTypeFont trueTypeFont = new PdfTrueTypeFont(font);
		//			//		PdfTrueTypeFont font = new PdfTrueTypeFont(new Font("华文中宋",  Font.BOLD, 12));
		//
		//			for (Object findObj : collection.getFinds()) {
		//				PdfTextFind find = (PdfTextFind) findObj;
		//
		//				//获取文本在页面中的范围
		//				Rectangle2D.Float rec = (Rectangle2D.Float) find.getBounds();
		//				page.getCanvas().drawRectangle(PdfBrushes.getWhite(), rec);
		//
		//				//绘制文本
		//				page.getCanvas().drawString(newText, trueTypeFont, PdfBrushes.getBlue(), rec.getX(), rec.getY() - 3);
		//			}
		//
		//			String result = "C:\\Users\\Administrator\\Desktop\\法人番号登録申請BPS\\MergerPDF\\合并2\\ALLNEW.pdf";
		//
		//			//保存文档
		//			doc.saveToFile(result, FileFormat.PDF);

	}

	//	public void wordToPDF(String fromPath, String toPath) throws Exception {
	//
	//		FileInputStream fileInputStream = new FileInputStream(fromPath);
	//		XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
	//		PdfOptions pdfOptions = PdfOptions.create();
	//		FileOutputStream fileOutputStream = new FileOutputStream(toPath);
	//		PdfConverter.getInstance().convert(xwpfDocument,fileOutputStream,pdfOptions);
	//		fileInputStream.close();
	//		fileOutputStream.close();
	//
	//	}

	//
	//	 public static boolean getLicense() {
	//	        boolean result = false;
	//	        try {
	//	            InputStream is = Test.class.getClassLoader().getResourceAsStream("license.xml"); // license.xml应放在..WebRootWEB-INFclasses路径下
	//	            License aposeLic = new License();
	//	            aposeLic.setLicense(is);
	//	            result = true;
	//	        } catch (Exception e) {
	//	            e.printStackTrace();
	//	        }
	//	        return result;
	//	    }
	//	    public static void doc2pdf(String inPath, String outPath) {
	//	        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
	//	            return;
	//	        }
	//	        try {
	//	            long old = System.currentTimeMillis();
	//	            File file = new File(outPath); // 新建一个空白pdf文档
	//	            FileOutputStream os = new FileOutputStream(file);
	//	            Document doc = new Document(inPath); // Address是将要被转化的word文档
	//	            doc.save(os, SaveFormat.PDF);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
	//	                                         // EPUB, XPS, SWF 相互转换
	//	            long now = System.currentTimeMillis();
	//	            logger.info("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
	//	        } catch (Exception e) {
	//	            e.printStackTrace();
	//	        }
	//	    }

}