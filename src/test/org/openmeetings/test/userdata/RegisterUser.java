package org.openmeetings.test.userdata;

import junit.framework.TestCase;

import org.hibernate.Session;
import org.hibernate.Transaction;

import org.openmeetings.app.hibernate.utils.HibernateUtil;
import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.hibernate.beans.user.Users;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.data.user.Usermanagement;

public class RegisterUser extends TestCase{
	
	public RegisterUser(String testname) {
		super(testname);
		// TODO Auto-generated constructor stub
	}
	
	public void testRegisterUser(){
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "swagner", "67810",false,null,"localhost");		
		
		//Configurationmanagement.getInstance().addConfByKey(3, "allow_frontend_register", "1", 2, "coment");

		
		//registerUser(String SID, String Username, String Userpass, String lastname, String firstname, String email, int age, String adresse, String Zip, String state, String town)
		Long user_id = mService.registerUser(sessionData.getSession_id(),"username3", "userpass", "lastname", 
				"firstname", "email", new java.util.Date(), "street", "No 14", 
				"faxno", "zip", 1, "town", 1, "089/0815");
		System.out.println("++++ user_id: "+user_id);
		
	}

}
