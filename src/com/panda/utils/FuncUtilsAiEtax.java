package com.panda.utils;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_jct_shenqingBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_jct_shenqingDao;
import com.panda.servlet.ai.ColumnDefinition;
import com.panda.servlet.ai.TableDefinition;

public class FuncUtilsAiEtax {

	private static Logger logger = Logger.getLogger(FuncUtilsAiEtax.class.toString());

	// 定义主数据存储结构
	public static TreeMap<String, TreeMap<String, String[]>> PropertyCsvMap_shouxu = new TreeMap<>();
	public static TreeMap<String, TreeMap<String, TreeMap<String, String[]>>> PropertyCsvMap_zhangpiao = new TreeMap<>();

	public static TreeMap<String, TreeMap<String, String[]>> PropertyPsMap_帳票フィールド仕様書 = new TreeMap<>();


	static LinkedHashMap<String, String> tblMap = new LinkedHashMap<>();
	// 假设文件夹路径
    static String directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）";

	static {


 		logger.info("start master Load.");

        // 递归遍历目录并读取csv文件
        try {

    		logger.info("Processed file and updated PropertyCsvMap_shouxu.");
            String directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）/07手続一覧等/01手続一覧";
            Files.walk(Paths.get(directoryPath))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_shouxu(path.toFile()));

    		logger.info("Processed file and updated processCsvFile_zhangpiao.");
            directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）/07手続一覧等/02手続内帳票対応表";
            Files.walk(Paths.get(directoryPath))
                    .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                    .forEach(path -> processCsvFile_zhangpiao(path.toFile()));


    		logger.info("Processed file and updated processPsFile_帳票フィールド仕様書.");
			directoryPath = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（一括ダウンロード）";
			Files.walk(Paths.get(directoryPath))
					.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".ps"))
					.forEach(path -> processPsFile_帳票フィールド仕様書(path.toFile()));

        } catch (IOException e) {
            e.printStackTrace();
        }




            // 递归遍历目录并读取csv文件
            try {

        		logger.info("Processed file and updated processCsvFile_shouxu.");
                Files.walk(Paths.get(directoryPath+"/07手続一覧等"))
                        .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                        .forEach(path -> processCsvFile_shouxu(path.toFile()));

        		logger.info("Processed file and updated processCsvFile_zhangpiao.");
                Files.walk(Paths.get(directoryPath+"/07手続一覧等"))
                        .filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".csv"))
                        .forEach(path -> processCsvFile_zhangpiao(path.toFile()));

            } catch (IOException e) {
                e.printStackTrace();
            }

        	/*
        	 * 定义t_etax_account_info
        	 */
        	//主名字
            tblMap.put("[定义]t_etax_account_info", "");
        	tblMap.put("t_etax_account_info.申告主体名称（英文）", "CompanyName_English");
        	tblMap.put("t_etax_account_info.申告主体地址（英文）", "address_English");



        	tblMap.put("t_etax_account_info.电话第一段（国家码）", "tel_country");
        	tblMap.put("t_etax_account_info.电话第二段", "tel_1");
        	tblMap.put("t_etax_account_info.电话第三段", "tel_2");
        	tblMap.put("t_etax_account_info.电话第四段", "tel_3");
        	tblMap.put("t_etax_account_info.公司或本人姓名（日语片假名）", "CompanyName_pianjiaming");
        	tblMap.put("t_etax_account_info.公司或本人姓名（英文）", "CompanyName_English");
        	tblMap.put("t_etax_account_info.公司或本人姓名（本国语言）", "CompanyName_Chinese");
        	tblMap.put("t_etax_account_info.公司代表人或经营场所名称（英文）", "DaibiaoName_English");
        	tblMap.put("t_etax_account_info.公司代表人或经营场所名称（日语片假名）", "DaibiaoName_pianjiaming");


        	tblMap.put("t_etax_account_info.公司或本人地址（日语片假名）", "address_pianjiaming");
        	tblMap.put("t_etax_account_info.公司或本人地址（英文）", "address_English");

        	tblMap.put("t_etax_account_info.公司成立年或本人出生月", "company_MM");
        	tblMap.put("t_etax_account_info.公司成立年或本人出生日", "company_DD");
        	tblMap.put("t_etax_account_info.公司注册资本金（请换算为日元）", "zhice_ziben");




        	//复数名字定义
        	tblMap.put("t_etax_account_info.公司カタガナ", "CompanyName_pianjiaming");
        	tblMap.put("t_etax_account_info.公司英文名称", "CompanyName_English");
        	tblMap.put("t_etax_account_info.代表人读音カタガナ", "DaibiaoName_pianjiaming");
        	tblMap.put("t_etax_account_info.代表人名字英文", "DaibiaoName_English");
        	tblMap.put("t_etax_account_info.申告主体类别", "user_type");





        	//扩展名字定义
        	tblMap.put("t_etax_account_info.公司成立年月日或本人出生年月日", "company_YYYYMMDD");




//        	tblMap.put("公司或本人姓名（日语片假名）", "CompanyName_pianjiaming");
//        	tblMap.put("电话第一段（国家码）", "tel_country");
//        	tblMap.put("电话第二段", "tel_1");
//        	tblMap.put("电话第三段", "tel_2");
//        	tblMap.put("电话第四段", "tel_3");
//        	tblMap.put("公司注册资本金（请换算为日元）", "zhice_ziben");

        	/*
        	 * 定义t_etax_account_res
        	 */
        	//主名字
        	tblMap.put("t_etax_account_res.etax番号", "bangou");
        	tblMap.put("t_etax_account_res.法人番号", "HoujinBangou");
        	tblMap.put("t_etax_account_res.インボイス番号", "InvoiceBangou");




        	/*
        	 * 定义t_xiaofeishui_shengao
        	 */

        	//主名字


            tblMap.put("[定义]t_xiaofeishui_shengao", "");
            tblMap.put("UPDATE_DATE", "UPDATE_DATE");
            tblMap.put("PDSK编号", "PDSK");
            tblMap.put("yyyymmdd_count", "yyyymmdd_count");
            tblMap.put("yyyy", "yyyy");
            tblMap.put("会计年度自", "shengao_qijian_from");
            tblMap.put("会计年度至", "shengao_qijian_to");
            tblMap.put("本申告主体在基准期间的日本课税销售额", "jizhun_qijian");
            tblMap.put("本申告主体在特定期间的日本课税销售额", "teding_qijian");
            tblMap.put("本申告主体在上一会计年度的日本课税销售额", "shangyi_niandu");
            tblMap.put("去年是否申告过消费税", "qunian_xiaofeishui_shengao");
            tblMap.put("本申告主体在该会计年度计算消费税时采用", "keshui_type");
            tblMap.put("去年消费税申告的消费税国税额", "qunian_xiaofeishui_guoshui");
            tblMap.put("含税总销售额", "hanshui_zongxiaoshoue");
            tblMap.put("适格请求书总支出额", "shige_qingqiushu_zongzhichue");
            tblMap.put("非适格请求书总支出额", "fei_shige_qingqiushu_zongzhichue");
            tblMap.put("进口消费税国税部分总额", "jinkou_xiaofeishui_guoshui_zonge");
            tblMap.put("activation_code", "activation_code");
            tblMap.put("email", "email");
            tblMap.put("法定中间申告次数", "fading_zhongjian_shengao_cishu");
            tblMap.put("法定中间申告单次对应月数", "fading_zhongjian_shengao_danci_duiying_yueshu");
            tblMap.put("法定中间申告单次国税额", "fading_zhongjian_shengao_danci_guoshui_e");
            tblMap.put("法定中间申告单次地方税额", "fading_zhongjian_shengao_danci_difangshui_e");
            tblMap.put("不含税销售额", "buhan_shui_xiaoshou_e");
            tblMap.put("课税标准额", "keshuibiao_zhun_e");
            tblMap.put("消费税额国税部分", "xiaofeishui_e_guoshui_bufen");
            tblMap.put("控除税额国税部分（有合规发票部分）", "kongchu_shui_e_guoshui_bufen_you_hegui_fapiao");
            tblMap.put("控除税额国税部分（无合规发票部分）", "kongchu_shui_e_guoshui_bufen_wu_hegui_fapiao");
            tblMap.put("控除税额国税部分（进口部分）", "kongchu_shui_e_guoshui_bufen_jinkou");
            tblMap.put("控除税额国税部分（合计）", "kongchu_shui_e_guoshui_bufen_heji");

            //简易课税（零售业）  OR  原则课税（2割特例）
            tblMap.put("默认抵扣额国税部分", "kongchu_shui_e_guoshui_bufen_heji");

            tblMap.put("全年应缴消费税国税部分", "quannian_yingjiao_xiaofeishui_guoshui_bufen");
            tblMap.put("中间申告应缴消费税国税部分", "zhongjian_shengao_yingjiao_xiaofeishui_guoshui_bufen");
            tblMap.put("确定申告应缴消费税国税部分", "queren_shengao_yingjiao_xiaofeishui_guoshui_bufen");
            tblMap.put("全年应缴消费税地方税部分", "quannian_yingjiao_xiaofeishui_difangshui_bufen");
            tblMap.put("中间申告应缴消费税地方税部分", "zhongjian_shengao_yingjiao_xiaofeishui_difangshui_bufen");
            tblMap.put("确定申告应缴消费税地方税部分", "queren_shengao_yingjiao_xiaofeishui_difangshui_bufen");
            tblMap.put("全年应缴消费税合计额", "quannian_yingjiao_xiaofeishui_heji");
            tblMap.put("中间申告应缴消费税合计额", "zhongjian_shengao_yingjiao_xiaofeishui_heji");
            tblMap.put("确定申告应缴消费税合计额", "queren_shengao_yingjiao_xiaofeishui_heji");


        	/*
        	 * 定义t_jct_shenqing
        	 *
CREATE TABLE `t_jct_shenqing` (
  `UPDATE_DATE` timestamp(6) NOT NULL,
  `yyyymmdd_count` bigint NOT NULL,
  `INSQ` varchar(45) DEFAULT NULL COMMENT 'JCT管理番号',
  `riben_kaishi_shiye_YYYY` varchar(4) DEFAULT NULL COMMENT '在日本开始事业的年',
  `riben_kaishi_shiye_MM` varchar(2) DEFAULT NULL COMMENT '在日本开始事业的月',
  `riben_kaishi_shiye_DD` varchar(2) DEFAULT NULL COMMENT '在日本开始事业的日',
  `xiaoshouerYYYY_2` varchar(45) DEFAULT NULL COMMENT '基准期间在日本的课税销售额（含税日元金额）',
  `xiaoshouerYYYY_1_half` varchar(45) DEFAULT NULL COMMENT '特定期间在日本的课税销售额（含税日元金额）',
  `keshui_or_mianshui` varchar(45) DEFAULT NULL COMMENT '该主体此刻是课税的还是免税的',
  `YYYY_1` varchar(45) DEFAULT NULL COMMENT '该主体是第一年在日本开始事业吗',
  `keshui_shiyezhe_wenshu` varchar(45) DEFAULT NULL COMMENT '该主体应该递交课税事业者文件吗',
  PRIMARY KEY (`yyyymmdd_count`),
  UNIQUE KEY `INSQ_UNIQUE` (`INSQ`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        	 */

            tblMap.put("[定义]t_jct_shenqing", "");
            tblMap.put("t_jct_shenqing.INSQ编号", "INSQ");

            tblMap.put("t_jct_shenqing.在日本开始事业的年", "riben_kaishi_shiye_YYYY");
            tblMap.put("t_jct_shenqing.在日本开始事业的月", "riben_kaishi_shiye_MM");
            tblMap.put("t_jct_shenqing.在日本开始事业的日", "riben_kaishi_shiye_DD");

            tblMap.put("t_jct_shenqing.基准期间在日本的课税销售额（含税日元金额）", "xiaoshouerYYYY_2");
            tblMap.put("t_jct_shenqing.特定期间在日本的课税销售额（含税日元金额）", "xiaoshouerYYYY_1_half");
            tblMap.put("t_jct_shenqing.该主体此刻是课税的还是免税的", "keshui_or_mianshui");
            tblMap.put("t_jct_shenqing.该主体是第一年在日本开始事业吗", "YYYY_1");
            tblMap.put("t_jct_shenqing.该主体应该递交课税事业者文件吗", "keshui_shiyezhe_wenshu");





            tblMap.put("公司或本人地址（日语片假名）", "");
            tblMap.put("电话第一段（国家码）", "");
            tblMap.put("电话第二段", "");
            tblMap.put("电话第三段", "");
            tblMap.put("电话第四段", "");
            tblMap.put("公司注册资本金（请换算为日元）", "");

     		logger.info("end");

        }



	// 处理每个CSV文件的示例方法
	public static void processCsvFile_shouxu(File File) {
//		logger.info("Processing csvFile：" + File.getPath().replace("\\", "/").replace(FuncUtils.projectPath, ""));
		//TODO
//		if (!csvFile.getName().startsWith("手続一覧（申告）")) {
//			return;
//		}

		String line = null;
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File), StandardCharsets.UTF_8))) {

			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] values = line.split(",", -1);

				// 获取csv第一列作为第一层的key
				String firstColumn = values[0];
				// 获取版本号作为第二层的key

				int i = 7;// 假设第8列是版本
				if (values.length < 7) {
					i = 2;
				}
				String versionKey = values[i];

				// 获取完整行内容，存储为String[]
				String[] rowData = values;

				// 构建数据结构，添加每一层的key和内容
				PropertyCsvMap_shouxu
						.computeIfAbsent(firstColumn, k -> new TreeMap<>())
						.put(versionKey, rowData);

//            	PropertyCsvMap_shouxu
//            	└── firstColumn (String)
//            	    └── versionKey (String) : rowData (String[])
			}

            if (br != null) {
            	br.close();
            }
		} catch (Exception e) {
			logger.error("Processing csv：" + line);
			e.printStackTrace();

        } finally {
            // 关闭资源

		}


	}


	// 处理每个CSV文件的示例方法
	public static void processPsFile_帳票フィールド仕様書(File File) {
//		logger.info("Processing csvFile：" + File.getPath().replace("\\", "/").replace(FuncUtils.projectPath, ""));

		//TODO
//		if (!File.getName().startsWith("帳票フィールド仕様書")) {
//			return;
//		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File), StandardCharsets.UTF_8))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] values = line.split(",");



				// 获取csv每列的值作为不同层级的key
				String firstKey = values[0];
				String secondKey = values[1];

				// 获取完整行内容，存储为String[]
				String[] rowData = values;

//				logger.info("master Load: " + line);
				if("SOZ042".equals(firstKey)) {
					firstKey = firstKey;
				}
				// 构建数据结构，添加每一层的key和内容
				PropertyPsMap_帳票フィールド仕様書
						.computeIfAbsent(firstKey, k -> new TreeMap<>())
						.put(secondKey, rowData);

//            	PropertyCsvMap_shouxu
//            	└── firstColumn (String)
//	                        └── secondKey (String) : rowData (String[])

				TreeMap<String, String[]> myTreeMap = PropertyPsMap_帳票フィールド仕様書.get(firstKey);
				if (!myTreeMap.containsKey("PS")) {

					LinkedHashMap<String, String[]> tempMap = new LinkedHashMap<>();
					// 先插入 "PS" 键到第一个位置
					tempMap.put("PS", new String[] { File.getParent() });
					// 将 myTreeMap 中的其他内容插入到 tempMap 中
					tempMap.putAll(myTreeMap);
					// 用 tempMap 更新 myTreeMap，以确保 "PS" 在第一个位置
					myTreeMap.clear();
					myTreeMap.putAll(tempMap);


				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}



	// 处理每个CSV文件的示例方法
	public static void processCsvFile_zhangpiao(File File) {
//		logger.info("Processing csvFile：" + File.getPath().replace("\\", "/").replace(FuncUtils.projectPath, ""));

		//TODO
		if (File.getName().startsWith("手続内帳票対応表(申告）") || File.getName().startsWith("手続内帳票対応表(申請）")) {

		} else {
			return;

		}


		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(File), StandardCharsets.UTF_8))) {
			String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				String[] values = line.split(",");

				if (values.length < 10) {
					if (line.startsWith("※")) {
					}
//					logger.info("values[" + ++i + "]：" + line);
					continue;

				}

				// 获取csv每列的值作为不同层级的key
				String firstKey = values[0];
				String secondKey = values[8];
				String thirdKey = values[2];
				String fourthKey = values[9];

				// 获取完整行内容，存储为String[]
				String[] rowData = values;

				// 构建数据结构，添加每一层的key和内容
				PropertyCsvMap_zhangpiao
						.computeIfAbsent(firstKey, k -> new TreeMap<>())
						.computeIfAbsent(secondKey, k -> new TreeMap<>())
						.put(thirdKey + "_" + fourthKey, rowData);

				TreeMap<String, String[]> myTreeMap = PropertyCsvMap_zhangpiao.get(firstKey).get(secondKey);
				if (!myTreeMap.containsKey("PS_0.0.0")) {

					LinkedHashMap<String, String[]> tempMap = new LinkedHashMap<>();
					// 先插入 "PS" 键到第一个位置
					tempMap.put("PS_0.0.0", new String[] { File.getPath() });
					//TODO
					tempMap.put("SOZ074_1.0", new String[] { "税務代理権限証書(令和6年4月1日以降提出分)" });
					// 将 myTreeMap 中的其他内容插入到 tempMap 中
					tempMap.putAll(myTreeMap);
					// 用 tempMap 更新 myTreeMap，以确保 "PS" 在第一个位置
					myTreeMap.clear();
					myTreeMap.putAll(tempMap);


				}

//            	PropertyCsvMap_shouxu
//            	└── firstColumn (String)
//            	    └── secondKey (String)
//        	            └── thirdKey (String)
//	                        └── versionKey (String) : rowData (String[])


			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	// 判断单元格是否为合并单元格
	public static boolean isMergedRegion(Sheet sheet, int row, int col) {
	    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
	        CellRangeAddress range = sheet.getMergedRegion(i);
	        if (range.isInRange(row, col)) {
	            return true;
	        }
	    }
	    return false;
	}

	// 获取合并单元格的值
	public static String getMergedCellValue(Sheet sheet, int row, int col) {
	    for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
	        CellRangeAddress range = sheet.getMergedRegion(i);
	        if (range.isInRange(row, col)) {
	            Row mergedRow = sheet.getRow(range.getFirstRow());
	            Cell mergedCell = mergedRow.getCell(range.getFirstColumn());
	            return FuncUtilsExcel.getCellValueAsString(mergedCell);
	        }
	    }
	    return "";
	}


	public static TreeMap<String, String> getPropertyCsvMap_zhangpiao_user() {
		   // 初始化 HashMap
        TreeMap<String, String> PropertyCsvMap_zhangpiao_user = new TreeMap<>();

	    String csvFile = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/PropertyCsvMap_zhangpiao_user.csv";
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {


                // 添加 propertyKey 和 propertyValues 到用户的 HashMap 中
                PropertyCsvMap_zhangpiao_user.put(line, "");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
		return PropertyCsvMap_zhangpiao_user;

	}

	public static void writePropertyCsvMap(String form_shouxu_type, String form_zhangpiao, String user_id) {
	    // 获取当前的 PropertyCsvMap
		TreeMap<String, String> PropertyCsvMap_zhangpiao_user = FuncUtilsAiEtax.getPropertyCsvMap_zhangpiao_user();
	    String csvFile = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/PropertyCsvMap_zhangpiao_user.csv";

	    // 将 form_zhangpiao 按逗号分隔解析为键值对
	    String[] formParts = form_zhangpiao.split(";");
	    if (formParts.length < 1) {
	        System.out.println("form_zhangpiao 格式不正确");
	        return;
	    }

	    TreeMap<String, String> newPropertyCsvMap_zhangpiao_user = new TreeMap<>(PropertyCsvMap_zhangpiao_user);;

	    // 删除已在 formParts 中出现的 key
        for (String part : formParts) {
            String[] keyValue = part.split(",");
            String key = keyValue[0].trim(); // 获取第一个字段值
            // 检查该 key 是否在 PropertyCsvMap_zhangpiao_user 中，如果有则删除
            // 遍历 HashMap 查找包含该 key 的项
            for (String mapKey : PropertyCsvMap_zhangpiao_user.keySet()) {
                if (mapKey.startsWith(form_shouxu_type + "," + key)) {
                    // 如果 mapKey 包含 key，则删除该条数据
                	newPropertyCsvMap_zhangpiao_user.remove(mapKey);
                }
            }

        }


        // 遍历每一部分并解析 key=value，存入 HashMap
        for (String part : formParts) {
        	newPropertyCsvMap_zhangpiao_user.put(form_shouxu_type + "," + part, "");
        }

        newPropertyCsvMap_zhangpiao_user.remove(form_shouxu_type + ",");
        newPropertyCsvMap_zhangpiao_user.remove("");

        // 清空文件并重新写入
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, false))) { // false 确保覆盖文件
            // 写入新的内容
            for (HashMap.Entry<String, String> entry : newPropertyCsvMap_zhangpiao_user.entrySet()) {
                // 写入 key,value 格式
                writer.write(entry.getKey());
                writer.newLine(); // 换行
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
	}





	public static String getXtx(HttpServletRequest req, String INSQ) throws Exception {

		ZipSecureFile.setMinInflateRatio(0);

		String path = FuncUtils.projectPath + "ETAX_moban";
		String path_Excel = path + "/e-Tax仕様書一覧全仕様書（PS）";
		String path_ETAX_output = FuncUtils.projectPath + "ETAX_output";

		t_jct_shenqingDao t_jct_shenqingDao = new t_jct_shenqingDao();
		t_jct_shenqingBean t_jct_shenqingBean = t_jct_shenqingDao.SelectKeyValue("INSQ", INSQ);

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(t_jct_shenqingBean.getYyyymmdd_count());

		String fileName = INSQ + "_" + t_etax_account_infoExBean.getCompanyName_English() + "_xtx";
    	path_ETAX_output = path_ETAX_output + "/" + fileName;


    	FuncUtils.deleteFile(path_ETAX_output + ".zip");
    	FuncUtils.deleteFolder(new File(path_ETAX_output));


        StringBuffer loggerStringBuffer = new StringBuffer();
        writeToFile(loggerStringBuffer.toString(), path_ETAX_output + ".log");


		// 将字符串转换为 XML Document 对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // 启用命名空间支持
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(Template)));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String shouxu_id = null;
        String  shouxu_banben = null;

    	LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

    	/*
		 * xtx作成
		 */
		xtxMap = getXtx_sheet(INSQ, path, path_Excel, path_ETAX_output, t_jct_shenqingBean, t_etax_account_infoExBean, xtxMap);

		/*
		 * ncc
		 */

		get_ncc(INSQ, path_ETAX_output, t_etax_account_infoExBean);



		/*
		 * IT
		 */
		LinkedHashMap<String, String> xtxMap_IT = new LinkedHashMap<String, String>();
		XPathExpression expression = xpath.compile("//*[@id='IT']");
		Element element = (Element) expression.evaluate(document, XPathConstants.NODE);

		if (element != null) {
			xtxMap_IT = getXtx_sheet_IT(INSQ, path, path_Excel, path_ETAX_output, t_jct_shenqingBean, t_etax_account_infoExBean, xtxMap);

			String[] shouxu_liset = xtxMap.get(shouxu_sheetName).split(",");
			shouxu_id = shouxu_liset[0];
			shouxu_banben = shouxu_liset[1];

			xtxMap.remove(shouxu_sheetName);
//            // 新建一个临时 LinkedHashMap 并添加新元素在最前面
//            LinkedHashMap<String, String> newXtxMap = new LinkedHashMap<>();
//            newXtxMap.put("PS_IT部", xtxMap_IT.get("PS_IT部")); // 新元素添加在最前
//            // 将原始元素添加到新 LinkedHashMap
//            newXtxMap.putAll(xtxMap);
//            // 用新的 map 覆盖旧的 map
//            xtxMap = newXtxMap;

            for (String key : xtxMap_IT.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap_IT.get(key));
//                element.appendChild(newChild.getChildNodes());
                // 循环 newChild 的所有子节点并添加到 element
                NodeList childNodes = newChild.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    // 采用 importNode 方法确保正确的父子结构
                    Node importedChild = document.importNode(childNode, true);
                    element.appendChild(importedChild);
                }
            }

		} else {
			logger.error("未找到 ID 为 'IT' 的元素。");
		}



        /*
         * CONTENTS
         */
        expression = xpath.compile("//*[@id='CONTENTS']");
        element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap.get(key));
                element.appendChild(newChild);
            }



        } else {
            logger.error("未找到 ID 为 'IT' 的元素。");
        }


        /*
         * CATALOG
         */

        // 使用 XPath 查找 <rdf:Seq> 元素
         xPathFactory = XPathFactory.newInstance();
         xpath = xPathFactory.newXPath();

        // 设置命名空间上下文以解析带有前缀的元素
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if ("rdf".equals(prefix)) {
                    return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                }
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }

            public Iterator<String> getPrefixes(String uri) {
                return null;
            }
        });

        // 查找 <rdf:Seq> 标签
		expression = xpath.compile("//rdf:Seq");
		element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            logger.debug("找到的元素：<rdf:Seq>");
//            logger.debug("内容：" + element.getTextContent());

            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
//    			+ "              <rdf:li>"
//    			+ "                <rdf:description about=\"#SHA010-1\" />"
//    			+ "              </rdf:li>"


                //TENPU
				if (key.contains("soz")) {

				} else {
					Element newChild = document.createElement("rdf:li");
					newChild.setTextContent("<rdf:description about=\"#" + key.toUpperCase().split("_")[0] + "-1\" />");
					element.appendChild(newChild);

				}
            }




        } else {
            logger.error("未找到 <rdf:Seq> 元素。");
        }



        File folder = new File(path_ETAX_output);
		String fileExtension = ".xtx"; // 需要删除的文件扩展名
		FuncUtils.deleteFilesInFolder(folder, fileExtension);





/*

<	&amp;lt;
"	&amp;quot;
>	&amp;gt;


 */

        /*
         * all.xtx
         */
        // 输出修改后的 XML 到文件
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String outputString = writer.toString();

        for (String key : xtxMap.keySet()) {
        	outputString = outputString.replace("<"+key+">", "");
        	outputString = outputString.replace("</"+key+">", "");
        }

    	outputString = outputString.replace("&lt;", "<");
    	outputString = outputString.replace("&gt;", ">");
    	outputString = outputString.replace("&#13;", "");

    	outputString = outputString.replace("PS_shouxu_id_PS", shouxu_id);
    	outputString = outputString.replace("PS_shouxu_banben_PS", shouxu_banben);

    	outputString = outputString.replace("xmlns=\"\"", "");

    	String key = "PS_ROOT";
    	outputString = outputString.replace("<"+key+">", "");
    	outputString = outputString.replace("</"+key+">", "");

        // 移除空行
        outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");


        // 输出格式化后的 XML 字符串到后台
//        logger.info("XML 格式化输出：");
//        logger.info("\n" + outputString);


        String outputPath = path_ETAX_output + "/" + INSQ + "_all.xtx"; // 输出文件路径
        Files.write(Paths.get(outputPath), outputString.getBytes());
        logger.debug("字符串已成功输出到文件：" + outputPath);


       	return path_ETAX_output;

	}


	public static String getXtx_AI(HttpServletRequest req, String INSQ, TableDefinition tableDefinition
			, String m_ai_guanli_yewubiao, t_etax_account_infoExBean t_etax_account_infoExBean) throws Exception {

		ZipSecureFile.setMinInflateRatio(0);

		String path = FuncUtils.projectPath + "ETAX_moban";
		String path_Excel = path + "/e-Tax仕様書一覧全仕様書（PS）";
		String path_ETAX_output = FuncUtils.projectPath + "ETAX_output";


		List<ColumnDefinition> columns = tableDefinition.columns;

		/*
		0		UPDATE_DATE timestamp(6)
		1		yyyymmdd_count bigint
		2		user_id varchar(45)
		3		activation_code varchar(45)
		4		status varchar(45)
		5		file_name varchar(45)
		6		col_name_0 int
		7		col_name_1 int
		・・・・・・
		*/

		String shouxu_name = tableDefinition.columns_chk_etax.get(0).split("/")[1];
		String fileName = t_etax_account_infoExBean.getCompanyName_English() + "_" + tableDefinition.tableName_comment + "_" + shouxu_name;
    	path_ETAX_output = path_ETAX_output + "/" + fileName;


    	FuncUtils.deleteFile(path_ETAX_output + ".zip");
    	FuncUtils.deleteFolder(new File(path_ETAX_output));


        StringBuffer loggerStringBuffer = new StringBuffer();
        writeToFile(loggerStringBuffer.toString(), path_ETAX_output + ".log");


		// 将字符串转换为 XML Document 对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // 启用命名空间支持
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(Template)));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();


        String shouxu_id = null;
        String  shouxu_banben = null;

    	LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

    	/*
		 * xtx作成
		 */
		xtxMap = getXtx_sheet_AI(INSQ, path, path_Excel, path_ETAX_output, tableDefinition, t_etax_account_infoExBean, xtxMap);

		/*
		 * ncc
		 */

		get_ncc(INSQ, path_ETAX_output, t_etax_account_infoExBean);



		/*
		 * IT
		 */
		LinkedHashMap<String, String> xtxMap_IT = new LinkedHashMap<String, String>();
		XPathExpression expression = xpath.compile("//*[@id='IT']");
		Element element = (Element) expression.evaluate(document, XPathConstants.NODE);

		if (element != null) {
			xtxMap_IT = getXtx_sheet_IT_AI(INSQ, path, path_Excel, path_ETAX_output, tableDefinition, t_etax_account_infoExBean, xtxMap);

			String[] shouxu_liset = xtxMap.get(shouxu_sheetName).split(",");
			shouxu_id = shouxu_liset[0];
			shouxu_banben = shouxu_liset[1];

			xtxMap.remove(shouxu_sheetName);
//            // 新建一个临时 LinkedHashMap 并添加新元素在最前面
//            LinkedHashMap<String, String> newXtxMap = new LinkedHashMap<>();
//            newXtxMap.put("PS_IT部", xtxMap_IT.get("PS_IT部")); // 新元素添加在最前
//            // 将原始元素添加到新 LinkedHashMap
//            newXtxMap.putAll(xtxMap);
//            // 用新的 map 覆盖旧的 map
//            xtxMap = newXtxMap;

            for (String key : xtxMap_IT.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap_IT.get(key));
//                element.appendChild(newChild.getChildNodes());
                // 循环 newChild 的所有子节点并添加到 element
                NodeList childNodes = newChild.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    // 采用 importNode 方法确保正确的父子结构
                    Node importedChild = document.importNode(childNode, true);
                    element.appendChild(importedChild);
                }
            }

		} else {
			logger.error("未找到 ID 为 'IT' 的元素。");
		}



        /*
         * CONTENTS
         */
        expression = xpath.compile("//*[@id='CONTENTS']");
        element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
                Element newChild = document.createElement(key);
                newChild.setTextContent(xtxMap.get(key));
                element.appendChild(newChild);
            }



        } else {
            logger.error("未找到 ID 为 'IT' 的元素。");
        }


        /*
         * CATALOG
         */

        // 使用 XPath 查找 <rdf:Seq> 元素
         xPathFactory = XPathFactory.newInstance();
         xpath = xPathFactory.newXPath();

        // 设置命名空间上下文以解析带有前缀的元素
        xpath.setNamespaceContext(new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                if ("rdf".equals(prefix)) {
                    return "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
                }
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }

            public Iterator<String> getPrefixes(String uri) {
                return null;
            }
        });

        // 查找 <rdf:Seq> 标签
		expression = xpath.compile("//rdf:Seq");
		element = (Element) expression.evaluate(document, XPathConstants.NODE);

        if (element != null) {
            logger.debug("找到的元素：<rdf:Seq>");
//            logger.debug("内容：" + element.getTextContent());

            // 遍历 xtxMap 并将每个键值对作为子元素添加到 IT 元素上
            for (String key : xtxMap.keySet()) {
//    			+ "              <rdf:li>"
//    			+ "                <rdf:description about=\"#SHA010-1\" />"
//    			+ "              </rdf:li>"


                //TENPU
				if (key.contains("soz")) {

				} else {
					Element newChild = document.createElement("rdf:li");
					newChild.setTextContent("<rdf:description about=\"#" + key.toUpperCase().split("_")[0] + "-1\" />");
					element.appendChild(newChild);

				}
            }




        } else {
            logger.error("未找到 <rdf:Seq> 元素。");
        }



        File folder = new File(path_ETAX_output);
		String fileExtension = ".xtx"; // 需要删除的文件扩展名
		FuncUtils.deleteFilesInFolder(folder, fileExtension);





/*

<	&amp;lt;
"	&amp;quot;
>	&amp;gt;


 */

        /*
         * all.xtx
         */
        // 输出修改后的 XML 到文件
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        String outputString = writer.toString();

        for (String key : xtxMap.keySet()) {
        	outputString = outputString.replace("<"+key+">", "");
        	outputString = outputString.replace("</"+key+">", "");
        }

    	outputString = outputString.replace("&lt;", "<");
    	outputString = outputString.replace("&gt;", ">");
    	outputString = outputString.replace("&#13;", "");

    	outputString = outputString.replace("PS_shouxu_id_PS", shouxu_id);
    	outputString = outputString.replace("PS_shouxu_banben_PS", shouxu_banben);

    	outputString = outputString.replace("xmlns=\"\"", "");

    	String key = "PS_ROOT";
    	outputString = outputString.replace("<"+key+">", "");
    	outputString = outputString.replace("</"+key+">", "");

        // 移除空行
        outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");


        // 输出格式化后的 XML 字符串到后台
//        logger.info("XML 格式化输出：");
//        logger.info("\n" + outputString);


        String outputPath = path_ETAX_output + "/" + INSQ + "_all.xtx"; // 输出文件路径
        Files.write(Paths.get(outputPath), outputString.getBytes());
        logger.debug("字符串已成功输出到文件：" + outputPath);


       	return path_ETAX_output;

	}





	 /**
    * 将指定内容写入文件。
    *
    * @param content 文件内容
    * @param filePath 文件路径
    * @throws IOException 如果写入文件时发生错误
    */
   public static void writeToFile(String content, String filePath) throws IOException {
       File file = new File(filePath);

       // 如果文件不存在，创建新文件
       if (!file.exists()) {
           file.getParentFile().mkdirs(); // 创建父目录
           file.createNewFile();           // 创建新文件
       }

       // 使用 BufferedWriter 将内容写入文件
       try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
           writer.write(content); // 写入内容
       }
   }


	private static String shouxu_sheetName = "手続内帳票対応表";

	static String Template = ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ ""
			+ "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
			+ "<DATA id=\"DATA\" xmlns=\"http://xml.e-tax.nta.go.jp/XSD/hojin\" xmlns:gen=\"http://xml.e-tax.nta.go.jp/XSD/general\" xmlns:kyo=\"http://xml.e-tax.nta.go.jp/XSD/kyotsu\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
			+ "  <PS_shouxu_id_PS VR=\"PS_shouxu_banben_PS\" id=\"PS_shouxu_id_PS\">"
			+ "    <CATALOG id=\"CATALOG\">"
			+ "      <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">"
			+ "        <rdf:description id=\"REPORT\">"
			+ "          <SEND_DATA />"
			+ "          <IT_SEC>"
			+ "            <rdf:description about=\"#IT\" />"
			+ "          </IT_SEC>"
			+ "          <FORM_SEC>"
			+ "            <rdf:Seq>"
//			+ "              <rdf:li>"
//			+ "                <rdf:description about=\"#SHA010-1\" />"
//			+ "              </rdf:li>"
			+ "            </rdf:Seq>"
			+ "          </FORM_SEC>"
			+ ""
			+ ""
			+ "          <TENPU_SEC>"
			+ "            <rdf:Seq>"
			+ "              <rdf:li>"
			+ "                <rdf:description about=\"#TENPU\" />"
			+ "              </rdf:li>"
			+ "            </rdf:Seq>"
			+ "          </TENPU_SEC>"
			+ ""
			+ "          <XBRL_SEC />"
			+ "          <XBRL2_1_SEC />"
			+ "          <SOFUSHO_SEC />"
			+ "          <ATTACH_SEC />"
			+ "          <CSV_SEC />"
			+ "        </rdf:description>"
			+ "      </rdf:RDF>"
			+ "    </CATALOG>"
			+ "    <CONTENTS id=\"CONTENTS\">"
			+ "      <IT VR=\"1.5\" id=\"IT\">"
			+ ""
			+ ""
//			+ "        <ZEIMUSHO ID=\"ZEIMUSHO\">"
//			+ "          <gen:zeimusho_CD>01115</gen:zeimusho_CD>"
//			+ "          <gen:zeimusho_NM>小石川</gen:zeimusho_NM>"
//			+ "        </ZEIMUSHO>"
//			+ "        <NOZEISHA_ID ID=\"NOZEISHA_ID\">2043062810920066</NOZEISHA_ID>"
//			+ "        <NOZEISHA_BANGO ID=\"NOZEISHA_BANGO\">"
//			+ "          <gen:hojinbango>9700150121748</gen:hojinbango>"
//			+ "        </NOZEISHA_BANGO>"
//			+ "        <NOZEISHA_NM_KN ID=\"NOZEISHA_NM_KN\">チェジエン　ジンジュエ　ディエンツ　シャンウ　カンパニーリミテッド</NOZEISHA_NM_KN>"
//			+ "        <NOZEISHA_NM ID=\"NOZEISHA_NM\">Ｚｈｅ　Ｊｉａｎｇ　Ｊｉｎｇ　Ｊｕｅ　Ｄｉａｎ　Ｚｉ　Ｓｈａｎｇ　Ｗｕ　Ｃｏ．，Ｌｔｄ</NOZEISHA_NM>"
//			+ "        <NOZEISHA_ZIP ID=\"NOZEISHA_ZIP\">"
//			+ "          <gen:zip1>112</gen:zip1>"
//			+ "          <gen:zip2>0011</gen:zip2>"
//			+ "        </NOZEISHA_ZIP>"
//			+ "        <NOZEISHA_ADR_KN ID=\"NOZEISHA_ADR_KN\">トウキョウトブンキョウクセンゴク</NOZEISHA_ADR_KN>"
//			+ "        <NOZEISHA_ADR ID=\"NOZEISHA_ADR\">東京都文京区千石４丁目１４番９号１階</NOZEISHA_ADR>"
//			+ "        <NOZEISHA_TEL ID=\"NOZEISHA_TEL\">"
//			+ "          <gen:tel1>03</gen:tel1>"
//			+ "          <gen:tel2>5981</gen:tel2>"
//			+ "          <gen:tel3>8383</gen:tel3>"
//			+ "        </NOZEISHA_TEL>"
//			+ "        <DAIHYO_NM_KN ID=\"DAIHYO_NM_KN\">ツォン フアン ボー</DAIHYO_NM_KN>"
//			+ "        <DAIHYO_NM ID=\"DAIHYO_NM\">ｃｅｎｇ　ｈｕａｎｇｂｏ</DAIHYO_NM>"
//			+ "        <DAIHYO_ZIP ID=\"DAIHYO_ZIP\">"
//			+ "          <gen:zip1>000</gen:zip1>"
//			+ "          <gen:zip2>0000</gen:zip2>"
//			+ "        </DAIHYO_ZIP>"
//			+ "        <DAIHYO_ADR ID=\"DAIHYO_ADR\">国外</DAIHYO_ADR>"
//			+ "        <DAIRI_ID ID=\"DAIRI_ID\">2674011631920063</DAIRI_ID>"
//			+ "        <DAIRI_NM_KN ID=\"DAIRI_NM_KN\">アイタックス</DAIRI_NM_KN>"
//			+ "        <DAIRI_NM ID=\"DAIRI_NM\">ｉＴＡＸ税理士法人</DAIRI_NM>"
//			+ "        <DAIRI_ZIP ID=\"DAIRI_ZIP\">"
//			+ "          <gen:zip1>101</gen:zip1>"
//			+ "          <gen:zip2>0064</gen:zip2>"
//			+ "        </DAIRI_ZIP>"
//			+ "        <DAIRI_ADR ID=\"DAIRI_ADR\">東京都千代田区神田猿楽町２－７－１７織本ビル５階</DAIRI_ADR>"
//			+ "        <DAIRI_TEL ID=\"DAIRI_TEL\">"
//			+ "          <gen:tel1>03</gen:tel1>"
//			+ "          <gen:tel2>6272</gen:tel2>"
//			+ "          <gen:tel3>8525</gen:tel3>"
//			+ "        </DAIRI_TEL>"
//			+ "        <TETSUZUKI ID=\"TETSUZUKI\">"
//			+ "          <procedure_CD>RSH0020</procedure_CD>"
//			+ "          <procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
//			+ "        </TETSUZUKI>"
//			+ "        <KAZEI_KIKAN_FROM ID=\"KAZEI_KIKAN_FROM\">"
//			+ "          <gen:era>5</gen:era>"
//			+ "          <gen:yy>5</gen:yy>"
//			+ "          <gen:mm>1</gen:mm>"
//			+ "          <gen:dd>1</gen:dd>"
//			+ "        </KAZEI_KIKAN_FROM>"
//			+ "        <KAZEI_KIKAN_TO ID=\"KAZEI_KIKAN_TO\">"
//			+ "          <gen:era>5</gen:era>"
//			+ "          <gen:yy>5</gen:yy>"
//			+ "          <gen:mm>12</gen:mm>"
//			+ "          <gen:dd>31</gen:dd>"
//			+ "        </KAZEI_KIKAN_TO>"
//			+ "        <SHINKOKU_KBN ID=\"SHINKOKU_KBN\">"
//			+ "          <kubun_CD>1</kubun_CD>"
//			+ "        </SHINKOKU_KBN>"
			+ ""

			+ "      </IT>"

//			+ "      <SHA010 VR=\"10.0\" id=\"SHA010-1\" page=\"1\" sakuseiDay=\"2024-03-25\" sakuseiNM=\"Ｓｈｅｎｚｈｅｎ　ＬｅｉＭｉｎｇＹｕＧｕａｎｇＤｉａｎ　Ｃｏ．，Ｌｔｄ\" softNM=\"ntaclient\">"
//			+ ""
//			+ "      </SHA010-1>"
			+ ""
			+ ""
			+ "    </CONTENTS>"
			+ "  </PS_shouxu_id_PS>"
			+ "</DATA>"
			+ ""
			+ ""
			+ "";


	private static LinkedHashMap<String, String> getXtx_sheet(String PDSK, String path, String path_Excel, String path_ETAX_output
			, t_jct_shenqingBean t_jct_shenqingBean, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

        try {

    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";
//
//        		}

        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";

    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";
//
//        		}

        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";
    		}

        	xtxMap = findSheetsStartingWithPS(t_etax_account_infoExBean, t_jct_shenqingBean, t_jct_shenqingDao.CREATE_TABL
        			, path, path_Excel, path_ETAX_output + "/" + PDSK);

        } catch (Exception e) {
//            e.printStackTrace();
            throw e;
        }

		return xtxMap;
	}

	private static LinkedHashMap<String, String> getXtx_sheet_AI(String PDSK, String path, String path_Excel, String path_ETAX_output
			, TableDefinition tableDefinition, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

        try {




    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";
//
//        		}

//        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";

        		path_Excel = path_Excel + tableDefinition.columns_chk_etax.get(0);




    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";
//
//        		}

        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";
    		}

        	xtxMap = findSheetsStartingWithPS(t_etax_account_infoExBean, tableDefinition, t_jct_shenqingDao.CREATE_TABL
        			, path, path_Excel, path_ETAX_output + "/" + PDSK);

        } catch (Exception e) {
//            e.printStackTrace();
            throw e;
        }

		return xtxMap;
	}


	private static void get_ncc(String PDSK, String path_ETAX_output, t_etax_account_infoExBean t_etax_account_infoExBean) throws IOException {
		String pathNCC0 = FuncUtils.projectPath + "ETAX_moban/e-Tax仕様書一覧全仕様書（PS）/ps_ncc.ncc";
		File dataModelFileNCC0 = new File(pathNCC0);
		String fileContent0 = FuncUtils.readFileContent(dataModelFileNCC0);
		String pathNewNCC = path_ETAX_output + "/" + PDSK + "_" + t_etax_account_infoExBean.getCompanyName_English() + ".ncc";

		// 将 A 列和 B 列的数据存储到 excelData HashMap
		String ncc_key = "";
		String ncc_value = "";



//		#利用者識別番号#
		ncc_key = "#利用者識別番号#";
		ncc_value = t_etax_account_infoExBean.getBangou();
		if (StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
			ncc_value = "";
		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);


//			#法人番号#
		ncc_key = "#法人番号#";
		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			ncc_value = "";
		} else {
			ncc_value = t_etax_account_infoExBean.getHoujinBangou();
			if (StringUtils.isEmpty(t_etax_account_infoExBean.getHoujinBangou())) {
				ncc_value = "";
			}

		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#事業者名#
		ncc_key = "#事業者名#";
		ncc_value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25);
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#事業者名カナ#
		ncc_key = "#事業者名カナ#";
		ncc_value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_pianjiaming(), 25);
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#代表人氏名#
		ncc_key = "#代表人氏名#";
		ncc_value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getDaibiaoName_English());
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#代表人氏名カナ#
		ncc_key = "#代表人氏名カナ#";
		ncc_value = t_etax_account_infoExBean.getDaibiaoName_pianjiaming();
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//		#個人法人フラグ#	個人0	法人1
		ncc_key = "#個人法人フラグ#";
		ncc_value = "1";
		if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
			ncc_value = "0";

		}
		fileContent0 = fileContent0.replace(ncc_key, ncc_value);



		// 写入文件
		FileWriter writer = new FileWriter(pathNewNCC);
		writer.write(fileContent0);
		writer.close();
		logger.debug("File saved: " + pathNewNCC);
	}


	private static LinkedHashMap<String, String> getXtx_sheet_IT(String PDSK, String path, String path_Excel, String path_ETAX_output, t_jct_shenqingBean t_jct_shenqingBean, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

//		path_Excel = path_Excel + "/4 IT部仕様書/IT部PS仕様書.xlsx";
        try {
    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";
//
//        		}



        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";

    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";
//
//        		}

        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";
    		}

        	xtxMap = findSheetsStartingWithPS_IT(t_etax_account_infoExBean, t_jct_shenqingBean, path, path_Excel, path_ETAX_output + "/" + PDSK, xtxMap);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }


		return xtxMap;
	}

	private static LinkedHashMap<String, String> getXtx_sheet_IT_AI(String PDSK, String path, String path_Excel, String path_ETAX_output, TableDefinition tableDefinition, t_etax_account_infoExBean t_etax_account_infoExBean,
			LinkedHashMap<String, String> xtxMap) throws Exception {

//		path_Excel = path_Excel + "/4 IT部仕様書/IT部PS仕様書.xlsx";
        try {
    		if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/１法人原則一般/XML情報　法人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/３法人簡易/XML情報　法人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/２法人原則二割/XML情報　法人原則二割.xlsx";
//
//        		}



        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";

    		} else if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
//        		if ("原则课税".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/5 個人原則一般/XML情報　個人原則一般.xlsx";
//
//        		} else if ("简易课税（零售业）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/7 個人簡易/XML情報　個人簡易.xlsx";
//
//        		} else if ("原则课税（2割特例）".equals(t_xiaofeishui_shengaoBean.getKeshui_type())) {
//        			path_Excel = path_Excel + "/6 個人原則二割/XML情報　個人原則二割.xlsx";
//
//        		}

        		path_Excel = path_Excel + "/PHO3110/PHO3110_XML情報　手続内帳票対応表(申請）.xlsx";
    		}

        	xtxMap = findSheetsStartingWithPS_IT(t_etax_account_infoExBean, tableDefinition, path, path_Excel, path_ETAX_output + "/" + PDSK, xtxMap);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }


		return xtxMap;
	}

	private static LinkedHashMap<String, String> findSheetsStartingWithPS(t_etax_account_infoExBean t_etax_account_infoExBean
			, Object objBean, String CREATE_TABL, String filePath, String path_Excel, String path_ETAX_output) throws Exception {
    	logger.debug("\n" + "path_Excel: " + path_Excel);

        StringBuffer loggerStringBuffer = new StringBuffer();

		loggerStringBuffer.append(CREATE_TABL);
		loggerStringBuffer.append("\n");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        String json = objectMapper.writeValueAsString(t_etax_account_infoExBean);
		loggerStringBuffer.append("\n");
        loggerStringBuffer.append("t_etax_account_infoExBean");
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append(json);
        loggerStringBuffer.append("\n");

        json = objectMapper.writeValueAsString(objBean);
		loggerStringBuffer.append("\n");
        loggerStringBuffer.append("t_jct_shenqingBean");
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append(json);
        loggerStringBuffer.append("\n");

        json = objectMapper.writeValueAsString(tblMap);
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append("tblMap");
        loggerStringBuffer.append("\n");
        loggerStringBuffer.append(json);
        loggerStringBuffer.append("\n");

        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = formatter.format(now);
        loggerStringBuffer.append(formattedDate);
        loggerStringBuffer.append("\n");

        logger.debug(loggerStringBuffer.toString());


		LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

		FileInputStream fis = null;
        Workbook workbook = null;
        XMLCalculator XMLCalculator = new XMLCalculator();
        try {
            // 打开 Excel 文件
            fis = new FileInputStream(path_Excel);
            workbook = WorkbookFactory.create(fis);

            String shouxu_id = "";
            String shouxu_banben = "";
            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "手続内帳票対応表（申告）" 开头
                if (sheetName.startsWith(shouxu_sheetName)) {

                    // 先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 1; j++) {
                        // 从第四行开始输出 A 列和 I 列的值
                        for (int rowIndex = 4; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                            Row row = sheet.getRow(rowIndex);

                            // 如果该行为空，跳过
                            if (row == null) {
                                continue;
                            }

                            // 跳过隐藏的行
                            if (row.getZeroHeight()) {
                                continue;
                            }



                        	Cell aCell = row.getCell(0);  // A 列（索引 0）	//手続きＩＤ
//                        	Cell bCell = row.getCell(1);  // B 列（索引 1）	//
//                        	Cell cCell = row.getCell(2);  // C 列（索引 2）	//
//                        	Cell dCell = row.getCell(3);  // D 列（索引 3）	//
//                        	Cell eCell = row.getCell(4);  // E 列（索引 4）	//
//                        	Cell fCell = row.getCell(5);  // F 列（索引 5）	//
//                        	Cell gCell = row.getCell(6);  // G 列（索引 6）	//
//                        	Cell hCell = row.getCell(7);  // H 列（索引 7）	//
                        	Cell iCell = row.getCell(8);  // I 列（索引 8）	//バージョン
//                        	Cell jCell = row.getCell(9);  // J 列（索引 9）	//
//                        	Cell kCell = row.getCell(10); // K 列（索引 10）	//
//                        	Cell lCell = row.getCell(11); // L 列（索引 11）	//
//                        	Cell mCell = row.getCell(12); // M 列（索引 12）	//
//                        	Cell nCell = row.getCell(13); // N 列（索引 13）	//
//                        	Cell oCell = row.getCell(14); // O 列（索引 14）	//
//                        	Cell pCell = row.getCell(15); // P 列（索引 15）	//

//                        	Cell qCell = row.getCell(16); // Q 列（索引 16）
//                        	Cell rCell = row.getCell(17); // R 列（索引 17）
//                        	Cell sCell = row.getCell(18); // S 列（索引 18）
//                        	Cell tCell = row.getCell(19); // S 列（索引 19）


                        	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//手続きＩＤ
//                        	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//
//                        	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//
//                        	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//
//                        	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//
//                        	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//
//                        	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//
//                        	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//
                        	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//バージョン
//                        	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//
//                        	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//
//                        	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//
//                        	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//
//                        	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//
//                        	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//
//                        	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//



//                        	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
//                        	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//
//                        	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//
//                        	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//
                            // 输出或处理 A 列和 I 列的值
                            System.out.println("A列值: " + aValue + ", I列值: " + iValue);

                            shouxu_id = aValue;
                            shouxu_banben = iValue;
                            xtxMap.put(shouxu_sheetName, aValue + "," + iValue);

                            // 找到第一个非隐藏行后，跳出循环
                            break;
                        }
                    }
                    break; // 找到符合条件的工作表后，退出外层循环
                }
            }



            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "PS_" 开头
                if (sheetName.startsWith("PS_")) {
                	logger.debug("找到符合条件的工作表: " + sheetName);

                	if (sheetName.contains("PS_IT部")) {
                		continue;
                	}

                	String xtx_fileName = sheetName.toLowerCase().replace("ps_", "").replace(".0", "x");
                	xtx_fileName = xtx_fileName.substring(0, 6) + "_ver" + xtx_fileName.substring(7) + ".xml";

                    File file = findFileByNameRecursive(filePath, xtx_fileName);
                    // 读取整个文件的内容为字符串
                    String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), StandardCharsets.UTF_8);


                    //先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 3; j++) {
                    	// 从第四行开始输出 M 列和 S 列的值
                    	for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {

                            	//項番
                                if (StringUtils.isEmpty(FuncUtilsExcel.getCellValueAsString(row.getCell(0))) == true) {
                                	break;
                                }

//                            	Cell aCell = row.getCell(0);  // A 列（索引 0）	//項番
                            	Cell bCell = row.getCell(1);  // B 列（索引 1）	//入力型
//                            	Cell cCell = row.getCell(2);  // C 列（索引 2）	//帳票項番
                            	Cell dCell = row.getCell(3);  // D 列（索引 3）	//項目（ｸﾞﾙｰﾌﾟ）名
                            	Cell eCell = row.getCell(4);  // E 列（索引 4）	//項目名
//                            	Cell fCell = row.getCell(5);  // F 列（索引 5）	//繰返し回数
                            	Cell gCell = row.getCell(6);  // G 列（索引 6）	//書式
//                            	Cell hCell = row.getCell(7);  // H 列（索引 7）	//入力ﾁｪｯｸ
//                            	Cell iCell = row.getCell(8);  // I 列（索引 8）	//計算
                            	Cell jCell = row.getCell(9);  // J 列（索引 9）	//値の範囲
//                            	Cell kCell = row.getCell(10); // K 列（索引 10）	//計算No
                            	Cell lCell = row.getCell(11); // L 列（索引 11）	//計算／備考
                            	Cell mCell = row.getCell(12); // M 列（索引 12）	//ＸＭＬタグ
//                            	Cell nCell = row.getCell(13); // N 列（索引 13）	//順位
                            	Cell oCell = row.getCell(14); // O 列（索引 14）	//ID属性
                            	Cell pCell = row.getCell(15); // P 列（索引 15）	//IDREF属性

                            	Cell qCell = row.getCell(16); // Q 列（索引 16）
                            	Cell rCell = row.getCell(17); // R 列（索引 17）
                            	Cell sCell = row.getCell(18); // S 列（索引 18）
                            	Cell tCell = row.getCell(19); // S 列（索引 19）


//                            	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//項番
                            	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//入力型
//                            	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//帳票項番
                            	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//項目（ｸﾞﾙｰﾌﾟ）名
                            	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//項目名
//                            	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//繰返し回数
                            	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//書式
//                            	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//入力ﾁｪｯｸ
//                            	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//計算
                            	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//値の範囲
//                            	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//計算No
                            	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//計算／備考
                            	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//ＸＭＬタグ
//                            	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//順位
                            	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//ID属性
                            	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//IDREF属性


                            	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
                            	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//区分
                            	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//对象元素/値
                            	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//运算关系

                                if ("DEA00190".equals(mValue)) {
                                	mValue=mValue;
                                }


                                if (j == 1) {


                                    if ("DEA00240".equals(mValue)) {
                                    	mValue=mValue;
                                    }

                                    //PS区分
                                    if (rValue.equals("删除")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                		String regex = "<" + mValue + "(.*?)>(.*?)</" + mValue + ">";
                                		Pattern pattern = Pattern.compile(regex);
                                		Matcher matcher = pattern.matcher(content);
                                		content = matcher.replaceAll("");

                                        continue;
                                    }
                                 	//IDREF属性
                                    if (StringUtils.isEmpty(oValue) == true) {

                                    } else {

										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                		String regex = "<" + mValue + "(.*?)>(.*?)</" + mValue + ">";
                                		Pattern pattern = Pattern.compile(regex);
                                		Matcher matcher = pattern.matcher(content);

//                                        if ("DEA00190".equals(mValue)) {
//                                        	mValue=mValue;
//                                        }
//
//                                        if (matcher.find()) {
//                                            String value = matcher.group(1);
//                                            System.out.println("标签内的值是: " + value);
//                                        } else {
//                                            System.out.println("没有找到标签值");
//                                        }


                                		regex = "<" + mValue + " ID=\""+ oValue +"\"></" + mValue + ">";
                                		content = matcher.replaceAll(regex);

                                    }

                                    if (qValue.contains("個人の場合のIDREF属性:")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                		String regex = "<" + mValue + "(.*?)>(.*?)</" + mValue + ">";
                                		Pattern pattern = Pattern.compile(regex);
                                		Matcher matcher = pattern.matcher(content);
                                		regex = "<" + mValue + " IDREF=\""+qValue.replace("個人の場合のIDREF属性:", "")+"\"></" + mValue + ">";
                                		content = matcher.replaceAll(regex);

                                        continue;
                                    }

                                    if (qValue.contains("個人の場合のIDREF属性[設定しない]")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	// 正则：只替换 标签中的 IDREF 属性
//                                    	content = content.replaceAll(
//                                                "(<"+mValue+"\\s+)([^>]*?)\\s*IDREF=\"[^\"]*\"\\s*([^>]*?>)",
//                                                "$1$2$3"
//                                            );

                                    	//正则：删除整个 标签
                                    	content = content.replaceAll(
                                    		    "<"+mValue+"[^>]*>(.*?)</"+mValue+">|<"+mValue+"[^>]*/>",
                                    		    ""
                                    		);

                                        continue;
                                    }

                                }

                            	//IDREF属性
                                if (StringUtils.isEmpty(pValue) == true) {

                                } else {
                                	continue;
                                }


                				String sValueOld = "";
                				String sValueNew = "";
                                if (j == 1) {





                                    if ("DEA00050".equals(mValue)) {
                                    	mValue=mValue;
                                    }


                                    if ("【公司或本人地址（日语片假名）】".equals(sValue)) {
                                    	mValue=mValue;
//                                    	return xtxMap;
                                    }

                                    //PS区分
                                    if (rValue.equals("固定")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                        if ("区分".equals(bValue)) {

                                        	if ("元号".equals(eValue)) {
                                        		continue;
                                        	}


                                        	//TODO
//                                        	if ("振替継続希望区分".equals(dValue)) {
//                                        		sValue = "<kubun_CD>"+sValue+"</kubun_CD>";
//                                            }


//                                            if (StringUtils.isEmpty(sValue) == false) {
//                                        		sValue = "<kubun_CD>"+sValue+"</kubun_CD>";
//                                            }

//
//                                        	if (jValue.contains("4:平成") || jValue.contains("5:令和")) {
//                                        		//削除
//                                        		// 使用正则表达式匹配指定标签内的值
//                                        		String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
//                                        		Pattern pattern = Pattern.compile(regex);
//                                        		Matcher matcher = pattern.matcher(content);
//                                        		// 进行替换，将匹配到的值替换为 newValue
//                                        		content = matcher.replaceAll("");
//
//
//                                        		//書換
////                                        		sValue = " <gen:era>5</gen:era>"
////                                        				+ " <gen:yy>5</gen:yy>"
////                                        				+ " <gen:mm>12</gen:mm>"
////                                        				+ " <gen:dd>31</gen:dd>";
////                            					content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");
//
//                                        	} else {
//
//                                        	}

//                                        	sValue = "<kubun_CD>"+sValue+"</kubun_CD>";

                                        }

                                    } else if (rValue.contains("DB")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                        				sValueOld = sValue;
                            			if (sValue.startsWith("【") && sValue.endsWith("】")) {
                            				sValue = sValue.replace("【", "").replace("】", "");
                            				sValueNew = sValue;
                                            sValue = tblMap.get(sValue);

                                            if (StringUtils.isEmpty(sValue) == true) {
                                            	sValue = findFuzzyMatchkey(tblMap, sValueNew); // 模糊查找
                                            	sValueNew = sValue;
                                            	sValue = tblMap.get(sValue);
                                    		}

                                    		if (StringUtils.isEmpty(sValue) == true) {
                                                logger.error("没有找到DB定义项目:" + sValue);
                                            	continue;

                                    		} else {
                                    			if (sValueNew.contains("t_etax_account_info") || sValueNew.contains("t_etax_account_res")) {
                                    				sValue = FuncUtils.getBeanValue(t_etax_account_infoExBean, sValue);
                                    		        // 替换非法字符
                                    				sValue = sValue.replaceAll("<(\\d+[-]\\d+)>", "&lt;$1&gt;");
                                    			} else {
                                    				sValue = FuncUtils.getBeanValue(objBean, sValue);

                                    			}

                                			}


                                		} else {
                                			logger.error("没有找到DB定义项目【】格式写法:" + sValue);
                                        	continue;

                                		}



                                    }
                                } else if (j == 2) {


                                    if ("DED00020".equals(mValue)) {
                                    	mValue=mValue;
//                                    	return xtxMap;
                                    }

                                    if ("【公司成立年月日或本人出生年月日】(日本纪年_年号)".equals(tValue)) {
                                    	mValue=mValue;
//                                    	return xtxMap;
                                    }

                                    //PS区分
                                    if (rValue.contains("【計算】")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: \n" + tValue + "\n][L列值: " + lValue.replace("\n", "") + "]";
										logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	//PS运算关系
                                    	String formula = tValue;
                                    	if (StringUtils.isEmpty(formula) == true) {
                                    		//ETAX式样計算
                                    		//計算／備考
                                    		formula = lValue;
                                    	}

                                    	formula = formula.replace("【計算】", "");
                                    	//百分数表示，不是计算公式，不需要计算，删除
                                    	formula = formula.replace("％", "");
        								formula = FuncUtils.toHalfWidth(formula);


                                    	if (StringUtils.isEmpty(formula) == true) {
                                			logger.error("没有計算式:" + formula);
//                                        	continue;
                                    	} else {
                                    		//自动识别，自定义计算公式
                                            for (Entry<String, String> entry : XMLCalculator.roundingRules.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();

                                                if (formula.contains(key)) {
                                                    formula = formula.replace(key, "");
                                                    formula = formula + "\n" + value.replace("【】", mValue);
                                                }
                                            }

            								formula = formula.replace("・", "");
            								formula = formula.replace("()", "");
//            								if (formula.contains("(") || formula.contains(")")) {
//            									logger.error("有未定义的运算逻辑项目:" + formula);
//            									continue;
//            								}

            								//逻辑计算的，去掉换行符
            								if (formula.toLowerCase().contains("if")) {// && formula.toLowerCase().contains("else")
            									formula = formula.replace("\n", " ");
            								}

            								formula = evaluateLogicFromTemplate(formula, t_etax_account_infoExBean, objBean);

            						        String[] formulaLines = formula.split("\n"); // 按行分割公式字符串

            						        for (String line : formulaLines) {
            						            line = line.trim(); // 去除行首尾的空白
            						            if (!line.isEmpty()) {
            						            	if (line.indexOf("角") > -1) {
            						            		line = line.replace("【】", "【" + mValue + "】");
            						            		String result = (String) XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer);
            						            		sValue = "" + result;

            						            	} else if (StringUtils.isEmpty(gValue) == true) {
            						            		//逻辑计算的，去掉换行符
            						            		String result = XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer).toString();
            						            		sValue = "" + result;

            						            	} else {
            						            		double result = Double.parseDouble(XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer).toString());
            						            		sValue = "" + result;

            						            	}


            										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
            										Pattern pattern = Pattern.compile(regex);
            										Matcher matcher = pattern.matcher(content);
            										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");

            						            }
            						        }

                                    	}




                                    	if (StringUtils.isEmpty(gValue) == true) {

        						        } else {
        						            logger.debug("["+sheetName+"]数据格式化: " + gValue);


        						        	gValue = gValue.replace(",", "").replace("Z", "#");
        						            DecimalFormat oneDecimalFormat = new DecimalFormat(gValue); // ZZZ.Z 格式
        						            sValue = oneDecimalFormat.format(Double.parseDouble(sValue));


    										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
    										Pattern pattern = Pattern.compile(regex);
    										Matcher matcher = pattern.matcher(content);
    										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");

        						        }


                                        if ("DED00050".equals(mValue)) {
                                        	mValue=mValue;
                                        }

                                      	if ("元号NG".equals(eValue)) {
                                      		sValue = ""
                                      				+ "<gen:era>5</gen:era>"
                                      				+ "<gen:yy>5</gen:yy>"
                                      				+ "<gen:mm>1</gen:mm>"
                                      				+ "<gen:dd>1</gen:dd>"
                                      				+ ""
                                      				+ "";

                    						// 使用正则表达式匹配指定标签内的值
    										String regex = "<" + mValue + ">(.*?)</" + mValue + ">";
    										Pattern pattern = Pattern.compile(regex);
    										Matcher matcher = pattern.matcher(content);
    										// 进行替换，将匹配到的值替换为 newValue
    										content = matcher.replaceAll("<" + mValue + ">" + sValue + "</" + mValue + ">");
    										continue;

//          						        } else if ("年".equals(eValue)) {
//                                      		sValue = "<gen:yy>6</gen:yy>";
//          						        } else if ("月".equals(eValue)) {
//          						        	//TODO
//                                      		sValue = "<gen:mm>1</gen:mm>";
//          						        } else if ("日".equals(eValue)) {
//                                      		sValue = "<gen:dd>2</gen:dd>";

                    			        }

                                    }


                                } else if (j == 3) {

                                }

                          		sValue = sValue.replaceAll("[\\t\\n\\r]", "");

                                if (j == 3) {
                                    if ("DEA00050".equals(mValue)) {
                                    	mValue=mValue;
                                    }

                                    if ("区分".equals(bValue)) {
                                      	if ("元号".equals(eValue)) {

                                      	} else {
                                      		String mValueOld = mValue;

                                            // 替换目标字符串
                                            mValue = "<" + mValueOld + ">(.*?)</" + mValueOld + ">";
                                        	Pattern pattern = Pattern.compile(mValue);
                                        	Matcher matcher = pattern.matcher(content);
                                        	if (matcher.find()) {
                                        		sValue = matcher.group(1);
                                        	} else {
                                        		logger.error("未找到匹配的标签:" + mValue);
                                        	}

                                      		sValue = "<kubun_CD>"+sValue+"</kubun_CD>";

                                      		mValue = "(<" + mValueOld + ">)(.*?)(</" + mValueOld + ">)";
                                      		content = content.replaceAll(mValue, "$1" + Matcher.quoteReplacement(sValue) + "$3");
                                      	}
                                    }


                                } else {


                        			if (sValue.startsWith("【") && sValue.endsWith("】")) {

                        			} else {


//                        		        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//                        		        DocumentBuilder builder = factory.newDocumentBuilder();
//                                        Document doc = builder.parse(new InputSource(new StringReader(content)));
//
//                        		        NodeList list = doc.getElementsByTagName(mValue);
//                        		        if (list.getLength() > 0) {
//                        		            Element Element = (Element) list.item(0);
//                        		            Element.setTextContent(sValue);
//                        		        }
//
//                        		        // 保存修改后的 XML
//                                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//                                        Transformer transformer = transformerFactory.newTransformer();
//                                        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//                                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//
//                        		        StringWriter writer = new StringWriter();
//                                        transformer.transform(new DOMSource(doc), new StreamResult(writer));
//                                        content = writer.toString();

                        				/*
                                   	 *
                                   	 */


                                        if ("DEA00190".equals(mValue)) {
                                        	mValue=mValue;
                                        }
                                        // 替换目标字符串
                                        //mValue = "<" + mValue + "></" + mValue + ">";
                                		if (StringUtils.isEmpty(sValue) == true) {
//                                			sValue = sValueOld;
                                        }
                                    	//sValue = ">" + sValue + "<";
                                    	//content = content.replaceAll(mValue, mValue.replace("><", sValue));
                                    	content = content.replaceAll(
                                                "(<" + mValue + "[^>]*>)(\\s*)</" + mValue + ">",
                                                "$1" + sValue + "</" + mValue + ">"
                                            );



                        			}



                                }



                            }

                    	}

                    }


                    /*
                     *
                     */


                    String namespace = "xmlns=\"http://xml.e-tax.nta.go.jp/XSD/shohi\"";
					// 如果 SHY110 标签中还没有 xmlns 属性
					if (!content.contains("xmlns=")) {
						content = content.replaceFirst("<SHY110", "<SHY110 " + namespace);
					}

                    String key ="";
                    String value ="";

                    key = "softNM=\"\"" ;
                    value = "softNM=\"ntaclient\"" ;
                    content = content.replace(key, value);

                    key = "sakuseiNM=\"\"" ;
                    value = "sakuseiNM=\""+ FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25)  +"\"" ;
                    content = content.replace(key, value);

                    formatter = new SimpleDateFormat("yyyy-MM-dd");
                    formattedDate = formatter.format(now);

                    key = "sakuseiDay=\"\"" ;
                    value = "sakuseiDay=\""+ formattedDate  +"\"" ;
                    content = content.replace(key, value);


                    /*
                     *
                     */
                    //TODO
                    //TENPU
					if (sheetName.contains("SOZ")) {
						content = "<TENPU id=\"TENPU\">" + content + "</TENPU>";
					}


                    // 将字符串转换为 XML Document 对象
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(content)));

                    // 将 XML Document 转换为格式化的字符串
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                    XPathFactory xPathFactory = XPathFactory.newInstance();
                    XPath xpath = xPathFactory.newXPath();


                    //TENPU
					if (sheetName.contains("SOZ")) {
						//                    	content = "<TENPU id=\"TENPU\">" + content + "</TENPU>";

						XPathExpression expression = xpath.compile("//SOZ074");
						Element element = (Element) expression.evaluate(document, XPathConstants.NODE);

						// 添加命名空间属性
						if (element != null) {
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns", "http://xml.e-tax.nta.go.jp/XSD/somu");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:gen", "http://xml.e-tax.nta.go.jp/XSD/general");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:kyo", "http://xml.e-tax.nta.go.jp/XSD/kyotsu");
							element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

						}

					}



					/*
					 *
					 */
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String outputString = writer.toString();

                    // 移除空行
                    outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");

                    xtxMap.put(xtx_fileName, outputString);

                    // 输出格式化后的 XML 字符串到后台
                    logger.info("XML 格式化输出：");
                    logger.info("\n" + outputString);

                    writeToFile(outputString, path_ETAX_output+ "_" + file.getName().replace(".xml", ".xtx"));
//                    writeToFile(XmlConverter.convertXmlToEscaped(outputString), path_ETAX_output+ "_" + file.getName().replace(".xml", ".XmlToEscaped"));

					if (sheetName.contains("SOZ")) {
						outputString = outputString.replace("<TENPU id=\"TENPU\">", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
						outputString = outputString.replace("</TENPU>", "");

						TreeMap<String, String[]> zhangpiaoTreeMap = FuncUtilsAiEtax.PropertyCsvMap_zhangpiao.get(shouxu_id).get(shouxu_banben);
						String xml_fileName = "";
						for (Entry<String, String[]> entry : zhangpiaoTreeMap.entrySet()) {
				            if (entry.getKey().startsWith(xtx_fileName.split("_")[0].toUpperCase())) {
				            	xml_fileName = entry.getValue()[0];
				            }
						}
						xml_fileName = "_" + xml_fileName + "_" + file.getName();
	                    writeToFile(outputString, path_ETAX_output + xml_fileName);
//	                    writeToFile(XmlConverter.convertXmlToEscaped(outputString), path_ETAX_output+ "_" + xml_fileName.replace(".xml", ".XmlToEscaped"));

					}

            		//TODO
//            		break;
                }

            }
        } catch (Exception e) {
        	loggerStringBuffer.append(e);
//            e.printStackTrace();
            throw e;

        } finally {
            // 关闭资源
            if (workbook != null) {
            	workbook.close();
            }
            if (fis != null) {
                fis.close();
            }

        }
        writeToFile(loggerStringBuffer.toString(), path_ETAX_output + ".log");
		return xtxMap;
	}

	private static String evaluateLogicFromTemplate(String formula, t_etax_account_infoExBean t_etax_account_infoExBean, Object objBean) {
		// 提取并替换表达式中的【key】为 value
		Pattern pattern = Pattern.compile("【(.*?)】");
        Matcher matcher = pattern.matcher(formula);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String keyNew = findFuzzyMatchkey(tblMap, key); // 模糊查找
            String value = tblMap.get(keyNew);

            //TODO
            if (objBean instanceof TableDefinition) {
            	value = "【" + key + "】";
			}

    		if (StringUtils.isEmpty(value) == true) {
                logger.error("没有找到DB定义项目:" + value);
            	continue;

    		} else {
    			if (keyNew.contains("t_etax_account_info") || keyNew.contains("t_etax_account_res")) {
    				value = FuncUtils.getBeanValue(t_etax_account_infoExBean, value);
    		        // 替换非法字符
    				value = value.replaceAll("<(\\d+[-]\\d+)>", "&lt;$1&gt;");

    			} else {
    				if (objBean instanceof TableDefinition) {
        				value = FuncUtils.getBeanValue_AI((TableDefinition)objBean, value);
    				} else {
        				value = FuncUtils.getBeanValue(objBean, value);
    				}

    			}

			}

            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(sb);
        return sb.toString();
	}


    // 支持模糊查找 tblMap 的 key（包含即可）
    private static String findFuzzyMatchkey(LinkedHashMap<String, String> map, String keyword) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().contains(keyword)) {
                return entry.getKey();
            }
        }
        return ""; // 未匹配返回空字符串
    }


	private static LinkedHashMap<String, String> findSheetsStartingWithPS_IT(t_etax_account_infoExBean t_etax_account_infoExBean
			, Object objBean, String filePath, String path_Excel, String path_ETAX_output
			, LinkedHashMap<String, String> xtxMap0) throws Exception {
		StringBuffer loggerStringBuffer = new StringBuffer();
		LinkedHashMap<String, String> xtxMap = new LinkedHashMap<String, String>();

		FileInputStream fis = null;
        Workbook workbook = null;
        XMLCalculator XMLCalculator = new XMLCalculator();
        try {
            // 打开 Excel 文件
            fis = new FileInputStream(path_Excel);
            workbook = WorkbookFactory.create(fis);

            // 循环所有工作表
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                String sheetName = sheet.getSheetName();

                // 检查工作表名称是否以 "PS_" 开头
                if (sheetName.startsWith("PS_IT部")) {
                	logger.debug("找到符合条件的工作表: " + sheetName);


                    // 读取整个文件的内容为字符串
                    String content = "";

                    //先循环固定 OR DB，再循环计算
                    for (int j = 1; j <= 2; j++) {
                    	// 从第四行开始输出 M 列和 S 列的值
                    	for (int rowIndex = 3; rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                            Row row = sheet.getRow(rowIndex);
                            if (row != null) {

                            	//項番
                                if (StringUtils.isEmpty(FuncUtilsExcel.getCellValueAsString(row.getCell(0))) == true) {
                                	break;
                                }

//                            	Cell aCell = row.getCell(0);  // A 列（索引 0）	//項番
                            	Cell bCell = row.getCell(1);  // B 列（索引 1）	//入力型
                            	Cell cCell = row.getCell(2);  // C 列（索引 2）	//帳票項番
                            	Cell dCell = row.getCell(3);  // D 列（索引 3）	//項目（ｸﾞﾙｰﾌﾟ）名
                            	Cell eCell = row.getCell(4);  // E 列（索引 4）	//項目名
//                            	Cell fCell = row.getCell(5);  // F 列（索引 5）	//繰返し回数
                            	Cell gCell = row.getCell(6);  // G 列（索引 6）	//書式
//                            	Cell hCell = row.getCell(7);  // H 列（索引 7）	//入力ﾁｪｯｸ
//                            	Cell iCell = row.getCell(8);  // I 列（索引 8）	//計算
                            	Cell jCell = row.getCell(9);  // J 列（索引 9）	//値の範囲
//                            	Cell kCell = row.getCell(10); // K 列（索引 10）	//計算No
                            	Cell lCell = row.getCell(11); // L 列（索引 11）	//計算／備考
                            	Cell mCell = row.getCell(12); // M 列（索引 12）	//ＸＭＬタグ
//                            	Cell nCell = row.getCell(13); // N 列（索引 13）	//順位
//                            	Cell oCell = row.getCell(14); // O 列（索引 14）	//ID属性
                            	Cell pCell = row.getCell(15); // P 列（索引 15）	//IDREF属性

//                            	Cell qCell = row.getCell(16); // Q 列（索引 16）
                            	Cell rCell = row.getCell(17); // R 列（索引 17）
                            	Cell sCell = row.getCell(18); // S 列（索引 18）
                            	Cell tCell = row.getCell(19); // S 列（索引 19）


//                            	String aValue = FuncUtilsExcel.getCellValueAsString(aCell);		//項番
                            	String bValue = FuncUtilsExcel.getCellValueAsString(bCell);		//入力型
                            	String cValue = FuncUtilsExcel.getCellValueAsString(cCell);		//帳票項番
                            	String dValue = FuncUtilsExcel.getCellValueAsString(dCell);		//項目（ｸﾞﾙｰﾌﾟ）名
                            	String eValue = FuncUtilsExcel.getCellValueAsString(eCell);		//項目名
//                            	String fValue = FuncUtilsExcel.getCellValueAsString(fCell);		//繰返し回数
                            	String gValue = FuncUtilsExcel.getCellValueAsString(gCell);		//書式
//                            	String hValue = FuncUtilsExcel.getCellValueAsString(hCell);		//入力ﾁｪｯｸ
//                            	String iValue = FuncUtilsExcel.getCellValueAsString(iCell);		//計算
                            	String jValue = FuncUtilsExcel.getCellValueAsString(jCell);		//値の範囲
//                            	String kValue = FuncUtilsExcel.getCellValueAsString(kCell);		//計算No
                            	String lValue = FuncUtilsExcel.getCellValueAsString(lCell);		//計算／備考
                            	String mValue = FuncUtilsExcel.getCellValueAsString(mCell);		//ＸＭＬタグ
//                            	String nValue = FuncUtilsExcel.getCellValueAsString(nCell);		//順位
//                            	String oValue = FuncUtilsExcel.getCellValueAsString(oCell);		//ID属性
                            	String pValue = FuncUtilsExcel.getCellValueAsString(pCell);		//IDREF属性


//                            	String qValue = FuncUtilsExcel.getCellValueAsString(qCell);
                            	String rValue = FuncUtilsExcel.getCellValueAsString(rCell);		//区分
                            	String sValue = FuncUtilsExcel.getCellValueAsString(sCell);		//对象元素/値
                            	String tValue = FuncUtilsExcel.getCellValueAsString(tCell);		//运算关系

                                if ("ATC00120".equals(cValue)) {
                                	cValue=cValue;
                                }

                                if (j == 1) {

                                    if ("NOZEISHA_BANGO".equals(cValue)) {
                                    	cValue=cValue;
                                    }

                                    //PS区分
                                    if ("删除".equals(rValue)) {
                                        continue;
                                    }



                                    //PS区分
                                    if (rValue.equals("固定")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);


                                    } else if (rValue.contains("DB")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	if (sValue.startsWith("【") && sValue.endsWith("】")) {
                            				String sValueOld = sValue;
                            				sValue = sValue.replace("【", "").replace("】", "");
                                			sValue = tblMap.get(sValue);
                                    		if (StringUtils.isEmpty(sValue) == true) {
                                                logger.error("没有找到DB定义项目:" + sValue);
                                            	continue;

                                    		} else {
                                    			if (sValueOld.contains("t_etax_account_info") || sValueOld.contains("t_etax_account_res")) {
                                    				sValue = FuncUtils.getBeanValue(t_etax_account_infoExBean, sValue);

                                    			} else {
                                    				sValue = FuncUtils.getBeanValue(objBean, sValue);

                                    			}

                                			}


                                		} else {
                                			logger.error("没有找到DB定义项目【】格式写法:" + sValue);
                                        	continue;

                                		}



                                    } else  if (rValue.contains("定义")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	String[] shouxu_liset = xtxMap0.get(shouxu_sheetName).split(",");
                                    	String shouxu_id = shouxu_liset[0];
                                    	String shouxu_banben = shouxu_liset[1];

                                    	TreeMap<String, String[]> shouxu_all = PropertyCsvMap_shouxu.getOrDefault(shouxu_id, new TreeMap<>());
//                                    	PropertyCsvMap_shouxu
//                                    	└── firstColumn (String)
//                                    	    └── versionKey (String) : rowData (String[])
                                    	String[] csv_shouxu = shouxu_all.get(shouxu_banben);


//                                    	TreeMap<String, String[]> zhangpiao = PropertyCsvMap_zhangpiao.getOrDefault(sValue, new TreeMap<>());
//                                    	String[] csv_zhangpiao = zhangpiao.get(csv_shouxu[0]);

										if (csv_shouxu != null) {
											sValue = ""
													+ ""
													+ "<procedure_CD>" + csv_shouxu[0] + "</procedure_CD>"
													+ "<procedure_NM>" + csv_shouxu[1] + "</procedure_NM>"
													+ ""
													+ "";

	                                    	//TODO
//											sValue = ""
//													+ "<procedure_CD>RSH0020</procedure_CD>"
//													+ "<procedure_NM>消費税及び地方消費税申告(一般・法人)</procedure_NM>"
//													+ ""
//													+ "";
										} else {
                                    	    logger.error("TreeMap is empty.");
                                    	}

                                    }



                                    // 目标字符串作成
                                    sValue = "<" + cValue + " ID=\"" + cValue + "\">" + sValue + "</" + cValue + ">";
                                    content = content +"\n"+ sValue;


                                } else if (j == 2) {
                                    if ("NOZEISHA_BANGO".equals(cValue)) {
                                    	cValue=cValue;
                                    	tValue="/^\\d{13}$/.test('【】') ? '<gen:hojinbango>【】</gen:hojinbango>' : ''";
                                    }

                                    //PS区分
                                    if (rValue.contains("【計算】")) {
										String logger_info = "[" + sheetName + "][" + (rowIndex + 1) + "行][M列值: " + dValue + "][D列值: " + mValue + "][E列值: " + eValue + "][R列值: " + rValue + "][S列值: " + sValue + "][T列值: " + tValue + "][L列值: " + lValue.replace("\n", "") + "]";
                                    	logger.debug(logger_info);loggerStringBuffer.append("\n" + logger_info);

                                    	//PS运算关系
                                    	String formula = tValue;
                                    	if (StringUtils.isEmpty(formula) == true) {
                                    		//ETAX式样計算
                                    		//計算／備考
                                    		formula = lValue;
                                    	}

                                    	formula = formula.replace("【計算】", "");
        								formula = FuncUtils.toHalfWidth(formula);


                                    	if (StringUtils.isEmpty(formula) == true) {
                                			logger.error("没有計算式:" + formula);
//                                        	continue;
                                    	} else {
                                    		//自动识别，自定义计算公式
                                            for (Entry<String, String> entry : XMLCalculator.roundingRules.entrySet()) {
                                                String key = entry.getKey();
                                                String value = entry.getValue();

                                                if (formula.contains(key)) {
                                                    formula = formula.replace(key, "");
                                                    formula = formula + "\n" + value.replace("【】", cValue);
                                                }
                                            }

            								formula = formula.replace("・", "");
            								formula = formula.replace("()", "");
//            								if (formula.contains("(") || formula.contains(")")) {
//            									logger.error("有未定义的运算逻辑项目:" + formula);
//            									continue;
//            								}

            						        String[] formulaLines = formula.split("\n"); // 按行分割公式字符串

            						        for (String line : formulaLines) {
            						            line = line.trim(); // 去除行首尾的空白
            						            if (!line.isEmpty()) {
													line = line.replace("【】", "【" + cValue + "】");
            						            	String result = (String) XMLCalculator.calculate(line, content, logger_info, loggerStringBuffer);

            										sValue = "" + result;

            										String regex = "<(" + cValue + ")([^>]*)>(.*?)</\\1>";
            										Pattern pattern = Pattern.compile(regex);
            										Matcher matcher = pattern.matcher(content);
            										content = matcher.replaceAll("<$1$2>" + sValue + "</$1>");


            						            }
            						        }

                                    	}
                                    }

                                }

                            }
                    	}
                    }


                    /*
                     *
                     */
                    content = "<PS_ROOT>" + content + "</PS_ROOT>";

                    // 将字符串转换为 XML Document 对象
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document document = builder.parse(new InputSource(new StringReader(content)));

                    // 将 XML Document 转换为格式化的字符串
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");



					/*
					 *
					 */
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String outputString = writer.toString();


                	String key = "PS_ROOT";
                	outputString = outputString.replace("<"+key+">", "");
                	outputString = outputString.replace("</"+key+">", "");

                    // 移除空行
                    outputString = outputString.replaceAll("(?m)^[ \t]*\r?\n", "");

                    xtxMap.put(sheetName, outputString);

                    // 输出格式化后的 XML 字符串到后台
//                    logger.info("XML 格式化输出：");
//                    logger.info("\n" + outputString);

                    writeToFile(outputString, path_ETAX_output+ "_" + sheetName + ".xtx");

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            // 关闭资源
            if (workbook != null) {
            	workbook.close();
            }
            if (fis != null) {
                fis.close();
            }

        }
		return xtxMap;
	}


	public static File findFileByNameRecursive(String directoryPath, String fileName) {
        File directory = new File(directoryPath);

        // 检查路径是否是目录且存在
        if (directory.exists() && directory.isDirectory()) {
            // 列出目录下的所有文件和子文件夹
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().equalsIgnoreCase(fileName)) {
                        return file; // 返回找到的文件对象
                    } else if (file.isDirectory()) {
                        // 递归查找子文件夹
                        File found = findFileByNameRecursive(file.getAbsolutePath(), fileName);
                        if (found != null) {
                            return found; // 返回找到的文件对象
                        }
                    }
                }
            }
        }

        return null; // 如果未找到匹配文件则返回 null
    }



	public static String convertColumnsToXml(List<ColumnDefinition> columns) {
	    try {
	        StringBuilder sb = new StringBuilder();
	        for (ColumnDefinition col : columns) {
	            if (col.name != null && !col.name.isEmpty()) {
	                sb.append("<").append(col.name).append(">")
	                  .append(col.value != null ? col.value : "")
	                  .append("</").append(col.name).append(">\n");
	            }
	        }
	        return sb.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}


}