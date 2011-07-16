package org.openmeetings.test.userdata;

import junit.framework.TestCase;

import org.openmeetings.app.hibernate.beans.basic.Sessiondata;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.utils.crypt.ManageCryptStyle;

public class TestAuth extends TestCase{


	public TestAuth(String testname) {
		super(testname);
		// TODO Auto-generated constructor stub
	}
	
	public void testTestAuth() {
		
		MainService mService = new MainService();
		Sessiondata sessionData = mService.getsessiondata();
		
		System.out.println("sessionData: "+sessionData.getSession_id());
		
		String tTemp = ManageCryptStyle.getInstance().getInstanceOfCrypt().createPassPhrase("test");
		
		System.out.println("tTemp: "+tTemp);
		

	}	

}
