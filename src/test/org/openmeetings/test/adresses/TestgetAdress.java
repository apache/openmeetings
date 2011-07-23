package org.openmeetings.test.adresses;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.persistence.beans.adresses.Adresses;
import org.openmeetings.app.data.user.Addressmanagement;

import junit.framework.TestCase;

public class TestgetAdress extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestgetAdress.class);
	
	public TestgetAdress(String testname){
		super(testname);
	}
	
	public void testGetAdress(){
		
		Adresses adresses = Addressmanagement.getInstance().getAdressbyId(1);
		
		log.error("Adresses: "+adresses.getStates().getName());
		log.error("Adresses: "+adresses.getEmail());
		log.error("Adresses: "+adresses.getStreet());
		log.error("Adresses: "+adresses.getTown());
		
	}

}
