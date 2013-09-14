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

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SeleniumUtils {
	
	//we need to retry some actions because our web site is dynamic
	static int numberOfRetries = 10;
	
	//we need to sleep to make sure Ajax could complete whatever it does
	static long defaultSleepInterval = 1000;

	public static void inputText(WebDriver driver, String search,
			String inputText) throws Exception {
		WebElement element = SeleniumUtils.findElement(driver, search);

		// Would make send to check if this element is really an input text
		element.sendKeys(inputText);

	}
	
	public static WebElement findElement(WebDriver driver, String search) throws Exception {
		
		for (int i=0;i<numberOfRetries;i++) {
			WebElement element = _findElement(driver, search);
			if (element != null) {
				return element;
			}
			
			Thread.sleep(defaultSleepInterval);
		}
		
		
		throw new Exception("Could not find element with specified path "
				+ search);
	}

	private static WebElement _findElement(WebDriver driver, String search) {

		try {
			WebElement element = driver.findElement(By.id(search));
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		
		try {
			WebElement element = driver.findElement(By.name(search));
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		try {
			WebElement element = driver.findElement(By.className(search));
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		try {
			WebElement element = driver.findElement(By.tagName(search));
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		try {
			WebElement element = driver.findElement(By.xpath(search));
			if (element != null) {
				return element;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		
		return null;

		
	}
	
	public static void elementExists(WebDriver driver, String search, boolean shouldExist) throws Exception {
		
		Thread.sleep(defaultSleepInterval);
		
		boolean doesExist = !shouldExist;
		
		for (int i=0;i<numberOfRetries;i++) {
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

	private static boolean checkExists(WebDriver driver, String search)
			throws Exception {
		try {
			List<WebElement> element = driver.findElements(By.id(search));
			if (element.size() > 0) {
				return true;
			}

			element = driver.findElements(By.name(search));
			if (element.size() > 0) {
				return true;
			}

			element = driver.findElements(By.className(search));
			if (element.size() > 0) {
				return true;
			}

			element = driver.findElements(By.tagName(search));
			if (element.size() > 0) {
				return true;
			}

			element = driver.findElements(By.xpath(search));
			if (element.size() > 0) {
				return true;
			}
		} catch (Exception e) {
			// Do not show any warnings
		}
		return false;
	}

}
