package com.panda.batch;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.panda.bean.t_etax_account_infoBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.utils.FuncUtils;

/*
 * 消费税申告生成
 */
public class zhuandailiIO {

	private static Logger logger = Logger.getLogger(zhuandailiIO.class.toString());

//	static String chuli_type = "申告";
//	chuli_type = "转代理";

	public static void main(String[] args) {

		logger.info("start");

		try {

			// 获取当前日期时间
			Date currentDate = new Date();
			// 设置日期时间格式
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
			// 格式化日期时间
			String yyyymmddhhmmss = dateFormat.format(currentDate);

			//アップロードするフォルダ
			String sourceFilePath = "C:\\Users\\Administrator\\Desktop\\転代理";

			String ByLike_yyyymmdd_count = "";
			 ByLike_yyyymmdd_count = "2024041398";
			 ByLike_yyyymmdd_count = "2024041798";
			 ByLike_yyyymmdd_count = "2024042298";
//			 ByLike_yyyymmdd_count = "2024050898";
//			 ByLike_yyyymmdd_count = "2024051398";

			 ByLike_yyyymmdd_count = "2024052099";
			 ByLike_yyyymmdd_count = "2024052799";
			 ByLike_yyyymmdd_count = "20240604990001";



//			t_nashui_guanliren_infoDao t_nashui_guanliren_infoDao = new t_nashui_guanliren_infoDao();
//			LinkedHashMap<String, t_nashui_guanliren_infoBean> LinkedHashMap_t_nashui_guanliren_infoBean = t_nashui_guanliren_infoDao.selectAll();

//			for (Entry<String, t_nashui_guanliren_infoBean> entry0 : LinkedHashMap_t_nashui_guanliren_infoBean.entrySet()) {
//			    String key0 = entry0.getKey();
//			    t_nashui_guanliren_infoBean value0 = entry0.getValue();
//
//				// 将 EtaxAccountInfoBean 对象转换为 JSON 字符串
//				Gson gson = new Gson();
//			    String json = gson.toJson(value0);
//			    logger.info("Key: " + key0 + ", Value: " + json.toString());
//
//
//
//			}



			get_zhuandaili_ncc(sourceFilePath, yyyymmddhhmmss, ByLike_yyyymmdd_count);
//			get_zhuandaili_jieguo(sourceFilePath, t_etax_account_infoDao, LinkedHashMap_t_etax_account_infoBean);

		} catch (Throwable e) {
			e.printStackTrace();
			return;
		}

		logger.info("end");
		return;

	}



	public void get_zhuandaili_jieguo(String sourceFilePath, String ByLike_yyyymmdd_count,String chuli_type) throws IOException {

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectAll_ByLike_yyyymmdd_count(ByLike_yyyymmdd_count);


		int count = 0;
		for (Entry<String, t_etax_account_infoBean> entry : LinkedHashMap_t_etax_account_infoBean.entrySet()) {
			++count;
			logger.info("处理个数 : " + count);
			String yyyymmdd_count = entry.getKey();
			t_etax_account_infoBean t_etax_account_infoBean = entry.getValue();


			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);


			t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
			t_etax_jieguoExBean t_etax_jieguoExBean = t_etax_jieguoDao.select_jietuo_by_bangou(t_etax_account_infoExBean.getBangou(), chuli_type);

			String destinationFilePath = sourceFilePath;
//				String destinationFilePath = sourceFilePath + "\\" + t_etax_account_infoBean.getCompanyName_Chinese();
//				File directory = new File(destinationFilePath);
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}

			/*
			 * html
			 */

			String pathNewNCC = destinationFilePath + "\\" + t_etax_account_infoExBean.getCompanyName_Chinese() + ".html";

			// 写入文件
			FileWriter writer = new FileWriter(pathNewNCC);
			writer.write(t_etax_jieguoExBean.getHtml());
			writer.close();
			logger.debug("File saved: " + pathNewNCC);

		}
	}



	public static void get_zhuandaili_ncc(String sourceFilePath, String yyyymmddhhmmss, String ByLike_yyyymmdd_count) throws IOException {
		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
		LinkedHashMap<String, t_etax_account_infoBean> LinkedHashMap_t_etax_account_infoBean = t_etax_account_infoDao.selectAll_ByLike_yyyymmdd_count(ByLike_yyyymmdd_count);

		String pathNCC = sourceFilePath+"/moban/転代理新テンプレート.ncc";
		String pathNCC個人 = sourceFilePath+"/moban/転代理新テンプレート個人.ncc";
		File dataModelFileNCC = new File(pathNCC);
		if (dataModelFileNCC.length() == 0) {
			logger.debug(pathNCC + " → NG:ncc File data model ");
			return;
		}

		int count = 0;
		for (Entry<String, t_etax_account_infoBean> entry : LinkedHashMap_t_etax_account_infoBean.entrySet()) {
			++count;
			logger.info("处理个数 : " + count);
			String yyyymmdd_count = entry.getKey();
//			t_etax_account_infoBean t_etax_account_infoBean = entry.getValue();

			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

			String destinationFilePath = sourceFilePath + "/output/" + yyyymmddhhmmss;
			File directory = new File(destinationFilePath);
			directory.mkdir();

//				String destinationFilePath = sourceFilePath + "\\" + t_etax_account_infoBean.getCompanyName_Chinese();
//				File directory = new File(destinationFilePath);
//				if (!directory.exists() || !directory.isDirectory()) {
//					directory.mkdirs();
//				}

			/*
			 * ncc
			 */

			String 事業者名25全角 = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_English(), 25);

			String key = "";
			String value = "";

			if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
				dataModelFileNCC = new File(pathNCC個人);
				if (dataModelFileNCC.length() == 0) {
					logger.debug(pathNCC + " → NG:ncc File data model ");
					return;
				}
			}
			String fileContent = FuncUtils.readFileContent(dataModelFileNCC);

//				#登録番号#
			key = "#登録番号#";
			value = t_etax_account_infoExBean.getInvoiceBangou();
			fileContent = fileContent.replace(key, value);

//				t_nashui_guanliren_infoBean t_nashui_guanliren_infoBean = LinkedHashMap_t_nashui_guanliren_infoBean.get(t_etax_account_infoExBean.getNashui_guanliren());
//				#解任納税管理人会社名#
//				#解任納税管理人代表者#
//				#解任納税管理人住所#
//				#解任納税地#
//				#解任税務署#
//				#解任税務署番号#
			key = "#解任納税管理人会社名#";
			value = t_etax_account_infoExBean.getNashui_CompanyName();
			fileContent = fileContent.replace(key, value);

			key = "#解任納税管理人代表者#";
			value = t_etax_account_infoExBean.getNashui_DaibiaoName();
			fileContent = fileContent.replace(key, value);

			key = "#解任納税管理人住所#";
			value = t_etax_account_infoExBean.getNashui_address();
			fileContent = fileContent.replace(key, value);

			key = "#解任納税地#";
			value = t_etax_account_infoExBean.getNashui_di_biangengqian();
			fileContent = fileContent.replace(key, value);

			key = "#解任税務署#";
			value = t_etax_account_infoExBean.getNashui_shuiwushu_biangengqian();
			fileContent = fileContent.replace(key, value);

			key = "#解任税務署番号#";
			value = t_etax_account_infoExBean.getNashui_shuiwushu_fanhao();
			fileContent = fileContent.replace(key, value);


//				#事業者名25全角#
			key = "#事業者名25全角#";
			value = 事業者名25全角;
			fileContent = fileContent.replace(key, value);


//				#事業者名カナ25全角#
			key = "#事業者名カナ25全角#";
			value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getCompanyName_pianjiaming(), 25);
			fileContent = fileContent.replace(key, value);

//				#海外本店25全角#
			key = "#海外本店25全角#";
			value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getAddress_English(), 25);
			fileContent = fileContent.replace(key, value);

//				#代表者名25全角#
			key = "#代表者名25全角#";
			value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getDaibiaoName_English(), 25);
			fileContent = fileContent.replace(key, value);

//				#代表者名カナ25全角#
			key = "#代表者名カナ25全角#";
			value = FuncUtils.toFullWidthAndTruncate(t_etax_account_infoExBean.getDaibiaoName_pianjiaming(), 25);
			fileContent = fileContent.replace(key, value);

//				#法人番号#
			key = "#法人番号#";
			value = "";
			if ("公司".equals(t_etax_account_infoExBean.getUser_type())) {
				value = t_etax_account_infoExBean.getHoujinBangou();
			}
			fileContent = fileContent.replace(key, value);


//				#利用者識別番号#
			key = "#利用者識別番号#";
			value = t_etax_account_infoExBean.getBangou();
			fileContent = fileContent.replace(key, value);

			String pathNewNCC = destinationFilePath + "/" + 事業者名25全角 + ".ncc";

			// 写入文件
			FileWriter writer = new FileWriter(pathNewNCC);
			writer.write(fileContent);
			writer.close();
			logger.debug("File saved: " + pathNewNCC);

		}
	}



}
