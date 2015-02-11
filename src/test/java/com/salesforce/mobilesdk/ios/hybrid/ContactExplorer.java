

package com.salesforce.mobilesdk.ios.hybrid;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.Listeners;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.saucerest.SauceREST;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

@Listeners({SauceOnDemandTestListener.class})
public class ContactExplorer implements SauceOnDemandSessionIdProvider, SauceOnDemandAuthenticationProvider {

	
	/** Authenticate to Sauce with environment variables SAUCE_USER_NAME and SAUCE_API_KEY **/
	private SauceOnDemandAuthentication auth = new SauceOnDemandAuthentication(
			"gauravkheterpal", "your sauce api key here");
	private WebDriver driver;


	private String sessionId;
	/** Run before each test method **/
	@BeforeMethod
	public void setUp() throws Exception {


		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Jdk14Logger");
		// set up appium
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability("platformName","iOS");
		capabilities.setCapability("platformVersion","6.1");
		capabilities.setCapability("deviceName", "iPhone Simulator");


		boolean runOnSauce = false;
		try{
			if (runOnSauce) {
				String user = auth.getUsername();
				String key = auth.getAccessKey();

				// Upload app to Sauce Labs
				SauceREST rest = new SauceREST(user, key);
				String localApp = "ContactExplorer.app.zip";
				String userDir = System.getProperty("user.dir");
				rest.uploadFile(new File(userDir, localApp), localApp);
				capabilities.setCapability("app", "sauce-storage:" + localApp);
				URL sauceURL = new URL("http://" + user + ":" + key + "@ondemand.saucelabs.com:80/wd/hub");
				driver = new RemoteWebDriver(sauceURL, capabilities);
			} 
			//run on local machine
			else {
				boolean runOnSimulator = false;
				File appDir,app;
				appDir = new File( "/Users/gauravkheterpal/Desktop/AppFiles");
				app = new File(appDir,"ContactExplorer.app");
				if(!runOnSimulator){
					appDir = new File( "/Users/gauravkheterpal/Desktop/AppFiles/Device-apps");
					app = new File(appDir,"ContactExplorer.app");
					capabilities.setCapability("device", "iPhone");
					capabilities.setCapability("udid", "7f5b0cbc4a6e84b8eef128031e3ff2829a749e31");
					capabilities.setCapability("bundleid", "com.salesforce.mobilesdk.ContactExplorer");
				}
				//tell Appium where the location of the app is
				capabilities.setCapability("app", app.getAbsolutePath());
				driver = new RemoteWebDriver(new URL("http://127.0.0.1:4723/wd/hub"), capabilities);
			}
			sessionId = ((RemoteWebDriver) driver).getSessionId().toString();
			driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);
		}
		catch (MalformedURLException e) {
			System.out.println("App launch fail");
			e.printStackTrace();
		}
	}

	//Tests for automatic signIn
	@Test(priority=0)
	public void login() throws InterruptedException {
		try {
			
			driver.findElement(By.xpath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIAWebView[1]/UIATextField[1]")).click();
			driver.findElement(By.xpath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIAWebView[1]/UIATextField[1]")).sendKeys("dreamforce-demo@appium.com");
            //Thread.sleep(12000);
			driver.findElement(By.xpath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIAWebView[1]/UIASecureTextField[1]")).click();
			driver.findElement(By.xpath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIAWebView[1]/UIASecureTextField[1]")).sendKeys("test1234");
			driver.findElement(By.name("Log in to Salesforce")).click();
			driver.findElement(By.name(" Allow ")).click();
			//Thread.sleep(2000);
			driver.findElement(By.xpath("//UIAApplication[1]/UIAWindow[1]/UIAScrollView[1]/UIAWebView[1]/UIALink[3]")).click();
			//Thread.sleep(10000);
			driver.findElement(By.name("console:")).click();
			Thread.sleep(2000);

		} 
		catch (TimeoutException e) {
			System.out.println("Timeout Exception");
		}
	}


	/** Run after each test method **/
	@AfterMethod(alwaysRun = true)
	public void tearDown(ITestResult result) throws IOException {
		if (driver != null) driver.quit();
	}




	@Override
	public String getSessionId() {
		//SessionId sessionId = ((RemoteWebDriver)driver).getSessionId();
		return (sessionId == null) ? null : sessionId.toString();
	}


	@Override
	public SauceOnDemandAuthentication getAuthentication() {
		return auth;
	}
}




