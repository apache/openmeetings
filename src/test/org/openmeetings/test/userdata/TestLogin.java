package org.openmeetings.test.userdata;

import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestLogin extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private MainService mService;
	
	public void testTestLogin(){
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "swagner", "test",false,null,-1L);
		
		System.out.println("us: "+us.getFirstname());
		
		//mService.getLanguageById(1);
		
	}

}
