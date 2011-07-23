package org.openmeetings.test.userdata;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.persistence.utils.PersistenceSessionUtil;
import org.openmeetings.app.remote.MainService;

import junit.framework.TestCase;

public class AddConfigParams extends TestCase {

	
	public AddConfigParams(String testname){
		super(testname);
	}
	
	public void testAddConfigParams(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "wagner", "test",false,null,-1L);		
		
		String ret = "";
		
		//ret = Configurationmanagement.getInstance().addConfByKey(3, "allow_frontend_register", "1", us.getUser_id().intValue(), "coment");
		
		System.out.println("ret: "+ret);
		
		ret = Configurationmanagement.getInstance().addConfByKey(3, "default_group_id", "1", us.getUser_id(), "coment");
		
		
		
	}

}
