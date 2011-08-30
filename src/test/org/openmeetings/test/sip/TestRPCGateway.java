package org.openmeetings.test.sip;

//import org.apache.log4j.Logger;
import junit.framework.TestCase;

import org.junit.Test;
import org.openmeetings.app.sip.xmlrpc.test.OpenXGClient;

public class TestRPCGateway extends TestCase {
	
	//private static final Logger log = Logger.getLogger(TestRPCGateway.class);

	@Test
	public void testTestSocket(){
		try {
			
			OpenXGClient.getInstance().testConnection();
			
		} catch (Exception err) {
			err.printStackTrace();
			//log.error("[TestSocket] ",err);
		}
	}
}
