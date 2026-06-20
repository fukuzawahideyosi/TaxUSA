package com.panda.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.bean.t_etax_account_resExBean;
import com.panda.chrome.pandaWebDriver;
import com.panda.dao.EtaxDao;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_account_xiaofeishuiDao;
import com.panda.utils.AllToPDF;
import com.panda.utils.FuncUtils;
import com.panda.utils.FuncUtilsAiEtax;

@WebServlet("/SearchEtaxLogic")
public class SearchEtaxLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SearchEtaxLogic.class.toString());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//TODO
		//        // 获取本地计算机上的字体环境
		//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		//
		//        // 获取可用字体的名称
		//        String[] fontNames = ge.getAvailableFontFamilyNames();
		//
		//        // 输出所有可用字体的名称
		//        for (String fontName : fontNames) {
		//            logger.info(fontName);
		//        }

		logger.info("start");

		try {
			resp.setContentType("text/html; charset=UTF-8");
			PrintWriter out = resp.getWriter();
			HttpSession session = req.getSession();
			String user_id = req.getParameter("license");
			String filter = req.getParameter("filter");
			String web = req.getParameter("web");


			String maxNo = req.getParameter("maxNo");
			String value_shengcheng_file_type = req.getParameter("value_shengcheng_file_type");

			session.setAttribute("web", web);
			session.setAttribute("license", user_id);

			/*
			 * license確認
			 */
			String pw = req.getParameter("pw");
			session.setAttribute("pw", pw);
			FuncUtils FunctionUtils = new FuncUtils();
			User_infoDao LicenseDao = new User_infoDao();
			User_infoBean User_infoBean = LicenseDao.select(user_id);
			session.setAttribute("User_infoBean", User_infoBean);

			String license = User_infoBean.getLicense_yyyymmdd();
			logger.info(license);
			if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == false) {
				logger.debug("PandaServiceTools → License invalid");

				out.write("PandaServiceMA → License invalid");
				return;
			}
			if ("AutoExe".equals(web)) {

				try {

					String output_file = req.getParameter("output_file");
					String[] output_fileList = output_file.split(",");

					String folderPathFileData = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/fileData";
					String outportPath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/output/ftpalist/temp";
					String ftpPath = "/var/ftp/ftpalist";
					// 获取系统类型属性
					String osName = System.getProperty("os.name");
					// 您可以根据不同的系统类型执行不同的操作
					if (osName.toLowerCase().contains("windows")) {
						logger.info("这是Windows系统");
						// 在Windows系统上执行特定操作
						folderPathFileData = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileData";
						outportPath = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\output\\ftpalist\\temp";
						ftpPath = "E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\ftpalist";
					} else if (osName.toLowerCase().contains("linux")) {
						logger.info("这是Linux系统");
						// 在Linux系统上执行特定操作
					}

					// 東京のタイムゾーンを設定
					ZoneId tokyoZone = ZoneId.of("Asia/Tokyo");
					ZonedDateTime tokyoTime = ZonedDateTime.now(tokyoZone);
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					String yyyyMMdd = tokyoTime.format(formatter);
					formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
					String yyyyMMddHHmmss = tokyoTime.format(formatter);

					AllToPDF AllToPDF = new AllToPDF();
					File folder;
					File[] files;
					KanaLogic KanaLogic = new KanaLogic();

					LinkedHashMap<String, String> LinkedHashMapKeyValueNew = new LinkedHashMap<String, String>();
					LinkedHashMap<String, String> LinkedHashMapKeyValue = new LinkedHashMap<String, String>();
					LinkedHashMapKeyValue.put("#会社名自国語全部#", "CompanyName_Chinese");
					LinkedHashMapKeyValue.put("#会社外国本店自国語全部#", "address_Chinese");
					LinkedHashMapKeyValue.put("#代表者名自国語#", "DaibiaoName_Chinese");
					LinkedHashMapKeyValue.put("#会社名英語全部#", "CompanyName_English");
					LinkedHashMapKeyValue.put("#会社外国本店英語全部#", "address_English");
					LinkedHashMapKeyValue.put("#外国本店電話地区番号#", "tel_country");
					LinkedHashMapKeyValue.put("#外国本店電話1#", "tel_1");
					LinkedHashMapKeyValue.put("#外国本店電話2#", "tel_2");
					LinkedHashMapKeyValue.put("#外国本店電話3#", "tel_3");
					LinkedHashMapKeyValue.put("#資本金#", "zhice_ziben");
					LinkedHashMapKeyValue.put("#PEありますか#", "changshe_jigou_Select");
					LinkedHashMapKeyValue.put("#申請年度の基準期間課税売上#", "xiaoshouerYYYY_2");
					LinkedHashMapKeyValue.put("#申請年度の特定期間課税売上#", "xiaoshouerYYYY_1_half");
					LinkedHashMapKeyValue.put("#申請年度の特定期間支払給与#", "tokutei_kikann_siharai_kyuuyo");
					LinkedHashMapKeyValue.put("#申請年度の昨年度売上#", "xiaoshouerYYYY_1");
					LinkedHashMapKeyValue.put("#申請簡易課税漢字#", "jianyi_keshui_Select");
					LinkedHashMapKeyValue.put("#代表者名英語全部#", "DaibiaoName_English");
					LinkedHashMapKeyValue.put("#作为免税事业者您想从下一个会计年度的首日开始登录吗#", "shouri_kaishi_denglu_xiayige");
					LinkedHashMapKeyValue.put("#您想从本会计期间的首日开始登录吗#", "shouri_kaishi_denglu_ben");

					LinkedHashMapKeyValue.put("#公司名（フリガナ）#", "CompanyName_pianjiaming");
					LinkedHashMapKeyValue.put("#公司本店地址（フリガナ）#", "address_pianjiaming");
					LinkedHashMapKeyValue.put("#代表者氏名（フリガナ）#", "DaibiaoName_pianjiaming");
					LinkedHashMapKeyValue.put("#纳税地地址邮编（第一段）#", "nashuidi_youbian1");
					LinkedHashMapKeyValue.put("#纳税地地址邮编（第二段）#", "nashuidi_youbian2");
					LinkedHashMapKeyValue.put("#纳税地地址（日语）#", "nashuidi");
					LinkedHashMapKeyValue.put("#纳税地地址（フリガナ）#", "nashuidi_pianjiaming");
					LinkedHashMapKeyValue.put("#纳税地电话（第一段）#", "nashuidi_tel1");
					LinkedHashMapKeyValue.put("#纳税地电话（第二段）#", "nashuidi_tel2");
					LinkedHashMapKeyValue.put("#纳税地电话（第三段）#", "nashuidi_tel3");
					LinkedHashMapKeyValue.put("#管辖税务署#", "guanxia_shuiwushu");
					//TODO
					LinkedHashMapKeyValue.put("#利用者識別番号#", "riyongzhe_shibie_fanhao");
					//t_etax_account_xiaofeishui
					LinkedHashMapKeyValue.put("#申告时期首公元年月日#", "shengao_shiqishou_YYYYMMDD");
					LinkedHashMapKeyValue.put("#申告时期末公元年月日#", "shengao_shiqimo_YYYYMMDD");
					LinkedHashMapKeyValue.put("#原则课税还是简易课税#", "yuanze_or_jianyi");
					LinkedHashMapKeyValue.put("#(10%分)売上課税売上第2種事業　小売業税込価額#", "keshui_maishang_2_xiaomai");
					LinkedHashMapKeyValue.put("#中間納付税額#", "zhongjian_nafu_shuie");
					LinkedHashMapKeyValue.put("#中間納付譲渡割額#", "zhongjian_nafu_durang");
					LinkedHashMapKeyValue.put("#(10%分)売上課税売上税込価額#", "xiaoshoue_10");
					LinkedHashMapKeyValue.put("#(軽8%分)売上課税売上税込価額#", "xiaoshoue_8");
					LinkedHashMapKeyValue.put("#(10%分)仕入仕入課税売上対応税込価額#", "fapiao_10");
					LinkedHashMapKeyValue.put("#(軽8%分)仕入仕入課税売上対応税込価額#", "fapiao_8");
					LinkedHashMapKeyValue.put("#(10%分)適格請求書発行事業者以外からの仕入80%控除分課税売上対応税込価額#", "fapiao_10_20231001");
					LinkedHashMapKeyValue.put("#(10%分)適格請求書発行事業者以外からの仕入50%控除分課税売上対応税込価額#", "fapiao_10_20261001");
					LinkedHashMapKeyValue.put("#(軽8%分)適格請求書発行事業者以外からの仕入80%控除分課税売上対応税込価額#", "fapiao_8_20231001");
					LinkedHashMapKeyValue.put("#(軽8%分)適格請求書発行事業者以外からの仕入50%控除分課税売上対応税込価額#", "fapiao_8_20261001");

					LinkedHashMapKeyValue.put("#(10%分)仕入輸入仕入課税売上対応仮払消費税#", "jinkou_xiaofeishui_guoshui");
					LinkedHashMapKeyValue.put("#(10%分)仕入輸入仕入地方消費税分仮払消費税#", "jinkou_xiaofeishui_dishui");

					//特别定义处理
					LinkedHashMapKeyValue.put("#法人番号#", "HoujinBangou");
					LinkedHashMapKeyValue.put("#利用者識別番号#", "bangou");
					LinkedHashMapKeyValue.put("#利用者識別番号0#", "liyongzhe_shibie_fanhao");

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					LinkedHashMap<String, HashMap<String, String>> LinkedHashMap = t_etax_account_infoDao
							.selectEx_t_etax_account(output_fileList[0]);

					//				if (true) return;

					for (Map.Entry<String, HashMap<String, String>> entry : LinkedHashMap.entrySet()) {
						String yyyymmdd_count = entry.getKey(); // 获取 LinkedHashMap 的键
						HashMap<String, String> value = entry.getValue(); // 获取 LinkedHashMap 的值

						String CompanyName_Chinese = value.get("CompanyName_Chinese");
						String folderName = yyyyMMddHHmmss + "_" + yyyymmdd_count + "_" + CompanyName_Chinese;
						String outportPathNew = outportPath + "/" + folderName;

						logger.info("★★★" + yyyymmdd_count + " " + CompanyName_Chinese);

						//						LinkedHashMap<String, EtaxBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, EtaxBean>();
						//						LinkedHashMapEtaxBean = t_etax_account_resDao.selectSPEED(yyyymmdd_count);
						//						EtaxBean EtaxBean = LinkedHashMapEtaxBean.get(yyyymmdd_count);
						//						if ("ZIP生成中".equals(EtaxBean.getHoryuu())) {
						//							out.print("{\"res\":\"ZIP生成中\"}");
						//							logger.info("end");
						//							return;
						//						}

						//设定 ZIP生成中
						t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count, "", "ZIP生成中");
						logger.info("t_etax_account_resDao -> ZIP生成中 " + yyyymmdd_count);

						//公司文件夹做成
						File directory = new File(outportPathNew);
						directory.mkdir();

						/*
						 * 添付資料PDF
						 */
						getTianpuPDF(folderPathFileData, AllToPDF, yyyymmdd_count, value, outportPathNew);

						/*
						 * 输出数据基本信息
						 */
						setHashMapDataBase(LinkedHashMapKeyValueNew, LinkedHashMapKeyValue, yyyymmdd_count, value);

						/*
						 * 根据客户输入数据，生成输出用的相应数据
						 */
						if (value_shengcheng_file_type.indexOf("ALL") > -1
								|| value_shengcheng_file_type.indexOf("JCT") > -1) {
							setHashMapDataJCT(yyyyMMdd, KanaLogic, LinkedHashMapKeyValueNew, LinkedHashMapKeyValue,
									yyyymmdd_count, value);

						}
						if (value_shengcheng_file_type.indexOf("ALL") > -1
								|| value_shengcheng_file_type.indexOf("消费税") > -1) {
							setHashMapDataXiaofeishui(yyyyMMdd, LinkedHashMapKeyValueNew,
									LinkedHashMapKeyValue, yyyymmdd_count, value);

						}

				        // 遍历 LinkedHashMap 并打印 key 和 value
				        for (Map.Entry<String, String> my_entry : LinkedHashMapKeyValueNew.entrySet()) {
				            String my_entrykey = my_entry.getKey();
				            String my_entryvalue = my_entry.getValue();
				            logger.info("yyyymmdd_count: " + yyyymmdd_count + ", " + my_entrykey + ": " + my_entryvalue);
				        }


						/*
						 * 按照模板生成数据
						 */
						// 指定文件夹路径
						String folderPath = getServletContext().getRealPath("/IMPORT_WORDXTX");
						// 创建一个 File 对象表示文件夹
						folder = new File(folderPath);
						// 获取文件夹中的所有文件
						//files = folder.listFiles();
						files = FuncUtils.listFilesInFolder(folderPath);

						for (File file : files) {
							if (file.isFile()) {
								String fileName = file.getName();

								String outportfolderNew = file.getPath().replace(folderPath, "");

								outportfolderNew = outportfolderNew.replace(fileName, "");
								if ("\\".equals(outportfolderNew) || "/".equals(outportfolderNew)) {
									outportfolderNew = "";
								}

								if (value_shengcheng_file_type.indexOf("ALL") > -1) {
									continue;

								} else if (value_shengcheng_file_type.indexOf("JCT") > -1) {
									if ("0system和master_book中间加页即可精简版.xml".equals(fileName)
											|| "1国外インボイス登録申請全ページ.xml".equals(fileName)
											|| "2納税管理人届出書.xml".equals(fileName)
											|| "3簡易課税選択届出書.xml".equals(fileName)
											|| "4課税事業者届出書基準期間用.xml".equals(fileName)
											|| "5課税事業者届出書新設法人用.xml".equals(fileName)
											|| "6法人番号指定申請.xml".equals(fileName)
											|| "7税務代理権限証書.xml".equals(fileName)
											|| "21法人番号登録申請添付ファイル.docx".equals(fileName)
											|| "22必要情報まとめ.docx".equals(fileName)) {

									} else {
										continue;

									}

								} else if (value_shengcheng_file_type.indexOf("消费税") > -1) {
									if ("原则课税".equals(LinkedHashMapKeyValueNew.get("#原则课税还是简易课税#"))) {
										if ("法人原則還付無基本情報ファイル.xlsx".equals(fileName)
												|| "法人原則還付無基礎データ.xlsx".equals(fileName)
												|| "法人原則還付無申告書共通情報固定項目.xlsx".equals(fileName)
												|| "法人原則還付無税務代理権限証書固定項目.xlsx".equals(fileName)) {

										} else {
											continue;

										}

									} else {
										if ("法人簡易基本情報ファイル.xlsx".equals(fileName)
												|| "法人簡易計算基礎データ.xlsx".equals(fileName)
												|| "法人簡易申告書共通情報固定項目.xlsx".equals(fileName)
												|| "法人簡易税務代理権限証書固定項目.xlsx".equals(fileName)) {

										} else {
											continue;

										}
									}
								}

								// 提取扩展名
								String fileExtension = "";
								int dotIndex = fileName.lastIndexOf('.');
								if (dotIndex > 0) {
									fileExtension = fileName.substring(dotIndex + 1);
								}

								String fileNameNew = yyyymmdd_count + "_" + fileName;
								// 根据扩展名执行不同的方法
								if ("xml".equalsIgnoreCase(fileExtension)) {

									// 您可以根据不同的系统类型执行不同的操作
									if (osName.toLowerCase().contains("windows")) {
										logger.info("这是Windows系统");
										// 在Windows系统上执行特定操作
									} else if (osName.toLowerCase().contains("linux")) {
										logger.info("这是Linux系统");
										// 在Linux系统上执行特定操作

										//#我可以申请简易课税吗#等于1 且 #申請簡易課税01#等于1的时候才加入
										//3簡易課税選択届出書.xml
										if ("3簡易課税選択届出書.xml".equals(fileName)) {
											if ("1".equals(LinkedHashMapKeyValueNew.get("#我可以申请简易课税吗#"))
													&& "1".equals(LinkedHashMapKeyValueNew.get("#申請簡易課税01#"))) {
												//何もしない

											} else {
												//TODO
												continue;
											}

										}

										//#新設法人ですか#等于1时加入
										//4課税事業者届出書基準期間用.xml
										if ("4課税事業者届出書基準期間用.xml".equals(fileName)) {
											if (Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) >= 3
													&& "1".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))) {
												//何もしない

											} else {
												//TODO
												continue;
											}

										}

										//#新設法人ですか#等于1时加入
										//5課税事業者届出書新設法人用.xml
										if ("5課税事業者届出書新設法人用.xml".equals(fileName)) {
											if (Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) < 3
													&& "1".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))) {
												//何もしない

											} else {
												//TODO
												continue;
											}

										}
									}

									// 执行针对 XML 文件的方法
									String readTxt = FuncUtils.readTxt(file.getPath());
									//			                    String readTxt = FuncUtils.readTxt(file.getPath(), "SJIS");

									for (String innerKey : LinkedHashMapKeyValueNew.keySet()) {
										if (StringUtils.isEmpty(innerKey) == false) {
											String innerValue = LinkedHashMapKeyValueNew.get(innerKey);
											readTxt = readTxt.replaceAll(innerKey, innerValue);
										}

									}

									FuncUtils.writeTxt(outportPathNew + "/" + fileNameNew, readTxt);
									//		                    	FuncUtils.writeTxt(outportPathNew + "/" + fileNameNew, readTxt, "SJIS");

								} else if ("docx".equalsIgnoreCase(fileExtension)) {
									// 执行针对 Word 文件的方法
									//TODO
									FuncUtils.readWord(file.getPath(), outportPathNew + "/" + fileNameNew,
											LinkedHashMapKeyValueNew);
									AllToPDF.wordToPDF(outportPathNew + "/" + fileNameNew,
											outportPathNew + "/" + fileNameNew.replaceAll(".docx", ".pdf"));
									//					            	AllToPDF.wordToPDF2(outportPathNew + "/" + fileNameNew, outportPathNew + "/" + fileNameNew.replaceAll(".docx", "2.pdf"));

									FuncUtils.deleteFile(outportPathNew + "/" + fileNameNew);

								} else if ("xlsx".equalsIgnoreCase(fileExtension)) {
									if ("原则课税".equals(LinkedHashMapKeyValueNew.get("#原则课税还是简易课税#"))) {
										if ("法人原則還付無基本情報ファイル.xlsx".equals(fileName)
												|| "法人原則還付無基礎データ.xlsx".equals(fileName)
												|| "法人原則還付無申告書共通情報固定項目.xlsx".equals(fileName)
												|| "法人原則還付無税務代理権限証書固定項目.xlsx".equals(fileName)) {

										} else {
											continue;

										}

									} else {
										if ("法人簡易基本情報ファイル.xlsx".equals(fileName)
												|| "法人簡易計算基礎データ.xlsx".equals(fileName)
												|| "法人簡易申告書共通情報固定項目.xlsx".equals(fileName)
												|| "法人簡易税務代理権限証書固定項目.xlsx".equals(fileName)) {

										} else {
											continue;

										}
									}

									directory = new File(outportPathNew + outportfolderNew);
									directory.mkdir();

									//TODO
									FuncUtils.readeExcel(file.getPath(), outportPathNew + outportfolderNew + "/" + fileNameNew,
											LinkedHashMapKeyValueNew);

								}

								logger.info(fileNameNew + " -> ok");
							}
						}

						if (value_shengcheng_file_type.indexOf("ALL") > -1
								|| value_shengcheng_file_type.indexOf("JCT") > -1) {
							/*
							 * xml合并
							 */
							String xmlFilePath = outportPathNew + "/" + yyyymmdd_count + "_"
									+ "0system和master_book中间加页即可精简版.xml";
							String outputFilePath = outportPathNew + "/" + yyyymmdd_count + "_" + CompanyName_Chinese
									+ ".xml";

							folder = new File(outportPath + "/" + yyyyMMddHHmmss + "_" + yyyymmdd_count + "_"
									+ CompanyName_Chinese);
							files = folder.listFiles();

							if (files != null) {
								String readTxt = FuncUtils.readTxt(xmlFilePath);
								//	                    	String readTxt = FuncUtils.readTxt(xmlFilePath, "SJIS");
								for (File file : files) {
									if (file.isFile() && file.getName().endsWith(".xml")) {
										if ((yyyymmdd_count + "_" + "0system和master_book中间加页即可精简版.xml")
												.equals(file.getName())) {
											FuncUtils.deleteFile(file.getPath());
											continue;

										}
										String readTxtNew = FuncUtils.readTxt(file.getPath());
										FuncUtils.deleteFile(file.getPath());
										//			                    	String readTxtNew = FuncUtils.readTxt(file.getPath(), "SJIS");
										readTxt = readTxt.replaceAll(
												file.getName().replaceAll(yyyymmdd_count + "_", ""), readTxtNew);
									}
								}

								readTxt = readTxt.replaceAll("1国外インボイス登録申請全ページ.xml", "");
								readTxt = readTxt.replaceAll("2納税管理人届出書.xml", "");
								readTxt = readTxt.replaceAll("3簡易課税選択届出書.xml", "");
								readTxt = readTxt.replaceAll("4課税事業者届出書基準期間用.xml", "");
								readTxt = readTxt.replaceAll("5課税事業者届出書新設法人用.xml", "");
								readTxt = readTxt.replaceAll("6法人番号指定申請.xml", "");
								readTxt = readTxt.replaceAll("7税務代理権限証書.xml", "");

								//	                    	FuncUtils.writeTxt(outputFilePath, readTxt);
								FuncUtils.writeTxt(outputFilePath, readTxt, "SJIS");
							}
						}













						/*
						 * xtx
						 */
						if (output_fileList.length == 2) {
							String INSQ = output_fileList[1];
							logger.info("yyyymmdd_count: " + yyyymmdd_count + ", INSQ, " + INSQ);
							if (StringUtils.isEmpty(INSQ) == true) {

							} else if ("null".equals(INSQ.toLowerCase())) {

							} else {
								try {
									FuncUtilsAiEtax FuncUtilsAiEtax = new FuncUtilsAiEtax();
									String path_Xtx = FuncUtilsAiEtax.getXtx(req, INSQ);

									File file = new File(path_Xtx);
									if (file.exists()) {

										Path sourceDir = Paths.get(path_Xtx);
										Path targetDir = Paths.get(outportPathNew);

										// 如果目标文件夹不存在，创建它
										if (!Files.exists(targetDir)) {
											Files.createDirectories(targetDir);
										}

										// 遍历源目录下的所有文件和子目录
										Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
											@Override
											public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
												Path targetFile = targetDir.resolve(sourceDir.relativize(file));
												Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
												return FileVisitResult.CONTINUE;
											}
										});

									} else {
										out.print("{\"res\":\"结果文件不存在\"}");
										logger.info("end KakuninAuto");
										return;
									}

								} catch (Exception e1) {
									// TODO 自動生成された catch ブロック
									e1.printStackTrace();

									out.print("{\"res\":\""+e1+"\"}");
									logger.info("end KakuninAuto");
									return;
								}
							}


						}


						/*
						 * 生成文件ZIP
						 */
						// 源文件夹的路径
						String sourceFolderPath = outportPathNew;
						// 目标ZIP文件的路径
						String targetZipFilePath = outportPathNew + ".zip";
						try {
							// 创建一个输出流，将文件写入ZIP文件
							FileOutputStream fos = new FileOutputStream(targetZipFilePath);
							ZipOutputStream zipOut = new ZipOutputStream(fos);

							// 调用递归方法将文件夹及其内容添加到ZIP文件中
							FuncUtils.addToZipFile(sourceFolderPath, sourceFolderPath, zipOut);


							// 关闭ZIP文件输出流
							zipOut.close();
							fos.close();

							logger.info("ZIP文件创建成功：" + targetZipFilePath);
						} catch (IOException e) {
							e.printStackTrace();
						}

						File sourceFolderZip = new File(targetZipFilePath); // 替换为实际的源文件夹路径
						//Zip复制到备份处
						File destinationFolderZip = new File(
								getServletContext().getRealPath("/output/ftpalist") + "/" + folderName + ".zip"); // 替换为实际的目标文件夹路径
						Files.copy(sourceFolderZip.toPath(), destinationFolderZip.toPath(),
								StandardCopyOption.REPLACE_EXISTING);
						//			        FuncUtils.copyZipFiles(targetZipFilePath, getServletContext().getRealPath("/output/ftpalist"));
						//Zip复制到FTP
						//					    destinationFolderZip = new File(ftpPath + "/" + folderName + ".zip"); // 替换为实际的目标文件夹路径
						//					    Files.copy(sourceFolderZip.toPath(), destinationFolderZip.toPath(), StandardCopyOption.REPLACE_EXISTING);

						//ALL复制到备份处
						FuncUtils.copyFolder(outportPathNew,
								getServletContext().getRealPath("/output/ftpalist") + "/" + folderName);
						//ALL复制到FTP
						//					    //公司文件夹做成
						//					    FuncUtils.copyFolder(outportPathNew , ftpPath + "/" + folderName);

						//删除临时文件
						FuncUtils.deleteFile(targetZipFilePath);
						FuncUtils.deleteFolder(new File(outportPathNew));

						/*
						 * 权限更改 有BUG
						 */
						// 定义新的所有者名称（用户名）
						//						setOwner("ftpalist", osName, ftpPath + "/" + folderName, destinationFolderZip.getPath());
						//						setOwner("ec2-user", osName, ftpPath + "/" + folderName, destinationFolderZip.getPath());
						//						setOwner("fukuzawa", osName, getServletContext().getRealPath("/output/ftpalist") + "/" + folderName, destinationFolderZip.getPath());

						/*
						 * 数据生成完了后状态变更
						 */
						// 您可以根据不同的系统类型执行不同的操作
						if (osName.toLowerCase().contains("windows")) {
							logger.info("这是Windows系统");
							// 在Windows系统上执行特定操作
						} else if (osName.toLowerCase().contains("linux")) {
							logger.info("这是Linux系统");
							// 在Linux系统上执行特定操作

						}
						//TODO
						//完了设定

						if (value_shengcheng_file_type.indexOf("ALL") > -1
								|| value_shengcheng_file_type.indexOf("JCT") > -1) {
							t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count, folderName + ".zip", "完了");
							logger.info("t_etax_account_resDao -> 完了 " + yyyymmdd_count);

						}

						if (value_shengcheng_file_type.indexOf("消费税") > -1) {
							t_etax_account_xiaofeishuiDao t_etax_account_xiaofeishuiDao = new t_etax_account_xiaofeishuiDao();
							t_etax_account_xiaofeishuiDao.Update_res_horyuu(yyyymmdd_count, folderName + ".zip", "完了");

						}
					}

					out.print("OK");

				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					out.print("NG");
				}

				logger.info("end KakuninAuto");
				return;

			}

			Map<String, String[]> HashMapParameterMap = req.getParameterMap();
			for (String key : HashMapParameterMap.keySet()) {
				if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
					continue;
				}

				if ("delete_activation".equals(key)) {
					String yyyymmdd_count = HashMapParameterMap.get(key)[0].toString();

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					if (t_etax_account_resDao.DELETE_res(yyyymmdd_count) > 0) {
						EtaxDao EtaxDao = new EtaxDao();
						EtaxDao.DELETE(yyyymmdd_count);
						t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
						t_etax_account_infoDao.Update_syouninn_status(yyyymmdd_count, "");

					}

					out.print("{\"res\":\"ok\"}");
					logger.info("end");
					return;

				} else if ("horyuu".equals(key)) {
					String yyyymmdd_count = HashMapParameterMap.get(key)[0].toString();
					//					String horyuu = HashMapParameterMap.get(key)[1].toString();

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);

					out.print("{\"res\":\"ok\"}");
					logger.info("end");
					return;

				} else if ("getEtaxNo".equals(key)) {
					String yyyymmdd_count = HashMapParameterMap.get(key)[0].toString();

					if (StringUtils.isEmpty(yyyymmdd_count) == true) {
						out.print("没有指定，管理ID！");
						logger.info("end");
						return;
					}

					t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
					t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
					if (StringUtils.isEmpty(t_etax_account_infoExBean.getYyyymmdd_count()) == true) {
						out.print("指定的管理ID，数据不存在！");
						logger.info("end");
						return;
					}

					if (StringUtils.isEmpty(t_etax_account_infoExBean.getBangou()) == false) {
						out.print(t_etax_account_infoExBean.getBangou());
						logger.info("end");
						return;
					}

					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);
					pandaWebDriver testNoWEB = new pandaWebDriver(null);
					String msg = testNoWEB.getEtaxNo(yyyymmdd_count);
					if("国税局系统维护中".equals(msg)) {
						out.print(msg);
						logger.info("end");
						return;

					}

					String Bangou = t_etax_account_resDao.selecBangou(yyyymmdd_count);
					out.print(Bangou);
					logger.info("end");
					return;




				} else if ("filter".equals(key)) {
					t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
					LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = new LinkedHashMap<String, t_etax_account_resExBean>();
					if ("SPEED".equals(filter)) {
						LinkedHashMap_t_etax_account_resExBean = t_etax_account_resDao.selectSPEED(web, maxNo);
					} else {
						LinkedHashMap_t_etax_account_resExBean = t_etax_account_resDao.selectAll();
					}
					LinkedHashMap<String, t_etax_account_resBean> LinkedHashMap_t_etax_account_resBeanBK = new LinkedHashMap<String, t_etax_account_resBean>();
					LinkedHashMap<String, t_etax_account_resBean> LinkedHashMap_t_etax_account_resBeanErr = new LinkedHashMap<String, t_etax_account_resBean>();

					for (String yyyymmdd_count : LinkedHashMap_t_etax_account_resExBean.keySet()) {
						t_etax_account_resBean EtaxBean = LinkedHashMap_t_etax_account_resExBean.get(yyyymmdd_count);
						String gHojinmei = EtaxBean.getgHojinmei();
						if (LinkedHashMap_t_etax_account_resBeanBK.containsKey(gHojinmei) == true) {
							LinkedHashMap_t_etax_account_resBeanErr.put(gHojinmei, null);
						} else {
							LinkedHashMap_t_etax_account_resBeanBK.put(gHojinmei, null);
						}
					}

					session.setAttribute("LinkedHashMap_t_etax_account_resExBean",
							LinkedHashMap_t_etax_account_resExBean);
					session.setAttribute("LinkedHashMap_t_etax_account_resBeanErr",
							LinkedHashMap_t_etax_account_resBeanErr);

				}
			}

			if ("Kakunin".equals(web) || "KakuninAuto".equals(web)) {
				req.getRequestDispatcher("/searchEtaxKakunin.jsp").forward(req, resp);

			} else {
				req.getRequestDispatcher("/searchEtax.jsp").forward(req, resp);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("end");

		return;

	}

	private void setHashMapDataJCT(String yyyyMMdd, KanaLogic KanaLogic,
			LinkedHashMap<String, String> LinkedHashMapKeyValueNew, LinkedHashMap<String, String> LinkedHashMapKeyValue,
			String yyyymmdd_count, HashMap<String, String> value) throws ParseException {

		/*
		 * 式样
		 * https://docs.google.com/spreadsheets/d/1PgfqRxR3t4rEmU6ia_w_vcl8qtZnPRl9/edit#gid=1371705051
		 */

		String innerkeyNew = "";
		String innerValueNew = "";

		innerkeyNew = "#作成日点点式#";
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy.MM.dd");
		Date date = inputDateFormat.parse(yyyyMMdd);
		innerValueNew = outputDateFormat.format(date);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//                	innerValueNew = value.get("company_YYYY")
		//                			+ "."
		//                			+ String.format("%02d", Integer.parseInt(value.get("company_MM")))
		//                			+ "."
		//                			+ String.format("%02d", Integer.parseInt(value.get("company_DD")));

		innerkeyNew = "#作成日西暦年#";
		innerValueNew = yyyyMMdd;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		String[] wareki = KanaLogic.getWareki(yyyyMMdd).split(",");
		innerkeyNew = "#作成日年号#";
		innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#作成日年#";
		innerValueNew = wareki[1];
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以要加1
		int day = calendar.get(Calendar.DAY_OF_MONTH);

		innerkeyNew = "#作成日月#";
		innerValueNew = "" + month;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#作成日日#";
		innerValueNew = "" + day;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#外国本店設立西暦#";
		innerValueNew = value.get("company_YYYY")
				+ String.format("%02d", Integer.parseInt(value.get("company_MM")))
				+ String.format("%02d", Integer.parseInt(value.get("company_DD")));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#外国本店設立公元年#";
		innerValueNew = value.get("company_YYYY");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//		"默认条件，(#作成日西暦年# +15)的下一个月的1号
		//		#作为免税事业者您想从下一个会计年度的首日开始登录吗#等于1的人是下一会计期间的期首日
		//		#您想从本会计期间的首日开始登录吗#等于1的人是公司设立日"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate inputDate = LocalDate.parse(yyyyMMdd, formatter);
		// 将日期向后推迟15天
		LocalDate dateAfterDays = inputDate.plusDays(15);
		// 将日期移到下一个月的第一天
		LocalDate nextMonthDate = dateAfterDays.plusMonths(1).withDayOfMonth(1);
		// 使用DateTimeFormatter格式化日期并打印结果
		String formattedDate = nextMonthDate.format(formatter);
		if ("1".equals(LinkedHashMapKeyValueNew.get("#作为免税事业者您想从下一个会计年度的首日开始登录吗#"))) {
			formattedDate = year + 1 + "0101";

		} else if ("1".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"))) {
			formattedDate = LinkedHashMapKeyValueNew.get("#外国本店設立西暦#");

		}

		innerkeyNew = "#登录预定日西暦年#";
		innerValueNew = formattedDate;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		wareki = KanaLogic.getWareki(formattedDate).split(",");
		innerkeyNew = "#登录预定日年号#";
		innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#登录预定日年#";
		innerValueNew = wareki[1];
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		calendar = Calendar.getInstance();
		date = inputDateFormat.parse(formattedDate);
		calendar.setTime(date);
		month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以要加1
		day = calendar.get(Calendar.DAY_OF_MONTH);

		innerkeyNew = "#登录预定日月#";
		innerValueNew = "" + month;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#登录预定日日#";
		innerValueNew = "" + day;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#達人コード#";
		innerValueNew = value.get("tatuji_code");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社名フリガナ全部#";
		innerValueNew = FuncUtils.fn_hanzi(value.get("CompanyName_Chinese"));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社名フリガナ#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#会社名フリガナ全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社外国本店フリガナ全部#";
		innerValueNew = FuncUtils.fn_hanzi(value.get("CompanyName_English"));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社名英語#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#会社名英語全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社外国本店フリガナ全部#";
		innerValueNew = FuncUtils.fn_hanzi(value.get("address_English"));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社外国本店フリガナ#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#会社外国本店フリガナ全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#会社外国本店英語#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(value.get("address_English"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		wareki = KanaLogic.getWareki(LinkedHashMapKeyValueNew.get("#外国本店設立西暦#")).split(",");
		innerkeyNew = "#外国本店設立年号#";
		innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#外国本店設立年#";
		innerValueNew = wareki[1];
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		calendar = Calendar.getInstance();
		date = inputDateFormat.parse(LinkedHashMapKeyValueNew.get("#外国本店設立西暦#"));
		calendar.setTime(date);
		month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，所以要加1
		day = calendar.get(Calendar.DAY_OF_MONTH);

		innerkeyNew = "#外国本店設立月#";
		innerValueNew = "" + month;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#外国本店設立日#";
		innerValueNew = "" + day;
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#申請簡易課税01#";
		innerValueNew = "NO".equals(LinkedHashMapKeyValueNew.get("#申請簡易課税漢字#")) ? "0" : "1";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#代表者名フリガナ#";
		innerValueNew = FuncUtils.fn_hanzi(value.get("DaibiaoName_Chinese"));
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#代表者名英語#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#代表者名英語全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#何期目#";
		innerValueNew = ""
				+ FuncUtils.calculateFiscalYear(LinkedHashMapKeyValueNew.get("#外国本店設立西暦#").substring(0, 4), yyyyMMdd);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#新設法人ですか#";
		innerValueNew = Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) == 1 ? "1" : "0";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#申請年度の基準期間#";
		innerValueNew = Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) - 2 <= 0 ? "-"
				: Integer.toString(year - 2) + "0101-" + Integer.toString(year - 2) + "1231";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#申請年度の特定期間#";
		innerValueNew = Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) - 1 <= 0 ? "-"
				: Integer.toString(year - 1) + "0101-" + Integer.toString(year - 1) + "0630";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#申請年度の昨年度#";
		innerValueNew = Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) - 1 <= 0 ? "-"
				: Integer.toString(year - 1) + "0101-" + Integer.toString(year - 1) + "1231";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		/*
		A　#何期目#＜＝2 and #資本金#大于等于1000万时  ＝1
		B    #何期目#＜＝2 　and #申請年度の特定期間課税売上# > 1000万 and #申請年度の特定期間支払給与#>1000万时  ＝1
		C　#何期目#＞＝3　and #申請年度の基準期間課税売上# >1000万时  ＝1
		D　其他情况＝0
		 */
		innerkeyNew = "#課税事業者ですか#";
		if (Long.parseLong(LinkedHashMapKeyValueNew.get("#何期目#")) <= 2
				&& Long.parseLong(LinkedHashMapKeyValueNew.get("#資本金#")) >= 10000000) {
			innerValueNew = "1";

		} else if (Long.parseLong(LinkedHashMapKeyValueNew.get("#何期目#")) <= 2
				&& Long.parseLong(LinkedHashMapKeyValueNew.get("#申請年度の特定期間課税売上#")) > 10000000
				&& Long.parseLong(LinkedHashMapKeyValueNew.get("#申請年度の特定期間支払給与#")) > 10000000) {
			innerValueNew = "1";

		} else if (Long.parseLong(LinkedHashMapKeyValueNew.get("#何期目#")) >= 3
				&& Long.parseLong(LinkedHashMapKeyValueNew.get("#申請年度の基準期間課税売上#")) > 10000000) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";
		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#从本期开始简易可以吗#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				|| "1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#从下一期开始简易可以吗#";
		innerValueNew = Long.parseLong(LinkedHashMapKeyValueNew.get("#申請年度の昨年度売上#")) <= 50000000 ? "1" : "0";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#我可以申请简易课税吗#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#从本期开始简易可以吗#"))
				|| "1".equals(LinkedHashMapKeyValueNew.get("#从下一期开始简易可以吗#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#特定国外事業者#", "1");
		LinkedHashMapKeyValueNew.put("#非特定国外事業者#", "0");

		innerkeyNew = "#課税事業者非新#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& !"1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#免税事業者非新#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& !"1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#ETAXによる通知#", "0");

		//
		innerkeyNew = "#新事業者#";
		innerValueNew = "1".equals(LinkedHashMapKeyValueNew.get("#何期目#")) ? "1" : "0";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#課税期間初日から登録#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))
				&& "1".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#上記以外の課税事業者#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))
				&& "0".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"))
				&& "1".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#上記以外の免税事業者#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))
				&& "0".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"))
				&& "0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#課税期間初日から登録年号#";
		innerValueNew = "1".equals(LinkedHashMapKeyValueNew.get("#課税期間初日から登録#"))
				? LinkedHashMapKeyValueNew.get("#登录预定日年号#")
				: "";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#課税期間初日から登録年#";
		innerValueNew = "1".equals(LinkedHashMapKeyValueNew.get("#課税期間初日から登録#"))
				? LinkedHashMapKeyValueNew.get("#登录预定日年#")
				: "";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#課税期間初日から登録月#";
		innerValueNew = "1".equals(LinkedHashMapKeyValueNew.get("#課税期間初日から登録#"))
				? LinkedHashMapKeyValueNew.get("#登录预定日月#")
				: "";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#課税期間初日から登録日#";
		innerValueNew = "1".equals(LinkedHashMapKeyValueNew.get("#課税期間初日から登録#"))
				? LinkedHashMapKeyValueNew.get("#登录预定日日#")
				: "";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#免税事業者の確認第2チェック#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& "1".equals(LinkedHashMapKeyValueNew.get("#作为免税事业者您想从下一个会计年度的首日开始登录吗#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第2チェック翌課税初日年号#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第2チェック翌課税初日年#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第2チェック翌課税初日月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第2チェック翌課税初日日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#免税事業者の確認第3チェック#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& "1".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#特定以外国内事業所カナ#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所郵便1#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所郵便2#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所住所#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所電話1#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所電話2#", "");
		LinkedHashMapKeyValueNew.put("#特定以外国内事業所電話3#", "");

		LinkedHashMapKeyValueNew.put("#添付資料第1チェック謄本定款#", "1");
		LinkedHashMapKeyValueNew.put("#添付資料第2チェック代理証書#", "1");
		LinkedHashMapKeyValueNew.put("#添付資料第3チェックHP#", "0");
		LinkedHashMapKeyValueNew.put("#添付資料第4チェックその他#", "0");

		//
		innerkeyNew = "#免税事業者の確認第1チェック#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#"))
				&& "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第3チェック#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック設立年号#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立年号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック設立年#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立年#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック設立月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック設立日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック事業年度開始月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店事業年度開始月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック事業年度開始日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店事業年度開始日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック事業年度終了月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店事業年度終了月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック事業年度終了日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店事業年度終了日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック資本金#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#資本金#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック登録希望日年号#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック登録希望日年#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック登録希望日月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#第1チェック登録希望日日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#納税管理人届出書提出日年号#", LinkedHashMapKeyValueNew.get("#作成日年号#"));
		LinkedHashMapKeyValueNew.put("#納税管理人届出書提出日年#", LinkedHashMapKeyValueNew.get("#作成日年#"));
		LinkedHashMapKeyValueNew.put("#納税管理人届出書提出日月#", LinkedHashMapKeyValueNew.get("#作成日月#"));
		LinkedHashMapKeyValueNew.put("#納税管理人届出書提出日日#", LinkedHashMapKeyValueNew.get("#作成日日#"));

		LinkedHashMapKeyValueNew.put("#届出者との関係#", "在日納管");
		LinkedHashMapKeyValueNew.put("#職業または事業内容#", "会計代行");
		LinkedHashMapKeyValueNew.put("#定めた理由#", "消費税を申告・納税するため");

		LinkedHashMapKeyValueNew.put("#事業区分#", "2");

		//
		innerkeyNew = "#所得消費税法改正チェック#";
		if ("0".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#"))
				&& ("0".equals(LinkedHashMapKeyValueNew.get("#作为免税事业者您想从下一个会计年度的首日开始登录吗#"))
						|| "1".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#")))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";
		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#ロ該当#";
		innerValueNew = "0";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))
				&& ("1".equals(LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#")))) {
			innerValueNew = "1";

		}
		if ("1".equals(LinkedHashMapKeyValueNew.get("#何期目#"))
				&& ("1".equals(LinkedHashMapKeyValueNew.get("#課税事業者ですか#")))) {
			innerValueNew = "1";

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#ロ該当課税事業者なった日年号#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#ロ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立年号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#ロ該当課税事業者なった日年#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#ロ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立年#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#ロ該当課税事業者なった日月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#ロ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#ロ該当課税事業者なった日日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#ロ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#外国本店設立日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イ該当#";
		innerValueNew = "0";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#"))) {
			innerValueNew = "1";

		}
		if ("1".equals(LinkedHashMapKeyValueNew.get("#ロ該当#"))) {
			innerValueNew = "0";
		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イ該当課税事業者なった日年号#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#イ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イ該当課税事業者なった日年#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#イ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日年#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イ該当課税事業者なった日月#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#イ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日月#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イ該当課税事業者なった日日#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#イ該当#")) ? ""
				: LinkedHashMapKeyValueNew.get("#登录预定日日#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イロハ該当#";
		if ("1".equals(LinkedHashMapKeyValueNew.get("#イ該当#"))
				|| "1".equals(LinkedHashMapKeyValueNew.get("#ロ該当#"))) {
			innerValueNew = "1";

		} else {
			innerValueNew = "0";
		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		innerkeyNew = "#イロハ非該当#";
		innerValueNew = "0".equals(LinkedHashMapKeyValueNew.get("#イロハ該当#")) ? "1" : "0";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		//
		if ("1".equals(LinkedHashMapKeyValueNew.get("#ロ該当#"))) {

			//
			LinkedHashMapKeyValueNew.put("#簡易適用年度の基準期間課税売上#", "0");

			LinkedHashMapKeyValueNew.put("#簡易適用期間開始年号#", LinkedHashMapKeyValueNew.get("#外国本店設立年号#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始年#", LinkedHashMapKeyValueNew.get("#外国本店設立年#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始月#", LinkedHashMapKeyValueNew.get("#外国本店設立月#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始日#", LinkedHashMapKeyValueNew.get("#外国本店設立日#"));

			wareki = KanaLogic.getWareki(LinkedHashMapKeyValueNew.get("#外国本店設立西暦#")).split(",");
			//
			innerkeyNew = "#簡易適用期間終了年号#";
			innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			//
			innerkeyNew = "#簡易適用期間終了年#";
			innerValueNew = wareki[1];
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#簡易適用期間終了月#", "12");
			LinkedHashMapKeyValueNew.put("#簡易適用期間終了日#", "31");

			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年号#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始月#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始日#", "");

			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年号#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了月#", "");
			LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了日#", "");

		} else if ("1".equals(LinkedHashMapKeyValueNew.get("#イ該当#"))) {
			//
			innerkeyNew = "#簡易適用年度の基準期間課税売上#";
			innerValueNew = LinkedHashMapKeyValueNew.get("#申請年度の基準期間課税売上#");
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#簡易適用期間開始年号#", LinkedHashMapKeyValueNew.get("#作成日年号#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始年#", LinkedHashMapKeyValueNew.get("#作成日年#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始月#", "1");
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始日#", "1");

			LinkedHashMapKeyValueNew.put("#簡易適用期間終了年号#", LinkedHashMapKeyValueNew.get("#作成日年号#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間終了年#", LinkedHashMapKeyValueNew.get("#作成日年#"));
			LinkedHashMapKeyValueNew.put("#簡易適用期間終了月#", "12");
			LinkedHashMapKeyValueNew.put("#簡易適用期間終了日#", "31");

			if (Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) - 2 <= 0) {
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年号#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始月#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始日#", "");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年号#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了月#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了日#", "");

			} else {
				wareki = KanaLogic.getWareki(Integer.toString(year - 2) + "0101").split(",");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年号#", FuncUtils.convertToEra(Integer.parseInt(wareki[0])));
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年#", wareki[1]);
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始月#", "1");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始日#", "1");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年号#", FuncUtils.convertToEra(Integer.parseInt(wareki[0])));
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年#", wareki[1]);
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了月#", "12");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了日#", "31");

			}

		} else if ("1".equals(LinkedHashMapKeyValueNew.get("#イロハ非該当#"))) {
			//
			innerkeyNew = "#簡易適用年度の基準期間課税売上#";
			innerValueNew = LinkedHashMapKeyValueNew.get("#申請年度の昨年度売上#");
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			wareki = KanaLogic.getWareki(Integer.toString(year + 1) + "0101").split(",");
			//
			innerkeyNew = "#簡易適用期間開始年号#";
			innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			//
			innerkeyNew = "#簡易適用期間開始年#";
			innerValueNew = wareki[1];
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#簡易適用期間開始月#", "1");
			LinkedHashMapKeyValueNew.put("#簡易適用期間開始日#", "1");

			//
			innerkeyNew = "#簡易適用期間終了年号#";
			innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			//
			innerkeyNew = "#簡易適用期間終了年#";
			innerValueNew = wareki[1];
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#簡易適用期間終了月#", "12");
			LinkedHashMapKeyValueNew.put("#簡易適用期間終了日#", "31");

			if (Integer.parseInt(LinkedHashMapKeyValueNew.get("#何期目#")) - 1 <= 0) {
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年号#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始月#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始日#", "");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年号#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了月#", "");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了日#", "");

			} else {
				wareki = KanaLogic.getWareki(Integer.toString(year - 1) + "0101").split(",");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年号#", FuncUtils.convertToEra(Integer.parseInt(wareki[0])));
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始年#", wareki[1]);
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始月#", "1");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間開始日#", "1");

				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年号#", FuncUtils.convertToEra(Integer.parseInt(wareki[0])));
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了年#", wareki[1]);
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了月#", "12");
				LinkedHashMapKeyValueNew.put("#簡易適用の基準期間終了日#", "31");

			}

		}

		LinkedHashMapKeyValueNew.put("#基準適用期間開始年号#", LinkedHashMapKeyValueNew.get("#登录预定日年号#"));
		LinkedHashMapKeyValueNew.put("#基準適用期間開始年#", LinkedHashMapKeyValueNew.get("#登录预定日年#"));
		LinkedHashMapKeyValueNew.put("#基準適用期間開始月#", "1");
		LinkedHashMapKeyValueNew.put("#基準適用期間開始日#", "1");
		LinkedHashMapKeyValueNew.put("#基準適用期間終了年号#", LinkedHashMapKeyValueNew.get("#登录预定日年号#"));
		LinkedHashMapKeyValueNew.put("#基準適用期間終了年#", LinkedHashMapKeyValueNew.get("#登录预定日年#"));
		LinkedHashMapKeyValueNew.put("#基準適用期間終了月#", "12");
		LinkedHashMapKeyValueNew.put("#基準適用期間終了日#", "31");

		if (StringUtils.isEmpty(LinkedHashMapKeyValueNew.get("#申請年度の基準期間#")) == false
				&& !"-".equals(LinkedHashMapKeyValueNew.get("#申請年度の基準期間#"))) {
			wareki = KanaLogic.getWareki(LinkedHashMapKeyValueNew.get("#申請年度の基準期間#")).split(",");

			//
			innerkeyNew = "#基準適用の基準期間開始年号#";
			innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			//
			innerkeyNew = "#基準適用の基準期間開始年#";
			innerValueNew = wareki[1];
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#基準適用の基準期間開始月#", "1");
			LinkedHashMapKeyValueNew.put("#基準適用の基準期間開始日#", "1");

			//
			innerkeyNew = "#基準適用の基準期間終了年号#";
			innerValueNew = FuncUtils.convertToEra(Integer.parseInt(wareki[0]));
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			//
			innerkeyNew = "#基準適用の基準期間終了年#";
			innerValueNew = wareki[1];
			LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

			LinkedHashMapKeyValueNew.put("#基準適用の基準期間終了月#", "12");
			LinkedHashMapKeyValueNew.put("#基準適用の基準期間終了日#", "31");
		}

		LinkedHashMapKeyValueNew.put("#基準適用の基準期間総売上#", LinkedHashMapKeyValueNew.get("#申請年度の基準期間課税売上#"));
		LinkedHashMapKeyValueNew.put("#基準適用の基準期間課税売上#", LinkedHashMapKeyValueNew.get("#申請年度の基準期間課税売上#"));

		LinkedHashMapKeyValueNew.put("#新設該当事業年度開始年号#", LinkedHashMapKeyValueNew.get("#外国本店設立年号#"));
		LinkedHashMapKeyValueNew.put("#新設該当事業年度開始年#", LinkedHashMapKeyValueNew.get("#外国本店設立年#"));
		LinkedHashMapKeyValueNew.put("#新設該当事業年度開始月#", LinkedHashMapKeyValueNew.get("#外国本店設立月#"));
		LinkedHashMapKeyValueNew.put("#新設該当事業年度開始日#", LinkedHashMapKeyValueNew.get("#外国本店設立日#"));

		LinkedHashMapKeyValueNew.put("", LinkedHashMapKeyValueNew.get(""));

		//                	//
		//    	            innerkeyNew = "";
		//            		innerValueNew = "";
		//                	LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);
		//

		logger.info(yyyymmdd_count + ", #作成日点点式#, " + LinkedHashMapKeyValueNew.get("#作成日点点式#"));
		logger.info(yyyymmdd_count + ", #作成日西暦年#, " + LinkedHashMapKeyValueNew.get("#作成日西暦年#"));
		logger.info(yyyymmdd_count + ", #作成日年号#, " + LinkedHashMapKeyValueNew.get("#作成日年号#"));
		logger.info(yyyymmdd_count + ", #作成日年#, " + LinkedHashMapKeyValueNew.get("#作成日年#"));
		logger.info(yyyymmdd_count + ", #作成日月#, " + LinkedHashMapKeyValueNew.get("#作成日月#"));
		logger.info(yyyymmdd_count + ", #作成日日#, " + LinkedHashMapKeyValueNew.get("#作成日日#"));
		logger.info(yyyymmdd_count + ", #登录预定日西暦年#, " + LinkedHashMapKeyValueNew.get("#登录预定日西暦年#"));
		logger.info(yyyymmdd_count + ", #登录预定日年号#, " + LinkedHashMapKeyValueNew.get("#登录预定日年号#"));
		logger.info(yyyymmdd_count + ", #登录预定日年#, " + LinkedHashMapKeyValueNew.get("#登录预定日年#"));
		logger.info(yyyymmdd_count + ", #登录预定日月#, " + LinkedHashMapKeyValueNew.get("#登录预定日月#"));
		logger.info(yyyymmdd_count + ", #登录预定日日#, " + LinkedHashMapKeyValueNew.get("#登录预定日日#"));
		logger.info(yyyymmdd_count + ", #達人コード#, " + LinkedHashMapKeyValueNew.get("#達人コード#"));
		logger.info(yyyymmdd_count + ", #会社名自国語全部#, " + LinkedHashMapKeyValueNew.get("#会社名自国語全部#"));
		logger.info(yyyymmdd_count + ", #会社外国本店自国語全部#, " + LinkedHashMapKeyValueNew.get("#会社外国本店自国語全部#"));
		logger.info(yyyymmdd_count + ", #代表者名自国語#, " + LinkedHashMapKeyValueNew.get("#代表者名自国語#"));
		logger.info(yyyymmdd_count + ", #会社名フリガナ#, " + LinkedHashMapKeyValueNew.get("#会社名フリガナ#"));
		logger.info(yyyymmdd_count + ", #会社名フリガナ全部#, " + LinkedHashMapKeyValueNew.get("#会社名フリガナ全部#"));
		logger.info(yyyymmdd_count + ", #会社名英語#, " + LinkedHashMapKeyValueNew.get("#会社名英語#"));
		logger.info(yyyymmdd_count + ", #会社名英語全部#, " + LinkedHashMapKeyValueNew.get("#会社名英語全部#"));
		logger.info(yyyymmdd_count + ", #会社外国本店フリガナ#, " + LinkedHashMapKeyValueNew.get("#会社外国本店フリガナ#"));
		logger.info(yyyymmdd_count + ", #会社外国本店フリガナ全部#, " + LinkedHashMapKeyValueNew.get("#会社外国本店フリガナ全部#"));
		logger.info(yyyymmdd_count + ", #会社外国本店英語#, " + LinkedHashMapKeyValueNew.get("#会社外国本店英語#"));
		logger.info(yyyymmdd_count + ", #会社外国本店英語全部#, " + LinkedHashMapKeyValueNew.get("#会社外国本店英語全部#"));
		logger.info(yyyymmdd_count + ", #納税地郵便番号1#, " + LinkedHashMapKeyValueNew.get("#納税地郵便番号1#"));
		logger.info(yyyymmdd_count + ", #納税地郵便番号2#, " + LinkedHashMapKeyValueNew.get("#納税地郵便番号2#"));
		logger.info(yyyymmdd_count + ", #納税地フリガナ#, " + LinkedHashMapKeyValueNew.get("#納税地フリガナ#"));
		logger.info(yyyymmdd_count + ", #納税地#, " + LinkedHashMapKeyValueNew.get("#納税地#"));
		logger.info(yyyymmdd_count + ", #外国本店電話地区番号#, " + LinkedHashMapKeyValueNew.get("#外国本店電話地区番号#"));
		logger.info(yyyymmdd_count + ", #外国本店電話1#, " + LinkedHashMapKeyValueNew.get("#外国本店電話1#"));
		logger.info(yyyymmdd_count + ", #外国本店電話2#, " + LinkedHashMapKeyValueNew.get("#外国本店電話2#"));
		logger.info(yyyymmdd_count + ", #外国本店電話3#, " + LinkedHashMapKeyValueNew.get("#外国本店電話3#"));
		logger.info(yyyymmdd_count + ", #外国本店設立西暦#, " + LinkedHashMapKeyValueNew.get("#外国本店設立西暦#"));
		logger.info(yyyymmdd_count + ", #外国本店設立公元年#, " + LinkedHashMapKeyValueNew.get("#外国本店設立公元年#"));
		logger.info(yyyymmdd_count + ", #外国本店設立年号#, " + LinkedHashMapKeyValueNew.get("#外国本店設立年号#"));
		logger.info(yyyymmdd_count + ", #外国本店設立年#, " + LinkedHashMapKeyValueNew.get("#外国本店設立年#"));
		logger.info(yyyymmdd_count + ", #外国本店設立月#, " + LinkedHashMapKeyValueNew.get("#外国本店設立月#"));
		logger.info(yyyymmdd_count + ", #外国本店設立日#, " + LinkedHashMapKeyValueNew.get("#外国本店設立日#"));
		logger.info(yyyymmdd_count + ", #外国本店事業年度開始月#, " + LinkedHashMapKeyValueNew.get("#外国本店事業年度開始月#"));
		logger.info(yyyymmdd_count + ", #外国本店事業年度開始日#, " + LinkedHashMapKeyValueNew.get("#外国本店事業年度開始日#"));
		logger.info(yyyymmdd_count + ", #外国本店事業年度終了月#, " + LinkedHashMapKeyValueNew.get("#外国本店事業年度終了月#"));
		logger.info(yyyymmdd_count + ", #外国本店事業年度終了日#, " + LinkedHashMapKeyValueNew.get("#外国本店事業年度終了日#"));
		logger.info(yyyymmdd_count + ", #資本金#, " + LinkedHashMapKeyValueNew.get("#資本金#"));
		logger.info(yyyymmdd_count + ", #PEありますか#, " + LinkedHashMapKeyValueNew.get("#PEありますか#"));
		logger.info(yyyymmdd_count + ", #申請年度の基準期間課税売上#, " + LinkedHashMapKeyValueNew.get("#申請年度の基準期間課税売上#"));
		logger.info(yyyymmdd_count + ", #申請年度の特定期間課税売上#, " + LinkedHashMapKeyValueNew.get("#申請年度の特定期間課税売上#"));
		logger.info(yyyymmdd_count + ", #申請年度の特定期間支払給与#, " + LinkedHashMapKeyValueNew.get("#申請年度の特定期間支払給与#"));
		logger.info(yyyymmdd_count + ", #申請年度の昨年度売上#, " + LinkedHashMapKeyValueNew.get("#申請年度の昨年度売上#"));
		logger.info(yyyymmdd_count + ", #申請簡易課税漢字#, " + LinkedHashMapKeyValueNew.get("#申請簡易課税漢字#"));
		logger.info(yyyymmdd_count + ", #申請簡易課税01#, " + LinkedHashMapKeyValueNew.get("#申請簡易課税01#"));
		logger.info(yyyymmdd_count + ", #税務署名#, " + LinkedHashMapKeyValueNew.get("#税務署名#"));
		logger.info(yyyymmdd_count + ", #代表者名フリガナ#, " + LinkedHashMapKeyValueNew.get("#代表者名フリガナ#"));
		logger.info(yyyymmdd_count + ", #代表者名英語#, " + LinkedHashMapKeyValueNew.get("#代表者名英語#"));
		logger.info(yyyymmdd_count + ", #代表者名英語全部#, " + LinkedHashMapKeyValueNew.get("#代表者名英語全部#"));
		logger.info(yyyymmdd_count + ", #代表者住所フリガナ#, " + LinkedHashMapKeyValueNew.get("#代表者住所フリガナ#"));
		logger.info(yyyymmdd_count + ", #代表者住所#, " + LinkedHashMapKeyValueNew.get("#代表者住所#"));
		logger.info(yyyymmdd_count + ", #代表者住所郵便1#, " + LinkedHashMapKeyValueNew.get("#代表者住所郵便1#"));
		logger.info(yyyymmdd_count + ", #代表者住所郵便2#, " + LinkedHashMapKeyValueNew.get("#代表者住所郵便2#"));
		logger.info(yyyymmdd_count + ", #外国法人事業内容自国語#, " + LinkedHashMapKeyValueNew.get("#外国法人事業内容自国語#"));
		logger.info(yyyymmdd_count + ", #外国法人事業内容#, " + LinkedHashMapKeyValueNew.get("#外国法人事業内容#"));
		logger.info(yyyymmdd_count + ", #業種#, " + LinkedHashMapKeyValueNew.get("#業種#"));
		logger.info(yyyymmdd_count + ", #法人番号#, " + LinkedHashMapKeyValueNew.get("#法人番号#"));
		logger.info(yyyymmdd_count + ", #利用者識別番号#, " + LinkedHashMapKeyValueNew.get("#利用者識別番号#"));

		logger.info(yyyymmdd_count + ", #作为免税事业者您想从下一个会计年度的首日开始登录吗#, "
				+ LinkedHashMapKeyValueNew.get("#作为免税事业者您想从下一个会计年度的首日开始登录吗#"));
		logger.info(yyyymmdd_count + ", #您想从本会计期间的首日开始登录吗#, " + LinkedHashMapKeyValueNew.get("#您想从本会计期间的首日开始登录吗#"));

		logger.info(yyyymmdd_count + ", #税務代理人住所郵便1#, " + LinkedHashMapKeyValueNew.get("#税務代理人住所郵便1#"));
		logger.info(yyyymmdd_count + ", #税務代理人住所郵便2#, " + LinkedHashMapKeyValueNew.get("#税務代理人住所郵便2#"));
		logger.info(yyyymmdd_count + ", #税務代理人住所カナ#, " + LinkedHashMapKeyValueNew.get("#税務代理人住所カナ#"));
		logger.info(yyyymmdd_count + ", #税務代理人住所#, " + LinkedHashMapKeyValueNew.get("#税務代理人住所#"));
		logger.info(yyyymmdd_count + ", #税務代理人電話1#, " + LinkedHashMapKeyValueNew.get("#税務代理人電話1#"));
		logger.info(yyyymmdd_count + ", #税務代理人電話2#, " + LinkedHashMapKeyValueNew.get("#税務代理人電話2#"));
		logger.info(yyyymmdd_count + ", #税務代理人電話3#, " + LinkedHashMapKeyValueNew.get("#税務代理人電話3#"));
		logger.info(yyyymmdd_count + ", #税務代理人氏名カナ#, " + LinkedHashMapKeyValueNew.get("#税務代理人氏名カナ#"));
		logger.info(yyyymmdd_count + ", #税務代理人氏名#, " + LinkedHashMapKeyValueNew.get("#税務代理人氏名#"));

		logger.info(yyyymmdd_count + ", #納税管理人住所郵便1#, " + LinkedHashMapKeyValueNew.get("#納税管理人住所郵便1#"));
		logger.info(yyyymmdd_count + ", #納税管理人住所郵便2#, " + LinkedHashMapKeyValueNew.get("#納税管理人住所郵便2#"));
		logger.info(yyyymmdd_count + ", #納税管理人住所カナ#, " + LinkedHashMapKeyValueNew.get("#納税管理人住所カナ#"));
		logger.info(yyyymmdd_count + ", #納税管理人住所#, " + LinkedHashMapKeyValueNew.get("#納税管理人住所#"));
		logger.info(yyyymmdd_count + ", #納税管理人電話1#, " + LinkedHashMapKeyValueNew.get("#納税管理人電話1#"));
		logger.info(yyyymmdd_count + ", #納税管理人電話2#, " + LinkedHashMapKeyValueNew.get("#納税管理人電話2#"));
		logger.info(yyyymmdd_count + ", #納税管理人電話3#, " + LinkedHashMapKeyValueNew.get("#納税管理人電話3#"));
		logger.info(yyyymmdd_count + ", #納税管理人氏名カナ#, " + LinkedHashMapKeyValueNew.get("#納税管理人氏名カナ#"));
		logger.info(yyyymmdd_count + ", #納税管理人氏名#, " + LinkedHashMapKeyValueNew.get("#納税管理人氏名#"));
		logger.info(yyyymmdd_count + ", #納税管理人代表者氏名カナ#, " + LinkedHashMapKeyValueNew.get("#納税管理人代表者氏名カナ#"));
		logger.info(yyyymmdd_count + ", #納税管理人代表者氏名#, " + LinkedHashMapKeyValueNew.get("#納税管理人代表者氏名#"));

		logger.info(yyyymmdd_count + ", #何期目#, " + LinkedHashMapKeyValueNew.get("#何期目#"));
		logger.info(yyyymmdd_count + ", #新設法人ですか#, " + LinkedHashMapKeyValueNew.get("#新設法人ですか#"));
		logger.info(yyyymmdd_count + ", #申請年度の基準期間#, " + LinkedHashMapKeyValueNew.get("#申請年度の基準期間#"));
		logger.info(yyyymmdd_count + ", #申請年度の特定期間#, " + LinkedHashMapKeyValueNew.get("#申請年度の特定期間#"));
		logger.info(yyyymmdd_count + ", #申請年度の昨年度#, " + LinkedHashMapKeyValueNew.get("#申請年度の昨年度#"));
		logger.info(yyyymmdd_count + ", #課税事業者ですか#, " + LinkedHashMapKeyValueNew.get("#課税事業者ですか#"));

		logger.info(yyyymmdd_count + ", #从本期开始简易可以吗#, " + LinkedHashMapKeyValueNew.get("#从本期开始简易可以吗#"));
		logger.info(yyyymmdd_count + ", #从下一期开始简易可以吗#, " + LinkedHashMapKeyValueNew.get("#从下一期开始简易可以吗#"));
		logger.info(yyyymmdd_count + ", #我可以申请简易课税吗#, " + LinkedHashMapKeyValueNew.get("#我可以申请简易课税吗#"));

		logger.info(yyyymmdd_count + ", #特定国外事業者#, " + LinkedHashMapKeyValueNew.get("#特定国外事業者#"));
		logger.info(yyyymmdd_count + ", #非特定国外事業者#, " + LinkedHashMapKeyValueNew.get("#非特定国外事業者#"));

		logger.info(yyyymmdd_count + ", #課税事業者非新#, " + LinkedHashMapKeyValueNew.get("#課税事業者非新#"));
		logger.info(yyyymmdd_count + ", #免税事業者非新#, " + LinkedHashMapKeyValueNew.get("#免税事業者非新#"));
		logger.info(yyyymmdd_count + ", #ETAXによる通知#, " + LinkedHashMapKeyValueNew.get("#ETAXによる通知#"));
		logger.info(yyyymmdd_count + ", #新事業者#, " + LinkedHashMapKeyValueNew.get("#新事業者#"));
		logger.info(yyyymmdd_count + ", #課税期間初日から登録#, " + LinkedHashMapKeyValueNew.get("#課税期間初日から登録#"));
		logger.info(yyyymmdd_count + ", #上記以外の課税事業者#, " + LinkedHashMapKeyValueNew.get("#上記以外の課税事業者#"));
		logger.info(yyyymmdd_count + ", #上記以外の免税事業者#, " + LinkedHashMapKeyValueNew.get("#上記以外の免税事業者#"));
		logger.info(yyyymmdd_count + ", #課税期間初日から登録年号#, " + LinkedHashMapKeyValueNew.get("#課税期間初日から登録年号#"));
		logger.info(yyyymmdd_count + ", #課税期間初日から登録年#, " + LinkedHashMapKeyValueNew.get("#課税期間初日から登録年#"));
		logger.info(yyyymmdd_count + ", #課税期間初日から登録月#, " + LinkedHashMapKeyValueNew.get("#課税期間初日から登録月#"));
		logger.info(yyyymmdd_count + ", #課税期間初日から登録日#, " + LinkedHashMapKeyValueNew.get("#課税期間初日から登録日#"));

		logger.info(yyyymmdd_count + ", #免税事業者の確認第1チェック#, " + LinkedHashMapKeyValueNew.get("#免税事業者の確認第1チェック#"));
		logger.info(yyyymmdd_count + ", #第1チェック設立年号#, " + LinkedHashMapKeyValueNew.get("#第1チェック設立年号#"));
		logger.info(yyyymmdd_count + ", #第1チェック設立年#, " + LinkedHashMapKeyValueNew.get("#第1チェック設立年#"));
		logger.info(yyyymmdd_count + ", #第1チェック設立月#, " + LinkedHashMapKeyValueNew.get("#第1チェック設立月#"));
		logger.info(yyyymmdd_count + ", #第1チェック設立日#, " + LinkedHashMapKeyValueNew.get("#第1チェック設立日#"));
		logger.info(yyyymmdd_count + ", #第1チェック事業年度開始月#, " + LinkedHashMapKeyValueNew.get("#第1チェック事業年度開始月#"));
		logger.info(yyyymmdd_count + ", #第1チェック事業年度開始日#, " + LinkedHashMapKeyValueNew.get("#第1チェック事業年度開始日#"));
		logger.info(yyyymmdd_count + ", #第1チェック事業年度終了月#, " + LinkedHashMapKeyValueNew.get("#第1チェック事業年度終了月#"));
		logger.info(yyyymmdd_count + ", #第1チェック事業年度終了日#, " + LinkedHashMapKeyValueNew.get("#第1チェック事業年度終了日#"));
		logger.info(yyyymmdd_count + ", #第1チェック資本金#, " + LinkedHashMapKeyValueNew.get("#第1チェック資本金#"));
		logger.info(yyyymmdd_count + ", #第1チェック登録希望日年号#, " + LinkedHashMapKeyValueNew.get("#第1チェック登録希望日年号#"));
		logger.info(yyyymmdd_count + ", #第1チェック登録希望日年#, " + LinkedHashMapKeyValueNew.get("#第1チェック登録希望日年#"));
		logger.info(yyyymmdd_count + ", #第1チェック登録希望日月#, " + LinkedHashMapKeyValueNew.get("#第1チェック登録希望日月#"));
		logger.info(yyyymmdd_count + ", #第1チェック登録希望日日#, " + LinkedHashMapKeyValueNew.get("#第1チェック登録希望日日#"));

		logger.info(yyyymmdd_count + ", #免税事業者の確認第2チェック#, " + LinkedHashMapKeyValueNew.get("#免税事業者の確認第2チェック#"));
		logger.info(yyyymmdd_count + ", #第2チェック翌課税初日年号#, " + LinkedHashMapKeyValueNew.get("#第2チェック翌課税初日年号#"));
		logger.info(yyyymmdd_count + ", #第2チェック翌課税初日年#, " + LinkedHashMapKeyValueNew.get("#第2チェック翌課税初日年#"));
		logger.info(yyyymmdd_count + ", #第2チェック翌課税初日月#, " + LinkedHashMapKeyValueNew.get("#第2チェック翌課税初日月#"));
		logger.info(yyyymmdd_count + ", #第2チェック翌課税初日日#, " + LinkedHashMapKeyValueNew.get("#第2チェック翌課税初日日#"));

		logger.info(yyyymmdd_count + ", #免税事業者の確認第3チェック#, " + LinkedHashMapKeyValueNew.get("#免税事業者の確認第3チェック#"));

		logger.info(yyyymmdd_count + ", #特定以外国内事業所カナ#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所カナ#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所郵便1#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所郵便1#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所郵便2#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所郵便2#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所住所#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所住所#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所電話1#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所電話1#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所電話2#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所電話2#"));
		logger.info(yyyymmdd_count + ", #特定以外国内事業所電話3#, " + LinkedHashMapKeyValueNew.get("#特定以外国内事業所電話3#"));

		logger.info(yyyymmdd_count + ", #添付資料第1チェック謄本定款#, " + LinkedHashMapKeyValueNew.get("#添付資料第1チェック謄本定款#"));
		logger.info(yyyymmdd_count + ", #添付資料第2チェック代理証書#, " + LinkedHashMapKeyValueNew.get("#添付資料第2チェック代理証書#"));
		logger.info(yyyymmdd_count + ", #添付資料第3チェックHP#, " + LinkedHashMapKeyValueNew.get("#添付資料第3チェックHP#"));
		logger.info(yyyymmdd_count + ", #添付資料第4チェックその他#, " + LinkedHashMapKeyValueNew.get("#添付資料第4チェックその他#"));

		logger.info(yyyymmdd_count + ", #納税管理人届出書提出日年号#, " + LinkedHashMapKeyValueNew.get("#納税管理人届出書提出日年号#"));
		logger.info(yyyymmdd_count + ", #納税管理人届出書提出日年#, " + LinkedHashMapKeyValueNew.get("#納税管理人届出書提出日年#"));
		logger.info(yyyymmdd_count + ", #納税管理人届出書提出日月#, " + LinkedHashMapKeyValueNew.get("#納税管理人届出書提出日月#"));
		logger.info(yyyymmdd_count + ", #納税管理人届出書提出日日#, " + LinkedHashMapKeyValueNew.get("#納税管理人届出書提出日日#"));

		logger.info(yyyymmdd_count + ", #届出者との関係#, " + LinkedHashMapKeyValueNew.get("#届出者との関係#"));
		logger.info(yyyymmdd_count + ", #職業または事業内容#, " + LinkedHashMapKeyValueNew.get("#職業または事業内容#"));
		logger.info(yyyymmdd_count + ", #定めた理由#, " + LinkedHashMapKeyValueNew.get("#定めた理由#"));

		logger.info(yyyymmdd_count + ", #事業区分#, " + LinkedHashMapKeyValueNew.get("#事業区分#"));
		logger.info(yyyymmdd_count + ", #所得消費税法改正チェック#, " + LinkedHashMapKeyValueNew.get("#所得消費税法改正チェック#"));
		logger.info(yyyymmdd_count + ", #イロハ該当#, " + LinkedHashMapKeyValueNew.get("#イロハ該当#"));
		logger.info(yyyymmdd_count + ", #イロハ非該当#, " + LinkedHashMapKeyValueNew.get("#イロハ非該当#"));
		logger.info(yyyymmdd_count + ", #イ該当#, " + LinkedHashMapKeyValueNew.get("#イ該当#"));
		logger.info(yyyymmdd_count + ", #イ該当課税事業者なった日年号#, " + LinkedHashMapKeyValueNew.get("#イ該当課税事業者なった日年号#"));
		logger.info(yyyymmdd_count + ", #イ該当課税事業者なった日年#, " + LinkedHashMapKeyValueNew.get("#イ該当課税事業者なった日年#"));
		logger.info(yyyymmdd_count + ", #イ該当課税事業者なった日月#, " + LinkedHashMapKeyValueNew.get("#イ該当課税事業者なった日月#"));
		logger.info(yyyymmdd_count + ", #イ該当課税事業者なった日日#, " + LinkedHashMapKeyValueNew.get("#イ該当課税事業者なった日日#"));
		logger.info(yyyymmdd_count + ", #ロ該当#, " + LinkedHashMapKeyValueNew.get("#ロ該当#"));
		logger.info(yyyymmdd_count + ", #ロ該当課税事業者なった日年号#, " + LinkedHashMapKeyValueNew.get("#ロ該当課税事業者なった日年号#"));
		logger.info(yyyymmdd_count + ", #ロ該当課税事業者なった日年#, " + LinkedHashMapKeyValueNew.get("#ロ該当課税事業者なった日年#"));
		logger.info(yyyymmdd_count + ", #ロ該当課税事業者なった日月#, " + LinkedHashMapKeyValueNew.get("#ロ該当課税事業者なった日月#"));
		logger.info(yyyymmdd_count + ", #ロ該当課税事業者なった日日#, " + LinkedHashMapKeyValueNew.get("#ロ該当課税事業者なった日日#"));

		logger.info(yyyymmdd_count + ", #簡易適用年度の基準期間課税売上#, " + LinkedHashMapKeyValueNew.get("#簡易適用年度の基準期間課税売上#"));

		logger.info(yyyymmdd_count + ", #簡易適用期間開始年号#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間開始年号#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間開始年#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間開始年#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間開始月#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間開始月#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間開始日#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間開始日#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間終了年号#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間終了年号#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間終了年#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間終了年#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間終了月#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間終了月#"));
		logger.info(yyyymmdd_count + ", #簡易適用期間終了日#, " + LinkedHashMapKeyValueNew.get("#簡易適用期間終了日#"));

		logger.info(yyyymmdd_count + ", #簡易適用の基準期間開始年号#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間開始年号#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間開始年#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間開始年#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間開始月#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間開始月#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間開始日#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間開始日#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間終了年号#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間終了年号#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間終了年#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間終了年#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間終了月#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間終了月#"));
		logger.info(yyyymmdd_count + ", #簡易適用の基準期間終了日#, " + LinkedHashMapKeyValueNew.get("#簡易適用の基準期間終了日#"));

		logger.info(yyyymmdd_count + ", #基準適用期間開始年号#, " + LinkedHashMapKeyValueNew.get("#基準適用期間開始年号#"));
		logger.info(yyyymmdd_count + ", #基準適用期間開始年#, " + LinkedHashMapKeyValueNew.get("#基準適用期間開始年#"));
		logger.info(yyyymmdd_count + ", #基準適用期間開始月#, " + LinkedHashMapKeyValueNew.get("#基準適用期間開始月#"));
		logger.info(yyyymmdd_count + ", #基準適用期間開始日#, " + LinkedHashMapKeyValueNew.get("#基準適用期間開始日#"));
		logger.info(yyyymmdd_count + ", #基準適用期間終了年号#, " + LinkedHashMapKeyValueNew.get("#基準適用期間終了年号#"));
		logger.info(yyyymmdd_count + ", #基準適用期間終了年#, " + LinkedHashMapKeyValueNew.get("#基準適用期間終了年#"));
		logger.info(yyyymmdd_count + ", #基準適用期間終了月#, " + LinkedHashMapKeyValueNew.get("#基準適用期間終了月#"));
		logger.info(yyyymmdd_count + ", #基準適用期間終了日#, " + LinkedHashMapKeyValueNew.get("#基準適用期間終了日#"));

		logger.info(yyyymmdd_count + ", #基準適用の基準期間開始年号#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間開始年号#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間開始年#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間開始年#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間開始月#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間開始月#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間開始日#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間開始日#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間終了年号#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間終了年号#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間終了年#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間終了年#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間終了月#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間終了月#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間終了日#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間終了日#"));

		logger.info(yyyymmdd_count + ", #基準適用の基準期間総売上#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間総売上#"));
		logger.info(yyyymmdd_count + ", #基準適用の基準期間課税売上#, " + LinkedHashMapKeyValueNew.get("#基準適用の基準期間課税売上#"));

		logger.info(yyyymmdd_count + ", #新設該当事業年度開始年号#, " + LinkedHashMapKeyValueNew.get("#新設該当事業年度開始年号#"));
		logger.info(yyyymmdd_count + ", #新設該当事業年度開始年#, " + LinkedHashMapKeyValueNew.get("#新設該当事業年度開始年#"));
		logger.info(yyyymmdd_count + ", #新設該当事業年度開始月#, " + LinkedHashMapKeyValueNew.get("#新設該当事業年度開始月#"));
		logger.info(yyyymmdd_count + ", #新設該当事業年度開始日#, " + LinkedHashMapKeyValueNew.get("#新設該当事業年度開始日#"));

	}

	private void setHashMapDataBase(LinkedHashMap<String, String> LinkedHashMapKeyValueNew,
			LinkedHashMap<String, String> LinkedHashMapKeyValue, String yyyymmdd_count, HashMap<String, String> value) {
		//key和value mapping
		for (Map.Entry<String, String> innerEntry : LinkedHashMapKeyValue.entrySet()) {
			String innerKey = innerEntry.getKey(); // 获取内部 HashMap 的键
			if (innerEntry.getValue() == null) {
				continue;
			}
			String innerValue = value.get(innerEntry.getValue()); // 获取内部 HashMap 的值
			if (StringUtils.isEmpty(innerValue) == true) {
				continue;
			}
			LinkedHashMapKeyValueNew.put(innerKey, innerValue.replaceAll("\n", " "));
			logger.info("yyyymmdd_count: " + yyyymmdd_count + ", " + innerKey + ", " + innerValue);
		}

		LinkedHashMapKeyValueNew.put("#納税地郵便番号1#", "112");
		LinkedHashMapKeyValueNew.put("#納税地郵便番号2#", "0011");
		LinkedHashMapKeyValueNew.put("#納税地フリガナ#", "トウキョウトブンキョウクセンゴク");
		LinkedHashMapKeyValueNew.put("#納税地#", "東京都文京区千石４丁目１４番９号１階");

		LinkedHashMapKeyValueNew.put("#外国本店事業年度開始月#", "1");
		LinkedHashMapKeyValueNew.put("#外国本店事業年度開始日#", "1");
		LinkedHashMapKeyValueNew.put("#外国本店事業年度終了月#", "12");
		LinkedHashMapKeyValueNew.put("#外国本店事業年度終了日#", "31");

		LinkedHashMapKeyValueNew.put("#税務署名#", "京橋");

		LinkedHashMapKeyValueNew.put("#代表者住所フリガナ#", "チュウカジンミンキョウワコク");
		LinkedHashMapKeyValueNew.put("#代表者住所#", "中華人民共和国");
		LinkedHashMapKeyValueNew.put("#代表者住所郵便1#", "000");
		LinkedHashMapKeyValueNew.put("#代表者住所郵便2#", "0000");
		LinkedHashMapKeyValueNew.put("#外国法人事業内容自国語#", "网店售货，进出口贸易");
		LinkedHashMapKeyValueNew.put("#外国法人事業内容#", "ECショップ販売、輸出入");
		LinkedHashMapKeyValueNew.put("#業種#", "小売業");

		LinkedHashMapKeyValueNew.put("#税務代理人住所郵便1#", "104");
		LinkedHashMapKeyValueNew.put("#税務代理人住所郵便2#", "0061");
		LinkedHashMapKeyValueNew.put("#税務代理人住所カナ#", "トウキョウトチュウオウクギンザ");
		LinkedHashMapKeyValueNew.put("#税務代理人住所#", "東京都中央区銀座8-8-5 陽栄銀座ビル4階");
		LinkedHashMapKeyValueNew.put("#税務代理人電話1#", "03");
		LinkedHashMapKeyValueNew.put("#税務代理人電話2#", "6264");
		LinkedHashMapKeyValueNew.put("#税務代理人電話3#", "3477");
		LinkedHashMapKeyValueNew.put("#税務代理人氏名カナ#", "ビーピーエスコクサイゼイリシホウジン");
		LinkedHashMapKeyValueNew.put("#税務代理人氏名#", "ＢＰＳ国際税理士法人");

		LinkedHashMapKeyValueNew.put("#納税管理人住所郵便1#", "112");
		LinkedHashMapKeyValueNew.put("#納税管理人住所郵便2#", "0011");
		LinkedHashMapKeyValueNew.put("#納税管理人住所カナ#", "トウキョウトブンキョウクセンゴク");
		LinkedHashMapKeyValueNew.put("#納税管理人住所#", "東京都文京区千石４丁目１４番９号１階");
		LinkedHashMapKeyValueNew.put("#納税管理人電話1#", "03");
		LinkedHashMapKeyValueNew.put("#納税管理人電話2#", "5981");
		LinkedHashMapKeyValueNew.put("#納税管理人電話3#", "8383");
		LinkedHashMapKeyValueNew.put("#納税管理人氏名カナ#", "パンダサービスカブシキガイシャ");
		LinkedHashMapKeyValueNew.put("#納税管理人氏名#", "ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社");
		LinkedHashMapKeyValueNew.put("#納税管理人代表者氏名カナ#", "フクザワ　ヒデヨシ");
		LinkedHashMapKeyValueNew.put("#納税管理人代表者氏名#", "福沢　秀吉");
	}

	private void getTianpuPDF(String folderPathFileData, AllToPDF JpgToPDF, String yyyymmdd_count,
			HashMap<String, String> value, String outportPathNew) throws Exception {
		logger.info("getTianpuPDF start");
		List<String> filePathNames = new ArrayList<String>();
		// 创建一个 File 对象表示文件夹
		File folder = new File(folderPathFileData + "/" + yyyymmdd_count + "_" + value.get("CompanyName_Chinese"));
		// 获取文件夹中的所有文件
		File[] files = folder.listFiles();
		int i = 0;
		if (files != null) {
			// 添付資料循环
			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					String extension = fileName.substring(fileName.lastIndexOf("."));
					fileName = fileName.replaceAll(extension, ".pdf");
					fileName = yyyymmdd_count + "_添付資料" + ++i + "_" + fileName;

					if (".jpg".equals(extension.toLowerCase()) || ".png".equals(extension.toLowerCase())) {
						JpgToPDF.imageToPDF(file.getPath(), outportPathNew + "/" + fileName);

					} else if (".pdf".equals(extension.toLowerCase())) {
						Path p1 = Paths.get(file.getPath());
						Path p2 = Paths.get(outportPathNew + "/" + fileName);
						//TODO
						Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);

					}
					filePathNames.add(outportPathNew + "/" + fileName);
					logger.info(fileName + " -> ok");

				}
			}
			//添付資料PDF合并成一个
			//TODO BUG什么都没有的时候也会生成个空的PDF
			//		    String fileName = outportPathNew + "/"  + yyyymmdd_count + "_添付資料ALL.pdf";
			//			JpgToPDF.mergePDF(filePathNames, fileName);
			//		    logger.info(fileName + " -> ok");
		} else {
			logger.info("添付資料 -> No files");

		}

		logger.info("getTianpuPDF end");
	}

	private void getTianpuCopy(String folderPathFileData, String yyyyMMddHHmmss, AllToPDF JpgToPDF,
			String yyyymmdd_count,
			HashMap<String, String> value, String outportPathNew) throws Exception {
		logger.info("getTianpuCopy start");
		logger.info(folderPathFileData + "/" + yyyymmdd_count + "_" + value.get("CompanyName_Chinese"));
		// 创建一个 File 对象表示文件夹
		File folder = new File(folderPathFileData + "/" + yyyymmdd_count + "_" + value.get("CompanyName_Chinese"));
		// 获取文件夹中的所有文件
		File[] files = folder.listFiles();
		int i = 0;
		if (files != null) {
			// 添付資料循环
			for (File file : files) {
				if (file.isFile()) {
					String fileName = file.getName();
					fileName = yyyymmdd_count + "_添付資料" + ++i + "_" + fileName;

					Path p1 = Paths.get(file.getPath());
					Path p2 = Paths.get(outportPathNew + "/" + fileName);
					Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);
					logger.info(fileName + " -> ok");

				}
			}
		} else {
			logger.info("添付資料 -> No files");

		}

		logger.info("getTianpuCopy end");
	}

	private void setOwner(String newOwnerName, String osName, String outportPathNew, String targetZipFilePath) {
		// 您可以根据不同的系统类型执行不同的操作
		if (osName.toLowerCase().contains("windows")) {
			logger.info("这是Windows系统");
			// 在Windows系统上执行特定操作
		} else if (osName.toLowerCase().contains("linux")) {
			logger.info("这是Linux系统");
			// 在Linux系统上执行特定操作

			//文件夹设置所有者
			try {
				// 获取文件夹路径的Path对象
				Path folder = Paths.get(outportPathNew);

				// 获取UserPrincipalLookupService
				UserPrincipalLookupService lookupService = folder.getFileSystem()
						.getUserPrincipalLookupService();

				// 通过用户名查找UserPrincipal
				UserPrincipal newOwner = lookupService.lookupPrincipalByName(newOwnerName);

				// 递归地修改文件夹及其子文件夹和子文件的所有者
				Files.walkFileTree(folder, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
						new FileOwnerVisitor(newOwner));

				logger.info("文件夹及其子文件夹和子文件的新所有者设置为：" + newOwnerName);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//ZIP设置所有者
			try {
				// 获取文件路径的Path对象
				Path file = Paths.get(targetZipFilePath);

				// 获取文件的FileOwnerAttributeView
				FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(file,
						FileOwnerAttributeView.class);

				// 获取UserPrincipalLookupService
				UserPrincipalLookupService lookupService = file.getFileSystem().getUserPrincipalLookupService();

				// 通过用户名查找UserPrincipal
				UserPrincipal newOwner = lookupService.lookupPrincipalByName(newOwnerName);

				// 设置新的文件所有者
				ownerAttributeView.setOwner(newOwner);

				logger.info("文件的新所有者设置为：" + newOwnerName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 自定义FileVisitor来修改所有者
	static class FileOwnerVisitor extends SimpleFileVisitor<Path> {
		private UserPrincipal newOwner;

		public FileOwnerVisitor(UserPrincipal newOwner) {
			this.newOwner = newOwner;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
			FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(file, FileOwnerAttributeView.class);
			ownerAttributeView.setOwner(newOwner);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
			// 处理访问文件失败的情况
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
			FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(dir, FileOwnerAttributeView.class);
			ownerAttributeView.setOwner(newOwner);
			return FileVisitResult.CONTINUE;
		}
	}

	private void setHashMapDataXiaofeishui(String yyyyMMdd,
			LinkedHashMap<String, String> LinkedHashMapKeyValueNew, LinkedHashMap<String, String> LinkedHashMapKeyValue,
			String yyyymmdd_count, HashMap<String, String> value) throws ParseException {

		/*
		 * 式样
		 * https://docs.google.com/spreadsheets/d/1SRwu3PSQDKBiB7Bg2ZCCI_qD1GSjk33egaQd8p4XgCk/edit#gid=973883502
		 */

		String innerkeyNew = "";
		String innerValueNew = "";

		/*
		 * 法人簡易基本情報ファイル
		 */
		//#達人コード#
		innerkeyNew = "#事業者コード#";
		innerValueNew = value.get("tatuji_code");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#事業者名#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#会社名英語全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#課税区分#";
		if ("原则课税".equals(LinkedHashMapKeyValueNew.get("#原则课税还是简易课税#"))) {
			innerValueNew = "一般用";
		} else {
			innerValueNew = "簡易課税用";
		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#税務署名#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#管辖税务署#").replace("税務署", "");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#税務署番号#";
		innerValueNew = "";
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#法人番号#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#法人番号#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#振替継続希望#";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#納税地_郵便番号#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#纳税地地址邮编（第一段）#") + "-" + LinkedHashMapKeyValueNew.get("#纳税地地址邮编（第二段）#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#納税地フリガナ#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#纳税地地址（フリガナ）#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#納税地#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#纳税地地址（日语）#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#本店等の所在地#";
		innerValueNew = FuncUtils.toFullWidthAndTruncate(LinkedHashMapKeyValueNew.get("#会社外国本店英語全部#"), 25);
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#納税地_電話番号#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#纳税地电话（第一段）#")
				+"-" + LinkedHashMapKeyValueNew.get("#纳税地电话（第二段）#")
				+"-" + LinkedHashMapKeyValueNew.get("#纳税地电话（第三段）#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#メールアドレス#";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#屋号フリガナ#";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#屋号#";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#被合併法人名#";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#代表者氏名フリガナ#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#代表者氏名（フリガナ）#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#代表者氏名#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#代表者名英語全部#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#役職名#", "代表取締役");
		LinkedHashMapKeyValueNew.put("#所管#", "");
		LinkedHashMapKeyValueNew.put("#要否#", "");
		LinkedHashMapKeyValueNew.put("#整理番号#", "");
		LinkedHashMapKeyValueNew.put("#送付不要_区分#", "");
		LinkedHashMapKeyValueNew.put("#金融機関種別#", "銀行等");
		LinkedHashMapKeyValueNew.put("#金融機関名#", "");
		LinkedHashMapKeyValueNew.put("#金融機関区分#", "");
		LinkedHashMapKeyValueNew.put("#本支店名#", "");
		LinkedHashMapKeyValueNew.put("#本支店区分#", "");
		LinkedHashMapKeyValueNew.put("#預金種類#", "");
		LinkedHashMapKeyValueNew.put("#預金種類_その他#", "");
		LinkedHashMapKeyValueNew.put("#口座番号#", "");
		LinkedHashMapKeyValueNew.put("#郵便局名#", "");
		LinkedHashMapKeyValueNew.put("#ゆうちょ銀行の貯金記号番号_前#", "");
		LinkedHashMapKeyValueNew.put("#ゆうちょ銀行の貯金記号番号_後#", "");
		LinkedHashMapKeyValueNew.put("#公金受取口座の利用#", "");

		innerkeyNew = "#利用者識別番号#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#利用者識別番号#");
		//TODO
		if (StringUtils.isEmpty(innerValueNew)) {
			innerValueNew = LinkedHashMapKeyValueNew.get("#利用者識別番号0#");

		}
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "#資本金_出資金#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#資本金#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		LinkedHashMapKeyValueNew.put("#事業内容#", "小売業");
		LinkedHashMapKeyValueNew.put("#代表者住所_郵便番号#", "000-0000");
		LinkedHashMapKeyValueNew.put("#代表者住所#", "国外");
		LinkedHashMapKeyValueNew.put("#代表者住所_電話番号#", "");
		LinkedHashMapKeyValueNew.put("#経理担当者氏名#", "");
		LinkedHashMapKeyValueNew.put("#性別#", "");
		LinkedHashMapKeyValueNew.put("#生年月日#", "");
		LinkedHashMapKeyValueNew.put("#職業#", "");
		LinkedHashMapKeyValueNew.put("#世帯主氏名#", "");
		LinkedHashMapKeyValueNew.put("#世帯主との続柄#", "");
		LinkedHashMapKeyValueNew.put("#税理士法人名又は事務所名フリガナ#", "ビーピーエスコクサイゼイリシホウジン");
		LinkedHashMapKeyValueNew.put("#税理士法人名又は事務所名#", "ＢＰＳ国際税理士法人");
		LinkedHashMapKeyValueNew.put("#税理士名フリガナ#", "ゼイリシ スズキヒデアキ");
		LinkedHashMapKeyValueNew.put("#税理士名#", "税理士 鈴木秀明");
		LinkedHashMapKeyValueNew.put("#事務所所在地_郵便番号#", "104-0061");
		LinkedHashMapKeyValueNew.put("#事務所所在地#", "東京都中央区銀座8-8-5 陽栄銀座ビル4階");
		LinkedHashMapKeyValueNew.put("#事務所電話番号#", "03-6264-3477");
		LinkedHashMapKeyValueNew.put("#所属税理士会等_税理士会#", "東京");
		LinkedHashMapKeyValueNew.put("#所属税理士会等_支部#", "京橋");
		LinkedHashMapKeyValueNew.put("#所属税理士会等_登録番号#", "92174");
		LinkedHashMapKeyValueNew.put("#税理士利用者識別番号#", "1595560602927032");


		/*
		 * 法人簡易計算基礎データ
		 */
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第2種事業　小売業仮受消費税#", "");
//		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第3種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第1種事業　卸売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第1種事業　卸売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第1種事業　卸売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第2種事業　小売業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第2種事業　小売業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第2種事業　小売業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　製造業等本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　製造業等仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　製造業等税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第4種事業　その他本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第4種事業　その他仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第4種事業　その他税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第5種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第5種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第5種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　サービス業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　サービス業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第3種事業　サービス業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第6種事業　不動産業本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第6種事業　不動産業仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還第6種事業　不動産業税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入返還課税売上返還事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入返還課税売上返還事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入返還課税売上返還事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入返還課税売上返還事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還事業区分「0」本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還事業区分「0」仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入返還課税売上返還事業区分「0」税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)免税売上に係る対価の返還本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)免税売上に係る対価の返還本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)免税売上に係る対価の返還本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)免税売上に係る対価の返還本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)免税売上に係る対価の返還本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額税込価額#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額仮受消費税#", "");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額税込価額#", "");

		/*
		 * 法人簡易申告書共通情報固定項目
		 */
		LinkedHashMapKeyValueNew.put("#売上計算選択#", "原則計算");
		LinkedHashMapKeyValueNew.put("#仕入計算選択#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_元号#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_年#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_月#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_日#", "");
//		LinkedHashMapKeyValueNew.put("#中間納付税額#", "");
//		LinkedHashMapKeyValueNew.put("#中間納付譲渡割額#", "");
		LinkedHashMapKeyValueNew.put("#割賦基準の適用#", "無");
		LinkedHashMapKeyValueNew.put("#延払基準等の適用#", "無");
		LinkedHashMapKeyValueNew.put("#工事進行基準の適用#", "無");
		LinkedHashMapKeyValueNew.put("#現金主義会計の適用#", "無");
		LinkedHashMapKeyValueNew.put("#基準期間の課税売上高#", "");
		LinkedHashMapKeyValueNew.put("#税理士法30条の書面提出有_該当区分#", "該当");
		LinkedHashMapKeyValueNew.put("#税理士法33条の2の書面提出有_該当区分#", "");



		/*
		 * 法人簡易税務代理権限証書固定項目
		 */
		LinkedHashMapKeyValueNew.put("#整理番号#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_元号#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_年#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_月#", "");
		LinkedHashMapKeyValueNew.put("#提出年月日_日#", "");
//		LinkedHashMapKeyValueNew.put("#税務署名#", "");
		LinkedHashMapKeyValueNew.put("#氏名又は名称#", "ＢＰＳ国際税理士法人");

		/*
		 * 東京都中央区銀座４?１３?８　
		 * OK東京都中央区銀座４ー１３ー８　
		 * NG東京都中央区銀座４－１３－８　
		 * NG東京都中央区銀座４―１３―８　
		 */


		LinkedHashMapKeyValueNew.put("#事務所の名称及び所在地_名称#", "東京都中央区銀座8-8-5 陽栄銀座ビル4階");
		LinkedHashMapKeyValueNew.put("#事務所の名称及び所在地_所在地#", "");
		LinkedHashMapKeyValueNew.put("#事務所の名称及び所在地_電話番号#", "03-6264-3477");
		LinkedHashMapKeyValueNew.put("#事務所の名称及び所在地_連絡先#", "");
		LinkedHashMapKeyValueNew.put("#事務所の名称及び所在地_連絡先_電話番号#", "");
		LinkedHashMapKeyValueNew.put("#税理士会名#", "東京");
		LinkedHashMapKeyValueNew.put("#支部名#", "京橋");
		LinkedHashMapKeyValueNew.put("#登録番号等#", "2302");
		LinkedHashMapKeyValueNew.put("#税務代理#", "税理士法人");
		LinkedHashMapKeyValueNew.put("#税務代理_年月日#", "");
		LinkedHashMapKeyValueNew.put("#過年分に関する税務代理#", "");
		LinkedHashMapKeyValueNew.put("#調査の通知に関する同意#", "該当");
		LinkedHashMapKeyValueNew.put("#代理人が複数ある場合における代表する代理人の定め#", "");


		innerkeyNew = "#依頼者_氏名又は名称#";
		innerValueNew = LinkedHashMapKeyValueNew.get("#事業者名#");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);


		LinkedHashMapKeyValueNew.put("#依頼者_住所又は事務所の所在地#", "");
		LinkedHashMapKeyValueNew.put("#依頼者_電話番号#", "");
		LinkedHashMapKeyValueNew.put("#所得税_申告に係るもの_該当税目#", "");
		LinkedHashMapKeyValueNew.put("#所得税_申告に係るもの_年分#", "");
		LinkedHashMapKeyValueNew.put("#法人税_該当税目#", "");
		LinkedHashMapKeyValueNew.put("#法人税_事業年度_自#", "");
		LinkedHashMapKeyValueNew.put("#法人税_事業年度_至#", "");
		LinkedHashMapKeyValueNew.put("#消費税及び地方消費税_該当税目#", "該当");
		LinkedHashMapKeyValueNew.put("#消費税及び地方消費税_事業年度_自#", "");
		LinkedHashMapKeyValueNew.put("#消費税及び地方消費税_事業年度_至#", "");
		LinkedHashMapKeyValueNew.put("#所得税_源泉徴収に係るもの_該当税目#", "");
		LinkedHashMapKeyValueNew.put("#所得税_源泉徴収に係るもの_事業年度_自#", "");
		LinkedHashMapKeyValueNew.put("#所得税_源泉徴収に係るもの_事業年度_至#", "");
		LinkedHashMapKeyValueNew.put("#税目名_01#", "");
		LinkedHashMapKeyValueNew.put("#該当税目_01#", "");
		LinkedHashMapKeyValueNew.put("#年分等_01#", "");
		LinkedHashMapKeyValueNew.put("#税目名_02#", "");
		LinkedHashMapKeyValueNew.put("#該当税目_02#", "");
		LinkedHashMapKeyValueNew.put("#年分等_02#", "");
		LinkedHashMapKeyValueNew.put("#税目名_03#", "");
		LinkedHashMapKeyValueNew.put("#該当税目_03#", "");
		LinkedHashMapKeyValueNew.put("#年分等_03#", "");
		LinkedHashMapKeyValueNew.put("#税目名_04#", "");
		LinkedHashMapKeyValueNew.put("#該当税目_04#", "");
		LinkedHashMapKeyValueNew.put("#年分等_04#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_01#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_02#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_03#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_04#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_05#", "");
		LinkedHashMapKeyValueNew.put("#その他の事項_06#", "");
		LinkedHashMapKeyValueNew.put("#事務処理欄_部門#", "");
		LinkedHashMapKeyValueNew.put("#事務処理欄_業種#", "");
		LinkedHashMapKeyValueNew.put("#事務処理欄_予備#", "");
		LinkedHashMapKeyValueNew.put("#事務処理欄_他部門等回付#", "");
		LinkedHashMapKeyValueNew.put("#事務処理欄_他部門等回付_部門#", "");





		/*
		 * 法人原則還付無総合
		 */
		//法人原則還付無基礎データ
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上仮受消費税#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上非課税売上・有価証券本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上非課税売上・有価証券以外本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上非課税資産の輸出等本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上に係る対価の返還仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上課税売上に係る対価の返還税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上免税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上非課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)売上非課税資産の輸出等の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上非課税売上・有価証券本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上非課税売上・有価証券以外本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上非課税資産の輸出等本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上に係る対価の返還仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上課税売上に係る対価の返還税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上免税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上非課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)売上非課税資産の輸出等の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上非課税売上・有価証券本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上非課税売上・有価証券以外本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上非課税資産の輸出等本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上に係る対価の返還仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上課税売上に係る対価の返還税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上免税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上非課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)売上非課税資産の輸出等の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上非課税売上・有価証券本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上非課税売上・有価証券以外本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上非課税資産の輸出等本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上に係る対価の返還仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上課税売上に係る対価の返還税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上免税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上非課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)売上非課税資産の輸出等の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上免税売上(輸出取引等)本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上非課税売上・有価証券本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上非課税売上・有価証券以外本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上非課税資産の輸出等本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上に係る対価の返還仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上課税売上に係る対価の返還税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上免税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上非課税売上に係る対価の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)売上非課税資産の輸出等の返還本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入課税売上対応仮払消費税#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入共通売上対応税込価額#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入共通売上対応仮払消費税#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入仕入返還共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)仕入輸入仕入返還地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入課税売上対応仮払消費税#", "0");
//		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入仕入返還共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)仕入輸入仕入返還地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入仕入返還共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)仕入輸入仕入返還地方消費税分仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)仕入仕入返還共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還非課税売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還共通売上対応仮払消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)仕入仕入返還共通売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)特定仕入特定仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入返還課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入返還非課税売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)特定仕入特定仕入返還共通売上対応本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)貸倒貸倒れ回収金額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)貸倒貸倒れ回収金額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(8%分)貸倒貸倒れ回収金額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(5%分)貸倒貸倒れ回収金額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ額税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額本体価額(税抜き)#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額仮受消費税#", "0");
		LinkedHashMapKeyValueNew.put("#(3%分)貸倒貸倒れ回収金額税込価額#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入80%控除分課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入80%控除分非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入80%控除分共通売上対応税込価額#", "0");
//		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入50%控除分課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入50%控除分非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(10%分)適格請求書発行事業者以外からの仕入50%控除分共通売上対応税込価額#", "0");
//		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入80%控除分課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入80%控除分非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入80%控除分共通売上対応税込価額#", "0");
//		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入50%控除分課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入50%控除分非課税売上対応税込価額#", "0");
		LinkedHashMapKeyValueNew.put("#(軽8%分)適格請求書発行事業者以外からの仕入50%控除分共通売上対応税込価額#", "0");

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

		innerkeyNew = "";
		innerValueNew = LinkedHashMapKeyValueNew.get("");
		LinkedHashMapKeyValueNew.put(innerkeyNew, innerValueNew);

	}


    public static void main(String[] args) {
    	try {

    		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();

    		LinkedHashMap<String, t_etax_account_resExBean> LinkedHashMap_t_etax_account_resExBean = t_etax_account_resDao.selecBangouIsNull();
    		String msg = "";
    		for (String yyyymmdd_count : LinkedHashMap_t_etax_account_resExBean.keySet()) {
    			t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);
    			pandaWebDriver testNoWEB = new pandaWebDriver(null);

    				msg = testNoWEB.getEtaxNo(yyyymmdd_count);

    			if("国税局系统维护中".equals(msg)) {
//    				out.print(msg);
    				logger.info("end" + msg);
    				return;

    			}

    			logger.info("getEtaxNo ok:" + yyyymmdd_count);
    		}

			logger.info("end" + msg);
			return;

    	} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }
}