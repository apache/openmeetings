package org.apache.openmeetings.test.web;

import org.apache.openmeetings.test.AbstractWicketTester;
import org.apache.openmeetings.web.pages.MainPage;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class TestLoginUI extends AbstractWicketTester {

	@Test
	public void testLoginUi() {
		
		tester.startPage(MainPage.class);
		
		tester.assertRenderedPage(SignInPage.class);
		
		FormTester formTester = tester.newFormTester("signin:signin");
		formTester.setValue("login", username);
		formTester.setValue("pass", userpass);
		
		//How to reference specific buttons in Wicket jQuery UI ?!
		
		formTester.submit();
		
		System.err.println("getLastRenderedPage: "+ tester.getLastRenderedPage().getMarkup().toString());
		
		//will fail
		//tester.assertComponent("dashboard", DashboardPanel.class);
		
	}
	
}

