package com.panda.batch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.panda.bean.User_infoBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.utils.FuncUtils;

public class batchTatujin {

	private static Logger logger = Logger.getLogger(batchTatujin.class.toString());




	public static void main(String[] args) throws Exception {


		System.out.println(FuncUtils.toHalfWidthAndTruncate("液晶テレビ液晶テレビ液晶テレビ液晶テレビ液晶テレビ液晶テレビ液晶テレビ液晶テレビ", 20));
	}

	public static void main1(String[] args) throws Exception {
		/*
		复制文件夹
		D:\batch消费税申告数据抽出\申告用文件夹模板
		，到D:\batch消费税申告数据抽出\申告用文件夹
		下边。重新命名
		命名规则：Tatujin_id加上下划线，加上CompanyName_Chinese
		同时在文件夹内创建txt文件，名字，Tatujin_id加上下划线，加上CompanyName_English
		 */

		Path templateDir = Paths.get("D:\\batch消费税申告数据抽出\\申告用文件夹模板");
		Path targetBaseDir = Paths.get("D:\\batch消费税申告数据抽出\\申告用文件夹");

		String maxNo = "";
		String sort = "update_date";
		String filter = "";

		User_infoBean User_infoBean = new User_infoBean();
		User_infoBean.setUser_id("wangzihao");
		User_infoBean.setPermissions("admin");

		t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();

		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectUserInfo();
		//		LinkedHashMap<String, ArrayList<String>> tatujinHashMap = t_etax_account_infoDao.selectTatujin();

		ArrayList<String> timu = new ArrayList<String>();

		//		 0			 timu.add("UPDATE_DATE");
		//		 1			 timu.add("管理ID");
		//		 2			 timu.add("达人ID");
		//		 3			 timu.add("日本消费税税号");
		//		 4			 timu.add("ETAX账号");
		//		 5			 timu.add("ETAX密码");
		//		 6			 timu.add("官方用户类型");
		//		 7			 timu.add("自选用户类型");
		//		 8			 timu.add("公司名称或个体户本人姓名（所在地区文字）");
		//		 9			 timu.add("公司名称或个体户本人姓名（英文）");
		//		 10			 timu.add("公司名称或个体户本人姓名（日文片假名）");
		//		 11			 timu.add("公司名称或个体户本人姓名（全大日文片假名）");
		//		 12			 timu.add("公司地址或个体户本人住址（所在地区文字）");
		//		 13			 timu.add("公司地址或个体户本人住址（英文）");
		//		 14			 timu.add("公司地址或个体户本人住址（日文片假名）");
		//		 15			 timu.add("公司代表人姓名或个体户经营场所名称（所在地区文字）");
		//		 16			 timu.add("公司代表人姓名或个体户经营场所名称（英文）");
		//		 17			 timu.add("公司代表人姓名或个体户经营场所名称（日文片假名）");
		//		 18			 timu.add("公司代表人住址或个体户经营场所地址（所在地区文字）");
		//		 19			 timu.add("公司代表人住址或个体户经营场所地址（英文）");
		//		 20			 timu.add("公司代表人住址或个体户经营场所地址（日文片假名）");
		//		 21			 timu.add("公司成立年或个体户本人出生年");
		//		 22			 timu.add("公司成立月或个体户本人出生月");
		//		 23			 timu.add("公司成立日或个体户本人出生日");

		for (ArrayList<String> myList : tatujinHashMap.values()) {

			//			String CompanyName_Chinese = t_etax_account_infoBean.getCompanyName_Chinese();
			String CompanyName_Chinese = myList.get(8);

			//			String CompanyName_English = t_etax_account_infoBean.getCompanyName_English();
			String CompanyName_English = myList.get(9);

			//			String Tatujin_id = t_etax_account_infoBean.getTatujin_id();
			String Tatujin_id = myList.get(2);

			//			+ "    and ( "
			//			+ "        ta_gHojinmei.html_value IS NULL "
			//			+ "        or ( "
			//			+ "            ta_gHojinmei.html_value not like '%ＴＥＳＴ%' "
			//			+ "            AND ta_gHojinmei.html_value not like '%Ｆｏｒｅｖｅｒ%'"
			//			+ "        )"
			//			+ "    ) "
			//			+ "    and ( "
			//			+ "        ti.CompanyName_Chinese NOT LIKE '%（删除20%' "
			//			+ "        AND ti.CompanyName_English NOT LIKE '%（删除20%' "
			//			+ "        AND ti.DaibiaoName_Chinese NOT LIKE '%（删除20%' "
			//			+ "        AND ti.DaibiaoName_English NOT LIKE '%（删除20%'"
			//			+ "    ) "

			if ("V240612775".equals(Tatujin_id) || "V241106247".equals(Tatujin_id)) {
				//				System.out.println("NG0：" + Tatujin_id + " " +CompanyName_English);
			}

			if (CompanyName_English.indexOf("TEST") > -1 || CompanyName_English.indexOf("Forever") > -1
					|| CompanyName_Chinese.indexOf("删除") > -1 || CompanyName_English.indexOf("删除") > -1
			//					||DaibiaoName_Chinese.indexOf("删除") > -1 || DaibiaoName_English.indexOf("删除") > -1
			) {
				System.out.println("NG1：" + Tatujin_id + " " + CompanyName_English);
				continue;
			}

			//			Windows 文件名 不能包含：
			//			\ / : * ? " < > |
			if (CompanyName_English.indexOf("\\") > -1 || CompanyName_English.indexOf("/") > -1) {
				System.out.println("NG2：" + Tatujin_id + " " + CompanyName_English);
				//				continue;

				CompanyName_English = CompanyName_English.replaceAll("[\\\\/:*?\"<>|]", " ");
				CompanyName_Chinese = CompanyName_Chinese.replaceAll("[\\\\/:*?\"<>|]", " ");

			}

			//			CompanyName_Chinese = FuncUtils.toHalfWidthAndTruncate(CompanyName_Chinese, 25);
			//			CompanyName_English = FuncUtils.toHalfWidthAndTruncate(CompanyName_English, 25);
			CompanyName_Chinese = FuncUtils.toHalfWidth(CompanyName_Chinese);
			CompanyName_English = FuncUtils.toHalfWidth(CompanyName_English);
			Tatujin_id = FuncUtils.toHalfWidth(Tatujin_id);

			// 新文件夹名
			String newFolderName = Tatujin_id + "_" + CompanyName_Chinese;
			Path targetDir = targetBaseDir.resolve(newFolderName);

			// 1. 复制整个目录
			Files.walk(templateDir).forEach(source -> {
				try {
					Path destination = targetDir.resolve(templateDir.relativize(source));
					if (Files.isDirectory(source)) {
						Files.createDirectories(destination);
					} else {
						Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});

			// 2. 创建 txt 文件
			String txtFileName = Tatujin_id + "_" + CompanyName_English + ".txt";
			Path txtFilePath = targetDir.resolve(txtFileName);

			Files.write(
					txtFilePath,
					("TatujinId=" + Tatujin_id).getBytes(StandardCharsets.UTF_8),
					StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);

			//			System.out.println("处理完成：" + targetDir);

		}

		//100个分组
		batchMoveFolders(
				"D:\\batch消费税申告数据抽出\\申告用文件夹",
				100);

	}

	public static void batchMoveFolders(String baseDir, int batchSize) throws IOException {
		Path basePath = Paths.get(baseDir);

		if (!Files.isDirectory(basePath)) {
			throw new IllegalArgumentException("不是有效目录: " + baseDir);
		}

		// 1. 读取所有子文件夹
		List<Path> folders = Files.list(basePath)
				.filter(Files::isDirectory)
				.collect(Collectors.toList());

		// 2. 按“下划线左侧”排序
		folders.sort(Comparator.comparing(p -> getLeftName(p.getFileName().toString())));

		// 3. 每 batchSize 个分组
		for (int i = 0; i < folders.size(); i += batchSize) {
			int end = Math.min(i + batchSize, folders.size());

			List<Path> batch = folders.subList(i, end);

			String startName = getLeftName(batch.get(0).getFileName().toString());
			String endName = getLeftName(batch.get(batch.size() - 1).getFileName().toString());

			// 新文件夹名
			String newFolderName = startName + "_" + endName;
			Path targetDir = basePath.resolve(newFolderName);

			// 创建目标文件夹
			if (!Files.exists(targetDir)) {
				Files.createDirectories(targetDir);
			}

			// 4. 移动文件夹
			for (Path folder : batch) {
				Path target = targetDir.resolve(folder.getFileName());
				Files.move(folder, target, StandardCopyOption.REPLACE_EXISTING);
			}

			System.out.println("已处理: " + newFolderName);
		}
	}

	// 提取下划线左侧
	private static String getLeftName(String folderName) {
		int idx = folderName.indexOf('_');
		return idx > 0 ? folderName.substring(0, idx) : folderName;
	}

}
