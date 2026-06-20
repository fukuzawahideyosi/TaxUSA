package com.panda.chrome;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v133.network.Network;
import org.openqa.selenium.devtools.v133.network.model.Headers;
//import org.openqa.selenium.devtools.v123.network.Network;
//import org.openqa.selenium.devtools.v123.network.model.Request;
import org.openqa.selenium.support.ui.Select;

import com.panda.bean.t_etax_account_infoExBean;
import com.panda.bean.t_etax_jieguoBean;
import com.panda.bean.t_etax_jieguoExBean;
import com.panda.bean.t_freeeBean;
import com.panda.dao.t_etax_account_infoDao;
import com.panda.dao.t_etax_account_resDao;
import com.panda.dao.t_user_account_amountDao;
import com.panda.servlet.EtaxLogic;
import com.panda.utils.FuncUtils;


public class pandaWebDriver {

	private static Logger logger = Logger.getLogger(pandaWebDriver.class.toString());

	String ChromeDriverPath = "";
	String p12Path = "";


    // 下载目录（必须是已存在目录）
    String downloadDir = "C:\\Users\\Administrator\\Downloads";

    // 使用 ThreadLocal 确保每个线程有自己的 WebDriver 实例
    private static ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();


	public pandaWebDriver(String WebDriverType) {

        // 设置ChromeOptions以启用无头模式
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--js-flags=--max-old-space-size=1024"); // 设置最大堆内存为1GB
//        options.addArguments("--remote-allow-origins=*"); // 允许远程调试
//   	 options.addArguments("--remote-debugging-port=9222"); // 启用远程调试端口


        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.prompt_for_download", false);     // ⭐ 不弹保存确认
        prefs.put("download.directory_upgrade", true);
        prefs.put("safebrowsing.enabled", true);

		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows")) {
			p12Path = "I:\\我的云端硬盘\\日本-PANDASERVICE株式会社\\商業登記電子証明書\\666666.p12";

			//https://googlechromelabs.github.io/chrome-for-testing/

//			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver.exe";
//			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver - 127.exe";
			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver - 138.exe";

//	        options.addArguments("--headless"); // 如果你想以无头模式运行

	        // 添加扩展程序
			//C:\Users\Administrator\AppData\Local\Google\Chrome\User Data\Default\Extensions\hopiajgbpnepghlkfmdonpgdnmcajpeb
	        //options.addExtensions(new File("E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\3.0.3.0_0.crx"));


		       // ⭐ 指定你给的 Chrome 路径
	        options.setBinary(
	            "E:/Users/Administrator/git/PandaServiceMA/PandaServiceMA/WebContent/WEB-INF/lib/chrome-win64-138.0.7204.183/chrome.exe"
	        );

//			从 Chrome 140+ 开始：
//			1️⃣ CRX 被严格限制
//			Selenium / ChromeDriver 不再信任外部 CRX
//	        options.addExtensions(new File("E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\3.0.3.2_0.crx"));
	        options.addArguments(
	        	    "--load-extension=E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\3.0.3.2_0"
	        	);

		} else if (osName.toLowerCase().contains("linux")) {
			downloadDir = "C:\\Users\\Administrator\\Downloads";


			//20250612　ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社　Panda0518
			p12Path = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/666666.p12";
			ChromeDriverPath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/chromedriver";

	        options.addArguments("--headless"); // 如果你想以无头模式运行


	        /*
作用：
显式指定 Chrome 浏览器的二进制可执行文件路径。
场景：
当系统中有多个 Chrome 安装时，用于确保 WebDriver 使用指定版本的 Chrome。
通常在 Linux 环境中需要设置路径，例如 /usr/bin/google-chrome 是常见的 Linux 安装路径。
注意：
如果未设置该选项，WebDriver 会尝试自动查找 Chrome 浏览器，但路径可能不准确，尤其在非标准安装的情况下。
	         */
			options.setBinary("/usr/bin/google-chrome");
			/*
作用：
禁用沙盒模式（Sandbox Mode）。
场景：
在某些受限的 Linux 环境中（如 Docker 容器或 CI/CD 环境），默认的沙盒模式可能无法正常工作，因此需要禁用它。
注意：
沙盒模式是 Chrome 的一个安全机制，禁用它可能会降低安全性。仅在受信任的环境中使用。
			 */
			options.addArguments("--no-sandbox");
			/*
作用：
禁用 /dev/shm（共享内存）目录的使用，改用磁盘进行临时存储。
场景：
在某些容器化环境（如 Docker）中，默认的 /dev/shm 空间可能不足，导致浏览器崩溃。此选项通过禁用共享内存缓解问题。
注意：
使用此选项可能会降低性能，因为磁盘 I/O 的速度通常慢于共享内存。
			 */
//			options.addArguments("--disable-dev-shm-usage");

	        // 添加扩展程序
//	        options.addExtensions(new File("/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/3.0.3.0_0.crx"));



	        options.addArguments(
	        	    "--load-extension=/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/3.0.3.2_0"
	        	);

	        	// 强烈建议加上这些
	        	options.addArguments(
	        	    "--disable-extensions-except=/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/3.0.3.2_0",
	        	    "--no-first-run",
	        	    "--no-default-browser-check"
	        	);


		}

		// ⭐ 默认下载路径
		prefs.put("download.default_directory", downloadDir);
		options.setExperimentalOption("prefs", prefs);

		//TODO TODO
        // 创建 ChromeOptions 并优化性能
//        options.addArguments("--headless"); // 无头模式，加快速度



//        options.addArguments("--disable-images"); // 禁用图片
//        options.addArguments("--blink-settings=imagesEnabled=false"); // 另一种禁用图片的方式
//        options.addArguments("--disable-javascript"); // 禁用 JavaScript
//        options.addArguments("--disable-css"); // 禁用 CSS
//        options.addArguments("--disable-extensions"); // 禁用扩展
//        options.addArguments("--disable-popup-blocking"); // 禁用弹窗
//        options.addArguments("--disable-gpu"); // 禁用 GPU 渲染
//        options.addArguments("--no-sandbox"); // 适用于 Linux 服务器
//        options.addArguments("--disable-dev-shm-usage"); // 适用于 Linux 服务器


//        options.addArguments("--disable-software-rasterizer");//这个选项用于禁用软件光栅化（Software Rasterization），主要影响 Chromium/Chrome 浏览器的图形渲染方式。


//        options.setPageLoadStrategy(PageLoadStrategy.NONE);  // 改为 NONE 可以跳过等待页面完全加载



//        options.addArguments("--disable-gpu"); // 关闭 GPU 加速，避免某些 bug
        options.addArguments("--disable-blink-features=AutomationControlled"); // 取消 Selenium 控制标记
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"); // 伪装 User-Agent
//        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent



//        options.addArguments("user-agent=" + getUserAgent(options));



        // 设置ChromeDriver路径
//        System.setProperty("webdriver.chrome.driver", ChromeDriverPath);

        //TODO
        WebDriverType = "";
        if ("DevTools".equals(WebDriverType)) {
        	driverThreadLocal.set(getNewDriver(options));
            logger.info("WebDriverType " + WebDriverType);

        } else {
        	// 线程安全地初始化 WebDriver
        	driverThreadLocal.set(new ChromeDriver(options));

        }
//        getDriver().manage().timeouts().scriptTimeout(Duration.ofSeconds(600));


	}



	/**
     * 获取 WebDriver 实例
     */
//    public static WebDriver getDriver0() {
//
//    	WebDriver driver = driverThreadLocal.get();
//        // 运行 JavaScript 代码隐藏 `navigator.webdriver`
//        JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
//        JavascriptExecutor.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
//
//        // 获取 User-Agent 进行检查
//        String userAgent = (String) JavascriptExecutor.executeScript("return navigator.userAgent;");
//        logger.info("test " + (0) + "伪装后的 User-Agent: " + userAgent);
//
//        return driver;
//    }




    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static String getUserAgent(ChromeOptions options) {

        WebDriver driver = new ChromeDriver(options);

        // 获取原始 User-Agent
        String originalUserAgent = (String) ((JavascriptExecutor) driver).executeScript("return navigator.userAgent;");
        logger.info("Original User-Agent: " + originalUserAgent);
        driver.quit();


        // 修改 User-Agent，将 HeadlessChrome 替换为 Chrome
        String modifiedUserAgent = originalUserAgent.replace("HeadlessChrome", "Chrome");
        logger.info("Modified User-Agent: " + modifiedUserAgent);

        // 设置新的 User-Agent
        options.addArguments("user-agent=" + modifiedUserAgent);

//        options.addArguments("--headless"); // 无头模式，加快速度

        // 创建新的 WebDriver 实例
        driver = new ChromeDriver(options);

        // 运行 JavaScript 代码隐藏 `navigator.webdriver`
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        // 获取修改后的 User-Agent 进行检查
        String userAgent = (String) jsExecutor.executeScript("return navigator.userAgent;");
        logger.info("伪装后的 User-Agent: " + userAgent);

        driver.quit();

        return modifiedUserAgent;

    }

    /**
     * 关闭 WebDriver 并移除 ThreadLocal 变量
     */
    public static void quitDriver() {
        if (driverThreadLocal.get() != null) {
            driverThreadLocal.get().quit();
            driverThreadLocal.remove();
        }
    }



    public static ChromeDriver getNewDriver(ChromeOptions options) {
        options.addArguments("--disable-blink-features=AutomationControlled"); // 防止网站检测 Selenium
//      options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"); // 伪装 User-Agent
//      options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent
//      options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent



//    options.addArguments("user-agent=" + getUserAgent(options));



//        LoggingPreferences logPrefs = new LoggingPreferences();
//        logPrefs.enable(LogType.BROWSER, Level.OFF);
//        logPrefs.enable(LogType.DRIVER, Level.OFF);
//        options.setCapability("goog:loggingPrefs", logPrefs);







        // 启动 WebDriver
        ChromeDriver driver = new ChromeDriver(options);
//        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();
        devTools.createSession();

        // 自定义请求头
        Map<String, Object> headers = new HashMap<>();
//        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headers.put("Accept-Encoding", "gzip, deflate, br, zstd");
        headers.put("Accept-Language", "ja,zh-CN;q=0.9,zh;q=0.8,zh-TW;q=0.7");
//        headers.put("User-Agent", "Mozilla/5.0 (Windows NT10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36");
        headers.put("Sec-Ch-Ua", "\"Not(A:Brand\";v=\"99\", \"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\"");
        headers.put("Sec-Ch-Ua-Mobile", "?0");
        headers.put("Sec-Ch-Ua-Platform", "\"Windows\"");
        headers.put("Sec-Fetch-Dest", "document");
        headers.put("Sec-Fetch-Mode", "navigate");
        headers.put("Sec-Fetch-Site", "none");
        headers.put("Sec-Fetch-User", "?1");
//        headers.put("Upgrade-Insecure-Requests", "1"); //有这个设置，登录失败
//        headers.put("Cookie", "_gcl_au=1.1.198620801.1739507951; _ga=GA1.1.781949905.1739507951; ...."); // 添加你的完整 Cookie

        // 启用自定义请求头
        devTools.send(Network.enable(java.util.Optional.empty(), java.util.Optional.empty(), java.util.Optional.empty()));
        devTools.send(Network.setExtraHTTPHeaders(new Headers(headers)));





        // 监听网络请求，捕获实际请求头
//        devTools.addListener(Network.requestWillBeSent(), request -> {
//        	  String url = request.getRequest().getUrl();
//        	    // 只监听没有扩展名的 URL
//        	    if (!url.matches(".*\\.[a-zA-Z0-9]+$")) {
//        	        System.out.println("🔹 实际请求 URL: " + url);
//        	        System.out.println("🔹 实际请求头: ");
//        	        request.getRequest().getHeaders().forEach((key, value) -> {
//        	            System.out.println(key + ": " + value);
//        	        });
//        	        System.out.println("-----------------------------------");
//        	    }
//        });
//
//
//
//
//
//
//
//
//
//		int i =0;
//
//		logger.info("start get_freee");
//
//        // 创建HashMap存储每一行的数据
//        List<t_freeeBean> rowDataList = new ArrayList<>();
//
//			try {
//
//		        // 设置最大等待时间为 30 分钟 (1800 秒)
//		        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(30));
//
//	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
//
//
//				/*
//				 * 法人ログイン
//				 */
//				driver.get("https://secure.freee.co.jp/");
//				if (driver.getPageSource().contains("メンテナンス")) {
//					logger.info("freee系统维护中");
//					logger.info("end get_freee");
//				}
//
//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				// 查找用户名和密码输入框及登录按钮
//				WebElement WebElement = driver.findElement(By.id("loginIdField"));
//				WebElement.sendKeys("info@pandaservicejapan.com");
//				WebElement = driver.findElement(By.id("passwordField"));
//				WebElement.sendKeys("Panda0518");
//
//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//	            WebElement = driver.findElement(By.cssSelector("button[data-testid='submit']"));
//				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
////				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
////				WebElement = driver.findElement(By.id("header-nav-walletables"));
////				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
////
////				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
////				WebElement = driver.findElement(By.xpath("//a[contains(text(),'口座の一覧・登録')]"));
////				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//
//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				driver.get("https://secure.freee.co.jp/walletables");
//
//				/*
//				 * 同期みずほ（法人）（API）
//				 */
//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				WebElement WebElement_tr = driver.findElement(By.xpath("//td[contains(string(), 'みずほ（法人）（API） 築地支店 普通 ***6381')]"))
//						.findElement(By.xpath("./parent::tr"))
//				//						.getAttribute("outerHTML")
//				;
//
//
//
//				int count = 0;
//				boolean while_flag = true;
//
//
//
//
//			} catch (Exception e) {
//				e.printStackTrace();
//
//			} finally {
//	            quitDriver();
//				logger.info("end get_freee");
//			}
//
//
//			driver.quit();



        return driver;
    }

	public void beforeWindowOpen(WebDriver driver, String url, String windowName) {
        // 在打开新窗口前要做的事情
        logger.info("Before window open: " + url);
    }

    public void afterWindowOpen(WebDriver driver, String url) {
        // 在打开新窗口后要做的事情
        logger.info("After window open: " + url);
    }

    // 其他方法可以不做任何操作
    public void onException(Throwable throwable, WebDriver driver) {
        // Handle exceptions
    }

    // ... 其他方法


    public static void main(String[] args) {

//    	 System.out.println("Selenium Version: " + pandaWebDriver.class.getPackage().getImplementationVersion());


    	String ChromeDriverPath = "";
    	String p12Path = "";



        // 设置ChromeOptions以启用无头模式
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--js-flags=--max-old-space-size=1024"); // 设置最大堆内存为1GB
//        options.addArguments("--remote-allow-origins=*"); // 允许远程调试
//   	 options.addArguments("--remote-debugging-port=9222"); // 启用远程调试端口



		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows")) {
			p12Path = "H:\\我的云端硬盘\\日本-PANDASERVICE株式会社\\商業登記電子証明書\\666666.p12";

			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver.exe";
//			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver - 127.exe";

//	        options.addArguments("--headless"); // 如果你想以无头模式运行

	        // 添加扩展程序
	        options.addExtensions(new File("E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\3.0.3.0_0.crx"));


		} else if (osName.toLowerCase().contains("linux")) {
			//20250612　ＰＡＮＤＡ　ＳＥＲＶＩＣＥ株式会社　Panda0518
			p12Path = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/666666.p12";
			ChromeDriverPath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/chromedriver";

	        options.addArguments("--headless"); // 如果你想以无头模式运行


	        /*
作用：
显式指定 Chrome 浏览器的二进制可执行文件路径。
场景：
当系统中有多个 Chrome 安装时，用于确保 WebDriver 使用指定版本的 Chrome。
通常在 Linux 环境中需要设置路径，例如 /usr/bin/google-chrome 是常见的 Linux 安装路径。
注意：
如果未设置该选项，WebDriver 会尝试自动查找 Chrome 浏览器，但路径可能不准确，尤其在非标准安装的情况下。
	         */
			options.setBinary("/usr/bin/google-chrome");
			/*
作用：
禁用沙盒模式（Sandbox Mode）。
场景：
在某些受限的 Linux 环境中（如 Docker 容器或 CI/CD 环境），默认的沙盒模式可能无法正常工作，因此需要禁用它。
注意：
沙盒模式是 Chrome 的一个安全机制，禁用它可能会降低安全性。仅在受信任的环境中使用。
			 */
			options.addArguments("--no-sandbox");
			/*
作用：
禁用 /dev/shm（共享内存）目录的使用，改用磁盘进行临时存储。
场景：
在某些容器化环境（如 Docker）中，默认的 /dev/shm 空间可能不足，导致浏览器崩溃。此选项通过禁用共享内存缓解问题。
注意：
使用此选项可能会降低性能，因为磁盘 I/O 的速度通常慢于共享内存。
			 */
//			options.addArguments("--disable-dev-shm-usage");

	        // 添加扩展程序
	        options.addExtensions(new File("/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/3.0.3.0_0.crx"));





		}




        // 创建 ChromeOptions 并优化性能
        options.addArguments("--headless"); // 无头模式，加快速度
//        options.addArguments("--disable-images"); // 禁用图片
//        options.addArguments("--blink-settings=imagesEnabled=false"); // 另一种禁用图片的方式
//        options.addArguments("--disable-javascript"); // 禁用 JavaScript
//        options.addArguments("--disable-css"); // 禁用 CSS
//        options.addArguments("--disable-extensions"); // 禁用扩展
//        options.addArguments("--disable-popup-blocking"); // 禁用弹窗
//        options.addArguments("--disable-gpu"); // 禁用 GPU 渲染
//        options.addArguments("--no-sandbox"); // 适用于 Linux 服务器
//        options.addArguments("--disable-dev-shm-usage"); // 适用于 Linux 服务器


//        options.addArguments("--disable-software-rasterizer");//这个选项用于禁用软件光栅化（Software Rasterization），主要影响 Chromium/Chrome 浏览器的图形渲染方式。


//        options.setPageLoadStrategy(PageLoadStrategy.NONE);  // 改为 NONE 可以跳过等待页面完全加载



//        options.addArguments("--disable-gpu"); // 关闭 GPU 加速，避免某些 bug
        options.addArguments("--disable-blink-features=AutomationControlled"); // 取消 Selenium 控制标记
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36"); // 伪装 User-Agent
//        options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.6422.141 Safari/537.36"); // 伪装 User-Agent



//        options.addArguments("user-agent=" + getUserAgent(options));



        // 设置ChromeDriver路径
        System.setProperty("webdriver.chrome.driver", ChromeDriverPath);


        getNewDriver(options);




    }

    public static void main1(String[] args) {


        try {

        	pandaWebDriver testNoWEB = new pandaWebDriver(null);
//        	testNoWEB.getEtaxNo("20240612000864");
        	testNoWEB.getOpenAI("20240612000864");



//         testNoWEB.getShenqingJieguo(driver0);

//        	t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
//        	t_etax_jieguoExBean.setBangou("2043062710920062");
//        	t_etax_jieguoExBean.setEtax_pw("ps10337585");


//        	t_etax_jieguoExBean.setBangou("2436022620910056");
//        	t_etax_jieguoExBean.setEtax_pw("ps42660284");
//
//
//        	testNoWEB.getShenqingJieguo(t_etax_jieguoExBean);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭WebDriver
//            driver0.quit();
        }
    }


	public String getOpenAI(String yyyymmdd_count) throws Exception {
		logger.info("yyyymmdd_count : " + yyyymmdd_count);
		try {


	        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

//			EventFiringWebDriver driver = new EventFiringWebDriver(driver0);
//			WindowOpenListener listener = new WindowOpenListener();
//			driver.register(listener);

			JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
			/*
			 *
			 */


//			driver.get("https://chatgpt.com/c/d7f1345d-788f-40e8-8b6b-1e00bc409e43");
			driver.get("https://chatgpt.com");
			WebElement WebElement = driver.findElement(By.cssSelector("button[class=\"btn relative btn-secondary\"]"));
//			JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
			int count = 0;
			boolean while_flag = true;

			/*
			 */
			String Title = "";
			logger.info("处理 : " + Title);
			count = 0;
			while_flag = true;
			do {
				Thread.sleep(1000);
			    driver = getNewWindow(driver, Title);

			    // 查找所有符合条件的 <p> 元素
			    List<WebElement> matchingElements = driver.findElements(By.cssSelector("button"));

			    matchingElements = matchingElements.stream()
			        .filter(element -> element.getText().contains("登录"))
			        .collect(Collectors.toList());

			    // 如果匹配的元素只有一个，则点击它
			    if (matchingElements.size() >= 1) {
			        try {
			            // 滚动到元素可见
			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						matchingElements.get(0).click();
			            while_flag = false;
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
			    }


				++count;
				if (count > 2) {
					while_flag = false;
					return null;
				}

			} while (while_flag);




			WebElement = driver.findElement(By.cssSelector("input[class=\"email-input \"]"));
			JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


			/*
			 * 利用者識別番号等の通知
			 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin?SS000170
			 */
			while_flag = true;
			do {
				Thread.sleep(1000);
				driver = getNewWindow(driver, "");
			    if (driver.getCurrentUrl().indexOf("SS000170") > -1) {
		            while_flag = false;
			    }
			} while (while_flag);


//			doc = Jsoup.parse(driver.getPageSource());
////			logger.debug(doc.html());
//
//			String bangou = "";
//			Elements elements = doc.select("td.right");
//			for (Element Element : elements) {
//				bangou = bangou + Element.text();
//			}
//			addCss(doc);



		} catch (Exception e) {
			throw e;
		} finally {
            quitDriver();
			logger.info("end getOpenAI");
		}
		return "";

	}


	public String getEtaxNo(String yyyymmdd_count) throws Exception {
		logger.info("yyyymmdd_count : " + yyyymmdd_count);
		try {

	        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

//			EventFiringWebDriver driver = new EventFiringWebDriver(driver0);
//			WindowOpenListener listener = new WindowOpenListener();
//			driver.register(listener);

			JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
			/*
			 *
			 */

			t_etax_account_resDao t_etax_account_resDao = new t_etax_account_resDao();
//			String Max_yyyymmdd_count = t_etax_account_resDao.selectMax_yyyymmdd_count_active();

			t_etax_account_infoDao t_etax_account_infoDao = new t_etax_account_infoDao();
			t_etax_account_infoExBean t_etax_account_infoExBean = t_etax_account_infoDao.select(yyyymmdd_count);

			EtaxLogic EtaxLogic = new EtaxLogic();
			LinkedHashMap<String, HashMap<String, String>> HashMapKeyValueHtmlAll = EtaxLogic.getHashMapKeyValueHtmlAll(yyyymmdd_count);
			if (HashMapKeyValueHtmlAll.size() == 0) {
				return "";
			}

			/*
			 * ログイン
			 */
			//開始届出（個人の方用）　新規
			if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
				driver.get("https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin");
				if (driver.getPageSource().contains("メンテナンス中です")) {
					logger.info("国税局系统维护中");
					return "国税局系统维护中";
				}

				WebElement button = driver.findElement(By.id("SU_next"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);


				/*
				 * 氏名等の入力
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin?SS000120
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);
				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);

				/*
				 * 納税地等の入力
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin?SS000130
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);
				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();

				/*
				 * 暗証番号等の入力
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin?SS000140
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);
				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();

				/*
				 * 入力内容の確認
				 *https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000160
				 */
				Thread.sleep(1000);
				Document doc = Jsoup.parse(driver.getPageSource());
				doc.select("button").remove();
				addCss(doc);
				if (t_etax_account_resDao.UPDATE_res("res_send_mae", yyyymmdd_count, doc.html(), null) == false) {

					return "";
				}

				button = driver.findElement(By.id("SU_send"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();


				/*
				 * 利用者識別番号等の通知
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiKojin?SS000170
				 */
				boolean while_flag = true;
				do {
					Thread.sleep(1000);
					driver = getNewWindow(driver, "");
				    if (driver.getCurrentUrl().indexOf("SS000170") > -1) {
			            while_flag = false;
				    }
				} while (while_flag);


				doc = Jsoup.parse(driver.getPageSource());
//				logger.debug(doc.html());

				String bangou = "";
				Elements elements = doc.select("td.right");
				for (Element Element : elements) {
					bangou = bangou + Element.text();
				}
				addCss(doc);
				if (t_etax_account_resDao.UPDATE_res("res_send_go", yyyymmdd_count, doc.html(), bangou) == false) {

					return "";
				}

			} else {
				//法人
				driver.get("https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin");
				if (driver.getPageSource().contains("メンテナンス中です")) {
					logger.info("国税局系统维护中");
					return "国税局系统维护中";
				}

				WebElement button = driver.findElement(By.id("SU_next"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);


				/*
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000210
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);

				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();

				/*
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000220
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);
				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);

				/*
				 *https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000240
				 */
				Thread.sleep(1000);
				setElementValue(driver, HashMapKeyValueHtmlAll, t_etax_account_infoExBean);
				button = driver.findElement(By.id("form-confirm-btn"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();

				Document doc = Jsoup.parse(driver.getPageSource());
				doc.select("button").remove();
				addCss(doc);
				if (t_etax_account_resDao.UPDATE_res("res_send_mae", yyyymmdd_count, doc.html(), null) == false) {

					return "";
				}

				/*
				 *https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000260
				 */
				Thread.sleep(1000);
				button = driver.findElement(By.id("SU_send"));
				JavascriptExecutor.executeScript("arguments[0].click();", button);
				// 接受（点击确认）消息框
				driver.switchTo().alert().accept();


				/*
				 * https://kaishi.e-tax.nta.go.jp/SU_APP/lnk/kaishiShinkiHojin?SS000270
				 */
				boolean while_flag = true;
				do {
					Thread.sleep(1000);
					driver = getNewWindow(driver, "");
				    if (driver.getCurrentUrl().indexOf("SS000270") > -1) {
			            while_flag = false;
				    }
				} while (while_flag);


				doc = Jsoup.parse(driver.getPageSource());
//				logger.debug(doc.html());

				String bangou = "";
				Elements elements = doc.select("td.right");
				for (Element Element : elements) {
					bangou = bangou + Element.text();
				}
				addCss(doc);
				if (t_etax_account_resDao.UPDATE_res("res_send_go", yyyymmdd_count, doc.html(), bangou) == false) {

					return "";
				}

			}

		} catch (Exception e) {
			throw e;
		} finally {
            quitDriver();
			logger.info("end getEtaxNo");
		}
		return "";

	}

	private void setElementValue(WebDriver driver, LinkedHashMap<String, HashMap<String, String>> HashMapKeyValueHtmlAll, t_etax_account_infoExBean t_etax_account_infoExBean) {
		// 遍历LinkedHashMap
        for (Entry<String, HashMap<String, String>> outerEntry : HashMapKeyValueHtmlAll.entrySet()) {
            HashMap<String, String> innerMap = outerEntry.getValue();

            // 获取元素的 ID
            String elementId = innerMap.get("html_id");
			if ("个人".equals(t_etax_account_infoExBean.getUser_type())) {
				elementId = elementId.replace("gD", "g").replace("gm", "gM");
			}

            // 使用 findElements 而不是 findElement
            List<WebElement> elements = driver.findElements(By.id(elementId));

            // 检查元素是否存在
            if (!elements.isEmpty()) {
                // 元素存在
                WebElement element = elements.get(0);
                String TagName = element.getTagName().toLowerCase();
                if ("input".equals(TagName) || "textarea".equals(TagName)) {
                    // 进行您需要的操作
                    element.sendKeys(innerMap.get("html_value"));
                } else if ("select".equals(TagName)) {
                    Select select = new Select(element);
                    // 方法一：按可见文本选择
//                    select.selectByVisibleText("选项的可见文本");

                    // 方法二：按值选择
                     select.selectByValue(innerMap.get("html_value"));

                    // 方法三：按索引选择
                    // select.selectByIndex(1);

                    // 检查是否成功选择
                    WebElement selectedOption = select.getFirstSelectedOption();

                }


        		JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
        		WebElement button = driver.findElement(By.id(elementId));
                // 失焦（blur）元素
                JavascriptExecutor.executeScript("arguments[0].blur();", element);



            }
        }
	}

	public List<t_freeeBean> get_freee() throws Exception {
		int i =0;

		logger.info("start get_freee");

        // 创建HashMap存储每一行的数据
        List<t_freeeBean> rowDataList = new ArrayList<>();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

		        // 设置最大等待时间为 30 分钟 (1800 秒)
		        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(30));

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;


				/*
				 * 法人ログイン
				 */
				driver.get("https://secure.freee.co.jp/");
				if (driver.getPageSource().contains("メンテナンス")) {
					logger.info("freee系统维护中");
					logger.info("end get_freee");
					return rowDataList;
				}

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				// 查找用户名和密码输入框及登录按钮
				WebElement WebElement = driver.findElement(By.id("loginIdField"));
				WebElement.sendKeys("info@pandaservicejapan.com");
				WebElement = driver.findElement(By.id("passwordField"));
				WebElement.sendKeys("Panda0518");

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
	            WebElement = driver.findElement(By.cssSelector("button[data-testid='submit']"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				WebElement = driver.findElement(By.id("header-nav-walletables"));
//				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				WebElement = driver.findElement(By.xpath("//a[contains(text(),'口座の一覧・登録')]"));
//				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				driver.get("https://secure.freee.co.jp/walletables");

				/*
				 * 同期みずほ（法人）（API）
				 */
				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement WebElement_tr = driver.findElement(By.xpath("//td[contains(string(), 'みずほ（法人）（API） 築地支店 普通 ***6381')]"))
						.findElement(By.xpath("./parent::tr"))
				//						.getAttribute("outerHTML")
				;



				int count = 0;
				boolean while_flag = true;
				do {
					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());

/*
关键点

    加 .：
    ".//span | .//td" 表示只在 WebElement_tr 内查找，而不是整个页面。
    不加 . 的问题：
    //span | //td 会查找整个 DOM，而不是 WebElement_tr 的子元素。
 */
					  // 获取所有 <span> 和 <td>，因为日期可能出现在这些标签里
			        List<WebElement> elements = WebElement_tr.findElements(By.xpath(".//span | .//td"));

			        // 正则匹配 YYYY-MM-DD HH:mm
			        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
			        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//			        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
	                LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Tokyo")); // 获取当前时间
			        LocalDateTime thresholdTime = now.minusMinutes(30); // 当前时间减去30分钟

			        for (WebElement element : elements) {
			            String text = element.getText().trim();
			            if (text.contains("同期中")) {
//			            	logger.info("sleep: " + "1分钟");
//							Thread.sleep(1000*60);
							++count;
							break;
			            } else if (text.contains("")) {

			            }


			            Matcher matcher = pattern.matcher(text);

			            if (matcher.find()) {
			                String dateStr = matcher.group(); // 提取匹配的时间字符串

			                LocalDateTime extractedDate = LocalDateTime.parse(dateStr, formatter);

			                logger.info("找到的时间: " + dateStr);
			                logger.info("元素HTML: " + element.getAttribute("outerHTML"));

			                // 比较时间 (比当前时间早 30 分钟就输出 NG)
			                if (extractedDate.isBefore(thresholdTime)) {
			                    logger.info("ステータスNG: 时间早于当前 30 分钟");
			                    List<WebElement> WebElements = WebElement_tr.findElements(By.xpath(".//button[span[text()='同期']]"));
					            if (WebElements.size() > 0) {  // 确保这一行有数据
					            	WebElement = WebElements.get(0);
					            	JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
								} else {
									WebElements = WebElement_tr.findElements(By.xpath(".//button[span[text()='確認']]"));
									if (WebElements.size() > 0) { // 确保这一行有数据

										//TODO
										// 计算临界时间（昨天 00:00）
								        thresholdTime = now.toLocalDate().atStartOfDay();

								        // 判断 extractedDate 是否在昨日 00:00 之前
								        if (extractedDate.isBefore(thresholdTime)) {
//								            logger.info("extractedDate 是昨日以前的时间");

											WebElement = WebElements.get(0);
											JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

											Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
								            WebElement = driver.findElement(By.xpath("//button[text()='同期を再開する']"));
											JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


											Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
											driver.get("https://secure.freee.co.jp/bank_account/walletables/3357830");



								        } else {
//								            logger.info("extractedDate 是今天的时间或更晚");
								        }



									}
								}



			                } else {
			                    logger.info("ステータスOK: 时间未过期 30 分钟以内");
						        while_flag = false;
			                }
			                break; // 只取第一个匹配的元素
			            }
			        }

					if (count > -10) {
				        while_flag = false;
					}
				} while (while_flag);



				/*
				 * 明細の一覧
				 */
				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.xpath("//a[contains(text(),'明細の一覧')]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//	            WebElement = driver.findElement(By.cssSelector("input[aria-label=\"取引開始日\"]"));
//				JavascriptExecutor.executeScript("arguments[0].value = '2025-02-01';", WebElement);


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
		        // 找到<select>元素
		        WebElement limitSelect = driver.findElement(By.id("limit-selector"));
		        // 使用Select类来操作<select>元素
		        Select select = new Select(limitSelect);
		        // 选择500件
		        select.selectByValue("100");  // 使用option的value属性值

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				   // 找到表格的tbody部分
		        WebElement tableBody = driver.findElement(By.id("wallet-txn-list-page-body"));

		        // 获取所有的行数据
		        List<WebElement> rows = tableBody.findElements(By.xpath(".//tr"));

		        for (WebElement row : rows) {
		        	LinkedHashMap<String, String> rowData = new LinkedHashMap<>();

		            // 获取每一行的单元格（td元素）
		            List<WebElement> cells = row.findElements(By.xpath(".//td"));


		            if (!cells.isEmpty()) { // 确保这一行有数据
		                // 用 StringBuilder 组装整行数据
		                StringBuilder rowText = new StringBuilder();

		                for (WebElement cell : cells) {
		                    rowText.append(cell.getText().trim()).append(" | "); // 以 " | " 分隔单元格内容
		                }

		                // 移除最后一个 " | "
		                if (rowText.length() > 3) {
		                    rowText.setLength(rowText.length() - 3);
		                }

		                logger.info("Row Data: " + rowText.toString());
		            }

		            if (cells.size() > 0) {  // 确保这一行有数据
		            	// 创建 t_freeeBean 对象并将数据直接填充
		            	t_freeeBean bean = new t_freeeBean();

		            	bean.setKouza_mei(cells.get(1).getText().trim()); // 口座名
		            	bean.setTorihiki_bi(cells.get(4).getText().trim()); // 取引日
		            	bean.setTorihiki_naiyou(cells.get(5).getText().trim()); // 取引内容
		            	bean.setNyuukin_gaku(cells.get(6).getText().trim()); // 入金額
		            	bean.setShukkin_gaku(cells.get(7).getText().trim()); // 出金額
		            	bean.setZandaka(cells.get(8).getText().trim()); // 残高
		            	bean.setJoukyou(cells.get(9).getText().trim()); // 状態
		            	bean.setKoushin_bi(cells.get(10).getText().trim()); // 更新日
		            	bean.setShutoku_bi(cells.get(11).getText().trim()); // 取得日

		                rowDataList.add(bean);

//						logger.info("getTorihiki_naiyou : " + bean.getTorihiki_naiyou());

		            }
		        }



			} catch (Exception e) {
				throw e;


			} finally {
	            quitDriver();
				logger.info("end get_freee");
			}


			return rowDataList;


	}



	public String exc_letian_zhifu(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {
		int i=0;

		logger.info("start set_yinhang_letian");
		logger.info("PDSK : " + t_etax_jieguoExBean.getPDSK());

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

		double noufuKingaku = Double.parseDouble(t_etax_jieguoExBean.getNoufu_kingaku());
		double noufuKingaku_max = 31000;
		if (noufuKingaku > noufuKingaku_max) {
			logger.warn("払込金額过大(>"+noufuKingaku_max+")，请手动支付：" + noufuKingaku);
			return "払込金額过大(>"+noufuKingaku_max+")，请手动支付：" + noufuKingaku;
		}


			try {

		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

		        // 设置最大等待时间为 30 分钟 (1800 秒)
		        driver.manage().timeouts().pageLoadTimeout(Duration.ofMinutes(30));

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



				logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				/*
				 * 法人ログイン
				 */
				driver.get("https://fes.rakuten-bank.co.jp/XMS/security/RbS?COMMAND=LOGIN&CurrentPageID=START_CORP");
				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				if (driver.getPageSource().contains("メンテナンス")) {
					logger.info("楽天銀行系统维护中");
					t_etax_jieguoBean.setYyyymmdd_count("楽天銀行系统维护中");
					return "楽天銀行系统维护中";
				}



//
//	            // 运行 JavaScript 代码隐藏 `navigator.webdriver`
////	            JavascriptExecutor js = (JavascriptExecutor) driver;
//	            JavascriptExecutor.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
//
//	            // 获取 User-Agent 进行检查
//	            String userAgent = (String) JavascriptExecutor.executeScript("return navigator.userAgent;");
//	            logger.info("test " + (++i) + "伪装后的 User-Agent: " + userAgent);




				logger.info("test " + (++i) + " getTitle " + driver.getTitle());

				// 查找用户名和密码输入框及登录按钮
				WebElement usernameField = driver.findElement(By.id("LOGIN:USER_ID"));
				WebElement passwordField = driver.findElement(By.id("LOGIN:LOGIN_PASSWORD"));
				// 输入用户名和密码
				usernameField.sendKeys("PS2537295344");
				passwordField.sendKeys("dr2rZxGiLJ@7");


				logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement WebElement = driver.findElement(By.cssSelector("a[class=\"btn btn-default btn-login-01\"]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				driver.get("https://fes.rakuten-bank.co.jp/XMS/affiliation/gns?=&CurrentPageID=HEADER_FOOTER_LINK&COMMAND=MULTI_PAYMENT_SERVICE_START");

//				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
//				WebElement = driver.findElement(By.cssSelector("a[href=\"/XMS/inquiry/gns?=&CurrentPageID=CONTAINER_TRANSITION&NEXTNAVIGATION=pay.easy&COMMAND=CONTAINER_TRANSITION_START\"]"));
//				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.xpath("//a[contains(text(),'収納機関番号検索')]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				if (driver.getPageSource().contains("サービス提供時間外の為、払込みできません")) {
					logger.warn("サービス提供時間外の為、払込みできません");
					return "サービス提供時間外の為、払込みできません";
				}

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.id("FORM:TAX_CODE"));
				WebElement.sendKeys(t_etax_jieguoExBean.getShuunou_kikan_bangou());

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.cssSelector("input[value=\"検 索\"]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

/*
収納機関番号	00200
納付番号	利用者識別番号を入力してください。
確認番号	納税用確認番号を入力してください。
納付区分	XXXXXXXXX
有効期限	令和07年04月21日
納付金額	XXXXX 円

 */
				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.id("FORM:PAYMENT_NUMBER"));
				WebElement.sendKeys(t_etax_jieguoExBean.getBangou());


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.id("FORM:CONFIRMATION_NUMBER"));
				WebElement.sendKeys("123456");

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.id("FORM:PAYMENT_DIVISION"));
				WebElement.sendKeys(t_etax_jieguoExBean.getNoufu_kubun());

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.cssSelector("input[value=\"次へ（確認）\"]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				if (!driver.getPageSource().contains("当払込書は既にお支払済みです")) {

					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
					WebElement currentElement = driver.findElement(By.xpath("//th[contains(string(), '払込人名')]"));
					WebElement nextElement = currentElement.findElement(By.xpath("following-sibling::*[1]"));
					String nextText = nextElement.getText().replace("－", "ー");
					if (t_etax_jieguoExBean.getCompanyName_pianjiaming().indexOf(nextText) > -1) {

					} else {
						logger.warn("払込人名 NG：" + nextText);
						return "払込人名 NG：" + nextText;
					}


					currentElement = driver.findElement(By.xpath("//th[contains(string(), '払込金額')]"));
					nextElement = currentElement.findElement(By.xpath("following-sibling::*[1]"));
					nextText = nextElement.getText().replace(",", "").replace("円", "").replace(" ", "");

					noufuKingaku = Double.parseDouble(nextText);
					if (noufuKingaku > noufuKingaku_max) {
						logger.warn("払込金額过大(>"+noufuKingaku_max+")，请手动支付：" + nextText);
						return "払込金額过大(>"+noufuKingaku_max+")，请手动支付：" + nextText;
					}
					if (t_etax_jieguoExBean.getNoufu_kingaku().equals(nextText)) {

					} else {
						logger.warn("払込金額DB不一致 NG：" + nextText);
						return "払込金額DB不一致 NG：" + nextText;
					}

					WebElement = driver.findElement(By.cssSelector("input[value=\"次へ（確認）\"]"));
					JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


					try {
						Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
						WebElement = driver.findElement(By.cssSelector("font[class=\"errortxt\"]"));
						nextText = WebElement.getText();
						if (nextText.contains("残高が不足しています") || nextText.contains("615007")) {
							logger.warn("払込 NG：" + nextText);
							return "払込 NG：" + nextText;
						}
					} catch (NoSuchElementException e) {
						// TODO 自動生成された catch ブロック
					}

					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
					WebElement = driver.findElement(By.id("SECURITY_BOARD:NOTICE_FLAG"));
					JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
					WebElement = driver.findElement(By.id("SECURITY_BOARD:USER_PASSWORD"));
//					WebElement.sendKeys("2244");


					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
					WebElement = driver.findElement(By.cssSelector("input[value=\"払込実行\"]"));
//					JavascriptExecutor.executeScript("arguments[0].click();", WebElement);



					Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
					if (driver.getPageSource().contains("払込が完了しました。") || driver.getPageSource().contains("払込完了")) {

					} else {

						logger.info("支付失败");
						return "支付失败";
					}


				} else {
					logger.info("当払込書は既にお支払済みです");
					return "当払込書は既にお支払済みです";

				}




				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				driver.get("https://fes.rakuten-bank.co.jp/XMS/affiliation/gns?=&CurrentPageID=HEADER_FOOTER_LINK&COMMAND=MULTI_PAYMENT_SERVICE_START");

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				WebElement = driver.findElement(By.xpath("//a[contains(string(), 'Pay-easy（ペイジー）利用明細')]"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

				Thread.sleep(1000);logger.info("test " + (++i) + " getTitle " + driver.getTitle());
				String Noufu_kingaku = String.format("%,d", Long.parseLong(t_etax_jieguoExBean.getNoufu_kingaku())) + "円";
				WebElement row = driver.findElement(By.xpath("//tr[td/div[contains(text(), '"+Noufu_kingaku+"')]]"));

				// 在该行内找到 "确认" 按钮
				WebElement  = row.findElement(By.xpath(".//input[@type='submit' and @value='確 認']"));
				JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


				// 找到 FORM 内的第一个 TABLE 元素
				WebElement  = driver.findElement(By.xpath("//form[@id='FORM']/table[1]"));


				// 获取元素的 HTML 代码
				String elementHtml = WebElement.getAttribute("outerHTML");
				Document doc = Jsoup.parse(elementHtml);

				// 创建新的 <link> 标签
				Element linkTag = doc.createElement("link")
				    .attr("rel", "stylesheet")
				    .attr("href", "/rb/xfes/css/basicStyle.css")
				    .attr("type", "text/css");

				// 将 <link> 标签添加到 <head> 部分
				doc.head().appendChild(linkTag);

				// 输出修改后的 HTML
//				System.out.println(doc.html());


				String yyyymmdd_count = t_etax_jieguoExBean.getYyyymmdd_count();
				String yyyy = t_etax_jieguoExBean.getYyyy();
				String amount = t_etax_jieguoExBean.getNoufu_kingaku();


				t_user_account_amountDao t_user_account_amountDao = new t_user_account_amountDao();
				t_user_account_amountDao.UPDATE_blob("zhifu_pingzheng", yyyymmdd_count, yyyy, amount, doc.html());

			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end set_yinhang_letian");
			}
			return "楽天支付完了";





	}


	public t_etax_jieguoBean getShenqingJieguo_dianzi_nashui(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {

		logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count());

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



	            //NG linuxご利用のパソコン環境が、e-Taxの推奨環境を満たしているか
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					String osName = System.getProperty("os.name");
					if (osName.toLowerCase().contains("linux")) {
						return t_etax_jieguoBean;
					}
				}
				/*
				 * ログイン
				 */
				//開始届出（個人の方用）　新規
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					/*
					 * 個人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginIndividual");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='loginUserNumber(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

//					Thread.sleep(1000);
//					button = driver.findElement(By.id("btn_ReceiptA"));
//					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);

					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知（納付区分番号通知）"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }




						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);


				} else {




					/*
					 * 法人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					Thread.sleep(1000);
					button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知（納付区分番号通知）"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }




						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);




				}




			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end getShenqingJieguo");
			}
			return t_etax_jieguoBean;





	}


	public t_etax_jieguoBean getShenqingJieguo(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {

		logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count());

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



	            //NG linuxご利用のパソコン環境が、e-Taxの推奨環境を満たしているか
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					String osName = System.getProperty("os.name");
					if (osName.toLowerCase().contains("linux")) {
						return t_etax_jieguoBean;
					}
				}
				/*
				 * ログイン
				 */
				//開始届出（個人の方用）　新規
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					/*
					 * 個人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginIndividual");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='loginUserNumber(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

//					Thread.sleep(1000);
//					button = driver.findElement(By.id("btn_ReceiptA"));
//					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {



					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}


							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}



					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {



					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}



					        try {

								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
							    if (matchingElements.size() == 1) {
							    	t_etax_jieguoBean.setHtml_qr("OK");
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
							    	matchingElements.get(0).click();
							    }


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


					    matchingElements = driver.findElements(By.id("btn_close"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {
					        try {

					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }

						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
						/*
						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
						 */
						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

									addCss(doc);
									t_etax_jieguoBean.setHtml_qr(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);

						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();

								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);




					/*
					 * 追加認証 | e-Tax
					 * https://login.e-tax.nta.go.jp/login/reception/addAuth
					 */
					set_zhuijia_renzheng(driver, JavascriptExecutor);

//
//					Thread.sleep(1000);
//				    driver = getNewWindow(driver, Title);
//
//				    // 查找所有符合条件的 <p> 元素
//				    List<WebElement> matchingElements = driver.findElements(By.cssSelector("img[src=\"/content/WP200/assets/images/icn_key_b.png\"]"));
//
//					logger.warn(4);
//
//				    // 如果匹配的元素只有一个，则点击它
//				    if (matchingElements.size() >= 1) {
//				    	logger.warn("OKOKOK");
//
//				    }




//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//
//								Thread.sleep(1000);
//								WebElement WebElement = driver.findElement(By.cssSelector("button[onclick=\"doAddCertification();\"]"));
//								JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//					} while (while_flag);



					/*
					 * 登録された電子証明書と一致しません
Exception in thread "main" org.openqa.selenium.UnhandledAlertException: unexpected alert open: {Alert text : 登録された電子証明書と一致しません。（4120250129110853957）}
					 */
					while_flag = true;
					do {

						Thread.sleep(1000);
						 Alert alert = null;
					    // 切换到弹出框
			            try {
							alert = driver.switchTo().alert();
						} catch (Exception e1) {
							logger.info("处理 : 追加認証OK");
							while_flag = false;
						}


					    // 如果匹配的元素只有一个，则点击它
					    if (alert != null) {
				            // 获取弹出框文本
				            String alertText = alert.getText();

				            if (alertText.indexOf("登録された電子証明書と一致しません") > -1
				            		|| alertText.indexOf("電子証明書が登録されていません") > -1) {
								logger.warn("弹出框文本: " + alertText);
					            // 点击“确定”
					            alert.accept();


					            List<WebElement> matchingElements = driver.findElements(By.cssSelector("button[onclick='submitBack();']"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }

					            Title = "お知らせ・受信通知 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					            matchingElements = driver.findElements(By.id("btn_back"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }


					            Title = "TOP | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_MyPage"));

							    if (matchingElements.size() == 1) {

							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



					            Title = "マイページ | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_other"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "その他の登録情報 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_certificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Thread.sleep(1000);
					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_skipCertificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "電子証明書の登録・更新 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

							    matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));

							    // 如果匹配的元素只有一个，则点击它
							    if (matchingElements.size() == 1) {


							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();

							            // 使 div 显示
							            WebElement WebElement = driver.findElement(By.id("file_area"));
							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);

										WebElement = driver.findElement(By.id("inputEcertFileNm"));
										 // 使用 JavaScript 将内容插入到 <p> 标签中
							            JavascriptExecutor js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].innerText = arguments[1];", WebElement, p12Path);

							            WebElement inputElement = driver.findElement(By.id("txt_filpath_sig"));
							            // 使用 JavaScript 更新 value 属性
							            js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].value = arguments[1];", inputElement, p12Path);
							            // 验证更新后的 value
							            inputElement.getAttribute("value");


										WebElement = driver.findElement(By.id("inputEcertFilePass"));
										WebElement.sendKeys("Panda0518");

										logger.warn(0);
										WebElement = driver.findElement(By.id("btn_readECert"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//							            // 使 div 显示
//							            WebElement = driver.findElement(By.id("fild04"));
//							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnSrlNum"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "0731021E0034C2");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnHks"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "CN=0402010000001");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnLimit"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "2024/01/04 16:12:26.000～2024/07/04 23:59:59.000");

										logger.warn(1);
										Thread.sleep(3000);
										WebElement = driver.findElement(By.id("btn_updECert_sig_file"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
										logger.warn(2);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_back"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

										/*
										 *
										 */
							            Title = "マイページ | e-Tax";
							            logger.info("处理 : " + Title);

							            Thread.sleep(1000);
							            driver = getNewWindow(driver, Title);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_to_top"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);



							            while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



				            } else {
				            	logger.error("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 弹出框文本: " + alertText);
				            }


					    }

					} while (while_flag);

					String pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("TOP | e-Tax".equals(pageTitle)) {

						/*
						 * TOP | e-Tax
						 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
						 */
						Title = "TOP | e-Tax";
						logger.info("处理 : " + Title);


						count = 0;
						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

						    if (matchingElements.size() == 1) {
//				                	// 获取匹配的第一个元素的outerHTML
//				                	String html = matchingElements.get(0).getAttribute("outerHTML");
//				                	logger.info(html);

						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);
					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1500);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();


								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }

					boolean pdf_flag = true;
					if (pdf_flag ) {

						/*
						 * 帳票の表示
						 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
						 */
						Title = "帳票の表示 | e-Tax";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//すべて選択
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.id("btn_create_chohyo"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//帳票作成
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						        	// 注入JavaScript以拦截URL.createObjectURL
						            JavascriptExecutor.executeScript(
						                    "var originalCreateObjectURL = URL.createObjectURL;" +
						                    "var pdfURL = null;" +
						                    "URL.createObjectURL = function(blob) {" +
						                    "   pdfURL = originalCreateObjectURL(blob);" +
						                    "   console.log('Pdf URL: ' + pdfURL);" +
						                    "   return pdfURL;" +
						                    "};" +
						                    "window.getPdfURL = function() { return pdfURL; };"
						                );

						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						            //帳票イメージの表示を行います。よろしいですか？
						        	matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						} while (while_flag);



						String blobUrl="";
						while_flag = true;
						do {
							Thread.sleep(1000);
							List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
							WebElement matchingElement = null;

							for (WebElement element : divElements) {
								if (element.getText().equals("PDF作成が完了しました。")) {
									matchingElement = element;
						            // 获取拦截到的Blob URL
						            blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
									break;
								}
							}

							if (matchingElement != null) {
								// 查找所有符合条件的 <button> 元素
								List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));

								for (WebElement my_button : buttonElements) {
									if (my_button.getText().equals("表示")) {
										//表示
//										my_button.click();

										String script = "return (async function() {" +
												"  let response = await fetch('" + blobUrl + "');" +
												"  let blob = await response.blob();" +
												"  let reader = new FileReader();" +
												"  return new Promise((resolve, reject) => {" +
												"    reader.onloadend = () => resolve(reader.result);" +
												"    reader.onerror = reject;" +
												"    reader.readAsDataURL(blob);" +
												"  });" +
												"})();";

										// 使用Selenium执行JavaScript
										String base64Data  = (String) JavascriptExecutor.executeScript(script);
										// 去除Base64数据头部的"data:application/pdf;base64,"
										String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
										 // 获取上传文件的输入流
										InputStream fileContent = base64ToInputStream(base64Content);
										t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);

										while_flag = false;
										break;
									}
								}
							}
						} while (while_flag);
					} else {
						// pdf_ng
						String base64Data  = "pdf_ng";
					    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
						t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);

					}



				} else {


					/*
					 * 法人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					Thread.sleep(1000);
					button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {


					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}


							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);
					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}


					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知（納付区分番号通知）"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {



					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}




					        try {

								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
							    if (matchingElements.size() == 1) {
							    	t_etax_jieguoBean.setHtml_qr("OK");
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
							    	matchingElements.get(0).click();
							    }


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


					    matchingElements = driver.findElements(By.id("btn_close"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }

						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
						/*
						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
						 */
						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

									addCss(doc);
									t_etax_jieguoBean.setHtml_qr(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
							++count;
							if (count > 2) {
								while_flag = false;
//								return null;
							}
						} while (while_flag);


						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}

					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    } else {
					    	Thread.sleep(1000);

					    }

						++count;
						logger.info("count : " + count);
						if (count > 4) {
							logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 消費税及び地方消費税申告 : null");
							while_flag = false;
							return null;
						}
					} while (while_flag);



					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
								while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }


					boolean pdf_flag = true;
					if (pdf_flag ) {
						/*
						 * 帳票の表示
						 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
						 */
						Title = "帳票の表示 | e-Tax";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
							driver = getNewWindow(driver, Title);

							String pageTitle = driver.getTitle();
							logger.info("pageTitle : " + pageTitle);
							if (!Title.equals(pageTitle)) {
								List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));
								// 如果匹配的元素只有一个，则点击它
								if (matchingElements.size() == 1) {
									try {
										// 滚动到元素可见
										((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
//										while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
								}
							}

							Thread.sleep(500);
							List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//すべて選択
									matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

							matchingElements = driver.findElements(By.id("btn_create_chohyo"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//帳票作成
									matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

							matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 注入JavaScript以拦截URL.createObjectURL
									JavascriptExecutor.executeScript(
											"var originalCreateObjectURL = URL.createObjectURL;" +
													"var pdfURL = null;" +
													"URL.createObjectURL = function(blob) {" +
													"   pdfURL = originalCreateObjectURL(blob);" +
													"   console.log('Pdf URL: ' + pdfURL);" +
													"   return pdfURL;" +
													"};" +
													"window.getPdfURL = function() { return pdfURL; };"
											);

									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//帳票イメージの表示を行います。よろしいですか？
									matchingElements.get(0).click();
									while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

						} while (while_flag);



						String blobUrl="";
						while_flag = true;
						do {
							Thread.sleep(1000);
							List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
							WebElement matchingElement = null;

							for (WebElement element : divElements) {
								if (element.getText().equals("PDF作成が完了しました。")) {
									logger.info("处理 : PDF作成が完了しました。");
									matchingElement = element;
									// 获取拦截到的Blob URL
									blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
									break;
								}
							}

							if (matchingElement != null) {
								// 查找所有符合条件的 <button> 元素
								List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));

								for (WebElement my_button : buttonElements) {
									if (my_button.getText().equals("表示")) {
										//表示
//										my_button.click();

										String script = "return (async function() {" +
												"  let response = await fetch('" + blobUrl + "');" +
												"  let blob = await response.blob();" +
												"  let reader = new FileReader();" +
												"  return new Promise((resolve, reject) => {" +
												"    reader.onloadend = () => resolve(reader.result);" +
												"    reader.onerror = reject;" +
												"    reader.readAsDataURL(blob);" +
												"  });" +
												"})();";

										// 使用Selenium执行JavaScript
										String base64Data  = (String) JavascriptExecutor.executeScript(script);
										// 去除Base64数据头部的"data:application/pdf;base64,"
										String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
										// 获取上传文件的输入流
										InputStream fileContent = base64ToInputStream(base64Content);
										t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);

										while_flag = false;
										break;
									}
								}
							}
						} while (while_flag);
					} else {
						// pdf_ng
						String base64Data  = "pdf_ng";
					    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
						t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);
					}


				}







			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end getShenqingJieguo");
			}
			return t_etax_jieguoBean;





	}


	public t_etax_jieguoBean getShenqingJieguoTatujin(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {

		logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count());

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



	            //NG linuxご利用のパソコン環境が、e-Taxの推奨環境を満たしているか
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					String osName = System.getProperty("os.name");
					if (osName.toLowerCase().contains("linux")) {
						return t_etax_jieguoBean;
					}
					//TODO TODO
//					return t_etax_jieguoBean;
				} else {
					//TODO
//					return t_etax_jieguoBean;
				}
				/*
				 * ログイン
				 */
				//開始届出（個人の方用）　新規
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					/*
					 * 個人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginIndividual");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='loginUserNumber(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

//					Thread.sleep(1000);
//					button = driver.findElement(By.id("btn_ReceiptA"));
//					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    // 查找所有符合条件的 <p> 元素
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));
//
//					    matchingElements = matchingElements.stream()
//					        .filter(element -> element.getText().contains("納付情報登録依頼"))
//					        .collect(Collectors.toList());
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() >= 1) {
//
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//
//
//							Thread.sleep(1000);
//						    driver = getNewWindow(driver, Title);
//
//					        try {
//
//								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
//							    if (matchingElements.size() == 1) {
//
//
//						            WebElement table = matchingElements.get(0);
//
//						            List<WebElement> rows = table.findElements(By.tagName("tr"));
//
//						            String shunoKikanBango = "";
//						            String nofuKubun = "";
//						            String yukokigen = "";
//						            String nofuKingaku = "";
//
//						            for (WebElement row : rows) {
//						                List<WebElement> cells = row.findElements(By.tagName("td"));
//						                if (cells.size() == 2) {
//						                    String key = cells.get(0).getText().trim();
//						                    String value = cells.get(1).getText().trim();
//
//						                    switch (key) {
//						                        case "収納機関番号":
//						                            shunoKikanBango = value;
//						                            break;
//						                        case "納付区分":
//						                            nofuKubun = value;
//						                            break;
//						                        case "有効期限":
//						                            yukokigen = value;
//						                            break;
//						                        case "納付金額":
//						                            nofuKingaku = value;
//						                            break;
//						                    }
//						                }
//						            }
//
//
//						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
//						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
//						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
//						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));
//
//						            while_flag = false;
//
//							    }
//
//
//
//
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//
//
//
//					    }
//
//
//						++count;
//						if (count > 2) {
//							while_flag = false;
////							return null;
//						}
//
//					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//
//					    // 查找所有符合条件的 <p> 元素
//					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));
//
//					    matchingElements = matchingElements.stream()
//					        .filter(element -> element.getText().contains("受信通知"))
//					        .collect(Collectors.toList());
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() >= 1) {
//
//
//
//					        try {
//
//								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
//							    if (matchingElements.size() == 1) {
//
//
//						            WebElement table = matchingElements.get(0);
//
//						            List<WebElement> rows = table.findElements(By.tagName("tr"));
//
//						            String shunoKikanBango = "";
//						            String nofuKubun = "";
//						            String yukokigen = "";
//						            String nofuKingaku = "";
//
//						            for (WebElement row : rows) {
//						                List<WebElement> cells = row.findElements(By.tagName("td"));
//						                if (cells.size() == 2) {
//						                    String key = cells.get(0).getText().trim();
//						                    String value = cells.get(1).getText().trim();
//
//						                    switch (key) {
//						                        case "収納機関番号":
//						                            shunoKikanBango = value;
//						                            break;
//						                        case "納付区分":
//						                            nofuKubun = value;
//						                            break;
//						                        case "有効期限":
//						                            yukokigen = value;
//						                            break;
//						                        case "納付金額":
//						                            nofuKingaku = value;
//						                            break;
//						                    }
//						                }
//						            }
//
//
//						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
//						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
//						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
//						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));
//
//						            while_flag = false;
//
//							    }
//
//
//
//
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//
//
//
//					        try {
//
//								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
//							    if (matchingElements.size() == 1) {
//							    	t_etax_jieguoBean.setHtml_qr("OK");
//						            // 滚动到元素可见
//						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//							    	matchingElements.get(0).click();
//							    }
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//					    }
//
//
//					    matchingElements = driver.findElements(By.id("btn_close"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(1000);
//								matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//						++count;
//						if (count > 2) {
//							while_flag = false;
////							return null;
//						}
//					} while (while_flag);

//					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
//						/*
//						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
//						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
//						 */
//						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
//						logger.info("处理 : " + Title);
//
//						while_flag = true;
//						do {
//							Thread.sleep(1000);
//						    driver = getNewWindow(driver, Title);
//
//
//						    // 查找所有符合条件的 <p> 元素
//						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));
//
//
//						    matchingElements = matchingElements.stream()
//						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
//						        .collect(Collectors.toList());
//
//						    // 如果匹配的元素只有一个，则点击它
//						    if (matchingElements.size() >= 1) {
//						        try {
//
//									Document doc = Jsoup.parse(driver.getPageSource());
//							        doc.select("input[type=button]").remove();
//
//									addCss(doc);
//									t_etax_jieguoBean.setHtml_qr(doc.html());
//
//									driver.close();
//						            while_flag = false;
//								} catch (Exception e) {
//									logger.warn(e.getMessage());
//								}
//						    }
//						} while (while_flag);
//
//						//受信通知（納付区分番号通知）
//					    driver = getNewWindow(driver, Title);
//						driver.close();
//
//					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();

								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);




					/*
					 * 追加認証 | e-Tax
					 * https://login.e-tax.nta.go.jp/login/reception/addAuth
					 */
					set_zhuijia_renzheng(driver, JavascriptExecutor);

//
//					Thread.sleep(1000);
//				    driver = getNewWindow(driver, Title);
//
//				    // 查找所有符合条件的 <p> 元素
//				    List<WebElement> matchingElements = driver.findElements(By.cssSelector("img[src=\"/content/WP200/assets/images/icn_key_b.png\"]"));
//
//					logger.warn(4);
//
//				    // 如果匹配的元素只有一个，则点击它
//				    if (matchingElements.size() >= 1) {
//				    	logger.warn("OKOKOK");
//
//				    }




//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//
//								Thread.sleep(1000);
//								WebElement WebElement = driver.findElement(By.cssSelector("button[onclick=\"doAddCertification();\"]"));
//								JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//					} while (while_flag);



					/*
					 * 登録された電子証明書と一致しません
Exception in thread "main" org.openqa.selenium.UnhandledAlertException: unexpected alert open: {Alert text : 登録された電子証明書と一致しません。（4120250129110853957）}
					 */
					while_flag = true;
					do {

						Thread.sleep(1000);
						 Alert alert = null;
					    // 切换到弹出框
			            try {
							alert = driver.switchTo().alert();
						} catch (Exception e1) {
							logger.info("处理 : 追加認証OK");
							while_flag = false;
						}


					    // 如果匹配的元素只有一个，则点击它
					    if (alert != null) {
				            // 获取弹出框文本
				            String alertText = alert.getText();

				            if (alertText.indexOf("登録された電子証明書と一致しません") > -1
				            		|| alertText.indexOf("電子証明書が登録されていません") > -1) {
								logger.warn("弹出框文本: " + alertText);
					            // 点击“确定”
					            alert.accept();


					            List<WebElement> matchingElements = driver.findElements(By.cssSelector("button[onclick='submitBack();']"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }

					            Title = "お知らせ・受信通知 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					            matchingElements = driver.findElements(By.id("btn_back"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }


					            Title = "TOP | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_MyPage"));

							    if (matchingElements.size() == 1) {

							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



					            Title = "マイページ | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_other"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "その他の登録情報 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_certificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Thread.sleep(1000);
					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_skipCertificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "電子証明書の登録・更新 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

							    matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));

							    // 如果匹配的元素只有一个，则点击它
							    if (matchingElements.size() == 1) {


							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();

							            // 使 div 显示
							            WebElement WebElement = driver.findElement(By.id("file_area"));
							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);

										WebElement = driver.findElement(By.id("inputEcertFileNm"));
										 // 使用 JavaScript 将内容插入到 <p> 标签中
							            JavascriptExecutor js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].innerText = arguments[1];", WebElement, p12Path);

							            WebElement inputElement = driver.findElement(By.id("txt_filpath_sig"));
							            // 使用 JavaScript 更新 value 属性
							            js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].value = arguments[1];", inputElement, p12Path);
							            // 验证更新后的 value
							            inputElement.getAttribute("value");


										WebElement = driver.findElement(By.id("inputEcertFilePass"));
										WebElement.sendKeys("Panda0518");

										Thread.sleep(500);
										logger.warn(0);
										WebElement = driver.findElement(By.id("btn_readECert"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//							            // 使 div 显示
//							            WebElement = driver.findElement(By.id("fild04"));
//							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnSrlNum"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "0731021E0034C2");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnHks"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "CN=0402010000001");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnLimit"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "2024/01/04 16:12:26.000～2024/07/04 23:59:59.000");

										logger.warn(1);
										Thread.sleep(3000);
										WebElement = driver.findElement(By.id("btn_updECert_sig_file"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
										logger.warn(2);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_back"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

										/*
										 *
										 */
							            Title = "マイページ | e-Tax";
							            logger.info("处理 : " + Title);

							            Thread.sleep(1000);
							            driver = getNewWindow(driver, Title);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_to_top"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);



							            while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



				            } else {
				            	logger.error("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 弹出框文本: " + alertText);
				            }


					    }

					} while (while_flag);

					String pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("TOP | e-Tax".equals(pageTitle)) {

						/*
						 * TOP | e-Tax
						 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
						 */
						Title = "TOP | e-Tax";
						logger.info("处理 : " + Title);


						count = 0;
						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

						    if (matchingElements.size() == 1) {
//				                	// 获取匹配的第一个元素的outerHTML
//				                	String html = matchingElements.get(0).getAttribute("outerHTML");
//				                	logger.info(html);

						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);
					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1500);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();


								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


					Thread.sleep(500);

					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}


//
//
//					/*
//					 * お知らせ・受信通知
//					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
//					 */
//					Title = "お知らせ・受信通知 | e-Tax";
//					logger.info("处理 : " + Title);
//
//					while_flag = true;
//					do {
//						Thread.sleep(1500);
//					    driver = getNewWindow(driver, Title);
//
//					    // 查找所有符合条件的 <p> 元素
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));
//
//					    matchingElements = matchingElements.stream()
//					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
//					        .collect(Collectors.toList());
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() >= 1) {
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//
//
//								Thread.sleep(500);
//								button = driver.findElement(By.id("btn_addCertification"));
//								JavascriptExecutor.executeScript("arguments[0].click();", button);
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//					} while (while_flag);

					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_save_shinkokutou"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();


								Thread.sleep(1000);
								WebElement td = driver.findElement(By.name("content_uketsuke_no"));
								String fileName = "受信データ" + td.getText().trim() + ".xtx";
								Path filePath = Path.of(downloadDir, fileName);
								String etax_xtx = Files.readString(filePath, StandardCharsets.UTF_8);
								t_etax_jieguoBean.setEtax_xtx(etax_xtx);
								Files.deleteIfExists(filePath);


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }

					boolean pdf_flag = true;
					pdf_flag = false;
					if (pdf_flag ) {

						/*
						 * 帳票の表示
						 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
						 */
						Title = "帳票の表示 | e-Tax";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//すべて選択
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.id("btn_create_chohyo"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//帳票作成
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						        	// 注入JavaScript以拦截URL.createObjectURL
						            JavascriptExecutor.executeScript(
						                    "var originalCreateObjectURL = URL.createObjectURL;" +
						                    "var pdfURL = null;" +
						                    "URL.createObjectURL = function(blob) {" +
						                    "   pdfURL = originalCreateObjectURL(blob);" +
						                    "   console.log('Pdf URL: ' + pdfURL);" +
						                    "   return pdfURL;" +
						                    "};" +
						                    "window.getPdfURL = function() { return pdfURL; };"
						                );

						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						            //帳票イメージの表示を行います。よろしいですか？
						        	matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						} while (while_flag);



						String blobUrl="";
						while_flag = true;
						do {
							Thread.sleep(1000);
							List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
							WebElement matchingElement = null;

							for (WebElement element : divElements) {
								if (element.getText().equals("PDF作成が完了しました。")) {
									matchingElement = element;
						            // 获取拦截到的Blob URL
						            blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
									break;
								}
							}

							if (matchingElement != null) {
								// 查找所有符合条件的 <button> 元素
								List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));

								for (WebElement my_button : buttonElements) {
									if (my_button.getText().equals("表示")) {
										//表示
//										my_button.click();

										String script = "return (async function() {" +
												"  let response = await fetch('" + blobUrl + "');" +
												"  let blob = await response.blob();" +
												"  let reader = new FileReader();" +
												"  return new Promise((resolve, reject) => {" +
												"    reader.onloadend = () => resolve(reader.result);" +
												"    reader.onerror = reject;" +
												"    reader.readAsDataURL(blob);" +
												"  });" +
												"})();";

										// 使用Selenium执行JavaScript
										String base64Data  = (String) JavascriptExecutor.executeScript(script);
										// 去除Base64数据头部的"data:application/pdf;base64,"
										String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
										 // 获取上传文件的输入流
										InputStream fileContent = base64ToInputStream(base64Content);
										t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);

										while_flag = false;
										break;
									}
								}
							}
						} while (while_flag);
					} else {
						// pdf_ng
						String base64Data  = "pdf_ng";
					    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
						t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);

					}



				} else {


					/*
					 * 法人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					Thread.sleep(1000);
					button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    // 查找所有符合条件的 <p> 元素
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));
//
//					    matchingElements = matchingElements.stream()
//					        .filter(element -> element.getText().contains("納付情報登録依頼"))
//					        .collect(Collectors.toList());
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() >= 1) {
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//
//
//							Thread.sleep(1000);
//						    driver = getNewWindow(driver, Title);
//					        try {
//
//								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
//							    if (matchingElements.size() == 1) {
//
//
//						            WebElement table = matchingElements.get(0);
//
//						            List<WebElement> rows = table.findElements(By.tagName("tr"));
//
//						            String shunoKikanBango = "";
//						            String nofuKubun = "";
//						            String yukokigen = "";
//						            String nofuKingaku = "";
//
//						            for (WebElement row : rows) {
//						                List<WebElement> cells = row.findElements(By.tagName("td"));
//						                if (cells.size() == 2) {
//						                    String key = cells.get(0).getText().trim();
//						                    String value = cells.get(1).getText().trim();
//
//						                    switch (key) {
//						                        case "収納機関番号":
//						                            shunoKikanBango = value;
//						                            break;
//						                        case "納付区分":
//						                            nofuKubun = value;
//						                            break;
//						                        case "有効期限":
//						                            yukokigen = value;
//						                            break;
//						                        case "納付金額":
//						                            nofuKingaku = value;
//						                            break;
//						                    }
//						                }
//						            }
//
//
//						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
//						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
//						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
//						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));
//
//						            while_flag = false;
//
//							    }
//
//
//
//
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//
//
//					    }
//
//
//						++count;
//						if (count > 2) {
//							while_flag = false;
////							return null;
//						}
//
//					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//
//					    // 查找所有符合条件的 <p> 元素
//					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));
//
//					    matchingElements = matchingElements.stream()
//					        .filter(element -> element.getText().contains("受信通知（納付区分番号通知）"))
//					        .collect(Collectors.toList());
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() >= 1) {
//
//
//
//					        try {
//
//								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
//							    if (matchingElements.size() == 1) {
//
//
//						            WebElement table = matchingElements.get(0);
//
//						            List<WebElement> rows = table.findElements(By.tagName("tr"));
//
//						            String shunoKikanBango = "";
//						            String nofuKubun = "";
//						            String yukokigen = "";
//						            String nofuKingaku = "";
//
//						            for (WebElement row : rows) {
//						                List<WebElement> cells = row.findElements(By.tagName("td"));
//						                if (cells.size() == 2) {
//						                    String key = cells.get(0).getText().trim();
//						                    String value = cells.get(1).getText().trim();
//
//						                    switch (key) {
//						                        case "収納機関番号":
//						                            shunoKikanBango = value;
//						                            break;
//						                        case "納付区分":
//						                            nofuKubun = value;
//						                            break;
//						                        case "有効期限":
//						                            yukokigen = value;
//						                            break;
//						                        case "納付金額":
//						                            nofuKingaku = value;
//						                            break;
//						                    }
//						                }
//						            }
//
//
//						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
//						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
//						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
//						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));
//
//						            while_flag = false;
//
//							    }
//
//
//
//
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//
//
//
//
//					        try {
//
//								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
//							    if (matchingElements.size() == 1) {
//							    	t_etax_jieguoBean.setHtml_qr("OK");
//						            // 滚动到元素可见
//						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//							    	matchingElements.get(0).click();
//							    }
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								logger.warn(e.getMessage());
//							}
//					    }
//
//
//					    matchingElements = driver.findElements(By.id("btn_close"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(1000);
//					            matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//						++count;
//						if (count > 2) {
//							while_flag = false;
////							return null;
//						}
//					} while (while_flag);

//					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
//						/*
//						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
//						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
//						 */
//						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
//						logger.info("处理 : " + Title);
//
//						while_flag = true;
//						do {
//							Thread.sleep(1000);
//						    driver = getNewWindow(driver, Title);
//
//
//						    // 查找所有符合条件的 <p> 元素
//						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));
//
//
//						    matchingElements = matchingElements.stream()
//						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
//						        .collect(Collectors.toList());
//
//						    // 如果匹配的元素只有一个，则点击它
//						    if (matchingElements.size() >= 1) {
//						        try {
//
//									Document doc = Jsoup.parse(driver.getPageSource());
//							        doc.select("input[type=button]").remove();
//
//									addCss(doc);
//									t_etax_jieguoBean.setHtml_qr(doc.html());
//
//									driver.close();
//						            while_flag = false;
//								} catch (Exception e) {
//									logger.warn(e.getMessage());
//								}
//						    }
//							++count;
//							if (count > 2) {
//								while_flag = false;
////								return null;
//							}
//						} while (while_flag);
//
//
//						//受信通知（納付区分番号通知）
//					    driver = getNewWindow(driver, Title);
//						driver.close();
//
//					}

					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    } else {
					    	Thread.sleep(1000);

					    }

						++count;
						logger.info("count : " + count);
						if (count > 4) {
							logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 消費税及び地方消費税申告 : null");
							while_flag = false;
							return null;
						}
					} while (while_flag);



					//TODO TODO
					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_save_shinkokutou"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					           matchingElements.get(0).click();

					           int count_etax_xtx=0;
								while (true) {
									++count_etax_xtx;
									if (count_etax_xtx > 100) {
										break;
									}
									try {

										WebElement td = driver.findElement(By.name("content_uketsuke_no"));
										String fileName = "受信データ" + td.getText().trim() + ".xtx";
										Path filePath = Path.of(downloadDir, fileName);
										String etax_xtx = Files.readString(filePath, StandardCharsets.UTF_8);
										t_etax_jieguoBean.setEtax_xtx(etax_xtx);
										Files.deleteIfExists(filePath);
										break;

									} catch (NoSuchFileException e) {
										// 文件还没下载完成，继续等
										Thread.sleep(500); // 建议加点延迟，避免CPU空转

									}
								}



								while_flag = false;
							} catch (Exception e) {
								e.printStackTrace();
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }


					boolean pdf_flag = true;
					pdf_flag = false;
					if (pdf_flag ) {
						/*
						 * 帳票の表示
						 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
						 */
						Title = "帳票の表示 | e-Tax";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
							driver = getNewWindow(driver, Title);

							String pageTitle = driver.getTitle();
							logger.info("pageTitle : " + pageTitle);
							if (!Title.equals(pageTitle)) {
								List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));
								// 如果匹配的元素只有一个，则点击它
								if (matchingElements.size() == 1) {
									try {
										// 滚动到元素可见
										((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
//										while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
								}
							}

							Thread.sleep(500);
							List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//すべて選択
									matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

							matchingElements = driver.findElements(By.id("btn_create_chohyo"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//帳票作成
									matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

							matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));

							// 如果匹配的元素只有一个，则点击它
							if (matchingElements.size() == 1) {
								try {
									// 注入JavaScript以拦截URL.createObjectURL
									JavascriptExecutor.executeScript(
											"var originalCreateObjectURL = URL.createObjectURL;" +
													"var pdfURL = null;" +
													"URL.createObjectURL = function(blob) {" +
													"   pdfURL = originalCreateObjectURL(blob);" +
													"   console.log('Pdf URL: ' + pdfURL);" +
													"   return pdfURL;" +
													"};" +
													"window.getPdfURL = function() { return pdfURL; };"
											);

									// 滚动到元素可见
									((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									//帳票イメージの表示を行います。よろしいですか？
									matchingElements.get(0).click();
									while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
							}

						} while (while_flag);



						String blobUrl="";
						while_flag = true;
						do {
							Thread.sleep(1000);
							List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
							WebElement matchingElement = null;

							for (WebElement element : divElements) {
								if (element.getText().equals("PDF作成が完了しました。")) {
									logger.info("处理 : PDF作成が完了しました。");
									matchingElement = element;
									// 获取拦截到的Blob URL
									blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
									break;
								}
							}

							if (matchingElement != null) {
								// 查找所有符合条件的 <button> 元素
								List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));

								for (WebElement my_button : buttonElements) {
									if (my_button.getText().equals("表示")) {
										//表示
//										my_button.click();

										String script = "return (async function() {" +
												"  let response = await fetch('" + blobUrl + "');" +
												"  let blob = await response.blob();" +
												"  let reader = new FileReader();" +
												"  return new Promise((resolve, reject) => {" +
												"    reader.onloadend = () => resolve(reader.result);" +
												"    reader.onerror = reject;" +
												"    reader.readAsDataURL(blob);" +
												"  });" +
												"})();";

										// 使用Selenium执行JavaScript
										String base64Data  = (String) JavascriptExecutor.executeScript(script);
										// 去除Base64数据头部的"data:application/pdf;base64,"
										String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
										// 获取上传文件的输入流
										InputStream fileContent = base64ToInputStream(base64Content);
										t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);

										while_flag = false;
										break;
									}
								}
							}
						} while (while_flag);
					} else {
						// pdf_ng
						String base64Data  = "pdf_ng";
					    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
						t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);
					}


				}







			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end getShenqingJieguo");
			}
			return t_etax_jieguoBean;





	}


	/*
	 * 消費税及び地方消費税の中間申告について
	 */
	public t_etax_jieguoBean getZhongjianShengao(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {

		logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count());

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver

	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



	            //NG linuxご利用のパソコン環境が、e-Taxの推奨環境を満たしているか
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					String osName = System.getProperty("os.name");
					if (osName.toLowerCase().contains("linux")) {
						return t_etax_jieguoBean;
					}
				}
				/*
				 * ログイン
				 */
				//開始届出（個人の方用）　新規
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {

					//TODO 个人中間申告　未対応
					//TODO 个人中間申告　未対応
					//TODO 个人中間申告　未対応
					if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
						return t_etax_jieguoBean;
					}


					/*
					 * 個人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginIndividual");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='loginUserNumber(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

//					Thread.sleep(1000);
//					button = driver.findElement(By.id("btn_ReceiptA"));
//					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税の中間申告について"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {



					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}


							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}



					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {



					        try {

								matchingElements = driver.findElements(By.id("area_detail_info_banking"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "納付区分":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setShuunou_kikan_bangou(shunoKikanBango);
						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);
						            t_etax_jieguoBean.setYuukou_kigen(FuncUtils.convertJapaneseEraDate(yukokigen));
						            t_etax_jieguoBean.setNoufu_kingaku(FuncUtils.cleanCurrency(nofuKingaku));

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}



					        try {

								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
							    if (matchingElements.size() == 1) {
							    	t_etax_jieguoBean.setHtml_qr("OK");
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
							    	matchingElements.get(0).click();
							    }


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


					    matchingElements = driver.findElements(By.id("btn_close"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {
					        try {

					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " " + e.getMessage());
							}
					    }

						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
						/*
						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
						 */
						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

									addCss(doc);
									t_etax_jieguoBean.setHtml_qr(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);

						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();

								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);




					/*
					 * 追加認証 | e-Tax
					 * https://login.e-tax.nta.go.jp/login/reception/addAuth
					 */
					set_zhuijia_renzheng(driver, JavascriptExecutor);

//
//					Thread.sleep(1000);
//				    driver = getNewWindow(driver, Title);
//
//				    // 查找所有符合条件的 <p> 元素
//				    List<WebElement> matchingElements = driver.findElements(By.cssSelector("img[src=\"/content/WP200/assets/images/icn_key_b.png\"]"));
//
//					logger.warn(4);
//
//				    // 如果匹配的元素只有一个，则点击它
//				    if (matchingElements.size() >= 1) {
//				    	logger.warn("OKOKOK");
//
//				    }




//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//
//								Thread.sleep(1000);
//								WebElement WebElement = driver.findElement(By.cssSelector("button[onclick=\"doAddCertification();\"]"));
//								JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//					} while (while_flag);



					/*
					 * 登録された電子証明書と一致しません
Exception in thread "main" org.openqa.selenium.UnhandledAlertException: unexpected alert open: {Alert text : 登録された電子証明書と一致しません。（4120250129110853957）}
					 */
					while_flag = true;
					do {

						Thread.sleep(1000);
						 Alert alert = null;
					    // 切换到弹出框
			            try {
							alert = driver.switchTo().alert();
						} catch (Exception e1) {
							logger.info("处理 : 追加認証OK");
							while_flag = false;
						}


					    // 如果匹配的元素只有一个，则点击它
					    if (alert != null) {
				            // 获取弹出框文本
				            String alertText = alert.getText();

				            if (alertText.indexOf("登録された電子証明書と一致しません") > -1
				            		|| alertText.indexOf("電子証明書が登録されていません") > -1) {
								logger.warn("弹出框文本: " + alertText);
					            // 点击“确定”
					            alert.accept();


					            List<WebElement> matchingElements = driver.findElements(By.cssSelector("button[onclick='submitBack();']"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }

					            Title = "お知らせ・受信通知 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					            matchingElements = driver.findElements(By.id("btn_back"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }


					            Title = "TOP | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_MyPage"));

							    if (matchingElements.size() == 1) {

							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



					            Title = "マイページ | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_other"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "その他の登録情報 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_certificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Thread.sleep(1000);
					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_skipCertificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "電子証明書の登録・更新 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

							    matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));

							    // 如果匹配的元素只有一个，则点击它
							    if (matchingElements.size() == 1) {


							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();

							            // 使 div 显示
							            WebElement WebElement = driver.findElement(By.id("file_area"));
							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);

										WebElement = driver.findElement(By.id("inputEcertFileNm"));
										 // 使用 JavaScript 将内容插入到 <p> 标签中
							            JavascriptExecutor js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].innerText = arguments[1];", WebElement, p12Path);

							            WebElement inputElement = driver.findElement(By.id("txt_filpath_sig"));
							            // 使用 JavaScript 更新 value 属性
							            js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].value = arguments[1];", inputElement, p12Path);
							            // 验证更新后的 value
							            inputElement.getAttribute("value");


										WebElement = driver.findElement(By.id("inputEcertFilePass"));
										WebElement.sendKeys("Panda0518");

										logger.warn(0);
										WebElement = driver.findElement(By.id("btn_readECert"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//							            // 使 div 显示
//							            WebElement = driver.findElement(By.id("fild04"));
//							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnSrlNum"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "0731021E0034C2");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnHks"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "CN=0402010000001");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnLimit"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "2024/01/04 16:12:26.000～2024/07/04 23:59:59.000");

										logger.warn(1);
										Thread.sleep(3000);
										WebElement = driver.findElement(By.id("btn_updECert_sig_file"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
										logger.warn(2);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_back"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

										/*
										 *
										 */
							            Title = "マイページ | e-Tax";
							            logger.info("处理 : " + Title);

							            Thread.sleep(1000);
							            driver = getNewWindow(driver, Title);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_to_top"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);



							            while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



				            } else {
				            	logger.error("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 弹出框文本: " + alertText);
				            }


					    }

					} while (while_flag);

					String pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("TOP | e-Tax".equals(pageTitle)) {

						/*
						 * TOP | e-Tax
						 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
						 */
						Title = "TOP | e-Tax";
						logger.info("处理 : " + Title);


						count = 0;
						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

						    if (matchingElements.size() == 1) {
//				                	// 获取匹配的第一个元素的outerHTML
//				                	String html = matchingElements.get(0).getAttribute("outerHTML");
//				                	logger.info(html);

						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);
					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1500);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();


								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }

					boolean pdf_flag = true;
					if (pdf_flag ) {

						/*
						 * 帳票の表示
						 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
						 */
						Title = "帳票の表示 | e-Tax";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//すべて選択
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.id("btn_create_chohyo"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						        	//帳票作成
						        	matchingElements.get(0).click();
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						    matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() == 1) {
						        try {
						        	// 注入JavaScript以拦截URL.createObjectURL
						            JavascriptExecutor.executeScript(
						                    "var originalCreateObjectURL = URL.createObjectURL;" +
						                    "var pdfURL = null;" +
						                    "URL.createObjectURL = function(blob) {" +
						                    "   pdfURL = originalCreateObjectURL(blob);" +
						                    "   console.log('Pdf URL: ' + pdfURL);" +
						                    "   return pdfURL;" +
						                    "};" +
						                    "window.getPdfURL = function() { return pdfURL; };"
						                );

						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						            //帳票イメージの表示を行います。よろしいですか？
						        	matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
									continue;
								}
						    }

						} while (while_flag);



						String blobUrl="";
						while_flag = true;
						do {
							Thread.sleep(1000);
							List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
							WebElement matchingElement = null;

							for (WebElement element : divElements) {
								if (element.getText().equals("PDF作成が完了しました。")) {
									matchingElement = element;
						            // 获取拦截到的Blob URL
						            blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
									break;
								}
							}

							if (matchingElement != null) {
								// 查找所有符合条件的 <button> 元素
								List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));

								for (WebElement my_button : buttonElements) {
									if (my_button.getText().equals("表示")) {
										//表示
//										my_button.click();

										String script = "return (async function() {" +
												"  let response = await fetch('" + blobUrl + "');" +
												"  let blob = await response.blob();" +
												"  let reader = new FileReader();" +
												"  return new Promise((resolve, reject) => {" +
												"    reader.onloadend = () => resolve(reader.result);" +
												"    reader.onerror = reject;" +
												"    reader.readAsDataURL(blob);" +
												"  });" +
												"})();";

										// 使用Selenium执行JavaScript
										String base64Data  = (String) JavascriptExecutor.executeScript(script);
										// 去除Base64数据头部的"data:application/pdf;base64,"
										String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
										 // 获取上传文件的输入流
										InputStream fileContent = base64ToInputStream(base64Content);
										t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);

										while_flag = false;
										break;
									}
								}
							}
						} while (while_flag);
					} else {
						// pdf_ng
						String base64Data  = "pdf_ng";
					    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
						t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);

					}



				} else {


					/*
					 * 法人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					Thread.sleep(1000);
					button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						if (driver.getPageSource().contains("エラー")) {
							logger.info("エラー");
							t_etax_jieguoBean.setYyyymmdd_count("エラー");
							t_etax_jieguoBean.setFile_name("エラー");
							return t_etax_jieguoBean;
						}

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税の中間申告について"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {


					        WebElement targetTtl = matchingElements.get(0);

					        // 找到父元素（例如 div.msg_tetsuzuki）
					        WebElement parent = targetTtl.findElement(By.xpath("ancestor::div[contains(@class, 'msg_tetsuzuki')]"));

					        // 在这个父元素下查找 date
					        WebElement dateElement = parent.findElement(By.cssSelector("div.box-info > p.date"));

					        String dateText = dateElement.getText();
					        System.out.println("日期是: " + dateText);

				            t_etax_jieguoBean.setYuukou_kigen(dateText);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}


							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);
					        try {

								matchingElements = driver.findElements(By.id("area_detail_info"));
							    if (matchingElements.size() == 1) {


						            WebElement table = matchingElements.get(0);

						            List<WebElement> rows = table.findElements(By.tagName("tr"));

						            String shunoKikanBango = "";
						            String nofuKubun = "";
						            String yukokigen = "";
						            String nofuKingaku = "";

						            for (WebElement row : rows) {
						                List<WebElement> cells = row.findElements(By.tagName("td"));
						                if (cells.size() == 2) {
						                    String key = cells.get(0).getText().trim();
						                    String value = cells.get(1).getText().trim();

						                    switch (key) {
						                        case "収納機関番号":
						                            shunoKikanBango = value;
						                            break;
						                        case "種目":
						                            nofuKubun = value;
						                            break;
						                        case "有効期限":
						                            yukokigen = value;
						                            break;
						                        case "納付金額":
						                            nofuKingaku = value;
						                            break;
						                    }
						                }
						            }


						            t_etax_jieguoBean.setNoufu_kubun(nofuKubun);

						            while_flag = false;

							    }




							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}


					    }


						++count;
						if (count > 2) {
							while_flag = false;
							return new t_etax_jieguoBean();
						}

					} while (while_flag);


					/*
					 * 受信通知（申告のお知らせ）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（申告のお知らせ）";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_oshirase_shinkoku"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {


					        try {

					        	t_etax_jieguoBean.setHtml("OK");
					        	// 滚动到元素可见
					        	((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					        	matchingElements.get(0).click();


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml())) {
						/*
						 * 課税期間分の中間申告について
						 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030203_SM23S020_oshs_E_01.do
						 */
						Title = "課税期間分の中間申告について";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("font"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("課税期間分の中間申告について"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

//									addCss(doc);
									t_etax_jieguoBean.setHtml(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }


							++count;
							if (count > 2) {
								while_flag = false;
//								return null;
							}
						} while (while_flag);


						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}

				}







			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end getShenqingJieguo");
			}
			return t_etax_jieguoBean;





	}



	public t_etax_jieguoBean getShenqingPDF(String fileName) throws InterruptedException {

		t_etax_jieguoExBean t_etax_jieguoExBean = new t_etax_jieguoExBean();
		t_etax_jieguoExBean.setUser_type("法人");
		t_etax_jieguoExBean.setBangou("2522002811930029");
		t_etax_jieguoExBean.setEtax_pw("pstax0518");


		logger.info("fileName : " + fileName);

		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

			try {
		        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver
	            JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;



	            //NG linuxご利用のパソコン環境が、e-Taxの推奨環境を満たしているか
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					String osName = System.getProperty("os.name");
					if (osName.toLowerCase().contains("linux")) {
						return t_etax_jieguoBean;
					}
				}
				/*
				 * ログイン
				 */
				//開始届出（個人の方用）　新規
				if ("个人".equals(t_etax_jieguoExBean.getUser_type())) {
					/*
					 * 個人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginIndividual");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='loginUserNumber(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

//					Thread.sleep(1000);
//					button = driver.findElement(By.id("btn_ReceiptA"));
//					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {

								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
							    if (matchingElements.size() == 1) {
							    	t_etax_jieguoBean.setHtml_qr("OK");
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
							    	matchingElements.get(0).click();
							    }


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


					    matchingElements = driver.findElements(By.id("btn_close"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {
					        try {

					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }

						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
						/*
						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
						 */
						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

									addCss(doc);
									t_etax_jieguoBean.setHtml_qr(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);

						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();

								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);




					/*
					 * 追加認証 | e-Tax
					 * https://login.e-tax.nta.go.jp/login/reception/addAuth
					 */
					set_zhuijia_renzheng(driver, JavascriptExecutor);

//
//					Thread.sleep(1000);
//				    driver = getNewWindow(driver, Title);
//
//				    // 查找所有符合条件的 <p> 元素
//				    List<WebElement> matchingElements = driver.findElements(By.cssSelector("img[src=\"/content/WP200/assets/images/icn_key_b.png\"]"));
//
//					logger.warn(4);
//
//				    // 如果匹配的元素只有一个，则点击它
//				    if (matchingElements.size() >= 1) {
//				    	logger.warn("OKOKOK");
//
//				    }




//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//
//
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//								matchingElements.get(0).click();
//
//								Thread.sleep(1000);
//								WebElement WebElement = driver.findElement(By.cssSelector("button[onclick=\"doAddCertification();\"]"));
//								JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
//
//
//
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//							}
//					    }
//
//					} while (while_flag);



					/*
					 * 登録された電子証明書と一致しません
Exception in thread "main" org.openqa.selenium.UnhandledAlertException: unexpected alert open: {Alert text : 登録された電子証明書と一致しません。（4120250129110853957）}
					 */
					while_flag = true;
					do {

						Thread.sleep(1000);
						 Alert alert = null;
					    // 切换到弹出框
			            try {
							alert = driver.switchTo().alert();
						} catch (Exception e1) {
							logger.info("处理 : 追加認証OK");
							while_flag = false;
						}


					    // 如果匹配的元素只有一个，则点击它
					    if (alert != null) {
				            // 获取弹出框文本
				            String alertText = alert.getText();

				            if (alertText.indexOf("登録された電子証明書と一致しません") > -1
				            		|| alertText.indexOf("電子証明書が登録されていません") > -1) {
								logger.warn("弹出框文本: " + alertText);
					            // 点击“确定”
					            alert.accept();


					            List<WebElement> matchingElements = driver.findElements(By.cssSelector("button[onclick='submitBack();']"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }

					            Title = "お知らせ・受信通知 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					            matchingElements = driver.findElements(By.id("btn_back"));

					            // 如果匹配的元素只有一个，则点击它
					            if (matchingElements.size() == 1) {

					            	try {
					            		// 滚动到元素可见
					            		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            		matchingElements.get(0).click();
					            	} catch (Exception e) {
					            		logger.warn(e.getMessage());
					            	}
					            }


					            Title = "TOP | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_MyPage"));

							    if (matchingElements.size() == 1) {

							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



					            Title = "マイページ | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_other"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "その他の登録情報 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

					    	    // 查找所有符合条件的 <p> 元素
							    matchingElements = driver.findElements(By.id("btn_certificationReg"));

							    if (matchingElements.size() == 1) {
							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }


					            Title = "電子証明書の登録・更新 | e-Tax";
					            logger.info("处理 : " + Title);

					            Thread.sleep(1000);
					            driver = getNewWindow(driver, Title);

							    matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));

							    // 如果匹配的元素只有一个，则点击它
							    if (matchingElements.size() == 1) {


							        try {
							            // 滚动到元素可见
							           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
										matchingElements.get(0).click();

							            // 使 div 显示
							            WebElement WebElement = driver.findElement(By.id("file_area"));
							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);

										WebElement = driver.findElement(By.id("inputEcertFileNm"));
										 // 使用 JavaScript 将内容插入到 <p> 标签中
							            JavascriptExecutor js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].innerText = arguments[1];", WebElement, p12Path);

							            WebElement inputElement = driver.findElement(By.id("txt_filpath_sig"));
							            // 使用 JavaScript 更新 value 属性
							            js = (JavascriptExecutor) driver;
							            js.executeScript("arguments[0].value = arguments[1];", inputElement, p12Path);
							            // 验证更新后的 value
							            inputElement.getAttribute("value");


										WebElement = driver.findElement(By.id("inputEcertFilePass"));
										WebElement.sendKeys("Panda0518");

										logger.warn(0);
										WebElement = driver.findElement(By.id("btn_readECert"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//							            // 使 div 显示
//							            WebElement = driver.findElement(By.id("fild04"));
//							            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnSrlNum"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "0731021E0034C2");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnHks"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "CN=0402010000001");
		//
//										WebElement = driver.findElement(By.id("oStAddAuthnGmnLimit"));
//										((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "2024/01/04 16:12:26.000～2024/07/04 23:59:59.000");

										logger.warn(1);
										Thread.sleep(3000);
										WebElement = driver.findElement(By.id("btn_updECert_sig_file"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
										logger.warn(2);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_back"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

										/*
										 *
										 */
							            Title = "マイページ | e-Tax";
							            logger.info("处理 : " + Title);

							            Thread.sleep(1000);
							            driver = getNewWindow(driver, Title);


										Thread.sleep(1000);
										WebElement = driver.findElement(By.id("btn_to_top"));
										JavascriptExecutor.executeScript("arguments[0].click();", WebElement);



							            while_flag = false;
									} catch (Exception e) {
										logger.warn(e.getMessage());
									}
							    }



				            } else {
				            	logger.error("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 弹出框文本: " + alertText);
				            }


					    }

					} while (while_flag);

					String pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("TOP | e-Tax".equals(pageTitle)) {

						/*
						 * TOP | e-Tax
						 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
						 */
						Title = "TOP | e-Tax";
						logger.info("处理 : " + Title);


						count = 0;
						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);

						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

						    if (matchingElements.size() == 1) {
//				                	// 获取匹配的第一个元素的outerHTML
//				                	String html = matchingElements.get(0).getAttribute("outerHTML");
//				                	logger.info(html);

						        try {
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
									matchingElements.get(0).click();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
						} while (while_flag);
					}


					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();


								Thread.sleep(500);
								button = driver.findElement(By.id("btn_addCertification"));
								JavascriptExecutor.executeScript("arguments[0].click();", button);

					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


					pageTitle = driver.getTitle();
					logger.info("pageTitle : " + pageTitle);
					if ("追加認証 | e-Tax".equals(pageTitle)) {
						/*
						 * 追加認証 | e-Tax
						 * https://login.e-tax.nta.go.jp/login/reception/addAuth
						 */
						set_zhuijia_renzheng(driver, JavascriptExecutor);
					}

					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }

					/*
					 * 帳票の表示
					 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
					 */
//					Title = "帳票の表示 | e-Tax";
//					logger.info("处理 : " + Title);
//
//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//					    List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					        	//すべて選択
//					        	matchingElements.get(0).click();
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					    matchingElements = driver.findElements(By.id("btn_create_chohyo"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					        	//帳票作成
//					        	matchingElements.get(0).click();
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					    matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					        	// 注入JavaScript以拦截URL.createObjectURL
//					            JavascriptExecutor.executeScript(
//					                    "var originalCreateObjectURL = URL.createObjectURL;" +
//					                    "var pdfURL = null;" +
//					                    "URL.createObjectURL = function(blob) {" +
//					                    "   pdfURL = originalCreateObjectURL(blob);" +
//					                    "   console.log('Pdf URL: ' + pdfURL);" +
//					                    "   return pdfURL;" +
//					                    "};" +
//					                    "window.getPdfURL = function() { return pdfURL; };"
//					                );
//
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					            //帳票イメージの表示を行います。よろしいですか？
//					        	matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					} while (while_flag);
//
//
//
//					String blobUrl="";
//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//						List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
//						WebElement matchingElement = null;
//
//						for (WebElement element : divElements) {
//							if (element.getText().equals("PDF作成が完了しました。")) {
//								matchingElement = element;
//					            // 获取拦截到的Blob URL
//					            blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
//								break;
//							}
//						}
//
//						if (matchingElement != null) {
//							// 查找所有符合条件的 <button> 元素
//							List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));
//
//							for (WebElement my_button : buttonElements) {
//								if (my_button.getText().equals("表示")) {
//									//表示
////									my_button.click();
//
//									String script = "return (async function() {" +
//											"  let response = await fetch('" + blobUrl + "');" +
//											"  let blob = await response.blob();" +
//											"  let reader = new FileReader();" +
//											"  return new Promise((resolve, reject) => {" +
//											"    reader.onloadend = () => resolve(reader.result);" +
//											"    reader.onerror = reject;" +
//											"    reader.readAsDataURL(blob);" +
//											"  });" +
//											"})();";
//
//									// 使用Selenium执行JavaScript
//									String base64Data  = (String) JavascriptExecutor.executeScript(script);
//									// 去除Base64数据头部的"data:application/pdf;base64,"
//									String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
//									 // 获取上传文件的输入流
//									InputStream fileContent = base64ToInputStream(base64Content);
//									t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);
//
//									while_flag = false;
//									break;
//								}
//							}
//						}
//					} while (while_flag);


					// pdf_ng
					String base64Data  = "pdf_ng";
				    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
					t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);


				} else {


					/*
					 * 法人ログイン
					 */
					driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
					if (driver.getPageSource().contains("メンテナンス中です")) {
						logger.info("国税局系统维护中");
						t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
						return t_etax_jieguoBean;
					}

					// 查找用户名和密码输入框及登录按钮
					WebElement usernameField = driver.findElement(By.id("oStUserId"));
					WebElement passwordField = driver.findElement(By.id("oStPassword"));
					// 输入用户名和密码
					usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
					passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


					WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					Thread.sleep(1000);
					button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
					JavascriptExecutor.executeScript("arguments[0].click();", button);

					/*
					 * TOP | e-Tax
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
					 */
					String Title = "TOP | e-Tax";
					logger.info("处理 : " + Title);


					int count = 0;
					boolean while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("btn_shinkokuA"));

					    if (matchingElements.size() == 1) {
//			                	// 获取匹配的第一个元素的outerHTML
//			                	String html = matchingElements.get(0).getAttribute("outerHTML");
//			                	logger.info(html);

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);



					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "申告・申請・納税サブメニュー SE00S020 国税電子申告・納税システム";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("納付情報登録依頼"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }


						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}

					} while (while_flag);


					/*
					 * 受信通知（納付区分番号通知）
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知（納付区分番号通知） | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);


					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("受信通知（納付区分番号通知）"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {
					        try {

								matchingElements = driver.findElements(By.id("btn_nouhu_sp_apps"));
							    if (matchingElements.size() == 1) {
							    	t_etax_jieguoBean.setHtml_qr("OK");
						            // 滚动到元素可见
						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
							    	matchingElements.get(0).click();
							    }


					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
								logger.warn(e.getMessage());
							}
					    }


					    matchingElements = driver.findElements(By.id("btn_close"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {
					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					            matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }

						++count;
						if (count > 2) {
							while_flag = false;
//							return null;
						}
					} while (while_flag);

					if ("OK".equals(t_etax_jieguoBean.getHtml_qr())) {
						/*
						 * 国税電子申告・納税システム－SU00SF10 スマホアプリ納付
						 * https://uketsuke.e-tax.nta.go.jp/UF_APP/lnk/SpAppNofuGmnViewPage
						 */
						Title = "国税電子申告・納税システム－SU00SF10 スマホアプリ納付";
						logger.info("处理 : " + Title);

						while_flag = true;
						do {
							Thread.sleep(1000);
						    driver = getNewWindow(driver, Title);


						    // 查找所有符合条件的 <p> 元素
						    List<WebElement> matchingElements = driver.findElements(By.cssSelector("h1"));


						    matchingElements = matchingElements.stream()
						        .filter(element -> element.getText().contains("スマホアプリ納付用ＱＲコード表示"))
						        .collect(Collectors.toList());

						    // 如果匹配的元素只有一个，则点击它
						    if (matchingElements.size() >= 1) {
						        try {

									Document doc = Jsoup.parse(driver.getPageSource());
							        doc.select("input[type=button]").remove();

									addCss(doc);
									t_etax_jieguoBean.setHtml_qr(doc.html());

									driver.close();
						            while_flag = false;
								} catch (Exception e) {
									logger.warn(e.getMessage());
								}
						    }
							++count;
							if (count > 2) {
								while_flag = false;
//								return null;
							}
						} while (while_flag);


						//受信通知（納付区分番号通知）
					    driver = getNewWindow(driver, Title);
						driver.close();

					}

					/*
					 * お知らせ・受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
					 */
					Title = "お知らせ・受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					count = 0;
					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    // 查找所有符合条件的 <p> 元素
					    List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

					    matchingElements = matchingElements.stream()
					        .filter(element -> element.getText().contains("消費税及び地方消費税申告"))
					        .collect(Collectors.toList());

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() >= 1) {

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
					            while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
						++count;
						if (count > 2) {
							logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count() + " 消費税及び地方消費税申告 : null");
							while_flag = false;
							return null;
						}
					} while (while_flag);



					/*
					 * 受信通知
					 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
					 */
					Title = "受信通知 | e-Tax";
					logger.info("处理 : " + Title);

					while_flag = true;
					do {
						Thread.sleep(1000);
					    driver = getNewWindow(driver, Title);

						String pageTitle = driver.getTitle();
						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//							return null;
//						}

					    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

					    // 如果匹配的元素只有一个，则点击它
					    if (matchingElements.size() == 1) {

							Document doc = Jsoup.parse(driver.getPageSource());
							doc.getElementById("area_service").remove();
							doc.getElementById("area_info").remove();

							doc.select("button").remove();
							doc.select("img").remove();

							doc = Jsoup.parse(doc.getElementById("globalMain").html());

							addCss(doc);
							t_etax_jieguoBean.setHtml(doc.html());

					        try {
					            // 滚动到元素可见
					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
								matchingElements.get(0).click();
								while_flag = false;
							} catch (Exception e) {
								logger.warn(e.getMessage());
							}
					    }
					} while (while_flag);


//			            List<WebElement> buttons = driver.findElements(By.id("btn_chohyo_hyouji"));
//			            JavascriptExecutor.executeScript("arguments[0].click();", buttons.get(0));
//			            if (buttons.isEmpty()) {
//			                logger.info("按钮不存在。");
//			            } else {
//			                logger.info("按钮存在。");
//			            }

					/*
					 * 帳票の表示
					 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
					 */
//					Title = "帳票の表示 | e-Tax";
//					logger.info("处理 : " + Title);
//
//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//					    driver = getNewWindow(driver, Title);
//
//						String pageTitle = driver.getTitle();
//						logger.info("pageTitle : " + pageTitle);
//						if (!Title.equals(pageTitle)) {
//						    List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));
//						    // 如果匹配的元素只有一个，则点击它
//						    if (matchingElements.size() == 1) {
//						        try {
//						            // 滚动到元素可见
//						           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//									matchingElements.get(0).click();
////									while_flag = false;
//								} catch (Exception e) {
//									logger.warn(e.getMessage());
//								}
//						    }
//						}
//
//						Thread.sleep(500);
//					    List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					        	//すべて選択
//					        	matchingElements.get(0).click();
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					    matchingElements = driver.findElements(By.id("btn_create_chohyo"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					        	//帳票作成
//					        	matchingElements.get(0).click();
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					    matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));
//
//					    // 如果匹配的元素只有一个，则点击它
//					    if (matchingElements.size() == 1) {
//					        try {
//					        	// 注入JavaScript以拦截URL.createObjectURL
//					            JavascriptExecutor.executeScript(
//					                    "var originalCreateObjectURL = URL.createObjectURL;" +
//					                    "var pdfURL = null;" +
//					                    "URL.createObjectURL = function(blob) {" +
//					                    "   pdfURL = originalCreateObjectURL(blob);" +
//					                    "   console.log('Pdf URL: ' + pdfURL);" +
//					                    "   return pdfURL;" +
//					                    "};" +
//					                    "window.getPdfURL = function() { return pdfURL; };"
//					                );
//
//					            // 滚动到元素可见
//					           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//					            //帳票イメージの表示を行います。よろしいですか？
//					        	matchingElements.get(0).click();
//					            while_flag = false;
//							} catch (Exception e) {
//								logger.warn(e.getMessage());
//								continue;
//							}
//					    }
//
//					} while (while_flag);
//
//
//
//					String blobUrl="";
//					while_flag = true;
//					do {
//						Thread.sleep(1000);
//						List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
//						WebElement matchingElement = null;
//
//						for (WebElement element : divElements) {
//							if (element.getText().equals("PDF作成が完了しました。")) {
//								logger.info("处理 : PDF作成が完了しました。");
//								matchingElement = element;
//					            // 获取拦截到的Blob URL
//					            blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
//								break;
//							}
//						}
//
//						if (matchingElement != null) {
//							// 查找所有符合条件的 <button> 元素
//							List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));
//
//							for (WebElement my_button : buttonElements) {
//								if (my_button.getText().equals("表示")) {
//									//表示
////									my_button.click();
//
//									String script = "return (async function() {" +
//											"  let response = await fetch('" + blobUrl + "');" +
//											"  let blob = await response.blob();" +
//											"  let reader = new FileReader();" +
//											"  return new Promise((resolve, reject) => {" +
//											"    reader.onloadend = () => resolve(reader.result);" +
//											"    reader.onerror = reject;" +
//											"    reader.readAsDataURL(blob);" +
//											"  });" +
//											"})();";
//
//									// 使用Selenium执行JavaScript
//									String base64Data  = (String) JavascriptExecutor.executeScript(script);
//									// 去除Base64数据头部的"data:application/pdf;base64,"
//									String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
//									 // 获取上传文件的输入流
//									InputStream fileContent = base64ToInputStream(base64Content);
//									t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);
//
//									while_flag = false;
//									break;
//								}
//							}
//						}
//					} while (while_flag);


					// pdf_ng
					String base64Data  = "pdf_ng";
				    InputStream pdf_xiaofeishui_shengaoshu = new ByteArrayInputStream(base64Data.getBytes(StandardCharsets.UTF_8));
					t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(pdf_xiaofeishui_shengaoshu);

				}







			} catch (Exception e) {
				throw e;
			} finally {
	            quitDriver();
				logger.info("end getShenqingJieguo");
			}
			return t_etax_jieguoBean;





	}



	private void set_zhuijia_renzheng(WebDriver driver, JavascriptExecutor JavascriptExecutor) throws InterruptedException {
		String Title;
		boolean while_flag;
		Title = "追加認証 | e-Tax";
		logger.info("处理 : " + Title);

		while_flag = true;
		do {
			Thread.sleep(1000);
		    driver = getNewWindow(driver, Title);

		    List<WebElement> matchingElements = driver.findElements(By.cssSelector("input[value='電子証明書ファイル']"));

		    // 如果匹配的元素只有一个，则点击它
		    if (matchingElements.size() == 1) {


		        try {
		            // 滚动到元素可见
		           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
					matchingElements.get(0).click();

		            // 使 div 显示
		            WebElement WebElement = driver.findElement(By.id("fild03"));
		            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);

					WebElement = driver.findElement(By.id("oStEcertFileSel"));        // 使用 JavaScript 移除 disabled 属性
		            ((JavascriptExecutor) driver).executeScript("arguments[0].removeAttribute('disabled');", WebElement);
					WebElement.sendKeys(p12Path);

					WebElement = driver.findElement(By.id("inputEcertPwd"));
					WebElement.sendKeys("Panda0518");

					logger.warn(0);
					WebElement = driver.findElement(By.cssSelector("a[onclick=\"doFileRead()\"]"));
					JavascriptExecutor.executeScript("arguments[0].click();", WebElement);

//					            // 使 div 显示
//					            WebElement = driver.findElement(By.id("fild04"));
//					            ((JavascriptExecutor) driver).executeScript("arguments[0].style.display='block';", WebElement);
//
//								WebElement = driver.findElement(By.id("oStAddAuthnGmnSrlNum"));
//								((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "0731021E0034C2");
//
//								WebElement = driver.findElement(By.id("oStAddAuthnGmnHks"));
//								((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "CN=0402010000001");
//
//								WebElement = driver.findElement(By.id("oStAddAuthnGmnLimit"));
//								((JavascriptExecutor) driver).executeScript("arguments[0].innerHTML = arguments[0].innerHTML + arguments[1];", WebElement, "2024/01/04 16:12:26.000～2024/07/04 23:59:59.000");

					logger.warn(1);
					Thread.sleep(3000);
					WebElement = driver.findElement(By.cssSelector("button[onclick=\"doAddCertification();\"]"));
					JavascriptExecutor.executeScript("arguments[0].click();", WebElement);
					logger.warn(2);


		            while_flag = false;
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
		    }


			String pageTitle = driver.getTitle();
			logger.info("pageTitle : " + pageTitle);
			if ("追加認証 | e-Tax".equals(pageTitle)) {
				/*
				 * 追加認証 | e-Tax
				 * https://login.e-tax.nta.go.jp/login/reception/addAuth
				 */

			} else {
	            while_flag = false;
			}

		} while (while_flag);



		logger.warn(3);
	}


	public t_etax_jieguoBean get_zhuandaili_jieguo(t_etax_jieguoExBean t_etax_jieguoExBean) throws InterruptedException {

		logger.info("yyyymmdd_count : " + t_etax_jieguoExBean.getYyyymmdd_count());
		t_etax_jieguoBean t_etax_jieguoBean = new t_etax_jieguoBean();

		try {
	        WebDriver driver = getDriver(); // 获取当前线程的 WebDriver
			JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;

			/*
			 * 法人ログイン
			 */
			driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");
			if (driver.getPageSource().contains("メンテナンス中です")) {
				logger.info("国税局系统维护中");
				t_etax_jieguoBean.setYyyymmdd_count("国税局系统维护中");
				return t_etax_jieguoBean;
			}

			// 查找用户名和密码输入框及登录按钮
			WebElement usernameField = driver.findElement(By.id("oStUserId"));
			WebElement passwordField = driver.findElement(By.id("oStPassword"));
			// 输入用户名和密码
			usernameField.sendKeys(t_etax_jieguoExBean.getBangou());
			passwordField.sendKeys(t_etax_jieguoExBean.getEtax_pw());


			WebElement button = driver.findElement(By.cssSelector("button[onclick='houjinCheckEvn(); return false;']"));
			JavascriptExecutor.executeScript("arguments[0].click();", button);

			Thread.sleep(1000);
			button = driver.findElement(By.cssSelector("a[onclick='houjinLoginNext(); return false;']"));
			JavascriptExecutor.executeScript("arguments[0].click();", button);

			/*
			 * TOP | e-Tax
			 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20010/BL2001001_top.do
			 */
			String Title = "TOP | e-Tax";
			logger.info("处理 : " + Title);


			int count = 0;
			boolean while_flag = true;
			do {
				Thread.sleep(1000);
				driver = getNewWindow(driver, Title);

				// 查找所有符合条件的 <p> 元素
				List<WebElement> matchingElements = driver.findElements(By.id("btn_ReceiptA"));

				if (matchingElements.size() == 1) {
					try {
			            // 滚动到元素可见
			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						matchingElements.get(0).click();
						while_flag = false;
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				}
			} while (while_flag);



			/*
			 * お知らせ・受信通知
			 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20301/BL2030101.do
			 */
			Title = "お知らせ・受信通知 | e-Tax";
			logger.info("处理 : " + Title);

			count = 0;
			while_flag = true;
			do {
				Thread.sleep(1000);
				driver = getNewWindow(driver, Title);

				// 查找所有符合条件的 <p> 元素
				List<WebElement> matchingElements = driver.findElements(By.cssSelector("p.ttl"));

				matchingElements = matchingElements.stream()
						.filter(element -> element.getText().contains("納税管理人の届出"))
						.collect(Collectors.toList());

				// 如果匹配的元素只有一个，则点击它
				if (matchingElements.size() >= 1) {
					try {
			            // 滚动到元素可见
			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						matchingElements.get(0).click();
						while_flag = false;
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				}


				++count;
				if (count > 2) {
					while_flag = false;
					return null;
				}

			} while (while_flag);


			/*
			 * 受信通知（納付区分番号通知）
			 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
			 */
			Title = "受信通知（納付区分番号通知） | e-Tax";
			logger.info("处理 : " + Title);

			while_flag = true;
			do {
				Thread.sleep(1000);
				driver = getNewWindow(driver, Title);


				// 查找所有符合条件的 <p> 元素
				List<WebElement> matchingElements = driver.findElements(By.id("lbl_title"));

				matchingElements = matchingElements.stream()
						.filter(element -> element.getText().contains("受信通知"))
						.collect(Collectors.toList());

				// 如果匹配的元素只有一个，则点击它
				if (matchingElements.size() >= 1) {
					try {



						Document doc = Jsoup.parse(driver.getPageSource());
						doc.getElementById("area_service").remove();
						doc.getElementById("area_info").remove();

						doc.select("button").remove();
						doc.select("img").remove();

						doc = Jsoup.parse(doc.getElementById("globalMain").html());
						addCss(doc);
						t_etax_jieguoBean.setHtml(doc.html());

						while_flag = false;
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				}

			} while (while_flag);

			/*
			 * 受信通知
			 * https://mypage.e-tax.nta.go.jp/UF_MYP/WP200/FCSM20302/BL2030201.do
			 */
			Title = "受信通知 | e-Tax";
			logger.info("处理 : " + Title);

			while_flag = true;
			do {
				Thread.sleep(1000);
				driver = getNewWindow(driver, Title);

				List<WebElement> matchingElements = driver.findElements(By.id("btn_chohyo_hyouji"));

				// 如果匹配的元素只有一个，则点击它
				if (matchingElements.size() == 1) {
					try {
			            // 滚动到元素可见
			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
						matchingElements.get(0).click();
						while_flag = false;
					} catch (Exception e) {
						logger.warn(e.getMessage());
					}
				}
			} while (while_flag);

			/*
			 * 帳票の表示
			 * https://clientweb.e-tax.nta.go.jp/UF_WEB/WP200/FCSE20010/BL2001002_0015.do
			 */
//			Title = "帳票の表示 | e-Tax";
//			logger.info("处理 : " + Title);
//
//			while_flag = true;
//			do {
//				Thread.sleep(1000);
//				driver = getNewWindow(driver, Title);
//
//				List<WebElement> matchingElements = driver.findElements(By.id("btn_select_all"));
//
//				// 如果匹配的元素只有一个，则点击它
//				if (matchingElements.size() == 1) {
//					try {
//			            // 滚动到元素可见
//			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//						//すべて選択
//						matchingElements.get(0).click();
//					} catch (Exception e) {
//						logger.warn(e.getMessage());
//						continue;
//					}
//				}
//
//				matchingElements = driver.findElements(By.id("btn_create_chohyo"));
//
//				// 如果匹配的元素只有一个，则点击它
//				if (matchingElements.size() == 1) {
//					try {
//			            // 滚动到元素可见
//			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//						//帳票作成
//						matchingElements.get(0).click();
//					} catch (Exception e) {
//						logger.warn(e.getMessage());
//						continue;
//					}
//				}
//
//				matchingElements = driver.findElements(By.cssSelector("a[class='btn w-100 uni-modal--close uni-modal--1stbtn']"));
//
//				// 如果匹配的元素只有一个，则点击它
//				if (matchingElements.size() == 1) {
//					try {
//						// 注入JavaScript以拦截URL.createObjectURL
//						JavascriptExecutor.executeScript(
//								"var originalCreateObjectURL = URL.createObjectURL;" +
//										"var pdfURL = null;" +
//										"URL.createObjectURL = function(blob) {" +
//										"   pdfURL = originalCreateObjectURL(blob);" +
//										"   console.log('Pdf URL: ' + pdfURL);" +
//										"   return pdfURL;" +
//										"};" +
//										"window.getPdfURL = function() { return pdfURL; };"
//								);
//
//			            // 滚动到元素可见
//			           ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", matchingElements.get(0));Thread.sleep(500);
//						//帳票イメージの表示を行います。よろしいですか？
//						matchingElements.get(0).click();
//						while_flag = false;
//					} catch (Exception e) {
//						logger.warn(e.getMessage());
//						continue;
//					}
//				}
//
//			} while (while_flag);
//
//
//
//			String blobUrl="";
//			while_flag = true;
//			do {
//				Thread.sleep(1000);
//				List<WebElement> divElements = driver.findElements(By.cssSelector("#report-creator div"));
//				WebElement matchingElement = null;
//
//				for (WebElement element : divElements) {
//					if (element.getText().equals("PDF作成が完了しました。")) {
//						matchingElement = element;
//						// 获取拦截到的Blob URL
//						blobUrl = (String) JavascriptExecutor.executeScript("return window.getPdfURL();");
//						break;
//					}
//				}
//
//				if (matchingElement != null) {
//					// 查找所有符合条件的 <button> 元素
//					List<WebElement> buttonElements = driver.findElements(By.cssSelector("#report-creator button"));
//
//					for (WebElement my_button : buttonElements) {
//						if (my_button.getText().equals("表示")) {
//							//表示
////								my_button.click();
//
//							String script = "return (async function() {" +
//									"  let response = await fetch('" + blobUrl + "');" +
//									"  let blob = await response.blob();" +
//									"  let reader = new FileReader();" +
//									"  return new Promise((resolve, reject) => {" +
//									"    reader.onloadend = () => resolve(reader.result);" +
//									"    reader.onerror = reject;" +
//									"    reader.readAsDataURL(blob);" +
//									"  });" +
//									"})();";
//
//							// 使用Selenium执行JavaScript
//							String base64Data  = (String) JavascriptExecutor.executeScript(script);
//							// 去除Base64数据头部的"data:application/pdf;base64,"
//							String base64Content = base64Data.substring(base64Data.indexOf(",") + 1);
//							// 获取上传文件的输入流
//							InputStream fileContent = base64ToInputStream(base64Content);
//							t_etax_jieguoBean.setPdf_xiaofeishui_shengaoshu(fileContent);
//
//							while_flag = false;
//							break;
//						}
//					}
//				}
//			} while (while_flag);





			/*
			 * 帳票
			 * blob:https://clientweb.e-tax.nta.go.jp/f571a630-3a88-4068-a58c-9affd39f19da
			 */
//				Thread.sleep(1000);
//				Title = "帳票の表示";
//				logger.info("处理 : " + Title);
//				Title = "";
//				driver = getNewWindow(driver, Title);
//				driver.navigate().refresh();





//		            String pageSource = driver.getPageSource();
//		            logger.info(pageSource);


		} catch (Exception e) {
			throw e;
		} finally {
            quitDriver();
			logger.info("end get_zhuandaili_jieguo");
		}
		return t_etax_jieguoBean;






	}



	private void addCss(Document doc) {
		// 创建新的<link>元素
		Element link1 = doc.createElement("link");
		link1.attr("rel", "stylesheet");
		link1.attr("href", "https://mypage.e-tax.nta.go.jp/content/WP200/assets/css/lib/bootstrap.min.css");
		link1.attr("type", "text/css");

		Element link2 = doc.createElement("link");
		link2.attr("rel", "stylesheet");
		link2.attr("href", "https://mypage.e-tax.nta.go.jp/content/WP200/assets/css/style.css");
		link2.attr("type", "text/css");

        Element metaCharset = doc.selectFirst("meta[charset=UTF-8]");
        if (metaCharset != null) {
            metaCharset.before(link1);
            metaCharset.before(link2);
        } else {
            metaCharset = doc.createElement("meta");
            metaCharset.attr("charset", "utf-8");
            doc.head().appendChild(metaCharset);

            doc.head().appendChild(link1);
            doc.head().appendChild(link2);


            // 添加新的<link>标签
            doc.head().append("<link href=\"https://uketsuke.e-tax.nta.go.jp/css/UF_common.css\" type=\"text/css\" rel=\"stylesheet\"></link>");
            doc.head().append("<link href=\"https://uketsuke.e-tax.nta.go.jp/css/UF_qrcodecreate.css\" type=\"text/css\" rel=\"stylesheet\"></link>");



        }
	}


    private static InputStream base64ToInputStream(String base64Content) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        return new ByteArrayInputStream(decodedBytes);
    }


	private static WebDriver getNewWindow(WebDriver driver, String Title) throws InterruptedException {


		  // 获取所有窗口句柄
          Set<String> windowHandles = driver.getWindowHandles();
          List<String> windowHandleList = new ArrayList<>(windowHandles);

          // 获取最后一个窗口句柄
          String lastWindowHandle = windowHandleList.get(windowHandleList.size() - 1);

          // 切换到最后一个窗口
          driver.switchTo().window(lastWindowHandle);

          // 执行其他操作，例如获取当前窗口的标题
//          String currentTitle = driver.getTitle();
//          logger.info("Current window title: " + currentTitle);


		return driver;
	}
	private static WebDriver getNewWindow_do(WebDriver driver, String Title) throws InterruptedException {

        boolean while_flag = true;
        do {
        	Thread.sleep(2000);
  		  // 获取所有窗口句柄
            Set<String> windowHandles = driver.getWindowHandles();
            List<String> windowHandleList = new ArrayList<>(windowHandles);

            // 获取最后一个窗口句柄
            String lastWindowHandle = windowHandleList.get(windowHandleList.size() - 1);

            // 切换到最后一个窗口
            driver.switchTo().window(lastWindowHandle);

            // 执行其他操作，例如获取当前窗口的标题
            String currentTitle = driver.getTitle();
            logger.info("Current window title: " + currentTitle);

            if (Title.equals(currentTitle)) {
            	 while_flag = false;
            }

        } while (while_flag);

//        Thread.sleep(2000);

		return driver;
	}






//	@Override
//	public void beforeAlertAccept(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterAlertAccept(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterAlertDismiss(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeAlertDismiss(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeNavigateTo(String url, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterNavigateTo(String url, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeNavigateBack(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterNavigateBack(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeNavigateForward(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterNavigateForward(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeNavigateRefresh(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterNavigateRefresh(WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeFindBy(By by, WebElement element, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterFindBy(By by, WebElement element, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeClickOn(WebElement element, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterClickOn(WebElement element, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeScript(String script, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterScript(String script, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeSwitchToWindow(String windowName, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterSwitchToWindow(String windowName, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public <X> void beforeGetScreenshotAs(OutputType<X> target) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void beforeGetText(WebElement element, WebDriver driver) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	public void afterGetText(WebElement element, WebDriver driver, String text) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}



	/*


SELECT CONCAT(
    'INSERT INTO t_etax_account (yyyymmdd_count, html_id, html_value, register, UPDATE_DATE) VALUES (\'',
    t_etax_account_info.yyyymmdd_count, '\', \'gShoKugyo\', \'小売業\', 0, NOW());'
) AS insert_statement
FROM t_etax_account_info
WHERE t_etax_account_info.yyyymmdd_count NOT IN (
    SELECT yyyymmdd_count
    FROM t_etax_account
    WHERE html_id = 'gShoKugyo'
);



	 */
}
