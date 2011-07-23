package org.openmeetings.test.userdata;

import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.MainService;

import junit.framework.TestCase;

public class TestLogin extends TestCase {
	
	
	public TestLogin(String testname){
		super(testname);
	}
	
	public void testTestLogin(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "swagner", "test",false,null,-1L);
		
		System.out.println("us: "+us.getFirstname());
		
		//mService.getLanguageById(1);
		
	}

}
