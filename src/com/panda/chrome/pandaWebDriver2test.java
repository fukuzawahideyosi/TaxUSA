package com.panda.chrome;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
public class pandaWebDriver2test {

	private static Logger logger = Logger.getLogger(pandaWebDriver2test.class.toString());

	String ChromeDriverPath = "";
	 public static WebDriver driver;

	public pandaWebDriver2test() {

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

        	pandaWebDriver2test testNoWEB = new pandaWebDriver2test();
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

			String Title;
			String cssSelector = "";
			String contains = "";

			/*
iPhone 16シリーズの発売日
Apple新製品発表イベント	2024年9月11日（水）（日本時間午前2時）
予約開始	9月13日（金）午後9時
発売開始	9月20日（金）
			 */


			driver.get("https://www.apple.com/jp/shop/buy-iphone/iphone-15");
//			driver.get("https://www.apple.com/jp/shop/buy-iphone/iphone-14");





		    // モデル。 あなたにぴったりなモデルは？
		    List<WebElement> matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));
		    matchingElements.get(0).click();

		    matchingElements = driver.findElements(By.cssSelector("img[class=\"colornav-swatch\"]"));
		    matchingElements.get(0).click();

		    matchingElements = driver.findElements(By.cssSelector("div[class=\"rc-dimension-selector-row form-selector\"]"));
		    matchingElements.get(3).click();


			WebElement WebElement = driver.findElement(By.id("noTradeIn"));
			JavascriptExecutor.executeScript("arguments[0].click();", WebElement);


			cssSelector = "span[class=\"rf-po-bfe-dimension-base-title\"]";
			contains = "一括払いまたはそのほかの支払い方法";
			if (exe_click(cssSelector, contains) == false) {
				return null;
			}


			cssSelector = "span[class=\"form-selector-title\"]";
			contains = "あとで通信キャリアと接続する";
			if (exe_click(cssSelector, contains) == false) {
				return null;
			}

			cssSelector = "span[class=\"form-selector-title\"]";
			contains = "AppleCare+による保証なし";
			if (exe_click(cssSelector, contains) == false) {
				return null;
			}



			cssSelector = "button[data-analytics-title=\"バッグに追加\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}


			cssSelector = "button[data-autom=\"proceed\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}



			cssSelector = "button[data-autom=\"checkout\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}

			cssSelector = "button[data-autom=\"guest-checkout-btn\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}


/*
 *ご希望の受け取り方法は？
 */
		    /*
		    配送を希望する
		     */
			cssSelector = "div[class=\"rc-segmented-control-text\"]";
			contains = "";
			if (exe_click(cssSelector, 1) == false) {
				return null;
			}

			cssSelector = "button[data-autom=\"fulfillment-pickup-store-search-button\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}

		    List<WebElement> elements = driver.findElements(By.id("checkout.fulfillment.pickupTab.pickup.storeLocator.searchInput"));
            if (!elements.isEmpty()) {
            	elements.get(0).clear();
            	elements.get(0).sendKeys("171-0044");
            }


		    matchingElements = driver.findElements(By.id("checkout.fulfillment.pickupTab.pickup.storeLocator.search"));
		    matchingElements.get(0).click();

//		    matchingElements = driver.findElements(By.cssSelector("li[class=\"form-selector\"]"));
//		    matchingElements.get(0).click();


			cssSelector = "li[class=\"form-selector\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}

			cssSelector = "span[class=\"rt-storelocator-store-availabilityquote\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}



//			Thread.sleep(1500);
//		    matchingElements = driver.findElements(By.id("rs-checkout-continue-button-bottom"));
//		    matchingElements.get(0).click();

			cssSelector = "button[data-autom=\"fulfillment-continue-button\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}

//			Thread.sleep(1500);
//            elements = driver.findElements(By.id("checkout.pickupContact.selfPickupContact.selfContact.address.lastName"));
//            if (!elements.isEmpty()) {
//                elements.get(0).sendKeys(generateRandomString(8));
//
//            }

            String id = "checkout.pickupContact.selfPickupContact.selfContact.address.lastName";
			contains = "";
			if (exe_sendKeys(id, generateRandomString(8)) == false) {
				return null;
			}

            elements = driver.findElements(By.id("checkout.pickupContact.selfPickupContact.selfContact.address.firstName"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys(generateRandomString(8));
            }

            //TODO
            elements = driver.findElements(By.id("checkout.pickupContact.selfPickupContact.selfContact.address.emailAddress"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("teishin8888@gmail.com");
            }

            //TODO
            elements = driver.findElements(By.id("checkout.pickupContact.selfPickupContact.selfContact.address.mobilePhone"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("09033362498");
            }


//			Thread.sleep(1000);
//		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"continue-button-label\"]"));
//		    matchingElements.get(0).click();

			cssSelector = "button[data-autom=\"continue-button-label\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}


		    /*
		     * ご希望の支払い方法は？
		     */


			cssSelector = "div[class=\"form-selector\"]";
			contains = "";
			if (exe_click(cssSelector, 0) == false) {
				return null;
			}

/*
 *
4599055301152883
09/28
880
 *
 */

			Thread.sleep(1500);
			elements = driver.findElements(By.id("checkout.billing.billingOptions.selectedBillingOptions.creditCard.cardInputs.cardInput-0.cardNumber"));
            if (!elements.isEmpty()) {
            	elements.get(0).sendKeys("9999999999999999");
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
			e.printStackTrace();
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




	private boolean exe_click(String cssSelector, String contains) {
		int count;
		boolean while_flag;
		List<WebElement> matchingElements;
		String Title = "";
		logger.info("处理 : " + Title);
		count = 0;
		while_flag = true;
		do {
		    try {
			    Thread.sleep(500);
			    matchingElements = driver.findElements(By.cssSelector(cssSelector));
			    matchingElements = matchingElements.stream()
			        .filter(element -> element.getText().contains(contains))
			        .collect(Collectors.toList());
			    if (matchingElements.size() >= 1) {
			    	matchingElements.get(0).click();
					while_flag = false;
			    }

				++count;
				if (count > 6) {
					while_flag = false;
					return false;
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}

		} while (while_flag);
		return true;
	}



	private boolean exe_click(String cssSelector, int index) {
		int count;
		boolean while_flag;
		List<WebElement> matchingElements;
		String Title = "";
		logger.info("处理 : " + Title);
		count = 0;
		while_flag = true;
		do {
		    try {
			    Thread.sleep(500);
			    matchingElements = driver.findElements(By.cssSelector(cssSelector));
			    if (matchingElements.size() >= 1) {
			    	matchingElements.get(index).click();
					while_flag = false;
			    }

				++count;
				if (count > 6) {
					while_flag = false;
					return false;
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}

		} while (while_flag);
		return true;
	}



	private boolean exe_sendKeys(String id, String contains) {
		int count;
		boolean while_flag;
		List<WebElement> matchingElements;
		String Title = "";
		logger.info("处理 : " + Title);
		count = 0;
		while_flag = true;
		do {
		    try {
			    Thread.sleep(500);
			    matchingElements = driver.findElements(By.id(id));
			    if (matchingElements.size() >= 1) {
				    matchingElements.get(0).sendKeys(generateRandomString(8));
					while_flag = false;
			    }


				++count;
				if (count > 6) {
					while_flag = false;
					return false;
				}
			} catch (Exception e) {
				logger.warn(e.getMessage());
			}

		} while (while_flag);
		return true;
	}


    public static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }

        return stringBuilder.toString();
    }
}

/*
 *
お客様がご自身で受け取る
 */


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



			Thread.sleep(1000);
		    matchingElements = driver.findElements(By.cssSelector("button[data-autom=\"fulfillment-continue-button\"]"));
		    matchingElements.get(0).click();



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
            	elements.get(0).clear();
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
 */



//matchingElements.get(0).getAttribute("outerHTML");