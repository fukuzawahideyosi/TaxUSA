package com.panda.batch;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;

import com.panda.utils.FuncUtils;

public class PDFToImageToPDF {


	private static Logger logger = Logger.getLogger(PDFToImageToPDF.class.toString());

	public static void main(String[] args) {
		if (args.length == 0) {
			args = new String[1];
			args[0] = "K:\\共享云端硬盘\\ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社\\５０４　backup\\0326オウテキ処理 - 抓取申告结果 - 删除税理士信息";

		}


		String path = "C:\\Users\\Administrator\\Desktop\\PDSK20240305数据不对重新抓";
		path = "C:\\Users\\Administrator\\Desktop\\STBL";
		path = "C:\\Users\\Administrator\\Desktop\\02XXオウテキ処理TEST\\HGZJ 待处理 删除税理士信息";
		path = "K:\\共享云端硬盘\\ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社\\５０４　backup\\0514オウテキ処理 - 抓取申告结果 - 删除税理士信息";


		pdfToImageToPDF(path, "shuilishi_del_YES");
	}

	public static void pdfToImageToPDF(String path, String hidden_shuilishi_del_type) {
		try {


			File[] files = FuncUtils.listFilesInFolder(path);
			int count = 0;
            for (File file : files) {
                String pdfFileName = file.getName();
            	if (pdfFileName.contains("提出消費税申告書.pdf") || pdfFileName.contains("消費税及び地方消費税申告(一般・法人).pdf")) {

            	} else {
            		continue;

            	}


    			logger.info("处理数量：" + ++count);

//    			logger.info("file：" + file);
    			PDDocument document = PDDocument.load(file);
    			int lastPageNumber = document.getNumberOfPages();
    			if ("shuilishi_del_YES_0".equals(hidden_shuilishi_del_type)) {

    			} else {
    				document.removePage(lastPageNumber - 1);

    			}

    			/*
    			 *
    			 */
    			File directory = new File(file.getParent() + "/pdf_to_image");
    			FuncUtils.deleteFolder(directory);
    			if (!directory.exists() || !directory.isDirectory()) {
    				directory.mkdirs();
    			}

    			PDFRenderer renderer = new PDFRenderer(document);
    			int dpi = 200; // Adjust the dpi as needed
    			int pageCount = document.getNumberOfPages();

    			for (int i = 0; i < pageCount; i++) {
    				BufferedImage image = renderer.renderImageWithDPI(i, dpi);
    				File outputFile = new File(directory.getPath() + "/output" + i + ".png");
    				ImageIO.write(image, "png", outputFile);
    			}
    			document.close();

    			/*
    			 *
    			 */
    			// 加载原始图像
    			File inputFile = new File(directory.getPath() + "/output0.png");
    			BufferedImage originalImage = ImageIO.read(inputFile);

    			// 定义选定区域的坐标和尺寸
//    			int x = 1800; // 选定区域左上角的X坐标
//    			int y = 2900; // 选定区域左上角的Y坐标
//    			int width = 530; // 选定区域的宽度
//    			int height = 230; // 选定区域的高度


    			int x = 1190; // 选定区域左上角的X坐标
    			int y = 1936; // 选定区域左上角的Y坐标
    			int width = 360; // 选定区域的宽度
    			int height = 146; // 选定区域的高度

    			// 创建一个与原始图像相同尺寸的新图像
    			BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);


    			// 将不在选定区域内的像素从原始图像复制到新图像中
    			for (int i = 0; i < originalImage.getHeight(); i++) {
    				for (int j = 0; j < originalImage.getWidth(); j++) {
    					// 检查像素是否在选定区域内
    					if (j >= x && j < x + width && i >= y && i < y + height) {
    						// 如果像素在选定区域内，则将其颜色设置为白色
    						resultImage.setRGB(j, i, Color.WHITE.getRGB());
//    						resultImage.setRGB(j, i, Color.green.getRGB());
    					} else {
    						// 如果像素不在选定区域内，则将其复制到新图像中
    						resultImage.setRGB(j, i, originalImage.getRGB(j, i));
    					}

    				}
    			}

    			// 将结果图像保存为文件
    			File outputFile = new File(directory.getPath() + "/output0.png");
    			ImageIO.write(resultImage, "png", outputFile);
    			logger.info("成功编辑图像.");

    			/*
    			 *
    			 */
    			// 指定包含图片的文件夹路径
    			File folder = new File(directory.getPath());

    			// 获取文件夹中所有图片文件，并按名称排序
    			File[] imageFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));
    			Arrays.sort(imageFiles);

    			// 创建一个新的PDF文档
    			document = new PDDocument();

    			// 遍历图片文件并将每张图片添加到PDF文档中
    			for (File imageFile : imageFiles) {
    				BufferedImage image = ImageIO.read(imageFile);
    				PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
    				document.addPage(page);

    				// 将图片绘制到页面上
    				PDImageXObject pdImage = LosslessFactory.createFromImage(document, image);
    				try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
    					contentStream.drawImage(pdImage, 0, 0);
    				}
    			}



    			// 使用PDFBox提供的方法压缩PDF
                document.setAllSecurityToBeRemoved(true);



//    			// 保存PDF文档
//              String pdfFileName = file.getName();
//    			String fileExtension = FuncUtils.getFileExtension(pdfFileName);
//    			pdfFileName = pdfFileName.replace("." + fileExtension, "new.pdf");
//    			document.save(file.getParent() + "/" + pdfFileName);



    			document.save(file.getPath());
    			document.close();

    			logger.info("成功将文件夹下所有图片转换为PDF.");

    			FuncUtils.deleteFolder(directory);
            }



			/*


E:
cd E:\pleiades\2022-12\java\11\bin
java -cp "E:\pleiades\PandaServiceMA_lib\*";"E:\pleiades\PandaServiceMA_lib\PandaServiceMA.jar" com.panda.batch.PDFToImageToPDF "C:\Users\Administrator\Desktop\ningbofenghuazhierchengjiankangkejiyouxiangongsi_提出消費税申告書.pdf"


			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 // ページから画像XObjectを作成
    private static PDImageXObject getImageXObjectFromPage(PDDocument document, PDPage page) throws IOException {
        PDResources resources = page.getResources();
        for (COSName name : resources.getXObjectNames()) {
            PDXObject xobject = resources.getXObject(name);
            if (xobject instanceof PDImageXObject) {
                return (PDImageXObject) xobject;
            }
        }
        return null;
    }

}
