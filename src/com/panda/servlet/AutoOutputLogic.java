package com.panda.servlet;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;

/**
 * 社保计算
 */
@WebServlet("/AutoOutputLogic")
public class AutoOutputLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(AutoOutputLogic.class.toString());

	private static final long serialVersionUID = 1L;

	HashMap<String, String> HashMapKeyValue = new HashMap<String, String>();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AutoOutputLogic() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			logger.info("start");

			//	    //获取当前文件所在的路径
			//	    String localPath = this.getClass().getResource("").getPath();
			//	    logger.info("localPath = " + localPath);
			//	    //localPath = /C:/work/idea-WorkSpace/my-demo/demo-file/target/classes/com/zgd/demo/file/path/+
			//
			//
			//
			//
			//	    Properties prop = new Properties();
			//	    prop.load(this.getClass().getResourceAsStream("/import/合同会社_一人_持分全部譲渡_代表社員変更_就任承諾書_SAMPLE(1).docx"));
			//	    InputStream is = this.getClass().getResourceAsStream("/import/合同会社_一人_持分全部譲渡_代表社員変更_就任承諾書_SAMPLE(1).docx");
			//

			//		File file = new File("/import/合同会社_一人_持分全部譲渡_代表社員変更_就任承諾書_SAMPLE(1).docx");
			//		if (!file.exists()) {
			//			logger.debug("not fond input!");
			//		}

			for (int i = 1; i <= 50; i++) {
				String key = request.getParameter("key" + i);
				String value = request.getParameter("value" + i);
				HashMapKeyValue.put(key, value);
				logger.debug("[" + i + "] ：" + key + " ：" + value);

			}
			request.getParameter("key20");
			String file = "C:\\Users\\Administrator\\Desktop\\PandaServiceTools\\IMPORT01_合同会社_一人_持分全部譲渡_代表社員変更\\インボイス登録一式.docx";
			String newFile = "C:\\Users\\Administrator\\Desktop\\PandaServiceTools\\IMPORT01_合同会社_一人_持分全部譲渡_代表社員変更\\インボイス登録一式2.docx";

//			file = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/classes/import/インボイス登録一式.docx";
//			newFile = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/classes/outport/インボイス登録一式2.docx";

			readWord(file, newFile);

			//设置返回信息数据
			response.setContentType(
					"application/vnd.openxmlformats-officedocument.wordprocessingml.document;charset=UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("content-disposition", "attachment;filename=" + "" + "123.docx");

			//1.获取输入流
			FileInputStream is = new FileInputStream(newFile);

			//2.获取输出流,对拷
			ServletOutputStream os = response.getOutputStream();
			IOUtils.copy(is, os);

			//3.释放流
			os.close();
			is.close();

			//            // path是指欲下载的文件的路径。
			//            File file1 = new File(newFile);
			//            // 取得文件名。
			//            String filename = file1.getName();
			//            // 取得文件的后缀名。
			//            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
			//
			//            // 以流的形式下载文件。
			//            InputStream fis = new BufferedInputStream(new FileInputStream(newFile));
			//            byte[] buffer = new byte[fis.available()];
			//            fis.read(buffer);
			//            fis.close();
			//            // 清空response
			//            response.reset();
			//            // 设置response的Header
			//            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
			//            response.addHeader("Content-Length", "" + file1.length());
			//            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			//            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			//            toClient.write(buffer);
			//            toClient.flush();
			//            toClient.close();

			//		    logger.info("ajax download file");
			//		    File file1 = new File(newFile);
			//
			//		    response.setContentType("application/octet-stream");
			//		    response.setHeader("Content-Disposition","attachment;filename=" + "123.docx");
			//		    response.setContentLength((int) file1.length());
			//
			//		    FileInputStream fis = null;
			//		    try {
			//		        fis = new FileInputStream(file1);
			//		        byte[] buffer = new byte[128];
			//		        int count = 0;
			//		        while ((count = fis.read(buffer)) > 0) {
			//		        	response.getOutputStream().write(buffer, 0, count);
			//		        }
			//		    } catch (Exception e) {
			//		        e.printStackTrace();
			//		    } finally {
			//		    	response.getOutputStream().flush();
			//		    	response.getOutputStream().close();
			//		        fis.close();
			//		    }

			HashMapKeyValue = new HashMap<String, String>();
			logger.info("end");

		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void readWord(String file, String newFile) {
		try {

			readWordText2(file, newFile);

			deleteDocxFirstText(newFile);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readWordText2(String file, String newFile) {

		//加载文档
		Document doc = new Document();
		doc.loadFromFile(file);

		for (String key : HashMapKeyValue.keySet()) {
			if (null != key && "".equals(key) == false) {
				String value = HashMapKeyValue.get(key);

				//要替换第一个出现的指定文本，只需在替换前调用setReplaceFirst方法来指定只替换第一个出现的指定文本
				//doc.setReplaceFirst(true);

				//调用方法用新文本替换原文本内容
				doc.replace(key, value, false, true);
			}

		}

		//保存文档
		doc.saveToFile(newFile, FileFormat.Docx_2013);
		doc.dispose();

	}

	/**
	  *	删除Word.docx第一行
	  * @param path 需要删除首行警示语的doc路径
	  */
	public static void deleteDocxFirstText(String path) {
		try {
			FileInputStream inputStream = new FileInputStream(path);
			XWPFDocument document = new XWPFDocument(inputStream);
			inputStream.close();
			XWPFParagraph toDelete = document.getParagraphs().stream()
					.filter(p -> StringUtils.equalsIgnoreCase(
							"Evaluation Warning: The document was created with Spire.Doc for JAVA.",
							p.getParagraphText()))
					.findFirst().orElse(null);
			if (toDelete != null) {
				document.removeBodyElement(document.getPosOfParagraph(toDelete));
				OutputStream fos = new FileOutputStream(path);
				document.write(fos);
				fos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}