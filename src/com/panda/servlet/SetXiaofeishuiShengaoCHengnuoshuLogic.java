package com.panda.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.panda.bean.User_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.dao.User_infoDao;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;

@WebServlet("/SetXiaofeishuiShengaoCHengnuoshuLogic")
@MultipartConfig
public class SetXiaofeishuiShengaoCHengnuoshuLogic extends HttpServlet {

	private static Logger logger = Logger.getLogger(SetXiaofeishuiShengaoCHengnuoshuLogic.class.toString());


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

 		logger.info("start");

		req.setCharacterEncoding("UTF-8");
		resp.setContentType("text/html; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		HttpSession session = req.getSession();
		String yyyymmdd_count = req.getParameter("yyyymmdd_count");
		String user_id = req.getParameter("license");

		User_infoDao LicenseDao = new User_infoDao();
		User_infoBean User_infoBean = new User_infoBean();



		String hidden_key = req.getParameter("hidden_key");
		if (hidden_key == null) {
			hidden_key = "";
		}

		String hidden_value = req.getParameter("hidden_value");

		session.setAttribute("license", user_id);
		session.setAttribute("user_id", user_id);

		/*
		 * license確認
		 */
		String pw = req.getParameter("pw");
		session.setAttribute("pw", pw);
//		PrintWriter out = resp.getWriter();
		FuncUtils FunctionUtils = new FuncUtils();
		User_infoBean = LicenseDao.select(user_id);
		if ("open_id".equals(user_id)) {
			session.setAttribute("User_infoBean", new User_infoBean());

		} else {
			session.setAttribute("User_infoBean", User_infoBean);

		}

		String license = User_infoBean.getLicense_yyyymmdd();
		logger.info("license YYYYMMDD" +  license);
		if (FunctionUtils.getLicenseboolean(pw, this.getServletName(), User_infoBean) == false) {
			logger.debug("PandaServiceTools → License invalid");
			out.write("PandaServiceMA → License invalid");
			return;
		}

		String form24 = req.getParameter("form24");
		String form25 = req.getParameter("form25");
		String form26 = req.getParameter("form26");
		String form27 = req.getParameter("form27");
		String form28 = req.getParameter("form28");
		String form29 = req.getParameter("form29");
		String form30 = req.getParameter("form30");


		String form_jizhun_qijian2 = req.getParameter("form_jizhun_qijian2");


		String form_A = req.getParameter("form_A");
		String form_B = req.getParameter("form_B");
		String form_C = req.getParameter("form_C");
		String form_D = req.getParameter("form_D");



		/*
		 * xiaofeishui PDF
		 */
		if (!StringUtils.isEmpty(yyyymmdd_count) && "get-file".equals(hidden_key) && "xiaofeishui".equals(hidden_value)) {



			try {

				String selectedValues = req.getParameter("selectedValues");

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				String form_CompanyName_English = t_etax_account_infoExBean.getCompanyName_English();
				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				form_CompanyName_English = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_English);






				/*
				 * PDF
				 */
				String pathPDF = getServletContext().getRealPath("/ETAX_moban/消費税申告書_" + selectedValues + ".pdf");
//				String fileName = t_etax_account_infoExBean.getPDSK();
//				if (StringUtils.isEmpty(fileName)) {
//					fileName = "PDSKXXXXXX";
//				}
				String fileName = "消費税申告書_" + form_CompanyName_English + ".pdf";
				String destinationFilePathPDF = getServletContext().getRealPath("/ETAX_output/消費税申告書/" + fileName);

				File filePDF = new File(destinationFilePathPDF);
				filePDF.delete();

	            // 加载PDF模板
	            PDDocument document = PDDocument.load(new File(pathPDF));
	            // ドキュメントカタログを取得
	            PDDocumentCatalog catalog = document.getDocumentCatalog();
	            // AcroForm（交互式フォーム）を取得
	            PDAcroForm acroForm = catalog.getAcroForm();
	            // 假设 pdField 是你设置值的表单字段对象
	            acroForm.setNeedAppearances(true);





//	            // 移除所有的安全设置，将文档设置为只读
//	            document.setAllSecurityToBeRemoved(true);
//	            // 关闭文档，不保存修改
//              document.setAllSecurityToBeRemoved(true);
//
//	            // アクセス許可を取得
//	            AccessPermission permission = new AccessPermission();
//
//	            // 文書を読み取り専用に設定
//	            permission.setReadOnly();
//
//	            // 標準の保護ポリシーを作成
//	            StandardProtectionPolicy policy = new StandardProtectionPolicy("", "", permission);
//
//	            // 保護ポリシーを文書に設定
//	            document.protect(policy);



            	// 遍历所有表单字段
	            for (PDField field : acroForm.getFieldTree()) {
	            	// 设置为只读
	                field.setReadOnly(true);
	            }


//	            {0=PDSK230193
//	            		1=shen zhen shi yi shuo ke ji you xian gong si
//	            		2=615
//	            		Building8,1970 Technology Town,Mingzhi Community,Mingzhi Street,Longhua District,ShenzhenShi
//	            		3=Jiang Xueyuan
//	            		4=2344042800920001
//	            		5=5700150109350
//	            		6=T5700150109350
//	            		7=ｓｈｅｎ　ｚｈｅｎ　ｓｈｉ　ｙｉ　ｓｈｕｏ　ｋｅ
//	            		8=６１５，　Ｂｕｉｌｄｉｎｇ８，１９７０　Ｔｅｃｈｎ
//	            		9=Ｊｉａｎｇ　Ｘｕｅｙｕａｎ
//	            		10=シンセンシイセキカギユウゲンコウシ
//	            		11=シェン ジェン シー ロン フワー チー ミン ジー ジエ ダオ ミン ジー ショー チー コー ジー ユエン ドン
//	            		12=ジャンガクゲン
//	            		13=シンセンシイセキカギユウゲンコウシ
//	            		14=シェン ジェン シー ロン フワー チー ミン ジ
//	            		15=ジャンガクゲン
//	            		16=深圳市易硕科技有限公司
//	            		17=深圳市龙华区民治街道民治社区1970科技园8栋615
//	            		18=蒋学元}


	            PDTextField fieldB;


				//内容替换
				String key = "";
				String value = "";

	            //如果是个人，这三个地方是空白的 B7 B6 B5
				if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {

	            } else {
//					#事業者名カナ#
					key = "#事業者名カナ#";
					value = t_etax_account_infoExBean.getCompanyName_pianjiaming();
	            	fieldB = (PDTextField) acroForm.getField("B7");fieldB.setValue(value);

//					#事業者名#
					key = "#事業者名#";
					value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getCompanyName_English());
	            	fieldB = (PDTextField) acroForm.getField("B6");fieldB.setValue(value);


//					#法人番号#
					key = "#法人番号#";
					value = t_etax_account_infoExBean.getHoujinBangou();
					if (!StringUtils.isEmpty(value)) {
			            if (value.length() == 13) {
			            	// 截取一位
			            	String part1 = value.substring(0, 1);
			            	// 截取四位
			            	String part2 = value.substring(1, 5);
			            	String part3 = value.substring(5, 9);
			            	String part4 = value.substring(9, 13);
			            	fieldB = (PDTextField) acroForm.getField("B5_1");fieldB.setValue(part1);
			            	fieldB = (PDTextField) acroForm.getField("B5_2");fieldB.setValue(part2);
			            	fieldB = (PDTextField) acroForm.getField("B5_3");fieldB.setValue(part3);
			            	fieldB = (PDTextField) acroForm.getField("B5_4");fieldB.setValue(part4);
			            }
					}



	            }

//				#代表人氏名カナ#
				key = "#代表人氏名カナ#";
				value = t_etax_account_infoExBean.getDaibiaoName_pianjiaming();
	            fieldB = (PDTextField) acroForm.getField("B9");fieldB.setValue(value);

//				#代表人氏名#
				key = "#代表人氏名#";
				value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getDaibiaoName_English());
	            fieldB = (PDTextField) acroForm.getField("B8");fieldB.setValue(value);

//				#課税標準額#	form25
				key = "#課税標準額#";
				value = form25;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B14");fieldB.setValue(value);


//				#消費税額#	form26
				key = "#消費税額#";
				value = form26;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B15");fieldB.setValue(value);


//				#控除対象仕入税額#	form27
				key = "#控除対象仕入税額#";
				value = form27;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B16");fieldB.setValue(value);

//				#差引税額#	form28
				key = "#差引税額#";
				value = form28;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B17");fieldB.setValue(value);

//				#課税資産の譲渡等の対価の額#	form24
				key = "#課税資産の譲渡等の対価の額#";
				value = form24;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B13");fieldB.setValue(value);


//		        本納税者が基準期間における課税売上高は
	            if ("简易课税（零售业）".equals(selectedValues))  {
					value = form_jizhun_qijian2;
		            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            }
	            fieldB = (PDTextField) acroForm.getField("fill_38");fieldB.setValue(value);


//				#譲渡割額納税額#	form29
				key = "#譲渡割額納税額#";
				value = form29;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B18");fieldB.setValue(value);

//				#消費税及び地方消費税の合計#	form30
				key = "#消費税及び地方消費税の合計#";
				value = form30;
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B19");fieldB.setValue(value);

	            long result = 0;
	            if ("简易课税（零售业）".equals(selectedValues))  {
//				#第２種課税売上高千円#
	            	result =  (long) Math.ceil(Double.parseDouble(form24) / 1000.0) * 1000;

	            } else {
	            	result =  (long) Math.ceil(Double.parseDouble(form_jizhun_qijian2) / 1000.0) * 1000;

	            }
				key = "#第２種課税売上高千円#";
				value = String.valueOf(result);
	            if (value.length() > 3) {
	            	value  = value.substring(0, value.length() - 3);
	            } else {
	            	value = "0";
	            }
	            value = new DecimalFormat("#,##0").format(Long.parseLong(value));
	            fieldB = (PDTextField) acroForm.getField("B21");fieldB.setValue(value);


	            if ("简易课税（零售业）".equals(selectedValues))  {
		            //売 上 割 合 ％
		            value = "100.0";
		            if (result == 0) {
			            value = "0.0";
		            }
		            fieldB = (PDTextField) acroForm.getField("Text1");fieldB.setValue(value);
	            }




	            // 保存修改后的文档
	            document.save(destinationFilePathPDF);
	            // 关闭文档
	            document.close();

	            //TODO
//	            Thread.sleep(5000);


				out.print("{\"res\":\"" + fileName.replace("\\", "\\\\")+ "\"}");
				logger.debug("end " + hidden_key);
				return;

			} catch (Throwable e) {
				e.printStackTrace();
				out.print("{\"res\":\"ZIP文件不存在\"}");
				logger.debug("end " + hidden_key);
				return;
			}




			/*
			 * ncc
			 */
		} else if (!StringUtils.isEmpty(yyyymmdd_count) && "get-file".equals(hidden_key) && "ncc".equals(hidden_value)) {


			try {

				String selectedValues = req.getParameter("selectedValues");
				String selectedValues_ncc_type = req.getParameter("selectedValues_ncc_type");


				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);
				String form_CompanyName_English = t_etax_account_infoExBean.getCompanyName_English();
				//去掉字符串里的TAB，首尾半角空格，首尾全角空格
				form_CompanyName_English = FuncUtils.trimWhitespaceAndTabs(form_CompanyName_English);
				form_CompanyName_English = FuncUtils.toHalfWidthAndTruncate(form_CompanyName_English, 25);

//				#利用者識別番号#
//				#法人番号#
//				#事業者名#
//				#事業者名カナ#
//				#代表人氏名#
//				#代表人氏名カナ#




				String path = getServletContext().getRealPath("/ETAX_moban");
				String path_ETAX_output = getServletContext().getRealPath("/ETAX_output");


				String pathNCC0 = getServletContext().getRealPath("/ETAX_moban/ncc0法人.txt");
				if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
					pathNCC0 = getServletContext().getRealPath("/ETAX_moban/ncc0個人.txt");
				}
				File dataModelFileNCC0 = new File(pathNCC0);
				String fileContent0 = FuncUtils.readFileContent(dataModelFileNCC0);


				String fileContent = "";
				if (selectedValues_ncc_type.contains("ncc_type_all") || selectedValues_ncc_type.contains("ncc_type_xiaofeishui_shengao")) {
					String pathNCC = getServletContext().getRealPath("/ETAX_moban/ncc消費税申告書_法人_" + selectedValues + ".txt");
					if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
						pathNCC = getServletContext().getRealPath("/ETAX_moban/ncc消費税申告書_個人_" + selectedValues + ".txt");
					}
					File dataModelFileNCC = new File(pathNCC);
					fileContent = fileContent + "\r\n" + FuncUtils.readFileContent(dataModelFileNCC);
				}


				if (selectedValues_ncc_type.contains("ncc_type_all") || selectedValues_ncc_type.contains("ncc_type_xiaofeishui_yidong")) {
					String pathNCC = getServletContext().getRealPath("/ETAX_moban/ncc消費税異動届出書.txt");
					File dataModelFileNCC = new File(pathNCC);
					fileContent = fileContent + "\r\n" + FuncUtils.readFileContent(dataModelFileNCC);
				}



				if (selectedValues_ncc_type.contains("ncc_type_all") || selectedValues_ncc_type.contains("ncc_type_zhuandaili_jiechushu")) {
					String pathNCC = getServletContext().getRealPath("/ETAX_moban/ncc法人転代理.txt");
					File dataModelFileNCC = new File(pathNCC);
					fileContent = fileContent + "\r\n" + FuncUtils.readFileContent(dataModelFileNCC);
				}

				fileContent = "<container name=\"申告等管理\" progId=\"nta.CLCStatementManager.1\">" + fileContent + "\r\n</container>\r\n\r\n";
				fileContent0 = fileContent0.replace("<container name=\"申告等管理\" progId=\"nta.CLCStatementManager.1\"/>", fileContent);


				//内容替换
				String fileName = yyyymmdd_count + "_" + form_CompanyName_English;
				path = path + "/" + fileName;
				path_ETAX_output = path_ETAX_output + "/" + fileName;


				File directory_ETAX_output = new File(path_ETAX_output);
				directory_ETAX_output.mkdirs();


				File directory = new File(path_ETAX_output + "/nccファイル　xtxファイル");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(path_ETAX_output + "/R５年度　Amazon Transaction report");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(path_ETAX_output + "/前年度・前々年度売上資料");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(path_ETAX_output + "/謄本・定款");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}
				directory = new File(path_ETAX_output + "/提出済み税務書類");
				if (!directory.exists() || !directory.isDirectory()) {
					directory.mkdirs();
				}

				/*
				 * ncc
				 */
				String pathNewNCC = path_ETAX_output + "/" + form_CompanyName_English + ".ncc";

				// 将 A 列和 B 列的数据存储到 excelData HashMap
				String ncc_key = "";
				String ncc_value = "";



//				#利用者識別番号#
				ncc_key = "#利用者識別番号#";
				ncc_value = t_etax_account_infoExBean.getBangou();
				if (StringUtils.isEmpty(t_etax_account_infoExBean.getBangou())) {
					ncc_value = "";
				}
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);


//					#法人番号#
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

//				#事業者名#
				ncc_key = "#事業者名#";
				ncc_value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getCompanyName_English());
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#事業者名カナ#
				ncc_key = "#事業者名カナ#";
				ncc_value = t_etax_account_infoExBean.getCompanyName_pianjiaming();
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#代表人氏名#
				ncc_key = "#代表人氏名#";
				ncc_value = FuncUtils.toFullWidth(t_etax_account_infoExBean.getDaibiaoName_English());
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#代表人氏名カナ#
				ncc_key = "#代表人氏名カナ#";
				ncc_value = t_etax_account_infoExBean.getDaibiaoName_pianjiaming();
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);





//				#課税資産の譲渡等の対価の額#	form24
				ncc_key = "#課税資産の譲渡等の対価の額#";
				ncc_value = form24;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#課税標準額#	form25
				ncc_key = "#課税標準額#";
				ncc_value = form25;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#消費税額#	form26
				ncc_key = "#消費税額#";
				ncc_value = form26;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#控除対象仕入税額#	form27
				ncc_key = "#控除対象仕入税額#";
				ncc_value = form27;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#差引税額#	form28
				ncc_key = "#差引税額#";
				ncc_value = form28;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#譲渡割額納税額#	form29
				ncc_key = "#譲渡割額納税額#";
				ncc_value = form29;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//				#消費税及び地方消費税の合計#	form30
				ncc_key = "#消費税及び地方消費税の合計#";
				ncc_value = form30;
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);

				//TODO
//				#第２種課税売上高千円#
				long result =  (long) Math.ceil(Double.parseDouble(form24) / 1000.0) * 1000;
				ncc_key = "#第２種課税売上高千円#";
				ncc_value = String.valueOf(result);
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);


	            if ("简易课税（零售业）".equals(selectedValues))  {
		            //売 上 割 合 ％
	            	ncc_value = "100.0";
		            if (result == 0) {
		            	ncc_value = "0.0";
		            }
					ncc_key = "#第２種売上割合#";
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);
	            }


	            ncc_key = "#基準期間の課税売上#";
	            if ("简易课税（零售业）".equals(selectedValues))  {
					ncc_value = form_jizhun_qijian2;

	            } else {
					result =  (long) Math.ceil(Double.parseDouble(form_jizhun_qijian2) / 1000.0) * 1000;
					ncc_value = String.valueOf(result);
	            }
	            fileContent0 = fileContent0.replace(ncc_key, ncc_value);


	            if ("原则课税".equals(selectedValues))  {
//	            	A	含税总销售额	総課税売上高	form_A
//	            	B	适格请求书总支出额	適格請求書総支出額	form_B
//	            	C	非适格请求书总支出额	非適格請求書総支出額	form_C
//	            	D	进口消费税国税部分总额		form_D

//	            	#適格請求書税込#	画面　适格请求书总支出额
					ncc_key = "#適格請求書税込#";
					ncc_value = form_B;
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//	            	#適格請求書控除消費税#	計算　适格请求书总支出额*7.8/110　舍去小数点后数字 直接舍去不要四舍五入
					ncc_key = "#適格請求書控除消費税#";
					ncc_value = String.valueOf((int) (Double.parseDouble(form_B)*7.8/110));
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//	            	#不適請求書税込#	画面　非适格请求书总支出额
					ncc_key = "#不適請求書税込#";
					ncc_value = form_C;
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//	            	#不適請求書控除消費税#	計算　非适格请求书总支出额*7.8/110*80%　舍去小数点后数字 直接舍去不要四舍五入
					ncc_key = "#不適請求書控除消費税#";
					ncc_value = String.valueOf((int) (Double.parseDouble(form_C)*7.8/110*0.8));
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);

//	            	#輸入消費税国税#	画面　进口消费税国税部分总额
					ncc_key = "#輸入消費税国税#";
					ncc_value = form_D;
					fileContent0 = fileContent0.replace(ncc_key, ncc_value);

	            }

	            /*
	             * ncc法人転代理.txt
	             */
				ncc_key = "#英字本店住所２５全角#";
				ncc_value = t_etax_account_infoExBean.getAddress_English();
				if (StringUtils.isEmpty(ncc_value)) {
					ncc_value = "";
				} else {
					ncc_value = FuncUtils.toFullWidthAndTruncate(ncc_value, 25);
				}
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);


				ncc_key = "#インボイス登録番号#";
				ncc_value = t_etax_account_infoExBean.getInvoiceBangou();
				if (StringUtils.isEmpty(ncc_value)) {
					ncc_value = "";
				}
				fileContent0 = fileContent0.replace(ncc_key, ncc_value);


				// 写入文件
				FileWriter writer = new FileWriter(pathNewNCC);
				writer.write(fileContent0);
				writer.close();
				logger.debug("File saved: " + pathNewNCC);


				/*
				 * 会計　ZIP下载
				 */

				File file = new File(path_ETAX_output);
				if (file.exists()) {

					/*
					 * 生成文件ZIP
					 */
					// 源文件夹的路径
					String sourceFolderPath = path_ETAX_output;
					// 目标ZIP文件的路径
					String targetZipFilePath = path_ETAX_output + ".zip";
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

						out.print("{\"res\":\"ZIP生成错误\"}");
						logger.debug("end " + hidden_key);
						return;
					}

				}


				out.print("{\"res\":\"" + (fileName + ".zip").replace("\\", "\\\\")+ "\"}");
				logger.debug("end " + hidden_key);
				return;

			} catch (Throwable e) {
				e.printStackTrace();
				out.print("{\"res\":\"ZIP文件不存在\"}");
				logger.debug("end " + hidden_key);
				return;
			}


		} else if ("button-search".equals(hidden_key)) {


			try {

				String key = req.getParameter("key");
				String value = req.getParameter("value");
				if ("InvoiceBangou".toLowerCase().equals(key.toLowerCase())) {
					value = "T" + value;
				}

				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
				t_etax_account_infoExBean t_etax_account_infoExBean = new t_etax_account_infoExBean();
				t_etax_account_infoExBean = t_etax_account_infoDao.SelectExKeyValue(key, value);
				// 将Java对象转换为JSON字符串

				// 创建ObjectMapper对象
				ObjectMapper objectMapper = new ObjectMapper();
				String jsonString = objectMapper.writeValueAsString(t_etax_account_infoExBean);

				out.print(jsonString);

			} catch (Exception e) {
				e.printStackTrace();
			}



			logger.debug("end " + hidden_key);
			return;


		} else {
			try {

//
//
//
//				t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
//				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//				EtaxDao EtaxDao = new EtaxDao();
//
//				Map<String, String[]> HashMapParameterMap = req.getParameterMap();
//				for (String key : HashMapParameterMap.keySet()) {
//					if ("license".equals(key) == true && HashMapParameterMap.size() > 1) {
//						continue;
//					}
//
//					if ("delete".equals(key)) {
//						yyyymmdd_count = HashMapParameterMap.get(key)[0].toString();
//						if (t_etax_account_infoDao.DELETE(User_infoBean, yyyymmdd_count) > 0 ) {
//							EtaxDao.DELETE(yyyymmdd_count);
//							t_etax_account_resDao.DELETE_res(yyyymmdd_count);
//
//						}
//
//						out.print("{\"res\":\"ok\"}");
//						logger.info("end");
//						return;
//
//					} else if ("Syouninn".equals(key)) {
//
//						try {
//							String syouninn_status = HashMapParameterMap.get("Syouninn_status")[0];
//							//TODO
//							yyyymmdd_count = HashMapParameterMap.get(key)[0];
//
//							t_etax_account_infoDao.Update_syouninn_status(yyyymmdd_count, syouninn_status);
//
//							if ("承認有".equals(syouninn_status)) {
//								try {
//									t_etax_account_infoBean EtaxAccountInfoBean = FuncUtils.exe_activation(yyyymmdd_count, t_etax_account_infoDao,
//											t_etax_account_resDao, EtaxDao, "");
//									t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活完了");
//
//								} catch (SQLException e) {
//									t_etax_account_infoDao.Update_activation_code(yyyymmdd_count, "激活失败");
//
//								}
//							} else {
//								t_etax_account_resDao.DELETE_res(yyyymmdd_count);
//								EtaxDao.DELETE(yyyymmdd_count);
//
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//
//						out.print("{\"res\":\"ok\"}");
//						logger.info("end");
//						return;
//
//					}
//				}
//
//				LinkedHashMap<String, User_infoBean> HashMapGroup_id_user_id = new LinkedHashMap<String, User_infoBean>();
//				if (StringUtils.isEmpty(User_infoBean.getGroup_id()) == false) {
//					User_infoDao User_infoDao = new User_infoDao();
//					HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(User_infoBean.getGroup_id());
//					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);
//
//				} else if ("admin".equals(User_infoBean.getPermissions())) {
//					User_infoDao User_infoDao = new User_infoDao();
//					HashMapGroup_id_user_id = User_infoDao.selectByGroup_id(null);
//					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);
//
//				} else {
//					HashMapGroup_id_user_id.put(User_infoBean.getUser_id(), User_infoBean);
//					User_infoBean.setGroup_id_user_id(HashMapGroup_id_user_id);
//
//				}
//				LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = new LinkedHashMap<>();
////				LinkedHashMap<String, t_etax_account_infoExBean> LinkedHashMap_t_etax_account_infoExBean = t_etax_account_infoDao.selectAll(User_infoBean, maxNo, yyyy);
//				session.setAttribute("LinkedHashMapt_etax_account_infoBean", LinkedHashMap_t_etax_account_infoExBean);
//
//				//重复数据检查
//				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanBKCompanyName_Chinese = new LinkedHashMap<String, t_etax_account_resBean>();
//				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_Chinese = new LinkedHashMap<String, t_etax_account_resBean>();
//				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanBKCompanyName_English = new LinkedHashMap<String, t_etax_account_resBean>();
//				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErrCompanyName_English = new LinkedHashMap<String, t_etax_account_resBean>();
////				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBeanErr = new LinkedHashMap<String, t_etax_account_resBean>();
//				for (t_etax_account_infoBean t_etax_account_infoBean : LinkedHashMap_t_etax_account_infoExBean.values()) {
//					String CompanyName_Chinese = t_etax_account_infoBean.getCompanyName_Chinese();
//					if (LinkedHashMapEtaxBeanBKCompanyName_Chinese.containsKey(CompanyName_Chinese) == true) {
//						LinkedHashMapEtaxBeanErrCompanyName_Chinese.put(CompanyName_Chinese, null);
//					} else {
//						LinkedHashMapEtaxBeanBKCompanyName_Chinese.put(CompanyName_Chinese, null);
//					}
//
//					String CompanyName_English = t_etax_account_infoBean.getCompanyName_English();
//
//					if (LinkedHashMapEtaxBeanBKCompanyName_English.containsKey(CompanyName_English) == true) {
//						LinkedHashMapEtaxBeanErrCompanyName_English.put(CompanyName_English, null);
//					} else {
//						LinkedHashMapEtaxBeanBKCompanyName_English.put(CompanyName_English, null);
//					}
//				}
//
//				session.setAttribute("LinkedHashMapEtaxBeanErrCompanyName_Chinese", LinkedHashMapEtaxBeanErrCompanyName_Chinese);
//				session.setAttribute("LinkedHashMapEtaxBeanErrCompanyName_English", LinkedHashMapEtaxBeanErrCompanyName_English);
//
//
//				if ("admin".equals(User_infoBean.getPermissions()) || "groupAdmin".equals(User_infoBean.getPermissions())) {
//					LinkedHashMap<String, LinkedHashMap<String, String>> LinkedHashMapTongji = t_etax_account_infoDao.selectTongji(User_infoBean);
//					session.setAttribute("LinkedHashMapTongji", LinkedHashMapTongji);
//				}
//


				req.getRequestDispatcher("/setXiaofeishuiShengaoCHengnuoshu.jsp?fromBackend=true").forward(req, resp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		logger.debug("end");

		return;

	}



	public static void main(String[] args) {

	}



}