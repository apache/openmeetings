package org.openmeetings.test.init;

import junit.framework.TestCase;

import org.openmeetings.app.data.user.Salutationmanagement;

public class AddTitles extends TestCase {
	
	public AddTitles(String testname){
		super(testname);
	}
	
	public void testaddTestTitles(){
		
		Salutationmanagement.getInstance().addUserSalutation("Herr",261);
		Salutationmanagement.getInstance().addUserSalutation("Frau",262);
		
	}

}
