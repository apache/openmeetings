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

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.WebElement;

import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButtons;

public class TestSignUp extends AbstractTestDefaults {
	String pass = "pass";

	@Override
	public boolean getDoTearDownAfterTest() {
		return false;
	}

	@Test
	public void testSignUp() throws Exception {
		try {
			driver.get(getBASE_URL());

			String currentRandomCounter = "" + ((new Date().getTime())/1000);
			String userName = "seba" + currentRandomCounter;
			String email = "hans." + currentRandomCounter + "@openmeetings.apache.org";

			super.testIsInstalledAndDoInstallation();

			WebElement signUpButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), '" + getString("123") + "')]]", true, true);
			signUpButton.click();

			// ##################################
			// Test validation message for passwords to be identical
			// ##################################
			doSignUp("Hans","Muster", userName, "pw", "pw2", email);

			//Find Error label-id 232 "Please enter two identical passwords"
			SeleniumUtils.findElement(driver, "//span[@class='feedbackPanelERROR'][contains(text(), '" + getString("232") + "')]", true, true);

			// ##################################
			// Sign up with user and sign in
			// ##################################
			doSignUp("Hans","Muster", userName, pass, pass, email);

			//Check for popup with success message and email to check
			SeleniumUtils.findElement(driver, "//span[contains(text(), '" + getString("warn.notverified") + "')]", true, true);

			//click button to close popup
			WebElement signUpSucessPopUpOkButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), '" + DialogButtons.OK.toString() + "')]]", true, true);
			signUpSucessPopUpOkButton.click();

			//Login with user
			SeleniumUtils.inputText(driver, "login", userName);
			SeleniumUtils.inputText(driver, "pass", pass);

			//click labelid 112 "Sign In"
			WebElement signInButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), '" + getString("112") + "')]]", true, true);
			signInButton.click();

			// check for some text in dashbaord, labelid 281, "Help and support"
			SeleniumUtils.elementExists(driver,
					"//h3[contains(text(), '" + getString("281") + "')]", true);

			//sign out
			WebElement signOutLink = SeleniumUtils.findElement(driver,
					"//a[contains(text(), '" + getString("310") + "')]", true, true);
			signOutLink.click();

			Alert alert = driver.switchTo().alert();
			alert.accept();

			// ##################################
			// Sign up with same user and email and check duplicate messages
			// ##################################

			signUpButton = SeleniumUtils.findElement(driver,
					"//button[span[contains(text(), '" + getString("123") + "')]]", true, true);
			signUpButton.click();

			doSignUp("Hans","Muster", userName, pass, pass, email);

			SeleniumUtils.findElement(driver, "//span[@class='feedbackPanelERROR'][contains(text(), '" + getString("error.login.inuse") + "')]", true, true);

			SeleniumUtils.findElement(driver, "//span[@class='feedbackPanelERROR'][contains(text(), '" + getString("error.email.inuse") + "')]", true, true);
		} catch (Exception e) {
			SeleniumUtils.makeScreenShot(this.getClass().getSimpleName(), e,
					driver);
			throw e;
		}
	}

	private void doSignUp(String firstName, String lastName, String login, String password,
					String confirmPassword, String email) throws Exception {

		SeleniumUtils.inputText(driver, "firstName", firstName);
		SeleniumUtils.inputText(driver, "lastName", lastName);
		SeleniumUtils.inputText(driver, "//input[@name='login']", login);
		SeleniumUtils.inputText(driver, "password", password);
		SeleniumUtils.inputText(driver, "confirmPassword", confirmPassword);
		SeleniumUtils.inputText(driver, "email", email);

		WebElement submitButton = SeleniumUtils.findElement(driver,
				"//button[span[contains(text(), 'Register')]]", true, true);
		submitButton.click();
	}
}
