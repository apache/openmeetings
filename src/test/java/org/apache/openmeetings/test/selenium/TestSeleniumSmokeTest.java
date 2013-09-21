/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.test.selenium;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestSeleniumSmokeTest {

	public static String BASE_URL = "http://localhost:20080/openmeetings";
	public static String username = "swagner";
	public static String userpass = "qweqwe";
	private static final String orgname = "seleniumtest";
	private static final String email = "selenium@openmeetings.apache.org";

	public WebDriver driver = null;

	// setting this to false can be handy if you run the test from inside
	// Eclipse, the browser will not shut down after the test so you can start
	// to diagnose the test issue
	public boolean doTearDownAfterTest = false;

	@Before
	public void setUp() {
		driver = new FirefoxDriver();
	}

	@Test
	public void smokeTest() throws Exception {
		try {
			driver.get(BASE_URL);
			
			testWebSite();
			
			SeleniumUtils.inputText(driver, "login", username);
			SeleniumUtils.inputText(driver, "pass", userpass);

			WebElement signInButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), 'Sign in')]]", true);
			signInButton.click();

			SeleniumUtils.elementExists(driver,
					"//h3[contains(text(), 'Help and support')]", true);
		} catch (Exception e) {
			SeleniumUtils.makeScreenShot(this.getClass().getSimpleName(), e,
					driver);
			throw e;
		}
	}

	private void testWebSite() throws Exception {
		
		WebElement wicketExtensionsWizardHeaderTitle = SeleniumUtils.findElement(driver,
				"wicketExtensionsWizardHeaderTitle", false);
		if (wicketExtensionsWizardHeaderTitle == null) {
			return;
		}
		if (wicketExtensionsWizardHeaderTitle.getText().contains("Installation")) {
			System.out.println("Do Installation");
			doInstallation();
		}
		
	}
	
	private void doInstallation() throws Exception {
		Thread.sleep(3000L);
		
		WebElement buttons_next = SeleniumUtils.findElement(driver, "buttons:next", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_next);
		
		Thread.sleep(1000L);
		
		SeleniumUtils.inputText(driver, "view:cfg.username", username);
		SeleniumUtils.inputText(driver, "view:cfg.password", userpass);
		SeleniumUtils.inputText(driver, "view:cfg.email", email);
		SeleniumUtils.inputText(driver, "view:cfg.group", orgname);
		
		buttons_next = SeleniumUtils.findElement(driver, "buttons:next", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_next);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElement(driver, "buttons:next", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_next);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElement(driver, "buttons:next", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_next);
		
		Thread.sleep(1000L);
		
		buttons_next = SeleniumUtils.findElement(driver, "buttons:next", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_next);
		
		Thread.sleep(1000L);
		
		WebElement buttons_finish = SeleniumUtils.findElement(driver, "buttons:finish", true);
		
		((JavascriptExecutor) driver).executeScript("arguments[0].click();", buttons_finish);
		
		//Installation takes a while
		Thread.sleep(30000L);
		
		//the ajax loading thing does not work, just goto the main page
		driver.get(BASE_URL);
	}

	@After
	public void tearDown() throws Exception {
		if (doTearDownAfterTest) {
			driver.close();
			driver.quit();
		}
	}

}
