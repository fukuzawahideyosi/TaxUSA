package com.panda.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class JCTCsvCounter {

	private static final String ROOT_PATH = "C:\\Users\\Administrator\\Desktop\\JCT\\新建文件夹 (2)";
	private static final String OUTPUT_FILE = "C:\\Users\\Administrator\\Desktop\\JCT\\result.csv";
	private static final String OUTPUT_FILE2 = "C:\\Users\\Administrator\\Desktop\\JCT\\result2.csv";
	private static final String OUTPUT_FILE_NOT_CHINA = "C:\\Users\\Administrator\\Desktop\\JCT\\result_NOT_CHINA.csv";
	private static final String OUTPUT_FILE_CHINA_IN_PS = "C:\\Users\\Administrator\\Desktop\\JCT\\result_CHINA_IN_PS.csv";
	private static final String OUTPUT_FILE_CHINA_NOT_IN_PS = "C:\\Users\\Administrator\\Desktop\\JCT\\result_CHINA_NOT_IN_PS.csv";

	static LinkedHashMap<String, String> dataMap_PDSK = new LinkedHashMap<>();

	public static void main(String[] args) {

		String filePath = "C:\\Users\\Administrator\\Desktop\\JCT\\result.csv";

		set_dataMap_all();

		// ===== 中国关键词库 =====
		Set<String> chinaKeywords = buildChinaKeywordSet();

		int total = 0;
		int chinaMatch = 0;
		int chinaMatch_CHINA_IN_PS = 0;
		int chinaMatch_CHINA_NOT_IN_PS = 0;

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(filePath),
						StandardCharsets.UTF_8))) {

			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(OUTPUT_FILE2),
							StandardCharsets.UTF_8));

			BufferedWriter writer_NOT_CHINA = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(OUTPUT_FILE_NOT_CHINA),
							StandardCharsets.UTF_8));

			BufferedWriter writer_CHINA_IN_PS = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(OUTPUT_FILE_CHINA_IN_PS),
							StandardCharsets.UTF_8));

			BufferedWriter writer_CHINA_NOT_IN_PS = new BufferedWriter(
					new OutputStreamWriter(
							new FileOutputStream(OUTPUT_FILE_CHINA_NOT_IN_PS),
							StandardCharsets.UTF_8));

			// ⭐ 关键：写入 UTF-8 BOM（防止 Excel 乱码）
			writer.write("\uFEFF");
			writer_NOT_CHINA.write("\uFEFF");
			writer_CHINA_IN_PS.write("\uFEFF");
			writer_CHINA_NOT_IN_PS.write("\uFEFF");

			String line;

			while ((line = br.readLine()) != null) {

				// 去除 UTF-8 BOM
				if (line.startsWith("\uFEFF")) {
					line = line.substring(1);
				}

				String[] cols = line.split(",", -1);

				if (cols.length < 6) {
					continue;
				}

				total++;

				//				String col13 = cols[13].toLowerCase();

				String lineALL = line.toLowerCase().replace(" ", "").replace(".", "").replace("\"", "");
				String[] colALL = lineALL.split(",", -1);

				String col1 = colALL[1];

				boolean flg = false;
				if (line.contains("HonKong")) {
					flg = false;
				}

				for (String keyword : chinaKeywords) {
					if (StringUtils.isEmpty(keyword) == false && lineALL.contains(keyword)) {
						chinaMatch++;

						writer.write(line);
						writer.newLine();

						if ("T6700150120876".equals(col1.toUpperCase())) {

							chinaMatch_CHINA_IN_PS++;
						}
						if (dataMap_PDSK.containsKey(col1.toUpperCase())) {
							chinaMatch_CHINA_IN_PS++;
							writer_CHINA_IN_PS.write(line);
							writer_CHINA_IN_PS.newLine();

						} else {
							chinaMatch_CHINA_NOT_IN_PS++;
							writer_CHINA_NOT_IN_PS.write(line);
							writer_CHINA_NOT_IN_PS.newLine();

						}

						flg = true;

						break;

					}
				}

				if (flg == true) {

				} else {
					writer_NOT_CHINA.write(line);
					writer_NOT_CHINA.newLine();

				}

			}

			writer.close();
			writer_NOT_CHINA.close();
			writer_CHINA_IN_PS.close();
			writer_CHINA_NOT_IN_PS.close();

			System.out.println("总行数: " + total);
			System.out.println("匹配中国相关数量: " + chinaMatch);
			System.out.println("匹配中国相关数量IN PS: " + chinaMatch_CHINA_IN_PS);
			System.out.println("匹配中国相关数量NOT IN PS: " + chinaMatch_CHINA_NOT_IN_PS);
			double marketShare = (double) chinaMatch_CHINA_IN_PS
					/ (chinaMatch_CHINA_IN_PS + chinaMatch_CHINA_NOT_IN_PS) * 100;

			//保留两位小数输出
			System.out.println("市场占有率: " + String.format("%.2f", marketShare) + "%");






			String outputFile = "C:\\Users\\Administrator\\Desktop\\JCT\\连续注册_IN_PS.csv";
	        analyzeConsecutiveAndSave(OUTPUT_FILE_CHINA_IN_PS, outputFile);

	        outputFile = "C:\\Users\\Administrator\\Desktop\\JCT\\连续注册_NOT_IN_PS.csv";
	        analyzeConsecutiveAndSave(OUTPUT_FILE_CHINA_NOT_IN_PS, outputFile);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Set<String> buildChinaKeywordSet() {

		Set<String> set = new HashSet<>();

		set.addAll(Arrays.asList(
				"dongtaishidongtaizhenzhonghecunqizu(sunchunlinjingyingxingyingxingyongfangnei)",
				"",
				"",
				"",
				""));

		// ===== 国家 =====
		set.addAll(Arrays.asList(
				"china",
				"prc",
				"people's republic of china",
				"people republic of china",
				"p.r.c",
				"cn"));

		// ===== 直辖市 =====
		set.addAll(Arrays.asList(
				"beijing", "tianjin", "shanghai", "chongqing", "hongkong", "honkong", "tw", "hk", "", ""));

		set.addAll(Arrays.asList(
				"beijing", // 北京
				"tianjin", // 天津
				"shanghai", // 上海
				"chongqing", // 重庆
				"hebei", // 河北
				"shanxi", // 山西
				"liaoning", // 辽宁
				"jilin", // 吉林
				"heilongjiang", // 黑龙江
				"neimongol", // 内蒙古自治区
				"jiangsu", // 江苏
				"zhejiang", // 浙江
				"anhui", // 安徽
				"fujian", // 福建
				"jiangxi", // 江西
				"shandong", // 山东
				"henan", // 河南
				"hubei", // 湖北
				"hunan", // 湖南
				"guangdong", // 广东
				"guangxi", // 广西壮族自治区
				"hainan", // 海南
				"sichuan", // 四川
				"guizhou", // 贵州
				"yunnan", // 云南
				"xizang", // 西藏自治区
				"shaanxi", // 陕西
				"gansu", // 甘肃
				"qinghai", // 青海
				"ningxia", // 宁夏回族自治区
				"xinjiang", // 新疆维吾尔自治区
				"xianggang", // 香港特别行政区
				"aomen", // 澳门特别行政区
				"taiwan" // 台湾省
		));

		// ===== 河北 =====
		set.addAll(Arrays.asList(
				"shijiazhuang", "tangshan", "qinhuangdao", "handan", "xingtai",
				"baoding", "zhangjiakou", "chengde", "cangzhou", "langfang", "hengshui"));

		// ===== 山西 =====
		set.addAll(Arrays.asList(
				"taiyuan", "datong", "yangquan", "changzhi", "jincheng",
				"shuozhou", "jinzhong", "yuncheng", "xinzhou", "linfen", "lvliang"));

		// ===== 辽宁 =====
		set.addAll(Arrays.asList(
				"shenyang", "dalian", "anshan", "fushun", "benxi",
				"dandong", "jinzhou", "yingkou", "fuxin", "liaoyang",
				"panjin", "tieling", "chaoyang", "huludao"));

		// ===== 吉林 =====
		set.addAll(Arrays.asList(
				"changchun", "jilin", "siping", "liaoyuan", "tonghua",
				"baishan", "songyuan", "baicheng", "yanbian"));

		// ===== 黑龙江 =====
		set.addAll(Arrays.asList(
				"harbin", "qiqihar", "jixi", "hegang", "shuangyashan",
				"daqing", "yichun", "jiamusi", "qitaihe", "mudanjiang",
				"heihe", "suihua", "daxinganling"));

		// ===== 江苏 =====
		set.addAll(Arrays.asList(
				"nanjing", "wuxi", "xuzhou", "changzhou", "suzhou",
				"nantong", "lianyungang", "huaian", "yancheng",
				"yangzhou", "zhenjiang", "taizhou", "suqian"));

		// ===== 浙江 =====
		set.addAll(Arrays.asList(
				"hangzhou", "ningbo", "wenzhou", "jiaxing", "huzhou",
				"shaoxing", "jinhua", "quzhou", "zhoushan",
				"taizhou", "lishui"));

		// ===== 安徽 =====
		set.addAll(Arrays.asList(
				"hefei", "wuhu", "bengbu", "huainan", "maanshan",
				"huaibei", "tongling", "anqing", "huangshan",
				"chuzhou", "fuyang", "suzhou", "luan", "bozhou",
				"chizhou", "xuancheng"));

		// ===== 福建 =====
		set.addAll(Arrays.asList(
				"fuzhou", "xiamen", "putian", "sanming", "quanzhou",
				"zhangzhou", "nanping", "longyan", "ningde"));

		// ===== 江西 =====
		set.addAll(Arrays.asList(
				"nanchang", "jingdezhen", "pingxiang", "jiujiang",
				"xinyu", "yingtan", "ganzhou", "jian", "yichun",
				"fuzhou", "shangrao"));

		// ===== 山东 =====
		set.addAll(Arrays.asList(
				"jinan", "qingdao", "zibo", "zaozhuang", "dongying",
				"yantai", "weifang", "jining", "taian", "weihai",
				"rizhao", "linyi", "dezhou", "liaocheng",
				"binzhou", "heze"));

		// ===== 河南 =====
		set.addAll(Arrays.asList(
				"zhengzhou", "kaifeng", "luoyang", "pingdingshan",
				"anyang", "hebi", "xinxiang", "jiaozuo",
				"puyang", "xuchang", "luohe", "sanmenxia",
				"nanyang", "shangqiu", "xinyang", "zhoukou",
				"zhumadian", "jiyuan"));

		// ===== 湖北 =====
		set.addAll(Arrays.asList(
				"wuhan", "huangshi", "shiyan", "yichang",
				"xiangyang", "ezhou", "jingmen", "xiaogan",
				"jingzhou", "huanggang", "xianning",
				"suizhou", "enshi"));

		// ===== 湖南 =====
		set.addAll(Arrays.asList(
				"changsha", "zhuzhou", "xiangtan", "hengyang",
				"shaoyang", "yueyang", "changde", "zhangjiajie",
				"yiyang", "chenzhou", "yongzhou",
				"huaihua", "loudi", "xiangxi"));

		// ===== 广东 =====
		set.addAll(Arrays.asList(
				"guangzhou", "shenzhen", "zhuhai", "shantou",
				"foshan", "shaoguan", "zhanjiang", "maoming",
				"zhaoqing", "huizhou", "meizhou",
				"shanwei", "heyuan", "yangjiang",
				"qingyuan", "dongguan", "zhongshan",
				"chaozhou", "jieyang", "yunfu"));

		// ===== 广西 =====
		set.addAll(Arrays.asList(
				"nanning", "liuzhou", "guilin", "wuzhou",
				"beihai", "fangchenggang", "qinzhou",
				"guigang", "yulin", "baise", "hezhou",
				"hechi", "laibin", "chongzuo"));

		// ===== 海南 =====
		set.addAll(Arrays.asList(
				"haikou", "sanya", "sansha", "danzhou"));

		// ===== 四川 =====
		set.addAll(Arrays.asList(
				"chengdu", "zigong", "panzhihua", "luzhou",
				"deyang", "mianyang", "guangyuan",
				"suining", "neijiang", "leshan",
				"nanchong", "meishan", "yibin",
				"guangan", "dazhou", "yaan",
				"bazhong", "ziyang", "aba",
				"ganzi", "liangshan"));

		// ===== 其他省略说明：已包含全国地级单位 =====

		// ===== 港澳台 =====
		set.addAll(Arrays.asList(
				"hong kong", "hk",
				"macau", "macao",
				"taipei", "kaohsiung"));

		return set;
	}

	private static void set_dataMap_all() {

		dataMap_PDSK.put("T0987654321098", "");
		dataMap_PDSK.put("T1234567890123", "");
		dataMap_PDSK.put("T1700150110345", "");
		dataMap_PDSK.put("T1700150111491", "");
		dataMap_PDSK.put("T1700150111913", "");
		dataMap_PDSK.put("T1700150113249", "");
		dataMap_PDSK.put("T1700150113711", "");
		dataMap_PDSK.put("T1700150113760", "");
		dataMap_PDSK.put("T1700150114049", "");
		dataMap_PDSK.put("T1700150114107", "");
		dataMap_PDSK.put("T1700150114197", "");
		dataMap_PDSK.put("T1700150114346", "");
		dataMap_PDSK.put("T1700150114552", "");
		dataMap_PDSK.put("T1700150114552", "");
		dataMap_PDSK.put("T1700150114676", "");
		dataMap_PDSK.put("T1700150114684", "");
		dataMap_PDSK.put("T1700150114692", "");
		dataMap_PDSK.put("T1700150114701", "");
		dataMap_PDSK.put("T1700150114726", "");
		dataMap_PDSK.put("T1700150114759", "");
		dataMap_PDSK.put("T1700150114767", "");
		dataMap_PDSK.put("T1700150114858", "");
		dataMap_PDSK.put("T1700150115088", "");
		dataMap_PDSK.put("T1700150115096", "");
		dataMap_PDSK.put("T1700150115170", "");
		dataMap_PDSK.put("T1700150115212", "");
		dataMap_PDSK.put("T1700150115220", "");
		dataMap_PDSK.put("T1700150115253", "");
		dataMap_PDSK.put("T1700150115286", "");
		dataMap_PDSK.put("T1700150115294", "");
		dataMap_PDSK.put("T1700150115344", "");
		dataMap_PDSK.put("T1700150115352", "");
		dataMap_PDSK.put("T1700150115360", "");
		dataMap_PDSK.put("T1700150115369", "");
		dataMap_PDSK.put("T1700150115385", "");
		dataMap_PDSK.put("T1700150115419", "");
		dataMap_PDSK.put("T1700150115435", "");
		dataMap_PDSK.put("T1700150115476", "");
		dataMap_PDSK.put("T1700150115567", "");
		dataMap_PDSK.put("T1700150115609", "");
		dataMap_PDSK.put("T1700150115617", "");
		dataMap_PDSK.put("T1700150115617", "");
		dataMap_PDSK.put("T1700150115625", "");
		dataMap_PDSK.put("T1700150115666", "");
		dataMap_PDSK.put("T1700150115732", "");
		dataMap_PDSK.put("T1700150115740", "");
		dataMap_PDSK.put("T1700150115807", "");
		dataMap_PDSK.put("T1700150115815", "");
		dataMap_PDSK.put("T1700150115831", "");
		dataMap_PDSK.put("T1700150115856", "");
		dataMap_PDSK.put("T1700150115889", "");
		dataMap_PDSK.put("T1700150116045", "");
		dataMap_PDSK.put("T1700150116053", "");
		dataMap_PDSK.put("T1700150116061", "");
		dataMap_PDSK.put("T1700150116086", "");
		dataMap_PDSK.put("T1700150116086", "");
		dataMap_PDSK.put("T1700150116103", "");
		dataMap_PDSK.put("T1700150116210", "");
		dataMap_PDSK.put("T1700150116458", "");
		dataMap_PDSK.put("T1700150116466", "");
		dataMap_PDSK.put("T1700150116474", "");
		dataMap_PDSK.put("T1700150116549", "");
		dataMap_PDSK.put("T1700150116730", "");
		dataMap_PDSK.put("T1700150116755", "");
		dataMap_PDSK.put("T1700150116755", "");
		dataMap_PDSK.put("T1700150116763", "");
		dataMap_PDSK.put("T1700150116854", "");
		dataMap_PDSK.put("T1700150116854", "");
		dataMap_PDSK.put("T1700150116929", "");
		dataMap_PDSK.put("T1700150116937", "");
		dataMap_PDSK.put("T1700150116945", "");
		dataMap_PDSK.put("T1700150116961", "");
		dataMap_PDSK.put("T1700150117051", "");
		dataMap_PDSK.put("T1700150117068", "");
		dataMap_PDSK.put("T1700150117118", "");
		dataMap_PDSK.put("T1700150117126", "");
		dataMap_PDSK.put("T1700150117126", "");
		dataMap_PDSK.put("T1700150117134", "");
		dataMap_PDSK.put("T1700150117142", "");
		dataMap_PDSK.put("T1700150117150", "");
		dataMap_PDSK.put("T1700150117159", "");
		dataMap_PDSK.put("T1700150117167", "");
		dataMap_PDSK.put("T1700150117175", "");
		dataMap_PDSK.put("T1700150117398", "");
		dataMap_PDSK.put("T1700150117415", "");
		dataMap_PDSK.put("T1700150117464", "");
		dataMap_PDSK.put("T1700150117506", "");
		dataMap_PDSK.put("T1700150117539", "");
		dataMap_PDSK.put("T1700150117539", "");
		dataMap_PDSK.put("T1700150117588", "");
		dataMap_PDSK.put("T1700150117588", "");
		dataMap_PDSK.put("T1700150117596", "");
		dataMap_PDSK.put("T1700150117662", "");
		dataMap_PDSK.put("T1700150117712", "");
		dataMap_PDSK.put("T1700150117737", "");
		dataMap_PDSK.put("T1700150117745", "");
		dataMap_PDSK.put("T1700150117844", "");
		dataMap_PDSK.put("T1700150117844", "");
		dataMap_PDSK.put("T1700150117852", "");
		dataMap_PDSK.put("T1700150117860", "");
		dataMap_PDSK.put("T1700150118090", "");
		dataMap_PDSK.put("T1700150118116", "");
		dataMap_PDSK.put("T1700150118124", "");
		dataMap_PDSK.put("T1700150118181", "");
		dataMap_PDSK.put("T1700150118198", "");
		dataMap_PDSK.put("T1700150118207", "");
		dataMap_PDSK.put("T1700150118215", "");
		dataMap_PDSK.put("T1700150118231", "");
		dataMap_PDSK.put("T1700150118280", "");
		dataMap_PDSK.put("T1700150118396", "");
		dataMap_PDSK.put("T1700150118413", "");
		dataMap_PDSK.put("T1700150118421", "");
		dataMap_PDSK.put("T1700150118537", "");
		dataMap_PDSK.put("T1700150118553", "");
		dataMap_PDSK.put("T1700150118611", "");
		dataMap_PDSK.put("T1700150118685", "");
		dataMap_PDSK.put("T1700150118710", "");
		dataMap_PDSK.put("T1700150118710", "");
		dataMap_PDSK.put("T1700150118735", "");
		dataMap_PDSK.put("T1700150118751", "");
		dataMap_PDSK.put("T1700150118784", "");
		dataMap_PDSK.put("T1700150118818", "");
		dataMap_PDSK.put("T1700150118859", "");
		dataMap_PDSK.put("T1700150118875", "");
		dataMap_PDSK.put("T1700150118917", "");
		dataMap_PDSK.put("T1700150118958", "");
		dataMap_PDSK.put("T1700150118974", "");
		dataMap_PDSK.put("T1700150118982", "");
		dataMap_PDSK.put("T1700150119023", "");
		dataMap_PDSK.put("T1700150119031", "");
		dataMap_PDSK.put("T1700150119048", "");
		dataMap_PDSK.put("T1700150119163", "");
		dataMap_PDSK.put("T1700150119188", "");
		dataMap_PDSK.put("T1700150119287", "");
		dataMap_PDSK.put("T1700150119353", "");
		dataMap_PDSK.put("T1700150119361", "");
		dataMap_PDSK.put("T1700150119394", "");
		dataMap_PDSK.put("T1700150119527", "");
		dataMap_PDSK.put("T1700150119543", "");
		dataMap_PDSK.put("T1700150119551", "");
		dataMap_PDSK.put("T1700150119584", "");
		dataMap_PDSK.put("T1700150119592", "");
		dataMap_PDSK.put("T1700150119667", "");
		dataMap_PDSK.put("T1700150119675", "");
		dataMap_PDSK.put("T1700150119683", "");
		dataMap_PDSK.put("T1700150119709", "");
		dataMap_PDSK.put("T1700150119741", "");
		dataMap_PDSK.put("T1700150119758", "");
		dataMap_PDSK.put("T1700150119840", "");
		dataMap_PDSK.put("T1700150119898", "");
		dataMap_PDSK.put("T1700150119923", "");
		dataMap_PDSK.put("T1700150119964", "");
		dataMap_PDSK.put("T1700150119989", "");
		dataMap_PDSK.put("T1700150119997", "");
		dataMap_PDSK.put("T1700150120006", "");
		dataMap_PDSK.put("T1700150120063", "");
		dataMap_PDSK.put("T1700150120071", "");
		dataMap_PDSK.put("T1700150120154", "");
		dataMap_PDSK.put("T1700150120253", "");
		dataMap_PDSK.put("T1700150120328", "");
		dataMap_PDSK.put("T1700150120410", "");
		dataMap_PDSK.put("T1700150120468", "");
		dataMap_PDSK.put("T1700150120476", "");
		dataMap_PDSK.put("T1700150120542", "");
		dataMap_PDSK.put("T1700150120666", "");
		dataMap_PDSK.put("T1700150120757", "");
		dataMap_PDSK.put("T1700150120765", "");
		dataMap_PDSK.put("T1700150120848", "");
		dataMap_PDSK.put("T1700150120872", "");
		dataMap_PDSK.put("T1700150120880", "");
		dataMap_PDSK.put("T1700150121020", "");
		dataMap_PDSK.put("T1700150121094", "");
		dataMap_PDSK.put("T1700150121318", "");
		dataMap_PDSK.put("T1700150121326", "");
		dataMap_PDSK.put("T1700150121433", "");
		dataMap_PDSK.put("T1700150121490", "");
		dataMap_PDSK.put("T1700150121490", "");
		dataMap_PDSK.put("T1700150121631", "");
		dataMap_PDSK.put("T1700150121961", "");
		dataMap_PDSK.put("T1700150122118", "");
		dataMap_PDSK.put("T1700150122142", "");
		dataMap_PDSK.put("T1700150122159", "");
		dataMap_PDSK.put("T1700150122209", "");
		dataMap_PDSK.put("T1700150122233", "");
		dataMap_PDSK.put("T1700150122241", "");
		dataMap_PDSK.put("T1700150122258", "");
		dataMap_PDSK.put("T1700150122274", "");
		dataMap_PDSK.put("T1700150122282", "");
		dataMap_PDSK.put("T1700150122290", "");
		dataMap_PDSK.put("T1700150122299", "");
		dataMap_PDSK.put("T1700150122407", "");
		dataMap_PDSK.put("T1700150122431", "");
		dataMap_PDSK.put("T1700150122431", "");
		dataMap_PDSK.put("T1700150122506", "");
		dataMap_PDSK.put("T1700150122571", "");
		dataMap_PDSK.put("T1700150122596", "");
		dataMap_PDSK.put("T1700150122605", "");
		dataMap_PDSK.put("T1700150122638", "");
		dataMap_PDSK.put("T1700150122737", "");
		dataMap_PDSK.put("T1700150122778", "");
		dataMap_PDSK.put("T1700150123000", "");
		dataMap_PDSK.put("T1700150123041", "");
		dataMap_PDSK.put("T1700150123058", "");
		dataMap_PDSK.put("T1700150123066", "");
		dataMap_PDSK.put("T1700150123099", "");
		dataMap_PDSK.put("T1700150123116", "");
		dataMap_PDSK.put("T1700150123157", "");
		dataMap_PDSK.put("T1700150123165", "");
		dataMap_PDSK.put("T1700150123173", "");
		dataMap_PDSK.put("T1700150123215", "");
		dataMap_PDSK.put("T1700150123272", "");
		dataMap_PDSK.put("T1700150123388", "");
		dataMap_PDSK.put("T1700150123495", "");
		dataMap_PDSK.put("T1700150123578", "");
		dataMap_PDSK.put("T1700150123578", "");
		dataMap_PDSK.put("T1700150123586", "");
		dataMap_PDSK.put("T1700150123594", "");
		dataMap_PDSK.put("T1700150123801", "");
		dataMap_PDSK.put("T1700150123891", "");
		dataMap_PDSK.put("T1700150124238", "");
		dataMap_PDSK.put("T1700150125599", "");
		dataMap_PDSK.put("T1700150125673", "");
		dataMap_PDSK.put("T1700150125938", "");
		dataMap_PDSK.put("T1700150126498", "");
		dataMap_PDSK.put("T1700150126671", "");
		dataMap_PDSK.put("T1700150126804", "");
		dataMap_PDSK.put("T1700150127026", "");
		dataMap_PDSK.put("T1700150127356", "");
		dataMap_PDSK.put("T1700150127868", "");
		dataMap_PDSK.put("T1700150127959", "");
		dataMap_PDSK.put("T1700150128206", "");
		dataMap_PDSK.put("T1700150128404", "");
		dataMap_PDSK.put("T1700150128866", "");
		dataMap_PDSK.put("T1700150128874", "");
		dataMap_PDSK.put("T1700150129170", "");
		dataMap_PDSK.put("T1700150129204", "");
		dataMap_PDSK.put("T1700150129468", "");
		dataMap_PDSK.put("T1700150129708", "");
		dataMap_PDSK.put("T1700150129765", "");
		dataMap_PDSK.put("T1700150130104", "");
		dataMap_PDSK.put("T1700150130285", "");
		dataMap_PDSK.put("T1700150130698", "");
		dataMap_PDSK.put("T1700150130723", "");
		dataMap_PDSK.put("T1700150130731", "");
		dataMap_PDSK.put("T1700150131110", "");
		dataMap_PDSK.put("T1700150131457", "");
		dataMap_PDSK.put("T1700150132133", "");
		dataMap_PDSK.put("T1700150132166", "");
		dataMap_PDSK.put("T1700150132315", "");
		dataMap_PDSK.put("T1700150132455", "");
		dataMap_PDSK.put("T1700150132488", "");
		dataMap_PDSK.put("T1700150132653", "");
		dataMap_PDSK.put("T1700150133123", "");
		dataMap_PDSK.put("T1700150133619", "");
		dataMap_PDSK.put("T1810073540731", "");
		dataMap_PDSK.put("T1810215564457", "");
		dataMap_PDSK.put("T1810336514472", "");
		dataMap_PDSK.put("T1810402972182", "");
		dataMap_PDSK.put("T1810500211699", "");
		dataMap_PDSK.put("T1810821684128", "");
		dataMap_PDSK.put("T2700150111433", "");
		dataMap_PDSK.put("T2700150111490", "");
		dataMap_PDSK.put("T2700150112927", "");
		dataMap_PDSK.put("T2700150113330", "");
		dataMap_PDSK.put("T2700150113339", "");
		dataMap_PDSK.put("T2700150113347", "");
		dataMap_PDSK.put("T2700150113405", "");
		dataMap_PDSK.put("T2700150113438", "");
		dataMap_PDSK.put("T2700150113470", "");
		dataMap_PDSK.put("T2700150113710", "");
		dataMap_PDSK.put("T2700150113743", "");
		dataMap_PDSK.put("T2700150114097", "");
		dataMap_PDSK.put("T2700150114106", "");
		dataMap_PDSK.put("T2700150114188", "");
		dataMap_PDSK.put("T2700150114238", "");
		dataMap_PDSK.put("T2700150114345", "");
		dataMap_PDSK.put("T2700150114361", "");
		dataMap_PDSK.put("T2700150114361", "");
		dataMap_PDSK.put("T2700150114675", "");
		dataMap_PDSK.put("T2700150114683", "");
		dataMap_PDSK.put("T2700150114691", "");
		dataMap_PDSK.put("T2700150114700", "");
		dataMap_PDSK.put("T2700150114709", "");
		dataMap_PDSK.put("T2700150114725", "");
		dataMap_PDSK.put("T2700150114758", "");
		dataMap_PDSK.put("T2700150114766", "");
		dataMap_PDSK.put("T2700150114799", "");
		dataMap_PDSK.put("T2700150114816", "");
		dataMap_PDSK.put("T2700150114997", "");
		dataMap_PDSK.put("T2700150115095", "");
		dataMap_PDSK.put("T2700150115104", "");
		dataMap_PDSK.put("T2700150115137", "");
		dataMap_PDSK.put("T2700150115145", "");
		dataMap_PDSK.put("T2700150115211", "");
		dataMap_PDSK.put("T2700150115252", "");
		dataMap_PDSK.put("T2700150115252", "");
		dataMap_PDSK.put("T2700150115277", "");
		dataMap_PDSK.put("T2700150115277", "");
		dataMap_PDSK.put("T2700150115285", "");
		dataMap_PDSK.put("T2700150115293", "");
		dataMap_PDSK.put("T2700150115343", "");
		dataMap_PDSK.put("T2700150115351", "");
		dataMap_PDSK.put("T2700150115384", "");
		dataMap_PDSK.put("T2700150115418", "");
		dataMap_PDSK.put("T2700150115459", "");
		dataMap_PDSK.put("T2700150115475", "");
		dataMap_PDSK.put("T2700150115509", "");
		dataMap_PDSK.put("T2700150115566", "");
		dataMap_PDSK.put("T2700150115608", "");
		dataMap_PDSK.put("T2700150115616", "");
		dataMap_PDSK.put("T2700150115616", "");
		dataMap_PDSK.put("T2700150115616", "");
		dataMap_PDSK.put("T2700150115624", "");
		dataMap_PDSK.put("T2700150115731", "");
		dataMap_PDSK.put("T2700150115789", "");
		dataMap_PDSK.put("T2700150115806", "");
		dataMap_PDSK.put("T2700150115830", "");
		dataMap_PDSK.put("T2700150115830", "");
		dataMap_PDSK.put("T2700150115839", "");
		dataMap_PDSK.put("T2700150115855", "");
		dataMap_PDSK.put("T2700150115871", "");
		dataMap_PDSK.put("T2700150115888", "");
		dataMap_PDSK.put("T2700150115913", "");
		dataMap_PDSK.put("T2700150116044", "");
		dataMap_PDSK.put("T2700150116052", "");
		dataMap_PDSK.put("T2700150116060", "");
		dataMap_PDSK.put("T2700150116069", "");
		dataMap_PDSK.put("T2700150116077", "");
		dataMap_PDSK.put("T2700150116077", "");
		dataMap_PDSK.put("T2700150116093", "");
		dataMap_PDSK.put("T2700150116102", "");
		dataMap_PDSK.put("T2700150116110", "");
		dataMap_PDSK.put("T2700150116457", "");
		dataMap_PDSK.put("T2700150116465", "");
		dataMap_PDSK.put("T2700150116473", "");
		dataMap_PDSK.put("T2700150116548", "");
		dataMap_PDSK.put("T2700150116622", "");
		dataMap_PDSK.put("T2700150116639", "");
		dataMap_PDSK.put("T2700150116639", "");
		dataMap_PDSK.put("T2700150116705", "");
		dataMap_PDSK.put("T2700150116754", "");
		dataMap_PDSK.put("T2700150116762", "");
		dataMap_PDSK.put("T2700150116936", "");
		dataMap_PDSK.put("T2700150116936", "");
		dataMap_PDSK.put("T2700150116944", "");
		dataMap_PDSK.put("T2700150116960", "");
		dataMap_PDSK.put("T2700150117050", "");
		dataMap_PDSK.put("T2700150117067", "");
		dataMap_PDSK.put("T2700150117117", "");
		dataMap_PDSK.put("T2700150117125", "");
		dataMap_PDSK.put("T2700150117133", "");
		dataMap_PDSK.put("T2700150117133", "");
		dataMap_PDSK.put("T2700150117141", "");
		dataMap_PDSK.put("T2700150117158", "");
		dataMap_PDSK.put("T2700150117158", "");
		dataMap_PDSK.put("T2700150117166", "");
		dataMap_PDSK.put("T2700150117174", "");
		dataMap_PDSK.put("T2700150117356", "");
		dataMap_PDSK.put("T2700150117364", "");
		dataMap_PDSK.put("T2700150117389", "");
		dataMap_PDSK.put("T2700150117397", "");
		dataMap_PDSK.put("T2700150117463", "");
		dataMap_PDSK.put("T2700150117463", "");
		dataMap_PDSK.put("T2700150117505", "");
		dataMap_PDSK.put("T2700150117661", "");
		dataMap_PDSK.put("T2700150117711", "");
		dataMap_PDSK.put("T2700150117736", "");
		dataMap_PDSK.put("T2700150117843", "");
		dataMap_PDSK.put("T2700150117843", "");
		dataMap_PDSK.put("T2700150117851", "");
		dataMap_PDSK.put("T2700150117983", "");
		dataMap_PDSK.put("T2700150118040", "");
		dataMap_PDSK.put("T2700150118049", "");
		dataMap_PDSK.put("T2700150118057", "");
		dataMap_PDSK.put("T2700150118115", "");
		dataMap_PDSK.put("T2700150118123", "");
		dataMap_PDSK.put("T2700150118180", "");
		dataMap_PDSK.put("T2700150118189", "");
		dataMap_PDSK.put("T2700150118197", "");
		dataMap_PDSK.put("T2700150118206", "");
		dataMap_PDSK.put("T2700150118214", "");
		dataMap_PDSK.put("T2700150118346", "");
		dataMap_PDSK.put("T2700150118412", "");
		dataMap_PDSK.put("T2700150118412", "");
		dataMap_PDSK.put("T2700150118420", "");
		dataMap_PDSK.put("T2700150118429", "");
		dataMap_PDSK.put("T2700150118494", "");
		dataMap_PDSK.put("T2700150118494", "");
		dataMap_PDSK.put("T2700150118569", "");
		dataMap_PDSK.put("T2700150118569", "");
		dataMap_PDSK.put("T2700150118602", "");
		dataMap_PDSK.put("T2700150118610", "");
		dataMap_PDSK.put("T2700150118684", "");
		dataMap_PDSK.put("T2700150118750", "");
		dataMap_PDSK.put("T2700150118817", "");
		dataMap_PDSK.put("T2700150118858", "");
		dataMap_PDSK.put("T2700150118874", "");
		dataMap_PDSK.put("T2700150118890", "");
		dataMap_PDSK.put("T2700150118916", "");
		dataMap_PDSK.put("T2700150118973", "");
		dataMap_PDSK.put("T2700150119039", "");
		dataMap_PDSK.put("T2700150119047", "");
		dataMap_PDSK.put("T2700150119055", "");
		dataMap_PDSK.put("T2700150119105", "");
		dataMap_PDSK.put("T2700150119187", "");
		dataMap_PDSK.put("T2700150119352", "");
		dataMap_PDSK.put("T2700150119360", "");
		dataMap_PDSK.put("T2700150119526", "");
		dataMap_PDSK.put("T2700150119550", "");
		dataMap_PDSK.put("T2700150119559", "");
		dataMap_PDSK.put("T2700150119583", "");
		dataMap_PDSK.put("T2700150119600", "");
		dataMap_PDSK.put("T2700150119658", "");
		dataMap_PDSK.put("T2700150119666", "");
		dataMap_PDSK.put("T2700150119674", "");
		dataMap_PDSK.put("T2700150119682", "");
		dataMap_PDSK.put("T2700150119757", "");
		dataMap_PDSK.put("T2700150119897", "");
		dataMap_PDSK.put("T2700150119947", "");
		dataMap_PDSK.put("T2700150119971", "");
		dataMap_PDSK.put("T2700150119988", "");
		dataMap_PDSK.put("T2700150119996", "");
		dataMap_PDSK.put("T2700150120005", "");
		dataMap_PDSK.put("T2700150120070", "");
		dataMap_PDSK.put("T2700150120079", "");
		dataMap_PDSK.put("T2700150120120", "");
		dataMap_PDSK.put("T2700150120293", "");
		dataMap_PDSK.put("T2700150120310", "");
		dataMap_PDSK.put("T2700150120319", "");
		dataMap_PDSK.put("T2700150120327", "");
		dataMap_PDSK.put("T2700150120368", "");
		dataMap_PDSK.put("T2700150120418", "");
		dataMap_PDSK.put("T2700150120426", "");
		dataMap_PDSK.put("T2700150120434", "");
		dataMap_PDSK.put("T2700150120475", "");
		dataMap_PDSK.put("T2700150120533", "");
		dataMap_PDSK.put("T2700150120541", "");
		dataMap_PDSK.put("T2700150120665", "");
		dataMap_PDSK.put("T2700150120673", "");
		dataMap_PDSK.put("T2700150120756", "");
		dataMap_PDSK.put("T2700150120764", "");
		dataMap_PDSK.put("T2700150120847", "");
		dataMap_PDSK.put("T2700150121093", "");
		dataMap_PDSK.put("T2700150121102", "");
		dataMap_PDSK.put("T2700150121309", "");
		dataMap_PDSK.put("T2700150121317", "");
		dataMap_PDSK.put("T2700150121325", "");
		dataMap_PDSK.put("T2700150121341", "");
		dataMap_PDSK.put("T2700150121432", "");
		dataMap_PDSK.put("T2700150121449", "");
		dataMap_PDSK.put("T2700150121630", "");
		dataMap_PDSK.put("T2700150121696", "");
		dataMap_PDSK.put("T2700150121804", "");
		dataMap_PDSK.put("T2700150121960", "");
		dataMap_PDSK.put("T2700150122059", "");
		dataMap_PDSK.put("T2700150122141", "");
		dataMap_PDSK.put("T2700150122141", "");
		dataMap_PDSK.put("T2700150122158", "");
		dataMap_PDSK.put("T2700150122158", "");
		dataMap_PDSK.put("T2700150122232", "");
		dataMap_PDSK.put("T2700150122257", "");
		dataMap_PDSK.put("T2700150122257", "");
		dataMap_PDSK.put("T2700150122265", "");
		dataMap_PDSK.put("T2700150122273", "");
		dataMap_PDSK.put("T2700150122281", "");
		dataMap_PDSK.put("T2700150122298", "");
		dataMap_PDSK.put("T2700150122380", "");
		dataMap_PDSK.put("T2700150122430", "");
		dataMap_PDSK.put("T2700150122439", "");
		dataMap_PDSK.put("T2700150122570", "");
		dataMap_PDSK.put("T2700150122587", "");
		dataMap_PDSK.put("T2700150122595", "");
		dataMap_PDSK.put("T2700150122604", "");
		dataMap_PDSK.put("T2700150122752", "");
		dataMap_PDSK.put("T2700150122777", "");
		dataMap_PDSK.put("T2700150123057", "");
		dataMap_PDSK.put("T2700150123065", "");
		dataMap_PDSK.put("T2700150123065", "");
		dataMap_PDSK.put("T2700150123098", "");
		dataMap_PDSK.put("T2700150123131", "");
		dataMap_PDSK.put("T2700150123148", "");
		dataMap_PDSK.put("T2700150123156", "");
		dataMap_PDSK.put("T2700150123164", "");
		dataMap_PDSK.put("T2700150123172", "");
		dataMap_PDSK.put("T2700150123214", "");
		dataMap_PDSK.put("T2700150123222", "");
		dataMap_PDSK.put("T2700150123247", "");
		dataMap_PDSK.put("T2700150123271", "");
		dataMap_PDSK.put("T2700150123387", "");
		dataMap_PDSK.put("T2700150123395", "");
		dataMap_PDSK.put("T2700150123453", "");
		dataMap_PDSK.put("T2700150123494", "");
		dataMap_PDSK.put("T2700150123577", "");
		dataMap_PDSK.put("T2700150123585", "");
		dataMap_PDSK.put("T2700150123701", "");
		dataMap_PDSK.put("T2700150123718", "");
		dataMap_PDSK.put("T2700150123775", "");
		dataMap_PDSK.put("T2700150123800", "");
		dataMap_PDSK.put("T2700150123908", "");
		dataMap_PDSK.put("T2700150124237", "");
		dataMap_PDSK.put("T2700150124286", "");
		dataMap_PDSK.put("T2700150124385", "");
		dataMap_PDSK.put("T2700150124658", "");
		dataMap_PDSK.put("T2700150124749", "");
		dataMap_PDSK.put("T2700150124831", "");
		dataMap_PDSK.put("T2700150125441", "");
		dataMap_PDSK.put("T2700150125598", "");
		dataMap_PDSK.put("T2700150125672", "");
		dataMap_PDSK.put("T2700150125937", "");
		dataMap_PDSK.put("T2700150126497", "");
		dataMap_PDSK.put("T2700150126670", "");
		dataMap_PDSK.put("T2700150126803", "");
		dataMap_PDSK.put("T2700150127025", "");
		dataMap_PDSK.put("T2700150127066", "");
		dataMap_PDSK.put("T2700150127280", "");
		dataMap_PDSK.put("T2700150127958", "");
		dataMap_PDSK.put("T2700150128031", "");
		dataMap_PDSK.put("T2700150128205", "");
		dataMap_PDSK.put("T2700150128403", "");
		dataMap_PDSK.put("T2700150128865", "");
		dataMap_PDSK.put("T2700150128873", "");
		dataMap_PDSK.put("T2700150129269", "");
		dataMap_PDSK.put("T2700150129459", "");
		dataMap_PDSK.put("T2700150129467", "");
		dataMap_PDSK.put("T2700150129707", "");
		dataMap_PDSK.put("T2700150130219", "");
		dataMap_PDSK.put("T2700150130697", "");
		dataMap_PDSK.put("T2700150131183", "");
		dataMap_PDSK.put("T2700150132132", "");
		dataMap_PDSK.put("T2700150132157", "");
		dataMap_PDSK.put("T2700150132330", "");
		dataMap_PDSK.put("T2700150132355", "");
		dataMap_PDSK.put("T2700150132454", "");
		dataMap_PDSK.put("T2700150132487", "");
		dataMap_PDSK.put("T2700150133122", "");
		dataMap_PDSK.put("T2700150133527", "");
		dataMap_PDSK.put("T2700150133618", "");
		dataMap_PDSK.put("T2700150133782", "");
		dataMap_PDSK.put("T2810028156301", "");
		dataMap_PDSK.put("T2810047173723", "");
		dataMap_PDSK.put("T2810126790562", "");
		dataMap_PDSK.put("T2810163944411", "");
		dataMap_PDSK.put("T2810174329123", "");
		dataMap_PDSK.put("T2810206638310", "");
		dataMap_PDSK.put("T2810218599152", "");
		dataMap_PDSK.put("T2810330526902", "");
		dataMap_PDSK.put("T2810340116660", "");
		dataMap_PDSK.put("T2810340116660", "");
		dataMap_PDSK.put("T2810471631049", "");
		dataMap_PDSK.put("T2810477652551", "");
		dataMap_PDSK.put("T2810477652551", "");
		dataMap_PDSK.put("T2810477652551", "");
		dataMap_PDSK.put("T2810520558409", "");
		dataMap_PDSK.put("T2810752808308", "");
		dataMap_PDSK.put("T2810765649714", "");
		dataMap_PDSK.put("T3700150111242", "");
		dataMap_PDSK.put("T3700150112926", "");
		dataMap_PDSK.put("T3700150113404", "");
		dataMap_PDSK.put("T3700150113759", "");
		dataMap_PDSK.put("T3700150113981", "");
		dataMap_PDSK.put("T3700150114096", "");
		dataMap_PDSK.put("T3700150114105", "");
		dataMap_PDSK.put("T3700150114195", "");
		dataMap_PDSK.put("T3700150114237", "");
		dataMap_PDSK.put("T3700150114344", "");
		dataMap_PDSK.put("T3700150114360", "");
		dataMap_PDSK.put("T3700150114583", "");
		dataMap_PDSK.put("T3700150114583", "");
		dataMap_PDSK.put("T3700150114633", "");
		dataMap_PDSK.put("T3700150114674", "");
		dataMap_PDSK.put("T3700150114682", "");
		dataMap_PDSK.put("T3700150114690", "");
		dataMap_PDSK.put("T3700150114699", "");
		dataMap_PDSK.put("T3700150114708", "");
		dataMap_PDSK.put("T3700150114724", "");
		dataMap_PDSK.put("T3700150114740", "");
		dataMap_PDSK.put("T3700150114749", "");
		dataMap_PDSK.put("T3700150114757", "");
		dataMap_PDSK.put("T3700150114765", "");
		dataMap_PDSK.put("T3700150114798", "");
		dataMap_PDSK.put("T3700150115094", "");
		dataMap_PDSK.put("T3700150115103", "");
		dataMap_PDSK.put("T3700150115136", "");
		dataMap_PDSK.put("T3700150115144", "");
		dataMap_PDSK.put("T3700150115169", "");
		dataMap_PDSK.put("T3700150115210", "");
		dataMap_PDSK.put("T3700150115219", "");
		dataMap_PDSK.put("T3700150115235", "");
		dataMap_PDSK.put("T3700150115251", "");
		dataMap_PDSK.put("T3700150115251", "");
		dataMap_PDSK.put("T3700150115284", "");
		dataMap_PDSK.put("T3700150115292", "");
		dataMap_PDSK.put("T3700150115342", "");
		dataMap_PDSK.put("T3700150115350", "");
		dataMap_PDSK.put("T3700150115359", "");
		dataMap_PDSK.put("T3700150115383", "");
		dataMap_PDSK.put("T3700150115383", "");
		dataMap_PDSK.put("T3700150115417", "");
		dataMap_PDSK.put("T3700150115458", "");
		dataMap_PDSK.put("T3700150115466", "");
		dataMap_PDSK.put("T3700150115474", "");
		dataMap_PDSK.put("T3700150115490", "");
		dataMap_PDSK.put("T3700150115516", "");
		dataMap_PDSK.put("T3700150115565", "");
		dataMap_PDSK.put("T3700150115607", "");
		dataMap_PDSK.put("T3700150115615", "");
		dataMap_PDSK.put("T3700150115615", "");
		dataMap_PDSK.put("T3700150115615", "");
		dataMap_PDSK.put("T3700150115623", "");
		dataMap_PDSK.put("T3700150115730", "");
		dataMap_PDSK.put("T3700150115739", "");
		dataMap_PDSK.put("T3700150115813", "");
		dataMap_PDSK.put("T3700150115813", "");
		dataMap_PDSK.put("T3700150115821", "");
		dataMap_PDSK.put("T3700150115854", "");
		dataMap_PDSK.put("T3700150115870", "");
		dataMap_PDSK.put("T3700150115879", "");
		dataMap_PDSK.put("T3700150116002", "");
		dataMap_PDSK.put("T3700150116043", "");
		dataMap_PDSK.put("T3700150116051", "");
		dataMap_PDSK.put("T3700150116076", "");
		dataMap_PDSK.put("T3700150116076", "");
		dataMap_PDSK.put("T3700150116092", "");
		dataMap_PDSK.put("T3700150116324", "");
		dataMap_PDSK.put("T3700150116464", "");
		dataMap_PDSK.put("T3700150116472", "");
		dataMap_PDSK.put("T3700150116571", "");
		dataMap_PDSK.put("T3700150116638", "");
		dataMap_PDSK.put("T3700150116729", "");
		dataMap_PDSK.put("T3700150116737", "");
		dataMap_PDSK.put("T3700150116761", "");
		dataMap_PDSK.put("T3700150116935", "");
		dataMap_PDSK.put("T3700150116943", "");
		dataMap_PDSK.put("T3700150117074", "");
		dataMap_PDSK.put("T3700150117116", "");
		dataMap_PDSK.put("T3700150117124", "");
		dataMap_PDSK.put("T3700150117124", "");
		dataMap_PDSK.put("T3700150117132", "");
		dataMap_PDSK.put("T3700150117140", "");
		dataMap_PDSK.put("T3700150117149", "");
		dataMap_PDSK.put("T3700150117157", "");
		dataMap_PDSK.put("T3700150117157", "");
		dataMap_PDSK.put("T3700150117165", "");
		dataMap_PDSK.put("T3700150117173", "");
		dataMap_PDSK.put("T3700150117173", "");
		dataMap_PDSK.put("T3700150117207", "");
		dataMap_PDSK.put("T3700150117207", "");
		dataMap_PDSK.put("T3700150117396", "");
		dataMap_PDSK.put("T3700150117405", "");
		dataMap_PDSK.put("T3700150117462", "");
		dataMap_PDSK.put("T3700150117462", "");
		dataMap_PDSK.put("T3700150117504", "");
		dataMap_PDSK.put("T3700150117603", "");
		dataMap_PDSK.put("T3700150117660", "");
		dataMap_PDSK.put("T3700150117842", "");
		dataMap_PDSK.put("T3700150117850", "");
		dataMap_PDSK.put("T3700150117859", "");
		dataMap_PDSK.put("T3700150118048", "");
		dataMap_PDSK.put("T3700150118056", "");
		dataMap_PDSK.put("T3700150118064", "");
		dataMap_PDSK.put("T3700150118080", "");
		dataMap_PDSK.put("T3700150118089", "");
		dataMap_PDSK.put("T3700150118097", "");
		dataMap_PDSK.put("T3700150118106", "");
		dataMap_PDSK.put("T3700150118122", "");
		dataMap_PDSK.put("T3700150118122", "");
		dataMap_PDSK.put("T3700150118188", "");
		dataMap_PDSK.put("T3700150118196", "");
		dataMap_PDSK.put("T3700150118205", "");
		dataMap_PDSK.put("T3700150118221", "");
		dataMap_PDSK.put("T3700150118304", "");
		dataMap_PDSK.put("T3700150118345", "");
		dataMap_PDSK.put("T3700150118394", "");
		dataMap_PDSK.put("T3700150118394", "");
		dataMap_PDSK.put("T3700150118411", "");
		dataMap_PDSK.put("T3700150118428", "");
		dataMap_PDSK.put("T3700150118568", "");
		dataMap_PDSK.put("T3700150118683", "");
		dataMap_PDSK.put("T3700150118709", "");
		dataMap_PDSK.put("T3700150118741", "");
		dataMap_PDSK.put("T3700150118799", "");
		dataMap_PDSK.put("T3700150118816", "");
		dataMap_PDSK.put("T3700150118857", "");
		dataMap_PDSK.put("T3700150118873", "");
		dataMap_PDSK.put("T3700150118915", "");
		dataMap_PDSK.put("T3700150118972", "");
		dataMap_PDSK.put("T3700150118972", "");
		dataMap_PDSK.put("T3700150118980", "");
		dataMap_PDSK.put("T3700150119046", "");
		dataMap_PDSK.put("T3700150119054", "");
		dataMap_PDSK.put("T3700150119104", "");
		dataMap_PDSK.put("T3700150119186", "");
		dataMap_PDSK.put("T3700150119252", "");
		dataMap_PDSK.put("T3700150119351", "");
		dataMap_PDSK.put("T3700150119483", "");
		dataMap_PDSK.put("T3700150119525", "");
		dataMap_PDSK.put("T3700150119582", "");
		dataMap_PDSK.put("T3700150119590", "");
		dataMap_PDSK.put("T3700150119599", "");
		dataMap_PDSK.put("T3700150119599", "");
		dataMap_PDSK.put("T3700150119657", "");
		dataMap_PDSK.put("T3700150119665", "");
		dataMap_PDSK.put("T3700150119673", "");
		dataMap_PDSK.put("T3700150119681", "");
		dataMap_PDSK.put("T3700150119748", "");
		dataMap_PDSK.put("T3700150119756", "");
		dataMap_PDSK.put("T3700150119830", "");
		dataMap_PDSK.put("T3700150119855", "");
		dataMap_PDSK.put("T3700150119896", "");
		dataMap_PDSK.put("T3700150119946", "");
		dataMap_PDSK.put("T3700150119987", "");
		dataMap_PDSK.put("T3700150120004", "");
		dataMap_PDSK.put("T3700150120078", "");
		dataMap_PDSK.put("T3700150120268", "");
		dataMap_PDSK.put("T3700150120326", "");
		dataMap_PDSK.put("T3700150120400", "");
		dataMap_PDSK.put("T3700150120409", "");
		dataMap_PDSK.put("T3700150120417", "");
		dataMap_PDSK.put("T3700150120433", "");
		dataMap_PDSK.put("T3700150120474", "");
		dataMap_PDSK.put("T3700150120532", "");
		dataMap_PDSK.put("T3700150120540", "");
		dataMap_PDSK.put("T3700150120573", "");
		dataMap_PDSK.put("T3700150120664", "");
		dataMap_PDSK.put("T3700150120755", "");
		dataMap_PDSK.put("T3700150120763", "");
		dataMap_PDSK.put("T3700150120846", "");
		dataMap_PDSK.put("T3700150120887", "");
		dataMap_PDSK.put("T3700150120887", "");
		dataMap_PDSK.put("T3700150120929", "");
		dataMap_PDSK.put("T3700150121019", "");
		dataMap_PDSK.put("T3700150121101", "");
		dataMap_PDSK.put("T3700150121316", "");
		dataMap_PDSK.put("T3700150121340", "");
		dataMap_PDSK.put("T3700150121431", "");
		dataMap_PDSK.put("T3700150121464", "");
		dataMap_PDSK.put("T3700150121695", "");
		dataMap_PDSK.put("T3700150121803", "");
		dataMap_PDSK.put("T3700150122033", "");
		dataMap_PDSK.put("T3700150122058", "");
		dataMap_PDSK.put("T3700150122140", "");
		dataMap_PDSK.put("T3700150122157", "");
		dataMap_PDSK.put("T3700150122198", "");
		dataMap_PDSK.put("T3700150122215", "");
		dataMap_PDSK.put("T3700150122231", "");
		dataMap_PDSK.put("T3700150122280", "");
		dataMap_PDSK.put("T3700150122289", "");
		dataMap_PDSK.put("T3700150122297", "");
		dataMap_PDSK.put("T3700150122371", "");
		dataMap_PDSK.put("T3700150122438", "");
		dataMap_PDSK.put("T3700150122487", "");
		dataMap_PDSK.put("T3700150122529", "");
		dataMap_PDSK.put("T3700150122561", "");
		dataMap_PDSK.put("T3700150122586", "");
		dataMap_PDSK.put("T3700150122751", "");
		dataMap_PDSK.put("T3700150122776", "");
		dataMap_PDSK.put("T3700150122792", "");
		dataMap_PDSK.put("T3700150123064", "");
		dataMap_PDSK.put("T3700150123130", "");
		dataMap_PDSK.put("T3700150123147", "");
		dataMap_PDSK.put("T3700150123155", "");
		dataMap_PDSK.put("T3700150123213", "");
		dataMap_PDSK.put("T3700150123221", "");
		dataMap_PDSK.put("T3700150123246", "");
		dataMap_PDSK.put("T3700150123270", "");
		dataMap_PDSK.put("T3700150123394", "");
		dataMap_PDSK.put("T3700150123584", "");
		dataMap_PDSK.put("T3700150123700", "");
		dataMap_PDSK.put("T3700150123717", "");
		dataMap_PDSK.put("T3700150123774", "");
		dataMap_PDSK.put("T3700150123907", "");
		dataMap_PDSK.put("T3700150124285", "");
		dataMap_PDSK.put("T3700150124384", "");
		dataMap_PDSK.put("T3700150124467", "");
		dataMap_PDSK.put("T3700150124657", "");
		dataMap_PDSK.put("T3700150124748", "");
		dataMap_PDSK.put("T3700150124756", "");
		dataMap_PDSK.put("T3700150124830", "");
		dataMap_PDSK.put("T3700150124913", "");
		dataMap_PDSK.put("T3700150125143", "");
		dataMap_PDSK.put("T3700150125390", "");
		dataMap_PDSK.put("T3700150125424", "");
		dataMap_PDSK.put("T3700150125440", "");
		dataMap_PDSK.put("T3700150125597", "");
		dataMap_PDSK.put("T3700150125671", "");
		dataMap_PDSK.put("T3700150125936", "");
		dataMap_PDSK.put("T3700150126208", "");
		dataMap_PDSK.put("T3700150126513", "");
		dataMap_PDSK.put("T3700150126802", "");
		dataMap_PDSK.put("T3700150127445", "");
		dataMap_PDSK.put("T3700150127635", "");
		dataMap_PDSK.put("T3700150127957", "");
		dataMap_PDSK.put("T3700150128030", "");
		dataMap_PDSK.put("T3700150128402", "");
		dataMap_PDSK.put("T3700150128443", "");
		dataMap_PDSK.put("T3700150128864", "");
		dataMap_PDSK.put("T3700150128872", "");
		dataMap_PDSK.put("T3700150129169", "");
		dataMap_PDSK.put("T3700150129466", "");
		dataMap_PDSK.put("T3700150129706", "");
		dataMap_PDSK.put("T3700150129739", "");
		dataMap_PDSK.put("T3700150130696", "");
		dataMap_PDSK.put("T3700150130738", "");
		dataMap_PDSK.put("T3700150131109", "");
		dataMap_PDSK.put("T3700150132156", "");
		dataMap_PDSK.put("T3700150132453", "");
		dataMap_PDSK.put("T3700150132486", "");
		dataMap_PDSK.put("T3700150132734", "");
		dataMap_PDSK.put("T3800051000047", "");
		dataMap_PDSK.put("T3810212382397", "");
		dataMap_PDSK.put("T3810403983747", "");
		dataMap_PDSK.put("T3810599252741", "");
		dataMap_PDSK.put("T3810599252741", "");
		dataMap_PDSK.put("T3810764840462", "");
		dataMap_PDSK.put("T3810771016478", "");
		dataMap_PDSK.put("T3810848097731", "");
		dataMap_PDSK.put("T3810904250052", "");
		dataMap_PDSK.put("T3810926330765", "");
		dataMap_PDSK.put("T3810970294783", "");
		dataMap_PDSK.put("T3810970294783", "");
		dataMap_PDSK.put("T4700150111241", "");
		dataMap_PDSK.put("T4700150111489", "");
		dataMap_PDSK.put("T4700150113758", "");
		dataMap_PDSK.put("T4700150114104", "");
		dataMap_PDSK.put("T4700150114112", "");
		dataMap_PDSK.put("T4700150114194", "");
		dataMap_PDSK.put("T4700150114236", "");
		dataMap_PDSK.put("T4700150114665", "");
		dataMap_PDSK.put("T4700150114681", "");
		dataMap_PDSK.put("T4700150114698", "");
		dataMap_PDSK.put("T4700150114707", "");
		dataMap_PDSK.put("T4700150114715", "");
		dataMap_PDSK.put("T4700150114723", "");
		dataMap_PDSK.put("T4700150114756", "");
		dataMap_PDSK.put("T4700150114764", "");
		dataMap_PDSK.put("T4700150114772", "");
		dataMap_PDSK.put("T4700150114797", "");
		dataMap_PDSK.put("T4700150114814", "");
		dataMap_PDSK.put("T4700150115093", "");
		dataMap_PDSK.put("T4700150115102", "");
		dataMap_PDSK.put("T4700150115168", "");
		dataMap_PDSK.put("T4700150115218", "");
		dataMap_PDSK.put("T4700150115259", "");
		dataMap_PDSK.put("T4700150115275", "");
		dataMap_PDSK.put("T4700150115283", "");
		dataMap_PDSK.put("T4700150115291", "");
		dataMap_PDSK.put("T4700150115291", "");
		dataMap_PDSK.put("T4700150115291", "");
		dataMap_PDSK.put("T4700150115309", "");
		dataMap_PDSK.put("T4700150115358", "");
		dataMap_PDSK.put("T4700150115366", "");
		dataMap_PDSK.put("T4700150115374", "");
		dataMap_PDSK.put("T4700150115382", "");
		dataMap_PDSK.put("T4700150115416", "");
		dataMap_PDSK.put("T4700150115424", "");
		dataMap_PDSK.put("T4700150115424", "");
		dataMap_PDSK.put("T4700150115432", "");
		dataMap_PDSK.put("T4700150115457", "");
		dataMap_PDSK.put("T4700150115465", "");
		dataMap_PDSK.put("T4700150115564", "");
		dataMap_PDSK.put("T4700150115572", "");
		dataMap_PDSK.put("T4700150115606", "");
		dataMap_PDSK.put("T4700150115614", "");
		dataMap_PDSK.put("T4700150115622", "");
		dataMap_PDSK.put("T4700150115738", "");
		dataMap_PDSK.put("T4700150115738", "");
		dataMap_PDSK.put("T4700150115795", "");
		dataMap_PDSK.put("T4700150115812", "");
		dataMap_PDSK.put("T4700150115829", "");
		dataMap_PDSK.put("T4700150115853", "");
		dataMap_PDSK.put("T4700150115878", "");
		dataMap_PDSK.put("T4700150115878", "");
		dataMap_PDSK.put("T4700150116001", "");
		dataMap_PDSK.put("T4700150116042", "");
		dataMap_PDSK.put("T4700150116050", "");
		dataMap_PDSK.put("T4700150116059", "");
		dataMap_PDSK.put("T4700150116075", "");
		dataMap_PDSK.put("T4700150116083", "");
		dataMap_PDSK.put("T4700150116083", "");
		dataMap_PDSK.put("T4700150116091", "");
		dataMap_PDSK.put("T4700150116109", "");
		dataMap_PDSK.put("T4700150116406", "");
		dataMap_PDSK.put("T4700150116463", "");
		dataMap_PDSK.put("T4700150116471", "");
		dataMap_PDSK.put("T4700150116513", "");
		dataMap_PDSK.put("T4700150116570", "");
		dataMap_PDSK.put("T4700150116629", "");
		dataMap_PDSK.put("T4700150116637", "");
		dataMap_PDSK.put("T4700150116637", "");
		dataMap_PDSK.put("T4700150116769", "");
		dataMap_PDSK.put("T4700150116934", "");
		dataMap_PDSK.put("T4700150116942", "");
		dataMap_PDSK.put("T4700150116950", "");
		dataMap_PDSK.put("T4700150116959", "");
		dataMap_PDSK.put("T4700150117049", "");
		dataMap_PDSK.put("T4700150117057", "");
		dataMap_PDSK.put("T4700150117073", "");
		dataMap_PDSK.put("T4700150117115", "");
		dataMap_PDSK.put("T4700150117115", "");
		dataMap_PDSK.put("T4700150117123", "");
		dataMap_PDSK.put("T4700150117131", "");
		dataMap_PDSK.put("T4700150117131", "");
		dataMap_PDSK.put("T4700150117148", "");
		dataMap_PDSK.put("T4700150117148", "");
		dataMap_PDSK.put("T4700150117156", "");
		dataMap_PDSK.put("T4700150117164", "");
		dataMap_PDSK.put("T4700150117164", "");
		dataMap_PDSK.put("T4700150117172", "");
		dataMap_PDSK.put("T4700150117354", "");
		dataMap_PDSK.put("T4700150117379", "");
		dataMap_PDSK.put("T4700150117379", "");
		dataMap_PDSK.put("T4700150117461", "");
		dataMap_PDSK.put("T4700150117461", "");
		dataMap_PDSK.put("T4700150117503", "");
		dataMap_PDSK.put("T4700150117503", "");
		dataMap_PDSK.put("T4700150117577", "");
		dataMap_PDSK.put("T4700150117585", "");
		dataMap_PDSK.put("T4700150117602", "");
		dataMap_PDSK.put("T4700150117841", "");
		dataMap_PDSK.put("T4700150117858", "");
		dataMap_PDSK.put("T4700150117858", "");
		dataMap_PDSK.put("T4700150117899", "");
		dataMap_PDSK.put("T4700150118014", "");
		dataMap_PDSK.put("T4700150118030", "");
		dataMap_PDSK.put("T4700150118055", "");
		dataMap_PDSK.put("T4700150118088", "");
		dataMap_PDSK.put("T4700150118096", "");
		dataMap_PDSK.put("T4700150118105", "");
		dataMap_PDSK.put("T4700150118187", "");
		dataMap_PDSK.put("T4700150118195", "");
		dataMap_PDSK.put("T4700150118204", "");
		dataMap_PDSK.put("T4700150118212", "");
		dataMap_PDSK.put("T4700150118229", "");
		dataMap_PDSK.put("T4700150118344", "");
		dataMap_PDSK.put("T4700150118410", "");
		dataMap_PDSK.put("T4700150118419", "");
		dataMap_PDSK.put("T4700150118419", "");
		dataMap_PDSK.put("T4700150118427", "");
		dataMap_PDSK.put("T4700150118427", "");
		dataMap_PDSK.put("T4700150118690", "");
		dataMap_PDSK.put("T4700150118708", "");
		dataMap_PDSK.put("T4700150118749", "");
		dataMap_PDSK.put("T4700150118798", "");
		dataMap_PDSK.put("T4700150118856", "");
		dataMap_PDSK.put("T4700150118872", "");
		dataMap_PDSK.put("T4700150118914", "");
		dataMap_PDSK.put("T4700150118988", "");
		dataMap_PDSK.put("T4700150119037", "");
		dataMap_PDSK.put("T4700150119045", "");
		dataMap_PDSK.put("T4700150119053", "");
		dataMap_PDSK.put("T4700150119193", "");
		dataMap_PDSK.put("T4700150119235", "");
		dataMap_PDSK.put("T4700150119350", "");
		dataMap_PDSK.put("T4700150119359", "");
		dataMap_PDSK.put("T4700150119367", "");
		dataMap_PDSK.put("T4700150119400", "");
		dataMap_PDSK.put("T4700150119549", "");
		dataMap_PDSK.put("T4700150119581", "");
		dataMap_PDSK.put("T4700150119598", "");
		dataMap_PDSK.put("T4700150119672", "");
		dataMap_PDSK.put("T4700150119680", "");
		dataMap_PDSK.put("T4700150119747", "");
		dataMap_PDSK.put("T4700150119755", "");
		dataMap_PDSK.put("T4700150119763", "");
		dataMap_PDSK.put("T4700150119838", "");
		dataMap_PDSK.put("T4700150119945", "");
		dataMap_PDSK.put("T4700150119978", "");
		dataMap_PDSK.put("T4700150119986", "");
		dataMap_PDSK.put("T4700150120003", "");
		dataMap_PDSK.put("T4700150120069", "");
		dataMap_PDSK.put("T4700150120077", "");
		dataMap_PDSK.put("T4700150120143", "");
		dataMap_PDSK.put("T4700150120283", "");
		dataMap_PDSK.put("T4700150120309", "");
		dataMap_PDSK.put("T4700150120333", "");
		dataMap_PDSK.put("T4700150120374", "");
		dataMap_PDSK.put("T4700150120399", "");
		dataMap_PDSK.put("T4700150120399", "");
		dataMap_PDSK.put("T4700150120408", "");
		dataMap_PDSK.put("T4700150120416", "");
		dataMap_PDSK.put("T4700150120432", "");
		dataMap_PDSK.put("T4700150120473", "");
		dataMap_PDSK.put("T4700150120531", "");
		dataMap_PDSK.put("T4700150120671", "");
		dataMap_PDSK.put("T4700150120762", "");
		dataMap_PDSK.put("T4700150120779", "");
		dataMap_PDSK.put("T4700150120845", "");
		dataMap_PDSK.put("T4700150120878", "");
		dataMap_PDSK.put("T4700150120886", "");
		dataMap_PDSK.put("T4700150121018", "");
		dataMap_PDSK.put("T4700150121026", "");
		dataMap_PDSK.put("T4700150121100", "");
		dataMap_PDSK.put("T4700150121199", "");
		dataMap_PDSK.put("T4700150121315", "");
		dataMap_PDSK.put("T4700150121430", "");
		dataMap_PDSK.put("T4700150121579", "");
		dataMap_PDSK.put("T4700150121629", "");
		dataMap_PDSK.put("T4700150121694", "");
		dataMap_PDSK.put("T4700150121694", "");
		dataMap_PDSK.put("T4700150121959", "");
		dataMap_PDSK.put("T4700150121967", "");
		dataMap_PDSK.put("T4700150121975", "");
		dataMap_PDSK.put("T4700150122057", "");
		dataMap_PDSK.put("T4700150122065", "");
		dataMap_PDSK.put("T4700150122156", "");
		dataMap_PDSK.put("T4700150122164", "");
		dataMap_PDSK.put("T4700150122172", "");
		dataMap_PDSK.put("T4700150122255", "");
		dataMap_PDSK.put("T4700150122288", "");
		dataMap_PDSK.put("T4700150122296", "");
		dataMap_PDSK.put("T4700150122387", "");
		dataMap_PDSK.put("T4700150122412", "");
		dataMap_PDSK.put("T4700150122437", "");
		dataMap_PDSK.put("T4700150122437", "");
		dataMap_PDSK.put("T4700150122486", "");
		dataMap_PDSK.put("T4700150122528", "");
		dataMap_PDSK.put("T4700150122560", "");
		dataMap_PDSK.put("T4700150122577", "");
		dataMap_PDSK.put("T4700150122602", "");
		dataMap_PDSK.put("T4700150122726", "");
		dataMap_PDSK.put("T4700150122775", "");
		dataMap_PDSK.put("T4700150122791", "");
		dataMap_PDSK.put("T4700150123006", "");
		dataMap_PDSK.put("T4700150123055", "");
		dataMap_PDSK.put("T4700150123055", "");
		dataMap_PDSK.put("T4700150123063", "");
		dataMap_PDSK.put("T4700150123113", "");
		dataMap_PDSK.put("T4700150123121", "");
		dataMap_PDSK.put("T4700150123154", "");
		dataMap_PDSK.put("T4700150123170", "");
		dataMap_PDSK.put("T4700150123212", "");
		dataMap_PDSK.put("T4700150123220", "");
		dataMap_PDSK.put("T4700150123245", "");
		dataMap_PDSK.put("T4700150123393", "");
		dataMap_PDSK.put("T4700150123575", "");
		dataMap_PDSK.put("T4700150123583", "");
		dataMap_PDSK.put("T4700150123591", "");
		dataMap_PDSK.put("T4700150123600", "");
		dataMap_PDSK.put("T4700150123773", "");
		dataMap_PDSK.put("T4700150123906", "");
		dataMap_PDSK.put("T4700150124284", "");
		dataMap_PDSK.put("T4700150124383", "");
		dataMap_PDSK.put("T4700150124466", "");
		dataMap_PDSK.put("T4700150124656", "");
		dataMap_PDSK.put("T4700150124739", "");
		dataMap_PDSK.put("T4700150124747", "");
		dataMap_PDSK.put("T4700150125209", "");
		dataMap_PDSK.put("T4700150125423", "");
		dataMap_PDSK.put("T4700150125596", "");
		dataMap_PDSK.put("T4700150125670", "");
		dataMap_PDSK.put("T4700150125935", "");
		dataMap_PDSK.put("T4700150126512", "");
		dataMap_PDSK.put("T4700150126644", "");
		dataMap_PDSK.put("T4700150126801", "");
		dataMap_PDSK.put("T4700150126826", "");
		dataMap_PDSK.put("T4700150127171", "");
		dataMap_PDSK.put("T4700150127246", "");
		dataMap_PDSK.put("T4700150127386", "");
		dataMap_PDSK.put("T4700150127634", "");
		dataMap_PDSK.put("T4700150127956", "");
		dataMap_PDSK.put("T4700150127980", "");
		dataMap_PDSK.put("T4700150128293", "");
		dataMap_PDSK.put("T4700150128401", "");
		dataMap_PDSK.put("T4700150128665", "");
		dataMap_PDSK.put("T4700150128871", "");
		dataMap_PDSK.put("T4700150129168", "");
		dataMap_PDSK.put("T4700150129449", "");
		dataMap_PDSK.put("T4700150129465", "");
		dataMap_PDSK.put("T4700150129705", "");
		dataMap_PDSK.put("T4700150129738", "");
		dataMap_PDSK.put("T4700150130225", "");
		dataMap_PDSK.put("T4700150130695", "");
		dataMap_PDSK.put("T4700150130737", "");
		dataMap_PDSK.put("T4700150131108", "");
		dataMap_PDSK.put("T4700150131462", "");
		dataMap_PDSK.put("T4700150131974", "");
		dataMap_PDSK.put("T4700150131982", "");
		dataMap_PDSK.put("T4700150132155", "");
		dataMap_PDSK.put("T4700150132320", "");
		dataMap_PDSK.put("T4700150132329", "");
		dataMap_PDSK.put("T4700150132485", "");
		dataMap_PDSK.put("T4700150133021", "");
		dataMap_PDSK.put("T4700150133525", "");
		dataMap_PDSK.put("T4810005497245", "");
		dataMap_PDSK.put("T4810238392667", "");
		dataMap_PDSK.put("T4810243054650", "");
		dataMap_PDSK.put("T4810298853161", "");
		dataMap_PDSK.put("T4810330040456", "");
		dataMap_PDSK.put("T4810336411205", "");
		dataMap_PDSK.put("T4810420371100", "");
		dataMap_PDSK.put("T4810424215047", "");
		dataMap_PDSK.put("T4810525906312", "");
		dataMap_PDSK.put("T4810772701300", "");
		dataMap_PDSK.put("T4810945612597", "");
		dataMap_PDSK.put("T5700150109350", "");
		dataMap_PDSK.put("T5700150111744", "");
		dataMap_PDSK.put("T5700150112189", "");
		dataMap_PDSK.put("T5700150113484", "");
		dataMap_PDSK.put("T5700150113757", "");
		dataMap_PDSK.put("T5700150114094", "");
		dataMap_PDSK.put("T5700150114103", "");
		dataMap_PDSK.put("T5700150114111", "");
		dataMap_PDSK.put("T5700150114193", "");
		dataMap_PDSK.put("T5700150114235", "");
		dataMap_PDSK.put("T5700150114664", "");
		dataMap_PDSK.put("T5700150114672", "");
		dataMap_PDSK.put("T5700150114680", "");
		dataMap_PDSK.put("T5700150114689", "");
		dataMap_PDSK.put("T5700150114697", "");
		dataMap_PDSK.put("T5700150114706", "");
		dataMap_PDSK.put("T5700150114714", "");
		dataMap_PDSK.put("T5700150114722", "");
		dataMap_PDSK.put("T5700150114739", "");
		dataMap_PDSK.put("T5700150114755", "");
		dataMap_PDSK.put("T5700150114763", "");
		dataMap_PDSK.put("T5700150114771", "");
		dataMap_PDSK.put("T5700150114796", "");
		dataMap_PDSK.put("T5700150114813", "");
		dataMap_PDSK.put("T5700150115092", "");
		dataMap_PDSK.put("T5700150115101", "");
		dataMap_PDSK.put("T5700150115150", "");
		dataMap_PDSK.put("T5700150115150", "");
		dataMap_PDSK.put("T5700150115167", "");
		dataMap_PDSK.put("T5700150115209", "");
		dataMap_PDSK.put("T5700150115217", "");
		dataMap_PDSK.put("T5700150115258", "");
		dataMap_PDSK.put("T5700150115274", "");
		dataMap_PDSK.put("T5700150115282", "");
		dataMap_PDSK.put("T5700150115290", "");
		dataMap_PDSK.put("T5700150115308", "");
		dataMap_PDSK.put("T5700150115349", "");
		dataMap_PDSK.put("T5700150115357", "");
		dataMap_PDSK.put("T5700150115365", "");
		dataMap_PDSK.put("T5700150115373", "");
		dataMap_PDSK.put("T5700150115381", "");
		dataMap_PDSK.put("T5700150115415", "");
		dataMap_PDSK.put("T5700150115423", "");
		dataMap_PDSK.put("T5700150115431", "");
		dataMap_PDSK.put("T5700150115464", "");
		dataMap_PDSK.put("T5700150115489", "");
		dataMap_PDSK.put("T5700150115571", "");
		dataMap_PDSK.put("T5700150115605", "");
		dataMap_PDSK.put("T5700150115613", "");
		dataMap_PDSK.put("T5700150115621", "");
		dataMap_PDSK.put("T5700150115737", "");
		dataMap_PDSK.put("T5700150115794", "");
		dataMap_PDSK.put("T5700150115811", "");
		dataMap_PDSK.put("T5700150115828", "");
		dataMap_PDSK.put("T5700150115860", "");
		dataMap_PDSK.put("T5700150115869", "");
		dataMap_PDSK.put("T5700150116058", "");
		dataMap_PDSK.put("T5700150116066", "");
		dataMap_PDSK.put("T5700150116082", "");
		dataMap_PDSK.put("T5700150116090", "");
		dataMap_PDSK.put("T5700150116099", "");
		dataMap_PDSK.put("T5700150116099", "");
		dataMap_PDSK.put("T5700150116108", "");
		dataMap_PDSK.put("T5700150116215", "");
		dataMap_PDSK.put("T5700150116388", "");
		dataMap_PDSK.put("T5700150116438", "");
		dataMap_PDSK.put("T5700150116462", "");
		dataMap_PDSK.put("T5700150116470", "");
		dataMap_PDSK.put("T5700150116495", "");
		dataMap_PDSK.put("T5700150116504", "");
		dataMap_PDSK.put("T5700150116553", "");
		dataMap_PDSK.put("T5700150116628", "");
		dataMap_PDSK.put("T5700150116636", "");
		dataMap_PDSK.put("T5700150116636", "");
		dataMap_PDSK.put("T5700150116727", "");
		dataMap_PDSK.put("T5700150116768", "");
		dataMap_PDSK.put("T5700150116784", "");
		dataMap_PDSK.put("T5700150116917", "");
		dataMap_PDSK.put("T5700150116933", "");
		dataMap_PDSK.put("T5700150116941", "");
		dataMap_PDSK.put("T5700150116966", "");
		dataMap_PDSK.put("T5700150117056", "");
		dataMap_PDSK.put("T5700150117064", "");
		dataMap_PDSK.put("T5700150117072", "");
		dataMap_PDSK.put("T5700150117122", "");
		dataMap_PDSK.put("T5700150117130", "");
		dataMap_PDSK.put("T5700150117139", "");
		dataMap_PDSK.put("T5700150117147", "");
		dataMap_PDSK.put("T5700150117147", "");
		dataMap_PDSK.put("T5700150117155", "");
		dataMap_PDSK.put("T5700150117163", "");
		dataMap_PDSK.put("T5700150117171", "");
		dataMap_PDSK.put("T5700150117221", "");
		dataMap_PDSK.put("T5700150117460", "");
		dataMap_PDSK.put("T5700150117460", "");
		dataMap_PDSK.put("T5700150117502", "");
		dataMap_PDSK.put("T5700150117510", "");
		dataMap_PDSK.put("T5700150117584", "");
		dataMap_PDSK.put("T5700150117584", "");
		dataMap_PDSK.put("T5700150117592", "");
		dataMap_PDSK.put("T5700150117601", "");
		dataMap_PDSK.put("T5700150117675", "");
		dataMap_PDSK.put("T5700150117733", "");
		dataMap_PDSK.put("T5700150117849", "");
		dataMap_PDSK.put("T5700150117849", "");
		dataMap_PDSK.put("T5700150117857", "");
		dataMap_PDSK.put("T5700150117898", "");
		dataMap_PDSK.put("T5700150118038", "");
		dataMap_PDSK.put("T5700150118046", "");
		dataMap_PDSK.put("T5700150118054", "");
		dataMap_PDSK.put("T5700150118087", "");
		dataMap_PDSK.put("T5700150118095", "");
		dataMap_PDSK.put("T5700150118104", "");
		dataMap_PDSK.put("T5700150118120", "");
		dataMap_PDSK.put("T5700150118194", "");
		dataMap_PDSK.put("T5700150118203", "");
		dataMap_PDSK.put("T5700150118228", "");
		dataMap_PDSK.put("T5700150118285", "");
		dataMap_PDSK.put("T5700150118302", "");
		dataMap_PDSK.put("T5700150118351", "");
		dataMap_PDSK.put("T5700150118384", "");
		dataMap_PDSK.put("T5700150118418", "");
		dataMap_PDSK.put("T5700150118426", "");
		dataMap_PDSK.put("T5700150118608", "");
		dataMap_PDSK.put("T5700150118707", "");
		dataMap_PDSK.put("T5700150118748", "");
		dataMap_PDSK.put("T5700150118855", "");
		dataMap_PDSK.put("T5700150118871", "");
		dataMap_PDSK.put("T5700150118913", "");
		dataMap_PDSK.put("T5700150118954", "");
		dataMap_PDSK.put("T5700150118979", "");
		dataMap_PDSK.put("T5700150118987", "");
		dataMap_PDSK.put("T5700150119028", "");
		dataMap_PDSK.put("T5700150119028", "");
		dataMap_PDSK.put("T5700150119036", "");
		dataMap_PDSK.put("T5700150119044", "");
		dataMap_PDSK.put("T5700150119110", "");
		dataMap_PDSK.put("T5700150119168", "");
		dataMap_PDSK.put("T5700150119184", "");
		dataMap_PDSK.put("T5700150119358", "");
		dataMap_PDSK.put("T5700150119399", "");
		dataMap_PDSK.put("T5700150119548", "");
		dataMap_PDSK.put("T5700150119556", "");
		dataMap_PDSK.put("T5700150119580", "");
		dataMap_PDSK.put("T5700150119589", "");
		dataMap_PDSK.put("T5700150119597", "");
		dataMap_PDSK.put("T5700150119639", "");
		dataMap_PDSK.put("T5700150119671", "");
		dataMap_PDSK.put("T5700150119754", "");
		dataMap_PDSK.put("T5700150119762", "");
		dataMap_PDSK.put("T5700150119795", "");
		dataMap_PDSK.put("T5700150119795", "");
		dataMap_PDSK.put("T5700150119944", "");
		dataMap_PDSK.put("T5700150119952", "");
		dataMap_PDSK.put("T5700150119985", "");
		dataMap_PDSK.put("T5700150120002", "");
		dataMap_PDSK.put("T5700150120010", "");
		dataMap_PDSK.put("T5700150120068", "");
		dataMap_PDSK.put("T5700150120076", "");
		dataMap_PDSK.put("T5700150120191", "");
		dataMap_PDSK.put("T5700150120209", "");
		dataMap_PDSK.put("T5700150120324", "");
		dataMap_PDSK.put("T5700150120332", "");
		dataMap_PDSK.put("T5700150120340", "");
		dataMap_PDSK.put("T5700150120415", "");
		dataMap_PDSK.put("T5700150120431", "");
		dataMap_PDSK.put("T5700150120472", "");
		dataMap_PDSK.put("T5700150120530", "");
		dataMap_PDSK.put("T5700150120539", "");
		dataMap_PDSK.put("T5700150120547", "");
		dataMap_PDSK.put("T5700150120670", "");
		dataMap_PDSK.put("T5700150120745", "");
		dataMap_PDSK.put("T5700150120761", "");
		dataMap_PDSK.put("T5700150120778", "");
		dataMap_PDSK.put("T5700150120794", "");
		dataMap_PDSK.put("T5700150120885", "");
		dataMap_PDSK.put("T5700150121025", "");
		dataMap_PDSK.put("T5700150121099", "");
		dataMap_PDSK.put("T5700150121256", "");
		dataMap_PDSK.put("T5700150121314", "");
		dataMap_PDSK.put("T5700150121330", "");
		dataMap_PDSK.put("T5700150121339", "");
		dataMap_PDSK.put("T5700150121438", "");
		dataMap_PDSK.put("T5700150121495", "");
		dataMap_PDSK.put("T5700150121628", "");
		dataMap_PDSK.put("T5700150121958", "");
		dataMap_PDSK.put("T5700150121958", "");
		dataMap_PDSK.put("T5700150121966", "");
		dataMap_PDSK.put("T5700150121974", "");
		dataMap_PDSK.put("T5700150122031", "");
		dataMap_PDSK.put("T5700150122031", "");
		dataMap_PDSK.put("T5700150122139", "");
		dataMap_PDSK.put("T5700150122155", "");
		dataMap_PDSK.put("T5700150122163", "");
		dataMap_PDSK.put("T5700150122205", "");
		dataMap_PDSK.put("T5700150122221", "");
		dataMap_PDSK.put("T5700150122254", "");
		dataMap_PDSK.put("T5700150122279", "");
		dataMap_PDSK.put("T5700150122287", "");
		dataMap_PDSK.put("T5700150122295", "");
		dataMap_PDSK.put("T5700150122386", "");
		dataMap_PDSK.put("T5700150122411", "");
		dataMap_PDSK.put("T5700150122436", "");
		dataMap_PDSK.put("T5700150122510", "");
		dataMap_PDSK.put("T5700150122510", "");
		dataMap_PDSK.put("T5700150122527", "");
		dataMap_PDSK.put("T5700150122543", "");
		dataMap_PDSK.put("T5700150122584", "");
		dataMap_PDSK.put("T5700150122601", "");
		dataMap_PDSK.put("T5700150122618", "");
		dataMap_PDSK.put("T5700150122642", "");
		dataMap_PDSK.put("T5700150122725", "");
		dataMap_PDSK.put("T5700150122782", "");
		dataMap_PDSK.put("T5700150123054", "");
		dataMap_PDSK.put("T5700150123062", "");
		dataMap_PDSK.put("T5700150123120", "");
		dataMap_PDSK.put("T5700150123153", "");
		dataMap_PDSK.put("T5700150123244", "");
		dataMap_PDSK.put("T5700150123269", "");
		dataMap_PDSK.put("T5700150123376", "");
		dataMap_PDSK.put("T5700150123392", "");
		dataMap_PDSK.put("T5700150123574", "");
		dataMap_PDSK.put("T5700150123582", "");
		dataMap_PDSK.put("T5700150123590", "");
		dataMap_PDSK.put("T5700150123599", "");
		dataMap_PDSK.put("T5700150124283", "");
		dataMap_PDSK.put("T5700150124465", "");
		dataMap_PDSK.put("T5700150124655", "");
		dataMap_PDSK.put("T5700150124738", "");
		dataMap_PDSK.put("T5700150124795", "");
		dataMap_PDSK.put("T5700150124829", "");
		dataMap_PDSK.put("T5700150125141", "");
		dataMap_PDSK.put("T5700150125389", "");
		dataMap_PDSK.put("T5700150125777", "");
		dataMap_PDSK.put("T5700150125934", "");
		dataMap_PDSK.put("T5700150126635", "");
		dataMap_PDSK.put("T5700150126643", "");
		dataMap_PDSK.put("T5700150126800", "");
		dataMap_PDSK.put("T5700150126932", "");
		dataMap_PDSK.put("T5700150127393", "");
		dataMap_PDSK.put("T5700150127633", "");
		dataMap_PDSK.put("T5700150128029", "");
		dataMap_PDSK.put("T5700150128292", "");
		dataMap_PDSK.put("T5700150128664", "");
		dataMap_PDSK.put("T5700150128870", "");
		dataMap_PDSK.put("T5700150129448", "");
		dataMap_PDSK.put("T5700150129464", "");
		dataMap_PDSK.put("T5700150129704", "");
		dataMap_PDSK.put("T5700150129737", "");
		dataMap_PDSK.put("T5700150130224", "");
		dataMap_PDSK.put("T5700150130694", "");
		dataMap_PDSK.put("T5700150130728", "");
		dataMap_PDSK.put("T5700150130736", "");
		dataMap_PDSK.put("T5700150131065", "");
		dataMap_PDSK.put("T5700150131107", "");
		dataMap_PDSK.put("T5700150131387", "");
		dataMap_PDSK.put("T5700150131461", "");
		dataMap_PDSK.put("T5700150131494", "");
		dataMap_PDSK.put("T5700150131973", "");
		dataMap_PDSK.put("T5700150132154", "");
		dataMap_PDSK.put("T5700150132328", "");
		dataMap_PDSK.put("T5700150132385", "");
		dataMap_PDSK.put("T5700150132484", "");
		dataMap_PDSK.put("T5700150133020", "");
		dataMap_PDSK.put("T5810003344407", "");
		dataMap_PDSK.put("T5810006923082", "");
		dataMap_PDSK.put("T5810229321741", "");
		dataMap_PDSK.put("T5810247092126", "");
		dataMap_PDSK.put("T5810312617163", "");
		dataMap_PDSK.put("T5810335525558", "");
		dataMap_PDSK.put("T5810631015882", "");
		dataMap_PDSK.put("T5810733993199", "");
		dataMap_PDSK.put("T5810791247778", "");
		dataMap_PDSK.put("T5810824629423", "");
		dataMap_PDSK.put("T5810890381148", "");
		dataMap_PDSK.put("T5810917895716", "");
		dataMap_PDSK.put("T6700150099137", "");
		dataMap_PDSK.put("T6700150110943", "");
		dataMap_PDSK.put("T6700150111512", "");
		dataMap_PDSK.put("T6700150113252", "");
		dataMap_PDSK.put("T6700150113335", "");
		dataMap_PDSK.put("T6700150113343", "");
		dataMap_PDSK.put("T6700150113401", "");
		dataMap_PDSK.put("T6700150113483", "");
		dataMap_PDSK.put("T6700150113756", "");
		dataMap_PDSK.put("T6700150114093", "");
		dataMap_PDSK.put("T6700150114102", "");
		dataMap_PDSK.put("T6700150114110", "");
		dataMap_PDSK.put("T6700150114168", "");
		dataMap_PDSK.put("T6700150114192", "");
		dataMap_PDSK.put("T6700150114234", "");
		dataMap_PDSK.put("T6700150114408", "");
		dataMap_PDSK.put("T6700150114688", "");
		dataMap_PDSK.put("T6700150114696", "");
		dataMap_PDSK.put("T6700150114705", "");
		dataMap_PDSK.put("T6700150114713", "");
		dataMap_PDSK.put("T6700150114713", "");
		dataMap_PDSK.put("T6700150114762", "");
		dataMap_PDSK.put("T6700150114770", "");
		dataMap_PDSK.put("T6700150114795", "");
		dataMap_PDSK.put("T6700150114812", "");
		dataMap_PDSK.put("T6700150115091", "");
		dataMap_PDSK.put("T6700150115100", "");
		dataMap_PDSK.put("T6700150115166", "");
		dataMap_PDSK.put("T6700150115216", "");
		dataMap_PDSK.put("T6700150115224", "");
		dataMap_PDSK.put("T6700150115257", "");
		dataMap_PDSK.put("T6700150115273", "");
		dataMap_PDSK.put("T6700150115281", "");
		dataMap_PDSK.put("T6700150115307", "");
		dataMap_PDSK.put("T6700150115348", "");
		dataMap_PDSK.put("T6700150115356", "");
		dataMap_PDSK.put("T6700150115364", "");
		dataMap_PDSK.put("T6700150115380", "");
		dataMap_PDSK.put("T6700150115414", "");
		dataMap_PDSK.put("T6700150115422", "");
		dataMap_PDSK.put("T6700150115430", "");
		dataMap_PDSK.put("T6700150115463", "");
		dataMap_PDSK.put("T6700150115488", "");
		dataMap_PDSK.put("T6700150115570", "");
		dataMap_PDSK.put("T6700150115620", "");
		dataMap_PDSK.put("T6700150115736", "");
		dataMap_PDSK.put("T6700150115810", "");
		dataMap_PDSK.put("T6700150115810", "");
		dataMap_PDSK.put("T6700150115835", "");
		dataMap_PDSK.put("T6700150115868", "");
		dataMap_PDSK.put("T6700150115876", "");
		dataMap_PDSK.put("T6700150115884", "");
		dataMap_PDSK.put("T6700150116049", "");
		dataMap_PDSK.put("T6700150116057", "");
		dataMap_PDSK.put("T6700150116065", "");
		dataMap_PDSK.put("T6700150116107", "");
		dataMap_PDSK.put("T6700150116107", "");
		dataMap_PDSK.put("T6700150116189", "");
		dataMap_PDSK.put("T6700150116214", "");
		dataMap_PDSK.put("T6700150116478", "");
		dataMap_PDSK.put("T6700150116552", "");
		dataMap_PDSK.put("T6700150116602", "");
		dataMap_PDSK.put("T6700150116602", "");
		dataMap_PDSK.put("T6700150116619", "");
		dataMap_PDSK.put("T6700150116627", "");
		dataMap_PDSK.put("T6700150116635", "");
		dataMap_PDSK.put("T6700150116734", "");
		dataMap_PDSK.put("T6700150116750", "");
		dataMap_PDSK.put("T6700150116767", "");
		dataMap_PDSK.put("T6700150116775", "");
		dataMap_PDSK.put("T6700150116783", "");
		dataMap_PDSK.put("T6700150116874", "");
		dataMap_PDSK.put("T6700150116916", "");
		dataMap_PDSK.put("T6700150116932", "");
		dataMap_PDSK.put("T6700150116932", "");
		dataMap_PDSK.put("T6700150116940", "");
		dataMap_PDSK.put("T6700150116940", "");
		dataMap_PDSK.put("T6700150116965", "");
		dataMap_PDSK.put("T6700150117063", "");
		dataMap_PDSK.put("T6700150117063", "");
		dataMap_PDSK.put("T6700150117071", "");
		dataMap_PDSK.put("T6700150117121", "");
		dataMap_PDSK.put("T6700150117138", "");
		dataMap_PDSK.put("T6700150117146", "");
		dataMap_PDSK.put("T6700150117154", "");
		dataMap_PDSK.put("T6700150117162", "");
		dataMap_PDSK.put("T6700150117170", "");
		dataMap_PDSK.put("T6700150117245", "");
		dataMap_PDSK.put("T6700150117286", "");
		dataMap_PDSK.put("T6700150117311", "");
		dataMap_PDSK.put("T6700150117393", "");
		dataMap_PDSK.put("T6700150117402", "");
		dataMap_PDSK.put("T6700150117501", "");
		dataMap_PDSK.put("T6700150117583", "");
		dataMap_PDSK.put("T6700150117583", "");
		dataMap_PDSK.put("T6700150117591", "");
		dataMap_PDSK.put("T6700150117600", "");
		dataMap_PDSK.put("T6700150117633", "");
		dataMap_PDSK.put("T6700150117633", "");
		dataMap_PDSK.put("T6700150117732", "");
		dataMap_PDSK.put("T6700150117848", "");
		dataMap_PDSK.put("T6700150117856", "");
		dataMap_PDSK.put("T6700150117864", "");
		dataMap_PDSK.put("T6700150118029", "");
		dataMap_PDSK.put("T6700150118053", "");
		dataMap_PDSK.put("T6700150118078", "");
		dataMap_PDSK.put("T6700150118094", "");
		dataMap_PDSK.put("T6700150118103", "");
		dataMap_PDSK.put("T6700150118111", "");
		dataMap_PDSK.put("T6700150118193", "");
		dataMap_PDSK.put("T6700150118193", "");
		dataMap_PDSK.put("T6700150118202", "");
		dataMap_PDSK.put("T6700150118219", "");
		dataMap_PDSK.put("T6700150118301", "");
		dataMap_PDSK.put("T6700150118409", "");
		dataMap_PDSK.put("T6700150118417", "");
		dataMap_PDSK.put("T6700150118425", "");
		dataMap_PDSK.put("T6700150118482", "");
		dataMap_PDSK.put("T6700150118557", "");
		dataMap_PDSK.put("T6700150118581", "");
		dataMap_PDSK.put("T6700150118689", "");
		dataMap_PDSK.put("T6700150118739", "");
		dataMap_PDSK.put("T6700150118747", "");
		dataMap_PDSK.put("T6700150118821", "");
		dataMap_PDSK.put("T6700150118854", "");
		dataMap_PDSK.put("T6700150118870", "");
		dataMap_PDSK.put("T6700150118870", "");
		dataMap_PDSK.put("T6700150118887", "");
		dataMap_PDSK.put("T6700150118912", "");
		dataMap_PDSK.put("T6700150118978", "");
		dataMap_PDSK.put("T6700150118986", "");
		dataMap_PDSK.put("T6700150119035", "");
		dataMap_PDSK.put("T6700150119134", "");
		dataMap_PDSK.put("T6700150119167", "");
		dataMap_PDSK.put("T6700150119299", "");
		dataMap_PDSK.put("T6700150119349", "");
		dataMap_PDSK.put("T6700150119357", "");
		dataMap_PDSK.put("T6700150119365", "");
		dataMap_PDSK.put("T6700150119398", "");
		dataMap_PDSK.put("T6700150119398", "");
		dataMap_PDSK.put("T6700150119464", "");
		dataMap_PDSK.put("T6700150119547", "");
		dataMap_PDSK.put("T6700150119588", "");
		dataMap_PDSK.put("T6700150119596", "");
		dataMap_PDSK.put("T6700150119638", "");
		dataMap_PDSK.put("T6700150119662", "");
		dataMap_PDSK.put("T6700150119662", "");
		dataMap_PDSK.put("T6700150119670", "");
		dataMap_PDSK.put("T6700150119679", "");
		dataMap_PDSK.put("T6700150119737", "");
		dataMap_PDSK.put("T6700150119745", "");
		dataMap_PDSK.put("T6700150119753", "");
		dataMap_PDSK.put("T6700150119761", "");
		dataMap_PDSK.put("T6700150119902", "");
		dataMap_PDSK.put("T6700150119943", "");
		dataMap_PDSK.put("T6700150120001", "");
		dataMap_PDSK.put("T6700150120026", "");
		dataMap_PDSK.put("T6700150120075", "");
		dataMap_PDSK.put("T6700150120083", "");
		dataMap_PDSK.put("T6700150120208", "");
		dataMap_PDSK.put("T6700150120307", "");
		dataMap_PDSK.put("T6700150120315", "");
		dataMap_PDSK.put("T6700150120331", "");
		dataMap_PDSK.put("T6700150120372", "");
		dataMap_PDSK.put("T6700150120414", "");
		dataMap_PDSK.put("T6700150120430", "");
		dataMap_PDSK.put("T6700150120471", "");
		dataMap_PDSK.put("T6700150120538", "");
		dataMap_PDSK.put("T6700150120546", "");
		dataMap_PDSK.put("T6700150120604", "");
		dataMap_PDSK.put("T6700150120629", "");
		dataMap_PDSK.put("T6700150120703", "");
		dataMap_PDSK.put("T6700150120752", "");
		dataMap_PDSK.put("T6700150120835", "");
		dataMap_PDSK.put("T6700150120876", "");
		dataMap_PDSK.put("T6700150121024", "");
		dataMap_PDSK.put("T6700150121032", "");
		dataMap_PDSK.put("T6700150121098", "");
		dataMap_PDSK.put("T6700150121172", "");
		dataMap_PDSK.put("T6700150121172", "");
		dataMap_PDSK.put("T6700150121255", "");
		dataMap_PDSK.put("T6700150121255", "");
		dataMap_PDSK.put("T6700150121313", "");
		dataMap_PDSK.put("T6700150121338", "");
		dataMap_PDSK.put("T6700150121404", "");
		dataMap_PDSK.put("T6700150121437", "");
		dataMap_PDSK.put("T6700150121494", "");
		dataMap_PDSK.put("T6700150121627", "");
		dataMap_PDSK.put("T6700150121734", "");
		dataMap_PDSK.put("T6700150121742", "");
		dataMap_PDSK.put("T6700150121750", "");
		dataMap_PDSK.put("T6700150121965", "");
		dataMap_PDSK.put("T6700150122030", "");
		dataMap_PDSK.put("T6700150122138", "");
		dataMap_PDSK.put("T6700150122154", "");
		dataMap_PDSK.put("T6700150122162", "");
		dataMap_PDSK.put("T6700150122162", "");
		dataMap_PDSK.put("T6700150122170", "");
		dataMap_PDSK.put("T6700150122220", "");
		dataMap_PDSK.put("T6700150122253", "");
		dataMap_PDSK.put("T6700150122261", "");
		dataMap_PDSK.put("T6700150122278", "");
		dataMap_PDSK.put("T6700150122286", "");
		dataMap_PDSK.put("T6700150122294", "");
		dataMap_PDSK.put("T6700150122385", "");
		dataMap_PDSK.put("T6700150122410", "");
		dataMap_PDSK.put("T6700150122435", "");
		dataMap_PDSK.put("T6700150122526", "");
		dataMap_PDSK.put("T6700150122591", "");
		dataMap_PDSK.put("T6700150122617", "");
		dataMap_PDSK.put("T6700150122641", "");
		dataMap_PDSK.put("T6700150122781", "");
		dataMap_PDSK.put("T6700150122996", "");
		dataMap_PDSK.put("T6700150123053", "");
		dataMap_PDSK.put("T6700150123061", "");
		dataMap_PDSK.put("T6700150123103", "");
		dataMap_PDSK.put("T6700150123103", "");
		dataMap_PDSK.put("T6700150123152", "");
		dataMap_PDSK.put("T6700150123160", "");
		dataMap_PDSK.put("T6700150123169", "");
		dataMap_PDSK.put("T6700150123219", "");
		dataMap_PDSK.put("T6700150123243", "");
		dataMap_PDSK.put("T6700150123375", "");
		dataMap_PDSK.put("T6700150123391", "");
		dataMap_PDSK.put("T6700150123573", "");
		dataMap_PDSK.put("T6700150123581", "");
		dataMap_PDSK.put("T6700150123598", "");
		dataMap_PDSK.put("T6700150123714", "");
		dataMap_PDSK.put("T6700150123722", "");
		dataMap_PDSK.put("T6700150123838", "");
		dataMap_PDSK.put("T6700150124423", "");
		dataMap_PDSK.put("T6700150124431", "");
		dataMap_PDSK.put("T6700150124464", "");
		dataMap_PDSK.put("T6700150124547", "");
		dataMap_PDSK.put("T6700150124654", "");
		dataMap_PDSK.put("T6700150124737", "");
		dataMap_PDSK.put("T6700150124794", "");
		dataMap_PDSK.put("T6700150124976", "");
		dataMap_PDSK.put("T6700150125388", "");
		dataMap_PDSK.put("T6700150126196", "");
		dataMap_PDSK.put("T6700150126246", "");
		dataMap_PDSK.put("T6700150126642", "");
		dataMap_PDSK.put("T6700150127236", "");
		dataMap_PDSK.put("T6700150127392", "");
		dataMap_PDSK.put("T6700150127632", "");
		dataMap_PDSK.put("T6700150127962", "");
		dataMap_PDSK.put("T6700150127979", "");
		dataMap_PDSK.put("T6700150129447", "");
		dataMap_PDSK.put("T6700150129463", "");
		dataMap_PDSK.put("T6700150129703", "");
		dataMap_PDSK.put("T6700150130223", "");
		dataMap_PDSK.put("T6700150130693", "");
		dataMap_PDSK.put("T6700150130727", "");
		dataMap_PDSK.put("T6700150130735", "");
		dataMap_PDSK.put("T6700150131064", "");
		dataMap_PDSK.put("T6700150131114", "");
		dataMap_PDSK.put("T6700150131452", "");
		dataMap_PDSK.put("T6700150131460", "");
		dataMap_PDSK.put("T6700150131618", "");
		dataMap_PDSK.put("T6700150131972", "");
		dataMap_PDSK.put("T6700150132153", "");
		dataMap_PDSK.put("T6700150132319", "");
		dataMap_PDSK.put("T6700150132384", "");
		dataMap_PDSK.put("T6700150132624", "");
		dataMap_PDSK.put("T6700150132970", "");
		dataMap_PDSK.put("T6700150133176", "");
		dataMap_PDSK.put("T6810091344275", "");
		dataMap_PDSK.put("T6810200243617", "");
		dataMap_PDSK.put("T6810200243617", "");
		dataMap_PDSK.put("T6810298031766", "");
		dataMap_PDSK.put("T6810732128715", "");
		dataMap_PDSK.put("T6810806333919", "");
		dataMap_PDSK.put("T6810832317317", "");
		dataMap_PDSK.put("T7700150109209", "");
		dataMap_PDSK.put("T7700150111494", "");
		dataMap_PDSK.put("T7700150112518", "");
		dataMap_PDSK.put("T7700150113251", "");
		dataMap_PDSK.put("T7700150113342", "");
		dataMap_PDSK.put("T7700150113755", "");
		dataMap_PDSK.put("T7700150114092", "");
		dataMap_PDSK.put("T7700150114101", "");
		dataMap_PDSK.put("T7700150114191", "");
		dataMap_PDSK.put("T7700150114233", "");
		dataMap_PDSK.put("T7700150114349", "");
		dataMap_PDSK.put("T7700150114349", "");
		dataMap_PDSK.put("T7700150114423", "");
		dataMap_PDSK.put("T7700150114679", "");
		dataMap_PDSK.put("T7700150114687", "");
		dataMap_PDSK.put("T7700150114695", "");
		dataMap_PDSK.put("T7700150114704", "");
		dataMap_PDSK.put("T7700150114712", "");
		dataMap_PDSK.put("T7700150114712", "");
		dataMap_PDSK.put("T7700150114720", "");
		dataMap_PDSK.put("T7700150114745", "");
		dataMap_PDSK.put("T7700150114753", "");
		dataMap_PDSK.put("T7700150114761", "");
		dataMap_PDSK.put("T7700150114811", "");
		dataMap_PDSK.put("T7700150114844", "");
		dataMap_PDSK.put("T7700150114844", "");
		dataMap_PDSK.put("T7700150115090", "");
		dataMap_PDSK.put("T7700150115099", "");
		dataMap_PDSK.put("T7700150115140", "");
		dataMap_PDSK.put("T7700150115140", "");
		dataMap_PDSK.put("T7700150115165", "");
		dataMap_PDSK.put("T7700150115215", "");
		dataMap_PDSK.put("T7700150115223", "");
		dataMap_PDSK.put("T7700150115223", "");
		dataMap_PDSK.put("T7700150115256", "");
		dataMap_PDSK.put("T7700150115264", "");
		dataMap_PDSK.put("T7700150115280", "");
		dataMap_PDSK.put("T7700150115289", "");
		dataMap_PDSK.put("T7700150115297", "");
		dataMap_PDSK.put("T7700150115306", "");
		dataMap_PDSK.put("T7700150115347", "");
		dataMap_PDSK.put("T7700150115355", "");
		dataMap_PDSK.put("T7700150115363", "");
		dataMap_PDSK.put("T7700150115388", "");
		dataMap_PDSK.put("T7700150115421", "");
		dataMap_PDSK.put("T7700150115462", "");
		dataMap_PDSK.put("T7700150115611", "");
		dataMap_PDSK.put("T7700150115735", "");
		dataMap_PDSK.put("T7700150115867", "");
		dataMap_PDSK.put("T7700150115891", "");
		dataMap_PDSK.put("T7700150116048", "");
		dataMap_PDSK.put("T7700150116056", "");
		dataMap_PDSK.put("T7700150116064", "");
		dataMap_PDSK.put("T7700150116072", "");
		dataMap_PDSK.put("T7700150116089", "");
		dataMap_PDSK.put("T7700150116106", "");
		dataMap_PDSK.put("T7700150116213", "");
		dataMap_PDSK.put("T7700150116469", "");
		dataMap_PDSK.put("T7700150116477", "");
		dataMap_PDSK.put("T7700150116485", "");
		dataMap_PDSK.put("T7700150116551", "");
		dataMap_PDSK.put("T7700150116626", "");
		dataMap_PDSK.put("T7700150116675", "");
		dataMap_PDSK.put("T7700150116725", "");
		dataMap_PDSK.put("T7700150116758", "");
		dataMap_PDSK.put("T7700150116766", "");
		dataMap_PDSK.put("T7700150116774", "");
		dataMap_PDSK.put("T7700150116931", "");
		dataMap_PDSK.put("T7700150116964", "");
		dataMap_PDSK.put("T7700150117062", "");
		dataMap_PDSK.put("T7700150117070", "");
		dataMap_PDSK.put("T7700150117120", "");
		dataMap_PDSK.put("T7700150117120", "");
		dataMap_PDSK.put("T7700150117129", "");
		dataMap_PDSK.put("T7700150117137", "");
		dataMap_PDSK.put("T7700150117145", "");
		dataMap_PDSK.put("T7700150117153", "");
		dataMap_PDSK.put("T7700150117211", "");
		dataMap_PDSK.put("T7700150117310", "");
		dataMap_PDSK.put("T7700150117310", "");
		dataMap_PDSK.put("T7700150117376", "");
		dataMap_PDSK.put("T7700150117392", "");
		dataMap_PDSK.put("T7700150117392", "");
		dataMap_PDSK.put("T7700150117401", "");
		dataMap_PDSK.put("T7700150117500", "");
		dataMap_PDSK.put("T7700150117509", "");
		dataMap_PDSK.put("T7700150117582", "");
		dataMap_PDSK.put("T7700150117599", "");
		dataMap_PDSK.put("T7700150117715", "");
		dataMap_PDSK.put("T7700150117723", "");
		dataMap_PDSK.put("T7700150117731", "");
		dataMap_PDSK.put("T7700150117814", "");
		dataMap_PDSK.put("T7700150117847", "");
		dataMap_PDSK.put("T7700150117855", "");
		dataMap_PDSK.put("T7700150117863", "");
		dataMap_PDSK.put("T7700150118028", "");
		dataMap_PDSK.put("T7700150118052", "");
		dataMap_PDSK.put("T7700150118060", "");
		dataMap_PDSK.put("T7700150118077", "");
		dataMap_PDSK.put("T7700150118077", "");
		dataMap_PDSK.put("T7700150118077", "");
		dataMap_PDSK.put("T7700150118085", "");
		dataMap_PDSK.put("T7700150118093", "");
		dataMap_PDSK.put("T7700150118110", "");
		dataMap_PDSK.put("T7700150118192", "");
		dataMap_PDSK.put("T7700150118201", "");
		dataMap_PDSK.put("T7700150118226", "");
		dataMap_PDSK.put("T7700150118234", "");
		dataMap_PDSK.put("T7700150118300", "");
		dataMap_PDSK.put("T7700150118408", "");
		dataMap_PDSK.put("T7700150118416", "");
		dataMap_PDSK.put("T7700150118424", "");
		dataMap_PDSK.put("T7700150118556", "");
		dataMap_PDSK.put("T7700150118606", "");
		dataMap_PDSK.put("T7700150118688", "");
		dataMap_PDSK.put("T7700150118738", "");
		dataMap_PDSK.put("T7700150118820", "");
		dataMap_PDSK.put("T7700150118878", "");
		dataMap_PDSK.put("T7700150118878", "");
		dataMap_PDSK.put("T7700150118960", "");
		dataMap_PDSK.put("T7700150118977", "");
		dataMap_PDSK.put("T7700150118985", "");
		dataMap_PDSK.put("T7700150119166", "");
		dataMap_PDSK.put("T7700150119190", "");
		dataMap_PDSK.put("T7700150119348", "");
		dataMap_PDSK.put("T7700150119356", "");
		dataMap_PDSK.put("T7700150119364", "");
		dataMap_PDSK.put("T7700150119397", "");
		dataMap_PDSK.put("T7700150119455", "");
		dataMap_PDSK.put("T7700150119546", "");
		dataMap_PDSK.put("T7700150119554", "");
		dataMap_PDSK.put("T7700150119579", "");
		dataMap_PDSK.put("T7700150119587", "");
		dataMap_PDSK.put("T7700150119595", "");
		dataMap_PDSK.put("T7700150119620", "");
		dataMap_PDSK.put("T7700150119637", "");
		dataMap_PDSK.put("T7700150119661", "");
		dataMap_PDSK.put("T7700150119678", "");
		dataMap_PDSK.put("T7700150119686", "");
		dataMap_PDSK.put("T7700150119736", "");
		dataMap_PDSK.put("T7700150119760", "");
		dataMap_PDSK.put("T7700150119901", "");
		dataMap_PDSK.put("T7700150119926", "");
		dataMap_PDSK.put("T7700150119942", "");
		dataMap_PDSK.put("T7700150119975", "");
		dataMap_PDSK.put("T7700150119991", "");
		dataMap_PDSK.put("T7700150120009", "");
		dataMap_PDSK.put("T7700150120025", "");
		dataMap_PDSK.put("T7700150120066", "");
		dataMap_PDSK.put("T7700150120074", "");
		dataMap_PDSK.put("T7700150120082", "");
		dataMap_PDSK.put("T7700150120207", "");
		dataMap_PDSK.put("T7700150120330", "");
		dataMap_PDSK.put("T7700150120339", "");
		dataMap_PDSK.put("T7700150120413", "");
		dataMap_PDSK.put("T7700150120470", "");
		dataMap_PDSK.put("T7700150120537", "");
		dataMap_PDSK.put("T7700150120545", "");
		dataMap_PDSK.put("T7700150120545", "");
		dataMap_PDSK.put("T7700150120603", "");
		dataMap_PDSK.put("T7700150120669", "");
		dataMap_PDSK.put("T7700150120677", "");
		dataMap_PDSK.put("T7700150120702", "");
		dataMap_PDSK.put("T7700150120710", "");
		dataMap_PDSK.put("T7700150120792", "");
		dataMap_PDSK.put("T7700150120883", "");
		dataMap_PDSK.put("T7700150120891", "");
		dataMap_PDSK.put("T7700150121023", "");
		dataMap_PDSK.put("T7700150121089", "");
		dataMap_PDSK.put("T7700150121097", "");
		dataMap_PDSK.put("T7700150121254", "");
		dataMap_PDSK.put("T7700150121312", "");
		dataMap_PDSK.put("T7700150121329", "");
		dataMap_PDSK.put("T7700150121337", "");
		dataMap_PDSK.put("T7700150121436", "");
		dataMap_PDSK.put("T7700150121493", "");
		dataMap_PDSK.put("T7700150121568", "");
		dataMap_PDSK.put("T7700150121709", "");
		dataMap_PDSK.put("T7700150121733", "");
		dataMap_PDSK.put("T7700150121758", "");
		dataMap_PDSK.put("T7700150121964", "");
		dataMap_PDSK.put("T7700150122062", "");
		dataMap_PDSK.put("T7700150122137", "");
		dataMap_PDSK.put("T7700150122145", "");
		dataMap_PDSK.put("T7700150122153", "");
		dataMap_PDSK.put("T7700150122161", "");
		dataMap_PDSK.put("T7700150122211", "");
		dataMap_PDSK.put("T7700150122244", "");
		dataMap_PDSK.put("T7700150122252", "");
		dataMap_PDSK.put("T7700150122260", "");
		dataMap_PDSK.put("T7700150122277", "");
		dataMap_PDSK.put("T7700150122285", "");
		dataMap_PDSK.put("T7700150122293", "");
		dataMap_PDSK.put("T7700150122426", "");
		dataMap_PDSK.put("T7700150122434", "");
		dataMap_PDSK.put("T7700150122434", "");
		dataMap_PDSK.put("T7700150122616", "");
		dataMap_PDSK.put("T7700150122640", "");
		dataMap_PDSK.put("T7700150122780", "");
		dataMap_PDSK.put("T7700150122995", "");
		dataMap_PDSK.put("T7700150123052", "");
		dataMap_PDSK.put("T7700150123060", "");
		dataMap_PDSK.put("T7700150123135", "");
		dataMap_PDSK.put("T7700150123151", "");
		dataMap_PDSK.put("T7700150123168", "");
		dataMap_PDSK.put("T7700150123218", "");
		dataMap_PDSK.put("T7700150123242", "");
		dataMap_PDSK.put("T7700150123374", "");
		dataMap_PDSK.put("T7700150123390", "");
		dataMap_PDSK.put("T7700150123498", "");
		dataMap_PDSK.put("T7700150123572", "");
		dataMap_PDSK.put("T7700150123580", "");
		dataMap_PDSK.put("T7700150123597", "");
		dataMap_PDSK.put("T7700150124397", "");
		dataMap_PDSK.put("T7700150124422", "");
		dataMap_PDSK.put("T7700150124463", "");
		dataMap_PDSK.put("T7700150124538", "");
		dataMap_PDSK.put("T7700150124975", "");
		dataMap_PDSK.put("T7700150125123", "");
		dataMap_PDSK.put("T7700150125222", "");
		dataMap_PDSK.put("T7700150125395", "");
		dataMap_PDSK.put("T7700150126146", "");
		dataMap_PDSK.put("T7700150126245", "");
		dataMap_PDSK.put("T7700150127235", "");
		dataMap_PDSK.put("T7700150127243", "");
		dataMap_PDSK.put("T7700150127301", "");
		dataMap_PDSK.put("T7700150127862", "");
		dataMap_PDSK.put("T7700150127879", "");
		dataMap_PDSK.put("T7700150127961", "");
		dataMap_PDSK.put("T7700150127978", "");
		dataMap_PDSK.put("T7700150128332", "");
		dataMap_PDSK.put("T7700150128869", "");
		dataMap_PDSK.put("T7700150129446", "");
		dataMap_PDSK.put("T7700150129462", "");
		dataMap_PDSK.put("T7700150129702", "");
		dataMap_PDSK.put("T7700150130107", "");
		dataMap_PDSK.put("T7700150130222", "");
		dataMap_PDSK.put("T7700150130726", "");
		dataMap_PDSK.put("T7700150130734", "");
		dataMap_PDSK.put("T7700150131063", "");
		dataMap_PDSK.put("T7700150131113", "");
		dataMap_PDSK.put("T7700150131617", "");
		dataMap_PDSK.put("T7700150131971", "");
		dataMap_PDSK.put("T7700150132318", "");
		dataMap_PDSK.put("T7700150132490", "");
		dataMap_PDSK.put("T7700150132672", "");
		dataMap_PDSK.put("T7700150133019", "");
		dataMap_PDSK.put("T7700150133068", "");
		dataMap_PDSK.put("T7810027328961", "");
		dataMap_PDSK.put("T7810211977771", "");
		dataMap_PDSK.put("T7810371838631", "");
		dataMap_PDSK.put("T7810403579121", "");
		dataMap_PDSK.put("T7810752092246", "");
		dataMap_PDSK.put("T7810752092246", "");
		dataMap_PDSK.put("T7810757056881", "");
		dataMap_PDSK.put("T7810769207373", "");
		dataMap_PDSK.put("T7810802623470", "");
		dataMap_PDSK.put("T7810935294634", "");
		dataMap_PDSK.put("T8700150109208", "");
		dataMap_PDSK.put("T8700150110990", "");
		dataMap_PDSK.put("T8700150111114", "");
		dataMap_PDSK.put("T8700150111493", "");
		dataMap_PDSK.put("T8700150112161", "");
		dataMap_PDSK.put("T8700150113250", "");
		dataMap_PDSK.put("T8700150113341", "");
		dataMap_PDSK.put("T8700150113713", "");
		dataMap_PDSK.put("T8700150113754", "");
		dataMap_PDSK.put("T8700150113762", "");
		dataMap_PDSK.put("T8700150114091", "");
		dataMap_PDSK.put("T8700150114100", "");
		dataMap_PDSK.put("T8700150114109", "");
		dataMap_PDSK.put("T8700150114190", "");
		dataMap_PDSK.put("T8700150114232", "");
		dataMap_PDSK.put("T8700150114240", "");
		dataMap_PDSK.put("T8700150114348", "");
		dataMap_PDSK.put("T8700150114348", "");
		dataMap_PDSK.put("T8700150114678", "");
		dataMap_PDSK.put("T8700150114686", "");
		dataMap_PDSK.put("T8700150114694", "");
		dataMap_PDSK.put("T8700150114703", "");
		dataMap_PDSK.put("T8700150114711", "");
		dataMap_PDSK.put("T8700150114752", "");
		dataMap_PDSK.put("T8700150114752", "");
		dataMap_PDSK.put("T8700150114760", "");
		dataMap_PDSK.put("T8700150114769", "");
		dataMap_PDSK.put("T8700150114843", "");
		dataMap_PDSK.put("T8700150114934", "");
		dataMap_PDSK.put("T8700150114934", "");
		dataMap_PDSK.put("T8700150115098", "");
		dataMap_PDSK.put("T8700150115164", "");
		dataMap_PDSK.put("T8700150115214", "");
		dataMap_PDSK.put("T8700150115222", "");
		dataMap_PDSK.put("T8700150115230", "");
		dataMap_PDSK.put("T8700150115255", "");
		dataMap_PDSK.put("T8700150115288", "");
		dataMap_PDSK.put("T8700150115296", "");
		dataMap_PDSK.put("T8700150115346", "");
		dataMap_PDSK.put("T8700150115354", "");
		dataMap_PDSK.put("T8700150115362", "");
		dataMap_PDSK.put("T8700150115379", "");
		dataMap_PDSK.put("T8700150115387", "");
		dataMap_PDSK.put("T8700150115420", "");
		dataMap_PDSK.put("T8700150115429", "");
		dataMap_PDSK.put("T8700150115445", "");
		dataMap_PDSK.put("T8700150115461", "");
		dataMap_PDSK.put("T8700150115478", "");
		dataMap_PDSK.put("T8700150115569", "");
		dataMap_PDSK.put("T8700150115569", "");
		dataMap_PDSK.put("T8700150115610", "");
		dataMap_PDSK.put("T8700150115619", "");
		dataMap_PDSK.put("T8700150115627", "");
		dataMap_PDSK.put("T8700150115668", "");
		dataMap_PDSK.put("T8700150115734", "");
		dataMap_PDSK.put("T8700150115750", "");
		dataMap_PDSK.put("T8700150115791", "");
		dataMap_PDSK.put("T8700150115890", "");
		dataMap_PDSK.put("T8700150116047", "");
		dataMap_PDSK.put("T8700150116055", "");
		dataMap_PDSK.put("T8700150116063", "");
		dataMap_PDSK.put("T8700150116071", "");
		dataMap_PDSK.put("T8700150116071", "");
		dataMap_PDSK.put("T8700150116088", "");
		dataMap_PDSK.put("T8700150116088", "");
		dataMap_PDSK.put("T8700150116121", "");
		dataMap_PDSK.put("T8700150116212", "");
		dataMap_PDSK.put("T8700150116468", "");
		dataMap_PDSK.put("T8700150116476", "");
		dataMap_PDSK.put("T8700150116550", "");
		dataMap_PDSK.put("T8700150116625", "");
		dataMap_PDSK.put("T8700150116633", "");
		dataMap_PDSK.put("T8700150116641", "");
		dataMap_PDSK.put("T8700150116732", "");
		dataMap_PDSK.put("T8700150116749", "");
		dataMap_PDSK.put("T8700150116749", "");
		dataMap_PDSK.put("T8700150116757", "");
		dataMap_PDSK.put("T8700150116765", "");
		dataMap_PDSK.put("T8700150116773", "");
		dataMap_PDSK.put("T8700150116930", "");
		dataMap_PDSK.put("T8700150116939", "");
		dataMap_PDSK.put("T8700150116963", "");
		dataMap_PDSK.put("T8700150117128", "");
		dataMap_PDSK.put("T8700150117128", "");
		dataMap_PDSK.put("T8700150117136", "");
		dataMap_PDSK.put("T8700150117144", "");
		dataMap_PDSK.put("T8700150117152", "");
		dataMap_PDSK.put("T8700150117160", "");
		dataMap_PDSK.put("T8700150117169", "");
		dataMap_PDSK.put("T8700150117210", "");
		dataMap_PDSK.put("T8700150117391", "");
		dataMap_PDSK.put("T8700150117400", "");
		dataMap_PDSK.put("T8700150117466", "");
		dataMap_PDSK.put("T8700150117508", "");
		dataMap_PDSK.put("T8700150117581", "");
		dataMap_PDSK.put("T8700150117598", "");
		dataMap_PDSK.put("T8700150117664", "");
		dataMap_PDSK.put("T8700150117680", "");
		dataMap_PDSK.put("T8700150117730", "");
		dataMap_PDSK.put("T8700150117788", "");
		dataMap_PDSK.put("T8700150117846", "");
		dataMap_PDSK.put("T8700150117854", "");
		dataMap_PDSK.put("T8700150117862", "");
		dataMap_PDSK.put("T8700150117895", "");
		dataMap_PDSK.put("T8700150117895", "");
		dataMap_PDSK.put("T8700150117994", "");
		dataMap_PDSK.put("T8700150118027", "");
		dataMap_PDSK.put("T8700150118051", "");
		dataMap_PDSK.put("T8700150118068", "");
		dataMap_PDSK.put("T8700150118076", "");
		dataMap_PDSK.put("T8700150118084", "");
		dataMap_PDSK.put("T8700150118092", "");
		dataMap_PDSK.put("T8700150118191", "");
		dataMap_PDSK.put("T8700150118191", "");
		dataMap_PDSK.put("T8700150118200", "");
		dataMap_PDSK.put("T8700150118209", "");
		dataMap_PDSK.put("T8700150118299", "");
		dataMap_PDSK.put("T8700150118332", "");
		dataMap_PDSK.put("T8700150118398", "");
		dataMap_PDSK.put("T8700150118415", "");
		dataMap_PDSK.put("T8700150118423", "");
		dataMap_PDSK.put("T8700150118423", "");
		dataMap_PDSK.put("T8700150118530", "");
		dataMap_PDSK.put("T8700150118547", "");
		dataMap_PDSK.put("T8700150118555", "");
		dataMap_PDSK.put("T8700150118646", "");
		dataMap_PDSK.put("T8700150118687", "");
		dataMap_PDSK.put("T8700150118869", "");
		dataMap_PDSK.put("T8700150118877", "");
		dataMap_PDSK.put("T8700150118919", "");
		dataMap_PDSK.put("T8700150118968", "");
		dataMap_PDSK.put("T8700150118976", "");
		dataMap_PDSK.put("T8700150118984", "");
		dataMap_PDSK.put("T8700150119108", "");
		dataMap_PDSK.put("T8700150119165", "");
		dataMap_PDSK.put("T8700150119347", "");
		dataMap_PDSK.put("T8700150119355", "");
		dataMap_PDSK.put("T8700150119363", "");
		dataMap_PDSK.put("T8700150119396", "");
		dataMap_PDSK.put("T8700150119470", "");
		dataMap_PDSK.put("T8700150119479", "");
		dataMap_PDSK.put("T8700150119545", "");
		dataMap_PDSK.put("T8700150119578", "");
		dataMap_PDSK.put("T8700150119586", "");
		dataMap_PDSK.put("T8700150119594", "");
		dataMap_PDSK.put("T8700150119636", "");
		dataMap_PDSK.put("T8700150119669", "");
		dataMap_PDSK.put("T8700150119677", "");
		dataMap_PDSK.put("T8700150119685", "");
		dataMap_PDSK.put("T8700150119693", "");
		dataMap_PDSK.put("T8700150119727", "");
		dataMap_PDSK.put("T8700150119743", "");
		dataMap_PDSK.put("T8700150119900", "");
		dataMap_PDSK.put("T8700150119900", "");
		dataMap_PDSK.put("T8700150119909", "");
		dataMap_PDSK.put("T8700150119941", "");
		dataMap_PDSK.put("T8700150119990", "");
		dataMap_PDSK.put("T8700150119999", "");
		dataMap_PDSK.put("T8700150120008", "");
		dataMap_PDSK.put("T8700150120024", "");
		dataMap_PDSK.put("T8700150120065", "");
		dataMap_PDSK.put("T8700150120073", "");
		dataMap_PDSK.put("T8700150120081", "");
		dataMap_PDSK.put("T8700150120206", "");
		dataMap_PDSK.put("T8700150120255", "");
		dataMap_PDSK.put("T8700150120321", "");
		dataMap_PDSK.put("T8700150120370", "");
		dataMap_PDSK.put("T8700150120412", "");
		dataMap_PDSK.put("T8700150120429", "");
		dataMap_PDSK.put("T8700150120536", "");
		dataMap_PDSK.put("T8700150120544", "");
		dataMap_PDSK.put("T8700150120602", "");
		dataMap_PDSK.put("T8700150120610", "");
		dataMap_PDSK.put("T8700150120627", "");
		dataMap_PDSK.put("T8700150120668", "");
		dataMap_PDSK.put("T8700150120676", "");
		dataMap_PDSK.put("T8700150120701", "");
		dataMap_PDSK.put("T8700150120701", "");
		dataMap_PDSK.put("T8700150120759", "");
		dataMap_PDSK.put("T8700150120767", "");
		dataMap_PDSK.put("T8700150120791", "");
		dataMap_PDSK.put("T8700150120874", "");
		dataMap_PDSK.put("T8700150120874", "");
		dataMap_PDSK.put("T8700150120882", "");
		dataMap_PDSK.put("T8700150120890", "");
		dataMap_PDSK.put("T8700150120965", "");
		dataMap_PDSK.put("T8700150121022", "");
		dataMap_PDSK.put("T8700150121096", "");
		dataMap_PDSK.put("T8700150121253", "");
		dataMap_PDSK.put("T8700150121311", "");
		dataMap_PDSK.put("T8700150121328", "");
		dataMap_PDSK.put("T8700150121336", "");
		dataMap_PDSK.put("T8700150121435", "");
		dataMap_PDSK.put("T8700150121492", "");
		dataMap_PDSK.put("T8700150121633", "");
		dataMap_PDSK.put("T8700150121963", "");
		dataMap_PDSK.put("T8700150122037", "");
		dataMap_PDSK.put("T8700150122061", "");
		dataMap_PDSK.put("T8700150122061", "");
		dataMap_PDSK.put("T8700150122136", "");
		dataMap_PDSK.put("T8700150122144", "");
		dataMap_PDSK.put("T8700150122152", "");
		dataMap_PDSK.put("T8700150122160", "");
		dataMap_PDSK.put("T8700150122210", "");
		dataMap_PDSK.put("T8700150122219", "");
		dataMap_PDSK.put("T8700150122243", "");
		dataMap_PDSK.put("T8700150122251", "");
		dataMap_PDSK.put("T8700150122284", "");
		dataMap_PDSK.put("T8700150122292", "");
		dataMap_PDSK.put("T8700150122334", "");
		dataMap_PDSK.put("T8700150122425", "");
		dataMap_PDSK.put("T8700150122433", "");
		dataMap_PDSK.put("T8700150122474", "");
		dataMap_PDSK.put("T8700150122490", "");
		dataMap_PDSK.put("T8700150122532", "");
		dataMap_PDSK.put("T8700150122581", "");
		dataMap_PDSK.put("T8700150122598", "");
		dataMap_PDSK.put("T8700150122607", "");
		dataMap_PDSK.put("T8700150122607", "");
		dataMap_PDSK.put("T8700150122747", "");
		dataMap_PDSK.put("T8700150122994", "");
		dataMap_PDSK.put("T8700150123043", "");
		dataMap_PDSK.put("T8700150123051", "");
		dataMap_PDSK.put("T8700150123068", "");
		dataMap_PDSK.put("T8700150123118", "");
		dataMap_PDSK.put("T8700150123142", "");
		dataMap_PDSK.put("T8700150123167", "");
		dataMap_PDSK.put("T8700150123217", "");
		dataMap_PDSK.put("T8700150123274", "");
		dataMap_PDSK.put("T8700150123373", "");
		dataMap_PDSK.put("T8700150123497", "");
		dataMap_PDSK.put("T8700150123571", "");
		dataMap_PDSK.put("T8700150123588", "");
		dataMap_PDSK.put("T8700150123605", "");
		dataMap_PDSK.put("T8700150123679", "");
		dataMap_PDSK.put("T8700150123844", "");
		dataMap_PDSK.put("T8700150123893", "");
		dataMap_PDSK.put("T8700150124421", "");
		dataMap_PDSK.put("T8700150124462", "");
		dataMap_PDSK.put("T8700150124537", "");
		dataMap_PDSK.put("T8700150124628", "");
		dataMap_PDSK.put("T8700150124974", "");
		dataMap_PDSK.put("T8700150125122", "");
		dataMap_PDSK.put("T8700150126145", "");
		dataMap_PDSK.put("T8700150126244", "");
		dataMap_PDSK.put("T8700150126277", "");
		dataMap_PDSK.put("T8700150126310", "");
		dataMap_PDSK.put("T8700150126913", "");
		dataMap_PDSK.put("T8700150127234", "");
		dataMap_PDSK.put("T8700150127242", "");
		dataMap_PDSK.put("T8700150127382", "");
		dataMap_PDSK.put("T8700150127861", "");
		dataMap_PDSK.put("T8700150127878", "");
		dataMap_PDSK.put("T8700150127960", "");
		dataMap_PDSK.put("T8700150128265", "");
		dataMap_PDSK.put("T8700150128868", "");
		dataMap_PDSK.put("T8700150128876", "");
		dataMap_PDSK.put("T8700150129461", "");
		dataMap_PDSK.put("T8700150130106", "");
		dataMap_PDSK.put("T8700150130700", "");
		dataMap_PDSK.put("T8700150130725", "");
		dataMap_PDSK.put("T8700150131104", "");
		dataMap_PDSK.put("T8700150131112", "");
		dataMap_PDSK.put("T8700150131459", "");
		dataMap_PDSK.put("T8700150131616", "");
		dataMap_PDSK.put("T8700150132317", "");
		dataMap_PDSK.put("T8700150132457", "");
		dataMap_PDSK.put("T8700150132969", "");
		dataMap_PDSK.put("T8700150133018", "");
		dataMap_PDSK.put("T8810184254603", "");
		dataMap_PDSK.put("T8810216753960", "");
		dataMap_PDSK.put("T8810218777090", "");
		dataMap_PDSK.put("T8810218777090", "");
		dataMap_PDSK.put("T8810533796519", "");
		dataMap_PDSK.put("T8810811369955", "");
		dataMap_PDSK.put("T8810933676130", "");
		dataMap_PDSK.put("T9010001226741", "");
		dataMap_PDSK.put("T9010901045737", "");
		dataMap_PDSK.put("T9700150109207", "");
		dataMap_PDSK.put("T9700150111113", "");
		dataMap_PDSK.put("T9700150111492", "");
		dataMap_PDSK.put("T9700150112516", "");
		dataMap_PDSK.put("T9700150113340", "");
		dataMap_PDSK.put("T9700150113373", "");
		dataMap_PDSK.put("T9700150113712", "");
		dataMap_PDSK.put("T9700150113753", "");
		dataMap_PDSK.put("T9700150113761", "");
		dataMap_PDSK.put("T9700150114099", "");
		dataMap_PDSK.put("T9700150114108", "");
		dataMap_PDSK.put("T9700150114677", "");
		dataMap_PDSK.put("T9700150114685", "");
		dataMap_PDSK.put("T9700150114693", "");
		dataMap_PDSK.put("T9700150114702", "");
		dataMap_PDSK.put("T9700150114710", "");
		dataMap_PDSK.put("T9700150114719", "");
		dataMap_PDSK.put("T9700150114727", "");
		dataMap_PDSK.put("T9700150114751", "");
		dataMap_PDSK.put("T9700150114768", "");
		dataMap_PDSK.put("T9700150114818", "");
		dataMap_PDSK.put("T9700150114842", "");
		dataMap_PDSK.put("T9700150114859", "");
		dataMap_PDSK.put("T9700150115171", "");
		dataMap_PDSK.put("T9700150115213", "");
		dataMap_PDSK.put("T9700150115221", "");
		dataMap_PDSK.put("T9700150115254", "");
		dataMap_PDSK.put("T9700150115279", "");
		dataMap_PDSK.put("T9700150115287", "");
		dataMap_PDSK.put("T9700150115295", "");
		dataMap_PDSK.put("T9700150115345", "");
		dataMap_PDSK.put("T9700150115353", "");
		dataMap_PDSK.put("T9700150115361", "");
		dataMap_PDSK.put("T9700150115386", "");
		dataMap_PDSK.put("T9700150115428", "");
		dataMap_PDSK.put("T9700150115460", "");
		dataMap_PDSK.put("T9700150115477", "");
		dataMap_PDSK.put("T9700150115568", "");
		dataMap_PDSK.put("T9700150115618", "");
		dataMap_PDSK.put("T9700150115618", "");
		dataMap_PDSK.put("T9700150115626", "");
		dataMap_PDSK.put("T9700150115832", "");
		dataMap_PDSK.put("T9700150115857", "");
		dataMap_PDSK.put("T9700150115873", "");
		dataMap_PDSK.put("T9700150115923", "");
		dataMap_PDSK.put("T9700150116046", "");
		dataMap_PDSK.put("T9700150116054", "");
		dataMap_PDSK.put("T9700150116062", "");
		dataMap_PDSK.put("T9700150116079", "");
		dataMap_PDSK.put("T9700150116087", "");
		dataMap_PDSK.put("T9700150116095", "");
		dataMap_PDSK.put("T9700150116104", "");
		dataMap_PDSK.put("T9700150116120", "");
		dataMap_PDSK.put("T9700150116137", "");
		dataMap_PDSK.put("T9700150116211", "");
		dataMap_PDSK.put("T9700150116467", "");
		dataMap_PDSK.put("T9700150116475", "");
		dataMap_PDSK.put("T9700150116491", "");
		dataMap_PDSK.put("T9700150116673", "");
		dataMap_PDSK.put("T9700150116723", "");
		dataMap_PDSK.put("T9700150116731", "");
		dataMap_PDSK.put("T9700150116764", "");
		dataMap_PDSK.put("T9700150116772", "");
		dataMap_PDSK.put("T9700150116789", "");
		dataMap_PDSK.put("T9700150116921", "");
		dataMap_PDSK.put("T9700150116938", "");
		dataMap_PDSK.put("T9700150116962", "");
		dataMap_PDSK.put("T9700150117069", "");
		dataMap_PDSK.put("T9700150117119", "");
		dataMap_PDSK.put("T9700150117119", "");
		dataMap_PDSK.put("T9700150117127", "");
		dataMap_PDSK.put("T9700150117135", "");
		dataMap_PDSK.put("T9700150117135", "");
		dataMap_PDSK.put("T9700150117143", "");
		dataMap_PDSK.put("T9700150117151", "");
		dataMap_PDSK.put("T9700150117151", "");
		dataMap_PDSK.put("T9700150117168", "");
		dataMap_PDSK.put("T9700150117168", "");
		dataMap_PDSK.put("T9700150117300", "");
		dataMap_PDSK.put("T9700150117390", "");
		dataMap_PDSK.put("T9700150117399", "");
		dataMap_PDSK.put("T9700150117465", "");
		dataMap_PDSK.put("T9700150117507", "");
		dataMap_PDSK.put("T9700150117507", "");
		dataMap_PDSK.put("T9700150117580", "");
		dataMap_PDSK.put("T9700150117597", "");
		dataMap_PDSK.put("T9700150117663", "");
		dataMap_PDSK.put("T9700150117713", "");
		dataMap_PDSK.put("T9700150117845", "");
		dataMap_PDSK.put("T9700150117853", "");
		dataMap_PDSK.put("T9700150117861", "");
		dataMap_PDSK.put("T9700150117894", "");
		dataMap_PDSK.put("T9700150118050", "");
		dataMap_PDSK.put("T9700150118059", "");
		dataMap_PDSK.put("T9700150118067", "");
		dataMap_PDSK.put("T9700150118075", "");
		dataMap_PDSK.put("T9700150118091", "");
		dataMap_PDSK.put("T9700150118100", "");
		dataMap_PDSK.put("T9700150118117", "");
		dataMap_PDSK.put("T9700150118125", "");
		dataMap_PDSK.put("T9700150118190", "");
		dataMap_PDSK.put("T9700150118208", "");
		dataMap_PDSK.put("T9700150118298", "");
		dataMap_PDSK.put("T9700150118331", "");
		dataMap_PDSK.put("T9700150118414", "");
		dataMap_PDSK.put("T9700150118422", "");
		dataMap_PDSK.put("T9700150118554", "");
		dataMap_PDSK.put("T9700150118570", "");
		dataMap_PDSK.put("T9700150118604", "");
		dataMap_PDSK.put("T9700150118604", "");
		dataMap_PDSK.put("T9700150118653", "");
		dataMap_PDSK.put("T9700150118686", "");
		dataMap_PDSK.put("T9700150118819", "");
		dataMap_PDSK.put("T9700150118868", "");
		dataMap_PDSK.put("T9700150118876", "");
		dataMap_PDSK.put("T9700150118918", "");
		dataMap_PDSK.put("T9700150118959", "");
		dataMap_PDSK.put("T9700150118975", "");
		dataMap_PDSK.put("T9700150118983", "");
		dataMap_PDSK.put("T9700150119107", "");
		dataMap_PDSK.put("T9700150119189", "");
		dataMap_PDSK.put("T9700150119288", "");
		dataMap_PDSK.put("T9700150119354", "");
		dataMap_PDSK.put("T9700150119362", "");
		dataMap_PDSK.put("T9700150119395", "");
		dataMap_PDSK.put("T9700150119395", "");
		dataMap_PDSK.put("T9700150119395", "");
		dataMap_PDSK.put("T9700150119528", "");
		dataMap_PDSK.put("T9700150119544", "");
		dataMap_PDSK.put("T9700150119577", "");
		dataMap_PDSK.put("T9700150119585", "");
		dataMap_PDSK.put("T9700150119593", "");
		dataMap_PDSK.put("T9700150119668", "");
		dataMap_PDSK.put("T9700150119668", "");
		dataMap_PDSK.put("T9700150119676", "");
		dataMap_PDSK.put("T9700150119684", "");
		dataMap_PDSK.put("T9700150119750", "");
		dataMap_PDSK.put("T9700150119759", "");
		dataMap_PDSK.put("T9700150119899", "");
		dataMap_PDSK.put("T9700150119916", "");
		dataMap_PDSK.put("T9700150119916", "");
		dataMap_PDSK.put("T9700150120007", "");
		dataMap_PDSK.put("T9700150120023", "");
		dataMap_PDSK.put("T9700150120023", "");
		dataMap_PDSK.put("T9700150120064", "");
		dataMap_PDSK.put("T9700150120072", "");
		dataMap_PDSK.put("T9700150120080", "");
		dataMap_PDSK.put("T9700150120329", "");
		dataMap_PDSK.put("T9700150120411", "");
		dataMap_PDSK.put("T9700150120428", "");
		dataMap_PDSK.put("T9700150120535", "");
		dataMap_PDSK.put("T9700150120543", "");
		dataMap_PDSK.put("T9700150120601", "");
		dataMap_PDSK.put("T9700150120667", "");
		dataMap_PDSK.put("T9700150120675", "");
		dataMap_PDSK.put("T9700150120700", "");
		dataMap_PDSK.put("T9700150120758", "");
		dataMap_PDSK.put("T9700150120766", "");
		dataMap_PDSK.put("T9700150120840", "");
		dataMap_PDSK.put("T9700150120849", "");
		dataMap_PDSK.put("T9700150120857", "");
		dataMap_PDSK.put("T9700150120881", "");
		dataMap_PDSK.put("T9700150121021", "");
		dataMap_PDSK.put("T9700150121095", "");
		dataMap_PDSK.put("T9700150121310", "");
		dataMap_PDSK.put("T9700150121327", "");
		dataMap_PDSK.put("T9700150121335", "");
		dataMap_PDSK.put("T9700150121450", "");
		dataMap_PDSK.put("T9700150121491", "");
		dataMap_PDSK.put("T9700150121632", "");
		dataMap_PDSK.put("T9700150121707", "");
		dataMap_PDSK.put("T9700150121731", "");
		dataMap_PDSK.put("T9700150121748", "");
		dataMap_PDSK.put("T9700150121962", "");
		dataMap_PDSK.put("T9700150122060", "");
		dataMap_PDSK.put("T9700150122143", "");
		dataMap_PDSK.put("T9700150122151", "");
		dataMap_PDSK.put("T9700150122218", "");
		dataMap_PDSK.put("T9700150122242", "");
		dataMap_PDSK.put("T9700150122259", "");
		dataMap_PDSK.put("T9700150122275", "");
		dataMap_PDSK.put("T9700150122283", "");
		dataMap_PDSK.put("T9700150122283", "");
		dataMap_PDSK.put("T9700150122283", "");
		dataMap_PDSK.put("T9700150122291", "");
		dataMap_PDSK.put("T9700150122333", "");
		dataMap_PDSK.put("T9700150122424", "");
		dataMap_PDSK.put("T9700150122432", "");
		dataMap_PDSK.put("T9700150122432", "");
		dataMap_PDSK.put("T9700150122572", "");
		dataMap_PDSK.put("T9700150122580", "");
		dataMap_PDSK.put("T9700150122589", "");
		dataMap_PDSK.put("T9700150122597", "");
		dataMap_PDSK.put("T9700150122606", "");
		dataMap_PDSK.put("T9700150122639", "");
		dataMap_PDSK.put("T9700150122738", "");
		dataMap_PDSK.put("T9700150122779", "");
		dataMap_PDSK.put("T9700150122993", "");
		dataMap_PDSK.put("T9700150123001", "");
		dataMap_PDSK.put("T9700150123042", "");
		dataMap_PDSK.put("T9700150123050", "");
		dataMap_PDSK.put("T9700150123067", "");
		dataMap_PDSK.put("T9700150123100", "");
		dataMap_PDSK.put("T9700150123158", "");
		dataMap_PDSK.put("T9700150123166", "");
		dataMap_PDSK.put("T9700150123216", "");
		dataMap_PDSK.put("T9700150123273", "");
		dataMap_PDSK.put("T9700150123496", "");
		dataMap_PDSK.put("T9700150123570", "");
		dataMap_PDSK.put("T9700150123579", "");
		dataMap_PDSK.put("T9700150123587", "");
		dataMap_PDSK.put("T9700150123587", "");
		dataMap_PDSK.put("T9700150123595", "");
		dataMap_PDSK.put("T9700150123678", "");
		dataMap_PDSK.put("T9700150123760", "");
		dataMap_PDSK.put("T9700150123802", "");
		dataMap_PDSK.put("T9700150124313", "");
		dataMap_PDSK.put("T9700150124420", "");
		dataMap_PDSK.put("T9700150124429", "");
		dataMap_PDSK.put("T9700150124461", "");
		dataMap_PDSK.put("T9700150124627", "");
		dataMap_PDSK.put("T9700150124750", "");
		dataMap_PDSK.put("T9700150125542", "");
		dataMap_PDSK.put("T9700150125939", "");
		dataMap_PDSK.put("T9700150126425", "");
		dataMap_PDSK.put("T9700150126912", "");
		dataMap_PDSK.put("T9700150127877", "");
		dataMap_PDSK.put("T9700150128405", "");
		dataMap_PDSK.put("T9700150128867", "");
		dataMap_PDSK.put("T9700150128875", "");
		dataMap_PDSK.put("T9700150129171", "");
		dataMap_PDSK.put("T9700150129205", "");
		dataMap_PDSK.put("T9700150129460", "");
		dataMap_PDSK.put("T9700150129469", "");
		dataMap_PDSK.put("T9700150129717", "");
		dataMap_PDSK.put("T9700150129766", "");
		dataMap_PDSK.put("T9700150130105", "");
		dataMap_PDSK.put("T9700150130220", "");
		dataMap_PDSK.put("T9700150130286", "");
		dataMap_PDSK.put("T9700150130699", "");
		dataMap_PDSK.put("T9700150130724", "");
		dataMap_PDSK.put("T9700150131103", "");
		dataMap_PDSK.put("T9700150131111", "");
		dataMap_PDSK.put("T9700150131458", "");
		dataMap_PDSK.put("T9700150131474", "");
		dataMap_PDSK.put("T9700150131961", "");
		dataMap_PDSK.put("T9700150132134", "");
		dataMap_PDSK.put("T9700150132167", "");
		dataMap_PDSK.put("T9700150132316", "");
		dataMap_PDSK.put("T9700150132357", "");
		dataMap_PDSK.put("T9700150132456", "");
		dataMap_PDSK.put("T9700150132489", "");
		dataMap_PDSK.put("T9700150132968", "");
		dataMap_PDSK.put("T9700150133124", "");
		dataMap_PDSK.put("T9810302930514", "");
		dataMap_PDSK.put("T9810323665486", "");
		dataMap_PDSK.put("T9810345321761", "");
		dataMap_PDSK.put("T9810402769869", "");
		dataMap_PDSK.put("T9810448148012", "");
		dataMap_PDSK.put("T9810584207250", "");
		dataMap_PDSK.put("T9810732533341", "");
		dataMap_PDSK.put("T9810812490643", "");

	}

	public static void main1(String[] args) throws Exception {

		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream(OUTPUT_FILE),
						StandardCharsets.UTF_8));

		// ⭐ 关键：写入 UTF-8 BOM（防止 Excel 乱码）
		writer.write("\uFEFF");

		long totalCount = 0;

		try (Stream<Path> paths = Files.walk(Paths.get(ROOT_PATH))) {

			for (Path path : (Iterable<Path>) paths
					.filter(Files::isRegularFile)
					.filter(p -> p.toString().toLowerCase().endsWith(".csv"))::iterator) {

				System.out.println(path);
				totalCount += processFile(path, writer);
			}
		}

		writer.close();

		System.out.println("符合条件总件数: " + totalCount);
	}

	private static long processFile(Path path, BufferedWriter writer) {

		long count = 0;

		try (BufferedReader br = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(path.toFile()),
						StandardCharsets.UTF_8))) {

			String line;
			boolean firstLine = true;

			while ((line = br.readLine()) != null) {

				if (firstLine) {
					firstLine = false;
					continue;
				}

				String[] cols = line.split(",", -1);

				String domesticForeign = cols[5].trim();
				String cancelDate = cols[9].trim();
				String invalidDate = cols[10].trim();

				if (("2".equals(domesticForeign) || "3".equals(domesticForeign))
						&& cancelDate.isEmpty()
						&& invalidDate.isEmpty()) {

					writer.write(line);
					writer.newLine();
					count++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return count;
	}



    /**
     * 根据序列号分析连续数据分组，输出 CSV
     * @param inputCsv 输入 CSV 文件路径（第一列为序列号）
     * @param outputCsv 输出 CSV 文件路径
     * @throws IOException
     */
    public static void analyzeConsecutiveAndSave(String inputCsv, String outputCsv) throws IOException {
        List<Integer> numbers = new ArrayList<>();

        // 读取 CSV 第一列
        try (BufferedReader br = new BufferedReader(new FileReader(inputCsv))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(","); // 逗号分隔
                if (parts.length > 0) {
                    try {
                        numbers.add(Integer.parseInt(parts[0].trim()));
                    } catch (NumberFormatException e) {
                        // 跳过非数字行
                    }
                }
            }
        }

        if (numbers.isEmpty()) {
            System.out.println("没有有效序列号！");
            return;
        }

        // 排序
        Collections.sort(numbers);

        List<String> resultLines = new ArrayList<>();
        int start = numbers.get(0);
        int prev = start;
        int count = 1;

        for (int i = 1; i < numbers.size(); i++) {
            int curr = numbers.get(i);
            if (curr == prev + 1) {
                count++;
            } else {
                // 不连续，保存上一组
                resultLines.add(start + "," + prev + "," + count);
                start = curr;
                count = 1;
            }
            prev = curr;
        }
        // 保存最后一组
        resultLines.add(start + "," + prev + "," + count);

        // 写 CSV
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputCsv))) {
            bw.write("startSeq,endSeq,count");
            bw.newLine();
            for (String line : resultLines) {
                bw.write(line);
                bw.newLine();
            }
        }

        System.out.println("分析完成，结果已保存到: " + outputCsv);
    }


}
