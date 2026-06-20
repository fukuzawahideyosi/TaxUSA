package com.panda.batch;



import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.json.JSONObject; // 添加 JSON 库依赖

import com.panda.chrome.pandaWebDriver;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_etax_jieguoDao;
import com.panda.utils.FuncUtils;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;



public class batch {

	private static Logger logger = Logger.getLogger(batch.class.toString());
	 // 数据映射
	static LinkedHashMap<String, String> dataMap_PDSK = new LinkedHashMap<>();
	static LinkedHashMap<String, String> dataMap_PDSK_skip = new LinkedHashMap<>();
	static LinkedHashMap<String, String> dataMap_PDSK_keshui_type = new LinkedHashMap<>();
	static LinkedHashMap<String, String> dataMap_PDSK_yyyymmdd_count = new LinkedHashMap<>();
	static LinkedHashMap<String, String> dataMap_PDSK_CompanyName_Chinese = new LinkedHashMap<>();

	static LinkedHashMap<String, String> dataMap_EtaxNo_yyyymmdd_count = new LinkedHashMap<>();
	static LinkedHashMap<String, String> dataMap_yyyymmdd_count_CompanyName_Chinese = new LinkedHashMap<>();
//	static LinkedHashMap<String, String> dataMap_yyyymmdd_count_CompanyName_Chinese_skip = new LinkedHashMap<>();


	static LinkedHashMap<String, String> dataMap_yyyymmdd_count_skip = new LinkedHashMap<>();

	static LinkedHashMap<String, String> dataMap_CompanyName_Chinese_yyyymmdd_count = new LinkedHashMap<>();

	// 目标目录
	static String rootPath = "";

    //服务器式样设定资料，生成申告数据需要最新的服务器式样设定资料
	static String URL = "https://www.sunmoonjp.com";
//	static String URL = "http://127.0.0.1:8080/PandaServiceMA";

	static String hidden_shuilishi_del_type = "shuilishi_del_YES";
	static String ExecutorService_type = "";


	static boolean xtx = false;
	static boolean zip = false;
	static String DIRECTORY_PATH = "";



	static LocalDateTime now0 = LocalDateTime.now();
/*

取得凭证
cp -r /usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/kuaiji_import /var/ftp/ftpalist/20250114/

/var/ftp/ftpalist/20250114/get_zip.sh




 */

	public static void main(String[] args) throws Exception {


		set_dataMap_all();
//		set_dataMap_all1();
//		set_dataMap_skip();
//		set_dataMap_skip_DB();

/*
PDSK241004
【収納機関番号】
00200
【納付区分】
7424102073
【有効期限】
2025-04-21
【納付金額 】
200
 */

//		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
//		t_etax_jieguoExBean.setBangou("2333092810920096");
//		t_etax_jieguoExBean.setEtax_pw("");
//		t_etax_jieguoExBean.setShuunou_kikan_bangou("00200");
//		t_etax_jieguoExBean.setNoufu_kubun("7324108720");
//		t_etax_jieguoExBean.setNoufu_kingaku("164100");
//		t_etax_jieguoExBean.setCompanyName_pianjiaming("ジヨージヤンアーフイコージーヨウシエンゴンスー");


//		t_etax_jieguoExBean.setBangou("2646042410910064");
//		t_etax_jieguoExBean.setEtax_pw("");
//		t_etax_jieguoExBean.setShuunou_kikan_bangou("00200");
//		t_etax_jieguoExBean.setNoufu_kubun("7424102073");
//		t_etax_jieguoExBean.setNoufu_kingaku("200");
//		t_etax_jieguoExBean.setCompanyName_pianjiaming("シエンジエンシーモーベイコージーヨウシエンゴンスー");
//
//
//		pandaWebDriver testNoWEB = new pandaWebDriver();
//		testNoWEB.exc_letian_zhifu(t_etax_jieguoExBean);

//        List<t_freeeBean> rowDataList = testNoWEB.get_freee(t_etax_jieguoExBean);
//
//        t_freeeDao t_freeeDao = new t_freeeDao();
//        t_freeeDao.delete_where_max_torihiki_bi();
//        t_freeeDao.INSERT(rowDataList);

		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告";



		/*
"0，G列小于零，提示王
1，Etax没有号，提示王
2 ，重名，提示王
3，Etax有效检测，无效重新拿号
4，申告文件生成，Etax软件转一圈
※生成xtx文件，必须使用服务器，因为生定文件服务器是最新的。禁止使用本地"

		 */
		URL = "https://sunmoonjp.com";
		URL = "https://www.japanetax.com";
//		URL = "http://127.0.0.1:8080/PandaServiceMA";
		//dataMap設定必要
//		CheckEtaxNo();


		//bug
		//set_dataMap_EtaxNo設定必要
//		set_dataMap_EtaxNo();
//		//////getEtaxNoAll();


	    //服务器式样设定资料，生成申告数据需要最新的服务器式样设定资料
		URL = "https://sunmoonjp.com";
		URL = "https://www.japanetax.com";
		URL = "http://127.0.0.1:8080/PandaServiceMA";

		//test
		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告";

	    //服务器式样设定资料，生成申告数据需要最新的服务器式样设定资料
		xtx = true;
		zip = false;
		MAX_THREADS = 1; // 最大线程数
		ExecutorService_type = "get_jct_shengao_xtx_ncc_zip";
//		ExecutorService();
		//	_all.xtx 有没有下载异常的

		xtx = false;
		zip = true;
		MAX_THREADS = 10; // 最大线程数
		ExecutorService_type = "get_jct_shengao_xtx_ncc_zip";
//		ExecutorService();
		// _2024.zip 有没有下载异常的


		// 替换为你需要处理的路径
		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告" + " - 副本";
//		CheckCSV();


		/*
		 * 结果
		 */
		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告" + "结果";

		//dataMap設定必要
//		hidden_shuilishi_del_type = "shuilishi_del_YES_0";//外挂成的时候，没有权限代理，不需要删除
		hidden_shuilishi_del_type = "shuilishi_del_YES";
//		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告" + "结果";
//		set_dataMap_all_skip();
//		get_shengao_jieguo();


		//TODO TODO
		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告" + "结果";
		MAX_THREADS = 10; // 最大线程数
//		ExecutorService_type = "get_shengao_jieguo";
//		ExecutorService_type = "get_shengao_jieguo_dianzi_nashui";
		ExecutorService_type = "get_shengao_jieguo_tatujin";
//		set_dataMap_all_skip();//删除无用文件
		ExecutorService();
//		set_dataMap_all_skip();//删除无用文件


//		rootPath = "D:\\batch消费税申告数据抽出\\bk";
		DIRECTORY_PATH = rootPath;
//		Check_shengao_pdf();

		/*
		 * 中間申告
		 */
		URL = "https://www.sunmoonjp.com";
		URL = "http://127.0.0.1:8080/PandaServiceMA";
		rootPath = "D:\\batch消费税申告数据抽出\\20260403_1申告";
		MAX_THREADS = 1; // 最大线程数
		ExecutorService_type = "get_shengao_zhongjian";
//		set_dataMap_all_skip_zhongjian_shengao();//删除无用文件
//		ExecutorService_yyyymmdd_count();
//		set_dataMap_all_skip_zhongjian_shengao();//删除无用文件


/*
		//废弃
		//废弃
		//废弃
		//dataMap設定必要
//		get_jct_shengao_xtx_ncc();


//			get_shengao_pdf();
//			Check_shengao_pdf();


//			get_shengao_pdf_Update();
 */
	}


	private static  int MAX_THREADS = 1; // 最大线程数
	private static  int MIN_THREADS = 1; // 最小线程数
	private static final double TARGET_CPU_USAGE = 0.90; // 目标 CPU 负载 90%

	private static ThreadPoolExecutor executor;

	public static void ExecutorService() throws Exception {


		createFolderIfNotExists(rootPath);


		int availableCores = Runtime.getRuntime().availableProcessors();
		int initialPoolSize = Math.max(MIN_THREADS, availableCores / 2); // 初始线程数
		initialPoolSize=MAX_THREADS;

		// 创建可动态调整的线程池
		executor = new ThreadPoolExecutor(
				initialPoolSize, // 初始线程数
				MAX_THREADS, // 最大线程数
				60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(), // 任务队列
				new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略

		// 启动 CPU 负载监测任务
		ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();
		monitorService.scheduleAtFixedRate(() -> adjustThreadPoolSize(), 1, 2, TimeUnit.SECONDS); // 每2秒调整一次

		// 遍历 LinkedHashMap 并提交任务到线程池
		for (Map.Entry<String, String> entry : dataMap_PDSK.entrySet()) {
			if (dataMap_PDSK_skip.containsKey(entry.getKey())) {
				continue;
			}
			executor.submit(new Task(entry));
		}

		// 关闭线程池（所有任务执行完毕后才关闭）
		executor.shutdown();
		try {
			if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}

		// 关闭监测线程
		monitorService.shutdown();
	}


	public static void ExecutorService_yyyymmdd_count() throws Exception {


		createFolderIfNotExists(rootPath);


		int availableCores = Runtime.getRuntime().availableProcessors();
		int initialPoolSize = Math.max(MIN_THREADS, availableCores / 2); // 初始线程数
		initialPoolSize=MAX_THREADS;

		// 创建可动态调整的线程池
		executor = new ThreadPoolExecutor(
				initialPoolSize, // 初始线程数
				MAX_THREADS, // 最大线程数
				60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(), // 任务队列
				new ThreadPoolExecutor.CallerRunsPolicy()); // 拒绝策略

		// 启动 CPU 负载监测任务
		ScheduledExecutorService monitorService = Executors.newSingleThreadScheduledExecutor();
		monitorService.scheduleAtFixedRate(() -> adjustThreadPoolSize(), 1, 2, TimeUnit.SECONDS); // 每2秒调整一次

		// 遍历 LinkedHashMap 并提交任务到线程池
		for (Map.Entry<String, String> entry : dataMap_yyyymmdd_count_CompanyName_Chinese.entrySet()) {
			if (dataMap_yyyymmdd_count_skip.containsKey(entry.getKey())) {
				continue;
			}
			executor.submit(new Task(entry));
		}

		// 关闭线程池（所有任务执行完毕后才关闭）
		executor.shutdown();
		try {
			if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}

		// 关闭监测线程
		monitorService.shutdown();
	}


	/**
	 * 获取当前 CPU 负载，并动态调整线程池大小
	 */
	private static void adjustThreadPoolSize() {
		double cpuLoad = getCpuLoad(); // 获取 CPU 负载（0.0 - 1.0）
		int currentPoolSize = executor.getCorePoolSize();
		int newPoolSize = currentPoolSize;

		if (cpuLoad > TARGET_CPU_USAGE) {
			// CPU 负载过高，减少线程数
			newPoolSize = Math.max(MIN_THREADS, currentPoolSize - 1);
		} else if (cpuLoad < TARGET_CPU_USAGE * 0.85) {
			// CPU 负载过低，增加线程数
			newPoolSize = Math.min(MAX_THREADS, currentPoolSize + 1);
		}

		if (newPoolSize != currentPoolSize) {
			executor.setCorePoolSize(newPoolSize);
			executor.setMaximumPoolSize(newPoolSize);
			logger.info("调整线程数: " + newPoolSize + "，当前 CPU 负载: " + (cpuLoad * 100) + "%");
		}
	}

    // 线程执行的任务
    static class Task implements Runnable {
        private final Map.Entry<String, String> entry;

        public Task(Map.Entry<String, String> entry) {
            this.entry = entry;
        }

        @Override
        public void run() {
        	// 模拟任务执行时间
        	String folderName = entry.getKey() + "_" + entry.getValue();
            logger.info("🔹 线程 " + Thread.currentThread().getName() + " " + folderName);




    		try {

    			if ("get_jct_shengao_xtx_ncc_zip".equals(ExecutorService_type)) {
    				get_jct_shengao_xtx_ncc_zip(entry);

    			} else if ("get_shengao_jieguo".equals(ExecutorService_type) || "get_shengao_jieguo_tatujin".equals(ExecutorService_type)) {
    				get_shengao_jieguo(entry);


     			} else if ("get_shengao_zhongjian".equals(ExecutorService_type)) {
     				get_shengao_zhongjian(entry);



    			} else if ("get_shengao_jieguo_dianzi_nashui".equals(ExecutorService_type)) {
//    				get_shengao_jieguo_dianzi_nashui(entry);

    			}




			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
//				e1.printStackTrace();
			}

            logger.info("✅ 任务完成: " + folderName);
        }
    }


	/**
	 * 获取当前 CPU 负载（返回值 0.0 - 1.0 之间）
	 */
	private static double getCpuLoad() {
		OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
		return osBean.getSystemLoadAverage() / Runtime.getRuntime().availableProcessors();
	}

	public static void get_shengao_jieguo_dianzi_nashui(Map.Entry<String, String> entry) {

    	String folderName = entry.getKey() + "_" + entry.getValue();

        /*
         * xtx_nccファイル
         */
        try {
            // 在主文件夹下创建 xtx_nccファイル 子文件夹
//            File subFolder = new File(folder, "xtx_nccファイル");
//            if (subFolder.mkdirs()) {
////                logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
//            } else {
////                logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
//            }


        	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            // 第一步：通过请求获得压缩文件名
            String urlString = URL + "/ToolsReplaceLogic"
                    + "?license=wangzihao"
                    + "&pw=undefined"
                    + "&hidden_key=getShenqingJieguo"
                    + "&yyyymmddhhmmss="+ LocalDateTime.now().format(formatter)
                    + "&hidden_shenqing_type=" + URLEncoder.encode("申告")//
                    + "&hidden_shuilishi_del_type=" + hidden_shuilishi_del_type
                    + "&PDSK=" + entry.getKey();
                    ;
            String fileName = getFileNameFromServer("POST", urlString, entry.getKey());
            //TODO
//            if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
//            	logger.info("获取到的文件名: " + fileName);
//                logger.error("无法从服务器获取文件名。");
//                return;
//            }
//            logger.info("获取到的文件名: " + fileName);

        } catch (Exception e) {
        	e.printStackTrace(); //
            logger.error("处理过程中发生错误: " + e.getMessage());
            throw new RuntimeException("处理过程中发生错误:"+e.getMessage(),e);
        }
	}



	public static void get_shengao_zhongjian(Map.Entry<String, String> entry) {

    	String folderName = entry.getKey() + "_" + entry.getValue();

        /*
         * xtx_nccファイル
         */
        try {
            // 在主文件夹下创建 xtx_nccファイル 子文件夹
//            File subFolder = new File(folder, "xtx_nccファイル");
//            if (subFolder.mkdirs()) {
////                logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
//            } else {
////                logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
//            }


        	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

            // 第一步：通过请求获得压缩文件名
            String urlString = URL + "/ToolsReplaceLogic"
                    + "?license=wangzihao"
                    + "&pw=undefined"
                    + "&hidden_key=get_shengao_zhongjian"
                    + "&yyyymmddhhmmss="+ LocalDateTime.now().format(formatter)
                    + "&hidden_shenqing_type=" + URLEncoder.encode("中間申告")//
                    + "&hidden_shuilishi_del_type=" + hidden_shuilishi_del_type
                    + "&yyyymmdd_count=" + entry.getKey();
                    ;
            String fileName = getFileNameFromServer("POST", urlString, entry.getKey());

            if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
            	logger.info("获取到的文件名: " + fileName);
                logger.error("无法从服务器获取文件名。");
                return;
            }
//            logger.info("获取到的文件名: " + fileName);

            // 第二步：下载文件、解压缩并删除压缩文件
            String downloadUrl = URL + "/";
            String saveDir = rootPath;
            String saveFilePath = saveDir + "/" + folderName + ".zip";
            String extractDir = saveDir;

            // 下载 ZIP 文件
            downloadFile(downloadUrl, fileName, saveFilePath);

            // 解压 ZIP 文件
            extractZipFile(saveFilePath, extractDir);

            // 删除 ZIP 文件
            File zipFile = new File(saveFilePath);
            if (zipFile.delete()) {
//                logger.info("压缩文件已删除: " + saveFilePath);
            } else {
//                logger.error("无法删除压缩文件: " + saveFilePath);
            }

        } catch (Exception e) {
        	e.printStackTrace(); //
            logger.error("处理过程中发生错误: " + e.getMessage());
            throw new RuntimeException("处理过程中发生错误:"+e.getMessage(),e);
        }
	}



	public static void get_shengao_jieguo(Map.Entry<String, String> entry) {

    	String folderName = entry.getKey() + "_" + entry.getValue();

        /*
         * xtx_nccファイル
         */
        try {
            // 在主文件夹下创建 xtx_nccファイル 子文件夹
//            File subFolder = new File(folder, "xtx_nccファイル");
//            if (subFolder.mkdirs()) {
////                logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
//            } else {
////                logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
//            }


        	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        	 LocalDateTime now = LocalDateTime.now();

        	 String hidden_key = "getShenqingJieguo";
        	 if ("get_shengao_jieguo_tatujin".equals(ExecutorService_type)) {
        		 hidden_key = "getShenqingJieguoTatujin";
        		 now = now0;
        	 }

            // 第一步：通过请求获得压缩文件名
            String urlString = URL + "/ToolsReplaceLogic"
                    + "?license=wangzihao"
                    + "&pw=undefined"
                    + "&hidden_key=" + hidden_key
                    + "&yyyymmddhhmmss="+ now.format(formatter)
                    + "&hidden_shenqing_type=" + URLEncoder.encode("申告")//
                    + "&hidden_shuilishi_del_type=" + hidden_shuilishi_del_type
                    + "&PDSK=" + entry.getKey()
		            + "&tatujin_id=" + entry.getKey()
                    ;
            String fileName = getFileNameFromServer("POST", urlString, entry.getKey());

            if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
            	logger.info("获取到的文件名: " + fileName);
                logger.error("无法从服务器获取文件名。");
                return;
            }
//            logger.info("获取到的文件名: " + fileName);

            // 第二步：下载文件、解压缩并删除压缩文件
//            String downloadUrl = URL + "/";
//            String saveDir = rootPath;
//            String saveFilePath = saveDir + "/" + folderName + ".zip";
//            String extractDir = saveDir;
//
//            // 下载 ZIP 文件
//            downloadFile(downloadUrl, fileName, saveFilePath);
//
//            // 解压 ZIP 文件
//            extractZipFile(saveFilePath, extractDir);
//
//            // 删除 ZIP 文件
//            File zipFile = new File(saveFilePath);
//            if (zipFile.delete()) {
////                logger.info("压缩文件已删除: " + saveFilePath);
//            } else {
////                logger.error("无法删除压缩文件: " + saveFilePath);
//            }

        } catch (Exception e) {
        	e.printStackTrace(); //
            logger.error("处理过程中发生错误: " + e.getMessage());
            throw new RuntimeException("处理过程中发生错误:"+e.getMessage(),e);
        }
	}


	private static void CheckCSV() {


		logger.info("kaishi");






		try {
			// Step 1: 递归查找路径下所有压缩文件并解压
			processCompressedFiles(rootPath);

			// Step 2: 递归查找路径下所有 CSV 文件并打印路径
			findCsvFiles(rootPath);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
//			e.printStackTrace();
		}


        // **最后输出所有解压文件**
        logger.info("\nCSV文件NG列表：");
        for (String file : extractedFiles) {
            logger.info(file);
        }

	}


    /**
     * 递归查找路径下所有压缩文件并解压缩
     *
     * @param rootPath 起始路径
     * @throws IOException
     */
    public static void processCompressedFiles(String rootPath) throws IOException {
        File dir = new File(rootPath);

        // 检查是否为目录
        if (!dir.isDirectory()) {
            logger.error("路径不是目录：" + rootPath);
            return;
        }

        // 获取当前目录下的所有文件和文件夹
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，递归查找
                processCompressedFiles(file.getAbsolutePath());
            } else if (file.getName().toLowerCase().endsWith(".zip")) {
                // 如果是 ZIP 文件，进行解压
                try {
                    unzipFile(file);
                } catch (IOException e) {
                    logger.error("解压 ZIP 失败：" + file.getAbsolutePath(), e);
                    throw new IOException("解压 ZIP 失败：" + file.getAbsolutePath(), e);
                }
            } else if (file.getName().toLowerCase().endsWith(".rar")) {
                // 如果是 RAR 文件，使用 SevenZipJBinding 解压
                try {
                    unrarFile(file);
                } catch (IOException e) {
                    logger.error("解压 RAR 失败：" + file.getAbsolutePath(), e);
                    throw new IOException("解压 RAR 失败：" + file.getAbsolutePath(), e);
                }
            }
        }
    }

    /**
     * 解压 ZIP 文件到当前目录，并在文件名末尾加随机数防止重名
     *
     * @param zipFile ZIP 文件
     * @throws IOException 解压错误
     */
//    public static void unzipFile0(File zipFile) throws IOException {
//        File destDir = zipFile.getParentFile(); // 解压到当前目录
//        byte[] buffer = new byte[1024];
//
//        logger.info("解压対象：" + zipFile.getPath());
//
//        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
//            ZipEntry zipEntry;
//            while ((zipEntry = zis.getNextEntry()) != null) {
//                String fileName = zipEntry.getName();
//                // 给文件名加上随机数防止重名
//                String newFileName = generateUniqueFileName(destDir, fileName);
//                File newFile = new File(destDir, newFileName);
//
//                // 创建父目录
//                new File(newFile.getParent()).mkdirs();
//
//                // 写入文件
//                try (FileOutputStream fos = new FileOutputStream(newFile)) {
//                    int len;
//                    while ((len = zis.read(buffer)) > 0) {
//                        fos.write(buffer, 0, len);
//                    }
//                }
//                logger.info("解压文件：" + newFile.getAbsolutePath());
//            }
//            zis.closeEntry();
//        }
//    }



    /**
     * 解压 ZIP 文件到当前目录，并在文件名末尾加随机数防止重名
     *
     * @param zipFile ZIP 文件
     * @throws IOException 解压错误
     */
    public static void unzipFile(File zipFile) throws IOException {
        File destDir = zipFile.getParentFile(); // 解压到当前目录
        byte[] buffer = new byte[1024];

        logger.info("解压対象：" + zipFile.getPath());

        // 使用 Apache Commons Compress 处理 ZIP 文件
        try (ZipFile zip = new ZipFile(zipFile)) { // 使用默认构造函数
            Enumeration<ZipArchiveEntry> entries = zip.getEntries(); // 获取 ZIP 中的条目
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                String fileName = new String(entry.getRawName(), Charset.forName("GBK")); // 使用 GBK 解码

                // 给文件名加上随机数防止重名
                String newFileName = generateUniqueFileName(destDir, fileName);
                File newFile = new File(destDir, newFileName);

                if (entry.isDirectory()) {
                    // 创建目录
                    if (!newFile.mkdirs()) {
                        System.err.println("创建目录失败：" + newFile.getAbsolutePath());
                    }
                } else {
                    // 创建父目录
                    if (!newFile.getParentFile().mkdirs() && !newFile.getParentFile().exists()) {
                        System.err.println("创建父目录失败：" + newFile.getParentFile().getAbsolutePath());
                    }

                    // 写入文件
                    try (InputStream is = zip.getInputStream(entry);
                         FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = is.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    logger.info("解压文件：" + newFile.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            System.err.println("ZIP 文件解压错误，文件路径：" + zipFile.getPath());
            e.printStackTrace();
            throw new IOException("解压 ZIP 文件失败：" + zipFile.getPath(), e);
        }
    }


    /**
     * 使用 SevenZipJBinding 解压 RAR 文件
     *
     * @param rarFile RAR 文件
     * @throws IOException 解压错误
     */
    public static void unrarFile(File rarFile) throws IOException {
        File destDir = rarFile.getParentFile(); // 解压到当前目录

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(rarFile, "r")) {
        	IInArchive inArchive = null;
            // 自动识别格式
        	// 检查是否为 RAR5 格式，若不是则尝试 RAR 格式
            try {
				inArchive = SevenZip.openInArchive(
				    ArchiveFormat.RAR5, // 首先尝试使用 RAR 格式
				    new RandomAccessFileInStream(randomAccessFile)
				);
			} catch (Exception e1) {
			}

            if (inArchive == null) {
                inArchive = SevenZip.openInArchive(
                		ArchiveFormat.RAR,
                    new RandomAccessFileInStream(randomAccessFile)
                );
            }


            if (inArchive == null) {
                throw new IOException("无法识别 RAR 文件格式：" + rarFile.getAbsolutePath());
            }

            ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
                if (!item.isFolder()) {
                    File outputFile = new File(destDir, item.getPath());
                    // 创建父目录
                    new File(outputFile.getParent()).mkdirs();

                    // 解压文件
                    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                        ExtractOperationResult result = item.extractSlow(data -> {
                            try {
								fos.write(data);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
                            return data.length;
                        });

                        if (result != ExtractOperationResult.OK) {
                            throw new IOException("解压失败：" + item.getPath());
                        }
                    }
                    logger.info("解压文件：" + outputFile.getAbsolutePath());
                }
            }

            // 释放资源
            inArchive.close();
        } catch (Exception e) {
            throw new IOException("解压 RAR 文件失败：" + rarFile.getAbsolutePath(), e);
        }
    }



    /**
     * 生成唯一的文件名，在文件名末尾加随机数防止重名
     *
     * @param dir      解压目录
     * @param fileName 原始文件名
     * @return 唯一的文件名
     */
    public static String generateUniqueFileName(File dir, String fileName) {
        String name = fileName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex != -1) {
            name = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        }

        Random random = new Random();
        String newFileName = name + "_" + random.nextInt(10000) + extension;

        // 检查文件是否已存在
        while (new File(dir, newFileName).exists()) {
            newFileName = name + "_" + random.nextInt(10000) + extension;
        }
        return newFileName;
    }


    static ArrayList<String> extractedFiles = new ArrayList<>(); // 存储解压后的文件名
    /**
     * 递归查找路径下所有 CSV 文件并打印路径
     *
     * @param rootPath 起始路径
     * @throws Exception
     */
    public static void findCsvFiles(String rootPath) throws Exception {
        File dir = new File(rootPath);

        // 检查是否为目录
        if (!dir.isDirectory()) {
            logger.error("路径不是目录：" + rootPath);
            return;
        }

        // 获取当前目录下的所有文件和文件夹
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // 如果是目录，递归查找
                findCsvFiles(file.getAbsolutePath());
            } else if (file.getName().endsWith(".csv")) {
                // 如果是 CSV 文件，打印路径
                logger.info("找到 CSV 文件：" + file.getAbsolutePath());



				BufferedReader br = null;
				String line = "";
				StringBuilder sb = new StringBuilder();

				try {

		            String charset = FuncUtils.detectCharset(file.getPath());
		            logger.info("CSV 文件编码是: " + charset);

					if ("IBM866".equals(charset)) {
						charset = "GB18030";
					}

					FileInputStream fis = new FileInputStream(file);
//					InputStreamReader isr = new InputStreamReader(fis, Charset.forName("SJIS"));
					InputStreamReader isr = new InputStreamReader(fis, Charset.forName(charset));
					br = new BufferedReader(isr);

					boolean csv_falg = false;
					while ((line = br.readLine()) != null) {
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

						String dateTimeString = data[0];
						String startDateStr = "20000101";//req.getParameter("form_S_YYYYMMDD");
						String endDateStr = "20991231";//req.getParameter("form_E_YYYYMMDD");
						boolean isLastYear = FuncUtils.isLastYear(startDateStr, endDateStr, dateTimeString);
						//	1 删去表头行之上所有行，保留表头行
						//	表头行：即 【日付/時間  決済番号……】的这一行
						if ("日付/時間".equals(data[0])) {
							csv_falg = true;
							data[27] = data[27].replaceAll("\"", "");
							sb.append("\"" + String.join("\",\"", data) + "\"").append("\r\n");
							continue;

							//	2 删去所需时间之外的所有行（除表头行）
							//	所需时间，比如2023/10/01~2023/12/31 这种
						} else if (isLastYear == true) {

						} else {
							continue;

						}

						// 获取系统类型属性
						String osName = System.getProperty("os.name");
						// 您可以根据不同的系统类型执行不同的操作
						if (osName.toLowerCase().contains("windows")) {
//							logger.info("这是Windows系统");
//							logger.debug(data[0]);
						} else if (osName.toLowerCase().contains("linux")) {
//							logger.info("这是Linux系统");

						}

//						data[27] = data[27].replaceAll("\"", "");


						if (data.length != 28) {
							if ((data.length == 30 && file.getPath().contains("PDSK240365"))
									|| (data.length == 29 && file.getPath().contains("PDSK240444"))
									|| (data.length == 29 && file.getPath().contains("PDSK240560"))
									|| (data.length == 29 && file.getPath().contains("PDSK240201"))
									|| (data.length == 29 && file.getPath().contains("PDSK240467"))
									|| (data.length == 6 && file.getPath().contains("PDSK240577"))
									) {

							} else {
//								throw new Exception("CSV格式不正确");
					            logger.error("CSV格式不正确");
								extractedFiles.add(file.getPath());
								break;
							}
						}



					}
				} catch (Throwable e) {
					e.printStackTrace();
					throw new Exception("CSV格式不正确");
				} finally {
					if (br != null) {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}



            }
        }

    }




	private static void CheckEtaxNo() {



        try {
            // 设置请求体数据 (模拟 content)
            String content = "";
    	    // 遍历 dataMap
            for (Map.Entry<String, String> entry : dataMap_EtaxNo_yyyymmdd_count.entrySet()) {
                String folderName = entry.getKey() + "_" + entry.getValue();
                content = content + System.lineSeparator() + dataMap_EtaxNo_yyyymmdd_count.get(entry.getKey());
            }

            // https://www.sunmoonjp.com/ToolsReplaceLogic?license=wangzihao&pw=undefined&yaoqing_no=undefined&hidden_key=CheckEtaxNo

            String urlString = URL + "/ToolsReplaceLogic"
                    + "?license=wangzihao"
                    + "&pw=undefined"
                    + "&hidden_key=CheckEtaxNo"
                    ;

            // **设置超时：1小时（3600秒）**
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(3600))  // **连接超时**
                    .setResponseTimeout(Timeout.ofSeconds(3600)) // **响应超时**
                    .setConnectionRequestTimeout(Timeout.ofSeconds(3600)) // **获取连接超时**
                    .build();

            // **创建 HttpClient 并设置超时**
            try (CloseableHttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig)
                    .build()) {
                HttpPost postRequest = new HttpPost(urlString);
                postRequest.setHeader("Content-Type", "text/plain; charset=utf-8");
                postRequest.setEntity(new StringEntity(content, StandardCharsets.UTF_8));
                // 发送请求并获取响应
                try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        logger.info("Response: " + line);
                    }
                }
            }



//
//            // 模拟 HttpServletRequest
//            HttpServletRequest request = mock(HttpServletRequest.class);
//
//            when(request.getMethod()).thenReturn("GET");
//            when(request.getRequestURI()).thenReturn(urlString);
//            when(request.getParameter("etaxNo")).thenReturn("2038002220910031");
//
//            PrintWriter out = mock(PrintWriter.class);
//
//
//            ToolsReplaceLogic ToolsReplaceLogic = new ToolsReplaceLogic();
//            // 调用目标方法
//			ToolsReplaceLogic.CheckEtaxNo(request, out);
//	        // 输出结果
//	        logger.info("服务器返回内容：\n" + out.toString());


		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}








    private static void Check_shengao_pdf() {
    	File directory = new File(DIRECTORY_PATH);

    	if (!directory.exists() || !directory.isDirectory()) {
    		logger.info("目录不存在：" + DIRECTORY_PATH);
    		return;
    	}

    	// 获取所有 PDF 文件
    	//外挂生成的pdf
//    	File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
//    	if (pdfFiles == null || pdfFiles.length == 0) {
//    		logger.info("未找到 PDF 文件");
//    		return;
//    	}
    	//自动抓取的pdf
    	List<File> pdfFiles = findAllPDFs(directory);
    	if (pdfFiles == null || pdfFiles.size() == 0) {
    		logger.info("未找到 PDF 文件");
    		return;
    	}

    	try {
    		int count = 0;
    		for (File pdfFile : pdfFiles) {
    			// 解析文件名
    			String fileName = pdfFile.getName();

    			if (fileName.contains("提出消費税申告書.pdf")) {

    			} else {
    				continue;
    			}



    	        //检查 PDF 文件头和尾部
    	        try (RandomAccessFile file = new RandomAccessFile(pdfFile.getPath(), "r")) {
    	        	byte[] header = new byte[5];
    	        	file.read(header);
    	        	String headerStr = new String(header, StandardCharsets.US_ASCII);

    	        	file.seek(file.length() - 6);
    	        	byte[] footer = new byte[6];
    	        	file.read(footer);
    	        	String footerStr = new String(footer, StandardCharsets.US_ASCII);
    	        	if (headerStr.startsWith("%PDF-") && footerStr.contains("%%EOF") == true) {

    	        	} else {
    	        		System.err.println("❌ PDF损坏: " + pdfFile.getAbsolutePath());
    	        		continue;
    	        	}


    	        } catch (Exception e) {
    	        	logger.info("PDF 头部或尾部损坏：" + pdfFile.getAbsolutePath());
    	        	e.printStackTrace();
	        		continue;
    	        }



    	        try {

        			String firstPart = fileName.split("_")[0]; // 取下划线分隔的第一部分

        	        // 正则表达式匹配 "PDSK" 后 6 位数字（确保不多取）
        	        Pattern pattern = Pattern.compile("PDSK\\d{6}");
        	        Matcher matcher = pattern.matcher(pdfFile.getPath());

        	        if (matcher.find()) {
        	        	firstPart = matcher.group();
        	        }



    	        	PDDocument document = PDDocument.load(pdfFile);
    	        	int totalPages = document.getNumberOfPages(); // 获取 PDF 页数
    		            logger.info(fileName + " 页数: " + totalPages);

    	        	document.close(); // 关闭 PDF 资源


    	        	String keshui_type = dataMap_PDSK_keshui_type.get(firstPart);
    	        	if (keshui_type.indexOf("原则课税（2割特例）") > -1) {
    	        		keshui_type = keshui_type;
    	        	}

    	        	int expectedPages = getExpectedPages(keshui_type);

    	        	// 4️⃣ 判断页数是否符合预期
    	        	if (expectedPages == -1) {
    	        		logger.info("⚠️ 未知的 keshui_type: " + keshui_type + " 文件: " + pdfFile.getAbsolutePath());
    	        		continue;
    	        	}

    	        	if (totalPages != expectedPages) {
    	        		System.err.println("❌ 页数不符: " + pdfFile.getAbsolutePath() + " (实际: " + totalPages + " 预期: " + expectedPages + ")");
    	        	} else {
    	        		++count;
//                            logger.info("✅ 页数符合: " + pdfFile.getAbsolutePath() + " (" + totalPages + " 页)");
    	        	}


    	        } catch (IOException e) {
    	        	e.printStackTrace();
    	        }


    		}
    		logger.info("✅ 页数符合count: " + count);


    	} catch (Exception e) {
    		e.printStackTrace();
    	}

    }

    public static List<File> findAllPDFs(File directory) {
        try {
            return Files.walk(directory.toPath()) // 递归遍历所有子目录
                    .filter(path -> Files.isRegularFile(path) && path.toString().toLowerCase().endsWith(".pdf")) // 筛选 PDF
                    .map(Path::toFile) // 转换为 File
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("遍历文件夹失败: " + e.getMessage(), e);
        }
    }

    /*
简易课税，申告书5页，代理权限1页，共6页
原则课税，申告书4页，代理权限1页，共5页
二割课税，申告书3页，代理权限1页，共4页
     */

    /**
     * 根据 keshui_type 返回预期页数
     */
    private static int getExpectedPages(String keshui_type) {
        if (keshui_type == null) {
            return -1; // 避免 NullPointerException
        }

        keshui_type = keshui_type.trim(); // 去除前后空格

        if ("简易课税（零售业）".equals(keshui_type)) {
            return 5;
        } else if ("原则课税".equals(keshui_type)) {
            return 4;
        } else if ("原则课税（2割特例）".equals(keshui_type)) {
            return 3;
        } else {
            return -1; // 未知类型
        }
    }


    private static void get_shengao_pdf_Update() {

    	File directory = new File(DIRECTORY_PATH);

    	if (!directory.exists() || !directory.isDirectory()) {
    		logger.info("目录不存在：" + DIRECTORY_PATH);
    		return;
    	}

    	// 获取所有 PDF 文件
    	File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

    	if (pdfFiles == null || pdfFiles.length == 0) {
    		logger.info("未找到 PDF 文件");
    		return;
    	}
    	t_etax_jieguoDao t_etax_jieguoDao = new t_etax_jieguoDao();
    	try {
    		for (File pdfFile : pdfFiles) {
    			// 解析文件名
    			String fileName = pdfFile.getName();
    			String firstPart = fileName.split("_")[0]; // 取下划线分隔的第一部分

    			InputStream fileContent = new FileInputStream(new File(pdfFile.getPath()));
    			// 更新数据库
    			t_etax_jieguoDao.UPDATE_jietuo_pdf_where_PDSK(firstPart, "2024", "申告", fileContent);
    			logger.info("已更新文件：" + fileName + " 到数据库，PDSK：" + firstPart);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

	}




	public static void get_shengao_pdf() throws Exception {
		/*
		 *取得：お知らせ・受信通知
		 */
		pandaWebDriver testNoWEB = new pandaWebDriver(null);

		 // 指定目录
        File directory = new File("C:\\xtxALL");


        // 检查目录是否存在
        if (directory.exists() && directory.isDirectory()) {
            // 获取所有 .xtx 文件
            File[] xtxFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xtx"));

            // 检查是否有符合条件的文件
            if (xtxFiles != null && xtxFiles.length > 0) {
                for (File file : xtxFiles) {

                    logger.info("文件路径：" + file.getAbsolutePath());
            		testNoWEB.getShenqingPDF(file.getPath());
                }
            } else {
                logger.info("未找到任何 .xtx 文件");
            }
        } else {
            logger.info("目录不存在或不是一个有效目录：" + directory.getAbsolutePath());
        }





	    // 遍历 dataMap
        for (Map.Entry<String, String> entry : dataMap_PDSK.entrySet()) {
            String folderName = entry.getKey() + "_" + entry.getValue();
//            File folder = new File(rootPath, folderName);
//            logger.info("folderName: " + folderName);
//
//            // 创建主文件夹
//            if (folder.mkdirs()) {
////                logger.info("Created folder: " + folder.getAbsolutePath());
//            } else {
////                logger.info("Failed to create folder: " + folder.getAbsolutePath());
//            }


            /*
             * xtx_nccファイル
             */
            try {
                // 在主文件夹下创建 xtx_nccファイル 子文件夹
//                File subFolder = new File(folder, "xtx_nccファイル");
//                if (subFolder.mkdirs()) {
////                    logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
//                } else {
////                    logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
//                }


            	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

                // 第一步：通过请求获得压缩文件名
                String urlString = URL + "/ToolsReplaceLogic"
                        + "?license=wangzihao"
                        + "&pw=undefined"
                        + "&hidden_key=getShenqingJieguo"
                        + "&yyyymmddhhmmss="+ LocalDateTime.now().format(formatter)
                        + "&hidden_shenqing_type=" + URLEncoder.encode("申告")//
                        + "&hidden_shuilishi_del_type=" + hidden_shuilishi_del_type
                        + "&PDSK=" + entry.getKey();
                        ;
                String fileName = getFileNameFromServer("POST", urlString, entry.getKey());

                if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
                	logger.info("获取到的文件名: " + fileName);
                    logger.error("无法从服务器获取文件名。");
                    return;
                }
                logger.info("获取到的文件名: " + fileName);

                // 第二步：下载文件、解压缩并删除压缩文件
                String downloadUrl = URL + "/";
                String saveDir = rootPath;
                String saveFilePath = saveDir + "/" + folderName + ".zip";
                String extractDir = saveDir;

                // 下载 ZIP 文件
                downloadFile(downloadUrl, fileName, saveFilePath);

                // 解压 ZIP 文件
                extractZipFile(saveFilePath, extractDir);

                // 删除 ZIP 文件
                File zipFile = new File(saveFilePath);
                if (zipFile.delete()) {
//                    logger.info("压缩文件已删除: " + saveFilePath);
                } else {
//                    logger.error("无法删除压缩文件: " + saveFilePath);
                }

            } catch (Exception e) {
                logger.error("处理过程中发生错误: " + e.getMessage());
				throw new Exception("处理过程中发生错误: " + e.getMessage(), e);
            }


        }

	}





	public static void get_shengao_jieguo1() throws Exception {



	    // 遍历 dataMap
        for (Map.Entry<String, String> entry : dataMap_PDSK.entrySet()) {
            String folderName = entry.getKey() + "_" + entry.getValue();
//            File folder = new File(rootPath, folderName);
//            logger.info("folderName: " + folderName);
//
//            // 创建主文件夹
//            if (folder.mkdirs()) {
////                logger.info("Created folder: " + folder.getAbsolutePath());
//            } else {
////                logger.info("Failed to create folder: " + folder.getAbsolutePath());
//            }


            /*
             * xtx_nccファイル
             */
            try {
                // 在主文件夹下创建 xtx_nccファイル 子文件夹
//                File subFolder = new File(folder, "xtx_nccファイル");
//                if (subFolder.mkdirs()) {
////                    logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
//                } else {
////                    logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
//                }


            	 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

                // 第一步：通过请求获得压缩文件名
                String urlString = URL + "/ToolsReplaceLogic"
                        + "?license=wangzihao"
                        + "&pw=undefined"
                        + "&hidden_key=getShenqingJieguo"
                        + "&yyyymmddhhmmss="+ LocalDateTime.now().format(formatter)
                        + "&hidden_shenqing_type=" + URLEncoder.encode("申告")//
						+ "&hidden_shuilishi_del_type=" + hidden_shuilishi_del_type	//
                        + "&PDSK=" + entry.getKey();
                        ;
                String fileName = getFileNameFromServer("POST", urlString, entry.getKey());

                if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
                	logger.info("获取到的文件名: " + fileName);
                    logger.error("无法从服务器获取文件名。");
                    return;
                }
                logger.info("获取到的文件名: " + fileName);

                // 第二步：下载文件、解压缩并删除压缩文件
                String downloadUrl = URL + "/";
                String saveDir = rootPath;
                String saveFilePath = saveDir + "/" + folderName + ".zip";
                String extractDir = saveDir;

                // 下载 ZIP 文件
                downloadFile(downloadUrl, fileName, saveFilePath);

                // 解压 ZIP 文件
                extractZipFile(saveFilePath, extractDir);

                // 删除 ZIP 文件
                File zipFile = new File(saveFilePath);
                if (zipFile.delete()) {
//                    logger.info("压缩文件已删除: " + saveFilePath);
                } else {
//                    logger.error("无法删除压缩文件: " + saveFilePath);
                }

            } catch (Exception e) {
                logger.error("处理过程中发生错误: " + e.getMessage());
				throw new Exception("处理过程中发生错误: " + e.getMessage(), e);
            }


        }

	}


	private static void getEtaxNoAll() throws Exception {

		t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();

		String msg = "";
		String res = "";
		int count = 0;
        for (Map.Entry<String, String> entry : dataMap_EtaxNo_yyyymmdd_count.entrySet()) {
        	String yyyymmdd_count = entry.getKey();
			++count;
			t_etax_account_resDao.Update_res_horyuu(yyyymmdd_count);
			pandaWebDriver testNoWEB = new pandaWebDriver(null);
			msg = testNoWEB.getEtaxNo(yyyymmdd_count);
			if("国税局系统维护中".equals(msg)) {
//				out.print(msg);
				logger.info("end" + msg);
				return;

			} else if (!StringUtils.isEmpty(msg)) {
				res = res + msg + "<br>";

			}


		}
		msg ="Etax拿号处理件数：" + count + "件";
		res = res + msg + "<br>";

		msg ="※温情提示：请刷新【客户信息收集表一览】画面确认结果。";
		res = res + msg + "<br>";

        logger.info(res.replace("<br>", "\n"));
        logger.info("end getEtaxNoAll");
//        out.print("{\"info\":\"" + res + "\"}");
        return;

	}




	public static void get_jct_shengao_xtx_ncc_zip(Map.Entry<String, String> entry) throws Exception {

		URL = "https://www.sunmoonjp.com";
		URL = "https://www.sunmoonjp.com";
		URL = "https://www.sunmoonjp.com";

	    // 遍历 dataMap
//        for (Map.Entry<String, String> entry : dataMap_PDSK.entrySet()) {}



        if (dataMap_PDSK_skip.containsKey(entry.getKey())) {
        	return;
        }


        String folderName = entry.getKey() + "_" + entry.getValue();
        File folder = new File(rootPath, folderName);
        logger.info("folderName: " + folderName);



        // 创建主文件夹
        if (folder.mkdirs()) {
//            logger.info("Created folder: " + folder.getAbsolutePath());
        } else {
//            logger.info("Failed to create folder: " + folder.getAbsolutePath());
        }


        /*
         * xtx_nccファイル
         */
        if (xtx == true) {
        	 try {
                 // 在主文件夹下创建 xtx_nccファイル 子文件夹
                 File subFolder = new File(folder, "xtx_nccファイル");
                 if (subFolder.mkdirs()) {
//                     logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
                 } else {
//                     logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
                 }

                 // 第一步：通过请求获得压缩文件名
                 String urlString = URL + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic"
                         + "?license=wangzihao"
                         + "&pw=your_password_value"
                         + "&hidden_key=get_file"
                         + "&hidden_value=xtx"
                         + "&yyyymmdd_count="+ dataMap_PDSK_yyyymmdd_count.get(entry.getKey())
                         + "&PDSK=" + entry.getKey();
                 String fileName = getFileNameFromServer("GET", urlString, null);

                 if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
                 	logger.info("获取到的文件名: " + fileName);
                     logger.error("无法从服务器获取文件名。");
                     return;
                 }
                 logger.info("获取到的文件名: " + fileName);

                 // 第二步：下载文件、解压缩并删除压缩文件
                 String downloadUrl = URL + "/ETAX_output/";
                 String saveDir = subFolder.toString();
                 String saveFilePath = saveDir + "/" + fileName;
                 String extractDir = saveDir;

                 // 下载 ZIP 文件
                 downloadFile(downloadUrl, fileName, saveFilePath);

                 // 解压 ZIP 文件
                 extractZipFile(saveFilePath, extractDir);

                 // 删除 ZIP 文件
                 File zipFile = new File(saveFilePath);
                 if (zipFile.delete()) {
//                     logger.info("压缩文件已删除: " + saveFilePath);
                 } else {
//                     logger.error("无法删除压缩文件: " + saveFilePath);
                 }

             } catch (Exception e) {
                 logger.error("处理过程中发生错误: " + e.getMessage());
 				throw new Exception("处理过程中发生错误: " + e.getMessage(), e);
             }
        }

        /*
         * 会計材料
         */
        if (zip == true) {
        	 try {
                 // 在主文件夹下创建 会計材料 子文件夹
             	File subFolder = new File(folder, "会計材料");
                 if (subFolder.mkdirs()) {
//                     logger.info("Created sub-folder: " + subFolder.getAbsolutePath());
                 } else {
//                     logger.info("Failed to create sub-folder: " + subFolder.getAbsolutePath());
                 }

                 // 第一步：通过请求获得压缩文件名
                 String urlString = URL + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic"
                         + "?license=wangzihao"
                         + "&pw=your_password_value"
                         + "&hidden_key=get_file"
//                         + "&hidden_value=ZIP下载"
                         + "&hidden_value=ZIP%E4%B8%8B%E8%BD%BD"
                         + "&yyyymmdd_count="+ dataMap_PDSK_yyyymmdd_count.get(entry.getKey())
                         + "&PDSK=" + entry.getKey();
                 String fileName = getFileNameFromServer("GET", urlString, null);

                 if (fileName == null || fileName.isEmpty() || fileName.indexOf("Exception") > -1) {
                 	logger.info("获取到的文件名: " + fileName);
                     logger.error("无法从服务器获取文件名。");
                     return;
                 }
                 logger.info("获取到的文件名: " + fileName);

                 // 第二步：下载文件、解压缩并删除压缩文件
                 String downloadUrl = URL + "/kuaiji_import/";
                 String saveDir = subFolder.toString();
                 String saveFilePath = saveDir + "/" + fileName;
                 String extractDir = saveDir;

                 // 下载 ZIP 文件
                 downloadFile(downloadUrl, fileName, saveFilePath);

                 // 解压 ZIP 文件
                 extractZipFile(saveFilePath, extractDir);

                 // 删除 ZIP 文件
                 File zipFile = new File(saveFilePath);
                 if (zipFile.delete()) {
//                     logger.info("压缩文件已删除: " + saveFilePath);
                 } else {
//                     logger.error("无法删除压缩文件: " + saveFilePath);
                 }

             } catch (Exception e) {
                 logger.error("处理过程中发生错误: " + e.getMessage());
                 throw new Exception("处理过程中发生错误: " + e.getMessage(), e);
             }
        }





    }



	public static void get_jct_shengao_xtx_ncc() throws Exception {
        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            logger.info("目录不存在或不是文件夹: " + rootPath);
            return;
        }

        // 获取第一层子文件夹
        File[] subDirs = rootDir.listFiles(File::isDirectory);
        if (subDirs == null || subDirs.length == 0) {
            logger.info("没有找到子文件夹");
            return;
        }

        for (File subDir : subDirs) {
            String folderName = subDir.getName();

            // 获取文件夹名字的第一部分
            String key = folderName.split("_")[0];

            // 检查数据映射中是否存在匹配的键
            if (dataMap_PDSK.containsKey(key)) {
                // 获取第二列的值
                String newName = key + "_" + dataMap_PDSK.get(key);

                // 生成新文件夹路径
                File renamedFolder = new File(subDir.getParent(), newName);

                if (subDir.renameTo(renamedFolder)) {
                    logger.info("重命名成功: " + subDir.getName() + " -> " + renamedFolder.getName());



                    // 创建“会計材料”文件夹
                    File materialsFolder = new File(renamedFolder, "会計材料");
                    if (!materialsFolder.exists() && materialsFolder.mkdir()) {
                        logger.info("创建文件夹: " + materialsFolder.getAbsolutePath());
                    }

                    // 移动子文件到“会計材料”文件夹
                    File[] files = renamedFolder.listFiles(File::isFile);
                    if (files != null) {
                        for (File file : files) {
                            File destFile = new File(materialsFolder, file.getName());
                            try {
                                Files.move(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                logger.info("移动文件: " + file.getName() + " -> " + destFile.getAbsolutePath());
                            } catch (IOException e) {
                                logger.info("移动文件失败: " + file.getName() + " -> " + destFile.getAbsolutePath());
                                e.printStackTrace();
                            }
                        }
                    }



                    // 创建“xtx_nccファイル”文件夹
                    File materialsFolder_xtx_nccファイル = new File(renamedFolder, "xtx_nccファイル");
                    if (!materialsFolder_xtx_nccファイル.exists() && materialsFolder_xtx_nccファイル.mkdir()) {
                        logger.info("创建文件夹: " + materialsFolder_xtx_nccファイル.getAbsolutePath());
                    }





                    try {

                        // 第一步：通过请求获得压缩文件名
                        String urlString = URL + "/SetXiaofeishuiShengaoCHengnuoshuOpenLogic"

                                + "?license=wangzihao"
                                + "&pw=your_password_value"
                                + "&hidden_key=get_file"
                                + "&hidden_value=xtx"
                                + "&yyyymmdd_count="+ dataMap_PDSK_yyyymmdd_count.get(key)
                                + "&PDSK=" + key;
                        String fileName = getFileNameFromServer("GET", urlString, null);

                        if (fileName == null || fileName.isEmpty()) {
                            logger.error("无法从服务器获取文件名。");
                            return;
                        }
                        logger.info("获取到的文件名: " + fileName);

                        // 第二步：下载文件、解压缩并删除压缩文件
                        String downloadUrl = URL + "/ETAX_output/";
                        String saveDir = materialsFolder_xtx_nccファイル.toString();
                        String saveFilePath = saveDir + "/" + fileName;
                        String extractDir = saveDir;

                        // 下载 ZIP 文件
                        downloadFile(downloadUrl, fileName, saveFilePath);

                        // 解压 ZIP 文件
                        extractZipFile(saveFilePath, extractDir);

                        // 删除 ZIP 文件
                        File zipFile = new File(saveFilePath);
                        if (zipFile.delete()) {
                            logger.info("压缩文件已删除: " + saveFilePath);
                        } else {
                            logger.error("无法删除压缩文件: " + saveFilePath);
                        }

                    } catch (Exception e) {
                        logger.error("处理过程中发生错误: " + e.getMessage());
                        throw new Exception("处理过程中发生错误: " + e.getMessage(), e);
                    }


                } else {
                    logger.info("重命名失败: " + subDir.getName());
                }






            }
        }
    }



	 /**
     * 第一步：从指定 URL 获取压缩文件名
     *
     * @param urlString 请求文件名的 URL
     * @return 文件名
     * @throws IOException 如果发生 IO 异常
     */
    public static String getFileNameFromServer(String RequestMethod, String urlString, String content) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod(RequestMethod);
        connection.setDoOutput(true); // **必须设置，否则无法写入请求体**
        connection.setConnectTimeout(5000);
//        connection.setReadTimeout(50000);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "text/plain");

        if (content != null && !content.isEmpty()) {
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = content.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
                os.flush(); // 确保数据写入完成
            }
        } else {
//            logger.info("请求数据为空，可能导致服务器端无法解析");
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = reader.readLine();
                if (response != null) {
                    // 使用 JSON 库解析文件名
                    JSONObject jsonObject = new JSONObject(response);
                    return jsonObject.getString("res"); // 提取 "res" 字段的值
                }
            }
        } else {
            logger.error("无法从服务器获取文件名，响应码: " + responseCode);
        }
        return null;
    }


    /**
     * 第二步：从指定 URL 下载文件并保存到本地
     *
     * @param fileURL  文件下载的 URL
     * @param savePath 保存文件的路径
     * @param fileName
     * @throws IOException 如果发生 IO 异常
     */
    public static void downloadFile(String fileURL, String fileName, String savePath) throws IOException {


    	   // 对 URL 进行编码
        String encodedURL = fileURL + URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");
        if (fileName.indexOf("getShenqingJieguo") > -1) {
        	encodedURL = fileURL + fileName;

        }

        int maxRetries = 5; // 最大重试次数
        int retryCount = 0;

        while (retryCount < maxRetries) {
            HttpURLConnection httpConn = (HttpURLConnection) new URL(encodedURL).openConnection();
            httpConn.setRequestMethod("GET");

            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedInputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
                     FileOutputStream outputStream = new FileOutputStream(savePath)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
//                    logger.info("文件已成功下载: " + savePath);
                }
                httpConn.disconnect();
                break; // 成功后退出循环
            } else {
                logger.error("文件下载失败，响应码: " + responseCode + "，将在 1 秒后重试...");
                retryCount++;
                httpConn.disconnect();

                try {
                    Thread.sleep(1000); // 等待 1 秒
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("线程被中断，退出重试机制");
                    break;
                }
            }

            if (retryCount == maxRetries) {
                logger.error("达到最大重试次数，下载失败！");
            }
        }


    }

    /**
     * 解压 ZIP 文件到指定目录
     *
     * @param zipFilePath ZIP 文件路径
     * @param extractDir  解压目标目录
     * @throws IOException 如果发生 IO 异常
     */
    public static void extractZipFile(String zipFilePath, String extractDir) throws IOException {
        File destDir = new File(extractDir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String filePath = extractDir + File.separator + entry.getName();
                if (!entry.isDirectory()) {
                    extractFile(zipInputStream, filePath);
                } else {
                    File dir = new File(filePath);
                    dir.mkdirs();
                }
                zipInputStream.closeEntry();
            }
//            logger.info("文件解压完成，解压路径: " + extractDir);
        }
    }

    /**
     * 将 ZIP 文件中的单个条目写入文件
     *
     * @param zipInputStream ZIP 输入流
     * @param filePath       解压文件路径
     * @throws IOException 如果发生 IO 异常
     */
    private static void extractFile(ZipInputStream zipInputStream, String filePath) throws IOException {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public static void set_dataMap_all_skip() {


		createFolderIfNotExists(rootPath);

    	   // 1. 删除根目录下的 zip 文件（不递归）
        deleteZipFiles(rootPath);

        // 2. 递归删除小于 600KB 且文件名为 _提出消費税申告書.pdf 的文件
        deleteSpecificPdfFiles(new File(rootPath), "_提出消費税申告書.pdf", 600 * 1024);

        // 3. 递归删除名为 pdf_to_image 的文件夹
        deletePdfToImageFolders(new File(rootPath));

        // 4. 删除不包含 _提出消費税申告書.pdf 的 pdf 文件的文件夹（不递归）
        deleteFoldersWithoutSpecificPdf(rootPath, "_提出消費税申告書.pdf");

        // 5. 遍历所有文件夹，获取特定 PDF 文件并提取信息
        extractPDSKInfo(new File(rootPath), "_提出消費税申告書.pdf");

        int count = 0;
		// 遍历 LinkedHashMap 并提交任务到线程池
		for (Map.Entry<String, String> entry : dataMap_PDSK.entrySet()) {
			if (dataMap_PDSK_skip.containsKey(entry.getKey())) {
				continue;
			}
			++count;
			logger.info("剩余 dataMap_PDSK: " + entry.getKey());
		}

		logger.info("剩余 count: " + count);
    }


    public static void set_dataMap_all_skip_zhongjian_shengao() {


		createFolderIfNotExists(rootPath);

    	   // 1. 删除根目录下的 zip 文件（不递归）
        deleteZipFiles(rootPath);

        // 2. 递归删除小于 600KB 且文件名为 _提出消費税申告書.pdf 的文件
        deleteSpecificPdfFiles(new File(rootPath), ".html", 20 * 1024);

        // 3. 递归删除名为 pdf_to_image 的文件夹
        deletePdfToImageFolders(new File(rootPath));

        // 4. 删除不包含 _提出消費税申告書.pdf 的 pdf 文件的文件夹（不递归）
        deleteFoldersWithoutSpecificPdf(rootPath, ".html");

        // 5. 遍历所有文件夹，获取特定 PDF 文件并提取信息
        extractPDSKInfo(new File(rootPath), ".html");

        int count = 0;
		// 遍历 LinkedHashMap 并提交任务到线程池
		for (Map.Entry<String, String> entry : dataMap_yyyymmdd_count_CompanyName_Chinese.entrySet()) {
			if (dataMap_yyyymmdd_count_skip.containsKey(entry.getKey())) {
				continue;
			}
			++count;
			logger.info("剩余 dataMap_yyyymmdd_count_CompanyName_Chinese: " + entry.getKey());
		}

		logger.info("剩余 count: " + count);
    }




    // 删除根目录下的所有 zip 文件（不递归）
    private static void deleteZipFiles(String path) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".zip")) {
                    file.delete();
                    logger.info("删除 ZIP 文件: " + file.getAbsolutePath());
                }
            }
        }
    }

    // 递归删除小于 600KB 且文件名为 _提出消費税申告書.pdf 的 PDF 文件
    private static void deleteSpecificPdfFiles(File dir, String file_key, long file_length) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    deleteSpecificPdfFiles(file, file_key, file_length); // 递归处理子目录
                } else if (file.getName().endsWith(file_key) && file.length() < file_length) {
                    file.delete();
                    logger.info("删除小于 600KB 的 PDF 文件: " + file.getAbsolutePath());
                }
            }
        }
    }



    // 递归删除名为 pdf_to_image 的文件夹
    private static void deletePdfToImageFolders(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    if (file.getName().equals("pdf_to_image")) {
                        deleteFolder(file);
                        logger.info("删除文件夹: " + file.getAbsolutePath());
                    } else {
                        deletePdfToImageFolders(file); // 递归处理子目录
                    }
                }
            }
        }
    }

    // 递归删除文件夹及其内容
    private static void deleteFolder(File folder) {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                deleteFolder(file);
            } else {
                file.delete();
            }
        }
        folder.delete();
    }

    // 删除不包含 _提出消費税申告書.pdf 的 pdf 文件的文件夹（不递归）
    private static void deleteFoldersWithoutSpecificPdf(String path, String file_key) {
        File dir = new File(path);
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    boolean containsSpecificPdf = false;
                    for (File subFile : file.listFiles()) {
                        if (subFile.isFile() && subFile.getName().endsWith(file_key)) {
                            containsSpecificPdf = true;
                            break;
                        }
                    }
                    if (!containsSpecificPdf) {
                        deleteFolder(file);
                        logger.info("删除不包含指定 PDF 的文件夹: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    // 遍历文件夹并提取 _提出消費税申告書.pdf 的 PDSK 相关信息
    private static void extractPDSKInfo(File dir, String file_key) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    extractPDSKInfo(file, file_key); // 递归处理子目录
                } else if (file.getName().endsWith(file_key)) {
                    String fileName = file.getName();
                    String extractedKey = extractPDSK(file.getPath());
                    if (extractedKey != null) {
                        dataMap_PDSK_skip.put(extractedKey, file.getAbsolutePath());
                        String CompanyName_Chinese = fileName.split("_")[0];
                        dataMap_yyyymmdd_count_skip.put(dataMap_CompanyName_Chinese_yyyymmdd_count.get(CompanyName_Chinese), CompanyName_Chinese);
//                        if (CompanyName_Chinese.indexOf("PDSK") == 0) {
//
//                        } else {
//                        	CompanyName_Chinese = CompanyName_Chinese.substring(0, 14);
//                        	dataMap_yyyymmdd_count_skip.put(CompanyName_Chinese, dataMap_yyyymmdd_count_CompanyName_Chinese.get(CompanyName_Chinese));
//
//                        }

//                        logger.info("添加到 dataMap_PDSK_skip: " + extractedKey);
                    }
                }
            }
        }



    }

    // 提取文件名中的 PDSK 开头的部分及其后 6 位数字
    private static String extractPDSK(String fileName) {
        String regex = "(PDSK\\d{6})";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
/*
 *
		dataMap_PDSK.put("PDSK240214", "Guangzhou Nansha Yuanqu e-commerce Co.,Ltd");	dataMap_PDSK_keshui_type.put("PDSK240214", "简易课税（零售业）");	dataMap_PDSK_yyyymmdd_count.put("PDSK240214", "20240621000242");	dataMap_PDSK_CompanyName_Chinese.put("PDSK240214", "广州南海区苑驱电子商务有限公司");	dataMap_EtaxNo_yyyymmdd_count.put("20240201000605", "深圳市杰源创新科技有限公司");
		dataMap_PDSK.put("PDSK240217", "Quanzhou Shengri Trading Co.,Ltd");	dataMap_PDSK_keshui_type.put("PDSK240217", "简易课税（零售业）");	dataMap_PDSK_yyyymmdd_count.put("PDSK240217", "20240328000117");	dataMap_PDSK_CompanyName_Chinese.put("PDSK240217", "泉州市圣日贸易有限公司");	dataMap_EtaxNo_yyyymmdd_count.put("20240201000605", "深圳市杰源创新科技有限公司");
    dataMap_PDSK.put("PDSK240238", "PUTIAN DIDOU E-COMMERCE CO., LTD");	dataMap_PDSK_keshui_type.put("PDSK240238", "简易课税（零售业）");	dataMap_PDSK_yyyymmdd_count.put("PDSK240238", "20240621000249");

 *
 */
    //TODO
//		dataMap_PDSK.put("PDSK240311", "pujiangxianzhuojianshuijinggongyipinyouxiangongsi");	dataMap_PDSK_keshui_type.put("PDSK240311", "简易课税（零售业）");	dataMap_PDSK_yyyymmdd_count.put("PDSK240311", "QS_yH7bN7");	dataMap_PDSK_CompanyName_Chinese.put("PDSK240311", "浦江县卓舰水晶工艺品有限公司");	dataMap_EtaxNo_yyyymmdd_count.put("20241209000100", "浦江县卓舰水晶工艺品有限公司");


//	set_dataMap_all1();
//	set_dataMap_all2();
//	set_dataMap_all3();


    private static void set_dataMap_all() {


//    	dataMap_PDSK.put("V230328006","");
//    	dataMap_PDSK.put("V230403015","");
//    	dataMap_PDSK.put("V230614004","");
//    	dataMap_PDSK.put("V240612841","");
//    	dataMap_PDSK.put("V240805256","");
//    	dataMap_PDSK.put("V250109435","");
//    	dataMap_PDSK.put("V260304107","");
//    	dataMap_PDSK.put("V230519004","");
//    	dataMap_PDSK.put("V240202920","");
//    	dataMap_PDSK.put("V240729686","");
//    	dataMap_PDSK.put("V230519007","");
//    	dataMap_PDSK.put("V230619001","");
//    	dataMap_PDSK.put("V230710001","");
//    	dataMap_PDSK.put("V240201623","");
//    	dataMap_PDSK.put("V240611236","");
//    	dataMap_PDSK.put("V240905465","");
    	dataMap_PDSK.put("V230519007","");

    }






    private static void set_dataMap_all1111() {
    }

//    dataMap_PDSK.put("V240413072","");
    private static void set_dataMap_all4() {
//    	dataMap_EtaxNo_yyyymmdd_count.put("20241105000840","群利科技股份有限公司");
//		dataMap_yyyymmdd_count_CompanyName_Chinese.put("20241105000840", "群利科技股份有限公司");	dataMap_CompanyName_Chinese_yyyymmdd_count.put("群利科技股份有限公司", "20241105000840");


    }

		private static void set_dataMap_all1() {

		}

//    	dataMap_PDSK_skip.put("V260213324","");

	private static void set_dataMap_skip() {
    	dataMap_PDSK_skip.put("V230225005","");
    	dataMap_PDSK_skip.put("V230308008","");
    	dataMap_PDSK_skip.put("V230313025","");
    	dataMap_PDSK_skip.put("V230320002","");
    	dataMap_PDSK_skip.put("V230320003","");
    	dataMap_PDSK_skip.put("V230425015","");
    	dataMap_PDSK_skip.put("V230526010","");
    	dataMap_PDSK_skip.put("V230612003","");
    	dataMap_PDSK_skip.put("V230704001","");
    	dataMap_PDSK_skip.put("V230714002","");
    	dataMap_PDSK_skip.put("V230714009","");
    	dataMap_PDSK_skip.put("V230719004","");
    	dataMap_PDSK_skip.put("V230807005","");
    	dataMap_PDSK_skip.put("V230818006","");
    	dataMap_PDSK_skip.put("V230829007","");
    	dataMap_PDSK_skip.put("V230830001","");
    	dataMap_PDSK_skip.put("V230913004","");
    	dataMap_PDSK_skip.put("V230925007","");
    	dataMap_PDSK_skip.put("V231009035","");
    	dataMap_PDSK_skip.put("V231111347","");
    	dataMap_PDSK_skip.put("V231116180","");
    	dataMap_PDSK_skip.put("V231204189","");
    	dataMap_PDSK_skip.put("V231206248","");
    	dataMap_PDSK_skip.put("V231207535","");
    	dataMap_PDSK_skip.put("V231220148","");
    	dataMap_PDSK_skip.put("V231221750","");
    	dataMap_PDSK_skip.put("V240202930","");
    	dataMap_PDSK_skip.put("V240205285","");
    	dataMap_PDSK_skip.put("V240305350","");
    	dataMap_PDSK_skip.put("V240309017","");
    	dataMap_PDSK_skip.put("V240312604","");
    	dataMap_PDSK_skip.put("V240321014","");
    	dataMap_PDSK_skip.put("V240413002","");
    	dataMap_PDSK_skip.put("V240413004","");
    	dataMap_PDSK_skip.put("V240413068","");
    	dataMap_PDSK_skip.put("V240413130","");
    	dataMap_PDSK_skip.put("V240413165","");
    	dataMap_PDSK_skip.put("V240416200","");
    	dataMap_PDSK_skip.put("V240511398","");
    	dataMap_PDSK_skip.put("V240515142","");
    	dataMap_PDSK_skip.put("V240612783","");
    	dataMap_PDSK_skip.put("V240612797","");
    	dataMap_PDSK_skip.put("V240612819","");
    	dataMap_PDSK_skip.put("V240612835","");
    	dataMap_PDSK_skip.put("V240612847","");
    	dataMap_PDSK_skip.put("V240729679","");
    	dataMap_PDSK_skip.put("V240729682","");
    	dataMap_PDSK_skip.put("V240729684","");
    	dataMap_PDSK_skip.put("V240729694","");
    	dataMap_PDSK_skip.put("V240731418","");
    	dataMap_PDSK_skip.put("V240903375","");
    	dataMap_PDSK_skip.put("V240911763","");
    	dataMap_PDSK_skip.put("V240917769","");
    	dataMap_PDSK_skip.put("V241028114","");
    	dataMap_PDSK_skip.put("V241105799","");
    	dataMap_PDSK_skip.put("V241105801","");
    	dataMap_PDSK_skip.put("V241105806","");
    	dataMap_PDSK_skip.put("V241105810","");
    	dataMap_PDSK_skip.put("V241105811","");
    	dataMap_PDSK_skip.put("V241105812","");
    	dataMap_PDSK_skip.put("V241105814","");
    	dataMap_PDSK_skip.put("V241105823","");
    	dataMap_PDSK_skip.put("V241105827","");
    	dataMap_PDSK_skip.put("V241105832","");
    	dataMap_PDSK_skip.put("V241105834","");
    	dataMap_PDSK_skip.put("V241105844","");
    	dataMap_PDSK_skip.put("V241113468","");
    	dataMap_PDSK_skip.put("V241116118","");
    	dataMap_PDSK_skip.put("V241120495","");
    	dataMap_PDSK_skip.put("V241123258","");
    	dataMap_PDSK_skip.put("V241127677","");
    	dataMap_PDSK_skip.put("V241204183","");
    	dataMap_PDSK_skip.put("V241206235","");
    	dataMap_PDSK_skip.put("V250106751","");
    	dataMap_PDSK_skip.put("V250109417","");
    	dataMap_PDSK_skip.put("V250109418","");
    	dataMap_PDSK_skip.put("V250109421","");
    	dataMap_PDSK_skip.put("V250109429","");
    	dataMap_PDSK_skip.put("V250109432","");
    	dataMap_PDSK_skip.put("V250109433","");
    	dataMap_PDSK_skip.put("V250113374","");
    	dataMap_PDSK_skip.put("V250116393","");
    	dataMap_PDSK_skip.put("V250208647","");
    	dataMap_PDSK_skip.put("V250210265","");
    	dataMap_PDSK_skip.put("V250212724","");
    	dataMap_PDSK_skip.put("V250213819","");
    	dataMap_PDSK_skip.put("V250213824","");
    	dataMap_PDSK_skip.put("V250213826","");
    	dataMap_PDSK_skip.put("V250213827","");
    	dataMap_PDSK_skip.put("V250219391","");
    	dataMap_PDSK_skip.put("V250328267","");
    	dataMap_PDSK_skip.put("V250331165","");
    	dataMap_PDSK_skip.put("V250408744","");
    	dataMap_PDSK_skip.put("V250411740","");
    	dataMap_PDSK_skip.put("V250429270","");
    	dataMap_PDSK_skip.put("V250507365","");
    	dataMap_PDSK_skip.put("V250507366","");
    	dataMap_PDSK_skip.put("V250509373","");
    	dataMap_PDSK_skip.put("V250514607","");
    	dataMap_PDSK_skip.put("V250519597","");
    	dataMap_PDSK_skip.put("V250520719","");
    	dataMap_PDSK_skip.put("V250520721","");
    	dataMap_PDSK_skip.put("V250520722","");
    	dataMap_PDSK_skip.put("V250520723","");
    	dataMap_PDSK_skip.put("V250526460","");
    	dataMap_PDSK_skip.put("V250529356","");
    	dataMap_PDSK_skip.put("V250603181","");
    	dataMap_PDSK_skip.put("V250609726","");
    	dataMap_PDSK_skip.put("V250627648","");
    	dataMap_PDSK_skip.put("V250714285","");
    	dataMap_PDSK_skip.put("V250723191","");
    	dataMap_PDSK_skip.put("V250929240","");
    	dataMap_PDSK_skip.put("V260210650","");
    	dataMap_PDSK_skip.put("","");
    	dataMap_PDSK_skip.put("","");
    	dataMap_PDSK_skip.put("","");
	}
	//DB skip


	private static void set_dataMap_skip_DB() {



	}


	private static void set_dataMap_EtaxNo() {



	}

    public static void createFolderIfNotExists(String path) {
        File folder = new File(path);
        if (!folder.exists()) {
            boolean created = folder.mkdirs();
            if (created) {
//                logger.info("文件夹创建成功: " + path);
            } else {
                logger.info("文件夹创建失败: " + path);
            }
        } else {
//            logger.info("文件夹已存在: " + path);
        }
    }

}
/*


SELECT
    pdsks.PDSK AS 查询条件,
    teai.*
FROM
    (
        SELECT 'PDSK240099' AS PDSK
        UNION ALL SELECT 'PDSK240046'
        UNION ALL SELECT 'PDSK240093'
        UNION ALL SELECT 'PDSK240106'
        UNION ALL SELECT 'PDSK240109'
        UNION ALL SELECT 'PDSK240113'
        UNION ALL SELECT 'PDSK240072'
        UNION ALL SELECT 'PDSK240103'
        UNION ALL SELECT 'PDSK240051'
        UNION ALL SELECT 'PDSK240041'
        UNION ALL SELECT 'PDSK240080'
        UNION ALL SELECT 'PDSK240108'
        UNION ALL SELECT 'PDSK240077'
        UNION ALL SELECT 'PDSK240048'
        UNION ALL SELECT 'PDSK240067'
        UNION ALL SELECT 'PDSK240029'
        UNION ALL SELECT 'PDSK240070'
        UNION ALL SELECT 'PDSK240098'
        UNION ALL SELECT 'PDSK240117'
        UNION ALL SELECT 'PDSK240095'
        UNION ALL SELECT 'PDSK240114'
        UNION ALL SELECT 'PDSK240042'
        UNION ALL SELECT 'PDSK240100'
        UNION ALL SELECT 'PDSK240073'
        UNION ALL SELECT 'PDSK240065'
        UNION ALL SELECT 'PDSK240115'
        UNION ALL SELECT 'PDSK240104'
        UNION ALL SELECT 'PDSK240094'
        UNION ALL SELECT 'PDSK240084'
        UNION ALL SELECT 'PDSK240064'
        UNION ALL SELECT 'PDSK240112'
        UNION ALL SELECT 'PDSK240068'
        UNION ALL SELECT 'PDSK240105'
        UNION ALL SELECT 'PDSK240071'
        UNION ALL SELECT 'PDSK240050'
        UNION ALL SELECT 'PDSK240074'
        UNION ALL SELECT 'PDSK240069'
        UNION ALL SELECT 'PDSK240062'
        UNION ALL SELECT 'PDSK240043'
        UNION ALL SELECT 'PDSK240096'
        UNION ALL SELECT 'PDSK240075'
        UNION ALL SELECT 'PDSK240110'
        UNION ALL SELECT 'PDSK240086'
        UNION ALL SELECT 'PDSK240097'
        UNION ALL SELECT 'PDSK240047'
        UNION ALL SELECT 'PDSK240107'
        UNION ALL SELECT 'PDSK240101'
        UNION ALL SELECT 'PDSK240087'
        UNION ALL SELECT 'PDSK240066'
        UNION ALL SELECT 'PDSK240049'
        UNION ALL SELECT 'PDSK240076'
    ) pdsks
LEFT JOIN t_xiaofeishui_shengao txs
    ON pdsks.PDSK = txs.PDSK

    LEFT JOIN t_etax_account_info teai
        ON teai.yyyymmdd_count = txs.yyyymmdd_count

***************************



SELECT
    teai.yyyymmdd_count
    , txs.PDSK
    , teai.CompanyName_English
    , (
        SELECT
            GROUP_CONCAT(
                CONCAT(
                    PDSK
                    , '_'
                    , activation_code
                    , '_'
                    , IFNULL(NULLIF(TRIM(shuilishi_id), ''), '税理士ID')
                ) SEPARATOR ','
            )
        FROM
            t_xiaofeishui_shengao txs
        where
            teai.yyyymmdd_count = txs.yyyymmdd_count
    ) AS txs_PDSK
FROM
    t_etax_account_info teai
    INNER JOIN t_xiaofeishui_shengao txs
        ON teai.yyyymmdd_count = txs.yyyymmdd_count
        AND txs.yyyy = '2024'
        AND txs.activation_code LIKE '激活完了pdf%'
where
    txs.PDSK
  in (
 "PDSK240049"
,"PDSK240064"
,"PDSK240065"
,"PDSK240066"
,"PDSK240067"
,"PDSK240068"
,"PDSK240069"
,"PDSK240070"
,"PDSK240071"
,"PDSK240072"
,"PDSK240073"
,"PDSK240074"
,"PDSK240075"
,"PDSK240077"
,"PDSK240080"
,"PDSK240084"
,"PDSK240086"
,"PDSK240087"
,"PDSK240088"
,"PDSK240089"
,"PDSK240090"
,"PDSK240091"
,"PDSK240092"
,"PDSK240093"
,"PDSK240094"
,"PDSK240095"
,"PDSK240096"
,"PDSK240097"
,"PDSK240098"
,"PDSK240099"
,"PDSK240101"
,"PDSK240103"
,"PDSK240105"
,"PDSK240108"
,"PDSK240109"
,"PDSK240110"
,"PDSK240111"
,"PDSK240112"
,"PDSK240113"
,"PDSK240114"
,"PDSK240115"
,"PDSK240116"
,"PDSK240117"
,"PDSK240118"
,"PDSK240119"
,"PDSK240120"
,"PDSK240121"
,"PDSK240122"
,"PDSK240123"
,"PDSK240124"
,"PDSK240127"
,"PDSK240128"
,"PDSK240130"
,"PDSK240131"
,"PDSK240133"
,"PDSK240135"
,"PDSK240136"
,"PDSK240137"
,"PDSK240138"
,"PDSK240139"
,"PDSK240140"
,"PDSK240141"
,"PDSK240142"
,"PDSK240143"
,"PDSK240144"
,"PDSK240149"
,"PDSK240150"
,"PDSK240151"
,"PDSK240156"
,"PDSK240157"
,"PDSK240158"
,"PDSK240160"
,"PDSK240161"
,"PDSK240162"
,"PDSK240163"
,"PDSK240164"
,"PDSK240165"
,"PDSK240167"
,"PDSK240170"
,"PDSK240171"
,"PDSK240173"
,"PDSK240174"
,"PDSK240176"
,"PDSK240177"
,"PDSK240178"
,"PDSK240180"
,"PDSK240182"
,"PDSK240213"
,"PDSK240216"
,"PDSK240224"
,"PDSK240231"
,"PDSK240234"
,"PDSK240237"
,"PDSK240240"
,"PDSK240241"
,"PDSK240242"
,"PDSK240243"
,"PDSK240244"
,"PDSK240246"
,"PDSK240250"
,"PDSK240258"
,"PDSK240263"
,"PDSK240265"
,"PDSK240273"
)



ORDER BY
    txs_PDSK asc


SELECT * FROM t_etax_jieguo where yyyy='2025' and yyyymmdd_count in (SELECT yyyymmdd_count FROM t_etax_account_info where tatujin_id = 'V260213324') LIMIT 10;
SELECT * FROM t_etax_jieguo where  yyyy='2025' and taxable_amount > 100000000 ORDER BY taxable_amount DESC;
SELECT * FROM t_etax_jieguo where yyyy='2025' and yyyymmdd_count in ('20230216000114') LIMIT 10;

SELECT SUM(taxable_amount) AS total_taxable_amount FROM t_etax_jieguo where yyyy='2024'

SELECT count(1) FROM t_etax_jieguo where yyyy='2022'
SELECT count(1) FROM t_etax_jieguo where yyyy='2024'
SELECT count(1) FROM t_etax_jieguo where yyyy='2025'
SELECT file_name,taxable_amount,total_tax_amount FROM t_etax_jieguo where yyyy='2025'
UPDATE t_etax_jieguo   SET uketsuke_datetime = '2026/02/25 12:26:51'     , kazei_kikan = '自　令和07年01月01日,至　令和07年12月31日'


SELECT * FROM t_etax_jieguo
WHERE yyyymmdd_count = '20230216000114'

SELECT b.taxable_amount, b.file_name,a.*
FROM t_etax_account_info a
LEFT JOIN t_etax_jieguo b
    ON a.yyyymmdd_count = b.yyyymmdd_count
    AND b.yyyy = '2025'
    AND b.taxable_amount > -8000000
WHERE b.yyyymmdd_count IS NOT NULL
ORDER BY b.taxable_amount DESC;

de lete FROM t_etax_jieguo where  yyyy='2025'

SELECT * FROM t_etax_account_info where tatujin_id = 'V260213324'  LIMIT 10;
SELECT * FROM h_etax_account_info where yyyymmdd_count = '20230216000114'  LIMIT 10;
SELECT * FROM h_etax_account_info where yyyymmdd_count like '20260208%' ;


SELECT * FROM t_etax_account_res where yyyymmdd_count = '20260213000324' ;
SELECT * FROM h_etax_account_res where yyyymmdd_count = '20260213000324' ;

SELECT * FROM t_etax_account_info where tatujin_id


SELECT * FROM t_etax_jieguo where yyyy='2025' and yyyymmdd_count in (SELECT yyyymmdd_count FROM t_etax_account_info where tatujin_id in(
 'V250804136'))

file_name,etax_xtx


SELECT * FROM t_etax_jieguo
WHERE
yyyy='2025' and yyyymmdd_count in (SELECT yyyymmdd_count FROM t_etax_account_info where tatujin_id in(
 'V240819548'
,''
))



SELECT file_name , UPDATE_DATE FROM t_etax_jieguo

SELECT * FROM t_etax_jieguo
WHERE  (etax_xtx IS NULL OR etax_xtx = ''
or html IS NULL OR html = '')
AND



SELECT
    a.tatujin_id,
    j.taxable_amount,
    j.total_tax_amount,
    j.uketsuke_datetime,
    j.kazei_kikan,
    a.CompanyName_Chinese,
    a.CompanyName_English
FROM t_etax_jieguo j
LEFT JOIN t_etax_account_info a
    ON j.yyyymmdd_count = a.yyyymmdd_count
WHERE j.yyyy = '2025'
AND (
        j.uketsuke_datetime IS NULL
     OR j.kazei_kikan IS NULL
     OR TRIM(j.kazei_kikan) = ''
);

SELECT * FROM t_etax_jieguo
WHERE  (etax_xtx IS NULL OR etax_xtx = ''
or html IS NULL OR html = '')
AND


SELECT file_name,taxable_amount,total_tax_amount FROM t_etax_jieguo
WHERE
 yyyy='2025' and yyyymmdd_count in (SELECT yyyymmdd_count FROM t_etax_account_info where tatujin_id in(
 'V230216114'
,'V230308016'
,'V230317008'
,'V230317012'
,'V230326018'
,'V230404009'
,'V230815006'
,'V231115296'
,'V231206236'
,'V240201630'
,'V240309002'
,'V240315623'
,'V240410001'
,'V240511399'
,'V240528599'
,'V240612770'
,'V240612771'
,'V240612818'
,'V240612826'
,'V240612859'
,'V240729697'
,'V240826375'
,'V240902575'
,'V240919655'
,'V241105798'
,'V241105802'
,'V241105803'
,'V241105825'
,'V241105826'
,'V241105830'
,'V241105833'
,'V241105847'
,'V241209100'
,'V250210264'
,'V250212719'
,'V250308535'
,'V250415258'
,'V250531489'
,'V250822587'
,'V260113201'
,'V260116721'
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
,''
))
ORDER BY file_name ASC;



*/