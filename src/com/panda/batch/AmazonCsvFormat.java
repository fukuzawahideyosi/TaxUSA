package com.panda.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.panda.utils.FuncUtils;

/*
 * 销售额数据抽出和计算
 */
public class AmazonCsvFormat {

	private static Logger logger = Logger.getLogger(AmazonCsvFormat.class.toString());

	public static void main(String[] args) {
		try {



//			Map<String, String> bugCSV = new LinkedHashMap<String, String>();
//			Map<String, String> bugCSVHistory = new LinkedHashMap<String, String>();
//			Map<String, String> bugCSVHistory_M = new LinkedHashMap<String, String>();

////			[bugCSV]
////					0204オウテキ処理　PDSK142-326
//					bugCSVHistory.put("Botaojiaye (Beijing) Sanitary Ware Co., Ltd   2023 Amazon Transaction report.csv", "");
//					bugCSVHistory.put("guangzhou keyuan shengwukeji youxiangongsi   2023 Amazon Transaction report.csv", "");
//					bugCSVHistory.put("hebeiruiguanguojimaoyiyouxiangongsi   2023 Amazon Transaction report.csv", "");
//					bugCSVHistory.put("Jiaxing Sai en Trading Co.,Ltd 2023 Amazon Transaction report .csv", "");
//					bugCSVHistory.put("Nan Yang Jia Bei Shang Mao  2023 Amazon Transaction report .csv", "");
//					bugCSVHistory.put("Shenzhen Hope Ecommerce Co.LTD  2023 Amazon Transaction report.csv", "");
//
//
////					0206オウテキ処理　PDSK327-385
//					bugCSVHistory.put("dongguanshitaijingyundianzikejiyouxiangongsi   2023 Amazon Transaction report.csv", "");
//					bugCSVHistory.put("dongguanzhenyidakejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
//					bugCSVHistory.put("He Haifang 2023 Amazon Transaction report .csv", "");
//					bugCSVHistory.put("Wang Xiaoan 2023 Amazon Transaction report .csv", "");
//
//
////					追加1个
//					bugCSVHistory.put("Yiwushizuojundianzishangwuyouxiangongsi 2023 Amazon Transaction report.csv", "");


//			[bugCSVHistory]
//			bugCSVHistory.put("DONGGUAN XINRUI KEJI YOUXIANGONGSI 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("dongguanshijieshidaqingjieyongpinyouxiangongsi  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("dongguanshitaijingyundianzikejiyouxiangongsi   2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Chen Meiliang 2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("fujianshenglongyanshiluolingmaoyiyouxianzerengongsi  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("guangming li  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Guangzhou Bison Cloting Co.,Ltd  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("He Haifang 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("henankuaishouyunkejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("HONGKONG UMEDIA LIMITED  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Huizhou Zhongzhan Technology Co., Ltd.  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Jiangsu Zhenchang Educational Equipment Co., Ltd  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Jiaxing Sai en Trading Co.,Ltd 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("JiaXingAiQuHuWaiYongPinYouXianGongSi   2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("kun shan shu lan xin xi ke ji you xian gong si  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("LiaochengChaoxingyitengdianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("LONGYAN SHI WANRUI NONGYE FAZHANG YOU XIAN GONG SI  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("ONEKEY LIMITED  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("pujiangxianjinchaidianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("pujiangxianliushicuidianzishangwuyouxiangongsi   2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("pujiangxianrongxingdianzishangwuyouxiangongsi  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Quanzhou Hanyu Trading Co. LTD  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Ruian LIHER Trading Co., Ltd.  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shen Zhen Ding Jiang Ke Ji You Xian Gong Si   2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shen Zhen Shi Mo Deng Ke Ji You Xian Gong Si  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shenzhen Bullfrog Technology Co., Ltd   2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Shenzhen Fast sales ecommerce Co.,Ltd.  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shenzhen Hope Ecommerce Co.LTD  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Shenzhen Luerman Maoyi Youxian Gongsi 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shenzhen Qidacheng Trading Co., Ltd.  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Shenzhen Xingyiheng Technology Co., Ltd.  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shenzhen Youmingda Technology Co., Ltd  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Shenzhen Zhuoshuo technology service co.ltd  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("shenzhenfangcaoyiyimaoyiyouxiangongsi  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("shenzhenshikaershangwudianzikejiyouxiangongsi   2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("shenzhenshishengyitangkejiyouxiangongsi  2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("shenzhishishizhirankejiyouxiangongsi  2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Simpeak Technology Co.,Limited 2023 Amazon Transaction report  .csv", "");
//			bugCSVHistory.put("Wang Xiaoan 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("Xiamen Yong Zheng Feng Trade Co., Ltd 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("YiWuShiRuiRanMaoYiYouXianGongSi 2023 Amazon Transaction report .csv", "");
//			bugCSVHistory.put("yuju home textile natong co., LTD  2023 Amazon Transaction report  .csv", "");
//			bugCSVHistory.put("zhijiangshiguanghuoshangmaoyouxiangongsi 2023 Amazon Transaction report.csv", "");
//			bugCSVHistory.put("Zyurong Development Limited 2023 Amazon Transaction report .csv", "");




			String path = "C:\\Users\\Administrator\\Desktop\\消費税申告試算データ\\csv";
			/*
			 * 获取上传的多个文件部分
			 */
			File directory = new File(path);

			if (!directory.exists() || !directory.isDirectory()) {
				logger.debug("指定的路径不是一个有效的目录。");
				return;
			}

			File[] files = directory.listFiles();

			if (files == null || files.length == 0) {
				logger.debug("目录下没有文件。");
				return;
			}

			for (File file : files) {
				String fileName = file.getName();

				try {
					kuaiji_amazon_to_csv(path, file);
				} catch (Exception e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					FuncUtils.copyFile(path + "/" + fileName, path + "/csvERR/" + fileName);
					logger.debug("[NG]" + fileName);
				}












			}



			//TODO
//			for (Map.Entry<String, String> entry : bugCSV.entrySet()) {
//				String fileName = entry.getKey();
//				logger.debug("[bugCSV]" + fileName);
//			}
//			for (Map.Entry<String, String> entry : bugCSVHistory_M.entrySet()) {
//				String fileName = entry.getKey();
//				logger.debug("[bugCSVHistory_M]" + fileName);
//			}



		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}





	}

	public static void kuaiji_amazon_to_csv(String path, File file) throws Exception {
		if (file.isFile()) {
			String fileName = file.getName();
			String fileExtension = FuncUtils.getFileExtension(fileName);
			logger.debug(fileName);

			//TODO
//						[bugCSV*]
//					if (bugCSVHistory.containsKey(fileName)) {
//
//					} else {
//						continue;
//
//					}

			if (fileName.indexOf("計算用") > -1) {
				return;
			}

			if ("csv".equalsIgnoreCase(fileExtension)) {
				BufferedReader br = null;
				String line = "";
				String cvsSplitBy = "\",\""; // 逗号分隔符
				String cvsSplitBy1 = ","; // 逗号分隔符
				Double total6 = 0.0;
				Double total13 = 0.0;
				Double total14 = 0.0;
				Double total15 = 0.0;
				Double total16 = 0.0;
				Double total17 = 0.0;
				Double total18 = 0.0;
				Double total19 = 0.0;
				Double total20 = 0.0;
				Double total21 = 0.0;
				Double total22 = 0.0;
				Double total23 = 0.0;
				Double total24 = 0.0;
				Double total25 = 0.0;
				Double total26 = 0.0;
				Double total27 = 0.0;
				StringBuilder sb = new StringBuilder();

				try {

		            String charset = FuncUtils.detectCharset(file.getPath());
		            logger.debug("CSV 文件编码是: " + charset);

					FileInputStream fis = new FileInputStream(file);
//							InputStreamReader isr = new InputStreamReader(fis, Charset.forName("SJIS"));
					InputStreamReader isr = new InputStreamReader(fis, Charset.forName(charset));
					br = new BufferedReader(isr);

					while ((line = br.readLine()) != null) {

						//NG
//						         // 使用Apache Commons IO
//						            String content = new String(line.getBytes(charset), "Shift-JIS");



						// 去除双引号中的逗号
						//			            	line = removeCommasInsideQuotes(line);
						//							line.replaceAll("\\\",\\\"", "\\\"、\\\"");
						//							line.replaceAll(",", "");
						//							line.replaceAll( "\"、\"","\",\"");

						//			            	line = line.replaceAll("\"", "");

						// 使用逗号分隔符拆分每一行的数据

						//			                String[] data = line.split(cvsSplitBy);
						//			            	if (line.indexOf(cvsSplitBy) == -1) {
						//			            		data = line.split(cvsSplitBy1);
						//			            	}

						String[] data = FuncUtils.splitCSV(line);

						data[0] = data[0].replaceAll("\"", "");

						String startDateStr = "20231001";
						String endDateStr = "20231231";
						String dateTimeString = data[0];
						boolean isLastYear = FuncUtils.isLastYear(startDateStr, endDateStr, dateTimeString);
						//	1 删去表头行之上所有行，保留表头行
						//	表头行：即 【日付/時間  決済番号……】的这一行
						if ("日付/時間".equals(data[0])) {
							data[27] = data[27].replaceAll("\"", "");
							sb.append("\"" + String.join("\",\"", data) + "\"").append("\r\n");
							continue;

							//	2 删去所需时间之外的所有行（除表头行）
							//	所需时间，比如2023/10/01~2023/12/31 这种
						} else if (isLastYear == true) {

						} else {
							continue;

						}

						//							logger.debug(line);
						data[27] = data[27].replaceAll("\"", "");

						//	3 保留C列为【注文】或【返金】的行，其他行删去（除表头行）
						if ("注文".equals(data[2]) || "返金".equals(data[2])) {

						} else {
							continue;

						}

						data[6] = StringUtils.isEmpty(data[6]) ? "0" : data[6].replaceAll(",", "");
						data[13] = StringUtils.isEmpty(data[13]) ? "0" : data[13].replaceAll(",", "");
						data[14] = StringUtils.isEmpty(data[14]) ? "0" : data[14].replaceAll(",", "");
						data[15] = StringUtils.isEmpty(data[15]) ? "0" : data[15].replaceAll(",", "");
						data[16] = StringUtils.isEmpty(data[16]) ? "0" : data[16].replaceAll(",", "");
						data[17] = StringUtils.isEmpty(data[17]) ? "0" : data[17].replaceAll(",", "");
						data[18] = StringUtils.isEmpty(data[18]) ? "0" : data[18].replaceAll(",", "");
						data[19] = StringUtils.isEmpty(data[19]) ? "0" : data[19].replaceAll(",", "");
						data[20] = StringUtils.isEmpty(data[20]) ? "0" : data[20].replaceAll(",", "");
						data[21] = StringUtils.isEmpty(data[21]) ? "0" : data[21].replaceAll(",", "");
						data[22] = StringUtils.isEmpty(data[22]) ? "0" : data[22].replaceAll(",", "");
						data[23] = StringUtils.isEmpty(data[23]) ? "0" : data[23].replaceAll(",", "");
						data[24] = StringUtils.isEmpty(data[24]) ? "0" : data[24].replaceAll(",", "");
						data[25] = StringUtils.isEmpty(data[25]) ? "0" : data[25].replaceAll(",", "");
						data[26] = StringUtils.isEmpty(data[26]) ? "0" : data[26].replaceAll(",", "");
						data[27] = StringUtils.isEmpty(data[27]) ? "0" : data[27].replaceAll(",", "");

						//	4 删去OQSV四列同时为0的行（除表头行）
						//	14		16		18			21
						if (Double.parseDouble(data[14]) == 0 && Double.parseDouble(data[16]) == 0
								&& Double.parseDouble(data[18]) == 0 && Double.parseDouble(data[21]) == 0) {
							continue;
						}

						//去除M列【不为空】的行
						if (StringUtils.isEmpty(data[12])) {

						} else {
							//TODO
//									bugCSVHistory_M.put(file.getName(), file.getName());
							continue;

						}


						sb.append("\"" + String.join("\",\"", data) + "\"").append("\r\n");

						//	5 合计列数值并写入新加最后一行
						//	6	13-27
						total6 = total6 + Double.parseDouble(data[6]);
						total13 = total13 + Double.parseDouble(data[13]);
						total14 = total14 + Double.parseDouble(data[14]);
						total15 = total15 + Double.parseDouble(data[15]);
						total16 = total16 + Double.parseDouble(data[16]);
						total17 = total17 + Double.parseDouble(data[17]);
						total18 = total18 + Double.parseDouble(data[18]);
						total19 = total19 + Double.parseDouble(data[19]);
						total20 = total20 + Double.parseDouble(data[20]);
						total21 = total21 + Double.parseDouble(data[21]);
						total22 = total22 + Double.parseDouble(data[22]);
						total23 = total23 + Double.parseDouble(data[23]);
						total24 = total24 + Double.parseDouble(data[24]);
						total25 = total25 + Double.parseDouble(data[25]);
						total26 = total26 + Double.parseDouble(data[26]);
						total27 = total27 + Double.parseDouble(data[27]);

					}
				} catch (Exception e) {
					throw e;

				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				String[] data = new String[28];
				data[6] = String.format("%.0f", total6);
				data[13] = String.format("%.0f", total13);
				data[14] = String.format("%.0f", total14);
				data[15] = String.format("%.0f", total15);
				data[16] = String.format("%.0f", total16);
				data[17] = String.format("%.0f", total17);
				data[18] = String.format("%.0f", total18);
				data[19] = String.format("%.0f", total19);
				data[20] = String.format("%.0f", total20);
				data[21] = String.format("%.0f", total21);
				data[22] = String.format("%.0f", total22);
				data[23] = String.format("%.0f", total23);
				data[24] = String.format("%.0f", total24);
				data[25] = String.format("%.0f", total25);
				data[26] = String.format("%.0f", total26);
				data[27] = String.format("%.0f", total27);

				sb.append("\"" + String.join("\",\"", data).replaceAll("null", "") + "\"");

				/*
				 * 循环 textFiles，将每个键值对写入文件
				 */
				String fileContent = sb.toString();
				if (fileContent.contains("\n")) {
//							logger.debug(fileName);

				} else {
//					throw new Exception("csvERR");
				}

				//	输出CSV，输出的文件名【計算用+初始文件名】
				// 构建文件路径
				String filePath = path + "/計算用" + fileName;

				FileWriter writer = new FileWriter(filePath);
				writer.write(fileContent);
				writer.close();

			}

		}
	}




}
