package com.panda.batch;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.bean.JCTToukeiBean;
import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_account_resBean;
import com.panda.dao.JCTDao;
import com.panda.dao.JCTToukeiDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.utils.FuncUtils;
public class SearchInvoice {

	private static Logger logger = Logger.getLogger(SearchInvoice.class.toString());


	public static void main(String[] args) throws Exception {
		logger.debug("START");

		System.setProperty("https.protocols", "TLSv1.2");




//		getCsv("2022-10-21");
//		getCsv("2022-10-22");
//		getCsv("2022-10-23");
//		getCsv("2022-10-24");
//		getCsv("2022-10-25");
//		getCsv("2022-10-26");
//		getCsv("2022-10-27");



		//海外公司专用数据导入
		if(args.length > 0) {
			if("t_jct_overseas".equals(args[0])) {
				JCTDao JCTDao = new JCTDao();
				JCTDao.delete_t_jct_overseas();
				JCTDao.INSERT_t_jct_overseas();
				logger.debug("END t_jct_overseas");
				return;
			} else if ("api.houjin-bangou.nta.go.jp".equals(args[0])) {

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				LinkedHashMap<String, t_etax_account_resBean> t_etax_account_resLinkedHashMap= t_etax_account_resDao.selectHoujinBangou();

				for (String yyyymmdd_count : t_etax_account_resLinkedHashMap.keySet()) {
					t_etax_account_resBean EtaxBean = t_etax_account_resLinkedHashMap.get(yyyymmdd_count);
					t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetHoujinBangouByHoujinName(EtaxBean.getgHojinmei());
					String HoujinBangou = t_etax_account_infoExBean.getHoujinBangou();

					if (StringUtils.isEmpty(HoujinBangou)) {
						continue;
					}
					t_etax_account_resDao.Update_res_HoujinBangou(yyyymmdd_count, HoujinBangou);
				}


				//个人的法人番号全部设置成-
				t_etax_account_resDao.Update_HoujinBangou_geren_all();

				logger.debug("END api.houjin-bangou.nta.go.jp");
				return;


			} else if ("t_jct_toukei_image".equals(args[0])) {

				ArrayList<JCTToukeiBean> maListJCT = new ArrayList<JCTToukeiBean>();
				JCTToukeiDao JCTToukeiDao = new JCTToukeiDao();

				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				LinkedHashMap<String, t_etax_account_resBean> LinkedHashMapEtaxBean = new LinkedHashMap<String, t_etax_account_resBean>();
				LinkedHashMapEtaxBean = t_etax_account_resDao.selectInvoiceBangouKey();

				maListJCT = JCTToukeiDao.select(LinkedHashMapEtaxBean, "1");

			    String text = "Hello, World!"; // 要转换为图片的字符串
			    text= maListJCT.get(0).getBikou2();
			    text =text.replaceAll("<BR>", "\r\n");
//			    String imagePath = "C:\\Users\\Administrator\\Desktop\\output.png";
			    String imagePath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/output/t_jct_toukei.png";

		        Font font = new Font("UTF-8", Font.BOLD, 30);
		        BufferedImage image = FuncUtils.createImageFromText(text, font);
		        ImageIO.write(image, "PNG", new File(imagePath));

				logger.debug(text);





//				String namedata = req.getParameter("namedata");
//				String mailarea = req.getParameter("mailarea");
//				String notification = req.getParameter("notification");
//				String value = req.getParameter("value");
//				String no = req.getParameter("no");
//				String textboxdata = req.getParameter("textboxdata");
//
//
//
//
//				String title = "【盼达商务服务】感谢您的咨询"
//						+ "";
//
//
//
//				 textboxdata = namedata
//					 		+ "<br>"
//					 		+ "<br>您好。"
//					 		+ "<br>"
//					 		+ "<br>盼达商务服务已经收到您的如下咨询。"
//					 		+ "<br>****************"
//							+ "<br>[姓名]" + namedata + ""
//							+ "<br>[联系方式]" + notification + ""
//							+ "<br>[咨询内容]" + value + ""
//							+ "<br>[案件No]" + no + ""
//					 		+ "<br>[正文]<br>" + textboxdata+""
//					 		+ "<br>****************"
//					 		+ "<br>"
//					 		+ "<br>我们将尽快与您联系。"
//					 		+ "<br>感谢您对盼达商务服务的信赖与支持。"
//					 		+ "<br>"
//					 		+ "<br>盼达商务服务"
//					 		+ "<br>日本東京都文京区千石４丁目１４番９号１階"
//					 		+ "<br>www.pandaservicejapan.com"
//						+ "";
//
//
//				SendMail SendMail = new SendMail();
//				SendMail.sendMessage("info@pandaservicejapan.com", mailarea, title, textboxdata);


				logger.debug("END t_jct_toukei_SendMail");

				return;


			} else if ("t_jct_toukei".equals(args[0])) {

				try {
					JCTToukeiDao JCTToukeiDao = new JCTToukeiDao();

					// 日期格式化
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

					// 起始日期
					Date d1;
					if (args.length == 3) {
						d1 = sdf.parse(args[2]);
					} else {
						d1 = sdf.parse(sdf.format(new Date()));
					}

					// 结束日期
					Date d2 = sdf.parse(sdf.format(new Date()));

//					// 起始日期
//					d1 = sdf.parse("20230517");
//					// 结束日期
//					Date d2 = sdf.parse("2022-11-07");

					Calendar dd1 = Calendar.getInstance();
					dd1.setTime(d1);
					// 天数加-1
					dd1.add(Calendar.DAY_OF_MONTH, -6);
					d1 = dd1.getTime();

					Calendar dd2 = Calendar.getInstance();
					dd2 = Calendar.getInstance();
					dd2.setTime(d2);
					// 天数加-1
					dd2.add(Calendar.DAY_OF_MONTH, -1);
					d2 = dd2.getTime();

					logger.debug(sdf.format(d1));
					logger.debug(sdf.format(d2));

					// 打印2018年2月25日到2018年3月5日的日期
					while (d1.getTime() < d2.getTime()) {
						d1 = dd1.getTime();
						logger.debug(sdf.format(d1));

				        String str = sdf.format(d1);

				        //diff_20230518.csv
				        String file_name = "diff_"+str+".csv";
//				        file_name= "diff_20230518.csv";

				        Path path = Paths.get(args[1] + "//" + file_name);

			        	int add_count=0;
			        	int del_count=0;
			        	int update_count=0;

			        	int riben_gongsi=0;
			        	int waiguo_gongsi_banshichu_you=0;
			        	int waiguo_gongsi_banshichu_wu=0;

			        	int riben_gere=0;
			        	int waiguo_gere_banshichu_you=0;
			        	int waiguo_gere_banshichu_wu=0;

						if (Files.exists(path)) {
							logger.debug(file_name + "存在します");

				        	ArrayList<String> list = new ArrayList<>();
				            StringBuilder sb = new StringBuilder();

				            // CSVファイルの読み込み
				            List<String> lines = Files.readAllLines(path, Charset.forName("UTF-8"));
				            for (int i = 1; i < lines.size(); i++) {
				                String[] data = lines.get(i).split(",");
			                	int j = 0;
//				                	一連番号0
//				                	登録番号1
//				                	事業者処理区分2	01新規	02公表内容の変更	03登録の失効	04登録の取消	99削除
//				                	訂正区分3	0訂正以外	1訂正	空文字削除
//				                	人格区分4	1個人	2法人
//				                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
//				                	最新履歴6
//				                	登録年月日7
//				                	更新年月日8
//				                	取消年月日9
//				                	失効年月日10
//				                	本店又は主たる事務所の所在地（法人）11

//			                	事業者処理区分2	01新規	02公表内容の変更	03登録の失効	04登録の取消	99削除
			                	if ("01".equals(data[2])) {
			                		++add_count;//?

//				                	人格区分4	1個人	2法人
				                	if ("2".equals(data[4])) {

//					                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
					                	if ("1".equals(data[5])) {
				//	                		logger.debug(lines.get(i));
					                		++riben_gongsi;//?

//						                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
					                	} else  if ("3".equals(data[5])) {// && StringUtils.isEmpty(data[11].replaceAll("\"", "")) == true
					                		logger.debug(lines.get(i));
					                		++waiguo_gongsi_banshichu_you;//OK

//						                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
					                	} else  if ("2".equals(data[5])) {// && StringUtils.isEmpty(data[11].replaceAll("\"", "")) == false
					                		logger.debug(lines.get(i));
					                		++waiguo_gongsi_banshichu_wu;//OK
					                		list.add(data[18]);
							                sb.append(data[1] + "," + data[18] + "<BR>");//.replace("\"", "")
					                	}
				                	}

//				                	人格区分4	1個人	2法人
//				                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
				                	if ("1".equals(data[4]) && "1".equals(data[5])) {
				                		++riben_gere;//OK
				                	}


//				                	人格区分4	1個人	2法人
//				                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
				                	if ("1".equals(data[4]) && !"1".equals(data[5])) {
//					                	国内外区分5	1国内事業者	2特定国外事業者	3特定国外事業者以外の国外事業者
				                		if ("3".equals(data[5])) {
//					                		logger.debug(lines.get(i));
					                		++waiguo_gere_banshichu_you;//OK
					                	} else  if ("2".equals(data[5])) {
//					                		logger.debug(lines.get(i));
					                		++waiguo_gere_banshichu_wu;//OK
					                	}
				                	}

//				                	事業者処理区分2	01新規	02公表内容の変更	03登録の失効	04登録の取消	99削除
			                	} else if ("99".equals(data[2])) {
			                		++del_count;//OK
			                	}

//			                	訂正区分3	0訂正以外	1訂正	空文字削除
			                	if ("1".equals(data[3])) {
			                		++update_count;//OK
			                	}


				            }

							JCTToukeiBean JCTToukeiBean = new JCTToukeiBean();

							JCTToukeiBean.setDataFileName(file_name);

							JCTToukeiBean.setAdd_count(add_count);
							JCTToukeiBean.setUpdate_count(update_count);
							JCTToukeiBean.setDel_count(del_count);

							JCTToukeiBean.setRiben_gongsi(riben_gongsi);
							JCTToukeiBean.setWaiguo_gongsi_banshichu_you(waiguo_gongsi_banshichu_you);
							JCTToukeiBean.setWaiguo_gongsi_banshichu_wu(waiguo_gongsi_banshichu_wu);

							JCTToukeiBean.setRiben_gere(riben_gere);
							JCTToukeiBean.setWaiguo_gere_banshichu_you(waiguo_gere_banshichu_you);
							JCTToukeiBean.setWaiguo_gere_banshichu_wu(waiguo_gere_banshichu_wu);

							JCTToukeiBean.setWaiguo_gongsi_banshichu_wu_list(sb.toString());

							JCTToukeiDao.add(JCTToukeiBean);

						} else {
							logger.debug(path + "存在しません");

						}

						// 天数加上1
						dd1.add(Calendar.DAY_OF_MONTH, 1);

					}
				} catch (ParseException e) {
					e.printStackTrace();
				}

				logger.debug("END t_jct_toukei");
				return;

			} else if ("web-api.invoice-kohyo.nta.go.jp".equals(args[0])) {
				JCTDao JCTDao = new JCTDao();
				t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
				LinkedHashMap<String, t_etax_account_resBean> t_etax_account_resLinkedHashMap= t_etax_account_resDao.selectInvoiceBangou();

				for (String yyyymmdd_count : t_etax_account_resLinkedHashMap.keySet()) {
					t_etax_account_resBean EtaxBean = t_etax_account_resLinkedHashMap.get(yyyymmdd_count);
					String HoujinBangou = EtaxBean.getHoujinBangou();
					String InvoiceBangou = "";
					if (StringUtils.isEmpty(HoujinBangou)) {
						if (StringUtils.isEmpty(EtaxBean.getgHojinmei())) {
							InvoiceBangou = "NG";
						} else {
							InvoiceBangou = JCTDao.selectName(EtaxBean.getgHojinmei());
						}

					} else if ("NG".equals(HoujinBangou)) {
						continue;

					} else {
						t_etax_account_infoExBean t_etax_account_infoExBean = FuncUtils.sendGetInvoiceBangou("T" + HoujinBangou);
						InvoiceBangou = t_etax_account_infoExBean.getInvoiceBangou();

					}

					if (StringUtils.isEmpty(InvoiceBangou)) {
						// 何もしない
					} else {
						t_etax_account_resDao.Update_res_InvoiceBangou(yyyymmdd_count, InvoiceBangou);

					}
				}

				logger.debug("END web-api.invoice-kohyo.nta.go.jp");
				return;





			}

		}

		JCTDao JCTDao = new JCTDao();
		String updateDateMax = JCTDao.selectUpdateDateMax();
		if (StringUtils.isEmpty(updateDateMax) == true ) {
			updateDateMax = "2021-10-01";
		}

		//API取得的数据有延迟，删除最大日期的当天数据
//		JCTDao.deleteUpdateDateMax(updateDateMax);
//		updateDateMax = JCTDao.selectUpdateDateMax();
//		if (StringUtils.isEmpty(updateDateMax) == true ) {
//			updateDateMax = "2021-10-01";
//		}
//		logger.debug("updateDateMax " + updateDateMax);


		// 日期格式化
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			// 起始日期
			Date d1 = sdf.parse(updateDateMax);
			// 结束日期
			Date d2 = sdf.parse(sdf.format(new Date()));

//			//API取得的数据有延迟，-1天
//			dd.setTime(d2);
//			// 天数加-1
//			dd.add(Calendar.DAY_OF_MONTH, -1);
//			d2 = dd.getTime();

//			// 起始日期
//			Date d1 = sdf.parse("2022-09-22");
//			// 结束日期
//			Date d2 = sdf.parse("2022-11-07");

			Date tmp = d1;
			Calendar dd = Calendar.getInstance();
			dd.setTime(d1);
			// 天数加上1
			dd.add(Calendar.DAY_OF_MONTH, 1);


			// 打印2018年2月25日到2018年3月5日的日期
			while (tmp.getTime() < d2.getTime()) {
				tmp = dd.getTime();
				logger.debug(sdf.format(tmp));

				getCsv(sdf.format(tmp));

				// 天数加上1
				dd.add(Calendar.DAY_OF_MONTH, 1);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}


	private static void getCsv(String date) {
		try {

			int i = 1;
			int num = 0;
			do {
				FileWriter fw = null;
				//如果文件存在，则追加内容；如果文件不存在，则创建文件
				//diff_20220729.csv
				File f = new File("C:\\Users\\Administrator\\Documents\\API_diff_" + date.replace("-", "") + ".csv");
//				File f = new File("/home/batch/temp/API_diff_" + date.replace("-", "") + ".csv");
				fw = new FileWriter(f, true);
				PrintWriter pw = new PrintWriter(fw);

				String[] line1 = sendGet(i, pw, date);

				fw.flush();
				pw.close();
				fw.close();

				num = Integer.parseInt(line1[3]);
				i++;
			} while (i <= num);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String[] sendGet(int i, PrintWriter pw, String date) throws Exception {

		String[] line1 = new String[0];
		URL url;
		try {

			String url_s = "https://web-api.invoice-kohyo.nta.go.jp/1/diff?id=Kp9FL6TRHTUbj&from=" + date + "&to="
					+ date
					+ "&type=01&divide="
					+ i;
			url = new URL(url_s);
			URLConnection conn;
			conn = url.openConnection();
			InputStream in = conn.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line;
			int count = 1;

			while ((line = br.readLine()) != null) {

				if (count == 1) {
					//2022-10-13,8742,1,18
					line1 = line.split(",");
					logger.debug(line);

				} else {
					// 写入信息
					pw.println(line);
					pw.flush();
				}

				count++;
			}
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line1;

	}

}
