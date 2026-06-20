package com.panda.batch;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.LinkedHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;

public class set_shuiwu_quanxian_daili_zhengshu {

	private static Logger logger = Logger.getLogger(set_shuiwu_quanxian_daili_zhengshu.class.toString());

		static int count = 0;

		static String fileContent = "";
		static String formattedXml = "";
		static String key__ = "";
		static String value = "";


		static String key__list = "";

		static LinkedHashMap<String, String> LinkedHashMap_path_output_xml_NG = new LinkedHashMap<>();
		static LinkedHashMap<String, String> LinkedHashMap_path_output_xtx_NG = new LinkedHashMap<>();
		static LinkedHashMap<String, String> LinkedHashMap_path_output_ncc_NG = new LinkedHashMap<>();




		static String shiming25quanjiao = "";
		static String bangou = "";
		static String zhusuo25quanjiao = "";
		static String PXXXX = "";

	public static void main(String[] args) {


		logger.info("start");
/*
証書付け版
	法人　簡易　iTAX利用者番号あり
	法人　簡易 　BPS一式
 */


		String path_output = "C:\\Users\\Administrator\\Desktop\\証書付け版wd_output";
		String path_impot = "C:\\Users\\Administrator\\Desktop\\証書付け版wd_impot";
		String path_moban = "C:\\Users\\Administrator\\Desktop\\証書付け版wd_moban";
		String exe_type = "itax";
//		static String exe_type = "bps";

		try {
			exe(exe_type, path_output, path_impot, path_moban);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}


		logger.debug("end");
	}

	public static void exe(String exe_type, String path_output, String path_impot, String path_moban) throws IOException {



		logger.debug("set_shuiwu_quanxian_daili_zhengshu S");

		FuncUtils.deleteFile(path_output + "/処理内訳"+exe_type+".txt");
        File rootFolder_path_output = new File(path_output);
		for (File subFolder : rootFolder_path_output.listFiles(File::isDirectory)) {

	        // 使用 Files.walk 删除非空目录
	        try {
	            Path path = Paths.get(subFolder.getPath());
	            Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
	                @Override
	                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
	                    // 删除文件
	                    Files.delete(file);
//	                    logger.info("Deleted file: " + file.toString());
	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
	                    // 处理访问文件失败的情况
	                    logger.error("Failed to visit file: " + file.toString());
	                    return FileVisitResult.CONTINUE;
	                }

	                @Override
	                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
	                    // 删除非空目录
	                    Files.delete(dir);
//	                    logger.info("Deleted folder: " + dir.toString());
	                    return FileVisitResult.CONTINUE;
	                }
	            });

//	            logger.info("Folder and its contents have been deleted.");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}





		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();
		User_infoBean = LicenseDao.select("wangzihao");

		String maxNo = "99999999";
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao
				.selectAll(User_infoBean, maxNo, null, null, null);

//		LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBeanNEW = new LinkedHashMap<String, t_etax_account_infoExBean>();
//		for (t_etax_account_infoExBean t_etax_account_infoExBean : LinkedHashMap_t_etax_account_infoExBean.values()) {
//			String key = t_etax_account_infoExBean.getCompanyName_English();
//			key = key.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
//        	LinkedHashMap_t_etax_account_infoExBeanNEW.put(key, t_etax_account_infoExBean);
//		}

		String fileNameKey = "_証書付き";

        File rootFolder = new File(path_impot);




		/*
		shiming30banjiao
		納税者名（英字）

		本店住所（英字）
		PXXX
		管理番号

		ファイル名
		XXX（証書付け版）
		*/

		for (File subFolder : rootFolder.listFiles(File::isDirectory)) {
			 shiming25quanjiao = "";
			 bangou = "";
			 zhusuo25quanjiao = "";
			 PXXXX = "";


			String FolderName = subFolder.getName();
//			logger.debug("File : " + FolderName);

			String FolderName_mohu_chaxun = FuncUtils.toHalfWidth(FolderName);
			FolderName_mohu_chaxun = FolderName_mohu_chaxun.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");

			count = 0;
			t_etax_account_infoExBean t_etax_account_infoExBeanBK = new t_etax_account_infoExBean();
			for (t_etax_account_infoExBean t_etax_account_infoExBean : LinkedHashMap_t_etax_account_infoExBean.values()) {
				String CompanyName_English = t_etax_account_infoExBean.getCompanyName_English();
				CompanyName_English = FuncUtils.toHalfWidth(CompanyName_English);
				CompanyName_English = CompanyName_English.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
				if (FolderName_mohu_chaxun.indexOf(CompanyName_English) > -1) {
					++count;

					shiming25quanjiao = t_etax_account_infoExBean.getCompanyName_English();
					shiming25quanjiao = FuncUtils.toFullWidthAndTruncate(shiming25quanjiao, 25);

					bangou = t_etax_account_infoExBean.getBangou();

					zhusuo25quanjiao = t_etax_account_infoExBean.getAddress_English();
					zhusuo25quanjiao = FuncUtils.toFullWidthAndTruncate(zhusuo25quanjiao, 25);

					PXXXX = FolderName_mohu_chaxun.toUpperCase();
					PXXXX = PXXXX.substring(0, 4) + PXXXX.substring(6, 10);
					t_etax_account_infoExBeanBK = t_etax_account_infoExBean;
					break;
				}
			}
			if (count == 0) {
				logger.debug("File xml NG: " + subFolder.getPath().replace(path_impot, ""));

			}


        	key__list = "";
       		count = 0;

        	/*
        	 * xml
        	 */
        	File myFile_xml = new File(path_moban + "/税務代理権限証書(令和6年4月1日以降提出分).xml");
			fileContent = FuncUtils.readFileContent(myFile_xml);

//			fileContent.replace("&lt;", "<");
//			fileContent.replace("&gt;", ">");


//	        formattedXml = formatXmlWithStandalone(fileContent);
			formattedXml = fileContent;

	        fileContent = formattedXml;

			key__ = "shiming25quanjiao";
			value = shiming25quanjiao;
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}

			key__ = "bangou";
			value = bangou;
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}

			key__ = "zhusuo25quanjiao";
			value = zhusuo25quanjiao;
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}

			key__ = "PXXXX";
			value = PXXXX;
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}

			if(StringUtils.isEmpty(key__list)){//if(count == 3) {

			} else {
				LinkedHashMap_path_output_xml_NG.put(subFolder.getName(), "");

			}

			String fileNameNEW = myFile_xml.getName();
			String fileExtensionNEW = FuncUtils.getFileExtension(fileNameNEW);
			fileNameNEW = shiming25quanjiao + fileNameNEW;

			// 写入文件
			String path_output_New = path_output + "/" + FolderName;
			File directory = new File(path_output_New);
			directory.mkdir();
			path_output_New = path_output + "/" + FolderName + "/nccファイル　xtxファイル";
			directory = new File(path_output_New);
			directory.mkdir();

			FileWriter writer = new FileWriter(path_output_New + "/" + fileNameNEW);
			writer.write(formattedXml);
			writer.close();

//			writer = new FileWriter(path_output_New + "/" + fileNameNEW + "OLD");
//			writer.write(fileContent);
//			writer.close();

//			logger.debug("File saved: " + path_output_New);



            for (File myFile : FuncUtils.listFilesInFolder(subFolder.getPath())) {
            	key__list = "";
            	String flieName =  myFile.getName();
//				logger.info("File：" + flieName);

				String fileExtension = FuncUtils.getFileExtension(flieName);


//				Document doc = null;
// 	            // 解析XML文件
//	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

	       		count = 0;

	       		/*
	       		 * xtx
	       		 */
            	if ("xtx".equals(fileExtension.toLowerCase())) {

					if ("itax".equals(exe_type)) {
            			set_xtx_itax(myFile, t_etax_account_infoExBeanBK);

            		} else if ("bps".equals(exe_type)) {
            			set_xtx_bps(myFile, t_etax_account_infoExBeanBK);

            		}

            	} else if ("ncc".equals(fileExtension.toLowerCase())) {
					if ("itax".equals(exe_type )) {
        				set_ncc_itax(myFile);

            		} else if ("bps".equals(exe_type)) {
        				set_ncc_bps(myFile);

            		}

            	} else {
            		continue;

            	}


//            	key__ = "shiming25quanjiao";
//				value = shiming25quanjiao;
//				if (formattedXml.contains(key__)) {
//					++count;
//					formattedXml = formattedXml.replace(key__, value);
//
//				} else {
//					key__list = key__list + "," + key__;
//
//				}
//
//				key__ = "zhusuo25quanjiao";
//				value = zhusuo25quanjiao;
//				if (formattedXml.contains(key__)) {
//					++count;
//					formattedXml = formattedXml.replace(key__, value);
//
//				} else {
//					key__list = key__list + "," + key__;
//
//				}
//
//				key__ = "PXXX";
//				value = PXXXX;
//				if (formattedXml.contains(key__)) {
//					++count;
//					formattedXml = formattedXml.replace(key__, value);
//
//				} else {
//					key__list = key__list + "," + key__;
//
//				}

//				formattedXml.replace("&lt;", "<");
//				formattedXml.replace("&gt;", ">");

            	if ("xtx".equals(fileExtension.toLowerCase()) || "ncc".equals(fileExtension.toLowerCase())) {
					// 写入文件
					// 获取文件的Path对象
					Path file = Paths.get(myFile.getParent());
					Path parent = file.getParent();
					// 获取上两层目录的文件夹名字
					String grandparentFolderName = parent.getFileName().toString();
					path_output_New = path_output + "/" + grandparentFolderName + "/" + file.getFileName();

					directory = new File(path_output_New);
					directory.mkdir();

					fileNameNEW = myFile.getName();
					fileExtensionNEW = FuncUtils.getFileExtension(fileNameNEW);
					fileNameNEW = fileNameNEW.replace("." + fileExtensionNEW, fileNameKey + "." + fileExtensionNEW);

					writer = new FileWriter(path_output_New + "/" + fileNameNEW);
					writer.write(formattedXml);
					writer.close();

//					writer = new FileWriter(path_output_New + "/" + fileNameNEW + "OLD");
//					writer.write(fileContent);
//					writer.close();



					if ("itax".equals(exe_type)) {
                    	if ("xtx".equals(fileExtension.toLowerCase())) {
                    		if(StringUtils.isEmpty(key__list)){//if (count == 4 + 3) {

    						} else {
	        					LinkedHashMap_path_output_xtx_NG.put(subFolder.getName(), key__list);
            				}

						} else if ("ncc".equals(fileExtension.toLowerCase())) {
							if(StringUtils.isEmpty(key__list)){//if(count == 12 +3) {

	        				} else {
	        					LinkedHashMap_path_output_ncc_NG.put(subFolder.getName(), key__list);

	        				}
						}
            		} else if ("bps".equals(exe_type)) {
            			//bps
                    	if ("xtx".equals(fileExtension.toLowerCase())) {
                    		if(StringUtils.isEmpty(key__list)){//if (count == 4 + 3) {

    						} else {
	        					LinkedHashMap_path_output_xtx_NG.put(subFolder.getName(), key__list);
            				}

						} else if ("ncc".equals(fileExtension.toLowerCase())) {
							if(StringUtils.isEmpty(key__list)){//if(count == 12 + 3) {

	        				} else {
	        					LinkedHashMap_path_output_ncc_NG.put(subFolder.getName(), key__list);

	        				}
						}
            		}




            	}


            }



//        	return;





        }


		String textContent = "";
        String filePath = path_output + "/処理内訳"+exe_type+".txt";
        Path path = Paths.get(filePath);

		for (String key : LinkedHashMap_path_output_xml_NG.keySet()) {
			textContent = exe_type + " File xml NG：" + key + " " + LinkedHashMap_path_output_xml_NG.get(key);
			logger.info(textContent);
			textContent = textContent + "\r\n";
            Files.write(path, textContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}
		for (String key : LinkedHashMap_path_output_xtx_NG.keySet()) {
			textContent = exe_type + " File xtx NG：" + key + " " + LinkedHashMap_path_output_xtx_NG.get(key);
			logger.info(textContent);
			textContent = textContent + "\r\n";
            Files.write(path, textContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
		for (String key : LinkedHashMap_path_output_ncc_NG.keySet()) {
			textContent = exe_type + " File ncc NG：" + key + " " + LinkedHashMap_path_output_ncc_NG.get(key);
			logger.info(textContent);
			textContent = textContent + "\r\n";
            Files.write(path, textContent.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }


		logger.debug("set_shuiwu_quanxian_daili_zhengshu E");
	}

	private static void set_xtx_itax(File myFile, t_etax_account_infoExBean t_etax_account_infoExBean) throws IOException {
		fileContent = FuncUtils.readFileContent(myFile);
		formattedXml = formatXmlWithStandalone(fileContent);
		fileContent = formattedXml;

		key__ = "<TENPU_SEC/>";
		value = "<TENPU_SEC>\r\n"
				+ "            <rdf:Seq>\r\n"
				+ "              <rdf:li>\r\n"
				+ "                <rdf:description about=\"#TENPU\" />\r\n"
				+ "              </rdf:li>\r\n"
				+ "            </rdf:Seq>\r\n"
				+ "          </TENPU_SEC>"
				+ "";
		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;
			//key__list = key__list + "," + key__.substring(0, (key__.length()<20?key__.length():20));
		}



		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {


		} else {

		}


		//法人と個人は違うため

//		key__ = "</DAIHYO_ZIP>";
//		value = "</DAIHYO_ZIP>\r\n"


//		key__ = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>";
//		if (formattedXml.contains(key__)) {
////			++count;
//			formattedXml = formattedXml.replace(key__, "");
//
//		} else {
////			key__list = key__list + "," + key__;
//		}


		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			key__ = "</NOZEISHA_TEL>";
			value = "</NOZEISHA_TEL>\r\n"
					+ "<DAIRI_ID ID=\"DAIRI_ID\">2674011631920063</DAIRI_ID>\r\n"
					+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">アイタックス</DAIRI_NM_KN>\r\n"
					+ "        <DAIRI_NM ID=\"DAIRI_NM\">ｉＴＡＸ税理士法人</DAIRI_NM>\r\n"
					+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">\r\n"
					+ "          <gen:zip1>101</gen:zip1>\r\n"
					+ "          <gen:zip2>0064</gen:zip2>\r\n"
					+ "        </DAIRI_ZIP>\r\n"
					+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</DAIRI_ADR>\r\n"
					+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">\r\n"
					+ "          <gen:tel1>03</gen:tel1>\r\n"
					+ "          <gen:tel2>6272</gen:tel2>\r\n"
					+ "          <gen:tel3>8525</gen:tel3>\r\n"
					+ "        </DAIRI_TEL>"
					+ ""
					+ "";

		} else {

			key__ = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>";
			value = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>\r\n"
					+ "<DAIRI_ID ID=\"DAIRI_ID\">2674011631920063</DAIRI_ID>\r\n"
					+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">アイタックス</DAIRI_NM_KN>\r\n"
					+ "        <DAIRI_NM ID=\"DAIRI_NM\">ｉＴＡＸ税理士法人</DAIRI_NM>\r\n"
					+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">\r\n"
					+ "          <gen:zip1>101</gen:zip1>\r\n"
					+ "          <gen:zip2>0064</gen:zip2>\r\n"
					+ "        </DAIRI_ZIP>\r\n"
					+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</DAIRI_ADR>\r\n"
					+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">\r\n"
					+ "          <gen:tel1>03</gen:tel1>\r\n"
					+ "          <gen:tel2>6272</gen:tel2>\r\n"
					+ "          <gen:tel3>8525</gen:tel3>\r\n"
					+ "        </DAIRI_TEL>"
					+ ""
					+ "";


		}


		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;

		}


		//TODO
//		key__ = "</ABH00250>";
//		value = "</ABH00250>\r\n"
//				+ "            <ABH00200>\r\n"
//				+ "              <ABH00210 IDREF=\"DAIRI_NM\" />\r\n"
//				+ "              <ABH00220 IDREF=\"DAIRI_TEL\" />\r\n"
//				+ "            </ABH00200>"
//				+ ""
//				+ "";
//		if (formattedXml.contains(key__)) {
//			++count;
//			formattedXml = formattedXml.replace(key__, value);
//
//		} else {
//			key__list = key__list + "," + key__;
//
//		}




		//TODO
		//没找到，特殊处理</IT>
//		key__ = "</IT>";
//		value = "</IT>\r\n"



//		key__ = "<param name=\"帳票データ\"><reportValue reportId=\"SHA020\" reportName=\"消費税及び地方消費税の申告書(簡易課税用)\" leafId=\"\" page=\"1\"><page reportId=\"SHA020-1\" reportName=\"\" status=\"0\">&lt;ABH00000&gt;&lt;ABH00020 IDREF=&quot;ZEIMUSHO&quot;/&gt;&lt;ABH00030 IDREF=&quot;NOZEISHA_ADR&quot;/&gt;&lt;ABH00040 IDREF=&quot;NOZEISHA_TEL&quot;/&gt;&lt;ABH00050&gt;&lt;ABH00060 IDREF=&quot;NOZEISHA_NM_KN&quot;/&gt;&lt;ABH00070 IDREF=&quot;NOZEISHA_NM&quot;/&gt;&lt;/ABH00050&gt;&lt;ABH00080 IDREF=&quot;NOZEISHA_BANGO&quot;/&gt;&lt;ABH00090&gt;&lt;ABH00100 IDREF=&quot;DAIHYO_NM_KN&quot;/&gt;&lt;ABH00110 IDREF=&quot;DAIHYO_NM&quot;/&gt;&lt;/ABH00090&gt;&lt;ABH00115&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00115&gt;&lt;ABH00120&gt;&lt;ABH00130 IDREF=&quot;KAZEI_KIKAN_FROM&quot;/&gt;&lt;ABH00140 IDREF=&quot;KAZEI_KIKAN_TO&quot;/&gt;&lt;/ABH00120&gt;&lt;ABH00150 IDREF=&quot;SHINKOKU_KBN&quot;/&gt;&lt;ABH00250&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00250&gt;&lt;ABH00230&gt;&lt;kubun_CD&gt;1&lt;/kubun_CD&gt;&lt;/ABH00230&gt;&lt;ABH00240&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00240&gt;&lt;/ABH00000&gt;&lt;ABI00000&gt;&lt;ABI00010&gt;0&lt;/ABI00010&gt;&lt;ABI00020&gt;0&lt;/ABI00020&gt;&lt;ABI00040&gt;&lt;ABI00050&gt;0&lt;/ABI00050&gt;&lt;ABI00080 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00080&gt;&lt;/ABI00040&gt;&lt;ABI00090 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00090&gt;&lt;ABI00100 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00100&gt;&lt;ABI00120 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00120&gt;&lt;ABI00130 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00130&gt;&lt;ABI00170&gt;0&lt;/ABI00170&gt;&lt;ABI00180&gt;4688660&lt;/ABI00180&gt;&lt;/ABI00000&gt;&lt;ABJ00000&gt;&lt;ABJ00010&gt;&lt;ABJ00030&gt;0&lt;/ABJ00030&gt;&lt;/ABJ00010&gt;&lt;ABJ00040&gt;&lt;ABJ00060&gt;0&lt;/ABJ00060&gt;&lt;/ABJ00040&gt;&lt;ABJ00080 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00080&gt;&lt;ABJ00090 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00090&gt;&lt;ABJ00130 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00130&gt;&lt;/ABJ00000&gt;&lt;ABK00000&gt;&lt;ABK00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00010&gt;&lt;ABK00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00020&gt;&lt;ABK00030&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00030&gt;&lt;ABK00040&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00040&gt;&lt;/ABK00000&gt;&lt;ABL00000&gt;&lt;ABL00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABL00010&gt;&lt;ABL00020&gt;&lt;ABL00060&gt;&lt;ABL00070&gt;0&lt;/ABL00070&gt;&lt;/ABL00060&gt;&lt;/ABL00020&gt;&lt;ABL00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABL00210&gt;&lt;/ABL00000&gt;&lt;ABY00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABY00000&gt;&lt;ABW00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABW00000&gt;&lt;ABX00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABX00000&gt;</page><page reportId=\"SHA020-2\" reportName=\"\" status=\"0\">&lt;ABM00000&gt;&lt;ABM00010 IDREF=&quot;NOZEISHA_ADR&quot;/&gt;&lt;ABM00020 IDREF=&quot;NOZEISHA_TEL&quot;/&gt;&lt;ABM00030&gt;&lt;ABM00040 IDREF=&quot;NOZEISHA_NM_KN&quot;/&gt;&lt;ABM00050 IDREF=&quot;NOZEISHA_NM&quot;/&gt;&lt;/ABM00030&gt;&lt;ABM00060&gt;&lt;ABM00070 IDREF=&quot;DAIHYO_NM_KN&quot;/&gt;&lt;ABM00080 IDREF=&quot;DAIHYO_NM&quot;/&gt;&lt;/ABM00060&gt;&lt;ABM00090&gt;&lt;ABM00100 IDREF=&quot;KAZEI_KIKAN_FROM&quot;/&gt;&lt;ABM00110 IDREF=&quot;KAZEI_KIKAN_TO&quot;/&gt;&lt;/ABM00090&gt;&lt;ABM00120 IDREF=&quot;SHINKOKU_KBN&quot;/&gt;&lt;/ABM00000&gt;&lt;ABN00000&gt;&lt;ABN00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABN00010&gt;&lt;ABN00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABN00020&gt;&lt;/ABN00000&gt;&lt;ABO00000&gt;0&lt;/ABO00000&gt;&lt;ABP00000&gt;&lt;ABP00050&gt;0&lt;/ABP00050&gt;&lt;ABP00060 AutoCalc=&quot;1&quot;&gt;0&lt;/ABP00060&gt;&lt;/ABP00000&gt;&lt;ABR00000 AutoCalc=&quot;1&quot;&gt;0&lt;/ABR00000&gt;&lt;ABS00000&gt;&lt;ABS00050&gt;0&lt;/ABS00050&gt;&lt;/ABS00000&gt;&lt;ABV00000&gt;&lt;ABV00010 AutoCalc=&quot;1&quot;&gt;0&lt;/ABV00010&gt;&lt;ABV00040&gt;0&lt;/ABV00040&gt;&lt;/ABV00000&gt;</page></reportValue></param>";
//		value = "<param name=\"帳票データ\"><reportValue reportId=\"SHA020\" reportName=\"消費税及び地方消費税の申告書(簡易課税用)\" leafId=\"\" page=\"1\"><page reportId=\"SHA020-1\" reportName=\"\" status=\"0\">&lt;ABH00000&gt;&lt;ABH00020 IDREF=&quot;ZEIMUSHO&quot;/&gt;&lt;ABH00030 IDREF=&quot;NOZEISHA_ADR&quot;/&gt;&lt;ABH00040 IDREF=&quot;NOZEISHA_TEL&quot;/&gt;&lt;ABH00050&gt;&lt;ABH00060 IDREF=&quot;NOZEISHA_NM_KN&quot;/&gt;&lt;ABH00070 IDREF=&quot;NOZEISHA_NM&quot;/&gt;&lt;/ABH00050&gt;&lt;ABH00080 IDREF=&quot;NOZEISHA_BANGO&quot;/&gt;&lt;ABH00090&gt;&lt;ABH00100 IDREF=&quot;DAIHYO_NM_KN&quot;/&gt;&lt;ABH00110 IDREF=&quot;DAIHYO_NM&quot;/&gt;&lt;/ABH00090&gt;&lt;ABH00115&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00115&gt;&lt;ABH00120&gt;&lt;ABH00130 IDREF=&quot;KAZEI_KIKAN_FROM&quot;/&gt;&lt;ABH00140 IDREF=&quot;KAZEI_KIKAN_TO&quot;/&gt;&lt;/ABH00120&gt;&lt;ABH00150 IDREF=&quot;SHINKOKU_KBN&quot;/&gt;&lt;ABH00250&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00250&gt;&lt;ABH00200&gt;&lt;ABH00210 IDREF=&quot;DAIRI_NM&quot;/&gt;&lt;ABH00220 IDREF=&quot;DAIRI_TEL&quot;/&gt;&lt;/ABH00200&gt;&lt;ABH00230&gt;&lt;kubun_CD&gt;1&lt;/kubun_CD&gt;&lt;/ABH00230&gt;&lt;ABH00240&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABH00240&gt;&lt;/ABH00000&gt;&lt;ABI00000&gt;&lt;ABI00010&gt;0&lt;/ABI00010&gt;&lt;ABI00020&gt;0&lt;/ABI00020&gt;&lt;ABI00040&gt;&lt;ABI00050&gt;0&lt;/ABI00050&gt;&lt;ABI00080 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00080&gt;&lt;/ABI00040&gt;&lt;ABI00090 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00090&gt;&lt;ABI00100 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00100&gt;&lt;ABI00120 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00120&gt;&lt;ABI00130 AutoCalc=&quot;1&quot;&gt;0&lt;/ABI00130&gt;&lt;ABI00170&gt;0&lt;/ABI00170&gt;&lt;ABI00180&gt;4688660&lt;/ABI00180&gt;&lt;/ABI00000&gt;&lt;ABJ00000&gt;&lt;ABJ00010&gt;&lt;ABJ00030&gt;0&lt;/ABJ00030&gt;&lt;/ABJ00010&gt;&lt;ABJ00040&gt;&lt;ABJ00060&gt;0&lt;/ABJ00060&gt;&lt;/ABJ00040&gt;&lt;ABJ00080 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00080&gt;&lt;ABJ00090 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00090&gt;&lt;ABJ00130 AutoCalc=&quot;1&quot;&gt;0&lt;/ABJ00130&gt;&lt;/ABJ00000&gt;&lt;ABK00000&gt;&lt;ABK00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00010&gt;&lt;ABK00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00020&gt;&lt;ABK00030&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00030&gt;&lt;ABK00040&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABK00040&gt;&lt;/ABK00000&gt;&lt;ABL00000&gt;&lt;ABL00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABL00010&gt;&lt;ABL00020&gt;&lt;ABL00060&gt;&lt;ABL00070&gt;0&lt;/ABL00070&gt;&lt;/ABL00060&gt;&lt;/ABL00020&gt;&lt;ABL00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABL00210&gt;&lt;/ABL00000&gt;&lt;ABY00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABY00000&gt;&lt;ABW00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABW00000&gt;&lt;ABX00000&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABX00000&gt;</page><page reportId=\"SHA020-2\" reportName=\"\" status=\"0\">&lt;ABM00000&gt;&lt;ABM00010 IDREF=&quot;NOZEISHA_ADR&quot;/&gt;&lt;ABM00020 IDREF=&quot;NOZEISHA_TEL&quot;/&gt;&lt;ABM00030&gt;&lt;ABM00040 IDREF=&quot;NOZEISHA_NM_KN&quot;/&gt;&lt;ABM00050 IDREF=&quot;NOZEISHA_NM&quot;/&gt;&lt;/ABM00030&gt;&lt;ABM00060&gt;&lt;ABM00070 IDREF=&quot;DAIHYO_NM_KN&quot;/&gt;&lt;ABM00080 IDREF=&quot;DAIHYO_NM&quot;/&gt;&lt;/ABM00060&gt;&lt;ABM00090&gt;&lt;ABM00100 IDREF=&quot;KAZEI_KIKAN_FROM&quot;/&gt;&lt;ABM00110 IDREF=&quot;KAZEI_KIKAN_TO&quot;/&gt;&lt;/ABM00090&gt;&lt;ABM00120 IDREF=&quot;SHINKOKU_KBN&quot;/&gt;&lt;/ABM00000&gt;&lt;ABN00000&gt;&lt;ABN00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABN00010&gt;&lt;ABN00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABN00020&gt;&lt;/ABN00000&gt;&lt;ABO00000&gt;0&lt;/ABO00000&gt;&lt;ABP00000&gt;&lt;ABP00050&gt;0&lt;/ABP00050&gt;&lt;ABP00060 AutoCalc=&quot;1&quot;&gt;0&lt;/ABP00060&gt;&lt;/ABP00000&gt;&lt;ABR00000 AutoCalc=&quot;1&quot;&gt;0&lt;/ABR00000&gt;&lt;ABS00000&gt;&lt;ABS00050&gt;0&lt;/ABS00050&gt;&lt;/ABS00000&gt;&lt;ABV00000&gt;&lt;ABV00010 AutoCalc=&quot;1&quot;&gt;0&lt;/ABV00010&gt;&lt;ABV00040&gt;0&lt;/ABV00040&gt;&lt;/ABV00000&gt;</page></reportValue></param>"
//				+ "";
//		if (formattedXml.contains(key__)) {
//			++count;
//			formattedXml = formattedXml.replace(key__, value);
//
//		} else {
//			key__list = key__list + "," + key__;
//
//		}




		key__ = "</CONTENTS>";
		value = "\r\n"
				+ "      <TENPU id=\"TENPU\">\r\n"
				+ "        <SOZ074 VR=\"1.0\" id=\"SOZ07424041213392758955\" page=\"1\" sakuseiDay=\"2024-04-12\" sakuseiNM=\""+shiming25quanjiao+" \" softNM=\"ntaclient\" xmlns=\"http://xml.e-tax.nta.go.jp/XSD/somu\" xmlns:gen=\"http://xml.e-tax.nta.go.jp/XSD/general\" xmlns:kyo=\"http://xml.e-tax.nta.go.jp/XSD/kyotsu\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ "          <ATB00000>\r\n"
				+ "            <ATB00020>小石川税務署長</ATB00020>\r\n"
				+ "            <ATB00030>\r\n"
				+ "              <ATB00040>\r\n"
				+ "                <ATB00050>iTAX税理士法人</ATB00050>\r\n"
				+ "                <ATB00060>2674011631920063</ATB00060>\r\n"
				+ "              </ATB00040>\r\n"
				+ "              <ATB00070>\r\n"
				+ "                <ATB00080>iTAX税理士法人</ATB00080>\r\n"
				+ "                <ATB00090>東京都千代田区神田猿楽町２－７－１７織本ビル５階</ATB00090>\r\n"
				+ "                <ATB00100>\r\n"
				+ "                  <gen:tel1>03</gen:tel1>\r\n"
				+ "                  <gen:tel2>6272</gen:tel2>\r\n"
				+ "                  <gen:tel3>8525</gen:tel3>\r\n"
				+ "                </ATB00100>\r\n"
				+ "              </ATB00070>\r\n"
				+ "              <ATB00110>\r\n"
				+ "                <ATB00120>東京</ATB00120>\r\n"
				+ "                <ATB00130>神田</ATB00130>\r\n"
				+ "                <ATB00140>3986</ATB00140>\r\n"
				+ "              </ATB00110>\r\n"
				+ "            </ATB00030>\r\n"
				+ "            <ATB00150>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATB00150>\r\n"
				+ "            <ATB00160>\r\n"
				+ "              <gen:era>5</gen:era>\r\n"
				+ "              <gen:yy>6</gen:yy>\r\n"
				+ "              <gen:mm>2</gen:mm>\r\n"
				+ "              <gen:dd>8</gen:dd>\r\n"
				+ "            </ATB00160>\r\n"
				+ "            <ATB00170>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATB00170>\r\n"
				+ "            <ATB00180>\r\n"
				+ "              <ATB00190>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATB00190>\r\n"
				+ "              <ATB00200>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATB00200>\r\n"
				+ "              <ATB00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATB00210>\r\n"
				+ "            </ATB00180>\r\n"
				+ "            <ATB00220>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATB00220>\r\n"
				+ "            <ATB00230>\r\n"
				+ "              <ATB00240>\r\n"
				+ "                <ATB00250>"+shiming25quanjiao+"\r\n"
				+ "</ATB00250>\r\n"
				+ "                <ATB00260>"+bangou+"</ATB00260>\r\n"
				+ "              </ATB00240>\r\n"
				+ "              <ATB00270>\r\n"
				+ "                <ATB00280>"+zhusuo25quanjiao+"\r\n"
				+ "\r\n"
				+ "</ATB00280>\r\n"
				+ "              </ATB00270>\r\n"
				+ "            </ATB00230>\r\n"
				+ "          </ATB00000>\r\n"
				+ "          <ATC00000>\r\n"
				+ "            <ATC00010>\r\n"
				+ "              <ATC00020>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00020>\r\n"
				+ "            </ATC00010>\r\n"
				+ "            <ATC00040>\r\n"
				+ "              <ATC00050>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00050>\r\n"
				+ "            </ATC00040>\r\n"
				+ "            <ATC00090>\r\n"
				+ "              <ATC00100>\r\n"
				+ "                <kubun_CD>1</kubun_CD>\r\n"
				+ "              </ATC00100>\r\n"
				+ "              <ATC00110>\r\n"
				+ "                <ATC00120>\r\n"
				+ "                  <gen:era>5</gen:era>\r\n"
				+ "                  <gen:yy>5</gen:yy>\r\n"
				+ "                  <gen:mm>1</gen:mm>\r\n"
				+ "                  <gen:dd>1</gen:dd>\r\n"
				+ "                </ATC00120>\r\n"
				+ "                <ATC00130>\r\n"
				+ "                  <gen:era>5</gen:era>\r\n"
				+ "                  <gen:yy>5</gen:yy>\r\n"
				+ "                  <gen:mm>12</gen:mm>\r\n"
				+ "                  <gen:dd>31</gen:dd>\r\n"
				+ "                </ATC00130>\r\n"
				+ "              </ATC00110>\r\n"
				+ "            </ATC00090>\r\n"
				+ "            <ATC00140>\r\n"
				+ "              <ATC00150>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00150>\r\n"
				+ "            </ATC00140>\r\n"
				+ "            <ATC00190>\r\n"
				+ "              <ATC00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00210>\r\n"
				+ "            </ATC00190>\r\n"
				+ "            <ATC00190>\r\n"
				+ "              <ATC00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00210>\r\n"
				+ "            </ATC00190>\r\n"
				+ "            <ATC00270>\r\n"
				+ "              <ATC00290>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ATC00290>\r\n"
				+ "            </ATC00270>\r\n"
				+ "          </ATC00000>\r\n"
				+ "          <ATD00000>\r\n"
				+ "            <ATD00010>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATD00010>\r\n"
				+ "            <ATD00020>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATD00020>\r\n"
				+ "            <ATD00030>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATD00030>\r\n"
				+ "            <ATD00040>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATD00040>\r\n"
				+ "            <ATD00050>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ATD00050>\r\n"
				+ "          </ATD00000>\r\n"
				+ "          <ATE00000>"+PXXXX+"</ATE00000>\r\n"
				+ "        </SOZ074>\r\n"
				+ "      </TENPU>\r\n"
				+ "</CONTENTS>\r\n"
				+ "";

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;
		}

	}


	private static void set_ncc_itax(File myFile) throws IOException {
		fileContent = FuncUtils.readFileContent(myFile);
		fileContent = fileContent.replace("\n", " ").replace("\t", " "); // 替换换行符和制表符为空格

		key__ = "<param name=\"申告等名称管理\">   <container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\"/>  </param>  <param name=\"添付書類管理\">   <container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\"/>";
		value = "<param name=\"申告等名称管理\">   <container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\"/>  </param>  <param name=\"添付書類管理\">   <container name=\"添付書類管理_申告等名称管理\" progId=\"nta.CLCAttachedManager.1\"/>"
				+ "";

		if (fileContent.contains(key__)) {
			++count;
			fileContent = fileContent.replace(key__, value);
		}

		formattedXml = formatXmlWithStandalone(fileContent);
//		formattedXml = fileContent;

		fileContent = formattedXml;


		//不能替换的地方
//		key__ = "<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SHA020&quot; reportName=&quot;消費税及び地方消費税の申告書(簡易課税用)&quot; leafId=&quot;&quot; page=&quot;1&quot;&gt;&lt;page reportId=&quot;SHA020-1&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABH00000&amp;gt;&amp;lt;ABH00020 IDREF=&amp;quot;ZEIMUSHO&amp;quot;/&amp;gt;&amp;lt;ABH00030 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABH00040 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABH00050&amp;gt;&amp;lt;ABH00060 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00070 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00050&amp;gt;&amp;lt;ABH00080 IDREF=&amp;quot;NOZEISHA_BANGO&amp;quot;/&amp;gt;&amp;lt;ABH00090&amp;gt;&amp;lt;ABH00100 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00110 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00090&amp;gt;&amp;lt;ABH00115&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00115&amp;gt;&amp;lt;ABH00120&amp;gt;&amp;lt;ABH00130 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABH00140 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABH00120&amp;gt;&amp;lt;ABH00150 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;ABH00250&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00250&amp;gt;&amp;lt;ABH00230&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00230&amp;gt;&amp;lt;ABH00240&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00240&amp;gt;&amp;lt;/ABH00000&amp;gt;&amp;lt;ABI00000&amp;gt;&amp;lt;ABI00010&amp;gt;5675000&amp;lt;/ABI00010&amp;gt;&amp;lt;ABI00020&amp;gt;442650&amp;lt;/ABI00020&amp;gt;&amp;lt;ABI00040&amp;gt;&amp;lt;ABI00050&amp;gt;354120&amp;lt;/ABI00050&amp;gt;&amp;lt;ABI00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;354120&amp;lt;/ABI00080&amp;gt;&amp;lt;/ABI00040&amp;gt;&amp;lt;ABI00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00090&amp;gt;&amp;lt;ABI00100 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00100&amp;gt;&amp;lt;ABI00120 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00120&amp;gt;&amp;lt;ABI00130 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00130&amp;gt;&amp;lt;ABI00170&amp;gt;5675797&amp;lt;/ABI00170&amp;gt;&amp;lt;ABI00180&amp;gt;0&amp;lt;/ABI00180&amp;gt;&amp;lt;/ABI00000&amp;gt;&amp;lt;ABJ00000&amp;gt;&amp;lt;ABJ00010&amp;gt;&amp;lt;ABJ00030&amp;gt;88500&amp;lt;/ABJ00030&amp;gt;&amp;lt;/ABJ00010&amp;gt;&amp;lt;ABJ00040&amp;gt;&amp;lt;ABJ00060&amp;gt;24900&amp;lt;/ABJ00060&amp;gt;&amp;lt;/ABJ00040&amp;gt;&amp;lt;ABJ00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;24900&amp;lt;/ABJ00080&amp;gt;&amp;lt;ABJ00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABJ00090&amp;gt;&amp;lt;ABJ00130&amp;gt;113400&amp;lt;/ABJ00130&amp;gt;&amp;lt;/ABJ00000&amp;gt;&amp;lt;ABK00000&amp;gt;&amp;lt;ABK00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00010&amp;gt;&amp;lt;ABK00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00020&amp;gt;&amp;lt;ABK00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00030&amp;gt;&amp;lt;ABK00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00040&amp;gt;&amp;lt;/ABK00000&amp;gt;&amp;lt;ABL00000&amp;gt;&amp;lt;ABL00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00010&amp;gt;&amp;lt;ABL00020&amp;gt;&amp;lt;ABL00060&amp;gt;&amp;lt;ABL00070&amp;gt;5676000&amp;lt;/ABL00070&amp;gt;&amp;lt;ABL00080&amp;gt;100&amp;lt;/ABL00080&amp;gt;&amp;lt;/ABL00060&amp;gt;&amp;lt;/ABL00020&amp;gt;&amp;lt;ABL00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00210&amp;gt;&amp;lt;/ABL00000&amp;gt;&amp;lt;ABY00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABY00000&amp;gt;&amp;lt;ABW00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABW00000&amp;gt;&amp;lt;ABX00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABX00000&amp;gt;&lt;/page&gt;&lt;page reportId=&quot;SHA020-2&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABM00000&amp;gt;&amp;lt;ABM00010 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABM00020 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABM00030&amp;gt;&amp;lt;ABM00040 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00050 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00030&amp;gt;&amp;lt;ABM00060&amp;gt;&amp;lt;ABM00070 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00080 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00060&amp;gt;&amp;lt;ABM00090&amp;gt;&amp;lt;ABM00100 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABM00110 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABM00090&amp;gt;&amp;lt;ABM00120 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;/ABM00000&amp;gt;&amp;lt;ABN00000&amp;gt;&amp;lt;ABN00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00010&amp;gt;&amp;lt;ABN00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00020&amp;gt;&amp;lt;/ABN00000&amp;gt;&amp;lt;ABO00000&amp;gt;5675000&amp;lt;/ABO00000&amp;gt;&amp;lt;ABP00000&amp;gt;&amp;lt;ABP00050&amp;gt;5675797&amp;lt;/ABP00050&amp;gt;&amp;lt;ABP00060 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;5675797&amp;lt;/ABP00060&amp;gt;&amp;lt;/ABP00000&amp;gt;&amp;lt;ABR00000 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;442650&amp;lt;/ABR00000&amp;gt;&amp;lt;ABS00000&amp;gt;&amp;lt;ABS00050&amp;gt;442650&amp;lt;/ABS00050&amp;gt;&amp;lt;/ABS00000&amp;gt;&amp;lt;ABV00000&amp;gt;&amp;lt;ABV00010 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABV00010&amp;gt;&amp;lt;ABV00040&amp;gt;88500&amp;lt;/ABV00040&amp;gt;&amp;lt;/ABV00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>";
//		value = "<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SHA020&quot; reportName=&quot;消費税及び地方消費税の申告書(簡易課税用)&quot; leafId=&quot;&quot; page=&quot;1&quot;&gt;&lt;page reportId=&quot;SHA020-1&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABH00000&amp;gt;&amp;lt;ABH00020 IDREF=&amp;quot;ZEIMUSHO&amp;quot;/&amp;gt;&amp;lt;ABH00030 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABH00040 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABH00050&amp;gt;&amp;lt;ABH00060 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00070 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00050&amp;gt;&amp;lt;ABH00080 IDREF=&amp;quot;NOZEISHA_BANGO&amp;quot;/&amp;gt;&amp;lt;ABH00090&amp;gt;&amp;lt;ABH00100 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00110 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00090&amp;gt;&amp;lt;ABH00115&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00115&amp;gt;&amp;lt;ABH00120&amp;gt;&amp;lt;ABH00130 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABH00140 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABH00120&amp;gt;&amp;lt;ABH00150 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;ABH00250&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00250&amp;gt;&amp;lt;ABH00200&amp;gt;&amp;lt;ABH00210 IDREF=&amp;quot;DAIRI_NM&amp;quot;/&amp;gt;&amp;lt;ABH00220 IDREF=&amp;quot;DAIRI_TEL&amp;quot;/&amp;gt;&amp;lt;/ABH00200&amp;gt;&amp;lt;ABH00230&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00230&amp;gt;&amp;lt;ABH00240&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00240&amp;gt;&amp;lt;/ABH00000&amp;gt;&amp;lt;ABI00000&amp;gt;&amp;lt;ABI00010&amp;gt;5675000&amp;lt;/ABI00010&amp;gt;&amp;lt;ABI00020&amp;gt;442650&amp;lt;/ABI00020&amp;gt;&amp;lt;ABI00040&amp;gt;&amp;lt;ABI00050&amp;gt;354120&amp;lt;/ABI00050&amp;gt;&amp;lt;ABI00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;354120&amp;lt;/ABI00080&amp;gt;&amp;lt;/ABI00040&amp;gt;&amp;lt;ABI00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00090&amp;gt;&amp;lt;ABI00100 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00100&amp;gt;&amp;lt;ABI00120 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00120&amp;gt;&amp;lt;ABI00130 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00130&amp;gt;&amp;lt;ABI00170&amp;gt;5675797&amp;lt;/ABI00170&amp;gt;&amp;lt;ABI00180&amp;gt;0&amp;lt;/ABI00180&amp;gt;&amp;lt;/ABI00000&amp;gt;&amp;lt;ABJ00000&amp;gt;&amp;lt;ABJ00010&amp;gt;&amp;lt;ABJ00030&amp;gt;88500&amp;lt;/ABJ00030&amp;gt;&amp;lt;/ABJ00010&amp;gt;&amp;lt;ABJ00040&amp;gt;&amp;lt;ABJ00060&amp;gt;24900&amp;lt;/ABJ00060&amp;gt;&amp;lt;/ABJ00040&amp;gt;&amp;lt;ABJ00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;24900&amp;lt;/ABJ00080&amp;gt;&amp;lt;ABJ00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABJ00090&amp;gt;&amp;lt;ABJ00130&amp;gt;113400&amp;lt;/ABJ00130&amp;gt;&amp;lt;/ABJ00000&amp;gt;&amp;lt;ABK00000&amp;gt;&amp;lt;ABK00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00010&amp;gt;&amp;lt;ABK00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00020&amp;gt;&amp;lt;ABK00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00030&amp;gt;&amp;lt;ABK00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00040&amp;gt;&amp;lt;/ABK00000&amp;gt;&amp;lt;ABL00000&amp;gt;&amp;lt;ABL00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00010&amp;gt;&amp;lt;ABL00020&amp;gt;&amp;lt;ABL00060&amp;gt;&amp;lt;ABL00070&amp;gt;5676000&amp;lt;/ABL00070&amp;gt;&amp;lt;ABL00080&amp;gt;100&amp;lt;/ABL00080&amp;gt;&amp;lt;/ABL00060&amp;gt;&amp;lt;/ABL00020&amp;gt;&amp;lt;ABL00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00210&amp;gt;&amp;lt;/ABL00000&amp;gt;&amp;lt;ABY00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABY00000&amp;gt;&amp;lt;ABW00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABW00000&amp;gt;&amp;lt;ABX00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABX00000&amp;gt;&lt;/page&gt;&lt;page reportId=&quot;SHA020-2&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABM00000&amp;gt;&amp;lt;ABM00010 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABM00020 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABM00030&amp;gt;&amp;lt;ABM00040 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00050 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00030&amp;gt;&amp;lt;ABM00060&amp;gt;&amp;lt;ABM00070 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00080 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00060&amp;gt;&amp;lt;ABM00090&amp;gt;&amp;lt;ABM00100 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABM00110 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABM00090&amp;gt;&amp;lt;ABM00120 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;/ABM00000&amp;gt;&amp;lt;ABN00000&amp;gt;&amp;lt;ABN00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00010&amp;gt;&amp;lt;ABN00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00020&amp;gt;&amp;lt;/ABN00000&amp;gt;&amp;lt;ABO00000&amp;gt;5675000&amp;lt;/ABO00000&amp;gt;&amp;lt;ABP00000&amp;gt;&amp;lt;ABP00050&amp;gt;5675797&amp;lt;/ABP00050&amp;gt;&amp;lt;ABP00060 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;5675797&amp;lt;/ABP00060&amp;gt;&amp;lt;/ABP00000&amp;gt;&amp;lt;ABR00000 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;442650&amp;lt;/ABR00000&amp;gt;&amp;lt;ABS00000&amp;gt;&amp;lt;ABS00050&amp;gt;442650&amp;lt;/ABS00050&amp;gt;&amp;lt;/ABS00000&amp;gt;&amp;lt;ABV00000&amp;gt;&amp;lt;ABV00010 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABV00010&amp;gt;&amp;lt;ABV00040&amp;gt;88500&amp;lt;/ABV00040&amp;gt;&amp;lt;/ABV00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>"
//				+ "";
//
//		if (formattedXml.contains(key__)) {
//			++count;
//			formattedXml = formattedXml.replace(key__, value);
//
//		} else {
//			key__list = key__list + "," + key__;
//		}


		key__ = "<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\"/>";
		value = "<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\">\r\n"
				+ "							<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "								<itemclass name=\"添付書類\" progId=\"nta.CLCAttachedItem.1\">\r\n"
				+ "									<param name=\"スキーマファイル名\">somu\\SOZ074-001.xsd</param>\r\n"
				+ "									<param name=\"スタイルシートファイル名\"/>\r\n"
				+ "									<param name=\"ステータス\">8</param>\r\n"
				+ "									<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "									<param name=\"バージョン\">1.0</param>\r\n"
				+ "									<param name=\"依頼者のXPath\">ATB00000/ATB00230/ATB00240/ATB00250</param>\r\n"
				+ "									<param name=\"更新日時\">2024-04-12 13:36:12</param>\r\n"
				+ "									<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "									<param name=\"氏名又は名称\">"+shiming25quanjiao+"\r\n"
				+ "</param>\r\n"
				+ "									<param name=\"種別\">2</param>\r\n"
				+ "									<param name=\"署名管理\">\r\n"
				+ "										<container name=\"署名管理\" progId=\"nta.CLCSignatureManager.1\"/>\r\n"
				+ "									</param>\r\n"
				+ "									<param name=\"署名者名\"/>\r\n"
				+ "									<param name=\"署名数\">0</param>\r\n"
				+ "									<param name=\"税目\"/>\r\n"
				+ "									<param name=\"税目コード\"/>\r\n"
				+ "									<param name=\"代理人\">iTAX税理士法人</param>\r\n"
				+ "									<param name=\"代理人XPath\">ATB00000/ATB00030/ATB00040/ATB00050</param>\r\n"
				+ "									<param name=\"帳票等管理\">\r\n"
				+ "										<container name=\"帳票等管理\" progId=\"nta.CLCReportManager.1\">\r\n"
				+ "											<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "												<itemclass name=\"帳票等\" progId=\"nta.CLCReport.1\">\r\n"
				+ "													<param name=\"CHMファイル名\">EX_ALL_001-001.chm</param>\r\n"
				+ "													<param name=\"CSVデータ\"/>\r\n"
				+ "													<param name=\"CSVデータ組み込みフラグ\"/>\r\n"
				+ "													<param name=\"CSVデータ追加種別\">0</param>\r\n"
				+ "													<param name=\"XBRL拡張\"/>\r\n"
				+ "													<param name=\"XFTファイル名\">CMAESOZ074-001_1.aep</param>\r\n"
				+ "													<param name=\"XSDファイル名\">somu\\SOZ074-001.xsd</param>\r\n"
				+ "													<param name=\"ステータス\">9</param>\r\n"
				+ "													<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "													<param name=\"バージョン\">1.0</param>\r\n"
				+ "													<param name=\"検査スクリプトファイル名\"/>\r\n"
				+ "													<param name=\"更新日時\">2024-04-12 13:36:12</param>\r\n"
				+ "													<param name=\"合計表の削除可否\">1</param>\r\n"
				+ "													<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "													<param name=\"作成者名\">"+shiming25quanjiao+"\r\n"
				+ "</param>\r\n"
				+ "													<param name=\"次葉ID\"/>\r\n"
				+ "													<param name=\"次葉XFTファイル名\"/>\r\n"
				+ "													<param name=\"次葉種別\">0</param>\r\n"
				+ "													<param name=\"次葉追加可能枚数\">0</param>\r\n"
				+ "													<param name=\"種別\">2</param>\r\n"
				+ "													<param name=\"順序数\">SOZ074-1</param>\r\n"
				+ "													<param name=\"相続税申告書参照作成初期状態フラグ\">0</param>\r\n"
				+ "													<param name=\"多面面限定様式識別ID\"/>\r\n"
				+ "													<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SOZ074&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; leafId=&quot;&quot;&gt;&lt;page reportId=&quot;SOZ074&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; status=&quot;0&quot;&gt;&amp;lt;ATB00000&amp;gt;&amp;lt;ATB00020&amp;gt;小石川税務署長&amp;lt;/ATB00020&amp;gt;&amp;lt;ATB00030&amp;gt;&amp;lt;ATB00040&amp;gt;&amp;lt;ATB00050&amp;gt;iTAX税理士法人&amp;lt;/ATB00050&amp;gt;&amp;lt;ATB00060&amp;gt;2674011631920063&amp;lt;/ATB00060&amp;gt;&amp;lt;/ATB00040&amp;gt;&amp;lt;ATB00070&amp;gt;&amp;lt;ATB00080&amp;gt;iTAX税理士法人&amp;lt;/ATB00080&amp;gt;&amp;lt;ATB00090&amp;gt;東京都千代田区神田猿楽町２－７－１７織本ビル５階&amp;lt;/ATB00090&amp;gt;&amp;lt;ATB00100&amp;gt;&amp;lt;gen:tel1&amp;gt;03&amp;lt;/gen:tel1&amp;gt;&amp;lt;gen:tel2&amp;gt;6272&amp;lt;/gen:tel2&amp;gt;&amp;lt;gen:tel3&amp;gt;8525&amp;lt;/gen:tel3&amp;gt;&amp;lt;/ATB00100&amp;gt;&amp;lt;/ATB00070&amp;gt;&amp;lt;ATB00110&amp;gt;&amp;lt;ATB00120&amp;gt;東京&amp;lt;/ATB00120&amp;gt;&amp;lt;ATB00130&amp;gt;神田&amp;lt;/ATB00130&amp;gt;&amp;lt;ATB00140&amp;gt;3986&amp;lt;/ATB00140&amp;gt;&amp;lt;/ATB00110&amp;gt;&amp;lt;/ATB00030&amp;gt;&amp;lt;ATB00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00150&amp;gt;&amp;lt;ATB00160&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;6&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;2&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;8&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATB00160&amp;gt;&amp;lt;ATB00170&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00170&amp;gt;&amp;lt;ATB00180&amp;gt;&amp;lt;ATB00190&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00190&amp;gt;&amp;lt;ATB00200&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00200&amp;gt;&amp;lt;ATB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00210&amp;gt;&amp;lt;/ATB00180&amp;gt;&amp;lt;ATB00220&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00220&amp;gt;&amp;lt;ATB00230&amp;gt;&amp;lt;ATB00240&amp;gt;&amp;lt;ATB00250&amp;gt;"+shiming25quanjiao+"\r\n"
				+ "&amp;lt;/ATB00250&amp;gt;&amp;lt;ATB00260&amp;gt;"+bangou+"&amp;lt;/ATB00260&amp;gt;&amp;lt;/ATB00240&amp;gt;&amp;lt;ATB00270&amp;gt;&amp;lt;ATB00280&amp;gt;"+zhusuo25quanjiao+"\r\n"
				+ "\r\n"
				+ "&amp;lt;/ATB00280&amp;gt;&amp;lt;/ATB00270&amp;gt;&amp;lt;/ATB00230&amp;gt;&amp;lt;/ATB00000&amp;gt;&amp;lt;ATC00000&amp;gt;&amp;lt;ATC00010&amp;gt;&amp;lt;ATC00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00020&amp;gt;&amp;lt;/ATC00010&amp;gt;&amp;lt;ATC00040&amp;gt;&amp;lt;ATC00050&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00050&amp;gt;&amp;lt;/ATC00040&amp;gt;&amp;lt;ATC00090&amp;gt;&amp;lt;ATC00100&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00100&amp;gt;&amp;lt;ATC00110&amp;gt;&amp;lt;ATC00120&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;1&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;1&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATC00120&amp;gt;&amp;lt;ATC00130&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;12&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;31&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATC00130&amp;gt;&amp;lt;/ATC00110&amp;gt;&amp;lt;/ATC00090&amp;gt;&amp;lt;ATC00140&amp;gt;&amp;lt;ATC00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00150&amp;gt;&amp;lt;/ATC00140&amp;gt;&amp;lt;ATC00190&amp;gt;&amp;lt;ATC00200&amp;gt;&amp;lt;kubun_CD/&amp;gt;&amp;lt;/ATC00200&amp;gt;&amp;lt;ATC00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00210&amp;gt;&amp;lt;ATC00220&amp;gt;&amp;lt;ATC00230&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00230&amp;gt;&amp;lt;ATC00240&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00240&amp;gt;&amp;lt;ATC00250&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;/ATC00250&amp;gt;&amp;lt;ATC00260&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;/ATC00260&amp;gt;&amp;lt;/ATC00220&amp;gt;&amp;lt;/ATC00190&amp;gt;&amp;lt;ATC00190&amp;gt;&amp;lt;ATC00200&amp;gt;&amp;lt;kubun_CD/&amp;gt;&amp;lt;/ATC00200&amp;gt;&amp;lt;ATC00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00210&amp;gt;&amp;lt;ATC00220&amp;gt;&amp;lt;ATC00230&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00230&amp;gt;&amp;lt;ATC00240&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00240&amp;gt;&amp;lt;ATC00250&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;/ATC00250&amp;gt;&amp;lt;ATC00260&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;/ATC00260&amp;gt;&amp;lt;/ATC00220&amp;gt;&amp;lt;/ATC00190&amp;gt;&amp;lt;ATC00270&amp;gt;&amp;lt;ATC00290&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00290&amp;gt;&amp;lt;/ATC00270&amp;gt;&amp;lt;/ATC00000&amp;gt;&amp;lt;ATD00000&amp;gt;&amp;lt;ATD00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00010&amp;gt;&amp;lt;ATD00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00020&amp;gt;&amp;lt;ATD00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00030&amp;gt;&amp;lt;ATD00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00040&amp;gt;&amp;lt;ATD00050&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00050&amp;gt;&amp;lt;/ATD00000&amp;gt;&amp;lt;ATE00000&amp;gt;"+PXXXX+"&amp;lt;/ATE00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>\r\n"
				+ "													<param name=\"帳票データ２\"/>\r\n"
				+ "													<param name=\"帳票データ３\"/>\r\n"
				+ "													<param name=\"帳票データ４\"/>\r\n"
				+ "													<param name=\"帳票種別ノード作成用\">4</param>\r\n"
				+ "													<param name=\"帳票種別一覧表示用\">1</param>\r\n"
				+ "													<param name=\"帳票種別管理単位\">1</param>\r\n"
				+ "													<param name=\"帳票数\">1</param>\r\n"
				+ "													<param name=\"排他対象様式ID\"/>\r\n"
				+ "													<param name=\"必須\"/>\r\n"
				+ "													<param name=\"複数帳票\">1</param>\r\n"
				+ "													<param name=\"編集中\">0</param>\r\n"
				+ "													<param name=\"面数\">1</param>\r\n"
				+ "													<param name=\"様式ID\">SOZ074</param>\r\n"
				+ "													<param name=\"様式名称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "													<param name=\"様式名称略称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "												</itemclass>\r\n"
				+ "											</item>\r\n"
				+ "										</container>\r\n"
				+ "									</param>\r\n"
				+ "									<param name=\"添付書類本体\">&lt;SOZ074 VR=&quot;1.0&quot; id=&quot;SOZ074-1&quot; page=&quot;1&quot; xmlns=&quot;http://xml.e-tax.nta.go.jp/XSD/somu&quot; softNM=&quot;ntaclient&quot; sakuseiNM=&quot;"+shiming25quanjiao+" &quot; xmlns:gen=&quot;http://xml.e-tax.nta.go.jp/XSD/general&quot; xmlns:kyo=&quot;http://xml.e-tax.nta.go.jp/XSD/kyotsu&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; sakuseiDay=&quot;2024-04-12&quot;&gt;&lt;ATB00000&gt;&lt;ATB00020&gt;小石川税務署長&lt;/ATB00020&gt;&lt;ATB00030&gt;&lt;ATB00040&gt;&lt;ATB00050&gt;iTAX税理士法人&lt;/ATB00050&gt;&lt;ATB00060&gt;2674011631920063&lt;/ATB00060&gt;&lt;/ATB00040&gt;&lt;ATB00070&gt;&lt;ATB00080&gt;iTAX税理士法人&lt;/ATB00080&gt;&lt;ATB00090&gt;東京都千代田区神田猿楽町２－７－１７織本ビル５階&lt;/ATB00090&gt;&lt;ATB00100&gt;&lt;gen:tel1&gt;03&lt;/gen:tel1&gt;&lt;gen:tel2&gt;6272&lt;/gen:tel2&gt;&lt;gen:tel3&gt;8525&lt;/gen:tel3&gt;&lt;/ATB00100&gt;&lt;/ATB00070&gt;&lt;ATB00110&gt;&lt;ATB00120&gt;東京&lt;/ATB00120&gt;&lt;ATB00130&gt;神田&lt;/ATB00130&gt;&lt;ATB00140&gt;3986&lt;/ATB00140&gt;&lt;/ATB00110&gt;&lt;/ATB00030&gt;&lt;ATB00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00150&gt;&lt;ATB00160&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;6&lt;/gen:yy&gt;&lt;gen:mm&gt;2&lt;/gen:mm&gt;&lt;gen:dd&gt;8&lt;/gen:dd&gt;&lt;/ATB00160&gt;&lt;ATB00170&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00170&gt;&lt;ATB00180&gt;&lt;ATB00190&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00190&gt;&lt;ATB00200&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00200&gt;&lt;ATB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00210&gt;&lt;/ATB00180&gt;&lt;ATB00220&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00220&gt;&lt;ATB00230&gt;&lt;ATB00240&gt;&lt;ATB00250&gt;"+shiming25quanjiao+"\r\n"
				+ "&lt;/ATB00250&gt;&lt;ATB00260&gt;"+bangou+"&lt;/ATB00260&gt;&lt;/ATB00240&gt;&lt;ATB00270&gt;&lt;ATB00280&gt;"+zhusuo25quanjiao+"\r\n"
				+ "\r\n"
				+ "&lt;/ATB00280&gt;&lt;/ATB00270&gt;&lt;/ATB00230&gt;&lt;/ATB00000&gt;&lt;ATC00000&gt;&lt;ATC00010&gt;&lt;ATC00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00020&gt;&lt;/ATC00010&gt;&lt;ATC00040&gt;&lt;ATC00050&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00050&gt;&lt;/ATC00040&gt;&lt;ATC00090&gt;&lt;ATC00100&gt;&lt;kubun_CD&gt;1&lt;/kubun_CD&gt;&lt;/ATC00100&gt;&lt;ATC00110&gt;&lt;ATC00120&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;1&lt;/gen:mm&gt;&lt;gen:dd&gt;1&lt;/gen:dd&gt;&lt;/ATC00120&gt;&lt;ATC00130&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;12&lt;/gen:mm&gt;&lt;gen:dd&gt;31&lt;/gen:dd&gt;&lt;/ATC00130&gt;&lt;/ATC00110&gt;&lt;/ATC00090&gt;&lt;ATC00140&gt;&lt;ATC00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00150&gt;&lt;/ATC00140&gt;&lt;ATC00190&gt;&lt;ATC00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00210&gt;&lt;/ATC00190&gt;&lt;ATC00190&gt;&lt;ATC00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00210&gt;&lt;/ATC00190&gt;&lt;ATC00270&gt;&lt;ATC00290&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00290&gt;&lt;/ATC00270&gt;&lt;/ATC00000&gt;&lt;ATD00000&gt;&lt;ATD00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00010&gt;&lt;ATD00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00020&gt;&lt;ATD00030&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00030&gt;&lt;ATD00040&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00040&gt;&lt;ATD00050&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00050&gt;&lt;/ATD00000&gt;&lt;ATE00000&gt;"+PXXXX+"&lt;/ATE00000&gt;&lt;/SOZ074&gt;\r\n"
				+ "</param>\r\n"
				+ "									<param name=\"添付書類名\"/>\r\n"
				+ "									<param name=\"不要ノード\">0</param>\r\n"
				+ "									<param name=\"編集可否\">0</param>\r\n"
				+ "									<param name=\"様式ID\">SOZ074</param>\r\n"
				+ "								</itemclass>\r\n"
				+ "							</item>\r\n"
				+ "						</container>"
				+ "";

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;

		}





		key__ = "<container name=\"添付書類管理_申告等名称管理\" progId=\"nta.CLCAttachedManager.1\"/>";
		value = "<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\">\r\n"
				+ "			<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "				<itemclass name=\"添付書類\" progId=\"nta.CLCAttachedItem.1\">\r\n"
				+ "					<param name=\"スキーマファイル名\">somu\\SOZ074-001.xsd</param>\r\n"
				+ "					<param name=\"スタイルシートファイル名\"/>\r\n"
				+ "					<param name=\"ステータス\">2</param>\r\n"
				+ "					<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "					<param name=\"バージョン\">1.0</param>\r\n"
				+ "					<param name=\"依頼者のXPath\">ATB00000/ATB00230/ATB00240/ATB00250</param>\r\n"
				+ "					<param name=\"更新日時\">2024-04-12 13:36:12</param>\r\n"
				+ "					<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "					<param name=\"氏名又は名称\">"+shiming25quanjiao+"\r\n"
				+ "</param>\r\n"
				+ "					<param name=\"種別\">2</param>\r\n"
				+ "					<param name=\"署名管理\">\r\n"
				+ "						<container name=\"署名管理\" progId=\"nta.CLCSignatureManager.1\"/>\r\n"
				+ "					</param>\r\n"
				+ "					<param name=\"署名者名\"/>\r\n"
				+ "					<param name=\"署名数\">0</param>\r\n"
				+ "					<param name=\"税目\"/>\r\n"
				+ "					<param name=\"税目コード\"/>\r\n"
				+ "					<param name=\"代理人\">iTAX税理士法人</param>\r\n"
				+ "					<param name=\"代理人XPath\">ATB00000/ATB00030/ATB00040/ATB00050</param>\r\n"
				+ "					<param name=\"帳票等管理\">\r\n"
				+ "						<container name=\"帳票等管理\" progId=\"nta.CLCReportManager.1\">\r\n"
				+ "							<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "								<itemclass name=\"帳票等\" progId=\"nta.CLCReport.1\">\r\n"
				+ "									<param name=\"CHMファイル名\">EX_ALL_001-001.chm</param>\r\n"
				+ "									<param name=\"CSVデータ\"/>\r\n"
				+ "									<param name=\"CSVデータ組み込みフラグ\"/>\r\n"
				+ "									<param name=\"CSVデータ追加種別\">0</param>\r\n"
				+ "									<param name=\"XBRL拡張\"/>\r\n"
				+ "									<param name=\"XFTファイル名\">CMAESOZ074-001_1.aep</param>\r\n"
				+ "									<param name=\"XSDファイル名\">somu\\SOZ074-001.xsd</param>\r\n"
				+ "									<param name=\"ステータス\">1</param>\r\n"
				+ "									<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "									<param name=\"バージョン\">1.0</param>\r\n"
				+ "									<param name=\"検査スクリプトファイル名\"/>\r\n"
				+ "									<param name=\"更新日時\">2024-04-12 13:36:12</param>\r\n"
				+ "									<param name=\"合計表の削除可否\">1</param>\r\n"
				+ "									<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "									<param name=\"作成者名\">"+shiming25quanjiao+"\r\n"
				+ "</param>\r\n"
				+ "									<param name=\"次葉ID\"/>\r\n"
				+ "									<param name=\"次葉XFTファイル名\"/>\r\n"
				+ "									<param name=\"次葉種別\">0</param>\r\n"
				+ "									<param name=\"次葉追加可能枚数\">0</param>\r\n"
				+ "									<param name=\"種別\">0</param>\r\n"
				+ "									<param name=\"順序数\">SOZ074-1</param>\r\n"
				+ "									<param name=\"相続税申告書参照作成初期状態フラグ\">0</param>\r\n"
				+ "									<param name=\"多面面限定様式識別ID\"/>\r\n"
				+ "									<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SOZ074&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; leafId=&quot;&quot;&gt;&lt;page reportId=&quot;SOZ074&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; status=&quot;0&quot;&gt;&amp;lt;ATB00000&amp;gt;&amp;lt;ATB00020&amp;gt;小石川税務署長&amp;lt;/ATB00020&amp;gt;&amp;lt;ATB00030&amp;gt;&amp;lt;ATB00040&amp;gt;&amp;lt;ATB00050&amp;gt;iTAX税理士法人&amp;lt;/ATB00050&amp;gt;&amp;lt;ATB00060&amp;gt;2674011631920063&amp;lt;/ATB00060&amp;gt;&amp;lt;/ATB00040&amp;gt;&amp;lt;ATB00070&amp;gt;&amp;lt;ATB00080&amp;gt;iTAX税理士法人&amp;lt;/ATB00080&amp;gt;&amp;lt;ATB00090&amp;gt;東京都千代田区神田猿楽町２－７－１７織本ビル５階&amp;lt;/ATB00090&amp;gt;&amp;lt;ATB00100&amp;gt;&amp;lt;gen:tel1&amp;gt;03&amp;lt;/gen:tel1&amp;gt;&amp;lt;gen:tel2&amp;gt;6272&amp;lt;/gen:tel2&amp;gt;&amp;lt;gen:tel3&amp;gt;8525&amp;lt;/gen:tel3&amp;gt;&amp;lt;/ATB00100&amp;gt;&amp;lt;/ATB00070&amp;gt;&amp;lt;ATB00110&amp;gt;&amp;lt;ATB00120&amp;gt;東京&amp;lt;/ATB00120&amp;gt;&amp;lt;ATB00130&amp;gt;神田&amp;lt;/ATB00130&amp;gt;&amp;lt;ATB00140&amp;gt;3986&amp;lt;/ATB00140&amp;gt;&amp;lt;/ATB00110&amp;gt;&amp;lt;/ATB00030&amp;gt;&amp;lt;ATB00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00150&amp;gt;&amp;lt;ATB00160&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;6&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;2&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;8&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATB00160&amp;gt;&amp;lt;ATB00170&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00170&amp;gt;&amp;lt;ATB00180&amp;gt;&amp;lt;ATB00190&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00190&amp;gt;&amp;lt;ATB00200&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00200&amp;gt;&amp;lt;ATB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00210&amp;gt;&amp;lt;/ATB00180&amp;gt;&amp;lt;ATB00220&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATB00220&amp;gt;&amp;lt;ATB00230&amp;gt;&amp;lt;ATB00240&amp;gt;&amp;lt;ATB00250&amp;gt;"+shiming25quanjiao+"\r\n"
				+ "&amp;lt;/ATB00250&amp;gt;&amp;lt;ATB00260&amp;gt;"+bangou+"&amp;lt;/ATB00260&amp;gt;&amp;lt;/ATB00240&amp;gt;&amp;lt;ATB00270&amp;gt;&amp;lt;ATB00280&amp;gt;"+zhusuo25quanjiao+"\r\n"
				+ "\r\n"
				+ "&amp;lt;/ATB00280&amp;gt;&amp;lt;/ATB00270&amp;gt;&amp;lt;/ATB00230&amp;gt;&amp;lt;/ATB00000&amp;gt;&amp;lt;ATC00000&amp;gt;&amp;lt;ATC00010&amp;gt;&amp;lt;ATC00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00020&amp;gt;&amp;lt;/ATC00010&amp;gt;&amp;lt;ATC00040&amp;gt;&amp;lt;ATC00050&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00050&amp;gt;&amp;lt;/ATC00040&amp;gt;&amp;lt;ATC00090&amp;gt;&amp;lt;ATC00100&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00100&amp;gt;&amp;lt;ATC00110&amp;gt;&amp;lt;ATC00120&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;1&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;1&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATC00120&amp;gt;&amp;lt;ATC00130&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;12&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;31&amp;lt;/gen:dd&amp;gt;&amp;lt;/ATC00130&amp;gt;&amp;lt;/ATC00110&amp;gt;&amp;lt;/ATC00090&amp;gt;&amp;lt;ATC00140&amp;gt;&amp;lt;ATC00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00150&amp;gt;&amp;lt;/ATC00140&amp;gt;&amp;lt;ATC00190&amp;gt;&amp;lt;ATC00200&amp;gt;&amp;lt;kubun_CD/&amp;gt;&amp;lt;/ATC00200&amp;gt;&amp;lt;ATC00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00210&amp;gt;&amp;lt;ATC00220&amp;gt;&amp;lt;ATC00230&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00230&amp;gt;&amp;lt;ATC00240&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00240&amp;gt;&amp;lt;ATC00250&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;/ATC00250&amp;gt;&amp;lt;ATC00260&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;/ATC00260&amp;gt;&amp;lt;/ATC00220&amp;gt;&amp;lt;/ATC00190&amp;gt;&amp;lt;ATC00190&amp;gt;&amp;lt;ATC00200&amp;gt;&amp;lt;kubun_CD/&amp;gt;&amp;lt;/ATC00200&amp;gt;&amp;lt;ATC00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00210&amp;gt;&amp;lt;ATC00220&amp;gt;&amp;lt;ATC00230&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00230&amp;gt;&amp;lt;ATC00240&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;gen:dd/&amp;gt;&amp;lt;/ATC00240&amp;gt;&amp;lt;ATC00250&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;gen:mm/&amp;gt;&amp;lt;/ATC00250&amp;gt;&amp;lt;ATC00260&amp;gt;&amp;lt;gen:era/&amp;gt;&amp;lt;gen:yy/&amp;gt;&amp;lt;/ATC00260&amp;gt;&amp;lt;/ATC00220&amp;gt;&amp;lt;/ATC00190&amp;gt;&amp;lt;ATC00270&amp;gt;&amp;lt;ATC00290&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATC00290&amp;gt;&amp;lt;/ATC00270&amp;gt;&amp;lt;/ATC00000&amp;gt;&amp;lt;ATD00000&amp;gt;&amp;lt;ATD00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00010&amp;gt;&amp;lt;ATD00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00020&amp;gt;&amp;lt;ATD00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00030&amp;gt;&amp;lt;ATD00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00040&amp;gt;&amp;lt;ATD00050&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ATD00050&amp;gt;&amp;lt;/ATD00000&amp;gt;&amp;lt;ATE00000&amp;gt;"+PXXXX+"&amp;lt;/ATE00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>\r\n"
				+ "									<param name=\"帳票データ２\"/>\r\n"
				+ "									<param name=\"帳票データ３\"/>\r\n"
				+ "									<param name=\"帳票データ４\"/>\r\n"
				+ "									<param name=\"帳票種別ノード作成用\">4</param>\r\n"
				+ "									<param name=\"帳票種別一覧表示用\">1</param>\r\n"
				+ "									<param name=\"帳票種別管理単位\">1</param>\r\n"
				+ "									<param name=\"帳票数\">1</param>\r\n"
				+ "									<param name=\"排他対象様式ID\"/>\r\n"
				+ "									<param name=\"必須\"/>\r\n"
				+ "									<param name=\"複数帳票\">1</param>\r\n"
				+ "									<param name=\"編集中\">0</param>\r\n"
				+ "									<param name=\"面数\">1</param>\r\n"
				+ "									<param name=\"様式ID\">SOZ074</param>\r\n"
				+ "									<param name=\"様式名称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "									<param name=\"様式名称略称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "								</itemclass>\r\n"
				+ "							</item>\r\n"
				+ "						</container>\r\n"
				+ "					</param>\r\n"
				+ "					<param name=\"添付書類本体\">&lt;SOZ074 VR=&quot;1.0&quot; id=&quot;SOZ074-1&quot; page=&quot;1&quot; xmlns=&quot;http://xml.e-tax.nta.go.jp/XSD/somu&quot; softNM=&quot;ntaclient&quot; sakuseiNM=&quot;"+shiming25quanjiao+" &quot; xmlns:gen=&quot;http://xml.e-tax.nta.go.jp/XSD/general&quot; xmlns:kyo=&quot;http://xml.e-tax.nta.go.jp/XSD/kyotsu&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; sakuseiDay=&quot;2024-04-12&quot;&gt;&lt;ATB00000&gt;&lt;ATB00020&gt;小石川税務署長&lt;/ATB00020&gt;&lt;ATB00030&gt;&lt;ATB00040&gt;&lt;ATB00050&gt;iTAX税理士法人&lt;/ATB00050&gt;&lt;ATB00060&gt;2674011631920063&lt;/ATB00060&gt;&lt;/ATB00040&gt;&lt;ATB00070&gt;&lt;ATB00080&gt;iTAX税理士法人&lt;/ATB00080&gt;&lt;ATB00090&gt;東京都千代田区神田猿楽町２－７－１７織本ビル５階&lt;/ATB00090&gt;&lt;ATB00100&gt;&lt;gen:tel1&gt;03&lt;/gen:tel1&gt;&lt;gen:tel2&gt;6272&lt;/gen:tel2&gt;&lt;gen:tel3&gt;8525&lt;/gen:tel3&gt;&lt;/ATB00100&gt;&lt;/ATB00070&gt;&lt;ATB00110&gt;&lt;ATB00120&gt;東京&lt;/ATB00120&gt;&lt;ATB00130&gt;神田&lt;/ATB00130&gt;&lt;ATB00140&gt;3986&lt;/ATB00140&gt;&lt;/ATB00110&gt;&lt;/ATB00030&gt;&lt;ATB00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00150&gt;&lt;ATB00160&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;6&lt;/gen:yy&gt;&lt;gen:mm&gt;2&lt;/gen:mm&gt;&lt;gen:dd&gt;8&lt;/gen:dd&gt;&lt;/ATB00160&gt;&lt;ATB00170&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00170&gt;&lt;ATB00180&gt;&lt;ATB00190&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00190&gt;&lt;ATB00200&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00200&gt;&lt;ATB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00210&gt;&lt;/ATB00180&gt;&lt;ATB00220&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATB00220&gt;&lt;ATB00230&gt;&lt;ATB00240&gt;&lt;ATB00250&gt;"+shiming25quanjiao+"\r\n"
				+ "&lt;/ATB00250&gt;&lt;ATB00260&gt;"+bangou+"&lt;/ATB00260&gt;&lt;/ATB00240&gt;&lt;ATB00270&gt;&lt;ATB00280&gt;"+zhusuo25quanjiao+"\r\n"
				+ "\r\n"
				+ "&lt;/ATB00280&gt;&lt;/ATB00270&gt;&lt;/ATB00230&gt;&lt;/ATB00000&gt;&lt;ATC00000&gt;&lt;ATC00010&gt;&lt;ATC00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00020&gt;&lt;/ATC00010&gt;&lt;ATC00040&gt;&lt;ATC00050&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00050&gt;&lt;/ATC00040&gt;&lt;ATC00090&gt;&lt;ATC00100&gt;&lt;kubun_CD&gt;1&lt;/kubun_CD&gt;&lt;/ATC00100&gt;&lt;ATC00110&gt;&lt;ATC00120&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;1&lt;/gen:mm&gt;&lt;gen:dd&gt;1&lt;/gen:dd&gt;&lt;/ATC00120&gt;&lt;ATC00130&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;12&lt;/gen:mm&gt;&lt;gen:dd&gt;31&lt;/gen:dd&gt;&lt;/ATC00130&gt;&lt;/ATC00110&gt;&lt;/ATC00090&gt;&lt;ATC00140&gt;&lt;ATC00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00150&gt;&lt;/ATC00140&gt;&lt;ATC00190&gt;&lt;ATC00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00210&gt;&lt;/ATC00190&gt;&lt;ATC00190&gt;&lt;ATC00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00210&gt;&lt;/ATC00190&gt;&lt;ATC00270&gt;&lt;ATC00290&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATC00290&gt;&lt;/ATC00270&gt;&lt;/ATC00000&gt;&lt;ATD00000&gt;&lt;ATD00010&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00010&gt;&lt;ATD00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00020&gt;&lt;ATD00030&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00030&gt;&lt;ATD00040&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00040&gt;&lt;ATD00050&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ATD00050&gt;&lt;/ATD00000&gt;&lt;ATE00000&gt;"+PXXXX+"&lt;/ATE00000&gt;&lt;/SOZ074&gt;\r\n"
				+ "</param>\r\n"
				+ "					<param name=\"添付書類名\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "					<param name=\"不要ノード\">1</param>\r\n"
				+ "					<param name=\"編集可否\">0</param>\r\n"
				+ "					<param name=\"様式ID\">SOZ074</param>\r\n"
				+ "				</itemclass>\r\n"
				+ "			</item>\r\n"
				+ "		</container>"
				+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}

			key__ = "<container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\"/>";
			value = "<container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\">\r\n"
					+ "              <item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
					+ "                <itemclass name=\"要素名称\" progId=\"nta.CLCItemName.1\">\r\n"
					+ "                  <param name=\"個数\">1</param>\r\n"
					+ "                </itemclass>\r\n"
					+ "              </item>\r\n"
					+ "            </container>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等氏名\"/>";
			value = "<param name=\"代理人等氏名\">ｉＴＡＸ税理士法人</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等氏名読み\"/>";
			value = "<param name=\"代理人等氏名読み\">アイタックス</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等住所\"/>";
			value = "<param name=\"代理人等住所\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号加入者番号\"/>";
			value = "<param name=\"代理人等電話番号加入者番号\">8525</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号市外局番\"/>";
			value = "<param name=\"代理人等電話番号市外局番\">03</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号市内局番\"/>";
			value = "<param name=\"代理人等電話番号市内局番\">6272</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等郵便番号下4桁\"/>";
			value = "<param name=\"代理人等郵便番号下4桁\">0064</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等郵便番号上3桁\"/>";
			value = "<param name=\"代理人等郵便番号上3桁\">101</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等利用者識別番号\"/>";
			value = "<param name=\"代理人等利用者識別番号\">2674011631920063</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}







	}


	private static void set_xtx_bps(File myFile, t_etax_account_infoExBean t_etax_account_infoExBean) throws IOException {
		fileContent = FuncUtils.readFileContent(myFile);
		formattedXml = formatXmlWithStandalone(fileContent);
		fileContent = formattedXml;

		key__ = "<TENPU_SEC/>";
		value = "<TENPU_SEC>\r\n"
				+ "            <rdf:Seq>\r\n"
				+ "              <rdf:li>\r\n"
				+ "                <rdf:description about=\"#TENPU\" />\r\n"
				+ "              </rdf:li>\r\n"
				+ "            </rdf:Seq>\r\n"
				+ "          </TENPU_SEC>"
				+ "";
		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;
			//key__list = key__list + "," + key__.substring(0, (key__.length()<20?key__.length():20));
		}



		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {


		} else {

		}


		//法人と個人は違うため

//		key__ = "</DAIHYO_ZIP>";
//		value = "</DAIHYO_ZIP>\r\n"


//		key__ = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>";
//		if (formattedXml.contains(key__)) {
////			++count;
//			formattedXml = formattedXml.replace(key__, "");
//
//		} else {
////			key__list = key__list + "," + key__;
//		}


		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			key__ = "</NOZEISHA_TEL>";
			value = "</NOZEISHA_TEL>\r\n"
					+ "<DAIRI_ID ID=\"DAIRI_ID\">2674011631920063</DAIRI_ID>\r\n"
					+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">アイタックス</DAIRI_NM_KN>\r\n"
					+ "        <DAIRI_NM ID=\"DAIRI_NM\">ｉＴＡＸ税理士法人</DAIRI_NM>\r\n"
					+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">\r\n"
					+ "          <gen:zip1>101</gen:zip1>\r\n"
					+ "          <gen:zip2>0064</gen:zip2>\r\n"
					+ "        </DAIRI_ZIP>\r\n"
					+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</DAIRI_ADR>\r\n"
					+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">\r\n"
					+ "          <gen:tel1>03</gen:tel1>\r\n"
					+ "          <gen:tel2>6272</gen:tel2>\r\n"
					+ "          <gen:tel3>8525</gen:tel3>\r\n"
					+ "        </DAIRI_TEL>"
					+ ""
					+ "";

		} else {
			key__ = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>";
			value = "<DAIHYO_ADR ID=\"DAIHYO_ADR\">外国</DAIHYO_ADR>\r\n"
				+ "<DAIRI_ID ID=\"DAIRI_ID\">1595560602927032</DAIRI_ID>\r\n"
				+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">ビーピーエスコクサイゼイリシホウジン</DAIRI_NM_KN>\r\n"
				+ "        <DAIRI_NM ID=\"DAIRI_NM\">ＢＰＳ国際税理士法人</DAIRI_NM>\r\n"
				+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">\r\n"
				+ "          <gen:zip1>104</gen:zip1>\r\n"
				+ "          <gen:zip2>0061</gen:zip2>\r\n"
				+ "        </DAIRI_ZIP>\r\n"
				+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都中央区銀座8-8-5 陽栄銀座ビル4階</DAIRI_ADR>\r\n"
				+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">\r\n"
				+ "          <gen:tel1>03</gen:tel1>\r\n"
				+ "          <gen:tel2>6264</gen:tel2>\r\n"
				+ "          <gen:tel3>3477</gen:tel3>\r\n"
				+ "        </DAIRI_TEL>"
				+ ""
				+ "";

		}

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;

		}


		//TODO
//		key__ = "</ABH00250>";
//		value = "</ABH00250>\r\n"
//				+ "            <ABH00200>\r\n"
//				+ "              <ABH00210 IDREF=\"DAIRI_NM\" />\r\n"
//				+ "              <ABH00220 IDREF=\"DAIRI_TEL\" />\r\n"
//				+ "            </ABH00200>"
//				+ ""
//				+ "";
//		if (formattedXml.contains(key__)) {
//			++count;
//			formattedXml = formattedXml.replace(key__, value);
//
//		} else {
//			key__list = key__list + "," + key__;
//
//		}




		//TODO
		//没找到，特殊处理</IT>
//		key__ = "</IT>";
//		value = "</IT>\r\n"

//		key__ = "</SHB067>";
//		value = "</SHB067>\r\n"

		key__ = "</CONTENTS>";
		value = "\r\n"
				+ "      <TENPU id=\"TENPU\">\r\n"
				+ "        <SOZ072 VR=\"1.0\" id=\"SOZ07224022516432982719\" page=\"1\" sakuseiDay=\"2024-02-25\" sakuseiNM=\"shiming30banjiao\" softNM=\"ntaclient\" xmlns=\"http://xml.e-tax.nta.go.jp/XSD/somu\" xmlns:gen=\"http://xml.e-tax.nta.go.jp/XSD/general\" xmlns:kyo=\"http://xml.e-tax.nta.go.jp/XSD/kyotsu\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n"
				+ "          <ABA00000>\r\n"
				+ "            <ABA00010>\r\n"
				+ "              <gen:era>5</gen:era>\r\n"
				+ "              <gen:yy>6</gen:yy>\r\n"
				+ "              <gen:mm>2</gen:mm>\r\n"
				+ "              <gen:dd>25</gen:dd>\r\n"
				+ "            </ABA00010>\r\n"
				+ "            <ABA00020>小石川税務署長</ABA00020>\r\n"
				+ "            <ABA00030>\r\n"
				+ "              <ABA00040>ＢＰＳ国際税理士法人</ABA00040>\r\n"
				+ "              <ABA00050>\r\n"
				+ "                <ABA00060>ＢＰＳ国際税理士法人</ABA00060>\r\n"
				+ "                <ABA00070>東京都中央区銀座8-8-5 陽栄銀座ビル4階</ABA00070>\r\n"
				+ "                <ABA00080>\r\n"
				+ "                  <gen:tel1>03</gen:tel1>\r\n"
				+ "                  <gen:tel2>6264</gen:tel2>\r\n"
				+ "                  <gen:tel3>3477</gen:tel3>\r\n"
				+ "                </ABA00080>\r\n"
				+ "              </ABA00050>\r\n"
				+ "              <ABA00110>\r\n"
				+ "                <ABA00120>東京</ABA00120>\r\n"
				+ "                <ABA00130>京橋</ABA00130>\r\n"
				+ "                <ABA00140>2302</ABA00140>\r\n"
				+ "              </ABA00110>\r\n"
				+ "            </ABA00030>\r\n"
				+ "            <ABA00150>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ABA00150>\r\n"
				+ "            <ABA00160>\r\n"
				+ "              <gen:era>5</gen:era>\r\n"
				+ "              <gen:yy>6</gen:yy>\r\n"
				+ "              <gen:mm>1</gen:mm>\r\n"
				+ "              <gen:dd>1</gen:dd>\r\n"
				+ "            </ABA00160>\r\n"
				+ "            <ABA00170>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ABA00170>\r\n"
				+ "            <ABA00180>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ABA00180>\r\n"
				+ "            <ABA00190>\r\n"
				+ "              <kubun_CD>2</kubun_CD>\r\n"
				+ "            </ABA00190>\r\n"
				+ "            <ABA00200>\r\n"
				+ "              <ABA00210>shiming30banjiao</ABA00210>\r\n"
				+ "              <ABA00220>zhusuo100banjiao</ABA00220>\r\n"
				+ "            </ABA00200>\r\n"
				+ "          </ABA00000>\r\n"
				+ "          <ABB00000>\r\n"
				+ "            <ABB00010>\r\n"
				+ "              <ABB00020>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00020>\r\n"
				+ "            </ABB00010>\r\n"
				+ "            <ABB00040>\r\n"
				+ "              <ABB00050>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00050>\r\n"
				+ "            </ABB00040>\r\n"
				+ "            <ABB00090>\r\n"
				+ "              <ABB00100>\r\n"
				+ "                <kubun_CD>1</kubun_CD>\r\n"
				+ "              </ABB00100>\r\n"
				+ "              <ABB00110>\r\n"
				+ "                <ABB00120>\r\n"
				+ "                  <gen:era>5</gen:era>\r\n"
				+ "                  <gen:yy>5</gen:yy>\r\n"
				+ "                  <gen:mm>1</gen:mm>\r\n"
				+ "                  <gen:dd>1</gen:dd>\r\n"
				+ "                </ABB00120>\r\n"
				+ "                <ABB00130>\r\n"
				+ "                  <gen:era>5</gen:era>\r\n"
				+ "                  <gen:yy>5</gen:yy>\r\n"
				+ "                  <gen:mm>12</gen:mm>\r\n"
				+ "                  <gen:dd>31</gen:dd>\r\n"
				+ "                </ABB00130>\r\n"
				+ "              </ABB00110>\r\n"
				+ "            </ABB00090>\r\n"
				+ "            <ABB00140>\r\n"
				+ "              <ABB00150>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00150>\r\n"
				+ "            </ABB00140>\r\n"
				+ "            <ABB00190>\r\n"
				+ "              <ABB00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00210>\r\n"
				+ "            </ABB00190>\r\n"
				+ "            <ABB00190>\r\n"
				+ "              <ABB00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00210>\r\n"
				+ "            </ABB00190>\r\n"
				+ "            <ABB00190>\r\n"
				+ "              <ABB00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00210>\r\n"
				+ "            </ABB00190>\r\n"
				+ "            <ABB00190>\r\n"
				+ "              <ABB00210>\r\n"
				+ "                <kubun_CD>2</kubun_CD>\r\n"
				+ "              </ABB00210>\r\n"
				+ "            </ABB00190>\r\n"
				+ "          </ABB00000>\r\n"
				+ "          <ABC00000>PXXX</ABC00000>\r\n"
				+ "        </SOZ072>\r\n"
				+ "      </TENPU>\r\n"
				+ "</CONTENTS>\r\n"
				+ "";

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;
		}

	}


	private static void set_ncc_bps(File myFile) throws IOException {
		fileContent = FuncUtils.readFileContent(myFile);
		formattedXml = formatXmlWithStandalone(fileContent);
		fileContent = formattedXml;

		// 1月1日住所-フラグ 应该为空
		key__ = "<param name=\"1月1日住所-フラグ\">0</param>";
		value = "<param name=\"1月1日住所-フラグ\"/>"
				+ "";

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;
		}

		//不能替换的地方
//		key__ = "<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SHA020&quot; reportName=&quot;消費税及び地方消費税の申告書(簡易課税用)&quot; leafId=&quot;&quot; page=&quot;1&quot;&gt;&lt;page reportId=&quot;SHA020-1&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABH00000&amp;gt;&amp;lt;ABH00020 IDREF=&amp;quot;ZEIMUSHO&amp;quot;/&amp;gt;&amp;lt;ABH00030 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABH00040 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABH00050&amp;gt;&amp;lt;ABH00060 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00070 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00050&amp;gt;&amp;lt;ABH00080 IDREF=&amp;quot;NOZEISHA_BANGO&amp;quot;/&amp;gt;&amp;lt;ABH00090&amp;gt;&amp;lt;ABH00100 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00110 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00090&amp;gt;&amp;lt;ABH00115&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00115&amp;gt;&amp;lt;ABH00120&amp;gt;&amp;lt;ABH00130 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABH00140 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABH00120&amp;gt;&amp;lt;ABH00150 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;ABH00250&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00250&amp;gt;&amp;lt;ABH00230&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00230&amp;gt;&amp;lt;ABH00240&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00240&amp;gt;&amp;lt;/ABH00000&amp;gt;&amp;lt;ABI00000&amp;gt;&amp;lt;ABI00010&amp;gt;5675000&amp;lt;/ABI00010&amp;gt;&amp;lt;ABI00020&amp;gt;442650&amp;lt;/ABI00020&amp;gt;&amp;lt;ABI00040&amp;gt;&amp;lt;ABI00050&amp;gt;354120&amp;lt;/ABI00050&amp;gt;&amp;lt;ABI00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;354120&amp;lt;/ABI00080&amp;gt;&amp;lt;/ABI00040&amp;gt;&amp;lt;ABI00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00090&amp;gt;&amp;lt;ABI00100 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00100&amp;gt;&amp;lt;ABI00120 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00120&amp;gt;&amp;lt;ABI00130 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00130&amp;gt;&amp;lt;ABI00170&amp;gt;5675797&amp;lt;/ABI00170&amp;gt;&amp;lt;ABI00180&amp;gt;0&amp;lt;/ABI00180&amp;gt;&amp;lt;/ABI00000&amp;gt;&amp;lt;ABJ00000&amp;gt;&amp;lt;ABJ00010&amp;gt;&amp;lt;ABJ00030&amp;gt;88500&amp;lt;/ABJ00030&amp;gt;&amp;lt;/ABJ00010&amp;gt;&amp;lt;ABJ00040&amp;gt;&amp;lt;ABJ00060&amp;gt;24900&amp;lt;/ABJ00060&amp;gt;&amp;lt;/ABJ00040&amp;gt;&amp;lt;ABJ00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;24900&amp;lt;/ABJ00080&amp;gt;&amp;lt;ABJ00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABJ00090&amp;gt;&amp;lt;ABJ00130&amp;gt;113400&amp;lt;/ABJ00130&amp;gt;&amp;lt;/ABJ00000&amp;gt;&amp;lt;ABK00000&amp;gt;&amp;lt;ABK00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00010&amp;gt;&amp;lt;ABK00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00020&amp;gt;&amp;lt;ABK00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00030&amp;gt;&amp;lt;ABK00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00040&amp;gt;&amp;lt;/ABK00000&amp;gt;&amp;lt;ABL00000&amp;gt;&amp;lt;ABL00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00010&amp;gt;&amp;lt;ABL00020&amp;gt;&amp;lt;ABL00060&amp;gt;&amp;lt;ABL00070&amp;gt;5676000&amp;lt;/ABL00070&amp;gt;&amp;lt;ABL00080&amp;gt;100&amp;lt;/ABL00080&amp;gt;&amp;lt;/ABL00060&amp;gt;&amp;lt;/ABL00020&amp;gt;&amp;lt;ABL00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00210&amp;gt;&amp;lt;/ABL00000&amp;gt;&amp;lt;ABY00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABY00000&amp;gt;&amp;lt;ABW00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABW00000&amp;gt;&amp;lt;ABX00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABX00000&amp;gt;&lt;/page&gt;&lt;page reportId=&quot;SHA020-2&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABM00000&amp;gt;&amp;lt;ABM00010 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABM00020 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABM00030&amp;gt;&amp;lt;ABM00040 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00050 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00030&amp;gt;&amp;lt;ABM00060&amp;gt;&amp;lt;ABM00070 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00080 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00060&amp;gt;&amp;lt;ABM00090&amp;gt;&amp;lt;ABM00100 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABM00110 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABM00090&amp;gt;&amp;lt;ABM00120 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;/ABM00000&amp;gt;&amp;lt;ABN00000&amp;gt;&amp;lt;ABN00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00010&amp;gt;&amp;lt;ABN00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00020&amp;gt;&amp;lt;/ABN00000&amp;gt;&amp;lt;ABO00000&amp;gt;5675000&amp;lt;/ABO00000&amp;gt;&amp;lt;ABP00000&amp;gt;&amp;lt;ABP00050&amp;gt;5675797&amp;lt;/ABP00050&amp;gt;&amp;lt;ABP00060 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;5675797&amp;lt;/ABP00060&amp;gt;&amp;lt;/ABP00000&amp;gt;&amp;lt;ABR00000 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;442650&amp;lt;/ABR00000&amp;gt;&amp;lt;ABS00000&amp;gt;&amp;lt;ABS00050&amp;gt;442650&amp;lt;/ABS00050&amp;gt;&amp;lt;/ABS00000&amp;gt;&amp;lt;ABV00000&amp;gt;&amp;lt;ABV00010 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABV00010&amp;gt;&amp;lt;ABV00040&amp;gt;88500&amp;lt;/ABV00040&amp;gt;&amp;lt;/ABV00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>";
//		value = "<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SHA020&quot; reportName=&quot;消費税及び地方消費税の申告書(簡易課税用)&quot; leafId=&quot;&quot; page=&quot;1&quot;&gt;&lt;page reportId=&quot;SHA020-1&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABH00000&amp;gt;&amp;lt;ABH00020 IDREF=&amp;quot;ZEIMUSHO&amp;quot;/&amp;gt;&amp;lt;ABH00030 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABH00040 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABH00050&amp;gt;&amp;lt;ABH00060 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00070 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00050&amp;gt;&amp;lt;ABH00080 IDREF=&amp;quot;NOZEISHA_BANGO&amp;quot;/&amp;gt;&amp;lt;ABH00090&amp;gt;&amp;lt;ABH00100 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABH00110 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABH00090&amp;gt;&amp;lt;ABH00115&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00115&amp;gt;&amp;lt;ABH00120&amp;gt;&amp;lt;ABH00130 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABH00140 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABH00120&amp;gt;&amp;lt;ABH00150 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;ABH00250&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00250&amp;gt;&amp;lt;ABH00200&amp;gt;&amp;lt;ABH00210 IDREF=&amp;quot;DAIRI_NM&amp;quot;/&amp;gt;&amp;lt;ABH00220 IDREF=&amp;quot;DAIRI_TEL&amp;quot;/&amp;gt;&amp;lt;/ABH00200&amp;gt;&amp;lt;ABH00230&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00230&amp;gt;&amp;lt;ABH00240&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABH00240&amp;gt;&amp;lt;/ABH00000&amp;gt;&amp;lt;ABI00000&amp;gt;&amp;lt;ABI00010&amp;gt;5675000&amp;lt;/ABI00010&amp;gt;&amp;lt;ABI00020&amp;gt;442650&amp;lt;/ABI00020&amp;gt;&amp;lt;ABI00040&amp;gt;&amp;lt;ABI00050&amp;gt;354120&amp;lt;/ABI00050&amp;gt;&amp;lt;ABI00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;354120&amp;lt;/ABI00080&amp;gt;&amp;lt;/ABI00040&amp;gt;&amp;lt;ABI00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00090&amp;gt;&amp;lt;ABI00100 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00100&amp;gt;&amp;lt;ABI00120 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABI00120&amp;gt;&amp;lt;ABI00130 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABI00130&amp;gt;&amp;lt;ABI00170&amp;gt;5675797&amp;lt;/ABI00170&amp;gt;&amp;lt;ABI00180&amp;gt;0&amp;lt;/ABI00180&amp;gt;&amp;lt;/ABI00000&amp;gt;&amp;lt;ABJ00000&amp;gt;&amp;lt;ABJ00010&amp;gt;&amp;lt;ABJ00030&amp;gt;88500&amp;lt;/ABJ00030&amp;gt;&amp;lt;/ABJ00010&amp;gt;&amp;lt;ABJ00040&amp;gt;&amp;lt;ABJ00060&amp;gt;24900&amp;lt;/ABJ00060&amp;gt;&amp;lt;/ABJ00040&amp;gt;&amp;lt;ABJ00080 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;24900&amp;lt;/ABJ00080&amp;gt;&amp;lt;ABJ00090 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;0&amp;lt;/ABJ00090&amp;gt;&amp;lt;ABJ00130&amp;gt;113400&amp;lt;/ABJ00130&amp;gt;&amp;lt;/ABJ00000&amp;gt;&amp;lt;ABK00000&amp;gt;&amp;lt;ABK00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00010&amp;gt;&amp;lt;ABK00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00020&amp;gt;&amp;lt;ABK00030&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00030&amp;gt;&amp;lt;ABK00040&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABK00040&amp;gt;&amp;lt;/ABK00000&amp;gt;&amp;lt;ABL00000&amp;gt;&amp;lt;ABL00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00010&amp;gt;&amp;lt;ABL00020&amp;gt;&amp;lt;ABL00060&amp;gt;&amp;lt;ABL00070&amp;gt;5676000&amp;lt;/ABL00070&amp;gt;&amp;lt;ABL00080&amp;gt;100&amp;lt;/ABL00080&amp;gt;&amp;lt;/ABL00060&amp;gt;&amp;lt;/ABL00020&amp;gt;&amp;lt;ABL00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABL00210&amp;gt;&amp;lt;/ABL00000&amp;gt;&amp;lt;ABY00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABY00000&amp;gt;&amp;lt;ABW00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABW00000&amp;gt;&amp;lt;ABX00000&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABX00000&amp;gt;&lt;/page&gt;&lt;page reportId=&quot;SHA020-2&quot; reportName=&quot;&quot; status=&quot;0&quot;&gt;&amp;lt;ABM00000&amp;gt;&amp;lt;ABM00010 IDREF=&amp;quot;NOZEISHA_ADR&amp;quot;/&amp;gt;&amp;lt;ABM00020 IDREF=&amp;quot;NOZEISHA_TEL&amp;quot;/&amp;gt;&amp;lt;ABM00030&amp;gt;&amp;lt;ABM00040 IDREF=&amp;quot;NOZEISHA_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00050 IDREF=&amp;quot;NOZEISHA_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00030&amp;gt;&amp;lt;ABM00060&amp;gt;&amp;lt;ABM00070 IDREF=&amp;quot;DAIHYO_NM_KN&amp;quot;/&amp;gt;&amp;lt;ABM00080 IDREF=&amp;quot;DAIHYO_NM&amp;quot;/&amp;gt;&amp;lt;/ABM00060&amp;gt;&amp;lt;ABM00090&amp;gt;&amp;lt;ABM00100 IDREF=&amp;quot;KAZEI_KIKAN_FROM&amp;quot;/&amp;gt;&amp;lt;ABM00110 IDREF=&amp;quot;KAZEI_KIKAN_TO&amp;quot;/&amp;gt;&amp;lt;/ABM00090&amp;gt;&amp;lt;ABM00120 IDREF=&amp;quot;SHINKOKU_KBN&amp;quot;/&amp;gt;&amp;lt;/ABM00000&amp;gt;&amp;lt;ABN00000&amp;gt;&amp;lt;ABN00010&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00010&amp;gt;&amp;lt;ABN00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABN00020&amp;gt;&amp;lt;/ABN00000&amp;gt;&amp;lt;ABO00000&amp;gt;5675000&amp;lt;/ABO00000&amp;gt;&amp;lt;ABP00000&amp;gt;&amp;lt;ABP00050&amp;gt;5675797&amp;lt;/ABP00050&amp;gt;&amp;lt;ABP00060 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;5675797&amp;lt;/ABP00060&amp;gt;&amp;lt;/ABP00000&amp;gt;&amp;lt;ABR00000 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;442650&amp;lt;/ABR00000&amp;gt;&amp;lt;ABS00000&amp;gt;&amp;lt;ABS00050&amp;gt;442650&amp;lt;/ABS00050&amp;gt;&amp;lt;/ABS00000&amp;gt;&amp;lt;ABV00000&amp;gt;&amp;lt;ABV00010 AutoCalc=&amp;quot;1&amp;quot;&amp;gt;88500&amp;lt;/ABV00010&amp;gt;&amp;lt;ABV00040&amp;gt;88500&amp;lt;/ABV00040&amp;gt;&amp;lt;/ABV00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>"
//				+ "";
//
//		if (formattedXml.contains(key__)) {
//			++count;
//			formattedXml = formattedXml.replace(key__, value);
//		}



		key__ = "<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\"/>";
		value = "<container name=\"添付書類管理\" progId=\"nta.CLCAttachedManager.1\">\r\n"
				+ "							<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "								<itemclass name=\"添付書類\" progId=\"nta.CLCAttachedItem.1\">\r\n"
				+ "									<param name=\"スキーマファイル名\">somu\\SOZ072-001.xsd</param>\r\n"
				+ "									<param name=\"スタイルシートファイル名\"/>\r\n"
				+ "									<param name=\"ステータス\">8</param>\r\n"
				+ "									<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "									<param name=\"バージョン\">1.0</param>\r\n"
				+ "									<param name=\"依頼者のXPath\">ABA00000/ABA00200/ABA00210</param>\r\n"
				+ "									<param name=\"更新日時\">2024-02-25 17:04:09</param>\r\n"
				+ "									<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "									<param name=\"氏名又は名称\">shiming30banjiao</param>\r\n"
				+ "									<param name=\"種別\">2</param>\r\n"
				+ "									<param name=\"署名管理\">\r\n"
				+ "										<container name=\"署名管理\" progId=\"nta.CLCSignatureManager.1\"/>\r\n"
				+ "									</param>\r\n"
				+ "									<param name=\"署名者名\"/>\r\n"
				+ "									<param name=\"署名数\">0</param>\r\n"
				+ "									<param name=\"税目\"/>\r\n"
				+ "									<param name=\"税目コード\"/>\r\n"
				+ "									<param name=\"代理人\">ＢＰＳ国際税理士法人</param>\r\n"
				+ "									<param name=\"代理人XPath\">ABA00000/ABA00030/ABA00040</param>\r\n"
				+ "									<param name=\"帳票等管理\">\r\n"
				+ "										<container name=\"帳票等管理\" progId=\"nta.CLCReportManager.1\">\r\n"
				+ "											<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
				+ "												<itemclass name=\"帳票等\" progId=\"nta.CLCReport.1\">\r\n"
				+ "													<param name=\"CHMファイル名\">EX_ALL_001-001.chm</param>\r\n"
				+ "													<param name=\"CSVデータ\"/>\r\n"
				+ "													<param name=\"CSVデータ組み込みフラグ\"/>\r\n"
				+ "													<param name=\"CSVデータ追加種別\">0</param>\r\n"
				+ "													<param name=\"XBRL拡張\"/>\r\n"
				+ "													<param name=\"XFTファイル名\">CMAESOZ072-001_1.aep</param>\r\n"
				+ "													<param name=\"XSDファイル名\">somu\\SOZ072-001.xsd</param>\r\n"
				+ "													<param name=\"ステータス\">9</param>\r\n"
				+ "													<param name=\"ネームスペース\">http://xml.e-tax.nta.go.jp/XSD/somu</param>\r\n"
				+ "													<param name=\"バージョン\">1.0</param>\r\n"
				+ "													<param name=\"検査スクリプトファイル名\"/>\r\n"
				+ "													<param name=\"更新日時\">2024-02-25 17:04:09</param>\r\n"
				+ "													<param name=\"合計表の削除可否\">1</param>\r\n"
				+ "													<param name=\"作成ソフト名\">ntaclient</param>\r\n"
				+ "													<param name=\"作成者名\">shiming30banjiao</param>\r\n"
				+ "													<param name=\"次葉ID\"/>\r\n"
				+ "													<param name=\"次葉XFTファイル名\"/>\r\n"
				+ "													<param name=\"次葉種別\">0</param>\r\n"
				+ "													<param name=\"次葉追加可能枚数\">0</param>\r\n"
				+ "													<param name=\"種別\">2</param>\r\n"
				+ "													<param name=\"順序数\">SOZ072-1</param>\r\n"
				+ "													<param name=\"相続税申告書参照作成初期状態フラグ\">0</param>\r\n"
				+ "													<param name=\"多面面限定様式識別ID\"/>\r\n"
				+ "													<param name=\"帳票データ\">&lt;reportValue reportId=&quot;SOZ072&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; leafId=&quot;&quot;&gt;&lt;page reportId=&quot;SOZ072&quot; reportName=&quot;税務代理権限証書(令和6年4月1日以降提出分)&quot; status=&quot;0&quot;&gt;&amp;lt;ABA00000&amp;gt;&amp;lt;ABA00010&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;6&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;2&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;25&amp;lt;/gen:dd&amp;gt;&amp;lt;/ABA00010&amp;gt;&amp;lt;ABA00020&amp;gt;小石川税務署長&amp;lt;/ABA00020&amp;gt;&amp;lt;ABA00030&amp;gt;&amp;lt;ABA00040&amp;gt;ＢＰＳ国際税理士法人&amp;lt;/ABA00040&amp;gt;&amp;lt;ABA00050&amp;gt;&amp;lt;ABA00060&amp;gt;ＢＰＳ国際税理士法人&amp;lt;/ABA00060&amp;gt;&amp;lt;ABA00070&amp;gt;東京都中央区銀座8-8-5 陽栄銀座ビル4階&amp;lt;/ABA00070&amp;gt;&amp;lt;ABA00080&amp;gt;&amp;lt;gen:tel1&amp;gt;03&amp;lt;/gen:tel1&amp;gt;&amp;lt;gen:tel2&amp;gt;6264&amp;lt;/gen:tel2&amp;gt;&amp;lt;gen:tel3&amp;gt;3477&amp;lt;/gen:tel3&amp;gt;&amp;lt;/ABA00080&amp;gt;&amp;lt;/ABA00050&amp;gt;&amp;lt;ABA00110&amp;gt;&amp;lt;ABA00120&amp;gt;東京&amp;lt;/ABA00120&amp;gt;&amp;lt;ABA00130&amp;gt;京橋&amp;lt;/ABA00130&amp;gt;&amp;lt;ABA00140&amp;gt;2302&amp;lt;/ABA00140&amp;gt;&amp;lt;/ABA00110&amp;gt;&amp;lt;/ABA00030&amp;gt;&amp;lt;ABA00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABA00150&amp;gt;&amp;lt;ABA00160&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;6&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;1&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;1&amp;lt;/gen:dd&amp;gt;&amp;lt;/ABA00160&amp;gt;&amp;lt;ABA00170&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABA00170&amp;gt;&amp;lt;ABA00180&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABA00180&amp;gt;&amp;lt;ABA00190&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABA00190&amp;gt;&amp;lt;ABA00200&amp;gt;&amp;lt;ABA00210&amp;gt;shiming30banjiao&amp;lt;/ABA00210&amp;gt;&amp;lt;ABA00220&amp;gt;zhusuo100banjiao&amp;lt;/ABA00220&amp;gt;&amp;lt;/ABA00200&amp;gt;&amp;lt;/ABA00000&amp;gt;&amp;lt;ABB00000&amp;gt;&amp;lt;ABB00010&amp;gt;&amp;lt;ABB00020&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00020&amp;gt;&amp;lt;/ABB00010&amp;gt;&amp;lt;ABB00040&amp;gt;&amp;lt;ABB00050&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00050&amp;gt;&amp;lt;/ABB00040&amp;gt;&amp;lt;ABB00090&amp;gt;&amp;lt;ABB00100&amp;gt;&amp;lt;kubun_CD&amp;gt;1&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00100&amp;gt;&amp;lt;ABB00110&amp;gt;&amp;lt;ABB00120&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;1&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;1&amp;lt;/gen:dd&amp;gt;&amp;lt;/ABB00120&amp;gt;&amp;lt;ABB00130&amp;gt;&amp;lt;gen:era&amp;gt;5&amp;lt;/gen:era&amp;gt;&amp;lt;gen:yy&amp;gt;5&amp;lt;/gen:yy&amp;gt;&amp;lt;gen:mm&amp;gt;12&amp;lt;/gen:mm&amp;gt;&amp;lt;gen:dd&amp;gt;31&amp;lt;/gen:dd&amp;gt;&amp;lt;/ABB00130&amp;gt;&amp;lt;/ABB00110&amp;gt;&amp;lt;/ABB00090&amp;gt;&amp;lt;ABB00140&amp;gt;&amp;lt;ABB00150&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00150&amp;gt;&amp;lt;/ABB00140&amp;gt;&amp;lt;ABB00190&amp;gt;&amp;lt;ABB00200/&amp;gt;&amp;lt;ABB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00210&amp;gt;&amp;lt;ABB00220/&amp;gt;&amp;lt;/ABB00190&amp;gt;&amp;lt;ABB00190&amp;gt;&amp;lt;ABB00200/&amp;gt;&amp;lt;ABB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00210&amp;gt;&amp;lt;ABB00220/&amp;gt;&amp;lt;/ABB00190&amp;gt;&amp;lt;ABB00190&amp;gt;&amp;lt;ABB00200/&amp;gt;&amp;lt;ABB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00210&amp;gt;&amp;lt;ABB00220/&amp;gt;&amp;lt;/ABB00190&amp;gt;&amp;lt;ABB00190&amp;gt;&amp;lt;ABB00200/&amp;gt;&amp;lt;ABB00210&amp;gt;&amp;lt;kubun_CD&amp;gt;2&amp;lt;/kubun_CD&amp;gt;&amp;lt;/ABB00210&amp;gt;&amp;lt;ABB00220/&amp;gt;&amp;lt;/ABB00190&amp;gt;&amp;lt;/ABB00000&amp;gt;&amp;lt;ABC00000&amp;gt;PXXX&amp;lt;/ABC00000&amp;gt;&lt;/page&gt;&lt;/reportValue&gt;</param>\r\n"
				+ "													<param name=\"帳票データ２\"/>\r\n"
				+ "													<param name=\"帳票データ３\"/>\r\n"
				+ "													<param name=\"帳票データ４\"/>\r\n"
				+ "													<param name=\"帳票種別ノード作成用\">4</param>\r\n"
				+ "													<param name=\"帳票種別一覧表示用\">1</param>\r\n"
				+ "													<param name=\"帳票種別管理単位\">1</param>\r\n"
				+ "													<param name=\"帳票数\">1</param>\r\n"
				+ "													<param name=\"排他対象様式ID\"/>\r\n"
				+ "													<param name=\"必須\"/>\r\n"
				+ "													<param name=\"複数帳票\">1</param>\r\n"
				+ "													<param name=\"編集中\">0</param>\r\n"
				+ "													<param name=\"面数\">1</param>\r\n"
				+ "													<param name=\"様式ID\">SOZ072</param>\r\n"
				+ "													<param name=\"様式名称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "													<param name=\"様式名称略称\">税務代理権限証書(令和6年4月1日以降提出分)</param>\r\n"
				+ "												</itemclass>\r\n"
				+ "											</item>\r\n"
				+ "										</container>\r\n"
				+ "									</param>\r\n"
				+ "									<param name=\"添付書類本体\">&lt;SOZ072 VR=&quot;1.0&quot; id=&quot;SOZ072-1&quot; page=&quot;1&quot; xmlns=&quot;http://xml.e-tax.nta.go.jp/XSD/somu&quot; softNM=&quot;ntaclient&quot; sakuseiNM=&quot;shiming30banjiao&quot; xmlns:gen=&quot;http://xml.e-tax.nta.go.jp/XSD/general&quot; xmlns:kyo=&quot;http://xml.e-tax.nta.go.jp/XSD/kyotsu&quot; xmlns:xsi=&quot;http://www.w3.org/2001/XMLSchema-instance&quot; sakuseiDay=&quot;2024-02-25&quot;&gt;&lt;ABA00000&gt;&lt;ABA00010&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;6&lt;/gen:yy&gt;&lt;gen:mm&gt;2&lt;/gen:mm&gt;&lt;gen:dd&gt;25&lt;/gen:dd&gt;&lt;/ABA00010&gt;&lt;ABA00020&gt;小石川税務署長&lt;/ABA00020&gt;&lt;ABA00030&gt;&lt;ABA00040&gt;ＢＰＳ国際税理士法人&lt;/ABA00040&gt;&lt;ABA00050&gt;&lt;ABA00060&gt;ＢＰＳ国際税理士法人&lt;/ABA00060&gt;&lt;ABA00070&gt;東京都中央区銀座8-8-5 陽栄銀座ビル4階&lt;/ABA00070&gt;&lt;ABA00080&gt;&lt;gen:tel1&gt;03&lt;/gen:tel1&gt;&lt;gen:tel2&gt;6264&lt;/gen:tel2&gt;&lt;gen:tel3&gt;3477&lt;/gen:tel3&gt;&lt;/ABA00080&gt;&lt;/ABA00050&gt;&lt;ABA00110&gt;&lt;ABA00120&gt;東京&lt;/ABA00120&gt;&lt;ABA00130&gt;京橋&lt;/ABA00130&gt;&lt;ABA00140&gt;2302&lt;/ABA00140&gt;&lt;/ABA00110&gt;&lt;/ABA00030&gt;&lt;ABA00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABA00150&gt;&lt;ABA00160&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;6&lt;/gen:yy&gt;&lt;gen:mm&gt;1&lt;/gen:mm&gt;&lt;gen:dd&gt;1&lt;/gen:dd&gt;&lt;/ABA00160&gt;&lt;ABA00170&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABA00170&gt;&lt;ABA00180&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABA00180&gt;&lt;ABA00190&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABA00190&gt;&lt;ABA00200&gt;&lt;ABA00210&gt;shiming30banjiao&lt;/ABA00210&gt;&lt;ABA00220&gt;zhusuo100banjiao&lt;/ABA00220&gt;&lt;/ABA00200&gt;&lt;/ABA00000&gt;&lt;ABB00000&gt;&lt;ABB00010&gt;&lt;ABB00020&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00020&gt;&lt;/ABB00010&gt;&lt;ABB00040&gt;&lt;ABB00050&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00050&gt;&lt;/ABB00040&gt;&lt;ABB00090&gt;&lt;ABB00100&gt;&lt;kubun_CD&gt;1&lt;/kubun_CD&gt;&lt;/ABB00100&gt;&lt;ABB00110&gt;&lt;ABB00120&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;1&lt;/gen:mm&gt;&lt;gen:dd&gt;1&lt;/gen:dd&gt;&lt;/ABB00120&gt;&lt;ABB00130&gt;&lt;gen:era&gt;5&lt;/gen:era&gt;&lt;gen:yy&gt;5&lt;/gen:yy&gt;&lt;gen:mm&gt;12&lt;/gen:mm&gt;&lt;gen:dd&gt;31&lt;/gen:dd&gt;&lt;/ABB00130&gt;&lt;/ABB00110&gt;&lt;/ABB00090&gt;&lt;ABB00140&gt;&lt;ABB00150&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00150&gt;&lt;/ABB00140&gt;&lt;ABB00190&gt;&lt;ABB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00210&gt;&lt;/ABB00190&gt;&lt;ABB00190&gt;&lt;ABB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00210&gt;&lt;/ABB00190&gt;&lt;ABB00190&gt;&lt;ABB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00210&gt;&lt;/ABB00190&gt;&lt;ABB00190&gt;&lt;ABB00210&gt;&lt;kubun_CD&gt;2&lt;/kubun_CD&gt;&lt;/ABB00210&gt;&lt;/ABB00190&gt;&lt;/ABB00000&gt;&lt;ABC00000&gt;PXXX&lt;/ABC00000&gt;&lt;/SOZ072&gt;\r\n"
				+ "</param>\r\n"
				+ "									<param name=\"添付書類名\"/>\r\n"
				+ "									<param name=\"不要ノード\">0</param>\r\n"
				+ "									<param name=\"編集可否\">0</param>\r\n"
				+ "									<param name=\"様式ID\">SOZ072</param>\r\n"
				+ "								</itemclass>\r\n"
				+ "							</item>\r\n"
				+ "						</container>"
				+ "";

		if (formattedXml.contains(key__)) {
			++count;
			formattedXml = formattedXml.replace(key__, value);

		} else {
			key__list = key__list + "," + key__;

		}




			key__ = "<container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\"/>";
			value = "<container name=\"要素名称管理\" progId=\"nta.CLCNameManager.1\">\r\n"
					+ "							<item filename=\"\" key=\"\" keysave=\"true\" mode=\"0\" name=\"税務代理権限証書(令和6年4月1日以降提出分)\">\r\n"
					+ "								<itemclass name=\"要素名称\" progId=\"nta.CLCItemName.1\">\r\n"
					+ "									<param name=\"個数\">1</param>\r\n"
					+ "								</itemclass>\r\n"
					+ "							</item>\r\n"
					+ "						</container>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}




			key__ = "<param name=\"代理人等氏名\"/>";
			value = "<param name=\"代理人等氏名\">ＢＰＳ国際税理士法人</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等氏名読み\"/>";
			value = "<param name=\"代理人等氏名読み\">ビーピーエスコクサイゼイリシホウジン</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等住所\"/>";
			value = "<param name=\"代理人等住所\">東京都中央区銀座8-8-5 陽栄銀座ビル4階</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号加入者番号\"/>";
			value = "<param name=\"代理人等電話番号加入者番号\">3477</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号市外局番\"/>";
			value = "<param name=\"代理人等電話番号市外局番\">03</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等電話番号市内局番\"/>";
			value = "<param name=\"代理人等電話番号市内局番\">6264</param>"
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等郵便番号下4桁\"/>";
			value = "<param name=\"代理人等郵便番号下4桁\">0061</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等郵便番号上3桁\"/>";
			value = "<param name=\"代理人等郵便番号上3桁\">104</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}


			key__ = "<param name=\"代理人等利用者識別番号\"/>";
			value = "<param name=\"代理人等利用者識別番号\">1595560602927032</param>"
					+ ""
					+ "";
			if (formattedXml.contains(key__)) {
				++count;
				formattedXml = formattedXml.replace(key__, value);

			} else {
				key__list = key__list + "," + key__;

			}



	}

	 public static String formatXml(String unformattedXml) {
	        try {
	            // Create a Transformer
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();

	            // Set the output properties to format the XML
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // 保留XML声明

	            // Transform the XML
	            StringWriter writer = new StringWriter();
	            transformer.transform(new StreamSource(new StringReader(unformattedXml)), new StreamResult(writer));

	            // Get the formatted XML as a string
	            return writer.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return unformattedXml; // Return unformatted XML in case of error
	        }
	    }

	 /**
	     * 格式化XML内容并保留standalone属性。
	     *
	     * @param xmlContent 带格式化的XML内容。
	     * @return 格式化后的XML字符串。
	     */
	    public static String formatXmlWithStandalone(String xmlContent) {
	        try {

	        	xmlContent = xmlContent.replace("\r\n", "");
	        	xmlContent = xmlContent.replace("\n", "");
	        	xmlContent = xmlContent.replace("\t", "");

	            // 解析现有的XML内容
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document document = dBuilder.parse(new InputSource(new StringReader(xmlContent)));

	            // 创建具有正确输出属性的Transformer
	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
	            Transformer transformer = transformerFactory.newTransformer();
	            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
	            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no"); // 保留XML声明

	            // 将更新后的文档转换为字符串
	            StringWriter writer = new StringWriter();
	            transformer.transform(new DOMSource(document), new StreamResult(writer));

	            return writer.toString();
	        } catch (Exception e) {
	            e.printStackTrace();
	            return xmlContent; // 在发生错误时返回原始内容
	        }
	    }
}
