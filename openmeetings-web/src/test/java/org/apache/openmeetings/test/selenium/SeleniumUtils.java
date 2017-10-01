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

import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class SeleniumUtils {
	private static final Logger log = Red5LoggerFactory.getLogger(SeleniumUtils.class, getWebAppRootKey());
	// we need to retry some actions because our web site is dynamic
	static int numberOfRetries = 10;

	// we need to sleep to make sure Ajax could complete whatever it does
	static long defaultSleepInterval = 1000;

	public static void inputText(WebDriver driver, String search,
			String inputText) throws Exception {
		WebElement element = SeleniumUtils.findElement(driver, search, true, true);

		//clear text before adding input
		element.clear();

		// Would make send to check if this element is really an input text
		element.sendKeys(inputText);
	}

	public static void click(WebDriver driver, String search) throws Exception {
		WebElement element = SeleniumUtils.findElement(driver, search, true, true);
		element.click();
	}

	/**
	 *
	 * @param driver
	 * @param search
	 * @param throwException
	 *            under some circumstance you do't want to exit the test here
	 * @return
	 * @throws Exception
	 */
	public static List<WebElement> findElements(WebDriver driver, String search,
			boolean throwException) throws Exception {
		for (int i = 0; i < numberOfRetries; i++) {
			List<WebElement> elements = _findElement(driver, search);
			if (elements != null) {
				return elements;
			}

			Thread.sleep(defaultSleepInterval);
		}

		if (throwException) {
			throw new Exception("Could not find element with specified path "
					+ search);
		}

		return null;
	}

	/**
	 *
	 * @param driver
	 * @param search
	 * @param throwException
	 *            under some circumstance you do't want to exit the test here
	 * @param onlyReturnVisisbleElement TODO
	 * @return
	 * @throws Exception
	 */
	public static WebElement findElement(WebDriver driver, String search,
			boolean throwException, boolean onlyReturnVisisbleElement) throws Exception {
		for (int i = 0; i < numberOfRetries; i++) {
			List<WebElement> elements = _findElement(driver, search);
			if (elements != null) {

				if (!onlyReturnVisisbleElement) {
					return elements.get(0);
				}

				for (WebElement element : elements) {
					if (element.isDisplayed()) {
						return element;
					}
				}

			}

			Thread.sleep(defaultSleepInterval);
		}

		if (throwException) {
			throw new Exception("Could not find element with specified path "
					+ search);
		}

		return null;
	}

	private static By[] _getSearchArray(String search) {
		//If xpath we have to use it, if xpath is used with By.className(...) there will be an exception
		if (search.startsWith("//")) {
			return new By[] { By.xpath(search) };
		} else {
			return new By[] { By.id(search), By.name(search), By.className(search),
					By.tagName(search), By.xpath(search) };
		}
	}

	private static List<WebElement> _findElement(WebDriver driver, String search) {
		for (By by : _getSearchArray(search)) {
			try {
				List<WebElement> elements = driver.findElements(by);
				if (elements != null && elements.size() > 0) {
					return elements;
				}
			} catch (Exception e) {
				// Do not show any warnings
			}
		}
		return null;
	}

	public static void elementExists(WebDriver driver, String search,
			boolean shouldExist) throws Exception {
		Thread.sleep(defaultSleepInterval);

		boolean doesExist = !shouldExist;

		for (int i = 0; i < numberOfRetries; i++) {
			doesExist = checkExists(driver, search);
			if (doesExist == shouldExist) {
				break;
			}

			Thread.sleep(defaultSleepInterval);
		}

		if (doesExist != shouldExist) {
			if (shouldExist) {
				throw new Exception("Element could not be found: " + search);
			} else {
				throw new Exception("Unexpected Element was found: " + search);
			}
		}
	}

	private static boolean checkExists(WebDriver driver, String search) {
		for (By by : _getSearchArray(search)) {
			try {
				List<WebElement> element = driver.findElements(by);
				if (element.size() > 0) {
					return true;
				}
			} catch (Exception e) {
				// Do not show any warnings
			}
		}
		return false;
	}

	public static void makeScreenShot(String testName, Exception e,
			WebDriver driver) {
		try {
			DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
			String fileName = e.getMessage().replace(" ", "_");
			fileName = fileName.replaceAll("(\r\n|\n)", "");
			fileName = fileName.replaceAll("/", "");

			if (fileName.length() > 100) {
				fileName = fileName.substring(0, 100);
			}

			fileName = fileName + "_" + df.format(new Date()) + ".png";
			File screenShotFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);

			String path = "." + File.separatorChar + "build"
					+ File.separatorChar + "screenshots" + File.separatorChar
					+ testName;

			File screenshotFolder = new File(path);
			if (!screenshotFolder.exists()) {
				screenshotFolder.mkdirs();
			}

			System.out.println("screenshot copy from: "
					+ screenShotFile.getAbsolutePath());
			System.out.println("Length Filename: " + fileName.length()
					+ " - Writing screenshot to: " + path + File.separatorChar
					+ fileName);

			FileUtils.moveFile(screenShotFile, new File(path
					+ File.separatorChar + fileName));
		} catch (Exception err) {
			log.error("Error", err);
		}

	}
}