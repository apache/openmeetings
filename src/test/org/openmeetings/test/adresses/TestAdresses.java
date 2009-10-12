package org.openmeetings.test.adresses;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.app.data.user.Addressmanagement;

public class TestAdresses extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestAdresses.class);
	
	public TestAdresses (String testname) {
		super(testname);
	}
	
	public void testAddAdress(){
		
		Long states_id = Statemanagement.getInstance().addState("Deutschland");
		
		System.out.println("states_id "+states_id);
		log.error("states_id: "+states_id);
		
		long adress_id = Addressmanagement.getInstance().saveAddress("street", "zip", "town", states_id, "additionalname", "comment", "fax", "phone", "email");
		
		System.out.println("adress_id "+adress_id);
		log.error("adress_id: "+adress_id);
		
	}

}
