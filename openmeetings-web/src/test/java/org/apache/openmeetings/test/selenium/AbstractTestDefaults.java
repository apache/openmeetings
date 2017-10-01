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

import java.util.List;

import org.apache.openmeetings.AbstractSpringTest;
import org.apache.openmeetings.db.dao.label.LabelDao;
import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;

@Category(SeleniumTests.class)
public abstract class AbstractTestDefaults extends AbstractSpringTest {
	public WebDriver driver = null;

	private String BASE_URL = "http://localhost:5080/openmeetings";
	private String username = "swagner";
	private String userpass = "qweqwe";
	private String groupName = "seleniumtest";
	private String email = "selenium@openmeetings.apache.org";
	private String locale = "en-us";


	public String getBASE_URL() {
		return BASE_URL;
	}

	public String getUsername() {
		return username;
	}

	public String getUserpass() {
		return userpass;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getEmail() {
		return email;
	}

	public Long getLanguageId() {
		return 1L;
	}

	public String getLocale() {
		return locale;
	}

	// setting this to false can be handy if you run the test from inside
	// Eclipse, the browser will not shut down after the test so you can start
	// to diagnose the test issue
	public boolean doTearDownAfterTest = false;

	public String getString(String key) {
		return LabelDao.getString(key, getLanguageId());
	}

	/**
	 * Make method overwrite possible to have custom behavior in tests
	 *
	 * @return
	 */
	public boolean getDoTearDownAfterTest() {
		return doTearDownAfterTest;
	}

	@Before
	public void setUp() {
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("intl.accept_languages", getLocale());
		driver = new FirefoxDriver(new FirefoxOptions().setProfile(profile));
	}

	/**
	 * This test is a basic test to verify the default loader mechanism works
	 * it is not intend to be a part of any sub test
	 *
	 * @throws Exception
	 */
	//@Test
	public void smokeTest() throws Exception {
		try {
			driver.get(getBASE_URL());

			testIsInstalledAndDoInstallation();

			SeleniumUtils.inputText(driver, "login", getUsername());
			SeleniumUtils.inputText(driver, "pass", getUserpass());

			WebElement signInButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), 'Sign in')]]", true, true);
			signInButton.click();

			SeleniumUtils.elementExists(driver,
					"//h3[contains(text(), 'Help and support')]", true);
		} catch (Exception e) {
			SeleniumUtils.makeScreenShot(this.getClass().getSimpleName(), e,
					driver);
			throw e;
		}
	}

	/**
	 * Throws exception in case that test fails, so it is important to not catch
	 * that exception but really let the test fail!
	 *
	 * @throws Exception
	 */
	protected void testIsInstalledAndDoInstallation() throws Exception {

		WebElement wicketExtensionsWizardHeaderTitle = SeleniumUtils
				.findElement(driver, "wicketExtensionsWizardHeaderTitle", false, true);
		if (wicketExtensionsWizardHeaderTitle == null) {
			return;
		}
		if (wicketExtensionsWizardHeaderTitle.getText()
				.contains("Installation")) {
			System.out.println("Do Installation");
			doInstallation();
		}

	}

	private void doInstallation() throws Exception {
		Thread.sleep(3000L);

		List<WebElement> buttons_next = SeleniumUtils.findElements(driver,
				"buttons:next", true);

		buttons_next.get(1).sendKeys(Keys.RETURN);

		Thread.sleep(1000L);

		SeleniumUtils.inputText(driver, "view:cfg.username", getUsername());
		SeleniumUtils.inputText(driver, "view:cfg.password", getUserpass());
		SeleniumUtils.inputText(driver, "view:cfg.email", getEmail());
		SeleniumUtils.inputText(driver, "view:cfg.group", getGroupName());

		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);

		buttons_next.get(1).sendKeys(Keys.RETURN);

		Thread.sleep(1000L);

		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);

		buttons_next.get(1).sendKeys(Keys.RETURN);

		Thread.sleep(1000L);

		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);

		buttons_next.get(1).sendKeys(Keys.RETURN);

		Thread.sleep(1000L);

		buttons_next = SeleniumUtils.findElements(driver, "buttons:next", true);

		buttons_next.get(1).sendKeys(Keys.RETURN);

		Thread.sleep(2000L);

		List<WebElement> elements = SeleniumUtils.findElements(driver,
				"buttons:finish", true);

		elements.get(1).sendKeys(Keys.RETURN);

		long maxMilliSecondsWait = 120000;

		while (maxMilliSecondsWait > 0) {

			// check if installation is complete by searching for the link on
			// the success page
			WebElement enterApplicationLink = SeleniumUtils.findElement(driver,
					"//a[contains(@href,'install')]", false, true);

			if (enterApplicationLink == null) {
				System.out
						.println("Installation running - wait 3 more seconds and check again");

				Thread.sleep(3000L);
				maxMilliSecondsWait -= 3000;
			} else {
				maxMilliSecondsWait = 0;

				enterApplicationLink.click();

				return;
			}
		}

		throw new Exception("Timeout during installation");
	}

	@After
	public void tearDown() {
		if (getDoTearDownAfterTest()) {
			driver.close();
			driver.quit();
		}
	}

}
