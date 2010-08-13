package org.openmeetings.test.userdata;

import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.MainService;

import junit.framework.TestCase;

public class DeleteUser extends TestCase {

	public DeleteUser(String testname){
		super(testname);
	}
	
	public void testdeleteUsers(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "swagner6", "678101",false,null,-1L);
		
		System.out.println("us: "+us.getFirstname());
		
		Long delete = mService.deleteUserIDSelf(sessionData.getSession_id());
		
		System.out.println("deleteSelf "+delete);
	}
	/*
	public void testdeleteUsersAdmin(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = mService.loginUser(sessionData.getSession_id(), "swagner", "67810");
		
		System.out.println("us: "+us.getFirstname());
		
		String delete = mService.deleteUserAdmin(sessionData.getSession_id(),9);
		
		System.out.println("deleteAdmin "+delete);
		
		mService.logoutUser(sessionData.getSession_id());
	}
	*/

}
