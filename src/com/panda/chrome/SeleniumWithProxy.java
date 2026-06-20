package com.panda.chrome;



import org.apache.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;

public class SeleniumWithProxy {


	private static Logger logger = Logger.getLogger(SeleniumWithProxy.class.toString());

    public static void main(String[] args) {





        // 启动BrowserMob Proxy
        BrowserMobProxy proxy = new BrowserMobProxyServer();
        proxy.start(0);

        // 获取Selenium使用的代理设置
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // 配置Chrome选项
        ChromeOptions options = new ChromeOptions();
        options.setProxy(seleniumProxy);

//        options.proxyServer(new ProxyServer());

    	String ChromeDriverPath = "";
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows")) {
			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver.exe";
//			ChromeDriverPath = "chromedriver.exe";
		} else if (osName.toLowerCase().contains("linux")) {
			ChromeDriverPath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/chromedriver";


	        options.addArguments("--headless"); // 如果你想以无头模式运行
			options.setBinary("/usr/bin/google-chrome");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");

		}

        // 设置ChromeDriver路径
        System.setProperty("webdriver.chrome.driver", ChromeDriverPath);


        // 启动WebDriver
        WebDriver driver = new ChromeDriver(options);

        // 导航到目标网站
        driver.get("https://login.e-tax.nta.go.jp/login/reception/loginCorporate");

        // 捕获HTTP请求和响应
        proxy.newHar("Host");


        // 获取HAR数据
        Har har = proxy.getHar();

        // 处理和分析HAR数据
        for (HarEntry entry : har.getLog().getEntries()) {
            logger.info(entry.getRequest().getUrl());
            logger.info(entry.getResponse().getStatus());
        }

        // 关闭资源
        driver.quit();
        proxy.stop();
    }
}
