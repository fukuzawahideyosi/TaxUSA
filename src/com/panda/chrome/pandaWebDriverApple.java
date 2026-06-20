package com.panda.chrome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
public class pandaWebDriverApple {

	private static Logger logger = Logger.getLogger(pandaWebDriverApple.class.toString());

	String ChromeDriverPath = "";
	 public static WebDriver driver;

	public pandaWebDriverApple() {

        // 设置ChromeOptions以启用无头模式
        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--js-flags=--max-old-space-size=1024"); // 设置最大堆内存为1GB
//        options.addArguments("--remote-allow-origins=*"); // 允许远程调试
//   	 options.addArguments("--remote-debugging-port=9222"); // 启用远程调试端口



		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().contains("windows")) {
			ChromeDriverPath = "E:\\Users\\Administrator\\git\\PandaServiceMA\\PandaServiceMA\\WebContent\\WEB-INF\\lib\\chromedriver.exe";


	        // 添加扩展程序
//	        options.addExtensions(new File("E:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\PandaServiceMA\\fileDataTools\\eatxChrom\\3.0.3.0_0.crx"));

//	        options.addArguments("--headless"); // 如果你想以无头模式运行

		} else if (osName.toLowerCase().contains("linux")) {
			ChromeDriverPath = "/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/chromedriver";


	        options.addArguments("--headless"); // 如果你想以无头模式运行
			options.setBinary("/usr/bin/google-chrome");
			options.addArguments("--no-sandbox");
			options.addArguments("--disable-dev-shm-usage");

	        // 添加扩展程序
	        options.addExtensions(new File("/usr/local/tomcat/apache-tomcat-9.0.62/webapps/PandaServiceMA/WEB-INF/lib/3.0.3.0_0.crx"));


		}

		options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");


        // 设置ChromeDriver路径
        System.setProperty("webdriver.chrome.driver", ChromeDriverPath);



        driver = new ChromeDriver(options);




	}







    // ... 其他方法




    public static void main(String[] args) {


        try {

        	pandaWebDriverApple testNoWEB = new pandaWebDriverApple();
//        	testNoWEB.getEtaxNo("20240612000864");
//        	testNoWEB.getOpenAI("20240612000864");
        	testNoWEB.getOpenAI_apple("20240612000864");



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

	public String getOpenAI_apple(String yyyymmdd_count) throws Exception {
		logger.info("yyyymmdd_count : " + yyyymmdd_count);
		try {

//			EventFiringWebDriver driver = new EventFiringWebDriver(driver0);
//			WindowOpenListener listener = new WindowOpenListener();
//			driver.register(listener);

			JavascriptExecutor JavascriptExecutor = (JavascriptExecutor) driver;
			/*
			 *
			 */


//			driver.get("https://chatgpt.com/c/d7f1345d-788f-40e8-8b6b-1e00bc409e43");
			driver.get("https://www.apple.com/jp/shop/buy-iphone/iphone-15");






			int count = 0;
			boolean while_flag = true;




		    // モデル。 あなたにぴったりなモデルは？
		    List<WebElement> matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));
		    matchingElements.get(0).click();

		    matchingElements = driver.findElements(By.cssSelector("img[class=\"colornav-swatch\"]"));
		    matchingElements.get(0).click();

		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));
		    matchingElements.get(3).click();


			WebElement WebElement = driver.findElement(By.id("noTradeIn"));
			JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rf-po-bfe-purchasegroupoption-container rf-po-bfe-purchasegroupoption-2\"]"));
//		    matchingElements.get(0).click();


		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rf-po-bfe-dimension-base rf-po-bfe-dimension-base-size-2\"]"));
//		    matchingElements.get(0).click();


//		    matchingElements = driver.findElements(By.cssSelector("div[class=\"form-selector column large-4 small-12 rf-accessory-applecare-fullwidth-option\"]"));
		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-multiple form-selector-threeline column large-6 small-12 form-selector\"]"));
//		    matchingElements.get(1).click();

		    Thread.sleep(500);
		    matchingElements = driver.findElements(By.cssSelector("span[class=\"rf-po-bfe-dimension-base-title\"]"));
		    matchingElements = matchingElements.stream()
		    		.filter(element -> element.getText().contains("一括払いまたはそのほかの支払い方法"))
			        .collect(Collectors.toList());
		    if (matchingElements.size() >= 1) {
		        try {
					matchingElements.get(0).click();
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
		    }

		    Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("span[class=\"form-selector-title\"]"));
		    matchingElements = matchingElements.stream()
			        .filter(element -> element.getText().contains("あとで通信キャリアと接続する"))
			        .collect(Collectors.toList());
		    if (matchingElements.size() >= 1) {
		        try {
					matchingElements.get(0).click();
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}
		    }

		    matchingElements = driver.findElements(By.cssSelector("div[class=\"form-selector column large-4 small-12 rf-accessory-applecare-fullwidth-option\"]"));
//		    matchingElements.get(3).click();
//		    matchingElements.get(3).getAttribute("outerHTML");



			/*
			 */
			String Title = "";
			logger.info("处理 : " + Title);
			count = 0;
			while_flag = true;
			do {
		        try {
				    Thread.sleep(500);
				    matchingElements = driver.findElements(By.cssSelector("span[class=\"form-selector-title\"]"));
				    matchingElements = matchingElements.stream()
				        .filter(element -> element.getText().contains("AppleCare+による保証なし"))
				        .collect(Collectors.toList());
				    if (matchingElements.size() >= 1) {
				    	matchingElements.get(0).click();
						while_flag = false;
				    }

					++count;
					if (count > 6) {
						while_flag = false;
						return null;
					}
				} catch (Exception e) {
					logger.warn(e.getMessage());
				}


			} while (while_flag);





//		    List<WebElement> matchingElementsAll = driver.findElements(By.cssSelector("span[class=\"form-selector-title\"]"));
//		    List<WebElement> matchingElements_body = driver.findElements(By.cssSelector("body"));




//		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));

//		    matchingElements = driver.findElements(By.cssSelector("span"));
//			Thread.sleep(1000);
//		    matchingElements = matchingElements.stream()
//		        .filter(element -> element.getText().contains("一括払いまたはそのほかの支払い方法"))
//		        .collect(Collectors.toList());
//
//		    // 如果匹配的元素只有一个，则点击它
//		    if (matchingElements.size() >= 1) {
//		        try {
//					matchingElements.get(0).click();matchingElements.get(0).getAttribute("outerHTML");
//				} catch (Exception e) {
//					logger.warn(e.getMessage());
//				}
//		    }




//		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));

//			Thread.sleep(1000);
//		    matchingElements = driver.findElements(By.cssSelector("span"));
//		    matchingElements = matchingElements.stream()
//		        .filter(element -> element.getText().contains("あとで通信キャリアと接続する"))
//		        .collect(Collectors.toList());
//
//		    // 如果匹配的元素只有一个，则点击它
//		    if (matchingElements.size() >= 1) {
//		        try {
//					matchingElements.get(0).click();
//				} catch (Exception e) {
//					logger.warn(e.getMessage());
//				}
//		    }

//		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));
//		    matchingElements = matchingElements.stream()
//		        .filter(element -> element.getText().contains("AppleCare+による保証なし"))
//		        .collect(Collectors.toList());
//
//		    // 如果匹配的元素只有一个，则点击它
//		    if (matchingElements.size() >= 1) {
//		        try {
//					matchingElements.get(0).click();
//				} catch (Exception e) {
//					logger.warn(e.getMessage());
//				}
//		    }


			Thread.sleep(1500);
		    matchingElements = driver.findElements(By.cssSelector("button[data-analytics-title=\"バッグに追加\"]"));
		    matchingElements.get(0).click();


			Thread.sleep(1000);

			/*
			 */
			Title = "";
			logger.info("处理 : " + Title);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"proceed\"]"));
		    matchingElements.get(0).click();




			Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"checkout\"]"));
		    matchingElements.get(0).click();



			Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"guest-checkout-btn\"]"));
		    matchingElements.get(0).click();

			Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"fulfillment-continue-button\"]"));
		    matchingElements.get(0).click();

/*
 *
 *
鼎信株式会社
TEISHIN Co.,Ltd
ヨウ ギョウxイ
楊 曉明
会長
〒171-0044 東京都豊島区千早2-31-1
TEL:03-5926-8163FAX:03-5926-8164
携带:090-3336-2498
E-mail:teishin8888@gmail.com
http://www.teishinltd.co.jp
 *
 *
 */





			Thread.sleep(3000);
            List<WebElement> elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.lastName"));
            if (!elements.isEmpty()) {
                elements.get(0).sendKeys("yang");

            }

            elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.firstName"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("xiaoming");
            }


            elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.postalCode"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("171-0044");
            }

            elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.state"));
            if (!elements.isEmpty()) {
                Select select = new Select(elements.get(0));
                // 方法一：按可见文本选择
                select.selectByVisibleText("東京都");

                // 方法二：按值选择
                 select.selectByValue("東京都");

                // 方法三：按索引选择
                // select.selectByIndex(1);

                // 检查是否成功选择
                WebElement selectedOption = select.getFirstSelectedOption();
            }





            elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.city"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("豊島区千早");
            }

            elements = driver.findElements(By.id("checkout.shipping.addressSelector.newAddress.address.street"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("2-31-1");
            }

            elements = driver.findElements(By.id("checkout.shipping.addressContactEmail.address.emailAddress"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("teishin8888@gmail.com");
            }

            elements = driver.findElements(By.id("checkout.shipping.addressContactPhone.address.mobilePhone"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("09033362498");
            }




			Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"shipping-continue-button\"]"));
		    matchingElements.get(0).click();


			Thread.sleep(1500);
		    matchingElements = driver.findElements(By.cssSelector("div[class=\"form-selector\"]"));
		    matchingElements.get(0).click();

/*
 *
4599055301152883
09/28
880
 *
 */

			Thread.sleep(1000);
            elements = driver.findElements(By.id("checkout.billing.billingOptions.selectedBillingOptions.creditCard.cardInputs.cardInput-0.cardNumber"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("4599055301152883");
            }

            elements = driver.findElements(By.id("checkout.billing.billingOptions.selectedBillingOptions.creditCard.cardInputs.cardInput-0.expiration"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("09/28");
            }

            elements = driver.findElements(By.id("checkout.billing.billingOptions.selectedBillingOptions.creditCard.cardInputs.cardInput-0.securityCode"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("880");
            }


		} catch (Exception e) {
			throw e;
		} finally {
			// 关闭WebDriver
			if (driver != null) {
				driver.quit();
			}
			logger.info("end getOpenAI");
		}
		return "";

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

}
